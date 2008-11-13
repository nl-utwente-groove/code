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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    /** Constructs a new register, initially loaded with all known signatures. */
    public AlgebraRegister(Set<NewAlgebra<?>> algebras) {
        for (NewAlgebra<?> algebra: algebras) {
            addSignature(getSignature(algebra.getClass()));
            setImplementation(algebra);
        }
    }

    /**
     * Adds a signature class to the register.
     * @throws IllegalArgumentException if the signature class does not have
     *         accessible fields called {@link #NAME_FIELD} or
     *         {@link #DEFAULT_FIELD}.
     */
    private void addSignature(Class<? extends Signature> signature)
        throws IllegalArgumentException {
        String name = getName(signature);
        if (this.signatureMap.put(name, signature) != null) {
            throw new IllegalArgumentException(String.format(
                "Signature named %s already registered", name));
        }
    }

    /**
     * Adds an algebra to the register.
     * The algebra must implement an already known signature.
     * @param algebra the algebra to be added
     */
    private void setImplementation(NewAlgebra<?> algebra) {
        String signatureName = getName(getSignature(algebra.getClass()));
        NewAlgebra<?> oldAlgebra = this.algebraMap.put(signatureName, algebra);
        if (oldAlgebra != null) {
            throw new IllegalArgumentException(String.format(
                "Signature '%s' already implemented by '%s'", signatureName,
                oldAlgebra.getName()));
        }
        try {
            Class<?> carrierType = algebra.getClass().getMethod("getValue").getReturnType();
            oldAlgebra = this.carrierToAlgebraMap.put(carrierType, algebra);
            if (oldAlgebra != null) {
                throw new IllegalArgumentException(String.format(
                    "Carrier type '%s' already used in algebra '%s'", carrierType.getName(),
                    oldAlgebra.getName()));
            }
        } catch (SecurityException e) {
            throw new IllegalArgumentException();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException();
        }
    }
    
    /** Returns the signature class registered for a given name, if any. */
    public Class<? extends Signature> getSignature(String signatureName) {
        return this.signatureMap.get(signatureName);
    }
    
    /** Returns the algebra class registered for a given named signature, if any. */
    public NewAlgebra<?> getImplementation(String signatureName) {
        return this.algebraMap.get(signatureName);
    }

    /** Returns an unmodifiable map from names to registered signature classes. */
    public Map<String,Class<? extends Signature>> getSignatureMap() {
        return Collections.unmodifiableMap(this.signatureMap);
    }
