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
import groove.graph.Label;
import groove.graph.NodeStore;
import groove.graph.StoreFactory;
import groove.graph.TypeFactory;
import groove.graph.TypeLabel;
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
    /** Constructor for a fresh factory. */
    protected HostFactory() {
        //
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
            int nr = getNodeStore().getNextNodeNr();
            result = new ValueNode(nr, algebra, value);
            valueMap.put(value, result);
            getNodeStore().addNode(result);
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
    public boolean addNode(HostNode node) throws IllegalArgumentException {
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
                    "Duplicate value nodes for %s:%s",
                    Algebras.getSigName(algebra), algebra.getSymbol(value)));
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

    /** Callback factory method to initialise the node store. */
    @Override
    protected NodeStore<? extends DefaultHostNode> createNodeStore() {
        return new NodeStore<DefaultHostNode>(new DefaultHostNode(0));
    }

    /** Internal store of previously generated value nodes. */
    private final Map<String,Map<Object,ValueNode>> valueMaps =
        new HashMap<String,Map<Object,ValueNode>>();

    /** Returns a fresh instance of this factory. */
    public static HostFactory newInstance() {
        return new HostFactory();
    }

    /** The factory used for creating labels. */
    private final static TypeFactory LABEL_FACTORY = TypeFactory.instance();
}
