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

import java.util.HashMap;
import java.util.Map;

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

    /**
     * Generates a previously non-existent label. The label generated is of the
     * form "L"+index, where the index increases for every next fresh label.
     */
    public PlainLabel createFreshLabel() {
        String text;
        do {
            this.freshLabelIndex++;
            text = "L" + this.freshLabelIndex;
        } while (this.labelMap.get(text) != null);
        return createLabel(text);
    }

    @Override
    public PlainLabel createLabel(String text) {
        PlainLabel result = this.labelMap.get(text);
        if (result == null) {
            int index = this.labelMap.size();
            result = new PlainLabel(text, index);
            this.labelMap.put(text, result);
            return result;
        } else {
            return result;
        }
    }

    @Override
    public Morphism<PlainNode,PlainEdge> createMorphism() {
        return new Morphism<PlainNode,PlainEdge>(this);
    }

    @Override
    protected PlainEdge newEdge(PlainNode source, Label label,
            PlainNode target, int nr) {
        return new PlainEdge(source, (PlainLabel) label, target, nr);
    }

    /**
     * The internal translation table from strings to standard (non-node type)
     * label indices.
     */
    private final Map<String,PlainLabel> labelMap =
        new HashMap<String,PlainLabel>();

    /** Counter to support the generation of fresh labels. */
    private int freshLabelIndex;

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
