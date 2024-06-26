/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.grammar.rule;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.algebra.Operator;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.grammar.host.DefaultHostNode;
import nl.utwente.groove.grammar.type.TypeEdge;
import nl.utwente.groove.grammar.type.TypeFactory;
import nl.utwente.groove.grammar.type.TypeGuard;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.graph.ElementFactory;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.graph.NodeFactory;

/** Factory class for graph elements. */
public class RuleFactory extends ElementFactory<RuleNode,RuleEdge> {
    /** Private constructor. */
    private RuleFactory(TypeFactory typeFactory) {
        this.typeFactory = typeFactory;
    }

    /* This implementation creates a node with top node type. */
    @Override
    protected RuleNode newNode(int nr) {
        return getTopNodeFactory().newNode(nr);
    }

    @Override
    protected boolean isAllowed(RuleNode node) {
        return node.getType().isTopType() && !node.isSharp() && node.getTypeGuards().isEmpty();
    }

    /** Returns the fixed node factory for the top type. */
    private RuleNodeFactory getTopNodeFactory() {
        if (this.topNodeFactory == null) {
            this.topNodeFactory
                = (RuleNodeFactory) nodes(getTypeFactory().getTopNode(), false, true, null);
        }
        return this.topNodeFactory;
    }

    private RuleNodeFactory topNodeFactory;

    /** Returns a node factory for typed default rule nodes.
     * @param declared flag indicating if the node type is explicitly declared (see {@link RuleNode#isDeclared()}). */
    public NodeFactory<RuleNode> nodes(@NonNull TypeNode type, boolean declared, boolean sharp,
                                       List<TypeGuard> typeGuards) {
        return new RuleNodeFactory(type, declared, sharp, typeGuards);
    }

    /** Creates a variable node for a given algebra term, and with a given node number. */
    public VariableNode createVariableNode(int nr, Expression term) {
        TypeNode type = getTypeFactory().getDataType(term.getSort());
        VariableNode result = new VariableNode(nr, term, type);
        registerNode(result);
        return result;
    }

    /** Creates an operator node for a given node number and arity. */
    public OperatorNode createOperatorNode(int nr, Operator operator, List<VariableNode> arguments,
                                           VariableNode target) {
        OperatorNode result = new OperatorNode(nr, operator, arguments, target);
        registerNode(result);
        return result;
    }

    /** Creates a label with the given text. */
    @Override
    public RuleLabel createLabel(String text) {
        return new RuleLabel(text);
    }

    /** Gets the appropriate type edge from the type factory. */
    @Override
    public RuleEdge createEdge(RuleNode source, Label label, RuleNode target) {
        RuleLabel ruleLabel = (RuleLabel) label;
        TypeLabel typeLabel = ruleLabel.getTypeLabel();
        TypeEdge type = typeLabel == null
            ? null
            : getTypeFactory().createEdge(source.getType(), typeLabel, target.getType(), false);
        return new RuleEdge(source, ruleLabel, type, target);
    }

    @Override
    public RuleGraphMorphism createMorphism() {
        return new RuleGraphMorphism();
    }

    /** Returns the type factory used by this rule factory. */
    public TypeFactory getTypeFactory() {
        return this.typeFactory;
    }

    /** The type factory used for creating node and edge types. */
    private final TypeFactory typeFactory;

    /** Returns a fresh instance of this factory, without type graph. */
    public static RuleFactory newInstance() {
        return newInstance(TypeFactory.newInstance());
    }

    /** Returns a fresh instance of this factory, for a given type graph. */
    public static RuleFactory newInstance(TypeFactory typeFactory) {
        return new RuleFactory(typeFactory);
    }

    /** Factory for (typed) {@link DefaultHostNode}s. */
    protected class RuleNodeFactory extends DependentNodeFactory {
        /** Constructor for subclassing.
         * @param declared flag indicating if the generated nodes are explicitly typed;
         * see RuleNode#isDeclared()
         */
        protected RuleNodeFactory(@NonNull TypeNode type, boolean declared, boolean sharp,
                                  List<TypeGuard> typeGuards) {
            this.type = type;
            this.declared = declared;
            this.sharp = sharp;
            this.typeGuards = typeGuards;
        }

        @Override
        protected boolean isAllowed(RuleNode node) {
            return node.getType() == this.type && node.isSharp() == this.sharp
                && node.getTypeGuards().equals(this.typeGuards);
        }

        @Override
        protected RuleNode newNode(int nr) {
            return new DefaultRuleNode(nr, this.type, this.declared, this.sharp, this.typeGuards);
        }

        /** Returns the type wrapped into this factory. */
        protected TypeNode getType() {
            return this.type;
        }

        private final @NonNull TypeNode type;
        private final boolean sharp;
        private final boolean declared;
        private final List<TypeGuard> typeGuards;
    }
}
