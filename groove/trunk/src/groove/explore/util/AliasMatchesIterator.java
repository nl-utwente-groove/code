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
		return super.next();
		//		RuleMatch m;
//		if (aliasMatchIter != null) {
//			if (! aliasMatchIter.hasNext()) {
//				throw new NoSuchElementException();
//			}
//			m = aliasMatchIter.next(); 
//			super.rulesIter.updateMatches(super.currentRule);
//			super.isEndRule = ! aliasMatchIter.hasNext();
//		} else {
//			m = super.next();
//		}
//		return m;
		
		
// Tom's version		
//		if( aliasMatchIter != null && aliasMatchIter.hasNext() ) {//			m = aliasMatchIter.next();
//			super.rulesIter.updateMatches(super.currentRule);
//			super.isEndRule = ! aliasMatchIter.hasNext();//		} else {
//			m = super.next();//		}//		return m;	}	
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
	
	// TODO what is this method supposed to do, and to return ?
	// this method may increment the rule
	// it may also change the value of matchIter, in order to set it to an alias matches iterator
	
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
			this.matchIter = aliasedRuleMatches.get(currentRule).iterator();
			return;
		}
		
		if (currentRule.getPriority() > priority && ! enabledRules.contains(currentRule)) {
			this.matchIter = new EmptyMatchIter();
			return;
		}
		
		if (currentRule.getPriority() == priority && 
				! ( (currentRule instanceof SPORule && ((SPORule)currentRule).hasSubRules()) || enabledRules.contains(currentRule) ) ) {
			this.matchIter = new EmptyMatchIter();
			return;
		}
		
			
		
//		
//		if( currentRule.getPriority() > priority ) {
//			if( enabledRules.contains(currentRule) ) {
//				// nothing to do, normal exploration//			} else {//				this.matchIter = new EmptyMatchIter();//			}//		} else if( currentRule.getPriority() == priority ) {////			// same priority, let's see what matched before, and filter what doesn't match anymore//			updateMatches(); // done once//			
//			boolean doTrue = false;//			if( aliasedRuleMatches.containsKey(currentRule)) {//				this.matchIter = aliasedRuleMatches.get(currentRule).iterator();//				// aliasMatchIter = aliasedRuleMatches.get(currentRule).iterator();
//				// for this particular rule, the super.matchIter will not be used
//				// super.matchIter = aliasMatchIter;//				doTrue = true;//			}//			if( (currentRule instanceof SPORule && ((SPORule)currentRule).hasSubRules()) || enabledRules.contains(currentRule) ) {//				// it didn't match in the previous state or no matches left after rematching//				doTrue = true;//			}//			if( doTrue ) {//				return true;//			}//			else {//				this.matchIter = new EmptyMatchIter();//			}//		}//		else if( (currentRule instanceof SPORule && ((SPORule)currentRule).hasSubRules()) || enabledRules.contains(currentRule)) {//				// its either a composite rule or the rule was enabled, or with lower priority//				// nothing to do, normal exploration//		} else {//			// this rule can impossibly match, it was not enabled and it didn't match before//			this.matchIter = new EmptyMatchIter();	//		}
//		
//		// TODO remove at the end
//		return false;
		
	}
	
	// TODO : what is this method supposed to do ?
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

	/** Is used as value for the matchIter when we know that it is empty. */
	private class EmptyMatchIter implements Iterator<RuleMatch> {

		@Override
		public boolean hasNext() { return false; }

		@Override
		public RuleMatch next() { throw new NoSuchElementException(); }

		@Override
		public void remove() { throw new UnsupportedOperationException(); }
		
	}
	
	
}
