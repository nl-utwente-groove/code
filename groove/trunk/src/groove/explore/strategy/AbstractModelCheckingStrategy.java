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

import groove.explore.result.Acceptor;
import groove.explore.result.CycleAcceptor;
import groove.explore.result.Result;
import groove.explore.util.RandomChooserInSequence;
import groove.explore.util.RandomNewStateChooser;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.verify.BuchiLocation;
import groove.verify.ModelChecking;
import groove.verify.ProductState;
import groove.verify.ProductStateSet;
import groove.verify.ProductTransition;
import groove.verify.ltl2ba.BuchiGraph;
import groove.verify.ltl2ba.BuchiGraphFactory;
import groove.view.FormatException;

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
public abstract class AbstractModelCheckingStrategy extends AbstractStrategy
        implements ModelCheckingStrategy {
    /** This implementation initialises the product automaton as well. */
    @Override
    public void prepare(GTS gts, GraphState state) {
        if (state != gts.startState()) {
            throw new IllegalArgumentException(
                "Model checking should start at initial state");
        }
        super.prepare(gts, state);
        this.productGTS = new ProductStateSet();
        this.productGTS.addListener(this.collector);
        setup();
    }

    public void setResult(Result result) {
        this.result = result;
    }

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
     * Returns the start B�chi graph-state
     */
    public final ProductState startBuchiState() {
        return this.startBuchiState;
    }

    /**
     * Closes B�chi graph-states.
     * @param state the B�chi graph-state to close
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
        assert listener instanceof CycleAcceptor;
        this.productGTS.addListener((CycleAcceptor) listener);
    }

    @Override
    public void removeGTSListener(Acceptor listener) {
        assert listener instanceof CycleAcceptor;
        this.productGTS.removeListener((CycleAcceptor) listener);
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
     * Initialise the data structures used during exploration.
     */
    public void setup() {
        // currentPath = new Stack<GraphTransition>();
        this.searchStack = new Stack<ProductState>();
        this.transitionStack = new Stack<ProductTransition>();
        assert (this.initialLocation != null) : "The property automaton should have an initial state";
        ProductState startState =
            new ProductState(getGTS().startState(), this.initialLocation);
        setStartBuchiState(startState);
        this.productGTS.addState(startState);
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
            // the resulting B�chi graph-state is nevertheless the product of
            // the
            // graph-state component of the source B�chi graph-state and the
            // target
            // B�chi-location
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

    /*
     * (non-Javadoc)
     * 
     * @see groove.explore.strategy.ModelCheckingStrategy#getAtBuchiState()
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

    public BuchiLocation getAtBuchiLocation() {
        return getAtBuchiState().getBuchiLocation();
    }

    public Stack<ProductState> searchStack() {
        return this.searchStack;
    }

    /**
     * Pushes the given state on the search stack.
     * @param state the state to push
     */
    public void pushState(ProductState state) {
        searchStack().push(state);
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

    public void setProperty(String property) {
        assert property != null;
        this.property = property;

        BuchiGraphFactory graphFactory = BuchiGraphFactory.getInstance();

        try {
            BuchiGraph buchiGraph =
                graphFactory.newBuchiGraph("!(" + this.property + ")");
            this.initialLocation =
                buchiGraph.initialLocations().iterator().next();
        } catch (FormatException e) {
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

    private String property;
    private BuchiLocation initialLocation;
    private Stack<ProductState> searchStack;
    private Stack<ProductTransition> transitionStack;
    private Result result;
}
