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
package groove.test.algebra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import groove.algebra.Operator;
import groove.algebra.Signature.OpValue;
import groove.algebra.Sort;
import groove.algebra.syntax.ExprTreeParser;
import groove.algebra.syntax.Expression;
import groove.algebra.syntax.FieldExpr;
import groove.util.Exceptions;
import groove.util.parse.FormatException;

/** Tests the parsing abilities of the {@link Expression} class. */
public class ExpressionTest {
    /** Test constant representations. */
    @Test
    public void testConstants() {
        // strings
        Expression empty = parse("\"\"");
        assertFalse(empty.equals(parse("\"a\"")));
        assertEquals(empty, parse("string:\"\""));
        parseFail("\"");
        assertTrue(parse("string:a") instanceof FieldExpr);
        // reals
        Expression oneReal = parse("1.");
        assertFalse(oneReal.equals(parse(".1")));
        assertEquals(oneReal, parse("real:1."));
        parse("real:-1.");
        parseFail(".");
        parseFail(".1.");
        parseFail("real:1");
        // integers
        Expression one = parse("1");
        assertFalse(one.equals(parse("1.")));
        assertFalse(one.equals(parse("\"1\"")));
        parse("int:-1");
        parseFail("1 1");
        parseFail("int:1.");
        // booleans
        Expression tru = parse("true");
        assertEquals(tru, parse("bool:true"));
        assertFalse(tru.equals(parse("false")));
        assertTrue(parse("bool:tru") instanceof FieldExpr);
    }

    /** Tests all operators. */
    @Test
    public void testOperators() {
        testOperators(Sort.INT);
        testOperators(Sort.REAL);
        testOperators(Sort.STRING);
        testOperators(Sort.BOOL);

        assertEquals(parse("x == 2"), parse("x=2", true));
    }

    /** Tests more complex expressions. */
    @Test
    public void testComplex() {
        parseFail("self.x-max(p1.speed,p2.speed)");
        parse("int:self.x-max(p1.speed,p2.speed)");
    }

    private void testOperators(Sort sig) {
        for (OpValue opValue : sig.getOpValues()) {
            Operator op = opValue.getOperator();
            String call = op.getFullName() + "(";
            Expression result = null;
            List<String> args = new ArrayList<>();
            for (int i = 0; i <= 2; i++) {
                if (i == op.getArity()) {
                    result = parse(call + ")");
                } else {
                    parseFail(call + ")");
                }
                if (i > 0) {
                    call = call + ",";
                }
                String arg;
                Sort type = i < op.getArity() ? op.getParamTypes()
                    .get(i) : Sort.STRING;
                switch (type) {
                case BOOL:
                    arg = this.boolOperands[i];
                    break;
                case INT:
                    arg = this.intOperands[i];
                    break;
                case REAL:
                    arg = this.realOperands[i];
                    break;
                case STRING:
                    arg = this.stringOperands[i];
                    break;
                default:
                    throw Exceptions.UNREACHABLE;
                }
                args.add(arg);
                call = call + arg;
            }
            String symbol = op.getSymbol();
            if (symbol != null) {
                switch (op.getKind()
                    .getPlace()) {
                case INFIX:
                    assertEquals(result, parse(args.get(0) + symbol + args.get(1)));
                    break;
                case POSTFIX:
                    assertEquals(result, parse(args.get(0) + symbol));
                    break;
                case PREFIX:
                    assertEquals(result, parse(symbol + args.get(0)));
                    break;
                }
            }
        }
    }

    /** Tests the associativity rules. */
    @Test
    public void testExprs() {
        Expression ex1 = parse("1+2-3*4");
        assertEquals(ex1, parse("(1+2)-(3*4)"));
        assertEquals(ex1, parse("sub(1+2,mul(3,4))"));
        assertFalse(ex1.equals(parse("(1+2-3)*4")));

        Expression ex2 = parse("! true & false");
        assertEquals(ex2, parse("(!true) & false"));
        assertFalse(ex2.equals(parse("!(true & false)")));

        Expression ex3 = parse("false | true & false");
        assertEquals(ex3, parse("false | (true & false)"));
        assertFalse(ex3.equals(parse("(false | true) & false")));

        Expression ex4 = parse("1 == 2+3");
        assertEquals(ex4, parse("1 == (2+3)"));
        parseFail("(1==2) + 3");

        Expression ex5 = parse("2<3 == true");
        assertEquals(ex5, parse("(2<3) == true"));
        parseFail("2 < (3==true)");

        Expression ex6 = parse("-3+5");
        assertEquals(ex6, parse("(-3)+5"));
        assertFalse(ex6.equals(parse("-(3+5)")));
    }

    private Expression parse(String expr) {
        return parse(expr, false);
    }

    private Expression parse(String expr, boolean test) {
        Expression result = null;
        try {
            result = (test ? ExprTreeParser.TEST_PARSER : ExprTreeParser.EXPR_PARSER).parse(expr)
                .toExpression();
        } catch (FormatException e) {
            fail(String.format("Expression %s should have been parsable but fails with %s",
                expr,
                e.getMessage()));
        }
        if (result != null) {
            String display = result.toDisplayString();
            try {
                assertEquals(result, Expression.parse(display));
            } catch (FormatException e) {
                // the display string didn't have enough typing information; proceed
            }
        }
        return result;
    }

    private void parseFail(String expr) {
        try {
            Expression result = Expression.parse(expr);
            fail(String.format("Expression %s should not have been parsable but yields %s",
                expr,
                result.toDisplayString()));
        } catch (FormatException e) {
            // this is the desired outcome
        }
    }

    private final String[] intOperands = {"1", "2", "3"};
    private final String[] realOperands = {"real:1.", "2.", "3."};
    private final String[] stringOperands = {"\"1\"", "\"2\"", "\"3\""};
    private final String[] boolOperands = {"true", "false", "true"};
}
