/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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

/** Abstract superclass of all boolean algebras.
 * <Bool> Representation type for boolean values
 */
public abstract class BoolAlgebra<BOOL> extends BoolSignature<BOOL> implements Algebra<BOOL> {
    @Override
    @SuppressWarnings("unchecked")
    public BOOL toValue(Expression term) {
        return (BOOL) getFamily().toValue(term);
    }

    /*
     * Specialises the return type.
     * @throws IllegalArgumentException if the parameter is not of type {@link Boolean}
     */
    @Override
    public final BOOL toValueFromJava(Object value) {
        if (!(value instanceof Boolean)) {
            throw new IllegalArgumentException(java.lang.String.format(
                "Native boolean type is %s, not %s", Boolean.class.getSimpleName(), value.getClass()
                    .getSimpleName()));
        }
        return toValueFromJavaBoolean((Boolean) value);
    }

    /**
     * Callback method to convert from the native ({@link Boolean})
     * representation to the algebra representation.
     */
    protected abstract BOOL toValueFromJavaBoolean(Boolean value);

    /* Specialises the return type. */
    @Override
    public abstract Boolean toJavaValue(Object value);
}
