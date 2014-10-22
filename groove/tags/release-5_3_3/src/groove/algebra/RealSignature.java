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

import static groove.util.parse.OpKind.ADD;
import static groove.util.parse.OpKind.COMPARE;
import static groove.util.parse.OpKind.EQUAL;
import static groove.util.parse.OpKind.MULT;
import static groove.util.parse.OpKind.UNARY;
import groove.annotation.InfixSymbol;
import groove.annotation.PrefixSymbol;
import groove.annotation.Syntax;
import groove.annotation.ToolTipBody;
import groove.annotation.ToolTipHeader;

/**
 * The signature for real number algebras.
 * @param <Real> The representation type of the real algebra 
 * @param <Bool> The representation type of the boolean algebra 
 * @param <String> The representation type of the string algebra  
 * @author Arend Rensink
 * @version $Revision$
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
    @InfixSymbol(symbol = "+", kind = ADD)
    public abstract Real add(Real arg0, Real arg1);

    /** Subtraction of two real numbers. */
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipHeader("Real number subtraction")
    @ToolTipBody("Returns the difference between %s and %s")
    @InfixSymbol(symbol = "-", kind = ADD)
    public abstract Real sub(Real arg0, Real arg1);

    /** Multiplication of two real numbers. */
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipHeader("Real number multiplication")
    @ToolTipBody("Returns the product of %s and %s")
    @InfixSymbol(symbol = "*", kind = MULT)
    public abstract Real mul(Real arg0, Real arg1);

    /** Division of two real numbers. */
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipHeader("Real number division")
    @ToolTipBody("Returns the quotient of %s and %s")
    @InfixSymbol(symbol = "/", kind = MULT)
    public abstract Real div(Real arg0, Real arg1);

    /** Minimum of two real numbers. */
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipHeader("Real number minimum")
    @ToolTipBody("Returns the minimum of %s and %s")
    public abstract Real min(Real arg0, Real arg1);

    /** Maximum of two real numbers. */
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipHeader("Real number maximum")
    @ToolTipBody("Returns the maximum of %s and %s")
    public abstract Real max(Real arg0, Real arg1);

    /** Lesser-than comparison. */
    @ToolTipHeader("Real number lesser-than test")
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipBody("Yields TRUE if real number %s is properly smaller than real number %s")
    @InfixSymbol(symbol = "<", kind = COMPARE)
    public abstract Bool lt(Real arg0, Real arg1);

    /** Lesser-or-equal comparison. */
    @ToolTipHeader("Real number lesser-or-equal test")
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipBody("Yields TRUE if real number %s is smaller than real number %s")
    @InfixSymbol(symbol = "<=", kind = COMPARE)
    public abstract Bool le(Real arg0, Real arg1);

    /** Greater-than comparison. */
    @ToolTipHeader("Real number greater-than test")
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipBody("Yields TRUE if real number %2$s is properly larger than real number %1$s")
    @InfixSymbol(symbol = ">", kind = COMPARE)
    public abstract Bool gt(Real arg0, Real arg1);

    /** Greater-or-equal comparison. */
    @ToolTipHeader("Real number greater-or-equal test")
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipBody("Yields TRUE if real number %s is larger than real number %s")
    @InfixSymbol(symbol = ">=", kind = COMPARE)
    public abstract Bool ge(Real arg0, Real arg1);

    /** Equality test. */
    @ToolTipHeader("Real number equality test")
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipBody("Yields TRUE if real number %s equals real number %s")
    @InfixSymbol(symbol = "==", kind = EQUAL)
    public abstract Bool eq(Real arg0, Real arg1);

    /** Inequality test. */
    @ToolTipHeader("Real number equality test")
    @Syntax("Q%s.LPAR.r1.COMMA.r2.RPAR")
    @ToolTipBody("Yields TRUE if real number %s does not equal real number %s")
    @InfixSymbol(symbol = "!=", kind = EQUAL)
    public abstract Bool neq(Real arg0, Real arg1);

    /** Inversion. */
    @ToolTipHeader("Real inversion")
    @Syntax("Q%s.LPAR.r1.RPAR")
    @ToolTipBody("Yields the inverse of %s")
    @PrefixSymbol(symbol = "-", kind = UNARY)
    public abstract Real neg(Real arg);

    /** String representation. */
    @ToolTipHeader("Real-to-string conversion")
    @Syntax("Q%s.LPAR.r1.RPAR")
    @ToolTipBody("Yields a string representation of %s")
    public abstract String toString(Real arg);

    @Override
    public Sort getSort() {
        return Sort.REAL;
    }

    /** Real constant for the value zero. */
    public static final Constant ZERO = Constant.instance(0.0);

    /** Enumeration of all operators defined in this signature. */
    public static enum Op implements Signature.OpValue {
        /** Value for {@link #abs(Object)}. */
        ABS,
        /** Value for {@link #add(Object, Object)}. */
        ADD,
        /** Value for {@link #div(Object, Object)}. */
        DIV,
        /** Value for {@link #eq(Object, Object)}. */
        EQ,
        /** Value for {@link #ge(Object, Object)}. */
        GE,
        /** Value for {@link #gt(Object, Object)}. */
        GT,
        /** Value for {@link #le(Object, Object)}. */
        LE,
        /** Value for {@link #lt(Object, Object)}. */
        LT,
        /** Value for {@link #max(Object, Object)}. */
        MAX,
        /** Value for {@link #min(Object, Object)}. */
        MIN,
        /** Value for {@link #mul(Object, Object)}. */
        MUL,
        /** Value for {@link #neq(Object, Object)}. */
        NEQ,
        /** Value for {@link #neq(Object, Object)}. */
        NEG,
        /** Value for {@link #sub(Object, Object)}. */
        SUB,
        /** Value for {@link #toString(Object)}. */
        TO_STRING, ;

        @Override
        public Operator getOperator() {
            if (this.operator == null) {
                this.operator = Operator.newInstance(Sort.REAL, this);
            }
            return this.operator;
        }

        /** Corresponding operator object. */
        private Operator operator;
    }
}
