/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.grammar.rule;

import java.util.List;

import nl.utwente.groove.algebra.Operator;
import nl.utwente.groove.grammar.AnchorKind;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.graph.ANode;
import nl.utwente.groove.graph.EdgeRole;

/**
 * Instances of this class represent operator invocations.
 */
public class OperatorNode extends ANode implements RuleNode {
    /**
     * Returns a fresh operator node with a given node number,
     * operator, arguments and target.
     */
    public OperatorNode(int nr, Operator operator, List<VariableNode> arguments,
                        VariableNode target) {
        super(nr);
        this.operator = operator;
        this.arguments = arguments;
        this.target = target;
    }

    /** Retrieves the list of arguments of the operator node. */
    public List<VariableNode> getArguments() {
        return this.arguments;
    }

    /**
     * The list of arguments of this product node (which are the value nodes to
     * which an outgoing AlgebraEdge is pointing).
     */
    private final List<VariableNode> arguments;

    /** Convenience method indicating that the wrapped operator is a set operator. */
    public boolean isSetOperator() {
        return getOperator().isVarArgs();
    }

    /**
     * Returns the arity of this node.
     */
    public int arity() {
        return this.arguments.size();
    }

    /** Returns the set of variable nodes used as targets of the operations. */
    public VariableNode getTarget() {
        return this.target;
    }

    private final VariableNode target;

    /** Returns the operations associated with this node. */
    public Operator getOperator() {
        return this.operator;
    }

    private final Operator operator;

    @Override
    public String getToStringPrefix() {
        return TO_STRING_PREFIX;
    }

    @Override
    public TypeNode getType() {
        return getTarget().getType();
    }

    @Override
    public AnchorKind getAnchorKind() {
        return AnchorKind.NODE;
    }

    /**
     * This class does not guarantee unique representatives for the same number,
     * so we need to override {@link #equals(Object)}.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof OperatorNode other)) {
            return false;
        }
        if (getNumber() != other.getNumber()) {
            return false;
        }
        if (!getOperator().equals(other.getOperator())) {
            return false;
        }
        if (!getTarget().equals(other.getTarget())) {
            return false;
        }
        return true;
    }

    @Override
    protected int computeHashCode() {
        int result = super.computeHashCode();
        final int prime = 31;
        result = result * prime + getOperator().hashCode();
        result = result * prime + getTarget().hashCode();
        return result;
    }

    @Override
    public boolean stronglyEquals(RuleNode other) {
        return equals(other);
    }

    static final private char TIMES_CHAR = '\u2a09';
    /** Type label of product nodes. */
    @SuppressWarnings("unused")
    static private final TypeLabel PROD_LABEL
        = TypeLabel.createLabel(EdgeRole.NODE_TYPE, "" + TIMES_CHAR);
    /** Prefix for product node IDs. */
    static public final String TO_STRING_PREFIX = "p";
}
