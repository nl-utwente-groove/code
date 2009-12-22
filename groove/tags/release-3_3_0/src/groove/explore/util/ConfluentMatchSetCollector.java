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
import groove.trans.VirtualEvent;

import java.util.Collection;
import java.util.Iterator;

/**
 * Algorithm to create a mapping from enabled rules to collections of events for
 * those rules, matching to a given state. From the set of rules marked as
 * confluent, one arbitrary rule is chosen and only one match of this rule is
 * considered. For non-confluent rules, this class behaves exactly as its
 * super class.
 * @author Eduardo Zambon
 */
public class ConfluentMatchSetCollector extends MatchSetCollector {

    /**
     * Constructs a match collector for a given (start) state.
     * @param state the state for which matches are to be collected
     * @param cache object to decide on applicable rules
     * @param record factory to turn {@link RuleMatch}es in to
     *        {@link RuleEvent}s.
     * @param virtualEventSet outgoing transitions from the parent state
     */
    public ConfluentMatchSetCollector(GraphState state, ExploreCache cache,
            SystemRecord record,
            Collection<VirtualEvent.GraphState> virtualEventSet) {
        super(state, cache, record, virtualEventSet);
    }

    /**
     * Adds the matching events for a given rule into an existing set.
     * If the rule is confluent only one arbitrary match is added.
     * @param rule the rule to be matched
     * @param result the set to which the resulting events are to be added
     * @return <code>true</code> if any events for <code>rule</code> were
     *         added to <code>result</code>
     */
    @Override
    protected boolean collectEvents(Rule rule, Collection<RuleEvent> result) {
        boolean eventAdded;
        if (rule.isConfluent()) {
            Iterator<RuleMatch> iterator =
                rule.getMatchIter(this.state.getGraph(), null);
            if (iterator.hasNext()) {
                // Get an arbitrary match
                RuleMatch match = iterator.next();
                result.add(this.record.getEvent(match));
                eventAdded = true;
            } else { // No matches, the result collection is returned empty.
                eventAdded = false;
            }
        } else { // Rule is not confluent.
            // Use method from super class.
            eventAdded = super.collectEvents(rule, result);
        }
        return eventAdded;
    }
    
    /**
     * Collects the set of matching events for the state passed in by the
     * constructor into a collection passed in as a parameter.
     * At most one arbitrary matching event is collected for all confluent
     * rules.
     */
    @Override
    public void collectMatchSet(Collection<RuleEvent> result) {
        Rule currentRule = firstRule();
        boolean usedConfluentRule = false;
        while (currentRule != null) {
            if (!(currentRule.isConfluent() && usedConfluentRule)) {
                boolean hasMatches = collectEvents(currentRule, result);
                if (hasMatches) {
                    this.cache.updateMatches(currentRule);
                    if (currentRule.isConfluent()) {
                        usedConfluentRule = true;
                    }
                }
            }
            this.cache.updateExplored(currentRule);
            currentRule = nextRule();
        }
    }
    
}
