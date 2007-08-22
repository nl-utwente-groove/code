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
 * $Id: VarNodeEdgeMultiMap.java,v 1.1 2007-08-22 09:19:49 kastenberg Exp $
 */
package groove.nesting;

import java.util.List;
import java.util.Map;
import java.util.Set;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.GenericNodeEdgeMap;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.rel.VarMap;
import groove.rel.VarNodeEdgeMap;

/**
 *
 * @author kramor
 * @version 0.1 $Revision: 1.1 $ $Date: 2007-08-22 09:19:49 $
 */
public interface VarNodeEdgeMultiMap extends GenericNodeEdgeMap<Node, Set<Node>, Edge, Set<Edge>>, VarMap {

	/**
	 * Tests if a given element occurs as a key in the node or edge map.
	 * @param elem the element tested for
	 * @return <code>true</code> if <code>elem</code> occurs as a key
	 */
	boolean containsKey(Element elem);
	/**
	 * Tests if a given element occurs as a value in the node or edge map.
	 * @param elem the element tested for
	 * @return <code>true</code> if <code>elem</code> occurs as a value
	 */
	boolean containsValue(Element elem);
	/** Returns the image of a label under this map. */
	Label getLabel(Label label);
	/**
	 * A public clone method returning a {@link NodeEdgeMap}.
	 * @return a copy of this object
	 */
	VarNodeEdgeMultiMap clone();

	public Set<Node> putNode(Node key, Node value);
	
	public Set<Edge> putEdge(Edge key, Edge value);
	
	public void putAll(VarNodeEdgeMap other);
	
	public VarNodeEdgeMap flatten();
}
