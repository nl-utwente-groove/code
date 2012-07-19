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
 * $Id: ControlState.java,v 1.10 2008-01-30 12:37:39 fladder Exp $
 */
package groove.control;

import groove.control.parse.Counter;
import groove.graph.Element;
import groove.graph.Node;
import groove.trans.Rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
	/** Contains the targets of outgoing lambda-transitions of this state **/
	/** hold the 'success' property of the state. **/ 
	private HashMap<Rule, HashSet<ControlState>> ruleTargetMap = new HashMap<Rule, HashSet<ControlState>>();
	private Set<ElseControlTransition> elseTransitions = new HashSet<ElseControlTransition>();
	/**
	
	public int compareTo(Element obj) {
	/**
		if( transition instanceof LambdaControlTransition ) {
			this.lambdaTargets.add(transition.target());
			return;
		}
		else if( transition instanceof ElseControlTransition ) {
			this.elseTransitions.add((ElseControlTransition)transition);
			return;
		}
		else if( transition instanceof RuleControlTransition ) {
			// TODO: store the transitions somehow..  not sure how's best.

			Rule rule = ((RuleControlTransition) transition).getRule();
			
			//store targets by rule
			HashSet<ControlState> targetSet = ruleTargetMap.get(rule);
			if( targetSet == null ) {
				ruleTargetMap.put(rule, targetSet = new HashSet<ControlState>());
			}
			targetSet.add(transition.target());
		}
		else {
			// should never be reached 
		}
	}
	@Override
	/**
	/**
	/**
	/**
}