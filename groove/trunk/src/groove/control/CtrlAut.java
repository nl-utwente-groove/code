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

import static groove.graph.GraphRole.CTRL;
import groove.graph.AbstractGraph;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.util.NestedIterator;
import groove.util.TransformIterator;
import groove.view.FormatError;
import groove.view.FormatException;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
public class CtrlAut extends AbstractGraph<CtrlState,CtrlTransition> {
    /**
     * Constructs a new control automaton.
     * The start state and final state are automatically initialised.
     * @param name the name of the control automaton
     */
    public CtrlAut(String name) {
        super(name);
        this.startState = addState();
        this.finalState = addState();
        GraphInfo.setErrors(this, new ArrayList<FormatError>());
    }

    @Override
    public GraphRole getRole() {
        return CTRL;
    }

    @Override
    public CtrlAut newGraph(String name) {
        return new CtrlAut(getName());
    }

    @Override
    public boolean addNode(CtrlState node) {
        throw new UnsupportedOperationException("Use addState(CtrlState)");
    }

    @Override
    public boolean removeEdge(CtrlTransition edge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addEdgeWithoutCheck(CtrlTransition edge) {
        return addTransition(edge);
    }

    @Override
    public boolean removeNodeWithoutCheck(CtrlState node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CtrlAut clone() {
        return clone(null);
    }

    /** Clones this automaton, optionally making the intermediate states transient. */
    public CtrlAut clone(String action) {
        CtrlAut result = newGraph(getName());
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
                image = result.copyState(state, action);
            }
            image.setBoundVars(state.getBoundVars());
            stateMap.put(state, image);
        }
        for (CtrlTransition trans : edgeSet()) {
            CtrlState newSource = stateMap.get(trans.source());
            CtrlState newTarget = stateMap.get(trans.target());
            boolean exitsTransient =
                trans.isExitsTransient() || action != null
                    && newSource.isTransient() && trans.getCall().isOmega();
            result.addTransition(newSource, trans.label(), newTarget,
                exitsTransient);
            if (exitsTransient) {
                newSource.setExitGuard(trans.label().getGuard());
            }
        }
        result.getInfo().getErrors().addAll(this.getInfo().getErrors());
        return result;
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
     * A final parameter indicates if this transition exits a transient phase.
     * @return the new transition, or {@code null} if it was not added
     * due to a nondeterminism
     */
    CtrlTransition addTransition(CtrlState source, CtrlLabel label,
            CtrlState target, boolean exitsTransient) {
        CtrlTransition result =
            createTransition(source, label, target, exitsTransient);
        if (addTransition(result)) {
            return result;
        } else {
            return null;
        }
    }

    /** Adds a fresh, non-transient state to this control automaton.
     */
    CtrlState addState() {
        CtrlState result = createState(null);
        addState(result);
        return result;
    }

    /**
     * Adds a fresh state to this control automaton, based on an existing state.
     * The transient nature of the new state is copied from the original.
     * @param original the state whose transiency information should be copied
     * @param action if {@code true}, the new state should be transient in any case
     */
    CtrlState copyState(CtrlState original, String action) {
        CtrlState result;
        if (action == null) {
            result = createState(original.getAction());
            if (original.hasExitGuard()) {
                result.setExitGuard(original.getExitGuard());
            }
        } else {
            result = createState(action);
        }
        addState(result);
        return result;
    }

    private void addState(CtrlState state) {
        if (state.getNumber() == this.maxStateNr + 1) {
            this.maxStateNr++;
        }
        this.states.add(state);
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

    /** Returns the set of rules and transactions invoked by this automaton. */
    public Set<String> getRules() {
        Set<String> result = new HashSet<String>();
        for (CtrlTransition trans : edgeSet()) {
            CtrlCall call = trans.getCall();
            result.add(call.getName());
        }
        return result;
    }

    /** Factory method to create a control state for this automaton. 
     * @param action optional name of the transaction of which the new state is part
     */
    private CtrlState createState(String action) {
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
        return new CtrlState(this, action, stateNr);
    }

    /** Factory method for control transitions. */
    private CtrlTransition createTransition(CtrlState source, CtrlLabel label,
            CtrlState target, boolean exitsTransient) {
        return new CtrlTransition(source, label, target, exitsTransient);
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
        CtrlAut result = this;
        if (getInfo().getErrors().isEmpty()) {
            Set<Set<CtrlState>> equivalence = computeEquivalence();
            Map<CtrlState,Set<CtrlState>> partition =
                computePartition(equivalence);
            result = computeQuotient(partition);
            result.setBoundVars();
        }
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
                    Map<CtrlLabel,CtrlTransition> iOutMap =
                        getOutTransitions(i);
                    Map<CtrlLabel,CtrlTransition> jOutMap =
                        getOutTransitions(j);
                    boolean distinct =
                        i.isTransient() != j.isTransient()
                            || !iOutMap.keySet().equals(jOutMap.keySet());
                    if (!distinct) {
                        for (Map.Entry<CtrlLabel,CtrlTransition> iOutEntry : iOutMap.entrySet()) {
                            CtrlTransition iOut = iOutEntry.getValue();
                            CtrlTransition jOut =
                                jOutMap.get(iOutEntry.getKey());
                            if (iOut.isExitsTransient() != jOut.isExitsTransient()) {
                                distinct = true;
                                break;
                            }
                            if (iOut.target() != jOut.target()) {
                                Set<CtrlState> ijTargetPair =
                                    new HashSet<CtrlState>(Arrays.asList(
                                        iOut.target(), jOut.target()));
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

    private Map<CtrlLabel,CtrlTransition> getOutTransitions(CtrlState s)
        throws FormatException {
        Map<CtrlLabel,CtrlTransition> result =
            new HashMap<CtrlLabel,CtrlTransition>();
        for (CtrlTransition outTrans : s.getTransitions()) {
            CtrlLabel label = outTrans.label();
            CtrlTransition oldTrans = result.put(label, outTrans);
            if (oldTrans != null) {
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
        CtrlAut result = newGraph(getName());
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
                    CtrlState representative = cell.iterator().next();
                    image = result.copyState(representative, null);
                }
                stateMap.put(cell, image);
            }
        }
        for (CtrlTransition trans : edgeSet()) {
            CtrlState newSource = stateMap.get(partition.get(trans.source()));
            CtrlState newTarget = stateMap.get(partition.get(trans.target()));
            result.addTransition(newSource, trans.label(), newTarget,
                trans.isExitsTransient());
        }
        return result;
    }

    /** Computes and sets the bound variables of every state, based on the
     * input parameters of their outgoing transitions.
     */
    private void setBoundVars() {
        // compute the map of incoming transitions
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
                    modified |= sourceVars.add(targetVar);
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

    /** Returns the control program of this automaton (if any). */
    public String getProgram() {
        return this.program;
    }

    /** Sets the control program of this automaton. */
    public void setProgram(String program) {
        this.program = program;
    }

    /** The control program of this automaton (if any). */
    private String program;

    /** 
     * Tests if the automaton terminates deterministically.
     * This is the case if all omega-transitions are guarded by
     * all other outgoing rule calls.
     */
    public boolean isEndDeterministic() {
        boolean result = true;
        outer: for (CtrlTransition omegaTrans : getOmegaTransitions()) {
            Collection<CtrlCall> guard = omegaTrans.label().getGuard();
            for (CtrlTransition otherTrans : outEdgeSet(omegaTrans.source())) {
                if (!otherTrans.getCall().isOmega()
                    && !guard.contains(otherTrans.getCall())) {
                    result = false;
                    break outer;
                }
            }
        }
        return result;
    }

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
