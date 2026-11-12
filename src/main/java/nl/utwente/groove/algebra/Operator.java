package nl.utwente.groove.algebra;

import static nl.utwente.groove.util.Factory.lazy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Signature.OpValue;
import nl.utwente.groove.algebra.syntax.CallExpr;
import nl.utwente.groove.algebra.syntax.ExprTreeParser;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.annotation.BoolSeq;
import nl.utwente.groove.annotation.IntSeq;
import nl.utwente.groove.annotation.OpSymbol;
import nl.utwente.groove.annotation.RealSeq;
import nl.utwente.groove.annotation.Seq;
import nl.utwente.groove.annotation.StringSeq;
import nl.utwente.groove.annotation.ToolTipHeader;
import nl.utwente.groove.annotation.UserOperation;
import nl.utwente.groove.annotation.UserSeq;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.Strings;
import nl.utwente.groove.util.parse.OpKind;

/**
 * Class encoding an operator declaration in a {@link Signature}.
 */
public class Operator implements Comparable<Operator> {
    /** Constructs an operator based on a user-defined method. */
    Operator(Executable executable) {
        Type[] parTypes = executable.getParameterTypes();
        this.declaringSort = Sort.USER;
        this.inverse = false;
        this.varArgs = false;
        this.zeroArgs = false;
        this.constructor = executable instanceof Constructor;
        this.name = switch (executable) {
        case Method m -> m.getName();
        case Constructor<?> c -> c.getDeclaringClass().getSimpleName();
        };
        this.parameterSorts = new ArrayList<>();
        if (!this.constructor && !Modifier.isStatic(executable.getModifiers())) {
            this.parameterSorts.add(Sort.USER);
        }
        var tailType = getTailType(executable);
        this.tail = tailType != null;
        for (int i = 0; i < parTypes.length; i++) {
            this.parameterSorts
                .add(toParSort(tailType != null && i == parTypes.length - 1
                    ? tailType
                    : parTypes[i]));
        }
        this.arity = this.parameterSorts.size();
        Class<?> returnType = switch (executable) {
        case Method m -> m.getReturnType();
        case Constructor<?> c -> c.getDeclaringClass();
        };
        if (Seq.class.isAssignableFrom(returnType)) {
            throw Exceptions
                .illegalArg("Return type %s is a sequence type, which is only allowed for the last parameter",
                            returnType.getSimpleName());
        }
        if (executable instanceof Method && Collection.class.isAssignableFrom(returnType)) {
            throw Exceptions
                .illegalArg("Return type %s is a collection: a user operation yields a single value",
                            returnType.getSimpleName());
        }
        this.setValued = false;
        this.returnSort = toReturnSort(returnType);
        this.symbol = null;
        this.kind = OpKind.CALL;
        this.description = "User-defined method '" + this.name + "'";

        var annotation = executable.getAnnotation(UserOperation.class);
        this.indeterminate = annotation == null
            ? false
            : annotation.indeterminate();
    }

