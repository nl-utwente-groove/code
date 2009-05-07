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

import groove.explore.result.ExploreCondition;
import groove.explore.strategy.ConditionalStrategy;
import groove.explore.strategy.Strategy;

public enum StrategyChoice implements Choice {
	//BREADTH_FIRST,
	DEPTH_FIRST,
	ARBITRARY,
	LINEAR,
	COND_BREADTH_FIRST,
	//COND_DEPTH_FIRST,
	;

	public String description() {
		switch (this) {
		//case BREADTH_FIRST : return "Breadth-first exploration";
		case DEPTH_FIRST : return "Depth-first exploration";
		case ARBITRARY : return "Open states are explored in arbitrary order";
		case LINEAR : return "Explore a single path until a final state or a loop is found";
		case COND_BREADTH_FIRST : return "Breadth-first exploration only of states satisfying some condition";
		//case COND_DEPTH_FIRST : return "Depth-first exploration only of states satisfying some condition";
		default : throw new UnsupportedOperationException("Missing implementation for " + this);	
		}
	}

	public Class<?> implementingClass () {
		switch (this) {
		//case BREADTH_FIRST : return groove.explore.strategy.BreadthFirstStrategy.class;
		case DEPTH_FIRST : return groove.explore.strategy.ExploreRuleDFStrategy.class;
		case ARBITRARY : return groove.explore.strategy.BranchingStrategy.class;
		case LINEAR : return groove.explore.strategy.LinearStrategy.class;
		case COND_BREADTH_FIRST : return groove.explore.strategy.ConditionalBFSStrategy.class;
		//case COND_DEPTH_FIRST : return groove.explore.strategy.ConditionalDepthFirstStrategy.class;
		default : throw new UnsupportedOperationException("Missing implementation for " + this);	
		}
	}

	public Object id() {
		return this;
	}

	public Object getInstance(Object options) {
		if (optionsClass() != null && options == null) {
			throw new IllegalArgumentException("The options for a " + this.getClass() + "should not be null");
		}
		if (optionsClass() != null && (! optionsClass().isAssignableFrom(options.getClass()))) {
			throw new IllegalArgumentException("The options for a " + this.getClass() + "should be of type " + optionsClass());
	    }
		
		Strategy result;
		try {
			result = (Strategy) implementingClass().newInstance();
		} catch (InstantiationException e) {
			throw new UnsupportedOperationException("Error in instanciation class for " + this);
		} catch (IllegalAccessException e) {
			throw new UnsupportedOperationException("Error in instanciation class for " + this);
		}
		
		
		// to make sure that the implementation is complete
		assert this.optionsClass() == null || this == COND_BREADTH_FIRST : "Implementation contains contradictions.";
		if (this == COND_BREADTH_FIRST) {
			((ConditionalStrategy) result).setExploreCondition((ExploreCondition<?>) options);
		}
		return result;
	}

	public Class<?> optionsClass() {
		switch (this) {
		//case BREADTH_FIRST : return null;
		case DEPTH_FIRST : return null;
		case ARBITRARY : return null;
		case LINEAR : return null;
		case COND_BREADTH_FIRST : return ExploreCondition.class;
		default : throw new UnsupportedOperationException("Missing implementation for " + this);	
		}
	}

	public String shortName() {
		switch (this) {
		//case BREADTH_FIRST : return "Breadth-first";
		case DEPTH_FIRST : return "Depth-first";
		case ARBITRARY : return "Arbitrary order";
		case LINEAR : return "Linear";
		case COND_BREADTH_FIRST : return "Conditional breadth-first";
		default : throw new UnsupportedOperationException("Missing implementation for " + this);	
		}
	}

	
	
	
}
