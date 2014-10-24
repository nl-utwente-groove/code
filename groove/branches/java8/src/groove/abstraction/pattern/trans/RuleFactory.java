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

import org.eclipse.jdt.annotation.NonNull;

/** Factory class for rule elements. */
public class RuleFactory extends ElementFactory<RuleNode,RuleEdge> {
    /** Creates a new rule node with given number and type. */
    public RuleNode createNode(int nr, TypeNode type) {
        RuleNode result = new RuleNode(nr, type);
        registerNode(result);
        return result;
    }

    /** Creates a new rule edge with given number and type. */
    public @NonNull RuleEdge createEdge(int nr, RuleNode rSrc, TypeEdge tEdge, RuleNode rTgt) {
        return new RuleEdge(nr, rSrc, tEdge, rTgt);
    }

    // ------------------------------------------------------------------------
    // Unsupported methods
    // ------------------------------------------------------------------------

    @Override
    public Label createLabel(String text) {
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

    @Override
    protected RuleNode newNode(int nr) {
        throw new UnsupportedOperationException();
    }
}
