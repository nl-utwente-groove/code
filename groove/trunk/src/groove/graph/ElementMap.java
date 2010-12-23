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
public class ElementMap<SN extends Node,SL extends Label,SE extends Edge,TN extends Node,TL extends Label,TE extends Edge>
        implements Cloneable {
    /**
     * Constructs an empty map.
     */
    public ElementMap(ElementFactory<TN,TL,TE> factory) {
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
    public boolean isEmpty() {
        return nodeMap().isEmpty() && edgeMap().isEmpty();
    }

    /**
     * Returns the combined number of node end edge entries in the map.
     */
    public int size() {
        return nodeMap().size() + edgeMap().size();
    }

    /**
     * Returns the image for a given node key.
     */
    public TN getNode(SN key) {
        return nodeMap().get(key);
    }

    /**
     * Returns the image for a given edge key.
     */
    public TE getEdge(SE key) {
        return edgeMap().get(key);
    }

    /**
     * Inserts a node key/image-pair
     * @return the old image for <code>key</code>, or <code>null</code> if
     *         there was none
     */
    public TN putNode(SN key, TN layout) {
        return this.nodeMap.put(key, layout);
    }

    /**
     * Inserts an edge key/image-pair
     * @return the old image for <code>key</code>, or <code>null</code> if
     *         there was none
     */
    public TE putEdge(SE key, TE layout) {
        return this.edgeMap.put(key, layout);
    }

    /**
     * Copies the information from a given element map to this one.
     * @param other the element map to be copied
     */
    public void putAll(ElementMap<SN,SL,SE,TN,TL,TE> other) {
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
    public boolean isInjective() {
        Set<TN> nodeValues = new HashSet<TN>(nodeMap().values());
        return nodeMap().size() == nodeValues.size();
    }

    /**
     * Tests for equality of the node and edge maps.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ElementMap)
            && nodeMap().equals(((ElementMap<?,?,?,?,?,?>) obj).nodeMap())
            && edgeMap().equals(((ElementMap<?,?,?,?,?,?>) obj).edgeMap());
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
     * Tests if a given edge occurs as a key in the edge map.
     * @param elem the element tested for
     * @return <code>true</code> if <code>elem</code> occurs as a key
     */
    public boolean containsEdgeKey(SE elem) {
        return edgeMap().containsKey(elem);
    }

    /**
     * Tests if a given node occurs as a key in the node map.
     * @param elem the element tested for
     * @return <code>true</code> if <code>elem</code> occurs as a key
     */
    public boolean containsNodeKey(SN elem) {
        return nodeMap().containsKey(elem);
    }

    /**
     * Tests if a given element occurs as a value in the node or edge map.
     * @param elem the element tested for
     * @return <code>true</code> if <code>elem</code> occurs as a value
     */
    public boolean containsNodeValue(TN elem) {
        return nodeMap().containsValue(elem);
    }

    /**
     * Tests if a given element occurs as a value in the node or edge map.
     * @param elem the element tested for
     * @return <code>true</code> if <code>elem</code> occurs as a value
     */
    public boolean containsEdgeValue(TE elem) {
        return edgeMap().containsValue(elem);
    }

    /** 
      * Returns the image of a label under this map.
      * This implementation calls {@link ElementFactory#createLabel(String)}
      * with as parameter {@link Label#toString()} called on the parameter.
      */
    public TL mapLabel(SL label) {
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
        @SuppressWarnings("unchecked")
        TN sourceImage = getNode((SN) key.source());
        if (sourceImage == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        TL labelImage = mapLabel((SL) key.label());
        if (labelImage == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        TN targetImage = getNode((SN) key.target());
        if (targetImage == null) {
            return null;
        } else {
            return getFactory().createEdge(sourceImage, labelImage, targetImage);
        }
    }

    /** Returns a factory for target graph elements. */
    public ElementFactory<TN,TL,TE> getFactory() {
        return this.factory;
    }

    /**
     * Returns the built-in node map.
     */
    public Map<SN,? extends TN> nodeMap() {
        return this.nodeMap;
    }

    /**
     * Returns the built-in edge map.
     */
    public Map<SE,? extends TE> edgeMap() {
        return this.edgeMap;
    }

    /**
     * Returns a deep copy of the node and edge maps.
     */
    @Override
    public ElementMap<SN,SL,SE,TN,TL,TE> clone() {
        ElementMap<SN,SL,SE,TN,TL,TE> result = newMap();
        result.putAll(this);
        return result;
    }

    /**
     * Factory method for this type of map.
     * Returns a fresh map of the type of this map.
     */
    public ElementMap<SN,SL,SE,TN,TL,TE> newMap() {
        return new ElementMap<SN,SL,SE,TN,TL,TE>(getFactory());
    }

    /**
     * Callback factory method to create the actual node map.
     * @return a {@link HashMap}.
     * @see #nodeMap()
     */
    protected Map<SN,TN> createNodeMap() {
        return new HashMap<SN,TN>();
    }

    /**
     * Callback factory method to create the actual edge map.
     * @return a {@link HashMap}.
     * @see #edgeMap()
     */
    protected Map<SE,TE> createEdgeMap() {
        return new HashMap<SE,TE>();
    }

    /** Mapping from node keys to <tt>NT</tt>s. */
    private final Map<SN,TN> nodeMap;
    /** Mapping from edge keys to <tt>ET</tt>s. */
    private final Map<SE,TE> edgeMap;
    private final ElementFactory<TN,TL,TE> factory;
}
