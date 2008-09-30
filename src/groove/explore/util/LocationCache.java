package groove.explore.util;

import groove.control.ControlLocation;
import groove.control.Location;
import groove.lts.GraphState;
import groove.trans.Rule;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/** An {@link ExploreCache} to be used with controlled exploration.
 * @author 
 *
 */
public class LocationCache implements ExploreCache {
	/** Creates a cache for given state with given location.
	 * @param location
	 * @param state
	 */
	public LocationCache(ControlLocation location, GraphState state, boolean isRandomized) {
		this.location = location;
		this.state = state;
		
		this.matched = new HashSet<Rule>();
		this.failed = new HashSet<Rule>();
		
		this.iterator = createIterator(location, isRandomized);
	}
	
	public void updateExplored(Rule rule) {
		if( !matched.contains(rule)) {
			 failed.add(rule);
		}
	}

	public void updateMatches(Rule rule) {
		matched.add(rule);
	}
	
	public boolean hasNext() {
		if( iterator == null ) {
			return false;
		}
		else if( iterator.hasNext() ) {
			return true;
		} else {
			iterator = createIterator(location, iterator instanceof RandomizedIterator);
			if( iterator.hasNext() ) {
				return true;
			} else {
				iterator = null;
				return false;
			}
		}
	}

	public Rule next() {
		// TODO: FIX THIS for interuptable
		if( iterator == null ) {
			return null;
		}
		if (!iterator.hasNext()) {
			iterator = createIterator(location, iterator instanceof RandomizedIterator);
		}
		if (!iterator.hasNext()) {
			iterator = null;
			return null;
		} else {
			Rule last = iterator.next();
			return last;
		}
	}

	private Iterator<Rule> createIterator(Location location, boolean isRandomized) {
		Set<Rule> enabledRules = location.getEnabledRules(getMatched(), getFailed());
		if (isRandomized) {
			return new RandomizedIterator<Rule>(enabledRules);
		} else {
			return enabledRules.iterator();
		}
	}
	
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public Location getTarget(Rule rule) {
		return location.getTarget(rule, getFailedRules(rule));
	}
	
	/** 
	 * Returns the subset of the dependency of a given rule
	 * consisting of those rules that do not match in this cache's state.
	 */
	private Set<Rule> getFailedRules(Rule applicableRule) {
		Set<Rule> result = new HashSet<Rule>();
		for( Rule rule : location.getDependency(applicableRule)) {
			if( failed.contains(rule)) {
				result.add(rule);
			} else if( matched.contains(rule)) {
				// do nothing
			} else if( testMatch(rule)) {
				matched.add(rule);
			} else {
				failed.add(rule);
				result.add(rule);
			}
		}
		return result;
	}

	/**
	 * On demand test if a certain rule has matches, store the result, and return the answer.
	 */
	private boolean testMatch(Rule rule) {
		boolean match = rule.hasMatch(this.state.getGraph());
		if( match ) {
			matched.add(rule);
		} else {
			failed.add(rule);
		}
		return match;
	}

	public Rule last() {
		return this.last;
	}
		
	/** 
	 * Returns the set of rules known to be applicable in this cache's state.
	 */
	public Set<Rule> getMatched() {
		return this.matched;
	}
	
	/**
	 * Returns the set of rules known to be inapplicable in this cache's state.
	 */
	public Set<Rule> getFailed() {
		return this.failed;
	}
	
	/** The set of rules that are known to match in this cache's state. */
	private final Set<Rule> matched;
	/** The set of rules that are known not to match in this cache's state. */
	private final Set<Rule> failed;
	/** The state on which this cache works. */
	private final GraphState state;
	private Iterator<Rule> iterator;
	
	private final Location location;
	private Rule last;
}
