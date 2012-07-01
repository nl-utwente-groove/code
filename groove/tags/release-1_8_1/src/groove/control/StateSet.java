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
 * $Id: StateSet.java,v 1.2 2007-08-26 07:23:33 rensink Exp $
 */
package groove.control;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class StateSet {
	
	private Set<ControlState> states;
	
	
	public StateSet()
	{
		this.states = new HashSet<ControlState>();
	}
	
	public void add(ControlState state)
	{
		this.states.add(state);
	}
	
	public Iterator iterator() 
	{
		return states.iterator();
	}
}