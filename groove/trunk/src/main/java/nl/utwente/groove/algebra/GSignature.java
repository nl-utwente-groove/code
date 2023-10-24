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
    @Syntax("[sort:]Q%s.LPAR.expr.RPAR")
    @ToolTipHeader("Absolute value")
    @ToolTipBody("Yields the absolute value of %2$s.")
    public default MAIN abs(MAIN arg) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "abs", getSort());
    }

    /** Addition of two real numbers. */
    @Syntax("[sort:]Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipHeader("Addition")
    @ToolTipBody("Yields the sum of %2$s and %3$s.")
    @InfixSymbol(symbol = "+", kind = ADD)
    public default MAIN add(MAIN arg0, MAIN arg1) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "add", getSort());
    }

    /** Maximum of a nonempty set of numbers. */
    @Syntax("[sort:]Q%s.LPAR.expr.RPAR")
    @ToolTipHeader("Collective maximum")
    @ToolTipBody("Yields the maximum of all quantified values of %2$s.")
    public default MAIN bigmax(List<MAIN> arg) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "bigmax", getSort());
    }

    /** Minimum of a nonempty set of numbers. */
    @Syntax("[sort:]Q%s.LPAR.expr.RPAR")
    @ToolTipHeader("Collective minimum")
    @ToolTipBody("Yields the minimum of all quantified values of %2$s.")
    public default MAIN bigmin(List<MAIN> arg) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "bigmin", getSort());
    }

    /** Subtraction of two numbers. */
    @Syntax("[sort:]Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipHeader("Subtraction")
    @ToolTipBody("Yields the difference between %2$s and %3$s.")
    @InfixSymbol(symbol = "-", kind = ADD)
    public default MAIN sub(MAIN arg0, MAIN arg1) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "sub", getSort());
    }

    /** Multiplication of two real numbers. */
    @Syntax("[sort:]Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipHeader("Multiplication")
    @ToolTipBody("Yields the product of %2$s and %3$s.")
    @InfixSymbol(symbol = "*", kind = MULT)
    public default MAIN mul(MAIN arg0, MAIN arg1) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "mul", getSort());
    }

    /** Division of two real numbers. */
    @Syntax("[sort:]Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipHeader("Division")
    @ToolTipBody("Yields the quotient of %2$s and %3$s.")
    @InfixSymbol(symbol = "/", kind = MULT)
    public default MAIN div(MAIN arg0, MAIN arg1) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "div", getSort());
    }

    /** Minimum of two real numbers. */
    @Syntax("[sort:]Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipHeader("Minimum")
    @ToolTipBody("Yields the minimum of %2$s and %3$s.")
    public default MAIN min(MAIN arg0, MAIN arg1) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "min", getSort());
    }

    /** Maximum of two real numbers. */
    @Syntax("[sort:]Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipHeader("Maximum")
    @ToolTipBody("Yields the maximum of %2$s and %3$s.")
    public default MAIN max(MAIN arg0, MAIN arg1) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "max", getSort());
    }

    /** Product of a set of values. */
    @Syntax("[sort:]Q%s.LPAR.expr.RPAR")
    @ToolTipHeader("Product")
    @ToolTipBody("Yields the product of all quantified values of %2$s.")
    public default MAIN prod(List<MAIN> arg) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "prod", getSort());
    }

    /** Summation over a set of values. */
    @Syntax("[sort:]Q%s.LPAR.expr.RPAR")
    @ToolTipHeader("Summation")
    @ToolTipBody("Yields the sum of all quantified values of %2$s.")
    public default MAIN sum(List<MAIN> arg) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "sum", getSort());
    }

    /** Lesser-than comparison. */
    @ToolTipHeader("Lesser-than test")
    @Syntax("[sort:]Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipBody("Yields TRUE if %2$s is properly smaller than %3$s.")
    @InfixSymbol(symbol = "<", kind = COMPARE)
    public default BOOL lt(MAIN arg0, MAIN arg1) {
        throw Exceptions.unsupportedOp("Operation %s not implemented for sort %s", "lt", getSort());
    }

    /** Lesser-or-equal comparison. */
    @ToolTipHeader("Lesser-or-equal test")
    @Syntax("[sort:]Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipBody("Yields TRUE if %2$s is smaller than or equal to %3$s.")
    @InfixSymbol(symbol = "<=", kind = COMPARE)
    public default BOOL le(MAIN arg0, MAIN arg1) {
        throw Exceptions.unsupportedOp("Operation %s not implemented for sort %s", "le", getSort());
    }

    /** If-then-else construct for reals. */
    @Syntax("[sort:]Q%s.LPAR.test.COMMA.expr1.COMMA.expr2.RPAR")
    @ToolTipHeader("If-then-else")
    @ToolTipBody("If %2$s is TRUE, yields %3$s, otherwise %4$s.")
    public default MAIN ite(BOOL arg0, MAIN arg1, MAIN arg2) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "ite", getSort());
    }

    /** Greater-than comparison. */
    @ToolTipHeader("Greater-than test")
    @Syntax("[sort:]Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipBody("Yields TRUE if %2$s is properly larger than %3$s, FALSE otherwise.")
    @InfixSymbol(symbol = ">", kind = COMPARE)
    public default BOOL gt(MAIN arg0, MAIN arg1) {
        throw Exceptions.unsupportedOp("Operation %s not implemented for sort %s", "gt", getSort());
    }

    /** Greater-or-equal comparison. */
    @ToolTipHeader("Greater-or-equal test")
    @Syntax("[sort:]Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipBody("Yields TRUE if %2$s is larger than or equal to %3$s, FALSE otherwise.")
    @InfixSymbol(symbol = ">=", kind = COMPARE)
    public default BOOL ge(MAIN arg0, MAIN arg1) {
        throw Exceptions.unsupportedOp("Operation %s not implemented for sort %s", "ge", getSort());
    }

    /** Equality test. */
    @ToolTipHeader("Equality test")
    @Syntax("[sort:]Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipBody("Yields TRUE if %2$s equals %3$s, FALSE otherwise.")
    @InfixSymbol(symbol = "==", kind = EQUAL)
    public default BOOL eq(MAIN arg0, MAIN arg1) {
        throw Exceptions.unsupportedOp("Operation %s not implemented for sort %s", "eq", getSort());
    }

    /** Inequality test. */
    @ToolTipHeader("Inequality test")
    @Syntax("[sort:]Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipBody("Yields TRUE if %2$s does not equal %3$s, FALSE otherwise.")
    @InfixSymbol(symbol = "!=", kind = EQUAL)
    public default BOOL neq(MAIN arg0, MAIN arg1) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "neq", getSort());
    }

    /** Inversion. */
    @ToolTipHeader("Inversion")
    @Syntax("[sort:]Q%s.LPAR.expr.RPAR")
    @ToolTipBody("Yields the inverse of %2%s.")
    @PrefixSymbol(symbol = "-", kind = UNARY)
    public default MAIN neg(MAIN arg) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "neg", getSort());
    }

    /** String representation. */
    @ToolTipHeader("Conversion to STRING")
    @Syntax("[sort:]Q%s.LPAR.expr.RPAR")
    @ToolTipBody("Yields a string representation of %2$s.")
    public default STRING toString(MAIN arg) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "toString", getSort());
    }

    /** Integer cast. */
    @ToolTipHeader("Conversion to INT")
    @Syntax("[sort:]Q%s.LPAR.expr.RPAR")
    @ToolTipBody("Casts %2$s to its whole part if it is a REAL, or interprets it as an INT constant if it is a STRING.")
    @PrefixSymbol(symbol = "(int)", kind = UNARY)
    public default INT toInt(MAIN arg) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "toInt", getSort());
    }

    /** String concatenation. */
    @Syntax("[sort:]Q%s.LPAR.expr1.COMMA.expr2.RPAR")
    @ToolTipHeader("String concatenation")
    @ToolTipBody("Yields a string consisting of %2$s followed by %3$s.")
    @InfixSymbol(symbol = "+", kind = ADD)
    public default MAIN concat(MAIN arg0, MAIN arg1) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "concat", getSort());
    }

    /** Test if a string represents a boolean value. */
    @ToolTipHeader("Boolean representation test")
    @Syntax("[sort:]Q%s.LPAR.expr.RPAR")
    @ToolTipBody("Yields TRUE if %2$s equals either (precisely) \"true\" or \"false\", FALSE otherwise.")
    public default BOOL isBool(MAIN arg0) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "isBool", getSort());
    }

    /** Test if a string represents an integer number. */
    @ToolTipHeader("Integer representation test")
    @Syntax("[sort:]Q%s.LPAR.expr.RPAR")
    @ToolTipBody("Yields TRUE if %2$s represents an integer number, FALSE otherwise.")
    public default BOOL isInt(MAIN arg0) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "isInt", getSort());
    }

    /** Test if a string represents a real number. */
    @ToolTipHeader("Real number representation test")
    @Syntax("[sort:]Q%s.LPAR.expr.RPAR")
    @ToolTipBody("Yields TRUE if %2$s represents a real number, FALSE otherwise.")
    public default BOOL isReal(MAIN arg0) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "isReal", getSort());
    }

    /** Converts a string into a boolean. */
    @ToolTipHeader("Boolean conversion")
    @Syntax("[sort:]Q%s.LPAR.expr.RPAR")
    @ToolTipBody("Yields TRUE if %2$s equals (precisely) \"true\", FALSE otherwise.")
    public default BOOL toBool(MAIN arg0) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "toBool", getSort());
    }

    /** Converts a string into a real number. */
    @ToolTipHeader("Real number conversion")
    @Syntax("[sort:]Q%s.LPAR.expr.RPAR")
    @ToolTipBody("Yields the real number represented by %2$s, or 0.0 if %2$s does not represent a real number.")
    public default REAL toReal(MAIN arg0) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "toReal", getSort());
    }

    /** Size function. */
    @ToolTipHeader("Length")
    @Syntax("[sort:]Q%s.LPAR.expr.RPAR")
    @ToolTipBody("Yields the number of characters in %2$s.")
    public default INT length(MAIN arg) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "length", getSort());
    }

    /** Substring function. */
    @ToolTipHeader("Substring")
    @Syntax("[sort:]Q%s.LPAR.expr1.COMMA.expr2.COMMA.expr3.RPAR")
    @ToolTipBody("Yields the substring of %2$s from position %3$s up to (but not including) position %4$s, counting from 0;"
        + " defaults to empty if %3$s or $%4$s are out of range or %3$s>%4$s.")
    public default MAIN substring(MAIN arg0, INT arg1, INT arg2) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "substring", getSort());
    }

    /** Suffix function. */
    @ToolTipHeader("Suffix")
    @Syntax("[sort:]Q%s.LPAR.expr1.COMMA.expr1.RPAR")
    @ToolTipBody("Yields the suffix of %2$s from position %3$s (up to the end), counting from 0;"
        + " defaults to empty if %3$s is out of range.")
    public default MAIN suffix(MAIN arg0, INT arg1) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "suffix", getSort());
    }

    /** Substring matching function. */
    @ToolTipHeader("Substring matching")
    @Syntax("[sort:]Q%s.LPAR.expr1.COMMA.expr3.RPAR")
    @ToolTipBody("Yields the index of the first occurrence of %3$s in %2$s, or -1 if there is no such occurrence.")
    public default INT lookup(MAIN arg0, MAIN arg1) {
        throw Exceptions
            .unsupportedOp("Operation %s not implemented for sort %s", "lookup", getSort());
    }

}
