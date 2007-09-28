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
 * $Id: AbstractUnaryEdge.java,v 1.7 2007-09-28 10:23:59 rensink Exp $
 */
package groove.graph;


/**
 * Abstract implementation of an (immutable) unary graph edge, consisting of one source node only.
 * @author Arend Rensink
 * @version $Revision: 1.7 $ $Date: 2007-09-28 10:23:59 $
 */
abstract public class AbstractUnaryEdge<N extends Node> extends AbstractEdge<N> implements UnaryEdge {
    static {
        AbstractEdge.setMaxEndCount(END_COUNT);
    }

    /**
     * Constructs a new edge on the basis of a given source. The label has to be provided
     * by the subclass.
     * @param source source node of the new edge
     * @param label label of the new edge
     * @require <tt>source != null</tt>
     * @ensure <tt>source()==source</tt>
     */
    protected AbstractUnaryEdge(N source, Label label) {
    	super(source, label);
    }
//
//    // ----------------- Element methods ----------------------------
//
//    @Deprecated
//    public UnaryEdge imageFor(GenericNodeEdgeMap elementMap) {
//    	if( elementMap instanceof NodeEdgeMap ) {
//    		return imageFor((NodeEdgeMap)elementMap);
//    	} else if( elementMap instanceof VarNodeEdgeMultiMap ) {
//    		return imageFor((VarNodeEdgeMultiMap)elementMap);
//    	} return null;
//    }
//    
//    @Deprecated
//    protected UnaryEdge imageFor(VarNodeEdgeMultiMap elementMap) {
//        Node sourceImage = elementMap.getNode(source()).toArray(new Node[0])[0];
//        if (sourceImage == null) {
//            return null;
//        }
//        Label labelImage = elementMap.getLabel(label());
//        if (source() == sourceImage && label() == labelImage) {
//            return this;
//        } else {
//            return newEdge(sourceImage, labelImage);
//        }
//    }
//    
//    @Deprecated
//    public UnaryEdge imageFor(NodeEdgeMap elementMap) {
//        Node sourceImage = elementMap.getNode(source());
//        if (sourceImage == null) {
//            return null;
//        }
//        Label labelImage = elementMap.getLabel(label());
//        if (source() == sourceImage && label() == labelImage) {
//            return this;
//        } else {
//            return newEdge(sourceImage, labelImage);
//        }
//    }

    final public Node[] ends() {
    	return new Node[] { source };
    }

    @Override
    final public N end(int i) {
        switch (i) {
        case SOURCE_INDEX:
            return source;
        default:
            throw new IllegalArgumentException("Illegal end index number " + i + " for " + this);
        }
    }

    @Override
    final public int endIndex(Node node) {
        if (source.equals(node)) {
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
        return source.equals(other);
    }

    @Override
    final public int endCount() {
        return END_COUNT;
    }
//
//    /**
//     * Factory method: constructs a new edge from given source and target nodes and label.
//     * @param source source of the new edge
//     * @param label label of the new edge
//     * @ensure <tt>result.source() == source</tt>, <tt>result.label() == label</tt>,
//     *         <tt>result.target() == target</tt>
//     * @deprecated use other factory methods
//     */
//    @Deprecated
//    abstract public UnaryEdge newEdge(Node source, Label label);
//
//    /**
//     * Factory method: constructs new edge between given nodes, with label taken from this one.
//     * Convenience method for <tt>newEdge(source, label(), target)</tt>.
//     * @param source source of the new edge
//     * @see #newEdge(Node,Label)
//     * @deprecated use other factory methods
//     */
//    @Deprecated
//    public UnaryEdge newEdge(Node source) {
//        return newEdge(source, this.label());
//    }

    // -------------------- Object and related methods --------------------

    /**
     * Improves the testing for end point equality.
     */
    @Override
    protected boolean isEndEqual(Edge other) {
        return (source.equals(other.source())) && other.endCount() == END_COUNT;
    }

    @Override
    public final N opposite() {
        return source;
    }

    /**
     * Returns a description consisting of the source node, an arrow with the label inscribed, and
     * the target node.
     */
    @Override
    public String toString() {
        return "" + source() + " --" + label() + "-|";
    }
    
    /**
     * Slightly more efficient implementation returning the same value as the super method.
     */
    @Override
    protected int computeHashCode() {
        return label().hashCode() + (source.hashCode() << 1);
    }
}