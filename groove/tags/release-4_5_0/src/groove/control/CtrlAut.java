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
import groove.control.CtrlCall.Kind;
import groove.graph.AbstractGraph;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.trans.Recipe;
import groove.trans.Rule;
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
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeNodeWithoutCheck(CtrlState node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CtrlAut clone() {
        return clone(null);
    }

    /** 
     * Clones this automaton, optionally making the intermediate states transient.
     * @param recipe if not {@code null}, the new automaton is to be the body
     * of a recipe with this name
     */
    public CtrlAut clone(Recipe recipe) {
        CtrlAut result =
            newGraph(recipe == null ? getName() : recipe.getFullName());
        CtrlMorphism morphism = new CtrlMorphism();
        morphism.putNode(getStart(), result.getStart());
        morphism.putNode(getFinal(), result.getFinal());
        for (CtrlState state : nodeSet()) {
            CtrlState image;
            if (state.equals(getStart())) {
                image = result.getStart();
            } else if (state.equals(getFinal())) {
                image = result.getFinal();
            } else if (recipe == null) {
                image = result.addState(state, state.getRecipe());
            } else {
                // determine if this state certainly terminates, in which case
                // it is already not transient any more
                boolean terminating =
                    state.getTransitions().size() == 1
                        && state.getTransitions().iterator().next().getCall().isOmega();
                image = result.addState(state, terminating ? null : recipe);
            }
            image.setBoundVars(state.getBoundVars());
            morphism.putNode(state, image);
        }
        for (CtrlTransition trans : edgeSet()) {
            CtrlLabel label = trans.label();
            CtrlCall call = trans.getCall();
            CtrlState newSource = morphism.getNode(trans.source());
            CtrlState newTarget = morphism.getNode(trans.target());
            CtrlGuard newGuard = label.getGuard().newGuard(morphism.edgeMap());
            Recipe newRecipe =
                recipe == null || call.isOmega() ? trans.getRecipe() : recipe;
            boolean newStart =
                recipe == null ? trans.isStart()
                        : newSource == result.getStart() || call.isOmega();
            CtrlLabel newLabel =
                new CtrlLabel(call, newGuard, newRecipe, newStart);
            CtrlTransition newTrans =
                newSource.addTransition(newLabel, newTarget);
            morphism.putEdge(trans, newTrans);
        }
        result.addErrors(this);
        return result;
    }

    /** Adds a control transition to this automaton. */
    void addOmega(CtrlTransition edge) {
        assert edge.getCall().isOmega();
        this.omegaTransitions.add(edge);
    }

    /** Removes a control transition from this automaton. */
    void removeOmega(CtrlTransition edge) {
        assert edge.getCall().isOmega();
        this.omegaTransitions.remove(edge);
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
     * @param recipe if {@code null}, gives the name of the recipe for which this is a transient state
     */
    CtrlState addState(CtrlState original, Recipe recipe) {
        CtrlState result;
        if (recipe == null) {
            result = createState(original.getRecipe());
            if (original.hasExitGuard()) {
                result.setExitGuard(original.getExitGuard());
            }
        } else {
            result = createState(recipe);
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

    /** Adds the errors of another automaton to this one. */
    void addErrors(CtrlAut other) {
        if (GraphInfo.hasErrors(other)) {
            GraphInfo.addErrors(this, GraphInfo.getErrors(other));
        }
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
    public Set<CtrlTransition> getOmegas() {
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

    /** 
     * Returns a guard consisting of all initial transitions of
     * this automaton, or {@code null} if the automaton is initially
     * terminating.
     */
    public CtrlGuard getInitGuard() {
        return getStart().getInit();
    }

    /** Returns the set of rules invoked by this automaton. */
    public Set<Rule> getRules() {
        Set<Rule> result = new HashSet<Rule>();
        for (CtrlTransition trans : edgeSet()) {
            CtrlCall call = trans.getCall();
            if (call.getKind() == Kind.RULE) {
                result.add(call.getRule());
            }
        }
        return result;
    }

    /** Factory method to create a control state for this automaton. 
     * @param recipe optional name of the recipe of which the new state is part
     */
    private CtrlState createState(Recipe recipe) {
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
        return new CtrlState(this, recipe, stateNr);
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
                            if (iOut.isStart() != jOut.isStart()) {
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
        Set<CtrlState> representatives = new HashSet<CtrlState>();
        Map<Set<CtrlState>,CtrlState> stateMap =
            new HashMap<Set<CtrlState>,CtrlState>();
        for (Map.Entry<CtrlState,Set<CtrlState>> cellEntry : partition.entrySet()) {
            Set<CtrlState> cell = cellEntry.getValue();
            CtrlState image;
            if (!stateMap.containsKey(cell)) {
                representatives.add(cellEntry.getKey());
                if (cell.contains(getStart())) {
                    image = result.getStart();
                    assert !cell.contains(getFinal());
                } else if (cell.contains(getFinal())) {
                    image = result.getFinal();
                } else {
                    CtrlState representative = cell.iterator().next();
                    image = result.addState(representative, null);
                }
                stateMap.put(cell, image);
            }
        }
        Map<CtrlState,CtrlState> normalMap = new HashMap<CtrlState,CtrlState>();
        for (Map.Entry<Set<CtrlState>,CtrlState> cellEntry : stateMap.entrySet()) {
            for (CtrlState state : cellEntry.getKey()) {
                normalMap.put(state, cellEntry.getValue());
            }
        }
        // only add outgoing transitions for the representatives,
        // to avoid the appearance of nondeterminism
        for (CtrlState state : representatives) {
            state.copyTransitions(normalMap, null);
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

    /** Indicates if this is the default control automaton of a grammar. */
    public boolean isDefault() {
        return this.isDefault;
    }

    /** Sets the default status of this automaton to {@code true}. */
    public void setDefault() {
        this.isDefault = true;
    }

    /** Flag indicating that this is the default control automaton of a grammar. */
    private boolean isDefault;

    /** 
     * Tests if the automaton terminates deterministically.
     * This is the case if all omega-transitions are guarded by
     * all other outgoing rule calls.
     */
    public boolean isEndDeterministic() {
        boolean result = true;
        outer: for (CtrlTransition omegaTrans : getOmegas()) {
            Collection<CtrlTransition> guard = omegaTrans.label().getGuard();
            for (CtrlTransition otherTrans : outEdgeSet(omegaTrans.source())) {
                if (!otherTrans.getCall().isOmega()
                    && !guard.contains(otherTrans)) {
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
        public int size() {
            int result = 0;
            for (CtrlState state : nodeSet()) {
                result += state.getTransitions().size();
            }
            return result;
        }
    }
}
