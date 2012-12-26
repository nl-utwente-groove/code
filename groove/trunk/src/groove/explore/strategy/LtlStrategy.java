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
 * $Id: DefaultModelCheckingStrategy.java,v 1.5 2008/03/05 08:41:17 kastenberg
 * Exp $
 */
package groove.explore.strategy;

import gov.nasa.ltl.trans.Formula;
import groove.explore.result.Acceptor;
import groove.explore.result.CycleAcceptor;
import groove.explore.result.Result;
import groove.explore.util.RandomChooserInSequence;
import groove.explore.util.RandomNewStateChooser;
import groove.graph.EdgeRole;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.match.MatcherFactory;
import groove.verify.BuchiGraph;
import groove.verify.BuchiLocation;
import groove.verify.BuchiTransition;
import groove.verify.FormulaParser;
import groove.verify.ModelChecking;
import groove.verify.ParseException;
import groove.verify.ProductState;
import groove.verify.ProductStateSet;
import groove.verify.ProductTransition;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * This class provides some default implementations for the methods that are
 * required for strategies that perform model checking activities.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class LtlStrategy extends Strategy implements ExploreIterator {
    @Override
    public void prepare(GTS gts, GraphState state, Acceptor acceptor) {
        MatcherFactory.instance().setDefaultEngine();
        this.stateSet = new ProductStateSet();
        this.stateSet.addListener(this.collector);
        assert acceptor instanceof CycleAcceptor;
        this.acceptor = (CycleAcceptor) acceptor;
        this.acceptor.setStrategy(this);
        this.result = acceptor.getResult();
        this.stateSet.addListener(this.acceptor);
        this.stateStack = new Stack<ProductState>();
        assert (this.startLocation != null) : "The property automaton should have an initial state";
        ProductState startState =
            new ProductState(gts.startState(), this.startLocation);
        this.startState = startState;
        this.nextState = startState;
        this.stateSet.addState(startState);
        this.stateStrategy.setGTS(gts);
    }

    @Override
    public void finish() {
        getStateSet().removeListener(this.collector);
        getStateSet().removeListener(this.acceptor);
    }

    @Override
    public boolean hasNext() {
        return getNextState() != null;
    }

    @Override
    public GraphState doNext() {
        ProductState prodState = getNextState();
        if (prodState == null) {
            return null;
        }
        // put current state on the stack
        pushState(prodState);
        // colour state cyan as being on the search stack
        prodState.setColour(ModelChecking.cyan());
        // fully explore the current state
        exploreState(prodState.getGraphState());
        this.collector.reset();

        if (!exploreCurrentLocation(prodState)) {
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
        assert property != null;
        try {
            Formula<String> formula =
                FormulaParser.parse(property).toLtlFormula();
            BuchiGraph buchiGraph =
                BuchiGraph.getPrototype().newBuchiGraph(Formula.Not(formula));
            this.startLocation = buchiGraph.getInitial();
        } catch (ParseException e) {
            throw new IllegalStateException(String.format(
                "Error in property '%s'", property), e);
        }
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
    protected final ProductState getNextState() {
        return this.nextState;
    }

    /** Pushes the current product state on the exploration stack. */
    protected void pushState(ProductState state) {
        // TODO: push the current state only on the stack when continuing with
        // one of its successors
        // this should therefore be done in the method updateAtState
        getStateStack().push(state);
    }

    /**
     * Looks in the GTS for the outgoing transitions of the
     * current state with the current Buchi location and add
     * the resulting combined transition to the product GTS
     * @return {@code true} if a counterexample was found
     */
    protected boolean exploreCurrentLocation(ProductState prodState) {
        boolean result = false;
        Set<? extends GraphTransition> outTransitions =
            prodState.getGraphState().getTransitions();
        Set<String> applicableRules = getLabels(outTransitions);
        trans: for (BuchiTransition buchiTrans : prodState.getBuchiLocation().outTransitions()) {
            if (buchiTrans.isEnabled(applicableRules)) {
                boolean finalState = true;
                for (GraphTransition trans : outTransitions) {
                    if (trans.getRole() == EdgeRole.BINARY) {
                        finalState = false;
                        ProductTransition prodTrans =
                            addTransition(prodState, trans, buchiTrans.target());
                        result =
                            findCounterExample(prodState, prodTrans.target());
                        if (result) {
                            break trans;
                        }
                    }
                }
                if (finalState) {
                    processFinalState(prodState, buchiTrans);
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
     * Callback method to determine the next state to be explored. This is the place where
     * satisfaction of the condition is to be tested.
     * @return The next state to be explored, or {@code null} if exploration is done.
     */
    protected ProductState computeNextState() {
        ProductState result = null;
        if (this.collector.pickRandomNewState() != null) {
            result = this.collector.pickRandomNewState();
        } else {
            ProductState s = null;

            // backtracking

            ProductState parent = null;

            do {
                // pop the current state from the search-stack
                ProductState previous = getStateStack().pop();
                // close the current state
                getStateSet().setClosed(previous);
                previous.setColour(ModelChecking.blue());
                // the parent is on top of the searchStack
                parent = peekSearchStack();
                if (parent != null) {
                    result = parent;
                    s = getRandomOpenBuchiSuccessor(parent);
                }
            } while (parent != null && s == null); // ) &&
            // !getProductGTS().isOpen(getAtBuchiState()));

            // identify the reason of exiting the loop
            if (parent == null) {
                // the start state is reached and does not have open successors
                result = null;
            } else if (s != null) { // the current state has an open successor (is
                // not really backtracking, a sibling state is
                // fully explored)
                result = s;
            }
        }
        return result;
    }

    /** Tests if a counterexample can be constructed between given
     * source and target states; if so, adds the counterexample to the result.
     * @param source source state of the potential counterexample
     * @param target target state of the potential counterexample
     * @return {@code true} if a counterexample was found
     */
    protected final boolean findCounterExample(ProductState source,
            ProductState target) {
        boolean result =
            (target.colour() == ModelChecking.cyan())
                && (source.getBuchiLocation().isAccepting() || target.getBuchiLocation().isAccepting());
        if (result) {
            // notify counter-example
            for (ProductState state : getStateStack()) {
                this.result.add(state.getGraphState());
            }
        }
        return result;
    }

    /**
     * @param state the final product state to be processed
     * @param transition may be null.
     */
    protected final void processFinalState(ProductState state,
            BuchiTransition transition) {
        if (transition == null) {
            // exclude the current state from further analysis
            // mark it red
            state.setColour(ModelChecking.RED);
        } else {
            addTransition(state, null, transition.target());
        }
    }

    /**
     * Returns the start product state.
     * @return the start product state; non-{@code null} after
     * a call to {@link #prepare}.
     */
    protected final ProductState getStartState() {
        return this.startState;
    }

    /**
     * Returns the top element from the search-stack.
     * @return the top element from the search-stack
     */
    protected ProductState peekSearchStack() {
        if (getStateStack().isEmpty()) {
            return null;
        } else {
            return getStateStack().peek();
        }
    }

    /**
     * Returns a random open successor of a state, if any. Returns null
     * otherwise.
     */
    protected ProductState getRandomOpenBuchiSuccessor(ProductState state) {
        RandomChooserInSequence<ProductState> chooser =
            new RandomChooserInSequence<ProductState>();
        for (ProductTransition trans : state.outTransitions()) {
            ProductState s = trans.target();
            if (!s.isClosed()) {
                chooser.show(s);
            }
        }
        return chooser.pickRandom();
    }

    /**
     * Extracts the labels from a given set of transitions.
     * @param transitions a set of graph transitions
     * @return the set of label texts of the transitions in {@code transitions}
     */
    private Set<String> getLabels(Set<? extends GraphTransition> transitions) {
        Set<String> result = new HashSet<String>();
        for (GraphTransition nextTransition : transitions) {
            result.add(nextTransition.label().toString());
        }
        return result;
    }

    /**
     * Method for exploring a single state locally. The state will be closed
     * afterwards.
     * @param state the state to be fully explored locally
     */
    private void exploreState(GraphState state) {
        if (!state.isClosed()) {
            this.stateStrategy.setState(state);
            this.stateStrategy.play();
        }
    }

    /**
     * Adds a product transition to the product GTS. If the source state is
     * already explored we do not have to add anything. In that case, we return
     * the corresponding transition.
     * @param source source of the new transition
     * @param transition the graph-transition component for the
     *        product-transition
     * @param targetLocation the location of the target Büchi graph-state
     * @see ProductState#addTransition(ProductTransition)
     */
    private ProductTransition addTransition(ProductState source,
            GraphTransition transition, BuchiLocation targetLocation) {
        ProductTransition result = null;
        if (!source.isClosed()) {
            // we assume that we only add transitions for modifying graph
            // transitions
            ProductState target =
                createState(source, transition, targetLocation);
            ProductState isoTarget = getStateSet().addState(target);
            if (isoTarget == null) {
                // no isomorphic state found
                result = createProductTransition(source, transition, target);
            } else {
                assert (isoTarget.iteration() <= ModelChecking.getIteration()) : "This state belongs to the next iteration and should not be explored now.";
                result = createProductTransition(source, transition, isoTarget);
            }
            source.addTransition(result);
        } else {
            // if the current source state is already closed
            // the product-gts contains all transitions and
            // we do not have to add new transitions.
            for (ProductTransition nextTransition : source.outTransitions()) {
                if (nextTransition.graphTransition().equals(transition)
                    && nextTransition.target().getBuchiLocation().equals(
                        targetLocation)) {
                    result = nextTransition;
                    break;
                }
            }
        }
        return result;
    }

    private ProductState createState(ProductState source,
            GraphTransition transition, BuchiLocation targetLocation) {
        if (transition == null) {
            // the system-state is a final one for which we assume an artificial
            // self-loop
            // the resulting Büchi graph-state is nevertheless the product of
            // the
            // graph-state component of the source Büchi graph-state and the
            // target
            // Büchi-location
            return new ProductState(source.getGraphState(), targetLocation);
        } else {
            return new ProductState(transition.target(), targetLocation);
        }
    }

    private ProductTransition createProductTransition(ProductState source,
            GraphTransition transition, ProductState target) {
        return new ProductTransition(source, transition, target);
    }

    /**
     * Returns the product GTS.
     * @return the product GTS; non-{@code null} after a
     * call to {@link #prepare}
     */
    protected final ProductStateSet getStateSet() {
        return this.stateSet;
    }

    /**
     * Returns the current search-stack.
     */
    public final Stack<ProductState> getStateStack() {
        return this.stateStack;
    }

    private final Strategy stateStrategy = new ExploreStateStrategy();
    /** The synchronised product of the system and the property. */
    private ProductStateSet stateSet;
    /** The current Buchi graph-state the system is at. */
    private ProductState nextState;
    /** The Buchi start graph-state of the system. */
    private ProductState startState;
    /** Acceptor to be added to the product GTS. */
    private CycleAcceptor acceptor;
    /** State collector which randomly provides unexplored states. */
    private RandomNewStateChooser collector = new RandomNewStateChooser();
    /** Initial location of the Büchi graph encoding the property to be verified. */
    private BuchiLocation startLocation;
    private Stack<ProductState> stateStack;
    private Result result;
}
