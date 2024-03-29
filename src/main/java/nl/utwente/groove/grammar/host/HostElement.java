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

import nl.utwente.groove.grammar.type.TypeElement;
import nl.utwente.groove.graph.Element;

/**
 * Type of (node and edge) elements that may appear in a {@link HostGraph}.
 * Super-interface of {@link HostNode}s and {@link HostEdge}s.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface HostElement extends Element {
    /**
     * Returns the (non-{@code null}) type of this element.
     */
    public TypeElement getType();
}
