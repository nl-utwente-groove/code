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
 * $Id: ScenarioMenu.java,v 1.4 2008-03-04 14:48:57 kastenberg Exp $
 */
package groove.gui;

import groove.explore.ConditionalScenarioHandler;
import groove.explore.ScenarioHandler;
import groove.explore.ScenarioHandlerFactory;
import groove.explore.result.CycleAcceptor;
import groove.explore.result.EmptyAcceptor;
import groove.explore.result.EmptyResult;
import groove.explore.result.ExploreCondition;
import groove.explore.result.FinalStateAcceptor;
import groove.explore.result.InvariantViolatedAcceptor;
import groove.explore.result.IsRuleApplicableCondition;
import groove.explore.result.SizedResult;
import groove.explore.strategy.BoundedNestedDFSStrategy;
import groove.explore.strategy.BranchingStrategy;
import groove.explore.strategy.BreadthFirstModelCheckingStrategy;
import groove.explore.strategy.BreadthFirstStrategy;
import groove.explore.strategy.DepthFirstStrategy2;
import groove.explore.strategy.GraphNodeSizeBoundary;
import groove.explore.strategy.LinearStrategy;
import groove.explore.strategy.NestedDFSStrategy;
import groove.explore.strategy.OptimizedBoundedNestedDFSStrategy;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.LTS;
import groove.lts.LTSAdapter;
import groove.lts.State;
import groove.trans.NameLabel;
import groove.trans.Rule;
import groove.util.GrooveModules;
import groove.view.DefaultGrammarView;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JMenu;

/**
 * 
 * @author Arend Rensink
 * @author Iovka Boneva
 * @version $Revision: 1.4 $
 */
public class ScenarioMenu extends JMenu implements SimulationListener {
    /**
     * Constructs an exploration menu on top of a given simulator.
     * The menu will disable as soon as all states are closed.
     * @param simulator the associated simulator
     */
    public ScenarioMenu(Simulator simulator) {
        this(simulator, true);
    }
    
