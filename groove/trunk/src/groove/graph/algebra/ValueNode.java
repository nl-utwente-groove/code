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
import groove.graph.DefaultNode;
import groove.graph.Graph;
import groove.graph.Node;
import groove.view.aspect.AttributeAspect;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Implementation of graph elements that represent algebraic data values.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-02-12 15:15:32 $
 */
public class ValueNode extends ProductNode {
    /**
     * Constructs a (numbered) node for a given algebra and value of that algebra.
     * @param nr the number for the new node.
     * @param algebra the algebra that the value belongs to; non-null
     * @param value the value to create a graph node for; non-null
     */
    private ValueNode(int nr, Algebra<?> algebra, Object value) {
        super(nr, 0);
        this.algebra = algebra;
        this.value = value;
        assert value == null || algebra != null;
    }

    /**
     * Constructs a (numbered) value node for a variable.
     */
    private ValueNode(int nr) {
        this(nr, null, null);
    }

    /**
     * Method returning the algebra to which the attribute node belongs.
     * @return the algebra to which the attribute node belongs
     */
    public Algebra<?> getAlgebra() {
        return this.algebra;
    }

    /**
     * Returns the algebra value this value node is
     * representing. May be <code>null</code> if the value node is
     * a variable.
     */
    public Object getValue() {
        return this.value;
    }

    /** 
     * Returns <code>true</code> if this value node represents a variable.
     * @see #getValue()
     */
    public boolean hasValue() {
        return this.value != null;
    }
    
    /**
     * Returns a symbolic description of the value, which uniquely identifies
     * the value in the algebra.
     */
    public String getSymbol() {
        if (hasValue()) {
            return getAlgebra().getSymbol(getValue());
        } else {
            return null;
        }
    }

    /**
     * This methods returns an indication of the variable if there is no
     * associated algebra, or a description of the value otherwise.
     */
    @Override
    public String toString() {
        if (!hasValue()) {
            return "x" + getNumber();
        } else {
            String algebraName =
                AttributeAspect.getAttributeValueFor(getAlgebra()).getName();
            return algebraName + CONTENT_SEPARATOR + getSymbol();
        }
    }

    /**
     * the algebra to which this value belongs; <code>null</code> if the node
     * stands for a variable.
     */
    private final Algebra<?> algebra;
    /**
     * the operation represented by this value node; <code>null</code> if the
     * node stands for a variable.
     */
    private final Object value;

    /** 
     * Returns a (numbered) value node for a given algebra and value.
     * Stores previously generated instances for reuse. 
     */
    static public ValueNode createValueNode(Algebra<?> algebra, Object value) {
        Map<Object,ValueNode> nodeMap = valueNodeStore.get(algebra.getName());
        if (nodeMap == null) {
            nodeMap = new HashMap<Object,ValueNode>();
            valueNodeStore.put(algebra.getName(),nodeMap);
        }
        ValueNode result = nodeMap.get(value);
        if (result == null) {
            valueNodeCount++;
            result = new ValueNode(valueNodeCount, algebra, value);
            nodeMap.put(value,result);
        }
        return result;
    }
    
    /** 
     * Returns a new value node, with a given number but without predefined value.
     * Reuses a previously created variable node with the same number, if any.
     */
    static public ValueNode createVariableNode(int nr) {
        ValueNode result = variableNodeStore.get(nr);
        if (result == null) {
            variableNodeStore.put(nr, result = new ValueNode(nr));
        }
        return result;
    }
    
    /** Tests if a given graph contains value nodes. */
    static public boolean hasValueNodes(Graph graph) {
        boolean result = false;
        Iterator<? extends Node> nodeIter = graph.nodeSet().iterator();
        while (!result && nodeIter.hasNext()) {
            result = (nodeIter.next() instanceof ValueNode);
        }
        return result;
    }
    
    /** Internal store of previously generated value nodes. */
    static private final Map<String,Map<Object,ValueNode>> valueNodeStore = new HashMap<String,Map<Object,ValueNode>>();
    /** Maximum value node number */
    static private int valueNodeCount;
    /** Store of previously created variable nodes. */
    static private final Map<Integer,ValueNode> variableNodeStore = new HashMap<Integer,ValueNode>();
}
