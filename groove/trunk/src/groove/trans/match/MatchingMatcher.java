/*
 * $Id: MatchingMatcher.java,v 1.1.1.2 2007-03-20 10:42:57 kastenberg Exp $
 */
package groove.trans.match;

import groove.graph.NodeEdgeMap;
import groove.graph.match.SearchItem;
import groove.rel.match.RegExprMatcher;
import groove.trans.GraphCondition;
import groove.trans.Matching;
import groove.trans.SPORule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Matcher that takes conditions into account.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class MatchingMatcher extends RegExprMatcher {
	/** 
	 * A static mapping from graph conditions to search plans for those conditions.
	 * TODO: this is a stopgap measure until the notion of matching has been revised
	 * in the main branch. 
	 */
	private static final Map<GraphCondition,List<SearchItem>> searchPlanMap = new HashMap<GraphCondition,List<SearchItem>>();
	
	/** 
	 * A static mapping from graph conditions to search plans for those conditions,
	 * where for the search plan the anchor of the condition (as an {@link SPORule})
	 * has been pre-matched.
	 * TODO: this is a stopgap measure until the notion of matching has been revised
	 * in the main branch. 
	 */
	private static final Map<GraphCondition,List<SearchItem>> preMatchedSearchPlanMap = new HashMap<GraphCondition,List<SearchItem>>();
	
	/**
	 * The factory used to create the search plans in {@link #searchPlanMap}.
	 */
	private static final ConditionSearchPlanFactory searchPlanFactory = new DefaultConditionSearchPlanFactory();
	
	/** 
	 * Temporary method as long as the search plan maps are static.
	 */
	public static void clear() {
		searchPlanMap.clear();
		preMatchedSearchPlanMap.clear();
	}
	
	/** Constructs a matcher on the basis of a pre-existing mapping. */
	public MatchingMatcher(Matching mapping) {
        super(mapping);
    }
    
    /** Specialises the return type. */
    @Override
	public Matching getMorphism() {
		return (Matching) super.getMorphism();
	}

	/**
     * Returns the graph condition of which this is a matching simulation.
     */
    protected GraphCondition getCondition() {
        return getMorphism().getCondition();
    }
    
	/**
	 * Returns the factory used for creating search plans 
	 */
	protected ConditionSearchPlanFactory getSearchPlanFactory() {
		return searchPlanFactory;
	}

	/**
	 * Search plans for matching the whole condition and
	 * the non-anchor part are stored internally and reused.
	 */
    @Override
	protected List<SearchItem> computeSearchPlan() {
		List<SearchItem> result;
    	GraphCondition condition = getCondition();
//    	if (!(condition instanceof Rule) || getMorphism().isEmpty()) {
    	if (getMorphism().isEmpty()) {
			result = searchPlanMap.get(condition);
			if (result == null) {
				result = getSearchPlanFactory().createSearchPlan(condition);
				searchPlanMap.put(condition, result);
			}
    	} else {
			result = preMatchedSearchPlanMap.get(condition);
			if (result == null) {
				NodeEdgeMap elementMap = getMorphism().elementMap();
				result = getSearchPlanFactory().createSearchPlan(condition, elementMap.nodeMap().keySet(), elementMap.edgeMap().keySet());
				preMatchedSearchPlanMap.put(condition, result);
			}
		}
		return result;
    }
}
