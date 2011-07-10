package groove.algebra;

import groove.annotation.InfixSymbol;
import groove.annotation.PrefixSymbol;
import groove.util.Groove;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

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
    @SuppressWarnings("unchecked")
    Operator(Method method) throws IllegalArgumentException {
        Type[] methodParameterTypes = method.getGenericParameterTypes();
        this.signature =
            Algebras.getSigKind((Class<? extends Signature>) method.getDeclaringClass());
        this.arity = methodParameterTypes.length;
        this.name = method.getName();
        this.parameterTypes = new ArrayList<SignatureKind>();
        for (int i = 0; i < this.arity; i++) {
            if (!(methodParameterTypes[i] instanceof TypeVariable<?>)) {
                throw new IllegalArgumentException(String.format(
                    "Method '%s' should only have generic parameter types",
                    method.getName()));
            }
            String typeName =
                ((TypeVariable<?>) methodParameterTypes[i]).getName();
            this.parameterTypes.add(SignatureKind.getKind(typeName.toLowerCase()));
        }
        Type returnType = method.getGenericReturnType();
        if (!(returnType instanceof TypeVariable<?>)) {
            throw new IllegalArgumentException(
                String.format("Method '%s' should have generic return type",
                    method.getName()));
        }
        String typeName = ((TypeVariable<?>) returnType).getName();
        this.returnType = SignatureKind.getKind(typeName.toLowerCase());
        InfixSymbol infix = method.getAnnotation(InfixSymbol.class);
        PrefixSymbol prefix = method.getAnnotation(PrefixSymbol.class);
        this.symbol =
            infix == null ? (prefix == null ? null : prefix.symbol())
                    : infix.symbol();
        this.precedence =
            infix == null ? (prefix == null ? null : Precedence.UNARY)
                    : infix.precedence();
    }

    /** Returns the signature to which this operator belongs. */
    public SignatureKind getSignature() {
        return this.signature;
    }

    /** Returns the name of the operator. */
    public String getName() {
        return this.name;
    }

    /** Returns the number of parameters of this operator. */
    public int getArity() {
        return this.arity;
    }

    /** 
     * Returns the parameter type names of this operator.
     * The type names are actually the names of the defining signatures. 
     */
    public List<SignatureKind> getParamTypes() {
        return this.parameterTypes;
    }

    /** 
     * Returns the result type name of this operator.
     * The type name is actually the name of the defining signature.
     */
    public SignatureKind getResultType() {
        return this.returnType;
    }

    /** Returns the name of the operator, preceded with its type prefix. */
    public String getTypedName() {
        return this.signature + ":" + this.name;
    }

    /** Returns the infix symbol of this operator, or {@code null} if it has none. */
    public String getSymbol() {
        return this.symbol;
    }

    /** Returns the priority of this operator. */
    public Precedence getPrecedence() {
        return this.precedence;
    }

    @Override
    public String toString() {
        return getTypedName()
            + Groove.toString(this.parameterTypes.toArray(), "(", ")", ",");
    }

    private final SignatureKind signature;
    private final int arity;
    private final List<SignatureKind> parameterTypes;
    private final SignatureKind returnType;
    private final String name;
    private final String symbol;
    private final Precedence precedence;
}