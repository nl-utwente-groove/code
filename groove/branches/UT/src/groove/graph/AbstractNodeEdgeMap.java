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
 * $Id: AbstractNodeEdgeMap.java,v 1.1.1.1 2007-03-20 10:05:33 kastenberg Exp $
 */
package groove.graph;

/**
 * Abstract implementation of a generic node-edge-map.
 * The underlying node and edge maps are left abstract.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public abstract class AbstractNodeEdgeMap<NS,NT,ES,ET> implements GenericNodeEdgeMap<NS, NT, ES, ET> {
	public void clear() {
	    nodeMap().clear();
	    edgeMap().clear();
	}

    public boolean isEmpty() {
        return nodeMap().isEmpty() && edgeMap().isEmpty();
    }

    public int size() {
		return nodeMap().size() + edgeMap().size();
	}

    /* (non-Javadoc)
	 * @see groove.graph.GenericNodeEdgeMap#getNode(NS)
	 */
    public NT getNode(NS key) {
        return nodeMap().get(key);
    }

    /* (non-Javadoc)
	 * @see groove.graph.GenericNodeEdgeMap#getEdge(ES)
	 */
    public ET getEdge(ES key) {
        return edgeMap().get(key);
    }

    /* (non-Javadoc)
	 * @see groove.graph.GenericNodeEdgeMap#putNode(NS, NT)
	 */
    public NT putNode(NS key, NT layout) {
    	return nodeMap().put(key, layout);
    }

    /* (non-Javadoc)
	 * @see groove.graph.GenericNodeEdgeMap#putEdge(ES, ET)
	 */
    public ET putEdge(ES key, ET layout) {
    	return edgeMap().put(key, layout);
    }

    /* (non-Javadoc)
	 * @see groove.graph.GenericNodeEdgeMap#putAll(groove.graph.GenericNodeEdgeHashMap)
	 */
    public void putAll(GenericNodeEdgeMap<NS, NT, ES, ET> other) {
    	nodeMap().putAll(other.nodeMap());
    	edgeMap().putAll(other.edgeMap());
    }
    
    /* (non-Javadoc)
	 * @see groove.graph.GenericNodeEdgeMap#removeNode(NS)
	 */
    public NT removeNode(NS key) {
        return nodeMap().remove(key);
    }

    /* (non-Javadoc)
	 * @see groove.graph.GenericNodeEdgeMap#removeEdge(ES)
	 */
    public ET removeEdge(ES key) {
        return edgeMap().remove(key);
    }

    /**
     * Tests for equality of the node and edge maps.
     */
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof NodeEdgeMap) && nodeMap().equals(((NodeEdgeMap) obj).nodeMap()) && edgeMap().equals(((NodeEdgeMap) obj).edgeMap()); 
	}

	/**
	 * Adds the hash codes of the node and edge maps.
	 */
	@Override
	public int hashCode() {
		return nodeMap().hashCode() + edgeMap().hashCode();
	}

	@Override
	public String toString() {
		String result;
		result = "Node map: "+nodeMap();
		result += "\nEdge map: "+edgeMap();
		return result;
	}
}
