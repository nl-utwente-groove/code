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
public class LtlStrategy extends GTSStrategy {
    @Override
    public void prepare(GTS gts, GraphState state, Acceptor acceptor) {
        // don't pass on the acceptor, we deal with this here
        super.prepare(gts, state, null);
        this.productGTS = new ProductStateSet();
        this.productGTS.addListener(this.collector);
        assert acceptor instanceof CycleAcceptor;
        this.acceptor = (CycleAcceptor) acceptor;
        this.acceptor.setStrategy(this);
        this.result = acceptor.getResult();
        this.productGTS.addListener(this.acceptor);
        this.searchStack = new Stack<ProductState>();
        this.transitionStack = new Stack<ProductTransition>();
        assert (this.initialLocation != null) : "The property automaton should have an initial state";
        ProductState startState =
            new ProductState(getGTS().startState(), this.initialLocation);
        this.startProdState = startState;
        this.atProductState = startState;
        this.productGTS.addState(startState);
    }

    @Override
    public void finish() {
        super.finish();
        getProductGTS().removeListener(this.collector);
        if (this.acceptor != null) {
            getProductGTS().removeListener(this.acceptor);
        }
    }

    @Override
    public GraphState doNext() {
        ProductState prodState = getNextProductState();
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
     * Callback method to return the next state to be explored.
     * Also pushes this state on the explored stack.
     */
    protected ProductState getNextProductState() {
        ProductState result = getAtProductState();
        return result;
    }

    /** Pushes the current product state on the exploration stack. */
    protected void pushState(ProductState state) {
        // TODO: push the current state only on the stack when continuing with
        // one of its successors
        // this should therefore be done in the method updateAtState
        searchStack().push(state);
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
            getNextState().getTransitions();
        Set<String> applicableRules = filterRuleNames(outTransitions);
        trans: for (BuchiTransition buchiTrans : prodState.getBuchiLocation().outTransitions()) {
            if (buchiTrans.isEnabled(applicableRules)) {
                boolean finalState = true;
                for (GraphTransition trans : outTransitions) {
                    if (trans.getRole() == EdgeRole.BINARY) {
                        finalState = false;
                        ProductTransition prodTrans =
                            addProductTransition(trans, buchiTrans.target());
                        result = findCounterExample(prodTrans.target());
                        if (result) {
                            break trans;
                        }
                    }
                }
                if (finalState) {
                    processFinalState(buchiTrans);
                }
            }
            // if the transition of the property automaton is not enabled
            // the states reached in the system automaton do not have to
            // be explored further since all paths starting from here
            // will never yield a counter-example
        }
        return result;
    }

    @Override
    protected GraphState computeNextState() {
        if (this.collector.pickRandomNewState() != null) {
            ProductState newState = this.collector.pickRandomNewState();
            this.atProductState = newState;
        } else {
            ProductState s = null;

            // backtracking

            ProductState parent = null;

            do {
                // pop the current state from the search-stack
                searchStack().pop();
                // close the current state
                setClosed(getAtProductState());
                getAtProductState().setColour(ModelChecking.blue());
                // the parent is on top of the searchStack
                parent = peekSearchStack();
                if (parent != null) {
                    this.atProductState = parent;
                    s = getRandomOpenBuchiSuccessor(parent);
                }
            } while (parent != null && s == null); // ) &&
            // !getProductGTS().isOpen(getAtBuchiState()));

            // identify the reason of exiting the loop
            if (parent == null) {
                // the start state is reached and does not have open successors
                this.atProductState = null;
            } else if (s != null) { // the current state has an open successor (is
                // not really backtracking, a sibling state is
                // fully explored)
                this.atProductState = s;
            }
        }
        return this.atProductState == null ? null
                : this.atProductState.getGraphState();
    }

    /** Tests if a counterexample can be constructed from the 
     * current product state in combination with a given state.
     * @param prodState the state to test for a counterexample
     * @return {@code true} if a counterexample was found;
     * if so, it has also been added to the result
     */
    protected boolean findCounterExample(ProductState prodState) {
        boolean result = counterExample(getAtProductState(), prodState);
        if (result) {
            // notify counter-example
            for (ProductState state : searchStack()) {
                getResult().add(state.getGraphState());
            }
        }
        return result;
    }

    /**
     * @param transition may be null.
     */
    protected void processFinalState(BuchiTransition transition) {
        if (transition == null) {
            // exclude the current state from further analysis
            // mark it red
            getAtProductState().setColour(ModelChecking.RED);
        } else {
            addProductTransition(null, transition.target());
        }
    }

    /**
     * Returns the result container.
     * @return the result container; non-{@code null}
     */
    public Result getResult() {
        return this.result;
    }

    /**
     * Returns the start product state.
     * @return the start product state; non-{@code null} after
     * a call to {@link #prepare}.
     */
    public final ProductState getStartProductState() {
        return this.startProdState;
    }

    /**
     * Closes Büchi graph-states.
     * @param state the Büchi graph-state to close
     */
    public void setClosed(ProductState state) {
        getProductGTS().setClosed(state);
    }

    /**
     * Identifies special cases of counter-examples.
     * @param source the source Buchi graph-state
     * @param target the target Buchi graph-state
     * @return <tt>true</tt> if the target Buchi graph-state has colour
     *         {@link ModelChecking#CYAN} and the buchi location of either the
     *         <tt>source</tt> or <tt>target</tt> is accepting, <tt>false</tt>
     *         otherwise.
     */
    public boolean counterExample(ProductState source, ProductState target) {
        boolean result =
            (target.colour() == ModelChecking.cyan())
                && (source.getBuchiLocation().isAccepting() || target.getBuchiLocation().isAccepting());
        return result;
    }

    /**
     * Pops the top element from the search-stack
     * @return the top element from the search-stack
     */
    protected ProductState popSearchStack() {
        if (searchStack().isEmpty()) {
            return null;
        } else {
            return searchStack().pop();
        }
    }

    /**
     * Returns the top element from the search-stack.
     * @return the top element from the search-stack
     */
    protected ProductState peekSearchStack() {
        if (searchStack().isEmpty()) {
            return null;
        } else {
            return searchStack().peek();
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
     * Constructs the set of rule names for which the iterator contains a match.
     * @param graphTransitions a set of {@link groove.lts.RuleTransition}s
     * @return the set of rule names contained in the given
     *         <code>iterator</code>
     */
    protected Set<String> filterRuleNames(
            Set<? extends GraphTransition> graphTransitions) {
        Set<String> result = new HashSet<String>();
        for (GraphTransition nextTransition : graphTransitions) {
            result.add(nextTransition.label().toString());
        }
        return result;
    }

    /**
     * Method for exploring a single state locally. The state will be closed
     * afterwards.
     * @param state the state to be fully explored locally
     */
    public void exploreState(GraphState state) {
        if (getGTS().isOpen(state)) {
            Strategy explore = new ExploreStateStrategy();
            explore.setGTS(getGTS(), state);
            explore.play();
        }
    }

    /**
     * Adds a product transition to the product gts. If the current state is
     * already explored we do not have to add anything. In that case, we return
     * the corresponding transition.
     * @param transition the graph-transition component for the
     *        product-transition
     * @param location the location of the target Buechi graph-state
     * @see ProductState#addTransition(ProductTransition)
     */
    public ProductTransition addProductTransition(GraphTransition transition,
            BuchiLocation location) {
        ProductTransition result = null; // new
        // HashSet<ProductTransition>();
        // if the current source state is already closed
        // the product-gts contains all transitions and
        // we do not have to add new transitions.
        if (!getAtProductState().isClosed()) {
            result = addTransition(getAtProductState(), transition, location);
        } else {
            for (ProductTransition nextTransition : getAtProductState().outTransitions()) {
                if (nextTransition.graphTransition().equals(transition)
                    && nextTransition.target().getBuchiLocation().equals(
                        location)) {
                    result = nextTransition;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Adds a transition to the product gts given a source Buechi graph-state, a
     * graph transition, and a target Buechi location.
     * @param source the source Buechi graph-state
     * @param transition the graph transition
     * @param targetLocation the target Buechi location
     * @return the added product transition
     */
    public ProductTransition addTransition(ProductState source,
            GraphTransition transition, BuchiLocation targetLocation) {
        // we assume that we only add transitions for modifying graph
        // transitions
        ProductState target =
            createBuchiGraphState(source, transition, targetLocation);
        ProductState isoTarget = getProductGTS().addState(target);
        ProductTransition result = null;

        if (isoTarget == null) {
            // no isomorphic state found
            result = createProductTransition(source, transition, target);
        } else {
            assert (isoTarget.iteration() <= ModelChecking.getIteration()) : "This state belongs to the next iteration and should not be explored now.";
            result = createProductTransition(source, transition, isoTarget);
        }
        source.addTransition(result);
        return result;
    }

    private ProductState createBuchiGraphState(ProductState source,
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
    public ProductStateSet getProductGTS() {
        return this.productGTS;
    }

    /**
     * Returns the Büchi graph-state the strategy is currently at.
     */
    public ProductState getAtProductState() {
        return this.atProductState;
    }

    /**
     * Set the current product state
     * @param atState the state to set; non-{@code null}.
     */
    public void setAtProductState(ProductState atState) {
        assert atState != null;
        this.atProductState = atState;
    }

    /**
     * Returns the current Büchi location.
     */
    public BuchiLocation getAtBuchiLocation() {
        return getAtProductState().getBuchiLocation();
    }

    /**
     * Returns the current search-stack.
     */
    public Stack<ProductState> searchStack() {
        return this.searchStack;
    }

    /**
     * Returns the transition stack.
     */
    public Stack<ProductTransition> transitionStack() {
        return this.transitionStack;
    }

    /**
     * Pushes a transition on the transition stack.
     * @param transition the transition to push
     */
    public void pushTransition(ProductTransition transition) {
        transitionStack().push(transition);
        assert (transitionStack().size() == (searchStack().size() - 1)) : "search stacks out of sync ("
            + transitionStack().size() + " vs " + searchStack().size() + ")";
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
            this.initialLocation = buchiGraph.getInitial();
        } catch (ParseException e) {
            throw new IllegalStateException(String.format(
                "Error in property '%s'", property), e);
        }
    }

    /** The Buchi start graph-state of the system. */
    private ProductState startProdState;
    /** Acceptor to be added to the product GTS, once it is created. */
    private CycleAcceptor acceptor;
    /** The synchronised product of the system and the property. */
    private ProductStateSet productGTS;
    /** The current Buchi graph-state the system is at. */
    private ProductState atProductState;
    /** State collector which randomly provides unexplored states. */
    private RandomNewStateChooser collector = new RandomNewStateChooser();

    private BuchiLocation initialLocation;
    private Stack<ProductState> searchStack;
    private Stack<ProductTransition> transitionStack;
    private Result result;
}
