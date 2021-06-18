/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.algebra;

import groove.algebra.syntax.Expression;

/** Abstract superclass of all string algebras.
 * @param <INT> The representation type of the integer algebra
 * @param <REAL> The representation type of the reals algebra
 * @param <BOOL> The representation type of the boolean algebra
 * @param <STRING> The representation type of the string algebra
 */
public abstract class StringAlgebra<INT,REAL,BOOL,STRING>
    extends StringSignature<INT,REAL,BOOL,STRING> implements Algebra<STRING> {
    @Override
    @SuppressWarnings("unchecked")
    public STRING toValue(Expression term) {
        return (STRING) getFamily().toValue(term);
    }

    /*
     * Specialises the return type.
     * @throws IllegalArgumentException if the parameter is not of type {@link java.lang.String}
     */
    @Override
    public final STRING toValueFromJava(Object value) {
        if (!(value instanceof java.lang.String)) {
            throw new IllegalArgumentException(
                java.lang.String.format("Native int type is %s, not %s",
                    java.lang.String.class.getSimpleName(),
                    value.getClass()
                        .getSimpleName()));
        }
        return toValueFromJavaString((java.lang.String) value);
    }

    /**
     * Callback method to convert from the native ({@link java.lang.String})
     * representation to the algebra representation.
     */
    protected abstract STRING toValueFromJavaString(java.lang.String value);

    /* Specialises the return type. */
    @Override
    public abstract java.lang.String toJavaValue(Object value);
}
