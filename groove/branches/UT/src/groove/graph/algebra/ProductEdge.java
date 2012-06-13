// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: ProductEdge.java,v 1.1.1.2 2007-03-20 10:42:43 kastenberg Exp $
 */

package groove.graph.algebra;

import groove.algebra.Constant;
import groove.algebra.Operation;
import groove.graph.DefaultEdge;
import groove.graph.Edge;
import groove.graph.Node;

/**
 * This class represents the edges in attributed graphs which support the
 * application of algebra operations on tuples of data values. The source-
 * node should be an instance of {@link groove.graph.algebra.ProductNode}
 * and the target-node should be an instance of {@link groove.graph.algebra.ValueNode}.
 *
 * @author Harmen Kastenberg
 * @version $Revision: 1.1.1.2 $
 */
public class ProductEdge extends DefaultEdge {

    public ProductEdge(ProductNode source, ValueNode target, Operation operation) {
        super(source, operation.symbol(), target);
        this.operation = operation;
    }

    public ProductEdge(Node[] ends, Operation operation) {
        this((ProductNode) ends[Edge.SOURCE_INDEX], (ValueNode) ends[Edge.TARGET_INDEX], operation);
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
    public Constant getResult() {
        Constant result = operation.apply(source().getOperands());
        return result;
    }

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

    /** Specialises the return type. */
    @Override
	public ValueNode target() {
		return (ValueNode) target;
	}

    /** Specialises the return type. */
	@Override
	public ProductNode source() {
		return (ProductNode) source;
	}


	protected Operation operation;
}