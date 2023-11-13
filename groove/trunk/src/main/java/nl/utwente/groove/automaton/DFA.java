/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.automaton;

import static nl.utwente.groove.graph.Direction.INCOMING;
import static nl.utwente.groove.graph.Direction.OUTGOING;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.Direction;

/**
 * Deterministic automaton optimised towards matching.
 * @author Arend Rensink
 * @version $Revision$
 */
public class DFA {
    /**
     * Creates an automaton with a start state constructed from a given set of regular nodes.
     * @param dir the direction in which this automaton goes over a graph
     */
    public DFA(Direction dir, Set<RegNode> startNodes, boolean isFinal) {
        this.dir = dir;
        this.startState = new DFAState(0, startNodes, true, isFinal);
        this.stateMap.put(startNodes, this.startState);
    }

    /** Creates an automaton with a start state corresponding to a given regular node.
     * @param dir the direction in which this automaton goes over the graph
     */
    public DFA(Direction dir, RegNode startNode, boolean isFinal) {
        this(dir, Collections.singleton(startNode), isFinal);
    }

    /** Returns the normalised state corresponding to a set of regular automaton nodes. */
    public DFAState getState(Set<RegNode> nodes) {
        return this.stateMap.get(nodes);
    }

    /** Returns the direction in which this automaton goes over the graph. */
    public Direction getDirection() {
        return this.dir;
    }

    /** Returns the collection of states of this automaton. */
    public Collection<DFAState> getStates() {
        return this.stateMap.values();
    }

    /** Adds a normalised state corresponding to a given set of regular automaton nodes. */
    public DFAState addState(Set<RegNode> nodes, boolean isFinal) {
        DFAState result = new DFAState(this.stateMap.size(), nodes, false, isFinal);
        DFAState oldState = this.stateMap.put(nodes, result);
        assert oldState == null;
        return result;
    }

    /** Returns the start state of this automaton. */
    public DFAState getStartState() {
        return this.startState;
    }

    //
    //    /** Adds a label guard to this automaton. */
    //    public void addLabelGuard(TypeGuard guard) {
    //        this.guardList.add(guard);
    //    }
    //
    //    /** Returns the set of label guards associated with this automaton. */
    //    public List<TypeGuard> getLabelGuards() {
    //        return this.guardList;
    //    }

    /** Returns the minimised automaton depending on this one. */
    public DFA toMinimised() {
        Set<Cell> equivalence = computeEquivalence();
        Map<DFAState,Cell> partition = computePartition(equivalence);
        return computeQuotient(partition);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (DFAState state : getStates()) {
            result.append(String.format("%s%n", state));
            for (Direction dir : Direction.values()) {
                for (Map.Entry<TypeLabel,DFAState> labelEntry : state.getLabelMap()
                    .get(dir)
                    .entrySet()) {
                    result.append(dir == OUTGOING ? "   " : "  -");
                    result.append(labelEntry.getKey());
                    result.append(" --> ");
                    result.append(labelEntry.getValue());
                    result.append('\n');
                }
            }
        }
        return result.toString();
    }

    /** Tests if this DFA has an empty language. */
    public boolean isEmpty() {
        return getStartState().getLabelMap()
            .get(OUTGOING)
            .isEmpty()
            && getStartState().getLabelMap()
                .get(INCOMING)
                .isEmpty()
            && !getStartState().isFinal();
    }

    /** Tests if this automaton is isomorphic with another. */
    public boolean isEquivalent(DFA other) {
        if (getStates().size() != other.getStates()
            .size()) {
            return false;
        }
        boolean result = true;
        Map<DFAState,DFAState> isoMap = new HashMap<>();
        Set<Pair> newPairs = new HashSet<>();
        isoMap.put(getStartState(), other.getStartState());
        newPairs.add(new Pair(getStartState(), other.getStartState()));
        do {
            Iterator<Pair> newIter = newPairs.iterator();
            Pair current = newIter.next();
            newIter.remove();
            Set<Pair> targetPairs = compareStates(current);
            if (targetPairs == null) {
                result = false;
            } else {
                for (Pair pair : targetPairs) {
                    DFAState old = isoMap.put(pair.one(), pair.two());
                    if (old == null) {
                        newPairs.add(pair);
                    } else {
                        result = old == pair.two();
                    }
                }
            }
        } while (result && !newPairs.isEmpty());
        return result;
    }

