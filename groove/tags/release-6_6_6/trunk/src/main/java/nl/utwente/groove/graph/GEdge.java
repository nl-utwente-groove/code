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
 * Generically typed specialisation of the {@link Edge} interface.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public interface GEdge<N extends Node> extends Edge {
    /*
     * Specialises the return type.
     */
    @Override
    public N source();

    /*
     * Specialises the return type.
     */
    @Override
    public N target();

    @Override
    default boolean hasRole(EdgeRole role) {
        return getRole() == role;
    }
}