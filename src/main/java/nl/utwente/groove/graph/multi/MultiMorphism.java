/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
 * $Id$
 */
package nl.utwente.groove.graph.multi;

import nl.utwente.groove.graph.Morphism;

/**
 * Multi-graph morphism.
 * Default implementation of a generic node-edge-map. The implementation is
 * based on two internally stored hash maps, for the nodes and edges. Labels are
 * not translated.
 * @author Arend Rensink
 * @version $Revision$
 */
public class MultiMorphism extends Morphism<MultiNode,MultiEdge> {
    /** Constructs an empty morphism. */
    public MultiMorphism() {
        super(MultiFactory.instance());
    }

    @Override
    public MultiMorphism clone() {
        return (MultiMorphism) super.clone();
    }

    @Override
    protected MultiMorphism newMap() {
        return new MultiMorphism();
    }

    @Override
    public MultiMorphism then(Morphism<MultiNode,MultiEdge> other) {
        return (MultiMorphism) super.then(other);
    }

    @Override
    public MultiMorphism inverseThen(Morphism<MultiNode,MultiEdge> other) {
        return (MultiMorphism) super.inverseThen(other);
    }
}
