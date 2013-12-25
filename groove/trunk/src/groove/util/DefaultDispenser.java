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
/* $Id: DefaultDispenser.java,v 1.2 2008-01-30 09:32:03 iovka Exp $ */
package groove.util;

/**
 * Dispenser that works on the basis of a resettable counter.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultDispenser implements Dispenser {
    /**
     * Sets the counter to a given number.
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Sets the counter to the maximum of the current count and a given number.
     */
    public void maxCount(int count) {
        setCount(Math.max(getCount(), count));
    }

    /**
     * Resets the counter to zero.
     */
    public void reset() {
        setCount(0);
    }

    public int getNext() {
        int result = this.count;
        this.count++;
        return result;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    /**
     * Returns the current value of the counter, without increasing it.
     * Thus, the return value is the same as that of {@link #getNext()}.
     */
    public int getCount() {
        return this.count;
    }

    /** The value of the counter. */
    private int count;
}
