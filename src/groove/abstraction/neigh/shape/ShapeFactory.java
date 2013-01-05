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
package groove.abstraction.neigh.shape;

import groove.abstraction.neigh.trans.RuleToShapeMap;
import groove.grammar.host.HostFactory;
import groove.grammar.host.HostNode;
import groove.grammar.rule.RuleToHostMap;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeFactory;
import groove.grammar.type.TypeLabel;
import groove.graph.Label;

import java.util.Set;

/**
 * Factory class for shape elements.
 * 
 * @author Eduardo Zambon
 */
public final class ShapeFactory extends HostFactory {
    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Private constructor. */
    private ShapeFactory(TypeFactory typeFactory) {
        super(typeFactory);
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------
    @Override
    protected ShapeNode newNode(int nr) {
        return new ShapeNode(nr, getLastNodeType());
    }

    @Override
    public ShapeNode createNode() {
        return (ShapeNode) super.createNode();
    }

    @Override
    public ShapeNode createNode(int nr) {
        return (ShapeNode) super.createNode(nr);
    }

    @Override
    public ShapeNode createNode(TypeLabel type) {
        return (ShapeNode) super.createNode(type);
    }

    @Override
    public ShapeNode createNode(int nr, TypeLabel type) {
        return (ShapeNode) super.createNode(nr, type);
    }

    @Override
    public ShapeNode createNode(TypeLabel type,
            Set<? extends HostNode> usedNodes) {
        return (ShapeNode) super.createNode(type, usedNodes);
    }

    @Override
    public ShapeNode getNode(int nr) {
        return (ShapeNode) super.getNode(nr);
    }

    @Override
    public ShapeEdge createEdge(HostNode source, String text, HostNode target) {
        return (ShapeEdge) super.createEdge(source, text, target);
    }

    @Override
    public ShapeEdge createEdge(HostNode source, Label label, HostNode target) {
        return (ShapeEdge) super.createEdge(source, label, target);
    }

    @Override
    protected ShapeEdge newEdge(HostNode source, TypeEdge type,
            HostNode target, int nr) {
        return new ShapeEdge(this, (ShapeNode) source, type,
            (ShapeNode) target, nr);
    }

    @Override
    public ShapeMorphism createMorphism() {
        return new ShapeMorphism(this);
    }

    @Override
    public RuleToHostMap createRuleToHostMap() {
        return new RuleToShapeMap(this);
    }

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------
    /** Returns a new, untyped instance of this factory. */
    public static ShapeFactory newInstance() {
        return newInstance(TypeFactory.newInstance());
    }

    /** Returns a new instance of this factory for a given type graph. */
    public static ShapeFactory newInstance(TypeFactory typeFactory) {
        return new ShapeFactory(typeFactory);
    }
}
