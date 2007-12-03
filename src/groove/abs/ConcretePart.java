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
 * $Id: ConcretePart.java,v 1.2 2007-12-03 09:42:24 iovka Exp $
 */
package groove.abs;

import groove.graph.DefaultEdge;
import groove.graph.DefaultMorphism;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.graph.NodeFactory;
import groove.rel.VarNodeEdgeMap;
import groove.trans.SystemRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/** Contains all algorithms used for constructing the graph structure of a concrete part of a materialisation.
 * @author Iovka Boneva
 * @version $Revision $
 */
public class ConcretePart {
	
	/** Associates types (graph patterns) with nodes */ 
	public interface Typing {
		/** The type of a node.
		 * @param n
		 * @return The type of <code>n</code>.
		 */
		GraphPattern typeOf(Node n);
	}
	/** Associates a types (graph patterns) and typing morphisms with nodes. */
	interface SubTyping extends Typing {
		/** The typing morphism for the neighbourhood of a node.
		 * @param n
		 * @return The typing morphism for the neighbourhood of <code>n</code>.
		 */
		NodeEdgeMap typeMapOf (Node n);
	}
	
	/** Computes the neighbourhoods of nodes that are not central.
	 * @param family The pattern family used for computing the neighbourhoods. 
	 * @require the type mapping contains types for all central nodes
	 * @ensure after calling this method, the type mapping will be empty
	 * @throws ExceptionIncompatibleWithMaxIncidence when some of the neighbourhoods is not compatible with the family
	 */
	private void initNeigh (PatternFamily family) throws ExceptionIncompatibleWithMaxIncidence {
		assert this.neigh == null : "Double initialisation of neighbourhoods.";
		checkInvariants();
		this.neigh = new HashMap<Node, Graph>(this.graph.nodeSet().size() - this.type.size());
		for (Node n : this.graph.nodeSet()) {
			if (! this.centerNodes.contains(n)) {
				// ExceptionIncompatibleWithMaxIncidence possible when the neighbourhood has become too large
				this.neigh.put(n, family.getNeighInGraph(this.graph, n));
			}
		}
		this.type = null;
		checkInvariants();
	}
	
	/** The set of nodes in this.graph() that are at a certain distace from this.center()
	 * @param d
	 * @return The set of nodes in this.graph() that are at distace d from this.center()
	 */
	Set<Node> nodesAtDist (int d) {
		HashSet<Node> result = new HashSet<Node>();
		HashSet<Node> visited = new HashSet<Node>();
		result.addAll(centerNodes());
		visited.addAll(result);
		// computes the nodes at distance i
		for (int i = 1; i <= d; i++) {
			ArrayList<Node> temp = new ArrayList<Node>();
			for (Node n : result) {
				for (Edge ee : this.graph().edgeSet(n)) {
					DefaultEdge e = (DefaultEdge) ee;
					if (e.source() != e.target()) {
						Node nn = e.end(1 - e.endIndex(n));
						if (! visited.contains(nn)) {
							temp.add(nn);
						}
					}
				}
			}
			result = new HashSet<Node>(temp);
			visited.addAll(result);
		}
		return result;
	}
	
	/** Computes the set of ConcretePart that extend a graph with neighbourhoods for all its nodes.
	 * @param center The base graph that will be extended
	 * @param typing Gives the desired type for all nodes in <code>center</code>
	 * @param family The pattern family from which types come
	 * @param symmetryReduction indicates whether symmetry reduction is to be performed while computing typing morphisms
	 * @return The set of extensions of <code>center</code>
	 * @require typing.get(n) != null for all n in center.nodeSet()
	 */
	public static Collection<ConcretePart> extensions (Graph center, Typing typing, PatternFamily family, boolean symmetryReduction, NodeFactory nodeFactory) {
		ArrayList<ConcretePart> result = new ArrayList<ConcretePart>();
		ArrayList<ConcretePart> temp;
	
		ConcretePart base = new ConcretePart(center, new HashMap<Node,Morphism>(), new HashSet<Node>(center.nodeSet()));
		result.add(base);
		for (Node n : center.nodeSet()) {
			temp = new ArrayList<ConcretePart>();
			for (ConcretePart cp : result) {
				temp.addAll(cp.extension(n, typing.typeOf(n), null, family, symmetryReduction, nodeFactory));
			}
			result = temp;
		}
		// This allows to remove some impossible extensions
		Iterator<ConcretePart> it = result.iterator();
		while (it.hasNext()) {
			try {
				it.next().initNeigh(family);
			} catch (ExceptionIncompatibleWithMaxIncidence e) {
				it.remove();
			}
		}
		return result;
	}
	
