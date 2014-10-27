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
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Provides a shared view upon an underlying set, filtering those values that
 * satisfy a certain condition, to be provided through the abstract method
 * <tt>approve(Object)</tt>. The view allows removal but not addition of
 * values.
 * @author Arend Rensink
 * @version $Revision$
 */
public class SetView<T> extends AbstractSet<T> {
    /**
     * Constructs a shared set view on a given underlying set,
     * with a given filter function for the set elements.
     */
    public SetView(Set<?> set, Predicate<Object> approval) {
        this.set = set;
        this.approval = approval;
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
        return stream().filter(getApproval()).iterator();
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
    final public boolean approves(Object obj) {
        return getApproval().test(obj);
    }

    /** Returns the approval predicate of this set view. */
    protected Predicate<Object> getApproval() {
        return this.approval;
    }

    private final Predicate<Object> approval;

    /**
     * The underlying set.
     */
    protected final Set<?> set;
}
