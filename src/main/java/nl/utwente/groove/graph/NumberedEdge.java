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

/**
 * Interface for edges that carry an identifying edge number.
 * Edge numbers are factory-scoped: within the {@link StoreFactory}
 * that created it, the number uniquely identifies the edge.
 * Edges without this interface are identified by their content
 * (source, label and target) instead.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public interface NumberedEdge extends Edge {
    /**
     * Returns the edge number.
     * Within the factory that created it, the edge number, together
     * with its actual type, uniquely defines the edge.
     */
    public int getNumber();
}
