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
 * $Id: DefaultOperation.java,v 1.5 2007-07-21 20:07:43 rensink Exp $
 */
package groove.algebra;

/**
 * Class implementing the <tt>Operation</tt> interface.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.5 $ $Date: 2007-07-21 20:07:43 $
 */
abstract public class DefaultOperation implements Operation {
    /**
     * Constructor.
     * @param algebra the algebra for this operation
     * @param symbol the symbol representing this operation
     * @param arity the arity of this operation
     */
    public DefaultOperation(Algebra algebra, String symbol, int arity) {
        this(algebra, symbol, arity, algebra);
    }
    
    /**
     * Constructor for an operation whose result type differs from the algebra in which it is defined.
     * @param algebra the algebra for this operation
     * @param symbol the symbol representing this operation
     * @param arity the arity of this operation
     */
    public DefaultOperation(Algebra algebra, String symbol, int arity, Algebra resultType) {
        this.algebra = algebra;
        this.symbol = symbol;
        this.arity = arity;
        this.resultType = resultType;
        assert algebra != null;
    }
//
//	/**
//	 * Constructor.
//	 * @param symbol the symbol of this operation
//	 * @param arity the arity of this operation
//	 */
//	protected DefaultOperation(String symbol, int arity) {
//		this(null, symbol, arity);
//	}
//
//	/**
//	 * Method setting the different fields.
//	 * @param algebra the new algebra
//	 * @param symbol the new symbol
//	 * @param arity the new arity
//	 */
//	public void set(Algebra algebra, String symbol, int arity) {
//		if (algebra != null)
//			this.algebra = algebra;
//		if (symbol != null)
//			this.symbol = symbol;
//		if (arity != -1)
//			this.arity = arity;
//	}

	public Algebra algebra() {
		return algebra;
	}
    
    public Algebra getResultType() {
        return resultType;
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
	@Override
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

	/** Combines type, arity and the operator symbol. */
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + arity;
		hash = 31 * hash + symbol.hashCode();
		return hash;
	}

	/** Returns the operator's symbol. */
	@Override
	public String toString() {
		return symbol();
	}

	/** the algebra to which this operation belongs */
    private final Algebra algebra;
	/** the symbol of the operation. */
	private final String symbol;
	/** the arity of the operation. */
	private final int arity;
    /** The result algebra of the operation. */
    private final Algebra resultType;
}
