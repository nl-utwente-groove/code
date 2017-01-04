/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2010
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
package groove.explore.strategy;

import java.util.ArrayList;
import java.util.List;

import groove.lts.MatchResult;

/**
 * Explores a single path until reaching a final state or a loop. In case of
 * abstract simulation, this implementation will prefer going along a path then
 * stopping exploration when a loop is met.
 * @author Amir Hossein Ghamarian
 *
 */
public class ReteRandomLinearStrategy extends ReteLinearStrategy {
    /** This implementation returns a random element from the set of all matches. */
    @Override
    protected MatchResult getMatch() {
        // collect all matches
        List<MatchResult> matches = new ArrayList<>(getNextState().getMatches());
        // select a random match
        int matchCount = matches.size();
        if (matchCount == 0) {
            return null;
        } else {
            int randomIndex = (int) (Math.random() * matchCount);
            // add the random match
            return matches.get(randomIndex);
        }
    }

}
