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
package groove.grammar.rule;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;

import groove.grammar.UnitPar.RulePar;
import groove.grammar.aspect.AspectKind;
import groove.grammar.type.TypeNode;
import groove.graph.Node;

/**
 * Supertype of all nodes that can occur in a {@link RuleGraph}.
 * @author Arend Rensink
 * @version $Revision $
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
    public boolean isSharp();

    /* Specialises the return type. */
    @Override
    public Set<TypeNode> getMatchingTypes();

    /** Tests if the matching types and type guards of this node
     * equal that of another. (This is not covered by #equals).
     */
    public boolean stronglyEquals(RuleNode other);

    /** Fixed global empty set of matching types. */
    final static Set<TypeNode> EMPTY_MATCH_SET = Collections.emptySet();
}
