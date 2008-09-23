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

import groove.explore.result.CycleAcceptor;
import groove.explore.strategy.ModelCheckingStrategy;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class ModelCheckingScenario extends DefaultScenario {
	/**
	 * Creates a new instance from a given strategy and acceptor.
	 */
	public ModelCheckingScenario(ModelCheckingStrategy strategy) {
		super(strategy, new CycleAcceptor(strategy));
	}

	@Override
	public ModelCheckingStrategy getStrategy() {
		return (ModelCheckingStrategy) super.getStrategy();
	}
}
