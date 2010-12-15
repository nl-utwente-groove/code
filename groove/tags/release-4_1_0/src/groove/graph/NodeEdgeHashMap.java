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

import groove.view.FormatException;

import java.util.Map;

/**
 * Default implementation of a generic node-edge-map. The implementation is
 * based on two internally stored hash maps, for the nodes and edges. Labels are
 * not translated.
 * @author Arend Rensink
 * @version $Revision$
 */
public class NodeEdgeHashMap extends GraphHashMap<Node,Node,Edge,Edge>
        implements NodeEdgeMap {
    @Override
    public NodeEdgeHashMap clone() {
        return (NodeEdgeHashMap) super.clone();
    }

    @Override
    public NodeEdgeHashMap newMap() {
        return new NodeEdgeHashMap();
    }

    @Override
    public ElementFactory<? extends Node,?,? extends Edge> getFactory() {
        return DefaultFactory.INSTANCE;
    }

    @Override
    public NodeEdgeMap after(NodeEdgeMap other) {
        NodeEdgeMap result = newMap();
        constructConcat(this, other, result);
        return result;
    }

    @Override
    public NodeEdgeMap then(NodeEdgeMap other) {
        NodeEdgeMap result = newMap();
        constructConcat(other, this, result);
        return result;
    }

    @Override
    public NodeEdgeMap afterInverse(NodeEdgeMap other) {
        try {
            NodeEdgeMap result = newMap();
            constructInvertConcat(other, this, result);
            return result;
        } catch (FormatException exc) {
            return null;
        }
    }

    @Override
    public NodeEdgeMap inverseThen(NodeEdgeMap other) {
        try {
            NodeEdgeMap result = newMap();
            constructInvertConcat(this, other, result);
            return result;
        } catch (FormatException exc) {
            return null;
        }
    }

    /**
     * Constructs a morphism that is the concatenation of the inverse of the one
     * morphism, followed by another morphism, if this concatenation exists. It
     * may fail to exist if the inverted morphism is non-injective on elements
     * on which the concatenated morphism is injective; in this case an
     * {@link FormatException} is thrown. The result is to be stored in a
     * predefined morphism, whose domain and codomain are assumed to have been
     * constructed correctly.
     * @param invert morphism whose inverse is serving as the first argument of
     *        the concatenation
     * @param concat second argument of the concatenation
     * @param result morphism where the result is to be stored; may be affected
     *        even if a {@link FormatException} is thrown
     * @throws FormatException if the injectivity of <tt>invert</tt> and
     *         <tt>concat</tt> is inconsistent
     */
    static public void constructInvertConcat(NodeEdgeMap invert,
            NodeEdgeMap concat, NodeEdgeMap result) throws FormatException {
        for (Map.Entry<Node,Node> entry : invert.nodeMap().entrySet()) {
            Node image = concat.getNode(entry.getKey());
            if (image != null) {
                Node key = entry.getValue();
                // result already contains an image for nodeKey
                // if it is not the same as the one we want to insert now,
                // stop the whole thing; otherwise we're fine
                Node oldImage = result.getNode(key);
                if (oldImage != null && !oldImage.equals(image)) {
                    throw new FormatException();
                } else {
                    result.putNode(key, image);
                }
            }
        }
        for (Map.Entry<Edge,Edge> entry : invert.edgeMap().entrySet()) {
            Edge image = concat.getEdge(entry.getKey());
            if (image != null) {
                Edge key = entry.getValue();
                // result already contains an image for nodeKey
                // if it is not the same as the one we want to insert now,
                // stop the whole thing; otherwise we're fine
                Edge oldImage = result.putEdge(key, image);
                if (oldImage != null && !oldImage.equals(image)) {
                    throw new FormatException();
                }
            }
        }
    }

    /**
     * Constructs a morphism that is the concatenation of two morphisms. The
     * result is to be stored in a predefined morphism, whose domain and
     * codomain are assumed to have been constructed correctly.
     * @param subject the first argument of concatenation
     * @param concat the second argument of concatenation
     * @param result morphism where the result is to be stored
     */
    static public void constructConcat(NodeEdgeMap subject, NodeEdgeMap concat,
            NodeEdgeMap result) {
        for (Map.Entry<Node,Node> entry : concat.nodeMap().entrySet()) {
            Node image = subject.getNode(entry.getValue());
            if (image != null) {
                result.putNode(entry.getKey(), image);
            }
        }
        for (Map.Entry<Edge,Edge> entry : concat.edgeMap().entrySet()) {
            Edge image = subject.getEdge(entry.getValue());
            if (image != null) {
                result.putEdge(entry.getKey(), image);
            }
        }
    }
}
