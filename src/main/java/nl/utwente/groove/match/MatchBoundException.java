/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the specific language governing
 * permissions and limitations under the License.
 *
 * $Id$
 */
package nl.utwente.groove.match;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.grammar.GrammarKey;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;

/**
 * Exception thrown when the number of matches collected for a single state
 * exceeds the bound set by the {@link GrammarKey#MATCH_BOUND} grammar
 * property. Exploration catches this exception and halts gracefully,
 * after the offending state has been flagged as an error state.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5, 2026-08")
public class MatchBoundException extends RuntimeException {
    /**
     * Constructs an exception for a given rule and bound.
     * @param ruleName qualified name of the rule whose matches exceed the bound
     * @param bound the exceeded bound, as set by the
     * {@link GrammarKey#MATCH_BOUND} grammar property
     */
    public MatchBoundException(QualName ruleName, int bound) {
        super(String
            .format("Number of matches for rule '%s' exceeds the match bound (%s=%d)", ruleName,
                    GrammarKey.MATCH_BOUND.getName(), bound));
        this.ruleName = ruleName;
        this.bound = bound;
    }

    /** Returns the qualified name of the rule whose matches exceed the bound. */
    public QualName getRuleName() {
        return this.ruleName;
    }

    private final QualName ruleName;

    /** Returns the exceeded bound. */
    public int getBound() {
        return this.bound;
    }

    private final int bound;
}
