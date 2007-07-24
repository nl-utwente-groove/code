/* $Id: RealAlgebra.java,v 1.1 2007-07-24 06:16:29 rensink Exp $ */
package groove.algebra;

/**
 * Interface for underlying algebras.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface RealAlgebra<T> {
    /**
     * Turns a given string symbol into a value of this algebra.
     * Returns <code>null</code> if the symbol is not recognized as a value
     * of this algebra.
     * Note that the symbol does not need to be a constant of the signature,
     * but may be an algebra-internal representation.
     * @param symbol the symbol to be converted
     * @return the value corresponding to <code>symbol</code>, or <code>null</code>
     * if <code>symbol does not represent a value of this algebra</code>
     */
    public T getValue(String symbol);
    
    /**
     * Converts a value of this algebra into a symbolic string representation
     * of that value.
     * Note that the symbol does not have to be one of the constants of the signature.
     * This is the inverse operation of {@link #getValue(String)}.
     * @param value the value to be converted
     * @return a string representation of <code>value</code>, or <code>null</code> if
     * <code>value</code> is not a value of this algebra.
     */
    public String getSymbol(T value);
}
