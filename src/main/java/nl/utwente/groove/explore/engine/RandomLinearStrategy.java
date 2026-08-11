/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.explore.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.MatchResult;
import nl.utwente.groove.util.Randomness;
import nl.utwente.groove.util.Randomness.Purpose;

/**
 * Explores a single path until reaching a final state or a loop. In case of
 * abstract simulation, this implementation will prefer going along a path then
 * stopping exploration when a loop is met.
 * @author Iovka Boneva
 *
 */
@NonNullByDefault
public class RandomLinearStrategy extends LinearStrategy {
    @Override
    protected void prepare(GTS gts, @Nullable GraphState state) {
        super.prepare(gts, state);
        // obtain the generator per exploration, so that a fixed master seed
        // makes every exploration draw the identical sequence
        this.random = Randomness.newRandom(Purpose.EXPLORATION);
    }

    /** This implementation returns a random element from the set of all matches. */
    @Override
    protected @Nullable MatchResult getMatch() {
        var state = getNextState();
        assert state != null : "doNext called without hasNext";
        // collect all matches
        List<MatchResult> matches = new ArrayList<>(state.getMatches());
        // select a random match
        int matchCount = matches.size();
        if (matchCount == 0) {
            return null;
        } else {
            var random = this.random;
            assert random != null : "Strategy not prepared";
            return matches.get(random.nextInt(matchCount));
        }
    }

    /** Source of the random choices; obtained in {@link #prepare}. */
    private @Nullable Random random;
}
