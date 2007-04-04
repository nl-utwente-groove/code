/*
 * $Id: MatchingMatcher.java,v 1.3 2007-04-04 07:04:06 rensink Exp $
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
 * @version $Revision: 1.3 $
 */
public class MatchingMatcher extends RegExprMatcher {
//	/** 
//	 * A static mapping from graph conditions to search plans for those conditions.
//	 * TODO: this is a stopgap measure until the notion of matching has been revised
//	 * in the main branch. 
//	 */
//	private static final Map<GraphCondition,List<SearchItem>> searchPlanMap = new HashMap<GraphCondition,List<SearchItem>>();
//	
//	/** 
//	 * A static mapping from graph conditions to search plans for those conditions,
//	 * where for the search plan the anchor of the condition (as an {@link SPORule})
//	 * has been pre-matched.
//	 * TODO: this is a stopgap measure until the notion of matching has been revised
//	 * in the main branch. 
//	 */
//	private static final Map<GraphCondition,List<SearchItem>> preMatchedSearchPlanMap = new HashMap<GraphCondition,List<SearchItem>>();
//	
//	/**
//	 * The factory used to create the search plans in {@link #searchPlanMap}.
//	 */
//	private static final ConditionSearchPlanFactory searchPlanFactory = new DefaultConditionSearchPlanFactory();
//	
//	/** 
//	 * Temporary method as long as the search plan maps are static.
//	 */
//	public static void clear() {
//		preMatchedSearchPlanMap.clear();
//	}
	
	/** Constructs a matcher on the basis of a pre-existing mapping. */
	public MatchingMatcher(Matching mapping) {
        super(mapping);
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
//    
//	/**
//	 * Returns the factory used for creating search plans 
//	 */
//    @Override
//	protected ConditionSearchPlanFactory getSearchPlanFactory() {
//		return searchPlanFactory;
//	}

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
			result = ((Rule) condition).getEventSearchPlan();
		}
		return result;
    }
}
