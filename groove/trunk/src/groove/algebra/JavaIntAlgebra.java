/**
 * 
 */
package groove.algebra;

/**
 * Integer algebra based on the java type {@link Integer}.
 * @author Arend Rensink
 * @version $Revision: 1.1 $
 */
public class JavaIntAlgebra implements IntSignature<Integer> {
    /** This implementation applies the java operator <code>+</code> to the arguments. */
    public Integer add(Integer arg1, Integer arg2) {
        return arg1+arg2;
    }

    /** Name of the algebra. */
    public static final String NAME = "jint";
}
