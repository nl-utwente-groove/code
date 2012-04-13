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
 * $Id: Algebra.java,v 1.2 2007-03-30 15:50:31 rensink Exp $
 */
package groove.algebra;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
 
/**
 * Generic class defining the structure of all implemented algebras.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.2 $ $Date: 2007-03-30 15:50:31 $
 */
public abstract class Algebra {

	/**
	 * The name of the algebra.
	 */
	private String name;

	/**
	 * A mapping from a string-representation of each operation in this algebra to the actual {@link groove.algebra.Operation}.
	 */
	private HashMap<String,Operation> operations = new HashMap<String,Operation>();

	/**
	 * Creates an algebra with the specified name.
	 * @param name the name for this algebra
	 */
	protected Algebra(String name) {
		this.name = name;
	}

	/**
	 * Method adding an given operation to the set of operations of this signature.
	 * If the operation is not already specified, it is added to the set of operation
	 * and the return-value is 0. Otherwise, nothing is done and -1 is returned.
	 * @param operation the operation to be added
	 * @return <tt>true</tt> if the set did not already contain the specified element, false otherwise
	 */
	public boolean addOperation(Operation operation) {
		Object result = operations.put(operation.symbol(), operation);
		return (result != null);
	}

	/**
	 * Checks whether a given operation is valid within the algebra.
	 * @param operation the operation to check validity for
	 * @return <tt>true</tt> if the given operation is valid in this algebra, <tt>false</tt> otherwise
	 */
	public boolean isValidOperation(Operation operation) {
		return operations.containsValue(operation);
	}

	/**
	 * Gets the operation in this algebra corresponding to a given symbol.
	 * @param symbol the string representing the operation looked for
	 * @return the operations represented by the given symbol
	 */
	public Operation getOperation(String symbol) throws UnknownSymbolException {
	    if (operations.containsKey(symbol))
	        return operations.get(symbol);
	    else
	        throw new UnknownSymbolException(getName() + " does not contain the operation represented by " + symbol);
	}

	/**
	 * Method returning the set of all operations of this algebra.
	 * @return the set of operations in this algebra
	 */
	public Set<Operation> getOperations() {
	    Set<Operation> result = new HashSet<Operation>();
	    result.addAll(operations.values());
		return result;
	}

	/**
	 * Method returning the set containing the string-representations
	 * of all the operations available in this algebra.
	 * @return the set of string-representations of all the operations
	 * available in this algera
	 */
	public Set<String> getOperationSymbols() {
		return operations.keySet();
	}
	/**
	 * Method returning the operations having an arity equal to the given arity. This method
	 * is convenient when you want to have the set of operations that form the domain of the
	 * algebra (they all have arity 0), or the set of operations (they have arity > 0). 
	 * @param arity the arity of the operations that will be part of the resulting set
	 * @return the set of operations with the given arity
	 */
	public Set<Operation> getOperations(int arity) {
		Set<Operation> result = new HashSet<Operation>();
		for (Operation oper: operations.values()) {
			if (oper.arity() == arity) {
				result.add(oper);
			}
		}
		return result;
	}

	/**
	 * Returns the name of this algebra.
	 * @return the name of this algebra
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the prefix of this algebra.
	 * @return the prefix of this algebra
	 */
	public abstract String prefix();

	/**
	 * Checks whether two algebras are equal.
	 * Equality is decided on the basis of the algebra name.
	 */
	@Override
	public boolean equals(Object object) {
		// the given object must be an instance of class Algebra
		if (object instanceof Algebra) {
			Algebra algebra = (Algebra) object;
			boolean result = algebra.getName().equals(getName());
			assert result == algebra.getOperations().equals(getOperations());
			return result;
		} else {
			return true;
		}
	}

	/**
	 * The hash code is based on the algebra name.
	 */
	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public String toString() {
	    return name + " with the following operations:\n" + operations.values().toString();
	}
}