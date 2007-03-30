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
 * $Id: ValueNode.java,v 1.3 2007-03-30 15:50:45 rensink Exp $
 */
package groove.graph.algebra;

import java.util.Iterator;

import groove.algebra.Algebra;
import groove.algebra.Constant;
import groove.algebra.Variable;
import groove.graph.Graph;
import groove.graph.Node;

/**
 * Implementation of graph elements that represent algebraic data values.
 *
 * @author Harmen Kastenberg
 * @version $Revision: 1.3 $ $Date: 2007-03-30 15:50:45 $
 */
public class ValueNode extends ProductNode {
	/** Tests if a given graph contains value nodes. */
	static public boolean isAttributed(Graph graph) {
		boolean result = false;
		Iterator<? extends Node> nodeIter = graph.nodeSet().iterator();
		while (!result && nodeIter.hasNext()) {
			result = (nodeIter.next() instanceof ValueNode);
		}
		return result;
	}
	
// AREND made the superclass a ProductNode to unify the 
// treatment of constants and other operators
	/** the algebra to which this operation belongs */
	private Algebra algebra;
	/** the operation represented by this node */
	private Constant constant;

	/**
	 * Constructs a node for a given {@link groove.algebra.Constant}.
	 * Preferred construction through {@link AlgebraGraph#getValueNode(Constant)}.
	 * @param constant the constant to create a graph node for
	 */
	ValueNode(Constant constant) {
		super(0);
		this.algebra = constant.algebra();
		this.constant = constant;
	}
	
	/** 
	 * Constructs a value node for a fresh variable.
	 */
	public ValueNode() {
		this(new Variable());
	}
//
//    /**
//     * Creates a graph node {@link groove.graph.algebra.ValueNode} for a given {@link groove.algebra.Variable}.
//     * @param variable the variable to create a graph node for
//     */
//    public ValueNode(Variable variable) {
//		super(0);
//        this.constant = variable;
//    }
//
//    /**
//     * Creates a graph node from a given {@link groove.algebra.Algebra} and string representing the value.
//	 * @param algebra the algebra in which to look for the value
//	 * @param symbol the symbol representing the value to look for
//	 */
//	public ValueNode(Algebra algebra, String symbol) throws UnknownSymbolException {
//		this((Constant) algebra.getOperation(symbol));
//        try {
//            this.algebra = algebra;
//    		this.constant = (Constant) algebra.getOperation(symbol);
//        } catch (UnknownSymbolException use) {
//            use.printStackTrace();
//        }
//	}

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
    @Override
	public String toString() {
		if (getAlgebra() == null) {
			return "x"+(getNumber()-AlgebraGraph.START_NODE_NR);
		} else {
			return getAlgebra().prefix() + constant.symbol();
		}
	}
}
