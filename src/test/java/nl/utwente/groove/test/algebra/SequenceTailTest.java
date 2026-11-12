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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.algebra.ErrorValue;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.UserAlgebra;
import nl.utwente.groove.algebra.UserSignature;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.annotation.IntSeq;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the parsing, printing and evaluation of user operators with a variable
 * argument tail: constructors of user types whose last field is a sequence,
 * and user operations whose last parameter is one.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.2, 2026-11")
public class SequenceTailTest {
    /** Loads the user classes of this test. */
    @BeforeEach
    public void setUp() {
        UserSignature
            .setUserClass(Stream
                .of("UserTypePoly", "UserTypeBag", "UserOpsSeq", "UserTypeIntString")
                .map(PACKAGE_NAME::extend)
                .toList());
    }

    /** Unloads the user classes, so that other tests start without them. */
    @AfterEach
    public void tearDown() {
        UserSignature.setUserClass(Collections.emptyList());
    }

    /** A user constant lists the elements of its sequence as trailing arguments. */
    @Test
    public void testConstants() throws FormatException {
        checkConstant("UserTypePoly(\"p\",1,2,3)", new UserTypePoly("p", IntSeq.of(1, 2, 3)));
        checkConstant("UserTypePoly(\"p\")", new UserTypePoly("p", IntSeq.of()));
        checkConstant("UserTypeBag(4,4)", new UserTypeBag(IntSeq.of(4, 4)));
        checkConstant("UserTypeBag()", new UserTypeBag(IntSeq.of()));
    }

    /** Tests that a symbol and a value stand for one another. */
    private void checkConstant(String symbol, Object value) throws FormatException {
        assertEquals(value, AlgebraFamily.DEFAULT.toValue(Sort.USER.createConstant(symbol)));
        assertEquals(symbol, UserAlgebra.instance.toSymbol(value));
        var term = UserAlgebra.instance.toTerm(value);
        assertEquals(value, eval(term.toParseString()));
    }

    /** A constructor call with the wrong number or sorts of arguments is not a constant. */
    @Test
    public void testConstantErrors() {
        // the fixed part is missing
        assertNoConstant("UserTypePoly()");
        // the fixed part and the tail each have their own sort
        assertNoConstant("UserTypePoly(1,2)");
        assertNoConstant("UserTypePoly(\"p\",1,true)");
        // without a tail, too many constant arguments used to overrun the argument array
        assertNoConstant("UserTypeIntString(1,\"a\",3)");
        assertThrows(FormatException.class, () -> Expression.parse("UserTypeIntString(1,\"a\",3)"));
    }

    /** Tests that a given symbol is rejected as a user constant. */
    private void assertNoConstant(String symbol) {
        assertThrows(FormatException.class, () -> Sort.USER.createConstant(symbol));
    }

    /** The tail arguments of a call reach the operation as one sequence, for every sequence type. */
    @Test
    public void testEvaluation() throws FormatException {
        // 1 + 2*2 + 3*2*2
        assertEquals(17, eval("horner(2,1,2,3)"));
        assertEquals(0, eval("horner(2)"));
        assertEquals(4.0, eval("total(1.5,2.5)"));
        assertEquals(0.0, eval("total()"));
        assertEquals(false, eval("all(true,false)"));
        assertEquals(true, eval("all()"));
        assertEquals("a-b-c", eval("join(\"-\",\"a\",\"b\",\"c\")"));
        assertEquals("", eval("join(\"-\")"));
        assertEquals(3, eval("countCoeffs(UserTypePoly(\"p\",1),UserTypePoly(\"q\",2,3))"));
        assertEquals(0, eval("countCoeffs()"));
        // the tail arguments may be expressions themselves: 1 + 0*2 + 2*4 + 3*8
        assertEquals(33, eval("horner(1+1,1,horner(2),1+1,3)"));
    }

    /** An operation of a user type has the receiving object in front of its other arguments. */
    @Test
    public void testInstanceOperation() throws FormatException {
        assertEquals(2, eval("degree(UserTypePoly(\"p\",1,2,3))"));
        // the polynomial 1 + x at the points 0, 1 and 2
        assertEquals(6, eval("sumAt(UserTypePoly(\"p\",1,1),0,1,2)"));
        assertEquals(0, eval("sumAt(UserTypePoly(\"p\",1,1))"));
    }

    /** Arguments of the wrong sort are found when the expression is parsed. */
    @Test
    public void testCallErrors() {
        assertThrows(FormatException.class, () -> eval("horner()"));
        assertThrows(FormatException.class, () -> eval("horner(2,1,true)"));
        assertThrows(FormatException.class, () -> eval("join(\"-\",\"a\",1)"));
        assertThrows(FormatException.class, () -> eval("sumAt()"));
    }

    /**
     * All user types have the same sort, so a user value of another type than the elements of
     * the sequence is only found when the operation is applied; the outcome is an error value.
     */
    @Test
    public void testUserElementError() throws FormatException {
        assertInstanceOf(ErrorValue.class, eval("countCoeffs(UserTypeBag(1))"));
        assertInstanceOf(ErrorValue.class,
                         eval("countCoeffs(UserTypePoly(\"p\",1),UserTypeBag(1))"));
    }

    /** Parses and evaluates a closed expression in the default algebra family. */
    private Object eval(String text) throws FormatException {
        return AlgebraFamily.DEFAULT.toValue(Expression.parse(text).toExpression());
    }

    static private final QualName PACKAGE_NAME = QualName.name("nl.utwente.groove.test.algebra");
}