    /**
     * Constructs an exploration menu on top of a given simulator.
     * The menu will optionally disable as soon as all states are closed.
     * @param simulator the associated simulator
     * @param disableOnFinish <tt>true</tt> if the menu is to be disabled when
     * the last state is closed
     */
    public ScenarioMenu(Simulator simulator, boolean disableOnFinish) {
        super(Options.EXPLORE_MENU_NAME);
        this.simulator = simulator;
        this.disableOnFinish = disableOnFinish;
        simulator.addSimulationListener(this);

        ScenarioHandler handler;

        handler = ScenarioHandlerFactory.getScenario(
        		new BreadthFirstStrategy(), new EmptyResult<Object>(), new EmptyAcceptor(),
        		"Breadth first full exploration.", "Full exploration (breadth-first, aliasing)");
        addScenarioHandler(handler);

        handler = ScenarioHandlerFactory.getScenario(
        		new BranchingStrategy(), new EmptyResult<Object>(), new EmptyAcceptor(),
        		"Branching full exploration.", "Full exploration (branching, aliasing)");
        addScenarioHandler(handler);

        handler = ScenarioHandlerFactory.getScenario(
        		new DepthFirstStrategy2(), new EmptyResult<Object>(), new EmptyAcceptor(),
        		"Depth first full exploration.", "Full exploration (depth-first, no aliasing)");
        addScenarioHandler(handler);

        
        handler = ScenarioHandlerFactory.getScenario(
        		new LinearStrategy(), new EmptyResult<Object>(), new EmptyAcceptor(), 
        		"Explore first transition until a final state or a loop is reached.", "Linear exploration");
        addScenarioHandler(handler);
        
        handler = ScenarioHandlerFactory.getScenario(
        		new DepthFirstStrategy2(), new SizedResult<GraphState>(1), new FinalStateAcceptor(), 
        		"", "Find a final state (depth-first)"	);
        addScenarioHandler(handler);

        handler = ScenarioHandlerFactory.getScenario(
        		new BreadthFirstStrategy(), new SizedResult<GraphState>(1), new FinalStateAcceptor(), 
        		"", "Find a final state (breadth-first)"	);
        addScenarioHandler(handler);
        
        handler = ScenarioHandlerFactory.getConditionalScenario(
        		new BreadthFirstStrategy(), new SizedResult<GraphState>(1), new InvariantViolatedAcceptor<Rule>(), 
        		"", "Check invariant", false);
        addScenarioHandler(handler);

        handler = ScenarioHandlerFactory.getConditionalScenario(
        		new BreadthFirstStrategy(), new SizedResult<GraphState>(1), new InvariantViolatedAcceptor<Rule>(), 
        		"", "Check invariant", true);
        addScenarioHandler(handler);

        // the following explore-strategies are only provided
        // if the LTL module is loaded
        if (System.getProperty(GrooveModules.GROOVE_MODULE_LTL_VERIFICATION).equals(GrooveModules.GROOVE_MODULE_ENABLED)) {
        	handler = ScenarioHandlerFactory.getModelCheckingScenario(
        			new NestedDFSStrategy(),
        			new SizedResult<GraphState>(1),
        			new CycleAcceptor<GraphState>(), 
        			"", "Nested Depth-First Search", simulator);
        	addScenarioHandler(handler);

        	handler = ScenarioHandlerFactory.getModelCheckingScenario(
        			new BreadthFirstModelCheckingStrategy(),
        			new SizedResult<GraphState>(1),
        			new CycleAcceptor<GraphState>(), 
        			"", "Breadth-First Search", simulator);
        	addScenarioHandler(handler);

        	handler = ScenarioHandlerFactory.getBoundedModelCheckingScenario(
        			new BoundedNestedDFSStrategy(),
        			new SizedResult<GraphState>(1),
        			new CycleAcceptor<GraphState>(),
        			new GraphNodeSizeBoundary(8,5),
        			"", "Bounded Nested Depth-First Search (naive)", simulator);
        	addScenarioHandler(handler);

        	handler = ScenarioHandlerFactory.getBoundedModelCheckingScenario(
        			new OptimizedBoundedNestedDFSStrategy(),
        			new SizedResult<GraphState>(1),
        			new CycleAcceptor<GraphState>(),
        			new GraphNodeSizeBoundary(8,5),
        			"", "Bounded Nested Depth-First Search (optimized)", simulator);
        	addScenarioHandler(handler);
        }

//        handler = ScenarioHandlerFactory.getConditionalScenario(
//        		new RuleBoundedStrategy(), "Only explore states in which a rule is applicable", "Bounded", false);
//        addScenarioHandler(handler);
//        
//        handler = ScenarioHandlerFactory.getConditionalScenario(
//        		new RuleBoundedStrategy(), "Only explore states in which a rule is applicable", "Bounded", true);
//        addScenarioHandler(handler);
          
    }

    /**
     * Adds an explication strategy action to the end of this menu.
     * @param handler the new exploration strategy
     */
    public void addScenarioHandler(ScenarioHandler handler) {
        Action generateAction = simulator.createLaunchScenarioAction(handler);
        generateAction.setEnabled(false);
        scenarioActionMap.put(handler, generateAction);
        add(generateAction);
    }

    // ----------------------------- simulation listener methods -----------------------
    public void setGrammarUpdate(DefaultGrammarView grammar) {
		setStateUpdate(null);
		// the lts's of the strategies in this menu are changed
		// moreover, the conditions in condition strategies are reset
		// furthermore, the enabling is (re)set
		for (Action action : scenarioActionMap.values()) {
			action.setEnabled(false);
		}
	}

