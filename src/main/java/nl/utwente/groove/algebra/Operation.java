package nl.utwente.groove.algebra;

import java.util.List;

import nl.utwente.groove.util.Exceptions;

/**
 * Abstract class for an algebra operation.
 */
public abstract class Operation {
    /** Constructs an operation. */
    protected Operation(AlgebraFamily family, Operator operator) {
        this.operator = operator;
        this.declaringAlgebra = family.getAlgebra(operator.getDeclaringSort());
        this.resultAlgebra = family.getAlgebra(operator.getResultSort());
    }

    /**
     * Applies this operation on the list of operands and returns the result
     * value. Throws an exception if the operation cannot be applied,
     * either because one or more of the arguments are themselves error values
     * or because the operation is not defined on the argument values.
     * @param args the operands on which this operation operates
     * @return the resulting value when applying this operation on its
     *         <tt>args</tt>
     * @throws ErrorValue if the operation cannot be applied for any reason
     * @see #applyStrict(List)
     */
    public Object apply(List<Object> args) throws ErrorValue {
        for (var arg : args) {
            if (arg instanceof ErrorValue error) {
                throw getResultAlgebra().errorValue(error);
            }
        }
        try {
            return invoke(args);
        } catch (IllegalAccessException exc) {
            throw Exceptions.illegalArg("Can't apply %s to %s", this, args);
        } catch (ReflectiveOperationException | IllegalArgumentException exc) {
            // this catches any invocation error, including IllegalArgumentExceptions
            var error = exc.getCause() instanceof Exception inner
                ? inner
                : exc;
            throw getResultAlgebra().errorValue(error);
        }
    }

    /** Invokes the executable in this operation with the given list of argument. */
    abstract protected Object invoke(List<Object> args) throws ReflectiveOperationException;

    /**
     * Applies this operation on the list of operands and returns the result
     * value, also if this is an error value.
     * @param args the operands on which this operation operates
     * @return the resulting value when applying this operation on its
     *         <tt>args</tt>
     * @see #apply(List)
     */
    public Object applyStrict(List<Object> args) {
        try {
            return apply(args);
        } catch (ErrorValue error) {
            return error;
        }
    }

    /**
     * Returns the operator implemented by this operation.
     */
    public Operator getOperator() {
        return this.operator;
    }

    /** The operator implemented by this operation. */
    private final Operator operator;

    /**
     * Returns the algebra to which this operation belongs.
     */
    public Algebra<?> getDeclaringAlgebra() {
        return this.declaringAlgebra;
    }

    /** The algebra in which the operation is declared. */
    private final Algebra<?> declaringAlgebra;

    /**
     * Returns the algebra to which the result of the operation belongs. Note
     * that this may differ from {@link #getDeclaringAlgebra()}.
     */
    public Algebra<?> getResultAlgebra() {
        return this.resultAlgebra;
    }

    /** The algebra to which the result of the operation belongs. */
    private final Algebra<?> resultAlgebra;

    @Override
    public String toString() {
        return getOperator().getName();
    }
}