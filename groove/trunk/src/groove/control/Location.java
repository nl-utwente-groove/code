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
 * $Id: Location.java,v 1.4 2007-11-22 15:39:11 fladder Exp $
 */
package groove.control;

import groove.graph.Node;
import groove.trans.Rule;

import java.util.Set;

public interface Location extends Node {
	
	public Set<ControlTransition> getTransitions(Rule rule);
	public boolean isSuccess();
}