	/** Computes the set of ConcretePart that extend a graph with neighbourhoods for some of its nodes.
	 * This method takes into account pre-computed neighbourhood typings for some nodes.
	 * @param concrPart The concrete part which graph will be extended.
	 * @param nodes The nodes to which neighbourhood should be added.
	 * @param typing Gives the desired type and typing morphism for all nodes in <code>center</code>.
	 * @param family The pattern family from which types come
	 * @param symmetryReduction indicates whether symmetry reduction is to be performed while computing typing morphisms
	 * @return The set of extensions of <code>center</code>
	 * @require typing.get(n) != null for all n in center.nodeSet()
	 * TODO this method is not optimal, as it computes concrete parts first
	 */
	static Collection<Graph> extensions (ConcretePart concrPart, Collection<Node> nodes, SubTyping typing, PatternFamily family, boolean symmetryReduction, NodeFactory nodeFactory) {
		ArrayList<ConcretePart> concrParts = new ArrayList<ConcretePart>();
		ArrayList<ConcretePart> temp;
	
		ConcretePart base = new ConcretePart(concrPart.graph(), new HashMap<Node,Morphism>(), concrPart.centerNodes);
		concrParts.add(base);
		for (Node n : nodes) {
			temp = new ArrayList<ConcretePart>();
			for (ConcretePart cp : concrParts) {
				assert typing.typeMapOf(n) != null : "Something's wrong.";
				temp.addAll(cp.extension(n, typing.typeOf(n), typing.typeMapOf(n), family, symmetryReduction, nodeFactory));
			}
			concrParts = temp;
		}
	
		// This part allows to remove impossible concrete parts.
		Iterator<ConcretePart> it = concrParts.iterator();
		while (it.hasNext()) {
			try {
				it.next().initNeigh(family);
			} catch (ExceptionIncompatibleWithMaxIncidence e) {
				it.remove();
			}
		}
		ArrayList<Graph> result = new ArrayList<Graph>(concrParts.size());
		for (ConcretePart cp : concrParts) {
			result.add(cp.graph());
		}
		return result;
	}	
	
	/** Constructs the extensions of the concrete part by ensuring a complete neighbourhoods of the node <code>n</code>
	 * w.r.t. its desired type <code>typeN</code>, and eventually with pre-computed typing of the existing neighbourhood. 
	 * @param n (final) the node whose possible neighbourhoods are constructed
	 * @param typeN (final) the desired type of the node n
	 * @param preTyping (final) The typing from part of the neighbourhood of n into typeN. If null, then all such matchings should be considered.
	 * @param symmetryReduction indicates whether symmetry reduction is to be performed while computing typing morphisms
	 * @return the set of all possible extensions of the concrete part
	 * @require n is a node in this.graph() 
	 * @ensure all concrete parts in the result have this.concrPart as common subgraph
	 */
	private Collection<ConcretePart> extension (Node n, GraphPattern typeN, NodeEdgeMap preTyping, PatternFamily family, boolean symmetryReduction, NodeFactory nodeFactory) {
		assert this.graph.containsElement(n) : "Incorrect node";
		checkInvariants();
		
		ArrayList<ConcretePart> result = new ArrayList<ConcretePart>();
		
		// find all morphisms from the neighbourhood of node into its type
		// end enlarge the concrete part according to each morphism
		Graph neighN = null;
		try {
			neighN = family.getNeighInGraph(this.graph, n);
		} catch (ExceptionIncompatibleWithMaxIncidence e) {
			// TODO What to do in this case ? Normally it should never occur, as
			// in concrPart.graph() all nodes have exact neighbourhood or incomplete neighbourhood
			e.printStackTrace();
		}
		NodeEdgeMap baseMatch;
		if (preTyping != null) {
			baseMatch = preTyping;
		} else {
			baseMatch = new NodeEdgeHashMap();
			baseMatch.putNode(n, typeN.central());
		} 
		for (NodeEdgeMap m : typeN.possibleTypings(neighN, baseMatch, symmetryReduction)) {
			result.addAll(this.extensionsByMorphism(m, typeN, n, family, nodeFactory));
		}		
//		for (NodeEdgeMap m : Util.getInjMatchesIter(neighN, typeN, baseMatch)) {
//			result.addAll(this.extensionsByMorphism(m, typeN, n, family));
//		}
		 
		result.trimToSize();
		
		// ensure postcondition
		if (Util.ea()) {
			for (ConcretePart cp : result) {
				Util.checkSubgraph(this.graph, cp.graph);
			}
		}
		checkInvariants();
		return result;
	}
	
