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

import groove.algebra.syntax.Expression;
import groove.grammar.model.FormatException;
import groove.util.Keywords;
import groove.util.line.Line;
import groove.util.parse.StringHandler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;

/** A constant symbol for a particular signature. */
public class Constant extends Expression {
    /**
     * Constructs a new constant from a given signature and 
     * constant symbol.
     */
    private Constant(boolean explicitType, SignatureKind signature,
            String symbol) {
        super(explicitType);
        assert signature != null && symbol != null;
        this.signature = signature;
        this.symbol = symbol;
    }

    /**
     * Constructs a new string constant from a given string value.
     */
    private Constant(String value) {
        this(false, SignatureKind.STRING, StringHandler.toQuoted(value, '"'));
        this.stringRepr = value;
    }

    /**
     * Constructs a new boolean constant from a given boolean value.
     */
    private Constant(Boolean value) {
        this(false, SignatureKind.BOOL, value.toString());
        this.boolRepr = value;
    }

    /**
     * Constructs a new real constant from a given {@link BigDecimal} value.
     */
    private Constant(BigDecimal value) {
        this(false, SignatureKind.REAL, value.toString());
        this.realRepr = value;
    }

    /**
     * Constructs a new integer constant from a given {@link BigInteger} value.
     */
    private Constant(BigInteger value) {
        this(false, SignatureKind.INT, value.toString());
        this.intRepr = value;
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
    protected Map<String,SignatureKind> computeVarMap() {
        return Collections.emptyMap();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.signature.hashCode();
        result = prime * result + this.symbol.hashCode();
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
        if (!this.symbol.equals(other.symbol)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getSignature() + ":" + toDisplayString();
    }

    @Override
    public final SignatureKind getSignature() {
        return this.signature;
    }

    @Override
    protected Line toLine(Precedence context) {
        return Line.atom(this.symbol);
    }

    @Override
    protected String createParseString() {
        String result = toDisplayString();
        if (isPrefixed()) {
            result = getSignature() + ":" + result;
        }
        return result;
    }

    /**
     * Returns the internal string representation, if this is a {@link SignatureKind#STRING} constant.
     * This is the unquoted version of the constant symbol. 
     */
    public String getStringRepr() {
        assert getSignature() == SignatureKind.STRING;
        if (this.stringRepr == null) {
            try {
                this.stringRepr = StringHandler.toUnquoted(this.symbol, '"');
            } catch (FormatException e) {
                assert false : String.format(
                    "%s is not a double-quoted string", this.symbol);
            }
        }
        return this.stringRepr;
    }

    /**
     * Returns the internal integer representation, if this is a {@link SignatureKind#INT} constant.
     * This is the unquoted version of the constant symbol. 
     */
    public BigInteger getIntRepr() {
        assert getSignature() == SignatureKind.INT;
        if (this.intRepr == null) {
            this.intRepr = new BigInteger(this.symbol);
        }
        return this.intRepr;
    }

    /**
     * Returns the internal string representation, if this is a {@link SignatureKind#REAL} constant.
     * This is the unquoted version of the constant symbol. 
     */
    public BigDecimal getRealRepr() {
        assert getSignature() == SignatureKind.REAL;
        if (this.realRepr == null) {
            this.realRepr = new BigDecimal(this.symbol);
        }
        return this.realRepr;
    }

    /**
     * Returns the internal string representation, if this is a {@link SignatureKind#BOOL} constant.
     * This is the unquoted version of the constant symbol. 
     */
    public Boolean getBoolRepr() {
        assert getSignature() == SignatureKind.BOOL;
        if (this.boolRepr == null) {
            this.boolRepr = this.symbol.equals(Keywords.TRUE);
        }
        return this.boolRepr;
    }

    private final SignatureKind signature;
    private final String symbol;
    /** Internal representation in case this is a {@link SignatureKind#STRING} constant. */
    private String stringRepr;
    /** Internal representation in case this is a {@link SignatureKind#INT} constant. */
    private BigInteger intRepr;
    /** Internal representation in case this is a {@link SignatureKind#REAL} constant. */
    private BigDecimal realRepr;
    /** Internal representation in case this is a {@link SignatureKind#BOOL} constant. */
    private Boolean boolRepr;

    /**
     * Returns a constant object for a given symbol.
     * @param symbol syntactic symbol for the constant
     * @return a constant object, or {@code null} if 
     * the symbol does not represent a constant value of this.
     */
    public static Constant parseConstant(String symbol) throws FormatException {
        Constant result = null;
        Expression expr = Expression.parse(symbol);
        if (expr.getKind() == Kind.CONST) {
            result = (Constant) expr;
        } else {
            throw new FormatException("%s is not a constant term", expr);
        }
        return result;
    }

    /** Returns a string constant containing the given string representation. */
    public static Constant instance(String value) {
        return new Constant(value);
    }

    /** Returns a string constant of a certain type and symbolic value. */
    public static Constant instance(SignatureKind signature, String symbol) {
        return new Constant(true, signature, symbol);
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
