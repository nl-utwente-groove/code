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
package groove.trans;

import groove.graph.DefaultNode;
import groove.graph.ElementFactory;
import groove.graph.Label;
import groove.graph.Node;

/** Factory class for graph elements. */
public class RuleFactory implements ElementFactory<RuleNode,RuleLabel,RuleEdge> {
    /** Private constructor. */
    private RuleFactory() {
        // empty
    }

    /** Creates a fresh node. */
    public RuleNode createNode() {
        return DefaultNode.createNode();
    }

    /** Creates a node with a given number. */
    public RuleNode createNode(int nr) {
        return DefaultNode.createNode(nr);
    }

    /** Creates a label with the given text. */
    public RuleLabel createLabel(String text) {
        return new RuleLabel(text);
    }

    @Override
    public RuleEdge createEdge(Node source, String label, Node target) {
        return createEdge(source, createLabel(label), target);
    }

    /** Creates an edge with the given source, label and target. */
    public RuleEdge createEdge(Node source, Label label, Node target) {
        return new RuleEdge((RuleNode) source, (RuleLabel) label,
            (RuleNode) target);
    }

    /** Singleton instance of this factory. */
    public final static RuleFactory INSTANCE = new RuleFactory();
}
