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

import groove.explore.strategy.Strategy;

/** An object that holds a scenario. 
 * Handlers of this type are used by pre-defined scenarios in the {@link java.util.Generator} 
 * for compatibility with old versions of the {@link java.util.Generator}.
 * Such scenarios have an empty result and thus are completely defined by their strategy.
 * @author Iovka Boneva
 */
public interface GeneratorScenarioHandler extends ScenarioHandler {

	/** The strategy used by this scenario.
	 * @return The strategy used by this scenario.
	 */
	public Strategy getStrategy();
	
}
