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
public class LtlStrategy extends AbstractStrategy {
    /** This implementation initialises the product automaton as well. */
    @Override
    public void prepare(GTS gts, GraphState state) {
        if (state != gts.startState()) {
            throw new IllegalArgumentException(
                "Model checking should start at initial state");
        }
        super.prepare(gts, state);
        this.productGTS = new ProductStateSet();
        this.productGTS.addListener(this.collector); // currentPath = new Stack<GraphTransition>();
        this.searchStack = new Stack<ProductState>();
        this.transitionStack = new Stack<ProductTransition>();
        assert (this.initialLocation != null) : "The property automaton should have an initial state";
        ProductState startState =
            new ProductState(getGTS().startState(), this.initialLocation);
        setStartBuchiState(startState);
        this.productGTS.addState(startState);

    }

    /**
     * The next step makes atomic the full exploration of a state.
     */
    @Override
    public boolean next() {
        if (getAtBuchiState() == null) {
            return false;
        }

        // put current state on the stack
        searchStack().push(getAtBuchiState());
        this.atState = getAtBuchiState().getGraphState();
        // colour state cyan as being on the search stack
        getAtBuchiState().setColour(ModelChecking.cyan());

        // fully explore the current state
        exploreState(this.atState);
        this.collector.reset();

        // now look in the GTS for the outgoing transitions of the
        // current state with the current Buchi location and add
        // the resulting combined transition to the product GTS

        Set<GraphTransition> outTransitions = getAtState().getTransitionSet();
        Set<String> applicableRules = filterRuleNames(outTransitions);

        for (BuchiTransition buchiTrans : getAtBuchiLocation().outTransitions()) {
            if (buchiTrans.isEnabled(applicableRules)) {
                boolean finalState = true;
                for (GraphTransition nextTransition : outTransitions) {
                    if (nextTransition.getRole() == EdgeRole.BINARY) {
                        finalState = false;
                        ProductTransition productTransition =
                            addProductTransition(nextTransition,
                                buchiTrans.target());
                        if (counterExample(getAtBuchiState(),
                            productTransition.target())) {
                            // notify counter-example
                            for (ProductState state : searchStack()) {
                                getResult().add(state.getGraphState());
                            }
                            return false;
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

        return updateAtState();
    }

    @Override
    protected boolean updateAtState() {
        boolean result;
        if (this.collector.pickRandomNewState() != null) {
            ProductState newState = this.collector.pickRandomNewState();
            this.atBuchiState = newState;
            result = (this.atBuchiState != null);
        } else {
            ProductState s = null;

            // backtracking

            ProductState parent = null;

            do {
                // pop the current state from the search-stack
                searchStack().pop();
                // close the current state
                setClosed(getAtBuchiState());
                getAtBuchiState().setColour(ModelChecking.blue());
                // the parent is on top of the searchStack
                parent = peekSearchStack();
                if (parent != null) {
                    this.atBuchiState = parent;
                    s = getRandomOpenBuchiSuccessor(parent);
                }
            } while (parent != null && s == null); // ) &&
            // !getProductGTS().isOpen(getAtBuchiState()));

            // identify the reason of exiting the loop
            if (parent == null) {
                // the start state is reached and does not have open successors
                this.atBuchiState = null;
                result = false;
            } else if (s != null) { // the current state has an open successor (is
                // not really backtracking, a sibling state is
                // fully explored)
                this.atBuchiState = s;
                result = true;
            } else {
                // else, atState is open, so we continue exploring it
                result = true;
            }
        }
        if (!result) {
            getProductGTS().removeListener(this.collector);
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
            getAtBuchiState().setColour(ModelChecking.RED);
        } else {
            addProductTransition(null, transition.target());
        }
    }

    /**
     * Sets the result container for the strategy
     * @param result the result container
     */
    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * Returns the result container.
     * @return the result container
     */
    public Result getResult() {
        return this.result;
    }

    /**
     * Set the start Buchi graph-state.
     * @param startState the start Buchi graph-state
     */
    public void setStartBuchiState(ProductState startState) {
        this.startBuchiState = startState;
        this.atBuchiState = startState;
    }

    /**
     * Returns the start Büchi graph-state
     */
    public final ProductState startBuchiState() {
        return this.startBuchiState;
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

    @Override
    public void addGTSListener(Acceptor listener) {
        if (listener instanceof CycleAcceptor) {
            ((CycleAcceptor) listener).setStrategy(this);
            this.productGTS.addListener((CycleAcceptor) listener);
        }
    }

    @Override
    public void removeGTSListener(Acceptor listener) {
        if (listener instanceof CycleAcceptor) {
            this.productGTS.removeListener((CycleAcceptor) listener);
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
     * @param graphTransitions a set of {@link groove.lts.GraphTransition}s
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
        Strategy explore = new ExploreStateStrategy();
        if (getGTS().isOpen(state)) {
            explore.prepare(getGTS(), state);
            explore.next();
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
        if (!getAtBuchiState().isClosed()) {
            result = addTransition(getAtBuchiState(), transition, location);
        } else {
            for (ProductTransition nextTransition : getAtBuchiState().outTransitions()) {
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
            assert (isoTarget.iteration() <= ModelChecking.CURRENT_ITERATION) : "This state belongs to the next iteration and should not be explored now.";
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
     * @return the product GTS
     */
    public ProductStateSet getProductGTS() {
        return this.productGTS;
    }

    /**
     * Returns the Büchi graph-state the strategy is currently at.
     */
    public ProductState getAtBuchiState() {
        return this.atBuchiState;
    }

    /**
     * Returns the value of <code>lastTransition</code>.
     * @return the value of <code>lastTransition</code>
     */
    public ProductTransition getLastTransition() {
        return this.lastTransition;
    }

    /**
     * Sets the last transition taken.
     * @param transition the value for <code>lastTransition</code>
     */
    public void setLastTransition(ProductTransition transition) {
        this.lastTransition = transition;
    }

    /**
     * Set the current buchi state
     * @param atState the state to set
     */
    public void setAtBuchiState(ProductState atState) {
        this.atBuchiState = atState;
    }

    /**
     * Returns the current Büchi location.
     */
    public BuchiLocation getAtBuchiLocation() {
        return getAtBuchiState().getBuchiLocation();
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
                "Property %s not parsed correctly", property), e);
        }
    }

    /** The Buchi start graph-state of the system. */
    private ProductState startBuchiState;
    /** The synchronised product of the system and the property. */
    protected ProductStateSet productGTS;
    /** The current Buchi graph-state the system is at. */
    protected ProductState atBuchiState;
    /** The transition by which the current Buchi graph-state is reached. */
    protected ProductTransition lastTransition;
    /** State collector which randomly provides unexplored states. */
    protected RandomNewStateChooser collector = new RandomNewStateChooser();

    private BuchiLocation initialLocation;
    private Stack<ProductState> searchStack;
    private Stack<ProductTransition> transitionStack;
    private Result result;
}
