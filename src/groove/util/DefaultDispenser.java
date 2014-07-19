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

/**
 * Dispenser that works on the basis of a counter.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultDispenser extends Dispenser {
    /*
     * Sets the counter to the maximum of the current count and a given number.
     */
    @Override
    public void notifyUsed(int nr) {
        this.count = Math.max(this.count, nr + 1);
    }

    @Override
    protected int computeNext() {
        int result = this.count;
        this.count++;
        return result;
    }

    /** The value of the counter. */
    private int count;
}
