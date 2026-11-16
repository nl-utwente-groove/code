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
import java.util.ArrayList;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.syntax.CallExpr;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.annotation.Seq;
import nl.utwente.groove.annotation.UserDisplay;
import nl.utwente.groove.annotation.UserType;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.parse.StringHandler;

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
        assert op != null; // every user type has a constructor operator of the same name
        var rc = claz.getRecordComponents();
        assert rc != null : "User type '%s' is not a record".formatted(claz);
        var args = new ArrayList<Expression>();
        for (int i = 0; i < rc.length; i++) {
            try {
                var field = rc[i].getAccessor().invoke(value);
                var algebra = getFamily().getAlgebra(op.getParamSorts().get(i));
                if (field instanceof Seq<?> seq) {
                    // the elements of a sequence are the tail of the constructor arguments
                    for (int j = 0; j < seq.size(); j++) {
                        args.add(algebra.toTerm(seq.value(j)));
                    }
                } else {
                    args.add(algebra.toTerm(field));
                }
            } catch (IllegalAccessException | InvocationTargetException exc) {
                throw Exceptions.unreachable();
            }
        }
        return new CallExpr(op, args.toArray(new Expression[0]));
    }

    @Override
    public String toValidSymbol(Object value) {
        StringBuffer result = new StringBuffer();
        Class<?> claz = value.getClass();
        assert claz.getAnnotation(UserType.class) != null : "Object type '%s' is not user-defined"
            .formatted(claz);
        result.append(claz.getSimpleName());
        result.append('(');
        var rc = claz.getRecordComponents();
        assert rc != null : "User type '%s' is not a record".formatted(claz);
        // the arguments of the constructor call; a sequence contributes one for each element
        var args = new ArrayList<>();
        for (var c : rc) {
            try {
                var field = c.getAccessor().invoke(value);
                if (field instanceof Seq<?> seq) {
                    for (int j = 0; j < seq.size(); j++) {
                        args.add(seq.value(j));
                    }
                } else {
                    args.add(field);
                }
            } catch (IllegalAccessException | InvocationTargetException exc) {
                throw Exceptions.unreachable();
            }
        }
        boolean first = true;
        for (var arg : args) {
            if (first) {
                first = false;
            } else {
                result.append(",");
            }
            if (arg instanceof String string) {
                result.append(StringHandler.toQuoted(string, StringHandler.DOUBLE_QUOTE));
            } else {
                result.append(arg);
            }
        }
        result.append(')');
        return result.toString();
    }

    /** Returns the custom display string of a given user value: the string that the
     * {@link UserDisplay}-annotated method of its type returns for the value.
     * The display string is for showing the value only; unlike the symbol
     * (see {@link #toValidSymbol(Object)}) it need not determine the value
     * and cannot be parsed back.
     * @param value a value from this algebra, or an {@link ErrorValue}
     * @return the display string; {@code null} if the type of the value has no display
     * method, or the method throws an exception or returns {@code null} or the empty string
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    public @Nullable String toDisplayString(Object value) {
        var method = getDisplayMethod(value.getClass());
        if (method == null) {
            return null;
        }
        try {
            // the display method is user code: anything can go wrong in it
            var result = (String) method.invoke(value);
            // an empty string would show no value at all
            return result == null || result.isEmpty()
                ? null
                : result;
        } catch (ReflectiveOperationException | RuntimeException exc) {
            return null;
        }
    }

    /** Returns the custom display string of the value of a given user constant.
     * @param constant a constant of sort {@link Sort#USER}
     * @return the display string; {@code null} if the constant has no valid value,
     * or its value has no display string
     * @see #toDisplayString(Object)
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    public @Nullable String toDisplayString(Constant constant) {
        assert constant.getSort() == Sort.USER;
        try {
            // the value is built by the record constructor, which is user code as well,
            // and the user classes may have been unloaded since the constant was created
            return toDisplayString(AlgebraFamily.DEFAULT.toValue(constant));
        } catch (RuntimeException exc) {
            return null;
        }
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
