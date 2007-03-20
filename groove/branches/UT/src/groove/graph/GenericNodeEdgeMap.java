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
 * $Id: GenericNodeEdgeMap.java,v 1.1.1.1 2007-03-20 10:05:35 kastenberg Exp $
 */
package groove.graph;

import java.util.Map;

/**
 * Interface combining the functionality of two maps, one for nodes and the other for edges.
 * The map is completely generic, meaning that it can be instantiated with any
 * source and target types that are claimed to be node and edge types.
 * Consistency between the node and edge maps is not checked.
 * <ul>
 * <li><code>NS</code> is the <i>node source type</i>, i.e., the type of node keys
 * <li><code>NT</code> is the <i>node source type</i>, i.e., the type of node images
 * <li><code>ES</code> is the <i>edge target type</i>, i.e., the type of edge keys
 * <li><code>ET</code> is the <i>edge target type</i>, i.e., the type of edge images
 * </ul>
 * @author Arend Rensink
 * @version $Revision $
 */
public interface GenericNodeEdgeMap<NS, NT, ES, ET> {
	/**
	 * Tests if the entire map is empty.
	 * @return <code>true</code> if the entire map (both the node and the edge part) is empty.
	 */
	public abstract boolean isEmpty();

	/**
	 * Returns an unmodifiable view upon the node map.
	 */
	public abstract Map<NS, NT> nodeMap();

	/**
	 * Returns an unmodifiable view upon the edge map.
	 */
	public abstract Map<ES, ET> edgeMap();

	/**
	 * Returns the image for a given node key.
	 */
	public abstract NT getNode(NS key);

	/**
	 * Returns the image for a given edge key.
	 */
	public abstract ET getEdge(ES key);

	/**
	 * Inserts a node key/image-pair
	 * @return the old image for <code>key</code>, or <code>null</code> if there was none
	 */
	public abstract NT putNode(NS key, NT image);

	/**
	 * Inserts an edge key/image-pair
	 * @return the old image for <code>key</code>, or <code>null</code> if there was none
	 */
	public abstract ET putEdge(ES key, ET image);

	/**
	 * Copies the information from a given element map to this one.
	 * @param other the element map to be copied
	 */
	public abstract void putAll(GenericNodeEdgeMap<NS, NT, ES, ET> other);

	/**
	 * Clears the entire map.
	 */
	public abstract void clear();
	
	/**
	 * Returns the combined number of node end edge entries in the map.
	 */
	public abstract int size();

	/**
	 * Removes a node key-value pair from this map.
	 */
	public abstract NT removeNode(NS key);

	/**
	 * Removes an edge key-value pair from this map.
	 */
	public abstract ET removeEdge(ES key);
//
//	/**
//	 * Returns a new map constructed by composing another node-edge map
//	 * in front of this one.
//	 */
//	public abstract <OtherNS, OtherES> GenericNodeEdgeMap<OtherNS, NT, OtherES, ET> after(
//			GenericNodeEdgeMap<OtherNS, NS, OtherES, ES> other);
//
//	/**
//	 * Returns a new map constructed by composing the inverse of another 
//	 * node-edge-map in front of this one.
//	 */
//	public abstract <OtherNT, OtherET> GenericNodeEdgeMap<OtherNT, NT, OtherET, ET> afterInverse(
//			GenericNodeEdgeMap<NS, OtherNT, ES, OtherET> other);
//
}