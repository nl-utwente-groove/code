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
package groove.abstraction.pattern.lts;

import groove.abstraction.pattern.match.Match;
import groove.abstraction.pattern.trans.PatternRule;
import groove.control.instance.Step;

/** Class wrapping a match together with a corresponding control transition. */
public class MatchResult {
    /** Constructs an object from a given match and (possibly {@code null}) control step. */
    public MatchResult(Match match, Step step) {
        this.match = match;
        this.step = step;
    }

    /** Returns the match part of this result. */
    public Match getMatch() {
        return this.match;
    }

    /** Returns the underlying rule of this match result. */
    public PatternRule getRule() {
        return getMatch().getRule();
    }

    /** Returns the control step of this result. */
    public Step getStep() {
        return this.step;
    }

    private final Match match;
    private final Step step;
}
