/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
package groove.algebra;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Register for the currently used algebras.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AlgebraRegister {
    /**
     * Constructs a new register, loaded with a given set of algebras.
     * @throws IllegalArgumentException if there is an algebra for which there
     *         is no known signature, or more than one algebra for the same
     *         signature
     * @throws IllegalStateException if there are signatures without algebras
     */
    private AlgebraRegister(Set<Algebra<?>> algebras)
        throws IllegalArgumentException, IllegalStateException {
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
        String signatureName = getSignatureName(algebra);
        if (!signatureMap.containsKey(signatureName)) {
            throw new IllegalArgumentException(String.format(
                "Algebra '%s' implements unknown signature '%s'",
                algebra.getName(), signatureName));
        }
        Algebra<?> oldAlgebra = this.algebraMap.put(signatureName, algebra);
        if (oldAlgebra != null) {
            throw new IllegalArgumentException(String.format(
                "Signature '%s' already implemented by '%s'", signatureName,
                oldAlgebra.getName()));
        }
    }

    /**
     * Checks for the completeness of the register.
     * @throws IllegalStateException if there is an implementation missing for
     *         some signature.
     */
    private void checkCompleteness() throws IllegalStateException {
        for (String signatureName : getSignatureNames()) {
            if (!this.algebraMap.containsKey(signatureName)) {
                throw new IllegalStateException(String.format(
                    "Implementation of signature '%s' is missing",
                    signatureName));
            }
        }
    }

    /**
     * Returns the algebra class registered for a given named signature, if any.
     */
    public Algebra<?> getImplementation(String signatureName) {
        return this.algebraMap.get(signatureName);
    }

    // /** Returns an unmodifiable map from names to registered signature
    // classes. */
    // public Map<String,Class<? extends Signature>> getSignatureMap() {
    // return Collections.unmodifiableMap(this.signatureMap);
    // }
    //
    // public boolean isFixed() {
    // return this.fixed;
    // }
    //
    // /**
    // * Fixing the register means taking the default implementation
    // * of all signatures that have been registered but for which no algebra
    // * was explicitly provided.
    // */
    // public void setFixed() throws FormatException {
    // for (Map.Entry<String,Class<? extends Signature>> signatureEntry:
    // this.signatureMap.entrySet()) {
    // Class<? extends Signature> algebraClass =
    // getAlgebra(signatureEntry.getKey());
    // if (algebraClass == null) {
    // algebraClass = getDefault(signatureEntry.getValue());
    // }
    // Signature algebra = getInstance(algebraClass);
    // }
    // this.fixed = true;
    // }
    //
    // /**
    // * Constructs an instance of a given algebra class.
    // * If the algebra class implements {@link Fixable}, it is assumed that it
    // has a
    // * constructor with type {@link AlgebraRegister}, to which this register
    // is passed in.
    // * If not, it is assumed to have an empty constructor, which is invoked.
    // * @throws IllegalArgumentException if the algebra class has no
    // constructor of the appropriate type.
    // */
    // private Signature getInstance(Class<? extends Signature> algebra) {
    // try {
    // Signature result;
    // if (Fixable.class.isAssignableFrom(algebra)) {
    // Constructor<? extends Signature> constructor =
    // algebra.getConstructor(AlgebraRegister.class);
    // result = constructor.newInstance(this);
    // } else {
    // result = algebra.newInstance();
    // }
    // return result;
    // } catch (SecurityException e) {
    // throw new IllegalArgumentException(e);
    // } catch (NoSuchMethodException e) {
    // throw new IllegalArgumentException(e);
    // } catch (InstantiationException e) {
    // throw new IllegalArgumentException(e);
    // } catch (IllegalAccessException e) {
    // throw new IllegalArgumentException(e);
    // } catch (InvocationTargetException e) {
    // throw new IllegalArgumentException(e);
    // }
    // }
    //
    // public void testFixed(boolean fixed) {
    // if (this.fixed != fixed) {
    // throw new IllegalStateException();
    // }
    // }

    /**
     * Returns the constant value associated with a certain signature name and
     * constant symbol.
     * @throws UnknownSymbolException if the signature name does not exist, or
     *         the constant symbol does not denote a value of this signature.
     */
    public Object getConstant(String signatureName, String constant)
        throws UnknownSymbolException {
        Algebra<?> algebra = getImplementation(signatureName);
        if (algebra == null) {
            throw new UnknownSymbolException(String.format(
                "No algebra registered for signature '%s'", signatureName));
        }
        if (algebra.isValue(constant)) {
            return algebra.getValue(constant);
        } else {
            throw new UnknownSymbolException(String.format(
                "Constant '%s' does not occur in algebra '%s'", constant,
                algebra.getName()));
        }
    }

    /**
     * Returns the method associated with a certain signature name and operation
     * name.
     * @throws UnknownSymbolException if the signature name does not exist, or
     *         the operation name does not occur in the signature.
     */
    public Operation getOperation(String signatureName, String operation)
        throws UnknownSymbolException {
        Algebra<?> algebra = getImplementation(signatureName);
        if (algebra == null) {
            throw new UnknownSymbolException(String.format(
                "No algebra registered for signature '%s'", signatureName));
        }
        Map<String,Operation> operations = this.operationsMap.get(algebra);
        if (operations == null) {
            operations = createOperationsMap(algebra);
            this.operationsMap.put(algebra, operations);
        }
        Operation result = operations.get(operation);
        if (result == null) {
            throw new UnknownSymbolException(String.format(
                "Operation '%s' does not occur in signature '%s'", operation,
                signatureName));
        }
        return result;
    }

    /**
     * Returns a mapping from operation names to operations for a given algebra.
     */
    private Map<String,Operation> createOperationsMap(Algebra<?> algebra) {
        Map<String,Operation> result = new HashMap<String,Operation>();
        // first find out what methods were declared in the signature
        Set<String> methodNames = new HashSet<String>();
        Method[] signatureMethods = getSignature(algebra).getDeclaredMethods();
        for (Method method : signatureMethods) {
            methodNames.add(method.getName());
        }
        // now create an operation for all those declared methods
        Method[] methods = algebra.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (methodNames.contains(method.getName())) {
                result.put(method.getName(), createOperation(algebra, method));
            }
        }
        return result;
    }

    /**
     * Returns a new algebra operation object for the given method (from a given
     * algebra).
     */
    private Operation createOperation(Algebra<?> algebra, Method method) {
        return new Operation(this, algebra, method);
    }

    /**
     * Since we have controlled creation of registers, two registers are equal
     * if and only if they are the same.
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    /** Returns the identity hash code for this object. */
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return this.algebraMap.toString();
    }

    /** A map from signature names to algebras registered for that name. */
    private final Map<String,Algebra<?>> algebraMap =
        new TreeMap<String,Algebra<?>>();
    /** Store of operations created from the algebras. */
    private final Map<Algebra<?>,Map<String,Operation>> operationsMap =
        new HashMap<Algebra<?>,Map<String,Operation>>();

    /** Returns the algebra register with the family of default algebras. */
    static public AlgebraRegister getInstance() {
        return defaultRegister;
    }

    /**
     * Returns the algebra register with a given name.
     */
    static public AlgebraRegister getInstance(String instanceName) {
        AlgebraRegister result = registerMap.get(instanceName);
        return result;
    }

    /** Returns the set of all known signature names. */
    static public Set<String> getSignatureNames() {
        return Collections.unmodifiableSet(signatureMap.keySet());
    }

    /** Returns the signature name for a given algebra. */
    static public String getSignatureName(Algebra<?> algebra) {
        return getName(getSignature(algebra));
    }

    /**
     * Returns the operator for a given signature name and operator name.
     * @throws UnknownSymbolException if the signature or operator does not
     *         exist
     */
    static public Operator getOperator(String signature, String operator)
        throws UnknownSymbolException {
        Map<String,Operator> operators = operatorsMap.get(signature);
        if (operators == null) {
            throw new UnknownSymbolException(String.format(
                "No such signature '%s'", signature));
        }
        Operator result = operators.get(operator);
        if (result == null) {
            throw new UnknownSymbolException(String.format(
                "No such operator '%s' in signature '%s'", operator, signature));
        }
        return result;
    }

    /**
     * Tests if a string represents a constant in a given (named) signature.
     * @throws UnknownSymbolException if the signature does not exist
     */
    static public boolean isConstant(String signatureName, String constant)
        throws UnknownSymbolException {
        Class<? extends Signature> signature = signatureMap.get(signatureName);
        if (signature == null) {
            throw new UnknownSymbolException(String.format(
                "No such signature '%s'", signature));
        }
        Method isValueMethod = getIsValueMethod(signature);
        try {
            return (Boolean) isValueMethod.invoke(
                getInstance().getImplementation(signatureName), constant);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException();
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Adds a signature class to the register.
     * @throws IllegalArgumentException if a signature with the same name is
     *         already registered.
     */
    static private void addSignature(Class<? extends Signature> signature)
        throws IllegalArgumentException {
        String signatureName = getName(signature);
        int sigModifiers = signature.getModifiers();
        if (Modifier.isInterface(sigModifiers)
            || !Modifier.isAbstract(sigModifiers)) {
            throw new IllegalArgumentException(String.format(
                "Signature '%s' should be an abstract class", signature));
        }
        // test if the class has a public final method isValue
        Method isValueMethod = getIsValueMethod(signature);
        if (isValueMethod == null) {
            throw new IllegalArgumentException(String.format(
                "Signature '%s' should implement '%s'", signature,
                IS_VALUE_NAME));
        }
        int methodModifiers = isValueMethod.getModifiers();
        if (Modifier.isAbstract(methodModifiers)
            || !Modifier.isFinal(methodModifiers)) {
            throw new IllegalArgumentException(String.format(
                "Method '%s' in signature '%s' should be final",
                isValueMethod.getName(), signature));
        }
        if (signatureMap.put(signatureName, signature) != null) {
            throw new IllegalArgumentException(String.format(
                "Signature named %s already registered", signatureName));
        }
    }

    private static Method getIsValueMethod(Class<? extends Signature> signature) {
        for (Method method : signature.getMethods()) {
            if (method.getName().equals(IS_VALUE_NAME)) {
                Class<?>[] parTypes = method.getParameterTypes();
                if (parTypes.length == 1 && parTypes[0].equals(String.class)) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * Looks up the name of a signature or algebra class. The name is taken to
     * be the first part of the interface name, after which only
     * {@link #SIGNATURE_SUFFIX} follows.
     */
    static private String getName(Class<? extends Signature> signature)
        throws IllegalArgumentException {
        String interfaceName = signature.getName();
        // take off qualification
        if (interfaceName.indexOf('.') >= 0) {
            interfaceName =
                interfaceName.substring(interfaceName.lastIndexOf('.') + 1);
        }
        if (!interfaceName.endsWith(SIGNATURE_SUFFIX)) {
            throw new IllegalArgumentException(String.format(
                "Signature name '%s' should by convention end on \"%s\"",
                interfaceName, SIGNATURE_SUFFIX));
        }
        String result =
            interfaceName.substring(0, interfaceName.length()
                - SIGNATURE_SUFFIX.length());
        return result.toLowerCase();
    }

    /** Returns the signature implemented by a given algebra. */
    @SuppressWarnings("unchecked")
    static private Class<Signature> getSignature(Algebra algebra) {
        Class<? extends Algebra> algebraClass = algebra.getClass();
        if (algebraClass.isInterface()) {
            throw new IllegalArgumentException(String.format(
                "Algebra %s should be a concrete class", algebra.getName()));
        }
        // find the implemented signature
        Class<?> signature = algebraClass.getSuperclass();
        // for (int i = 0; signature == null && i < interfaces.length; i++) {
        if (!Signature.class.isAssignableFrom(signature)) {
            throw new IllegalArgumentException(String.format(
                "Algebra '%s' is not a subclass of '%s'", algebra.getName(),
                Signature.class));
        }
        int algebraModifiers = algebraClass.getModifiers();
        if (Modifier.isInterface(algebraModifiers)
            || Modifier.isAbstract(algebraModifiers)) {
            throw new IllegalArgumentException(String.format(
                "Signature '%s' is not an abstract class",
                signature.getClass(), Signature.class));
        }
        return (Class<Signature>) signature;
    }

    static private void checkSignatureConsistency() {
        for (Class<? extends Signature> signature : signatureMap.values()) {
            for (TypeVariable<?> type : signature.getTypeParameters()) {
                String typeName = type.getName().toLowerCase();
                if (!signatureMap.containsKey(typeName)) {
                    throw new IllegalArgumentException(String.format(
                        "Type '%s' not declared by any signature", typeName));
                }
            }
        }
    }

    static private Map<String,Map<String,Operator>> createOperatorsMap() {
        Map<String,Map<String,Operator>> result =
            new HashMap<String,Map<String,Operator>>();
        for (Map.Entry<String,Class<? extends Signature>> signatureEntry : signatureMap.entrySet()) {
            Map<String,Operator> operators = new HashMap<String,Operator>();
            Method[] methods = signatureEntry.getValue().getDeclaredMethods();
            for (Method method : methods) {
                if (Modifier.isAbstract(method.getModifiers())) {
                    Operator oldOperator =
                        operators.put(method.getName(), new Operator(method));
                    if (oldOperator != null) {
                        throw new IllegalArgumentException(
                            String.format(
                                "Operator overloading for '%s' (signature '%s') not allowed",
                                method.getName(), signatureEntry.getKey()));
                    }
                }
            }
            result.put(signatureEntry.getKey(), operators);
        }
        return result;
    }

    /** Name of the default (Java) algebra family. */
    static public final String DEFAULT_ALGEBRAS = "default";
    /** Name of the point algebra family. */
    static public final String POINT_ALGEBRAS = "point";
    /** Name of the point algebra family. */
    static public final String BIG_ALGEBRAS = "big";
    /**
     * The map of registered signatures.
     */
    static private final Map<String,Class<? extends Signature>> signatureMap =
        new TreeMap<String,Class<? extends Signature>>();
    /** Map from signature and method names to operators. */
    static private final Map<String,Map<String,Operator>> operatorsMap;
    static {
        addSignature(BoolSignature.class);
        addSignature(IntSignature.class);
        addSignature(RealSignature.class);
        addSignature(StringSignature.class);
        checkSignatureConsistency();
        operatorsMap = createOperatorsMap();
    }

    /** The default algebra register. */
    static private final AlgebraRegister defaultRegister;
    /** The point algebra register. */
    static private final AlgebraRegister pointRegister;
    /** The big algebra register. */
    static private final AlgebraRegister bigRegister;
    /** Default algebra register. */
    static private final Map<String,AlgebraRegister> registerMap;
    static {
        registerMap = new HashMap<String,AlgebraRegister>();
        Set<Algebra<?>> defaultAlgebraFamily = new HashSet<Algebra<?>>();
        defaultAlgebraFamily.add(new JavaIntAlgebra());
        defaultAlgebraFamily.add(new BoolAlgebra());
        defaultAlgebraFamily.add(new StringAlgebra());
        defaultAlgebraFamily.add(new JavaDoubleAlgebra());
        defaultRegister = new AlgebraRegister(defaultAlgebraFamily);
        registerMap.put(DEFAULT_ALGEBRAS, defaultRegister);
        Set<Algebra<?>> pointAlgebraFamily = new HashSet<Algebra<?>>();
        pointAlgebraFamily.add(new IntPointAlgebra());
        pointAlgebraFamily.add(new BoolPointAlgebra());
        pointAlgebraFamily.add(new StringPointAlgebra());
        pointAlgebraFamily.add(new RealPointAlgebra());
        pointRegister = new AlgebraRegister(pointAlgebraFamily);
        registerMap.put(POINT_ALGEBRAS, pointRegister);
        Set<Algebra<?>> bigAlgebraFamily = new HashSet<Algebra<?>>();
        bigAlgebraFamily.add(new IntPointAlgebra());
        bigAlgebraFamily.add(new BoolPointAlgebra());
        bigAlgebraFamily.add(new StringPointAlgebra());
        bigAlgebraFamily.add(new RealPointAlgebra());
        bigRegister = new AlgebraRegister(bigAlgebraFamily);
        registerMap.put(BIG_ALGEBRAS, bigRegister);
    }

    /** Required last part of the interface name of signatures. */
    static private final String SIGNATURE_SUFFIX = "Signature";
    /** Name of the {@link Signature#isValue(String)} method */
    static private final String IS_VALUE_NAME = "isValue";

    /** Implementation of an algebra operation. */
    private static class Operation implements groove.algebra.Operation {
        Operation(AlgebraRegister register, Algebra<?> algebra, Method method) {
            this.algebra = algebra;
            this.method = method;
            String returnTypeName =
                operatorsMap.get(getSignatureName(algebra)).get(
                    method.getName()).getResultType();
            this.returnType = register.getImplementation(returnTypeName);
        }

        public Object apply(List<Object> args) throws IllegalArgumentException {
            try {
                return this.method.invoke(this.algebra, args.toArray());
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException();
            } catch (InvocationTargetException e) {
                throw new IllegalArgumentException();
            }
        }

        public Algebra<?> getAlgebra() {
            return this.algebra;
        }

        public int getArity() {
            return this.method.getParameterTypes().length;
        }

        public Algebra<?> getResultAlgebra() {
            return this.returnType;
        }

        public String getSymbol() {
            return this.method.getName();
        }

        private final Algebra<?> algebra;
        private final Algebra<?> returnType;
        private final Method method;
    }
}
