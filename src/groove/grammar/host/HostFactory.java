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
package groove.grammar.host;

import groove.algebra.Algebra;
import groove.grammar.rule.RuleToHostMap;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeFactory;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeLabel;
import groove.grammar.type.TypeNode;
import groove.graph.Label;
import groove.graph.StoreFactory;
import groove.util.Dispenser;
import groove.util.FreeNumberDispenser;
import groove.util.SingleDispenser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Factory class for host graph elements.
 * It is important that all states in a GTS share their host factory,
 * as otherwise node numbers may conflict or overlap.
 * @author Arend Rensink
 * @version $Revision $
 */
public class HostFactory extends StoreFactory<HostNode,HostEdge,TypeLabel> {
    /** 
     * Constructor for a fresh factory, based on a given type factory.
     * @param typeFactory the (non-{@code null}) type factory to be used
     */
    protected HostFactory(TypeFactory typeFactory) {
        this.typeFactory = typeFactory;
    }

    /** 
     * This implementation creates a host node with top type.
     * Should only be called if the graph is implicitly typed.
     */
    @Override
    public HostNode createNode(Dispenser dispenser) {
        assert this.typeFactory.getGraph().isImplicit();
        return createNode(dispenser, this.typeFactory.getTopNode());
    }

    /** Creates and returns a node with a given type label, and the next available node number. */
    public HostNode createNode(TypeNode type) {
        return createNode(getNodeNrs(), type);
    }

    /** Creates and returns a node with a given number and node type. */
    public HostNode createNode(int nr, TypeNode typeNode) {
        return createNode(new SingleDispenser(nr), typeNode);
    }

    /**
     * Creates and returns a node with the given type. Tries to re-use node
     * numbers that do not occur in the given set, while ensuring type
     * consistency.
     * @see #createNode(Dispenser, TypeNode)
     */
    public HostNode createNode(TypeNode type, Set<? extends HostNode> usedNodes) {
        FreeNumberDispenser dispenser = new FreeNumberDispenser(usedNodes);
        return createNode(dispenser, type);
    }

    /**
     * Creates and returns a node with the given type. Tries to re-use node
     * numbers that do not occur in the given array, while ensuring type
     * consistency.
     * @see #createNode(Dispenser, TypeNode)
     */
    public HostNode createNode(TypeNode type, int usedNodes[]) {
        FreeNumberDispenser dispenser = new FreeNumberDispenser(usedNodes);
        return createNode(dispenser, type);
    }

    /**
     * Creates and returns a node with the given type. Tries to re-use node
     * numbers that do not occur in the set given to the dispenser, while
     * ensuring type consistency. 
     */
    public HostNode createNode(Dispenser dispenser, TypeNode type) {
        HostNode result = null;
        assert type.getGraph() == getTypeGraph();
        do {
            int nr = dispenser.getNext();
            result = getNode(nr);
            if (result == null) {
                // create a new node of the correct type
                result = newNode(nr, type);
                storeNode(result);
            } else if (!result.getType().equals(type)) {
                // use the existing node with this number
                result = null;
            }
        } while (result == null);
        return result;
    }

    /**
     * Returns a (numbered) value node for a given algebra and value, creating
     * it if necessary. Stores previously generated instances for reuse.
     * @param algebra the algebra of the value
     * @param value algebra representation of the value for the new node
     */
    public ValueNode createValueNode(Algebra<?> algebra, Object value) {
        return createValueNode(getNextNodeNr(), algebra, value);
    }

    /**
     * Returns a numbered value node for a given algebra and value, creating
     * it if necessary. Stores previously generated instances for reuse.
     * @param algebra the algebra of the value
     * @param value algebra representation of the value for the new node
     */
    public ValueNode createValueNode(int nr, Algebra<?> algebra, Object value) {
        Map<Object,ValueNode> valueMap = getValueMap(algebra);
        ValueNode result = valueMap.get(value);
        if (result == null) {
            result = newValueNode(nr, algebra, value);
            valueMap.put(value, result);
            storeNode(result);
        }
        return result;
    }

