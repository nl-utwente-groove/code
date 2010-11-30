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
package groove.explore.util;

import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.MatchResult;

/**
 * Interface wrapping the functionality to apply a rule event to a 
 * graph state, resulting in a new transition, which is added to the
 * underlying GTS.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface AltRuleEventApplier {
    /**
     * Returns the underlying GTS.
     */
    GTS getGTS();

    /**
     * Adds a transition to the GTS, from a given source state and for a given
     * rule event. The event is assumed not to have been explored yet.
     * @return the added (new) transition
     */
    public GraphTransition apply(GraphState source, MatchResult event);

}