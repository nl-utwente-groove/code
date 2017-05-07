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
 * $Id$
 */
package groove.grammar.rule;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import groove.algebra.Constant;
import groove.algebra.Sort;
import groove.algebra.syntax.Expression;
import groove.algebra.syntax.Variable;
import groove.grammar.AnchorKind;
import groove.grammar.UnitPar.RulePar;
import groove.grammar.type.TypeGuard;
import groove.grammar.type.TypeNode;
import groove.graph.ANode;

/**
 * Nodes used to represent attribute variables and values in rules and conditions.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-12 15:15:32 $
 */
public class VariableNode extends ANode implements RuleNode, AnchorKey {
    /**
     * Constructs a (numbered) variable node.
     * @param nr the node number; uniquely identifies the node
     * @param term a {@link Constant} or {@link Variable} characterising the node
     * @param type the corresponding type node
     */
    public VariableNode(int nr, Expression term, TypeNode type) {
        super(nr);
        this.term = term;
        assert type != null && type.isDataType();
        this.type = type;
    }

    /** Sets the special ID of this rule node. */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id == null ? toString() : this.id;
    }

    /** The optional special ID of this rule node. */
    private String id;

    @Override
    public void setPar(RulePar par) {
        this.par = par;
        setId(par.toString());
    }

    @Override
    public Optional<RulePar> getPar() {
        return Optional.ofNullable(this.par);
    }

    private RulePar par;

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
     * Returns the (non-{@code null}) sort to which the variable node
     * belongs.
     */
    public Sort getSort() {
        return this.term.getSort();
    }

    /**
     * Returns the term (a {@link Constant} or {@link Variable}) wrapped in this variable node.
     */
    public Expression getTerm() {
        return this.term;
    }

    /**
     * Indicates if this variable node has an associate constant.
     * If it does not have a constant, it has a variable.
     */
    public boolean hasConstant() {
        return this.term instanceof Constant;
    }

    /**
     * Returns the constant of the variable node.
     * if its wrapped term is a constant; otherwise returns {@code null}.
     */
    public Constant getConstant() {
        return hasConstant() ? (Constant) getTerm() : null;
    }

    /**
     * Returns the variable of the variable node.
     * if its wrapped term is a variable; otherwise returns {@code null}.
     */
    public Variable getVariable() {
        return hasConstant() ? null : (Variable) getTerm();
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
        return EMPTY_NODES_SET;
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
    /** Predefined empty list of matching types. */
    static private final @NonNull Set<TypeNode> EMPTY_NODES_SET = Collections.emptySet();
    /** Predefined empty list of type guards. */
    static private final @NonNull List<TypeGuard> EMPTY_GUARD_LIST = Collections.emptyList();
    /** Predefined empty list of type guards. */
    static private final @NonNull Set<LabelVar> EMPTY_VAR_SET = Collections.emptySet();
}
