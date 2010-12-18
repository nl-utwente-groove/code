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
 * $Id: NodeEdgeHashMap.java,v 1.5 2008-01-30 09:32:52 iovka Exp $
 */
package groove.graph;

/**
 * Map from graph elements to graph elements for graphs of
 * different types.
 * @author Arend Rensink
 * @version $Revision: 2754 $
 */
abstract public class GraphToGraphMap<SN extends Node,SL extends Label,SE extends Edge,TN extends Node,TL extends Label,TE extends Edge>
        extends GenericNodeEdgeHashMap<SN,TN,SE,TE> implements
        GraphMap<SN,SL,SE,TN,TL,TE> {
    /** Constructs a copy of another node-edge-map. */
    public GraphToGraphMap(GraphToGraphMap<SN,SL,SE,TN,TL,TE> other) {
        nodeMap().putAll(other.nodeMap());
        edgeMap().putAll(other.edgeMap());
    }

    /** Constructs an initially empty node-edge-map. */
    public GraphToGraphMap() {
        // empty constructor
    }

    public boolean containsEdgeKey(SE elem) {
        return edgeMap().containsKey(elem);
    }

    public boolean containsNodeKey(SN elem) {
        return nodeMap().containsKey(elem);
    }

    @Deprecated
    public boolean containsKey(Element elem) {
        if (elem instanceof Node) {
            return nodeMap().containsKey(elem);
        } else {
            return edgeMap().containsKey(elem);
        }
    }

    @Deprecated
    public boolean containsValue(Element elem) {
        if (elem instanceof Node) {
            return nodeMap().containsValue(elem);
        } else {
            return edgeMap().containsValue(elem);
        }
    }

    public boolean containsNodeValue(TN elem) {
        return nodeMap().containsValue(elem);
    }

    public boolean containsEdgeValue(TE elem) {
        return edgeMap().containsValue(elem);
    }

    /** This implementation acts as the identity function. */
    @SuppressWarnings("unchecked")
    public TL mapLabel(SL label) {
        return (TL) label;
    }

    /**
     * If no edge image is stored, this implementation invokes
     * {@link #createImage(Edge)}.
     */
    public TE mapEdge(SE key) {
        TE result = getEdge(key);
        if (result == null) {
            putEdge(key, result = createImage(key));
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

    @SuppressWarnings("unchecked")
    @Override
    public GraphToGraphMap<SN,SL,SE,TN,TL,TE> clone() {
        return (GraphToGraphMap<SN,SL,SE,TN,TL,TE>) super.clone();
    }

    @Override
    abstract public GraphToGraphMap<SN,SL,SE,TN,TL,TE> newMap();
}
