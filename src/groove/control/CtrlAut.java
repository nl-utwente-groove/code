/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: ControlAutomaton.java,v 1.10 2008-01-30 11:13:57 fladder Exp $
 */
package groove.control;

import groove.graph.AbstractGraphShape;
import groove.graph.GraphCache;
import groove.util.NestedIterator;
import groove.util.TransformIterator;
import groove.view.FormatException;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class implements a control automaton graph.
 * The graph has {@link CtrlState}s for nodes and {@link CtrlTransition}s for edges.
 * Termination is modelled by omega-labelled transitions.
 * A control automaton is built up in several stages:
 * <ul>
 * <li> The object is constructed. This initialises the initial and final state.
 * <li> States, transitions and parameters are added. 
 * At this stage rules are just represented by names.
 * <li> The automaton is instantiated to a given rule system. 
 * This replaces all rule names by actual rules. Some more type checking 
 * is done at this stage.
 * </ul>
 * @author Arend Rensink
 */
public class CtrlAut extends AbstractGraphShape<GraphCache> {
    /**
     * Constructs a new control automaton.
     * The start state and final state are automatically initialised.
     */
    public CtrlAut() {
        this.startState = addState();
        this.finalState = addState();
    }

    /** Adds a control transition to this automaton. */
    boolean addTransition(CtrlTransition edge) {
        boolean result = this.transitions.add(edge);
        if (result && edge.getCall().isOmega()) {
            this.omegaTransitions.add(edge);
        }
        return result;
    }

    /** Removes a control transition from this automaton. */
    boolean removeTransition(CtrlTransition edge) {
        boolean result = this.transitions.remove(edge);
        if (result) {
            this.omegaTransitions.remove(edge);
        }
        return result;
    }

    /** 
     * Convenience method for adding a control transition between given states and with a given label.
     */
    CtrlTransition addTransition(CtrlState source, CtrlLabel label,
            CtrlState target) {
        CtrlTransition result = createTransition(source, label, target);
        addTransition(result);
        return result;
    }

    /** Adds a control state to this automaton. */
    boolean addState(CtrlState node) {
        if (node.getNumber() == this.maxStateNr + 1) {
            this.maxStateNr++;
        }
        return this.states.add(node);
    }

    /** Adds a fresh state to this control automaton. */
    CtrlState addState() {
        CtrlState result = createState();
        addState(result);
        return result;
    }

    @Override
    public Set<CtrlTransition> edgeSet() {
        return this.transitions;
    }

    public Set<CtrlState> nodeSet() {
        return this.states;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (CtrlState state : new TreeSet<CtrlState>(nodeSet())) {
            result.append(String.format("State %s, variables %s%n", state,
                state.getBoundVars()));
            result.append(state.getSchedule().toString());
            //            for (CtrlTransition trans : new TreeSet<CtrlTransition>(
            //                state.getTransitions())) {
            //                result.append("  " + trans + "\n");
            //            }
        }
        return result.toString();
    }

    /** Returns the set of omega transitions. */
    public Set<CtrlTransition> getOmegaTransitions() {
        return this.omegaTransitions;
    }

    /** Returns the start state of the automaton. */
    public CtrlState getStart() {
        return this.startState;
    }

    /** The start state of the automaton. */
    private final CtrlState startState;

    /** Returns the final  state of the automaton. */
    public CtrlState getFinal() {
        return this.finalState;
    }

    /** The final state of the automaton. */
    private final CtrlState finalState;

    /** Factory method to create a control state for this automaton. */
    private CtrlState createState() {
        int stateNr = this.maxStateNr + 1;
        boolean fresh = false;
        while (!fresh) {
            fresh = true;
            for (CtrlState state : nodeSet()) {
                if (stateNr == state.getNumber()) {
                    fresh = false;
                    stateNr++;
                    break;
                }
            }
        }
        this.maxStateNr = stateNr;
        return new CtrlState(stateNr);
    }

    /** Factory method for control transitions. */
    private CtrlTransition createTransition(CtrlState source, CtrlLabel label,
            CtrlState target) {
        return new CtrlTransition(source, label, target);
    }

    /** Constructs a copy of this automaton. */
    public CtrlAut copy() {
        CtrlAut result = new CtrlAut();
        Map<CtrlState,CtrlState> stateMap = new HashMap<CtrlState,CtrlState>();
        stateMap.put(getStart(), result.getStart());
        stateMap.put(getFinal(), result.getFinal());
        for (CtrlState state : nodeSet()) {
            CtrlState image;
            if (state.equals(getStart())) {
                image = result.getStart();
            } else if (state.equals(getFinal())) {
                image = result.getFinal();
            } else {
                image = result.addState();
            }
            image.setBoundVars(state.getBoundVars());
            stateMap.put(state, image);
        }
        for (CtrlTransition trans : edgeSet()) {
            result.addTransition(stateMap.get(trans.source()), trans.label(),
                stateMap.get(trans.target()));
        }
        return result;
    }

