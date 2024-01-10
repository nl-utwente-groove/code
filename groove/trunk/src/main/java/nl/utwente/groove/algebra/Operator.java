package nl.utwente.groove.algebra;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Signature.OpValue;
import nl.utwente.groove.algebra.syntax.CallExpr;
import nl.utwente.groove.algebra.syntax.ExprTreeParser;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.annotation.OpSymbol;
import nl.utwente.groove.annotation.ToolTipHeader;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.parse.OpKind;

/**
 * Class encoding an operator declaration in a {@link Signature}.
 */
public class Operator {
    /**
     * Constructs an operator from a given method.
     * It is assumed that the method has only generic type variables as
     * parameter and result types, and that for each such type variable <code>Xxx</code>
     * there is a corresponding signature <code>XxxSignature</code>.
     * @param method the method to be converted into an operator
     * @throws IllegalArgumentException if the method parameter or return types
     * are not type variables.
     */
    @SuppressWarnings("null")
    private Operator(Sort sort, OpValue opValue, Method method) throws IllegalArgumentException {
        Type[] methodParameterTypes = method.getGenericParameterTypes();
        this.sort = sort;
        this.opValue = opValue;
        this.arity = methodParameterTypes.length;
        this.setOperator = this.arity == 1 && methodParameterTypes[0] instanceof ParameterizedType;
        this.supportsZero = opValue.isSupportsZero();
        this.name = method.getName();
        this.parameterTypes = new ArrayList<>();
        for (int i = 0; i < this.arity; i++) {
            Type type = methodParameterTypes[i];
            if (this.setOperator) {
                if (((ParameterizedType) type).getRawType() != List.class) {
                    throw Exceptions
                        .illegalArg("Method '%s' does not represent collection operator");
                }
                type = ((ParameterizedType) type).getActualTypeArguments()[0];
            }
            this.parameterTypes.add(toSort(type));
        }
        this.returnType = toSort(method.getGenericReturnType());
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
            ? OpKind.ATOM
            : op.kind();
        this.description = superMethod.getAnnotation(ToolTipHeader.class).value();
    }

    /** Converts a reflected type into a GROOVE sort. */
    private Sort toSort(Type type) throws IllegalArgumentException {
        if (!(type instanceof TypeVariable)) {
            throw Exceptions.illegalArg("Type '%s' should be generic", type);
        }
        String typeName = ((TypeVariable<?>) type).getName();
        Sort result = Sort.getSort(typeName.toLowerCase());
        if (result == null) {
            throw Exceptions.illegalArg("Type '%s' is not an existing sort", typeName);
        }
        return result;
    }

    /** Returns the sort to which this operator belongs. */
    public Sort getSort() {
        return this.sort;
    }

    private final Sort sort;

    /** Returns the enumerated operator value of the operator. */
    public OpValue getOpValue() {
        return this.opValue;
    }

    private final OpValue opValue;

    /** Returns the name of the operator. */
    public String getName() {
        return this.name;
    }

    private final String name;

    /** Indicates if this is a collection-based operator. */
    public boolean isSetOperator() {
        return this.setOperator;
    }

    private final boolean setOperator;

    /** Indicates if this collection operator supports zero arguments. */
    public boolean isSupportsZero() {
        return this.supportsZero;
    }

    private final boolean supportsZero;

    /** Returns the number of parameters of this operator.
     * For a collection-based operator, the arity is 1.
     */
    public int getArity() {
        return this.arity;
    }

    private final int arity;

    /**
     * Returns the parameter types of this operator.
     */
    public List<Sort> getParamTypes() {
        return this.parameterTypes;
    }

    private final List<Sort> parameterTypes;

    /**
     * Returns the result type of this operator.
     */
    public Sort getResultType() {
        return this.returnType;
    }

    private final Sort returnType;

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
        return getSymbol().equals(ExprTreeParser.EQUALS_SYMBOL);
    }

    /**
     * Returns the description in the {@link ToolTipHeader} annotation of the method.
     */
    public String getDescription() {
        return this.description;
    }

    private final String description;

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
                if (op != this && op.getArity() == getArity()) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /** Returns the name of the operator, preceded with its containing signature. */
    public String getFullName() {
        return getSort() + ":" + getName();
    }

    @Override
    public String toString() {
        return getFullName() + Groove.toString(this.parameterTypes.toArray(), "(", ")", ",");
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
        if (ops.isEmpty()) {
            for (Sort sort : Sort.values()) {
                for (OpValue opValue : sort.getOpValues()) {
                    ops.add(opValue.getOperator());
                }
            }
        }
        return ops;
    }

    /** List of all operators of all sorts. */
    private static final List<Operator> ops = new ArrayList<>();

    /** Returns the operators for a given (prefix or infix) operator symbol or name. */
    public static List<Operator> getOps(String symbol) {
        return getOpsMap().get(symbol);
    }

    /** Returns a map from operator symbols and names to operators with that symbol/name. */
    public static Map<String,List<Operator>> getOpsMap() {
        if (opLookupMap.isEmpty()) {
            // register all operators
            for (Operator op : getOps()) {
                registerOp(op);
            }
        }
        return opLookupMap;
    }

    /** Returns, for a given sort and name, the (unique) operators defined
     * for that sort with that symbol/name; or {@code null} if there is no
     * such operator. */
    public static @Nullable Operator getOp(Sort sort, String name) {
        if (sortOpLookupMap.isEmpty()) {
            getOps().forEach(Operator::registerOp);
        }
        return sortOpLookupMap.get(sort).get(name);
    }

    /** Adds an operator to the store, both by symbol and by name. */
    private static void registerOp(Operator op) {
        String symbol = op.getSymbol();
        var sortOps = sortOpLookupMap.get(op.getSort());
        if (sortOps == null) {
            sortOps = new HashMap<>();
            sortOpLookupMap.put(op.getSort(), sortOps);
        }
        if (symbol != null) {
            List<Operator> ops = opLookupMap.get(symbol);
            if (ops == null) {
                ops = new ArrayList<>();
                opLookupMap.put(symbol, ops);
            }
            ops.add(op);
            sortOps.put(symbol, op);
        }
        String opName = op.getName();
        List<Operator> ops = opLookupMap.get(opName);
        if (ops == null) {
            ops = new ArrayList<>();
            opLookupMap.put(opName, ops);
        }
        ops.add(op);
        sortOps.put(opName, op);
    }

    /** Mapping from operator names and symbols to lists of operators with that symbol. */
    private static final Map<String,List<Operator>> opLookupMap = new HashMap<>();
    /** Mapping from sorts plus operator names and symbols to the (optional) operator
     * with that symbol defined in that sort. */
    private static final Map<Sort,Map<String,Operator>> sortOpLookupMap = new HashMap<>();
}