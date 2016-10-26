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
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import groove.grammar.type.TypeNode;
import groove.graph.Node;

/**
 * Supertype of all nodes that can occur in a {@link RuleGraph}.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface RuleNode extends Node, RuleElement {
    /* Specialises the return type. */
    @Override
    public TypeNode getType();

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
    final static @NonNull Set<TypeNode> EMPTY_MATCH_SET = Collections.emptySet();
}
