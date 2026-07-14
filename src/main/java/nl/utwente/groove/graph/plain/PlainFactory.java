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
import nl.utwente.groove.util.collect.TreeHashSet;

/** Factory class for graph elements. */
public class PlainFactory extends StoreFactory<PlainNode,PlainEdge,PlainLabel> {
    /** Private constructor. */
    protected PlainFactory() {
        // plain edges are always simple
        super(true);
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

    @Override
    protected TreeHashSet<PlainEdge> createEdgeStore() {
        return new TreeHashSet<>() {
            /**
             * As {@link PlainEdge}s test equality by object identity,
             * the store has to compare source, label and target instead.
             */
            @Override
            final protected boolean areEqual(PlainEdge o1, PlainEdge o2) {
                return o1.source().equals(o2.source()) && o1.target().equals(o2.target())
                    && o1.label().equals(o2.label());
            }
        };
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
