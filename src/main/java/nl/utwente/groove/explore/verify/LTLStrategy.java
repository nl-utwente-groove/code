/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
 * $Id: DefaultModelCheckingStrategy.java,v 1.5 2008/03/05 08:41:17 kastenberg
 * Exp $
 */
package nl.utwente.groove.explore.verify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import gov.nasa.ltl.trans.Formula;
import nl.utwente.groove.explore.ExploreResult;
import nl.utwente.groove.explore.engine.ExploreStateStrategy;
import nl.utwente.groove.explore.engine.Strategy;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.match.MatcherFactory;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.RandomChooserInSequence;
import nl.utwente.groove.util.Randomness.Purpose;
import nl.utwente.groove.util.Randomness;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.verify.BuchiGraph;
import nl.utwente.groove.verify.BuchiLocation;
import nl.utwente.groove.verify.BuchiTransition;
import nl.utwente.groove.verify.CycleAcceptor;
import nl.utwente.groove.verify.ModelChecking.Record;
import nl.utwente.groove.verify.ProductState;
import nl.utwente.groove.verify.ProductStateSet;
import nl.utwente.groove.verify.ProductTransition;
import nl.utwente.groove.verify.Proposition;

/**
 * This class provides some default implementations for the methods that are
 * required for strategies that perform model checking activities.
 *
 * @author Harmen Kastenberg
 * @version $Revision$
 */
@NonNullByDefault
public class LTLStrategy extends Strategy {
    @Override
    protected void prepare(GTS gts, @Nullable GraphState state) {
        super.prepare(gts, state);
        MatcherFactory.instance(gts.hasSimpleGraphs()).setDefaultEngine();
        var stateSet = new ProductStateSet();
        this.stateSet = stateSet;
        stateSet.addListener(this.collector);
        var acceptor = getAcceptor();
        this.result = acceptor.getResult();
        stateSet.addListener(acceptor);
        this.stateStack = new Stack<>();
        var startLocation = this.startLocation;
        assert startLocation != null : "The property automaton should have an initial state";
        ProductState startState = createState(gts.startState(), null, startLocation);
        this.startState = startState;
        this.nextState = startState;
        stateSet.addState(startState);
        this.stateStrategy.setGTS(gts);
    }

    @Override
    public void finish() {
        getStateSet().removeListener(this.collector);
        getStateSet().removeListener(getAcceptor());
    }

    /** Sets the cycle acceptor that runs the nested (red) search of this
     * strategy, and wires it back to this strategy; called by the
     * exploration type upon creating the pair.
     */
    public void setAcceptor(CycleAcceptor acceptor) {
        this.acceptor = acceptor;
        acceptor.setStrategy(this);
    }

    /** Returns the cycle acceptor of this strategy. */
    private CycleAcceptor getAcceptor() {
        var result = this.acceptor;
        assert result != null : "Acceptor not set";
        return result;
    }

    @Override
    public boolean hasNext() {
        return getNextState() != null;
    }

    @Override
    public GraphState doNext() throws InterruptedException {
        ProductState prodState = getNextState();
        assert prodState != null;
        // put current state on the stack
        pushState(prodState);
        // colour state cyan as being on the search stack
        prodState.setColour(getRecord().cyan());
        // fully explore the current state
        exploreGraphState(prodState.getGraphState());
        this.collector.reset();

        if (!exploreState(prodState)) {
            setNextState();
        }
        return prodState.getGraphState();
    }

    /**
     * Sets the property to be verified.
     * @param property the property to be verified. It is required
     * that this property can be parsed correctly
     */
    public void setProperty(String property) {
        this.property = property;
        try {
            Formula<Proposition> formula
                = nl.utwente.groove.verify.Formula.parse(property).toLtlFormula();
            BuchiGraph buchiGraph = BuchiGraph.getPrototype().newBuchiGraph(Formula.Not(formula));
            this.startLocation = buchiGraph.getInitial();
        } catch (FormatException e) {
            throw new IllegalStateException(String.format("Error in property '%s'", property), e);
        }
    }

    /** Returns the property being checked (in string form as set by {@link #setProperty(String)}). */
    public String getProperty() {
        var result = this.property;
        assert result != null : "Property not set";
        return result;
    }

    /**
     * Sets the next state to be explored.
     * The next state is determined by a call to {@link #computeNextState()}.
     */
    protected final void setNextState() {
        this.nextState = computeNextState();
    }

