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

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Serves as a register for all data signatures in use. This provides
 * extensibility; just make sure this class gets loaded before any of the
 * classes that use the register, such as (in particular)
 * {@link NewAttributeAspect}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class SignatureRegister {
    /**
     * Adds a signature class to the register.
     * @throws IllegalArgumentException if the signature class does not have
     *         accessible fields called {@link #NAME_FIELD} or
     *         {@link #DEFAULT_FIELD}.
     */
    static public void addSignature(Class<Signature> signature)
        throws IllegalArgumentException {
        String name = getName(signature);
        Signature defaultImplementation = getDefault(signature);
        if (signatureMap.put(name, signature) != null) {
            throw new IllegalArgumentException(String.format(
                "Signature named %s already registered", name));
        }
        defaultMap.put(name, defaultImplementation);
    }

    /**
     * Adds an algebra to the register.
     * The algebra must implement an already known signature.
     * @param algebra the algebra to be added
     */
    static public void addAlgebra(Class<? extends Signature> algebra) {
        // check that the algebra is a concrete implementation
        // of a sub-interface of Signature
        getSignature(algebra);
        String name = getName(algebra);
        Class<? extends Signature> oldAlgebra = algebraMap.put(name, algebra);
        if (oldAlgebra != null) {
            throw new IllegalArgumentException(String.format("Algebra %s already implemented by %s", name, oldAlgebra.getName()));
        }
    }
    
    /** Returns the signature implemented by a given algebra class. */
    @SuppressWarnings("unchecked")
    static public Class<Signature> getSignature(Class<? extends Signature> algebra) {
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
    
    /** Returns the signature class registered for a given name, if any. */
    static public Class<Signature> getSignature(String name) {
        return signatureMap.get(name);
    }
    
    /** Returns the algebra class registered for a given name, if any. */
    static public Class<? extends Signature> getAlgebra(String name) {
        return algebraMap.get(name);
    }

    /**
     * Returns the default signature implementation registered for a given name,
     * if any.
     */
    static public Signature getDefault(String name) {
        return defaultMap.get(name);
    }

    /** Returns an unmodifiable map from names to registered signature classes. */
    static public Map<String,Class<Signature>> getSignatureMap() {
        return Collections.unmodifiableMap(signatureMap);
    }

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

    /**
     * Looks up the default implementation of a signature class. The name should
     * be available as the value of a field of the signature named
     * {@link #DEFAULT_FIELD}.
     * @throws IllegalArgumentException if there is no accessible field named
     *         {@link #DEFAULT_FIELD}.
     */
    static public Signature getDefault(Class<Signature> signature)
        throws IllegalArgumentException {
        try {
            return (Signature) signature.getField(DEFAULT_FIELD).get(null);
        } catch (SecurityException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** The map of registered signatures. */
    static private final Map<String,Class<Signature>> signatureMap =
        new TreeMap<String,Class<Signature>>();
    /** The map of registered signatures. */
    static private final Map<String,Signature> defaultMap =
        new TreeMap<String,Signature>();
    /** The map of registered algebras. */
    static private final Map<String,Class<? extends Signature>> algebraMap = new TreeMap<String,Class<? extends Signature>>();
    /** The name of the field containing the signature name. */
    static private final String NAME_FIELD = "NAME";
    /** The name of the field containing the default implementation. */
    static private final String DEFAULT_FIELD = "DEFAULT";
}