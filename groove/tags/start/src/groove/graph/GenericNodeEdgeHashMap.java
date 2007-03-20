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
 * $Id: GenericNodeEdgeHashMap.java,v 1.1.1.2 2007-03-20 10:42:41 kastenberg Exp $
 */
package groove.graph;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of a generic node-edge-map.
 * The implementation is based on two internally stored hash maps.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class GenericNodeEdgeHashMap<NS,NT,ES,ET> extends AbstractNodeEdgeMap<NS, NT, ES, ET> implements Cloneable {
	/**
	 * This implementation returns a {@link HashMap}.
	 */
    public Map<NS, NT> nodeMap() {
    	if (nodeMap == null) {
    		nodeMap = createNodeMap();
    	}
        return nodeMap;
    }

	/**
	 * This implementation returns a {@link HashMap}.
	 */
    public Map<ES, ET> edgeMap() {
    	if (edgeMap == null) {
    		edgeMap = createEdgeMap();
    	}
        return edgeMap;
    }

    /**
     * Returns a deep copy of the node and edge maps.
     */
    public GenericNodeEdgeMap<NS,NT,ES,ET> clone() {
    	try {
    		GenericNodeEdgeHashMap<NS,NT,ES,ET> result = (GenericNodeEdgeHashMap) super.clone();
    		result.nodeMap = null;
    		result.edgeMap = null;
    		result.putAll(this);
    		return result;
    	} catch (CloneNotSupportedException exc) {
    		throw new UnsupportedOperationException("Cloning went wrong");
    	}
    }

    /**
     * Callback factory method to create the actual node map.
     * @return a {@link HashMap}.
     * @see #nodeMap()
     */
    protected Map<NS,NT> createNodeMap() {
    	return new HashMap<NS,NT>();
    }
    
    /**
     * Callback factory method to create the actual edge map.
     * @return a {@link HashMap}.
     * @see #edgeMap()
     */
    protected Map<ES,ET> createEdgeMap() {
    	return new HashMap<ES,ET>();
    }

	/** Mapping from node keys to <tt>NT</tt>s. */
    private Map<NS,NT> nodeMap;
    /** Mapping from edge keys to <tt>ET</tt>s. */
    private Map<ES,ET> edgeMap;
}
