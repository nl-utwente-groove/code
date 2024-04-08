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

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.grammar.UnitPar.RulePar;
import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.graph.Node;

/**
 * Supertype of all nodes that can occur in a {@link RuleGraph}.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public interface RuleNode extends Node, RuleElement {
    /** Returns the optional ID of this rule node.
     * This is the {@link AspectKind#ID}-value if any;
     * it defaults to the {@link #toString()}-value.
     */
    default public String getId() {
        return toString();
    }

    /** Sets the optional rule parameter associated with this rule node. */
    default public void setPar(RulePar par) {
        throw new UnsupportedOperationException();
    }

    /** Returns the optional rule parameter associated with this rule node. */
    default public Optional<RulePar> getPar() {
        return Optional.empty();
    }

    /* Specialises the return type. */
    @Override
    public @NonNull TypeNode getType();

    /**
     * Indicates if the rule node is sharply typed.
     * Returns {@code false} if the node is untyped.
     */
    default public boolean isSharp() {
        return true;
    }

    /** Indicates if the node type is declared explicitly, by a type label.
     * (If not, it is determined by type variables).
     */
    default public boolean isDeclared() {
        return true;
    }

    /* Specialises the return type. */
    @Override
    default public Set<TypeNode> getMatchingTypes() {
        return Collections.singleton(getType());
    }

    /** Indicates if this rule node is correctly typed by a given node type.
     * This is the case if this node has a declared type that is a subtype of
     * the given node type, or, if this node type is not declared, if any of the
     * matching node types is a subtype of the given node type.
     */
    default public boolean isTypedBy(TypeNode type) {
        var subtypes = type.getSubtypes();
        if (isDeclared()) {
            return subtypes.contains(getType());
        } else {
            return getMatchingTypes().stream().anyMatch(subtypes::contains);
        }
    }

    /** Tests if the matching types and type guards of this node
     * equal that of another. (This is not covered by #equals).
     */
    public boolean stronglyEquals(RuleNode other);
}
