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

import groove.algebra.Constant;
import groove.algebra.SignatureKind;
import groove.graph.ElementFactory;
import groove.graph.Label;
import groove.graph.TypeEdge;
import groove.graph.TypeFactory;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.graph.TypeNode;
import groove.graph.algebra.ArgumentEdge;
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.VariableNode;

/** Factory class for graph elements. */
public class RuleFactory implements ElementFactory<RuleNode,RuleEdge> {
    /** Private constructor. */
    private RuleFactory(TypeFactory typeFactory) {
        this.typeFactory = typeFactory;
    }

    /** This implementation creates a node with top node type. */
    public RuleNode createNode(int nr) {
        return createNode(nr, TypeLabel.NODE, true);
    }

    /** Creates a node with a given number. */
    public RuleNode createNode(int nr, TypeLabel typeLabel, boolean sharp) {
        updateMaxNodeNr(nr);
        TypeNode type = this.typeFactory.getNode(typeLabel);
        return new DefaultRuleNode(nr, type, sharp);
    }

    /** Creates a variable node for a given data signature, and with a given node number. */
    public VariableNode createVariableNode(int nr, SignatureKind signature) {
        assert signature != null;
        updateMaxNodeNr(nr);
        TypeNode type = this.typeFactory.getDataType(signature);
        return new VariableNode(nr, signature, type);
    }

    /** Creates a variable node for a given data constant, and with a given node number. */
    public VariableNode createVariableNode(int nr, Constant constant) {
        updateMaxNodeNr(nr);
        TypeNode type = this.typeFactory.getDataType(constant.getSignature());
        return new VariableNode(nr, constant, type);
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
    @Override
    public RuleEdge createEdge(RuleNode source, Label label, RuleNode target) {
        RuleLabel ruleLabel = (RuleLabel) label;
        if (ruleLabel.isArgument()) {
            return new ArgumentEdge((ProductNode) source, ruleLabel,
                (VariableNode) target);
        } else if (ruleLabel.isOperator()) {
            return new OperatorEdge((ProductNode) source, ruleLabel,
                (VariableNode) target);
        } else {
            TypeLabel typeLabel = ruleLabel.getTypeLabel();
            TypeEdge type =
                typeLabel == null ? null : this.typeFactory.getEdge(
                    source.getType(), typeLabel, target.getType(), false);
            return new RuleEdge(source, ruleLabel, type, target);
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

    /** Maximises the current maximum node number with another number. */
    private void updateMaxNodeNr(int nr) {
        this.maxNodeNr = Math.max(this.maxNodeNr, nr);
    }

    /** The type factory used for creating node and edge types. */
    private final TypeFactory typeFactory;
    /** The highest node number returned by this factory. */
    private int maxNodeNr;

    /** Returns a fresh instance of this factory, without type graph. */
    public static RuleFactory newInstance() {
        return new RuleFactory(TypeFactory.instance());
    }

    /** Returns a fresh instance of this factory, for a given type graph. */
    public static RuleFactory newInstance(TypeGraph type) {
        assert type != null;
        return new RuleFactory(type.getFactory());
    }
}
