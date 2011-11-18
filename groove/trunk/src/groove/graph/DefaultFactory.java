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
package groove.graph;

import java.util.HashMap;
import java.util.Map;

/** Factory class for graph elements. */
public class DefaultFactory extends
        StoreFactory<DefaultNode,DefaultEdge,DefaultLabel> {
    /** Private constructor. */
    protected DefaultFactory() {
        // empty
    }

    @Override
    protected DefaultNode newNode(int nr) {
        return new DefaultNode(nr);
    }

    /**
     * Generates a previously non-existent label. The label generated is of the
     * form "L"+index, where the index increases for every next fresh label.
     */
    public DefaultLabel createFreshLabel() {
        String text;
        do {
            this.freshLabelIndex++;
            text = "L" + this.freshLabelIndex;
        } while (this.labelMap.get(text) != null);
        return createLabel(text);
    }

    @Override
    public DefaultLabel createLabel(String text) {
        DefaultLabel result = this.labelMap.get(text);
        if (result == null) {
            int index = this.labelMap.size();
            result = new DefaultLabel(text, index);
            this.labelMap.put(text, result);
            return result;
        } else {
            return result;
        }
    }

    @Override
    public Morphism<DefaultNode,DefaultEdge> createMorphism() {
        return new Morphism<DefaultNode,DefaultEdge>(this);
    }

    /** Clears the store of canonical edges. */
    @Override
    public void clear() {
        super.clear();
        this.labelMap.clear();
    }

    @Override
    protected DefaultEdge createEdge(DefaultNode source, Label label,
            DefaultNode target, int nr) {
        return new DefaultEdge(source, (DefaultLabel) label, target, nr);
    }

    /**
     * The internal translation table from strings to standard (non-node type)
     * label indices.
     */
    private final Map<String,DefaultLabel> labelMap =
        new HashMap<String,DefaultLabel>();

    /** Counter to support the generation of fresh labels. */
    private int freshLabelIndex;

    /** Returns the singleton instance of this factory. */
    public static DefaultFactory instance() {
        // initialise lazily to avoid initialisation circularities
        if (instance == null) {
            instance = new DefaultFactory();
        }
        return instance;
    }

    /** Singleton instance of this factory. */
    private static DefaultFactory instance;
}