//
//    public boolean isFixed() {
//        return this.fixed;
//    }
//
//    /** 
//     * Fixing the register means taking the default implementation
//     * of all signatures that have been registered but for which no algebra
//     * was explicitly provided. 
//     */
//    public void setFixed() throws FormatException {
//        for (Map.Entry<String,Class<? extends Signature>> signatureEntry: this.signatureMap.entrySet()) {
//            Class<? extends Signature> algebraClass = getAlgebra(signatureEntry.getKey());
//            if (algebraClass == null) {
//                algebraClass = getDefault(signatureEntry.getValue());
//            }
//            Signature algebra = getInstance(algebraClass);
//        }
//        this.fixed = true;
//    }
//
//    /**
//     * Constructs an instance of a given algebra class.
//     * If the algebra class implements {@link Fixable}, it is assumed that it has a 
//     * constructor with type {@link AlgebraRegister}, to which this register is passed in.
//     * If not, it is assumed to have an empty constructor, which is invoked.
//     * @throws IllegalArgumentException if the algebra class has no constructor of the appropriate type.
//     */
//    private Signature getInstance(Class<? extends Signature> algebra) {
//        try {
//            Signature result;
//            if (Fixable.class.isAssignableFrom(algebra)) {
//                Constructor<? extends Signature> constructor =
//                    algebra.getConstructor(AlgebraRegister.class);
//                result = constructor.newInstance(this);
//            } else {
//                result = algebra.newInstance();
//            }
//            return result;
//        } catch (SecurityException e) {
//            throw new IllegalArgumentException(e);
//        } catch (NoSuchMethodException e) {
//            throw new IllegalArgumentException(e);
//        } catch (InstantiationException e) {
//            throw new IllegalArgumentException(e);
//        } catch (IllegalAccessException e) {
//            throw new IllegalArgumentException(e);
//        } catch (InvocationTargetException e) {
//            throw new IllegalArgumentException(e);
//        }
//    }
//
//    public void testFixed(boolean fixed) {
//        if (this.fixed != fixed) {
//            throw new IllegalStateException();
//        }
//    }

    /** 
     * Returns the constant value associated with a certain signature name and constant symbol.
     * @throws IllegalArgumentException if the signature name does not exist,
     * or the constant symbol does not denote a value of this signature. 
     */
    public Object getConstant(String signatureName, String constant) throws IllegalArgumentException {
        NewAlgebra<?> algebra = getImplementation(signatureName);
        if (algebra == null) {
            throw new IllegalArgumentException(String.format(
                "No algebra registered for signature '%s'", signatureName));
        }
        Object result = algebra.getValue(constant);
        if (result == null) {
            throw new IllegalArgumentException(String.format(
                "Constant '%s' does not occur in algebra '%s'", constant,
                algebra.getName()));
        }
        return result;
    }

    /** 
     * Returns the method associated with a certain signature name and operation name.
     * @throws IllegalArgumentException if the signature name does not exist,
     * or the operation name does not occur in the signature.
     */
    public NewAlgebra.Operation getMethod(String signatureName, String operation) throws IllegalArgumentException {
        NewAlgebra<?> algebra = getImplementation(signatureName);
        if (algebra == null) {
            throw new IllegalArgumentException(String.format(
                "No algebra registered for signature '%s'", signatureName));
        }
        Map<String,NewAlgebra.Operation> operations = this.operationsMap.get(algebra);
        if (operations == null) {
            operations = createOperationsMap(algebra);
            this.operationsMap.put(algebra,operations);
        }
        NewAlgebra.Operation result = operations.get(operation);
        if (result == null) {
            throw new IllegalArgumentException(String.format(
                "Operation '%s' does not occur in signature '%s'", operation,
                signatureName));
        }
        return result;
    }
    
    /** Returns a mapping from operation names to operations for a given algebra. */
    private Map<String,NewAlgebra.Operation> createOperationsMap(NewAlgebra<?> algebra) {
        Map<String,NewAlgebra.Operation> result = new HashMap<String,NewAlgebra.Operation>();
        // first find out what methods were declared in the signature
        Set<String> methodNames = new HashSet<String>();
        Method[] signatureMethods = getSignature(algebra.getClass()).getDeclaredMethods();
        for (Method method: signatureMethods) {
            methodNames.add(method.getName());
        }
        // now create an operation for all those declared methods
        Method[] methods = algebra.getClass().getMethods();
        for (Method method: methods) {
            if (methodNames.contains(method.getName())) {
                result.put(method.getName(), createOperation(algebra, method));
            }
        }
        return result;
    }

    /** Returns a new algebra operation object for the given method (from a given algebra). */
    private NewAlgebra.Operation createOperation(NewAlgebra<?> algebra, Method method) {
        return new Operation(this, algebra, method);
    }

    /** The map of registered signatures. */
    private final Map<String,Class<? extends Signature>> signatureMap =
        new TreeMap<String,Class<? extends Signature>>();
    /** A map from signature names to algebras registered for that name. */
    private final Map<String,NewAlgebra<?>> algebraMap = new TreeMap<String,NewAlgebra<?>>();
    /** A map from carrier set types to the defining algebras. */
    private final Map<Class<?>,NewAlgebra<?>> carrierToAlgebraMap = new HashMap<Class<?>,NewAlgebra<?>>();
    /** Store of operations created from the algebras. */
    private final Map<NewAlgebra<?>,Map<String,NewAlgebra.Operation>> operationsMap =
        new HashMap<NewAlgebra<?>,Map<String,NewAlgebra.Operation>>();

    //    /** Flag to indicate that the register has been fixed. */
