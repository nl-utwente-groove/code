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
 * $Id: RealAlgebra.java 6353 2024-01-16 11:48:01Z rensink $
 */
package nl.utwente.groove.algebra;

import java.lang.reflect.InvocationTargetException;

import nl.utwente.groove.algebra.syntax.CallExpr;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.annotation.UserType;
import nl.utwente.groove.util.Exceptions;

/** User algebra.
 */
public final class UserAlgebra extends UserSignature implements Algebra<Object> {
    @Override
    public Sort getSort() {
        return Sort.USER;
    }

    @Override
    public boolean isValidValue(Object value) {
        return true;
    }

    @Override
    public Object toValueFromConstant(Constant constant) {
        return AlgebraFamily.DEFAULT.toValue(constant.getUserRepr());
    }

    @Override
    public Expression toValidTerm(Object value) {
        Class<?> claz = value.getClass();
        assert claz.getAnnotation(UserType.class) != null : "Object type '%s' is not user-defined"
            .formatted(claz);
        var op = UserSignature.getOperators().get(claz.getSimpleName());
        var rc = claz.getRecordComponents();
        var args = new Expression[rc.length];
        for (int i = 0; i < rc.length; i++) {
            try {
                var field = rc[i].getAccessor().invoke(value);
                args[i] = getFamily().getAlgebra(op.getParamSorts().get(i)).toTerm(field);
            } catch (IllegalAccessException | InvocationTargetException exc) {
                throw Exceptions.unreachable();
            }
        }
        return new CallExpr(op, args);
    }

    @Override
    public String toValidSymbol(Object value) {
        StringBuffer result = new StringBuffer();
        Class<?> claz = value.getClass();
        assert claz.getAnnotation(UserType.class) != null : "Object type '%s' is not user-defined"
            .formatted(claz);
        result.append(claz.getSimpleName());
        result.append('(');
        boolean first = true;
        for (var c : claz.getRecordComponents()) {
            if (first) {
                first = false;
            } else {
                result.append(",");
            }
            try {
                var field = c.getAccessor().invoke(value);
                if (c.getType() == String.class) {
                    result.append('"');
                    result.append(field);
                    result.append('"');
                } else {
                    result.append(field);
                }
            } catch (IllegalAccessException | InvocationTargetException exc) {
                throw Exceptions.unreachable();
            }
        }
        result.append(')');
        return result.toString();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public AlgebraFamily getFamily() {
        return AlgebraFamily.DEFAULT;
    }

    @Override
    public final Object toValueFromJava(Object value) {
        return value;
    }

    /** The name of this algebra. */
    public static final String NAME = "user";
    /** Singleton instance of this algebra. */
    public static final UserAlgebra instance = new UserAlgebra();
}
