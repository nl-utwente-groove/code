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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import groove.grammar.AnchorKind;
import groove.grammar.UnitPar.RulePar;
import groove.grammar.type.TypeGuard;
import groove.grammar.type.TypeNode;
import groove.graph.ANode;

/**
 * Default implementation of a graph node. Default nodes have numbers, but node
 * equality is determined by object identity and not by node number.
 * @author Arend Rensink
 * @version $Revision$
 */
public class DefaultRuleNode extends ANode implements RuleNode, AnchorKey {
    /**
     * Constructs a fresh node, with an explicitly given number and node type.
     * @param nr the number for this node
     * @param type the node type; may be {@code null}
     * @param sharp if {@code true}, the node is sharply typed
     * @param typeGuards collection of named and unnamed type guards for this node
     */
    protected DefaultRuleNode(int nr, @NonNull TypeNode type, boolean sharp,
        List<TypeGuard> typeGuards) {
        super(nr);
        assert type != null : "Can't instantiate untyped rule node";
        this.type = type;
        this.sharp = sharp;
        if (typeGuards == null) {
            this.typeGuards = Collections.emptyList();
            this.matchingTypes = type.getSubtypes();
        } else {
            this.typeGuards = new ArrayList<>();
            this.matchingTypes = new HashSet<>();
            if (sharp) {
                this.matchingTypes.add(type);
            } else {
                this.matchingTypes.addAll(type.getSubtypes());
            }
            // restrict the matching types to those that satisfy all label guards
            for (TypeGuard guard : typeGuards) {
                guard.filter(this.matchingTypes);
                if (guard.isNamed()) {
                    this.typeGuards.add(guard);
                }
            }
        }
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
    }

    @Override
    public Optional<RulePar> getPar() {
        return Optional.ofNullable(this.par);
    }

    private RulePar par;

    /**
     * Returns a string consisting of the letter <tt>'n'</tt>.
     */
    @Override
    public String getToStringPrefix() {
        return "n";
    }

    @Override
    public TypeNode getType() {
        return this.type;
    }

    @Override
    public List<TypeGuard> getTypeGuards() {
        return this.typeGuards;
    }

    @Override
    public Set<LabelVar> getVars() {
        Set<LabelVar> result = this.vars;
        if (result == null) {
            result = this.vars = new HashSet<>();
            for (TypeGuard guard : getTypeGuards()) {
                assert guard.isNamed();
                result.add(guard.getVar());
            }
        }
        return result;
    }

    @Override
    public @NonNull Set<@NonNull TypeNode> getMatchingTypes() {
        return this.matchingTypes;
    }

    @Override
    public boolean isSharp() {
        return this.sharp;
    }

    @Override
    public AnchorKind getAnchorKind() {
        return AnchorKind.NODE;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        DefaultRuleNode other = (DefaultRuleNode) obj;
        return getType().equals(other.getType());
    }

    @Override
    protected int computeHashCode() {
        int result = super.computeHashCode();
        int prime = 31;
        result = prime * result + getType().hashCode();
        return result;
    }

    @Override
    public boolean stronglyEquals(RuleNode other) {
        if (this == other) {
            return true;
        }
        if (!equals(other)) {
            return false;
        }
        if (!getTypeGuards().equals(other.getTypeGuards())) {
            return false;
        }
        if (!getMatchingTypes().equals(other.getMatchingTypes())) {
            return false;
        }
        return true;
    }

    /** Flag indicating if this node is sharply typed. */
    private final boolean sharp;
    /** The (possibly {@code null}) type of this rule node. */
    private final @NonNull TypeNode type;
    /** The list of type guards associated with this node. */
    private final @NonNull List<TypeGuard> typeGuards;
    /** The (named) label variables involved in the type guards. */
    private Set<LabelVar> vars;
    /** The set of matching node types. */
    private final @NonNull Set<@NonNull TypeNode> matchingTypes;
}
