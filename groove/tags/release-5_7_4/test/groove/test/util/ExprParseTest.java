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

import org.junit.Test;

import groove.util.parse.AExprTree;
import groove.util.parse.AExprTreeParser;
import groove.util.parse.Op;
import groove.util.parse.OpKind;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
@SuppressWarnings("javadoc")
public class ExprParseTest {
    @Test
    public void backAndForth() {
        roundtrip("ge(a.b,c)");
        // extension of predefined operator
        roundtrip("gee");
        roundtrip("-true");
        roundtrip("0-true");
        roundtrip("a+b+c");
        roundtrip("0");
        roundtrip("1234567890123456789");
        roundtrip("0.0");
        roundtrip("\"\"");
        roundtrip("\"\\\"'a\"");
        roundtrip("a+b+c");
    }

    @Test
    public void normalisation() {
        //        parseToEqual("bool:true", "true");
        //        parseToEqual("int: - 10", "-10");
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
        // malformed constants
        parseError("'a");
        parseError("0a");
        parseError("0.1.");
        //        parseError("string:0");
        //        parseError("string:-true");
        //        parseError("int:<");
        //        parseError("int:noop()");
        // arity errors
        parseError("ge()");
        parseError("ge(1)");
        parseError("ge(1,2,3)");
        // no sort prefixes
        parseError("int:0");
        //        parseError("a:");
        //        parseError("a:b");
        //        parseError("int:");
        //        parseError(":b");
        parseError("a.");
        parseError(".b");
        parseError("-");
        parseError("(a)b");
        parseError("+1");
        parseError("1+");
        parseError("(1");
        parseError("1)");
        // associativity error
        parseError("1=2=3");
        // unrecognised symbols
        parseError(";");
        parseError("/");
        // wrong use of operator
        parseError("ge+ge");
        parseError("ge()");
        parseError("ge(2)");
    }

    /** Asserts that parsing a string and converting at back results in the same string. */
    private void roundtrip(String text) {
        MyTree expr = parse(text);
        assertEquals(text, expr.toLine()
            .toFlatString());
    }

    private void parseToEqual(String one, String two) {
        assertEquals(parse(one), parse(two));
    }

    private MyTree parse(String text) {
        MyTree result = parser.parse(text);
        assertFalse(result.hasErrors());
        if (DEBUG) {
            System.out.printf("Tree representation of %s:%n", result.toString());
            System.out.println(result.toTreeString());
        }
        return result;
    }

    private void parseError(String text) {
        assertTrue(parser.parse(text)
            .hasErrors());
    }

    /** Main method: prints all its arguments and writes the result to stdout. */
    public static void main(String[] args) {
        for (String arg : args) {
            MyTree expr = parser.parse(arg);
            String error = expr.hasErrors() ? " with errors " + expr.getErrors()
                .toString() : "";
            System.out.printf("%s parsed to %s%s%n -> %s", arg, expr, error, expr.toLine()
                .toFlatString());
        }
    }

    static AExprTreeParser<MyOp,MyTree> parser =
        new AExprTreeParser<MyOp,MyTree>(new MyTree(MyOp.ATOM)) {
            // empty
        };

    static {
        parser.setQualIds(true);
    }

    private static final boolean DEBUG = false;

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
        ATOM(OpKind.ATOM, 0),
        GE(OpKind.CALL, "ge", 2),;

        private MyOp(OpKind kind, int arity) {
            this(kind, "", arity);
        }

        private MyOp(OpKind kind, String symbol) {
            this(kind, symbol, kind.getArity());
        }

        private MyOp(OpKind kind, String symbol, int arity) {
            this.kind = kind;
            this.symbol = symbol;
            this.arity = arity;
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

        @Override
        public int getArity() {
            return this.arity;
        }

        private final int arity;
    }

    public static class MyTree extends AExprTree<MyOp,MyTree> {
        /**
         * Constructs a tree with a given top-level operator.
         */
        public MyTree(MyOp op) {
            super(op);
        }

        @Override
        public MyTree createTree(MyOp op) {
            return new MyTree(op);
        }
    }
}
