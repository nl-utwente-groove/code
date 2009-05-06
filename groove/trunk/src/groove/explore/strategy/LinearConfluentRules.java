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
import groove.explore.util.ExploreCache;
import groove.lts.GraphState;
import groove.trans.RuleEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * At each step, either fully explores an open state if the rule to be applied
 * is not marked as confluent or selects a match of a confluent rule and
 * performs a linear exploration.
 * @author Eduardo Zambon
 */
public class LinearConfluentRules extends AbstractStrategy {

    /**
     * Selects an open state of the GTS as the current state to be explored. 
     */
    @Override
    protected void updateAtState() {
        Iterator<GraphState> stateIter = getGTS().getOpenStateIter();
        if (stateIter.hasNext()) {
            this.atState = stateIter.next();
        } else {
            this.atState = null;
        }
    }

    /**
     * A step of this strategy explores one state. All possible matches of
     * non-confluent rules and an arbitrary match of confluent rules are
     * explored.
     * @return <code>true</code> if a state was successfully explored and
     *         <code>false</code> otherwise.  
     */
    public boolean next() {
        if (getAtState() == null) {
            return false;
        }
        ExploreCache cache = getCache(true, false);
        ConfluentMatchSetCollector collector =
            new ConfluentMatchSetCollector(getAtState(), cache, getRecord(), null);
        // collect all matches
        List<RuleEvent> matches = new ArrayList<RuleEvent>();
        collector.collectMatchSet(matches);
        Iterator<RuleEvent> matchesIter = matches.iterator();
        while (matchesIter.hasNext()) {
            getGenerator().applyMatch(getAtState(), matchesIter.next(), cache);
        }
        setClosed(getAtState());
        updateAtState();
        return true;
    }

}