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
 * $Id: SetOfDisjointSets.java,v 1.1.1.2 2007-03-20 10:42:59 kastenberg Exp $
 */
package groove.util;

import java.util.Collection;
import java.util.Set;

/**
 * Implements a set by flattening a set of sets.
 * The set is unmodifiable.
 * There is a user requirement that the set must be initialized
 * with a set of disjoint sets.
 * However, the 
 * containment test does not notice multiplicities, and hence is
 * set equality. (It is implemented by iterating over the underlying collections,
 * which is expensive!)
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class SetOfDisjointSets<T> extends CollectionOfCollections<T> implements Set<T> {
    /**
     * Constructs a new set of sets.
     * It is required that the underlying sets are disjoint.
     * If this is violated, the set iterator will return the same value 
     * more than once, and the size of the set will be wrong. 
     * @require <tt>collections \subseteq Collection</tt>
     */
    public SetOfDisjointSets(Collection<? extends Collection<T>> collections) {
        super(collections);
    }
    
    /**
     * Iterates over this set
     * and tests whether each element found thus is in <tt>other</tt>
     * (which is required also to be a <tt>set</tt>).
     */
    public boolean equals(Object other) {
        if (! (other instanceof Set)) {
            return false;
        } else {
            Set<?> otherCollection = (Set) other;
            for (T elem: this) {
                if (! otherCollection.contains(elem)) {
                    return false;
                }
            }
            return true;
        }
    }
}
