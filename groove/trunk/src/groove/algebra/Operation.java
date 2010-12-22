package groove.algebra;

import java.util.List;

/**
 * Interface for an algebra operation.
 */
public interface Operation {
    /**
     * Applies this operation on the list of operands and returns the result
     * value.
     * @param args the operands on which this operation operates
     * @return the resulting value when applying this operation on its
     *         <tt>args</tt>
     * @throws IllegalArgumentException if the operation cannot be performed,
     *         due to typing errors of the operands or zero division
     */
    public Object apply(List<Object> args) throws IllegalArgumentException;

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