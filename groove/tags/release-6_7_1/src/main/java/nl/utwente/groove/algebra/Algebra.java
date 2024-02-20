/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
 * $Id$
 */
package nl.utwente.groove.algebra;

import nl.utwente.groove.algebra.syntax.Expression;

/**
 * Interface of an algebra (a class implementing a {@link Signature}).
 * @param <T> the Java type used as carrier set for this algebra
 * @author Arend Rensink
 * @version $Revision$
 */
public interface Algebra<T> extends Signature {
    /** Tests if a given object is a value of this algebra (i.e., either a correct value
     * or an error value).
     * @see #isValidValue(Object)
     * @see #isErrorValue(Object)
     */
    default boolean isValue(Object value) {
        return isValidValue(value) || isErrorValue(value);
    }

    /** Tests if a given object is a correct value of this algebra (i.e., not an error value). */
    boolean isValidValue(Object value);

    /** Tests if a given object is an error value of this algebra's sort. */
    default boolean isErrorValue(Object value) {
        return value instanceof ErrorValue error && error.getSort() == getSort();
    }

    /**
     * Converts a constant of the correct signature to the corresponding algebra value.
     * @param constant the constant to be converted to a value; required to be of the correct
     * signature
     */
    @SuppressWarnings("unchecked")
    default T toValue(Constant constant) {
        return (T) getFamily().toValue(constant);
    }

    /**
     * Converts a closed term of the correct signature to the corresponding algebra value.
     * @param term the term to be converted to a value; required to be of the correct
     * signature and to satisfy {@link Expression#isTerm()} and {@link Expression#isClosed()}
     * @throws ErrorValue if the conversion involves any operation that cannot be applied
     */
    @SuppressWarnings("unchecked")
    default T toValue(Expression term) throws ErrorValue {
        return (T) getFamily().toValidValue(term);
    }

    /** Returns an error value for this algebra, based on a given exception. */
    default ErrorValue errorValue(Exception exc) {
        return new ErrorValue(getSort(), exc);
    }

    /**
     * Converts a (non-error) constant of the right signature to the corresponding algebra value.
     * Should only be invoked if {@link Constant#isError()} does not hold for {@code constant}.
     * @see #toValue(Expression)
     */
    T toValueFromConstant(Constant constant);

    /**
     * Converts the native Java representation of a data value to
     * its corresponding algebra representation.
     * @param value the native Java representation of an algebra constants for
     * this signature
     * @throws IllegalArgumentException if the parameter is not of the
     * native Java type
     */
    T toValueFromJava(Object value) throws IllegalArgumentException;

    /**
     * Converts a given algebra value to the corresponding Java algebra value.
     * @param value a value from this algebra; must satisfy {@link #isValue(Object)}
     */
    default Object toJavaValue(Object value) {
        return value;
    }

    /**
     * Converts a (valid or error) algebra value to the canonical term representing it.
     * Typically this will be a constant, but for the term algebras it is the value itself.
     * @param value a value from this algebra
     */
    default Expression toTerm(Object value) {
        if (value instanceof ErrorValue) {
            return Constant.error(getSort());
        } else {
            return toValidTerm(value);
        }
    }

    /**
     * Converts a valid algebra value to the canonical term representing it.
     * Typically this will be a constant, but for the term algebras it is the value itself.
     * Should only be called if {@code value} is not an {@link ErrorValue}.
     * @param value a value from this algebra; must satisfy {@link #isValidValue(Object)}
     */
    Expression toValidTerm(Object value);

    /**
     * Converts a (valid or error) algebra value to its symbolic (parsable) string representation.
     * @param value a value from this algebra
     */
    default String toSymbol(Object value) {
        if (value instanceof ErrorValue) {
            return value.toString();
        } else {
            return toValidSymbol(value);
        }
    }

    /**
     * Converts a valid algebra value to its symbolic (parsable) string representation.
     * Should only be called if {@code value} is not an {@link ErrorValue}.
     * @param value a value from this algebra; must satisfy {@link #isValidValue(Object)}
     */
    default String toValidSymbol(Object value) {
        return value.toString();
    }

    /**
     * Returns the name of the algebra.
     * Note that this is <i>not</i> the same as the name of the signature;
     * for the signature name, use {@code getKind().getName()}
     * @see #getSort()
     */
    String getName();

    /**
     * Returns the algebra family to which this algebra primarily belongs.
     * Note that an algebra may belong to more than one family; in that case,
     * {@link AlgebraFamily#DEFAULT} is returned in preference to other values.
     */
    AlgebraFamily getFamily();
}
