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
 * $Id: MyHashSetExtended.java,v 1.1 2007-11-28 15:35:08 iovka Exp $
 */
package groove.abs;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/** Defines a hash set with additional functionality allowing to retrieve 
 * the set of elements that are equivalent to a given object.
 * Does not support the null element.
 * @author Iovka Boneva
 * @version $Revision $
 */
public class MyHashSetExtended<T> extends MyHashSet<T> {

	/** Initializes the set. The hasher must be compatible with the equalizer.
	 * @param hasher the hasher used for computing hash code. If null, then the default hashCode() method is used. 
	 */
	public MyHashSetExtended(Hasher<T> hasher) {
		super(hasher);
	}

	/** An iterator over all elements equivalent to some element, according to a given equivalent relation.
	 * @param o The element to which objects in the result are equivalent.
	 * @param eq Equivalence relation
	 * @require <code>eq</code> should be compatible with the <code>hasher</code> given at construct time in the following sense:
	 * for all o1,o2, hasher.areEqual(o1,o2) implies eq.areEqual(o1,o2) and eq.areEqual(o1,o2) implies hasher.getHashCode(o1) == hasher.getHashCode(o2)
	 */
	public Iterator<T> getAllEquivalent (final T o, final Equalizer<T> eq) {
		final Iterator<T> candidates = getElementsWithCode(getHasher().getHashCode(o)).iterator();
		return new Iterator<T> () {
			private T next;
			{ goToNext(); } // initialization
			
			public boolean hasNext() { return this.next != null; }

			public T next() {
				if (this.next == null) { throw new NoSuchElementException(); }
				T result = this.next;
				goToNext();
				return result;
			}
			public void remove() { 	throw new UnsupportedOperationException(); }
			
			private void goToNext() {
				this.next = null;
				while (candidates.hasNext()) {
					T t = candidates.next();
					if (eq.areEqual(o,t)) {	
						this.next = t;
						break;
					}
				}
			}
		};
	}

}
