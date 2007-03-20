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
 * $Id: DefaultLabelFlag.java,v 1.1.1.1 2007-03-20 10:05:34 kastenberg Exp $
 */
package groove.graph;


/**
 * Class representing a unary edge, i.e., a predicate instance,
 * with a {@link DefaultLabel}.
 * Instances are essentially pairs consisting of a node and a label.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 * @deprecated use {@link DefaultFlag} instead
 */
@Deprecated
public class DefaultLabelFlag extends AbstractUnaryEdge {
    /**
     * Constructs a new predicate on the basis of a given source node and label.
     * @param source source node of the new edge
     * @param label label of the new edge
     * @require <tt>source != null && label instanceof DefaultLabel</tt>
     * @ensure <tt>source()==source</tt> and <tt>label()==label</tt>
     */
    public DefaultLabelFlag(Node source, DefaultLabel label) {
        super(source, label);
//        this.label = label;
//        this.hashCode = computeHashCode();
    }
    
    /**
     * Constructs a new edge on the basis of a given source node and label text.
     * The label is a <tt>DefaultLabel</tt> on the basis of the label text.
     * @param source source node of the new edge
     * @param text text of the label of the new edge
     * @require <tt>text != null</tt>
     * @ensure <tt>result.source() == source</tt>, 
     *         <tt>result.label().text().equals(text)</tt>
     */
    public DefaultLabelFlag(Node source, String text) {
        this(source, DefaultLabel.createLabel(text));
    }
//
//    /**
//     * Constructs a new predicate on the basis of a given source and label index.
//     * For internal purposes only.
//     * @param source source node of the new predicate
//     * @param labelIndex the index of the label in <tt>DefaultLabel</tt>
//     * @require <tt>text != null</tt>
//     * @ensure <tt>result.source() == source</tt>, 
//     *         <tt>result.label()..equals(DefaultLabel.getLabel(index))</tt>
//     */
//    protected DefaultLabelFlag(Node source, char labelIndex) {
//        this.source = source;
//        this.labelIndex = labelIndex;
//        this.hashCode = label().hashCode() + (source.hashCode() << 1);
//        predicateCount++;
//    }

    // ----------------- Element methods ----------------------------

    public UnaryEdge imageFor(NodeEdgeMap elementMap) {
        Node sourceImage = elementMap.getNode(source());
        if (sourceImage == null)
            return null;
        return newEdge(sourceImage);
    }

    /**
     * Factory method: constructs a new edge from given source and target
     * nodes and label. Invokes the corresponding <tt>Predicate</tt> constructor
     * to create the edge.
     * @param source source of the new edge
     * @param label label of the new edge
     * @require <tt> (source == null || source instanceof Node) 
     *            && (label instanceof DefaultLabel)</tt>
     * @ensure <tt>result.source() == source</tt>, 
     *         <tt>result.label() == label</tt>
     */
    public UnaryEdge newEdge(Node source, Label label) {
        return new DefaultLabelFlag(source, (DefaultLabel) label);
    }

    // -------------------- Object and related methods --------------------

    /**
     * Returns <code>true</code> if <code>obj</code> is also a {@link UnaryEdge},
     * with the same source and label.
     */
    protected boolean isEndEqual(Edge other) {
        return (other.endCount() == 1) && source.equals(other.source());
    }
//
//    /**
//     * The hash code of a default edge is computed as a formula
//     * of the hash codes of source, label and target.
//     */
//    public int hashCode() {
//        return hashCode;
//    }

    public DefaultLabel label() {
        return (DefaultLabel) label;
    }

    /**
     * Returns a description consisting of the source node, an arrow with the
     * label inscribed, and the target node.
     */
    public String toString() {
        return "" + source() + ": " + label();
    }
    
    /**
     * Factory method to create a {@link DefaultLabel} from a string.
     * This implementation invokes {@link DefaultLabel#createLabel(String)}.
     */
    protected DefaultLabel createLabel(String text) {
        return DefaultLabel.createLabel(text);
    }
//
//    /** The label of this edge. */
//    protected final DefaultLabel label;
//    /** The pre-computed hash code. */
//    protected final int hashCode;
}