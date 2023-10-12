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
package nl.utwente.groove.grammar.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.util.parse.FormatErrorSet;

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

    /** The set of node types for which we want to check containment. */
    private final List<TypeEdge> checks;

    @Override
    public boolean isTrivial() {
        return this.checks.isEmpty();
    }

    @Override
    public FormatErrorSet check(HostGraph host) {
        FormatErrorSet result = new FormatErrorSet();
        Connect connect = buildConnect(host);
        while (!connect.isEmpty()) {
            for (var cycle : detectCycle(connect)) {
                result.add("Containment cycle in graph", cycle);
            }
        }
        return result;
    }

    /**
     * Builds the connection map for a given host graph.
     */
    private Connect buildConnect(HostGraph host) {
        this.recordMap.clear();
        Connect connect = new Connect();
        for (TypeEdge check : this.checks) {
            Set<? extends HostEdge> edges = host.edgeSet(check.label());
            for (HostEdge edge : edges) {
                Record source = getRecord(edge.source());
                List<Link> targets = connect.get(source);
                if (targets == null) {
                    connect.put(source, targets = new ArrayList<>());
                }
                targets.add(new Link(edge, getRecord(edge.target())));
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
    private List<List<HostEdge>> detectCycle(Connect connect) {
        if (!connect.isEmpty()) {
            this.result.clear();
            this.stack.clear();
            this.pool.clear();
            this.index = 0;
            Link start = new Link(connect.keySet().iterator().next());
            findSCCsFrom(connect, start);
            this.pool.forEach(connect::remove);
        }
        return this.result;
    }

    /** Finds the SCCs reachable from the target of a given link, using DFS. */
    private void findSCCsFrom(Connect connect, Link link) {
        Record target = link.target();
        target.index = this.index;
        target.lowlink = this.index;
        // make sure indices are unique
        this.index++;
        this.stack.push(link);
        this.pool.add(target);

        List<Link> outs = connect.get(target);
        if (outs != null) {
            for (Link out : outs) {
                Record neighbour = out.target();
                if (neighbour.index < 0) {
                    findSCCsFrom(connect, out);
                    if (neighbour.lowlink < target.lowlink) {
                        target.lowlink = neighbour.lowlink;
                        target.lowlinkSource = neighbour.lowlinkSource;
                    }
                } else if (this.pool.contains(neighbour)) {
                    if (neighbour.index < target.lowlink) {
                        target.lowlink = neighbour.index;
                        target.lowlinkSource = out.edge();
                    }
                }
            }
        }

        if (target.lowlink == target.index) {
            Link next = this.stack.pop();
            Record neighbour = next.target();
            var cycle = new ArrayList<HostEdge>();
            boolean first = true;
            while (neighbour != target) {
                if (first) {
                    cycle.add(neighbour.lowlinkSource);
                    first = false;
                }
                cycle.add(next.edge());
                // keep the pool in sync with the stack
                this.pool.remove(neighbour);
                connect.remove(neighbour);
                next = this.stack.pop();
                neighbour = next.target();
            }
            // keep the pool in sync with the stack
            this.pool.remove(neighbour);
            connect.remove(neighbour);
            if (cycle.size() > 1) {
                this.result.add(cycle);
            }
        }
    }

    /** Current index in the search. */
    private int index;
    /** Search stack of links. */
    private final Stack<Link> stack = new Stack<>();
    /** Set representation of {@link #stack} for efficiency of membership test. */
    private final Set<Record> pool = new HashSet<>();
    /** Collected list of cycles. */
    private final List<List<HostEdge>> result = new ArrayList<>();

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
            if (obj instanceof Record r) {
                otherNode = r.node;
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

        /** Node on which this record is based. */
        private final HostNode node;
        /** Lowest index of the reachable records. */
        int lowlink = -1;
        /** Edge whose target gave rise to index {@link #lowlink}.
         * This is the last edge in the cycle. */
        HostEdge lowlinkSource;
        /** Index of this record. */
        int index = -1;
    }

    static private record Link(HostEdge edge, Record target) {
        Link(Record record) {
            this(null, record);
        }
    }

    /** Connection structure. */
    static private class Connect extends LinkedHashMap<Record,List<Link>> {
        // empty
    }
}