    /** Returns a recogniser for this automaton, working on a given graph. */
    public Recogniser getRecogniser(HostGraph graph) {
        if (this.recogniser == null || this.recogniser.getGraph() != graph) {
            this.recogniser = new Recogniser(this, graph);
        }
        return this.recogniser;
    }

    /**
     * Compares two normal states.
     * Returns a set of target state pairs reachable by following equi-labelled
     * transitions, or {@code null} if there is no one-to-one correspondence
     * between the transitions.
     */
    private Set<Pair> compareStates(Pair statePair) {
        Set<Pair> result = new HashSet<>();
        DFAState one = statePair.one();
        DFAState two = statePair.two();
        if (one.isFinal() != two.isFinal()) {
            return null;
        }
        for (Direction dir : Direction.values()) {
            Map<TypeLabel,DFAState> oneLabelMap = one.getLabelMap()
                .get(dir);
            Map<TypeLabel,DFAState> twoLabelMap = two.getLabelMap()
                .get(dir);
            if (oneLabelMap.size() != twoLabelMap.size()) {
                return null;
            }
            for (Map.Entry<TypeLabel,DFAState> oneEntry : oneLabelMap.entrySet()) {
                TypeLabel key = oneEntry.getKey();
                DFAState twoTarget = twoLabelMap.get(key);
                if (twoTarget == null) {
                    return null;
                }
                result.add(new Pair(oneEntry.getValue(), twoTarget));
            }
        }
        return result;
    }

