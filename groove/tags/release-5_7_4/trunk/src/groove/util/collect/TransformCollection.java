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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

/**
 * Set that is built on an inner set but transforms the images using the
 * abstract method {@link #toOuter(Object)}. Note that this set is inefficient
 * in everything except for iteration.
 * @see groove.util.collect.TransformIterator
 * @see groove.util.collect.TransformMap
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class TransformCollection<T,U> extends AbstractCollection<U> {
    /** Constructs a set transforming a given inner set. */
    public TransformCollection(Collection<T> inner) {
        this.inner = inner;
    }

    /**
     * Creates a {@link TransformIterator} over the iterator obtained from the
     * inner set.
     */
    @Override
    public Iterator<U> iterator() {
        return new TransformIterator<T,U>(this.inner.iterator()) {
            /**
             * Delegates the method to the enclosing
             * {@link TransformSet#toOuter(Object)}.
             */
            @Override
            protected U toOuter(T key) {
                return TransformCollection.this.toOuter(key);
            }

        };
    }

    /**
     * Converts the parameter using {@link #toInner(Object)} and delegates the
     * method to the inner set.
     */
    @Override
    public boolean add(U obj) {
        return this.inner.add(toInner(obj));
    }

    /**
     * Converts the parameter using {@link #toInner(Object)} and delegates the
     * mathod to the inner set.
     */
    @Override
    public boolean contains(Object o) {
        return this.inner.contains(toInner(o));
    }

    /**
     * Converts the parameter using {@link #toInner(Object)} and delegates the
     * mathod to the inner set.
     */
    @Override
    public boolean remove(Object o) {
        return this.inner.remove(toInner(o));
    }

    /** Delegates the mathod to the inner set. */
    @Override
    public int size() {
        return this.inner.size();
    }

    /** Delegates the mathod to the inner set. */
    @Override
    public void clear() {
        this.inner.clear();
    }

    /**
     * Callback method to transform the value in the inner set to a value
     * visible from outside.
     * @param key the value from the inner set
     * @return the corresponding visible value
     */
    abstract protected U toOuter(T key);

    /**
     * Callback method to transform a value visible from outside to a value in
     * the inner set representation. This method should be left and right
     * inverse to {@link #toOuter(Object)}. Optional method used to implement
     * the collection modification methods ({@link #add(Object)},
     * {@link #remove(Object)} etc.). This implementation throws an
     * {@link UnsupportedOperationException}.
     * @param key the value as visible from outside
     * @return the corresponding inner value
     */
    protected T toInner(Object key) {
        throw new UnsupportedOperationException();
    }

    private final Collection<T> inner;
}
