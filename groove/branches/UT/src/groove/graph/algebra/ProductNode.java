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
 * $Id: ProductNode.java,v 1.1.1.1 2007-03-20 10:05:37 kastenberg Exp $
 */

package groove.graph.algebra;

import groove.algebra.Constant;
import groove.graph.DefaultNode;
import groove.util.Groove;

import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class represent tuples of data values on which one can
 * perform algebraic operations.
 * 
 * @author Harmen Kastenberg
 * @version $Revision 1.0$ $Date: 2007-03-20 10:05:37 $
 */
public class ProductNode extends DefaultNode {

    /**
     * Constructor.
     */
    public ProductNode() {
    	super(AlgebraGraph.getNextNodeNr());
        operands = new ArrayList<Constant>();
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
        return operands;
    }

    /**
     * Gets the operand at the given index.
     * @param index the index of the operand to be returned
     * @return the OperationInstance at <code>index</code>
     */
    public Constant getOperand(int index) {
        return operands.get(index);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object object) {
        // the given object may not be NULL and must be of this class
        if (object == null || !(object instanceof ProductNode))
            return false;

        ProductNode other = (ProductNode) object;

        // check whether the arities of the two are equal
        if (this.arity() != other.arity())
            return false;

        // check whether the operands of the two are equal, given that
        // the arities are equal
        for (int i = 0; i < arity(); i++) {
            Constant constant = getOperand(i);
            Constant compareConstant = other.getOperand(i);
            if (!(constant.equals(compareConstant)))
            	return false;
        }

        if (!super.equals(object))
            return false;

        // if we reach this point, both objects are equal
        return true;
    }

    /**
     * Returns the arity of this <code>ProductNode</code>
     * @return the arity of this <code>ProductNode</code>
     */
    public int arity() {
        return operands.size();
    }
    
    public String toString() {
    	List<Constant> operands = getOperands();
    	if (operands.isEmpty()) {
    		return "p"+(getNumber() - AlgebraGraph.START_NODE_NR);
    	} else {
    		return Groove.toString(getOperands().toArray());
    	}
    }

    /** the list of operands contained in this <code>ProductNode</code> */
    protected List<Constant> operands;
}
