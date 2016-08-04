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
package groove.graph.multi;

import groove.graph.ElementFactory;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.util.DefaultDispenser;
import groove.util.Dispenser;

/** Factory class for {@link MultiGraph} elements. */
public class MultiFactory extends ElementFactory<MultiNode,MultiEdge> {
    /** Private constructor. */
    private MultiFactory() {
        this.edgeNrs = new DefaultDispenser();
    }

    @Override
    protected MultiNode newNode(int nr) {
        return new MultiNode(nr);
    }

    @Override
    public MultiEdge createEdge(MultiNode source, Label label, MultiNode target) {
        return new MultiEdge(source, (MultiLabel) label, target, this.edgeNrs.getNext());
    }

    /** Dispenser for edge numbers. */
    private final Dispenser edgeNrs;

    @Override
    public MultiLabel createLabel(String text) {
        return MultiLabel.parseLabel(text);
    }

    @Override
    public Morphism<MultiNode,MultiEdge> createMorphism() {
        return new Morphism<>(this);
    }

    /** Returns the singleton instance of this factory. */
    public static MultiFactory instance() {
        // initialise lazily to avoid initialisation circularities
        if (instance == null) {
            instance = new MultiFactory();
        }
        return instance;
    }

    /** Singleton instance of this factory. */
    private static MultiFactory instance;
}
