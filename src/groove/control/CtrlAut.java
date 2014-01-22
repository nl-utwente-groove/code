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
import groove.control.template.Switch.Kind;
import groove.grammar.Recipe;
import groove.grammar.Rule;
import groove.grammar.model.FormatException;
import groove.graph.AGraph;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.util.collect.NestedIterator;
import groove.util.collect.TransformIterator;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

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
public class CtrlAut extends AGraph<CtrlState,CtrlTransition> {
    /**
     * Constructs a new control automaton.
     * The start state and final state are automatically initialised.
     * @param name the name of the control automaton
     */
    public CtrlAut(String name) {
        super(name);
        this.startState = addState();
        this.finalState = addState();
        // Dummy call to construct the info object before the automaton is
        // fixed. SF bug #3600975
        getInfo();
    }

    @Override
    public GraphRole getRole() {
        return CTRL;
    }

    @Override
    public CtrlAut newGraph(String name) {
        return new CtrlAut(name);
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
    public boolean addEdge(CtrlTransition edge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeNode(CtrlState node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CtrlAut clone() {
        return clone(getName());
    }

    /** 
     * Clones this automaton under a given name.
     * @param name the name of the new automaton
     */
    public CtrlAut clone(String name) {
        return clone(name, null);
    }

    /** 
     * Clones this automaton, optionally making the intermediate states transient
     * for a given recipe.
     * @param recipe if not {@code null}, the new automaton is to be the body
     * of a recipe with this name
     */
    public CtrlAut clone(Recipe recipe) {
        return clone(recipe == null ? getName() : recipe.getFullName(), recipe);
    }

    /** 
     * Clones this automaton, optionally making the intermediate states transient.
     * @param name the name of the new automaton
     * @param recipe if not {@code null}, the new automaton is to be the body
     * of a recipe with this name
     */
    private CtrlAut clone(String name, Recipe recipe) {
        CtrlAut result = newGraph(name);
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
            CtrlCall newCall = call.embed(newRecipe);
            boolean newStart =
                recipe == null ? trans.isStart()
                        : newSource == result.getStart() || call.isOmega();
            CtrlLabel newLabel = new CtrlLabel(newCall, newGuard, newStart);
            CtrlTransition newTrans =
                newSource.addTransition(newLabel, newTarget);
            assert newTrans != null;
            morphism.putEdge(trans, newTrans);
        }
        if (isDefault()) {
            result.setDefault();
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
        GraphInfo.addErrors(this, GraphInfo.getErrors(this));
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
        for (CtrlState state : nodeSet()) {
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

    /**
     * Returns a state in this automaton that only has an (unconditional)
     * outgoing omega-transition, if there is any such state.
     * @return a state with only an outgoing unguarded omega-transition;
     * or {@code null} if there is no such state
     */
    public CtrlState getOmegaOnlyState() {
        CtrlState result = null;
        for (CtrlTransition omega : getOmegas()) {
            if (omega.source().isOmegaOnly()) {
                result = omega.source();
                break;
            }
        }
        return result;
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
        if (!GraphInfo.hasErrors(this)) {
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
                if (i.getNumber() >= j.getNumber()) {
                    continue;
                }
                Set<CtrlState> ijPair =
                    new HashSet<CtrlState>(Arrays.asList(i, j));
                Set<Set<CtrlState>> depSet = depMap.remove(ijPair);
                assert depSet != null;
                // establish whether i and j are distinguishable states
                boolean distinct = i.isTransient() != j.isTransient();
                if (!distinct && i.isTransient()) {
                    distinct = !i.getRecipe().equals(j.getRecipe());
                }
                if (!distinct) {
                    distinct = !i.getBoundVars().equals(j.getBoundVars());
                }
                if (!distinct) {
                    Map<CtrlTransition,CtrlTransition> equivalence =
                        computeEquivalence(i, j);
                    distinct = equivalence == null;
                    if (!distinct) {
                        // defer the distinctness test to the target states
                        // of all outgoing transitions
                        for (Map.Entry<CtrlTransition,CtrlTransition> entry : equivalence.entrySet()) {
                            CtrlTransition iOut = entry.getKey();
                            CtrlTransition jOut = entry.getValue();
                            if (iOut.target() == jOut.target()) {
                                continue;
                            }
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
        return result;
    }

    /** Computes a one-to-one mapping between the outgoing transitions
     * of two control states. Return {@code null} if there is no such mapping.
     */
    private Map<CtrlTransition,CtrlTransition> computeEquivalence(CtrlState i,
            CtrlState j) throws FormatException {
        Map<CtrlTransition,CtrlTransition> result =
            new HashMap<CtrlTransition,CtrlTransition>();
        Map<CtrlCall,CtrlTransition> iOutMap = getOutTransitions(i);
        Map<CtrlCall,CtrlTransition> jOutMap = getOutTransitions(j);
        boolean distinct = !iOutMap.keySet().equals(jOutMap.keySet());
        // build the result map, based on the control calls in the transitions
        if (!distinct) {
            for (Map.Entry<CtrlCall,CtrlTransition> iOutEntry : iOutMap.entrySet()) {
                CtrlTransition iOut = iOutEntry.getValue();
                CtrlTransition jOut = jOutMap.get(iOutEntry.getKey());
                if (iOut.isStart() != jOut.isStart()) {
                    distinct = true;
                    break;
                }
                result.put(iOut, jOut);
            }
        }
        // now check if the guards also coincide
        if (!distinct) {
            check: for (Map.Entry<CtrlTransition,CtrlTransition> entry : result.entrySet()) {
                CtrlGuard iGuard = entry.getKey().getGuard();
                CtrlGuard jGuard = entry.getValue().getGuard();
                if (iGuard.size() != jGuard.size()) {
                    distinct = true;
                    break;
                }
                for (CtrlTransition iGuardElem : iGuard) {
                    if (!jGuard.contains(result.get(iGuardElem))) {
                        distinct = true;
                        break check;
                    }
                }
            }
        }
        return distinct ? null : result;
    }

    private Map<CtrlCall,CtrlTransition> getOutTransitions(CtrlState s)
        throws FormatException {
        Map<CtrlCall,CtrlTransition> result =
            new HashMap<CtrlCall,CtrlTransition>();
        for (CtrlTransition outTrans : s.getTransitions()) {
            CtrlCall call = outTrans.getCall();
            CtrlTransition oldTrans = result.put(call, outTrans);
            if (oldTrans != null) {
                throw new FormatException(
                    "State %s has multiple outgoing calls %s", s, call);
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
            CtrlVarSet sourceVars = new CtrlVarSet(source.getBoundVars());
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
    private final TransitionSet transitions = new TransitionSet();

    /** The set of omega transitions in this control automaton. */
    private final Set<CtrlTransition> omegaTransitions =
        new LinkedHashSet<CtrlTransition>();

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
                    trans.getCall()));
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
