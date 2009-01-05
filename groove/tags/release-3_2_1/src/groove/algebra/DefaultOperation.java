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
 * $Id: DefaultOperation.java,v 1.5 2007-07-21 20:07:43 rensink Exp $
 */
package groove.algebra;

/**
 * Class implementing the <tt>Operation</tt> interface.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2007-07-21 20:07:43 $
 * @deprecated Superseded by the new algebra implementation
 */
@Deprecated
abstract public class DefaultOperation implements OldOperation {
    /**
     * Constructor.
     * @param algebra the algebra for this operation
     * @param symbol the symbol representing this operation
     * @param arity the arity of this operation
     */
    public DefaultOperation(OldAlgebra algebra, String symbol, int arity) {
        this(algebra, symbol, arity, algebra);
    }

    /**
     * Constructor for an operation whose result type differs from the algebra
     * in which it is defined.
     * @param algebra the algebra for this operation
     * @param symbol the symbol representing this operation
     * @param arity the arity of this operation
     */
    public DefaultOperation(OldAlgebra algebra, String symbol, int arity,
            OldAlgebra resultType) {
        this.algebra = algebra;
        this.symbol = symbol;
        this.arity = arity;
        this.resultType = resultType;
        assert algebra != null;
    }

    public OldAlgebra algebra() {
        return this.algebra;
    }

    public OldAlgebra getResultType() {
        return this.resultType;
    }

    /*
     * (non-Javadoc)
     * @see groove.algebra.Operation#symbol()
     */
    public String symbol() {
        return this.symbol;
    }

    /*
     * (non-Javadoc)
     * @see groove.algebra.Operation#arity()
     */
    public int arity() {
        return this.arity;
    }

    /**
     * Method overriding the standard equals-method. This method returns
     * <tt>true</tt> if both <tt>Operation</tt>'s have the same symbol,
     * otherwise it will return <tt>false</tt>.
     * @param object the object to which the current object will be compared
     * @return <tt>true</tt> if both objects have the same symbol,
     *         <tt>false</tt> otherwise
     */
    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        } else {
            OldOperation operation = (OldOperation) object;
            if (!(this.algebra.equals(operation.algebra()))) {
                return false;
            } else if (!(this.symbol.equals(operation.symbol()))) {
                return false;
            } else if (!(this.arity == operation.arity())) {
                return false;
            }
        }
        return true;
    }

    /** Combines type, arity and the operator symbol. */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.arity;
        hash = 31 * hash + this.symbol.hashCode();
        return hash;
    }

    /** Returns the operator's symbol. */
    @Override
    public String toString() {
        return symbol();
    }

    /** the algebra to which this operation belongs */
    private final OldAlgebra algebra;
    /** the symbol of the operation. */
    private final String symbol;
    /** the arity of the operation. */
    private final int arity;
    /** The result algebra of the operation. */
    private final OldAlgebra resultType;
}
