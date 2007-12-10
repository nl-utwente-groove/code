package groove.abs;

import groove.graph.Graph;
import groove.graph.Node;
import java.util.Collection;
import groove.graph.NodeEdgeMap;
import groove.rel.VarNodeEdgeMap;

/** A graph pattern is a graph with identified central node. 
 * The radius of the graph around this central node is bounded.
 * GraphPattern objects can only be obtained as a part of a PatternFamily
 * @see PatternFamily 
 * @author Iovka Boneva
 */
public interface GraphPattern extends Graph {

	
	/** The graph structure of the pattern.
	 * @return a graph
	 */
	public Graph graph ();
	
	/** The central node of the pattern. Is a node of getGraph().
	 * @return the central node
	 */
	public Node central ();
	
	/** The distance from node n to the centre of the pattern. Optional operation.
	 * @param n
	 * @return The distance from node n to the centre of the pattern.
	 * @throws NoSuchNodeException 
	 */
	public int distance (Node n) throws NoSuchNodeException;
	
	
	// TODO make it an iterator as a further optimisation
	/** Computes the set of possible typings of a small graph by this pattern.
	 * @param preMatched the nodes already matched
	 */
	public Collection<VarNodeEdgeMap> possibleTypings (Graph g, NodeEdgeMap preMatched, boolean symmetryReduction);
	
	/** Computes the set of possible typings of a small graph by this pattern.
	 * @param center the nodes in g to be matched to the center of this pattern
	 */
	public Collection<VarNodeEdgeMap> possibleTypings (Graph g, Node center, boolean symmetryReduction);
	
}
