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
 * $Id: AbstractUnaryEdge.java,v 1.9 2008-01-30 09:32:57 iovka Exp $
 */
package groove.graph;

/**
 * Abstract implementation of an (immutable) unary graph edge, consisting of one
 * source node only.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:57 $
 */
abstract public class AbstractUnaryEdge<N extends Node,L extends Label> extends
        AbstractEdge<N,L> implements UnaryEdge {
    static {
        AbstractEdge.setMaxEndCount(END_COUNT);
    }

    /**
     * Constructs a new edge on the basis of a given source. The label has to be
     * provided by the subclass.
     * @param source source node of the new edge
     * @param label label of the new edge
     * @require <tt>source != null</tt>
     * @ensure <tt>source()==source</tt>
     */
    protected AbstractUnaryEdge(N source, L label) {
        super(source, label);
    }

    // ----------------- Element methods ----------------------------

    final public Node[] ends() {
        return new Node[] {this.source};
    }

    @Override
    final public N end(int i) {
        switch (i) {
        case SOURCE_INDEX:
            return this.source;
        default:
            throw new IllegalArgumentException("Illegal end index number " + i
                + " for " + this);
        }
    }

    @Override
    final public int endIndex(Node node) {
        if (this.source.equals(node)) {
            return SOURCE_INDEX;
        } else {
            return -1;
        }
    }

    /**
     * This implementation tests if <tt>other</tt> equals <tt>source</tt>.
     */
    @Override
    final public boolean hasEnd(Node other) {
        return this.source.equals(other);
    }

    @Override
    final public int endCount() {
        return END_COUNT;
    }

    // -------------------- Object and related methods --------------------

    /**
     * Improves the testing for end point equality.
     */
    @Override
    protected boolean isEndEqual(Edge other) {
        return (this.source.equals(other.source()))
            && other.endCount() == END_COUNT;
    }

    @Override
    public final N opposite() {
        return this.source;
    }

    /**
     * Returns a description consisting of the source node, an arrow with the
     * label inscribed, and the target node.
     */
    @Override
    public String toString() {
        return "" + source() + " --" + label() + "-|";
    }

    /**
     * Slightly more efficient implementation returning the same value as the
     * super method.
     */
    @Override
    protected int computeHashCode() {
        return label().hashCode() + (this.source.hashCode() << 1);
    }
}