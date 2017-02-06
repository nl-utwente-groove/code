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
package groove.graph.plain;

import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.StoreFactory;

/** Factory class for graph elements. */
public class PlainFactory extends StoreFactory<PlainNode,PlainEdge,PlainLabel> {
    /** Private constructor. */
    protected PlainFactory() {
        // empty
    }

    @Override
    protected PlainNode newNode(int nr) {
        return new PlainNode(nr);
    }

    @Override
    public PlainLabel createLabel(String text) {
        return PlainLabel.parseLabel(text);
    }

    @Override
    public Morphism<PlainNode,PlainEdge> createMorphism() {
        return new Morphism<>(this);
    }

    @Override
    protected PlainEdge newEdge(PlainNode source, Label label, PlainNode target, int nr) {
        return new PlainEdge(source, (PlainLabel) label, target, nr);
    }

    /** Returns the singleton instance of this factory. */
    public static PlainFactory instance() {
        // initialise lazily to avoid initialisation circularities
        if (instance == null) {
            instance = new PlainFactory();
        }
        return instance;
    }

    /** Singleton instance of this factory. */
    private static PlainFactory instance;
}
