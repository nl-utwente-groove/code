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
package groove.trans;

import groove.graph.Edge;
import groove.graph.TypeEdge;

import java.util.Collections;
import java.util.Set;

/**
 * Supertype of all edges that may appear in a {@link RuleGraph}.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface RuleEdge extends Edge, RuleElement {
    /** Specialises the return type. */
    RuleNode source();

    /** Specialises the return type. */
    RuleNode target();

    /** Specialises the return type. */
    RuleLabel label();

    /** Specialises the return type. */
    TypeEdge getType();

    /** Fixed global empty set of matching types. */
    public final static Set<TypeEdge> EMPTY_MATCH_SET = Collections.emptySet();
}