    /**
     * Callback method to return the next state to be explored.
     * Also pushes this state on the explored stack.
     */
    protected final @Nullable ProductState getNextState() {
        return this.nextState;
    }

    /** Pushes the current product state on the exploration stack. */
    protected void pushState(ProductState state) {
        getStateStack().push(state);
    }

    /**
     * Pops the top element of the state stack, and processes the fact
     * that this is now completely explored, without finding a counterexample.
     * @return the new top of the search stack, or {@code null} if
     * the stack is empty.
     */
    protected @Nullable ProductState rollbackState() {
        ProductState previous = getStateStack().pop();
        // close the current state
        getStateSet().setClosed(previous);
        colourState(previous);
        return getStateStack().isEmpty()
            ? null
            : getStateStack().peek();
    }

    /**
     * Looks in the GTS for the outgoing transitions of the
     * current state with the current Buchi location and add
     * the resulting combined transition to the product GTS
     * @return {@code true} if a counterexample was found
     */
    protected boolean exploreState(ProductState prodState) {
        boolean result = false;
        Set<? extends GraphTransition> outTransitions = prodState.getGraphState().getTransitions();
        Set<Proposition> satisfiedProps = getProps(outTransitions);
        trans: for (BuchiTransition buchiTrans : prodState.getBuchiLocation().outTransitions()) {
            if (buchiTrans.isEnabled(satisfiedProps)) {
                boolean finalState = prodState.getGraphState().isFinal();
                for (GraphTransition trans : outTransitions) {
                    if (trans.getRole() == EdgeRole.BINARY) {
                        finalState = false;
                        ProductTransition prodTrans
                            = addTransition(prodState, trans, buchiTrans.target());
                        assert prodTrans != null : "%s is being explored, so cannot be closed"
                            .formatted(prodState);
                        result = findCounterExample(prodState, prodTrans);
                        if (result) {
                            break trans;
                        }
                    }
                }
                if (finalState) {
                    // add a fake self-loop for final states
                    addTransition(prodState, null, buchiTrans.target());
                }
            }
            // if the transition of the property automaton is not enabled
            // the states reached in the system automaton do not have to
            // be explored further since all paths starting from here
            // will never yield a counter-example
        }
        return result;
    }

    /**
     * Callback method to determine the next state to be explored.
     * @return The next state to be explored, or {@code null} if exploration is done.
     */
    protected @Nullable ProductState computeNextState() {
        ProductState result = getFreshState();
        if (result == null) {
            var backtrack = backtrack();
            result = backtrack == null
                ? null
                : backtrack.target();
        }
        return result;
    }

    /**
     * Backtracks the state stack, and returns the
     * topmost unexplored outgoing transition.
     * @return the topmost incompletely explored transition on the
     * state stack, or {@code null} if there is none.
     */
    protected @Nullable ProductTransition backtrack() {
        ProductTransition result = null;
        ProductState parent = null;

        do {
            // the parent is on top of the searchStack
            parent = rollbackState();
            if (parent != null) {
                result = getNextSuccessor(parent);
            }
        } while (parent != null && result == null);
        return result;
    }

    /** Selects a state from the set of unexplored states. */
    protected @Nullable ProductState getFreshState() {
        return this.collector.pickRandomNewState();
    }

    /**
     * Colours a given state, in the course of backtracking.
     */
    protected void colourState(ProductState state) {
        state.setColour(getRecord().blue());
    }

    /** Tests if a counterexample can be constructed from the search stack
     * and a given potential closing transition; if so, adds the counterexample
     * to the result.
     * @param source source state of the potential closing transition; expected
     * to be the current top of the search stack
     * @param closing potential closing transition of the counterexample cycle
     * @return {@code true} if a counterexample was found
     */
    protected final boolean findCounterExample(ProductState source, ProductTransition closing) {
        ProductState target = closing.target();
        boolean result = (target.colour() == getRecord().cyan())
            && (source.getBuchiLocation().isAccepting() || target.getBuchiLocation().isAccepting());
        if (result) {
            addCounterExample(source, Collections.singletonList(closing));
        }
        return result;
    }

