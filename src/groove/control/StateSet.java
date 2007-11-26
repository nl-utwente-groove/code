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
 * $Id: StateSet.java,v 1.4 2007-11-26 08:58:12 fladder Exp $
 */
package groove.control;

import groove.trans.Rule;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 
 * A Location implementation consisting of a set of ControlStates.
 * 
 * @author Tom Staijen
 * @version $Revision $
 */
public class StateSet extends HashSet<ControlState> implements Location {

	SortedMap<Integer,Set<Rule>> priorityRuleMap = new TreeMap<Integer, Set<Rule>>(Rule.PRIORITY_COMPARATOR);
	
	public boolean isSuccess() {
		return false;
	}

	public StateSet targetSet(Rule rule) {
		StateSet newSet = new StateSet();
		for( ControlState cs : this ) {
			StateSet targets = cs.getRuleTargets(rule); 
			if( targets != null ) {
				newSet.addAll(targets);
			}
		}
		
		if( newSet.isEmpty() ) {
			System.err.println("targetSet empty for rule" + rule);
		}
		
		return newSet;
	}

	public boolean add(Location l) {
		if( l instanceof StateSet ) {
			return this.addAll((StateSet)l);
		} else {
			return false;
		}
	}
	
	public SortedMap<Integer, Set<Rule>> ruleMap() {
		SortedMap<Integer, Set<Rule>> myRuleMap = new TreeMap<Integer, Set<Rule>>(Rule.PRIORITY_COMPARATOR);
		for( ControlState cs : this ) {
			myRuleMap.putAll(cs.getRuleMap());
		}
		return myRuleMap;
	}
	
	/**
	 * Default Constructor
	 */
	public StateSet() {
		super();
	}
	
}
