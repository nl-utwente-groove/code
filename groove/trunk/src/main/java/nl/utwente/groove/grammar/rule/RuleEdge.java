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
package nl.utwente.groove.grammar.rule;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.AnchorKind;
import nl.utwente.groove.grammar.type.TypeEdge;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.grammar.type.TypeGuard;
import nl.utwente.groove.graph.AEdge;

/** Rule edge that is not attribute-related. */
@NonNullByDefault
public class RuleEdge extends AEdge<RuleNode,RuleLabel> implements RuleElement {
    /** Constructs a rule edge from a given rule label and/or type edge. */
    public RuleEdge(RuleNode source, RuleLabel label, @Nullable TypeEdge type, RuleNode target) {
        super(source, label, target);
        var tl = label.getTypeLabel();
        assert tl == null || type != null && tl.equals(type.label());
        this.type = type;
        TypeGuard guard = label.getWildcardGuard();
        if (guard != null) {
            this.typeGuards = guard.isNamed()
                ? singletonList(guard)
                : emptyList();
            TypeGraph typeGraph = source.getType().getGraph();
            this.matchingTypes = new HashSet<>();
            for (TypeEdge typeEdge : typeGraph.edgeSet()) {
                if (typeEdge.source().getSubtypes().contains(source.getType())
                    && typeEdge.target().getSubtypes().contains(target.getType())
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
    public @Nullable TypeEdge getType() {
        return this.type;
    }

    /** The edge type of this rule edge. */
    private final @Nullable TypeEdge type;

    /**
     * Returns the optional set of possible edge types,
     * if the label of this edge is a wildcard.
     */
    @Override
    public Set<TypeEdge> getMatchingTypes() {
        return this.matchingTypes;
    }

    /** Set of possible edge types, if the label is a wildcard. */
    private final Set<TypeEdge> matchingTypes;

    @Override
    public List<TypeGuard> getTypeGuards() {
        return this.typeGuards;
    }

    /** Possibly empty list of label variables. */
    private final List<TypeGuard> typeGuards;

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

    /** The (named) label variables involved in the type guards. */
    private @Nullable Set<LabelVar> vars;

    /** Sets the possible types of a wildcard edge. */
    public void setWildcardTypes(Set<TypeEdge> wildcardTypes) {
        assert label().isWildcard();
        this.matchingTypes.retainAll(wildcardTypes);
    }

    @Override
    public AnchorKind getAnchorKind() {
        return AnchorKind.EDGE;
    }

    /** Convenience method to assert non-nullness of singleton set. */
    private static <T> List<T> singletonList(T element) {
        return Collections.singletonList(element);
    }

    /** Convenience method to assert non-nullness of empty set. */
    private static <T> Set<T> emptySet() {
        return Collections.emptySet();
    }

    /** Convenience method to assert non-nullness of empty list. */
    private static <T> List<T> emptyList() {
        return Collections.emptyList();
    }
}
