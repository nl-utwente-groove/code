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
 * $Id: DefaultFlag.java,v 1.1.1.1 2007-03-20 10:05:33 kastenberg Exp $
 */
package groove.graph;

/**
 * Default implementation of an (immutable) unary graph edge, as a tuple consisting of
 * a source node and a label.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $ $Date: 2007-03-20 10:05:33 $
 */
public class DefaultFlag extends AbstractUnaryEdge {
	/**
     * Constructs a new edge on the basis of a given source and label text.
     * The label created will be a {@link DefaultLabel}.
     * @param source source node of the new edge
     * @param text label text of the new edge
     * @require <tt>source != null && text != null</tt>
     * @ensure <tt>source()==source</tt>,
     *         <tt>label().text().equals(text)</tt>
     */
    public DefaultFlag(Node source, String text) {
        this(source,DefaultLabel.createLabel(text));
    }

    /**
     * Constructs a new edge on the basis of a given source and label.
     * @param source source node of the new edge
     * @param label label of the new edge
     * @require <tt>source != null</tt>
     * @ensure <tt>source()==source</tt>,
     *         <tt>label()==label</tt>
     */
    public DefaultFlag(Node source, Label label) {
        super(source, label);
//        this.label = label;
//        this.hashCode = computeHashCode();
    }

    // ----------------- Element methods ----------------------------

    /**
     * This implamentation returns a {@link UnaryEdge}.
     */
    public UnaryEdge newEdge(Node source, Label label) {
        return new DefaultFlag(source, label);
    }

    // -------------------- Object and related methods --------------------
//
//    /**
//     * Returns the precomputed hash code.
//     */
//    public int hashCode() {
//        return hashCode;
//    }
//
//    public Label label() {
//        return label;
//    }

    // ---------------------------- COMMANDS --------------------------

    /**
     * Factory method for a new label.
     * This implementation returns a {@link DefaultLabel},
     * obtained by {@link DefaultLabel#createLabel(String)}.
     */
    protected Label createLabel(String text) {
        return DefaultLabel.createLabel(text);
    }
//
//    /** The label of this edge. @invariant label != null */
//    protected final Label label;
//    /** The pre-computed hash code. */
//    protected final int hashCode;

}
