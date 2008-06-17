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
package groove.explore.result;


import groove.explore.Scenario;

import java.util.Collection;
import java.util.TreeSet;

/** A set of objects that are the result of the execution of
 * some {@link Scenario}. Whether the result is fully computed 
 * is defined by the {@link #done()} method.
 * Typical such objects are elements of a graph transition system
 * (states, paths, etc.).
 * @author 
 *
 * @param <T> The type of the objects stored in the result.
 */
public abstract class Result<T> {
	
	/** The elements stored in this result. */
	protected Collection<T> elements;
	
	/** Creates a result with an empty set of elements. */
	public Result() {
		elements = new TreeSet<T>();
	}

	/** Adds an element to the result.
	 * @param t
	 */
	public void add(T t) {
		elements.add(t);
	}	
	
	/** The set of elements contained in the result.
	 * @return
	 */
	public Collection<T> getResult() {
		return elements;
	}
	
	/** Returns a result of the same type with no added elements. */ 
	public abstract Result<T> getFreshResult ();
	

	/** Indicates whether the result is computed.
	 * @return <code>true</code> if the result is computed, <code>false</code> otherwise.
	 * When the result is completed, no more elements should be added to it.
	 */
	public abstract boolean done();

}
