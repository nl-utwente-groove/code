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
package groove.trans;

import groove.graph.DefaultEdge;
import groove.graph.DefaultFactory;
import groove.graph.ElementFactory;
import groove.graph.TypeFactory;
import groove.graph.TypeLabel;
import groove.util.TreeHashSet;

/** Factory class for graph elements. */
public class HostFactory implements ElementFactory<HostNode,TypeLabel,HostEdge> {
    /** Private constructor. */
    protected HostFactory() {
        // empty
    }

    /** Creates a fresh node. */
    public HostNode createNode() {
        this.maxNodeNr++;
        return NODE_FACTORY.createNode(this.maxNodeNr);
    }

    /** Creates a node with a given number. */
    public HostNode createNode(int nr) {
        this.maxNodeNr = Math.max(this.maxNodeNr, nr);
        return NODE_FACTORY.createNode(nr);
    }

    /** Creates a label with the given text. */
    public TypeLabel createLabel(String text) {
        return LABEL_FACTORY.createLabel(text);
    }

    @Override
    public HostEdge createEdge(HostNode source, String text, HostNode target) {
        return createEdge(source, createLabel(text), target);
    }

    public HostEdge createEdge(HostNode source, TypeLabel label, HostNode target) {
        assert source != null : "Source node of default edge should not be null";
        assert target != null : "Target node of default edge should not be null";
        assert label != null : "Label of default edge should not be null";
        this.maxEdgeNr++;
        HostEdge edge = new HostEdge(source, label, target, this.maxEdgeNr);
        HostEdge result = this.edgeSet.put(edge);
        if (result == null) {
            result = edge;
        }
        return result;
    }

    /** Returns the highest default node node number. */
    @Override
    public int getMaxNodeNr() {
        return NODE_FACTORY.getMaxNodeNr();
    }

    /** Returns the number of created nodes. */
    public int getNodeCount() {
        return NODE_FACTORY.getNodeCount();
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
        return LABEL_FACTORY.getLabelCount();
    }

    /** Clears the store of canonical edges. */
    public void clear() {
        this.edgeSet.clear();
        this.maxNodeNr = -1;
    }

    /** 
     * Constructs a new factory, initialised to (the elements of) a given graph.
     */
    public HostFactory newFactory(HostGraph graph) {
        HostFactory result = new HostFactory();
        result.initialise(graph);
        return result;
    }

    /** 
     * Initialises this factory to (the elements of) a given graph.
     */
    private void initialise(HostGraph graph) {
        assert this.maxNodeNr < 0 && this.maxEdgeNr < 0;
        for (HostNode node : graph.nodeSet()) {
            this.maxNodeNr = Math.max(this.maxNodeNr, node.getNumber());
        }
        for (HostEdge edge : graph.edgeSet()) {
            this.edgeSet.add(edge);
            this.maxEdgeNr = Math.max(this.maxEdgeNr, edge.getNumber());
        }
    }

    @Override
    public HostGraphMorphism createMorphism() {
        return new HostGraphMorphism(this);
    }

    /** Creates a fresh mapping from rules to (this type of) host graph. */
    public RuleToHostMap createRuleToHostMap() {
        return new RuleToHostMap(this);
    }

    /**
     * The highest host node number known to this factory.
     * Used to keep the host node numbers to a small range.  
     */
    private int maxNodeNr = -1;

    /**
     * The highest host edge number known to this factory.
     * Used to keep the host edge numbers to a small range.  
     */
    private int maxEdgeNr = -1;

    /**
     * A identity map, mapping previously created instances of
     * {@link DefaultEdge} to themselves. Used to ensure that edge objects are
     * reused.
     */
    private final TreeHashSet<HostEdge> edgeSet = new TreeHashSet<HostEdge>() {
        /**
         * As {@link DefaultEdge}s test equality by object identity,
         * we need to weaken the set's equality test.
         */
        @Override
        final protected boolean areEqual(HostEdge o1, HostEdge o2) {
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
    public static HostFactory instance() {
        return INSTANCE;
    }

    /** Singleton instance of this factory. */
    private final static HostFactory INSTANCE = new HostFactory();
    /** The factory used for creating nodes. */
    private final static DefaultFactory NODE_FACTORY =
        DefaultFactory.instance();
    /** The factory used for creating labels. */
    private final static TypeFactory LABEL_FACTORY = TypeFactory.instance();
}
