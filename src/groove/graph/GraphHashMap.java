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
 * Default implementation of a generic node-edge-map. The implementation is
 * based on two internally stored hash maps, for the nodes and edges. Labels are
 * not translated.
 * @author Arend Rensink
 * @version $Revision: 2754 $
 */
abstract public class GraphHashMap<SN extends Node,TN extends Node,SE extends Edge,TE extends Edge>
        extends GenericNodeEdgeHashMap<SN,TN,SE,TE> implements
        GraphMap<SN,TN,SE,TE> {
    /** Constructs a copy of another node-edge-map. */
    public GraphHashMap(GraphMap<SN,TN,SE,TE> other) {
        nodeMap().putAll(other.nodeMap());
        edgeMap().putAll(other.edgeMap());
    }

    /** Constructs an initially empty node-edge-map. */
    public GraphHashMap() {
        // empty constructor
    }

    public boolean containsEdgeKey(Edge elem) {
        return edgeMap().containsKey(elem);
    }

    public boolean containsNodeKey(Node elem) {
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
        Label labelImage = getLabel(key.label());
        if (labelImage == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        TN targetImage = getNode((SN) key.target());
        if (targetImage == null) {
            return null;
        } else {
            return createEdge(sourceImage, labelImage, targetImage);
        }
    }

    /**
     * Callback method to create a binary edge image.
     */
    abstract protected TE createEdge(TN source, Label label,
            TN target);

    @Override
    public GraphMap<SN,TN,SE,TE> clone() {
        return (GraphMap<SN,TN,SE,TE>) super.clone();
    }
}
