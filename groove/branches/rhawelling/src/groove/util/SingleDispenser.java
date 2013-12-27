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
package groove.util;

/**
 * One-shot dispenser that can only return a single number.
 * @author Arend Rensink
 * @version $Revision $
 */
public class SingleDispenser extends Dispenser {
    /** Creates a new one-shot dispenser, for a given number. */
    public SingleDispenser(int nr) {
        this.nr = nr;
    }

    @Override
    protected int computeNext() {
        setExhausted();
        return this.nr;
    }

    @Override
    public void notifyUsed(int nr) {
        if (nr == this.nr) {
            setExhausted();
        }
    }

    /** Number with which the dispenser was initialised. */
    private final int nr;
}
