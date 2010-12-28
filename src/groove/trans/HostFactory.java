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

import groove.algebra.Algebra;
import groove.algebra.Algebras;
import groove.graph.DefaultEdge;
import groove.graph.ElementFactory;
import groove.graph.Label;
import groove.graph.NodeStore;
import groove.graph.TypeFactory;
import groove.graph.TypeLabel;
import groove.graph.algebra.ValueNode;
import groove.util.TreeHashSet;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for host graph elements.
 * Because the factory also creates value nodes, it needs
 * an algebra family to do so.
 * It is important that all states in a GTS share their host factory,
 * as otherwise node numbers may conflict or overlap.
 * @author Arend Rensink
 * @version $Revision $
 */
public class HostFactory implements ElementFactory<HostNode,HostEdge> {
    /** Constructor for a fresh factory. */
    protected HostFactory() {
        this.nodeStore = createNodeStore();
        this.edgeSet = createEdgeStore();
    }

    /** Creates a fresh default host node. */
    public DefaultHostNode createNode() {
        return this.nodeStore.createNode();
    }

    /** Creates a default host node with a given number.
     * @throws IllegalArgumentException if this factory has created a value node with this number
     */
    public DefaultHostNode createNode(int nr) {
        return this.nodeStore.createNode(nr);
    }

    /**
     * Returns a (numbered) value node for a given algebra and value, creating
     * it if necessary. Stores previously generated instances for reuse.
     * @param nr the intended number of the new node; if negative, a new (fresh) number
     * is to be assigned. If a node for this value is already in the store, it is 
     * returned without regard for the number
     * @param algebra the algebra of the value
     * @param value the value for the node to be created
     * @throws IllegalArgumentException if a different node with the same
     * number already exists in the store
     */
    public ValueNode createNode(int nr, Algebra<?> algebra, Object value) {
        Map<Object,ValueNode> valueMap = getValueMap(algebra);
        ValueNode result = valueMap.get(value);
        if (result == null) {
            if (nr < 0) {
                nr = this.nodeStore.getNextNodeNr();
            }
            result = new ValueNode(nr, algebra, value);
            valueMap.put(value, result);
            this.nodeStore.addNode(result);
        }
        return result;
    }

    /**
     * Returns a value node for a given algebra and value. Stores
     * previously generated instances for reuse.
     * @param algebra the algebra of the value
     * @param value the value for the node to be created
     */
    public ValueNode createNode(Algebra<?> algebra, Object value) {
        return createNode(-1, algebra, value);
    }

    /** 
     * Adds a given (existing) host node to this factory.
     * Throws an exception if an incompatible node with the same number
     * is already in the factory.
     * @return {@code true} if the node was not already in the factory
     * @throws IllegalArgumentException if an incompatible node with the same
     * number is already in the factory
     */
    public boolean addNode(HostNode node) throws IllegalArgumentException {
        boolean result = this.nodeStore.addNode(node);
        if (node instanceof ValueNode) {
            // make sure this value was not already wrapped in another node
            ValueNode valueNode = (ValueNode) node;
            Algebra<?> algebra = valueNode.getAlgebra();
            Object value = valueNode.getValue();
            Map<Object,ValueNode> valueMap = getValueMap(algebra);
            ValueNode oldNode = valueMap.put(value, valueNode);
            assert result == (oldNode == null);
            if (oldNode != null && oldNode != node) {
                throw new IllegalArgumentException(String.format(
                    "Duplicate value nodes for %s:%s",
                    Algebras.getSigName(algebra), algebra.getSymbol(value)));
            }
        }
        return result;
    }

    /** Creates a label with the given text. */
    public TypeLabel createLabel(String text) {
        return LABEL_FACTORY.createLabel(text);
    }

    /** Retrieves the value-to-node map for a given algebra,
     * creating it if necessary.
     */
    private Map<Object,ValueNode> getValueMap(Algebra<?> algebra) {
        Map<Object,ValueNode> result = this.valueMaps.get(algebra.getName());
        if (result == null) {
            result = new HashMap<Object,ValueNode>();
            this.valueMaps.put(algebra.getName(), result);
        }
        return result;
    }

    @Override
    public HostEdge createEdge(HostNode source, String text, HostNode target) {
        return createEdge(source, createLabel(text), target);
    }

    public HostEdge createEdge(HostNode source, Label label, HostNode target) {
        assert source != null : "Source node of host edge should not be null";
        assert target != null : "Target node of host edge should not be null";
        assert label instanceof TypeLabel : "Label of default edge should be TypeLabel";
        HostEdge edge = createEdge(source, label, target, getEdgeCount());
        HostEdge result = this.edgeSet.put(edge);
        if (result == null) {
            result = edge;
        }
        return result;
    }

    /** 
     * Callback factory method to create a new edge object.
     * This will then be compared with the edge store to replace it by its
     * canonical representative.
     */
    protected HostEdge createEdge(HostNode source, Label label,
            HostNode target, int nr) {
        return new HostEdge(source, (TypeLabel) label, target, nr);
    }

    /** 
     * Adds a given edge to the edges known to this store.
     * The source and target nodes are assumed to be known already.
     * Throws an exception if an equal but not identical edge was already in the store
     * @return {@code true} if the edge was not already known in this store.
     * @throws IllegalArgumentException if an equal but not identical edge
     * was already in the store
     */
    public boolean addEdge(HostEdge edge) throws IllegalArgumentException {
        HostEdge oldEdge = this.edgeSet.put(edge);
        if (oldEdge != null && oldEdge != edge) {
            throw new IllegalArgumentException(String.format(
                "Duplicate edges %s", edge));
        }
        return oldEdge == null;
    }

    /** Returns the highest default node node number. */
    @Override
    public int getMaxNodeNr() {
        return this.nodeStore.getMaxNodeNr();
    }

    /** Returns the number of known nodes. */
    public int getNodeCount() {
        return this.nodeStore.getNodeCount();
    }

    /**
     * Returns the total number of host edges created.
     * Since they are numbered in sequence, this is also the next free edge number.
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

    @Override
    public HostGraphMorphism createMorphism() {
        return new HostGraphMorphism(this);
    }

    /** Creates a fresh mapping from rules to (this type of) host graph. */
    public RuleToHostMap createRuleToHostMap() {
        return new RuleToHostMap(this);
    }

    /** Callback factory method to initialise the node store. */
    protected NodeStore<? extends DefaultHostNode> createNodeStore() {
        return new NodeStore<DefaultHostNode>(new DefaultHostNode(0));
    }

    /** Callback factory method to initialise the edge store. */
    protected TreeHashSet<HostEdge> createEdgeStore() {
        return new TreeHashSet<HostEdge>() {
            /**
             * As {@link HostEdge}s test equality by object identity,
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
    }

    /** Store and factory of canonical host nodes. */
    private final NodeStore<? extends DefaultHostNode> nodeStore;

    /** Internal store of previously generated value nodes. */
    private final Map<String,Map<Object,ValueNode>> valueMaps =
        new HashMap<String,Map<Object,ValueNode>>();

    /**
     * A identity map, mapping previously created instances of
     * {@link DefaultEdge} to themselves. Used to ensure that edge objects are
     * reused.
     */
    private final TreeHashSet<HostEdge> edgeSet;

    /** Returns a fresh instance of this factory. */
    public static HostFactory newInstance() {
        return new HostFactory();
    }

    /** The factory used for creating labels. */
    private final static TypeFactory LABEL_FACTORY = TypeFactory.instance();
}
