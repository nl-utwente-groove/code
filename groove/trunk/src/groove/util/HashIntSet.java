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
/*
 * $Id: HashIntSet.java,v 1.1.1.2 2007-03-20 10:42:59 kastenberg Exp $
 */
package groove.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link groove.util.IntSet} on the basis of
 * an underlying {@link java.util.HashSet}, holding an {@link Integer}
 * representation of the <code>int</code> keys.
 *
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:59 $
 */
final public class HashIntSet implements IntSet {
	/**
	 * This implementation clears the underlying {@link HashSet}.
	 */
	public void clear(int capacity) {
		store.clear();
	}

	/**
	 * This implementation returns the size of the underlying {@link HashSet}.
	 */
	public int size() {
		return store.size();
	}

	/**
	 * Adds an {@link Integer} on the basis of <code>key</code> to the underlying set.
	 */
	public boolean add(int key) {
		return store.add(new Integer(key));
	}
	
	/**
	 * The underlying set.
	 */
	private Set<Integer> store = new HashSet<Integer>();
}
