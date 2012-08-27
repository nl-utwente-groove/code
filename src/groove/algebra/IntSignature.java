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
 * $Id: IntSignature.java,v 1.3 2007-08-26 07:24:19 rensink Exp $
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

import java.math.BigInteger;

/**
 * Interface for integer algebras.
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("hiding")
public abstract class IntSignature<Int,Bool,String> implements Signature {
    /** Absolute value of an integer. */
    @Syntax("Q%s.LPAR.i.RPAR")
    @ToolTipHeader("Absolute integer value")
    @ToolTipBody("Returns the absolute value of %s")
    public abstract Int abs(Int arg);

    /** Addition of two integers. */
    @Syntax("Q%s.LPAR.i1.COMMA.i2.RPAR")
    @ToolTipHeader("Integer addition")
    @ToolTipBody("Returns the sum of %s and %s")
    @InfixSymbol(symbol = "+", precedence = ADD)
    public abstract Int add(Int arg0, Int arg1);

    /** Division of two integers. */
    @Syntax("Q%s.LPAR.i1.COMMA.i2.RPAR")
    @ToolTipHeader("Integer division")
    @ToolTipBody("Returns the integer quotient of %s and %s")
    @InfixSymbol(symbol = "/", precedence = MULT)
    public abstract Int div(Int arg0, Int arg1);

    /** Equality test. */
    @ToolTipHeader("Integer equality test")
    @Syntax("Q%s.LPAR.i1.COMMA.i2.RPAR")
    @ToolTipBody("Yields TRUE if integer %s equals integer %s")
    @InfixSymbol(symbol = "==", precedence = EQUAL)
    public abstract Bool eq(Int arg0, Int arg1);

    /** Greater-or-equal comparison. */
    @ToolTipHeader("Integer greater-or-equal test")
    @Syntax("Q%s.LPAR.i1.COMMA.i2.RPAR")
    @ToolTipBody("Yields TRUE if integer %s is larger than integer %s")
    @InfixSymbol(symbol = ">=", precedence = COMPARE)
    public abstract Bool ge(Int arg0, Int arg1);

    /** Greater-than comparison. */
    @ToolTipHeader("Integer greater-than test")
    @Syntax("Q%s.LPAR.i1.COMMA.i2.RPAR")
    @ToolTipBody("Yields TRUE if integer %2$s is properly larger than integer %1$s")
    @InfixSymbol(symbol = ">", precedence = COMPARE)
    public abstract Bool gt(Int arg0, Int arg1);

    /** Lesser-or-equal comparison. */
    @ToolTipHeader("Integer lesser-or-equal test")
    @Syntax("Q%s.LPAR.i1.COMMA.i2.RPAR")
    @ToolTipBody("Yields TRUE if integer %s is smaller than integer %s")
    @InfixSymbol(symbol = "<=", precedence = COMPARE)
    public abstract Bool le(Int arg0, Int arg1);

    /** Lesser-than comparison. */
    @ToolTipHeader("Integer lesser-than test")
    @Syntax("Q%s.LPAR.i1.COMMA.i2.RPAR")
    @ToolTipBody("Yields TRUE if integer %s is properly smaller than integer %s")
    @InfixSymbol(symbol = "<", precedence = COMPARE)
    public abstract Bool lt(Int arg0, Int arg1);

    /** Maximum of two integers. */
    @Syntax("Q%s.LPAR.i1.COMMA.i2.RPAR")
    @ToolTipHeader("Integer minimum")
    @ToolTipBody("Returns the maximum of %s and %s")
    public abstract Int max(Int arg0, Int arg1);

    /** Minimum of two integers. */
    @Syntax("Q%s.LPAR.i1.COMMA.i2.RPAR")
    @ToolTipHeader("Integer minimum")
    @ToolTipBody("Returns the minimum of %s and %s")
    public abstract Int min(Int arg0, Int arg1);

    /** Modulo of two integers. */
    @Syntax("Q%s.LPAR.i1.COMMA.i2.RPAR")
    @ToolTipHeader("Modulo")
    @ToolTipBody("Returns the remainder after dividing %s by %s")
    @InfixSymbol(symbol = "%", precedence = MULT)
    public abstract Int mod(Int arg0, Int arg1);

    /** Multiplication of two integers. */
    @Syntax("Q%s.LPAR.i1.COMMA.i2.RPAR")
    @ToolTipHeader("Integer multiplication")
    @ToolTipBody("Returns the product of %s and %s")
    @InfixSymbol(symbol = "*", precedence = MULT)
    public abstract Int mul(Int arg0, Int arg1);

    /** Inequality test. */
    @ToolTipHeader("Integer inequality test")
    @Syntax("Q%s.LPAR.i1.COMMA.i2.RPAR")
    @ToolTipBody("Yields TRUE if integer %s does not equal integer %s")
    @InfixSymbol(symbol = "!=", precedence = EQUAL)
    public abstract Bool neq(Int arg0, Int arg1);

    /** Inversion. */
    @ToolTipHeader("Integer inversion")
    @Syntax("Q%s.LPAR.i1.RPAR")
    @ToolTipBody("Yields the inverse of %s")
    @PrefixSymbol(symbol = "-")
    public abstract Int neg(Int arg);

    /** Subtraction of two integers. */
    @Syntax("Q%s.LPAR.i1.COMMA.i2.RPAR")
    @ToolTipHeader("Integer subtraction")
    @ToolTipBody("Returns the difference between %s and %s")
    @InfixSymbol(symbol = "-", precedence = ADD)
    public abstract Int sub(Int arg0, Int arg1);

    /** String representation. */
    @ToolTipHeader("Integer-to-string conversion")
    @Syntax("Q%s.LPAR.i1.RPAR")
    @ToolTipBody("Yields a string representation of %s")
    public abstract String toString(Int arg);

    @Override
    public SignatureKind getKind() {
        return SignatureKind.INT;
    }

    /**
     * Tests if the number can be parsed as a {@link BigInteger}. This means
     * that a number of any length is accepted.
     */
    final public boolean isValue(java.lang.String value) {
        try {
            new BigInteger(value);
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
    final public Int getValueFromJava(Object constant) {
        if (!(constant instanceof Integer)) {
            throw new IllegalArgumentException(java.lang.String.format(
                "Native int type is %s, not %s", Integer.class.getSimpleName(),
                constant.getClass().getSimpleName()));
        }
        return toValue((Integer) constant);
    }

    /** 
     * Callback method to convert from the native ({@link Integer})
     * representation to the algebra representation.
     */
    protected abstract Int toValue(Integer constant);
}
