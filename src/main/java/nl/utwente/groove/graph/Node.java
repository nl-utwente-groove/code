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
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.AIGenerated;

/**
 * Interface of a graph node. A node is a graph element that is not a composite.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:58 $
 */
@NonNullByDefault
public interface Node extends Element {
    /**
     * Returns the node number.
     * Within a given graph, the node number, together
     * with its actual type, uniquely defines the node.
     */
    public int getNumber();

    /**
     * Returns an object seeding the isomorphism certificate of this node,
     * or {@code null} if the node carries no seed.
     * The seed determines the initial certificate value, and nodes with
     * non-equal seeds are never considered symmetric.
     * This callback allows node implementations to refine isomorphism
     * checking without the certificate strategies knowing their type.
     */
    @AIGenerated("Claude Fable 5, 2026-08")
    default public @Nullable Object certificateSeed() {
        return null;
    }

    /**
     * Indicates if this node is completely identified by its certificate seed,
     * meaning that two nodes may be related by isomorphism if and only if
     * their seeds are equal, regardless of the surrounding graph structure.
     * This is the case for nodes whose identity is fixed across graphs, such
     * as data value nodes. If {@code true}, {@link #certificateSeed()} must
     * return a non-{@code null} value.
     */
    @AIGenerated("Claude Fable 5, 2026-08")
    default public boolean hasIdentityCertificate() {
        return false;
    }
}
