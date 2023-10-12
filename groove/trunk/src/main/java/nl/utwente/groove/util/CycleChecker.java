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
 * $Id: ContainmentChecker.java 6204 2023-10-12 15:49:34Z rensink $
 */
package nl.utwente.groove.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.util.parse.FormatErrorSet;

/**
 * The {@link CycleChecker} class provides functionality for
 * verifying containment in a {@link HostGraph}.
 *
 * @author Arend Rensink
 */
public abstract class CycleChecker<G extends Graph,I> implements Checker<G> {
    @Override
    public FormatErrorSet check(G graph) {
        FormatErrorSet result = new FormatErrorSet();
        this.connect = buildConnect(graph);
        while (!this.connect.isEmpty()) {
            for (var cycle : detectCycle()) {
                result.add("Containment cycle in graph", cycle);
            }
        }
        return result;
    }

    /**
     * Builds the connection map for a given graph.
     */
    abstract protected Connect buildConnect(G graph);

    /** The connection structure within which we are trying to detect cycles. */
    private Connect connect;

    /** Creates and returns a new Link object with the given info and node record. */
    protected Link<I> newLink(I info, Node node) {
        return new Link<>(info, getRecord(node));
    }

    /** Lazily creates and returns a record for a given node. */
    protected Record<I> getRecord(Node node) {
        var result = this.recordMap.get(node);
        if (result == null) {
            this.recordMap.put(node, result = new Record<>(node));
        }
        return result;
    }

    private final Map<Node,Record<I>> recordMap = new HashMap<>();

    /** Clears all internal structures.
     * Should be called at the start of {@link #buildConnect(Graph)}
     */
    protected void clear() {
        this.connect = null;
        this.recordMap.clear();
        this.stack.clear();
        this.pool.clear();
        this.result.clear();
    }

    /**
     * Detects the set of SCCs reachable from the first element of the
     * connection structure, and stores them in {@link #result}.
     * Afterwards removes all records reachable from that first
     * element.
     */
    private List<List<I>> detectCycle() {
        if (!this.connect.isEmpty()) {
            this.result.clear();
            this.stack.clear();
            this.pool.clear();
            this.index = 0;
            var start = new Link<>(this.connect.keySet().iterator().next());
            findSCCsFrom(start);
            this.pool.forEach(this.connect::remove);
        }
        return this.result;
    }

    /** Finds the SCCs reachable from the target of a given link, using DFS. */
    private void findSCCsFrom(Link<I> link) {
        var target = link.target();
        target.index = this.index;
        target.lowlink = this.index;
        // make sure indices are unique
        this.index++;
        this.stack.push(link);
        this.pool.add(target);

        var outs = this.connect.get(target);
        if (outs != null) {
            for (var out : outs) {
                var successor = out.target();
                if (successor.index < 0) {
                    findSCCsFrom(out);
                    if (successor.lowlink < target.lowlink) {
                        target.lowlink = successor.lowlink;
                        target.lowlinkSource = successor.lowlinkSource;
                    }
                } else if (this.pool.contains(successor)) {
                    if (successor.index < target.lowlink) {
                        target.lowlink = successor.index;
                        target.lowlinkSource = out.info();
                    }
                }
            }
        }

        if (target.lowlink == target.index) {
            var next = this.stack.pop();
            var nextTarget = next.target();
            var cycle = new ArrayList<I>();
            boolean first = true;
            while (nextTarget != target) {
                if (first) {
                    cycle.add(nextTarget.lowlinkSource);
                    first = false;
                }
                cycle.add(next.info());
                // keep the pool in sync with the stack
                this.pool.remove(nextTarget);
                this.connect.remove(nextTarget);
                next = this.stack.pop();
                nextTarget = next.target();
            }
            // keep the pool in sync with the stack
            this.pool.remove(nextTarget);
            this.connect.remove(nextTarget);
            if (cycle.size() > 1) {
                this.result.add(cycle);
            }
        }
    }

    /** Current index in the search. */
    private int index;
    /** Search stack of links. */
    private final Stack<Link<I>> stack = new Stack<>();
    /** Set representation of {@link #stack} for efficiency of membership test. */
    private final Set<Record<I>> pool = new HashSet<>();
    /** Collected list of cycles. */
    private final List<List<I>> result = new ArrayList<>();

    /** Record for a node during the search. */
    private static class Record<I> {
        Record(Node node) {
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
            Node otherNode;
            if (obj instanceof Record r) {
                otherNode = r.node;
            } else {
                assert obj instanceof Node;
                otherNode = (Node) obj;
            }
            return this.node.equals(otherNode);
        }

        @Override
        public String toString() {
            return "Record[" + this.node + "," + this.index + "," + this.lowlink + "]";
        }

        /** Node on which this record is based. */
        private final Node node;
        /** Lowest index of the reachable records. */
        int lowlink = -1;
        /** Edge whose target gave rise to index {@link #lowlink}.
         * This is the last edge in the cycle. */
        I lowlinkSource;
        /** Index of this record. */
        int index = -1;
    }

    /** Link to the next record, with information to be returned as part of the detected cycle. */
    protected static record Link<I>(I info, Record<I> target) {
        /** Constructs a link with {@code null} info. */
        public Link(Record<I> record) {
            this(null, record);
        }
    }

    /** Connection structure. */
    protected class Connect extends LinkedHashMap<Record<I>,List<Link<I>>> {
        /** Constructor for subclasses. */
        public Connect() {
            // empty by design
        }
    }
}
