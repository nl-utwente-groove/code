package groove.algebra;

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
    Operator(Method method) throws IllegalArgumentException {
        Type[] methodParameterTypes = method.getGenericParameterTypes();
        this.arity = methodParameterTypes.length;
        this.name = method.getName();
        this.parameterTypes = new ArrayList<String>();
        for (int i = 0; i < this.arity; i++) {
            if (!(methodParameterTypes[i] instanceof TypeVariable)) {
                throw new IllegalArgumentException(String.format(
                    "Method '%s' should only have generic parameter types",
                    method.getName()));
            }
            String typeName =
                ((TypeVariable<?>) methodParameterTypes[i]).getName();
            this.parameterTypes.add(typeName.toLowerCase());
        }
        Type returnType = method.getGenericReturnType();
        if (!(returnType instanceof TypeVariable)) {
            throw new IllegalArgumentException(String.format(
                "Method '%s' should have generic return type",
                method.getName()));
        }
        String typeName = ((TypeVariable<?>) returnType).getName();
        this.returnType = typeName.toLowerCase();
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
    public List<String> getParameterTypes() {
        return this.parameterTypes;
    }

    /** 
     * Returns the result type name of this operator.
     * The type name is actually the name of the defining signature.
     */
    public String getResultType() {
        return this.returnType;
    }
    
    private final int arity;
    private final List<String> parameterTypes;
    private final String returnType;
    private final String name;
}