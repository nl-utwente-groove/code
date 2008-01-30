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
 * $Id: StateSet.java,v 1.5 2008-01-30 09:33:23 iovka Exp $
 */
package groove.control;

import groove.trans.Rule;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 
 * @author Tom Staijen
 * @version $Revision $
 */
public class StateSet extends HashSet<ControlState> {

	/**
	 * member variable for caching the result of {@link #getRuleSets()}.
	 */
	Set<Rule> rules;
	
//	public boolean isSuccess() {
//		for( ControlState cs : this ) {
//			if( cs.isSuccess() ) {
//				return true;
//			}
//		}
//		return false;
//	}

	/** 
	 * Returns all states that can be reached with a certain rule and any succeeding 
	 * lambda transitions (by calling asStateSet on all rule targets.
	 *  
	 */
	
//	public StateSet target(Rule rule) {
//		StateSet newSet = new StateSet();
//		
//		for( ControlState cs : this ) {
//			StateSet targets = cs.targets(rule); 
//			if( targets != null ) {
//				for( ControlState t : targets ) {
//					newSet.addAll(t.asStateSet());
//				}
//			}
//		}
//		return newSet;
//	}
	
//	public StateSet lambdaTargets() {
//		StateSet result = new StateSet();
//		for( ControlState cs : this ) {
//			result.addAll(cs.lambdaTargets());
//		}
//		return result;
//	}
	
//	public StateSet elseTargets() {
//		StateSet result = new StateSet();
//		for( ControlState cs : this ) {
//			result.addAll(cs.elseTargets());
//		}
//		return result;
//	}
	
	
	/**
	 * Returns a prioritized Set of Sets of Rules.
	 * This is done by merging the prioritized rulesets
	 * of all ControlStates and then by appending the prioritized
	 * rulesets of the Else-Targets 
	 * 
	 * @return Set<Rule>
	 */
	public Set<Rule> rules() {
		// for now, priorities will be ignores when using control ?
		
		if( this.rules == null ) {
			
			this.rules = new HashSet<Rule>();

			for( ControlState cs : this ) {
				this.rules.addAll(cs.rules());
			}
		}
		return rules;
		
	}
	
	/**
	 * Default Constructor
	 */
	public StateSet() {
		super();
	}
	
}
