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
 * $Id: NodeEdgeHashMap.java,v 1.3 2007-05-14 18:52:01 rensink Exp $
 */
package groove.graph;

/**
 * Default implementation of a generic node-edge-map.
 * The implementation is based on two internally stored hash maps.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public class NodeEdgeHashMap extends GenericNodeEdgeHashMap<Node, Node, Edge, Edge> implements NodeEdgeMap {
	/** Constructs a copy of another node-edge-map. */
	public NodeEdgeHashMap(NodeEdgeMap other) {
		nodeMap().putAll(other.nodeMap());
		edgeMap().putAll(other.edgeMap());
	}
	
	/** Constructs an initially empty node-edge-map. */
	public NodeEdgeHashMap() {
		// empty constructor
	}
	
	public boolean containsKey(Element elem) {
		if (elem instanceof Node) {
			return nodeMap().containsKey(elem);
		} else {
			return edgeMap().containsKey(elem);
		}
	}

	public boolean containsValue(Element elem) {
		if (elem instanceof Node) {
			return nodeMap().containsValue(elem);
		} else {
			return edgeMap().containsValue(elem);
		}
	}
	
	/** This implementation acts as the identity function. */
	public Label getLabel(Label label) {
		return label;
	}

	@Override
	public NodeEdgeMap clone() {
		return new NodeEdgeHashMap(this);
	}
}
