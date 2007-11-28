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
 * $Id: SetMaterialisations.java,v 1.1 2007-11-28 15:35:04 iovka Exp $
 */
package groove.abs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import groove.graph.DefaultEdge;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.rel.VarNodeEdgeHashMap;
import groove.rel.VarNodeEdgeMap;
import groove.trans.RuleApplication;

/** Represents a set of materialisations.
 * A set of materialisations is determined by a normalised abstract graph and an injective matching into this graph.
 * @author Iovka Boneva
 * @version $Revision $
 */
public class SetMaterialisations {


	/** Performs the actual transformation and returns the set of resulting abstract graphs.
	 * @param appl The matching associated to this rule event should be the same as for the construction of this SetMaterialiastion object.
	 * @return The set of resulting abstract graphs.
	 * This method can be called immediately after the creation of the object.
	 */
	public Collection<AbstrGraph> transform (RuleApplication appl) {
		this.computeSet();
		this.transformAux(appl);
		return this._transfResults();
	}
	
	// --------------------------------------------------------------------------------------
	// MAIN ALGORITHMS
	// --------------------------------------------------------------------------------------
	/** Computes the internal representation of the set, so that it can be transformed. 
	 * @ensure For all couple of nodes (cn,an) that is in one of the origin maps, this.data contains a value for this couple.  
	 */
	void computeSet() {
		this.data = new HashMap<CNN, Collection<MapPattern>>();
		// Compute all possible origin embeddings that extend the initial one
		this.origins = new ArrayList<ExtendedVarNodeEdgeMap>();
		for (VarNodeEdgeMap origin : Util.getMatchesIter(this.concrPart.graph(), this.abstrPart, this.originBase)) {
			this.origins.add(new ExtendedVarNodeEdgeMap(origin));
		}
		cleanImpossibleOrigins();
		((ArrayList<ExtendedVarNodeEdgeMap>) this.origins).trimToSize();
		
		// For all origin
		for (ExtendedVarNodeEdgeMap originMap : this.origins) {
			// For all couple (cn,an) in origin and such that cn is not in the central nodes,
			// compute the corresponding type if it was not yet computed
			for (Map.Entry<Node, Graph> entry : this.concrPart.neigh().entrySet()) {
				Node n = entry.getKey();
				if (! this.data.containsKey(CNN.cnn(n, originMap.getNode(n)))) {
					Graph neigh = entry.getValue();
					GraphPattern type = this.abstrPart.typeOf(originMap.getNode(n));
					NodeEdgeMap centerMatch = new NodeEdgeHashMap();
					centerMatch.putNode(n, type.central());
					ArrayList<MapPattern> theTypes = new ArrayList<MapPattern>();
					this.data.put(CNN.cnn(n, originMap.getNode(n)), theTypes);
					for (VarNodeEdgeMap m : type.possibleTypings(neigh, n, this.options.SYMMETRY_REDUCTION)) {
						theTypes.add(new MapPattern(m));
					}
//					for (VarNodeEdgeMap m : Util.getInjMatchesIter(neigh, type, centerMatch)) {
//						theTypes.add(new MapPattern(m));
//					}
				}
			}
		}
	}
	
	/** Performs the actual transformation of the concrete part.
	 * Also computes possible new types of the nodes of the new concrete part.
	 * @param event The event used as transformation.
	 * @ensure {@link #data} is updated with the new types
	 * @ensure {@link #centerType}, {@link #newConcrPart}, {@link #morph} are computed
	 * @ensure {@link #transformed} is set to true
	 * @require {@link #computeSet()} should have been called before
	 */
	void transformAux (RuleApplication appl) {
		this.centerType = new HashMap<Node, GraphPattern>(this.concrPart.graph().nodeSet().size() - this.concrPart.neigh().size());
		// IOVKA if not cloning, the source graph is modified
		Graph clone = this.concrPart.graph().clone();
		appl.applyDelta(clone);
		//appl.applyDelta(this.concrPart.graph().clone());
		this.newConcrPart = appl.getTarget();
		this.morph = appl.getMorphism();
		
		// Update the data map with the transformed neighbourhoods
		for (Map.Entry<CNN, Collection<MapPattern>> entry : this.data.entrySet()) {
			for (MapPattern mp : entry.getValue()) {
				mp.setPattern(this.newType(entry.getKey().n1(),
						                    this.concrPart.neigh().get(entry.getKey().n1()),
						                    this.abstrPart.typeOf(entry.getKey().n2()),
						                    mp.getMap()));
			}
		}
		
		// compute the types for the central nodes
		for (Node n : this.concrPart.centerNodes()) {
			if (! this.morph.containsKey(n)) { continue; } // check whether deleted node
			// this is a read central node
			try {
				this.centerType.put(n, this.abstrPart.family().computeAddPattern(this.newConcrPart, this.morph.getNode(n)));
			} catch (ExceptionIncompatibleWithMaxIncidence e) {
				// INC_PB
				e.printStackTrace();
			}
		}
		// compute the types for the new nodes
		for (Node n : appl.getCreatedNodes()) {
			try {
				this.centerType.put(n, this.abstrPart.family().computeAddPattern(this.newConcrPart, n));
			} catch (ExceptionIncompatibleWithMaxIncidence e) {
				// INC_PB
				e.printStackTrace();
			}
		}
		this.transformed = true;
		checkFullTyping();
	}
	
