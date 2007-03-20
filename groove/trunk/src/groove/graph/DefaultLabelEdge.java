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
 * $Id: DefaultLabelEdge.java,v 1.1.1.1 2007-03-20 10:05:34 kastenberg Exp $
 */
package groove.graph;


/**
 * Default implementation of an (immutable) graph edge, as a triple consisting of
 * <tt>Node</tt> source and target nodes and a <tt>DefaultLabel</tt>.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $ $Date: 2007-03-20 10:05:34 $
 * @deprecated use {@link DefaultEdge} instead
 */
@Deprecated()
public class DefaultLabelEdge extends AbstractBinaryEdge {
/**
     * Constructs a new edge on the basis of a given source, label and target.
     * @param source source node of the new edge
     * @param label label of the new edge
     * @param target target node of the new edge
     * @require <tt>source != null && label instanceof DefaultLabel && target != null</tt>
     * @ensure <tt>source()==source</tt>,
     *         <tt>label()==label</tt>,
     *         <tt>target()==target </tt>
     */
    public DefaultLabelEdge(Node source, DefaultLabel label, Node target) {
        super(source, label, target);
        //        this.source = source;
        //        this.target = target;
        assert source
            != null : "Edge source for " + label + "-label should not be " + source;
        assert target
            != null : "Edge target for " + label + "-label should not be " + target;
//        setLabelIndex(label);
//        if (CLONE_DEBUG)
//            System.out.println("Constructing " + this);
    }

    public DefaultLabelEdge(Node[] ends, DefaultLabel label) {
        this(ends[SOURCE_INDEX], label, ends[TARGET_INDEX]);
        if (ends.length != END_COUNT) {
            throw new IllegalArgumentException("Illegal end count "+ends.length+" for default label edge");
        }
    }

    /**
     * Constructs a new edge on the basis of a given source, label text and target.
     * The label is a <tt>DefaultLabel</tt> on the basis of the label text.
     * @param source source node of the new edge
     * @param text text of the label of the new edge
     * @param target target node of the new edge
     * @require <tt>text != null</tt>
     * @ensure <tt>result.source() == source</tt>, 
     *         <tt>result.label().text().equals(text)</tt>,
     *         <tt>result.target() == target </tt>
     */
    public DefaultLabelEdge(Node source, String text, Node target) {
        this(source, DefaultLabel.createLabel(text), target);
    }
//
//    /**
//     * Constructor only used for creating edge prototypes.
//     * The resulting edge should not be used on graphs.
//     * @see GraphFactory
//     */
//    protected DefaultLabelEdge() {
//        // ecplicit empty constructor
//    }

    // ----------------- Element methods ----------------------------

    /**
     * Factory method: constructs a new edge from given source and target
     * nodes and label. Invokes the corresponding <tt>DefaultEdge</tt> constructor
     * to create the edge.
     * @param source source of the new edge
     * @param label label of the new edge
     * @param target target of the new edge
     * @require <tt> (source == null || source instanceof Node) 
     *            && (label instanceof DefaultLabel)
     *            && (target == null || target instanceof DefaultLabel)</tt>
     * @ensure <tt>result.source() == source</tt>, 
     *         <tt>result.label() == label</tt>, 
     *         <tt>result.target() == target</tt>
     */
    public BinaryEdge newEdge(Node source, Label label, Node target) {
        return DefaultEdge.createEdge(source, label, target);
    }

    /**
     * Factory method: constructs new edge between given nodes, 
     * with label taken from this one.
     * @param source source of the new edge
     * @param target target of the new edge
     * @see #newEdge(Node,Label,Node)
     * @require <tt> (source == null || source instanceof Node) 
     *            && (target == null || target instanceof DefaultLabel)</tt>
     * @ensure <tt>result == newEdge(source, label(), target)</tt>
     */
    public BinaryEdge newEdge(Node source, Node target) {
        return DefaultEdge.createEdge(source, label(), target);
    }

    // -------------------- Object and related methods --------------------

    public DefaultLabel label() {
        return (DefaultLabel) label;
    }

    // ---------------------------- COMMANDS --------------------------
//
//    /**
//     * Changes the label index. 
//     * Also recomputes <tt>longHashCode</tt> and <tt>hashCode</tt>.
//     * @param index the new label index
//     */
//    protected final void setLabelIndex(DefaultLabel label) {
//    	this.label = label;
//        this.hashCode = computeHashCode();
//    }
//
//    /** The label of this edge. @invariant label != null */
//    protected DefaultLabel label;
//    /** The pre-computed hash code. */
//    protected int hashCode;
//
//    // --------------------- DEBUG DEFINITIONS -----------------------
//    private static final boolean CLONE_DEBUG = false;
}