	/** Computes all possible extensions of the concrete part by preserving a morphism centered in a node.
	 * Extensions are obtained by adding the codomain of the morphism, and trying to merge the newly added
	 * nodes with existing ones in all possible ways so that existing neighbourhoods are preserved.
	 * Used by extension(Node, GraphPattern)
	 * @param morph a map representing a morphism from the neighbourhood of <code>node</code> into its type.
	 * @param cod the codomain of morph
	 * @param node the central node in <code>morph</code>
	 * @return the set of all possible extensions
	 * @require morph domain is a subgraph of this.graph()
	 * @require morph is a total injective morphism
	 * @see #extension(Node, GraphPattern, PatternFamily)
	 */
	private Collection<ConcretePart> extensionsByMorphism (NodeEdgeMap morph, Graph cod, Node node, PatternFamily family, NodeFactory nodeFactory) {
		checkInvariants();
		
		Graph baseGraph = this.graph.clone();
		NodeEdgeMap baseMorphMap = morph.clone();
		Util.dunion(baseGraph, cod, baseMorphMap, nodeFactory);
		
		// possible merges of node in the baseConcrPartGraph : 
		// all newly added node in baseGraph wrt this.graph() may be 
		// merged with some existing node in baseGraph
		Set<Node> mergeableNodesB = new HashSet<Node>(); // new nodes
		Set<Node> mergeableNodesA = new HashSet<Node>(); // possible candidates for merging
		for (Node n : baseGraph.nodeSet()) {
			if (baseMorphMap.containsKey(n)) {
				if (! this.graph.containsElement(n)) { mergeableNodesB.add(n); }
			} 
			else { 	mergeableNodesA.add(n); }
		}
		// any of mergeableNodesB can be merged with any of mergeableNodesA (or with none)
		// no repetitions allowed (i.e. if x,y \in B, x merged with a, y merged with b, then a != b)
		
		ArrayList<Graph> resGraphs = new ArrayList<Graph>();
		ArrayList<NodeEdgeMap> resMorphisms = new ArrayList<NodeEdgeMap>();
		ArrayList<Set<Node>> unusedNodes = new ArrayList<Set<Node>>(); // usedNodes \subseteq mergeableNodesA
		// invariant resGraphs[i], resMorphisms[i], usedNodes[i] are such that : 
		//  resGraphs[i] : a graph obtained by merging in baseGraph
		//  resMorphisms[i] : an update of baseMorph wrt to the mergings that allowed to obtain resGraphs[i] from baseGraph
		//  usedNodes[i] contains exactly the nodes in mergeableNodesA that have not been used for merging in resGraphs[i] 
		
		// temporal variables for updating the previous ones
		ArrayList<Graph> tempGraphs = new ArrayList<Graph>();
		ArrayList<NodeEdgeMap> tempMorphisms = new ArrayList<NodeEdgeMap>();
		ArrayList<Set<Node>> tempUnusedNodes = new ArrayList<Set<Node>>();
		
		resGraphs.add(baseGraph);
		resMorphisms.add(baseMorphMap);
		unusedNodes.add(new HashSet<Node>(mergeableNodesA));
		
		// postcond : resGraphs contains graphs with all possible mergings of already iterated nodes (including no merging of iterated nodes)
		for (Node n : mergeableNodesB) {
			tempGraphs = new ArrayList<Graph>();
			tempMorphisms = new ArrayList<NodeEdgeMap>();
			tempUnusedNodes = new ArrayList<Set<Node>>();
			
			// merging node n with any of the unused nodes for merging, this for any of the partial results
			for (int i = 0; i < resGraphs.size(); i++) {
				// Add the one without merging
				tempGraphs.add(resGraphs.get(i).clone());
				tempMorphisms.add(resMorphisms.get(i));
				tempUnusedNodes.add(new HashSet<Node>(unusedNodes.get(i)));
				
				// Add the ones with merging
				for (Node nn : unusedNodes.get(i)) {
					// trying to merge n and nn
					Graph g = resGraphs.get(i).clone();
					g.mergeNodes(n, nn);
					NodeEdgeMap map = resMorphisms.get(i).clone();
					map.putNode(nn, map.getNode(n));
					map.removeNode(n);
					Set<Node> unused = new HashSet<Node>(unusedNodes.get(i));
					unused.remove(nn);
					
					tempGraphs.add(g);
					tempMorphisms.add(map);
					tempUnusedNodes.add(unused);
				}
			}
			resGraphs = tempGraphs;
			resMorphisms = tempMorphisms;
			unusedNodes = tempUnusedNodes;
		}
		
		// now the resXXX tables contain enough information for constructing the resulting concrete parts
		ArrayList<ConcretePart> result = new ArrayList<ConcretePart>();
		for (int i = 0; i < resGraphs.size(); i++) {
			HashMap<Node,Morphism> newCpType = new HashMap<Node,Morphism>(this.type);			
			// construct the morphism corresponding to the resMorphisms(i) map
			Morphism resCpMorph = null;
			try {
				resCpMorph = new DefaultMorphism(family.getNeighInGraph(resGraphs.get(i), node), cod, resMorphisms.get(i));
			} catch (ExceptionIncompatibleWithMaxIncidence exc) {
				// If exception, then the actually constructed extension is incorrect  (the neighbourhood has become bigger that allowed, thus than its type)
				continue;
			}

			// Check that the neighbourhood has not became bigger than its type
			Collection<VarNodeEdgeMap> extensions = Util.getMatchSet(resCpMorph.dom(), resCpMorph.cod(), resMorphisms.get(i)); 
			if (extensions.isEmpty()) {
				// the currently constructed extension is incorrect (the neighbourhood has become bigger than its type)
				continue;
			}
			assert extensions.size() == 1 : "Too many extensions";
			newCpType.put(node, resCpMorph);
			ConcretePart resCP = new ConcretePart(resGraphs.get(i), newCpType, this.centerNodes);
			if (resCP.checkCorrect(family)) {
				result.add(resCP);
			}
		}
		result.trimToSize();
		checkInvariants();
		return result;
	}
	
