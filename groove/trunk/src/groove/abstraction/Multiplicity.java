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

import groove.graph.Edge;
import groove.graph.Node;

import java.util.HashSet;
import java.util.Set;

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
    // Static Methods
    // ------------------------------------------------------------------------

    /** EDUARDO */
    public static void initMultStore() {
        int multUpperBound =
            Math.max(Parameters.getNodeMultBound(),
                Parameters.getEdgeMultBound());
        STORE = new Multiplicity[multUpperBound + 2];
        for (int i = 0; i <= multUpperBound; i++) {
            STORE[i] = new Multiplicity(i);
        }
        STORE[multUpperBound + 1] = OMEGA;
    }

    /** EDUARDO */
    public static Multiplicity getMultOf(int value) {
        assert STORE != null : "The multiplicity store must be initialized first.";
        assert value >= 0 && value < STORE.length - 1 : "Invalid multiplicity value: "
            + value;
        return STORE[value];
    }

    /** EDUARDO */
    public static Multiplicity getNodeSetMult(Set<Node> nodes) {
        int setSize = nodes.size();
        int nodesMultBound = Parameters.getNodeMultBound();
        return getMult(setSize, nodesMultBound);
    }

    /** EDUARDO */
    public static Multiplicity getEdgeSetMult(Set<Edge> edges) {
        int setSize = edges.size();
        int edgesMultBound = Parameters.getEdgeMultBound();
        return getMult(setSize, edgesMultBound);
    }

    private static Multiplicity getMult(int setSize, int multBound) {
        Multiplicity result;
        if (setSize <= multBound) {
            result = getMultOf(setSize);
        } else {
            result = OMEGA;
        }
        return result;
    }

    /** EDUARDO */
    public static boolean haveSameMult(Set<Edge> s0, Set<Edge> s1) {
        return getEdgeSetMult(s0).equals(getEdgeSetMult(s1));
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
        } else if (this.value == OMEGA_VALUE
            || (mult.value != OMEGA_VALUE && this.value > mult.value)) {
            result = 1;
        } else {
            result = -1;
        }
        return result;
    }

    /**
     * @return false if the multiplicity equals zero; true, otherwise.
     */
    public boolean isPositive() {
        return this.value == OMEGA_VALUE || this.value > 0;
    }

    /**
     * @return false if the multiplicity is at most one; true, otherwise.
     */
    public boolean isAbstract() {
        return !this.isAtMost(getMultOf(1));
    }

    /**
     * @param mult the multiplicity to compare.
     * @return false if this is greater than mult; true, otherwise.
     */
    public boolean isAtMost(Multiplicity mult) {
        return this.compare(mult) != 1;
    }

    /** EDUARDO */
    public Multiplicity add(Multiplicity mult, int bound) {
        assert bound > 0 : "Invalid multiplicity bound: " + bound;
        Multiplicity result;
        if (this.value == OMEGA_VALUE || mult.value == OMEGA_VALUE) {
            result = OMEGA;
        } else {
            result = getMult(this.value + mult.value, bound);
        }
        return result;
    }

    /** EDUARDO */
    public Multiplicity addNodeMult(Multiplicity mult) {
        return this.add(mult, Parameters.getNodeMultBound());
    }

    /** EDUARDO */
    public Multiplicity addEdgeMult(Multiplicity mult) {
        return this.add(mult, Parameters.getEdgeMultBound());
    }

    /**
     * Subtracts mult from this.
     * @param mult the value to be subtracted; it is required that mult <= this.
     * @param bound the bound used in these multiplicities.
     * @return a set of multiplicities. There are two cases.
     *         - If this != omega, then the result is a singleton set with
     *           this.value - mult.value .
     *         - If this == omega, then the result is a set of multiplicities
     *           in the range (bound + 1 - mult.value, ..., omega) .
     */
    public Set<Multiplicity> sub(Multiplicity mult, int bound) {
        assert mult.isAtMost(this) : "Cannot subtract " + mult + " from "
            + this;
        Set<Multiplicity> result = new HashSet<Multiplicity>();
        if (this.value != OMEGA_VALUE
            || (this.value == OMEGA_VALUE && mult.value == OMEGA_VALUE)) {
            result.add(getMultOf(this.value - mult.value));
        } else {
            int lowerBound = bound + 1 - mult.value;
            for (int i = lowerBound; i <= bound; i++) {
                result.add(getMultOf(i));
            }
            result.add(OMEGA);
        }
        return result;
    }

    /** EDUARDO */
    public Set<Multiplicity> subNodeMult(Multiplicity mult) {
        return this.sub(mult, Parameters.getNodeMultBound());
    }

    /** EDUARDO */
    public Set<Multiplicity> subEdgeMult(Multiplicity mult) {
        return this.sub(mult, Parameters.getEdgeMultBound());
    }
}
