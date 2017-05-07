/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.util.collect;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implements a very simple immutable linked list of elements,
 * based on chained nodes.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Chain<E> implements Iterable<E> {
    /** Creates a chain consisting of a single value. */
    public Chain(E value) {
        this(value, null);
    }

    /** Constructs a chain consisting of a first value and a remaining chain. */
    public Chain(E value, Chain<E> next) {
        this.value = value;
        this.next = next;
        this.size = next == null ? 1 : 1 + next.size();
    }

    /** Returns the value in the head of the chain. */
    public E getValue() {
        return this.value;
    }

    /** Returns the (possibly {@code null}) tail of the chain. */
    public Chain<E> getNext() {
        return this.next;
    }

    /** 
     * Creates a prefixed version of this chain,
     * consisting of a given value followed by this chain.
     */
    public Chain<E> prefix(E value) {
        return new Chain<>(value, this);
    }

    /** Returns an iterator over the elements of the chain. */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            @Override
            public boolean hasNext() {
                return this.current != null;
            }

            @Override
            public E next() {
                if (this.current == null) {
                    throw new NoSuchElementException();
                }
                E result = this.current.getValue();
                this.current = this.current.getNext();
                return result;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            private Chain<E> current;
        };
    }

    /** Returns the number of elements in the chain. */
    public int size() {
        return this.size;
    }

    /** 
     * Returns a list consisting of the elements of this chain.
     * The list is in ascending or descending order.
     * @param inOrder if {@code true}, the list is filled in
     * order of the chain; otherwise, it is in inverse order
     */
    public List<E> toList(boolean inOrder) {
        @SuppressWarnings("unchecked")
        E[] result = (E[]) new Object[this.size];
        int inc = inOrder ? +1 : -1;
        int ix = inOrder ? 0 : this.size - 1;
        Chain<E> chain = this;
        do {
            result[ix] = chain.getValue();
            ix += inc;
            chain = chain.getNext();
        } while (chain != null);
        return Arrays.asList(result);
    }

    private final E value;
    private final Chain<E> next;
    private final int size;

    /** Creates a new singleton chain, consisting of a given value. */
    public static final <E> Chain<E> singleton(E value) {
        return new Chain<>(value);
    }
}
