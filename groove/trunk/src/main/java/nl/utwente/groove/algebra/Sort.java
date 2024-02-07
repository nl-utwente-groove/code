/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.algebra;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Signature.OpValue;
import nl.utwente.groove.annotation.Syntax;
import nl.utwente.groove.annotation.ToolTipBody;
import nl.utwente.groove.annotation.ToolTipHeader;
import nl.utwente.groove.annotation.ToolTipPars;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.Keywords;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.StringHandler;

/**
 * Enumeration of the currently supported signatures sorts.
 * @author Arend Rensink
 * @version $Revision$
 */
public enum Sort {
    /** Boolean sort. */
    @Syntax("TRUE.BAR.FALSE")
    @ToolTipHeader("Boolean literal")
    @ToolTipBody({"Representation of a BOOL constant.", "Only lowercase is valid."})
    BOOL(Keywords.BOOL, BoolSignature.class, EnumSet.allOf(BoolSignature.Op.class), Keywords.NAB) {
        @Override
        public Constant getDefaultValue() {
            return BoolSignature.FALSE;
        }

        @Override
        public Constant createValidConstant(String symbol) throws FormatException {
            Constant result;
            if (symbol.equals(Keywords.TRUE)) {
                result = BoolSignature.TRUE;
            } else if (symbol.equals(Keywords.FALSE)) {
                result = BoolSignature.FALSE;
            } else {
                throw new FormatException("'%s' is not a valid Boolean constant", symbol);
            }
            result.setSymbol(symbol);
            return result;
        }

        @Override
        public boolean denotesValidValue(String symbol) {
            return symbol.equals(Keywords.TRUE) || symbol.equals(Keywords.FALSE);
        }
    },
    /** Integer sort. */
    @Syntax("nr")
    @ToolTipHeader("Integer literal")
    @ToolTipBody("Representation of an INT constant.")
    @ToolTipPars("non-empty sequence of digits")
    INT(Keywords.INT, IntSignature.class, EnumSet.allOf(IntSignature.Op.class), Keywords.NAI) {
        @Override
        public Constant getDefaultValue() {
            return IntSignature.ZERO;
        }

        @Override
        public Constant createValidConstant(String symbol) throws FormatException {
            try {
                Constant result = Constant.instance(new BigInteger(symbol));
                result.setSymbol(symbol);
                return result;
            } catch (NumberFormatException exc) {
                throw new FormatException("'%s' does not denote an integer number", symbol);
            }
        }

        @Override
        public boolean denotesValidValue(String symbol) {
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
    @Syntax("[nr1].DOT.[nr2]")
    @ToolTipHeader("Real literal")
    @ToolTipBody({"Representation of a REAL constant.", "Either %1$s or %2$s must be provided."})
    @ToolTipPars({"optional whole-number part of the REAL: a non-empty sequence of digits",
            "optional fractional part of the REAL: a non-empty sequence of digits"})
    REAL(Keywords.REAL, RealSignature.class, EnumSet.allOf(RealSignature.Op.class), Keywords.NAR) {
        @Override
        public Constant getDefaultValue() {
            return RealSignature.ZERO;
        }

        @Override
        public Constant createValidConstant(String symbol) throws FormatException {
            try {
                Constant result = Constant.instance(new BigDecimal(symbol));
                result.setSymbol(symbol);
                return result;
            } catch (NumberFormatException exc) {
                throw new FormatException("'%s' does not denote a real-valued number", symbol);
            }
        }

        @Override
        public boolean denotesValidValue(String symbol) {
            try {
                // Test whether the symbol correctly represents a double
                Double.parseDouble(symbol);
                return true;
            } catch (NumberFormatException exc) {
                return false;
            }
        }
    },
    /** User-defined sort. */
    USER(Keywords.USER, UserSignature.class, Collections.emptySet(), Keywords.NAU) {
        @Override
        public Constant getDefaultValue() {
            throw Exceptions.unsupportedOp();
        }

        @Override
        public Constant createValidConstant(String symbol) throws FormatException {
            throw Exceptions.unsupportedOp();
        }

        @Override
        public boolean denotesValidValue(String symbol) {
            return false;
        }
    },
    /** String sort. */
    @Syntax("QUOTE.text.QUOTE")
    @ToolTipHeader("String literal")
    @ToolTipBody("Representation of a STRING constant.")
    @ToolTipPars("text of the literal. Follows the java rules for escaping special characters")
    STRING(Keywords.STRING, StringSignature.class, EnumSet.allOf(StringSignature.Op.class),
        Keywords.NAS) {
        @Override
        public Constant getDefaultValue() {
            return StringSignature.EMPTY;
        }

        @Override
        public Constant createValidConstant(String symbol) throws FormatException {
            Constant result = Constant.instance(StringHandler.toUnquoted(symbol));
            result.setSymbol(symbol);
            return result;
        }

        @Override
        public boolean denotesValidValue(String symbol) {
            try {
                createConstant(symbol);
                return true;
            } catch (FormatException exc) {
                return denotesError(symbol);
            }
        }
    };

    /** Constructs a sort with a given name. */
    private Sort(String name, Class<? extends Signature> sigClass, Set<? extends OpValue> opValues,
                 String errorSymbol) {
        assert name != null;
        this.name = name;
        this.sigClass = sigClass;
        this.opValues = opValues;
        this.errorSymbol = errorSymbol;
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

    /** Returns the operator corresponding to a given operator name of this sort.
     * @param name the name of the expected operator
     * @return the operator of this sort called {@code name},
     * or {@code null} if such an operator does not exist
     */
    public @Nullable Operator getOperator(String name) {
        return this.operatorMap.get().get(name);
    }

    /** Checks if this sort has an operator with a given name.
     * @param name the name of the expected operator
     */
    public boolean hasOperator(String name) {
        return this.operatorMap.get().containsKey(name);
    }

    private Factory<? extends Map<String,Operator>> operatorMap
        = Factory.lazy(this::computeOperatorMap);

    /** Creates content for {@link #operatorMap}. */
    private SortedMap<String,Operator> computeOperatorMap() {
        SortedMap<String,Operator> result = new TreeMap<>();
        var opStream = switch (this) {
        case USER -> UserSignature.getOperators().stream();
        default -> this.opValues.stream().map(OpValue::getOperator);
        };
        opStream.forEach(o -> result.put(o.getName(), o));
        return result;
    }

    /** Returns the symbolic representation of the error value. */
    public String getErrorSymbol() {
        return this.errorSymbol;
    }

    private final String errorSymbol;

    /**
     * Creates a (valid or error) constant of this sort
     * from a given symbolic string representation.
     * @param symbol the symbolic representation; non-{@code null}
     * @throws FormatException if {@code symbol} does not represent
     * a value of this sort
     * @see #denotesValue(String)
     */
    public Constant createConstant(String symbol) throws FormatException {
        try {
            return createErrorConstant(symbol);
        } catch (FormatException exc) {
            return createValidConstant(symbol);
        }
    }

    /**
     * Creates a valid (i.e., non-error) constant of this sort
     * from a given symbolic string representation.
     * @param symbol the symbolic representation; non-{@code null}
     * @throws FormatException if {@code symbol} does not represent
     * a valid value of this sort
     * @see #denotesValidValue(String)
     */
    abstract public Constant createValidConstant(String symbol) throws FormatException;

    /**
     * Creates an error constant of this sort
     * from a given symbolic string representation.
     * @param symbol the symbolic representation; non-{@code null}
     * @throws FormatException if {@code symbol} does not represent
     * the error value of this sort
     * @see #denotesError(String)
     */
    public Constant createErrorConstant(String symbol) throws FormatException {
        if (denotesError(symbol)) {
            return Constant.error(this);
        } else {
            throw new FormatException("'%s' does not represent an error value of sort %s", symbol,
                this);
        }
    }

    /**
     * Indicates if a given string is a valid symbolic representation of
     * a constant of this sort.
     */
    public boolean denotesValue(String symbol) {
        return denotesError(symbol) || denotesValidValue(symbol);
    }

    /** Indicates if a given symbol represents the error symbol of this sort. */
    public boolean denotesError(String symbol) {
        return symbol.equals(getErrorSymbol());
    }

    /** Indicates if a given symbol represents a valid (non-error) value of this sort. */
    abstract public boolean denotesValidValue(String symbol);

    /** Returns the sort for a given sort name.
     * @return the sort for {@code name}, or {@code null} if {@code name} is not a sort name
     */
    public static Sort getSort(String sortName) {
        return sortNameMap.get(sortName);
    }

    /** Returns the sort for a given signature class. */
    public static Sort getSort(Class<?> sortClass) {
        return sortClassMap.get(sortClass);
    }

    /** Returns the set of all known signature names. */
    public static Set<String> getNames() {
        return Collections.unmodifiableSet(sortNameMap.keySet());
    }

    /** Inverse mapping from signature names to sorts. */
    private static Map<String,Sort> sortNameMap = new HashMap<>();
    /** Inverse mapping from signature classes to sorts. */
    private static Map<Class<? extends Signature>,Sort> sortClassMap = new HashMap<>();

    static {
        for (Sort kind : Sort.values()) {
            sortNameMap.put(kind.getName(), kind);
            sortClassMap.put(kind.getSignatureClass(), kind);
        }
    }
}
