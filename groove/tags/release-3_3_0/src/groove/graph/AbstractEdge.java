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
public abstract class AbstractEdge<N extends Node,L extends Label> implements
        Edge {
    /**
     * Creates an edge with a given source node and label.
     */
    protected AbstractEdge(N source, L label) {
        this.source = source;
        this.label = label;
    }

    public final N source() {
        return this.source;
    }

    public final L label() {
        return this.label;
    }

    /**
     * Looks up the requested node through <tt>ends()[i]</tt>.
     */
    public Node end(int i) {
        return ends()[i];
    }

    /**
     * Computes the end count through <tt>ends().length</tt>.
     */
    public int endCount() {
        return ends().length;
    }

    /**
     * Looks up <tt>node</tt> by comparing it to each <tt>end(i)</tt> in turn,
     * and returning the first <tt>i</tt> for which the comparison holds.
     */
    public int endIndex(Node node) {
        int result = -1;
        for (int i = 0; result < 0 && i < endCount(); i++) {
            if (end(i).equals(node)) {
                result = i;
            }
        }
        return result;
    }

    /**
     * Looks up <tt>node</tt> by comparing it to each <tt>end(i)</tt> in turn.
     */
    public boolean hasEnd(Node node) {
        boolean result = false;
        for (int i = 0; !result && i < endCount(); i++) {
            result = end(i).equals(node);
        }
        return result;
    }

    /**
     * Yields {@link #source()} if {@link #endCount()} is 1, otherwise the end
     * at {@link #TARGET_INDEX}.
     */
    public Node opposite() {
        if (endCount() == 1) {
            return source();
        } else {
            return end(Edge.TARGET_INDEX);
        }
    }

    /**
     * Since all composites are immutable, the method just returns
     * <code>this</code>.
     */
    @Override
    public AbstractEdge<N,L> clone() {
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
     * Overwrites the method to add a hash code for the label.
     */
    protected int computeHashCode() {
        int result = 0;
        for (int i = 0; i < endCount(); i++) {
            result += end(i).hashCode() << (i + 1);
        }
        result += label().hashCode();
        return result;
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
        // assert result != 0 || this.equals(obj) : String.format("Ordering of
        // distinct objects %s and %s yields 0", this, obj);
        return result;
    }

    /**
     * Compares this edge to another edge.
     */
    protected int compareToEdge(Edge other) {
        int result;
        result = source().compareTo(other.source());
        // for other edges, first the end count, then the label, then the
        // other ends
        if (result == 0) {
            result = endCount() - other.endCount();
        }
        if (result == 0) {
            result = label().compareTo(other.label());
        }
        for (int i = 1; result == 0 && i < endCount(); i++) {
            result = end(i).compareTo(other.end(i));
        }
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
    protected boolean isEndEqual(Edge other) {
        boolean result = endCount() == other.endCount();
        for (int i = 0; result && i < endCount(); i++) {
            result = end(i).equals(other.end(i));
        }
        return result;
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
    protected final N source;
    /**
     * The label of this edge.
     * @invariant label != null
     */
    protected final L label;
    /** The pre-computed hash code. */
    private int hashCode;

    /**
     * Sets the maximal number of ends of any edge currently in the system. This
     * only has effect if the new number exceeds the previous maximum.
     * @param endCount the new maximum end count
     * @ensure if <tt>getMaxEndCount() &gtr;= endCount</tt>
     * @see #getMaxEndCount()
     */

    static public void setMaxEndCount(int endCount) {
        maxEndCount = Math.max(maxEndCount, endCount);
    }

    /**
     * Returns the maximal number of ends of any edge currently in the system.
     * Note that this may be dynamically increased by any concrete edge class.
     * @return the maximum number of ends of any edge
     * @ensure <tt>result &gtr;= 1</tt>
     */
    static public int getMaxEndCount() {
        return maxEndCount;
    }

    /**
     * The maximal number of ends of any edge currently in the system. Note that
     * this may be dynamically updated by any concrete edge class.
     * @invariant <tt>maxEndCount &gtr;= 1</tt>
     */
    static private int maxEndCount;
}