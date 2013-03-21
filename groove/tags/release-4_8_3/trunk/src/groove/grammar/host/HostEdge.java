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
package groove.grammar.host;

import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeLabel;
import groove.graph.Edge;

/**
 * Type of the edges of a host graph.
 * @author Arend Rensink
 */
public interface HostEdge extends Edge, HostElement, AnchorValue {
    /* Specialises the return type. */
    HostNode source();

    /* Specialises the return type. */
    HostNode target();

    /* Specialises the return type. */
    TypeLabel label();

    /* Specialises the return type. */
    TypeEdge getType();
}
