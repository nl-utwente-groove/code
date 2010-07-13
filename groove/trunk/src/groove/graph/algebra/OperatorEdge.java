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
 * $Id: ProductEdge.java,v 1.9 2007-10-18 14:57:42 rensink Exp $
 */
package groove.graph.algebra;

import groove.algebra.Operation;
import groove.graph.AbstractEdge;
import groove.graph.DefaultLabel;

/**
 * This class represents the edges in attributed graphs which support the
 * application of algebra operations on tuples of data values. The source- node
 * should be an instance of {@link groove.graph.algebra.ProductNode} and the
 * target-node should be an instance of {@link groove.graph.algebra.VariableNode}.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class OperatorEdge extends
        AbstractEdge<ProductNode,DefaultLabel,VariableNode> {
    /**
     * Constructs an edge for a given operation.
     * @param source the product node that is the source of the edge
     * @param target the target node for the edge
     * @param operation the associated operation
     */
    public OperatorEdge(ProductNode source, VariableNode target,
            Operation operation) {
        super(source, DefaultLabel.createLabel(operation.getSymbol()), target);
        this.operation = operation;
    }

    /**
     * Returns the <code>operation</code>-field of this
     * <code>ProductEdge</code>.
     * @return the <code>operation</code> of this <code>ProductEdge</code>
     */
    public Operation getOperation() {
        return this.operation;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof OperatorEdge)) {
            return false;
        } else {
            OperatorEdge other = (OperatorEdge) object;
            if (!source().equals(other.source())) {
                return false;
            } else if (!target().equals(other.target())) {
                return false;
            } else if (!getOperation().equals(other.getOperation())) {
                return false;
            }
        }
        return true;
    }

    /** The operation represented by this edge. */
    private final Operation operation;
}
