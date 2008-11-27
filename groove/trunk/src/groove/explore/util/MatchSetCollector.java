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

import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.trans.SystemRecord;
import groove.trans.VirtualEvent;
import groove.util.Reporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Algorithm to create a mapping from enabled rules to collections of events for
 * those rules, matching to a given state.
 * @author Arend Rensink
 * @version $Revision $
 */
public class MatchSetCollector {
    /**
     * Constructs a match collector for a given (start) state.
     * @param state the state for which matches are to be collected
     * @param cache object to decide on applicable rules
     * @param record factory to turn {@link RuleMatch}es in to
     *        {@link RuleEvent}s.
     * @param virtualEventSet outgoing transitions from the parent state
     */
    public MatchSetCollector(GraphState state, ExploreCache cache,
            SystemRecord record,
            Collection<VirtualEvent.GraphState> virtualEventSet) {
        this.state = state;
        this.cache = cache;
        this.record = record;
        this.virtualEventSet = virtualEventSet;
        if (virtualEventSet != null) {
            Rule lastRule = ((GraphNextState) state).getEvent().getRule();
            this.enabledRules = record.getEnabledRules(lastRule);
            this.disabledRules = record.getDisabledRules(lastRule);
        }
    }

    /** Returns a single match for the state passed in through the constructor. */
    public RuleEvent getMatch() {
        reporter.start(GET_MATCH);
        RuleEvent result = null;
        Rule currentRule = firstRule();
        while (result == null && currentRule != null) {
            // first try to find a virtual event
            Collection<RuleEvent> virtualEvents = getVirtualEvents(currentRule);
            if (virtualEvents == null) {
                // if this fails, try to find a match
                Iterator<RuleMatch> matchIter =
                    currentRule.getMatchIter(this.state.getGraph(), null);
                if (matchIter.hasNext()) {
                    // convert the match to an event
                    result = this.record.getEvent(matchIter.next());
                } else {
                    // no luck; try the next rule
                    currentRule = nextRule();
                }
            } else {
                result = virtualEvents.iterator().next();
            }
        }
        reporter.stop();
        return result;
    }

    /**
     * Returns the set of matching events for the state passed in by the
     * constructor.
     */
    public Collection<RuleEvent> getMatchSet() {
        Set<RuleEvent> result = new LinkedHashSet<RuleEvent>();
        collectMatchSet(result);
        return result;
    }

    /**
     * Collects the set of matching events for the state passed in by the
     * constructor into a collection passed in as a parameter.
     */
    public void collectMatchSet(Collection<RuleEvent> result) {
        reporter.start(GET_MATCH_SET);
        Rule currentRule = firstRule();
        while (currentRule != null) {
            boolean hasMatches = collectEvents(currentRule, result);
            if (hasMatches) {
                this.cache.updateMatches(currentRule);
            }
            this.cache.updateExplored(currentRule);
            currentRule = nextRule();
        }
        reporter.stop();
    }

    /**
     * Adds the matching events for a given rule into an existing set.
     * @param rule the rule to be matched
     * @param result the set to which the resulting events are to be added
     * @return <code>true</code> if any events for <code>rule</code> were
     *         added to <code>result</code>
     */
    private boolean collectEvents(Rule rule, Collection<RuleEvent> result) {
        boolean hasMatched = collectVirtualEvents(rule, result);
        // AREND: I added here a check so that new matches are also added when the cache is a locationcache
        // because the parent state may (regardless enabledRules) have no matches in the parent due to control.
        if (this.enabledRules == null || this.enabledRules.contains(rule) || cache instanceof LocationCache) {
            // the rule was possible enabled afresh, so we have to add the fresh
            // matches
            for (RuleMatch match : rule.getMatches(this.state.getGraph(), null)) {
                result.add(this.record.getEvent(match));
                hasMatched = true;
            }
        }
        return hasMatched;
    }

    /**
     * Adds the virtual events for a given rule into an existing set.
     */
    private boolean collectVirtualEvents(Rule rule, Collection<RuleEvent> result) {
        // add the virtual events for this rule if any
        Collection<RuleEvent> virtualEvents = getVirtualEvents(rule);
        if (virtualEvents == null) {
            return false;
        } else {
            result.addAll(virtualEvents);
            return true;
        }
    }

    /** Returns the virtual events for a given rule. */
    private Collection<RuleEvent> getVirtualEvents(Rule rule) {
        // Create the virtual event map if it is not yet there.
        if (this.virtualEventMap == null && this.virtualEventSet != null) {
            this.virtualEventMap = computeVirtualEventMap();
        }
        if (this.virtualEventMap == null) {
            return null;
        } else {
            return this.virtualEventMap.get(rule);
        }
    }

    /**
     * Computes a map with all matches from the previous state that still match
     * in the current state.
     */
    private Map<Rule,Collection<RuleEvent>> computeVirtualEventMap() {
        reporter.start(COMPUTE_EVENT_MAP);
        Map<Rule,Collection<RuleEvent>> result =
            new HashMap<Rule,Collection<RuleEvent>>();
        if (this.virtualEventSet != null) {
            for (VirtualEvent.GraphState virtual : this.virtualEventSet) {
                Rule rule = virtual.getRule();
                if (!this.disabledRules.contains(rule)
                    || virtual.hasMatch(this.state.getGraph())) {
                    Collection<RuleEvent> matches = result.get(rule);
                    if (matches == null) {
                        matches = new ArrayList<RuleEvent>();
                        result.put(rule, matches);
                    }
                    matches.add(virtual);
                }
            }
        }
        reporter.stop();
        return result;
    }

    /**
     * Returns either the last (previously returned) rule from the
     * {@link ExploreCache}, or the first new rule if there is no last.
     */
    private Rule firstRule() {
        Rule result = this.cache.last();
        if (result == null && this.cache.hasNext()) {
            // this means that the cache was freshly created and has never been
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
    private final Collection<VirtualEvent.GraphState> virtualEventSet;
    private Map<Rule,Collection<RuleEvent>> virtualEventMap;
    /** The rules that may be enabled. */
    private Set<Rule> enabledRules;
    /** The rules that may be disabled. */
    private Set<Rule> disabledRules;

    private static final Reporter reporter =
        Reporter.register(MatchSetCollector.class);
    private static final int GET_MATCH_SET = reporter.newMethod("getMatchSet");
    private static final int GET_MATCH = reporter.newMethod("getMatch");
    private static final int COMPUTE_EVENT_MAP =
        reporter.newMethod("computeVirtualEventMap");
}
