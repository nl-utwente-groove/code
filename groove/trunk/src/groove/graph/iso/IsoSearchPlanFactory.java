/*
 * $Id: IsoSearchPlanFactory.java,v 1.2 2007-08-22 15:04:59 rensink Exp $
 */
package groove.graph.iso;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.match.SearchItem;
import groove.graph.match.SearchPlanFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Strategy that yields the edges in order of ascending indegree of
 * their source nodes.
 * The idea is that the "roots" of a graph (those starting in nodes with
 * small indegree) are likely to give a better immediate reduction of
 * the number of possible matches.
 * Furthermore, regular expression edges are saved to the last.
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public class IsoSearchPlanFactory implements SearchPlanFactory {
	/** 
	 * This implementation merely returns search items for the edges and nodes of the
	 * graph, in arbitrary order.
	 */
	public Iterable<SearchItem> createSearchPlan(Graph graph) {
		return createSearchPlan(graph, Collections.<Node>emptySet(), Collections.<Edge>emptySet());
	}

    /** 
     * This implementation merely returns search items for the edges and nodes of the
     * graph, in arbitrary order.
     */
    public Iterable<SearchItem> createSearchPlan(Graph graph, Collection< ? extends Node> boundNodes, Collection< ? extends Edge> boundEdges) {
        List<SearchItem> result = new ArrayList<SearchItem>();
        for (Node node: graph.nodeSet()) {
            if (! boundNodes.contains(node)) {
                result.add(createNodeSearchItem(node));
            }
        }
        for (Edge edge: graph.edgeSet()) {
            if (! boundEdges.contains(edge)) {
                result.add(createEdgeSearchItem(edge));
            }
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
