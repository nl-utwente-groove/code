/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.algebra.Algebra;
import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.algebra.Operation;
import nl.utwente.groove.algebra.Operator;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.util.AIGenerated;

/**
 * Applies every (non-user) algebra operator generically in every algebra
 * family, and checks that the result is a value of the operator's result
 * algebra. For the TERM family this in particular exercises the term
 * constructions of the four term algebras, which the inherited
 * {@code AlgebraTest} battery never invokes.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class OperationTest {
    /** Applies every non-user operator in every family. */
    @Test
    public void testAllOperations() {
        for (AlgebraFamily family : AlgebraFamily.values()) {
            int applied = 0;
            for (Operator op : Operator.getOps()) {
                if (involvesUserSort(op)) {
                    continue;
                }
                Operation operation = family.getOperation(op);
                assertEquals(op, operation.getOperator());
                Object result = operation.applyStrict(sampleArgs(family, op));
                Algebra<?> resultAlgebra = family.getAlgebra(op.getResultSort());
                assertTrue(resultAlgebra.isValue(result),
                           String.format("%s of %s yields %s, not a value of %s", op, family,
                                         result, resultAlgebra.getName()));
                if (family == AlgebraFamily.TERM && !resultAlgebra.isErrorValue(result)) {
                    // term-algebra results are expressions of the result sort
                    assertTrue(result instanceof Expression);
                    assertEquals(op.getResultSort(), ((Expression) result).getSort());
                }
                applied++;
            }
            // guard against the loop silently skipping everything
            assertTrue(applied > 40, family + " applied only " + applied + " operators");
        }
    }

    /** Tests if an operator involves the user-defined sort, which has no
     * algebra outside the DEFAULT family. */
    private boolean involvesUserSort(Operator op) {
        return op.getDeclaringSort() == Sort.USER || op.getResultSort() == Sort.USER
            || op.getParamSorts().contains(Sort.USER);
    }

    /** Builds a sample argument list for an operator in a given family. */
    private List<Object> sampleArgs(AlgebraFamily family, Operator op) {
        List<Object> result = new ArrayList<>();
        for (Sort sort : op.getParamSorts()) {
            result.add(sampleValue(family, sort));
        }
        if (op.isVarArgs()) {
            // a var-args operation takes a single collection argument
            Object single = result.get(0);
            assertNotNull(single);
            return List.of(List.of(single, single));
        }
        return result;
    }

    /** Returns a sample value of a given sort in a given family. */
    private Object sampleValue(AlgebraFamily family, Sort sort) {
        Object javaValue = switch (sort) {
        case BOOL -> Boolean.TRUE;
        case INT -> 2;
        case REAL -> 1.5;
        case STRING -> "1";
        default -> throw new IllegalStateException("unexpected sort " + sort);
        };
        return family.getAlgebra(sort).toValueFromJava(javaValue);
    }
}
