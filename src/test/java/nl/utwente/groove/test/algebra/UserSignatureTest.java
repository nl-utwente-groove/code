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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.algebra.Operator;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.UserSignature;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.util.parse.FormatException;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
public class UserSignatureTest {
    /** Resets the user signature, so that the test classes loaded here
     * do not leak into tests that run later in the same JVM.
     */
    @After
    public void resetUserSignature() {
        UserSignature.setUserClass(Collections.emptyList());
    }

    /** Tests that caches derived from the user signature (in particular the
     * {@link AlgebraFamily} operation maps) are refreshed when a new user class is loaded.
     * Regression test for the non-deterministic failure of RuleApplicationTest.testUserOps.
     */
    @Test
    public void testUserClassReload() {
        load("UserOps", "UserTypeIntString");
        var one = UserSignature.getOperators().get("one");
        assertNotNull(one);
        // force computation of the (lazily created) user operation map
        var operation = AlgebraFamily.DEFAULT.getOperation(one);
        assertEquals(1, operation.applyStrict(List.of()));
        // now unload the user classes: the operation map must be recomputed
        UserSignature.setUserClass(Collections.emptyList());
        assertNull(UserSignature.getOperators().get("one"));
        assertNull(AlgebraFamily.DEFAULT.getOperation(one));
        // and reloading must make the operation available again
        load("UserOps", "UserTypeIntString");
        assertEquals(1,
                     AlgebraFamily.DEFAULT
                         .getOperation(UserSignature.getOperators().get("one"))
                         .applyStrict(List.of()));
    }

    /** Tests that the statically cached operator tables in {@link Operator} and
     * {@link Sort} are refreshed when a new user class is loaded.
     * Regression test for the residual non-deterministic failure of
     * RuleApplicationTest.testUserOps.
     */
    @Test
    public void testOperatorTableReload() {
        load("UserOps", "UserTypeIntString");
        var one = UserSignature.getOperators().get("one");
        assertNotNull(one);
        // force computation of the (lazily created) static operator tables
        assertTrue(Operator.getOps().contains(one));
        assertSame(one, Operator.getOp(Sort.USER, "one"));
        assertSame(one, Sort.USER.getOperator("one"));
        // now unload the user classes: the tables must be recomputed
        UserSignature.setUserClass(Collections.emptyList());
        assertFalse(Operator.getOps().contains(one));
        assertNull(Operator.getOp(Sort.USER, "one"));
        assertNull(Sort.USER.getOperator("one"));
        // and reloading must make the operators available again
        load("UserOps", "UserTypeIntString");
        var one2 = UserSignature.getOperators().get("one");
        assertNotNull(one2);
        assertTrue(Operator.getOps().contains(one2));
        assertSame(one2, Operator.getOp(Sort.USER, "one"));
        assertSame(one2, Sort.USER.getOperator("one"));
    }

    /** Tests the stand-alone working of UserSignature. */
    @Test
    public void test() {
        loadFail("UserOpsNonAccessibleMethod");
        loadFail("UserOpsNonPublicClass");
        loadFail("UserOpsNonStaticMethod");
        loadFail("UserOpsUnknownParType");
        loadFail("UserOpsUnknownReturnType");
        loadFail("UserOpsVoidReturnType");
        loadFail("UserTypeNonRecord");
        loadFail("UserTypeNonSort");
        loadFail("UserTypeIntString", "UserOpsDuplicateMethod");
        loadFail("UserOps");
        load("UserOps", "UserTypeIntString");
        var ops = UserSignature.getOperators();
        assertEquals(9, ops.size());
        for (var op : ops.values()) {
            switch (op.getName()) {
            case "randomInt" -> check(op, true, Sort.INT, Sort.INT);
            case "sqrt" -> check(op, false, Sort.REAL, Sort.REAL);
            case "one" -> check(op, false, Sort.INT);
            case "get" -> check(op, false, Sort.USER, Sort.INT, Sort.STRING);
            case "UserTypeIntString" -> check(op, false, Sort.USER, Sort.INT, Sort.STRING);
            case "intField" -> check(op, false, Sort.INT, Sort.USER);
            case "stringField" -> check(op, false, Sort.STRING, Sort.USER);
            case "charAt" -> check(op, false, Sort.STRING, Sort.USER);
            case "isPrefixOf" -> check(op, false, Sort.BOOL, Sort.USER, Sort.STRING);
            default -> fail("Unexpected operation " + op.getName());
            }
        }
    }

    private void loadFail(String... classNames) {
        var qualClassNames = Arrays.stream(classNames).map(PACKAGE_NAME::extend).toList();
        try {
            UserSignature.checkUserClass(qualClassNames);
            fail(classNames + " contain errors and should not be loadable");
        } catch (FormatException exc) {
            assertFalse("Failed to load " + classNames,
                        exc.getMessage().contains("cannot be loaded"));
        }
    }

    private void load(String... classNames) {
        var qualClassNames = Arrays.stream(classNames).map(PACKAGE_NAME::extend).toList();
        try {
            UserSignature.checkUserClass(qualClassNames);
        } catch (FormatException exc) {
            fail(exc.getMessage());
        }
        UserSignature.setUserClass(qualClassNames);
    }

    private void check(Operator op, boolean indeterminate, Sort returnSort, Sort... parSorts) {
        assertEquals(indeterminate, op.isIndeterminate());
        assertEquals(returnSort, op.getResultSort());
        assertArrayEquals(parSorts, op.getParamSorts().toArray(new Sort[0]));
    }

    static private final QualName PACKAGE_NAME = QualName.name("nl.utwente.groove.test.algebra");
}