    /** 
     * Computes and returns a normalised version of this automaton.
     * Normalisation includes filling in the sets of bound variables.
     * The resulting automaton may fail to satisfy the assumption that
     * the start state has no incoming transitions, and hence it should no
     * longer be used in constructions.
     * @throws FormatException if the automaton is not deterministic
     */
    public CtrlAut normalise() throws FormatException {
        Set<Set<CtrlState>> equivalence = computeEquivalence();
        Map<CtrlState,Set<CtrlState>> partition = computePartition(equivalence);
        CtrlAut result = computeQuotient(partition);
        result.setBoundVars();
        return result;
    }

    /** Computes the equivalence relation of a the states of a given control automaton. */
    private Set<Set<CtrlState>> computeEquivalence() throws FormatException {
        Set<Set<CtrlState>> result = new HashSet<Set<CtrlState>>();
        // declare and initialise the dependencies 
        // for each state pair, this records the previous state pairs
        // that are distinct if this state pair is distinct.
        Map<Set<CtrlState>,Set<Set<CtrlState>>> depMap =
            new HashMap<Set<CtrlState>,Set<Set<CtrlState>>>();
        for (CtrlState i : nodeSet()) {
            for (CtrlState j : nodeSet()) {
                if (i.getNumber() < j.getNumber()) {
                    Set<CtrlState> ijPair =
                        new HashSet<CtrlState>(Arrays.asList(i, j));
                    Set<Set<CtrlState>> depSet = new HashSet<Set<CtrlState>>();
                    depSet.add(ijPair);
                    depMap.put(ijPair, depSet);
                    // states are equivalent until proven otherwise
                    result.add(ijPair);
                }
            }
        }
        for (CtrlState i : nodeSet()) {
            for (CtrlState j : nodeSet()) {
                if (i.getNumber() < j.getNumber()) {
                    Set<CtrlState> ijPair =
                        new HashSet<CtrlState>(Arrays.asList(i, j));
                    Set<Set<CtrlState>> depSet = depMap.remove(ijPair);
                    assert depSet != null;
                    Map<CtrlLabel,CtrlState> iOut = getOutTransitions(i);
                    Map<CtrlLabel,CtrlState> jOut = getOutTransitions(j);
                    boolean distinct = !iOut.keySet().equals(jOut.keySet());
                    if (!distinct) {
                        for (Map.Entry<CtrlLabel,CtrlState> iOutEntry : iOut.entrySet()) {
                            CtrlState iOutTarget = iOutEntry.getValue();
                            CtrlState jOutTarget = jOut.get(iOutEntry.getKey());
                            if (iOutTarget != jOutTarget) {
                                Set<CtrlState> ijTargetPair =
                                    new HashSet<CtrlState>(Arrays.asList(
                                        iOutTarget, jOutTarget));
                                Set<Set<CtrlState>> ijTargetDep =
                                    depMap.get(ijTargetPair);
                                if (ijTargetDep == null) {
                                    distinct = true;
                                    break;
                                } else {
                                    ijTargetDep.addAll(depSet);
                                }
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

    private Map<CtrlLabel,CtrlState> getOutTransitions(CtrlState s)
        throws FormatException {
        Map<CtrlLabel,CtrlState> result = new HashMap<CtrlLabel,CtrlState>();
        for (CtrlTransition outTrans : s.getTransitions()) {
            CtrlLabel label = outTrans.label();
            CtrlState oldState = result.put(label, outTrans.target());
            if (oldState != null) {
                throw new FormatException(
                    "State %s has multiple outgoing labels %s", s, label);
            }
        }
        return result;
    }

    private Map<CtrlState,Set<CtrlState>> computePartition(
            Set<Set<CtrlState>> equivalence) {
        Map<CtrlState,Set<CtrlState>> result =
            new HashMap<CtrlState,Set<CtrlState>>();
        // initially the partition is discrete
        for (CtrlState state : nodeSet()) {
            Set<CtrlState> cell = new HashSet<CtrlState>();
            cell.add(state);
            result.put(state, cell);
        }
        for (Set<CtrlState> equiv : equivalence) {
            assert equiv.size() == 2;
            Iterator<CtrlState> distIter = equiv.iterator();
            CtrlState s1 = distIter.next();
            CtrlState s2 = distIter.next();
            Set<CtrlState> s1Cell = result.get(s1);
            Set<CtrlState> s2Cell = result.get(s2);
            // merge the cells if they are not already the same
            if (s1Cell != s2Cell) {
                s1Cell.addAll(s2Cell);
                for (CtrlState s2Sib : s2Cell) {
                    result.put(s2Sib, s1Cell);
                }
            }
        }
        return result;
    }

    /** Computes the quotient of this automaton, based on a given state partition. */
    private CtrlAut computeQuotient(Map<CtrlState,Set<CtrlState>> partition) {
        CtrlAut result = new CtrlAut();
        Map<Set<CtrlState>,CtrlState> stateMap =
            new HashMap<Set<CtrlState>,CtrlState>();
        for (Set<CtrlState> cell : partition.values()) {
            CtrlState image;
            if (!stateMap.containsKey(cell)) {
                if (cell.contains(getStart())) {
                    image = result.getStart();
                    assert !cell.contains(getFinal());
                } else if (cell.contains(getFinal())) {
                    image = result.getFinal();
                } else {
                    image = result.addState();
                }
                stateMap.put(cell, image);
            }
        }
        for (CtrlTransition trans : edgeSet()) {
            CtrlState newSource = stateMap.get(partition.get(trans.source()));
            CtrlState newTarget = stateMap.get(partition.get(trans.target()));
            result.addTransition(newSource, trans.label(), newTarget);
        }
        return result;
    }

    /** Computes and sets the bound variables of every state, based on the
     * input parameters of their outgoing transitions.
     */
    private void setBoundVars() {
        Map<CtrlState,Set<CtrlTransition>> inMap =
            new HashMap<CtrlState,Set<CtrlTransition>>();
        for (CtrlState state : nodeSet()) {
            inMap.put(state, new HashSet<CtrlTransition>());
        }
        for (CtrlTransition trans : edgeSet()) {
            inMap.get(trans.target()).add(trans);
            trans.source().addBoundVars(trans.getInVars());
        }
        Queue<CtrlTransition> queue = new LinkedList<CtrlTransition>(edgeSet());
        while (!queue.isEmpty()) {
            CtrlTransition next = queue.poll();
            CtrlState source = next.source();
            Set<CtrlVar> sourceVars =
                new LinkedHashSet<CtrlVar>(source.getBoundVars());
            boolean modified = false;
            for (CtrlVar targetVar : next.target().getBoundVars()) {
                if (!next.getOutVars().contains(targetVar)) {
                    modified = sourceVars.add(targetVar);
                }
            }
            if (modified) {
                source.setBoundVars(sourceVars);
                queue.addAll(inMap.get(source));
            }
        }
    }

    /** The set of states of this control automaton. */
    private final Set<CtrlState> states = new HashSet<CtrlState>();

    /** The set of transitions of this control automaton. */
    private final Set<CtrlTransition> transitions = new TransitionSet();

    /** The set of omega transitions in this control automaton. */
    private final Set<CtrlTransition> omegaTransitions =
        new HashSet<CtrlTransition>();

    /** 
     * Assigns a list formal parameters to this automaton.
     * @param pars the list of formal parameters; non-{@code null} 
     */
    public final void setPars(List<CtrlPar.Var> pars) {
        this.pars = new ArrayList<CtrlPar.Var>(pars);
    }

    /**
     * Returns the set of parameters of this control automaton.
     * @return the set of parameters; may be {@code null}
     */
    public final List<CtrlPar.Var> getPars() {
        return this.pars;
    }

    /** List of formal parameters of this automaton; may be {@code null}. */
    private List<CtrlPar.Var> pars;

    /** Upper bound of the range of known consecutive state numbers. */
    private int maxStateNr = -1;

    /** 
     * Offers a modifiable view on the transitions stored in the states 
     * of this automaton.
     */
    private class TransitionSet extends AbstractSet<CtrlTransition> {
        @Override
        public boolean add(CtrlTransition e) {
            return e.source().addTransition(e);
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object o) {
            if (o instanceof CtrlTransition) {
                CtrlTransition trans = (CtrlTransition) o;
                return trans.equals(trans.source().getTransition(
                    trans.getRule()));
            } else {
                return false;
            }
        }

        @Override
        public Iterator<CtrlTransition> iterator() {
            return new NestedIterator<CtrlTransition>(
                new TransformIterator<CtrlState,Iterator<CtrlTransition>>(
                    nodeSet().iterator()) {
                    @Override
                    protected Iterator<CtrlTransition> toOuter(CtrlState from) {
                        return from.getTransitions().iterator();
                    }
                });
        }

        @Override
        public boolean remove(Object o) {
            if (o instanceof CtrlTransition) {
                CtrlTransition trans = (CtrlTransition) o;
                return trans.source().removeTransition(trans);
            } else {
                return false;
            }
        }

        @Override
        public int size() {
            int result = 0;
            for (CtrlState state : nodeSet()) {
                result += state.getTransitions().size();
            }
            return result;
        }
    }
}
