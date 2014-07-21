/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import groove.util.parse.Expr;
import groove.util.parse.ExprParser;
import groove.util.parse.Op;
import groove.util.parse.OpKind;

import org.junit.Test;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
@SuppressWarnings("javadoc")
public class ExprParseTest {
    @Test
    public void backAndForth() {
        roundtrip("prf:a(a.b,c(),prf:a.b)");
        roundtrip("-true");
        roundtrip("0-true");
        roundtrip("a+b+c");
        roundtrip("0");
        roundtrip("1234567890123456789");
        roundtrip("0.0");
        roundtrip("\"\\\"'a\"");
        roundtrip("a+b+c");
    }

    @Test
    public void normalisation() {
        parseToEqual("a+b+c", "((a)+b)+c");
        parseToEqual("a->b->c", "a->(b->(c))");
        parseToEqual("a+b*c", "a+(b*c)");
        parseToEqual("a*b+c", "(a*b)+c");
        parseToEqual("!a|b&c", "(!a)|(b&c)");
        parseToEqual("c+!a+b", "c+(!(a+b))");
        parseToEqual("0.0", ".0");
        parseToEqual("\"'a\"", "'\\'a'");
    }

    @Test
    public void errors() {
        parseError("a:");
        parseError(":b");
        parseError("a.");
        parseError(".b");
        parseError("-");
        parseError("(a)b");
        parseError("+1");
        parseError("1+");
        parseError("(1");
        parseError("1)");
        parseError("1=2=3");
    }

    /** Asserts that parsing a string and converting at back results in the same string. */
    private void roundtrip(String text) {
        Expr<?> expr = parse(text);
        assertEquals(text, expr.toLine().toFlatString());
    }

    private void parseToEqual(String one, String two) {
        assertEquals(parse(one), parse(two));
    }

    private Expr<?> parse(String text) {
        Expr<?> result = parser.parse(text);
        assertFalse(result.hasErrors());
        return result;
    }

    private void parseError(String text) {
        assertTrue(parser.parse(text).hasErrors());
    }

    /** Main method: prints all its arguments and writes the result to stdout. */
    public static void main(String[] args) {
        for (String arg : args) {
            Expr<?> expr = parser.parse(arg);
            String error = expr.hasErrors() ? " with errors " + expr.getErrors().toString() : "";
            System.out.printf("%s parsed to %s%s%n -> %s", arg, expr, error,
                expr.toLine().toFlatString());
        }
    }

    static ExprParser<MyOp> parser = new ExprParser<MyOp>(MyOp.ATOM, true, true, "Test value");

    private static enum MyOp implements Op {
        INVERT(OpKind.UNARY, "-"),
        TIMES(OpKind.MULT, "*"),
        MINUS(OpKind.ADD, "-"),
        PLUS(OpKind.ADD, "+"),
        OR(OpKind.OR, "|"),
        NOT(OpKind.NOT, "!"),
        AND(OpKind.AND, "&"),
        EQ(OpKind.EQUAL, "="),
        IMPL(OpKind.COMPARE, "->"),
        CALL(OpKind.CALL),
        ATOM(OpKind.ATOM), ;
        private MyOp(OpKind kind) {
            this(kind, null);
        }

        private MyOp(OpKind kind, String symbol) {
            this.kind = kind;
            this.symbol = symbol;
        }

        @Override
        public boolean hasSymbol() {
            return getSymbol() != null;
        }

        @Override
        public String getSymbol() {
            return this.symbol;
        }

        private final String symbol;

        @Override
        public OpKind getKind() {
            return this.kind;
        }

        private final OpKind kind;
    }
}