    /**
     * Returns the type of the elements of the variable argument tail of a user-defined
     * constructor or method, or {@code null} if it has no such tail.
     * There is a tail if the last parameter is of one of the sequence types
     * (see {@link Seq}): the operator then takes any number of arguments in that position,
     * which are passed on as a single sequence.
     * For the sequence types of the system sorts, the type returned is the
     * corresponding Java type (for instance {@code int} for {@link IntSeq});
     * for a {@link UserSeq} it is the type argument.
     * @throws IllegalArgumentException if a parameter other than the last is of a sequence type,
     * or if the last parameter is a sequence type without a known element type
     * @see #hasTail()
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    static @Nullable Class<?> getTailType(Executable executable) throws IllegalArgumentException {
        Class<?>[] parTypes = executable.getParameterTypes();
        int last = parTypes.length - 1;
        for (int i = 0; i < last; i++) {
            if (Seq.class.isAssignableFrom(parTypes[i])) {
                throw Exceptions
                    .illegalArg("Sequence type %s is only allowed for the last parameter",
                                parTypes[i].getSimpleName());
            }
        }
        if (last < 0) {
            return null;
        }
        Class<?> lastType = parTypes[last];
        if (lastType == IntSeq.class) {
            return int.class;
        } else if (lastType == RealSeq.class) {
            return double.class;
        } else if (lastType == BoolSeq.class) {
            return boolean.class;
        } else if (lastType == StringSeq.class) {
            return String.class;
        } else if (lastType == UserSeq.class) {
            // the generic types can only fall short of the parameters for constructors
            // of inner classes, which do not occur here
            Type[] genTypes = executable.getGenericParameterTypes();
            if (genTypes.length == parTypes.length
                && genTypes[last] instanceof ParameterizedType parType
                && parType.getActualTypeArguments()[0] instanceof Class<?> argType) {
                return argType;
            }
            throw Exceptions
                .illegalArg("Sequence type %s does not specify the type of its elements",
                            lastType.getSimpleName());
        } else if (Seq.class.isAssignableFrom(lastType)) {
            throw Exceptions
                .illegalArg("Sequence type %s has no known element type",
                            lastType.getSimpleName());
        } else {
            return null;
        }
    }

    /**
     * Constructs an operator from a given {@link Signature} method.
     * It is assumed that the method has only generic type variables as
     * parameter and result types, and that for each such type variable <code>Xxx</code>
     * there is a corresponding signature <code>XxxSignature</code>.
     * @param method the method to be converted into an operator
     * @throws IllegalArgumentException if the method parameter or return types
     * are not type variables.
     */
    @SuppressWarnings("null")
    private Operator(Sort declaringSort, OpValue opValue,
                     Method method) throws IllegalArgumentException {
        Type[] methodParameterTypes = method.getGenericParameterTypes();
        this.declaringSort = declaringSort;
        this.inverse = opValue == IntSignature.Op.NEG || opValue == RealSignature.Op.NEG;
        this.arity = methodParameterTypes.length;
        this.varArgs = this.arity == 1 && methodParameterTypes[0] instanceof ParameterizedType;
        this.zeroArgs = opValue.isZeroArgs();
        this.name = method.getName();
        this.parameterSorts = new ArrayList<>();
        for (int i = 0; i < this.arity; i++) {
            Type type = methodParameterTypes[i];
            if (this.varArgs) {
                if (((ParameterizedType) type).getRawType() != List.class) {
                    throw Exceptions
                        .illegalArg("Method '%s' does not represent collection operator");
                }
                type = ((ParameterizedType) type).getActualTypeArguments()[0];
            }
            this.parameterSorts.add(toParSort(type));
        }
        Type returnType = method.getGenericReturnType();
        this.setValued = returnType instanceof ParameterizedType;
        if (returnType instanceof ParameterizedType parType) {
            if (parType.getRawType() != Set.class) {
                throw Exceptions
                    .illegalArg("Method '%s' does not represent set-valued operator", this.name);
            }
            if (this.varArgs) {
                throw Exceptions
                    .illegalArg("Collection operator '%s' cannot be set-valued", this.name);
            }
            returnType = parType.getActualTypeArguments()[0];
        }
        this.returnSort = toReturnSort(returnType);
        // look up the corresponding method declaration in GSignature, to find the annotations
        Method superMethod;
        try {
            superMethod = GSignature.class.getMethod(this.name, method.getParameterTypes());
        } catch (NoSuchMethodException exc) {
            throw Exceptions
                .illegalState("Method %s does not override annotated operation", this.name);
        }
        OpSymbol op = superMethod.getAnnotation(OpSymbol.class);
        this.symbol = op == null
            ? null
            : op.symbol();
        this.kind = op == null
            ? OpKind.CALL
            : op.kind();
        this.description = superMethod.getAnnotation(ToolTipHeader.class).value();
        this.indeterminate = false;
        this.constructor = false;
        this.tail = false;
    }

    /** Converts a reflected parameter type into a GROOVE sort. */
    private Sort toParSort(Type type) throws IllegalArgumentException {
        return toSort(type, "Parameter");
    }

    /** Converts a reflected return type into a GROOVE sort. */
    private Sort toReturnSort(Type type) throws IllegalArgumentException {
        return toSort(type, "Return");
    }

