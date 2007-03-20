/*
 * $Id: IsoSearchPlanFactory.java,v 1.1.1.2 2007-03-20 10:42:44 kastenberg Exp $
 */
package groove.graph.iso;

import java.util.ArrayList;
import java.util.List;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.match.SearchItem;
import groove.graph.match.SearchPlanFactory;

/**
 * Strategy that yields the edges in order of ascending indegree of
 * their source nodes.
 * The idea is that the "roots" of a graph (those starting in nodes with
 * small indegree) are likely to give a better immediate reduction of
 * the number of possible matches.
 * Furthermore, regular expression edges are saved to the last.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class IsoSearchPlanFactory implements SearchPlanFactory {
	/** 
	 * This implementation merely returns search items for the edges and nodes of the
	 * graph, in arbitrary order.
	 */
	public Iterable<SearchItem> createSearchPlan(Graph graph) {
//		Set<Node> remainingNodes = new HashSet<Node>(graph.nodeSet());
		List<SearchItem> result = new ArrayList<SearchItem>();
//		for (Edge edge: graph.edgeSet()) {
//			boolean[] matched = new boolean[edge.endCount()];
//			boolean allMatched = true;
//			for (int i = 0; i < edge.endCount(); i++) {
//				matched[i] = ! remainingNodes.remove(edge.end(i));
//				allMatched &= matched[i];
//			}
//			if (allMatched) {
//				matched = null;
//			} 
//			result.add(createEdgeSearchItem(edge, matched));
//		}
		for (Node node: graph.nodeSet()) {
			result.add(createNodeSearchItem(node));
		}
		for (Edge edge: graph.edgeSet()) {
			result.add(createEdgeSearchItem(edge));
		}
		return result;
	}

	/**
     * This implementation returns an {@link IsoNodeSearchItem}
     */
	protected SearchItem createNodeSearchItem(Node node) {
		return new IsoNodeSearchItem(node);
	}

	/**
     * This implementation returns an {@link IsoEdgeSearchItem}
     */
    protected SearchItem createEdgeSearchItem(Edge edge) {
    	return new IsoEdgeSearchItem(edge);
    }
}