    /**
     * Adds a counterexample lasso to the exploration result (see
     * {@link nl.utwente.groove.explore.ExploreResult.Lasso}).
     * The lasso consists of the path along the current search stack, extended
     * by a given pivot state (if that is not already the top of the stack) and
     * a chain of product transitions leading from the pivot state back to a
     * state on the path, at which the cycle closes.
     * @param pivot final product state of the path proper; either the top of
     * the search stack, or a state whose direct predecessor is the top
     * @param trail non-empty chain of product transitions leading from the
     * pivot state to the state at which the cycle closes
     */
    @AIGenerated("Claude Fable 5, 2026-08")
    public final void addCounterExample(ProductState pivot, List<ProductTransition> trail) {
        // reconstruct the product-level path along the search stack,
        // extended by the pivot state and the trail
        List<ProductState> pathStates = new ArrayList<>(getStateStack());
        List<ProductTransition> path = new ArrayList<>();
        for (int i = 1; i < pathStates.size(); i++) {
            path.add(getLink(pathStates.get(i - 1), pathStates.get(i)));
        }
        if (pathStates.isEmpty()) {
            pathStates.add(pivot);
        } else if (pathStates.get(pathStates.size() - 1) != pivot) {
            path.add(getLink(pathStates.get(pathStates.size() - 1), pivot));
            pathStates.add(pivot);
        }
        for (var trans : trail) {
            path.add(trans);
            pathStates.add(trans.target());
        }
        // the final path state closes the cycle; find its earlier occurrence
        ProductState closure = pathStates.get(pathStates.size() - 1);
        int start = pathStates.indexOf(closure);
        assert start < path.size() : "Cycle closure %s does not occur on the path %s"
            .formatted(closure, path);
        // store the counterexample in the result;
        // the final path state duplicates the cycle start and is skipped
        var result = this.result;
        assert result != null : "Strategy not prepared";
        pathStates.subList(0, pathStates.size() - 1).forEach(s -> result.addState(s.getGraphState()));
        var prefix = toGraphTransitions(path.subList(0, start));
        var cycle = toGraphTransitions(path.subList(start, path.size()));
        prefix.forEach(result::addTransition);
        cycle.forEach(result::addTransition);
        result.setLasso(new ExploreResult.Lasso(prefix, cycle));
    }

    /** Returns the first product transition between two given product states.
     * Such a transition is guaranteed to exist if the states are consecutive
     * on the search stack.
     */
    private ProductTransition getLink(ProductState source, ProductState target) {
        for (var trans : source.outTransitions()) {
            if (trans.target().equals(target)) {
                return trans;
            }
        }
        throw Exceptions.illegalState("No product transition from %s to %s", source, target);
    }

    /** Extracts the graph transitions from a chain of product transitions,
     * skipping the {@code null} graph transitions of the artificial self-loops
     * added for final states. */
    static private List<GraphTransition> toGraphTransitions(List<ProductTransition> path) {
        return path
            .stream()
            .map(ProductTransition::graphTransition)
            .filter(Objects::nonNull)
            .toList();
    }

    /**
     * Returns the start product state.
     * @return the start product state; non-{@code null} after
     * a call to {@link #prepare}.
     */
    protected final ProductState getStartState() {
        var result = this.startState;
        assert result != null : "Strategy not prepared";
        return result;
    }

    /**
     * Returns a random open successor of a state, if any. Returns null
     * otherwise.
     */
    protected @Nullable ProductTransition getNextSuccessor(ProductState state) {
        RandomChooserInSequence<ProductTransition> chooser
            = new RandomChooserInSequence<>(getRandomGen());
        for (ProductTransition trans : state.outTransitions()) {
            if (!trans.graphTransition().getAction().isProperty()) {
                if (!trans.target().isClosed()) {
                    chooser.show(trans);
                }
            }
        }
        return chooser.pickRandom();
    }

    /**
     * Extracts the labels from a given set of transitions.
     * @param transitions a set of graph transitions
     * @return the set of label texts of the transitions in {@code transitions}
     */
    private Set<Proposition> getProps(Set<? extends GraphTransition> transitions) {
        return transitions.stream().map(t -> toProp(t)).collect(Collectors.toSet());
    }

    private Proposition toProp(GraphTransition trans) {
        return Proposition.prop(trans.label());
    }

    /**
     * Method for exploring a single state locally. The state will be closed
     * afterwards.
     * @param state the state to be fully explored locally
     */
    private void exploreGraphState(GraphState state) {
        if (!state.isClosed()) {
            this.stateStrategy.setState(state);
            this.stateStrategy.play();
        }
    }

