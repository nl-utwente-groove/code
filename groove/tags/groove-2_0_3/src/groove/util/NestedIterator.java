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
 * $Id: NestedIterator.java,v 1.3 2007-10-06 11:27:39 rensink Exp $
 */
package groove.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * An iterator obtained by <i>flattening</i> other iterators. The inner
 * iterators are accessed one by one, whenever the previous is 
 * exhausted.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public class NestedIterator<T> extends AbstractNestedIterator<T> {
    /**
     * Constructs a nested iterator from a given list of iterators.
     * @param iterList the list of iterators from which this nested iterator
     * is to be constructed
     */
    public NestedIterator(Collection<? extends Iterator<? extends T>> iterList) {
        this(iterList.iterator());
    }
    
    /** 
     * Constructs a nested iterator from two existing iterators.
     * The outer iterator should yield the inner iterators.
     * @param iter1 the first inner iterator
     * @param iter2 the second inner iterator
     */
    public NestedIterator(Iterator<? extends T> iter1, Iterator<? extends T> iter2) {
        this(Arrays.asList(iter1, iter2));
    }
    
    /** 
     * Constructs a nested iterator from a given outer iterator.
     * The outer iterator should yield the inner iterators.
     * @param outerIterator the outer iterator from which this nested iterator
     * is to be constructed
     * @require <tt>outerIterator.next() instanceof Iterator</tt>
     */
    public NestedIterator(Iterator<? extends Iterator<? extends T>> outerIterator) {
        this.outerIterator = outerIterator;
    }

    /** This implementation returns the next element of the outer iterator. */
    @Override
    protected Iterator<? extends T> nextIterator() {
        return outerIterator.next();
    }

    /** This implementation queries the outer iterator. */
    @Override
    protected boolean hasNextIterator() {
        return outerIterator.hasNext();
    }

    /**
     * @invariant <tt>outerIterator.hasNext()</tt> implies
     * <tt>outerIterator.next() instanceof Iterator</tt>
     */
    private final Iterator<? extends Iterator<? extends T>> outerIterator;
}