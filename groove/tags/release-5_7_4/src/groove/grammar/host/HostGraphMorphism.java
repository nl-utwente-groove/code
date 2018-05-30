/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.grammar.host;

import groove.graph.Morphism;

/**
 * Mappings from elements of one host graph to those of another.
 * @author Arend Rensink
 * @version $Revision $
 */
public class HostGraphMorphism extends Morphism<HostNode,HostEdge> {
    /**
     * Creates a new, empty map.
     */
    public HostGraphMorphism(HostFactory factory) {
        super(factory);
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
