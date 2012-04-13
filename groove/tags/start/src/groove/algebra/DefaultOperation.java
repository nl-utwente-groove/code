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
 * $Id: DefaultOperation.java,v 1.1.1.2 2007-03-20 10:42:39 kastenberg Exp $
 */
package groove.algebra;

import java.util.List;


/**
 * Class implementing the <tt>Operation</tt> interface.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:39 $
 */
public class DefaultOperation implements Operation {

	/** the algebra to which this operation belongs */
    protected Algebra algebra;
	/** the algebra-type to which this operation belongs */
	protected int type;
	/** the symbol of the operand */
	protected String symbol;
	/** the arity of the operand */
	protected int arity;

	/**
	 * Constructor.
	 * @param algebra the algebra for this operation
	 * @param symbol the symbol representing this operation
	 * @param arity the arity of this operation
	 */
	public DefaultOperation(Algebra algebra, String symbol, int arity) {
		this.algebra = algebra;
		this.symbol = symbol;
		this.arity = arity;
	}

	/**
	 * Constructor.
	 * @param symbol the symbol of this operation
	 * @param arity the arity of this operation
	 */
	protected DefaultOperation(String symbol, int arity) {
		this(null, symbol, arity);
	}

	/**
	 * Method setting the different fields.
	 * @param algebra the new algebra
	 * @param symbol the new symbol
	 * @param arity the new arity
	 */
	public void set(Algebra algebra, String symbol, int arity) {
		if (algebra != null)
			this.algebra = algebra;
		if (symbol != null)
			this.symbol = symbol;
		if (arity != -1)
			this.arity = arity;
	}

	/* (non-Javadoc)
	 * @see groove.algebra.Operation#algebra()
	 */
	public Algebra algebra() {
		return algebra;
	}

	/* (non-Javadoc)
	 * @see groove.algebra.Operation#type()
	 */
	public int type() {
	    return type;
	}

	/* (non-Javadoc)
	 * @see groove.algebra.Operation#prefix()
	 */
	public String prefix() {
		return algebra.prefix();
	}

	/* (non-Javadoc)
	 * @see groove.algebra.Operation#symbol()
	 */
	public String symbol() {
		return symbol;
	}

	/* (non-Javadoc)
	 * @see groove.algebra.Operation#arity()
	 */
	public int arity() {
		return arity;
	}

	/**
	 * Method overriding the standard equals-method. This method returns <tt>true</tt>
	 * if both <tt>Operation</tt>'s have the same symbol, otherwise it will return
	 * <tt>false</tt>.
	 * @param object the object to which the current object will be compared
	 * @return <tt>true</tt> if both objects have the same symbol, <tt>false</tt> otherwise
	 */
	public boolean equals(Object object) {
		if (object == null)
			return false;
		else {
			Operation operation = (Operation) object;
			if (!(algebra.equals(operation.algebra())))
				return false;
			else if (!(symbol.equals(operation.symbol())))
				return false;
			else if (!(arity == operation.arity()))
				return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + type;
		hash = 31 * hash + arity;
		hash = 31 * hash + symbol.hashCode();
		return hash;
	}

	/* (non-Javadoc)
	 * @see groove.util.Operation#apply(java.util.List)
	 */
	public Constant apply(List<Constant> operands) {
		return new DefaultConstant();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return symbol;
	}
}