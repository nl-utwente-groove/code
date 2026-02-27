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

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.syntax.CallExpr;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.algebra.syntax.Expression.Kind;
import nl.utwente.groove.algebra.syntax.Variable;
import nl.utwente.groove.util.DocumentedEnum;
import nl.utwente.groove.util.Exceptions;

/**
 * Register for the currently used algebras.
 * @author Arend Rensink
 * @version $Revision$
 */
public enum AlgebraFamily implements DocumentedEnum {
    /** Default algebra family:
     * {@link Integer} for {@code int},
     * {@link Boolean} for {@code bool},
     * {@link String} for {@code string},
     * {@link Double} for {@code real},
     */
    DEFAULT("default",
        "Java-based values (<tt>int</tt>, <tt>boolean</tt>, <tt>String</tt>, <tt>double</tt>)",
        JavaIntAlgebra.instance, JavaBoolAlgebra.instance, JavaStringAlgebra.instance,
        JavaRealAlgebra.instance, UserAlgebra.instance),
    /** Point algebra family: every sort has a single value. */
    POINT("point", "A single value for every type (so all values are equal)",
        PointIntAlgebra.instance, PointBoolAlgebra.instance, PointStringAlgebra.instance,
        PointRealAlgebra.instance),
    /** High-precision algebra family:
     * {@link BigInteger} for {@code int},
     * {@link Boolean} for {@code bool},
     * {@link String} for {@code string},
     * {@link BigDecimal} for {@code real},
     */
    BIG("big",
        "High-precision values (<tt>BigInteger</tt>, <tt>boolean</tt>, <tt>String</tt>, <tt>BigDecimal</tt>)",
        BigIntAlgebra.instance, BigBoolAlgebra.instance, BigStringAlgebra.instance,
        BigRealAlgebra.instance),
    /** Term algebra: symbolic representations for all values. */
    TERM("term", "Symbolic term representations", TermIntAlgebra.instance, TermBoolAlgebra.instance,
        TermStringAlgebra.instance, TermRealAlgebra.instance, UserAlgebra.instance);

    /**
     * Constructs a new register, loaded with a given set of algebras.
     * @throws IllegalArgumentException if there is an algebra for which there
     *         is no known signature, or more than one algebra for the same
     *         signature
     * @throws IllegalStateException if there are signatures without algebras
     */
    private AlgebraFamily(String name, String explanation,
                          Algebra<?>... algebras) throws IllegalArgumentException,
                                                  IllegalStateException {
        this.name = name;
        this.explanation = explanation;
        for (Algebra<?> algebra : algebras) {
            setImplementation(algebra);
        }
        checkCompleteness();
    }

    /**
     * Adds an algebra to the register. The algebra must implement an already
     * known signature.
     * @param algebra the algebra to be added
     */
    private void setImplementation(Algebra<?> algebra) {
        Sort sort = algebra.getSort();
        Algebra<?> oldAlgebra = this.algebraMap.put(sort, algebra);
        if (oldAlgebra != null) {
            throw Exceptions
                .illegalArg("Signature '%s' already implemented by '%s'", sort,
                            oldAlgebra.getName());
        }
    }

    /**
     * Checks for the completeness of the register.
     * @throws IllegalStateException if there is an implementation missing for
     *         some signature.
     */
    private void checkCompleteness() throws IllegalStateException {
        for (Sort sort : Sort.values()) {
            if (sort == Sort.USER && this != DEFAULT) {
                continue;
            }
            if (!this.algebraMap.containsKey(sort)) {
                throw Exceptions.illegalState("Implementation of signature '%s' is missing", sort);
            }
        }
    }

    /** Returns the name of this algebra family. */
    @Override
    public final String getName() {
        return this.name;
    }

    /** The algebra family name. */
    private final String name;

    /** Returns a one-line explanation of this algebra family. */
    @Override
    public String getExplanation() {
        return this.explanation;
    }

    private final String explanation;

    /**
     * Returns the algebra class registered for a given named signature, if any.
     */
    public Algebra<?> getAlgebra(Sort sigKind) {
        return this.algebraMap.get(sigKind);
    }

