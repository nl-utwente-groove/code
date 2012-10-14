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
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.StoreFactory;
import groove.graph.TypeEdge;
import groove.graph.TypeFactory;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.graph.TypeNode;
import groove.graph.algebra.ValueNode;
import groove.util.FreeNumberDispenser;

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
    /** Constructor for a fresh factory, based on a given type graph. */
    protected HostFactory(TypeGraph type) {
        if (type == null) {
            this.typeFactory = TypeFactory.instance();
        } else {
            this.typeFactory = type.getFactory();
        }
    }

    /** This implementation creates a host node with top type. */
    @Override
    public HostNode createNode(int nr) {
        assert !this.typeFactory.hasGraph()
            || this.typeFactory.getGraph().isImplicit();
        return createNode(nr, this.typeFactory.getTopNode());
    }

    /** Creates and returns a node with a given type label, and the next available node number. */
    public HostNode createNode(TypeLabel type) {
        return createNode(getNextNodeNr(), type);
    }

    /** Creates and returns a node with a given number and node type label. */
    public HostNode createNode(int nr, TypeLabel type) {
        assert type.isNodeType();
        TypeNode typeNode = this.typeFactory.getNode(type);
        assert typeNode != null;
        setLastNodeType(typeNode);
        HostNode result = super.createNode(nr);
        resetLastNodeType();
        assert result.getType() == typeNode;
        return result;
    }

    /** Creates and returns a node with a given number and node type. */
    public HostNode createNode(int nr, TypeNode typeNode) {
        assert typeNode.getGraph() == this.typeFactory.getGraph();
        setLastNodeType(typeNode);
        HostNode result = super.createNode(nr);
        resetLastNodeType();
        assert result.getType() == typeNode;
        return result;
    }

    /**
     * Creates and returns a node with the given type. Tries to re-use node
     * numbers that do not occur in the given set, while ensuring type
     * consistency.
     * @see #createNode(TypeLabel, FreeNumberDispenser)
     */
    public HostNode createNode(TypeLabel type, Set<? extends HostNode> usedNodes) {
        FreeNumberDispenser dispenser = new FreeNumberDispenser(usedNodes);
        return this.createNode(type, dispenser);
    }

    /**
     * Creates and returns a node with the given type. Tries to re-use node
     * numbers that do not occur in the given array, while ensuring type
     * consistency.
     * @see #createNode(TypeLabel, FreeNumberDispenser)
     */
    public HostNode createNode(TypeLabel type, int usedNodes[]) {
        FreeNumberDispenser dispenser = new FreeNumberDispenser(usedNodes);
        return this.createNode(type, dispenser);
    }

    /**
     * Creates and returns a node with the given type. Tries to re-use node
     * numbers that do not occur in the set given to the dispenser, while
     * ensuring type consistency. 
     */
    private HostNode createNode(TypeLabel type, FreeNumberDispenser dispenser) {
        TypeNode typeNode = getTypeFactory().getNode(type);
        int freeNr = dispenser.getNext();
        HostNode result = null;
        while (freeNr != -1) {
            // We have a free number of a node that already exists in the store.
            // Retrieve this node and check if the type coincide.
            result = retrieveNode(freeNr, typeNode);
            if (result.getType() == typeNode) {
                // Yes, the types are the same. We are done.
                return result;
            } else {
                // No, the types are different. Try another free number.
                freeNr = dispenser.getNext();
            }
        }
        // There are no more free numbers to try. We can go over the rest
        // of the node store and look for an node with the proper type.
        for (int i = dispenser.getMaxNumber() + 1; i < getMaxNodeNr(); i++) {
            result = getNode(i);
            if (result.getType() == typeNode) {
                // Yes, the types are the same. We are done.
                return result;
            }
        }
        // Nothing else to do, we need to create a new node.
        result = this.createNode(type);
        return result;
    }

    /**
     * Returns the node from the store with the given number. If the entry is
     * empty a new node with the given number and type is created.
     * */
    private HostNode retrieveNode(int nodeNr, TypeNode type) {
        HostNode result = (HostNode) getNodeFromNr(nodeNr);
        if (result == null) {
            result = createNode(nodeNr, type);
        }
        return result;
    }

    /**
     * Returns a (numbered) value node for a given algebra and value, creating
     * it if necessary. Stores previously generated instances for reuse.
     * @param algebra the algebra of the value
     * @param value algebra representation of the value for the new node
     */
    public ValueNode createValueNode(Algebra<?> algebra, Object value) {
        ValueNode result = getValueMap(algebra).get(value);
        if (result == null) {
            result = newValueNode(getNextNodeNr(), algebra, value);
        }
        return result;
    }

    /**
     * Returns a numbered value node for a given algebra and value, creating
     * it if necessary. Stores previously generated instances for reuse.
     * @param algebra the algebra of the value
     * @param value algebra representation of the value for the new node
     */
    public ValueNode createValueNode(int nr, Algebra<?> algebra, Object value) {
        ValueNode result = getValueMap(algebra).get(value);
        if (result == null) {
            result = newValueNode(nr, algebra, value);
        }
        return result;
    }

    /** 
     * Creates a new value node with a given node number, algebra and value.
     * Raises an exception if a different node with that number already exists.
     */
    private ValueNode newValueNode(int nr, Algebra<?> algebra, Object value) {
        TypeNode type = this.typeFactory.getDataType(algebra.getKind());
        ValueNode result = new ValueNode(nr, algebra, value, type);
        addNode(result);
        return result;
    }

    /**
     * Returns a (numbered) value node for a given algebra and value.
     * The value is given in its string representation.
     * @param algebra the algebra of the value
     * @param value string representation of the value for the node to be created
     */
    public ValueNode createNodeFromString(Algebra<?> algebra, String value) {
        return createValueNode(algebra, algebra.getValueFromString(value));
    }

    /**
     * Returns a (numbered) value node for a given algebra and value.
     * The value is given in its string representation.
     * @param algebra the algebra of the value
     * @param value native Java representation of the value for the node to be created
     */
    public ValueNode createNodeFromJava(Algebra<?> algebra, Object value) {
        return createValueNode(algebra, algebra.getValueFromJava(value));
    }

    /** 
     * Adds a given (existing) host node to this factory.
     * Throws an exception if an incompatible node with the same number
     * is already in the factory.
     * @return {@code true} if the node was not already in the factory
     * @throws IllegalArgumentException if an incompatible node with the same
     * number is already in the factory
     */
    @Override
    public boolean addNode(Node node) throws IllegalArgumentException {
        assert node instanceof HostNode;
        assert ((HostNode) node).getType().getGraph() == this.typeFactory.getGraph();
        boolean result = super.addNode(node);
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
                    "Duplicate value nodes for %s:%s", algebra.getKind(),
                    algebra.getSymbol(value)));
            }
        }
        return result;
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

    @Override
    public HostEdge createEdge(HostNode source, Label label, HostNode target) {
        TypeEdge type =
            this.typeFactory.getEdge(source.getType(), (TypeLabel) label,
                target.getType(), false);
        assert type != null;
        HostEdge edge = newEdge(source, type, target, getEdgeCount());
        return storeEdge(edge);
    }

    @Override
    protected HostNode newNode(int nr) {
        return new DefaultHostNode(nr, getLastNodeType());
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
        assert type.getGraph() == this.typeFactory.getGraph();
        return new HostEdge(source, type, target, nr);
    }

    @Override
    public TypeLabel createLabel(String text) {
        return this.typeFactory.createLabel(text);
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
     * Sets the type to be used in the next invocation of {@link #newNode(int)}.
     * This should be called directly prior to a call of {@link #newNode(int)}.
     * @param type the node type to be used for the next node to be constructed;
     *  non-{@code null}.
     */
    protected final void setLastNodeType(TypeNode type) {
        assert this.lastNodeType == null;
        assert type != null;
        this.lastNodeType = type;
    }

    /** 
     * Resets the type to be used in the next invocation of {@link #newNode(int)}.
     * This should be called after an invocation of {@link #newNode(int)}.
     */
    protected final void resetLastNodeType() {
        assert this.lastNodeType != null;
        this.lastNodeType = null;
    }

    /**
     * Returns the type set by the last invocation of {@link #setLastNodeType(TypeNode)}.
     * Also sets the node type to {@code null}; it is illegal to call this
     * method twice in succession.
     * @return the node type set in the last invocation of {@link #setLastNodeType(TypeNode)}
     */
    protected final TypeNode getLastNodeType() {
        TypeNode result = this.lastNodeType;
        return result;
    }

    /** Returns the type factory used in this host factory. */
    public TypeFactory getTypeFactory() {
        return this.typeFactory;
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
    /** Stores the node type of the next node to be created. */
    private TypeNode lastNodeType;
    /** Store of normalised host node arrays. */
    private Map<List<HostNode>,HostNode[]> normalHostNodeMap;

    /** Returns a fresh instance of this factory, without type graph. */
    public static HostFactory newInstance() {
        return new HostFactory(null);
    }

    /** Returns a fresh instance of this factory, for a given type graph. */
    public static HostFactory newInstance(TypeGraph type) {
        assert type != null;
        return new HostFactory(type);
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
