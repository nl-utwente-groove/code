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
 * $Id$
 */
package groove.abstraction;

import groove.graph.Edge;
import groove.graph.Label;

/**
 * An edge signature is composed by a node (n), a label (l), and an equivalence
 * class (C) and is used as the key for the outgoing and incoming edge
 * multiplicity mappings.
 * 
 * @author Eduardo Zambon
 */
public class EdgeSignature {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    private Label label;
    private ShapeNode node;
    private EquivClass<ShapeNode> equivClass;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Standard constructor that just fills in the object fields. */
    public EdgeSignature(ShapeNode node, Label label,
            EquivClass<ShapeNode> equivClass) {
        this.label = label;
        this.node = node;
        this.equivClass = equivClass;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "(" + this.node + ", " + this.label + ", " + this.equivClass
            + ")";
    }

    /** 
     * Two edge signatures are equal if they have the same node, same label,
     * and the same equivalence class.
     */
    @Override
    public boolean equals(Object o) {
        assert o != null : "Cannot compare to null!";
        boolean result;
        if (o instanceof EdgeSignature) {
            EdgeSignature es = (EdgeSignature) o;
            result =
                this.node.equals(es.node) && this.label.equals(es.label)
                    && this.equivClass.equals(es.equivClass);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result
                + ((this.equivClass == null) ? 0 : this.equivClass.hashCode());
        result =
            prime * result + ((this.label == null) ? 0 : this.label.hashCode());
        result =
            prime * result + ((this.node == null) ? 0 : this.node.hashCode());
        return result;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Returns true if the edge signature, when being considered a signature
     * for outgoing edges, contains the edge (e) given as argument.
     * The test is true if n == src(e) && l == lbl(e) && tgt(e) \in C .
     */
    public boolean asOutSigContains(Edge edge) {
        return this.node.equals(edge.source())
            && this.label.equals(edge.label())
            && this.equivClass.contains(edge.opposite());
    }

    /**
     * Returns true if the edge signature, when being considered a signature
     * for incoming edges, contains the edge (e) given as argument.
     * The test is true if n == tgt(e) && l == lbl(e) && src(e) \in C .
     */
    public boolean asInSigContains(Edge edge) {
        return this.node.equals(edge.opposite())
            && this.label.equals(edge.label())
            && this.equivClass.contains(edge.source());
    }

    /** Basic getter method. */
    public ShapeNode getNode() {
        return this.node;
    }

    /** Basic getter method. */
    public Label getLabel() {
        return this.label;
    }

    /** Basic getter method. */
    public EquivClass<ShapeNode> getEquivClass() {
        return this.equivClass;
    }

    /** Returns true if C is a singleton set. */
    public boolean isUnique() {
        return this.equivClass.size() == 1;
    }

    /** Returns true if n \in C . */
    public boolean isSelfReferencing() {
        return this.equivClass.contains(this.node);
    }

}