	Collection<AbstrGraph> _transfResults() {
		Collection<AbstrGraph> result = new ArrayList<AbstrGraph>();
		
		Collection<Node> dist1Nodes = this.concrPart.nodesAtDist(1);  // pre-computed, needed later
		Collection<? extends Node> linkableNodes = this.concrPart.graph().nodeSet();
		
		// # For all possible origin
		for (final ExtendedVarNodeEdgeMap origin : this.origins) {
			
			TupleIterator.Mapping<Node, MapPattern> mappingIt = new TupleIterator.Mapping<Node, MapPattern>() {
				public Iterator<MapPattern> itFor(Node n) {
					return SetMaterialisations.this.data.get(CNN.cnn(n, origin.getNode(n))).iterator();
				}
				public Collection<Node> keySet() { return SetMaterialisations.this.concrPart.neigh().keySet(); }
				public int size() { return keySet().size(); }
			};
			TupleIterator<Node, MapPattern> it = new TupleIterator<Node, MapPattern>(mappingIt);

			// # o For all typings of the nodes of the concrete part
			while (it.hasNext()) {
				// # Compute the possible links
				final Map<Node,MapPattern> mapNodePattern = it.next();
				
				ArrayList<Set<Edge>> possibleSrcLinks = new ArrayList<Set<Edge>>();
				ArrayList<Set<Edge>> possibleTgtLinks = new ArrayList<Set<Edge>>();
				ArrayList<Set<Node>> zeroMultNodes = new ArrayList<Set<Node>>();
				ArrayList<Set<Edge>> linkConsumedEdges = new ArrayList<Set<Edge>>();
				possibleLinks(origin, mapNodePattern, dist1Nodes, linkableNodes, possibleSrcLinks, possibleTgtLinks, zeroMultNodes, linkConsumedEdges);
			
				// # For all possible links
				for (int i = 0; i < possibleSrcLinks.size(); i++) {
					// # Merge the concrete and abstract part
					
					// o Copy the old abstract part and remove the 0-multiplicity nodes 
					DefaultAbstrGraph res = newAbstractPart(origin);
					_removeZeroMultNodes(res);
					
					// o Remove from the abstract part edges that have been used for links
					for (Edge e : linkConsumedEdges.get(i)) {
						res.removeEdge(e);
					}
					
					// o Add the concrete part to the abstract part
					ConcretePart.Typing nodeTypes = new ConcretePart.Typing () {
						public GraphPattern typeOf(Node n) {
							MapPattern t = mapNodePattern.get(n);
							return t != null ? t.getPattern() : SetMaterialisations.this.centerType.get(n);
						}
					};
					
					_addConcrToAbstr(res, this.newConcrPart, nodeTypes);
					
					// o Add the link edges
					for (Edge ee : possibleSrcLinks.get(i)) {
						DefaultEdge e = (DefaultEdge) ee;
						res.addEdgeBetweenPatterns(nodeTypes.typeOf(e.source()), e.label(), e.target());
					}
					for (Edge ee : possibleTgtLinks.get(i)) {
						DefaultEdge e = (DefaultEdge) ee;
						res.addEdgeBetweenPatterns(e.source(), e.label(), nodeTypes.typeOf(e.target()));
					}
					
					// o Add the computed abstract graph, whenever it is not eleminated because without concretisations
					if (! res.isWithoutConcretisation()) { result.add(res); }
				}
			}
		}
		return result;
	}
	
