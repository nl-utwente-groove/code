/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.verify;

import java.util.BitSet;

import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.StateProperty;

/**
 * State property base don a bit set.
 * @author Arend Rensink
 * @version $Revision$
 */
public class BitStateProperty extends StateProperty {
    /**
     * Constructs a named {@link BitStateProperty} based on an underlying {@link BitSet}.
     */
    public BitStateProperty(String name, int capacity) {
        super(name);
        this.bits = new BitSet(capacity);
    }

    private final BitSet bits;

    /** Sets the property to {@code true} for a given state.
     */
    public void set(GraphState state) {
        this.bits.set(state.getNumber());
    }

    @Override
    public boolean test(GraphState t) {
        int nr = t.getNumber();
        return nr < this.bits.size() && this.bits.get(nr);
    }
}
