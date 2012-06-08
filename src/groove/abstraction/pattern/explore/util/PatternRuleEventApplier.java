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
package groove.abstraction.pattern.explore.util;

import groove.abstraction.pattern.lts.PGTS;
import groove.abstraction.pattern.lts.PatternState;
import groove.abstraction.pattern.match.Match;
import groove.explore.util.RuleEventApplier;

/**
 * See {@link RuleEventApplier}.
 */
public interface PatternRuleEventApplier {
    /**
     * Returns the underlying PGTS.
     */
    PGTS getPGTS();

    /**
     * Adds a transition to the GTS, from a given source state and for a given
     * rule event. The event is assumed not to have been explored yet.
     */
    public void apply(PatternState source, Match match);

}