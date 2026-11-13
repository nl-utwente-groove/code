/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.test.algebra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.UserSignature;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.algebra.syntax.SortMap;
import nl.utwente.groove.test.algebra.user.UserTypePoly;
import nl.utwente.groove.test.algebra.user.UserTypeQueue;
import nl.utwente.groove.test.algebra.user.UserTypeStack;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the check of a call of a user operator against the Java classes of its
 * implementations, at the time the call is parsed (gh #933): all user types have the
 * same sort, but where the class of an argument is statically known, a call that
 * no implementation can be invoked with is rejected rather than left to yield
 * an error value.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.2, 2026-11")
public class UserCallCheckTest {
    /** Loads the user classes of this test. */
    @BeforeEach
    public void setUp() {
        UserSignature
            .setUserClass(Stream
                .of("UserTypeStack", "UserTypeQueue", "UserTypeCounter", "UserTypePoly",
                    "UserOpsSeq")
                .map(PACKAGE_NAME::extend)
                .toList());
    }

    /** Unloads the user classes, so that other tests start without them. */
    @AfterEach
    public void tearDown() {
        UserSignature.setUserClass(Collections.emptyList());
    }

    /** A call is accepted if the operator is implemented for the class of the receiver
     * and that implementation takes the classes of the other arguments.
     */
    @Test
    public void testAccepted() throws FormatException {
        assertEquals(2, eval("next(UserTypeStack(\"s\",1,2))"));
        assertEquals(1, eval("next(UserTypeQueue(\"q\",1,2))"));
        assertEquals("c", eval("name(UserTypeCounter(\"c\",5))"));
        assertEquals(5, eval("count(UserTypeCounter(\"c\",5))"));
        // a generated accessor next to a declared operation of the same name
        assertEquals(2, eval("itemsSize(UserTypeStack(\"s\",1,2))"));
        assertEquals(5, eval("itemsSize(UserTypeCounter(\"c\",5))"));
        assertEquals(2, eval("itemsAt(UserTypeQueue(\"q\",1,2),1)"));
        // the other argument has the class that the implementation for the receiver takes
        assertEquals(true, eval("sameItems(UserTypeStack(\"s\",1),UserTypeStack(\"t\",1))"));
        assertEquals(false, eval("sameItems(UserTypeQueue(\"q\",1),UserTypeQueue(\"r\"))"));
        // a static operation, with a tail of user values
        assertEquals(3, eval("countCoeffs(UserTypePoly(\"p\",1),UserTypePoly(\"q\",2,3))"));
        assertEquals(0, eval("countCoeffs()"));
    }

    /** A call is rejected if the operator has no implementation for the class of the receiver. */
    @Test
    public void testRejectedReceiver() {
        assertRejected("next(UserTypeCounter(\"c\",5))",
                       "User operation 'next' is not implemented for a value of type UserTypeCounter, "
                           + "only for UserTypeStack, UserTypeQueue");
        // also if there is only one implementation
        assertRejected("count(UserTypeStack(\"s\"))",
                       "User operation 'count' is not implemented for a value of type UserTypeStack, "
                           + "only for UserTypeCounter");
        // a generated accessor
        assertRejected("itemsAt(UserTypeCounter(\"c\",5),0)",
                       "User operation 'itemsAt' is not implemented for a value of type UserTypeCounter, "
                           + "only for UserTypeStack, UserTypeQueue");
        // the receiver is a constructor call rather than a constant
        assertRejected("next(UserTypeCounter(n,5))",
                       "User operation 'next' is not implemented for a value of type UserTypeCounter, "
                           + "only for UserTypeStack, UserTypeQueue");
        // a type prefix does not change the error
        assertRejected("user:next(UserTypeCounter(\"c\",5))",
                       "User operation 'next' is not implemented for a value of type UserTypeCounter, "
                           + "only for UserTypeStack, UserTypeQueue");
    }

    /** A call is rejected if the class of another argument is not the one that the
     * implementation takes at that position.
     */
    @Test
    public void testRejectedArgument() {
        assertRejected("sameItems(UserTypeStack(\"s\",1),UserTypeQueue(\"q\",1))",
                       "Parameter 1 of user operation 'sameItems' should have type UserTypeStack "
                           + "rather than UserTypeQueue, as implemented for UserTypeStack");
        assertRejected("sameItems(UserTypeQueue(\"q\",1),UserTypeCounter(\"c\",1))",
                       "Parameter 1 of user operation 'sameItems' should have type UserTypeQueue "
                           + "rather than UserTypeCounter, as implemented for UserTypeQueue");
        // with a receiver of unknown class, the argument is rejected if no implementation takes it
        assertRejected("sameItems(x,UserTypeCounter(\"c\",1))",
                       "Parameter 1 of user operation 'sameItems' should have type UserTypeStack "
                           + "or UserTypeQueue rather than UserTypeCounter");
        // a static operation, where every position in the tail has the element class
        assertRejected("countCoeffs(UserTypeStack(\"s\"))",
                       "Parameter 0 of user operation 'countCoeffs' should have type UserTypePoly "
                           + "rather than UserTypeStack");
        assertRejected("countCoeffs(UserTypePoly(\"p\",1),x,UserTypeCounter(\"c\",1))",
                       "Parameter 2 of user operation 'countCoeffs' should have type UserTypePoly "
                           + "rather than UserTypeCounter");
    }

    /** The class of a call is that of the outcome of the implementation for its receiver,
     * and so is known if the class of the receiver is; without that, it is only known
     * if all implementations agree.
     */
    @Test
    public void testInferredClass() throws FormatException {
        assertSame(UserTypeStack.class, getUserClass("UserTypeStack(\"s\",1)"));
        assertSame(UserTypeStack.class, getUserClass("UserTypeStack(n,1)"));
        assertSame(UserTypeStack.class, getUserClass("push(UserTypeStack(\"s\"),1)"));
        assertSame(UserTypeQueue.class, getUserClass("push(push(UserTypeQueue(\"q\"),1),2)"));
        assertSame(UserTypePoly.class, getUserClass("append(x,1)"));
        assertNull(getUserClass("push(x,1)"));
        assertNull(getUserClass("x"));
        assertNull(getUserClass("next(UserTypeStack(\"s\",1))"));

        assertEquals(2, eval("next(push(UserTypeStack(\"s\",1),2))"));
        assertEquals(1, eval("next(push(UserTypeQueue(\"q\",1),2))"));
        assertEquals(true,
                     eval("sameItems(push(UserTypeStack(\"s\"),1),push(UserTypeStack(\"t\"),1))"));
        assertRejected("count(push(UserTypeStack(\"s\"),1))",
                       "User operation 'count' is not implemented for a value of type UserTypeStack, "
                           + "only for UserTypeCounter");
        assertRejected("sameItems(push(UserTypeStack(\"s\"),1),push(UserTypeQueue(\"q\"),1))",
                       "Parameter 1 of user operation 'sameItems' should have type UserTypeStack "
                           + "rather than UserTypeQueue, as implemented for UserTypeStack");
        // the only implementation determines the class, whatever its receiver
        assertRejected("next(append(x,1))",
                       "User operation 'next' is not implemented for a value of type UserTypePoly, "
                           + "only for UserTypeStack, UserTypeQueue");
        // an inner call is checked like any other
        assertRejected("degree(append(UserTypeCounter(\"c\",1),1))",
                       "User operation 'append' is not implemented for a value of type UserTypeCounter, "
                           + "only for UserTypePoly");
    }

    /** A call is never rejected for an argument whose class is not known:
     * a variable, a field, or a call of an operator whose implementations differ in their outcome.
     */
    @Test
    public void testUnknownClass() throws FormatException {
        parse("next(x)");
        parse("count(x)");
        parse("itemsSize(x)");
        parse("sameItems(x,y)");
        parse("sameItems(UserTypeStack(\"s\"),y)");
        parse("countCoeffs(x,y)");
        // a variable receiver leaves both implementations, one of which takes the argument
        parse("sameItems(x,UserTypeQueue(\"q\"))");
        // a field, whose sort is derived from the operator
        parse("next(f)");
        parse("next(self.f)");
        // the outcome of push is a stack or a queue, depending on x
        parse("next(push(x,1))");
        parse("sameItems(push(x,1),UserTypeQueue(\"q\"))");
        // also if no implementation exists for either
        parse("count(push(x,1))");
    }

    /** Tests that a given expression is rejected with a given message. */
    private void assertRejected(String text, String message) {
        var exc = assertThrows(FormatException.class, () -> parse(text), text);
        assertEquals(message, exc.getMessage());
    }

    /** Returns the statically known class of the values of a given expression, if any. */
    private @Nullable Class<?> getUserClass(String text) throws FormatException {
        return UserSignature.getUserClass(parse(text));
    }

    /** Parses and evaluates a closed expression in the default algebra family. */
    private Object eval(String text) throws FormatException {
        return AlgebraFamily.DEFAULT.toValue(parse(text));
    }

    /** Parses an expression in which {@code x} and {@code y} are user-sorted variables
     * and {@code n} is a string variable.
     */
    private Expression parse(String text) throws FormatException {
        var sortMap = SortMap.newInstance();
        sortMap.add("x", Sort.USER);
        sortMap.add("y", Sort.USER);
        sortMap.add("n", Sort.STRING);
        return Expression.parse(text).toExpression(sortMap);
    }

    static private final QualName PACKAGE_NAME = QualName.name("nl.utwente.groove.test.algebra.user");
}
