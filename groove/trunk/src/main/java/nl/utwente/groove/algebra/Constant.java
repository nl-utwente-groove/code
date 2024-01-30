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
import java.util.Objects;
import java.util.function.Function;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.algebra.syntax.SortMap;
import nl.utwente.groove.algebra.syntax.Variable;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.line.Line;
import nl.utwente.groove.util.parse.OpKind;
import nl.utwente.groove.util.parse.StringHandler;

/** A constant symbol for a particular signature. */
public final class Constant extends Expression {
    /**
     * Constructs a new string constant from a given (non-{@code null}) string value.
     */
    Constant(String value) {
        super(true);
        assert value != null;
        this.sort = Sort.STRING;
        this.stringRepr = value;
        this.boolRepr = null;
        this.intRepr = null;
        this.realRepr = null;
    }

    /**
     * Constructs a new boolean constant from a given (non-{@code null}) boolean value.
     */
    Constant(Boolean value) {
        super(true);
        assert value != null;
        this.sort = Sort.BOOL;
        this.boolRepr = value;
        this.stringRepr = null;
        this.intRepr = null;
        this.realRepr = null;
    }

    /**
     * Constructs a new real constant from a given (non-{@code null}) {@link BigDecimal} value.
     */
    Constant(BigDecimal value) {
        super(true);
        this.sort = Sort.REAL;
        this.symbol = value.toString();
        this.realRepr = value;
        this.boolRepr = null;
        this.intRepr = null;
        this.stringRepr = null;
    }

    /**
     * Constructs a new integer constant from a given (non-{@code null}) {@link BigInteger} value.
     */
    Constant(BigInteger value) {
        super(true);
        this.sort = Sort.INT;
        this.symbol = value.toString();
        this.intRepr = value;
        this.boolRepr = null;
        this.stringRepr = null;
        this.realRepr = null;
    }

    @Override
    public boolean isTerm() {
        return true;
    }

    @Override
    public boolean isClosed() {
        return true;
    }

    @Override
    protected SortMap computeTyping() {
        return SortMap.newInstance();
    }

    @Override
    public @NonNull Expression bind(Function<Variable,Object> bindMap) {
        return this;
    }

    @Override
    public final Sort getSort() {
        return this.sort;
    }

    private final Sort sort;

    @Override
    protected Line toLine(OpKind context) {
        return Line.atom(getSymbol());
    }

    @Override
    protected String createParseString() {
        String result = toDisplayString();
        if (isPrefixed()) {
            result = getSort() + ":" + result;
        }
        return result;
    }

    /**
     * Returns the internal string representation, if this is a {@link Sort#STRING} constant.
     * This is the unquoted version of the constant symbol.
     */
    public String getStringRepr() {
        assert getSort() == Sort.STRING;
        return this.stringRepr;
    }

    /** Internal representation in case this is a {@link Sort#STRING} constant. */
    private final String stringRepr;

    /**
     * Returns the internal integer representation, if this is a {@link Sort#INT} constant.
     * This is the unquoted version of the constant symbol.
     */
    public BigInteger getIntRepr() {
        assert getSort() == Sort.INT;
        return this.intRepr;
    }

    /** Internal representation in case this is a {@link Sort#INT} constant. */
    private final BigInteger intRepr;

    /**
     * Returns the internal string representation, if this is a {@link Sort#REAL} constant.
     * This is the unquoted version of the constant symbol.
     */
    public BigDecimal getRealRepr() {
        assert getSort() == Sort.REAL;
        return this.realRepr;
    }

    /** Internal representation in case this is a {@link Sort#REAL} constant. */
    private final BigDecimal realRepr;

    /**
     * Returns the internal string representation, if this is a {@link Sort#BOOL} constant.
     * This is the unquoted version of the constant symbol.
     */
    public Boolean getBoolRepr() {
        assert getSort() == Sort.BOOL;
        return this.boolRepr;
    }

    /** Internal representation in case this is a {@link Sort#BOOL} constant. */
    private final Boolean boolRepr;

    /** Returns the representational object of this constant. */
    private @NonNull Object getRepr() {
        return switch (getSort()) {
        case BOOL -> getBoolRepr();
        case INT -> getIntRepr();
        case REAL -> getRealRepr();
        case STRING -> getStringRepr();
        case USER -> throw Exceptions.unsupportedOp();
        };
    }

    /** Returns the symbolic string representation of this constant. */
    public @NonNull String getSymbol() {
        String result = this.symbol;
        if (result == null) {
            this.symbol = result = switch (getSort()) {
            case STRING -> StringHandler.toQuoted(getStringRepr(), '"');
            default -> getRepr().toString();
            };
        }
        return result;
    }

    /** Sets the symbolic string representation of this constant. */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    private String symbol;

    @Override
    public int hashCode() {
        return Objects.hash(getSort(), getRepr());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Constant other)) {
            return false;
        }
        return Objects.equals(this.sort, other.sort) && Objects.equals(getRepr(), other.getRepr());
    }

    @Override
    public String toString() {
        return getSort() + ":" + toDisplayString();
    }

    /** Returns a string constant containing the given string representation. */
    public static Constant instance(String value) {
        return new Constant(value);
    }

    /** Returns a string constant containing the given boolean representation. */
    public static Constant instance(Boolean value) {
        return new Constant(value);
    }

    /** Returns a string constant containing the given real-number representation. */
    public static Constant instance(BigDecimal value) {
        return new Constant(value);
    }

    /** Returns a string constant containing the given real-number representation. */
    public static Constant instance(double value) {
        return new Constant(BigDecimal.valueOf(value));
    }

    /** Returns a string constant containing the given integer representation. */
    public static Constant instance(BigInteger value) {
        return new Constant(value);
    }

    /** Returns a string constant containing the given integer representation. */
    public static Constant instance(int value) {
        return new Constant(BigInteger.valueOf(value));
    }
}
