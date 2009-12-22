package groove.explore.util;

import groove.control.Location;
import groove.trans.Rule;

/**
 * Caches information about the current state of the exploration of some state
 * in a graph transition system. This information concerns fully explored rules
 * (see {@link #updateExplored(Rule)}) and rules for which at least one match
 * was found (see {@link #updateMatches(Rule)}). Such a cache is an iterator
 * over rules that will avoid giving rules that have been fully explored. The
 * cache takes into account information on explored and matched rules for
 * computing the location resulting in applying a given rule.
 * @author
 * 
 */
public interface ExploreCache extends ResumableIterator<Rule> {
    /**
     * Inform the cache that a rule has been fully explored. This may prevent
     * the iterator from returning the rule in the future, even if it was not
     * yet returned in the past. If the rule matches, then
     * {@link #updateMatches(Rule)} should be invoked <i>before</i> this
     * method.
     * @param rule A fully explored rule.
     */
    public void updateExplored(Rule rule);

    /**
     * Inform the cache that a match has been found for a rule. This may
     * influence the rules that will be returned by the iterator later on.
     * @param rule A rule that has a match.
     */
    public void updateMatches(Rule rule);

    /**
     * The target location when a rule is applied.
     * @param rule
     * @return The target location when a rule is applied.
     */
    public Location getTarget(Rule rule);
}
