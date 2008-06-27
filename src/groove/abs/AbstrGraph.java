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
 * $Id: AbstrGraph.java,v 1.2 2008-01-30 09:32:22 iovka Exp $
 */
package groove.abs;

import groove.abs.ExceptionRemovalImpossible;
import groove.abs.GraphPattern;
import groove.abs.MultiplicityInformation;
import groove.abs.PatternFamily;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;

/** A graph with additional type and multiplicity information.
 * @author Iovka Boneva
 * @version $Revision $
 */
public interface AbstrGraph extends Graph {
	
	/** The associated family.
	 * @return The associated family.
	 */
	public PatternFamily family ();

	/** The precision
	 * @return the precision
	 */
	public int precision();

	/** The type of a node.
	 * @param n
	 * @return The type of n.
	 * @require n is a node of the underlying graph
	 * @ensure the result is a pattern from family()
	 */
	public GraphPattern typeOf (Node n);

	/** The multiplicity of a node.
	 * @param n
	 * @return The multiplicity of n.
	 * @require n is a node of the underlying graph
	 */
	public MultiplicityInformation multiplicityOf (Node n);

	/** Returns the unique node having some type, or null if this type is not present in the graph.
	 * @param p
	 * @return The unique node having type <code>p</code>, or null if this type is not present in the graph.
	 */
	public Node nodeFor (GraphPattern p);

	/** Removes certain quantity of the multiplicity of a node.
	 * @param n
	 * @param q
	 */
	public void removeFrom (Node n, int q) throws ExceptionRemovalImpossible;


	/** Ensures that a node for the pattern is represented in the graph, 
	 * and adds q to its multiplicity.
	 * @return <code>true</code> if the pattern <code>p</code> existed in the
	 * abstract graph and had multiplicity zero
	 */
	public boolean addTo(GraphPattern p, int q);
	
	/** Determines whether a given map into this abstract graph is injective. */
	public boolean isInjectiveMap (NodeEdgeMap om);
	
	/** Checks whether two abstract graphs are isomorphic.
	 * Let G1 and G2 be two abstract graphs, and consider the mapping 
	 * map : G1.nodeSet() -> G2.nodeSet() 
	 * map(n1) = n2 iff n1 and n2 have the same type.
	 * Then G1 and G2 are isomorphic if map defines an isomorphism on their graph structure, 
	 * map respects multiplicity, and G1 and G2 have types on the same pattern family.
	 * @param other
	 * @return An isomorphism between this graph and other, or null if the isomorphism does not exist.
	 */
	public Morphism getIsomorphismToAbstrGraph (AbstrGraph other);
	
	/** Tests equality of abstract graphs.
	 * Two abstract graphs are equal if they have isomorphic structure 
	 * preserving types and multiplicies, are constructed over the same family and
	 * with the same precision constraint.
	 * @param o
	 * @return <code>true</code> if o is an abstract graph and is equal to this abstract graph
	 */
	public boolean equals (Object o);
	
	/** Tests quasi-equality of abstract graphs, and tests eventual inclusion.
	 * Two abstract graphs are quasi-equal if they have isomorphic structure 
	 * preserving types, but not necessarily multiplicities. Moreover, same 
	 * family and precision for both graphs are required.
	 * An abstract graph G1 is included into an abstract graph G2 if all multiplicity
	 * sets of G1 are included into G2.
	 * @param other
	 * @param belongsIsSub see {@link Abstraction.Parameters}
	 * @return
	 * 	EQUAL if the two graphs are equal in the sense of the {@link #equals(Object)} method 
	 *  NOT_EQ if there is no isomorphism preserving typing between the two graphs
	 *  QUASI if there is an isomorphism between the two graphs that preserves typing, but not multiplicity, and none of INCLUDED or INCLUDES holds
	 *  INCLUDED if there is a type preserving isomorphism between the two graphs, and all multiplicities of this graph are included into the multiplicities of other
	 *  CONTAINS if other.quasiEquals(this) == INCLUDED
	 */
	public Abstraction.AbstrGraphsRelation compare (AbstrGraph other, boolean belongsIsSub);
	
	/** Returns true when the abstract graph does not have concretisations.
	 * If this method returns false, this does not meen that the abstract
	 * graph admits concretisations.
	 * @return true if it can be determined that the abstract graph does not admit concretisations,
	 * false if this cannot be determined
	 */
	public boolean isWithoutConcretisation ();
	
}
