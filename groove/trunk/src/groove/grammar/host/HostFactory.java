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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

import groove.algebra.Algebra;
import groove.grammar.rule.RuleToHostMap;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeFactory;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeLabel;
import groove.grammar.type.TypeNode;
import groove.graph.Label;
import groove.graph.NodeFactory;
import groove.graph.StoreFactory;
import groove.util.Dispenser;

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
     * @param simple indicates if host edges are simple or not
     */
    protected HostFactory(TypeFactory typeFactory, boolean simple) {
        this.typeFactory = typeFactory;
        this.valueMaps = new HashMap<>();
        this.simple = simple;
    }

    /*
     * This implementation creates a host node with top type.
     * Should only be called if the graph is implicitly typed.
     */
    @Override
    protected final HostNode newNode(int nr) {
        return getTopNodeFactory().newNode(nr);
    }

    /** Returns the fixed node factory for the top type. */
    private DefaultHostNodeFactory getTopNodeFactory() {
        assert getTypeGraph().isImplicit();
        if (this.topNodeFactory == null) {
            this.topNodeFactory = (DefaultHostNodeFactory) nodes(getTypeFactory().getTopNode());
        }
        return this.topNodeFactory;
    }

    private DefaultHostNodeFactory topNodeFactory;

    @Override
    protected boolean isAllowed(HostNode node) {
        return node.getType()
            .isTopType();
    }

    /** Returns a node factory for typed default host nodes. */
    public NodeFactory<HostNode> nodes(TypeNode type) {
        return new DefaultHostNodeFactory(type);
    }

    /** Returns a node factory for a given value node. */
    public NodeFactory<HostNode> values(Algebra<?> algebra, Object value) {
        return new ValueNodeFactory(algebra, value);
    }

    /**
     * Returns a (numbered) value node for a given algebra and value, creating
     * it if necessary. Stores previously generated instances for reuse.
     * @param algebra the algebra of the value
     * @param value algebra representation of the value for the new node
     */
    public ValueNode createNode(Algebra<?> algebra, Object value) {
        // implemented as a convenience method delegating to values()
        return (ValueNode) values(algebra, value).createNode();
    }

    /** Retrieves the value-to-node map for a given algebra,
     * creating it if necessary.
     */
    Map<Object,ValueNode> getValueMap(Algebra<?> algebra) {
        Map<Object,ValueNode> result = this.valueMaps.get(algebra.getName());
        if (result == null) {
            result = new HashMap<>();
            this.valueMaps.put(algebra.getName(), result);
        }
        return result;
    }

    /** Internal store of previously generated value nodes. */
    private final Map<String,Map<Object,ValueNode>> valueMaps;

    @Override
    public HostEdge createEdge(HostNode source, Label label, HostNode target) {
        TypeEdge type = getTypeFactory().createEdge(source.getType(),
            (TypeLabel) label,
            target.getType(),
            false);
        assert type != null;
        return createEdge(source, type, target);
    }

    /** Creates a host edge with given source and target nodes, and edge type. */
    public HostEdge createEdge(HostNode source, TypeEdge type, HostNode target) {
        HostEdge edge = newEdge(source, type, target, getEdgeCount());
        return storeEdge(edge);
    }

    /**
     * This method is not appropriate;
     * use {@link #newEdge(HostNode, TypeEdge, HostNode, int)} instead.
     */
    @Override
    protected HostEdge newEdge(HostNode source, Label label, HostNode target, int nr) {
        throw new UnsupportedOperationException();
    }

    /**
     * Callback factory method to create a new edge object.
     * This should then be compared with the edge store to replace it by its
     * canonical representative.
     */
    protected @NonNull HostEdge newEdge(HostNode source, TypeEdge type, HostNode target, int nr) {
        assert type.getGraph() == getTypeGraph();
        return new DefaultHostEdge(source, type, target, nr, isSimple());
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

    /** Returns the type graph used in this host factory. */
    public TypeGraph getTypeGraph() {
        return getTypeFactory().getGraph();
    }

    /** Returns the type factory used in this host factory. */
    public TypeFactory getTypeFactory() {
        return this.typeFactory;
    }

    /** The type factory used for creating node and edge types. */
    private final TypeFactory typeFactory;

    /**
     * Method to normalise an array of host nodes.
     * Normalised arrays reuse the same array object for an
     * array containing the same nodes.
     */
    public HostNode[] normalise(HostNode[] nodes) {
        if (this.normalHostNodeMap == null) {
            this.normalHostNodeMap = new HashMap<>();
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

    /** Store of normalised host node arrays. */
    private Map<List<HostNode>,HostNode[]> normalHostNodeMap;

    /** Indicates if host edges are simple or not. */
    public boolean isSimple() {
        return this.simple;
    }

    /** Flag indicating if host edges are simple or not. */
    private final boolean simple;

    /**
     * Returns a fresh instance of this factory, with a fresh type graph.
     * Generated host edges are simple.
     */
    public static HostFactory newInstance() {
        return newInstance(TypeFactory.newInstance(), true);
    }

    /** Returns a fresh instance of this factory, with a fresh type graph. */
    public static HostFactory newInstance(boolean simple) {
        return newInstance(TypeFactory.newInstance(), simple);
    }

    /** Returns a fresh instance of this factory, for a given type graph.
     * @param simple indicates if host edges are simple or not
     */
    public static HostFactory newInstance(TypeFactory typeFactory, boolean simple) {
        return new HostFactory(typeFactory, simple);
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

    /** Factory for (typed) {@link DefaultHostNode}s. */
    protected class DefaultHostNodeFactory extends DependentNodeFactory {
        /** Constructor for subclassing. */
        protected DefaultHostNodeFactory(TypeNode type) {
            this.type = type;
        }

        @Override
        protected boolean isAllowed(HostNode node) {
            return node.getType() == this.type;
        }

        @Override
        protected HostNode newNode(int nr) {
            return new DefaultHostNode(nr, this.type);
        }

        /** Returns the type wrapped into this factory. */
        protected TypeNode getType() {
            return this.type;
        }

        private final TypeNode type;
    }

    /** Factory for (typed) {@link DefaultHostNode}s. */
    private class ValueNodeFactory extends DependentNodeFactory {
        ValueNodeFactory(Algebra<?> algebra, Object value) {
            this.algebra = algebra;
            this.value = value;
        }

        /* Overridden as value nodes should always be reused when possible. */
        @Override
        public HostNode createNode(Dispenser dispenser) {
            Map<Object,ValueNode> valueMap = getValueMap(this.algebra);
            ValueNode result = valueMap.get(this.value);
            if (result == null) {
                // create a new node only if it is currently unknown
                result = newNode(dispenser.getNext());
                valueMap.put(this.value, result);
                registerNode(result);
            }
            return result;
        }

        @Override
        protected boolean isAllowed(HostNode node) {
            assert false : "This should never have to be called";
            return false;
        }

        @Override
        protected ValueNode newNode(int nr) {
            TypeNode type = getTypeFactory().getDataType(this.algebra.getSort());
            return new ValueNode(nr, this.algebra, this.value, type);
        }

        private final Algebra<?> algebra;
        private final Object value;
    }
}
