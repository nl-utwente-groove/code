/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package groove.graph;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Defines an abstract edge class by extending the abstract composite.
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class AEdge<N extends Node,L extends Label> implements GEdge<N> {
    /**
     * Creates a numbered edge with a given source and target node and label.
     */
    protected AEdge(N source, L label, N target, int number) {
        assert source != null && label != null && target != null;
        this.source = source;
        this.label = label;
        this.target = target;
        this.number = number;
    }

    /**
     * Creates an unnumbered edge with a given source and target node and label.
     * (Unnumbered means that the edge number will be 0.)
     */
    protected AEdge(N source, L label, N target) {
        this(source, label, target, 0);
        assert isSimple() : "Non-simple edges should have a proper edge number";
    }

    /**
     * Creates an unnumbered edge with a given source and target node.
     * Only for subclasses that overwrite {@link #label()} to
     * return a non-{@code null} value
     */
    @SuppressWarnings("unchecked")
    protected AEdge(N source, N target) {
        assert source != null && target != null;
        this.source = source;
        this.target = target;
        this.label = (L) this;
        this.number = 0;
        assert isSimple() : "Non-simple edges should have a proper edge number";
        assert label() != null;
    }

    @Override
    public N source() {
        return this.source;
    }

    /**
     * The source node of this edge.
     */
    protected final N source;

    @Override
    public N target() {
        return this.target;
    }

    /** The target node of this edge. */
    protected final N target;

    @Override
    public L label() {
        return this.label;
    }

    /**
     * The label of this edge.
     * @invariant label != null
     */
    private final @NonNull L label;

    /** Indicates if this edge is uniquely
     * identified by source, target and label.
     * If the edge is simple, the edge number is ignored
     * in hash code and equality test; if it is not simple,
     * then the edge number is also taken into account.
     * @return {@code true} if this edge is simple.
     */
    public abstract boolean isSimple();

    @Override
    public int getNumber() {
        return this.number;
    }

    private final int number;

    @Override
    public boolean isLoop() {
        return source() == target();
    }

    /**
     * Returns a description consisting of the source node, an arrow with the
     * label inscribed, and the target node.
     */
    @Override
    public String toString() {
        return "" + source() + "--" + getLabelText() + "-->" + target();
    }

    /** Callback method in {@link #toString()} to print the label text. */
    protected String getLabelText() {
        return label().text();
    }

    /**
     * Since all composites are immutable, the method just returns
     * <code>this</code>.
     */
    @Override
    public AEdge<N,L> clone() {
        return this;
    }

    /**
     * Delegates to {@link #computeHashCode()}.
     */
    @Override
    final public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = computeHashCode();
            if (result == 0) {
                result = 1;
            }
            this.hashCode = result;
        }
        return result;
    }

    /**
     * Deterministically computes the hash code out of the
     * source, label and target.
     */
    protected int computeHashCode() {
        int labelCode = label().hashCode();
        int sourceCode = 3 * this.source.hashCode();
        int targetCode = (labelCode + 2) * this.target.hashCode();
        int result = labelCode // + 3 * sourceCode - 2 * targetCode;
            ^ ((sourceCode << SOURCE_SHIFT) + (sourceCode >>> SOURCE_RIGHT_SHIFT))
                + ((targetCode << TARGET_SHIFT) + (targetCode >>> TARGET_RIGHT_SHIFT));
        if (!isSimple()) {
            result = 31 * result + getNumber();
        }
        return result;
    }

    /**
     * Returns <tt>true</tt> if <tt>obj</tt> is also an edge with the same label
     * and number of endpoints, and equal endpoints at each index. The actual
     * test is delegated to {@link #isTypeEqual(Object)} and
     * {@link #isEndEqual(Edge)}.
     * @see #isTypeEqual(Object)
     * @see #isEndEqual(Edge)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!isTypeEqual(obj)) {
            return false;
        }
        Edge other = (Edge) obj;
        if (!isSimple() && other.getNumber() != getNumber()) {
            return false;
        }
        return isEndEqual(other) && isLabelEqual(other);
    }

    // -------------------- Object and related methods --------------------

    /**
     * Improves the testing for end point equality.
     */
    protected boolean isEndEqual(Edge other) {
        return (this.source.equals(other.source())) && this.target.equals(other.target());
    }

    /**
     * Tests if another object is type equal to this one. This implementation
     * insists that the object is an {@link Edge}. Callback method from
     * {@link #equals(Object)}.
     */
    /**
     * This implementation tests if <code>obj instanceof Edge</code>.
     */
    protected boolean isTypeEqual(Object obj) {
        return obj instanceof Edge;
    }

    /**
     * Tests if this composite has the same number of end points as well as
     * equal end points as another. Callback method from {@link #equals(Object)}
     * .
     */
    protected boolean isLabelEqual(Edge other) {
        return label().equals(other.label());
    }

    @Override
    public EdgeRole getRole() {
        return label().getRole();
    }

    /** The pre-computed hash code. */
    private int hashCode;

    // constants for hash code computation
    static private final int SOURCE_SHIFT = 1;
    static private final int TARGET_SHIFT = 2;
    static private final int BIT_COUNT = 32;
    static private final int SOURCE_RIGHT_SHIFT = BIT_COUNT - SOURCE_SHIFT;
    static private final int TARGET_RIGHT_SHIFT = BIT_COUNT - TARGET_SHIFT;
}