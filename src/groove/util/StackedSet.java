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
 * $Id: StackedSet.java,v 1.5 2008-01-30 09:32:15 iovka Exp $
 */
package groove.util;

import java.util.Set;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Implements a set on top of another set.
 * The underlying set is not affected by any operations upon this one.
 * The implementation is based on a lower set, and sets of added and removed
 * elements with respect to this lower set.
 * @author Arend Rensink
 * @version $Revision$
 */
public class StackedSet<T> extends AbstractSet<T> {
	/**
	 * Constructs a stacked set from predefined lower, added and removed sets.
	 * It is required that <code>lower.containsAll(removed)</code> and <code>! lower.removeAll(added)</code>
	 * @param lower the lower set of the stacked set
	 * @param added the set of elements added to <code>lower</code>
	 * @param removed the set of elements removed from <code>lower</code>
	 */
	public StackedSet(Set<? extends T> lower, Set<T> added, Set<T> removed) {
		this.lower = lower;
		this.added = added;
		this.removed = removed;
        assert lower != null : "Lower set of stacked set should not be null";
        assert added != null : "Added set of stacked set should not be null";
        assert removed != null : "Removed set of stacked set should not be null";
//        assert lower.containsAll(removed) : String.format("Lower set %s does not contain all removed elements %s", lower, removed);
//        assert !added.removeAll(lower) : String.format("Lower set %s not disjoint with added elements %s", lower, added);
	}
	
    /**
     * Constructs a stacked set whose lower set is empty.
     */
    public StackedSet() {
        this(Collections.<T>emptySet());
    }

    /**
     * Constructs a stacked set, on top of a given lower set.
     * @param lower the lower set on which this one should be stacked.
     * Should not be <code>null</code>.
     */
    public StackedSet(Set<? extends T> lower) {
        this.lower = lower;
        this.added = createAddedSet();
        this.removed = createRemovedSet();
        assert lower != null : "Lower set of stacked set should not be null";
    }

    /**
     * Computed as the size of this set, minus the removed elements, plus the added elements.
     */
    @Override
    public int size() {
        return lower.size() + added.size() - removed.size();
    }

    /**
     * Creates a sequence iterator of the added elements and the
     * lower set elements, the latter filtered through the removed elements.
     */
    @Override
    public Iterator<T> iterator() {
        // the following is eqivalent to a nested iterator consisting of an
        // iterator over the added set followed by a filtered iterator over 
        // the lower set. Presumably it is more efficient this way?
        return new Iterator<T>() {
            public void remove() {
                if (lowerLatest == null) {
                    addedIter.remove();
                } else {
                    removed.add(lowerLatest);
                }
            }

            public boolean hasNext() {
                // as long as we're still in the added set, proceed
                if (lowerLatest == null && addedIter.hasNext()) {
                    return true;
                } else {
                	Iterator<? extends T> lowerIter = this.lowerIter;
                	T next = lowerNext;
                    // look for the first acceptable element
                    while (next == null && lowerIter.hasNext()) {
                        next = lowerIter.next();
                        // only acceptable if not in the removed set
                        if (removed.contains(next)) {
                            next = null;
                        }
                    }
                    lowerNext = next;
                    return next != null;
                }
            }

            public T next() {
                if (hasNext()) {
                    if (lowerNext != null) {
                        lowerLatest = lowerNext;
                        lowerNext = null;
                        return lowerLatest;
                    } else {
                        return addedIter.next();
                    }
                } else {
                    throw new NoSuchElementException();
                }
            }

            /**
             * Copy from the enclosing class
             */
            private final Set<T> removed = StackedSet.this.removed;
            /**
             * The current inner iterator; i.e., the latest element returned by
             * <tt>nextIterator()</tt>
             */
            private final Iterator<T> addedIter = added.iterator();

            /**
             * Iterator over the lower set.
             */
            private Iterator<? extends T> lowerIter = StackedSet.this.lower.iterator();

            /**
             * Next element to be retrieved from the lower set. Guaranteed not to be in the removed
             * set.
             */
            private T lowerNext = null;
            /**
             * Latest element from the lowerIter actually returned by next
             */
            private T lowerLatest;
        };
    }
    
    /**
     * Either removes the element from the removed set, or, if it is not
     * already in the lower set, adds it to the added set.
     */
    @Override
    public boolean add(T o) {
        if (removed.remove(o)) {
            return true;
        } else {
            return !lower.contains(o) && added.add(o);
        }
    }

    /**
     * This implementation clears the added set, and copies the lower set
     * into the removed set.
     */
    @Override
    public void clear() {
        added.clear();
        removed.addAll(lower);
    }

    /**
     * Returns <tt>true</tt> if the element is in the added set,
     * or in the lower but not the removed set.
     */
    @Override
    public boolean contains(Object o) {
        return added.contains(o) || (lower.contains(o) && !removed.contains(o));
    }

    /**
     * Either removes the element from the added set, or, if it is 
     * present in the lower set, adds it to the removed set.
     */
    @Override
    public boolean remove(Object o) {
        if (added.remove(o)) {
            return true;
        } else {
            return lower.contains(o) && removed.add((T) o);
        }
    }

    /**
     * Returns an unmodifiable view upon the added elements of this stacked set.
     */
    public Set<T> added() {
        return Collections.unmodifiableSet(added);
    }

    /**
     * Returns an unmodifiable view upon the added elements of this stacked set.
     */
    public Set<T> removed() {
        return Collections.unmodifiableSet(removed);
    }

    /**
     * Returns an unmodifiable view upon the lower set.
     */
    protected Set<T> lower() {
        return Collections.unmodifiableSet(lower);
    }

    /**
     * Constructor method for the set of added elements.
     * This implementation returns a {@link HashSet}.
     */
    protected Set<T> createAddedSet() {
        return new HashSet<T>();
    }

    /**
     * Constructor method for the set of added elements.
     * This implementation returns a {@link HashSet}.
     */
    protected Set<T> createRemovedSet() {
        return new HashSet<T>();
    }

    /**
     * The lower set, on top of which this one is stacked.
     */
    final Set<? extends T> lower;
    /**
     * The set of elements added w.r.t. the lower set.
     */
    final Set<T> added;
    /**
     * The set of elements removed w.r.t. the lower set.
     */
    final Set<T> removed;
}