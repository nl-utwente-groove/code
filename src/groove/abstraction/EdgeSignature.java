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
 * EDUARDO
 * @author Eduardo Zambon
 * @version $Revision $
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

    /** EDUARDO */
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

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** EDUARDO */
    public boolean asOutSigContains(Edge edge) {
        return this.node.equals(edge.source())
            && this.label.equals(edge.label())
            && this.equivClass.contains(edge.opposite());
    }

    /** EDUARDO */
    public boolean asInSigContains(Edge edge) {
        return this.node.equals(edge.opposite())
            && this.label.equals(edge.label())
            && this.equivClass.contains(edge.source());
    }

    /** EDUARDO */
    public ShapeNode getNode() {
        return this.node;
    }

    /** EDUARDO */
    public Label getLabel() {
        return this.label;
    }

    /** EDUARDO */
    public EquivClass<ShapeNode> getEquivClass() {
        return this.equivClass;
    }

    /** EDUARDO */
    public boolean isUnique() {
        return this.equivClass.size() == 1;
    }

}
