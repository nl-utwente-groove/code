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
import groove.graph.TypeLabel;

/** Factory class for graph elements. */
public class HostFactory implements ElementFactory<HostNode,TypeLabel,HostEdge> {
    /** Private constructor. */
    protected HostFactory() {
        // empty
    }

    /** Creates a fresh node. */
    public HostNode createNode() {
        return DefaultNode.createNode();
    }

    /** Creates a node with a given number. */
    public HostNode createNode(int nr) {
        return DefaultNode.createNode(nr);
    }

    /** Creates a label with the given text. */
    public TypeLabel createLabel(String text) {
        return TypeLabel.createLabel(text);
    }

    @Override
    public HostEdge createEdge(Node source, String label, Node target) {
        return createEdge(source, createLabel(label), target);
    }

    /** Creates an edge with the given source, label and target. */
    public HostEdge createEdge(Node source, Label label, Node target) {
        return HostEdge.createEdge((HostNode) source, (TypeLabel) label,
            (HostNode) target);
    }

    /** Creates a fresh mapping from rules to (this type of) host graph. */
    public RuleToHostMap createRuleToHostMap() {
        return new RuleToHostMap();
    }

    /** Singleton instance of this factory. */
    public final static HostFactory INSTANCE = new HostFactory();
}