    /** A map from signature kinds to algebras registered for that name. */
    private final Map<Sort,Algebra<?>> algebraMap = new EnumMap<>(Sort.class);

    /** Indicates if this algebra family can assign definite values to variables. */
    public boolean supportsSymbolic() {
        return this == POINT;
    }

    /**
     * Returns the value for a given expression, not containing {@link Kind#FIELD} sub-expressions,
     * using a valuation for the free variables. Throws an {@link ErrorValue} if the value cannot
     * be computed.
     * @param expr the expression to be evaluated
     * @param valuation mapping from free variables in {@code expr} to
     * their corresponding values (assumed to be elements of this algebra
     * family). May be {@code null}, in which case either this should be
     * the {@link #POINT} algebra family or {@code term} should be cloased.
     * @return the value {@code term} (in the appropriate algebra)
     * @throws ErrorValue if the computation involves any operation that cannot be applied
     */
    public Object compute(Expression expr,
                          @Nullable Function<Variable,Object> valuation) throws ErrorValue {
        if (this == POINT) {
            return ((PointAlgebra<?>) getAlgebra(expr.getSort())).getPointValue();
        }
        switch (expr.getKind()) {
        case CONST:
            return toValue((Constant) expr);
        case VAR:
            assert valuation != null;
            return valuation.apply((Variable) expr);
        case CALL:
            CallExpr call = (CallExpr) expr;
            List<Object> args = new ArrayList<>();
            for (Expression arg : call.getArgs()) {
                args.add(computeStrict(arg, valuation));
            }
            return getOperation(call.getOperator()).apply(args);
        default:
            throw Exceptions.illegalArg("Field expression %s cannot be evaluated", expr);
        }
    }

    /**
     * Returns the value for a given expression, not containing {@link Kind#FIELD} sub-expressions,
     * using a valuation for the free variables.
     * @param expr the expression to be evaluated
     * @param valuation mapping from free variables in {@code expr} to
     * their corresponding values (assumed to be elements of this algebra
     * family). May be {@code null}, in which case either this should be
     * the {@link #POINT} algebra family or {@code term} should be cloased.
     * @return the value {@code term} (in the appropriate algebra); may be an {@link ErrorValue}.
     */
    public Object computeStrict(Expression expr, @Nullable Function<Variable,Object> valuation) {
        try {
            return compute(expr, valuation);
        } catch (ErrorValue error) {
            return error;
        }
    }

    /**
     * Returns the value for a given constant.
     * @return the value of {@code term} (in the appropriate algebra)
     */
    public Object toValue(Constant constant) {
        if (constant.isError()) {
            return ErrorValue.instance(constant.getSort());
        } else {
            return getAlgebra(constant.getSort()).toValueFromConstant(constant);
        }
    }

    /**
     * Returns the value for a given term, not containing {@link Kind#FIELD} sub-expressions,
     * returning an error value if the term cannot be evaluated.
     * Either the term has to be closed or this should be the {@link #POINT} algebra family.
     * @return the value of {@code term} (in the appropriate algebra)
     * @see #toValidValue(Expression)
     */
    public Object toValue(Expression term) {
        try {
            return toValidValue(term);
        } catch (ErrorValue error) {
            return error;
        }
    }

    /**
     * Returns the value for a given term, not containing {@link Kind#FIELD} sub-expressions,
     * throwing an exception if the term cannot be evaluated.
     * Either the term has to be closed or this should be the {@link #POINT} algebra family.
     * @return the value of {@code term} (in the appropriate algebra)
     * @throws ErrorValue if the computation involves any operation that cannot be applied
     * @see #toValue(Expression)
     */
    public Object toValidValue(Expression term) throws ErrorValue {
        return compute(term, null);
    }

    /**
     * Returns the method associated with a certain operator.
     */
    public Operation getOperation(Operator operator) {
        return getOpsMap().get(operator.getDeclaringSort()).get().get(operator);
    }

