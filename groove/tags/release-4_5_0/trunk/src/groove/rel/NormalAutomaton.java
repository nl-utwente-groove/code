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
package groove.rel;

import static groove.rel.Direction.FORWARD;
import groove.graph.TypeGuard;
import groove.graph.TypeLabel;
import groove.util.Duo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Deterministic automaton optimised towards matching.
 * @author Arend Rensink
 * @version $Revision $
 */
public class NormalAutomaton {
    /** Creates an automaton with a start state constructed from a given set of regular nodes. */
    public NormalAutomaton(Set<RegNode> startNodes, boolean isFinal) {
        this.startState = new NormalState(0, startNodes, true, isFinal);
        this.stateMap.put(startNodes, this.startState);
    }

    /** Creates an automaton with a start state corresponding to a given regular node. */
    public NormalAutomaton(RegNode startNode, boolean isFinal) {
        this(Collections.singleton(startNode), isFinal);
    }

    /** Returns the normalised state corresponding to a set of regular automaton nodes. */
    public NormalState getState(Set<RegNode> nodes) {
        return this.stateMap.get(nodes);
    }

    /** Returns the collection of states of this automaton. */
    public Collection<NormalState> getStates() {
        return this.stateMap.values();
    }

    /** Adds a normalised state corresponding to a given set of regular automaton nodes. */
    public NormalState addState(Set<RegNode> nodes, boolean isFinal) {
        NormalState result =
            new NormalState(this.stateMap.size(), nodes, false, isFinal);
        NormalState oldState = this.stateMap.put(nodes, result);
        assert oldState == null;
        return result;
    }

    /** Returns the start state of this automaton. */
    public NormalState getStartState() {
        return this.startState;
    }

    /** Adds a label guard to this automaton. */
    public void addLabelGuard(TypeGuard guard) {
        this.guardList.add(guard);
    }

    /** Returns the set of label guards associated with this automaton. */
    public List<TypeGuard> getLabelGuards() {
        return this.guardList;
    }

    /** Returns the minimised automaton depending on this one. */
    public NormalAutomaton getMinimised() {
        Set<Set<NormalState>> equivalence = computeEquivalence();
        Map<NormalState,Set<NormalState>> partition =
            computePartition(equivalence);
        return computeQuotient(partition);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (NormalState state : getStates()) {
            result.append(String.format("%s%n", state));
            for (Direction dir : Direction.all) {
                for (Map.Entry<TypeLabel,NormalState> labelEntry : state.getLabelMap().get(
                    dir).entrySet()) {
                    result.append(dir == FORWARD ? "   " : "  -");
                    result.append(labelEntry.getKey());
                    result.append(" --> ");
                    result.append(labelEntry.getValue());
                    result.append('\n');
                }
                for (Map.Entry<LabelVar,NormalState> labelEntry : state.getVarMap().get(
                    dir).entrySet()) {
                    result.append(dir == FORWARD ? "   " : "  -");
                    result.append(labelEntry.getKey());
                    result.append(" --> ");
                    result.append(labelEntry.getValue());
                    result.append('\n');
                }
            }
        }
        return result.toString();
    }

