/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
import groove.abstraction.pattern.lts.PatternGraphNextState;
import groove.abstraction.pattern.lts.PatternGraphState;
import groove.abstraction.pattern.lts.PatternGraphTransition;
import groove.abstraction.pattern.lts.PatternNextState;
import groove.abstraction.pattern.lts.PatternState;
import groove.abstraction.pattern.lts.PatternTransition;
import groove.abstraction.pattern.match.Match;
import groove.abstraction.pattern.shape.PatternGraph;
import groove.abstraction.pattern.trans.PatternGraphRuleApplication;
import groove.explore.util.MatchApplier;

/**
 * Match applier for pattern graph transformation.
 * 
 * See {@link MatchApplier}. 
 */
public class PatternGraphMatchApplier implements PatternRuleEventApplier {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** The underlying PGTS. */
    private final PGTS pgts;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Creates an applier for a given pattern graph transition system. */
    public PatternGraphMatchApplier(PGTS pgts) {
        this.pgts = pgts;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public void apply(PatternState source, Match match) {
        PatternGraphRuleApplication app =
            new PatternGraphRuleApplication(source.getGraph(), match);
        PatternGraph result = app.transform(false);
        PatternNextState newState =
            new PatternGraphNextState(result, (PatternGraphState) source,
                this.pgts.getNextStateNr(), this.pgts, match);
        PatternState oldState = this.pgts.addState(newState);
        PatternTransition trans = null;
        if (oldState != null) {
            // The state was not added as an equivalent state existed.
            trans = new PatternGraphTransition(source, match, oldState);
        } else {
            // The state was added as a next-state.
            trans = newState;
        }
        this.pgts.addTransition(trans);
    }

}
