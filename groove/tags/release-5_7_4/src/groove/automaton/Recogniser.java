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
package groove.automaton;

import static groove.graph.Direction.OUTGOING;
import groove.automaton.RegAut.Result;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.host.HostNodeSet;
import groove.grammar.type.TypeLabel;
import groove.graph.Direction;
import groove.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Class that finds matches for a regular automaton
 * in a given graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Recogniser {
    /**
     * Constructs a recogniser for a given automaton, on a given graph and for a given
     * direction of search.
     */
    public Recogniser(DFA aut, HostGraph graph) {
        this.aut = aut;
        this.graph = graph;
        this.nextMap = new HashMap<>();
        this.reachMap = new HashMap<>();
    }

    /** Returns the host graph on which this recogniser works. */
    public HostGraph getGraph() {
        return this.graph;
    }

    /**
     * Returns the set of state pairs in this recogniser's host graph
     * between which a path exists that is accepted by this recogniser's
     * finite automaton.
     * @param from host node from which the path should start;
     * if {@code null}, all host nodes are tried
     * @param to host node in which the path should end;
     * if {@code null}, all host nodes are valid end nodes
     * @return set of host node pairs, ordered according to the
     */
    public Set<Result> getMatches(HostNode from, HostNode to) {
        assert to == null || from != null;
        Set<Result> result = new HashSet<>();
        if (from == null) {
            for (HostNode hn : this.graph.nodeSet()) {
                augmentReachMap(hn);
                addResults(result, hn);
            }
        } else {
            augmentReachMap(from);
            if (to == null) {
                addResults(result, from);
            } else if (getRelated(from).contains(to)) {
                result.add(createResult(from, to));
            }
        }
        return result;
    }

    /**
     * Computes all reachable nodes from a given start node,
     * and adds the result to the {@link #reachMap}.
     * @param fromNode the start node of the computation
     */
    private void augmentReachMap(HostNode fromNode) {
        Tuple from = createStartTuple(fromNode);
        Set<Tuple> fresh = new HashSet<>();
        // Map from explored tuples to all their predecessors
        // found during this exploration
        Map<Tuple,TupleSet> predMap = new HashMap<>();
        fresh.add(from);
        predMap.put(from, new TupleSet());
        // compute the predecessor map
        while (!fresh.isEmpty()) {
            Iterator<Tuple> pi = fresh.iterator();
            Tuple p = pi.next();
            pi.remove();
            if (this.reachMap.containsKey(p)) {
                continue;
            }
            for (Tuple np : getNext(p)) {
                TupleSet npPreds = predMap.get(np);
                if (npPreds == null) {
                    fresh.add(np);
                    predMap.put(np, npPreds = new TupleSet());
                }
                npPreds.add(p);
            }
        }
        // Set of all tuples with known reachable host nodes
        Map<Tuple,HostNodeSet> newReachMap = new HashMap<>();
        for (Tuple p : predMap.keySet()) {
            HostNodeSet ns = this.reachMap.get(p);
            if (ns == null) {
                this.reachMap.put(p, ns = new HostNodeSet());
                if (p.two()
                    .isFinal()) {
                    ns.add(p.one());
                }
            }
            newReachMap.put(p, ns);
        }
        propagateBackwards(predMap, newReachMap);
    }

    /**
     * Returns the set of successor tuples for a given tuple.
     * The set is retrieved from {@link #nextMap}, if it is there;
     * otherwise it is computed and inserted into {@link #nextMap}.
     * @param from the source tuple
     * @return the set of successor tuples
     */
    private TupleSet getNext(Tuple from) {
        TupleSet result = this.nextMap.get(from);
        if (result == null) {
            this.nextMap.put(from, result = new TupleSet());
            HostNode fromNode = from.one();
            Map<Direction,Map<TypeLabel,DFAState>> succMaps = from.two()
                .getLabelMap();
            // Add successor according to node type label
            DFAState ns = succMaps.get(Direction.OUTGOING)
                .get(fromNode.getType()
                    .label());
            if (ns != null) {
                result.add(new Tuple(fromNode, ns));
            }
            // Add successors according to edge labels
            for (Direction d : Direction.values()) {
                Map<TypeLabel,DFAState> succMap = succMaps.get(d);
                if (!succMap.isEmpty()) {
                    for (HostEdge e : d.edges(this.graph, fromNode)) {
                        DFAState s = succMap.get(e.label());
                        if (s != null) {
                            result.add(new Tuple(d.opposite(e), s));
                        }
                    }
                }
            }
        }
        return result;
    }

    private void addResults(Set<Result> result, HostNode from) {
        for (HostNode to : this.reachMap.get(createStartTuple(from))) {
            result.add(createResult(from, to));
        }
    }

    private HostNodeSet getRelated(HostNode from) {
        return this.reachMap.get(createStartTuple(from));
    }

    private void propagateBackwards(Map<Tuple,TupleSet> predMap, Map<Tuple,HostNodeSet> newReachMap) {
        while (!newReachMap.isEmpty()) {
            Iterator<Map.Entry<Tuple,HostNodeSet>> newReachIter = newReachMap.entrySet()
                .iterator();
            Map.Entry<Tuple,HostNodeSet> newReachEntry = newReachIter.next();
            newReachIter.remove();
            HostNodeSet toSet = newReachEntry.getValue();
            // add the toSet to all predecessors
            for (Tuple tp : predMap.get(newReachEntry.getKey())) {
                HostNodeSet tpToSet = newReachMap.get(tp);
                boolean tpFresh = tpToSet == null;
                if (tpFresh) {
                    tpToSet = new HostNodeSet();
                }
                assert tpToSet != null; // just initialised in case it was null
                for (HostNode hn : toSet) {
                    if (this.reachMap.get(tp)
                        .add(hn)) {
                        // it's a new reachable node, which has to be propagated
                        tpToSet.add(hn);
                    }
                }
                if (tpFresh && !tpToSet.isEmpty()) {
                    newReachMap.put(tp, tpToSet);
                }
            }
        }
    }

    /** Combines a given host node with the start state of the automaton. */
    private Tuple createStartTuple(HostNode node) {
        return new Tuple(node, this.aut.getStartState());
    }

    private Result createResult(HostNode from, HostNode to) {
        return this.aut.getDirection() == OUTGOING ? new Result(from, to) : new Result(to, from);
    }

    private final DFA aut;
    private final HostGraph graph;
    /** Mapping from explored product states to their sets of successors. */
    private final Map<Tuple,TupleSet> nextMap;
    /** Mapping from product states to sets of reachable host nodes. */
    private final Map<Tuple,HostNodeSet> reachMap;

    private static class Tuple extends Pair<HostNode,DFAState> {
        /** Constructs a product node. */
        public Tuple(HostNode node, DFAState state) {
            super(node, state);
        }
    }

    private static class TupleSet extends HashSet<Tuple> {
        // no added functionality
    }
}
