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
 * $Id: AbstrGraphTransition.java,v 1.1 2007-11-28 15:35:50 iovka Exp $
 */
package groove.abs.lts;

import groove.lts.GraphTransition;

/**
 * @author Iovka Boneva
 * @version $Revision $
 */
public interface AbstrGraphTransition extends GraphTransition {
	
	public AbstrGraphState source();
	
	public AbstrGraphState target();
	
	/** Two transitions are equivalent if they have same source state and same event.
	 * This implementation tests for reference equivalence.
	 * @param other
	 * @return True if other is equivalent to this transition.
	 */
	public boolean isEquivalent (AbstrGraphTransition other);
	 
}
