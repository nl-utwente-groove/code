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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

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
     * Clears the entire map.
     */
    public void clear() {
        nodeMap().clear();
        edgeMap().clear();
    }

    /**
     * Tests if the entire map is empty.
     * @return <code>true</code> if the entire map (both the node and the edge
     *         part) is empty.
     */
    @Override
    public boolean isEmpty() {
        return nodeMap().isEmpty() && edgeMap().isEmpty();
    }

    /**
     * Returns the combined number of node end edge entries in the map.
     */
    @Override
    public int size() {
        return nodeMap().size() + edgeMap().size();
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
        return this.nodeMap.put(key, value);
    }

    /**
     * Inserts an edge key/image-pair
     * @return the old image for <code>key</code>, or <code>null</code> if
     *         there was none
     */
    public TE putEdge(SE key, TE value) {
        return this.edgeMap.put(key, value);
    }

    /**
     * Copies the information from a given element map to this one.
     * @param other the element map to be copied
     */
    public void putAll(AElementMap<SN,SE,TN,TE> other) {
        this.nodeMap.putAll(other.nodeMap());
        this.edgeMap.putAll(other.edgeMap());
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
     * Tests whether all keys are mapped to different elements.
     */
    @Override
    public boolean isInjective() {
        Set<TN> nodeValues = new HashSet<>(nodeMap().values());
        return nodeMap().size() == nodeValues.size();
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
    public @Nullable TE mapEdge(SE key) {
        @Nullable TE result = getEdge(key);
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
    protected @Nullable TE createImage(SE key) {
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
    public Map<SN,? extends TN> nodeMap() {
        return this.nodeMap;
    }

    /**
     * Returns the built-in edge map.
     */
    @Override
    public Map<SE,? extends TE> edgeMap() {
        return this.edgeMap;
    }

    /**
     * Callback factory method to create the actual node map.
     * @return a {@link HashMap}.
     * @see #nodeMap()
     */
    protected Map<SN,TN> createNodeMap() {
        return new HashMap<>();
    }

    /**
     * Callback factory method to create the actual edge map.
     * @return a {@link HashMap}.
     * @see #edgeMap()
     */
    protected Map<SE,TE> createEdgeMap() {
        return new HashMap<>();
    }

    /** Mapping from node keys to <tt>NT</tt>s. */
    private final Map<SN,TN> nodeMap;
    /** Mapping from edge keys to <tt>ET</tt>s. */
    private final Map<SE,TE> edgeMap;
    private final ElementFactory<TN,TE> factory;
}
