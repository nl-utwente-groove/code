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

import groove.util.TreeHashSet;

import java.util.HashMap;
import java.util.Map;

/** Factory class for graph elements. */
public class DefaultFactory implements ElementFactory<DefaultNode,DefaultEdge> {
    /** Private constructor. */
    protected DefaultFactory() {
        // empty
    }

    /** Creates a fresh node. */
    public DefaultNode createNode() {
        return this.nodeStore.createNode();
    }

    @Override
    public DefaultNode createNode(int nr) {
        return this.nodeStore.createNode(nr);
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
        return newLabel(text);
    }

    @Override
    public DefaultEdge createEdge(DefaultNode source, String text,
            DefaultNode target) {
        return createEdge(source, createLabel(text), target);
    }

    @Override
    public DefaultEdge createEdge(DefaultNode source, Label label,
            DefaultNode target) {
        assert source != null : "Source node of default edge should not be null";
        assert target != null : "Target node of default edge should not be null";
        assert label instanceof DefaultLabel : "Label of default edge should not be null";
        DefaultEdge edge =
            new DefaultEdge(source, (DefaultLabel) label, target,
                getEdgeCount());
        DefaultEdge result = this.edgeSet.put(edge);
        if (result == null) {
            result = edge;
        }
        return result;
    }

    @Override
    public Morphism<DefaultNode,DefaultEdge> createMorphism() {
        return new Morphism<DefaultNode,DefaultEdge>(this);
    }

    /** Returns the highest default node node number. */
    @Override
    public int getMaxNodeNr() {
        return this.nodeStore.size();
    }

    /** Returns the number of created nodes. */
    public int getNodeCount() {
        return this.nodeStore.getNodeCount();
    }

    /**
     * Returns the total number of default edges created.
     */
    public int getEdgeCount() {
        return this.edgeSet.size();
    }

    /**
     * Yields the number of labels created in the course of the program.
     * @return Number of labels created
     */
    public int getLabelCount() {
        return this.labelMap.size();
    }

    /** Clears the store of canonical edges. */
    public void clear() {
        this.edgeSet.clear();
        this.labelMap.clear();
        this.nodeStore.clear();
    }

    /**
     * Returns a label with the given text, reusing previously created
     * labels where possible.
     * @param text the label text being looked up
     * @return the (reused or new) label object.
     */
    private DefaultLabel newLabel(String text) {
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

    /** Store and factory of canonical default nodes. */
    private final NodeStore<DefaultNode> nodeStore =
        new NodeStore<DefaultNode>(new DefaultNode(0));
    /**
     * The internal translation table from strings to standard (non-node type)
     * label indices.
     */
    private final Map<String,DefaultLabel> labelMap =
        new HashMap<String,DefaultLabel>();

    /** Counter to support the generation of fresh labels. */
    private int freshLabelIndex;

    /**
     * A identity map, mapping previously created instances of
     * {@link DefaultEdge} to themselves. Used to ensure that edge objects are
     * reused.
     */
    private final TreeHashSet<DefaultEdge> edgeSet =
        new TreeHashSet<DefaultEdge>() {
            /**
             * As {@link DefaultEdge}s test equality by object identity,
             * we need to weaken the set's equality test.
             */
            @Override
            final protected boolean areEqual(DefaultEdge o1, DefaultEdge o2) {
                return o1.source().equals(o2.source())
                    && o1.target().equals(o2.target())
                    && o1.label().equals(o2.label());
            }

            @Override
            final protected boolean allEqual() {
                return false;
            }
        };

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
