// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

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
package nl.utwente.groove.util.collect;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * An iterator obtained by <i>flattening</i> other iterators. The inner
 * iterators are accessed one by one, whenever the previous is exhausted.
 * @author Arend Rensink
 * @version $Revision$
 */
public class NestedIterator<T> extends AbstractNestedIterator<T> {
    /**
     * Constructs a nested iterator from a given list of iterators.
     * @param iterList the list of iterators from which this nested iterator is
     *        to be constructed
     */
    public NestedIterator(Iterable<? extends Iterator<? extends T>> iterList) {
        this(iterList.iterator());
    }

    /**
     * Constructs a nested iterator from existing iterators.
     */
    @SafeVarargs
    public NestedIterator(Iterator<? extends T>... iters) {
        this(Arrays.asList(iters));
    }

    /**
     * Constructs a nested iterator from a given outer iterator. The outer
     * iterator should yield the inner iterators.
     * @param outerIterator the outer iterator from which this nested iterator
     *        is to be constructed
     * @require <tt>outerIterator.next() instanceof Iterator</tt>
     */
    public NestedIterator(Iterator<? extends Iterator<? extends T>> outerIterator) {
        this.outerIterator = outerIterator;
    }

    /** This implementation returns the next element of the outer iterator. */
    @Override
    protected Iterator<? extends T> nextIterator() {
        return this.outerIterator.next();
    }

    /** This implementation queries the outer iterator. */
    @Override
    protected boolean hasNextIterator() {
        return this.outerIterator.hasNext();
    }

    /**
     * @invariant <tt>outerIterator.hasNext()</tt> implies
     *            <tt>outerIterator.next() instanceof Iterator</tt>
     */
    private final Iterator<? extends Iterator<? extends T>> outerIterator;

    /** Creates a new iterator from a sequence of iterators. */
    @SafeVarargs
    static public <T> Iterator<T> newInstance(Iterator<? extends T>... iterators) {
        return new NestedIterator<>(iterators);
    }

    /** Creates a new iterator from a sequence of iterables. */
    @SafeVarargs
    static public <T> Iterator<T> newInstance(Iterable<? extends T>... iterables) {
        return new NestedIterator<>(Arrays.stream(iterables).map(Iterable::iterator).toList());
    }

    /** Creates a new iterator from a stream of iterables. */
    static public <T> Iterator<T> newInstance(Stream<Iterable<? extends T>> iterables) {
        return new NestedIterator<>(iterables.map(Iterable::iterator).toList());
    }
}