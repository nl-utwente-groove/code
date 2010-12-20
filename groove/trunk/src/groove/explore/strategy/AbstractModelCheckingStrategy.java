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
import groove.explore.result.Result;
import groove.explore.util.RandomChooserInSequence;
import groove.explore.util.RandomNewStateChooser;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.ProductGTS;
import groove.lts.ProductTransition;
import groove.verify.BuchiGraphState;
import groove.verify.BuchiLocation;
import groove.verify.DefaultBuchiLocation;
import groove.verify.DefaultBuchiTransition;
import groove.verify.ModelChecking;
import groove.verify.ltl2ba.BuchiGraph;
import groove.verify.ltl2ba.BuchiGraphFactory;
import groove.verify.ltl2ba.LTL2BuchiGraph;
import groove.verify.ltl2ba.LTL2BuchiLabel;
import groove.verify.ltl2ba.LTL2BuchiTransition;
import groove.verify.ltl2ba.NASABuchiGraph;
import groove.view.FormatException;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import rwth.i2.ltl2ba4j.model.IState;
import rwth.i2.ltl2ba4j.model.ITransition;

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
        setProductGTS(new ProductGTS(gts.getGrammar()));
        setup();
    }

    private void setProductGTS(ProductGTS gts) {
        this.productGTS = gts;
        this.productGTS.addListener(this.collector);
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
    public void setStartBuchiState(BuchiGraphState startState) {
        this.startBuchiState = startState;
        this.atBuchiState = startState;
    }

    /**
     * Returns the start Büchi graph-state
     */
    public final BuchiGraphState startBuchiState() {
        return this.startBuchiState;
    }

    /**
     * Closes Büchi graph-states.
     * @param state the Büchi graph-state to close
     */
    public void setClosed(BuchiGraphState state) {
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
    public boolean counterExample(BuchiGraphState source, BuchiGraphState target) {
        boolean result =
            (target.colour() == ModelChecking.cyan())
                && (source.getBuchiLocation().isAccepting() || target.getBuchiLocation().isAccepting());

        // if (result) {
        // System.out.println("Counter-example found");
        // }

        return result;
    }

    /**
     * Pops the top element from the search-stack
     * @return the top element from the search-stack
     */
    protected BuchiGraphState popSearchStack() {
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
    protected BuchiGraphState peekSearchStack() {
        if (searchStack().isEmpty()) {
            return null;
        } else {
            return searchStack().peek();
        }
    }

    @Override
    public void addGTSListener(Acceptor listener) {
        this.productGTS.addListener(listener);
    }

    @Override
    public void removeGTSListener(Acceptor listener) {
        this.productGTS.removeListener(listener);
    }

    /**
     * Returns a random open successor of a state, if any. Returns null
     * otherwise.
     */
    protected GraphState getRandomOpenBuchiSuccessor(BuchiGraphState state) {
        Iterator<? extends GraphState> sucIter = state.getNextStateIter();
        RandomChooserInSequence<GraphState> chooser =
            new RandomChooserInSequence<GraphState>();
        while (sucIter.hasNext()) {
            GraphState s = sucIter.next();
            assert (s instanceof BuchiGraphState) : "Expected a Buchi graph-state instead of a "
                + s.getClass();
            if (getProductGTS().getOpenStates().contains(s)) {
                chooser.show(s);
            }
        }
        return chooser.pickRandom();
    }

    /**
     * Returns the first open successor of a state, if any. Returns null
     * otherwise.
     */
    protected final GraphState getFirstOpenBuchiSuccessor(BuchiGraphState state) {
        Iterator<? extends GraphState> sucIter = state.getNextStateIter();
        while (sucIter.hasNext()) {
            GraphState s = sucIter.next();
            assert (s instanceof BuchiGraphState) : "Expected a Buchi graph-state instead of a "
                + s.getClass();
            if (getProductGTS().getOpenStates().contains(s)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Initialise the data structures used during exploration.
     */
    public void setup() {
        // currentPath = new Stack<GraphTransition>();
        this.searchStack = new Stack<BuchiGraphState>();
        this.transitionStack = new Stack<ProductTransition>();
        assert (this.initialLocation != null) : "The property automaton should have an initial state";
        BuchiGraphState startState =
            new BuchiGraphState(this.productGTS.getRecord(),
                getGTS().startState(), this.initialLocation, null);
        setStartBuchiState(startState);
        this.productGTS.setStartState(startState);
    }

    /**
     * Constructs an automaton-graph from a collection of {@link ITransition}s.
     * @param automaton the collection of {@link ITransition}s
     */
    protected void processAutomaton(Collection<ITransition> automaton) {
        Map<IState,DefaultBuchiLocation> state2location =
            new HashMap<IState,DefaultBuchiLocation>();
        // BuchiAutomatonGraph result = (BuchiAutomatonGraph) prototype;

        for (ITransition t : automaton) {
            IState sourceState = t.getSourceState();
            DefaultBuchiLocation sourceLocation;
            IState targetState = t.getTargetState();
            DefaultBuchiLocation targetLocation;

            if (state2location.containsKey(sourceState)) {
                sourceLocation = state2location.get(sourceState);
            } else {
                sourceLocation = new DefaultBuchiLocation();
                state2location.put(sourceState, sourceLocation);
            }

            if (state2location.containsKey(targetState)) {
                targetLocation = state2location.get(targetState);
            } else {
                targetLocation = new DefaultBuchiLocation();
                state2location.put(targetState, targetLocation);
            }
            LTL2BuchiLabel label = new LTL2BuchiLabel(t.getLabels());
            DefaultBuchiTransition transition =
                new LTL2BuchiTransition(sourceLocation, label, targetLocation);
            sourceLocation.addTransition(transition);

            // register the initial and final states
            if (sourceState.isInitial()) {
                this.initialLocation = sourceLocation;
            }
            if (sourceState.isFinal()) {
                sourceLocation.setAccepting();
            }
            if (targetState.isFinal()) {
                targetLocation.setAccepting();
            }
        }
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
     * @see ProductGTS#addTransition(ProductTransition)
     */
    public ProductTransition addProductTransition(GraphTransition transition,
            BuchiLocation location) {
        ProductTransition result = null; // new
        // HashSet<ProductTransition>();
        // if the current source state is already closed
        // the product-gts contains all transitions and
        // we do not have to add new transitions.
        if (getProductGTS().isOpen(getAtBuchiState())) {
            result = addTransition(getAtBuchiState(), transition, location);
        } else {
            for (ProductTransition nextTransition : getProductGTS().outEdgeSet(
                getAtBuchiState())) {
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
    public ProductTransition addTransition(BuchiGraphState source,
            GraphTransition transition, BuchiLocation targetLocation) {
        // we assume that we only add transitions for modifying graph
        // transitions
        BuchiGraphState target =
            createBuchiGraphState(source, transition, targetLocation);
        BuchiGraphState isoTarget = getProductGTS().addState(target);
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

    private BuchiGraphState createBuchiGraphState(BuchiGraphState source,
            GraphTransition transition, BuchiLocation targetLocation) {
        if (transition == null) {
            // the system-state is a final one for which we assume an artificial
            // self-loop
            // the resulting Buchi graph-state is nevertheless the product of
            // the
            // graph-state component of the source Buchi graph-state and the
            // target
            // Buchi-location
            return new BuchiGraphState(getProductGTS().getRecord(),
                source.getGraphState(), targetLocation, source);
        } else {
            return new BuchiGraphState(getProductGTS().getRecord(),
                transition.target(), targetLocation, source);
        }
    }

    private ProductTransition createProductTransition(BuchiGraphState source,
            GraphTransition transition, BuchiGraphState target) {
        return new ProductTransition(source, transition, target);
    }

    /**
     * Returns the product GTS.
     * @return the product GTS
     */
    public ProductGTS getProductGTS() {
        return this.productGTS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see groove.explore.strategy.ModelCheckingStrategy#getAtBuchiState()
     */
    public BuchiGraphState getAtBuchiState() {
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
    public void setAtBuchiState(BuchiGraphState atState) {
        this.atBuchiState = atState;
    }

    public BuchiLocation getAtBuchiLocation() {
        return getAtBuchiState().getBuchiLocation();
    }

    public Stack<BuchiGraphState> searchStack() {
        return this.searchStack;
    }

    /**
     * Pushes the given state on the search stack.
     * @param state the state to push
     */
    public void pushState(BuchiGraphState state) {
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

        BuchiGraphFactory graphFactory;

        switch (LTL2BUCHI_METHOD) {
        case LTL2BA:
            graphFactory =
                BuchiGraphFactory.getInstance(LTL2BuchiGraph.getPrototype());
            break;
        case NASABUCHI:
            // this is the default Buchi graph factory but nevertheless we set
            // it explicitely
            graphFactory =
                BuchiGraphFactory.getInstance(NASABuchiGraph.getPrototype());
            break;
        default:
            graphFactory = BuchiGraphFactory.getInstance();
            assert false;
        }

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
    private BuchiGraphState startBuchiState;
    /** The synchronised product of the system and the property. */
    protected ProductGTS productGTS;
    /** The current Buchi graph-state the system is at. */
    protected BuchiGraphState atBuchiState;
    /** The transition by which the current Buchi graph-state is reached. */
    protected ProductTransition lastTransition;
    /** State collector which randomly provides unexplored states. */
    protected RandomNewStateChooser collector = new RandomNewStateChooser();

    private String property;
    private BuchiLocation initialLocation;
    private Stack<BuchiGraphState> searchStack;
    private Stack<ProductTransition> transitionStack;
    private Result result;
}
