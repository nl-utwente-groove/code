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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
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
            for (var cycle : findCycles()) {
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

    /** Constructs a new link object with given source, target and info object. */
    protected Link<I> newLink(Node source, Node target, I info) {
        return new Link<>(source, target, info);
    }

    /**
     * Detects the set of SCCs reachable from the first element of the
     * connection structure, and stores them in {@link #result}.
     * Afterwards removes all records reachable from that first
     * element.
     */
    private List<List<I>> findCycles() {
        if (!this.connect.isEmpty()) {
            this.result.clear();
            this.visited.clear();
            var start = this.connect.keySet().iterator().next();
            findCyclesFrom(start);
            this.visited.forEach(this.connect::remove);
        }
        return this.result;
    }

    /** Finds the SCCs reachable from the target of a given link, using DFS. */
    private void findCyclesFrom(Node start) {
        this.pool.add(start);
        this.visited.add(start);

        var outs = this.connect.get(start);
        // if outs is null, we have visited that node in a previous round
        // so it is sure that there are no cycles back to start
        if (outs != null) {
            for (var out : outs) {
                this.stack.push(out);
                var outTarget = out.target();
                if (!this.visited.contains(outTarget)) {
                    // a previously unseen node, recurse
                    findCyclesFrom(outTarget);
                } else if (this.pool.contains(outTarget)) {
                    // this target is somewhere upstream in the stack
                    collectCycle();
                }
                this.stack.pop();
            }
        }
        this.pool.remove(start);
    }

    private void collectCycle() {
        var cycle = new ArrayList<I>();
        var end = this.stack.peek().target();
        // traverse the stack backwards until end is the source
        var stop = false;
        for (int i = this.stack.size() - 1; !stop; i--) {
            Link<I> link = this.stack.elementAt(i);
            cycle.add(link.info());
            stop = link.source() == end;
        }
        this.result.add(cycle);
    }

    /** Search stack of links. */
    private final Stack<Link<I>> stack = new Stack<>();
    /** Set of source nodes of links on the stack. */
    private final Set<Node> pool = new HashSet<>();
    /** Set of visited nodes in the current search. */
    private final Set<Node> visited = new HashSet<>();
    /** Collected list of cycles. */
    private final List<List<I>> result = new ArrayList<>();

    /** Link to the next record, with information to be returned as part of the detected cycle. */
    protected static record Link<I>(Node source, Node target, I info) {
        // empty by design
    }

    /** Connection structure. */
    protected class Connect extends LinkedHashMap<Node,List<Link<I>>> {
        /** Constructor for subclasses. */
        public Connect() {
            // empty by design
        }
    }
}
