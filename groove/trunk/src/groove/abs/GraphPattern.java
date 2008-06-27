/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
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
 * @version $Revision $
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
	
	// OPTIM make it an iterator as a further optimisation
	/** Computes the set of possible typings of a small graph by this pattern.
	 * @param preMatched the nodes already matched
	 */
	public Collection<VarNodeEdgeMap> possibleTypings (Graph g, NodeEdgeMap preMatched, boolean symmetryReduction);
	
	/** Computes the set of possible typings of a small graph by this pattern.
	 * @param center the nodes in g to be matched to the center of this pattern
	 */
	public Collection<VarNodeEdgeMap> possibleTypings (Graph g, Node center, boolean symmetryReduction);
	
}
