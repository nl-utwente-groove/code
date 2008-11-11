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
 * $Id: ProductNode.java,v 1.8 2008-02-12 15:15:32 fladder Exp $
 */
package groove.graph.algebra;

import groove.algebra.Constant;
import groove.graph.DefaultNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class represent tuples of data values on which one can
 * perform algebraic operations. A product node has <i>arguments</i>, which are
 * the {@link ValueNode}s attached to it through {@link ArgumentEdge}s, and
 * <i>operands</i>, which are the corresponding {@link Constant}s on those
 * nodes.
 * @author Harmen Kastenberg
 * @version $Revision 1.0$ $Date: 2008-02-12 15:15:32 $
 */
public class ProductNode extends DefaultNode {
    /**
     * Constructor.
     */
    public ProductNode(int arity) {
        super();
        this.arguments = new ArrayList<VariableNode>(arity);
        for (int i = 0; i < arity; i++) {
            this.arguments.add(null);
        }
        this.argCount = 0;
    }

    /**
     * Constructor.
     */
    public ProductNode(List<VariableNode> arguments) {
        super();
        this.arguments = new ArrayList<VariableNode>(arguments);
        assert !this.arguments.contains(null) : "Null argument not allowed";
        this.argCount = arguments.size();
    }

    /**
     * Sets one of the arguments of the product node.
     * @throws IllegalArgumentException if argument number <code>i</code> has
     *         already been set
     */
    public void setArgument(int i, VariableNode arg) {
        if (arg == null) {
            throw new IllegalArgumentException(
                String.format("Null argument not allowed"));
        }
        VariableNode oldArg = this.arguments.set(i, arg);
        if (oldArg == null) {
            this.argCount++;
        } else if (!oldArg.equals(arg)) {
            throw new IllegalArgumentException(String.format(
                "Argument number %d already contains %s", i, oldArg));
        }
    }

    /** Retrieves the list of arguments of the product node. */
    public List<VariableNode> getArguments() {
        return this.arguments;
    }

    /**
     * Adds an operand to <code>operands</code>.
     * @param constant the {@link groove.algebra.Constant} to be added to the
     *        <code>operands</code>
     * @return <tt>true</tt> (as per the general contract of the
     *         Collection.add method).
     * @deprecated operands of product nodes are variable nodes
     */
    @Deprecated
    public boolean addOperand(Constant constant) {
        return this.operands.add(constant);
    }

    /**
     * Returns the list of operands.
     * @return the list of operands.
     * @deprecated Operands should be {@link VariableNode}s
     */
    @Deprecated
    public List<Object> getOperands() {
        if (this.operands == null) {
            this.operands = computeOperands();
        }
        return this.operands;
    }

    /**
     * Computes the operands from the arguments.
     * @return a list of constant operand values
     * @throws IllegalStateException if there are operands which do not carry
     *         values
     * @deprecated Operands should be {@link VariableNode}s
     */
    @Deprecated
    protected List<Object> computeOperands() {
        if (this.argCount < arity()) {
            throw new IllegalStateException(String.format(
                "Arguments %s have not all been set", this.arguments));
        }
        List<Object> result = new ArrayList<Object>(this.arguments.size());
        for (VariableNode arg : this.arguments) {
            Object value = arg;//.getValue();
            if (value == null) {
                throw new IllegalStateException(String.format(
                    "Argument %s does not have value", arg));
            }
            result.add(value);
        }
        assert result.size() == arity();
        return result;
    }

    /**
     * Gets the operand at the given index.
     * @param index the index of the operand to be returned
     * @return the OperationInstance at <code>index</code>
     * @deprecated Operands should be {@link VariableNode}s
     */
    @Deprecated
    public Object getOperand(int index) {
        return getOperands().get(index);
    }

    /**
     * Returns the arity of this <code>ProductNode</code>
     * @return the arity of this <code>ProductNode</code>
     */
    public int arity() {
        return this.arguments.size();
    }

    @Override
    public String toString() {
        return "p" + (getNumber() - DefaultNode.MAX_NODE_NUMBER);
    }

    /**
     * The list of arguments of this product node (which are the value nodes to
     * which an outgoing AlgebraEdge is pointing).
     */
    private final List<VariableNode> arguments;
    /**
     * The number of arguments (i.e., elements of <code>argument</code>) that
     * have already been set.
     */
    private int argCount;
    /** the list of operands contained in this <code>ProductNode</code> */
    @Deprecated
    private List<Object> operands;
}
