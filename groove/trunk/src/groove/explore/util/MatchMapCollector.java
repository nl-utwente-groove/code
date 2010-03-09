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
package groove.explore.util;

import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.trans.SystemRecord;
import groove.trans.VirtualEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Algorithm to create a mapping from enabled rules to collections of events for
 * those rules, matching to a given state.
 * @author Arend Rensink
 * @version $Revision $
 * @deprecated not used anywhere; purpose lost in the mists of time
 */
@Deprecated
public class MatchMapCollector {
    /**
     * Constructs a match collector for a given (start) state. The collector
     * does not have prior information about matches in the parent state.
     * @param state the state for which matches are to be collected
     * @param cache object to decide on applicable rules
     * @param record system record to turn {@link RuleMatch}es in to
     *        {@link RuleEvent}s.
     */
    public MatchMapCollector(GraphState state, ExploreCache cache,
            SystemRecord record, Rule lastRule,
            Collection<GraphTransition> parentMap) {
        this.state = state;
        this.cache = cache;
        this.record = record;
        this.parentTransSet = parentMap;
        if (lastRule != null) {
            this.enabledRules = record.getEnabledRules(lastRule);
            this.disabledRules = record.getDisabledRules(lastRule);
        }
    }

    /**
     * Returns the match map for the state passed in by the constructor.
     */
    public MatchMap<RuleEvent> getMatchMap() {
        MatchMap<RuleEvent> result = new MatchMap<RuleEvent>();
        Rule currentRule = firstRule();
        while (currentRule != null) {
            Collection<RuleEvent> events = computeEventSet(currentRule);
            result.put(currentRule, events);
            if (!events.isEmpty()) {
                this.cache.updateMatches(currentRule);
            }
            currentRule = nextRule();
        }
        return result;
    }

    /** Returns the events for a given rule. */
    private Collection<RuleEvent> computeEventSet(Rule rule) {
        Collection<RuleEvent> result = getParentEventSet(rule);
        if (result == null || this.enabledRules.contains(rule)) {
            if (result == null) {
                result = createEventSet();
            }
            // the rule was possible enabled afresh, so we have to add the fresh
            // matches
            for (RuleMatch match : rule.getMatches(this.state.getGraph(), null)) {
                result.add(this.record.getEvent(match));
            }
        }
        return result;
    }

    private Collection<RuleEvent> getParentEventSet(Rule rule) {
        if (this.parentEventMap == null && this.parentTransSet != null) {
            this.parentEventMap = computeParentEventMap();
        }
        return this.parentEventMap == null ? null
                : this.parentEventMap.get(rule);
    }

    /**
     * Computes a map with all matches from the previous state that still match
     * in the current state.
     */
    private Map<Rule,Collection<RuleEvent>> computeParentEventMap() {
        Map<Rule,Collection<RuleEvent>> result =
            new HashMap<Rule,Collection<RuleEvent>>();
        if (this.parentTransSet != null) {
            for (GraphTransition parentTrans : this.parentTransSet) {
                RuleEvent event = parentTrans.getEvent();
                Rule rule = event.getRule();
                if (!this.disabledRules.contains(rule)
                    || event.hasMatch(this.state.getGraph())) {
                    Collection<RuleEvent> matches = result.get(rule);
                    if (matches == null) {
                        // if the rule is enabled, we will also add the fresh
                        // matches
                        // so we need a set; otherwise, a list is more efficient
                        if (this.enabledRules.contains(rule)) {
                            matches = new HashSet<RuleEvent>();
                        } else {
                            matches = new ArrayList<RuleEvent>();
                        }
                        result.put(rule, matches);
                    }
                    matches.add(new VirtualEvent<GraphTransition>(event,
                        parentTrans));
                }
            }
        }
        return result;
    }

    /** Callback factory method for event collections. */
    private Collection<RuleEvent> createEventSet() {
        return new HashSet<RuleEvent>();
    }

    /**
     * Returns either the last previously returned rule from the
     * {@link ExploreCache}, or the first new rule if there is no last.
     */
    private Rule firstRule() {
        Rule result = this.cache.last();
        if (result == null && this.cache.hasNext()) {
            // this means that rulesIter is freshly created and has never been
            // incremented before
            result = this.cache.next();
        }
        return result;
    }

    /**
     * Increments the rule iterator, and returns the next rule.
     */
    private Rule nextRule() {
        this.cache.updateExplored(this.cache.last());
        return this.cache.hasNext() ? this.cache.next() : null;
    }

    private final GraphState state;
    private final ExploreCache cache;
    private final SystemRecord record;
    private final Collection<GraphTransition> parentTransSet;
    private Map<Rule,Collection<RuleEvent>> parentEventMap;
    /** The rules that may be enabled. */
    private Set<Rule> enabledRules;
    /** The rules that may be disabled. */
    private Set<Rule> disabledRules;
}
