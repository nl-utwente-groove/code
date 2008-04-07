/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id: DefaultModelCheckingStrategy.java,v 1.5 2008/03/05 08:41:17 kastenberg Exp $
 */
package groove.explore.strategy;

import groove.explore.result.Acceptor;
import groove.explore.result.Result;
import groove.explore.util.RandomChooserInSequence;
import groove.explore.util.RandomNewStateChooser;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.Node;
import groove.gui.FormulaDialog;
import groove.gui.Simulator;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.ProductGTS;
import groove.lts.ProductTransition;
import groove.lts.StateGenerator;
import groove.verify.BuchiAutomatonGraph;
import groove.verify.BuchiGraphState;
import groove.verify.BuchiLocation;
import groove.verify.BuchiTransition;
import groove.verify.ModelChecking;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import rwth.i2.ltl2ba4j.LTL2BA4J;
import rwth.i2.ltl2ba4j.model.IGraphProposition;
import rwth.i2.ltl2ba4j.model.IState;
import rwth.i2.ltl2ba4j.model.ITransition;

/**
 * This class provides some default implementations for the methods that
 * are required for strategies that perform model checking activities.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.5 $
 */
public abstract class DefaultModelCheckingStrategy<T> extends AbstractStrategy implements ModelCheckingStrategy<T> {

	public void setProductGTS(ProductGTS gts) {
		this.productGTS = gts;
		productGTS.addListener(this.collector);
		setup();
	}

	public void setResult(Result<T> result) {
		this.result = result;
	}

