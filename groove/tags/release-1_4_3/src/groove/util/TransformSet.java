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
 * $Id: TransformSet.java,v 1.2 2007-03-28 15:12:28 rensink Exp $
 */
package groove.util;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Set that is built on an inner set but transforms the images using the
 * abstract methd {@link #toOuter(Object)}.
 * Note that this set is inefficient in everything except for iteration.
 * @see groove.util.TransformIterator
 * @see groove.util.TransformMap
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
abstract public class TransformSet<T,U> extends AbstractSet<U> implements Set<U> {
    /** Constructs a set transforming a given inner set. */
    public TransformSet(Set<T> inner) {
        this.inner = inner;
    }

    /**
     * Creates a {@link TransformIterator} over the iterator obtained 
     * from the inner set.
     */
    @Override
    public Iterator<U> iterator() {
        return new TransformIterator<T,U>(inner.iterator()) {
            /**
             * Delegates the method to the enclosing
             * {@link TransformSet#toOuter(Object)}.
             */
            @Override
            protected U toOuter(T key) {
                return TransformSet.this.toOuter(key);
            }
            
        };
    }
    
    /** 
     * Converts the parameter using {@link #toInner(Object)}
     * and delegates the mathod to the inner set.
     */
    @Override
    public boolean add(Object obj) {
        return inner.add(toInner(obj));
    }

    /** 
     * Converts the parameter using {@link #toInner(Object)}
     * and delegates the mathod to the inner set.
     */
    @Override
    public boolean contains(Object o) {
        return inner.contains(toInner(o));
    }

    /** 
     * Converts the parameter using {@link #toInner(Object)}
     * and delegates the mathod to the inner set.
     */
    @Override
    public boolean remove(Object o) {
        return inner.remove(toInner(o));
    }

    /** Delegates the mathod to the inner set. */
    @Override
    public int size() {
        return inner.size();
    }

    /** Delegates the mathod to the inner set. */
    @Override
    public void clear() {
        inner.clear();
    }

    /** 
     * Callback method to transform the value in the inner set to a
     * value visible from outside.
     * @param key the value from the inner set
     * @return the corresponding visible value
     */
    abstract protected U toOuter(T key);

    /** 
     * Callback method to transform a value visible from outside to a
     * value in the inner set representation.
     * This method should be left and right inverse to {@link #toOuter(Object)}.
     * Optional method used to implement the collection modification
     * methods ({@link #add(Object)}, {@link #remove(Object)} etc.).
     * This implementation throws an {@link UnsupportedOperationException}.
     * @param key the value as visible from outside
     * @return the corresponding inner value
     */
    protected T toInner(Object key) {
        throw new UnsupportedOperationException();
    }
    
    private final Set<T> inner;
}
