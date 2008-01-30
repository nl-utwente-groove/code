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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

// TODO does not update correctly the isEndRule flag, thus depth first strategies relying on that do not function correctly
public class AliasMatchesIterator extends MatchesIterator {

	public AliasMatchesIterator (DefaultGraphNextState state, ExploreCache rules, Set<Rule> enabledRules, Set<Rule> disabledRules ) {
		super(state, rules);
//		System.out.println("Enabled in " + state + ": " + listRules(enabledRules));
//		System.out.println("Disabled in " + state + ": " + listRules(enabledRules));
		this.enabledRules = enabledRules;
		this.disabledRules = disabledRules;
		priority = state.getEvent().getRule().getPriority();
		myFirstRule();
	}
	@Override
	public void firstRule() {
	}

//	private String listRules(Set<Rule> rules) {
//		String retval = null;
//		for( Rule rule: rules ) {
//			if( retval == null )
//				retval = rule.getName().text();
//			else
//				retval += "," + rule.getName().text();
//		}
//		return retval;
//	}
	
	public void myFirstRule() {
		super.firstRule();
		updateFirstRule();
		this.isEndRule = true;
	}
	@Override
	public boolean nextRule() {
		boolean nextrule = super.nextRule();
		
		if( nextrule ) {
			return doAliasSelection();
		} else {
			return false;
		}
	}
	
	public boolean doAliasSelection() {
		if( currentRule.getPriority() > priority ) {
			if( enabledRules.contains(currentRule)) {
				return true;
			} else {
				return nextRule();
			}
		} else if( currentRule.getPriority() == priority ) {
			// same priority, let's see what matched before, and filter what doesn't match anymore
			updateMatches();
			if( aliasedRuleMatches.containsKey(currentRule)) {
				this.matchIter = aliasedRuleMatches.get(currentRule).iterator();
				return true;
			} else if( (currentRule instanceof SPORule && ((SPORule)currentRule).hasSubRules()) || enabledRules.contains(currentRule) ) {
				// it didn't match or no matches left after rematching
				return true;
			} else {
				return nextRule();
			}
		}
		else if( (currentRule instanceof SPORule && ((SPORule)currentRule).hasSubRules())|| enabledRules.contains(currentRule)) {
				// its either a composite rule or the rule was enabled
				return true;
		} else {
			// this rule can impossibly match, it was not enabled and it didn't match before
			// goto next rule
			return nextRule();
		}
		// should not be reached
	}
	
	public void updateFirstRule() {
		if( currentRule != null ) {
			doAliasSelection();
		}
	}

	public void updateMatches() {
		if( aliasedRuleMatches.size() == 0 ) {
			// init the map
			
			for( GraphTransitionStub stub : ((AbstractGraphState)((DefaultGraphNextState) state).source()).getStoredTransitionStubs() ) {
				RuleEvent event = stub.getEvent(((DefaultGraphNextState)state).source());
				Rule rule = event.getRule();
	            List<RuleMatch> matches = aliasedRuleMatches.get(rule);
	            if ( isUseDependencies() && !disabledRules.contains(rule) || event.hasMatch(state.getGraph())) {
					if( matches == null ) {
						matches = new ArrayList<RuleMatch>();
						this.aliasedRuleMatches.put(rule, matches);
					}
					matches.add(new VirtualRuleMatch(new DefaultAliasApplication(event, (GraphNextState)state, stub)));
	            }
			}
		}
	}
	
	/**
	 * TODO: fixme, currently always enabled if this class is used... (?)
	 * @return
	 */
	private boolean isUseDependencies() {
		return true;
	}
	
	/** the matched priority in the preceeding state */
	private int priority = 0;
	
	/** set with matched rules and the corresponding matches **/
	private Map<Rule, List<RuleMatch>> aliasedRuleMatches = new TreeMap<Rule,List<RuleMatch>>();
	private Set<Rule> enabledRules;
	private Set<Rule> disabledRules;

}
