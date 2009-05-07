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

import groove.explore.result.Result;
import groove.lts.State;

/** Implements the different choices for a result.
 * 
 * @author Iovka Boneva
 * @version $Revision $
 */
public enum ResultChoice implements Choice {
	FIND_ONE,
	FIND_ALL;
	
	public String description() {
		switch (this) {
		case FIND_ONE : return "Find one state satisfying the condition";
		case FIND_ALL : return "Find all states satisfying the condition";
		default : throw new UnsupportedOperationException("Missing implementation for " + this);
		}
	}

	public Object getInstance(Object options) {
		// In case other types of results are added, this line is needed to verify that
		// the options parameter corresponds to the expected options
		if (optionsClass() != null && options == null) {
			throw new UnsupportedOperationException("Error in instanciation class for " + this);
		}
		if (optionsClass() != null && (! optionsClass().isAssignableFrom(options.getClass()))) {
			throw new IllegalArgumentException("The options for a " + this.getClass() + "should be of type " + optionsClass());
	    }
		
		switch (this) {
		case FIND_ONE : return new Result(1); 
		case FIND_ALL : return new Result(Integer.MAX_VALUE);
		default : throw new UnsupportedOperationException("Missing implementation for " + this);
		}
	}

	public Object id() {
		return this;
	}

	public Class<?> implementingClass() {
		switch (this) {
		case FIND_ONE : return groove.explore.result.Result.class;
		case FIND_ALL : return groove.explore.result.Result.class;
		default : throw new UnsupportedOperationException("Missing implementation for " + this);
		}
	}

	public Class<?> optionsClass() {
		switch (this) {
		case FIND_ONE : return null; 
		case FIND_ALL : return null;
		default : throw new UnsupportedOperationException("Missing implementation for " + this);
		}
	}

	public String shortName() {
		switch (this) {
		case FIND_ONE : return "Find one";
		case FIND_ALL : return "Find all";
		default : throw new UnsupportedOperationException("Missing implementation for " + this);
		}
	}

}
