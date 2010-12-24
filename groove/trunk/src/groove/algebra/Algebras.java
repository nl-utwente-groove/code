/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Helper class for algebra manipulation.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Algebras {

    /** Returns the set of all known signature names. */
    static public Set<String> getSigNames() {
        return Collections.unmodifiableSet(signatureMap.keySet());
    }

    /** Returns the signature name for a given algebra. */
    static public String getSigName(Algebra<?> algebra) {
        String result = getSigName(getSignature(algebra));
        if (!Algebras.signatureMap.containsKey(result)) {
            throw new IllegalArgumentException(String.format(
                "Algebra '%s' implements unknown signature '%s'",
                algebra.getName(), result));
        }
        return result;
    }

    /**
     * Returns the operator for a given signature name and operator name,
     * or {@code null} if the operator does not exist.
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
     * Returns the name of the signature defining a constant value with a given
     * string representation, if any.
     */
    static public String getSigNameFor(String value) {
        for (Class<? extends Signature> signature : signatureMap.values()) {
            if (isConstant(signature, value)) {
                return getSigName(signature);
            }
        }
        return null;
    }

    /**
     * Tests if a string represents a constant in a given (named) signature.
     * @throws UnknownSymbolException if the signature does not exist
     */
    static public boolean isConstant(String sigName, String value)
        throws UnknownSymbolException {
        Class<? extends Signature> signature = signatureMap.get(sigName);
        if (signature == null) {
            throw new UnknownSymbolException(String.format(
                "No such signature '%s'", signature));
        }
        return isConstant(signature, value);
    }

    /**
     * Tests if a string represents a constant in a given signature.
     */
    static private boolean isConstant(Class<? extends Signature> signature,
            String value) {
        Method isValueMethod = getIsValueMethod(signature);
        String signatureName = getSigName(signature);
        try {
            return (Boolean) isValueMethod.invoke(
                AlgebraFamily.getInstance().getAlgebra(signatureName),
                value);
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
        String signatureName = getSigName(signature);
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
    static String getSigName(Class<? extends Signature> signature)
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
    static Class<Signature> getSignature(Algebra<?> algebra) {
        @SuppressWarnings("rawtypes")
        Class<? extends Algebra> algebraClass = algebra.getClass();
        if (algebraClass.isInterface()) {
            throw new IllegalArgumentException(String.format(
                "Algebra %s should be a concrete class", algebra.getName()));
        }
        // find the implemented signature
        Class<?> signature = algebraClass.getSuperclass();
        if (!Signature.class.isAssignableFrom(signature)) {
            throw new IllegalArgumentException(String.format(
                "Algebra '%s' is not a subclass of '%s'", algebra.getName(),
                Signature.class));
        }
        int algebraModifiers = algebraClass.getModifiers();
        if (Modifier.isInterface(algebraModifiers)
            || Modifier.isAbstract(algebraModifiers)) {
            throw new IllegalArgumentException(
                String.format("Signature '%s' is not an abstract class",
                    signature.getClass()));
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

    /** Required last part of the interface name of signatures. */
    static private final String SIGNATURE_SUFFIX = "Signature";
    /** Name of the {@link Signature#isValue(String)} method */
    static private final String IS_VALUE_NAME = "isValue";
}
