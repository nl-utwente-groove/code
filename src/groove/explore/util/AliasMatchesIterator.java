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

import java.util.ArrayList;import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

// TODO This class may be better integrated into its super class. For instance,
// the aliasMatchIter field is not needed, super.matchIter may be used instead

public class AliasMatchesIterator extends MatchesIterator {
	private Iterator<RuleMatch> aliasMatchIter;

	/** Creates an aliased matches iterator for a state and a rule.
	 * 
	 * @param state
	 * @param rules
	 * @param enabledRules The rules that may be enabled by <code>rule</code>. 
	 * @param disabledRules The rules that may be disabled by <code>rule</code>. 
	 */
	public AliasMatchesIterator (DefaultGraphNextState state, ExploreCache rules, Set<Rule> enabledRules, Set<Rule> disabledRules ) {
		super(state, rules);
		this.enabledRules = enabledRules;
		this.disabledRules = disabledRules;
		priority = state.getEvent().getRule().getPriority();
		myFirstRule();
	}	@Override	public RuleMatch next() {		RuleMatch m;
		if (aliasMatchIter != null) {
			m = aliasMatchIter.next();  // exception possible
			super.rulesIter.updateMatches(super.currentRule);
			super.isEndRule = ! aliasMatchIter.hasNext();
		} else {
			m = super.next();
		}
		return m; 
//		if( aliasMatchIter != null && aliasMatchIter.hasNext() ) {//			m = aliasMatchIter.next();
//			super.rulesIter.updateMatches(super.currentRule);
//			super.isEndRule = ! aliasMatchIter.hasNext();//		} else {
//			m = super.next();//		}//		return m;	}		@Override	public boolean hasNext() {		return ((aliasMatchIter != null && aliasMatchIter.hasNext()) || super.hasNext());	}	
	@Override
	public void firstRule() {		// normally called in super() of constructor, but want to do this after enabled and disabled rules is set.
	}
	
	private void myFirstRule() {
		super.firstRule();		updateFirstRule();	}
	@Override
	public boolean nextRule() {		boolean nextrule = super.nextRule();		if( nextrule ) {			return doAliasSelection();		} else {			return false;		}	}
	
	private boolean doAliasSelection() {
		if( currentRule.getPriority() > priority ) {
			if( enabledRules.contains(currentRule)) {				return true;			} else {				return nextRule();			}		} else if( currentRule.getPriority() == priority ) {			// same priority, let's see what matched before, and filter what doesn't match anymore			updateMatches(); // done once						boolean doTrue = false;			if( aliasedRuleMatches.containsKey(currentRule)) {				//this.matchIter = aliasedRuleMatches.get(currentRule).iterator();				aliasMatchIter = aliasedRuleMatches.get(currentRule).iterator();
				// for this particular rule, the super.matchIter will not be used
				super.matchIter = aliasMatchIter;				doTrue = true;			}			if( (currentRule instanceof SPORule && ((SPORule)currentRule).hasSubRules()) || enabledRules.contains(currentRule) ) {				// it didn't match in the previous state or no matches left after rematching				doTrue = true;			}			if( doTrue ) {				return true;			}			else {				return nextRule();			}		}		else if( (currentRule instanceof SPORule && ((SPORule)currentRule).hasSubRules()) || enabledRules.contains(currentRule)) {				// its either a composite rule or the rule was enabled, or with lower priority				return true;		} else {			// this rule can impossibly match, it was not enabled and it didn't match before			// goto next rule			return nextRule();		}
	}
	
	private void updateFirstRule() {
		if( currentRule != null ) {
			doAliasSelection();
		}
	}

	private void updateMatches() {
		if( aliasedRuleMatches.size() == 0 ) {
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
	
	/** set with matched rules and the corresponding matches **/
	private Map<Rule, List<RuleMatch>> aliasedRuleMatches = new TreeMap<Rule,List<RuleMatch>>();
	private Set<Rule> enabledRules;
	private Set<Rule> disabledRules;

}
