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

import groove.algebra.Operator;
import groove.trans.RuleEdge;
import groove.trans.RuleLabel;

/**
 * This class represents the edges in attributed graphs which support the
 * application of algebra operations on tuples of data values. The source- node
 * should be an instance of {@link groove.graph.algebra.ProductNode} and the
 * target-node should be an instance of {@link groove.graph.algebra.VariableNode}.
 * AREND this can probably be merged with {@link RuleEdge}
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class OperatorEdge extends RuleEdge {
    /**
     * Constructs an edge for a given operation.
     * @param source the product node that is the source of the edge
     * @param label the rule label; must satisfy {@link RuleLabel#isOperator()}
     * @param target the target node for the edge
     */
    public OperatorEdge(ProductNode source, RuleLabel label, VariableNode target) {
        super(source, label, target);
        assert label.isOperator();
        this.operator = label.getOperator();
    }

    @Override
    public ProductNode source() {
        return (ProductNode) super.source();
    }

    @Override
    public VariableNode target() {
        return (VariableNode) super.target();
    }

    /**
     * Returns the <code>operation</code>-field of this
     * <code>ProductEdge</code>.
     * @return the <code>operation</code> of this <code>ProductEdge</code>
     */
    public Operator getOperator() {
        return this.operator;
    }

    /** The operation represented by this edge. */
    private final Operator operator;
}
