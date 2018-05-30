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
import java.util.Set;

/**
 * Provides a shared view upon an underlying set, filtering those values that
 * satisfy a certain condition, to be provided through the abstract method
 * <tt>approve(Object)</tt>. The view allows removal but not addition of
 * values.
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class SetView<T> extends AbstractSet<T> {
    /**
     * Constructs a view upon a set, newly created for this purpose. Since the
     * set itself is not available, this is only useful for creating a set whose
     * elements are guaranteed to satisfy a certain condition (to be provided by
     * the abstract method <tt>approves(Object)</tt>). This constructor is
     * provided primarily to satisfy the requirements on <tt>Set</tt>
     * implementations.
     * @see #approves(Object)
     */
    public SetView() {
        this.set = new HashSet<T>();
    }

    /**
     * Constructs a shared set view on a given underlying set.
     */
    public SetView(Set<?> set) {
        this.set = set;
    }

    /**
     * We can only calculate the size by running the iterator and counting the
     * number of returned values. This is therefore linear in the size of the
     * inner set!
     */
    @Override
    public int size() {
        int result = 0;
        for (Object elem : this.set) {
            if (approves(elem)) {
                result++;
            }
        }
        return result;
    }

    /** Tests if the element is approved and contained in the underlying set. */
    @Override
    public boolean contains(Object elem) {
        return approves(elem) && this.set.contains(elem);
    }

    /**
     * The iterator allows removal of elements returned by the previous
     * <tt>next()</tt>, but only if <tt>hasNext()</tt> has not been invoked
     * in the meanwhile.
     */
    @Override
    public Iterator<T> iterator() {
        return new FilterIterator<T>(this.set.iterator()) {
            /** Delegates the approval to the surrounding {@link SetView}. */
            @Override
            protected boolean approves(Object obj) {
                return SetView.this.approves(obj);
            }
        };
    }

    /**
     * Addition through the view is not supported; this method throws an
     * exception.
     */
    @Override
    public boolean add(T elem) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes an element only if it satisfies the criteria of this set,
     * according to <tt>approves(Object)</tt>
     */
    @Override
    public boolean remove(Object elem) {
        if (approves(elem)) {
            return this.set.remove(elem);
        } else {
            return false;
        }
    }

    /**
     * The condition imposed on set members. This has to guarantee type
     * correctness, i.e., <code>approves(obj)</code> should imply
     * <code>obj instanceof T</code>
     */
    public abstract boolean approves(Object obj);

    /**
     * The underlying set.
     */
    protected final Set<?> set;
}
