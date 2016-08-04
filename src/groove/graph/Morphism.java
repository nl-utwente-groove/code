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
 * $Id$
 */
package groove.graph;

import java.util.Map;

/**
 * Mapping between graphs of the same type.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Morphism<N extends Node,E extends Edge> extends AElementMap<N,E,N,E> {
    /**
     * Creates a morphism, based on a given element factory.
     */
    public Morphism(ElementFactory<N,E> factory) {
        super(factory);
    }

    /** Specialises the return type. */
    @SuppressWarnings("unchecked")
    @Override
    public Map<N,N> nodeMap() {
        return (Map<N,N>) super.nodeMap();
    }

    /** Specialises the return type. */
    @SuppressWarnings("unchecked")
    @Override
    public Map<E,E> edgeMap() {
        return (Map<E,E>) super.edgeMap();
    }

    @Override
    public Morphism<N,E> clone() {
        Morphism<N,E> result = newMap();
        result.putAll(this);
        return result;
    }

    /**
     * Factory method for this type of map.
     * Returns a fresh map of the type of this map.
     */
    protected Morphism<N,E> newMap() {
        return new Morphism<>(getFactory());
    }

    /** This implementation is the identity on labels. */
    @Override
    public Label mapLabel(Label label) {
        return label;
    }

    /**
     * Constructs a morphism that is the concatenation of two morphisms.
     * @param other the second argument of concatenation
     */
    public Morphism<N,E> then(Morphism<N,E> other) {
        Morphism<N,E> result = newMap();
        for (Map.Entry<N,N> entry : nodeMap().entrySet()) {
            N image = other.getNode(entry.getValue());
            if (image != null) {
                result.putNode(entry.getKey(), image);
            }
        }
        for (Map.Entry<E,E> entry : edgeMap().entrySet()) {
            E image = other.getEdge(entry.getValue());
            if (image != null) {
                result.putEdge(entry.getKey(), image);
            }
        }
        return result;
    }

    /**
     * Constructs a morphism that is the concatenation of the inverse of this
     * morphism, followed by another morphism, if this concatenation exists. It
     * may fail to exist if the inverted morphism is non-injective on elements
     * on which the concatenated morphism is injective.
     * @param other second argument of the concatenation
     * @return the result of the concatenation, or {@code null} if
     * this does not exist.
     */
    public Morphism<N,E> inverseThen(Morphism<N,E> other) {
        Morphism<N,E> result = newMap();
        construct: {
            for (Map.Entry<N,N> entry : nodeMap().entrySet()) {
                N image = other.getNode(entry.getKey());
                if (image != null) {
                    N key = entry.getValue();
                    // result already contains an image for nodeKey
                    // if it is not the same as the one we want to insert now,
                    // stop the whole thing; otherwise we're fine
                    N oldImage = result.getNode(key);
                    if (oldImage != null && !oldImage.equals(image)) {
                        result = null;
                        break construct;
                    } else {
                        result.putNode(key, image);
                    }
                }
            }
            for (Map.Entry<E,E> entry : edgeMap().entrySet()) {
                E image = other.getEdge(entry.getKey());
                if (image != null) {
                    E key = entry.getValue();
                    // result already contains an image for nodeKey
                    // if it is not the same as the one we want to insert now,
                    // stop the whole thing; otherwise we're fine
                    E oldImage = result.putEdge(key, image);
                    if (oldImage != null && !oldImage.equals(image)) {
                        result = null;
                        break construct;
                    }
                }
            }
        }
        return result;
    }
}
