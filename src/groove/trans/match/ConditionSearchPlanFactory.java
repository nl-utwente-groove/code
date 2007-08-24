/*
 * $Id: ConditionSearchPlanFactory.java,v 1.4 2007-08-24 17:34:50 rensink Exp $
 */
package groove.trans.match;

import java.util.Collection;
import java.util.List;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.match.SearchItem;
import groove.graph.match.SearchPlanFactory;
import groove.trans.GraphCondition;

/**
 * Interface that offers the functionality of creating a list of
 * graph elements for a given graph, in the order in which they should be
 * matched to minimized backtracking.
 * @author Arend Rensink
 * @version $Revision: 1.4 $
 */
@Deprecated
public interface ConditionSearchPlanFactory extends SearchPlanFactory {
    /** 
     * Factory method returning a list of search items for a graph condition.
     * This extends the ordinary search plan with negative tests.
     * @param subject the condition for which a search plan is to be constructed
     * @see #createSearchPlan(Graph)
     * @see #createSearchPlan(GraphCondition, Collection, Collection)
     */
    public List<SearchItem> createSearchPlan(GraphCondition subject);

    /** 
     * Factory method returning a list of search items for a graph condition,
     * taking into account that a certain set of nodes and edges has been matched already.
     * This extends the ordinary search plan with negative tests.
     * @param subject the condition for which a search plan is to be constructed
     * @param boundNodes the nodes of the condition that have been matched already
     * @param boundEdges the edges of the condition that have been matched already
     * @see #createSearchPlan(GraphCondition, Collection, Collection)
     */
    public List<SearchItem> createSearchPlan(GraphCondition subject, Collection<? extends Node> boundNodes, Collection<? extends Edge> boundEdges);
}
