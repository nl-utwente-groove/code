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
package groove.gui.chscenar;

import groove.explore.result.EdgeBoundCondition;
import groove.explore.result.ExploreCondition;
import groove.explore.result.IsRuleApplicableCondition;
import groove.explore.result.NodeBoundCondition;

/** Encloses the different choices for an explore condition.
 * 
 * @author Iovka Boneva
 * @version $Revision $
 */
@Deprecated
@SuppressWarnings("all")
public enum ExploreConditionChoice implements Choice {
	EDGE_BOUND,
	NODE_BOUND,
	RULE_APPL;

	public String description() {
		switch (this) {
		case EDGE_BOUND : return "Is satisfied by states whose number of edges with given label are within some bound."; 
		case NODE_BOUND : return "Is satisfied by states whose number of nodes is within some bound.";
		case RULE_APPL :  return "Is satisfied by states in which a given rule is applicable.";
		default : throw new UnsupportedOperationException("Missing implementation for " + this);
		}
	}

	public Object id() {
		return this;
	}

	public Class<?> implementingClass() {
		switch (this) {
		case EDGE_BOUND : return EdgeBoundCondition.class;
		case NODE_BOUND : return NodeBoundCondition.class;
		case RULE_APPL :  return IsRuleApplicableCondition.class;
		default : throw new UnsupportedOperationException("Missing implementation for " + this);
		}
	}

	public Class<?> optionsClass() {
		switch (this) {
		case EDGE_BOUND : 
		case NODE_BOUND : 
		case RULE_APPL :  
		default : return ExploreConditionOptions.class;
		}
	}

	public String shortName() {
		switch (this) {
		case EDGE_BOUND : return "Edge bound"; 
		case NODE_BOUND : return "Node bound";
		case RULE_APPL :  return "Rule application";
		default : throw new UnsupportedOperationException("Missing implementation for " + this);
		}
	}

	public ExploreCondition<?> getInstance(Object options) throws IllegalArgumentException {
        
        if (options == null || (! optionsClass().isAssignableFrom(options.getClass()))) {
            throw new IllegalArgumentException("The options for a " + this.getClass() + "should be of type " + optionsClass());
        }
        ExploreCondition result;
        ExploreConditionOptions opt = (ExploreConditionOptions) options;

        try {
            result = (ExploreCondition) implementingClass().newInstance();
            result.setCondition(opt.options());
            result.setNegated(opt.isNegated());
        } catch (InstantiationException ex) {
        	throw new UnsupportedOperationException("Error in instanciation class for " + this);
        } catch (IllegalAccessException ex) {
        	throw new UnsupportedOperationException("Error in instanciation class for " + this);
        }
		
        return result;
	}
	
}
