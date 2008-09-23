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
 * $Id$
 */
package groove.gui;

import groove.explore.ScenarioHandler;
import groove.explore.ScenarioHandlerFactory;
import groove.explore.result.Result;
import groove.explore.strategy.BoundedNestedDFSPocketStrategy;
import groove.explore.strategy.BoundedNestedDFSStrategy;
import groove.explore.strategy.NestedDFSStrategy;
import groove.explore.strategy.OptimizedBoundedNestedDFSPocketStrategy;
import groove.explore.strategy.OptimizedBoundedNestedDFSStrategy;
import groove.util.GrooveModules;

/** A menu for the model-checking actions.
 * 
 * @author Iovka Boneva
 * @version $Revision $
 */
public class MCScenarioMenu extends ScenarioMenu {
	
    /**
     * Constructs an model-checking menu on top of a given simulator.
     * The menu will disable as soon as all states are closed.
     * @param simulator the associated simulator
     */
    public MCScenarioMenu(Simulator simulator) {
        this(simulator, true);
    }

    
    /**
     * Constructs a model-checking menu on top of a given simulator.
     * The menu will optionally disable as soon as all states are closed.
     * @param simulator the associated simulator
     * @param disableOnFinish <tt>true</tt> if the menu is to be disabled when
     * the last state is closed
     */
    public MCScenarioMenu(Simulator simulator, boolean disableOnFinish) {
        super(simulator, disableOnFinish, Options.VERIFY_MENU_NAME);
    }
    
    @Override
    protected void createAddMenuItems () {
    	
    	ScenarioHandler handler;
    	
        // the following explore-strategies are only provided
        // if the LTL module is loaded
        if (System.getProperty(GrooveModules.GROOVE_MODULE_LTL_VERIFICATION).equals(GrooveModules.GROOVE_MODULE_ENABLED)) {
        	handler = ScenarioHandlerFactory.getModelCheckingScenario(
        			new NestedDFSStrategy(),
        			"",
        			"Nested Depth-First Search", 
        			simulator);
        	addScenarioHandler(handler);

//        	handler = ScenarioHandlerFactory.getModelCheckingScenario(
//        			new BreadthFirstModelCheckingStrategy(),
//        			new SizedResult<GraphState>(1),
//        			new CycleAcceptor<GraphState>(), 
//        			"", "Breadth-First Search", simulator);
//        	addScenarioHandler(handler);

        	handler = ScenarioHandlerFactory.getBoundedModelCheckingScenario(
        			new BoundedNestedDFSStrategy(),
        			//        			new GraphNodeSizeBoundary(10,5),
        			"",
        			"Bounded Nested Depth-First Search (naive)",
simulator);
        	addScenarioHandler(handler);

        	handler = ScenarioHandlerFactory.getBoundedModelCheckingScenario(
        			new BoundedNestedDFSPocketStrategy(),
        			//        			new GraphNodeSizeBoundary(10,5),
        			"",
        			"Bounded Nested Depth-First Search (naive)",
simulator);
        	addScenarioHandler(handler);

        	handler = ScenarioHandlerFactory.getBoundedModelCheckingScenario(
        			new OptimizedBoundedNestedDFSStrategy(),
        			"",
        			"Bounded Nested Depth-First Search (optimized)",
        			simulator);
        	addScenarioHandler(handler);

        	handler = ScenarioHandlerFactory.getBoundedModelCheckingScenario(
        			new OptimizedBoundedNestedDFSPocketStrategy(),
        			"",
        			"Bounded Nested Depth-First Search (optimized + pocket)",
        			simulator);
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
    
}
