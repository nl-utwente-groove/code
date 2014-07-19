/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
 * $Id: ValueNode.java,v 1.10 2008-02-12 15:15:32 fladder Exp $
 */
package groove.grammar.rule;

import groove.algebra.Constant;
import groove.algebra.SignatureKind;
import groove.algebra.syntax.Expression;
import groove.algebra.syntax.Variable;
import groove.grammar.AnchorKind;
import groove.grammar.type.TypeGuard;
import groove.grammar.type.TypeNode;
import groove.graph.ANode;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Nodes used to represent attribute variables in rules and conditions.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-12 15:15:32 $
 */
public class VariableNode extends ANode implements RuleNode, AnchorKey {
    /**
     * Constructs a (numbered) constant variable node.
     */
    public VariableNode(int nr, Expression term, TypeNode type) {
        super(nr);
        this.term = term;
        assert type != null;
        this.type = type;
    }

    /**
     * This methods returns description of the variable, based on its number.
     */
    @Override
    public String toString() {
        if (getConstant() == null) {
            return super.toString();
        } else {
            return getConstant().toString();
        }
    }

    @Override
    protected String getToStringPrefix() {
        return TO_STRING_PREFIX;
    }

    /** Nodes are now not canonical, so we need to test for the numbers and classes. */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof VariableNode)) {
            return false;
        }
        VariableNode other = (VariableNode) obj;
        return getNumber() == other.getNumber();
    }

    @Override
    public boolean stronglyEquals(RuleNode other) {
        return equals(other);
    }

    /**
     * Returns the (non-{@code null}) signature to which the variable node
     * belongs.
     */
    public SignatureKind getSignature() {
        return this.term.getSignature();
    }

    /**
     * Indicates if this variable node has an associate constant.
     * If it does not have a constant, it has a variable.
     */
    public boolean hasConstant() {
        return this.term instanceof Constant;
    }

    /**
     * Returns the term wrapped in this variable node.
     */
    public Expression getTerm() {
        return this.term;
    }

    /**
     * Returns the constant of the variable node 
     * if its wrapped term is a constant; otherwise returns {@code null}.
     */
    public Constant getConstant() {
        return hasConstant() ? (Constant) getTerm() : null;
    }

    /**
     * Returns the variable of the variable node 
     * if its wrapped term is a variable; otherwise returns {@code null}.
     */
    public Variable getVariable() {
        return (getTerm() instanceof Variable) ? null : (Variable) getTerm();
    }

    @Override
    public TypeNode getType() {
        return this.type;
    }

    @Override
    public AnchorKind getAnchorKind() {
        return AnchorKind.NODE;
    }

    @Override
    public List<TypeGuard> getTypeGuards() {
        return EMPTY_GUARD_LIST;
    }

    @Override
    public Set<LabelVar> getVars() {
        return EMPTY_VAR_SET;
    }

    @Override
    public Set<TypeNode> getMatchingTypes() {
        return Collections.singleton(this.type);
    }

    @Override
    public boolean isSharp() {
        return true;
    }

    /** The type of this variable node. */
    private final TypeNode type;
    /** Term (constant or variable) associated with this variable node. */
    private final Expression term;

    /** returns the string preceding the node number in the default variable node id. */
    static public final String TO_STRING_PREFIX = "x";
    /** Predefined empty list of type guards. */
    static private final List<TypeGuard> EMPTY_GUARD_LIST = Collections.emptyList();
    /** Predefined empty list of type guards. */
    static private final Set<LabelVar> EMPTY_VAR_SET = Collections.emptySet();
}
