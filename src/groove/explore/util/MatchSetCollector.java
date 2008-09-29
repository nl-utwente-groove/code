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

import groove.lts.GraphNextState;
import groove.lts.GraphState;
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
 * Algorithm to create a mapping from enabled rules to collections
 * of events for those rules, matching to a given state.
 * @author Arend Rensink
 * @version $Revision $
 */
public class MatchSetCollector {
    /** 
     * Constructs a match collector for a given (start) state.
     * @param state the state for which matches are to be collected
     * @param cache object to decide on applicable rules
     * @param record factory to turn {@link RuleMatch}es in to {@link RuleEvent}s.
     * @param parentTransitions outgoing transitions from the parent state
     */
    public MatchSetCollector(GraphState state, ExploreCache cache, SystemRecord record, Collection<VirtualEvent.GraphState> parentTransitions) {
        this.state = state;
        this.cache = cache;
        this.record = record;
        this.parentTransSet = parentTransitions;
        if (parentTransitions != null) {
        	Rule lastRule = ((GraphNextState) state).getEvent().getRule();
            this.enabledRules = record.getEnabledRules(lastRule);
            this.disabledRules = record.getDisabledRules(lastRule);
        }
    }
    
    /**
     * Returns the match map for the state passed in by the constructor.
     */
    public Collection<RuleEvent> getMatchMap() {
    	Set<RuleEvent> result = new HashSet<RuleEvent>();
        Rule currentRule = firstRule();
        while (currentRule != null) {
        	boolean hasMatches = collectEventSet(currentRule, result);
        	if (hasMatches) {
        		cache.updateMatches(currentRule);
        	}
        	currentRule = nextRule();
        }
        return result;
    }

    /** 
     * Collects the events for a given rule.
     * @param rule the rule to be matched
     * @param result the set to which the resulting events are to be added
     * @return <code>true</code> if any events for <code>rule</code> were added to <code>result</code> 
     */
    private boolean collectEventSet(Rule rule, Collection<RuleEvent> result) {
    	boolean hasMatched = collectParentEvents(rule, result);
		if (!hasMatched || enabledRules.contains(rule)) {
			// the rule was possible enabled afresh, so we have to add the fresh matches
			for (RuleMatch match : rule.getMatches(state.getGraph(), null)) {
				result.add(record.getEvent(match));
				hasMatched = true;
			}
		}
		return hasMatched;
	}

    private boolean collectParentEvents(Rule rule, Collection<RuleEvent> result) {
    	if (parentEventMap == null && parentTransSet != null) {
    		parentEventMap = computeParentEventMap();
    	}
    	if (parentEventMap == null) {
    		return false;
    	} else {
    		Collection<RuleEvent> parentEvents = parentEventMap.get(rule);
    		if (parentEvents == null) {
    			return false;
    		} else {
    			result.addAll(parentEvents);
    			return true;
    		}
    	}
    }
    
    /**
	 * Computes a map with all matches from the previous state that 
	 * still match in the current state.
	 */
	private Map<Rule, Collection<RuleEvent>> computeParentEventMap() {
        Map<Rule, Collection<RuleEvent>> result = new HashMap<Rule, Collection<RuleEvent>>();
        if (parentTransSet != null) {
			for (VirtualEvent.GraphState parentTrans : parentTransSet) {
				Rule rule = parentTrans.getRule();
				if (!disabledRules.contains(rule) || parentTrans.hasMatch(state.getGraph())) {
					Collection<RuleEvent> matches = result.get(rule);
					if (matches == null) {
						matches = new ArrayList<RuleEvent>();
						result.put(rule, matches);
					}
					matches.add(parentTrans);
				}
			}
		}
        return result;
    }
//
//	/** 
//	 * Callback factory method to create an event aliasing an existing one.
//	 * @param original the event to be aliased
//	 * @param parentTrans the transition containing the original event
//	 * @return if the new match is potentially part of a confluent diamond,
//	 * returns a {@link VirtualRuleEvent} wrapping the parameters; otherwise,
//	 * just returns <code>original</code>.
//	 */
//	private RuleEvent createVirtualEvent(RuleEvent original, GraphTransition parentTrans) {
//		GraphState innerTarget;
//		if (parentTrans.isSymmetry() || original.conflicts(((GraphNextState) state).getEvent())) {
//			innerTarget = null;
//		} else {
//			innerTarget = parentTrans.target();
//		}
//		return new VirtualRuleEvent.GraphState(original, innerTarget, parentTrans.getAddedNodes());
//	}
	
    /**
	 * Returns either the last previously returned rule from the
	 * {@link ExploreCache}, or the first new rule if there is no last.
	 */
    private Rule firstRule() {
        Rule result = cache.last();
        if (result == null && cache.hasNext()) {
            // this means that rulesIter is freshly created and has never been incremented before
        	result = cache.next();
        }
        return result;
    }

    /** 
     * Increments the rule iterator, and returns the next rule.
     */
    private Rule nextRule() {
    	cache.updateExplored(cache.last());
        return cache.hasNext() ? cache.next() : null;
    }

    private final GraphState state;
    private final ExploreCache cache;
    private final SystemRecord record;
    private final Collection<VirtualEvent.GraphState> parentTransSet;
    private Map<Rule,Collection<RuleEvent>> parentEventMap;
    /** The rules that may be enabled. */
    private Set<Rule> enabledRules;
    /** The rules that may be disabled. */
    private Set<Rule> disabledRules;
}
