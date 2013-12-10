/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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

import groove.algebra.Signature.OpValue;
import groove.util.Keywords;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Enumeration of the currently supported signatures. 
 * @author Arend Rensink
 * @version $Revision $
 */
public enum SignatureKind {
    /** Signature kind of booleans. */
    BOOL(Keywords.BOOL, BoolSignature.class,
            EnumSet.allOf(BoolSignature.Op.class)) {
        @Override
        public Constant getDefaultValue() {
            return BoolSignature.FALSE;
        }
    },
    /** Signature kind of integers. */
    INT(Keywords.INT, IntSignature.class, EnumSet.allOf(IntSignature.Op.class)) {
        @Override
        public Constant getDefaultValue() {
            return IntSignature.ZERO;
        }
    },
    /** Signature kind of real numbers. */
    REAL(Keywords.REAL, RealSignature.class,
            EnumSet.allOf(RealSignature.Op.class)) {
        @Override
        public Constant getDefaultValue() {
            return RealSignature.ZERO;
        }
    },
    /** Signature kind of strings. */
    STRING(Keywords.STRING, StringSignature.class,
            EnumSet.allOf(StringSignature.Op.class)) {
        @Override
        public Constant getDefaultValue() {
            return StringSignature.EMPTY;
        }
    };

    /** Constructs a signature kind with a given name. */
    private SignatureKind(String name, Class<? extends Signature> sigClass,
            Set<? extends OpValue> opValues) {
        assert name != null;
        this.name = name;
        this.sigClass = sigClass;
        this.opValues = opValues;
    }

    /** Returns the name of this signature. */
    final public String getName() {
        return this.name;
    }

    /** Returns a symbolic representation of the default value for this signature. */
    abstract public Constant getDefaultValue();

    @Override
    public String toString() {
        return getName();
    }

    /** Returns the signature class defining this signature kind. */
    Class<? extends Signature> getSignatureClass() {
        return this.sigClass;
    }

    /** Returns the operator corresponding to a given operator name of this signature. */
    public Operator getOperator(String name) {
        if (this.operatorMap == null) {
            this.operatorMap = computeOperatorMap();
        }
        return this.operatorMap.get(name);
    }

    /** Returns all the operators defined by this signature. */
    public Set<? extends OpValue> getOpValues() {
        return this.opValues;
    }

    /** Creates content for {@link #operatorMap}. */
    private SortedMap<String,Operator> computeOperatorMap() {
        SortedMap<String,Operator> result = new TreeMap<String,Operator>();
        for (OpValue op : this.opValues) {
            Operator operator = op.getOperator();
            result.put(operator.getName(), operator);
        }
        return result;
    }

    private final String name;
    private final Class<? extends Signature> sigClass;
    private final Set<? extends OpValue> opValues;
    private Map<String,Operator> operatorMap;

    /** Returns the signature kind for a given signature name. */
    public static SignatureKind getKind(String sigName) {
        return sigNameMap.get(sigName);
    }

    /** Returns the signature kind for a given signature class. */
    public static SignatureKind getKind(Class<?> sigClass) {
        return sigClassMap.get(sigClass);
    }

    /** Returns the set of all known signature names. */
    static public Set<String> getNames() {
        return Collections.unmodifiableSet(sigNameMap.keySet());
    }

    /** Inverse mapping from signature names to signature kinds. */
    private static Map<String,SignatureKind> sigNameMap =
        new HashMap<String,SignatureKind>();
    /** Inverse mapping from signature classes to signature kinds. */
    private static Map<Class<? extends Signature>,SignatureKind> sigClassMap =
        new HashMap<Class<? extends Signature>,SignatureKind>();

    static {
        for (SignatureKind kind : SignatureKind.values()) {
            sigNameMap.put(kind.getName(), kind);
            sigClassMap.put(kind.getSignatureClass(), kind);
        }
    }
}
