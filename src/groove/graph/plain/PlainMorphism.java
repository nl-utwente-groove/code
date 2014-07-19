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
 * $Id$
 */
package groove.graph.plain;

import groove.graph.Morphism;

/**
 * Default implementation of a generic node-edge-map. The implementation is
 * based on two internally stored hash maps, for the nodes and edges. Labels are
 * not translated.
 * @author Arend Rensink
 * @version $Revision$
 */
public class PlainMorphism extends Morphism<PlainNode,PlainEdge> {
    /** Constructs an empty morphism. */
    public PlainMorphism() {
        super(PlainFactory.instance());
    }

    @Override
    public PlainMorphism clone() {
        return (PlainMorphism) super.clone();
    }

    @Override
    protected PlainMorphism newMap() {
        return new PlainMorphism();
    }

    @Override
    public PlainMorphism then(Morphism<PlainNode,PlainEdge> other) {
        return (PlainMorphism) super.then(other);
    }

    @Override
    public PlainMorphism inverseThen(Morphism<PlainNode,PlainEdge> other) {
        return (PlainMorphism) super.inverseThen(other);
    }
}
