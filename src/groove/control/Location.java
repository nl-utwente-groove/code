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
 * $Id: Location.java,v 1.5 2007-11-26 08:58:12 fladder Exp $
 */
package groove.control;

import groove.trans.Rule;

import java.util.Set;
import java.util.SortedMap;



/**
 * 
 * The interface for control locations used for exploration of the state space.
 * 
 * @author Tom Staijen
 * @version $Revision $
 */
public interface Location {
	
//	public Set<ControlTransition> getTransitions(Rule rule);
	
	/**
	 * Returns whether this location is a success-state.
	 */
	public boolean isSuccess();
	
	/**
	 * Returns the reacheable {@Location}s given a certain rule.
	 * @param rule
	 * @return Set<Location>
	 */
	public StateSet targetSet(Rule rule);
	
	
	public SortedMap<Integer, Set<Rule>> ruleMap();
	
	
	/**
	 * Adds location to the current location (sort of merge).
	 * Returns true if the original set changed.
	 * @param location
	 */
	public boolean add(Location location);
}
