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

import static groove.view.aspect.Aspect.CONTENT_SEPARATOR;
import groove.algebra.Algebra;
import groove.view.aspect.AttributeAspect;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of graph elements that represent algebraic data values.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-02-12 15:15:32 $
 */
public class ValueNode extends VariableNode {
    /**
     * Constructs a (numbered) node for a given algebra and value of that
     * algebra.
     * @param nr the number for the new node.
     * @param algebra the algebra that the value belongs to; non-null
     * @param value the value to create a graph node for; non-null
     */
    private ValueNode(int nr, Algebra<?> algebra, Object value) {
        super(nr);
        this.algebra = algebra;
        this.value = value;
        assert value == null || algebra != null;
    }

    /**
     * Method returning the (non-null) algebra to which the attribute node
     * belongs.
     * @return the (non-null) algebra to which the attribute node belongs
     */
    public Algebra<?> getAlgebra() {
        return this.algebra;
    }

    /**
     * Returns the (non-null) algebra value this value node is representing.
     */
    public Object getValue() {
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
     * This methods returns an indication of the variable if there is no
     * associated algebra, or a description of the value otherwise.
     */
    @Override
    public String toString() {
        String algebraName =
            AttributeAspect.getAttributeValueFor(getAlgebra()).getName();
        return algebraName + CONTENT_SEPARATOR + getSymbol();
    }

    /**
     * The algebra to which this value belongs (non-null).
     */
    private final Algebra<?> algebra;
    /**
     * The operation represented by this value node (non-null).
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

    /** Internal store of previously generated value nodes. */
    static private final Map<String,Map<Object,ValueNode>> valueNodeStore =
        new HashMap<String,Map<Object,ValueNode>>();
    /** Maximum value node number */
    static private int valueNodeCount;
}
