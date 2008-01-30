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
 * $Id: RuleControlTransition.java,v 1.5 2008-01-30 09:33:24 iovka Exp $
 */
package groove.control;

import groove.trans.Rule;

/**
 * A ControlTransition associated with a Rule in a RuleSystem. 
 * @author Staijen
 *
 */
public class RuleControlTransition extends ControlTransition {

	private Rule rule;
	
	
	/**
	 * Contstructor for a rulecontroltransition. Allows to set the associated rule later, but requires a label already.
	 * @param source
	 * @param target
	 * @param label
	 */
	public RuleControlTransition(ControlState source, ControlState target, String label) {
		super(source, target, label);
	}

	/**
	 * setter method for the associated rule
	 * @param rule
	 */
	public void setRule(Rule rule) {
		this.rule = rule;
	}
	
	/** 
	 * getter method for the associated rule
	 * @return (Rule) associated rule or (null) null.
	 */
	public Rule getRule() {
		return this.rule;
	}

	/**
	 * returns the priority of the associated rule
	 * @return (int) the priority of the associated rule
	 */
	public int getPriority() {
		return this.rule.getPriority();
	}
}

