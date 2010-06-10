/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.abstraction;

/**
 * EDUARDO
 * @author Eduardo Zambon
 * @version $Revision $
 */
public final class Multiplicity {

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    private static final int OMEGA_VALUE = -1;

    /** The unbounded multiplicity. Differs from all other multiplicities. */
    public static final Multiplicity OMEGA = new Multiplicity(OMEGA_VALUE);

    private static Multiplicity STORE[];

    // ------------------------------------------------------------------------
    // Static Methods
    // ------------------------------------------------------------------------

    /** EDUARDO */
    public static void initMultStore() {
        int multUpperBound =
            Math.max(Parameters.getNodeMultBound(),
                Parameters.getEdgeMultBound());
        STORE = new Multiplicity[multUpperBound + 1];
        for (int i = 0; i < multUpperBound; i++) {
            STORE[i] = new Multiplicity(i);
        }
        STORE[multUpperBound] = OMEGA;
    }

    /** EDUARDO */
    public static Multiplicity getMultOf(int value) {
        assert STORE != null : "The multiplicity store must be initialized first.";
        assert value >= 0 && value < STORE.length - 1 : "Invalid multiplicity value";
        return STORE[value];
    }

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** Stores the multiplicity value. It is a natural number or omega. */
    private int value;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    private Multiplicity() {
        // does nothing by design.
    }

    /**
     * Creates a multiplicity.
     * @param value a natural number or omega.
     */
    private Multiplicity(int value) {
        assert (value >= 0 || value == OMEGA_VALUE) : "Multiplicities values must be natural numbers or omega.";
        this.value = value;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        boolean result;
        result =
            (o instanceof Multiplicity)
                && (((Multiplicity) o).value == this.value);
        return result;
    }

    @Override
    public String toString() {
        String result;
        if (this.value == OMEGA_VALUE) {
            result = "w";
        } else {
            result = this.value + "";
        }
        return result;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Compare this multiplicity with the given parameter for order.
     * @param mult the multiplicity to be compared with.
     * @return a negative integer, zero, or a positive integer if this
     *         multiplicity is less than, equal to, or greater than
     *         <code>mult</code>. This also considers omega.
     */
    public int compare(Multiplicity mult) {
        int result;
        if (this.value == mult.value) {
            result = 0;
        } else if (this.value == OMEGA_VALUE || this.value > mult.value) {
            result = 1;
        } else {
            result = -1;
        }
        return result;
    }

}
