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

import static nl.utwente.groove.util.parse.OpKind.ADD;
import static nl.utwente.groove.util.parse.OpKind.COMPARE;
import static nl.utwente.groove.util.parse.OpKind.EQUAL;
import static nl.utwente.groove.util.parse.OpKind.MULT;
import static nl.utwente.groove.util.parse.OpKind.UNARY;

import java.util.List;

import nl.utwente.groove.annotation.InfixSymbol;
import nl.utwente.groove.annotation.PrefixSymbol;
import nl.utwente.groove.annotation.Syntax;
import nl.utwente.groove.annotation.ToolTipBody;
import nl.utwente.groove.annotation.ToolTipHeader;
import nl.utwente.groove.util.Exceptions;

/**
 * Generic signature extension containing default implementations for all operations.
 * This serves for a uniform representation, in particular for documentation purposes.
 * @param <MAIN> The main sort to which the signature is specialised
 * @param <INT> The representation type of the int algebra
 * @param <REAL> The representation type of the real algebra
 * @param <BOOL> The representation type of the boolean algebra
 * @param <STRING> The representation type of the string algebra
 * @author Rensink
 * @version $Revision $
 */
public interface GSignature<MAIN,INT,REAL,BOOL,STRING> extends Signature {
    /** Absolute value of a real number. */
    @Syntax("Q%s.LPAR.expr.RPAR")
    @ToolTipHeader("Absolute value")
    @ToolTipBody("Returns the absolute value of %s")
    public default MAIN abs(MAIN arg) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "abs", getSort());
    }

    /** Addition of two real numbers. */
    @Syntax("Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipHeader("Addition")
    @ToolTipBody("Returns the sum of %1$s and %2$s")
    @InfixSymbol(symbol = "+", kind = ADD)
    public default MAIN add(MAIN arg0, MAIN arg1) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "add", getSort());
    }

    /** Maximum of a nonempty set of numbers. */
    @Syntax("Q%s.LPAR.expr.RPAR")
    @ToolTipHeader("Collective maximum")
    @ToolTipBody("Returns the maximum of all quantified values")
    public default MAIN bigmax(List<MAIN> arg) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "bigmax", getSort());
    }

    /** Minimum of a nonempty set of numbers. */
    @Syntax("Q%s.LPAR.expr.RPAR")
    @ToolTipHeader("Collective minimum")
    @ToolTipBody("Returns the minimum of all quantified values")
    public default MAIN bigmin(List<MAIN> arg) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "bigmin", getSort());
    }

    /** Subtraction of two numbers. */
    @Syntax("Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipHeader("Subtraction")
    @ToolTipBody("Returns the difference between %1$s and %2$s")
    @InfixSymbol(symbol = "-", kind = ADD)
    public default MAIN sub(MAIN arg0, MAIN arg1) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "sub", getSort());
    }

    /** Multiplication of two real numbers. */
    @Syntax("Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipHeader("Multiplication")
    @ToolTipBody("Returns the product of %1$s and %2$s")
    @InfixSymbol(symbol = "*", kind = MULT)
    public default MAIN mul(MAIN arg0, MAIN arg1) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "mul", getSort());
    }

    /** Division of two real numbers. */
    @Syntax("Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipHeader("Division")
    @ToolTipBody("Returns the quotient of %1$s and %2$s")
    @InfixSymbol(symbol = "/", kind = MULT)
    public default MAIN div(MAIN arg0, MAIN arg1) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "div", getSort());
    }

    /** Minimum of two real numbers. */
    @Syntax("Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipHeader("Minimum")
    @ToolTipBody("Returns the minimum of %1$s and %2$s")
    public default MAIN min(MAIN arg0, MAIN arg1) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "min", getSort());
    }

    /** Maximum of two real numbers. */
    @Syntax("Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipHeader("Maximum")
    @ToolTipBody("Returns the maximum of %1$s and %2$s")
    public default MAIN max(MAIN arg0, MAIN arg1) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "max", getSort());
    }

    /** Product of a set of values. */
    @Syntax("Q%s.LPAR.expr.RPAR")
    @ToolTipHeader("Product")
    @ToolTipBody("Returns the product of all quantified values")
    public default MAIN prod(List<MAIN> arg) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "prod", getSort());
    }

    /** Summation over a set of values. */
    @Syntax("Q%s.LPAR.expr.RPAR")
    @ToolTipHeader("Real summation")
    @ToolTipBody("Returns the sum of all quantified values")
    public default MAIN sum(List<MAIN> arg) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "sum", getSort());
    }

    /** Lesser-than comparison. */
    @ToolTipHeader("Lesser-than test")
    @Syntax("Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipBody("Yields TRUE if %1$s is properly smaller than %2$s")
    @InfixSymbol(symbol = "<", kind = COMPARE)
    public default BOOL lt(REAL arg0, REAL arg1) {
        throw Exceptions.unsupportedOp("Operation %s not implemented for sort %s", "lt", getSort());
    }

    /** Lesser-or-equal comparison. */
    @ToolTipHeader("Lesser-or-equal test")
    @Syntax("Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipBody("Yields TRUE if %1$s is smaller than or equal to %2$s")
    @InfixSymbol(symbol = "<=", kind = COMPARE)
    public default BOOL le(MAIN arg0, MAIN arg1) {
        throw Exceptions.unsupportedOp("Operation %s not implemented for sort %s", "le", getSort());
    }

    /** If-then-else construct for reals. */
    @Syntax("Q%s.LPAR.test.COMMA.expr1.COMMA.expr2.RPAR")
    @ToolTipHeader("If-then-else")
    @ToolTipBody("If %1$s is TRUE, returns %2$s, otherwise %3$s")
    public default MAIN ite(BOOL arg0, MAIN arg1, MAIN arg2) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "ite", getSort());
    }

    /** Greater-than comparison. */
    @ToolTipHeader("Greater-than test")
    @Syntax("Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipBody("Yields TRUE if %1$s is properly larger than %2$s")
    @InfixSymbol(symbol = ">", kind = COMPARE)
    public default BOOL gt(MAIN arg0, MAIN arg1) {
        throw Exceptions.unsupportedOp("Operation %s not implemented for sort %s", "gt", getSort());
    }

    /** Greater-or-equal comparison. */
    @ToolTipHeader("Greater-or-equal test")
    @Syntax("Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipBody("Yields TRUE if %1$s is larger than or equal to %2$s")
    @InfixSymbol(symbol = ">=", kind = COMPARE)
    public default BOOL ge(MAIN arg0, MAIN arg1) {
        throw Exceptions.unsupportedOp("Operation %s not implemented for sort %s", "ge", getSort());
    }

    /** Equality test. */
    @ToolTipHeader("Equality test")
    @Syntax("Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipBody("Yields TRUE if %1$s equals %2%s")
    @InfixSymbol(symbol = "==", kind = EQUAL)
    public default BOOL eq(MAIN arg0, MAIN arg1) {
        throw Exceptions.unsupportedOp("Operation %s not implemented for sort %s", "eq", getSort());
    }

    /** Inequality test. */
    @ToolTipHeader("Inequality test")
    @Syntax("Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipBody("Yields TRUE if %1$s does not equal real number %2$s")
    @InfixSymbol(symbol = "!=", kind = EQUAL)
    public default BOOL neq(MAIN arg0, MAIN arg1) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "neq", getSort());
    }

    /** Inversion. */
    @ToolTipHeader("Inversion")
    @Syntax("Q%s.LPAR.expr.RPAR")
    @ToolTipBody("Yields the inverse of %s")
    @PrefixSymbol(symbol = "-", kind = UNARY)
    public default MAIN neg(MAIN arg) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "neg", getSort());
    }

    /** String representation. */
    @ToolTipHeader("Conversion to STRING")
    @Syntax("Q%s.LPAR.expr.RPAR")
    @ToolTipBody("Yields a string representation of %s")
    public default STRING toString(MAIN arg) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "toString", getSort());
    }

    /** Integer cast. */
    @ToolTipHeader("Real-to-integer cast")
    @Syntax("Q%s.LPAR.expr.RPAR")
    @ToolTipBody("Casts %s to its whole part")
    @PrefixSymbol(symbol = "(int)", kind = UNARY)
    public default INT toInt(MAIN arg) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "toInt", getSort());
    }
}
