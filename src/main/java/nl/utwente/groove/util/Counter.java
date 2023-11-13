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
package nl.utwente.groove.util;

import java.util.Objects;

/**
 * Mutable counter.
 * @author Arend Rensink
 * @version $Revision$
 */
public class Counter {
    /** Increases the value of this counter, and returns the result. */
    public int increase() {
        this.value++;
        return this.value;
    }

    /** Decreases the value of this counter, and returns the result. */
    public int decrease() {
        this.value--;
        return this.value;
    }

    /** Returns the current value of this counter. */
    public int value() {
        return this.value;
    }

    private int value;

    /** Checks if the counter value is zero. */
    public boolean isZero() {
        return value() == 0;
    }

    @Override
    public String toString() {
        return "Counter [value=" + this.value + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Counter other)) {
            return false;
        }
        return this.value == other.value;
    }
}
