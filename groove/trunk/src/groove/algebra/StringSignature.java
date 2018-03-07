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
 * @param <STRING> The representation type of the string algebra
 * @param <BOOL> The representation type of the boolean algebra
 * @param <INT> The representation type of the integer algebra
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class StringSignature<STRING,BOOL,INT> implements Signature {
    /** String concatenation. */
    @Syntax("Q%s.LPAR.s1.COMMA.s2.RPAR")
    @ToolTipHeader("String concatenation")
    @ToolTipBody("Returns a string consisting of %s followed by %s")
    @ToolTipPars({"First string parameter", "Second string parameter"})
    @InfixSymbol(symbol = "+", kind = ADD)
    public abstract STRING concat(STRING arg0, STRING arg1);

    /** Lesser-than comparison. */
    @ToolTipHeader("String lesser-than test")
    @Syntax("Q%s.LPAR.s1.COMMA.s2.RPAR")
    @ToolTipBody("Yields TRUE if string %s is a proper prefix of string %s")
    @InfixSymbol(symbol = "<", kind = COMPARE)
    public abstract BOOL lt(STRING arg0, STRING arg1);

    /** Lesser-or-equal comparison. */
    @ToolTipHeader("String lesser-or-equal test")
    @Syntax("Q%s.LPAR.s1.COMMA.s2.RPAR")
    @ToolTipBody("Yields TRUE if string %s is a prefix of string %s")
    @InfixSymbol(symbol = "<=", kind = COMPARE)
    public abstract BOOL le(STRING arg0, STRING arg1);

    /** Greater-than comparison. */
    @ToolTipHeader("String greater-than test")
    @Syntax("Q%s.LPAR.s1.COMMA.s2.RPAR")
    @ToolTipBody("Yields TRUE if string %2$s is a prefix of string %1$s")
    @InfixSymbol(symbol = ">", kind = COMPARE)
    public abstract BOOL gt(STRING arg0, STRING arg1);

    /** Greater-or-equal comparison. */
    @ToolTipHeader("String greater-or-equals test")
    @Syntax("Q%s.LPAR.s1.COMMA.s2.RPAR")
    @ToolTipBody("Yields TRUE if string %2$s is a prefix of string %1$s")
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
        /** Value for {@link StringSignature#eq(Object, Object)}. */
        EQ,
        /** Value for {@link StringSignature#ge(Object, Object)}. */
        GE,
        /** Value for {@link StringSignature#gt(Object, Object)}. */
        GT,
        /** Value for {@link StringSignature#le(Object, Object)}. */
        LE,
        /** Value for {@link StringSignature#lt(Object, Object)}. */
        LT,
        /** Value for {@link StringSignature#neq(Object, Object)}. */
        LENGTH,
        /** Value for {@link StringSignature#lt(Object, Object)}. */
        NEQ,;

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
