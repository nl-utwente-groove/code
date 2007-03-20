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
 * $Id: SetView.java,v 1.1.1.2 2007-03-20 10:42:59 kastenberg Exp $
 */
package groove.util;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Provides a shared view upon an underlying set, filtering those values
 * that satisfy a certain condition, to be provided through the abstract
 * method <tt>approve(Object)</tt>.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public abstract class SetView<T> extends AbstractSet<T> {
    /**
     * Constucts a view upon a set, newly created for this purpose.
     * Since the set itself is not available, this is only useful for
     * creating a set whose elements are guaranteed to satisfy a 
     * certain condition (to be provided by the abstract method
     * <tt>approves(Object)</tt>). 
     * This constructor is provided primarily to satisfy the requirements 
     * on <tt>Set</tt> implementations.
     * @see #approves(Object)
     */
    public SetView() {
        set = new HashSet<T>();
    }
    
    /**
     * Constucts a shared set view on a given underlying set.
     */
    public SetView(Set<? super T> set) {
        this.set = set;
    }

    /**
     * We can only calculate the size by running the iterator and counting
     * the number of returned values. This is therefore linear in the size of
     * the inner set!
     */
    public int size() {
        int result = 0;
        for (Object elem: set) {
            if (approves(elem))
                result++;
        }
        return result;
    }

    /** Tests if the element is approved and contained in the underlying set. */
    public boolean contains(Object elem) {
        return approves(elem) && set.contains(elem);
    }

    /**
     * The iterator allows removal of elements returned by the previous <tt>next()</tt>,
     * but only if <tt>hasNext()</tt> has not been invoked in the meanwhile. 
     */
    public Iterator<T> iterator() {
        return new FilterIterator<T>(set.iterator()) {
            /** Delegates the approval to the surrounding {@link SetView}. */
            protected boolean approves(Object obj) {
                return SetView.this.approves(obj);
            }
        };
    }

    /**
     * Adds the element only if it is approved by <tt>{@link #approves(Object)}</tt>.
     */
    public boolean add(T elem) {
        return approves(elem) && set.add(elem);
    }

    /**
     * Removes an element only if it satisfies the criteria of this set,
     * according to <tt>approves(Object)</tt>
     */
    public boolean remove(Object elem) {
        if (approves(elem)) {
            return set.remove(elem);
        } else {
            return false;
        }
    }

    /**
     * The condition imposed on set members.
     * This has to guarantee type correctness, i.e., 
     * <code>approves(obj)</code> should imply <code>obj instanceof T</code>
     */
    public abstract boolean approves(Object obj);

    /**
     * The underlying set.
     */
    protected final Set<? super T> set;
}
