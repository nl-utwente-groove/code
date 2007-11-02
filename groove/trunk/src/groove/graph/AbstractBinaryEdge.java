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
 * $Id: AbstractBinaryEdge.java,v 1.12 2007-11-02 08:42:22 rensink Exp $
 */
package groove.graph;


/**
 * Abstract implementation of an (immutable) binary graph edge, as a tuple consisting of source and
 * target nodes.
 * @author Arend Rensink
 * @version $Revision: 1.12 $ $Date: 2007-11-02 08:42:22 $
 */
abstract public class AbstractBinaryEdge<SN extends Node, L extends Label, TN extends Node> extends AbstractEdge<SN,L> implements BinaryEdge {
    /**
     * Constructs a new edge on the basis of a given source and target. The label has to be provided
     * by this subclass.
     * @param source source node of the new edge
     * @param label label of the new edge
     * @param target target node of the new edge
     * @require <tt>source != null && target != null</tt>
     * @ensure <tt>source()==source</tt>, <tt>target()==target </tt>
     */
    protected AbstractBinaryEdge(SN source, L label, TN target) {
    	super(source, label);
        this.target = target;
    }

    // ----------------- Element methods ----------------------------
//
//    @Deprecated
//    public Edge imageFor(GenericNodeEdgeMap elementMap) {
//    	if( elementMap instanceof NodeEdgeMap ) {
//    		return imageFor((NodeEdgeMap)elementMap);
//    	} else if( elementMap instanceof VarNodeEdgeMultiMap ) {
//    		return imageFor((VarNodeEdgeMultiMap)elementMap);
//    	} return null;
//    }
//    
//    @Deprecated
//    private Edge imageFor(VarNodeEdgeMultiMap elementMap) {
//        // if this edge has an explicit image in the map, use that
//        Set<Edge> image = elementMap.getEdge(this);
//        if (image != null) {
//        	// TODO: This can't be right
//            return image.toArray(new Edge[0])[0];
//        }
//        Set<Node> sourceImage = elementMap.getNode(source());
//        if (sourceImage == null) {
//            return null;
//        }
//        Set<Node> targetImage = elementMap.getNode(target());
//        if (targetImage == null) {
//            return null;
//        }
//        Label labelImage = elementMap.getLabel(label());
//        Node imgSource = sourceImage.toArray(new Node[0])[0];
//        Node imgTarget = targetImage.toArray(new Node[0])[0];
//        if (source() == imgSource && target() == imgTarget && label() == labelImage) {
//            return this;
//        } else {
//            return newEdge(imgSource, labelImage, imgTarget);
//        }
//    }
//    
//    @Deprecated
//    private Edge imageFor(NodeEdgeMap elementMap) {
//        // if this edge has an explicit image in the map, use that
//        Edge image = elementMap.getEdge(this);
//        if (image != null) {
//            return image;
//        }
//        Node sourceImage = elementMap.getNode(source());
//        if (sourceImage == null) {
//            return null;
//        }
//        Node targetImage = elementMap.getNode(target());
//        if (targetImage == null) {
//            return null;
//        }
//        Label labelImage = elementMap.getLabel(label());
//        if (source() == sourceImage && target() == targetImage && label() == labelImage) {
//            return this;
//        } else {
//            return newEdge(sourceImage, labelImage, targetImage);
//        }
//    }

    final public Node[] ends() {
    	return new Node[] { source, target };
//    	if (ends == null) {
//    		ends = new Node[] { source, target };
//    	}
//    	return ends;
    }

    @Override
    final public Node end(int i) {
        switch (i) {
        case SOURCE_INDEX:
            return source;
        case TARGET_INDEX:
            return target;
        default:
            throw new IllegalArgumentException("Illegal end index number " + i + " for " + this);
        }
    }

    @Override
    final public int endIndex(Node node) {
        if (source.equals(node)) {
            return SOURCE_INDEX;
        } else if (target.equals(node)) {
            return TARGET_INDEX;
        } else {
            return -1;
        }
    }

    /**
     * This implementation tests if <tt>other</tt> equals <tt>source</tt> or <tt>target</tt>.
     */
    @Override
    final public boolean hasEnd(Node other) {
        return source.equals(other) || target.equals(other);
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
//     * @param target target of the new edge
//     * @ensure <tt>result.source() == source</tt>, <tt>result.label() == label</tt>,
//     *         <tt>result.target() == target</tt>
//     * @deprecated Use another factory method
//     */
//    @Deprecated
//    abstract public BinaryEdge newEdge(Node source, Label label, Node target);

    // -------------------- Object and related methods --------------------

    /**
     * Improves the testing for end point equality.
     */
    @Override
    protected boolean isEndEqual(Edge other) {
        return (source.equals(other.source())) && other.endCount() == END_COUNT && target.equals(other.end(TARGET_INDEX));
    }

    public final TN target() {
        return target;
    }

    @Override
    public final TN opposite() {
        return target;
    }

    /**
     * Returns a description consisting of the source node, an arrow with the label inscribed, and
     * the target node.
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
     * Slightly more efficient implementation returning the same value as the super method.
     */
    @Override
    protected int computeHashCode() {
    	int labelCode = label().hashCode();
    	int sourceCode = 3 * source.hashCode();
    	int targetCode = (labelCode+2) * target.hashCode();
        return labelCode //+ 3 * sourceCode - 2 * targetCode;
				^ ((sourceCode << SOURCE_SHIFT) + (sourceCode >>> SOURCE_RIGHT_SHIFT))
				+ ((targetCode << TARGET_SHIFT) + (targetCode >>> TARGET_RIGHT_SHIFT));
    }

    /** The target node of this edge. */
    protected final TN target;

    static {
        AbstractEdge.setMaxEndCount(END_COUNT);
    }

    // constants for hash code computation
	static private final int SOURCE_SHIFT = 1;
	static private final int TARGET_SHIFT = 2;
	static private final int BIT_COUNT = 32; 
	static private final int SOURCE_RIGHT_SHIFT = BIT_COUNT - SOURCE_SHIFT;
	static private final int TARGET_RIGHT_SHIFT = BIT_COUNT - TARGET_SHIFT;
}