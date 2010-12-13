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

import groove.graph.Label;
import groove.graph.Node;
import groove.graph.TypeLabel;
import groove.trans.HostFactory;
import groove.trans.RuleToHostMap;

/** Factory class for graph elements. */
public class ShapeFactory extends HostFactory {
    /** Private constructor. */
    private ShapeFactory() {
        // empty
    }

    /** Creates a fresh node. */
    @Override
    public ShapeNode createNode() {
        return ShapeNode.createNode();
    }

    /** Creates a node with a given number. */
    @Override
    public ShapeNode createNode(int nr) {
        return ShapeNode.createNode(nr);
    }

    /** Creates an edge with the given source, label and target. */
    @Override
    public ShapeEdge createEdge(Node source, Label label, Node target) {
        return ShapeEdge.createEdge((ShapeNode) source, (TypeLabel) label,
            (ShapeNode) target);
    }

    /** Creates a fresh mapping from rules to (this type of) host graph. */
    @Override
    public RuleToHostMap createRuleToHostMap() {
        return new RuleToShapeMap();
    }

    /** Singleton instance of this factory. */
    public final static ShapeFactory INSTANCE = new ShapeFactory();
}
