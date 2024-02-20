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
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import nl.utwente.groove.algebra.Operator;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.UserSignature;
import nl.utwente.groove.util.parse.FormatException;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
public class UserSignatureTest {

    /** Tests the stand-alone working of UserSignature. */
    @Test
    public void test() {
        loadFail("UserOperationsErr1");
        loadFail("UserOperationsErr2");
        loadFail("UserOperationsErr3");
        load("UserOperations");
    }

    private void loadFail(String className) {
        try {
            UserSignature.loadUserClass(PACKAGE_NAME + className);
            fail(className + " contains errors and should not be loadable");
        } catch (FormatException exc) {
            assertFalse("Failed to load " + className,
                        exc.getMessage().contains("cannot be loaded"));
        }
    }

    private void load(String className) {
        UserSignature.setUserClass(PACKAGE_NAME + className);
        assertNotNull(UserSignature.getUserClass());
        var ops = UserSignature.getOperators();
        assertEquals(5, ops.size());
        for (var op : ops) {
            switch (op.getName()) {
            case "randomInt" -> check(op, true, Sort.INT, Sort.INT);
            case "sqrt" -> check(op, false, Sort.REAL, Sort.REAL);
            case "one" -> check(op, false, Sort.INT);
            case "charAt" -> check(op, false, Sort.STRING, Sort.STRING, Sort.INT);
            case "isPrefix" -> check(op, false, Sort.BOOL, Sort.STRING, Sort.STRING);
            default -> fail("Unexpected operation " + op.getName());
            }
        }
    }

    private void check(Operator op, boolean indeterminate, Sort returnSort, Sort... parSorts) {
        assertEquals(indeterminate, op.isIndeterminate());
        assertEquals(returnSort, op.getResultType());
        assertArrayEquals(parSorts, op.getParamTypes().toArray(new Sort[0]));
    }

    static private final String PACKAGE_NAME = "nl.utwente.groove.test.algebra.";
}
