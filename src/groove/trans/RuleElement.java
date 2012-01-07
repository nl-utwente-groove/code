/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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

import groove.graph.Element;
import groove.graph.TypeElement;
import groove.graph.TypeGuard;
import groove.rel.LabelVar;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Type of (node and edge) elements that may appear in a {@link RuleGraph}.
 * Super-interface of {@link RuleNode}s and {@link RuleEdge}s.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface RuleElement extends Element {
    /** Returns the type of this rule element. */
    public TypeElement getType();

    /** Returns the set of label variables associated with this rule element. */
    public Set<LabelVar> getVars();

    /** Returns the collection of (named) type guards associated with this rule element. */
    public List<TypeGuard> getTypeGuards();

    /** 
     * Returns the set of type elements that are valid matches of this rule element.
     * This is typically the set of subtypes of {@link #getType()}, but it may be 
     * further constrained by type variables.
     * @see #getType()
     */
    public Set<? extends TypeElement> getMatchingTypes();

    /** Fixed global empty set of label variables. */
    final static Set<LabelVar> EMPTY_VAR_SET = Collections.emptySet();
    /** Fixed global empty set of label variables. */
    final static List<TypeGuard> EMPTY_GUARD_LIST = Collections.emptyList();
}