    /** Converts a reflected type into a GROOVE sort. */
    private Sort toSort(Type type, String kind) throws IllegalArgumentException {
        Sort result;
        if (type instanceof TypeVariable) {
            String typeName = ((TypeVariable<?>) type).getName();
            result = Sort.toSort(typeName.toLowerCase());
            if (result == null) {
                throw Exceptions.illegalArg("%s type '%s' is not an existing sort", kind, typeName);
            }
        } else {
            result = Sort.toSort(type);
            if (result == null) {
                throw Exceptions
                    .illegalArg("%s type %s cannot be converted to GROOVE sort", kind,
                                type.getTypeName());
            }
        }
        return result;
    }

    /** Returns the sort to which this operator belongs. */
    public Sort getDeclaringSort() {
        return this.declaringSort;
    }

    private final Sort declaringSort;

    /** Indicates if this operator is a numeric inverse,
     * which can alternatively be regarded as part of a constant.
     */
    public boolean isInverse() {
        return this.inverse;
    }

    /** Flag indicating if this is a numeric inverse operator. */
    private final boolean inverse;

    /** Returns the name of the operator. */
    public String getName() {
        return this.name;
    }

    private final String name;

    /** Indicates if this is a collection-based operator, i.e.,
     * with a variable number of arguments
     */
    public boolean isVarArgs() {
        return this.varArgs;
    }

    private final boolean varArgs;

    /** Indicates if this collection operator supports zero arguments. */
    public boolean isZeroArgs() {
        return this.zeroArgs;
    }

    private final boolean zeroArgs;

    /** Indicates if this operator has a variable argument tail: it then takes
     * any number of arguments (including none) in the position of its last parameter,
     * all of the sort of that parameter, in addition to one argument for each
     * of its other parameters.
     * This is the case for the constructor of a user type whose last component is a
     * sequence, and for a user operation whose last parameter is one.
     * Unlike a collection-based operator (see {@link #isVarArgs()}),
     * the tail arguments are explicitly listed in the call.
     * @see #getParamSort(int)
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    public boolean hasTail() {
        return this.tail;
    }

    private final boolean tail;

    /** Indicates if this is a set-valued operator, i.e., one that yields
     * a set of values of the result sort rather than a single one.
     * A set-valued operator is matched once for every value in the set.
     * The order of the values has no meaning, but the set has to iterate over them
     * in an order that is the same in every run, as exploration would otherwise
     * not be deterministic.
     * @see #getResultSort()
     */
    public boolean isSetValued() {
        return this.setValued;
    }

    private final boolean setValued;

    /** If {@code true}, the outcome of this operation is not fully determined by its parameters.
     * This is true for, e.g., random number generation.
     */
    public boolean isIndeterminate() {
        return this.indeterminate;
    }

    private final boolean indeterminate;

    /** Returns the number of parameters of this operator.
     * For a collection-based operator, the arity is 1.
     * For an operator with a variable argument tail, the tail counts as one parameter.
     */
    public int getArity() {
        return this.arity;
    }

    private final int arity;

    /** Tests if this operator supports a given argument count. */
    public boolean allowsArgCount(int argCount) {
        if (isVarArgs()) {
            return argCount > 0 || isZeroArgs();
        } else if (hasTail()) {
            return argCount >= getArity() - 1;
        } else {
            return argCount == getArity();
        }
    }

    /**
     * Returns the parameter sorts of this operator.
     * For an operator with a variable argument tail, the last of these
     * is the sort of each of the tail arguments.
     * @see #getParamSort(int)
     */
    public List<Sort> getParamSorts() {
        return this.parameterSorts;
    }

    /**
     * Returns the sort that this operator expects for the argument at a given position.
     * This differs from the corresponding element of {@link #getParamSorts()}
     * for an operator with a variable argument tail, where every
     * position from the last parameter onwards has the sort of that parameter.
     * @param index the position of the argument, counting from 0
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    public Sort getParamSort(int index) {
        return this.parameterSorts
            .get(hasTail()
                ? Math.min(index, getArity() - 1)
                : index);
    }

    /**
     * Returns the sorts that this operator expects for a given number of arguments.
     * This equals {@link #getParamSorts()} except for an operator with a variable
     * argument tail that allows the given number of arguments: the sort of the last parameter
     * is then left out or repeated as often as needed.
     * @param argCount the number of arguments
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    public List<Sort> getParamSorts(int argCount) {
        if (!hasTail() || !allowsArgCount(argCount)) {
            return this.parameterSorts;
        }
        var result = new ArrayList<Sort>(argCount);
        for (int i = 0; i < argCount; i++) {
            result.add(getParamSort(i));
        }
        return result;
    }

    private final List<Sort> parameterSorts;

    /**
     * Returns the result type of this operator.
     * For a set-valued operator, this is the sort of the individual values.
     * @see #isSetValued()
     */
    public Sort getResultSort() {
        return this.returnSort;
    }

