/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.lts;

import groove.graph.ElementFactory;
import groove.graph.Edge;
import groove.graph.GGraph;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;

/**
 * Factory that fails on all method calls except the call to get the maximum
 * node number which delegates to the associated LTS graph.
 * This class is used when we want to compute isomorphisms between LTSs.
 *  
 * @author Eduardo Zambon
 */
public final class LTSFactory<N extends Node,E extends Edge> extends
        ElementFactory<N,E> {

    private final GGraph<N,E> graph;

    /** Default constructor. */
    public LTSFactory(GGraph<N,E> graph) {
        this.graph = graph;
    }

    @Override
    public int getMaxNodeNr() {
        return this.graph.nodeCount();
    }

    @Override
    public Morphism<N,E> createMorphism() {
        return new Morphism<N,E>(this);
    }

    @Override
    protected N newNode(int nr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Label createLabel(String text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E createEdge(N source, Label label, N target) {
        throw new UnsupportedOperationException();
    }
}
