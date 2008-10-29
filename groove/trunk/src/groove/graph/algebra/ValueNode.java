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

import java.util.Iterator;

/**
 * Implementation of graph elements that represent algebraic data values.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-02-12 15:15:32 $
 */
public class ValueNode extends DefaultNode {
    /**
     * Constructs a node for a given {@link groove.algebra.Constant}. Preferred
     * construction through {@link AlgebraGraph#getValueNode(Algebra, Object)}.
     * @param algebra the algebra that the value belongs to
     * @param value the value to create a graph node for
     */
    ValueNode(Algebra algebra, Object value) {
        super();
        this.algebra = algebra;
        this.value = value;
        assert value == null || algebra != null;
    }

    /**
     * Constructs a value node for a variable.
     */
    public ValueNode() {
        this(null, null);
    }

    /**
     * Method returning the algebra to which the attribute node belongs.
     * @return the algebra to which the attribute node belongs
     */
    public Algebra getAlgebra() {
        return this.algebra;
    }

    /**
     * Returns the <code>Constant</code> this <code>ValueNode</code> is
     * representing.
     * @return the <code>Constant</code> this <code>ValueNode</code> is
     *         representing
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * Returns a symbolic description of the value, which uniquely identifies
     * the value in the algebra.
     */
    public String getSymbol() {
//        if (hasValue()) {
            return getAlgebra().getSymbol(getValue());
//        } else {
//            return null;
//        }
    }
//
//    /**
//     * Indicates if the constant has a definite value, i.e., is not a variable.
//     * Convenience method for <code>getConstant() != null</code>.
//     * @return <code>true</code> if this node's value is not a variable
//     */
//    public boolean hasValue() {
//        return this.value != null;
//    }

    /**
     * This methods returns an indication of the variable if there is no
     * associated algebra, or a description of the value otherwise.
     */
    @Override
    public String toString() {
        String algebraName =
            AttributeAspect.getAttributeValueFor(getAlgebra()).getName();
        return algebraName + CONTENT_SEPARATOR + this.value;
    }

    /**
     * the algebra to which this value belongs; <code>null</code> if the node
     * stands for a variable.
     */
    private final Algebra algebra;
    /**
     * the operation represented by this value node; <code>null</code> if the
     * node stands for a variable.
     */
    private final Object value;

    /** Tests if a given graph contains value nodes. */
    static public boolean hasValueNodes(Graph graph) {
        boolean result = false;
        Iterator<? extends Node> nodeIter = graph.nodeSet().iterator();
        while (!result && nodeIter.hasNext()) {
            result = (nodeIter.next() instanceof ValueNode);
        }
        return result;
    }
}
