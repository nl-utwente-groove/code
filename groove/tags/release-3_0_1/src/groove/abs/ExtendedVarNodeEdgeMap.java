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
 * $Id: ExtendedVarNodeEdgeMap.java,v 1.2 2008-02-05 13:28:21 rensink Exp $
 */
package groove.abs;

import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.rel.VarNodeEdgeHashMap;

import java.util.HashMap;
import java.util.Map;

/** Contains an origin map (VarNodeEdgeMap) together with a pre-processed information on the number of pre-images of all nodes in the codomain. */

/** Extends a VarNodeEdgeMap with a the possibility to retriev the number of pre-images for nodes. */
class ExtendedVarNodeEdgeMap extends VarNodeEdgeHashMap {
	
	@Override
	public Node putNode(Node key, Node image) {
		
		Node result = super.putNode(key, image);
		Integer old = this.nbPI.get(image);
		if (old == null) {
			this.nbPI.put(image, 1);
		} else {
			this.nbPI.put(image, old+1);
		}
		return result;
	}
	
	@Override 
	public Node removeNode (Node key) {
		Node image = super.removeNode(key);
		if (image != null) {
			Integer old = this.nbPI.get(image);
			assert old != 0 : "Something's wrong.";
			if (old == 1) {
				this.nbPI.remove(image);
			} else {
				this.nbPI.put(image, old-1);
			}
		}
		return image;
	}
	
	// --------------------------------------------------------------------------------------
	// FIELDS, CONSTRUCTORS, STANDAD METHODS
	// --------------------------------------------------------------------------------------
	/** The number of pre-images of each node in the co-domain. */
	private Map<Node, Integer> nbPI;
	
	/** Creates an empty map. */
	public ExtendedVarNodeEdgeMap () {
		super();
		this.nbPI = new HashMap<Node,Integer>();
	}
	
	/** Creates a map filled according to another map. */
	public ExtendedVarNodeEdgeMap (NodeEdgeMap map) {
		super(map);
		this.nbPI = new HashMap<Node,Integer>();
		// Initialise the pre-images mapping
		this.nbPI = new HashMap<Node, Integer> (this.nodeMap().size());
		for (Node node : this.nodeMap().values()) {
			this.nbPI.put(node, 0);
		}
		for (Node node : this.nodeMap().values()) {
			this.nbPI.put(node, this.nbPI.get(node) + 1);
		}
	}
	
	
	/** The number of pre-images of a given node.
	 * Lazy computation of the pre-images map.
	 * @param n
	 * @return The number of pre-images of <code>n</code>
	 * @require n is in the co-domain of this map
	 */
	final int getNbPreIm(Node n) {
		assert this.nodeMap().containsValue(n) : "Node " + n + " not in co-domain of " + super.toString();
		return this.nbPI.get(n); 
	}
	
	@Override
	public String toString () { return super.toString(); 	}
	
	@Override
	public ExtendedVarNodeEdgeMap clone() {
		// Extra computation here, as the nbPI matching is computed twice
		return new ExtendedVarNodeEdgeMap(this);
	}
}