package groove.explore.util;

import groove.lts.AbstractGraphState;
import groove.lts.GraphNextState;
import groove.lts.GraphTransitionStub;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.SystemRecord;
import groove.trans.VirtualRuleEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

// TODO This class may be better integrated into its super class. For instance,
// the aliasMatchIter field is not needed, super.matchIter may be used instead

public class AliasMatchesIterator extends MatchesIterator {
	/** 
	 * Creates an aliased matches iterator for a state and a rule.
	 * @param state
	 * @param rules
	 * @param enabledRules The rules that may be enabled by the rule event that created this state.
	 * @param disabledRules The rules that may be disabled by the rule event that created this state.
	 * @param record the event factory
	 */
	public AliasMatchesIterator (GraphNextState state, ExploreCache rules, Set<Rule> enabledRules, Set<Rule> disabledRules, SystemRecord record ) {
		super(state, rules, true, record);
		this.enabledRules = enabledRules;
		this.disabledRules = disabledRules;
//		this.priority = state.getEvent().getRule().getPriority();
		firstRule();
		goToNext();
	}
	
	@Override
    protected Iterator<RuleEvent> createMatchIter(Rule rule) {
        Collection<RuleEvent> aliasedMatches = getAliasedMatches(rule);
        if (aliasedMatches != null) {
        	if (enabledRules.contains(rule)) {
        		// the rule was possible enabled afresh, so we have to add the fresh matches
        		Iterator<RuleEvent> freshMatches = super.createMatchIter(rule);
        		while (freshMatches.hasNext()) {
        			aliasedMatches.add(freshMatches.next());
        		}
        	}
        	return aliasedMatches.iterator();
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
        return super.createMatchIter(rule);
    }

	/** 
	 * Returns the set of matches from the previous state, if any.
	 * @param rule The rule to be matched
	 * @return All matches for <code>rule</code>; <code>null</code> if the matches 
	 * could not be computed on the basis of the previous state.
	 */
	private Collection<RuleEvent> getAliasedMatches(Rule rule) {
		Collection<RuleEvent> result = null;
		AbstractGraphState parent = (AbstractGraphState) ((GraphNextState) state).source();
		if (parent.isClosed() && isUseDependencies()) {
			if (aliasedRuleMatches == null) {
				aliasedRuleMatches = computeAliasedMatches();
			}
			result = aliasedRuleMatches.get(rule);
			// rules that did not match at all are not included in the aliasedRuleMatches
			if (result == null && !enabledRules.contains(rule)) {
				result = Collections.emptyList();
			}
		}
		return result;
	}
	
	/**
	 * Computes a map with all matches from the previous state that still match in the current state.
	 */
	private Map<Rule, Collection<RuleEvent>> computeAliasedMatches() {
        Map<Rule, Collection<RuleEvent>> result = new TreeMap<Rule, Collection<RuleEvent>>();
        for (GraphTransitionStub stub : ((AbstractGraphState) ((GraphNextState) state).source()).getStoredTransitionStubs()) {
            RuleEvent event = stub.getEvent(((GraphNextState) state).source());
            Rule rule = event.getRule();
            if (!disabledRules.contains(rule) || event.hasMatch(state.getGraph())) {
				Collection<RuleEvent> matches = result.get(rule);
				if (matches == null) {
					// if the rule is enabled, we will also add the fresh matches
					// so we need a set; otherwise, a list is more efficient
					if (enabledRules.contains(rule)) {
						matches = new HashSet<RuleEvent>();
					} else {
						matches = new ArrayList<RuleEvent>();
					}
					result.put(rule, matches);
				}
				matches.add(new VirtualRuleEvent(event, stub));
			}
		}
        return result;
    }
	
	/**
	 * TODO: fixme, currently always enabled if this class is used... (?)
	 */
	private boolean isUseDependencies() {
		return true;
	}
//	
//	/** The matched priority in the preceding state */
//	private int priority = 0;
	
	/** Set with matched rules and the corresponding matches **/
	// TODO which exactly matches are there ?
	private Map<Rule, Collection<RuleEvent>> aliasedRuleMatches;
	
	/** The rules that may be enabled. */
	private Set<Rule> enabledRules;
	/** The rules that may be disabled. */
	private Set<Rule> disabledRules;
	
	/** Is used as value for the matchIter when we know that it is empty. */
	private class EmptyMatchIter implements Iterator<RuleEvent> {
	    /** Always returns <code>false</code>. */
		public boolean hasNext() { return false; }
		/** Always throws a {@link NoSuchElementException} . */
		public RuleEvent next() { throw new NoSuchElementException(); }
		/** Always throws an {@link UnsupportedOperationException}. */
		public void remove() { throw new UnsupportedOperationException(); }
		
	}
}