	/** Removes from g the nodes with multiplicity 0 */
	private static void _removeZeroMultNodes (AbstrGraph g) {
		ArrayList<Node> toRemove = new ArrayList<Node>();
		Iterator<? extends Node> nodeIt = g.nodeSet().iterator();
		while (nodeIt.hasNext()) {
			Node n = nodeIt.next();
			if (Abstraction.MULTIPLICITY.isZero(g.multiplicityOf(n))) {
				toRemove.add(n);
			}
		}
		for (Node n : toRemove) { g.removeNode(n); }
	}
	
	private static void _addConcrToAbstr (DefaultAbstrGraph ag, Graph cp, ConcretePart.Typing typing) {
		// Add the nodes
		for (Node n : cp.nodeSet()) {
			ag.addTo(typing.typeOf(n), 1);
		}
		
		// Add the edges
		for (Edge ee : cp.edgeSet()) {
			DefaultEdge e = (DefaultEdge) ee;
			ag.addEdgeBetweenPatterns(typing.typeOf(e.source()), e.label(), typing.typeOf(e.target()));
		}
	}
	
	
	/** Computes the set of abstract graphs result of the transformation.
	 * @require {@link #computeSet()} and {@link #transformAux(RuleApplication)} have been called previously.
	 * @return The set of abstract graphs result of the transformation.
	 */
	Collection<AbstrGraph> transfResults () {
		// for all possible origin
		//    for all possible combination of typings
		// 		  construct the new abstract part by updating multiplicities
		//        construct links (several link configurations per combination of typings are possible)
		// 	      merge the new abstract part with the new concrete part 
		//             - add the new types in the abstr part
		//             - add the edges structure
		Collection<AbstrGraph> result = new ArrayList<AbstrGraph>();
		
		// common used later on
		Collection<Node> dist1Nodes = this.concrPart.nodesAtDist(1);
		
		for (final ExtendedVarNodeEdgeMap origin : this.origins) {
		
			// Collects nodes that have 0 multiplicity at some time, and thus have to be removed at a later point 
			Collection<Node> collectZeroMult = new ArrayList<Node>();
			
			
			DefaultAbstrGraph newAbstrPart = newAbstractPart(origin);
			// enrich the new abstract part with new summary nodes for added and read nodes
			for (Map.Entry<Node, GraphPattern> entry : this.centerType.entrySet()) {
				GraphPattern pat = entry.getValue();
				if (newAbstrPart.addTo(pat, 1)) {
					// a node of type patN existed with 0 multiplicity
					collectZeroMult.add(newAbstrPart.nodeFor(pat));
				}
				//newAbstrPart.addTo(entry.getValue(), 1);
			}
			
			// enumerate combinations of typing morphisms for the neighbourhoods of the non central nodes
			TupleIterator.Mapping<Node, MapPattern> mappingIt = new TupleIterator.Mapping<Node, MapPattern>() {
				public Iterator<MapPattern> itFor(Node n) {
					return SetMaterialisations.this.data.get(CNN.cnn(n, origin.getNode(n))).iterator();
				}
				public Collection<Node> keySet() { return SetMaterialisations.this.concrPart.neigh().keySet(); }
				public int size() { return keySet().size(); }
			};
			TupleIterator<Node, MapPattern> it = new TupleIterator<Node, MapPattern>(mappingIt);
			
			while (it.hasNext()) {
				transformResultForTyping(it.next(), origin, newAbstrPart, collectZeroMult, dist1Nodes, result);
			}
		}
		return result;
	}
	