	public Result<T> getResult() {
		return result;
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
	 * Returns the start Buchi graph-state.
	 * @return the start Buchi graph-state
	 */
	public final BuchiGraphState startBuchiState() {
		return startBuchiState;
	}

	@Override
	public void setState(GraphState state) {
		super.setState(state);
	}

	/**
	 * Closes Buchi graph-states.
	 * @param state the Buchi graph-state to close
	 */
	public void setClosed(BuchiGraphState state) {
		getProductGTS().setClosed(state);
	}

	/**
	 * Identifies special cases of counter-examples.
	 * @param source the source Buchi graph-state
	 * @param target the target Buchi graph-state
	 * @return <tt>true</tt> if the target Buchi graph-state has colour
	 * {@link ModelChecking#CYAN} and the buchi location of either the
	 * <tt>source</tt> or <tt>target</tt> is accepting, <tt>false</tt>
	 * otherwise.
	 */
	public boolean counterExample(BuchiGraphState source, BuchiGraphState target) {
		boolean result = (target.colour() == ModelChecking.cyan()) &&
		(source.getBuchiLocation().isSuccess(null) || target.getBuchiLocation().isSuccess(null));
		
//		if (result) {
//			System.out.println("Counter-example found");
//		}
		
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
		productGTS.addListener(listener);
	}

	/** Returns a random open successor of a state, if any. 
	 * Returns null otherwise. 
	 */
	protected GraphState getRandomOpenBuchiSuccessor(BuchiGraphState state) {
		Iterator<? extends GraphState> sucIter = state.getNextStateIter();
		RandomChooserInSequence<GraphState>  chooser = new RandomChooserInSequence<GraphState>();
		while (sucIter.hasNext()) {
			GraphState s = sucIter.next();
			assert (s instanceof BuchiGraphState) : "Expected a Buchi graph-state instead of a " + s.getClass();
			if (getProductGTS().getOpenStates().contains(s)) {
				chooser.show(s);
			}
		}
		return chooser.pickRandom();
	}

	/** Returns the first open successor of a state, if any. Returns null otherwise. */
	protected final GraphState getFirstOpenBuchiSuccessor(BuchiGraphState state) {
		Iterator<? extends GraphState> sucIter = state.getNextStateIter();
		while (sucIter.hasNext()) {
			GraphState s = sucIter.next();
			assert (s instanceof BuchiGraphState) : "Expected a Buchi graph-state instead of a " + s.getClass();
			if (getProductGTS().getOpenStates().contains(s)) {
				return s;
			}
		}
		return null;
	}

    /**
     * Initialize the necessary fields.
     * @throws IllegalArgumentException
     */
    public void setup() throws IllegalArgumentException {
        state2node = new HashMap<IState,Node>();
//    	currentPath = new Stack<GraphTransition>();
    	searchStack = new Stack<BuchiGraphState>();
    	transitionStack = new Stack<ProductTransition>();

    	initializeProperty();
    	assert (initialLocation != null) : "The property automaton should have an initial state";
    	BuchiGraphState startState = new BuchiGraphState(productGTS.getRecord(), getGTS().startState(), initialLocation, null);
    	setStartBuchiState(startState);
    	productGTS.setStartState(startState);
    	this.productGenerator = productGTS.getRecord().getStateGenerator(productGTS);
    }

    /**
     * Checks whether a given transition of the Buechi automaton is enabled.
     * This is checked by means of the set of rules that are applicable.
     * @param transition the transition to be checked for enabledness
     * @param applicableRules the set of applicable rules
     * @return <tt>true</tt> if the label on the transition evaluates to
     * <tt>true</tt> provided the set of applicable rules, <tt>false</tt>
     * otherwise
     */
    protected boolean isEnabled(BuchiTransition transition, Set<String> applicableRules) {
    	boolean result = true;
    	for (IGraphProposition gp: transition.getLabels()) {
    		if (gp.getFullLabel().equals(ModelChecking.SIGMA)) {
    			continue;
    		}
    		boolean applicable = false;
    		// only take the label of the proposition - negation will be checked afterwards
    		String prop = gp.getLabel();
    		for (String ruleName: applicableRules) {
    			if (prop.equals(ruleName)) {
    				applicable = true;
    			}
    		}
    		boolean match = (gp.isNegated() ^ applicable);
    		result = result && match;
    	}
    	return result;
    }

    /**
     * Constructs an automaton-graph from a collection of {@link ITransition}s.
     * @param automaton the collection of {@link ITransition}s
     */
    protected void processAutomaton(Collection<ITransition> automaton) {
    	Graph prototype = GraphFactory.getInstance(BuchiAutomatonGraph.getPrototype()).newGraph();
    	assert (prototype instanceof BuchiAutomatonGraph): "Resulting graph wrongly instantiated.";
        Map<IState,BuchiLocation> state2location = new HashMap<IState,BuchiLocation>();
//    	BuchiAutomatonGraph result = (BuchiAutomatonGraph) prototype;

    	for (ITransition t: automaton) {
    		IState sourceState = t.getSourceState();
    		BuchiLocation sourceLocation;
    		IState targetState = t.getTargetState();
    		BuchiLocation targetLocation;

    		if (state2location.containsKey(sourceState)) {
    			sourceLocation = state2location.get(sourceState);
    		} else {
    			sourceLocation = new BuchiLocation();
    			state2location.put(sourceState, sourceLocation);
    		}

    		if (state2location.containsKey(targetState)) {
    			targetLocation = state2location.get(targetState);
    		} else {
    			targetLocation = new BuchiLocation();
    			state2location.put(targetState, targetLocation);
    		}
    		BuchiTransition transition = new BuchiTransition(targetLocation, t.getLabels());
    		sourceLocation.addTransition(transition);

    		// register the initial and final states
    		if (sourceState.isInitial()) {
    			initialLocation = sourceLocation;
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
     * @return the set of rule names contained in the given <code>iterator</code>
     */
    protected Set<String> filterRuleNames(Set<GraphTransition> graphTransitions) {
    	Set<String> result = new HashSet<String>();
    	for (GraphTransition nextTransition: graphTransitions) {
    		result.add(nextTransition.getEvent().getRule().getName().name());
    	}
    	return result;
    }

    /**
     * Adds a product transition to the product gts. If the current state
     * is already explored we do not have to add anything. In that case,
     * we return the corresponding transition.
     * @param transition the graph-transition component for the product-transition
     * @param location the location of the target Buechi graph-state
     * @return @see {@link ProductGTS#addTransition(ProductTransition)}
     */
    public Set<ProductTransition> addProductTransition(GraphTransition transition, BuchiLocation location) {
    	Set<ProductTransition> result = null; // new HashSet<ProductTransition>();
    	// if the current source state is already closed
    	// the product-gts contains all transitions and
    	// we do not have to add new transitions.
    	if (getProductGTS().isOpen(getAtBuchiState())) {
        	result = getProductGenerator().addTransition(getAtBuchiState(), transition, location);
			assert(result.size() == 1) : "Expected the result to be of size 1 instead of " + result.size();
    	} else {
    		result = new HashSet<ProductTransition>();
    		for (ProductTransition nextTransition: getProductGTS().outEdgeSet(getAtBuchiState())) {
    			if (nextTransition.graphTransition().equals(transition) &&
    					nextTransition.target().getBuchiLocation().equals(location)) {
    				result.add(nextTransition);
    			}
    		}
			assert(result.size() == 1) : "Expected the result to be of size 1 instead of " + result.size();
			return result;
    	}
    	return result;
    }

    /**
     * Initialize the property to be verified.
     */
    private void initializeProperty() {
    	if (property == null) {
    		String property = getProperty();
    		setProperty(property);
    	}
    	assert (property != null) : "Property should have been set already.";
    	automaton = LTL2BA4J.formulaToBA("! " + property);
    	processAutomaton(automaton);
    }

    /**
     * Returns the product GTS.
     * @return the product GTS
     */
    public ProductGTS getProductGTS() {
    	return productGTS;
    }

    /**
     * Returns the state-generator used for constructing the product gts.
     * @return the current state-generator
     */
    public StateGenerator getProductGenerator() {
    	return this.productGenerator;
    }

    /* (non-Javadoc)
     * @see groove.explore.strategy.ModelCheckingStrategy#getAtBuchiState()
     */
    public BuchiGraphState getAtBuchiState() {
    	return atBuchiState;
    }

    /**
     * Returns the value of <code>lastTransition</code>.
     * @return the value of <code>lastTransition</code>
     */
    public ProductTransition getLastTransition() {
    	return lastTransition;
    }

    /**
     * Sets the last transition taken.
     * @param transition the value for <code>lastTransition</code>
     */
    public void setLastTransition(ProductTransition transition) {
    	this.lastTransition = transition;
    }

    /* (non-Javadoc)
     * @see groove.explore.strategy.ModelCheckingStrategy#setAtBuchiState(groove.verify.BuchiGraphState)
     */
    public void setAtBuchiState(BuchiGraphState atState) {
    	this.atBuchiState = atState;
    }

    /* (non-Javadoc)
     * @see groove.explore.strategy.ModelCheckingStrategy#getAtBuchiLocation()
     */
    public BuchiLocation getAtBuchiLocation() {
    	return getAtBuchiState().getBuchiLocation();
    }

    /* (non-Javadoc)
     * @see groove.explore.strategy.ModelCheckingStrategy#searchStack()
     */
    public Stack<BuchiGraphState> searchStack() {
    	return searchStack;
    }

    public void pushState(BuchiGraphState state) {
    	searchStack().push(state);
    }

    public Stack<ProductTransition> transitionStack() {
    	return transitionStack;
    }

    public void pushTransition(ProductTransition transition) {
    	transitionStack().push(transition);
    	assert (transitionStack().size() == (searchStack().size() - 1)) : "search stacks out of sync (" + transitionStack().size() + " vs " + searchStack().size() + ")";
    }

    /* (non-Javadoc)
     * @see groove.explore.strategy.ModelCheckingStrategy#setSimulator(groove.gui.Simulator)
     */
    public void setSimulator(Simulator simulator) {
    	this.simulator = simulator;
    }

    /* (non-Javadoc)
     * @see groove.explore.strategy.ModelCheckingStrategy#setProperty(java.lang.String)
     */
    public void setProperty(String property) {
    	this.property = property;
    }

    /* (non-Javadoc)
	 * @see groove.explore.strategy.ModelCheckingStrategy#getProperty()
	 */
	public String getProperty() {
			FormulaDialog dialog = simulator.getFormulaDialog();
			dialog.showDialog(simulator.getFrame());
			String property = dialog.getProperty();
			if (property != null) {
				return property;
			}
		return null;
	}

	/** The Buchi start graph-state of the system. */
	private BuchiGraphState startBuchiState;
	/** The synchronized product of the system and the property. */
	protected ProductGTS productGTS;
    /** The current Buchi graph-state the system is at. */
    protected BuchiGraphState atBuchiState;
    /** The transition by which the current Buchi graph-state is reached. */
    protected ProductTransition lastTransition;
    /** The simulator instance that triggers this part. */
    protected Simulator simulator;
    /** A mapping from {@link IState}s to {@link Node}s. */
    protected Map<IState, Node> state2node = new HashMap<IState,Node>();
	/** State collector which randomly provides unexplored states. */
	protected RandomNewStateChooser collector = new RandomNewStateChooser();

    private StateGenerator productGenerator;
    private String property;
    private Collection<ITransition> automaton;
    private BuchiLocation initialLocation;
    private Stack<BuchiGraphState> searchStack;
    private Stack<ProductTransition> transitionStack;
    private Result<T> result;

}