    /**
     * Returns a mapping from sorts to operation maps.
     */
    private Map<Sort,Supplier<Map<Operator,Operation>>> getOpsMap() {
        var result = this.opsMap;
        if (result.isEmpty()) {
            for (Sort sort : Sort.values()) {
                Supplier<Map<Operator,Operation>> ops = switch (sort) {
                case USER -> nl.utwente.groove.util.Factory.lazy(this::computeUserOperations);
                default -> nl.utwente.groove.util.Factory.value(computeSystemOperations(sort));
                };
                result.put(sort, ops);
            }
        }
        return result;
    }

    /**
     * Returns a mapping from operator names to operations for a given system sort.
     */
    private Map<Operator,Operation> computeSystemOperations(Sort sort) {
        Map<Operator,Operation> result = new HashMap<>();
        var ops = new HashMap<>(sort.getOperatorMap());
        // now create an operation for all those declared methods
        // including those from superclasses
        Class<?> myClass = getAlgebra(sort).getClass();
        while (!ops.isEmpty()) {
            for (Method method : myClass.getDeclaredMethods()) {
                var name = method.getName();
                var op = ops.remove(name);
                if (op != null) {
                    result.put(op, new SystemOperation(sort, op, method));
                }
            }
            myClass = myClass.getSuperclass();
        }
        return result;
    }

    /**
     * Returns a mapping from operation names to user operations.
     */
    private Map<Operator,Operation> computeUserOperations() {
        Map<Operator,Operation> result = new HashMap<>();
        for (var exec : UserSignature.getMethods().entrySet()) {
            var op = exec.getKey();
            var operation = new UserOperation(op, exec.getValue());
            result.put(op, operation);
        }
        return result;
    }

    /** Store of operations created from the algebras. */
    private final Map<Sort,Supplier<Map<Operator,Operation>>> opsMap = new EnumMap<>(Sort.class);

    @Override
    public String toString() {
        return this.algebraMap.toString();
    }

    /** Returns the algebra register with the family of default algebras. */
    public static AlgebraFamily getInstance() {
        return DEFAULT;
    }

    /**
     * Returns the algebra register with a given name.
     */
    public static AlgebraFamily getInstance(String instanceName) {
        return familyMap.get(instanceName);
    }

    /** Mapping from names to algebra families. */
    private static Map<String,AlgebraFamily> familyMap = new HashMap<>();
    static {
        for (AlgebraFamily family : values()) {
            familyMap.put(family.getName(), family);
        }
    }

    /** Implementation of a system-provided operator. */
    private class SystemOperation extends Operation {
        SystemOperation(Sort declaringSort, Operator op, Method method) {
            super(AlgebraFamily.this, op);
            this.method = method;
        }

        /** Invokes the executable in this operation with the given list of argument. */
        @Override
        protected Object invoke(List<Object> args) throws ReflectiveOperationException {
            var m = this.method;
            if (getOperator().isVarArgs() && !(args.size() == 1 && args.get(0) instanceof List)) {
                return m.invoke(getDeclaringAlgebra(), args);
            } else {
                var argsArray = args.toArray();
                return m.invoke(getDeclaringAlgebra(), argsArray);
            }
        }

        /** The method on which this operation is based. */
        private final Method method;
    }

    /** Implementation of a user-defined operator. */
    private class UserOperation extends Operation {
        UserOperation(Operator op, Executable executable) {
            super(AlgebraFamily.this, op);
            this.executable = executable;
        }

        /** Invokes the executable in this operation with the given list of argument. */
        @Override
        protected Object invoke(List<Object> args) throws ReflectiveOperationException {
            var argsArray = args.toArray();
            switch (this.executable) {
            case Method m:
                if (Modifier.isStatic(m.getModifiers())) {
                    return m.invoke(getDeclaringAlgebra(), argsArray);
                } else {
                    var self = argsArray[0];
                    var others = Arrays.copyOfRange(argsArray, 1, argsArray.length);
                    return m.invoke(self, others);
                }
            case Constructor<?> c:
                return c.newInstance(argsArray);
            }
        }

        private final Executable executable;
    }
}