	/** Auxiliary method for {@link #transfResults()}. 
	 * @param mapPat the typing
	 * @param origin the origin map from the concrete part into the abstr part
	 * @param newAbstrPart the partially constructed new abstract part
	 * @param collectZeroMult nodes that have zero multiplicity and have to be removed at a letter point
	 * @param dist1Nodes The nodes in the concrete part at distance one from the center
	 * @param accuResult accumulates the constructed abstract graphs
	 * */
	private void transformResultForTyping (final Map<Node,MapPattern> mapPat, 
											final ExtendedVarNodeEdgeMap origin,
											DefaultAbstrGraph newAbstrPart,
											Collection<Node> collectZeroMult,
											Collection<Node> dist1Nodes,
											Collection<AbstrGraph> accuResult) {
		// 		  construct the new abstract part by updating multiplicities
		//        construct links (several link configurations per combination of typings are possible)
		//        update the new abstract part, by adding the new summary nodes
		//        construct new abstract graph
		
		// Collects nodes that have 0 multiplicity at some time, and thus have to be removed at a later point 
		Collection<Node> collectZeroMultLocal = new ArrayList<Node>();
		collectZeroMultLocal.addAll(collectZeroMult);
		
		
		// Update the abstract part w.r.t. the new types coming from non center nodes
		// This is independent on the variation due to different possible links
		DefaultAbstrGraph abstrPartBase = new DefaultAbstrGraph(newAbstrPart);
		for (Node n : mapPat.keySet()) {
			GraphPattern patN = mapPat.get(n).getPattern();
			if (abstrPartBase.addTo(patN, 1)) {
				// a node of type patN existed with 0 multiplicity
				collectZeroMultLocal.add(abstrPartBase.nodeFor(patN));
			}
			// abstrPartBase.addTo(mapPat.get(n).getPattern(), 1);
		}
		
		// Compute the possible links. 
		ArrayList<Set<Edge>> possibleSrcLinks = new ArrayList<Set<Edge>>();
		ArrayList<Set<Edge>> possibleTgtLinks = new ArrayList<Set<Edge>>();
		ArrayList<Set<Node>> zeroMultNodes = new ArrayList<Set<Node>>();
		ArrayList<Set<Edge>> linkConsumedEdges = new ArrayList<Set<Edge>>();
		possibleLinks(origin, mapPat, dist1Nodes, null, possibleSrcLinks, possibleTgtLinks, zeroMultNodes, linkConsumedEdges);
		
		// Now 0 multiplicity nodes can be removed from the abstract part
		
		// IOVKA It seems that the 0 multiplicity nodes can be simply removed, without bothering about adjacent edges
		// It can be shown that all "possible" adjacent edges have been added as links.
		// (An edge is not "possible" means that it does not comply to the typing defined by the origin map and the subtypings.)
		// Let N be a 0 multiplicity node. The proof is based on the distance of concrete nodes mapped into N to the center of the concrete part, and the fact that the center and the distance 1 nodes have had their neighbourhood computed

		ArrayList<Node> toRemove = new ArrayList<Node>();
		Iterator<? extends Node> nodeIt = this.abstrPart.nodeSet().iterator();  // only old nodes may have 0 multiplicity
		while (nodeIt.hasNext()) {
			Node n = nodeIt.next();
			if (Abstraction.MULTIPLICITY.isZero(abstrPartBase.multiplicityOf(n))) {
				toRemove.add(n);
				//abstrPartBase.removeNode(n);
				//nodeIt.remove();
			}
		}
		for (Node n : toRemove) { abstrPartBase.removeNode(n); }
		
		// remove the edges adjacent to the the nodes that had 0 mult at some moment
		ArrayList<Edge> eToRemove = new ArrayList<Edge>();
		for (Node n : collectZeroMultLocal) {
			eToRemove.addAll(abstrPartBase.edgeSet(n));
		}
		for (Edge e : eToRemove) {
			abstrPartBase.removeEdge(e);
		}
		
		
		// Update the abstract part w.r.t. the concrete part
		ConcretePart.Typing nodeTypes = new ConcretePart.Typing () {
			public GraphPattern typeOf(Node n) {
				MapPattern t = mapPat.get(n);
				return t != null ? t.getPattern() : SetMaterialisations.this.centerType.get(n);
			}
		};
	
		Collection<Edge> notToRemove = new ArrayList<Edge>();
		addConcrToAbstr (abstrPartBase, this.newConcrPart, this.concrPart.neigh().keySet(), nodeTypes, notToRemove);
		
		// Enrich the base abstract part with links, in all possible ways
		for (int i = 0; i < possibleSrcLinks.size(); i++) {
			// Construct the abstract graph
			DefaultAbstrGraph finalAbstrGraph = new DefaultAbstrGraph(abstrPartBase);
			
			// Remove the edges in the abstract part that are now links
			for (Edge e : linkConsumedEdges.get(i)) {
				if (! notToRemove.contains(e)) { finalAbstrGraph.removeEdge(e); }
			}

			for (Edge ee : possibleSrcLinks.get(i)) {
				DefaultEdge e = (DefaultEdge) ee;
				finalAbstrGraph.addEdge(DefaultEdge.createEdge(finalAbstrGraph.nodeFor(nodeTypes.typeOf(e.source())), e.label(), e.target()));
			}
			for (Edge ee : possibleTgtLinks.get(i)) {
				DefaultEdge e = (DefaultEdge) ee;
				finalAbstrGraph.addEdge(DefaultEdge.createEdge(e.source(), e.label(), finalAbstrGraph.nodeFor(nodeTypes.typeOf(e.target()))));
			}
			
			// Add it to result
			assert finalAbstrGraph.nodeSet().size() == abstrPartBase.nodeSet().size() : "Something get wrong, a new node was added.";
			accuResult.add(finalAbstrGraph);
		}
	}
	
