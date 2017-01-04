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
 * $Id$
 */
package groove.graph;

import java.util.Map;

/**
 * Default implementation of a generic node-edge-map. The implementation is
 * based on two internally stored hash maps.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface ElementMap {
    /**
     * Tests if the entire map is empty.
     * @return <code>true</code> if the entire map (both the node and the edge
     *         part) is empty.
     */
    public boolean isEmpty();

    /**
     * Returns the combined number of node end edge entries in the map.
     */
    public int size();

    /**
     * Returns the image for a given node key.
     */
    public Node getNode(Node key);

    /**
     * Returns the image for a given edge key.
     */
    public Edge getEdge(Edge key);

    /**
     * Tests whether all keys are mapped to different elements.
     */
    public boolean isInjective();

    /**
     * Returns the built-in node map.
     */
    public Map<? extends Node,? extends Node> nodeMap();

    /**
     * Returns the built-in edge map.
     */
    public Map<? extends Edge,? extends Edge> edgeMap();

}
