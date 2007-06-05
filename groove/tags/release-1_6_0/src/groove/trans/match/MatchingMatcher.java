/*
 * $Id: MatchingMatcher.java,v 1.6 2007-06-01 18:04:03 rensink Exp $
 */
package groove.trans.match;

import groove.graph.match.SearchItem;
import groove.rel.match.RegExprMatcher;
import groove.trans.DefaultGraphCondition;
import groove.trans.DefaultMatching;
import groove.trans.Matching;
import groove.trans.Rule;

import java.util.List;

/**
 * Matcher that takes conditions into account.
 * @author Arend Rensink
 * @version $Revision: 1.6 $
 */
public class MatchingMatcher extends RegExprMatcher {
	/** 
	 * Constructs a matcher on the basis of a pre-existing mapping. 
	 * @param injective flag indicating that the matching should be injective
	 */
	public MatchingMatcher(Matching mapping, boolean injective) {
        super(mapping, injective);
    }
    
    /** Specialises the return type. */
    @Override
	public DefaultMatching getMorphism() {
		return (DefaultMatching) super.getMorphism();
	}

	/**
     * Returns the graph condition of which this is a matching simulation.
     */
    protected DefaultGraphCondition getCondition() {
        return getMorphism().getCondition();
    }

	/**
	 * Search plans for matching the whole condition and
	 * the non-anchor part are stored internally and reused.
	 */
    @Override
	protected List<SearchItem> computeSearchPlan() {
		List<SearchItem> result;
    	DefaultGraphCondition condition = getCondition();
    	if (getMorphism().isEmpty() || !(condition instanceof Rule)) {
			return condition.getSearchPlan();
    	} else {
    		assert condition instanceof Rule : "Bound matching only implemented for rules";
			result = ((Rule) condition).getAnchorSearchPlan();
		}
		return result;
    }
}
