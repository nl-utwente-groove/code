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

import groove.explore.result.Acceptor;
import groove.explore.result.ConditionalAcceptor;
import groove.explore.result.ExploreCondition;
import groove.explore.result.FinalStateAcceptor;
import groove.explore.result.InvariantViolatedAcceptor;

/** The possible choices for an acceptor. */
@Deprecated
@SuppressWarnings("all")
public enum AcceptorChoice implements Choice {
	FINAL_STATE,
	CONDITIONAL,
	//ALL_STATES
	;

	public String description() {
		switch (this) {
		case FINAL_STATE : return "Looks for final states";
		case CONDITIONAL : return "Looks for states satisfying some condition";
		//case ALL_STATES : return "Makes full exploration";
		default : throw new UnsupportedOperationException("Missing implementation for " + this);	
		}
	}

	public Object getInstance(Object options) {
		if (optionsClass() != null && options == null) {
			throw new IllegalArgumentException("The options for a " + this.getClass() + "should not be null");
		}
		if (optionsClass() != null && (! optionsClass().isAssignableFrom(options.getClass()))) {
			throw new IllegalArgumentException("The options for a " + this.getClass() + "should be of type " + optionsClass());
		}
		Acceptor result;
		try {
			result = (Acceptor) implementingClass().newInstance();
		} catch (InstantiationException e) {
			throw new UnsupportedOperationException("Error in instanciation class for " + this);
		} catch (IllegalAccessException e) {
			throw new UnsupportedOperationException("Error in instanciation class for " + this);
		}
		
		assert optionsClass() == null || this == CONDITIONAL : "The implementation is not complete.";
		if (this == CONDITIONAL) {
			((ConditionalAcceptor) result).setCondition((ExploreCondition<?>) options);
		}
		return result;
	}

	public Object id() {
		return this;
	}

	public Class<?> implementingClass() {
		switch (this) {
		case FINAL_STATE : return FinalStateAcceptor.class;
		case CONDITIONAL : return InvariantViolatedAcceptor.class;
		//case ALL_STATES : return EmptyAcceptor.class;
		default : throw new UnsupportedOperationException("Missing implementation for " + this);	
		}
	}

	/** Returns null when the corresponding choice does not have any options. */
	public Class<?> optionsClass() {
		switch (this) {
		case FINAL_STATE : return null;
		case CONDITIONAL : return ExploreCondition.class;
		//case ALL_STATES : return null;
		default : throw new UnsupportedOperationException("Missing implementation for " + this);	
		}
	}

	public String shortName() {
		switch (this) {
		case FINAL_STATE : return "Final state";
		case CONDITIONAL : return "State satisfying a condition";
		//case ALL_STATES : return "Full exploration";
		default : throw new UnsupportedOperationException("Missing implementation for " + this);	
		}
	}

}
