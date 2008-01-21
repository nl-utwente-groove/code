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
 * $Id: ExploreStrategyMenu.java,v 1.12 2007-11-28 16:07:41 iovka Exp $
 */
package groove.gui;

import groove.graph.GraphShape;
import groove.graph.Node;
import groove.lts.ConditionalExploreStrategy;
import groove.lts.ExploreStrategy;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.LTS;
import groove.lts.LTSAdapter;
import groove.lts.State;
import groove.lts.explore.BarbedStrategy;
import groove.lts.explore.BoundedStrategy;
import groove.lts.explore.BranchingStrategy;
import groove.lts.explore.FullStrategy;
import groove.lts.explore.LinearStrategy;
import groove.trans.NameLabel;
import groove.view.DefaultGrammarView;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JMenu;

/**
 * 
 * @author Arend Rensink
 * @version $Revision: 1.12 $
 */
public class ExploreStrategyMenu extends JMenu implements SimulationListener {
    /**
     * Constructs an exploration menu on top of a given simulator.
     * The menu will disable as soon as all states are closed.
     * @param simulator the associated simulator
     */
    public ExploreStrategyMenu(Simulator simulator) {
        this(simulator, true);
    }
    
    /**
     * Constructs an exploration menu on top of a given simulator.
     * The menu will optionally disable as soon as all states are closed.
     * @param simulator the associated simulator
     * @param disableOnFinish <tt>true</tt> if the menu is to be disabled when
     * the last state is closed
     */
    public ExploreStrategyMenu(Simulator simulator, boolean disableOnFinish) {
        super(Options.EXPLORE_MENU_NAME);
        this.simulator = simulator;
        this.disableOnFinish = disableOnFinish;
        simulator.addSimulationListener(this);
        addExploreStrategy(new FullStrategy());
        addExploreStrategy(new BranchingStrategy());
        addExploreStrategy(new BarbedStrategy());
        addExploreStrategy(new LinearStrategy());
        addExploreStrategy(new BoundedStrategy());
        addExploreStrategy(new BoundedStrategy(true));
//       	addExploreStrategy(new PartialOrderStrategy(simulator));
    }

    /**
     * Adds an explication strategy action to the end of this menu.
     * @param strategy the new exploration strategy
     */
    public void addExploreStrategy(ExploreStrategy strategy) {
        Action generateAction = simulator.createGenerateLTSAction(strategy);
        generateAction.setEnabled(false);
        strategyActionMap.put(strategy, generateAction);
        add(generateAction);
    }

    // ----------------------------- simulation listener methods -----------------------
    public void setGrammarUpdate(DefaultGrammarView grammar) {
		setStateUpdate(null);
		// the lts's of the strategies in this menu are changed
		// moreover, the conditions in condition strategies are reset
		// furthermore, the enabling is (re)set
		for (Action action : strategyActionMap.values()) {
			action.setEnabled(false);
		}
	}

    public void startSimulationUpdate(GTS gts) {
		gtsListener.set(gts);
		// the lts's of the strategies in this menu are changed
		// moreover, the conditions in condition strategies are reset
		// furthermore, the enabling is (re)set
		for (Map.Entry<ExploreStrategy, Action> entry : strategyActionMap.entrySet()) {
			ExploreStrategy strategy = entry.getKey();
			Action generateAction = entry.getValue();
			if (strategy instanceof ConditionalExploreStrategy) {
				((ConditionalExploreStrategy) strategy).setCondition(null);
				generateAction.putValue(Action.NAME, strategy.toString());
				generateAction.setEnabled(false);
			}  
			else {
				generateAction.setEnabled(! this.simulator.isAbstractSimulation());
			}
		}
		setStateUpdate(gts.startState());
	}

	public void setStateUpdate(GraphState state) {
        for (Map.Entry<ExploreStrategy,Action> entry: strategyActionMap.entrySet()) {
            ExploreStrategy strategy = entry.getKey();
            Action generateAction = entry.getValue();
            strategy.setAtState(state);
            generateAction.putValue(Action.NAME, strategy.toString());
        }
    }

    public void setRuleUpdate(NameLabel name) {
    	GTS gts = simulator.getCurrentGTS();
    	if (gts != null) {
        for (Map.Entry<ExploreStrategy,Action> entry: strategyActionMap.entrySet()) {
            ExploreStrategy strategy = entry.getKey();
            if (strategy instanceof ConditionalExploreStrategy) {
                Action generateAction = entry.getValue();
                ((ConditionalExploreStrategy) strategy).setCondition(gts.getGrammar().getRule(name));
                generateAction.putValue(Action.NAME, strategy.toString());
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
    private final Map<ExploreStrategy,Action> strategyActionMap = new HashMap<ExploreStrategy,Action>();
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
