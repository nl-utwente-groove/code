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
 * $Id: ScenarioMenu.java,v 1.8 2008/03/26 07:29:40 kastenberg Exp $
 */
package groove.gui;

import groove.explore.ConditionalScenarioHandler;
import groove.explore.ScenarioHandler;
import groove.explore.ScenarioHandlerFactory;
import groove.explore.result.Acceptor;
import groove.explore.result.ExploreCondition;
import groove.explore.result.FinalStateAcceptor;
import groove.explore.result.InvariantViolatedAcceptor;
import groove.explore.result.IsRuleApplicableCondition;
import groove.explore.result.Result;
import groove.explore.strategy.BranchingStrategy;
import groove.explore.strategy.BreadthFirstStrategy;
import groove.explore.strategy.ExploreRuleDFStrategy;
import groove.explore.strategy.LinearStrategy;
import groove.explore.strategy.RandomLinearStrategy;
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
import groove.trans.RuleMatch;
import groove.view.DefaultGrammarView;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * 
 * @author Arend Rensink
 * @author Iovka Boneva
 * @version $Revision$
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
    	this(simulator, disableOnFinish, Options.EXPLORE_MENU_NAME);
    }
    
    /**
     * Constructs the exploration menu.
     * The menu will optionally disable as soon as all states are closed.
     * @param simulator the associated simulator
     * @param disableOnFinish <tt>true</tt> if the menu is to be disabled when
     * the last state is closed
     * @param menuName the name of the menu
     */
    protected ScenarioMenu(Simulator simulator, boolean disableOnFinish, String menuName) {
        super(menuName);
        this.simulator = simulator;
        this.disableOnFinish = disableOnFinish;
        simulator.addSimulationListener(this);
        
        createAddMenuItems();
    }
    
    /** Creates and adds the different menu items, corresponding to the different exploration scenarios. */
    protected void createAddMenuItems () {
 
        ScenarioHandler handler;

        handler = ScenarioHandlerFactory.getScenario(
        		new BranchingStrategy(), new Acceptor(), "Explores the full state space.",
        		"Full exploration (branching, aliasing)");
        addScenarioHandler(handler);
        
        handler = ScenarioHandlerFactory.getScenario(
        		new BreadthFirstStrategy(), new Acceptor(), "Explores all the new states reachable from the current state (breadth-first).",
        		"Full exploration (breadth-first, aliasing)");
        addScenarioHandler(handler);

        handler = ScenarioHandlerFactory.getScenario(
        		new ExploreRuleDFStrategy(), new Acceptor(), "Explores all the new states reachable from the current state (depth-first).",
        		"Full exploration (depth-first, no aliasing)");
        addScenarioHandler(handler);

        handler = ScenarioHandlerFactory.getScenario(
        		new LinearStrategy(), new Acceptor(), "Explores one transition for each state until a final state or a loop is reached.", 
        		"Linear exploration");
        addScenarioHandler(handler);

        handler = ScenarioHandlerFactory.getScenario(
        		new RandomLinearStrategy(), new Acceptor(), "Explores randomly (slower) one transition for each state until a final state or a loop is reached.", 
        		"Random Linear exploration");
        addScenarioHandler(handler);
        
        handler = ScenarioHandlerFactory.getScenario(
        		new BreadthFirstStrategy(), new FinalStateAcceptor(new Result(1)), "Looks for a final state starting from the current state (breadth-first)", 
        		"Find a final state (breadth-first)"	);
        addScenarioHandler(handler);
        
        handler = ScenarioHandlerFactory.getScenario(
        		new ExploreRuleDFStrategy(), new FinalStateAcceptor(new Result(1)), "Looks for a final state starting from the current state (depth-first).", 
        		"Find a final state (depth-first)"	);
        addScenarioHandler(handler);
        
        handler = ScenarioHandlerFactory.getConditionalScenario(
        		new BreadthFirstStrategy(), new InvariantViolatedAcceptor<Rule>(new Result(1)), "Explores all the new states reachable from the current state until the invariant is violated.", 
        		"Check invariant", false);
        addScenarioHandler(handler);

        handler = ScenarioHandlerFactory.getConditionalScenario(
        		new BreadthFirstStrategy(), new InvariantViolatedAcceptor<Rule>(new Result(1)), "Explores all the new states reachable from the current state until the invariant is violated.", 
        		"Check invariant", true);
        addScenarioHandler(handler);
        
        // IOVKA items related to model-checking are in the MCMMenu class
        
    }
    

    /**
     * Adds an explication strategy action to the end of this menu.
     * @param handler the new exploration strategy
     */
    public void addScenarioHandler(ScenarioHandler handler) {
        Action generateAction = simulator.createLaunchScenarioAction(handler);
        generateAction.setEnabled(false);
        scenarioActionMap.put(handler, generateAction);
        JMenuItem menuItem = add(generateAction);
        menuItem.setToolTipText(handler.getDescription());
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

    public void setMatchUpdate(RuleMatch match) {
        // nothing happens
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
    protected final Simulator simulator;
    /**
     * Indicates if the menu should be disable after tha last LTS state has closed.
     */
    protected final boolean disableOnFinish;
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
