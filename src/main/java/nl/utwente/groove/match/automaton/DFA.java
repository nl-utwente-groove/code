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
package nl.utwente.groove.match.automaton;

import static nl.utwente.groove.graph.Direction.INCOMING;
import static nl.utwente.groove.graph.Direction.OUTGOING;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.Direction;
import nl.utwente.groove.util.AIGenerated;

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

    /**
     * Returns the minimised automaton depending on this one.
     * Dead states (from which no final state is reachable) are pruned,
     * except for the start state; equivalent states are merged.
     */
    public DFA toMinimised() {
        Set<DFAState> kept = computeKeptStates();
        Map<DFAState,Cell> partition = computePartition(kept);
        return computeQuotient(partition, kept);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (DFAState state : getStates()) {
            result.append(String.format("%s, final=%s%n", state, state.isFinal()));
            for (Direction dir : Direction.values()) {
                Map<TypeLabel,DFAState> labelMap = state.getLabelMap().get(dir);
                assert labelMap != null; // the label map is filled for all directions
                for (Map.Entry<TypeLabel,DFAState> labelEntry : labelMap.entrySet()) {
                    result
                        .append(dir == OUTGOING
                            ? "   "
                            : "  -");
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
        Map<TypeLabel,DFAState> outMap = getStartState().getLabelMap().get(OUTGOING);
        assert outMap != null; // the label map is filled for all directions
        Map<TypeLabel,DFAState> inMap = getStartState().getLabelMap().get(INCOMING);
        assert inMap != null; // the label map is filled for all directions
        return outMap.isEmpty() && inMap.isEmpty() && !getStartState().isFinal();
    }

    /** Tests if this automaton is isomorphic with another. */
    public boolean isEquivalent(DFA other) {
        if (getStates().size() != other.getStates().size()) {
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
            Map<TypeLabel,DFAState> oneLabelMap = one.getLabelMap().get(dir);
            assert oneLabelMap != null; // the label map is filled for all directions
            Map<TypeLabel,DFAState> twoLabelMap = two.getLabelMap().get(dir);
            assert twoLabelMap != null; // the label map is filled for all directions
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
     * Computes the states to be kept in the minimised automaton: the live states,
     * i.e., those from which a final state is reachable, plus the start state.
     * Transitions into states that are not kept are dropped from the quotient,
     * which is language-preserving, and ensures that the quotient is minimal
     * as a partial automaton (missing and dead transitions become indistinguishable).
     */
    @AIGenerated("Claude Fable 5, 2026-08")
    private Set<DFAState> computeKeptStates() {
        // collect the predecessors of every state
        Map<DFAState,Set<DFAState>> predMap = new LinkedHashMap<>();
        for (DFAState s : getStates()) {
            predMap.put(s, new LinkedHashSet<>());
        }
        for (DFAState s : getStates()) {
            for (Map<TypeLabel,DFAState> labelMap : s.getLabelMap().values()) {
                for (DFAState succ : labelMap.values()) {
                    Set<DFAState> succPreds = predMap.get(succ);
                    assert succPreds != null; // the predecessor map covers all states
                    succPreds.add(s);
                }
            }
        }
        // backward reachability from the final states
        Set<DFAState> result = new LinkedHashSet<>();
        Deque<DFAState> queue = new ArrayDeque<>();
        for (DFAState s : getStates()) {
            if (s.isFinal()) {
                result.add(s);
                queue.add(s);
            }
        }
        while (!queue.isEmpty()) {
            DFAState s = queue.poll();
            assert s != null; // the queue is non-empty
            Set<DFAState> preds = predMap.get(s);
            assert preds != null; // the predecessor map covers all states
            for (DFAState pred : preds) {
                if (result.add(pred)) {
                    queue.add(pred);
                }
            }
        }
        result.add(getStartState());
        return result;
    }

    /**
     * Computes the coarsest partition of the kept states of this automaton into cells
     * of equivalent states, by Moore-style partition refinement: starting from the
     * division into final and non-final states, every cell is split according to
     * the successor signatures of its states (see {@link #computeSignature}),
     * until no cell splits any more. Two states thus end up in the same cell if they
     * are both final or both non-final and, for every direction and label, either
     * both lack a transition to a kept state or their successors are again in the same cell.
     * <p>
     * The state order of this automaton is preserved throughout, so the resulting
     * partition, and hence the numbering of the quotient states, is deterministic.
     * @param kept the states to be partitioned; transitions to other states are ignored
     */
    @AIGenerated("Claude Fable 5, 2026-08")
    private Map<DFAState,Cell> computePartition(Set<DFAState> kept) {
        // initial partition: final versus non-final states
        Map<DFAState,Cell> result = new LinkedHashMap<>();
        Cell finalCell = new Cell(0);
        Cell nonFinalCell = new Cell(1);
        for (DFAState s : kept) {
            Cell cell = s.isFinal()
                ? finalCell
                : nonFinalCell;
            cell.states().add(s);
            result.put(s, cell);
        }
        boolean refined;
        do {
            refined = false;
            Map<DFAState,Cell> next = new LinkedHashMap<>();
            int cellCount = 0;
            for (Cell cell : new LinkedHashSet<>(result.values())) {
                // split the cell according to the signatures of its states
                Map<Signature,Cell> split = new LinkedHashMap<>();
                for (DFAState s : cell.states()) {
                    Signature sig = computeSignature(s, result);
                    Cell newCell = split.get(sig);
                    if (newCell == null) {
                        split.put(sig, newCell = new Cell(cellCount++));
                    }
                    newCell.states().add(s);
                    next.put(s, newCell);
                }
                refined |= split.size() > 1;
            }
            result = next;
        } while (refined);
        return result;
    }

    /**
     * Computes the signature of a state with respect to a given partition:
     * per direction, the mapping from transition labels to the numbers of the
     * cells containing the corresponding successor states.
     * Transitions to states outside the partition are ignored.
     */
    @AIGenerated("Claude Fable 5, 2026-08")
    private Signature computeSignature(DFAState state, Map<DFAState,Cell> partition) {
        Map<Direction,Map<TypeLabel,Integer>> succCells = new EnumMap<>(Direction.class);
        for (Direction dir : Direction.values()) {
            Map<TypeLabel,DFAState> labelMap = state.getLabelMap().get(dir);
            assert labelMap != null; // the label map is filled for all directions
            Map<TypeLabel,Integer> dirCells = new HashMap<>();
            for (Map.Entry<TypeLabel,DFAState> entry : labelMap.entrySet()) {
                Cell succCell = partition.get(entry.getValue());
                if (succCell != null) {
                    dirCells.put(entry.getKey(), succCell.number());
                }
            }
            succCells.put(dir, dirCells);
        }
        return new Signature(succCells);
    }

    /**
     * Computes the quotient of this automaton, based on a given state partition.
     * @param kept the states covered by the partition; transitions to other states are dropped
     */
    private DFA computeQuotient(Map<DFAState,Cell> partition, Set<DFAState> kept) {
        Map<Cell,DFAState> newStateMap = new LinkedHashMap<>();
        // create an image for the start cell
        Cell startCell = partition.get(getStartState());
        assert startCell != null; // the partition covers all states
        Set<RegNode> startNodes = startCell.flatten();
        DFA result = new DFA(this.dir, startNodes, getStartState().isFinal());
        newStateMap.put(startCell, result.getStartState());
        // create images for the other cells of the partition
        // Note that distinct cells have distinct flattened node sets:
        // the language of a state is the union of the languages of its nodes,
        // so equal node sets would imply equal languages and hence the same cell
        for (Map.Entry<DFAState,Cell> cellEntry : partition.entrySet()) {
            Cell cell = cellEntry.getValue();
            if (!newStateMap.containsKey(cell)) {
                newStateMap
                    .put(cell, result.addState(cell.flatten(), cellEntry.getKey().isFinal()));
            }
        }
        // copy the successor maps
        for (Map.Entry<Cell,DFAState> newStateEntry : newStateMap.entrySet()) {
            DFAState oldState = newStateEntry.getKey().states().get(0);
            DFAState newState = newStateEntry.getValue();
            for (Direction dir : Direction.values()) {
                Map<TypeLabel,DFAState> labelMap = oldState.getLabelMap().get(dir);
                assert labelMap != null; // the label map is filled for all directions
                for (Map.Entry<TypeLabel,DFAState> entry : labelMap.entrySet()) {
                    if (!kept.contains(entry.getValue())) {
                        continue;
                    }
                    DFAState newSucc = newStateMap.get(partition.get(entry.getValue()));
                    assert newSucc != null; // all cells have images
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

    /**
     * Successor signature of a state with respect to a partition:
     * per direction, the mapping from labels to successor cell numbers.
     */
    private record Signature(Map<Direction,Map<TypeLabel,Integer>> succCells) {
        // no additional functionality
    }

    /**
     * Numbered cell of a state partition.
     * Cells are compared by identity; the number distinguishes
     * the cells of one partition from one another in signatures.
     */
    private static final class Cell {
        Cell(int number) {
            this.number = number;
        }

        /** Returns the number of this cell. */
        int number() {
            return this.number;
        }

        /** Returns the (modifiable) list of states in this cell, in state order. */
        List<DFAState> states() {
            return this.states;
        }

        /** Returns the set of all nodes in this cell. */
        Set<RegNode> flatten() {
            Set<RegNode> result = new HashSet<>();
            for (DFAState state : this.states) {
                result.addAll(state.getNodes());
            }
            return result;
        }

        private final int number;
        private final List<DFAState> states = new ArrayList<>();
    }
}
