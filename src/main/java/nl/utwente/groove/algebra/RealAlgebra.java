/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.algebra;

import nl.utwente.groove.util.Exceptions;

/** Abstract superclass of all real-number algebras.
 * @param <INT> The representation type of the integer algebra
 * @param <REAL> The representation type of the real algebra
 * @param <BOOL> The representation type of the boolean algebra
 * @param <STRING> The representation type of the string algebra
 */
public abstract sealed class RealAlgebra<INT,REAL,BOOL,STRING>
    extends RealSignature<INT,REAL,BOOL,STRING> implements Algebra<REAL>
    permits BigRealAlgebra, JavaRealAlgebra, PointRealAlgebra, TermRealAlgebra {
    /*
     * Specialises the return type.
     * @throws IllegalArgumentException if the parameter is not of type {@link Double}
     */
    @Override
    public final REAL toValueFromJava(Object value) {
        if (!(value instanceof Double)) {
            throw Exceptions
                .illegalArg("Native double type is %s, not %s", Double.class.getSimpleName(),
                            value.getClass().getSimpleName());
        }
        return toValueFromJavaDouble((Double) value);
    }

    /**
     * Callback method to convert from the native ({@link Double})
     * representation to the algebra representation.
     */
    protected abstract REAL toValueFromJavaDouble(Double value);
}
