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
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.Test;

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
