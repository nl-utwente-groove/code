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
 * $Id: Counter.java,v 1.2 2007-08-22 15:05:05 rensink Exp $
 */
package groove.util;

/**
 * An object of this class keeps an internal counter. 
 * @author J.H. Kuperus
 * @version 0.1 $Revision: 1.2 $ $Date: 2007-08-22 15:05:05 $
 */
public class Counter {
	private int count;
	
	public Counter() {
		this(0);
	}
	
	public Counter(int start) {
		count = start;
	}
	
	public int getValue() {
		return count;
	}
	
	public int increment() {
		return ++count;
	}
	
	public int decrement() {
		return --count;
	}
}