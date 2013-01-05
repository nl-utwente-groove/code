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

import static groove.algebra.Precedence.ADD;
import static groove.algebra.Precedence.COMPARE;
import static groove.algebra.Precedence.EQUAL;
import groove.annotation.InfixSymbol;
import groove.annotation.Syntax;
import groove.annotation.ToolTipBody;
import groove.annotation.ToolTipHeader;
import groove.annotation.ToolTipPars;
import groove.grammar.model.FormatException;
import groove.util.ExprParser;

/**
 * Signature for strings in graph grammars.
 * @author Arend Rensink
 * @version $Revision $
 */
@SuppressWarnings("hiding")
public abstract class StringSignature<String,Bool,Int> implements Signature {
    /** String concatenation. */
    @Syntax("Q%s.LPAR.s1.COMMA.s2.RPAR")
    @ToolTipHeader("String concatenation")
    @ToolTipBody("Returns a string consisting of %s followed by %s")
    @ToolTipPars({"First string parameter", "Second string parameter"})
    @InfixSymbol(symbol = "+", precedence = ADD)
    public abstract String concat(String arg0, String arg1);

    /** Lesser-than comparison. */
    @ToolTipHeader("String lesser-than test")
    @Syntax("Q%s.LPAR.s1.COMMA.s2.RPAR")
    @ToolTipBody("Yields TRUE if string %s is a proper prefix of string %s")
    @InfixSymbol(symbol = "<", precedence = COMPARE)
    public abstract Bool lt(String arg0, String arg1);

    /** Lesser-or-equal comparison. */
    @ToolTipHeader("String lesser-or-equal test")
    @Syntax("Q%s.LPAR.s1.COMMA.s2.RPAR")
    @ToolTipBody("Yields TRUE if string %s is a prefix of string %s")
    @InfixSymbol(symbol = "<=", precedence = COMPARE)
    public abstract Bool le(String arg0, String arg1);

    /** Greater-than comparison. */
    @ToolTipHeader("String greater-than test")
    @Syntax("Q%s.LPAR.s1.COMMA.s2.RPAR")
    @ToolTipBody("Yields TRUE if string %2$s is a prefix of string %1$s")
    @InfixSymbol(symbol = ">", precedence = COMPARE)
    public abstract Bool gt(String arg0, String arg1);

    /** Greater-or-equal comparison. */
    @ToolTipHeader("String greater-or-equals test")
    @Syntax("Q%s.LPAR.s1.COMMA.s2.RPAR")
    @ToolTipBody("Yields TRUE if string %2$s is a prefix of string %1$s")
    @InfixSymbol(symbol = ">=", precedence = COMPARE)
    public abstract Bool ge(String arg0, String arg1);

    /** Equality test. */
    @ToolTipHeader("String equality test")
    @Syntax("Q%s.LPAR.s1.COMMA.s2.RPAR")
    @ToolTipBody("Yields TRUE if string %s equals string %s")
    @InfixSymbol(symbol = "==", precedence = EQUAL)
    public abstract Bool eq(String arg0, String arg1);

    /** Inequality test. */
    @ToolTipHeader("String equality test")
    @Syntax("Q%s.LPAR.s1.COMMA.s2.RPAR")
    @ToolTipBody("Yields TRUE if string %s does not equal string %s")
    @InfixSymbol(symbol = "!=", precedence = EQUAL)
    public abstract Bool neq(String arg0, String arg1);

    /** Size function. */
    @ToolTipHeader("Length function")
    @Syntax("Q%s.LPAR.s.RPAR")
    @ToolTipBody("Yields the number of characters in string %s")
    public abstract Int length(String arg);

    /**
     * Tests if the string value is surrounded with double quotes.
     * @see ExprParser#toUnquoted(java.lang.String, char)
     */
    public final boolean isValue(java.lang.String value) {
        if (value.indexOf(ExprParser.DOUBLE_QUOTE_CHAR) != 0) {
            return false;
        }
        try {
            ExprParser.toUnquoted(value, ExprParser.DOUBLE_QUOTE_CHAR);
            return true;
        } catch (FormatException e) {
            return false;
        }
    }

    /**
     * Conversion of native Java representation of string constants to
     * the corresponding algebra values.
     * @throws IllegalArgumentException if the parameter is not of type {@link java.lang.String}
     */
    final public String getValueFromJava(Object constant) {
        if (!(constant instanceof java.lang.String)) {
            throw new IllegalArgumentException(java.lang.String.format(
                "Native int type is %s, not %s",
                java.lang.String.class.getSimpleName(),
                constant.getClass().getSimpleName()));
        }
        return toValue((java.lang.String) constant);
    }

    /** 
     * Callback method to convert from the native ({@link java.lang.String})
     * representation to the algebra representation.
     */
    protected abstract String toValue(java.lang.String constant);

    @Override
    public SignatureKind getKind() {
        return SignatureKind.STRING;
    }
}
