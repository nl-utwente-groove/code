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
 * $Id: ProductEdge.java,v 1.8 2007-09-28 10:20:54 rensink Exp $
 */
package groove.graph.algebra;

import groove.algebra.Constant;
import groove.algebra.Operation;
import groove.graph.AbstractBinaryEdge;
import groove.graph.DefaultLabel;

/**
 * This class represents the edges in attributed graphs which support the
 * application of algebra operations on tuples of data values. The source-
 * node should be an instance of {@link groove.graph.algebra.ProductNode}
 * and the target-node should be an instance of {@link groove.graph.algebra.ValueNode}.
 *
 * @author Harmen Kastenberg
 * @version $Revision: 1.8 $
 */
// AREND I would call this OperatorEdge and unify it with ValueEdge
public class ProductEdge extends AbstractBinaryEdge<ProductNode,ValueNode> {
    /**
     * Constructs a product edge for a constant, which is always a self-edge.
     * @param target the source and target of the edge
     * @param operation the constant associated with the edge
     */
    public ProductEdge(ValueNode target, Constant operation) {
        this(target, target, operation);
    }

    /**
     * Constructs an edge for a given operation.
     * @param source the product node that is the source of the edge
     * @param target the target node for the edge
     * @param operation the associated operation
     */
    public ProductEdge(ProductNode source, ValueNode target, Operation operation) {
        super(source, DefaultLabel.createLabel(operation.symbol()), target);
        this.operation = operation;
    }

    /**
     * Returns the <code>operation</code>-field of this <code>ProductEdge</code>.
     * @return the <code>operation</code> of this <code>ProductEdge</code>
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * Returns the result of applying the operations on its operands.
     * @return the result of applying the operations on its operands
     */
    public Object getResult() {
        return operation.apply(source().getOperands());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ProductEdge)) {
            return false;
        }
        else {
            ProductEdge other = (ProductEdge) object;
            if (!this.source().equals(other.source()))
                return false;
            else if (!this.target().equals(other.target()))
                return false;
            else if (!this.getOperation().equals(other.getOperation()))
                return false;
        }
        return true;
    }
//
//    /** Specialises the return type. */
//    @Override
//	public ValueNode target() {
//		return (ValueNode) target;
//	}
//
//    /** Specialises the return type. */
//	@Override
//	public ProductNode source() {
//		return (ProductNode) source;
//	}
//
//	@Override
//	@Deprecated
//	public BinaryEdge newEdge(Node source, Label label, Node target) {
//		throw new UnsupportedOperationException();
//	}

	/** The operation represented by this edge. */
	private final Operation operation;
}
