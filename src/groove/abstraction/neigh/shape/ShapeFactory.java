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
import groove.graph.Label;
import groove.graph.Node.Factory;
import groove.graph.NodeStore;
import groove.graph.TypeLabel;
import groove.graph.TypeNode;
import groove.trans.HostFactory;
import groove.trans.HostNode;
import groove.trans.RuleToHostMap;

/** Factory class for shape elements. */
public class ShapeFactory extends HostFactory {
    /** Private constructor. */
    private ShapeFactory() {
        super();
    }

    /** Creates an untyped node with the next number. */
    public ShapeNode createNode() {
        return (ShapeNode) createNode(null);
    }

    @Override
    public ShapeNode createNode(int nr) {
        return (ShapeNode) super.createNode(nr);
    }

    @Override
    protected ShapeEdge createEdge(HostNode source, Label label,
            HostNode target, int nr) {
        return new ShapeEdge(this, (ShapeNode) source, (TypeLabel) label,
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

    @Override
    protected NodeStore<ShapeNode> createNodeStore() {
        return new NodeStore<ShapeNode>(new Factory<ShapeNode>() {
            @Override
            public ShapeNode newNode(int nr, TypeNode type) {
                return NODE_PROTOTYPE.newNode(nr, type);
            }
        });
    }

    /** Returns the singleton instance of this factory. */
    public static ShapeFactory newInstance() {
        return new ShapeFactory();
    }

    /** Used only as a reference for the constructor. */
    private static final ShapeNode NODE_PROTOTYPE = new ShapeNode(0, null);
}
