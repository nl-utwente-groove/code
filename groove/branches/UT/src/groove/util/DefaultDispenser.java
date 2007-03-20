// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/* $Id: DefaultDispenser.java,v 1.1.1.1 2007-03-20 10:05:17 kastenberg Exp $ */
package groove.util;

/**
 * Dispenser that works on the basis of a resettable counter.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultDispenser implements Dispenser {
	/**
	 * Sets the counter to a given number.
	 */
	public void setCount(int count) {
		this.count = count;
	}
	
	/**
	 * Resets the counter to zero.
	 */
	public void reset() {
		setCount(0);
	}

	/**
	 * Returns the current value of the counter,
	 * and increases the counter.
	 */
	public int getNumber() {
		int result = count;
		count++;
		return result;
	}
	
	/**
	 * Returns the current value of the counter, without increasing it.
	 */
	public int getCount() {
		return count;
	}

	/** The value of the counter. */
	private int count;
}
