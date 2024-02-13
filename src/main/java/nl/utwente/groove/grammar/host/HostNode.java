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
package nl.utwente.groove.grammar.host;

import java.util.Comparator;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.graph.Node;

/**
 * Supertype of all nodes that can occur in a {@link DefaultHostGraph}.
 * These are {@link DefaultHostNode}s and {@link ValueNode}s.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public sealed interface HostNode extends Node, HostElement, AnchorValue
    permits DefaultHostNode, ValueNode {
    /** Returns the type of the host node, or {@code null} if
     * the host node is untyped. */
    @Override
    public TypeNode getType();

    /** Host node comparator, based on their {@link #toString()} value. */
    static Comparator<HostNode> COMPARATOR = (h1, h2) -> h1.toString().compareTo(h2.toString());
}
