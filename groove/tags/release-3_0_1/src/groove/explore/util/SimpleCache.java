package groove.explore.util;

import groove.control.Location;
import groove.trans.Rule;

import java.util.Collection;
import java.util.Iterator;

/** A cache that iterates over all rules, regardless any priorities or
 * control information.
 * @author 
 *
 */
public class SimpleCache implements ExploreCache {

	private Collection<Rule> rules;
	private Iterator<Rule> internalIterator;
	
	/** Creates a simple cache over a set of rules.
	 * @param rules
	 */
	public SimpleCache(Collection<Rule> rules, boolean isRandomized) {
		this.last = null;
		this.rules = rules;
		if (isRandomized) {
			internalIterator = new RandomizedIterator<Rule>(rules);
		}
		else {
			internalIterator = rules.iterator();
		}
	}
	
	public void updateExplored(Rule rule) {
		if (internalIterator instanceof RandomizedIterator) {
			((RandomizedIterator<Rule>) internalIterator).removeFromIterator(rule);
		} else {
			// we can advance the iterator to rule, if that rule was not returned before
			// this however requires the additional field rules, which otherwise is not needed
			Iterator<Rule> it = rules.iterator();
			Rule r;
			boolean met = false;
			while (it.hasNext() && (last() != null) && !met) {
				if (it.next().equals(rule)) {
					met = true;
				}
			}
			if (! met) {
				while (internalIterator.hasNext() && ! internalIterator.next().equals(rule)) {
					// empty
				}
			}
		}
	}

	public void updateMatches(Rule rule) {
		// does nothing
	}

	public boolean hasNext() {
		return internalIterator.hasNext();
	}

	public Rule next() {
		this.last = internalIterator.next();
		return this.last;
	}

	public void remove() {
		internalIterator.remove();
	}
	
	public Location getTarget(Rule rule) {
		return null;
	}
	
	public Rule last() {
		return this.last;
	}
	
	private Rule last;
}
