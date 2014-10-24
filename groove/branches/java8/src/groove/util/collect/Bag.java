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
 * $Id$
 */
package groove.util.collect;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
public interface Bag<T> extends Collection<T> {
    /** Encoding for the multiplicities of the bag elements. */
    public interface Multiplicity {
        /** Returns the value of this multiplicity. */
        int getValue();
    }

    /**
     * Returns the set of elements in this bag, i.e., the set of keys with
     * positive multiplicity.
     * @return the set of elements occurring in this bag
     */
    public abstract Set<T> elementSet();

    /**
     * Returns the multiplicity of a given element in this bag.
     * @ensure <tt>result >= 0</tt>
     */
    public abstract int multiplicity(Object elem);

    /**
     * Returns a mapping from keys to (positive) multiplicities.
     * @ensure <tt>result.keysSet().equals(elementSet())</tt>
     */
    public abstract Map<T,? extends Multiplicity> multiplicityMap();

    /**
     * Removes a copy of an object. The return value signifies if this was the
     * last copy.
     * @param elem the object to be removed
     * @return <tt>true</tt> if and only if the last instance of <tt>elem</tt>
     *         was removed
     * @see #remove(Object)
     */
    public abstract boolean removeWasLast(Object elem);

    /**
     * Removes an element and returns the remaining multiplicity of that element
     * @param elem the element to be removed
     * @return the remaining multiplicity of <tt>elem</tt> atfter removing one
     *         instance; <tt>-1</tt> if
     *         <tt>elem did not occur in the first place</tt>
     */
    public abstract int removeGetCount(Object elem);

    /**
     * Removes the elements from a given collection from this bag. The
     * difference with {@link #removeAll(Collection)} is that this method only
     * removes as many copies as are present in the other collection.
     * @param c the ollection from which the elements are to be removed
     * @return <tt>true</tt> if anything was actually removed
     */
    public abstract boolean minus(Collection<?> c);
}