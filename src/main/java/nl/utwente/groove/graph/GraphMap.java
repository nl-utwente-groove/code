/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
 * $Id$
 */
package nl.utwente.groove.graph;

import java.util.HashSet;
import java.util.Map;

/**
 * Default implementation of a generic graph-to-graph-map. The implementation is
 * based on two internally stored maps, respectively for nodes and edges .
 * @author Arend Rensink
 * @version $Revision$
 */
public interface GraphMap {
    /**
     * Tests if the entire map is empty.
     * @return <code>true</code> if the entire map (both the node and the edge
     *         part) is empty.
     */
    default public boolean isEmpty() {
        return nodeMap().isEmpty() && edgeMap().isEmpty();
    }

    /**
     * Returns the combined number of node end edge entries in the map.
     */
    default public int size() {
        return nodeMap().size() + edgeMap().size();
    }

    /**
     * Returns the image for a given node key.
     */
    default public Node getNode(Node key) {
        return nodeMap().get(key);
    }

    /**
     * Checks if this map contains a given node key.
     */
    default public boolean containsNode(Node key) {
        return nodeMap().containsKey(key);
    }

    /**
     * Returns the image for a given edge key.
     */
    default public Edge getEdge(Edge key) {
        return edgeMap().get(key);
    }

    /**
     * Checks if this map contains a given edge key.
     */
    default public boolean containsEdge(Edge key) {
        return edgeMap().containsKey(key);
    }

    /**
     * Tests whether all keys are mapped to different elements.
     */
    default public boolean isInjective() {
        var nodeValues = new HashSet<>(nodeMap().values());
        var result = nodeMap().size() == nodeValues.size();
        if (result) {
            var edgeValues = new HashSet<>(edgeMap().values());
            result = edgeMap().size() == edgeValues.size();
        }
        return result;
    }

    /**
     * Returns the built-in node map.
     */
    public Map<? extends Node,? extends Node> nodeMap();

    /**
     * Returns the built-in edge map.
     */
    public Map<? extends Edge,? extends Edge> edgeMap();

    /**
     * Clears the entire map.
     */
    default public void clear() {
        nodeMap().clear();
        edgeMap().clear();
    }
}
