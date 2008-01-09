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
 * $Id: AbstrExploreStrategyMenu.java,v 1.1 2007-11-28 16:07:41 iovka Exp $
 */
package groove.gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JMenu;

import groove.abs.Abstraction;
import groove.abs.lts.AbstrStateGenerator;
import groove.lts.ExploreStrategy;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.explore.AbstrFullStrategy;
import groove.trans.NameLabel;
import groove.view.DefaultGrammarView;

/** A menuc containing all available strategies for abstract simulation */
public class AbstrExploreStrategyMenu extends JMenu implements SimulationListener {


	// ------------------------------------------------------------------------
	// SIMULATION LISTENER METHODS
	// ------------------------------------------------------------------------
	
	public void applyTransitionUpdate(GraphTransition transition) {
		setStateUpdate(transition.source());
	}

	public void setGrammarUpdate(DefaultGrammarView grammar) {
		for (Action action : this.strategyActionMap.values()) {
			action.setEnabled(false);
		}
		
	}

	public void setRuleUpdate(NameLabel name) {
		// empty
	}

	public void setStateUpdate(GraphState state) {
		// empty
	}

	public void setTransitionUpdate(GraphTransition transition) {
		setStateUpdate(transition.source());
	}

	public void startSimulationUpdate(GTS gts) {
		for (Action action : this.strategyActionMap.values()) {
			action.setEnabled(this.simulator.isAbstractSimulation());
		}
	}
	
	// ------------------------------------------------------------------------
	// OTHER METHODS
	// ------------------------------------------------------------------------
	
	
    /**
     * Adds an explication strategy action to the end of this menu.
     * @param strategy the new exploration strategy
     */
    public void addExploreStrategy(ExploreStrategy strategy) {
        Action generateAction = this.simulator.createGenerateLTSAction(strategy);
        generateAction.setEnabled(this.simulator.isAbstractSimulation());
        this.strategyActionMap.put(strategy, generateAction);
        add(generateAction);
    }
	
    /** Refreshes the exploration options for all strategies with the options of the simulator. */
    public void refreshOptions () {
    	for (ExploreStrategy strategy : this.strategyActionMap.keySet()) {
    		try {
    			((AbstrStateGenerator) strategy).setOptions(this.simulator.getAbstrSimulationOptions());
    		} catch (ClassCastException e) {
    			// empty
    		}
		}
    }
	
	// ------------------------------------------------------------------------
	// FIELDS, CONSTRUCTORS AND STANDARD METHODS
	// ------------------------------------------------------------------------
	
    /**
     * Constructs an exploration menu on top of a given simulator.
     * The menu disables when all states are closed.
     */
    public AbstrExploreStrategyMenu (Simulator simulator) {
    	this(simulator, true);
    }
    
    /**
     * Constructs an abstract exploration menu on top of a given simulator.
     * The menu will optionally disable as soon as all states are closed.
     * @param simulator the associated simulator
     * @param disableOnFinish <tt>true</tt> if the menu is to be disabled when
     * the last state is closed
     */
    public AbstrExploreStrategyMenu(Simulator simulator, boolean disableOnFinish) {
    	super(Options.EXPLORE_MENU_NAME);
        this.simulator = simulator;
        simulator.addSimulationListener(this);
        addExploreStrategy(new AbstrFullStrategy());
    }
    
    private Simulator simulator;
    
    /** Mapping from exploratin strategies to {@link Action}s resulting in that strategy. */
    private final Map<ExploreStrategy,Action> strategyActionMap = new HashMap<ExploreStrategy,Action>();
    
    
}
