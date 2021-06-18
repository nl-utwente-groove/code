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

import groove.annotation.InfixSymbol;
import groove.annotation.Syntax;
import groove.annotation.ToolTipBody;
import groove.annotation.ToolTipHeader;
import groove.annotation.ToolTipPars;

/**
 * Signature for string algebras.
 * @param <INT> The representation type of the integer algebra
 * @param <REAL> The representation type of the real algebra
 * @param <BOOL> The representation type of the boolean algebra
 * @param <STRING> The representation type of the string algebra
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class StringSignature<INT,REAL,BOOL,STRING> implements Signature {
    /** String concatenation. */
    @Syntax("Q%s.LPAR.s1.COMMA.s2.RPAR")
    @ToolTipHeader("String concatenation")
    @ToolTipBody("Returns a string consisting of %s followed by %s")
    @ToolTipPars({"First string parameter", "Second string parameter"})
    @InfixSymbol(symbol = "+", kind = ADD)
    public abstract STRING concat(STRING arg0, STRING arg1);

    /** Test if a string represents a boolean value. */
    @ToolTipHeader("Test whether a string represents a boolean")
    @Syntax("Q%s.LPAR.s.RPAR")
    @ToolTipBody("Yields TRUE if string %s equals either (precisely) \"true\" or \"false\"")
    public abstract BOOL isBool(STRING arg0);

    /** Test if a string represents an integer number. */
    @ToolTipHeader("Test whether a string represents an integer")
    @Syntax("Q%s.LPAR.s.RPAR")
    @ToolTipBody("Yields TRUE if string %s represents an integer number")
    public abstract BOOL isInt(STRING arg0);

    /** Test if a string represents a real number. */
    @ToolTipHeader("Test whether a string represents a real number")
    @Syntax("Q%s.LPAR.s.RPAR")
    @ToolTipBody("Yields TRUE if string %s represents a real number")
    public abstract BOOL isReal(STRING arg0);

    /** Converts a string into a boolean. */
    @ToolTipHeader("String-to-boolean conversion")
    @Syntax("Q%s.LPAR.s.RPAR")
    @ToolTipBody("Yields TRUE if %s equals (precisely) \"true\"")
    public abstract BOOL toBool(STRING arg0);

    /** Converts a string into an integer. */
    @ToolTipHeader("String-to-integer conversion")
    @Syntax("Q%s.LPAR.s.RPAR")
    @ToolTipBody("Yields the integer value represented by %s, or 0 if %1$s does not represent an integer")
    public abstract INT toInt(STRING arg0);

    /** Converts a string into a real number. */
    @ToolTipHeader("String-to-real conversion")
    @Syntax("Q%s.LPAR.s.RPAR")
    @ToolTipBody("Yields the real number represented by %s, or 0.0 if %1$s does not represent a real")
    public abstract REAL toReal(STRING arg0);

    /** Lesser-than comparison. */
    @ToolTipHeader("String lesser-than test")
    @Syntax("Q%s.LPAR.s1.COMMA.s2.RPAR")
    @ToolTipBody("Yields TRUE if string %s is a proper prefix of string %s")
    @InfixSymbol(symbol = "<", kind = COMPARE)
    public abstract BOOL lt(STRING arg0, STRING arg1);

    /** Lesser-or-equal comparison. */
    @ToolTipHeader("String lesser-or-equal test")
    @Syntax("Q%s.LPAR.s1.COMMA.s2.RPAR")
    @ToolTipBody("Yields TRUE if string %s is a prefix of (or equal to) string %s")
    @InfixSymbol(symbol = "<=", kind = COMPARE)
    public abstract BOOL le(STRING arg0, STRING arg1);

    /** If-then-else construct for strings. */
    @Syntax("Q%s.LPAR.b.COMMA.s1.COMMA.s2.RPAR")
    @ToolTipHeader("If-then-else for strings")
    @ToolTipBody("If %s is true, returns %s, otherwise %s")
    public abstract STRING ite(BOOL arg0, STRING arg1, STRING arg2);

    /** Greater-than comparison. */
    @ToolTipHeader("String greater-than test")
    @Syntax("Q%s.LPAR.s1.COMMA.s2.RPAR")
    @ToolTipBody("Yields TRUE if string %2$s is a proper prefix of string %1$s")
    @InfixSymbol(symbol = ">", kind = COMPARE)
    public abstract BOOL gt(STRING arg0, STRING arg1);

    /** Greater-or-equal comparison. */
    @ToolTipHeader("String greater-or-equals test")
    @Syntax("Q%s.LPAR.s1.COMMA.s2.RPAR")
    @ToolTipBody("Yields TRUE if string %2$s is a prefix of (or equal to) string %1$s")
    @InfixSymbol(symbol = ">=", kind = COMPARE)
    public abstract BOOL ge(STRING arg0, STRING arg1);

    /** Equality test. */
    @ToolTipHeader("String equality test")
    @Syntax("Q%s.LPAR.s1.COMMA.s2.RPAR")
    @ToolTipBody("Yields TRUE if string %s equals string %s")
    @InfixSymbol(symbol = "==", kind = EQUAL)
    public abstract BOOL eq(STRING arg0, STRING arg1);

    /** Inequality test. */
    @ToolTipHeader("String equality test")
    @Syntax("Q%s.LPAR.s1.COMMA.s2.RPAR")
    @ToolTipBody("Yields TRUE if string %s does not equal string %s")
    @InfixSymbol(symbol = "!=", kind = EQUAL)
    public abstract BOOL neq(STRING arg0, STRING arg1);

    /** Size function. */
    @ToolTipHeader("Length function")
    @Syntax("Q%s.LPAR.s.RPAR")
    @ToolTipBody("Yields the number of characters in string %s")
    public abstract INT length(STRING arg);

    /** Substring function. */
    @ToolTipHeader("Substring function")
    @Syntax("Q%s.LPAR.s.COMMA.i1.COMMA.i2.RPAR")
    @ToolTipBody("Returns the substring of %s from position %s up to (but not including) position %s, counting from 0;"
        + " defaults to empty if %2$s or $%3$s are out of range or %2$s>%3$s")
    public abstract STRING substring(STRING arg0, INT arg1, INT arg2);

    /** Suffix function. */
    @ToolTipHeader("Suffix function")
    @Syntax("Q%s.LPAR.s.COMMA.i1.RPAR")
    @ToolTipBody("Returns the suffix of %s from position %s (up to the end), counting from 0;"
        + " defaults to empty if %2$s is out of range")
    public abstract STRING suffix(STRING arg0, INT arg1);

    /** Substring matching function. */
    @ToolTipHeader("Substring matching function")
    @Syntax("Q%s.LPAR.s.COMMA.i1.COMMA.i2.RPAR")
    @ToolTipBody("Returns the index of the first occurrence of %2$s in %1$s, or -1 if there is no such occurrence")
    public abstract INT lookup(STRING arg0, STRING arg1);

    @Override
    public Sort getSort() {
        return Sort.STRING;
    }

    /** String constant for the empty string. */
    public static final Constant EMPTY = Constant.instance("");

    /** Enumeration of all operators defined in this signature. */
    public enum Op implements Signature.OpValue {
        /** Value for {@link StringSignature#concat(Object, Object)}. */
        CONCAT,
        /** Value for {@link StringSignature#isBool(Object)}. */
        IS_BOOL,
        /** Value for {@link StringSignature#isInt(Object)}. */
        IS_INT,
        /** Value for {@link StringSignature#isReal(Object)}. */
        IS_REAL,
        /** Value for {@link StringSignature#toBool(Object)}. */
        TO_BOOL,
        /** Value for {@link StringSignature#toInt(Object)}. */
        TO_INT,
        /** Value for {@link StringSignature#toReal(Object)}. */
        TO_REAL,
        /** Value for {@link StringSignature#eq(Object, Object)}. */
        EQ,
        /** Value for {@link StringSignature#ge(Object, Object)}. */
        GE,
        /** Value for {@link StringSignature#gt(Object, Object)}. */
        GT,
        /** Value for {@link StringSignature#ite(Object, Object, Object)}. */
        ITE,
        /** Value for {@link StringSignature#le(Object, Object)}. */
        LE,
        /** Value for {@link StringSignature#lt(Object, Object)}. */
        LT,
        /** Value for {@link StringSignature#neq(Object, Object)}. */
        NEQ,
        /** Value for {@link StringSignature#length(Object)}. */
        LENGTH,
        /** Value for {@link StringSignature#substring(Object, Object, Object)}. */
        SUBSTRING,
        /** Value for {@link StringSignature#suffix(Object, Object)}. */
        SUFFIX,
        /** Value for {@link StringSignature#lookup(Object, Object)}. */
        LOOKUP,;

        @Override
        public Operator getOperator() {
            if (this.operator == null) {
                this.operator = Operator.newInstance(Sort.STRING, this);
            }
            return this.operator;
        }

        /** Corresponding operator object. */
        private Operator operator;
    }
}
