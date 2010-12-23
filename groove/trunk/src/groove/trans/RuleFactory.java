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

import groove.graph.DefaultFactory;
import groove.graph.ElementFactory;
import groove.graph.algebra.ArgumentEdge;
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.VariableNode;

/** Factory class for graph elements. */
public class RuleFactory implements ElementFactory<RuleNode,RuleLabel,RuleEdge> {
    /** Private constructor. */
    private RuleFactory() {
        // empty
    }

    /** Creates a node with a given number. */
    public RuleNode createNode(int nr) {
        this.maxNodeNr = Math.max(this.maxNodeNr, nr);
        return defaultFactory.createNode(nr);
    }

    /** Creates a label with the given text. */
    public RuleLabel createLabel(String text) {
        return new RuleLabel(text);
    }

    @Override
    public RuleEdge createEdge(RuleNode source, String text, RuleNode target) {
        return createEdge(source, createLabel(text), target);
    }

    /** Creates an edge with the given source, label and target. */
    public RuleEdge createEdge(RuleNode source, RuleLabel label, RuleNode target) {
        if (label.isArgument()) {
            return new ArgumentEdge((ProductNode) source, label,
                (VariableNode) target);
        } else if (label.isOperator()) {
            return new OperatorEdge((ProductNode) source, label,
                (VariableNode) target);
        } else {
            return new RuleEdge(source, label, target);
        }
    }

    @Override
    public RuleGraphMorphism createMorphism() {
        return new RuleGraphMorphism();
    }

    @Override
    public int getMaxNodeNr() {
        return this.maxNodeNr;
    }

    /** The highest node number returned by this factory. */
    private int maxNodeNr;

    /** Returns the singleton instance of this factory. */
    public static RuleFactory instance() {
        return INSTANCE;
    }

    /** Singleton instance of this factory. */
    private final static RuleFactory INSTANCE = new RuleFactory();
    /** Factory for default nodes. */
    private final static DefaultFactory defaultFactory =
        DefaultFactory.instance();
}