	// --------------------------------------------------------------------------------------
	// AUXILIARY ALGORITHMS
	// --------------------------------------------------------------------------------------

	/** Computes the new type of a node after transformation.
	 * @param n A node from the concrete part.
	 * @param neighN The neighbourhood of <code>n</code> in the old concrete part.
	 * @param typeN the type of <code>n</code> before transformation.
	 * @param typeMorph A morphism from the neighbourhood of n in the concrete part into the corresponding type (from neighN into typeN). 
	 * @require n is not deleted by the rule application
	 * @return The new pattern for the node n.
	 */
	private GraphPattern newType (Node n, Graph neighN, GraphPattern typeN, NodeEdgeMap typeMorph) {
		// For an injective morphism
		// oldNeigh, newNeigh, t: oldNeigh -> oldType, 
		// - remove nodes/edges from oldType :
		//   - for all node/edge not in the domain of ra.getMorphism(), remove typeMorph(node/edge) from oldType
		// - compute the morphism  mm : newNeigh -> oldType s.t. m \circ ra.getMorphism() = t
		// - dunion of newNeigh, oldType(modified) with m
		
		Graph newNeigh = null;
		try {
			newNeigh = this.abstrPart.family().getNeighInGraph(this.newConcrPart, this.morph.getNode(n));
		} catch (ExceptionIncompatibleWithMaxIncidence e) {
			// INC_PB
			e.printStackTrace();
		}
		Graph modTypeN = typeN.clone();
		NodeEdgeMap mm = new NodeEdgeHashMap(); 
		for (Node nn : neighN.nodeSet()) {
			if (! this.morph.containsKey(n)) {
				modTypeN.removeNode(typeMorph.getNode(nn));
			} else {
				mm.putNode(this.morph.getNode(nn), typeMorph.getNode(nn));
			}
		}
		for (Edge ee : neighN.edgeSet()) {
			if (! this.morph.containsKey(ee)) {
				modTypeN.removeEdge(typeMorph.getEdge(ee));
			} else {
				mm.putEdge(this.morph.getEdge(ee), typeMorph.getEdge(ee));
			}
		}
		Util.dunion(newNeigh, modTypeN, mm);
		try {
			return this.abstrPart.family().computeAddPattern(newNeigh, this.morph.getNode(n));
		} catch (ExceptionIncompatibleWithMaxIncidence e) {
			// What to do ?
			e.printStackTrace();
			return null;
		}
	}
		
	/** Removes from the set of origins all these that are not possible due to multiplicity constraints. */
	private void cleanImpossibleOrigins () {
		Iterator<ExtendedVarNodeEdgeMap> it = this.origins.iterator();
		while (it.hasNext()) {
			ExtendedVarNodeEdgeMap next = it.next();
			if (! this.abstrPart.isInjectiveMap(next)) {
				it.remove();
			}
		}
	}
	
 
	/** Constructs a new abstract part by updating multiplicities of this.abstrPart w.r.t. some origin. 
	 * @param om An embedding into this.abstrPart
	 * @return Copy of this.abstrPart with updated multiplicities w.r.t. <code>om</code>.
	 */
	private DefaultAbstrGraph newAbstractPart (ExtendedVarNodeEdgeMap om) {
		DefaultAbstrGraph result = new DefaultAbstrGraph(this.abstrPart);
		for (Node n : new HashSet<Node>(om.nodeMap().values())) {
			try {
				result.removeFrom(n, om.getNbPreIm(n));
			} catch (ExceptionRemovalImpossible e) {
				// Never happens, as only possible origin embeddings are considered
				e.printStackTrace();
			}
		}
		return result;
	}

