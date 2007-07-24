/**
 * 
 */
package groove.algebra;

/**
 * Integer algebra based on the java type {@link Integer}.
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public class JavaIntAlgebra implements IntSignature<Integer> {
    /** This implementation applies the java operator <code>+</code> to the arguments. */
    public Integer add(Integer arg0, Integer arg1) {
        return arg0+arg1;
    }
    
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
    public Integer getValue(String symbol) {
    	try {
    		return Integer.parseInt(symbol);
    	} catch (NumberFormatException exc) {
    		return null;
    	}
    }
    
    /**
     * Converts a value of this algebra into a symbolic string representation
     * of that value.
     * Note that the symbol does not have to be one of the constants of the signature.
     * This is the inverse operation of {@link #getValue(String)}.
     * @param value the value to be converted
     * @return a string representation of <code>value</code>, or <code>null</code> if
     * <code>value</code> is not a value of this algebra.
     */
    public String getSymbol(Integer value) {
    	return value.toString();
    }

    /** Name of the algebra. */
    public static final String NAME = "jint";
}
