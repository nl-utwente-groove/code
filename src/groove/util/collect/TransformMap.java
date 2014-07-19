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

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

/**
 * Map that is built on an inner map but transforms the values using the
 * abstract method {@link #toOuter(Object)}.
 * @see groove.util.collect.TransformIterator
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class TransformMap<T,U,V> extends AbstractMap<T,V> {
    /** Constructs a map from a given inner map. */
    public TransformMap(Map<T,U> inner) {
        this.inner = inner;
    }

    /**
     * Creates a transform set on the entry set of the inner map, where the
     * transformation adapts the value accoding to {@link #toOuter(Object)}.
     */
    @Override
    public Set<Entry<T,V>> entrySet() {
        return new TransformSet<Entry<T,U>,Entry<T,V>>(this.inner.entrySet()) {
            @Override
            public Entry<T,V> toOuter(Entry<T,U> obj) {
                final Entry<T,U> innerEntry = obj;
                return new Entry<T,V>() {
                    /**
                     * Delegates the method to the inner entry.
                     */
                    @Override
                    public T getKey() {
                        return innerEntry.getKey();
                    }

                    /**
                     * Transforms the value of the inner entry.
                     */
                    @Override
                    public V getValue() {
                        return TransformMap.this.toOuter(innerEntry.getValue());
                    }

                    /**
                     * Transforms the new value using {@link #toInner(Object)},
                     * and the return value using {@link #toOuter(Entry)}.
                     */
                    @Override
                    public V setValue(V value) {
                        return TransformMap.this.toOuter(innerEntry.setValue(TransformMap.this.toInner(value)));
                    }
                };
            }
        };
    }

    /**
     * Transforms the value obtained from the inner map.
     */
    @Override
    public V get(Object key) {
        return toOuter(this.inner.get(key));
    }

    /**
     * Transforms the return value obtained from the inner map.
     */
    @Override
    public V remove(Object key) {
        return toOuter(this.inner.remove(key));
    }

    /**
     * Transforms the value using {@link #toInner(Object)} and delegates the
     * method to the inner map.
     */
    @Override
    public V put(T key, V value) {
        return toOuter(this.inner.put(key, toInner(value)));
    }

    /** Delegates the mathod to the inner map. */
    @Override
    public void clear() {
        this.inner.clear();
    }

    /** Delegates the mathod to the inner map. */
    @Override
    public boolean containsKey(Object key) {
        return this.inner.containsKey(key);
    }

    /**
     * Transforms the value using {@link #toInner(Object)} and delegated the
     * method to the inner map.
     */
    @Override
    public boolean containsValue(Object value) {
        return this.inner.containsValue(toInner(value));
    }

    /** Delegates the method to the inner map. */
    @Override
    public Set<T> keySet() {
        return this.inner.keySet();
    }

    /** Delegates the method to the inner map. */
    @Override
    public int size() {
        return this.inner.size();
    }

    /**
     * Callback method to transform the value in the inner map to a value
     * visible from outside.
     * @param obj the value from the inner map
     * @return the corresponding visible value
     */
    protected abstract V toOuter(U obj);

    /**
     * Callback method to transform a value visible from outside to a value in
     * the inner set representation. This method should be left and right
     * inverse to {@link #toOuter(Object)}. Optional method used to implement
     * the modification method {@link #put(Object,Object)}. This implementation
     * throws an {@link UnsupportedOperationException}.
     * @param value the value as visible from outside
     * @return the corresponding inner value
     */
    protected U toInner(Object value) {
        throw new UnsupportedOperationException();
    }

    /** The inner map, set at construction time. */
    private final Map<T,U> inner;
}
