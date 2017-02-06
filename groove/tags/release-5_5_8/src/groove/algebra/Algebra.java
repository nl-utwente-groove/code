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
 * $Id$
 */
package groove.algebra;

import groove.algebra.syntax.Expression;

/**
 * Interface of an algebra (a class implementing a {@link Signature}).
 * @author Arend Rensink
 * @version $Revision $
 */
public interface Algebra<T> extends Signature {
    /** Tests if a given object is a value of this algebra. */
    boolean isValue(Object value);

    /**
     * Converts a closed term of the correct signature to the corresponding algebra value.
     * @param term the term to be converted to a value; required to be of the correct
     * signature and to satisfy {@link Expression#isTerm()} and {@link Expression#isClosed()}
     */
    T toValue(Expression term);

    /**
     * Converts a constant of the right signature to the corresponding algebra value.
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
    Object toJavaValue(Object value);

    /** 
     * Converts an algebra value to the canonical term representing it.
     * Typically this will be a constant, but for the term algebras it is the value itself.
     * @param value a value from this algebra; must satisfy {@link #isValue(Object)}
     */
    Expression toTerm(Object value);

    /** 
     * Converts an algebra value to its symbolic string representation. 
     * @param value a value from this algebra; must satisfy {@link #isValue(Object)}
     */
    String getSymbol(Object value);

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
