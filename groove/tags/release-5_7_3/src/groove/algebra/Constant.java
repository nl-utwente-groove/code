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

import java.math.BigDecimal;
import java.math.BigInteger;

import groove.algebra.syntax.Expression;
import groove.algebra.syntax.Typing;
import groove.util.Exceptions;
import groove.util.line.Line;
import groove.util.parse.OpKind;
import groove.util.parse.StringHandler;

/** A constant symbol for a particular signature. */
public class Constant extends Expression {
    /**
     * Constructs a new string constant from a given (non-{@code null}) string value.
     */
    Constant(String value) {
        super(true);
        assert value != null;
        this.signature = Sort.STRING;
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
        this.signature = Sort.BOOL;
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
        this.signature = Sort.REAL;
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
        this.signature = Sort.INT;
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
    protected Typing computeTyping() {
        return Typing.emptyTyping();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.signature.hashCode();
        switch (this.signature) {
        case BOOL:
            result = prime * result + this.boolRepr.hashCode();
            break;
        case INT:
            result = prime * result + this.intRepr.hashCode();
            break;
        case REAL:
            result = prime * result + this.realRepr.hashCode();
            break;
        case STRING:
            result = prime * result + this.stringRepr.hashCode();
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Constant)) {
            return false;
        }
        Constant other = (Constant) obj;
        if (!this.signature.equals(other.signature)) {
            return false;
        }
        switch (this.signature) {
        case BOOL:
            return this.boolRepr.equals(other.boolRepr);
        case INT:
            return this.intRepr.equals(other.intRepr);
        case REAL:
            return this.realRepr.equals(other.realRepr);
        case STRING:
            return this.stringRepr.equals(other.stringRepr);
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

    @Override
    public String toString() {
        return getSort() + ":" + toDisplayString();
    }

    @Override
    public final Sort getSort() {
        return this.signature;
    }

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

    /**
     * Returns the internal integer representation, if this is a {@link Sort#INT} constant.
     * This is the unquoted version of the constant symbol.
     */
    public BigInteger getIntRepr() {
        assert getSort() == Sort.INT;
        return this.intRepr;
    }

    /**
     * Returns the internal string representation, if this is a {@link Sort#REAL} constant.
     * This is the unquoted version of the constant symbol.
     */
    public BigDecimal getRealRepr() {
        assert getSort() == Sort.REAL;
        return this.realRepr;
    }

    /**
     * Returns the internal string representation, if this is a {@link Sort#BOOL} constant.
     * This is the unquoted version of the constant symbol.
     */
    public Boolean getBoolRepr() {
        assert getSort() == Sort.BOOL;
        return this.boolRepr;
    }

    private final Sort signature;
    /** Internal representation in case this is a {@link Sort#STRING} constant. */
    private final String stringRepr;
    /** Internal representation in case this is a {@link Sort#INT} constant. */
    private final BigInteger intRepr;
    /** Internal representation in case this is a {@link Sort#REAL} constant. */
    private final BigDecimal realRepr;
    /** Internal representation in case this is a {@link Sort#BOOL} constant. */
    private final Boolean boolRepr;

    /** Returns the symbolic string representation of this constant. */
    public String getSymbol() {
        if (this.symbol == null) {
            switch (getSort()) {
            case BOOL:
                this.symbol = this.boolRepr.toString();
                break;
            case INT:
                this.symbol = this.intRepr.toString();
                break;
            case REAL:
                this.symbol = this.realRepr.toString();
                break;
            case STRING:
                this.symbol = StringHandler.toQuoted(this.stringRepr, '"');
                break;
            default:
                throw Exceptions.UNREACHABLE;
            }
        }
        return this.symbol;
    }

    /** Sets the symbolic string representation of this constant. */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    private String symbol;

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
