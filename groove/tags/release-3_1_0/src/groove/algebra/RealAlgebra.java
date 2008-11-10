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
 * $Id: RealAlgebra.java,v 1.2 2007-08-26 07:24:19 rensink Exp $
 */
package groove.algebra;

/**
 * Interface for underlying algebras.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface RealAlgebra<T> {
    /**
     * Turns a given string symbol into a value of this algebra. Returns
     * <code>null</code> if the symbol is not recognized as a value of this
     * algebra. Note that the symbol does not need to be a constant of the
     * signature, but may be an algebra-internal representation.
     * @param symbol the symbol to be converted
     * @return the value corresponding to <code>symbol</code>, or
     *         <code>null</code> if
     *         <code>symbol does not represent a value of this algebra</code>
     */
    public T getValue(String symbol);

    /**
     * Converts a value of this algebra into a symbolic string representation of
     * that value. Note that the symbol does not have to be one of the constants
     * of the signature. This is the inverse operation of
     * {@link #getValue(String)}.
     * @param value the value to be converted
     * @return a string representation of <code>value</code>, or
     *         <code>null</code> if <code>value</code> is not a value of
     *         this algebra.
     */
    public String getSymbol(T value);
}
