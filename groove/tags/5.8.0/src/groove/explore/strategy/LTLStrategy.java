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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import gov.nasa.ltl.trans.Formula;
import groove.explore.ExploreResult;
import groove.explore.result.Acceptor;
import groove.explore.result.CycleAcceptor;
import groove.explore.util.RandomChooserInSequence;
import groove.explore.util.RandomNewStateChooser;
import groove.grammar.host.ValueNode;
import groove.graph.EdgeRole;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.match.MatcherFactory;
import groove.util.parse.FormatException;
import groove.verify.BuchiGraph;
import groove.verify.BuchiLocation;
import groove.verify.BuchiTransition;
import groove.verify.ModelChecking.Record;
import groove.verify.ProductState;
import groove.verify.ProductStateSet;
import groove.verify.ProductTransition;
import groove.verify.Proposition;
import groove.verify.Proposition.Arg;

/**
 * This class provides some default implementations for the methods that are
 * required for strategies that perform model checking activities.
 *
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class LTLStrategy extends Strategy {
    @Override
    protected void prepare(GTS gts, GraphState state, Acceptor acceptor) {
        assert acceptor instanceof CycleAcceptor;
        super.prepare(gts, state, acceptor);
        MatcherFactory.instance(gts.isSimple())
            .setDefaultEngine();
        this.stateSet = new ProductStateSet();
        this.stateSet.addListener(this.collector);
        this.acceptor = (CycleAcceptor) acceptor;
        this.acceptor.setStrategy(this);
        this.result = acceptor.getResult();
        this.stateSet.addListener(this.acceptor);
        this.stateStack = new Stack<>();
        assert (this.startLocation != null) : "The property automaton should have an initial state";
        ProductState startState = createState(gts.startState(), null, this.startLocation);
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
        assert property != null;
        this.property = property;
        try {
            Formula<Proposition> formula = groove.verify.Formula.parse(property)
                .toLtlFormula();
            BuchiGraph buchiGraph = BuchiGraph.getPrototype()
                .newBuchiGraph(Formula.Not(formula));
            this.startLocation = buchiGraph.getInitial();
        } catch (FormatException e) {
            throw new IllegalStateException(String.format("Error in property '%s'", property), e);
        }
    }

    /** Returns the property being checked (in string form as set by {@link #setProperty(String)}). */
    public String getProperty() {
        return this.property;
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
        getStateStack().push(state);
    }

    /**
     * Pops the top element of the state stack, and processes the fact
     * that this is now completely explored, without finding a counterexample.
     * @return the new top of the search stack, or {@code null} if
     * the stack is empty.
     */
    protected ProductState rollbackState() {
        ProductState previous = getStateStack().pop();
        // close the current state
        getStateSet().setClosed(previous);
        colourState(previous);
        return getStateStack().isEmpty() ? null : getStateStack().peek();
    }

    /**
     * Looks in the GTS for the outgoing transitions of the
     * current state with the current Buchi location and add
     * the resulting combined transition to the product GTS
     * @return {@code true} if a counterexample was found
     */
    protected boolean exploreState(ProductState prodState) {
        boolean result = false;
        Set<? extends GraphTransition> outTransitions = prodState.getGraphState()
            .getTransitions();
        Set<Proposition> satisfiedProps = getProps(outTransitions);
        trans: for (BuchiTransition buchiTrans : prodState.getBuchiLocation()
            .outTransitions()) {
            if (buchiTrans.isEnabled(satisfiedProps)) {
                boolean finalState = true;
                for (GraphTransition trans : outTransitions) {
                    if (trans.getRole() == EdgeRole.BINARY) {
                        finalState = false;
                        ProductTransition prodTrans =
                            addTransition(prodState, trans, buchiTrans.target());
                        result = findCounterExample(prodState, prodTrans.target());
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
    protected ProductState computeNextState() {
        ProductState result = getFreshState();
        if (result == null) {
            result = backtrack();
        }
        return result;
    }

    /**
     * Backtracks the state stack, and returns the
     * topmost unexplored state.
     * @return the topmost incompletely explored state on the
     * state stack, or {@code null} if there is none.
     */
    protected ProductState backtrack() {
        ProductState result = null;
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
    protected ProductState getFreshState() {
        return this.collector.pickRandomNewState();
    }

    /**
     * Colours a given state, in the course of backtracking.
     */
    protected void colourState(ProductState state) {
        state.setColour(getRecord().blue());
    }

    /** Tests if a counterexample can be constructed between given
     * source and target states; if so, adds the counterexample to the result.
     * @param source source state of the potential counterexample
     * @param target target state of the potential counterexample
     * @return {@code true} if a counterexample was found
     */
    protected final boolean findCounterExample(ProductState source, ProductState target) {
        boolean result = (target.colour() == getRecord().cyan()) && (source.getBuchiLocation()
            .isAccepting()
            || target.getBuchiLocation()
                .isAccepting());
        if (result) {
            // notify counter-example
            for (ProductState state : getStateStack()) {
                this.result.addState(state.getGraphState());
            }
        }
        return result;
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
     * Returns a random open successor of a state, if any. Returns null
     * otherwise.
     */
    protected ProductState getNextSuccessor(ProductState state) {
        RandomChooserInSequence<ProductState> chooser = new RandomChooserInSequence<>();
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
    private Set<Proposition> getProps(Set<? extends GraphTransition> transitions) {
        return transitions.stream()
            .map(t -> toProp(t))
            .collect(Collectors.toSet());
    }

    private Proposition toProp(GraphTransition trans) {
        List<Arg> args = Arrays.stream(trans.label()
            .getArguments())
            .map(a -> a instanceof ValueNode ? Arg.arg(((ValueNode) a).toTerm())
                : Arg.arg(a.toString()))
            .collect(Collectors.toList());
        return Proposition.prop(trans.getAction()
            .getQualName(), args);
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
    private ProductTransition addTransition(ProductState source, GraphTransition transition,
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
                if (nextTransition.graphTransition()
                    .equals(transition)
                    && nextTransition.target()
                        .getBuchiLocation()
                        .equals(targetLocation)) {
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
    private ProductState createState(GraphState state, GraphTransition transition,
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

    /** Returns the record for this model checking run. */
    final public Record getRecord() {
        return this.record;
    }

    /** Property to be chacked. */
    private String property;
    /** Record of this model checking run. */
    private Record record = new Record();
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
    /** Initial location of the Buchi graph encoding the property to be verified. */
    private BuchiLocation startLocation;
    private Stack<ProductState> stateStack;
    private ExploreResult result;
}
