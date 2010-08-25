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
 * $Id: ArrayIterator.java,v 1.2 2008-01-30 09:32:14 iovka Exp $
 */
package groove.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator over a given fragment of an array.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:14 $
 */
public class ArrayIterator<T> implements Iterator<T> {

    /**
     * Creates an iterator over a given array, between a given start index
     * (inclusive) and end index (exclusive).
     */
    public ArrayIterator(Object[] array, int from, int to) {
        this.array = array;
        this.to = to;
        this.current = from;
    }

    /**
     * Creates an iterator over a given array, from a given start index to the
     * end of the array.
     */
    public ArrayIterator(Object[] array, int from) {
        this(array, from, array.length);
    }

    public boolean hasNext() {
        return this.current < this.to;
    }

    public T next() {
        if (hasNext()) {
            @SuppressWarnings("unchecked")
            T result = (T) this.array[this.current];
            this.current++;
            return result;
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * Throws an {@link UnsupportedOperationException} always.
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * The array over which to iterate.
     */
    private final Object[] array;
    /**
     * The index to which to iterator (exclusive).
     */
    private final int to;
    /**
     * The current index in {@link #array}.
     */
    private int current;
}
