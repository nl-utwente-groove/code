/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: GenericNodeEdgeHashMap.java,v 1.3 2008-01-30 09:32:50 iovka Exp $
 */
package groove.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of a generic node-edge-map. The implementation is
 * based on two internally stored hash maps.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GenericNodeEdgeHashMap<NS,NT,ES,ET> implements
        GenericNodeEdgeMap<NS,NT,ES,ET>, Cloneable {
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

    /*
     * (non-Javadoc)
     * @see groove.graph.GenericNodeEdgeMap#getNode(NS)
     */
    public NT getNode(NS key) {
        return nodeMap().get(key);
    }

    /*
     * (non-Javadoc)
     * @see groove.graph.GenericNodeEdgeMap#getEdge(ES)
     */
    public ET getEdge(ES key) {
        return edgeMap().get(key);
    }

    /*
     * (non-Javadoc)
     * @see groove.graph.GenericNodeEdgeMap#putNode(NS, NT)
     */
    public NT putNode(NS key, NT layout) {
        return nodeMap().put(key, layout);
    }

    /*
     * (non-Javadoc)
     * @see groove.graph.GenericNodeEdgeMap#putEdge(ES, ET)
     */
    public ET putEdge(ES key, ET layout) {
        return edgeMap().put(key, layout);
    }

    /*
     * (non-Javadoc)
     * @see groove.graph.GenericNodeEdgeMap#putAll(groove.graph.GenericNodeEdgeHashMap)
     */
    public void putAll(GenericNodeEdgeMap<NS,NT,ES,ET> other) {
        nodeMap().putAll(other.nodeMap());
        edgeMap().putAll(other.edgeMap());
    }

    /*
     * (non-Javadoc)
     * @see groove.graph.GenericNodeEdgeMap#removeNode(NS)
     */
    public NT removeNode(NS key) {
        return nodeMap().remove(key);
    }

    /*
     * (non-Javadoc)
     * @see groove.graph.GenericNodeEdgeMap#removeEdge(ES)
     */
    public ET removeEdge(ES key) {
        return edgeMap().remove(key);
    }

    @Override
    public boolean isInjective() {
        Set<NT> nodeValues = new HashSet<NT>(nodeMap().values());
        return nodeMap().size() == nodeValues.size();
    }

    /**
     * Tests for equality of the node and edge maps.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof GenericNodeEdgeMap)
            && nodeMap().equals(((GenericNodeEdgeMap<?,?,?,?>) obj).nodeMap())
            && edgeMap().equals(((GenericNodeEdgeMap<?,?,?,?>) obj).edgeMap());
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
        result = "Node map: " + nodeMap();
        result += "; Edge map: " + edgeMap();
        return result;
    }

    /**
     * This implementation returns a {@link HashMap}.
     */
    public Map<NS,NT> nodeMap() {
        if (this.nodeMap == null) {
            this.nodeMap = createNodeMap();
        }
        return this.nodeMap;
    }

    /**
     * This implementation returns a {@link HashMap}.
     */
    public Map<ES,ET> edgeMap() {
        if (this.edgeMap == null) {
            this.edgeMap = createEdgeMap();
        }
        return this.edgeMap;
    }

    /**
     * Returns a deep copy of the node and edge maps.
     */
    @Override
    public GenericNodeEdgeMap<NS,NT,ES,ET> clone() {
        GenericNodeEdgeMap<NS,NT,ES,ET> result = newMap();
        result.putAll(this);
        return result;
    }

    @Override
    public GenericNodeEdgeMap<NS,NT,ES,ET> newMap() {
        return new GenericNodeEdgeHashMap<NS,NT,ES,ET>();
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