	/** If high links' precision. */
	private void possibleLinksHigh (final VarNodeEdgeMap origin, 
			                     final Map<Node, MapPattern> typing, 
			                     final Collection<Node> dist1nodes,
			                     ArrayList<Set<Edge>> srcLinks,
			                     ArrayList<Set<Edge>> tgtLinks,
			                     ArrayList<Set<Node>> zeroNodes,
			                     ArrayList<Set<Edge>> consumedEdges) 
	{
		assert srcLinks.size() == 0 && tgtLinks.size() == 0 : "The out parameter sets are not empty.";
	
		ConcretePart.SubTyping subTyping = new ConcretePart.SubTyping() {
			public GraphPattern typeOf(Node n) { return SetMaterialisations.this.abstrPart.typeOf(origin.getNode(n)); }
			public NodeEdgeMap typeMapOf(Node n) { return typing.get(n).getMap(); }
		};
		
		// First compute all the extensions, and for each extension its possible embeddings
		for (Graph g : ConcretePart.extensions(this.concrPart, dist1nodes, subTyping, this.abstrPart.family(), this.options.SYMMETRY_REDUCTION)) {
		
			// Determine the new nodes, common to all possible embeddings
			Collection<Node> newNodes = new ArrayList<Node>();
			for (Node n : g.nodeSet()) { if (! this.concrPart.graph().containsElement(n)) { newNodes.add(n); } }
			
			for (VarNodeEdgeMap emb : Util.getMatchesIter(g, this.abstrPart, origin)) {
				// For each embedding, test whether it is a possible embedding and if yes, compute the corresponding set of links
				// TODO This test is inefficient, as it takes into account all nodes, and not only new nodes
				Set<Node> currZeroNodes = this.abstrPart.zeroMultNodes(new ExtendedVarNodeEdgeMap(emb));
				if (currZeroNodes == null) {
					// this embedding is not possible
					continue;
				}
				
				Set<Edge> currSrcLinks = new HashSet<Edge>();
				Set<Edge> currTgtLinks = new HashSet<Edge>();
				Set<Edge> currConsEdges = new HashSet<Edge>();
				for (Node n : newNodes) {
					for (Edge ee : g.edgeSet(n)) {
						DefaultEdge e = (DefaultEdge) ee;
						// e should be an edge between a new node and an existing node
						if ( ! newNodes.contains(e.source()) || ! newNodes.contains(e.target()) ) {
							Node imageN = emb.getNode(n);
							if (e.source() == n) {
								currTgtLinks.add(DefaultEdge.createEdge(imageN, e.label(), e.target()));
							} else {
								currSrcLinks.add(DefaultEdge.createEdge(e.source(), e.label(), imageN));
							}
							// if the abstract node has 0 multiplicity, then program for removal the edge that defines the link
							if (currZeroNodes.contains(imageN)) {
								currConsEdges.add(emb.getEdge(e));
							}
						}
					}
				}
				srcLinks.add(currSrcLinks);
				tgtLinks.add(currTgtLinks);
				zeroNodes.add(currZeroNodes);
				consumedEdges.add(currConsEdges);
			}
		}
	} 
	
