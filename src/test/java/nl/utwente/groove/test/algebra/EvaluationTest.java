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
package nl.utwente.groove.test.algebra;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.algebra.ErrorValue;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the evaluation of expressions
 * @author Arend Rensink
 * @version $Revision$
 */
public class EvaluationTest {
    /** Tests the evaluation of a number of expressions, for all algebra families. */
    @Test
    public void testEvaluation() {
        testEvaluation(AlgebraFamily.DEFAULT);
        testEvaluation(AlgebraFamily.BIG);
    }

    private void testEvaluation(AlgebraFamily family) {
        this.family = family;
        testEquals("2", "1+1");
        testEquals("5", "bigmax(-1,3,0,5)");
        testError("1/0");
    }

    private void testEquals(String outcome, String term) {
        assertEquals(eval(parse(outcome)), eval(parse(term)));
    }

    private void testError(String term) {
        assertTrue(eval(parse(term)) instanceof ErrorValue);
    }

    private Expression parse(String term) {
        try {
            return Expression.parse(term).toExpression();
        } catch (FormatException exc) {
            fail(exc.getMessage());
            return null;
        }
    }

    private Object eval(Expression expr) {
        return this.family.toValueFoldError(expr);
    }

    private AlgebraFamily family;
}
