/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: ValueNode.java,v 1.10 2008-02-12 15:15:32 fladder Exp $
 */
package groove.graph.algebra;

import groove.algebra.Algebra;
import groove.algebra.AlgebraFamily;
import groove.algebra.Algebras;
import groove.graph.AbstractNode;
import groove.trans.HostNode;
import groove.view.aspect.AspectParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of graph elements that represent algebraic data values.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-02-12 15:15:32 $
 */
public class ValueNode extends AbstractNode implements HostNode {
    /** Constructor for the unique dummy node. */
    private ValueNode() {
        super(Integer.MAX_VALUE);
        this.algebra = null;
        this.signature = null;
        this.value = null;
    }

    /**
     * Constructs a (numbered) node for a given algebra and value of that
     * algebra.
     * @param nr the number for the new node. The actual number will be the
     *        negative absolute value of {@code nr}, to ensure distinctness with
     *        default node numbers.
     * @param algebra the algebra that the value belongs to; non-null
     * @param value the value to create a graph node for; non-null
     */
    private ValueNode(int nr, Algebra<?> algebra, Object value) {
        super(-Math.abs(nr));
        this.algebra = algebra;
        this.signature = Algebras.getSigName(algebra);
        this.value = value;
        assert algebra != null && value != null;
    }

    /**
     * Returns the (non-null) algebra value this value node is representing.
     */
    public Object getValue() {
        assert this != DUMMY_NODE;
        return this.value;
    }

    /**
     * Returns a symbolic description of the value, which uniquely identifies
     * the value in the algebra.
     */
    public String getSymbol() {
        return getAlgebra().getSymbol(getValue());
    }

    /**
     * This methods returns a description of the value.
     */
    @Override
    public String toString() {
        return getAlgebra().getName() + AspectParser.SEPARATOR + getSymbol();
    }

    /** Superseded by the reimplemented {@link #toString()} method. */
    @Override
    protected String getToStringPrefix() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the algebra to which the value node
     * belongs.
     */
    public Algebra<?> getAlgebra() {
        assert this != DUMMY_NODE;
        return this.algebra;
    }

    /**
     * Returns the signature to which the value node
     * belongs.
     */
    public String getSignature() {
        assert this != DUMMY_NODE;
        return this.signature;
    }

    /** The signature name of this value node. */
    private final String signature;
    /** The algebra of this value node. */
    private final Algebra<?> algebra;
    /**
     * The constant represented by this value node (non-null).
     */
    private final Object value;

    /**
     * Returns a (numbered) value node for a given algebra and value. Stores
     * previously generated instances for reuse.
     */
    static public ValueNode createValueNode(Algebra<?> algebra, Object value) {
        Map<Object,ValueNode> nodeMap = valueNodeStore.get(algebra.getName());
        if (nodeMap == null) {
            nodeMap = new HashMap<Object,ValueNode>();
            valueNodeStore.put(algebra.getName(), nodeMap);
        }
        ValueNode result = nodeMap.get(value);
        if (result == null) {
            valueNodeCount++;
            result = new ValueNode(valueNodeCount, algebra, value);
            nodeMap.put(value, result);
        }
        return result;
    }

    /** Creates a value node for a given string representation of a value.
     * Chooses a value from the default algebras.
     */
    static public ValueNode createValueNode(String value) {
        Algebra<?> alg = AlgebraFamily.getInstance().getAlgebraFor(value);
        return createValueNode(alg, alg.getValue(value));
    }

    /** Internal store of previously generated value nodes. */
    static private final Map<String,Map<Object,ValueNode>> valueNodeStore =
        new HashMap<String,Map<Object,ValueNode>>();
    /** Maximum value node number */
    static private int valueNodeCount;
    /** Single dummy node, used in e.g., MergeMap */
    public static final ValueNode DUMMY_NODE = new ValueNode();
}
