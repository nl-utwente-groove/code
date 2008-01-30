package groove.control;

import groove.explore.util.LocationCache;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.Rule;
import groove.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ControlLocation implements Location {

	private LocationAutomatonBuilder builder;

	/** the location is based on this pair of state->failureset, initialized by setStates **/
	private Set<Pair<ControlState, Set<Rule>>> states = new HashSet<Pair<ControlState, Set<Rule>>>();
	
	/** the controlstate as key, the failureset as value, initialized by setStates **/
	protected Map<ControlState, Set<Rule>> stateFailures = new HashMap<ControlState, Set<Rule>>();

	
	protected void setStates(Set<Pair<ControlState, Set<Rule>>> pairs) {
		this.states = pairs;
		
		for( Pair<ControlState, Set<Rule>> pair: pairs ) {
			this.stateFailures.put(pair.first(), pair.second());
		}
	}

	public ControlLocation(LocationAutomatonBuilder builder) {
		this.builder = builder;
	}

	// cache for target ControlLocation of a certain rule with a certain subset of the depending rules
	// every unique Set<ControlState> has exactly one ControlLocation  
	protected Map<Pair<Rule,Set<Rule>>, ControlLocation> targetLocationCache = new HashMap<Pair<Rule,Set<Rule>>,ControlLocation>();

	
	protected ControlLocation getTargetLocation(Rule rule, Set<Rule> failed) {
		Pair p = new Pair<Rule,Set<Rule>>(rule, failed);
		ControlLocation l = targetLocationCache.get(p);
		if( l == null ) {
			Set<ControlState> states = getTargetStates(rule, failed);
			l = builder.getLocation(states);
			targetLocationCache.put(p, l);
		}
		return l;
	}
	
	protected Map<Rule, Set<ControlState>> ruleSourceStates = new HashMap<Rule, Set<ControlState>>();
	protected void addRuleSource(Rule rule, ControlState source) {
		Set<ControlState> sources= ruleSourceStates.get(rule);
		if( sources == null ) {
			sources = new HashSet<ControlState>();
			ruleSourceStates.put(rule, sources);
		}
		sources.add(source);
	}
	
	/** returns the set of target states given the rule and the set of failed rules **/
	protected Set<ControlState> getTargetStates(Rule rule, Set<Rule> failed) {
		Set<ControlState> targets = new HashSet<ControlState>();

		for( ControlState state : ruleSourceStates.get(rule)) {
			boolean stateReached = true;
			for( Rule failureRule : this.stateFailures.get(state) ) {
				if( !failed.contains(failureRule)) {
					stateReached = false;
				}
			}
			if( stateReached ) {
				targets.addAll(state.targets(rule));
			}
		}
		return targets;
	}
	
	// stores a set pairs of rules (second) that must fail before the rule first becomes active
	protected Set<Pair<Rule, Set<Rule>>> ruleActiveMap = new HashSet<Pair<Rule, Set<Rule>>>();

	protected Map<Rule, Set<Rule>> ruleTargetDependencyMap = new HashMap<Rule, Set<Rule>>();
	protected void addRuleTargetDependencies(Rule rule, Set<Rule> dependencies) {
		Set<Rule> dep = this.ruleTargetDependencyMap.get(rule);
		if( dep == null ) {
			dep = new HashSet<Rule>();
			this.ruleTargetDependencyMap.put(rule, dep);
		}
		dep.addAll(dependencies);
	}
	
	
	protected Set<Rule> alwaysActiveRules = new HashSet<Rule>();
	
	protected void initialize() {

		// store the source states for every rule
		for( Pair<ControlState, Set<Rule>> pair : states) {
			for( Rule rule : pair.first().rules()) {
				addRuleSource(rule, pair.first());
			}
		}
		
		// get the rules that are enabled from the start
		for( Pair<ControlState, Set<Rule>> pair : states) {
			if( pair.second().isEmpty() ) {
				alwaysActiveRules.addAll(pair.first().rules());
			}
		}
				
		// get all activation failures for rules that have one
		// store target dependency for all rules
		for( Pair<ControlState, Set<Rule>> pair: states) {
			if( pair.second().size() > 0 ) {
				Set<Rule> failure = pair.second();
				for( Rule rule : pair.first().rules() ) {
					addRuleTargetDependencies(rule, failure);
					// if the rule is not always active then it is active as soon as  
					// this failureset is applicable
					if( ! this.alwaysActiveRules.contains(rule) ) {
						ruleActiveMap.add(new Pair<Rule, Set<Rule>>(rule, failure));
					}
				}
			}
		}
		
		
		
	}
	
	public ControlLocation(Set<ControlState> baseStates) {
		
	}
	
	@Override
	public LocationCache createCache() {
		return null;
	}

	@Override
	public Set<Rule> getFailureDependency(Rule rule) {
		// TODO Auto-generated method stub
		return null;
	}

	public Rule getNextRule(LocationCache cache) {
		return null;
	}
	
	@Override
	public Location getTarget(Rule rule, LocationCache cache) {
		// TODO Auto-generated method stub

		Set<Rule> failureDependency = this.ruleTargetDependencyMap.get(rule);
		Set<Rule> failed;
		if( failureDependency != null ) {
			failed = cache.failed(failureDependency);
		} else {
			failed = new HashSet<Rule>();
		}
		
		return getTargetLocation(rule, failed);
	}

	public Set<Rule> moreRules(LocationCache cache) {
		return allowedRules(cache.getExplored(), cache.getFailed());
	}
	
	public Set<Rule> allowedRules(Set<Rule> explored, Set<Rule> failed) {
		HashSet<Rule> result = new HashSet<Rule>();
		
		for( Rule rule : alwaysActiveRules ) {
			if( !explored.contains(rule) && !failed.contains(rule)) {
				result.add(rule);
			}
		}
		
		for( Pair<Rule, Set<Rule>> pair : ruleActiveMap ) {
			if( explored.contains(pair.first())) {
				// do nothing, rule is finished
			} else {
				boolean allFailed = true;
				for( Rule rule : pair.second() ) {
					if( !failed.contains(rule)) {
						allFailed = false;
					}
				}
				if( allFailed )
					result.add(pair.first());
			}
		}
		return result;
	}

	/**
	 * Returns wether the GraphState is in a success-control state, given that all possible
	 * transitions have been added to the graphstate, thus any rule not found in a 
	 * transition has thus failed.  
	 * 
	 * @param state 
	 * FIXME: this method can be optimized if the succes-states or the corresponding failures are in a seperate set
	 * @return
	 */
	@Override
	public boolean isSuccess(GraphState state) {
			assert !state.isClosed(): "isSuccess should only be called when (and before) closing the state";
		
			Set<Rule> rulesFound = new HashSet<Rule>();
			
			for( GraphTransition trans : state.getTransitionSet() ) {
				rulesFound.add(trans.getEvent().getRule());
			}
			
			// more expensive computation, but will only be done once
			for( Pair<ControlState, Set<Rule>> pair : states ) {
				if( pair.first().isSuccess() ) {
					if( pair.second().size() == 0 ) {
						return true;
					}
					else {
						boolean failureSucceed = true;
						for( Rule rule : pair.second() ) {
							if( rulesFound.contains(rule)) {
								failureSucceed = false;
							}
						}
						if( failureSucceed )
							return true;
					}
				}
			}
			
			return false;
	}
	

		@Override
	public String toString() {
		String toString = null;
		for( Pair<ControlState, Set<Rule>> pair : this.states ) {
			if( toString == null ) {
				toString = "";
			} else {
				toString += ",";
			}
			
			toString += failureToString(pair.second()); 
			toString += pair.first().toString();
		}
		
		return toString;
	}
	
	private String failureToString(Set<Rule> rules) {
		String retval = "";
		
		
		if( rules.size() != 0 ) {

			retval = "![";	
			boolean first = true;
			
			for( Rule rule : rules ) {
				
				if( !first )
					retval += ",";
				
				retval += rule.getName().text();
				
				first = true;
			}
		
			retval += "]";
		}
		
		return retval;
		
	}
	
}
