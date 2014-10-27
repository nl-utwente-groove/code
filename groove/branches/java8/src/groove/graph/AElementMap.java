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

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of a generic node-edge-map. The implementation is
 * based on two internally stored hash maps.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class AElementMap<SN extends Node,SE extends Edge,TN extends Node,TE extends Edge>
    implements ElementMap, Cloneable {
    /**
     * Constructs an empty map.
     */
    public AElementMap(ElementFactory<TN,TE> factory) {
        this.nodeMap = createNodeMap();
        this.edgeMap = createEdgeMap();
        this.factory = factory;
    }

    /**
     * Returns the image for a given node key.
     */
    @Override
    public TN getNode(Node key) {
        return nodeMap().get(key);
    }

    /**
     * Returns the image for a given edge key.
     */
    @Override
    public TE getEdge(Edge key) {
        return edgeMap().get(key);
    }

    /**
     * Inserts a node key/image-pair
     * @return the old image for <code>key</code>, or <code>null</code> if
     *         there was none
     */
    public TN putNode(SN key, TN value) {
        return nodeMap().put(key, value);
    }

    /**
     * Inserts an edge key/image-pair
     * @return the old image for <code>key</code>, or <code>null</code> if
     *         there was none
     */
    public TE putEdge(SE key, TE value) {
        return edgeMap().put(key, value);
    }

    /**
     * Copies the information from a given element map to this one.
     * @param other the element map to be copied
     */
    public void putAll(AElementMap<SN,SE,TN,TE> other) {
        nodeMap().putAll(other.nodeMap());
        edgeMap().putAll(other.edgeMap());
    }

    /**
     * Removes a node key-value pair from this map.
     */
    public TN removeNode(SN key) {
        return nodeMap().remove(key);
    }

    /**
     * Removes an edge key-value pair from this map.
     */
    public TE removeEdge(SE key) {
        return edgeMap().remove(key);
    }

    /**
     * Tests for equality of the node and edge maps.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof AElementMap)
            && nodeMap().equals(((AElementMap<?,?,?,?>) obj).nodeMap())
            && edgeMap().equals(((AElementMap<?,?,?,?>) obj).edgeMap());
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
      * Returns the image of a label under this map.
      * This implementation calls {@link ElementFactory#createLabel(String)}
      * with as parameter {@link Label#toString()} called on the parameter.
      */
    public Label mapLabel(Label label) {
        return this.factory.createLabel(label.toString());
    }

    /**
     * Returns the image of an edge under this map, creating the image if
     * necessary. An image is created if the map does not contain an image but
     * does contain images for the end nodes and label. The exact type of the
     * created edge depends on the map instance.
     * If no edge image is stored, this implementation invokes
     * {@link #createImage(Edge)}.
     */
    public TE mapEdge(SE key) {
        TE result = getEdge(key);
        if (result == null) {
            result = createImage(key);
            if (result != null) {
                putEdge(key, result);
            }
        }
        return result;
    }

    /**
     * Callback method to create an edge image for {@link #mapEdge(Edge)}. This
     * implementation creates a @link DefaultEdge} if
     * the map contains images for the key's end nodes.
     */
    protected TE createImage(SE key) {
        TN sourceImage = getNode(key.source());
        if (sourceImage == null) {
            return null;
        }
        Label labelImage = mapLabel(key.label());
        if (labelImage == null) {
            return null;
        }
        TN targetImage = getNode(key.target());
        if (targetImage == null) {
            return null;
        } else {
            return getFactory().createEdge(sourceImage, labelImage, targetImage);
        }
    }

    /** Returns a factory for target graph elements. */
    public ElementFactory<TN,TE> getFactory() {
        return this.factory;
    }

    /**
     * Returns the built-in node map.
     */
    @Override
    public Map<SN,TN> nodeMap() {
        return this.nodeMap;
    }

    /**
     * Returns the built-in edge map.
     */
    @Override
    public Map<SE,TE> edgeMap() {
        return this.edgeMap;
    }

    /**
     * Callback factory method to create the actual node map.
     * @return a {@link HashMap}.
     * @see #nodeMap()
     */
    protected Map<SN,TN> createNodeMap() {
        return new NodeMap();
    }

    /**
     * Callback factory method to create the actual edge map.
     * @return a {@link HashMap}.
     * @see #edgeMap()
     */
    protected Map<SE,TE> createEdgeMap() {
        return new EdgeMap();
    }

    /** Mapping from node keys to <tt>NT</tt>s. */
    private final Map<SN,TN> nodeMap;
    /** Mapping from edge keys to <tt>ET</tt>s. */
    private final Map<SE,TE> edgeMap;
    private final ElementFactory<TN,TE> factory;

    /** Dedicated class for the node-to-node-map. */
    protected class NodeMap extends HashMap<SN,TN> {
        // empty
    }

    /** Dedicated class for the edge-to-edge-map. */
    protected class EdgeMap extends HashMap<SE,TE> {
        // empty
    }
}
