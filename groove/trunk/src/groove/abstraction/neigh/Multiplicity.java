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
package groove.abstraction.neigh;

import groove.graph.Edge;
import groove.graph.Node;

import java.util.Set;

/**
 * A multiplicity is an interval on \Nat^\omega, closed on both lower and upper
 * bounds. It is used as an approximate way for counting.
 * 
 * @author Eduardo Zambon
 */
public final class Multiplicity {

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    /** The \omega value, differs from all natural numbers. */
    public static final int OMEGA = Integer.MAX_VALUE;
    /** The node multiplicity store. */
    private static Multiplicity NODE_MULT_STORE[];
    /** The edge multiplicity store. */
    private static Multiplicity EDGE_MULT_STORE[];

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** The lower bound of the multiplicity. */
    private final int i;
    /** The upper bound of the multiplicity. */
    private final int j;
    /** The kind of the multiplicity. */
    private final MultKind kind;
    /**
     * The index of the multiplicity object in the store.
     * Serves as a perfect hash.
     */
    private final int index;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Private constructor to avoid object creation.
     * Use {@link #getMultiplicity(int, int, MultKind)} to retrieve an
     * multiplicity from the store.
     */
    private Multiplicity(int i, int j, MultKind kind, int index) {
        assert index >= 0;
        assert isInN(i) && isInNOmega(j);
        assert i <= j;
        this.i = i;
        this.j = j;
        this.kind = kind;
        this.index = index;
    }

    // ------------------------------------------------------------------------
    // Static Methods
    // ------------------------------------------------------------------------

    /** Returns the proper bound value for the given multiplicity kind. */
    private static int getBound(MultKind kind) {
        int bound = 0;
        switch (kind) {
        case NODE_MULT:
            bound = Parameters.getNodeMultBound();
            break;
        case EDGE_MULT:
            bound = Parameters.getEdgeMultBound();
            break;
        default:
            assert false;
        }
        return bound;
    }

    /** Returns the proper store for the given multiplicity kind. */
    private static Multiplicity[] getStore(MultKind kind) {
        Multiplicity store[] = null;
        switch (kind) {
        case NODE_MULT:
            store = NODE_MULT_STORE;
            break;
        case EDGE_MULT:
            store = EDGE_MULT_STORE;
            break;
        default:
            assert false;
        }
        return store;
    }

    /** Sets the proper store for the given multiplicity kind. */
    private static void setStore(Multiplicity store[], MultKind kind) {
        switch (kind) {
        case NODE_MULT:
            NODE_MULT_STORE = store;
            break;
        case EDGE_MULT:
            EDGE_MULT_STORE = store;
            break;
        default:
            assert false;
        }
    }

    /** Returns true if the given number is in \Nat^\omega. */
    private static boolean isInNOmega(int i) {
        return i >= 0;
    }

    /** Returns true if the given number is in \Nat. */
    private static boolean isInN(int i) {
        return isInNOmega(i) && i != OMEGA;
    }

    /**
     * Returns the cardinality of the set of bounded multiplicities, i.e.,
     * the size of the store, for the given bound b.
     */
    private static int getCardinality(int b) {
        return (b * b + 5 * b + 6) / 2;
    }

    /**
     * Initialises the multiplicity stores so that multiplicity objects can be
     * reused. This method must be called as a preparation for using any other
     * abstraction classes in this package.
     */
    public static void initMultStore() {
        for (MultKind kind : MultKind.values()) {
            // Get the maximum bound and create the store array.
            int b = getBound(kind);
            Multiplicity store[] = getStore(kind);
            store = new Multiplicity[getCardinality(b)];

            // Create all the multiplicity objects.
            int index = 0;
            for (int i = 0; i <= b + 1; i++) {
                for (int j = i; j <= b; j++) {
                    store[index] = new Multiplicity(i, j, kind, index);
                    index++;
                }
                // Special case for j = \omega.
                store[index] = new Multiplicity(i, OMEGA, kind, index);
                index++;
            }

            // Make sure the store is completely filled.
            assert index == getCardinality(b);
            setStore(store, kind);
        }
    }

    /** Returns all possible values for edge multiplicities. */
    public static Multiplicity[] getAllEdgeMultiplicities() {
        // Clone the store to avoid modifications.
        return EDGE_MULT_STORE.clone();
    }

    /**
     * Retrieves the multiplicity object from the store with the given
     * lower and upper bounds.
     */
    public static Multiplicity getMultiplicity(int i, int j, MultKind kind) {
        Multiplicity store[] = getStore(kind);
        Multiplicity result = null;
        for (int index = 0; index < store.length; index++) {
            if (store[index].i == i && store[index].j == j) {
                result = store[index];
                break;
            }
        }
        assert result != null;
        return result;
    }

    /**
     * Returns the addition of the two given values.
     * Both i and j must be in \Nat^\omega.
     */
    public static int add(int i, int j) {
        assert isInNOmega(i) && isInNOmega(j);
        int result;
        if (i != OMEGA && j != OMEGA) { // i, j \in N.
            result = i + j;
        } else { // otherwise
            result = OMEGA;
        }
        return result;
    }

    /**
     * Returns the subtraction of the two given values.
     * Both i and j must be in \Nat^\omega but \omega - \omega is undefined.
     */
    public static int sub(int i, int j) {
        assert isInNOmega(i) && isInNOmega(j);
        int result;
        if (i != OMEGA) { // i \in N.
            if (j < i) {
                result = i - j;
            } else { // j >= i.
                result = 0;
            }
        } else { // i == \omega.
            assert isInN(j) : "Subtraction undefined.";
            result = OMEGA;
        }
        return result;
    }

    /**
     * Approximates the interval formed by the given values to a bounded
     * multiplicity. This is the \beta operation.
     * Both i and j must be in \Nat^\omega.
     */
    public static Multiplicity approx(int i, int j, MultKind kind) {
        assert isInNOmega(i) && isInNOmega(j);
        assert i <= j;
        int b = getBound(kind);
        if (i <= b) {
            if (j <= b) {
                // Do nothing. i and j are already set.
            } else { // i <= b && j > b .
                // i is set.
                j = OMEGA;
            }
        } else { // i > b.
            i = b + 1;
            j = OMEGA;
        }
        return getMultiplicity(i, j, kind);
    }

    /** Scale the given multiplicity by the given factor. */
    public static Multiplicity scale(Multiplicity mult, int factor) {
        assert factor >= 0;
        return approx(mult.i * factor, mult.j * factor, mult.kind);
    }

    /**
     * Returns the multiplicity of the set of nodes given, bounded by the node
     * multiplicity bound (\nu) set in the Parameters class. 
     */
    public static Multiplicity getNodeSetMult(Set<? extends Node> nodes) {
        int setSize = nodes.size();
        return approx(setSize, setSize, MultKind.NODE_MULT);
    }

    /**
     * Returns the multiplicity of the set of edges given, bounded by the edge
     * multiplicity bound (\mu) set in the Parameters class. 
     */
    public static Multiplicity getEdgeSetMult(Set<? extends Edge> edges) {
        int setSize = edges.size();
        return approx(setSize, setSize, MultKind.EDGE_MULT);
    }

    /**
     * Converts the given edge multiplicity into an equivalent node
     * multiplicity. 
     */
    public static Multiplicity convertToNodeMult(Multiplicity mult) {
        assert mult.kind == MultKind.EDGE_MULT;
        return getMultiplicity(mult.i, mult.j, MultKind.NODE_MULT);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /** Two multiplicities are equal if they have the kind and store index. */
    @Override
    public boolean equals(Object o) {
        boolean result;
        if (!(o instanceof Multiplicity)) {
            result = false;
        } else {
            Multiplicity other = (Multiplicity) o;
            result = (this.kind == other.kind && this.index == other.index);
        }
        // Check for consistency between equals and hashCode.
        assert (!result || this.hashCode() == o.hashCode());
        return result;
    }

    @Override
    public final int hashCode() {
        int prime = 0;
        switch (this.kind) {
        case NODE_MULT:
            prime = 31;
            break;
        case EDGE_MULT:
            prime = 57;
            break;
        }
        return prime * this.index;
    }

    @Override
    public String toString() {
        String result;
        if (this.i == this.j) {
            result = this.i + "";
        } else if (this.j == OMEGA) {
            result = this.i + "+";
        } else {
            result = "<" + this.i + "," + this.j + ">";
        }
        return result;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Basic getter method. */
    public int getLowerBound() {
        return this.i;
    }

    /** Basic getter method. */
    public int getUpperBound() {
        return this.j;
    }

    /** Returns true if the multiplicity equals zero; false, otherwise. */
    public boolean isZero() {
        return this.i == 0 && this.j == 0;
    }

    /** Returns true if the multiplicity equals one; false, otherwise. */
    public boolean isOne() {
        return this.i == 1 && this.j == 1;
    }

    /** Returns true if the upper bound is more than one; false, otherwise. */
    public boolean isCollector() {
        return this.j > 1;
    }

    /** Returns the bounded addition of the two given multiplicities. */
    public Multiplicity add(Multiplicity other) {
        assert this.kind == other.kind;
        return approx(add(this.i, other.i), add(this.j, other.j), this.kind);
    }

    /** Returns the subtraction of the two given multiplicities. */
    public Multiplicity sub(Multiplicity other) {
        assert this.kind == other.kind;
        return getMultiplicity(sub(this.i, other.j), sub(this.j, other.i),
            this.kind);
    }

    /** Returns the addition of this with one. */
    public Multiplicity increment() {
        return approx(add(this.i, 1), add(this.j, 1), this.kind);
    }

    /** Returns true if this multiplicity is less or equal than the other. */
    public boolean le(Multiplicity other) {
        assert this.kind == other.kind;
        //return this.i <= other.i && this.j <= other.j;
        return (this.i <= other.i && this.j <= other.j)
            || (this.j != OMEGA && other.j == OMEGA);
    }

    /** Returns true if this multiplicity subsumes the other. */
    public boolean subsumes(Multiplicity other) {
        assert this.kind == other.kind;
        return other.i >= this.i && other.j <= this.j;
    }

    // ------------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------------

    /** Enumeration of multiplicity kinds. */
    public enum MultKind {
        /** Node multiplicity kind. */
        NODE_MULT,
        /** Edge multiplicity kind. */
        EDGE_MULT
    }

    /** Enumeration of edge multiplicity directions. */
    public enum EdgeMultDir {
        /** Outgoing edge multiplicity. */
        OUTGOING,
        /** Incoming edge multiplicity. */
        INCOMING
    }

}
