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
public class HostFactory extends StoreFactory<HostNode,HostEdge,TypeLabel> {
    /** Constructor for a fresh factory, based on a given type graph. */
    protected HostFactory(TypeGraph type) {
        if (type == null) {
            this.typeFactory = TypeFactory.instance();
        } else {
            this.typeFactory = type.getFactory();
        }
    }

    @Override
    protected HostNode newNode(int nr) {
        return new DefaultHostNode(nr, getLastNodeType());
    }

    /** This implementation creates a host node with top type. */
    @Override
    public HostNode createNode(int nr) {
        return createNode(nr, this.typeFactory.getTopNode());
    }

    /** Creates and returns a node with a given type, and the next available node number. */
    public HostNode createNode(TypeNode typeNode) {
        return createNode(getNextNodeNr(), typeNode);
    }

    /** Creates and returns a node with a given number and node type. */
    public HostNode createNode(int nr, TypeNode typeNode) {
        assert typeNode.getGraph() == this.typeFactory.getTypeGraph();
        setLastNodeType(typeNode);
        HostNode result = super.createNode(nr);
        resetLastNodeType();
        assert result.getType() == typeNode;
        return result;
    }

    /**
     * Returns a (numbered) value node for a given algebra and value, creating
     * it if necessary. Stores previously generated instances for reuse.
     * @param algebra the algebra of the value
     * @param value algebra representation of the value for the new node
     */
    public ValueNode createNode(Algebra<?> algebra, Object value) {
        Map<Object,ValueNode> valueMap = getValueMap(algebra);
        ValueNode result = valueMap.get(value);
        if (result == null) {
            TypeNode type = this.typeFactory.getDataType(algebra.getKind());
            int nr = getNextNodeNr();
            result = new ValueNode(nr, algebra, value, type);
            addNode(result);
        }
        return result;
    }

    /**
     * Returns a (numbered) value node for a given algebra and value.
     * The value is given in its string representation.
     * @param algebra the algebra of the value
     * @param value string representation of the value for the node to be created
     */
    public ValueNode createNodeFromString(Algebra<?> algebra, String value) {
        return createNode(algebra, algebra.getValueFromString(value));
    }

    /**
     * Returns a (numbered) value node for a given algebra and value.
     * The value is given in its string representation.
     * @param algebra the algebra of the value
     * @param value native Java representation of the value for the node to be created
     */
    public ValueNode createNodeFromJava(Algebra<?> algebra, Object value) {
        return createNode(algebra, algebra.getValueFromJava(value));
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
        assert ((HostNode) node).getType().getGraph() == this.typeFactory.getTypeGraph();
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

    /** Creates a typed rule edge. */
    public HostEdge createEdge(HostNode source, TypeEdge type, HostNode target) {
        assert type.getGraph() == this.typeFactory.getTypeGraph();
        HostEdge edge =
            new HostEdge(this, source, type, target, getEdgeCount());
        return storeEdge(edge);
    }

    /** 
     * Callback factory method to create a new edge object.
     * This will then be compared with the edge store to replace it by its
     * canonical representative.
     */
    @Override
    protected HostEdge createEdge(HostNode source, Label label,
            HostNode target, int nr) {
        return new HostEdge(this, source, (TypeLabel) label, target, nr);
    }

    @Override
    public TypeLabel createLabel(String text) {
        return LABEL_FACTORY.createLabel(text);
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

    /** Internal store of previously generated value nodes. */
    private final Map<String,Map<Object,ValueNode>> valueMaps =
        new HashMap<String,Map<Object,ValueNode>>();
    /** The type factory used for creating node and edge types. */
    private final TypeFactory typeFactory;
    /** Stores the node type of the last node that was created. */
    private TypeNode lastNodeType;

    /** Returns a fresh instance of this factory, without type graph. */
    public static HostFactory newInstance() {
        return new HostFactory(null);
    }

    /** Returns a fresh instance of this factory, for a given type graph. */
    public static HostFactory newInstance(TypeGraph type) {
        assert type != null;
        return new HostFactory(type);
    }

    /** The factory used for creating labels. */
    private final static TypeFactory LABEL_FACTORY = TypeFactory.instance();
}
