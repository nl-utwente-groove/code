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

import groove.control.CtrlTransition;
import groove.lts.GraphState;
import groove.lts.MatchResult;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.trans.SystemRecord;

import java.util.Collection;

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
     * @param record factory to turn {@link RuleMatch}es in to
     *        {@link RuleEvent}s.
     */
    public ConfluentMatchSetCollector(GraphState state, SystemRecord record,
            boolean checkDiamonds) {
        super(state, record, checkDiamonds);
    }

    /**
     * Adds the matching events for a given rule into an existing set.
     * If the rule is confluent only one arbitrary match is added.
     * @param call the rule to be matched
     * @param result the set to which the resulting events are to be added
     * @return <code>true</code> if any events for <code>rule</code> were
     *         added to <code>result</code>
     */
    @Override
    protected boolean collectEvents(CtrlTransition call,
            Collection<MatchResult> result) {
        boolean eventAdded;
        Rule rule = call.getRule();
        if (rule.isConfluent()) {
            // find a single match for this call
            MatchResult match = getMatch(call);
            eventAdded = match != null;
            if (eventAdded) {
                result.add(match);
            }
        } else { // Rule is not confluent.
            // Use method from super class.
            eventAdded = super.collectEvents(call, result);
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
    public void collectMatchSet(Collection<MatchResult> result) {
        CtrlTransition ctrlTrans = firstCall();
        boolean usedConfluentRule = false;
        while (ctrlTrans != null) {
            boolean hasMatches = false;
            if (!(ctrlTrans.getRule().isConfluent() && usedConfluentRule)) {
                hasMatches = collectEvents(ctrlTrans, result);
                if (hasMatches) {
                    if (ctrlTrans.getRule().isConfluent()) {
                        usedConfluentRule = true;
                    }
                }
            }
            ctrlTrans = nextCall(hasMatches);
        }
    }

}