    /** 
     * Creates a new value node with a given node number, algebra and value.
     * Raises an exception if a different node with that number already exists.
     */
    private ValueNode newValueNode(int nr, Algebra<?> algebra, Object value) {
        TypeNode type = this.typeFactory.getDataType(algebra.getSignature());
        return new ValueNode(nr, algebra, value, type);
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
    public HostEdge createEdge(HostNode source, Label label, HostNode target) {
        TypeEdge type =
            getTypeFactory().createEdge(source.getType(), (TypeLabel) label,
                target.getType(), false);
        assert type != null;
        return createEdge(source, type, target);
    }

    /** Creates a host edge with given source and target nodes, and edge type. */
    public HostEdge createEdge(HostNode source, TypeEdge type, HostNode target) {
        HostEdge edge = newEdge(source, type, target, getEdgeCount());
        return storeEdge(edge);
    }

    @Override
    protected HostNode newNode(int nr) {
        throw new UnsupportedOperationException();
    }

    /** Callback factory method for a host node with a given number and type. */
    protected HostNode newNode(int nr, TypeNode type) {
        return new DefaultHostNode(nr, type);
    }

    /** 
     * This method is not appropriate;
     * use {@link #newEdge(HostNode, TypeEdge, HostNode, int)} instead.
     */
    @Override
    protected HostEdge newEdge(HostNode source, Label label, HostNode target,
            int nr) {
        throw new UnsupportedOperationException();
    }

    /** 
     * Callback factory method to create a new edge object.
     * This will then be compared with the edge store to replace it by its
     * canonical representative.
     */
    protected HostEdge newEdge(HostNode source, TypeEdge type, HostNode target,
            int nr) {
        assert type.getGraph() == getTypeGraph();
        return new DefaultHostEdge(source, type, target, nr);
    }

    @Override
    public TypeLabel createLabel(String text) {
        return getTypeFactory().createLabel(text);
    }

    @Override
    public HostGraphMorphism createMorphism() {
        return new HostGraphMorphism(this);
    }

    /** Creates a fresh mapping from rules to (this type of) host graph. */
    public RuleToHostMap createRuleToHostMap() {
        return new RuleToHostMap(this);
    }

    /** Returns the type factory used in this host factory. */
    public TypeFactory getTypeFactory() {
        return this.typeFactory;
    }

    /** Returns the type graph used in this host factory. */
    public TypeGraph getTypeGraph() {
        return getTypeFactory().getGraph();
    }

    /** 
     * Method to normalise an array of host nodes.
     * Normalised arrays reuse the same array object for an 
     * array containing the same nodes. 
     */
    public HostNode[] normalise(HostNode[] nodes) {
        if (this.normalHostNodeMap == null) {
            this.normalHostNodeMap = new HashMap<List<HostNode>,HostNode[]>();
        }
        List<HostNode> nodeList = Arrays.asList(nodes);
        HostNode[] result = this.normalHostNodeMap.get(nodeList);
        if (result == null) {
            this.normalHostNodeMap.put(nodeList, result = nodes);
            normaliseCount++;
        } else {
            normaliseGain++;
        }
        return result;
    }

    /** Internal store of previously generated value nodes. */
    private final Map<String,Map<Object,ValueNode>> valueMaps =
        new HashMap<String,Map<Object,ValueNode>>();
    /** The type factory used for creating node and edge types. */
    private final TypeFactory typeFactory;
    /** Store of normalised host node arrays. */
    private Map<List<HostNode>,HostNode[]> normalHostNodeMap;

    /** Returns a fresh instance of this factory, with a fresh type graph. */
    public static HostFactory newInstance() {
        return newInstance(TypeFactory.newInstance());
    }

    /** Returns a fresh instance of this factory, for a given type graph. */
    public static HostFactory newInstance(TypeFactory typeFactory) {
        return new HostFactory(typeFactory);
    }

    /**
     * Reports the number of times a normalised node array was shared.
     */
    static public int getNormaliseGain() {
        return normaliseGain;
    }

    /**
     * Reports the total number of normalised node arrays.
     */
    static public int getNormaliseCount() {
        return normaliseCount;
    }

    /** Counter for the node array reuse. */
    static private int normaliseGain;
    /** Counter for the normalised node array. */
    static private int normaliseCount;
}
