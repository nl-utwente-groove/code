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
package groove.explore;

import groove.explore.result.Result;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.ProductGTS;


/** An object holding a scenario. Should be implemented for any pre-defined scenario.
 * Is used as an interface between tools using a scenario (e.g. {@link groove.util.Generator}, 
 * {@link groove.gui.Simulator}) and the pre-defined scenario.
 * @author Iovka Boneva
 *
 */
public interface ScenarioHandler {

	/** Creates and launches the scenario this handler is responsible for. 
	 */
	public void playScenario();
	
	/** A short description of the scenario. 
	 * @return A short description of the scenario. 
	 */
	public String getDescription();
	
	/** A short name for the scenario. 
	 * @return A short name for the scenario.
	 */
	public String getName();
	
	/**
	 * Returns the result of the scenario. 
	 * The result is valid only after the scenario has been played.
	 */
	public Result getResult();
	
	/** 
	 * Indicates if the last call of {@link #playScenario()}
	 * returned because of a thread interruption.
	 */
	public boolean isInterrupted();
	
	/** Sets the state where the scenario should start exploring. 
	 * @param state The state where the scenario should start exploring.
	 */
	public void setState (GraphState state);
	
	/** 
	 * Sets the transition system on which the scenario works. 
	 * The start state is in addition set to the GTS start state.
	 * @param gts The transition system on which the scenario works. 
	 */
	public void setGTS (GTS gts);	

	public ProductGTS getProductGTS();
}
