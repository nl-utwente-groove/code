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
package nl.utwente.groove.graph;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.grammar.host.ValueNode;

/**
 * Generic command interface to deal with graph changes.
 */
@NonNullByDefault
public interface GDeltaTarget<N extends Node,E extends GEdge<N>> {
    /** Callback method invoked to indicate that a node is to be added.
     * If the node is not a {@link ValueNode}, it is required to be fresh.
     * @return {@code true} if the node was added (which can only fail to be true
     * if the node is a {@link ValueNode})
     */
    public boolean addNode(N node);

    /** Callback method invoked to indicate that a node is to be removed.
     * @return {@code true} if the node was removed
     */
    public boolean removeNode(N node);

    /** Callback method invoked to indicate that an edge is to be added.
     * The edge is required to be fresh.
     * @return always {@code true}
     */
    public boolean addEdge(E edge);

    /** Callback method invoked to indicate that an edge is to be removed.
     * @return {@code true} if the edge was removed
     */
    public boolean removeEdge(E edge);
}