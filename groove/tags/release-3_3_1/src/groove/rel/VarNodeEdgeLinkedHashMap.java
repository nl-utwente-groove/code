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
package groove.rel;

import java.util.LinkedHashMap;
import java.util.Map;

import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;

/**
 * An implementation of VarNodeEdgeMap that
 * relies on {@link LinkedHashMap}s for the variable, node and edge maps.
 * @author Arend Rensink
 * @version $Revision $
 */
public class VarNodeEdgeLinkedHashMap extends VarNodeEdgeHashMap {
	/**
	 * Constructs an empty map.
	 */
	public VarNodeEdgeLinkedHashMap() {
		// empty constructor
	}

	/**
	 * Constructs a clone of a given map.
	 */
	public VarNodeEdgeLinkedHashMap(NodeEdgeMap map) {
		super(map);
	}

	@Override
	protected Map<String, Label> createValuation() {
		return new LinkedHashMap<String,Label>();
	}

	@Override
	protected Map<Edge, Edge> createEdgeMap() {
		return new LinkedHashMap<Edge,Edge>();
	}

	@Override
	protected Map<Node, Node> createNodeMap() {
		return new LinkedHashMap<Node,Node>();
	}
}