	/** Checks whether this concrete part is correct.
	 * A concrete part is correct when the neighbourhood of all typed node complies with its type.
	 * The unique source of incorrectness considered is the presence of additional edges.
	 * @return true if the concrete part is correct
	 */
	private boolean checkCorrect (PatternFamily family) {
		for (Map.Entry<Node, Morphism> entry : this.type.entrySet()) {
			Graph neighN = null;
			try {
				neighN = family.getNeighInGraph(this.graph, entry.getKey());
			} catch (ExceptionIncompatibleWithMaxIncidence e) {
				return false;
			}
			NodeEdgeMap typeMorphN = this.type.get(entry.getKey());
			for (Edge e: neighN.edgeSet()) {
				if (typeMorphN.mapEdge(e) == null) {
					return false;
				}
			}
		}
		return true;
	}

	// TODO this may be a parameter (testing structure equality is costly)
	@Override
	public boolean equals (Object o) {
		return this == o;
	}
	
	
	// --------------------------------------------------------------------------------------
	// FIELDS, CONSTRUCTORS, STANDAD METHODS
	// --------------------------------------------------------------------------------------
	/** The graph structure */
	private Graph graph;
	/** Associates with nodes their neighbourhood. Initialised after completion of the graph part. */
	private Map<Node,Graph> neigh;
	/** Associates typing morphisms with some nodes of the graph.
	 * Each such morphism is from the neighbourhood of the node into its type pattern.
	 * Used only during the computation.
	 */ 
	private Map<Node, Morphism> type;
	/** The set of center nodes.
	 * @invariant (A) whenever neigh != null, neigh.keySet() union centerNodes is graph.nodeSet() 
	 */
	private Set<Node> centerNodes;

	/** Default constructor, by aliasing.
	 * @param graph
	 * @param type
	 * @ensure Fixes <code>graph</code> as side effect.
	 */
	private ConcretePart (Graph graph, Map<Node, Morphism> type, Set<Node> centerNodes) {
		this.graph = graph;
		this.graph.setFixed();
		this.type = type;
		this.centerNodes = centerNodes; 
	}

	public final Graph graph() { return this.graph; }

	/** The neighbourhood mapping, associating with each node the subgraph that is its neighbourhood. 
	 * This operation is not available if the neighbourhood mapping was not explicitely computed.
	 * @see #initNeigh
	 * @return The neighbourhood mapping, associating with each node the subgraph that is its neighbourhood.
	 */
	final Map<Node,Graph> neigh() {
		if (this.neigh == null) {
			throw new UnsupportedOperationException();
		}
		return this.neigh;
	}
	/** The set of center nodes of the concrete part.
	 * These are exactly the nodes for which the concrete part has full neighbourhood 
	 * (thus, the neighbourhood mapping does not contain these nodes as keys). 
	 * @return The set of center nodes of the concrete part.
	 */
	final Set<Node> centerNodes() { return this.centerNodes; 	}
	@Override
	public String toString () {
		String result = new String();
		result += this.graph.toString(); 
		if (this.neigh != null) { result += "\nConcr part (neigh) : " + this.neigh; }
		return result;
	}
	
	// ----------------------------------------------------------------------------------
	// INVARIANTS 
	// ----------------------------------------------------------------------------------
	/** */
	private void checkInvariants() {
		if (! Util.ea()) { return; }
		checkInvA();
	}
	
	private void checkInvA () {
		if (this.neigh == null) { return; }
		Set<Node> union = new HashSet<Node>(neigh().keySet());
		union.addAll(this.centerNodes());
		assert union.equals(graph().nodeSet()) : "Invariant A failed.";
	}
	
}
