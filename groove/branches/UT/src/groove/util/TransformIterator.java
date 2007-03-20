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
 * $Id: TransformIterator.java,v 1.1.1.1 2007-03-20 10:05:18 kastenberg Exp $
 */
package groove.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator constructed by transforming the results from another ("inner") iterator.
 * Inner results can also be filtered out.
 * The abstract <tt>transform(Object)</tt> method describes the transformation from the 
 * inner iterator's returned objects to this one's results.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
abstract public class TransformIterator<T,U> implements Iterator<U> {
    /**
     * Constructs a transforming iterator from a given iterator.
     * @param inner The inner iterator for this filter iterator
     */
    public TransformIterator(Iterator<? extends T> inner) {
        this.inner = inner;
    }

    /**
     * Constructs a transforming iterator from the iterator of a given collection.
     * @param innerSet the inner iterator will be initialized from here
     */
    public TransformIterator(Collection<? extends T> innerSet) {
        this(innerSet.iterator());
    }

    /**
     * Forwards the request to the inner iterator.
     */
    public void remove() {
        inner.remove();
    }

    /**
     * Forwards the query to the inner iterator.
     */
    public boolean hasNext() {
        while (next == null && inner.hasNext()) {
            try {
                next = toOuter(inner.next());
            } catch (IllegalArgumentException exc) {
                // proceed
            }
        }
        return next != null;
    }

    /**
     * Retrieves the <tt>next()</tt> object from the inner iterator,
     * applies <tt>transform(Object)</tt> to it, and returns the result.
     */
    public U next() {
        if (hasNext()) {
            U result = next;
            next = null;
            return result;
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * The transformation method between the inner iterator's results and
     * this iterator's results.
     * If the method throws an <tt>IllegalArgumentException</tt> then
     * the inner iterator's result is filtered out; i.e., the next inner result is taken.
     * @param from the object to be transformed
     * (retrieved from the inner iterator's <tt>next()</tt>)
     * @return the transformed object (to be returned by this iterator's <tt>next</tt>)
     * @throws IllegalArgumentException if <tt>from</tt> is to be filtered out
     */
    abstract protected U toOuter(T from);

    /**
     * The inner iterator; set in the constructor.
     * @invariant <tt>inner != null</tt>
     */
    private final Iterator<? extends T> inner;
    /**
     * The precomputed (transformed) next element to be returned by <tt>next()</tt>.
     */
    private U next;
}