    public void startSimulationUpdate(GTS gts) {
		gtsListener.set(gts);
		// the lts's of the strategies in this menu are changed
		// moreover, the conditions in condition strategies are reset
		// furthermore, the enabling is (re)set
		for (Map.Entry<ScenarioHandler, Action> entry : scenarioActionMap.entrySet()) {
			ScenarioHandler handler = entry.getKey();
			Action generateAction = entry.getValue();
			handler.setGTS(gts);
			if (handler instanceof ConditionalScenarioHandler) {
				if (simulator.getCurrentRule() != null) {
    				ExploreCondition<Rule> explCond = new IsRuleApplicableCondition();
    				String ruleName = simulator.getCurrentRule().getName();
    				explCond.setCondition(gts.getGrammar().getRule(ruleName));
    				((ConditionalScenarioHandler<Rule>) handler).setCondition(explCond, ruleName);
    				generateAction.putValue(Action.NAME, handler.getName());
					generateAction.setEnabled(true);
				} else {
					((ConditionalScenarioHandler<?>) handler).setCondition(null, "");
					generateAction.putValue(Action.NAME, handler.getName());
					generateAction.setEnabled(false);
				}
			} else {
				generateAction.setEnabled(true);
			}
		}
		setStateUpdate(gts.startState());
	}

	public void setStateUpdate(GraphState state) {
        for (Map.Entry<ScenarioHandler,Action> entry: scenarioActionMap.entrySet()) {
            ScenarioHandler handler = entry.getKey();
            Action generateAction = entry.getValue();
            handler.setState(state);
            generateAction.putValue(Action.NAME, handler.getName());
        }
    }

    public void setRuleUpdate(NameLabel name) {
    	GTS gts = simulator.getCurrentGTS();
    	if (gts != null) {
    		for (Map.Entry<ScenarioHandler,Action> entry: scenarioActionMap.entrySet()) {
    			ScenarioHandler handler = entry.getKey();
    			if (handler instanceof ConditionalScenarioHandler) {
    				Action generateAction = entry.getValue();
    				ExploreCondition<Rule> explCond = new IsRuleApplicableCondition();
    				explCond.setCondition(gts.getGrammar().getRule(name));
    				((ConditionalScenarioHandler<Rule>) handler).setCondition(explCond, name.name());
    				generateAction.putValue(Action.NAME, handler.getName());
    				generateAction.setEnabled(true);
    			}
    		}
    	}
    }

    public void setTransitionUpdate(GraphTransition transition) {
        setStateUpdate(transition.source());
    }

    public void applyTransitionUpdate(GraphTransition transition) {
        setStateUpdate(transition.target());
    }

    /**
	 * @return Returns the disableOnFinish.
	 */
	final boolean isDisableOnFinish() {
		return this.disableOnFinish;
	}

	/**
     * The simulator with which this menu is associated.
     */
    private final Simulator simulator;
    /**
     * Indicates if the menu should be disable after tha last LTS state has closed.
     */
    private final boolean disableOnFinish;
    /** Mapping from exploratin strategies to {@link Action}s resulting in that strategy. */
    private final Map<ScenarioHandler,Action> scenarioActionMap = new HashMap<ScenarioHandler,Action>();
    /** The (permanent) GTS listener associated with this menu. */
    private final GTSListener gtsListener = new GTSListener();

    /** Listener that can be refreshed with the current GTS. */
    private class GTSListener extends LTSAdapter {
    	/** Empty constructor with the correct visibility. */
    	GTSListener() {
    		// empty
    	}
    	/** Sets the GTS to listen to. */
    	public void set(GTS newGTS) {
    		if (gts != null) {
    			gts.removeGraphListener(this);
    		}
    		gts = newGTS;
    		if (gts != null) {
    			gts.addGraphListener(this);
    			openStateCount = gts.openStateCount();
    			setEnabled(true);
    		}
    	}

        @Override
        public void closeUpdate(LTS graph, State explored) {
            assert graph == gts;
            openStateCount--;
            assert openStateCount == gts.openStateCount();
            if (openStateCount == 0 && ! isDisableOnFinish()) {
            	setEnabled(false);
            }
        }

        /** If the added element is a state, increases the open state count. */
        @Override
        public void addUpdate(GraphShape graph, Node node) {
        	openStateCount++;
        }
        
        /** The GTS this listener currently listens to. */
        private GTS gts;    
        /** The number of open states of the currently loaded LTS (if any). */
        private int openStateCount;
    }
}
