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
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * An iterator constructed by transforming the results from another ("inner")
 * iterator. Inner results can also be filtered out. The abstract
 * {@link #toOuter(Object)} method describes the transformation from the
 * inner iterator's returned objects to this one's results.
 * @author Arend Rensink
 * @version $Revision$
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
     * Constructs a transforming iterator from the iterator of a given
     * collection.
     * @param innerSet the inner iterator will be initialised from here
     */
    public TransformIterator(Collection<? extends T> innerSet) {
        this(innerSet.iterator());
    }

    /**
     * Forwards the request to the inner iterator.
     */
    @Override
    public void remove() {
        this.inner.remove();
    }

    /**
     * Forwards the query to the inner iterator.
     */
    @Override
    public boolean hasNext() {
        while (this.next == null && this.inner.hasNext()) {
            try {
                this.next = toOuter(this.inner.next());
            } catch (IllegalArgumentException exc) {
                // proceed
            }
        }
        return this.next != null;
    }

    /**
     * Retrieves the <tt>next()</tt> object from the inner iterator, applies
     * <tt>transform(Object)</tt> to it, and returns the result.
     */
    @Override
    public @NonNull U next() {
        if (hasNext()) {
            @Nullable U result = this.next;
            assert result != null; // because hasNext is true
            this.next = null;
            return result;
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * The transformation method between the inner iterator's results and this
     * iterator's results. If the method returns <code>null</code> then the
     * inner iterator's result is filtered out; i.e., the next inner result is
     * taken.
     * @param from the object to be transformed (retrieved from the inner
     *        iterator's <tt>next()</tt>)
     * @return the transformed object (to be returned by this iterator's
     *         <tt>next</tt>)
     * @throws IllegalArgumentException if <tt>from</tt> is to be filtered out
     */
    abstract protected U toOuter(T from) throws IllegalArgumentException;

    /**
     * The inner iterator; set in the constructor.
     * @invariant <tt>inner != null</tt>
     */
    private final Iterator<? extends T> inner;
    /**
     * The precomputed (transformed) next element to be returned by
     * <tt>next()</tt>.
     */
    private @Nullable U next;
}
