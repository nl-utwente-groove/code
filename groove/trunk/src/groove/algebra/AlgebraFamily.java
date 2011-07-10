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
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Register for the currently used algebras.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AlgebraFamily {
    /**
     * Constructs a new register, loaded with a given set of algebras.
     * @throws IllegalArgumentException if there is an algebra for which there
     *         is no known signature, or more than one algebra for the same
     *         signature
     * @throws IllegalStateException if there are signatures without algebras
     */
    private AlgebraFamily(String name, Set<Algebra<?>> algebras)
        throws IllegalArgumentException, IllegalStateException {
        this.name = name;
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
        SignatureKind sigKind = algebra.getKind();
        Algebra<?> oldAlgebra = this.algebraMap.put(sigKind, algebra);
        if (oldAlgebra != null) {
            throw new IllegalArgumentException(String.format(
                "Signature '%s' already implemented by '%s'", sigKind,
                oldAlgebra.getName()));
        }
    }

    /**
     * Checks for the completeness of the register.
     * @throws IllegalStateException if there is an implementation missing for
     *         some signature.
     */
    private void checkCompleteness() throws IllegalStateException {
        for (SignatureKind sigKind : EnumSet.allOf(SignatureKind.class)) {
            if (!this.algebraMap.containsKey(sigKind)) {
                throw new IllegalStateException(String.format(
                    "Implementation of signature '%s' is missing", sigKind));
            }
        }
    }

    /** Returns the name of this algebra family. */
    public final String getName() {
        return this.name;
    }

    /**
     * Returns the algebra class registered for a given named signature, if any.
     */
    public Algebra<?> getAlgebra(SignatureKind sigKind) {
        return this.algebraMap.get(sigKind);
    }

    /** 
     * Returns the value for a given constant.
     * @param signature the signature of which this is a constant
     * @param constant the string representation of the constant.
     * @return the value {@code constant} (in the appropriate algebra)
     * @see #getAlgebraFor(String)
     */
    public Object getValue(SignatureKind signature, String constant) {
        return getAlgebra(signature).getValueFromString(constant);
    }

    /** 
     * Returns the value for a given constant.
     * @return the value {@code constant} (in the appropriate algebra)
     * @see #getAlgebraFor(String)
     */
    public Object getValue(Constant constant) {
        return getValue(constant.getSignature(), constant.getSymbol());
    }

    /**
     * Returns the operation associated with a certain signature name and operation
     * name.
     * @throws UnknownSymbolException if the signature name does not exist, or
     *         the operation name does not occur in the signature.
     */
    public Operation getOperation(SignatureKind sigKind, String operation)
        throws UnknownSymbolException {
        Algebra<?> algebra = getAlgebra(sigKind);
        if (algebra == null) {
            throw new UnknownSymbolException(String.format(
                "No algebra registered for signature '%s'", sigKind));
        }
        Operation result = getOperations(algebra).get(operation);
        if (result == null) {
            throw new UnknownSymbolException(String.format(
                "Operation '%s' does not occur in signature '%s'", operation,
                sigKind));
        }
        return result;
    }

    /**
     * Returns the method associated with a certain operator.
     */
    public Operation getOperation(Operator operator) {
        Algebra<?> algebra = getAlgebra(operator.getSignature());
        assert algebra != null;
        return getOperations(algebra).get(operator.getName());
    }

    /**
     * Returns, for a given algebra, the corresponding mapping from
     * method names to methods.
     */
    private Map<String,Operation> getOperations(Algebra<?> algebra) {
        Map<String,Operation> result = this.operationsMap.get(algebra);
        if (result == null) {
            result = createOperationsMap(algebra);
            this.operationsMap.put(algebra, result);
        }
        return result;
    }

    /** 
     * Returns the algebra containing a given constant.
     * The constant is looked up in the available algebras of this register.
     * @param constant the string representation of the constant.
     * @return the algebra containing {@code constant}, or {@code null} if there is no such algebra.
     */
    public Algebra<?> getAlgebraFor(String constant) {
        for (Algebra<?> algebra : this.algebraMap.values()) {
            if (algebra.isValue(constant)) {
                return algebra;
            }
        }
        return null;
    }

    /**
     * Returns a mapping from operation names to operations for a given algebra.
     */
    private Map<String,Operation> createOperationsMap(Algebra<?> algebra) {
        Map<String,Operation> result = new HashMap<String,Operation>();
        // first find out what methods were declared in the signature
        Set<String> methodNames = new HashSet<String>();
        Method[] signatureMethods =
            Algebras.getSignature(algebra).getDeclaredMethods();
        for (Method method : signatureMethods) {
            if (Modifier.isAbstract(method.getModifiers())
                && Modifier.isPublic(method.getModifiers())) {
                methodNames.add(method.getName());
            }
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

    /** The algebra family name. */
    private final String name;
    /** A map from signature kinds to algebras registered for that name. */
    private final Map<SignatureKind,Algebra<?>> algebraMap =
        new EnumMap<SignatureKind,Algebra<?>>(SignatureKind.class);
    /** Store of operations created from the algebras. */
    private final Map<Algebra<?>,Map<String,Operation>> operationsMap =
        new HashMap<Algebra<?>,Map<String,Operation>>();

    /** Returns the algebra register with the family of default algebras. */
    static public AlgebraFamily getInstance() {
        return defaultFamily;
    }

    /**
     * Returns the algebra register with a given name.
     */
    static public AlgebraFamily getInstance(String instanceName) {
        AlgebraFamily result = familyMap.get(instanceName);
        return result;
    }

    /** Name of the default (Java) algebra family. */
    static public final String DEFAULT_ALGEBRAS = "default";
    /** Name of the point algebra family. */
    static public final String POINT_ALGEBRAS = "point";
    /** Name of the big algebra family. */
    static public final String BIG_ALGEBRAS = "big";

    /** The default algebra register. */
    static private final AlgebraFamily defaultFamily;
    /** The point algebra register. */
    static private final AlgebraFamily pointFamily;
    /** The big algebra register. */
    static private final AlgebraFamily bigFamily;
    /** Default algebra register. */
    static private final Map<String,AlgebraFamily> familyMap;
    static {
        familyMap = new HashMap<String,AlgebraFamily>();
        Set<Algebra<?>> defaultAlgebraFamily = new HashSet<Algebra<?>>();
        defaultAlgebraFamily.add(JavaIntAlgebra.instance);
        defaultAlgebraFamily.add(BoolAlgebra.instance);
        defaultAlgebraFamily.add(StringAlgebra.instance);
        defaultAlgebraFamily.add(JavaDoubleAlgebra.instance);
        defaultFamily =
            new AlgebraFamily(DEFAULT_ALGEBRAS, defaultAlgebraFamily);
        familyMap.put(DEFAULT_ALGEBRAS, defaultFamily);
        Set<Algebra<?>> pointAlgebraFamily = new HashSet<Algebra<?>>();
        pointAlgebraFamily.add(IntPointAlgebra.instance);
        pointAlgebraFamily.add(BoolPointAlgebra.instance);
        pointAlgebraFamily.add(StringPointAlgebra.instance);
        pointAlgebraFamily.add(RealPointAlgebra.instance);
        pointFamily = new AlgebraFamily(POINT_ALGEBRAS, pointAlgebraFamily);
        familyMap.put(POINT_ALGEBRAS, pointFamily);
        Set<Algebra<?>> bigAlgebraFamily = new HashSet<Algebra<?>>();
        bigAlgebraFamily.add(BigIntAlgebra.instance);
        bigAlgebraFamily.add(BoolAlgebra.instance);
        bigAlgebraFamily.add(StringAlgebra.instance);
        bigAlgebraFamily.add(BigDoubleAlgebra.instance);
        bigFamily = new AlgebraFamily(POINT_ALGEBRAS, bigAlgebraFamily);
        familyMap.put(BIG_ALGEBRAS, bigFamily);
    }

    /** Implementation of an algebra operation. */
    private static class Operation implements groove.algebra.Operation {
        Operation(AlgebraFamily register, Algebra<?> algebra, Method method) {
            this.algebra = algebra;
            this.method = method;
            SignatureKind returnType =
                Algebras.getOperator(algebra.getKind(), method.getName()).getResultType();
            this.returnType = register.getAlgebra(returnType);
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

        public String getName() {
            return this.method.getName();
        }

        @Override
        public String toString() {
            return getName();
        }

        private final Algebra<?> algebra;
        private final Algebra<?> returnType;
        private final Method method;
    }
}
