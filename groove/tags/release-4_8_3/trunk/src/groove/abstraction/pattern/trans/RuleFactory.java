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
package groove.abstraction.pattern.trans;

import groove.abstraction.pattern.shape.TypeEdge;
import groove.abstraction.pattern.shape.TypeNode;
import groove.graph.ElementFactory;
import groove.graph.Label;
import groove.graph.Morphism;

/** Factory class for rule elements. */
public class RuleFactory implements ElementFactory<RuleNode,RuleEdge> {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** The highest node number returned by this factory. */
    private int maxNodeNr;

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public int getMaxNodeNr() {
        return this.maxNodeNr;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Maximises the current maximum node number with another number. */
    private void updateMaxNodeNr(int nr) {
        this.maxNodeNr = Math.max(this.maxNodeNr, nr);
    }

    /** Creates a new rule node with given number and type. */
    public RuleNode createNode(int nr, TypeNode type) {
        updateMaxNodeNr(nr);
        return new RuleNode(nr, type);
    }

    /** Creates a new rule edge with given number and type. */
    public RuleEdge createEdge(int nr, RuleNode rSrc, TypeEdge tEdge,
            RuleNode rTgt) {
        return new RuleEdge(nr, rSrc, tEdge, rTgt);
    }

    // ------------------------------------------------------------------------
    // Unsupported methods
    // ------------------------------------------------------------------------

    @Override
    public RuleNode createNode(int nr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Label createLabel(String text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RuleEdge createEdge(RuleNode source, String text, RuleNode target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RuleEdge createEdge(RuleNode source, Label label, RuleNode target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Morphism<RuleNode,RuleEdge> createMorphism() {
        throw new UnsupportedOperationException();
    }

}