    private final Sort returnSort;

    /** Indicates if this operator has a (non-<code>null</code>) symbol. */
    public boolean hasSymbol() {
        return getSymbol() != null;
    }

    /** Returns the in- or prefix symbol of this operator, or {@code null} if it has none. */
    public String getSymbol() {
        return this.symbol;
    }

    private final String symbol;

    /** Returns the kind of this operator. */
    public OpKind getKind() {
        return this.kind;
    }

    private final OpKind kind;

    /** Tests if this is an equality operator (i.e., a direct comparison). */
    public boolean isEquality() {
        return hasSymbol() && getSymbol().equals(ExprTreeParser.EQUALS_SYMBOL);
    }

    /**
     * Returns the description in the {@link ToolTipHeader} annotation of the method.
     */
    public String getDescription() {
        return this.description;
    }

    private final String description;

    /**
     * Indicates if this operator is the constructor of a user type.
     */
    public boolean isConstructor() {
        return this.constructor;
    }

    /** Flag indicating if this operator is the constructor of a user type. */
    private final boolean constructor;

    /**
     * Indicates if there are more operators with the same name
     * and parameter count.
     */
    public boolean isAmbiguous() {
        List<Operator> ops = getOps(getName());
        boolean result = ops.size() > 1;
        if (result) {
            // there are more operators with the same name
            result = false;
            for (Operator op : ops) {
                if (op != this && op.getArity() == getArity() && op.isVarArgs() == isVarArgs()) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /** Returns the name of the operator, preceded with its containing signature. */
    public String getFullName() {
        return getDeclaringSort() + ":" + getName();
    }

    @Override
    public String toString() {
        return getFullName() + Strings
            .toString(this.parameterSorts.toArray(), "(", hasTail()
                ? "...)"
                : ")", ",");
    }

    @Override
    public int compareTo(Operator o) {
        return getFullName().compareTo(o.getFullName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.declaringSort.ordinal(), this.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Operator other)) {
            return false;
        }
        return this.declaringSort == other.declaringSort && Objects.equals(this.name, other.name);
    }

    /**
     * Constructs and returns a new composite term consisting of this
     * operator applied to a sequence of arguments.
     */
    public CallExpr newTerm(Expression... args) {
        return new CallExpr(this, args);
    }

    /**
     * Returns the method from a given signature class with a given name.
     * This method is supposed to implement an operator, and should therefore be
     * declared exactly once, as a public abstract method.
     */
    private static Method getOperatorMethod(Class<?> sigClass, java.lang.String name) {
        Method result = null;
        java.lang.String className = sigClass.getSimpleName();
        java.lang.String sigName
            = className.substring(0, className.indexOf("Signature")).toLowerCase();
        Method[] methods = sigClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(name)) {
                if (result != null) {
                    throw Exceptions
                        .illegalArg("Operator overloading for '%s:%s' not allowed", sigName, name);
                }
                result = method;
            }
        }
        if (result == null) {
            throw Exceptions.illegalArg("No method found for operator '%s:%s'", sigName, name);
        }
        if (!Modifier.isAbstract(result.getModifiers())) {
            throw Exceptions
                .illegalArg("Method for operator '%s:%s' should be abstract", sigName, name);
        }
        if (!Modifier.isPublic(result.getModifiers())) {
            throw Exceptions
                .illegalArg("Method for operator '%s:%s' should be public", sigName, name);
        }
        return result;
    }

