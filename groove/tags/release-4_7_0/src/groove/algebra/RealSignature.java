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
 * $Id: RealSignature.java,v 1.3 2007-08-26 07:24:19 rensink Exp $
 */
package groove.algebra;

import static groove.algebra.Precedence.ADD;
import static groove.algebra.Precedence.COMPARE;
import static groove.algebra.Precedence.EQUAL;
import static groove.algebra.Precedence.MULT;
import groove.annotation.InfixSymbol;
import groove.annotation.PrefixSymbol;
import groove.annotation.Syntax;
import groove.annotation.ToolTipBody;
import groove.annotation.ToolTipHeader;

import java.math.BigDecimal;

/**
 * Interface for real number algebras.
 * @author Arend Rensink
 * @version $Revision: 1577 $
 */
@SuppressWarnings("hiding")
public abstract class RealSignature<Real,Bool,String> implements Signature {
    /** Absolute value of a real number. */
    @Syntax("Q%s.LPAR.i.RPAR")
    @ToolTipHeader("Absolute value")
    @ToolTipBody("Returns the absolute value of %s")
    public abstract Real abs(Real arg);

    /** Addition of two real numbers. */
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipHeader("Real number addition")
    @ToolTipBody("Returns the sum of %s and %s")
    @InfixSymbol(symbol = "+", precedence = ADD)
    public abstract Real add(Real arg0, Real arg1);

    /** Subtraction of two real numbers. */
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipHeader("Real number subtraction")
    @ToolTipBody("Returns the difference between %s and %s")
    @InfixSymbol(symbol = "-", precedence = ADD)
    public abstract Real sub(Real arg0, Real arg1);

    /** Multiplication of two real numbers. */
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipHeader("Real number multiplication")
    @ToolTipBody("Returns the product of %s and %s")
    @InfixSymbol(symbol = "*", precedence = MULT)
    public abstract Real mul(Real arg0, Real arg1);

    /** Division of two real numbers. */
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipHeader("Real number division")
    @ToolTipBody("Returns the quotient of %s and %s")
    @InfixSymbol(symbol = "/", precedence = MULT)
    public abstract Real div(Real arg0, Real arg1);

    /** Minimum of two real numbers. */
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipHeader("Real number minimum")
    @ToolTipBody("Returns the minimum of %s and %s")
    public abstract Real min(Real arg0, Real arg1);

    /** Maximum of two real numbers. */
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipHeader("Real number minimum")
    @ToolTipBody("Returns the maximum of %s and %s")
    public abstract Real max(Real arg0, Real arg1);

    /** Lesser-than comparison. */
    @ToolTipHeader("Real number lesser-than test")
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipBody("Yields TRUE if real number %s is properly smaller than real number %s")
    @InfixSymbol(symbol = "<", precedence = COMPARE)
    public abstract Bool lt(Real arg0, Real arg1);

    /** Lesser-or-equal comparison. */
    @ToolTipHeader("Real number lesser-or-equal test")
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipBody("Yields TRUE if real number %s is smaller than real number %s")
    @InfixSymbol(symbol = "<=", precedence = COMPARE)
    public abstract Bool le(Real arg0, Real arg1);

    /** Greater-than comparison. */
    @ToolTipHeader("Real number greater-than test")
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipBody("Yields TRUE if real number %2$s is properly larger than real number %1$s")
    @InfixSymbol(symbol = ">", precedence = COMPARE)
    public abstract Bool gt(Real arg0, Real arg1);

    /** Greater-or-equal comparison. */
    @ToolTipHeader("Real number greater-or-equal test")
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipBody("Yields TRUE if real number %s is larger than real number %s")
    @InfixSymbol(symbol = ">=", precedence = COMPARE)
    public abstract Bool ge(Real arg0, Real arg1);

    /** Equality test. */
    @ToolTipHeader("Real number equality test")
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipBody("Yields TRUE if real number %s equals real number %s")
    @InfixSymbol(symbol = "==", precedence = EQUAL)
    public abstract Bool eq(Real arg0, Real arg1);

    /** Inequality test. */
    @ToolTipHeader("Real number equality test")
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipBody("Yields TRUE if real number %s does not equal real number %s")
    @InfixSymbol(symbol = "!=", precedence = EQUAL)
    public abstract Bool neq(Real arg0, Real arg1);

    /** Inversion. */
    @ToolTipHeader("Real inversion")
    @Syntax("Q%s.LPAR.r1.RPAR")
    @ToolTipBody("Yields the inverse of %s")
    @PrefixSymbol(symbol = "-")
    public abstract Real neg(Real arg);

    /** String representation. */
    @ToolTipHeader("Real-to-string conversion")
    @Syntax("Q%s.LPAR.r1.RPAR")
    @ToolTipBody("Yields a string representation of %s")
    public abstract String toString(Real arg);

    @Override
    public SignatureKind getKind() {
        return SignatureKind.REAL;
    }

    /**
     * Tests if the number can be parsed as a {@link BigDecimal}. This means
     * that a number of any length is accepted.
     */
    final public boolean isValue(java.lang.String value) {
        try {
            new BigDecimal(value);
            return true;
        } catch (NumberFormatException exc) {
            return false;
        }
    }

    /**
     * Conversion of native Java representation of integer constants to
     * the corresponding algebra values.
     * @throws IllegalArgumentException if the parameter is not of type {@link Integer}
     */
    final public Real getValueFromJava(Object constant) {
        if (!(constant instanceof Double)) {
            throw new IllegalArgumentException(java.lang.String.format(
                "Native int type is %s, not %s", Double.class.getSimpleName(),
                constant.getClass().getSimpleName()));
        }
        return toValue((Double) constant);
    }

    /** 
     * Callback method to convert from the native ({@link Integer})
     * representation to the algebra representation.
     */
    protected abstract Real toValue(Double constant);
}
