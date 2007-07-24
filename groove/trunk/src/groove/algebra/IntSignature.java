package groove.algebra;

/**
 * Interface for integer algebras. 
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public interface IntSignature<T> {
    /** Addition of two integers. */
    T add(T arg1, T arg2);
    
    /** Name of this signature. */
    static final String NAME = "int";
}
