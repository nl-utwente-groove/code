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
package nl.utwente.groove.graph.plain;

import nl.utwente.groove.graph.Label;
import nl.utwente.groove.graph.Morphism;
import nl.utwente.groove.graph.StoreFactory;

/** Factory class for graph elements. */
public class PlainFactory extends StoreFactory<PlainNode,PlainEdge,PlainLabel> {
    /** Constructor for a fresh factory.
     * @param simple indicates if the edges created by this factory are simple
     */
    protected PlainFactory(boolean simple) {
        super(simple);
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

    /** Returns a fresh instance of this factory.
     * @param simple indicates if the edges created by the factory are simple
     */
    public static PlainFactory newInstance(boolean simple) {
        return new PlainFactory(simple);
    }
}
