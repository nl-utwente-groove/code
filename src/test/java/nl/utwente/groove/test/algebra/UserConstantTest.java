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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.algebra.Constant;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.UserAlgebra;
import nl.utwente.groove.algebra.UserSignature;
import nl.utwente.groove.algebra.syntax.CallExpr;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.algebra.syntax.Variable;
import nl.utwente.groove.annotation.StringSeq;
import nl.utwente.groove.test.algebra.user.UserTypeString;
import nl.utwente.groove.test.algebra.user.UserTypeTags;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the recognition and the printing of constants of user types.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Opus 5.5, 2026-11")
public class UserConstantTest {
    /** Loads the user classes of this test. */
    @BeforeEach
    public void setUp() {
        UserSignature
            .setUserClass(Stream
                .of("UserTypeIntBool", "UserTypeString", "UserTypeTags")
                .map(PACKAGE_NAME::extend)
                .toList());
    }

    /** Unloads the user classes, so that other tests start without them. */
    @AfterEach
    public void tearDown() {
        UserSignature.setUserClass(Collections.emptyList());
    }

    /**
     * A call is a user constant if its operator is a constructor and its arguments are constants.
     * The parser folds such a call into a {@link Constant}, so the call is taken from the
     * constant or constructed directly.
     */
    @Test
    public void testIsUserConstant() throws FormatException {
        // a constructor with constant arguments
        var constant = assertInstanceOf(Constant.class,
                                        Expression.parse("UserTypeIntBool(1,true)").toExpression());
        assertTrue(constant.getUserRepr().isUserConstant());
        var constructor = UserSignature.getOperators().get("UserTypeIntBool");
        assertNotNull(constructor);
        assertTrue(new CallExpr(constructor, Constant.instance(1), Constant.instance(true))
            .isUserConstant());
        // a constructor with an argument that is not a constant
        assertFalse(new CallExpr(constructor, new Variable("x", Sort.INT), Constant.instance(true))
            .isUserConstant());
        // an operation that is not a constructor, with constant arguments
        var call = assertInstanceOf(CallExpr.class,
                                    Expression.parse("getInt(UserTypeIntBool(1,true))").toExpression());
        assertInstanceOf(Constant.class, call.getArgs().get(0));
        assertFalse(call.isUserConstant());
    }

    /**
     * The strings in the symbol of a user value are quoted and escaped like string constants,
     * so that the symbol is read back as the same value.
     */
    @Test
    public void testStringComponents() throws FormatException {
        // without quotes and backslashes, the symbol is as it always was
        checkSymbol("UserTypeString(\"ab\")", new UserTypeString("ab"));
        checkSymbol("UserTypeString(\"a\\\"b\")", new UserTypeString("a\"b"));
        checkSymbol("UserTypeString(\"a\\\\b\")", new UserTypeString("a\\b"));
        checkSymbol("UserTypeString(\"a\\\\\\\"b\")", new UserTypeString("a\\\"b"));
        checkSymbol("UserTypeString(\"ab\\\\\")", new UserTypeString("ab\\"));
        checkSymbol("UserTypeString(\"a\\\\nb\")", new UserTypeString("a\\nb"));
        // the elements of a string sequence
        checkSymbol("UserTypeTags(\"a\\\"b\",\"c\\\\\",\"d\")",
                    new UserTypeTags(StringSeq.of("a\"b", "c\\", "d")));
    }

    /** Tests that a value prints as a given symbol, and that the symbol is read back as the value. */
    private void checkSymbol(String symbol, Object value) throws FormatException {
        assertEquals(symbol, UserAlgebra.instance.toSymbol(value));
        assertEquals(value, AlgebraFamily.DEFAULT.toValue(Sort.USER.createConstant(symbol)));
    }

    /**
     * Symbols printed before strings were escaped hold a backslash unescaped; where it is
     * followed by neither a quote nor a backslash, it is still read back literally.
     */
    @Test
    public void testUnescapedBackslash() throws FormatException {
        assertEquals(new UserTypeString("a\\b"),
                     AlgebraFamily.DEFAULT
                         .toValue(Sort.USER.createConstant("UserTypeString(\"a\\b\")")));
        assertEquals(new UserTypeString("a\\nb"),
                     AlgebraFamily.DEFAULT
                         .toValue(Sort.USER.createConstant("UserTypeString(\"a\\nb\")")));
    }

    static private final QualName PACKAGE_NAME = QualName.name("nl.utwente.groove.test.algebra.user");
}
