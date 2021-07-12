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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import groove.algebra.Signature.OpValue;
import groove.util.Keywords;
import groove.util.parse.FormatException;
import groove.util.parse.StringHandler;

/**
 * Enumeration of the currently supported signatures sorts.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum Sort {
    /** Boolean sort. */
    BOOL(Keywords.BOOL, BoolSignature.class, EnumSet.allOf(BoolSignature.Op.class)) {
        @Override
        public Constant getDefaultValue() {
            return BoolSignature.FALSE;
        }

        @Override
        public Constant createConstant(String symbol) throws FormatException {
            Constant result;
            if (symbol.equals(Boolean.toString(true))) {
                result = BoolSignature.TRUE;
            } else if (symbol.equals(Boolean.toString(false))) {
                result = BoolSignature.FALSE;
            } else {
                throw new FormatException("'%s' is not a valid Boolean constant", symbol);
            }
            result.setSymbol(symbol);
            return result;
        }

        @Override
        public boolean denotesConstant(String symbol) {
            return symbol.equals(Boolean.toString(true)) || symbol.equals(Boolean.toString(false));
        }
    },
    /** Integer sort. */
    INT(Keywords.INT, IntSignature.class, EnumSet.allOf(IntSignature.Op.class)) {
        @Override
        public Constant getDefaultValue() {
            return IntSignature.ZERO;
        }

        @Override
        public Constant createConstant(String symbol) throws FormatException {
            try {
                Constant result = Constant.instance(new BigInteger(symbol));
                result.setSymbol(symbol);
                return result;
            } catch (NumberFormatException exc) {
                throw new FormatException("'%s' does not denote an integer number", symbol);
            }
        }

        @Override
        public boolean denotesConstant(String symbol) {
            try {
                // Test that the symbol is a correct integer
                Integer.parseInt(symbol);
                return true;
            } catch (NumberFormatException exc) {
                return false;
            }
        }
    },
    /** Real number sort. */
    REAL(Keywords.REAL, RealSignature.class, EnumSet.allOf(RealSignature.Op.class)) {
        @Override
        public Constant getDefaultValue() {
            return RealSignature.ZERO;
        }

        @Override
        public Constant createConstant(String symbol) throws FormatException {
            try {
                Constant result = Constant.instance(new BigDecimal(symbol));
                result.setSymbol(symbol);
                return result;
            } catch (NumberFormatException exc) {
                throw new FormatException("'%s' does not denote a real-valued number", symbol);
            }
        }

        @Override
        public boolean denotesConstant(String symbol) {
            try {
                // Test whether the symbol correctly represents a double
                Double.parseDouble(symbol);
                return true;
            } catch (NumberFormatException exc) {
                return false;
            }
        }
    },
    /** String sort. */
    STRING(Keywords.STRING, StringSignature.class, EnumSet.allOf(StringSignature.Op.class)) {
        @Override
        public Constant getDefaultValue() {
            return StringSignature.EMPTY;
        }

        @Override
        public Constant createConstant(String symbol) throws FormatException {
            Constant result = Constant.instance(StringHandler.toUnquoted(symbol));
            result.setSymbol(symbol);
            return result;
        }

        @Override
        public boolean denotesConstant(String symbol) {
            try {
                createConstant(symbol);
                return true;
            } catch (FormatException exc) {
                return false;
            }
        }
    };

    /** Constructs a sort with a given name. */
    private Sort(String name, Class<? extends Signature> sigClass,
        Set<? extends OpValue> opValues) {
        assert name != null;
        this.name = name;
        this.sigClass = sigClass;
        this.opValues = opValues;
    }

    /** Returns the name of this sort. */
    public final String getName() {
        return this.name;
    }

    private final String name;

    /** Returns a symbolic representation of the default value for this sort. */
    public abstract Constant getDefaultValue();

    @Override
    public String toString() {
        return getName();
    }

    /** Returns the signature class defining this sort. */
    Class<? extends Signature> getSignatureClass() {
        return this.sigClass;
    }

    private final Class<? extends Signature> sigClass;

    /** Returns all the operators defined by this sort. */
    public Set<? extends OpValue> getOpValues() {
        return this.opValues;
    }

    private final Set<? extends OpValue> opValues;

    /** Returns the operator corresponding to a given operator name of this sort. */
    public Operator getOperator(String name) {
        if (this.operatorMap == null) {
            this.operatorMap = computeOperatorMap();
        }
        return this.operatorMap.get(name);
    }

    /** Creates content for {@link #operatorMap}. */
    private SortedMap<String,Operator> computeOperatorMap() {
        SortedMap<String,Operator> result = new TreeMap<>();
        for (OpValue op : this.opValues) {
            Operator operator = op.getOperator();
            result.put(operator.getName(), operator);
        }
        return result;
    }

    private Map<String,Operator> operatorMap;

    /**
     * Creates a constant of this sort
     * from a given symbolic string representation.
     * @param symbol the symbolic representation; non-{@code null}
     * @throws FormatException if {@code symbol} is not a valid representation
     * of a constant of this sort
     * @see #denotesConstant(String)
     */
    public abstract Constant createConstant(String symbol) throws FormatException;

    /**
     * Indicates if a given string is a valid symbolic representation of
     * a constant of this sort.
     */
    public abstract boolean denotesConstant(String symbol);

    /** Returns the sort for a given sort name.
     * @return the sort for {@code name}, or {@code null} if {@code name} is not a sort name
     */
    public static Sort getKind(String sigName) {
        return sigNameMap.get(sigName);
    }

    /** Returns the sort for a given signature class. */
    public static Sort getKind(Class<?> sigClass) {
        return sigClassMap.get(sigClass);
    }

    /** Returns the set of all known signature names. */
    public static Set<String> getNames() {
        return Collections.unmodifiableSet(sigNameMap.keySet());
    }

    /** Inverse mapping from signature names to sorts. */
    private static Map<String,Sort> sigNameMap = new HashMap<>();
    /** Inverse mapping from signature classes to sorts. */
    private static Map<Class<? extends Signature>,Sort> sigClassMap = new HashMap<>();

    static {
        for (Sort kind : Sort.values()) {
            sigNameMap.put(kind.getName(), kind);
            sigClassMap.put(kind.getSignatureClass(), kind);
        }
    }
}
