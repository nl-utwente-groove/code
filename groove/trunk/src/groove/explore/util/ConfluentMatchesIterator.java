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

import groove.lts.GraphState;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.trans.SystemRecord;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Iterates over all matches of a collection of rules into a graph.
 * @author Eduardo Zambon
 */
public class ConfluentMatchesIterator extends MatchesIterator {

    /**
     * Constructs a new matches iterator for a given state.
     */
    public ConfluentMatchesIterator(GraphState state, ExploreCache rules,
            SystemRecord record) {
        super(state, rules, record);
    }

    /** 
     * Call-back method to create an iterator over the matches of a given
     * rule. For non-confluent rules the iterator of the super class is
     * returned. For confluent rules an iterator with at most one arbitrary
     * match is returned. 
     */
    @Override
    protected Iterator<RuleEvent> createEventIter(Rule rule) {
        if (rule.isConfluent()) {
            List<RuleEvent> result = new ArrayList<RuleEvent>(1);
            Iterator<RuleMatch> ruleMatchIter =
                rule.getMatchIter(this.state.getGraph(), null);
            if (ruleMatchIter.hasNext()) {
                RuleMatch match = ruleMatchIter.next();
                result.add(this.record.getEvent(match));
            }
            return result.iterator();
        } else {
            // Non-confluent rule, just use the method of super class.
            return super.createEventIter(rule);
        }
    }
}