    /** Tests if this automaton is isomorphic with another. */
    public boolean isEquivalent(NormalAutomaton other) {
        if (!getLabelGuards().equals(other.getLabelGuards())) {
            return false;
        }
        if (getStates().size() != other.getStates().size()) {
            return false;
        }
        boolean result = true;
        Map<NormalState,NormalState> isoMap =
            new HashMap<NormalState,NormalState>();
        Set<Duo<NormalState>> newPairs = new HashSet<Duo<NormalState>>();
        isoMap.put(getStartState(), other.getStartState());
        newPairs.add(Duo.newDuo(getStartState(), other.getStartState()));
        do {
            Iterator<Duo<NormalState>> newIter = newPairs.iterator();
            Duo<NormalState> current = newIter.next();
            newIter.remove();
            Set<Duo<NormalState>> targetPairs = compareStates(current);
            if (targetPairs == null) {
                result = false;
            } else {
                for (Duo<NormalState> pair : targetPairs) {
                    NormalState old = isoMap.put(pair.one(), pair.two());
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

    /**
     * Compares two normal states.
     * Returns a set of target state pairs reachable by following equi-labelled
     * transitions, or {@code null} if there is no one-to-one correspondence 
     * between the transitions.
     */
    private Set<Duo<NormalState>> compareStates(Duo<NormalState> statePair) {
        Set<Duo<NormalState>> result = new HashSet<Duo<NormalState>>();
        NormalState one = statePair.one();
        NormalState two = statePair.two();
        if (one.isFinal() != two.isFinal()) {
            return null;
        }
        for (Direction dir : Direction.all) {
            Map<TypeLabel,NormalState> oneLabelMap = one.getLabelMap().get(dir);
            Map<TypeLabel,NormalState> twoLabelMap = two.getLabelMap().get(dir);
            if (oneLabelMap.size() != twoLabelMap.size()) {
                return null;
            }
            for (Map.Entry<TypeLabel,NormalState> oneEntry : oneLabelMap.entrySet()) {
                TypeLabel key = oneEntry.getKey();
                NormalState twoTarget = twoLabelMap.get(key);
                if (twoTarget == null) {
                    return null;
                }
                result.add(Duo.newDuo(oneEntry.getValue(), twoTarget));
            }
            Map<LabelVar,NormalState> oneVarMap = one.getVarMap().get(dir);
            Map<LabelVar,NormalState> twoVarMap = two.getVarMap().get(dir);
            if (oneVarMap.size() != twoVarMap.size()) {
                return null;
            }
            for (Map.Entry<LabelVar,NormalState> oneEntry : oneVarMap.entrySet()) {
                LabelVar key = oneEntry.getKey();
                NormalState twoTarget = twoVarMap.get(key);
                if (twoTarget == null) {
                    return null;
                }
                result.add(Duo.newDuo(oneEntry.getValue(), twoTarget));
            }
        }
        return result;
    }

    /** 
     * Computes the equivalence relation of the states of this automaton,
     * based on the states incoming and outgoing label and variable transitions.
     */
    private Set<Set<NormalState>> computeEquivalence() {
        Set<Set<NormalState>> result = new HashSet<Set<NormalState>>();
        // declare and initialise the dependencies 
        // for each state pair, this records the previous state pairs
        // that are distinct if this state pair is distinct.
        Map<Set<NormalState>,Set<Set<NormalState>>> depMap =
            new HashMap<Set<NormalState>,Set<Set<NormalState>>>();
        for (NormalState i : getStates()) {
            for (NormalState j : getStates()) {
                if (i.getNumber() < j.getNumber()) {
                    Set<NormalState> ijPair =
                        new HashSet<NormalState>(Arrays.asList(i, j));
                    Set<Set<NormalState>> depSet =
                        new HashSet<Set<NormalState>>();
                    depSet.add(ijPair);
                    depMap.put(ijPair, depSet);
                    // states are equivalent until proven otherwise
                    result.add(ijPair);
                }
            }
        }
        for (NormalState i : getStates()) {
            for (NormalState j : getStates()) {
                if (i.getNumber() < j.getNumber()) {
                    Set<NormalState> ijPair =
                        new HashSet<NormalState>(Arrays.asList(i, j));
                    Set<Set<NormalState>> depSet = depMap.remove(ijPair);
                    assert depSet != null;
                    boolean distinct = i.isFinal() != j.isFinal();
                    if (!distinct) {
                        for (Direction dir : Direction.all) {
                            if (areDistinct(i.getLabelMap().get(dir),
                                j.getLabelMap().get(dir), depSet, depMap)) {
                                distinct = true;
                                break;
                            }
                            if (areDistinct(i.getVarMap().get(dir),
                                j.getVarMap().get(dir), depSet, depMap)) {
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
    private <K> boolean areDistinct(Map<K,NormalState> iMap,
            Map<K,NormalState> jMap, Set<Set<NormalState>> ijDepSet,
            Map<Set<NormalState>,Set<Set<NormalState>>> depMap) {
        boolean result = false;
        if (!iMap.keySet().equals(jMap.keySet())) {
            result = true;
        } else {
            for (Map.Entry<K,NormalState> iEntry : iMap.entrySet()) {
                NormalState iSucc = iEntry.getValue();
                NormalState jSucc = iMap.get(iEntry.getKey());
                Set<NormalState> ijTargetPair =
                    new HashSet<NormalState>(Arrays.asList(iSucc, jSucc));
                Set<Set<NormalState>> ijTargetDep = depMap.get(ijTargetPair);
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

    private Map<NormalState,Set<NormalState>> computePartition(
            Set<Set<NormalState>> equivalence) {
        Map<NormalState,Set<NormalState>> result =
            new HashMap<NormalState,Set<NormalState>>();
        // initially the partition is discrete
        for (NormalState state : getStates()) {
            Set<NormalState> cell = new HashSet<NormalState>();
            cell.add(state);
            result.put(state, cell);
        }
        for (Set<NormalState> equiv : equivalence) {
            assert equiv.size() == 2;
            Iterator<NormalState> distIter = equiv.iterator();
            NormalState s1 = distIter.next();
            NormalState s2 = distIter.next();
            Set<NormalState> s1Cell = result.get(s1);
            Set<NormalState> s2Cell = result.get(s2);
            // merge the cells if they are not already the same
            if (s1Cell != s2Cell) {
                s1Cell.addAll(s2Cell);
                for (NormalState s2Sib : s2Cell) {
                    result.put(s2Sib, s1Cell);
                }
            }
        }
        return result;
    }

    /** Computes the quotient of this automaton, based on a given state partition. */
    private NormalAutomaton computeQuotient(
            Map<NormalState,Set<NormalState>> partition) {
        Map<Set<NormalState>,NormalState> newStateMap =
            new HashMap<Set<NormalState>,NormalState>();
        // create an image for the start cell
        Set<NormalState> startCell = partition.remove(getStartState());
        Set<RegNode> startNodes = flatten(startCell);
        NormalAutomaton result =
            new NormalAutomaton(startNodes, getStartState().isFinal());
        newStateMap.put(startCell, result.getStartState());
        // create images for the other cells of the partition
        for (Map.Entry<NormalState,Set<NormalState>> cellEntry : partition.entrySet()) {
            Set<NormalState> cell = cellEntry.getValue();
            if (!newStateMap.containsKey(cell)) {
                newStateMap.put(
                    cell,
                    result.addState(flatten(cell), cellEntry.getKey().isFinal()));
            }
        }
        // copy the successor maps
        for (Map.Entry<Set<NormalState>,NormalState> newStateEntry : newStateMap.entrySet()) {
            NormalState oldState = newStateEntry.getKey().iterator().next();
            NormalState newState = newStateEntry.getValue();
            for (Direction dir : Direction.all) {
                for (Map.Entry<TypeLabel,NormalState> entry : oldState.getLabelMap().get(
                    dir).entrySet()) {
                    NormalState newSucc =
                        newStateMap.get(partition.get(entry.getValue()));
                    newState.addSuccessor(dir, entry.getKey(), newSucc);
                }
                for (Map.Entry<LabelVar,NormalState> entry : oldState.getVarMap().get(
                    dir).entrySet()) {
                    NormalState newSucc =
                        newStateMap.get(partition.get(entry.getValue()));
                    newState.addSuccessor(dir, entry.getKey(), newSucc);
                }
            }
        }
        // copy the label guards
        for (TypeGuard guard : getLabelGuards()) {
            result.addLabelGuard(guard);
        }
        return result;
    }

    private Set<RegNode> flatten(Set<NormalState> stateSet) {
        Set<RegNode> result = new HashSet<RegNode>();
        for (NormalState state : stateSet) {
            result.addAll(state.getNodes());
        }
        return result;
    }

    /** The start state of this automaton. */
    private final NormalState startState;
    /** Mapping from regular automaton nodes to states. */
    private final Map<Set<RegNode>,NormalState> stateMap =
        new LinkedHashMap<Set<RegNode>,NormalState>();
    private final List<TypeGuard> guardList = new ArrayList<TypeGuard>();
}
