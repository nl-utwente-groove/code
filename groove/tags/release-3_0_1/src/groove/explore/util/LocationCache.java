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

	private Set<Rule> matched;
	private Set<Rule> explored;
	private Set<Rule> failed;
	
	private GraphState state;
	
	private Iterator<Rule> iterator;
	
	private Location location;
	
	
	/** Creates a cache for given state with given location.
	 * @param location
	 * @param state
	 */
	public LocationCache(ControlLocation location, GraphState state, boolean isRandomized) {
		this.location = location;
		this.state = state;
		
		this.matched = new HashSet<Rule>();
		this.explored = new HashSet<Rule>();
		this.failed = new HashSet<Rule>();
		
		this.iterator = createIterator(isRandomized);
	}
	
	public void updateExplored(Rule rule) {
		if( !matched.contains(rule)) {
			 failed.add(rule);
		}
	}

	public void updateMatches(Rule rule) {
		matched.add(rule);
	}
	
	/** Returns those rules in the argument that did not match. *
	 * @param rules 
	 * @return Those rules in the argument that did not match
	 */
	public Set<Rule> failed(Set<Rule> rules) {
		Set<Rule> havefailed = new HashSet<Rule>();
		
		for( Rule rule : rules) {
			if( failed.contains(rule)) {
				havefailed.add(rule);
			} else if( matched.contains(rule)) {
				// do nothing
			} else if( !testMatch(rule)) {
				havefailed.add(rule);
			}
		}
		return havefailed;
	}
	
	
	/** TODO
	 * @param rules
	 * @return
	 */
	public boolean isFailAll(Set<Rule> rules) {
		for( Rule rule : rules ) {
			if( hasMatched(rule))
				return false;
		}
		return true;
	}
	
	/**
	 * On demand test if a certain rule has matches, store the result, and return the answer.
	 * 
	 * @param rule
	 * @return
	 */
	private boolean testMatch(Rule rule) {
		boolean match = new MatchesIterator(this.state, new TestMatchExploreCache(rule)).hasNext();
		if( match ) {
			matched.add(rule);
		} else {
			failed.add(rule);
		}
		return match;
	}
	
	

	private boolean hasMatched(Rule rule) {
		if( matched.contains(rule)) {
			return true;
		} else if ( failed.contains(rule)) {
			return false;
		} else {
			// test if rule matches. value will also be stored, so this is only done once.
			return testMatch(rule);
		}
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public Rule next() {
		// TODO: FIX THIS for interuptable
//		this.last = null;
		if( !iterator.hasNext())
			iterator = createIterator(iterator instanceof RandomizedIterator);
		if( !iterator.hasNext()) {
			return null;
		}
		else {
			Rule last = iterator.next();
			return last;
		}
	}

	private Iterator<Rule> createIterator(boolean isRandomized) {
		if (isRandomized) {
			return new RandomizedIterator<Rule>(location.moreRules(this));
		}
		
		Set<Rule> rules = location.moreRules(this);
		
		return location.moreRules(this).iterator();
	}
	
	public void remove() {
		// TODO Auto-generated method stub
	}

	public Location getTarget(Rule rule) {
		// TODO
		return location.getTarget(rule, this);
	}
	
	public Rule last() {
		return this.last;
	}
	private Rule last;
	
	/** TODO
	 * @return
	 */
	public Set<Rule> getExplored() {
		return this.explored;
	}

	
	/** TODO
	 * @return
	 */
	public Set<Rule> getMatched() {
		return this.matched;
	}
	
	/** TODO
	 * @return
	 */
	public Set<Rule> getFailed() {
		return this.failed;
	}
}

class TestMatchExploreCache implements ExploreCache {
	
	Iterator<Rule> it;
	
	/** @param rule */
	public TestMatchExploreCache(Rule rule) {
		HashSet<Rule> hs = new HashSet<Rule>(); 
		hs.add(rule);
		it = hs.iterator();
	}
	
	public void remove() {
	}
	
	public Rule last() {
		return null;
	}
	
	public Rule next() {
		return it.next();
	}
	
	public boolean hasNext() {
		return it.hasNext();
	}
	
	public void updateExplored(Rule rule) {
	}
	
	public void updateMatches(Rule rule) {
	}
	
	public Location getTarget(Rule rule) {
		return null;
	}
	
}