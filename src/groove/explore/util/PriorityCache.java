package groove.explore.util;

import groove.control.Location;
import groove.trans.Rule;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

public class PriorityCache implements ExploreCache {

	public PriorityCache(SortedMap<Integer, Set<Rule>> rules, boolean isRandomized) {
		this.rules = rules;
		if (! rules.isEmpty()) {
			setPriority(rules.keySet().iterator().next());
		} else {
			setPriority(-1);
		}
		ruleIterator = createRuleIterator(isRandomized);
	}
	
	/** Insures that
	 * - either there are no more rules to be returned and ruleIterator == null
	 * - or there are still rules to be returned, and the next rule to be returned is ruleIterator.next()
	 * This method is idempotent.
	 */
	public void assureNext() {
		while( ruleIterator != null && !ruleIterator.hasNext() ) {
			decrementPriority();
			ruleIterator = createRuleIterator(ruleIterator instanceof RandomizedIterator);
		}
	}
	
	/** Returns null if currPriority is negative, or if it does not correspond to a set of rules in the map. */
	
	/** Decrements currPriority until it reaches a priority for which there are rules or -1.
	 * Returns an iterator over the set of rules of that priority, or null if -1
	 * was reached for the current priority value.
	 */
	private Iterator<Rule> createRuleIterator(boolean isRandomized) {
		if (isLastPriority()) { return null; }
		Set<Rule> currRuleSet = null;
		while (getPriority() >= 0 && (currRuleSet = this.rules.get(getPriority())) == null ) {
			decrementPriority();
		}
		if (getPriority() < 0) {
			return null; 
		} else {
			if (isRandomized) {
				return new RandomizedIterator<Rule>(currRuleSet);
			} else {
				return currRuleSet.iterator();
			}
		}
	}
	
	/** Does not have effect if rule is not of the same priority as 
	 * the priority currently considered by the cache.
	 * [IOVKA The effect of this method is not the same as the same
	 * method for a {@link SimpleCache}. For a {@link SimpleCache},
	 * the iterator always guarantees that rule won't be returned
	 * in the future. If the iterator is not randomized, it turns out
	 * to advance it through all "previous" rules in the pre-defined
	 * rules' order. 
	 * For a priority cache, this is guaranteed only if the iterator
	 * currently points to a rule with same priority as rule. This 
	 * is always the case if {@link #updateMatches(rule)} was
	 * called just before.] 
 
	 */
	public void updateExplored(Rule rule) {
		if (rule.getPriority() == getPriority()) {
			if (ruleIterator instanceof RandomizedIterator) {
				((RandomizedIterator<Rule>) ruleIterator).removeFromIterator(rule);
			} else {
				// one can advance the iterator if rule was not yet returned
				Iterator<Rule> it = rules.get(getPriority()).iterator();
				boolean met = false;
				while (it.hasNext() && (last() != null) && !met) {
					if (it.next().equals(last())) {
						met = true;
					}
				}
				if (! met) {
					while (ruleIterator.hasNext() && ! (ruleIterator.next()).equals(rule)) {
						// empty
					}
				}
			}
		}
	}	
	
	/**
	 * Makes sure that the iterator won't iterate over rules of priority
	 * greater than that of <code>rule</code>. Indeed, if <code>rule</code>
	 * matches and the priorities were correctly used so far, then none
	 * of the rules with bigger priority can match.
	 */
	public void updateMatches(Rule rule) {
		int p = rule.getPriority();
		assert p <= this.getPriority() : "Rules with priority " + getPriority() + " should not be considered, as a rule with higher priority (" + p +") matches.";
		if (this.getPriority() > p) {
			this.setPriority(p);
			ruleIterator = createRuleIterator(ruleIterator instanceof RandomizedIterator);
			assureNext();
		}
		setLastPriority();
		assert getPriority() < 0 || rules.containsKey(getPriority()) : "Something's very wrong !";
	}

	public boolean hasNext() {
		assureNext();
		return ruleIterator != null;
	}
	
	public Rule next() {
		assureNext();
		if (ruleIterator != null) {
			this.last = ruleIterator.next();
			return this.last;
		} else {
			throw new NoSuchElementException();
		}
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public Location getTarget(Rule rule) {
		return null;
	}

	public Rule last() {
		return this.last;
	}
	
	private int getPriority () {
		if (this.priority >= 0) {
			return this.priority - 1;
		} else {
			return (- this.priority)-1;
		}
		// return currPriority;
	}
	private void decrementPriority () {
		if (this.priority > 0) {
			this.priority--;
		} else {
			assert true : "Should never happen";
		}
		//currPriority--;
	}
	private void setPriority (int prio) {
		this.priority = prio + 1;
		// this.currPriority = prio;
	}
	private boolean isLastPriority() {
		return this.priority <= 0;
		// return lastPriority;
	}
	private void setLastPriority() {
		if (this.priority > 0) {
			this.priority = -this.priority;
		}
		//this.lastPriority = true;
	}
	
	
	
//	/** Set to a negative value when no more need to iterate over priorities. */
//	private int currPriority;
//	/** Set to true when a match is found in the current priority. */
//	private boolean lastPriority = false;
	
	/** Encodes the current priority and whether a match is found for the current priority. 
	 * A positive value N codes priority N-1 and no match found.
	 * A negative value -N codes priority N-1 and match found.
	 * The value 0 is special and corresponds to the invalid priority -1.
	 */
	private int priority;
	
	
	
	/** Set to null if no more rules are available. Otherwise, is an iterator
	 * over the set of rules with priority <code>currPriority</code>.
	 * @invariant ruleIterator == null implies currPriority < 0
	 */
	private Iterator<Rule> ruleIterator;
	private SortedMap<Integer, Set<Rule>> rules;
	private Rule last;

}
