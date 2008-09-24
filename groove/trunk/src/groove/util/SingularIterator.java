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
 * $Id: SingularIterator.java,v 1.2 2008-01-30 09:32:15 iovka Exp $
 */
package groove.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator over a single <i>inner</i> object.
 * @author Arend Rensink
 * @version $Revision$
 */
public class SingularIterator<T> implements Iterator<T> {
    /**
     * Constructs an iterator over a given inner object.
     * @param obj the inner object
     */
    public SingularIterator(T obj) {
        this.obj = obj;
    }

    /**
     * Indicates if the inner object has been delivered.
     */
    public boolean hasNext() {
        return !thisDelivered;
    }

    /**
     * Yields the inner object if that has not yet been delivered (as indicated
     * also by {@link #hasNext()}.
     * @throws NoSuchElementException if the inner object has been delivered before
     */
    public T next() {
        if (thisDelivered) {
            throw new NoSuchElementException();
        } else {
            thisDelivered = true;
            return obj;
        }
    }

    /** 
     * This iterator supports removal, but nothing happens since there
     * is no underlying set.
     */
    public void remove() {
    	if (!thisDelivered) {
            throw new IllegalStateException();
    	}
    }

    /** Flag indicating that the one element has been returned by {@link #next()}. */
    private boolean thisDelivered;

    private final T obj;
}