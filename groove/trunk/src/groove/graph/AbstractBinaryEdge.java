// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/* 
 * $Id: AbstractBinaryEdge.java,v 1.1.1.1 2007-03-20 10:05:33 kastenberg Exp $
 */
package groove.graph;



/**
 * Abstract implementation of an (immutable) binary graph edge, as a tuple consisting of source and
 * target nodes.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $ $Date: 2007-03-20 10:05:33 $
 */
abstract public class AbstractBinaryEdge extends AbstractEdge implements BinaryEdge {
    static {
        AbstractEdge.setMaxEndCount(END_COUNT);
    }

    /**
     * Constructs a new edge on the basis of a given source and target. The label has to be provided
     * by ths subclass.
     * @param source source node of the new edge
     * @param label label of the new edge
     * @param target target node of the new edge
     * @require <tt>source != null && target != null</tt>
     * @ensure <tt>source()==source</tt>, <tt>target()==target </tt>
     */
    protected AbstractBinaryEdge(Node source, Label label, Node target) {
    	super(source, label);
        this.target = target;
    }
//
//    /**
//     * Constructor only used for creating edge prototypes. The resulting edge should not be used on
//     * graphs.
//     * @see GraphFactory
//     */
//    protected AbstractBinaryEdge() {
//    	super(null, null);
//        this.target = null;
//    }

    // ----------------- Element methods ----------------------------

    public Edge imageFor(NodeEdgeMap elementMap) {
        // if this edge has an explicit image in the map, use that
        Edge image = elementMap.getEdge(this);
//        if (image != null && !(elementMap instanceof MergeMap)
//                || elementMap.containsKey(this)) {
        if (image != null) {
            return image;
        }
        Node sourceImage = elementMap.getNode(source());
        if (sourceImage == null) {
            return null;
        }
        Node targetImage = elementMap.getNode(target());
        if (targetImage == null) {
            return null;
        }
        if (source() == sourceImage && target() == targetImage) {
            return this;
        } else {
            return newEdge(sourceImage, targetImage);
        }
    }

    final public Node[] ends() {
    	return new Node[] { source, target };
//    	if (ends == null) {
//    		ends = new Node[] { source, target };
//    	}
//    	return ends;
    }

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
    final public boolean hasEnd(Node other) {
        return source.equals(other) || target.equals(other);
    }

    final public int endCount() {
        return END_COUNT;
    }

    /**
     * Factory method: constructs a new edge from given source and target nodes and label.
     * @param source source of the new edge
     * @param label label of the new edge
     * @param target target of the new edge
     * @ensure <tt>result.source() == source</tt>, <tt>result.label() == label</tt>,
     *         <tt>result.target() == target</tt>
     */
    abstract public BinaryEdge newEdge(Node source, Label label, Node target);

    /**
     * Factory method: constructs new edge between given nodes, with label taken from this one.
     * Convenience method for <tt>newEdge(source, label(), target)</tt>.
     * @param source source of the new edge
     * @param target target of the new edge
     * @see #newEdge(Node,Label,Node)
     */
    public BinaryEdge newEdge(Node source, Node target) {
        return newEdge(source, this.label(), target);
    }

    // -------------------- Object and related methods --------------------

    /**
     * Improves the testing for end point equality.
     */
    protected boolean isEndEqual(Edge other) {
        return (source.equals(other.source())) && other.endCount() == END_COUNT && target.equals(other.end(TARGET_INDEX));
    }

    public Node target() {
        return target;
    }

    public final Node opposite() {
        return target;
    }

    /**
     * Returns a description consisting of the source node, an arrow with the label inscribed, and
     * the target node.
     */
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
    protected int computeHashCode() {
    	int labelCode = label().hashCode();
    	int sourceCode = source.hashCode();
    	int targetCode = target.hashCode();
    	final int SOURCE_SHIFT = 1;
    	final int TARGET_SHIFT = 2;
    	final int BIT_COUNT = 32; 
    	final int SOURCE_RIGHT_SHIFT = BIT_COUNT - SOURCE_SHIFT;
    	final int TARGET_RIGHT_SHIFT = BIT_COUNT - TARGET_SHIFT;
        return labelCode
				^ ((sourceCode << SOURCE_SHIFT) + (sourceCode >>> SOURCE_RIGHT_SHIFT))
				^ ((targetCode << TARGET_SHIFT) + (targetCode >>> TARGET_RIGHT_SHIFT));
//        return (((targetCode * labelCode) ^ labelCode) << 4) + sourceCode; 
    }

    /** The target node of this edge. */
    protected final Node target;
//    /**
//     * The end nodes of this edge.
//     */
//    private Node[] ends;
}