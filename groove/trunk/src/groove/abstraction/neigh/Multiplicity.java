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

import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.Edge;
import groove.graph.Node;
import groove.trans.HostEdge;
import groove.trans.HostNode;

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
    /** Multiplicity store per multiplicity kind. */
    private static Multiplicity[][] GLOBAL_MULT_STORE =
        new Multiplicity[MultKind.values().length][];
    /**
     * Array holding the multiplicities per kind in a matrix indexed by
     * lower bound and (non-{@link #OMEGA}) upper bound.
     */
    private static Multiplicity[][][] INDEXED_MULT_STORE =
        new Multiplicity[MultKind.values().length][][];
    /**
     * Array holding the {@link #OMEGA}-multiplicities per kind
     * in a indexed by lower bound.
     */
    private static Multiplicity[][] OMEGA_MULT_STORE =
        new Multiplicity[MultKind.values().length][];

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** Multiplicity lower bound. */
    private final int i;
    /** Multiplicity upper bound. */
    private final int j;
    /** Multiplicity kind. */
    private final MultKind kind;
    /**
     * Index of the multiplicity object in the store.
     * Serves as a perfect hash.
     */
    private final char index;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Private constructor to avoid object creation.
     * Use {@link #getMultiplicity(int, int, MultKind)} to retrieve an
     * multiplicity from the store.
     */
    private Multiplicity(int i, int j, MultKind kind, char index) {
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
    public static int getBound(MultKind kind) {
        int bound = 0;
        switch (kind) {
        case NODE_MULT:
            bound = Parameters.getNodeMultBound();
            break;
        case EDGE_MULT:
            bound = Parameters.getEdgeMultBound();
            break;
        case EQSYS_MULT:
            bound =
                ((Parameters.getNodeMultBound() + 1) * (Parameters.getEdgeMultBound() + 1)) - 1;
            break;
        default:
            assert false;
        }
        return bound;
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
            int cardinality = getCardinality(b);
            Multiplicity[] globalStore = new Multiplicity[cardinality];
            Multiplicity[][] indexedStore = new Multiplicity[b + 2][b + 1];
            Multiplicity[] omegaStore = new Multiplicity[b + 2];

            // Create all the multiplicity objects.
            char index = 0;
            for (int i = 0; i <= b + 1; i++) {
                for (int j = i; j <= b; j++) {
                    Multiplicity mult = new Multiplicity(i, j, kind, index);
                    globalStore[index] = indexedStore[i][j] = mult;
                    index++;
                    assert index != 0 : "Too many multiplicity values";
                }
                // Special case for j = \omega.
                Multiplicity mult = new Multiplicity(i, OMEGA, kind, index);
                globalStore[index] = omegaStore[i] = mult;
                index++;
                assert index != 0 : "Too many multiplicity values";
            }

            // Make sure the store is completely filled.
            assert index == getCardinality(b);
            int kindIx = kind.ordinal();
            GLOBAL_MULT_STORE[kindIx] = globalStore;
            INDEXED_MULT_STORE[kindIx] = indexedStore;
            OMEGA_MULT_STORE[kindIx] = omegaStore;
        }
    }

    /**
     * Retrieves the multiplicity object from the store with the given
     * lower and upper bounds.
     */
    public static Multiplicity getMultiplicity(int i, int j, MultKind kind) {
        Multiplicity result;
        int kindIx = kind.ordinal();
        if (j == OMEGA) {
            result = OMEGA_MULT_STORE[kindIx][i];
        } else {
            result = INDEXED_MULT_STORE[kindIx][i][j];
        }
        assert result != null;
        return result;
    }

    /**
     * Retrieves the multiplicity object from the store with the given
     * index.
     */
    public static Multiplicity getMultiplicity(int index, MultKind kind) {
        return GLOBAL_MULT_STORE[kind.ordinal()][index];
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
     * Returns the multiplication of the two given values.
     * Both i and j must be in \Nat^\omega.
     */
    public static int times(int i, int j) {
        assert isInNOmega(i) && isInNOmega(j);
        int result;
        if (i == 0 || j == 0) {
            result = 0;
        } else if (i != OMEGA && j != OMEGA) { // i, j \in N+.
            result = i * j;
        } else { // otherwise
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

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /** Equality of multiplicities comes down to object equality. */
    @Override
    public boolean equals(Object o) {
        boolean result;
        if (!(o instanceof Multiplicity)) {
            result = false;
        } else {
            Multiplicity other = (Multiplicity) o;
            result =
                (this.kind == other.kind && this.i == other.i && this.j == other.j);
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
            prime = 53;
            break;
        case EQSYS_MULT:
            prime = 79;
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

    /** Returns a string to be used when serializing shapes. */
    public String toSerialString() {
        String j;
        if (this.j == OMEGA) {
            j = "w";
        } else {
            j = this.j + "";
        }
        return this.i + " " + j;
    }

    /** Basic getter method. */
    public int getLowerBound() {
        return this.i;
    }

    /** Basic getter method. */
    public int getUpperBound() {
        return this.j;
    }

    /** Returns true if the multiplicity equals zero. */
    public boolean isZero() {
        return this.i == 0 && this.j == 0;
    }

    /** Returns true if the multiplicity equals one. */
    public boolean isOne() {
        return this.i == 1 && this.j == 1;
    }

    /** Returns true if the upper bound is more than one. */
    public boolean isCollector() {
        return this.j > 1;
    }

    /** Returns true if the lower and upper bound are equal. */
    public boolean isSingleton() {
        return this.i == this.j;
    }

    /** Returns true if the upper bound is omega. */
    public boolean isUnbounded() {
        return this.j == OMEGA;
    }

    /** Returns true if the multiplicity is <0,w>. */
    public boolean isZeroPlus() {
        return this.i == 0 && this.j == OMEGA;
    }

    /** Basic inspection method. */
    public boolean isNodeKind() {
        return this.kind == MultKind.NODE_MULT;
    }

    /** Basic inspection method. */
    public boolean isEdgeKind() {
        return this.kind == MultKind.EDGE_MULT;
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

    /** Returns the bounded multiplication of the two given multiplicities. */
    public Multiplicity times(Multiplicity other) {
        assert this.isNodeKind() && other.isEdgeKind();
        return getMultiplicity(times(this.i, other.i), times(this.j, other.j),
            MultKind.EQSYS_MULT);
    }

    /** Returns true if this multiplicity is less or equal than the other. */
    public boolean le(Multiplicity other) {
        assert this.kind == other.kind;
        return (this.i <= other.i && this.j <= other.j)
            || (this.j != OMEGA && other.j == OMEGA);
    }

    /** Returns true if this multiplicity subsumes the other. */
    public boolean subsumes(Multiplicity other) {
        assert this.kind == other.kind;
        return other.i >= this.i && other.j <= this.j;
    }

    /** Converts the multiplicity to a node kind. */
    public Multiplicity toNodeKind() {
        assert this.isEdgeKind();
        return approx(this.i, this.j, MultKind.NODE_MULT);
    }

    /** Basic getter method. */
    public MultKind getKind() {
        return this.kind;
    }

    // ------------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------------

    /** Returns the index of this multiplicity value. */
    public char getIndex() {
        return this.index;
    }

    /** Enumeration of multiplicity kinds. */
    public enum MultKind {
        /** Node multiplicity kind. */
        NODE_MULT,
        /** Edge multiplicity kind. */
        EDGE_MULT,
        /** Multiplicity used in equation systems. */
        EQSYS_MULT
    }

    /** Enumeration of edge multiplicity directions. */
    public enum EdgeMultDir {
        /** Outgoing edge multiplicity. */
        OUTGOING {
            @Override
            public EdgeMultDir reverse() {
                return INCOMING;
            }

            @Override
            public ShapeNode incident(ShapeEdge edge) {
                return edge.source();
            }

            @Override
            public ShapeNode opposite(ShapeEdge edge) {
                return edge.target();
            }

            @Override
            public HostNode incident(HostEdge edge) {
                return edge.source();
            }

            @Override
            public HostNode opposite(HostEdge edge) {
                return edge.target();
            }
        },
        /** Incoming edge multiplicity. */
        INCOMING {
            @Override
            public EdgeMultDir reverse() {
                return OUTGOING;
            }

            @Override
            public ShapeNode incident(ShapeEdge edge) {
                return edge.target();
            }

            @Override
            public ShapeNode opposite(ShapeEdge edge) {
                return edge.source();
            }

            @Override
            public HostNode incident(HostEdge edge) {
                return edge.target();
            }

            @Override
            public HostNode opposite(HostEdge edge) {
                return edge.source();
            }
        };

        /** Returns the reverse direction. */
        abstract public EdgeMultDir reverse();

        /**
         * Returns the incident end of an edge according to this direction.
         * @return the edge target if this is {@link #INCOMING},
         *         the source if this is {@link #OUTGOING}
         */
        abstract public ShapeNode incident(ShapeEdge edge);

        /**
         * Returns the opposite end of an edge according to this direction.
         * @return the edge source if this is {@link #INCOMING},
         *         the target if this is {@link #OUTGOING}
         */
        abstract public ShapeNode opposite(ShapeEdge edge);

        /**
         * Returns the incident end of an edge according to this direction.
         * @return the edge target if this is {@link #INCOMING},
         *         the source if this is {@link #OUTGOING}
         */
        abstract public HostNode incident(HostEdge edge);

        /**
         * Returns the opposite end of an edge according to this direction.
         * @return the edge source if this is {@link #INCOMING},
         *         the target if this is {@link #OUTGOING}
         */
        abstract public HostNode opposite(HostEdge edge);
    }

}
