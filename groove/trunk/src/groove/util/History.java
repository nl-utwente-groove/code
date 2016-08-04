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
package groove.util;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A history log that allows browsing back and forth, and adding a new element
 * at the current position. Resembles <tt>ListIterator</tt> in the
 * back-and-forth browsing.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:15 $
 * @see java.util.ListIterator
 */
public class History<T> {
    /**
     * Resets the log to empty.
     * @ensure <tt>isEmpty()</tt>
     */
    public void clear() {
        this.log.clear();
        this.index = -1;
    }

    /**
     * Indicates whether there are elements in the log.
     * @return <tt>true</tt> if there are no elements in the log
     * @ensure <tt>result</tt> implies <tt>! (hasNext() || hasPrevious())</tt>
     */
    public boolean isEmpty() {
        return this.log.size() == 0;
    }

    /**
     * Indicates whether there exists a next element in the log. This next
     * element can then be retrieved using <tt>next()</tt>.
     * @return <tt>true</tt> if there exists a next element
     * @see #next()
     */
    public boolean hasNext() {
        if (DEBUG) {
            Groove.message("Invoking History.hasNext at index " + this.index);
        }
        return this.index < this.log.size() - 1;
    }

    /**
     * Indicates whether there exists a previous element in the log. This
     * previous element can then be retrieved using <tt>previous()</tt>.
     * @return <tt>true</tt> if there exists a previous element
     * @see #previous()
     */
    public boolean hasPrevious() {
        if (DEBUG) {
            Groove.message("Invoking History.hasPrevious at index "
                + this.index);
        }
        return this.index > 0;
    }

    /**
     * Returns the next element in the history log. After invocation, the
     * elements just returned will be the current one.
     * @return the next element in the history log, if any
     * @require <tt>hasNext()</tt>
     * @ensure <tt>result == current()</tt>
     * @exception NoSuchElementException if there is no next element
     * @see #hasNext()
     */
    public T next() {
        if (DEBUG) {
            Groove.message("History.next() at index " + this.index);
        }
        if (hasNext()) {
            this.index++;
            return this.log.get(this.index);
        } else {
            throw new NoSuchElementException("No next element in history");
        }
    }

    /**
     * Returns the current element in the history log. Does not modify the log.
     * @return the current element in the history log
     * @require <tt>! isEmpty()</tt>
     * @ensure <tt>result == current()</tt>
     * @exception NoSuchElementException if there is no next element
     * @see #isEmpty()
     */
    public T current() {
        if (DEBUG) {
            Groove.message("History.current() at index " + this.index);
        }
        if (!isEmpty()) {
            return this.log.get(this.index);
        } else {
            throw new NoSuchElementException("No current element in history");
        }
    }

    /**
     * Returns the previous element in the history log. After invocation, the
     * elements just returned will be the current one.
     * @return the previous element in the history log, if any
     * @require <tt>hasPrevious()</tt>
     * @ensure <tt>result == current()</tt>
     * @exception NoSuchElementException if there is no previous element
     * @see #hasPrevious()
     */
    public T previous() {
        if (DEBUG) {
            Groove.message("History.previous() at index " + this.index);
        }
        if (hasPrevious()) {
            this.index--;
            return this.log.get(this.index);
        } else {
            throw new NoSuchElementException("No previous element in history");
        }
    }

    /**
     * Replaces the current element in the history. The new element will be the
     * current and last one, i.e., all existing next elements at the time of
     * invocation will be discarded.
     * @param element the element to be added to the history
     * @require <tt>! isEmpty()</tt>
     * @ensure <tt>current() == element</tt>, <tt>! hasNext()</tt>
     * @see #add(Object)
     */
    public void replace(T element) {
        if (DEBUG) {
            Groove.startMessage("History.replace(" + element + ") at index "
                + this.index);
        }
        this.log.set(this.index, element);
        for (int i = this.log.size() - 1; i > this.index; i--) {
            this.log.remove(i);
        }
        if (DEBUG) {
            Groove.endMessage("History.replace(" + element + ") at index "
                + this.index);
        }
    }

    /**
     * Adds an element to the history, if it is different from the previous one.
     * The new element will be the current and last one, i.e., all existing next
     * elements at the time of invocation will be discarded. If the element to
     * be added equals <tt>current()</tt>, nothing happens.
     * @param element the element to be added to the history
     * @ensure <tt>current() == element</tt>, <tt>! hasNext()</tt>
     * @see #replace(Object)
     */
    public void add(T element) {
        if (DEBUG) {
            Groove.startMessage("History.add(" + element + ") at index "
                + this.index);
        }
        if (this.index < 0 || !element.equals(current())) {
            assert this.index < this.log.size();
            this.index++;
            if (this.index < this.log.size()) {
                // new element will replace an existing one
                replace(element);
            } else {
                // new element will be appended
                this.log.add(element);
            }
        }
        if (DEBUG) {
            Groove.endMessage("History.add(" + element + ") at index "
                + this.index);
        }
    }

    /**
     * The log of elements.
     */
    protected final List<T> log = new ArrayList<>();
    /**
     * Index in <tt>log</tt> to the element to be returned by <tt>next()</tt>
     * (if any).
     * @invariant <tt>0 <= index <= log.size()</tt>
     */
    protected int index = -1;

    private static final boolean DEBUG = false;
}