    /**
     * Computes the equivalence relation of the states of this automaton,
     * based on the states incoming and outgoing label and variable transitions.
     */
    private Set<Cell> computeEquivalence() {
        Set<Cell> result = new HashSet<>();
        // declare and initialise the dependencies
        // for each state pair, this records the previous state pairs
        // that are distinct if this state pair is distinct.
        Map<Cell,Set<Cell>> depMap = new HashMap<>();
        for (DFAState i : getStates()) {
            for (DFAState j : getStates()) {
                if (i.getNumber() < j.getNumber()) {
                    Cell ijPair = new Cell(i, j);
                    Set<Cell> depSet = new HashSet<>();
                    depSet.add(ijPair);
                    depMap.put(ijPair, depSet);
                    // states are equivalent until proven otherwise
                    result.add(ijPair);
                }
            }
        }
        for (DFAState i : getStates()) {
            for (DFAState j : getStates()) {
                if (i.getNumber() < j.getNumber()) {
                    Cell ijPair = new Cell(i, j);
                    Set<Cell> depSet = depMap.remove(ijPair);
                    assert depSet != null;
                    boolean distinct = i.isFinal() != j.isFinal();
                    if (!distinct) {
                        for (Direction dir : Direction.values()) {
                            if (areDistinct(i.getLabelMap()
                                .get(dir),
                                j.getLabelMap()
                                    .get(dir),
                                depSet,
                                depMap)) {
                                distinct = true;
                                break;
                            }
                        }
                    }
                    if (distinct) {
                        result.removeAll(depSet);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Tests if two states can be distinguished on the basis of a mapping to next states.
     * If no distinction exists, the pair is added as a dependent to all
     * corresponding pairs of target states.
     */
    private <K> boolean areDistinct(Map<K,DFAState> iMap, Map<K,DFAState> jMap, Set<Cell> ijDepSet,
        Map<Cell,Set<Cell>> depMap) {
        boolean result = false;
        if (!iMap.keySet()
            .equals(jMap.keySet())) {
            result = true;
        } else {
            for (Map.Entry<K,DFAState> iEntry : iMap.entrySet()) {
                DFAState iSucc = iEntry.getValue();
                DFAState jSucc = iMap.get(iEntry.getKey());
                Cell ijTargetPair = new Cell(iSucc, jSucc);
                Set<Cell> ijTargetDep = depMap.get(ijTargetPair);
                if (ijTargetDep == null) {
                    result = true;
                    break;
                } else {
                    ijTargetDep.addAll(ijDepSet);
                }
            }
        }
        return result;
    }

    private Map<DFAState,Cell> computePartition(Set<Cell> equivalence) {
        Map<DFAState,Cell> result = new HashMap<>();
        // initially the partition is discrete
        getStates().stream()
            .forEach(s -> result.put(s, new Cell(s)));
        for (Cell equiv : equivalence) {
            assert equiv.size() == 2;
            Iterator<DFAState> distIter = equiv.iterator();
            DFAState s1 = distIter.next();
            DFAState s2 = distIter.next();
            Cell s1Cell = result.get(s1);
            Cell s2Cell = result.get(s2);
            // merge the cells if they are not already the same
            if (s1Cell != s2Cell) {
                s1Cell.addAll(s2Cell);
                for (DFAState s2Sib : s2Cell) {
                    result.put(s2Sib, s1Cell);
                }
            }
        }
        return result;
    }

    /** Computes the quotient of this automaton, based on a given state partition. */
    private DFA computeQuotient(Map<DFAState,Cell> partition) {
        Map<Cell,DFAState> newStateMap = new HashMap<>();
        // create an image for the start cell
        Cell startCell = partition.remove(getStartState());
        Set<RegNode> startNodes = startCell.flatten();
        DFA result = new DFA(this.dir, startNodes, getStartState().isFinal());
        newStateMap.put(startCell, result.getStartState());
        // create images for the other cells of the partition
        for (Map.Entry<DFAState,Cell> cellEntry : partition.entrySet()) {
            Cell cell = cellEntry.getValue();
            if (!newStateMap.containsKey(cell)) {
                newStateMap.put(cell,
                    result.addState(cell.flatten(),
                        cellEntry.getKey()
                            .isFinal()));
            }
        }
        // copy the successor maps
        for (Map.Entry<Cell,DFAState> newStateEntry : newStateMap.entrySet()) {
            DFAState oldState = newStateEntry.getKey()
                .iterator()
                .next();
            DFAState newState = newStateEntry.getValue();
            for (Direction dir : Direction.values()) {
                for (Map.Entry<TypeLabel,DFAState> entry : oldState.getLabelMap()
                    .get(dir)
                    .entrySet()) {
                    DFAState newSucc = newStateMap.get(partition.get(entry.getValue()));
                    newState.addSuccessor(dir, entry.getKey(), newSucc);
                }
            }
        }
        return result;
    }

    /** The direction in which this automaton goes over the graph. */
    private final Direction dir;
    /** The start state of this automaton. */
    private final DFAState startState;
    /** Mapping from regular automaton nodes to states. */
    private final Map<Set<RegNode>,DFAState> stateMap = new LinkedHashMap<>();
    /** Currently instantiated recogniser for this automaton. */
    private Recogniser recogniser;

    private record Pair(DFAState one, DFAState two) {
        // no additional functionality
    }

    private static class Cell extends HashSet<DFAState> {
        /** Constructs a singleton cell. */
        Cell(DFAState one) {
            this.add(one);
        }

        /** Constructs a cell consisting of two states. */
        Cell(DFAState one, DFAState two) {
            this.add(one);
            this.add(two);
        }

        /** Returns the set of all nodes in this cell. */
        Set<RegNode> flatten() {
            Set<RegNode> result = new HashSet<>();
            for (DFAState state : this) {
                result.addAll(state.getNodes());
            }
            return result;
        }
    }
}
