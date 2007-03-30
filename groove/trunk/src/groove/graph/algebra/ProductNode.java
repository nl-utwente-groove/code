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
 * $Id: ProductNode.java,v 1.4 2007-03-30 15:50:45 rensink Exp $
 */

package groove.graph.algebra;

import groove.algebra.Constant;
import groove.graph.DefaultNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class represent tuples of data values on which one can
 * perform algebraic operations.
 * A product node has <i>arguments</i>, which are the {@link ValueNode}s
 * attached to it through {@link AlgebraEdge}s, and <i>operands</i>, which
 * are the corresponding {@link Constant}s on those nodes.
 * @author Harmen Kastenberg
 * @version $Revision 1.0$ $Date: 2007-03-30 15:50:45 $
 */
public class ProductNode extends DefaultNode {
	// AREND I think the operands of a product node should be fixed at
	// construction time, and they should be ValueNodes not Constants
    /**
     * Constructor.
     */
    public ProductNode(int arity) {
    	super(AlgebraGraph.getNextNodeNr());
    	this.arguments = new ArrayList<ValueNode>(arity);
    	for (int i = 0; i < arity; i++) {
    		arguments.add(null);
    	}
    	this.argCount = 0;
    }   

    /**
     * Constructor.
     */
    public ProductNode(List<ValueNode> arguments) {
    	super(AlgebraGraph.getNextNodeNr());
    	this.arguments = new ArrayList<ValueNode>(arguments);
    	assert ! this.arguments.contains(null) : "Null argument not allowed";
    	this.argCount = arguments.size();
    }   

    /**
     * Sets one of the arguments of the product node.
     * @throws IllegalArgumentException if argument number <code>i</code> 
     * has already been set
     */
    public void setArgument(int i, ValueNode arg) {
    	if (arg == null) {
    		throw new IllegalArgumentException(String.format("Null argument not allowed"));
    	}
    	ValueNode oldArg = arguments.set(i, arg);
    	if (oldArg == null) {
        	argCount++;
    	} else if (!oldArg.equals(arg)) {
    		throw new IllegalArgumentException(String.format("Argument number %d already contains %s", i, oldArg));
    	}
    }
    
    /** Retrieves one of the arguments of the product node. */
    public ValueNode getArgument(int i) {
    	return arguments.get(i);
    }

    /**
     * Adds an operand to <code>operands</code>.
     * @param constant the {@link groove.algebra.Constant} to be added to the <code>operands</code>
     * @return <tt>true</tt> (as per the general contract of the Collection.add method).
     */
    public boolean addOperand(Constant constant) {
        return operands.add(constant);
    }

    /**
     * Returns the list of operands.
     * @return the list of operands.
     */
    public List<Constant> getOperands() {
    	if (operands == null) {
    		operands = computeOperands();
    	}
        return operands;
    }
    
    /** 
     * Computes the operands from the arguments. 
     * @return a list of constant operand values
     * @throws IllegalStateException if there are operands which do not carry values
     */
    protected List<Constant> computeOperands() {
    	if (argCount < arity()) {
			throw new IllegalStateException(String.format("Arguments %s have not all been set", arguments));
    	}
    	List<Constant> result = new ArrayList<Constant>(arguments.size());
    	for (ValueNode arg: arguments) {
    		Constant value = arg.getConstant();
    		if (value == null) {
    			throw new IllegalStateException(String.format("Argument %s does not have value", arg));
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
     */
    public Constant getOperand(int index) {
        return getOperands().get(index);
    }

//    @Override
//    public boolean equals(Object object) {
//    	return object instanceof ProductNode && arguments.equals(((ProductNode) object).arguments);
//        // the given object may not be NULL and must be of this class
//        if (!(object instanceof ProductNode))
//            return false;
//
//        ProductNode other = (ProductNode) object;
//
//        // check whether the arities of the two are equal
//        if (this.arity() != other.arity())
//            return false;
//
//        // check whether the operands of the two are equal, given that
//        // the arities are equal
//        for (int i = 0; i < arity(); i++) {
//            Constant constant = getOperand(i);
//            Constant compareConstant = other.getOperand(i);
//            if (!(constant.equals(compareConstant)))
//            	return false;
//        }
//
//        if (!super.equals(object))
//            return false;
//
//        // if we reach this point, both objects are equal
//        return true;
//    }
//
//    /** This implementation returns the hash code of the arguments.
//	@Override
//	protected int computeHashCode() {
//		return arguments.hashCode();
//	}

	/**
     * Returns the arity of this <code>ProductNode</code>
     * @return the arity of this <code>ProductNode</code>
     */
    public int arity() {
        return arguments.size();
    }
    
    @Override
    public String toString() {
    	return "p"+(getNumber() - AlgebraGraph.START_NODE_NR);
    }

    /** 
     * The list of arguments of this product node (which are the value nodes
     * to which an outgoing AlgebraEdge is pointing). 
     */
    private final List<ValueNode> arguments;
    /** 
     * The number of arguments (i.e., elements of <code>argument</code>)
     * that have already been set.
     */
    private int argCount;
    /** the list of operands contained in this <code>ProductNode</code> */
    private List<Constant> operands;
}