    /** Computes the name of an (all-caps) enum-value and converts it to camel case. */
    private static String getOperatorName(OpValue enumValue) {
        StringBuilder result = new StringBuilder();
        result.append(enumValue.name().toLowerCase());
        // delete underscores and set next char as uppercase
        int i = 0;
        while (i < result.length()) {
            char c = result.charAt(i);
            if (c == '_') {
                result.delete(i, i + 1);
                result.setCharAt(i, Character.toUpperCase(result.charAt(i)));
            } else {
                i = i + 1;
            }
        }
        return result.toString();
    }

    /** Creates the operator for a given signature and operator value. */
    static Operator newInstance(Sort sigKind, OpValue opValue) {
        String opName = getOperatorName(opValue);
        Method opMethod = getOperatorMethod(opValue.getClass().getEnclosingClass(), opName);
        return new Operator(sigKind, opValue, opMethod);
    }

    /** Returns the list of all operators of all sorts. */
    public static List<Operator> getOps() {
        return ops.get();
    }

    /** Lazyly computed list of all operators of all sorts. */
    private static final Factory<List<Operator>> ops = lazy(Operator::computeOps);

    /** Computes the value of {@link #ops}. */
    private static List<Operator> computeOps() {
        List<Operator> result = new ArrayList<>();
        if (result.isEmpty()) {
            for (Sort sort : Sort.values()) {
                for (OpValue opValue : sort.getOpValues()) {
                    result.add(opValue.getOperator());
                }
            }
            result.addAll(UserSignature.getOperators().values());
        }
        return result;
    }

    /** Returns the operators for a given (prefix or infix) operator symbol or name. */
    public static List<Operator> getOps(String symbol) {
        return getOpsMap().get(symbol);
    }

    /** Returns a map from operator symbols and names to operators with that symbol/name. */
    public static Map<String,List<Operator>> getOpsMap() {
        return opLookupMap.get();
    }

    /** Mapping from operator names and symbols to lists of operators with that symbol. */
    private static final Supplier<Map<String,List<Operator>>> opLookupMap
        = lazy(Operator::computeOpLookupMap);

    /** Adds an operator to the store, both by symbol and by name. */
    private static Map<String,List<Operator>> computeOpLookupMap() {
        Map<String,List<Operator>> result = new HashMap<>();
        for (Operator op : getOps()) {
            String symbol = op.getSymbol();
            if (symbol != null) {
                List<Operator> ops = result.get(symbol);
                if (ops == null) {
                    ops = new ArrayList<>();
                    result.put(symbol, ops);
                }
                ops.add(op);
            }
            String opName = op.getName();
            List<Operator> ops = result.get(opName);
            if (ops == null) {
                ops = new ArrayList<>();
                result.put(opName, ops);
            }
            ops.add(op);
        }
        return result;
    }

    /** Returns, for a given sort and name, the (unique) operators defined
     * for that sort with that symbol/name; or {@code null} if there is no
     * such operator. */
    public static @Nullable Operator getOp(Sort sort, String name) {
        var sortOps = sortOpLookupMap.get().get(sort);
        assert sortOps != null; // all sorts are pre-populated
        return sortOps.get(name);
    }

    /** Mapping from sorts plus operator names and symbols to the (optional) operator
     * with that symbol defined in that sort. */
    private static final Supplier<Map<Sort,Map<String,Operator>>> sortOpLookupMap
        = lazy(Operator::computeSortOpLookupMap);

    /** Adds an operator to the store, both by symbol and by name. */
    private static Map<Sort,Map<String,Operator>> computeSortOpLookupMap() {
        Map<Sort,Map<String,Operator>> result = new HashMap<>();
        // pre-populate all sorts, so lookup does not fail for sorts without
        // operators (in particular USER when no user class is loaded)
        for (Sort sort : Sort.values()) {
            result.put(sort, new HashMap<>());
        }
        for (Operator op : getOps()) {
            var sortOps = result.get(op.getDeclaringSort());
            if (sortOps == null) {
                sortOps = new HashMap<>();
                result.put(op.getDeclaringSort(), sortOps);
            }
            String opName = op.getName();
            sortOps.put(opName, op);
            String symbol = op.getSymbol();
            if (symbol != null) {
                sortOps.put(symbol, op);
            }
        }
        return result;
    }
}