/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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

import groove.explore.util.ConfluentMatchSetCollector;
import groove.explore.util.MatchSetCollector;

/**
 * At each step, either fully explores an open state if the rule to be applied
 * is not marked as confluent or selects a match of a confluent rule and
 * performs a linear exploration.
 * @author Eduardo Zambon
 */
public class LinearConfluentRules extends NextOpenStrategy {
    /**
     * Returns a {@link ConfluentMatchSetCollector}.
     */
    @Override
    protected MatchSetCollector createMatchCollector() {
        return new ConfluentMatchSetCollector(getAtState(), getRecord(),
            getGTS().checkDiamonds());
    }
}
