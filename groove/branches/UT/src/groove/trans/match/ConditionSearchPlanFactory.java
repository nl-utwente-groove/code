/*
 * $Id: ConditionSearchPlanFactory.java,v 1.1.1.1 2007-03-20 10:05:21 kastenberg Exp $
 */
package groove.trans.match;

import java.util.List;
import java.util.Set;

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
 * @version $Revision: 1.1.1.1 $
 */
public interface ConditionSearchPlanFactory extends SearchPlanFactory {
    /** 
     * Factory method returning a list of search items for a graph condition.
     * This extends the ordinary search plan with negative tests.
     * @param subject the condition for which a search plan is to be constructed
     * @see #createSearchPlan(Graph)
     * @see #createSearchPlan(GraphCondition, Set, Set)
     */
    public List<SearchItem> createSearchPlan(GraphCondition subject);

    /** 
     * Factory method returning a list of search items for a graph condition,
     * taking into account that a certain set of nodes and edges has been matched already.
     * This extends the ordinary search plan with negative tests.
     * @param subject the condition for which a search plan is to be constructed
     * @param preMatchedNodes the nodes of the condition that have been matched already
     * @param preMatchedEdges the edges of the condition that have been matched already
     * @see #createSearchPlan(Graph)
     */
    public List<SearchItem> createSearchPlan(GraphCondition subject, Set<Node> preMatchedNodes, Set<Edge> preMatchedEdges);
}
