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
import groove.abstraction.pattern.trans.PatternRuleApplication;
import groove.explore.util.MatchApplier;

/**
 * See {@link MatchApplier}. 
 */
public class PatternGraphMatchApplier implements PatternRuleEventApplier {

    /** The underlying PGTS. */
    private final PGTS pgts;

    /**
     * Creates an applier for a given graph transition system.
     */
    public PatternGraphMatchApplier(PGTS pgts) {
        this.pgts = pgts;
    }

    @Override
    public PGTS getPGTS() {
        return this.pgts;
    }

    @Override
    public void apply(PatternState source, Match match) {
        PatternRuleApplication app =
            new PatternRuleApplication(source.getGraph(), match);
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