//    private boolean fixed;
    /**
     * Looks up the name of a signature or algebra class. The name should be available as
     * the value of a field of the signature named {@link #NAME_FIELD}.
     * @throws IllegalArgumentException if there is no accessible field named
     *         {@link #NAME_FIELD}.
     */
    static public String getName(Class<? extends Signature> signature)
        throws IllegalArgumentException {
        try {
            return (String) signature.getField(NAME_FIELD).get(null);
        } catch (SecurityException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }
    }
//
//    /**
//     * Looks up the default implementation of a signature class. The name should
//     * be available as the value of a field of the signature named
//     * {@link #DEFAULT_FIELD}.
//     * @throws IllegalArgumentException if there is no accessible field named
//     *         {@link #DEFAULT_FIELD}.
//     */
//    static public Class<? extends Signature> getDefault(Class<? extends Signature> signature)
//        throws IllegalArgumentException {
//        try {
//            @SuppressWarnings("unchecked")
//            Class<? extends Signature> result = (Class<? extends Signature>) signature.getField(DEFAULT_FIELD).get(null);
//            if (result == null) {
//                throw new IllegalArgumentException();
//            }
//            return result;
//        } catch (SecurityException e) {
//            throw new IllegalArgumentException(e);
//        } catch (IllegalAccessException e) {
//            throw new IllegalArgumentException(e);
//        } catch (NoSuchFieldException e) {
//            throw new IllegalArgumentException(e);
//        }
//    }

    /** Returns the signature implemented by a given algebra class. */
    @SuppressWarnings("unchecked")
    static public Class<Signature> getSignature(Class<? extends NewAlgebra> algebra) {
        if (algebra.isInterface()) {
            throw new IllegalArgumentException(String.format("Algebra %s should be a concrete class", algebra.getName()));
        }
        // find the implemented signature
        Class<?> signature = algebra;
        while (!signature.isInterface()) {
            signature = signature.getSuperclass();
        }
        if (signature.equals(Signature.class)) {
            throw new IllegalArgumentException(String.format("Algebra %s should implement a sub-interface of %s", algebra.getName(), Signature.class.getName()));
        }
        return (Class<Signature>) signature;
    }
    
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
    
    /** Name of the default algebra family. */
    static public final String DEFAULT_ALGEBRAS = "default";
    /** Name of the point algebra family. */
    static public final String POINT_ALGEBRAS = "point";
    /** The default algebra register. */
    static private final AlgebraRegister defaultRegister;
    /** Default algebra register. */
    static private final Map<String,AlgebraRegister> registerMap;
    static {
        Set<NewAlgebra<?>> defaultAlgebraFamily =  new HashSet<NewAlgebra<?>>();
        defaultAlgebraFamily.add(new JavaIntAlgebra());
        defaultRegister = new AlgebraRegister(defaultAlgebraFamily);
        registerMap = new HashMap<String,AlgebraRegister>();
        registerMap.put(DEFAULT_ALGEBRAS, defaultRegister);
    }

    /** The name of the field containing the signature name. */
    static private final String NAME_FIELD = "NAME";
    /** The name of the field containing the default implementation. */
    static private final String DEFAULT_FIELD = "DEFAULT";
    
    /** Implementation of an algebra operation. */
    private static class Operation implements NewAlgebra.Operation {
        Operation(AlgebraRegister register, NewAlgebra<?> algebra, Method method) {
            this.algebra = algebra;
            this.method = method;
            this.returnType = register.carrierToAlgebraMap.get(method.getReturnType());
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

        public NewAlgebra<?> getDefiningAlgebra() {
            return this.algebra;
        }

        public int getArity() {
            return this.method.getParameterTypes().length;
        }

        public NewAlgebra<?> getResultAlgebra() {
            return this.returnType;
        }

        public String getSymbol() {
            return this.method.getName();
        }
        
        private final NewAlgebra<?> algebra;
        private final NewAlgebra<?> returnType;
        private final Method method;
    }
}
