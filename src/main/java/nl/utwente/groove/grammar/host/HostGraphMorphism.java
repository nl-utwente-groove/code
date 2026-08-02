/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.grammar.host;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Morphism;

/**
 * Mappings from elements of one host graph to those of another.
 * @author Arend Rensink
 * @version $Revision$
 */
public class HostGraphMorphism extends Morphism<HostNode,HostEdge> {
    /**
     * Creates a new, empty map.
     */
    public HostGraphMorphism(HostFactory factory) {
        super(factory);
    }

    /*
     * Overridden to preserve the identity of edges whose end nodes are mapped
     * to themselves. This is essential for non-simple (multigraph) factories,
     * where creating a new edge with the same content mints a fresh parallel
     * copy rather than returning a pooled representative, so that clones and
     * transformation deltas would silently lose edge identity.
     */
    @Override
    protected @Nullable HostEdge createImage(HostEdge key) {
        HostNode sourceImage = getNode(key.source());
        if (sourceImage == null) {
            return null;
        }
        HostNode targetImage = getNode(key.target());
        if (targetImage == null) {
            return null;
        }
        if (sourceImage == key.source() && targetImage == key.target()) {
            return key;
        }
        return getFactory().createEdge(sourceImage, key.label(), targetImage);
    }

    @Override
    public HostFactory getFactory() {
        return (HostFactory) super.getFactory();
    }

    @Override
    public HostGraphMorphism clone() {
        return (HostGraphMorphism) super.clone();
    }

    @Override
    protected HostGraphMorphism newMap() {
        return new HostGraphMorphism(getFactory());
    }

    @Override
    public HostGraphMorphism then(Morphism<HostNode,HostEdge> other) {
        return (HostGraphMorphism) super.then(other);
    }

    @Override
    public HostGraphMorphism inverseThen(Morphism<HostNode,HostEdge> other) {
        return (HostGraphMorphism) super.inverseThen(other);
    }

    /** Creates a host graph consisting precisely of the node and edge images in this morphism. */
    public DefaultHostGraph createImage(String name) {
        DefaultHostGraph result = new DefaultHostGraph(name, getFactory());
        result.addNodeSet(nodeMap().values());
        result.addEdgeSet(edgeMap().values());
        return result;
    }
}
