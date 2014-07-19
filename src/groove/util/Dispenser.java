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
/* $Id$ */
package groove.util;

import java.util.NoSuchElementException;

/**
 * Interface for a number dispenser.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class Dispenser {
    /** 
     * Returns a number, according to the policy of this dispenser.
     * This will result in a {@link NoSuchElementException} if
     * {@link #hasNext()} is {@code false}.
     * @throws NoSuchElementException if the dispenser has no next number
     */
    public final int getNext() throws NoSuchElementException {
        if (hasNext()) {
            int result = this.last = computeNext();
            return result;
        } else {
            throw new NoSuchElementException();
        }
    }

    /** 
     * Returns the value of the last successful call to {@link #getNext()},
     * or {@code -1} if {@link #getNext()} has never been called.
     */
    public final int getLast() throws NoSuchElementException {
        return this.last;
    }

    /**
     * Callback method from {@link #getNext()} to compute the next value of
     * this dispenser.
     * This is only called immediately after {@link #hasNext()} has
     * returned {@code true}.
     */
    protected abstract int computeNext();

    /** 
     * Indicates if this dispenser has a next number to return.
     * @return if {@code false}, any subsequent call to {@link #getNext()}
     * will fail
     */
    public final boolean hasNext() {
        return !this.exhausted;
    }

    /**
     * Notifies the dispenser that a given number has been used.
     * The default implementation calls {@link #setExhausted()}.
     * and should not be returned by a future {@link #getNext()}.
     */
    public void notifyUsed(int nr) {
        setExhausted();
    }

    /**
     * Sets the dispenser to exhausted.
     * This means that the next call to {@link #hasNext()} will return {@code false}.
     */
    protected final void setExhausted() {
        this.exhausted = true;
    }

    /** Flag indicating that the dispenser cannot generated new values. */
    private boolean exhausted;
    /** Value returned at the last call of {@link #getNext()}. */
    private int last = -1;

    /** Convenience method to create a {@link SingleDispenser} at a given number. */
    public static Dispenser single(int nr) {
        return new SingleDispenser(nr);
    }

    /** Convenience method to create a {@link DefaultDispenser}. */
    public static Dispenser counter() {
        return new DefaultDispenser();
    }
}
