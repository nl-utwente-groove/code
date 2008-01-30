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
 * $Id: ElseControlTransition.java,v 1.7 2008-01-30 11:13:57 fladder Exp $
 */
package groove.control;

import groove.trans.Rule;

import java.util.Set;

/**
 * A ControlTransition with lower priority then then any other transition.
 * 
 * @author Tom Staijen
 */
public class ElseControlTransition extends ControlTransition {

	/**
	 * The public contructor, which calls the super contructor with the default label for else transitions
	 * @param source
	 * @param target
	 */
	public ElseControlTransition(ControlState source, ControlState target) {
		super(source, target, ControlView.ELSE_LABEL);
	}
	
	public void setFailureSet(Set<Rule> rules) {
		this.rules = rules;
	}
	
	public Set<Rule> getFailureSet() {
		return rules;
	}
	
	private Set<Rule> rules;
		@Override
	public String toString() {
		if( rules != null )
			return "!" + rules.toString();
		else
			return "else"; 
	}
}
