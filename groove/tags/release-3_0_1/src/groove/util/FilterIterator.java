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
 * $Id: FilterIterator.java,v 1.2 2008-01-30 09:32:11 iovka Exp $
 */
package groove.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator constructed by filtering elements from some existing iterator.
 * The <i>inner</i> iterator is passed in at construction.
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public abstract class FilterIterator<T> implements Iterator<T> {
    /**
     * Constructs a filter iterator filtering a given (inner) iterator.
     * @param inner the iterator to be used as inner iterator
     */
    public FilterIterator(Iterator<?> inner) {
        this.inner = inner;
    }
    
    public boolean hasNext() {
    	Object next = this.next;
    	Iterator<?> inner = this.inner;
        while (next == null && inner.hasNext()) {
            if (!approves(next = inner.next())) {
            	next = null;
                removeAllowed = false;
            }
            if (ITERATE_DEBUG)
                Groove.message(
                    "Searching for hasNext(); now at " + next);
        }
        if (ITERATE_DEBUG)
            Groove.message(
                "Found next? "
                    + (next != null && approves(next) ? "Yes" : "No"));
        this.next = (T) next;
        return next != null;
    }

    public T next() {
        if (hasNext()) {
        	T result = next;
        	next = null;
            removeAllowed = true;
            if (ITERATE_DEBUG)
                Groove.message("Found next(): " + result);
            return result;
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * May throw an exception if {@link #hasNext()} was called after {@link #next()}, since then 
     * the element to be removed may be lost.
     * @throws IllegalStateException if {@link #hasNext()} was called after {@link #next()}
     */
    public void remove() {
        if (!removeAllowed)
            throw new IllegalStateException();
        else {
            inner.remove();
            removeAllowed = false;
        }
    }
    
    /**
     * Returns the last object previously returned by {@link #next()},
     * provided {@link #hasNext()} has not yet been invoked in the meantime.
     * This is also the element that will be removed by an invocation of {@link #remove()}.
     * @throws NoSuchElementException if {@link #next()} has not been invoked, or
     * {@link #hasNext()} has been invoked afterwards.
     */
    public T latest() {
        if (!removeAllowed)
            throw new IllegalStateException();
        else
            return next;
    }
    
    /**
     * Tests a given object to see if it passes the filter.
     * This method should only return <code>true</code> if <code>obj instanceof T</code>.
     * Callback method used to filter the objects from the inner iterator.
     * @param obj the object from the inner iterator
     * @return <tt>true</tt> if <tt>obj</tt> should be passed on to {@link #next()}
     */
    abstract protected boolean approves(Object obj);

    /**
     * The inner iterator, that we are filtering.
     */
    private final Iterator<?> inner;
    /**
     * The element to be returned by {@link #next()}, if not <code>null</code>.
     */
    private T next = null;
    /**
     * Flag indicating that the last invocation was {@link #next()}, so that
     * {@link #remove()} can be delegated to the inner iterator.
     */
    private boolean removeAllowed = false;  
    
    private static final boolean ITERATE_DEBUG = false;
}
