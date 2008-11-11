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
 * $Id: Algebra.java,v 1.4 2007-07-21 20:07:43 rensink Exp $
 */
package groove.algebra;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Generic class defining the structure of all implemented algebras.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2007-07-21 20:07:43 $
 */
public abstract class Algebra {
    /**
     * Creates a signature with the specified name.
     * @param name the short name for this signature
     * @param description the long name for this signature
     */
    protected Algebra(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Method adding an given operation to the set of operations of this
     * signature. If the operation is not already specified, it is added to the
     * set of operation and the return-value is 0. Otherwise, nothing is done
     * and -1 is returned.
     * @param operation the operation to be added
     * @return <tt>true</tt> if the set did not already contain the specified
     *         element, false otherwise
     */
    boolean addOperation(Operation operation) {
        Object result = this.operations.put(operation.symbol(), operation);
        return (result == null);
    }

    /**
     * Checks whether a given operation is valid within the algebra.
     * @param operation the operation to check validity for
     * @return <tt>true</tt> if the given operation is valid in this algebra,
     *         <tt>false</tt> otherwise
     */
    public boolean isValidOperation(Operation operation) {
        return this.operations.containsValue(operation);
    }

    /**
     * Gets the operation in this algebra corresponding to a given symbol.
     * @param symbol the string representing the operation looked for
     * @return the operations represented by the given symbol
     */
    public Operation getOperation(String symbol) throws UnknownSymbolException {
        Operation result = this.operations.get(symbol);
        if (result == null) {
            result = getConstant(symbol);
        }
        if (result == null) {
            throw new UnknownSymbolException(String.format(
                "'%s' is not an operation or constant of signature '%s'",
                symbol, getName()));
        }
        return result;
    }

    /**
     * Attempts to turn a string into a constant of this signature.
     * @param text the string to be turned into a constant
     * @return the constant obtained from <code>text</code>, or
     *         <code>null</code> if <code>text</code> does not constitute a
     *         correct constant of this signature
     */
    abstract public Constant getConstant(String text);

    /**
     * Method returning the set of all operations of this algebra.
     * @return the set of operations in this algebra
     */
    public Set<Operation> getOperations() {
        Set<Operation> result = new HashSet<Operation>();
        result.addAll(this.operations.values());
        return result;
    }

    /**
     * Method returning the set containing the string-representations of all the
     * operations available in this algebra.
     * @return the set of string-representations of all the operations available
     *         in this algera
     */
    public Set<String> getOperationSymbols() {
        return this.operations.keySet();
    }

    /**
     * Method returning the operations having an arity equal to the given arity.
     * This method is convenient when you want to have the set of operations
     * that form the domain of the algebra (they all have arity 0), or the set
     * of operations (they have arity > 0).
     * @param arity the arity of the operations that will be part of the
     *        resulting set
     * @return the set of operations with the given arity
     */
    public Set<Operation> getOperations(int arity) {
        Set<Operation> result = new HashSet<Operation>();
        for (Operation oper : this.operations.values()) {
            if (oper.arity() == arity) {
                result.add(oper);
            }
        }
        return result;
    }

    /**
     * Returns a human-understandable description of this signature.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the (brief) name of this signature.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Attempts to turn an object, presumably a value of this algebra, into a
     * unique string description.
     * @throws IllegalArgumentException if <code>value</code> is not a value
     *         of this algebra.
     */
    abstract public String getSymbol(Object value);

    /**
     * Checks whether two algebras are equal. Equality is decided on the basis
     * of the algebra name.
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
        return getDescription() + " with operators " + this.operations.values();
    }

    /**
     * The long, human-readable description of this signature.
     */
    private final String description;

    /** The short name of this signature. */
    private final String name;

    /**
     * A mapping from a string-representation of each operation in this algebra
     * to the actual {@link groove.algebra.Operation} .
     */
    private final Map<String,Operation> operations =
        new HashMap<String,Operation>();
}