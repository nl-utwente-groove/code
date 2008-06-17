package groove.explore.util;

import groove.lts.AbstractGraphState;
import groove.lts.DefaultAliasApplication;
import groove.lts.DefaultGraphNextState;
import groove.lts.GraphNextState;
import groove.lts.GraphTransitionStub;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.trans.SPORule;
import groove.trans.VirtualRuleMatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

// TODO This class may be better integrated into its super class. For instance,
// the aliasMatchIter field is not needed, super.matchIter may be used instead

public class AliasMatchesIterator extends MatchesIterator {
	
	//private Iterator<RuleMatch> aliasMatchIter;

	/** Creates an aliased matches iterator for a state and a rule.
	 * 
	 * @param state
	 * @param rules
	 * @param enabledRules The rules that may be enabled by the rule event that created this state.
	 * @param disabledRules The rules that may be disabled by the rule event that created this state.
	 */
	public AliasMatchesIterator (DefaultGraphNextState state, ExploreCache rules, Set<Rule> enabledRules, Set<Rule> disabledRules ) {
		super(state, rules, true);
		this.enabledRules = enabledRules;
		this.disabledRules = disabledRules;
		priority = state.getEvent().getRule().getPriority();
		firstRule();
		goToNext();
	}	@Override	public RuleMatch next() {
		
		RuleMatch m;
		if( aliasMatchIter != null && aliasMatchIter.hasNext() ) {
			m = aliasMatchIter.next();
		} else {
			m = super.next();
		}
		return m;
	}	
	@Override
	protected void firstRule() {
		super.firstRule();
		if (currentRule != null) {
			doAliasSelection();
		}	}
	@Override
	public boolean nextRule() {		if (super.nextRule()) {
			doAliasSelection();
			return true;
		}
		return false; 	}
	
	/** Updates the value of matchIter according to matches in the previous state. */
	private void doAliasSelection() {
		
		// cases where the matchIter should be replaced by aliased matches iterator
		// - the priority of currentRule is the same as priority, and aliasedRuleMatches has an entry for currentRule
		
		// cases where the matchIter should be replaced by an empty matches iterator
		// - the priority of currentRule is higher than priority, and currentRule was not enabled
		// - the priority of currentRule is the same as priority, and
		//       ! enabledRules.contains(currentRule) and
		//       ( ! (currentRule instanceof SPORule)   or   !(((SPORule)currentRule).hasSubRules())    )
		
		// do nothing in all the other situations
		
		
		if (currentRule.getPriority() == priority) {
			updateMatches(); // done once
		}
		

		if (currentRule.getPriority() == priority && aliasedRuleMatches.containsKey(currentRule)) {
			aliasMatchIter = aliasedRuleMatches.get(currentRule).iterator();
			return;
		}
		
		if (currentRule.getPriority() > priority && ! enabledRules.contains(currentRule)) {
			this.matchIter = new EmptyMatchIter();
			return;
		}
		
		if (currentRule.getPriority() == priority && 
				! ( (currentRule instanceof SPORule && ((SPORule)currentRule).hasSubRules()) || enabledRules.contains(currentRule) ) ) {
			// it didn't match in the previous state or no matches left after rematching
			this.matchIter = new EmptyMatchIter();
			return;
		}
		
		if (currentRule.getPriority() < priority && 
				! ( (currentRule instanceof SPORule && ((SPORule)currentRule).hasSubRules())|| enabledRules.contains(currentRule)) ) {
			// its neither a composite rule nor the rule was enabled
			this.matchIter = new EmptyMatchIter();
		}
	}

	
	/** Initializes a map with all matches from the previous state. 
	 * The map is precomputed once for all rules.
	 */
	private void updateMatches() {
		if( aliasedRuleMatches == null ) {
			aliasedRuleMatches = new TreeMap<Rule,List<RuleMatch>>();
			// init the map			for( GraphTransitionStub stub : ((AbstractGraphState)((DefaultGraphNextState) state).source()).getStoredTransitionStubs() ) {				RuleEvent event = stub.getEvent(((DefaultGraphNextState)state).source());				Rule rule = event.getRule();	            List<RuleMatch> matches = aliasedRuleMatches.get(rule);	            if ( isUseDependencies() && !disabledRules.contains(rule) || event.hasMatch(state.getGraph())) {					if( matches == null ) {						matches = new ArrayList<RuleMatch>();						this.aliasedRuleMatches.put(rule, matches);					}					matches.add(new VirtualRuleMatch(new DefaultAliasApplication(event, (GraphNextState)state, stub)));	            }			}		}	}
	
	/**
	 * TODO: fixme, currently always enabled if this class is used... (?)
	 * @return
	 */
	private boolean isUseDependencies() {
		return true;
	}
	
	/** The matched priority in the preceding state */
	private int priority = 0;
	
	/** Set with matched rules and the corresponding matches **/
	// TODO which exactly matches are there ?
	private Map<Rule, List<RuleMatch>> aliasedRuleMatches;
	
	/** The rules that may be enabled. */
	private Set<Rule> enabledRules;
	/** The rules that may be disabled. */
	private Set<Rule> disabledRules;

	private Iterator<RuleMatch> aliasMatchIter;
	
	/** Is used as value for the matchIter when we know that it is empty. */
	private class EmptyMatchIter implements Iterator<RuleMatch> {
	    /** Always returns <code>false</code>. */
		public boolean hasNext() { return false; }
		/** Always throws a {@link NoSuchElementException} . */
		public RuleMatch next() { throw new NoSuchElementException(); }
		/** Always throws an {@link UnsupportedOperationException}. */
		public void remove() { throw new UnsupportedOperationException(); }
		
	}
}