	/** TODO for the moment, coded only for radius 0.
	 */
	private void possibleLinksLow (final VarNodeEdgeMap origin,
									Collection<? extends Node> linkableNodes,
									ArrayList<Set<Edge>> srcLinks,
									ArrayList<Set<Edge>> tgtLinks,
									ArrayList<Set<Edge>> consumedEdges)
	{
		
		srcLinks.add(new HashSet<Edge>());
		tgtLinks.add(new HashSet<Edge>());
		consumedEdges.add(new HashSet<Edge>(0)); // will remain empty
		for (Node node : linkableNodes) {
			Node imageN = origin.getNode(node);
			if (Abstraction.MULTIPLICITY.isZero(this.abstrPart.multiplicityOf(imageN))) {
				continue;
			}
			// the srcLinks
			for (Edge ee : this.abstrPart.edgeSet(imageN, Edge.SOURCE_INDEX)) {
				DefaultEdge e = (DefaultEdge) ee;
				if (! Abstraction.MULTIPLICITY.isZero(this.abstrPart.multiplicityOf(e.target()))) {
					srcLinks.get(0).add(DefaultEdge.createEdge(node, e.label(), e.target()));
				}
			}
			// the tgtLinks
			for (Edge ee : this.abstrPart.edgeSet(imageN, Edge.TARGET_INDEX)) {
				DefaultEdge e = (DefaultEdge) ee;
				if (! Abstraction.MULTIPLICITY.isZero(this.abstrPart.multiplicityOf(e.source()))) {
					tgtLinks.get(0).add(DefaultEdge.createEdge(e.source(), e.label(), node));
				}
			}
		}
	}
	
	
	/** Constructs the possible links given an embedding of the concrete part into the abstract part, a typing of the the nodes in the concrete part.
	 * @param origin Embedding of the concrete part into the abstract part.
	 * @param typing Associates a typing moprphism with nodes from the concrete part. Only the map components are used.
	 * @param dist1nodes Should always be the set of nodes at distance one in the concrete part (which may be pre-computed)
	 * @param srcLinks Out parameter. After return, contains the set of links which source node is in the concrete part.
	 * @param tgtLinks Out parameter. After return, contains the set of links which target node is in the concrete part.
	 * @param zeroNodes Out parameter. After return, contains the set of nodes which multiplicity became 0 while computing the links. 
	 * @param consumedEdges Out parameter. After return, contains the edges from the abstract part that have been consumed by some link.
	 * @require srcLinks and tgtLinks are empty sets and zeroNodes
	 * TODO add comment for dist1nodes and linkableNodes
	 */
	private void possibleLinks (final VarNodeEdgeMap origin, 
            final Map<Node, MapPattern> typing, 
            final Collection<Node> dist1nodes,
            final Collection<? extends Node> linkableNodes,
            ArrayList<Set<Edge>> srcLinks,
            ArrayList<Set<Edge>> tgtLinks,
            ArrayList<Set<Node>> zeroNodes,
            ArrayList<Set<Edge>> consumedEdges) 
	{
		if (this.abstrPart.family().getRadius() == 0 || this.options.LINK_PRECISION == Abstraction.LinkPrecision.LOW) {
			possibleLinksLow(origin, linkableNodes, srcLinks, tgtLinks, consumedEdges);
		} else {
			possibleLinksHigh(origin, typing, dist1nodes, srcLinks, tgtLinks, zeroNodes, consumedEdges);
		}
	}
	
	
	
	

	/** Adds a concrete graph structure to an abstract graph, w.r.t. some typing of the concrete nodes.
	 * The nodes of the concrete graph are supposed to be already in the abstract graph.
	 * @param abstrGraph Abstract graph to be enriched. Is modified by the method.
	 * @param concrGraph Concrete part to be added.
	 * @param toAdd The nodes of the concrete part to be effectively added.
	 * @param notAdded Out parameter. This contains edges that present in the abstract graph whereas an equivalent edge was added.
	 * @param nodeTypes Types for the nodes of the concrete part.
	 */
	static private void addConcrToAbstr (DefaultAbstrGraph abstrGraph,
												   Graph concrGraph,
												   Set<Node> toAdd,
												   ConcretePart.Typing nodeTypes,
												   Collection<Edge> notAdded) 
	{
		// Add the edges
		for (Edge ee : concrGraph.edgeSet()) {
			DefaultEdge e = (DefaultEdge) ee;
			Node source = abstrGraph.nodeFor(nodeTypes.typeOf(e.source()));
			Node target = abstrGraph.nodeFor(nodeTypes.typeOf(e.target()));
			assert source != null && target != null : "No node for this pattern.";
			Edge added = abstrGraph.addEdge(source, e.label(), target);
			if (added != null) { notAdded.add(added);}
		}
	}
	
	
	/** Updates a matching that matched into the abstract graph to match into the 
	 * concrete part.
	 * @param match
	 */
	public VarNodeEdgeMap updateMatch(VarNodeEdgeMap match) {
		VarNodeEdgeMap result = new VarNodeEdgeHashMap();
		for (Node n : match.nodeMap().keySet()){
			result.putNode(n,n);
		}
		for (Edge e : match.edgeMap().keySet()) {
			result.putEdge(e, e);
		}
		return result;
	}
	
	// --------------------------------------------------------------------------------------
	// FIELDS, CONSTRUCTORS, STANDAD METHODS
	// --------------------------------------------------------------------------------------
	
	// FIELDS USED FOR MATERIALISATION
	/** The common concrete part of the set of materialisations. */
	ConcretePart concrPart;
	/** The common abstract part. */
	DefaultAbstrGraph abstrPart;
	/** The initial matching used for constructing the concrete part. */
	NodeEdgeMap originBase;

