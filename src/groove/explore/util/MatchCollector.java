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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import groove.lts.AbstractGraphState;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransitionStub;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.trans.SystemRecord;
import groove.trans.VirtualRuleEvent;
import groove.util.TransformIterator;

/**
 * Algorithm to create a mapping from enabled rules to collections
 * of events for those rules, matching to a given state.
 * @author Arend Rensink
 * @version $Revision $
 */
class MatchCollector {
    /** 
     * Constructs a match collector for a given (start) state.
     * The collector does not have prior information about matches in the parent state.
     * @param state the state for which matches are to be collected
     * @param cache object to decide on applicable rules
     * @param record system record to turn {@link RuleMatch}es in to {@link RuleEvent}s.
     */
    MatchCollector(GraphState state, ExploreCache cache, SystemRecord record, Rule lastRule, MatchMap parentMap) {
        this.state = state;
        this.cache = cache;
        this.record = record;
        this.lastRule = lastRule;
        this.parentMap = parentMap;
        if (lastRule != null) {
            this.enabledRules = record.getEnabledRules(lastRule);
            this.disabledRules = record.getDisabledRules(lastRule);
        }
    }
    
    /**
     * Returns the match map for the state passed in by the constructor.
     */
    public MatchMap getMatchMap() {
        
    }
    
    protected Iterator<RuleEvent> createEventIter(Rule rule) {
        if (parentMap != null) {
        Collection<RuleEvent> aliasedMatches = computeAliasedMatches(rule);
        if (aliasedMatches != null) {
            if (enabledRules.contains(rule)) {
                // the rule was possible enabled afresh, so we have to add the fresh matches
                Iterator<RuleEvent> freshMatches = createSimpleEventIter(rule);
                while (freshMatches.hasNext()) {
                    aliasedMatches.add(freshMatches.next());
                }
            }
            return aliasedMatches.iterator();
        }
        }
//        if (currentRule.getPriority() > priority && !enabledRules.contains(currentRule)) {
//            return new EmptyMatchIter();
//        }
//        if (currentRule.getPriority() <= priority
//                && !((currentRule instanceof SPORule && ((SPORule) currentRule).hasSubRules()) || enabledRules
//                        .contains(currentRule))) {
//            // it didn't match in the previous state or no matches left after rematching
//            return new EmptyMatchIter();
//        }
        return createSimpleEventIter(rule);
    }
    
    /** Callback method to create an iterator over the matches of a given rule. */
    protected Iterator<RuleEvent> createSimpleEventIter(Rule rule) {
        if (COLLECT_ALL_MATCHES) {
            List<RuleEvent> result = new ArrayList<RuleEvent>();
            for (RuleMatch match: rule.getMatches(state.getGraph(), null)) {
                result.add(record.getEvent(match));
            }
            return result.iterator();
        } else {
            return new TransformIterator<RuleMatch,RuleEvent>(rule.getMatchIter(state.getGraph(), null)) {
                @Override
                protected RuleEvent toOuter(RuleMatch from) {
                    return record.getEvent(from);
                }
            };
        }
    }
    
    /**
     * Computes a map with all matches from the previous state that still match in the current state.
     */
    private Collection<RuleEvent> computeAliasedMatches(Rule rule) {
        Collection<RuleEvent> result = new ArrayList<RuleEvent>();
        for (RuleEvent event: parentMap.get(rule)) {
            Rule parentRule = event.getRule();
            if (!disabledRules.contains(parentRule) || event.hasMatch(state.getGraph())) {
                result.add(new VirtualRuleEvent(event, stub));
            }
        }
        return result;
    }

    /** Increments the rule iterator after the creation of this 
     * matches iterator. Also initializes {@link #eventIter}, 
     * except if this iterator is consumed.
     * This is different from the general {@link #nextRule()}
     * as some additional treatment is performed for the first rule.
     */
    protected void firstRule() {
        this.currentRule = cache.last();
        if (this.currentRule == null) {
            // this means that rulesIter is freshly created and has never been incremented before
            if (!this.cache.hasNext()) {  // this iterator is entirely consumed 
                this.eventIter = null;
                return;
            }
            this.currentRule = cache.next();
        }
        this.eventIter = createEventIter(currentRule);
    }

    /** Increments the rule iterator.
     * Also initialises {@link #eventIter}, except if this iterator is consumed.
     * @return <code>true</code> if the rules iterator is not consumed
     */
    protected boolean nextRule() {
        this.cache.updateExplored(currentRule);
        if (!this.cache.hasNext()) { // this iterator is entirely consumed 
            this.eventIter = null;
            return false;
        } else {
            this.currentRule = cache.next();
            this.eventIter = createEventIter(currentRule);
            return true;
        }
    }

    private final GraphState state;
    private final ExploreCache cache;
    private final SystemRecord record;
    private final Rule lastRule;
    private final MatchMap parentMap;
    /** The rules that may be enabled. */
    private Set<Rule> enabledRules;
    /** The rules that may be disabled. */
    private Set<Rule> disabledRules;

    private Rule currentRule;
    private Iterator<RuleEvent> eventIter;
    
    
    /** Flag to collect all matches at once, rather than doing a true iteration. */
    private final boolean COLLECT_ALL_MATCHES = true;
}
