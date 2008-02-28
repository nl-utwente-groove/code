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
 * $Id: DefaultModelCheckingStrategy.java,v 1.3 2008-02-28 06:15:29 kastenberg Exp $
 */

package groove.explore.strategy;

import groove.explore.result.Acceptor;
import groove.explore.result.Result;
import groove.explore.util.RandomChooserInSequence;
import groove.explore.util.RandomNewStateChooser;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.Node;
import groove.gui.FormulaDialog;
import groove.gui.Simulator;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.ProductGTS;
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

/** This depth-first search algorithm systematically generates all outgoing 
 * transitions of any visited state.
 * 
 * At each step, the exploration continues with a random successor fresh state,
 * or backtracks if there are no unexplored successor states.
 * 
 * Even though this depth first search backtracks for finding the next state
 * to explore, it is not considered as a backtracking strategy (in the sense
 * of {@link AbstractBacktrackingStrategy}. This is because all explored
 * states are closed, thus the strategy does not need to cache any
 * information, neither to know from where it backtracked. 
 * 
 * @author Harmen Kastenberg
 *
 */
public abstract class DefaultModelCheckingStrategy<T> extends AbstractStrategy implements ModelCheckingStrategy<T> {

	@Override
	public void setGTS(GTS gts) {
		super.setGTS(gts);
//		gts.addGraphListener(this.collector);
	}

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

	public void setStartBuchiState(BuchiGraphState startState) {
		this.startBuchiState = startState;
		this.atBuchiState = startState;
	}

	public final BuchiGraphState startBuchiState() {
		return startBuchiState;
	}

	@Override
	public void setState(GraphState state) {
		super.setState(state);
//		this.atBuchiState = (BuchiGraphState) getProductGTS().startBuchiState();
//		BuchiGraphState productState = new BuchiGraphState(getProductGTS().getRecord(), state, getInitialLocation());
//		this.atBuchiState = productState;
//		getProductGTS().startState();
	}

	public void setClosed(BuchiGraphState state) {
		getProductGTS().setClosed(state);
	}

	protected BuchiGraphState popSearchStack() {
		if (searchStack().isEmpty()) {
			return null;
		} else {
			return searchStack().pop();
		}
	}

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

	protected RandomNewStateChooser collector = new RandomNewStateChooser();

    /**
     * Initialize the necessary fields
     * @throws IllegalArgumentException
     */
    public void setup() throws IllegalArgumentException {
        state2node = new HashMap<IState,Node>();
    	currentPath = new Stack<GraphTransition>();
    	searchStack = new Stack<BuchiGraphState>();

    	setProperty();
    	assert (property != null) : "Property should have been set already.";
    	automaton = LTL2BA4J.formulaToBA("! " + property);
    	processAutomaton(automaton);
    	assert (initialLocation != null) : "The property automaton should have an initial state";
    	BuchiGraphState startState = new BuchiGraphState(productGTS.getRecord(), getGTS().startState(), initialLocation, null);
    	setStartBuchiState(startState);
    	productGTS.setStartState(startState);
    	this.productGenerator = productGTS.getRecord().getStateGenerator(productGTS);
    }

    protected boolean isEnabled(Edge edge, Set<String> applicableRules) {
    	boolean result = true;
    	ITransition transition = negPropertyGraph.getTransition(edge);
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
     * @param automaton
     * @return
     */
    protected void processAutomaton(Collection<ITransition> automaton) {
    	Graph prototype = GraphFactory.getInstance(BuchiAutomatonGraph.getPrototype()).newGraph();
    	assert (prototype instanceof BuchiAutomatonGraph): "Resulting graph wrongly instantiated.";
        Map<IState,BuchiLocation> state2location = new HashMap<IState,BuchiLocation>();
    	BuchiAutomatonGraph result = (BuchiAutomatonGraph) prototype;

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
     * Returns the product GTS.
     * @return the product GTS
     */
    public ProductGTS getProductGTS() {
    	return productGTS;
    }

    public StateGenerator getProductGenerator() {
    	return this.productGenerator;
    }

    /**
     * Returns the current Buchi graph-state.
     * @return the current Buchi graph-state
     */
    public BuchiGraphState getAtBuchiState() {
    	return atBuchiState;
    }

    public void setAtBuchiState(BuchiGraphState atState) {
    	this.atBuchiState = atState;
    }

    public BuchiLocation getAtPropertyLocation() {
    	return getAtBuchiState().getBuchiLocation();
    }

    public BuchiLocation getInitialLocation() {
    	return startBuchiState().getBuchiLocation();
    }

    public Stack<BuchiGraphState> searchStack() {
    	return searchStack;
    }

    public void setProperty(String property) {
    	this.property = property;
    }

    private void setProperty() {
    	if (property == null) {
    		String property = getProperty();
    		setProperty(property);
    	}
    }

    /**
     * @deprecated
     */
    @Deprecated
    protected Stack<GraphTransition> currentPath() {
    	return currentPath;
    }

    /**
     * @deprecated
     */
    @Deprecated
    protected BuchiAutomatonGraph negPropertyGraph() {
    	return negPropertyGraph;
    }

    public void setSimulator(Simulator simulator) {
    	this.simulator = simulator;
    }

	protected String getProperty() {
			FormulaDialog dialog = simulator.getFormulaDialog();
			dialog.showDialog(simulator.getFrame());
			String property = dialog.getProperty();
			if (property != null) {
				return property;
			}
		return null;
	}

	private BuchiGraphState startBuchiState;
	private ProductGTS productGTS;
    private StateGenerator productGenerator;
    protected BuchiGraphState atBuchiState;

    private String property;
    private Collection<ITransition> automaton;
    private BuchiLocation initialLocation;
    private BuchiAutomatonGraph negPropertyGraph;

    protected Simulator simulator;

    protected Map<IState, Node> state2node = new HashMap<IState,Node>();
    private Stack<GraphTransition> currentPath;
    private Stack<BuchiGraphState> searchStack;
    private Result<T> result;
}
