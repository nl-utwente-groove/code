/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.grammar.type;

import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.util.parse.FormatErrorSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * The {@link ContainmentChecker} class provides functionality for
 * verifying containment in a {@link HostGraph}.
 *
 * @author Arend Rensink
 */
public class ContainmentChecker implements TypeChecker {
    /**
     * Default constructor. Collects the type edges that have a multiplicity,
     * and stores them for quick lookup.
     */
    public ContainmentChecker(TypeGraph type) {
        assert type != null;
        this.typeGraph = type;
        this.checks = new ArrayList<>();
        for (TypeEdge edge : type.edgeSet()) {
            for (TypeEdge subEdge : edge.getSubtypes()) {
                if (edge.isComposite() || subEdge.isComposite()) {
                    this.checks.add(edge);
                }
            }
        }
    }

    @Override
    public TypeGraph getTypeGraph() {
        return this.typeGraph;
    }

    private final TypeGraph typeGraph;

    /** The set of node types for which we want to check a multiplicity. */
    private final List<TypeEdge> checks;

    @Override
    public boolean isTrivial() {
        return this.checks.isEmpty();
    }

    @Override
    public FormatErrorSet check(HostGraph host) {
        FormatErrorSet result = new FormatErrorSet();
        Map<Record,List<Record>> connect = buildConnect(host);
        while (!connect.isEmpty()) {
            for (HostNode start : detectCycle(connect)) {
                result.add("Containment cycle starting at %s", start);
            }
        }
        return result;
    }

    /**
     * Builds the connection map for a given host graph.
     */
    private Map<Record,List<Record>> buildConnect(HostGraph host) {
        this.recordMap.clear();
        Map<Record,List<Record>> connect = new LinkedHashMap<>();
        for (TypeEdge check : this.checks) {
            Set<? extends HostEdge> edges = host.edgeSet(check.label());
            for (HostEdge edge : edges) {
                Record source = getRecord(edge.source());
                List<Record> targets = connect.get(source);
                if (targets == null) {
                    connect.put(source, targets = new ArrayList<>());
                }
                targets.add(getRecord(edge.target()));
            }
        }
        return connect;
    }

    private Record getRecord(HostNode node) {
        Record result = this.recordMap.get(node);
        if (result == null) {
            this.recordMap.put(node, result = new Record(node));
        }
        return result;
    }

    private final Map<HostNode,Record> recordMap = new HashMap<>();

    /**
     * Detects the set of SCCs reachable from the first element of the
     * connection map, and returns one representative from each SCC.
     */
    private List<HostNode> detectCycle(Map<Record,List<Record>> connect) {
        if (!connect.isEmpty()) {
            this.result.clear();
            this.stack.clear();
            this.pool.clear();
            this.index = 0;
            strongConnect(connect, connect.keySet().iterator().next());
            connect.keySet().removeAll(this.pool);
        }
        return this.result;
    }

    private void strongConnect(Map<Record,List<Record>> connect, Record record) {
        record.index = this.index;
        record.lowlink = this.index;
        this.index++;
        this.stack.push(record);
        this.pool.add(record);

        List<Record> neighbours = connect.get(record);

        if (neighbours != null) {
            for (Record neighbour : neighbours) {
                if (neighbour.index < 0) {
                    strongConnect(connect, neighbour);
                    record.lowlink = Math.min(record.lowlink, neighbour.lowlink);
                } else if (this.pool.contains(neighbour)) {
                    record.lowlink = Math.min(record.lowlink, neighbour.index);
                }
            }
        }

        if (record.lowlink == record.index) {
            Record neighbor = null;
            int size = 0;
            while (record != neighbor) {
                neighbor = this.stack.pop();
                size++;
                this.pool.remove(neighbor);
                connect.remove(neighbor);
            }
            if (size > 1) {
                this.result.add(record.node);
            }
        }
    }

    /** Current index in the search. */
    private int index;
    /** Search stack of records. */
    private final Stack<Record> stack = new Stack<>();
    /** Set representation of {@link #stack} for efficiency of membership test. */
    private final Set<Record> pool = new HashSet<>();
    /** Collected list of roots. */
    private final List<HostNode> result = new ArrayList<>();

    /** Record for a node during the search. */
    static private class Record {
        Record(HostNode node) {
            this.node = node;
        }

        @Override
        public int hashCode() {
            return this.node.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            HostNode otherNode;
            if (obj instanceof Record) {
                otherNode = ((Record) obj).node;
            } else {
                assert obj instanceof HostNode;
                otherNode = (HostNode) obj;
            }
            return this.node.equals(otherNode);
        }

        @Override
        public String toString() {
            return "Record[" + this.node + "," + this.index + "," + this.lowlink + "]";
        }

        private final HostNode node;
        int lowlink = -1;
        int index = -1;
    }
}
