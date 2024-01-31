package nl.utwente.groove.algebra;

import java.util.List;

/**
 * Interface for an algebra operation.
 */
public interface Operation {
    /**
     * Applies this operation on the list of operands and returns the result
     * value. Throws an exception if the operation cannot be applied.
     * @param args the operands on which this operation operates
     * @return the resulting value when applying this operation on its
     *         <tt>args</tt>
     * @throws ErrorValue if the operation cannot be applied for any reason
     * @see #applyFoldError(List)
     */
    public Object apply(List<Object> args) throws ErrorValue;

    /**
     * Applies this operation on the list of operands and returns the result
     * value, also if this is an error value.
     * @param args the operands on which this operation operates
     * @return the resulting value when applying this operation on its
     *         <tt>args</tt>
     * @see #apply(List)
     */
    default public Object applyFoldError(List<Object> args) {
        try {
            return apply(args);
        } catch (ErrorValue error) {
            return error;
        }
    }

    /**
     * Returns the string representation of this operation.
     */
    public String getName();

    /**
     * Returns the number of parameters of this operation.
     */
    public int getArity();

    /**
     * Returns the algebra to which this operation belongs.
     */
    public Algebra<?> getAlgebra();

    /**
     * Returns the algebra to which the result of the operation belongs. Note
     * that this may differ from {@link #getAlgebra()}.
     */
    public Algebra<?> getResultAlgebra();
}