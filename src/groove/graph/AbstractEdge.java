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
 * $Id: AbstractEdge.java,v 1.10 2008-01-30 09:32:57 iovka Exp $
 */
package groove.graph;

/**
 * Defines an abstract edge class by extending the abstract composite.
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class AbstractEdge<SN extends Node,L extends Label,TN extends Node>
        implements Edge {
    /**
     * Creates an edge with a given source node and label.
     */
    protected AbstractEdge(SN source, L label, TN target) {
        this.source = source;
        this.label = label;
        this.target = target;
    }

    public TN target() {
        return this.target;
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

    public SN source() {
        return this.source;
    }

    public final L label() {
        return this.label;
    }

    /**
     * Since all composites are immutable, the method just returns
     * <code>this</code>.
     */
    @Override
    public AbstractEdge<SN,L,TN> clone() {
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
     * Slightly more efficient implementation returning the same value as the
     * super method.
     */
    protected int computeHashCode() {
        return edgeHashCode();
    }

    /**
     * Slightly more efficient implementation returning the same value as the
     * super method.
     */
    protected int edgeHashCode() {
        int labelCode = label().hashCode();
        int sourceCode = 3 * this.source.hashCode();
        int targetCode = (labelCode + 2) * this.target.hashCode();
        return labelCode // + 3 * sourceCode - 2 * targetCode;
            ^ ((sourceCode << SOURCE_SHIFT) + (sourceCode >>> SOURCE_RIGHT_SHIFT))
            + ((targetCode << TARGET_SHIFT) + (targetCode >>> TARGET_RIGHT_SHIFT));
    }

    /**
     * Implements the ordering rules for {@link Element}s from the perspective
     * of an {@link Edge}.
     * @see Element#compareTo(Element)
     */
    public int compareTo(Element obj) {
        int result;
        if (obj instanceof Node) {
            result = compareToNode((Node) obj);
        } else {
            result = compareToEdge((Edge) obj);
        }
        return result;
    }

    /**
     * Compares this edge to another edge.
     */
    protected int compareToEdge(Edge other) {
        int result;
        result = source().compareTo(other.source());
        if (result != 0) {
            return result;
        }
        result = label().compareTo(other.label());
        if (result != 0) {
            return result;
        }
        result = target().compareTo(other.target());
        return result;
    }

    /**
     * Compares this edge to a node.
     */
    protected int compareToNode(Node node) {
        int result;
        // for nodes, we just need to look at the source of this edge
        result = source().compareTo(node);
        // if the source equals the node, edges come later
        if (result == 0) {
            result++;
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
        return isTypeEqual(obj) && isEndEqual((Edge) obj)
            && isLabelEqual((Edge) obj);
    }

    // -------------------- Object and related methods --------------------

    /**
     * Improves the testing for end point equality.
     */
    protected boolean isEndEqual(Edge other) {
        return (this.source.equals(other.source()))
            && this.target.equals(other.target());
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

    /**
     * The source node of this edge.
     */
    protected final SN source;
    /**
     * The label of this edge.
     * @invariant label != null
     */
    protected final L label;
    /** The target node of this edge. */
    protected final TN target;
    /** The pre-computed hash code. */
    private int hashCode;

    // constants for hash code computation
    static private final int SOURCE_SHIFT = 1;
    static private final int TARGET_SHIFT = 2;
    static private final int BIT_COUNT = 32;
    static private final int SOURCE_RIGHT_SHIFT = BIT_COUNT - SOURCE_SHIFT;
    static private final int TARGET_RIGHT_SHIFT = BIT_COUNT - TARGET_SHIFT;
}