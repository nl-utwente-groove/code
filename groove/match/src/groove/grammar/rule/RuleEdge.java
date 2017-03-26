/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.grammar.rule;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import groove.grammar.AnchorKind;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeGuard;
import groove.graph.AEdge;

/** Rule edge that is not attribute-related. */
public class RuleEdge extends AEdge<RuleNode,RuleLabel> implements RuleElement {
    /** Constructs a rule edge from a given rule label and/or type edge. */
    public RuleEdge(RuleNode source, RuleLabel label, TypeEdge type, RuleNode target) {
        super(source, label, target);
        assert label.getTypeLabel() == null || label.getTypeLabel()
            .equals(type.label());
        this.type = type;
        TypeGuard guard = label.getWildcardGuard();
        if (guard != null) {
            this.typeGuards = guard.isNamed() ? singletonList(guard) : emptyList();
            TypeGraph typeGraph = source.getType()
                .getGraph();
            this.matchingTypes = new HashSet<>();
            for (TypeEdge typeEdge : typeGraph.edgeSet()) {
                if (typeEdge.source()
                    .getSubtypes()
                    .contains(source.getType())
                    && typeEdge.target()
                        .getSubtypes()
                        .contains(target.getType())
                    && guard.isSatisfied(typeEdge)) {
                    this.matchingTypes.add(typeEdge);
                }
            }
        } else if (type == null) {
            this.matchingTypes = emptySet();
            this.typeGuards = emptyList();
        } else {
            this.matchingTypes = new HashSet<>(type.getSubtypes());
            this.typeGuards = emptyList();
        }
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    /**
     * Returns the (possibly {@code null}) edge type of this edge.
     */
    @Override
    public TypeEdge getType() {
        return this.type;
    }

    /**
     * Returns the optional set of possible edge types,
     * if the label of this edge is a wildcard.
     */
    @Override
    public Set<TypeEdge> getMatchingTypes() {
        return this.matchingTypes;
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
                if (guard.isNamed()) {
                    result.add(guard.getVar());
                }
            }
        }
        return result;
    }

    /** Sets the possible types of a wildcard edge. */
    public void setWildcardTypes(Set<TypeEdge> wildcardTypes) {
        assert label().isWildcard();
        this.matchingTypes.retainAll(wildcardTypes);
    }

    @Override
    public AnchorKind getAnchorKind() {
        return AnchorKind.EDGE;
    }

    /** The edge type of this rule edge. */
    private final TypeEdge type;
    /** Possibly empty list of label variables. */
    private final @NonNull List<TypeGuard> typeGuards;
    /** The (named) label variables involved in the type guards. */
    private Set<LabelVar> vars;
    /** Set of possible edge types, if the label is a wildcard. */
    private final @NonNull Set<TypeEdge> matchingTypes;

    /** Convenience method to assert non-nullness of singleton set. */
    private static <T> @NonNull List<T> singletonList(T element) {
        return Collections.singletonList(element);
    }

    /** Convenience method to assert non-nullness of empty set. */
    private static <T> @NonNull Set<T> emptySet() {
        return Collections.emptySet();
    }

    /** Convenience method to assert non-nullness of empty list. */
    private static <T> @NonNull List<T> emptyList() {
        return Collections.emptyList();
    }
}
