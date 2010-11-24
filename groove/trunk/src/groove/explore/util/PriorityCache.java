package groove.explore.util;

import groove.control.Location;
import groove.trans.Rule;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

/**
 * An ExploreCache for prioritizes rule sets.
 * 
 * @author Tom Staijen
 * @version $Revision $
 */
public class PriorityCache implements ExploreCache {

    /** Initializer */
    public PriorityCache(SortedMap<Integer,Set<Rule>> rules) {
        this.rules = rules;
        if (!rules.isEmpty()) {
            setPriority(rules.keySet().iterator().next());
        } else {
            setPriority(-1);
        }
        this.ruleIterator = createRuleIterator();
    }

    /**
     * Insures that - either there are no more rules to be returned and
     * ruleIterator == null - or there are still rules to be returned, and the
     * next rule to be returned is ruleIterator.next() This method is
     * idempotent.
     */
    public void assureNext() {
        while (this.ruleIterator != null && !this.ruleIterator.hasNext()) {
            decrementPriority();
            this.ruleIterator = createRuleIterator();
        }
    }

    /**
     * Returns null if currPriority is negative, or if it does not correspond to
     * a set of rules in the map.
     */

    /**
     * Decrements currPriority until it reaches a priority for which there are
     * rules or -1. Returns an iterator over the set of rules of that priority,
     * or null if -1 was reached for the current priority value.
     */
    private Iterator<Rule> createRuleIterator() {
        if (isLastPriority()) {
            return null;
        }
        Set<Rule> currRuleSet = null;
        while (getPriority() >= 0
            && (currRuleSet = this.rules.get(getPriority())) == null) {
            decrementPriority();
        }
        if (getPriority() < 0) {
            return null;
        } else {
            return currRuleSet.iterator();
        }
    }

    /**
     * Does not have effect if rule is not of the same priority as the priority
     * currently considered by the cache. [IOVKA The effect of this method is
     * not the same as the same method for a {@link SimpleCache}. For a
     * {@link SimpleCache}, the iterator always guarantees that rule won't be
     * returned in the future. If the iterator is not randomised, it turns out
     * to advance it through all "previous" rules in the pre-defined rules'
     * order. For a priority cache, this is guaranteed only if the iterator
     * currently points to a rule with same priority as rule. This is always the
     * case if {@link #updateMatches(Rule)} was called just before.]
     * 
     */
    public void updateExplored(Rule rule) {
        if (rule.getPriority() == getPriority()) {
            // one can advance the iterator if rule was not yet returned
            Iterator<Rule> it = this.rules.get(getPriority()).iterator();
            boolean met = false;
            while (it.hasNext() && (last() != null) && !met) {
                if (it.next().equals(last())) {
                    met = true;
                }
            }
            if (!met) {
                while (this.ruleIterator.hasNext()
                    && !(this.ruleIterator.next()).equals(rule)) {
                    // empty
                }
            }
        }
    }

    /**
     * Makes sure that the iterator won't iterate over rules of priority greater
     * than that of <code>rule</code>. Indeed, if <code>rule</code> matches
     * and the priorities were correctly used so far, then none of the rules
     * with bigger priority can match.
     */
    public void updateMatches(Rule rule) {
        int p = rule.getPriority();
        assert p <= getPriority() : "Rules with priority " + getPriority()
            + " should not be considered, as a rule with higher priority (" + p
            + ") matches.";
        if (getPriority() > p) {
            setPriority(p);
            this.ruleIterator = createRuleIterator();
            assureNext();
        }
        setLastPriority();
        assert getPriority() < 0 || this.rules.containsKey(getPriority()) : "Something's very wrong !";
    }

    public boolean hasNext() {
        assureNext();
        return this.ruleIterator != null;
    }

    public Rule next() {
        assureNext();
        if (this.ruleIterator != null) {
            this.last = this.ruleIterator.next();
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

    private int getPriority() {
        if (this.priority >= 0) {
            return this.priority - 1;
        } else {
            return (-this.priority) - 1;
        }
        // return currPriority;
    }

    private void decrementPriority() {
        if (this.priority > 0) {
            this.priority--;
        } else {
            assert this.priority != 0 : "Should not be called while priority = 0";
        }
        // currPriority--;
    }

    private void setPriority(int prio) {
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
        // this.lastPriority = true;
    }

    /**
     * Encodes the current priority and whether a match is found for the current
     * priority. A positive value N codes priority N-1 and no match found. A
     * negative value -N codes priority N-1 and match found. The value 0 is
     * special and corresponds to the invalid priority -1.
     */
    private int priority;

    /**
     * Set to null if no more rules are available. Otherwise, is an iterator
     * over the set of rules with priority <code>currPriority</code>.
     * @invariant ruleIterator == null implies currPriority < 0
     */
    private Iterator<Rule> ruleIterator;
    private final SortedMap<Integer,Set<Rule>> rules;
    private Rule last;

}