	/** With each couple of concrete and abstract nodes (cn, an) associates the set of possible typings of cn w.r.t. its type determined by an.
	 * The keys of this map are exactly the couples (cn,an) that appear in some of the origin mappings and s.t. cn is not a central node in the concrete part.
	 */
	Map<CNN, Collection<MapPattern>> data;
	/** Used to store the types of the center nodes of the transformed concrete part, after they are computed. */
	Map<Node, GraphPattern> centerType;
	/** The set of possible embedings of concrPart.graph() into abstrPart. */
	Collection<ExtendedVarNodeEdgeMap> origins;
	
	// FIELDS USED FOR TRANSFORMATION
	/** Set when the transformation is performed */
	boolean transformed;
	
	Graph newConcrPart;
	Morphism morph;
	//private Map<CNN, Collection<GraphPattern>> newData;

	private Abstraction.Options options;
	
	/** Defines (but does not compute) the set of materialisations.
	 * @param cp
	 * @param ag
	 * @param origin Defines also the matching used for the transformation 
	 * @see #computeSet()
	 */
	public SetMaterialisations (ConcretePart cp, DefaultAbstrGraph ag, NodeEdgeMap origin, Abstraction.Options options) {
		this.concrPart = cp;
		this.abstrPart = ag;
		this.originBase = origin;
		this.transformed = false;
		this.options = options;
	}
	
	@Override
	public String toString () {
		String result = new String();
		result += "Abstr part : " + this.abstrPart + "\n";
		result += "Concr part : " + this.concrPart + "\n";
		result += "Origins    : \n";
		for (ExtendedVarNodeEdgeMap originMap : this.origins) {
			result += "  " + originMap + "\n";
		}
		
		result += "Typing data: ";
		result += "{\n";
		for (Map.Entry<CNN, Collection<MapPattern>> entry : this.data.entrySet()) { 
			result += "  " + entry.getKey() + "=" + entry.getValue() + "\n";
		}
		result += "}";
		
		return result;
	}
	
	// --------------------------------------------------------------------------------------
	// SUBTYPES
	// --------------------------------------------------------------------------------------
	
	/** Represents a couple of a map and a pattern. */
	class MapPattern {
		private VarNodeEdgeMap map;
		private GraphPattern pattern;
		MapPattern(VarNodeEdgeMap map) { this.map = map; }
		VarNodeEdgeMap getMap () { return this.map; }
		GraphPattern getPattern () { return this.pattern; }
		void setPattern(GraphPattern pattern) { this.pattern = pattern; }
		@Override
		public String toString() {
//			if (! SetMaterialisations.this.transformed) {
//				return getMap().toString();
//			}
//			return "(* " + getMap().toString() + ", * ," +  getPattern().toString() +" *)" ;
			return SetMaterialisations.this.transformed 
					 ? "(* " + getMap().toString() + ", * ," +  getPattern().toString() +" *)"
				     : getMap().toString();
		}
	}
	
	// ----------------------------------------------------------------------------------
	// CHECKING INVARIANTS AND PROPERTIES
	// ----------------------------------------------------------------------------------
	
	/** Check whether all the nodes of the new concrete part have their new type computed. */
	private void checkFullTyping () {
		if (! Util.ea()) { return; }
		if (! this.transformed) { return; }
		for (ExtendedVarNodeEdgeMap origin : this.origins) {
			for (Map.Entry<Node, Node> entry : origin.nodeMap().entrySet()) {
				if (this.concrPart.centerNodes().contains(entry.getKey())) { continue; }
				CNN couple = CNN.cnn(entry.getKey(), entry.getValue());				
				Collection<MapPattern> dataC = this.data.get(couple);
				assert dataC != null : "No data entry for " + couple;
				assert dataC.size() != 0 : "Zero typings for " + couple;
				for (MapPattern p : dataC) {
					assert p.getMap() != null && p.getPattern() != null : "Map or pattern missing for a data for " + couple;
				}
			}
		}
		
		for (Node n : this.concrPart.centerNodes()) {
			if (! this.morph.containsKey(n)) { continue; }
			assert this.centerType.get(n) != null : "Type missing for center node " + n;
		}
		for (Node n : this.newConcrPart.nodeSet()) {
			if (! this.morph.nodeMap().values().contains(n)) { // this is a new node
				assert this.centerType.get(n) != null : "Type missing for new node " + n;
			}
		}
	}	
}
