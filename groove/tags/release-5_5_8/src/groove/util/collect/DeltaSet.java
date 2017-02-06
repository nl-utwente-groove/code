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

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Implements a set whose operations work on an underlying (lower) set, but
 * store the elements added to and removed from that lower set.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:13 $
 */
public class DeltaSet<T> extends AbstractSet<T> {
    /**
     * Constructs a delta set on top of an empty {@link HashSet}.
     */
    public DeltaSet() {
        this(new HashSet<T>());
    }

    /**
     * Constructs a delta set, on top of a given lower set.
     * @param lower the lower set on which this one should be built. Should not
     *        be <code>null</code>.
     */
    public DeltaSet(Set<T> lower) {
        assert lower != null : "Lower set of deltaset should not be null";
        this.lower = lower;
        this.added = createAddedSet();
        this.removed = createRemovedSet();
    }

    /**
     * Constructs a delta set, on top of a given lower set, and using given sets
     * for the added and removed elements.
     * @param lower the lower set on which this one should be built. Should not
     *        be <code>null</code>.
     */
    public DeltaSet(Set<T> lower, Set<T> added, Set<T> removed) {
        assert lower != null : "Lower set of deltaset should not be null";
        this.lower = lower;
        this.added = added;
        this.removed = removed;
    }

    /**
     * Computed as the size of this set, minus the removed elements, plus the
     * added elements.
     */
    @Override
    public int size() {
        return this.lower.size();
    }

    /**
     * Creates an iterator of the lower set.
     */
    @Override
    public Iterator<T> iterator() {
        // the following is eqivalent to a nested iterator consisting of an
        // iterator over the added set followed by a filtered iterator over
        // the lower set. Presumably it is more efficient this way?
        return new Iterator<T>() {
            @Override
            public void remove() {
                this.iter.remove();
                if (!this.added.remove(this.latestNext)) {
                    this.removed.add(this.latestNext);
                }
            }

            @Override
            public boolean hasNext() {
                return this.iter.hasNext();
            }

            @Override
            public T next() {
                if (hasNext()) {
                    this.latestNext = this.iter.next();
                    return this.latestNext;
                } else {
                    throw new NoSuchElementException();
                }
            }

            /**
             * Copy from the enclosing class
             */
            private final Set<T> removed = removed();
            /**
             * Copy from the enclosing class
             */
            private final Set<T> added = added();
            /**
             * Iterator over the inner set.
             */
            private final Iterator<T> iter = lower().iterator();
            /**
             * Latest element returned by <code>next()</code>. This is the
             * element removed by {@link #remove()}.
             */
            private T latestNext;
        };
    }

    /**
     * Either removes the element from the removed set, or, if it is not already
     * in the lower set, adds it to the added set.
     */
    @Override
    public boolean add(T o) {
        boolean result = this.lower.add(o);
        if (result && !this.removed.remove(o)) {
            boolean inner = this.added.add(o);
            assert inner : "Added element " + o + " already in added set" + this.added;
        }
        return result;
    }

    /**
     * This implementation clears the added set, and copies the lower set into
     * the removed set.
     */
    @Override
    public void clear() {
        this.removed.addAll(this.lower);
        this.removed.removeAll(this.added);
        this.added.clear();
        this.lower.clear();
    }

    /**
     * Returns <tt>true</tt> if the element is in the added set, or in the
     * lower but not the removed set.
     */
    @Override
    public boolean contains(Object o) {
        return this.lower.contains(o);
    }

    /**
     * Either removes the element from the added set, or, if it is present in
     * the lower set, adds it to the removed set.
     */
    @Override
    public boolean remove(Object o) {
        boolean result = this.lower.remove(o);
        if (result && !this.added.remove(o)) {
            @SuppressWarnings("unchecked")
            boolean inner = this.removed.add((T) o);
            assert inner : "Removed element " + o + " already in removed set" + this.removed;
        }
        return result;
    }

    /**
     * Returns an alias of the added elements of this deltaset.
     */
    public Set<T> added() {
        return this.added;
    }

    /**
     * Returns an alias of the removed elements of this deltaset.
     */
    public Set<T> removed() {
        return this.removed;
    }

    /**
     * Returns an alias of the lower set.
     */
    public Set<T> lower() {
        return this.lower;
    }

    /**
     * Constructor method for the set of added elements. This implementation
     * returns a {@link HashSet}.
     */
    protected Set<T> createAddedSet() {
        return new HashSet<>();
    }

    /**
     * Constructor method for the set of added elements. This implementation
     * returns a {@link HashSet}.
     */
    protected Set<T> createRemovedSet() {
        return new HashSet<>();
    }

    /**
     * The lower set, on top of which this one is stacked.
     */
    private final Set<T> lower;
    /**
     * The set of elements added w.r.t. the lower set.
     */
    private final Set<T> added;
    /**
     * The set of elements removed w.r.t. the lower set.
     */
    private final Set<T> removed;
}
