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
 * $Id: ValueNode.java,v 1.1.1.2 2007-03-20 10:42:43 kastenberg Exp $
 */
package groove.graph.algebra;

import groove.algebra.Algebra;
import groove.algebra.Constant;
import groove.algebra.UnknownSymbolException;
import groove.algebra.Variable;
import groove.graph.DefaultNode;

/**
 * Implementation of graph elements that represent algebraic data values.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:43 $
 */
public class ValueNode extends DefaultNode {

	/** the algebra to which this operation belongs */
	private Algebra algebra;
	/** the operation represented by this node */
	private Constant constant;

	/**
	 * Creates a graph node for a given {@link groove.algebra.Constant}.
	 * @param constant the constant to create a graph node for
	 */
	public ValueNode(Constant constant) {
		super(AlgebraGraph.getNextNodeNr());
        try {
            this.algebra = constant.algebra();
    		this.constant = (Constant) algebra.getOperation(constant.symbol());
        } catch (UnknownSymbolException use) {
            use.printStackTrace();
        }
	}

    /**
     * Creates a graph node {@link groove.graph.algebra.ValueNode} for a given {@link groove.algebra.Variable}.
     * @param variable the variable to create a graph node for
     */
    public ValueNode(Variable variable) {
    	super(AlgebraGraph.getNextNodeNr());
        this.constant = variable;
    }

    /**
     * Creates a graph node from a given {@link groove.algebra.Algebra} and string representing the value.
	 * @param algebra the algebra in which to look for the value
	 * @param symbol the symbol representing the value to look for
	 */
	public ValueNode(Algebra algebra, String symbol) {
		super(AlgebraGraph.getNextNodeNr());
        try {
            this.algebra = algebra;
    		this.constant = (Constant) algebra.getOperation(symbol);
        } catch (UnknownSymbolException use) {
            use.printStackTrace();
        }
	}

	/**
	 * Method returning the algebra to which the attribute node belongs.
	 * @return the algebra to which the attribute node belongs
	 */
	public Algebra getAlgebra() {
		return algebra;
	}

	/**
	 * Method returning the operand of the attribute node.
	 * @return the operand of the attribute node
	 */
	public Constant getOperation() {
		return constant;
	}

    /**
     * Returns the <code>Constant</code> this <code>ValueNode</code> is representing.
     * @return the <code>Constant</code> this <code>ValueNode</code> is representing
     */
    public Constant getConstant() {
        return constant;
    }

    /**
     * Sets the <code>constant</code>-field of this <code>ValueNode</code>
     * @param constant the new value for the <code>constant</code>-field
     */
    public void setConstant(Constant constant) {
        this.constant = constant;
    }

	/**
	 * This methods returns an indication of the variable if there is
	 * no associated algebra, or a description of the value otherwise.
	 */
	public String toString() {
		if (getAlgebra() == null) {
			return "x"+(getNumber()-AlgebraGraph.START_NODE_NR);
		} else {
			return getAlgebra().prefix() + constant.symbol();
		}
	}
}