    private final Strategy stateStrategy = new ExploreStateStrategy();

    /**
     * Adds a product transition to the product GTS. If the source state is
     * already explored we do not have to add anything. In that case, we return
     * the corresponding transition.
     * @param source source of the new transition
     * @param transition the graph-transition component for the
     *        product-transition
     * @param targetLocation the location of the target Buchi graph-state
     * @see ProductState#addTransition(ProductTransition)
     */
    private @Nullable ProductTransition addTransition(ProductState source,
                                                      @Nullable GraphTransition transition,
                                                      BuchiLocation targetLocation) {
        ProductTransition result = null;
        if (!source.isClosed()) {
            // we assume that we only add transitions for modifying graph
            // transitions
            ProductState target = createState(source.getGraphState(), transition, targetLocation);
            ProductState isoTarget = getStateSet().addState(target);
            if (isoTarget == null) {
                // no isomorphic state found
                result = createProductTransition(source, transition, target);
            } else {
                assert (isoTarget.iteration() <= getRecord()
                    .getIteration()) : "This state belongs to the next iteration and should not be explored now.";
                result = createProductTransition(source, transition, isoTarget);
            }
            source.addTransition(result);
        } else {
            // if the current source state is already closed
            // the product-gts contains all transitions and
            // we do not have to add new transitions.
            for (ProductTransition nextTransition : source.outTransitions()) {
                if (nextTransition.graphTransition().equals(transition)
                    && nextTransition.target().getBuchiLocation().equals(targetLocation)) {
                    result = nextTransition;
                    break;
                }
            }
        }
        return result;
    }

    /** Creates a product state from a graph state or transition, and
     * a Buchi location.
     */
    private ProductState createState(GraphState state, @Nullable GraphTransition transition,
                                     BuchiLocation targetLocation) {
        if (transition == null) {
            // the system-state is a final one for which we add an artificial
            // self-loop
            return new ProductState(state, targetLocation);
        } else {
            return new ProductState(transition, targetLocation);
        }
    }

    private ProductTransition createProductTransition(ProductState source,
                                                      @Nullable GraphTransition transition,
                                                      ProductState target) {
        return new ProductTransition(source, transition, target);
    }

    /**
     * Returns the product GTS.
     * @return the product GTS; non-{@code null} after a
     * call to {@link #prepare}
     */
    protected final ProductStateSet getStateSet() {
        var result = this.stateSet;
        assert result != null : "Strategy not prepared";
        return result;
    }

    /**
     * Returns the current search-stack.
     */
    public final Stack<ProductState> getStateStack() {
        var result = this.stateStack;
        assert result != null : "Strategy not prepared";
        return result;
    }

    /** Returns the record for this model checking run. */
    final public Record getRecord() {
        return this.record;
    }

    /** Property to be checked; set by {@link #setProperty}. */
    private @Nullable String property;
    /** Record of this model checking run. */
    private Record record = new Record();
    /** The synchronised product of the system and the property;
     * set in {@link #prepare}. */
    private @Nullable ProductStateSet stateSet;
    /** The current Buchi graph-state the system is at. */
    private @Nullable ProductState nextState;
    /** The Buchi start graph-state of the system; set in {@link #prepare}. */
    private @Nullable ProductState startState;
    /** Acceptor running the nested search; set by {@link #setAcceptor}. */
    private @Nullable CycleAcceptor acceptor;
    /** Returns the random generator for the successor and new-state choices. */
    protected final Random getRandomGen() {
        return this.rgen;
    }

    /**
     * Source of the random successor and new-state choices, seeded once per
     * strategy instance from the {@link Randomness} registry, so that a
     * fixed master seed makes the choices reproducible while successive
     * choices still draw fresh values (see {@link RandomChooserInSequence}).
     */
    private final Random rgen = Randomness.newRandom(Purpose.EXPLORATION);
    /** State collector which randomly provides unexplored states. */
    private RandomNewStateChooser collector = new RandomNewStateChooser(this.rgen);
    /** Initial location of the Buchi graph encoding the property to be verified;
     * set by {@link #setProperty}. */
    private @Nullable BuchiLocation startLocation;
    /** The search stack; set in {@link #prepare}. */
    private @Nullable Stack<ProductState> stateStack;
    /** The exploration result; set in {@link #prepare}. */
    private @Nullable ExploreResult result;
}
