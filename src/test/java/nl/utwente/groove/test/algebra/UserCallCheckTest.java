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
import nl.utwente.groove.test.algebra.user.UserContainer;
import nl.utwente.groove.test.algebra.user.UserTypePoly;
import nl.utwente.groove.test.algebra.user.UserTypeQueue;
import nl.utwente.groove.test.algebra.user.UserTypeStack;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the check of a call of a user operator against the Java classes of its
 * implementation, at the time the call is parsed (gh #933): all user types have the
 * same sort, but where the class of an argument is statically known, a call that
 * the implementation cannot be invoked with is rejected rather than left to yield
 * an error value. For an operator of a user interface, the class of the receiver
 * determines the implementing method, and so what is required of the other arguments.
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

    /** A call is accepted if the receiver implements the operator and the implementation
     * for its class takes the classes of the other arguments.
     */
    @Test
    @AIGenerated("Claude Opus 5.5, 2026-11")
    public void testAccepted() throws FormatException {
        assertEquals(2, eval("next(UserTypeStack(\"s\",1,2))"));
        assertEquals(1, eval("next(UserTypeQueue(\"q\",1,2))"));
        assertEquals("c", eval("name(UserTypeCounter(\"c\",5))"));
        assertEquals("s", eval("name(UserTypeStack(\"s\",1,2))"));
        assertEquals(5, eval("count(UserTypeCounter(\"c\",5))"));
        // the generated accessors of a sequence-valued field of a user interface
        assertEquals(2, eval("itemsSize(UserTypeStack(\"s\",1,2))"));
        assertEquals(2, eval("itemsAt(UserTypeQueue(\"q\",1,2),1)"));
        // a default method of a user interface
        assertEquals(true, eval("isEmpty(UserTypeQueue(\"q\"))"));
        assertEquals(false, eval("isEmpty(UserTypeStack(\"s\",1))"));
        // the other argument has the class that the implementation for the receiver takes
        assertEquals(true, eval("sameItems(UserTypeStack(\"s\",1),UserTypeStack(\"t\",1))"));
        assertEquals(false, eval("sameItems(UserTypeQueue(\"q\",1),UserTypeQueue(\"r\"))"));
        // a static operation, with a tail of user values
        assertEquals(3, eval("countCoeffs(UserTypePoly(\"p\",1),UserTypePoly(\"q\",2,3))"));
        assertEquals(0, eval("countCoeffs()"));
    }

    /** A call is rejected if the class of the receiver does not implement the operator. */
    @Test
    @AIGenerated("Claude Opus 5.5, 2026-11")
    public void testRejectedReceiver() {
        assertRejected("next(UserTypeCounter(\"c\",5))",
                       "User operation 'next' is not implemented for a value of type UserTypeCounter, "
                           + "only for " + CONTAINER);
        // an operation of a user type rather than a user interface
        assertRejected("count(UserTypeStack(\"s\"))",
                       "User operation 'count' is not implemented for a value of type UserTypeStack, "
                           + "only for UserTypeCounter");
        // a generated accessor
        assertRejected("itemsAt(UserTypeCounter(\"c\",5),0)",
                       "User operation 'itemsAt' is not implemented for a value of type UserTypeCounter, "
                           + "only for " + CONTAINER);
        // the receiver is a constructor call rather than a constant
        assertRejected("next(UserTypeCounter(n,5))",
                       "User operation 'next' is not implemented for a value of type UserTypeCounter, "
                           + "only for " + CONTAINER);
        // a type prefix does not change the error
        assertRejected("user:next(UserTypeCounter(\"c\",5))",
                       "User operation 'next' is not implemented for a value of type UserTypeCounter, "
                           + "only for " + CONTAINER);
    }

    /** A call is rejected if the class of another argument is not the one that the
     * implementation takes at that position.
     */
    @Test
    @AIGenerated("Claude Opus 5.5, 2026-11")
    public void testRejectedArgument() {
        assertRejected("sameItems(UserTypeStack(\"s\",1),UserTypeQueue(\"q\",1))",
                       "Parameter 1 of user operation 'sameItems' should have type UserTypeStack "
                           + "rather than UserTypeQueue, as implemented for UserTypeStack");
        assertRejected("sameItems(UserTypeQueue(\"q\",1),UserTypeCounter(\"c\",1))",
                       "Parameter 1 of user operation 'sameItems' should have type UserTypeQueue "
                           + "rather than UserTypeCounter, as implemented for UserTypeQueue");
        // with a receiver of unknown class, the argument has to be of the interface
        assertRejected("sameItems(x,UserTypeCounter(\"c\",1))",
                       "Parameter 1 of user operation 'sameItems' should have type " + CONTAINER
                           + " rather than UserTypeCounter");
        // a static operation, where every position in the tail has the element class
        assertRejected("countCoeffs(UserTypeStack(\"s\"))",
                       "Parameter 0 of user operation 'countCoeffs' should have type UserTypePoly "
                           + "rather than UserTypeStack");
        assertRejected("countCoeffs(UserTypePoly(\"p\",1),x,UserTypeCounter(\"c\",1))",
                       "Parameter 2 of user operation 'countCoeffs' should have type UserTypePoly "
                           + "rather than UserTypeCounter");
    }

    /** The class of a call is that of the outcome of the implementation for its receiver,
     * which is the user interface if the class of the receiver is not known.
     */
    @Test
    @AIGenerated("Claude Opus 5.5, 2026-11")
    public void testInferredClass() throws FormatException {
        assertSame(UserTypeStack.class, getUserClass("UserTypeStack(\"s\",1)"));
        assertSame(UserTypeStack.class, getUserClass("UserTypeStack(n,1)"));
        assertSame(UserTypeStack.class, getUserClass("push(UserTypeStack(\"s\"),1)"));
        assertSame(UserTypeQueue.class, getUserClass("push(push(UserTypeQueue(\"q\"),1),2)"));
        assertSame(UserTypePoly.class, getUserClass("append(x,1)"));
        assertSame(UserContainer.class, getUserClass("push(x,1)"));
        assertNull(getUserClass("x"));
        assertNull(getUserClass("next(UserTypeStack(\"s\",1))"));

        assertEquals(2, eval("next(push(UserTypeStack(\"s\",1),2))"));
        assertEquals(1, eval("next(push(UserTypeQueue(\"q\",1),2))"));
        assertEquals(true,
                     eval("sameItems(push(UserTypeStack(\"s\"),1),push(UserTypeStack(\"t\"),1))"));
        assertRejected("count(push(UserTypeStack(\"s\"),1))",
                       "User operation 'count' is not implemented for a value of type UserTypeStack, "
                           + "only for UserTypeCounter");
        assertRejected("sameItems(push(UserTypeStack(\"s\"),1),UserTypeQueue(\"q\"))",
                       "Parameter 1 of user operation 'sameItems' should have type UserTypeStack "
                           + "rather than UserTypeQueue, as implemented for UserTypeStack");
        // the outcome of push is a container, which is no counter, whatever x
        assertRejected("count(push(x,1))",
                       "User operation 'count' is not implemented for a value of type UserContainer, "
                           + "only for UserTypeCounter");
        // but it is a named value
        parse("name(push(x,1))");
        // the only implementation determines the class, whatever its receiver
        assertRejected("next(append(x,1))",
                       "User operation 'next' is not implemented for a value of type UserTypePoly, "
                           + "only for " + CONTAINER);
        // an inner call is checked like any other
        assertRejected("degree(append(UserTypeCounter(\"c\",1),1))",
                       "User operation 'append' is not implemented for a value of type UserTypeCounter, "
                           + "only for UserTypePoly");
    }

    /** A call is never rejected for an argument whose class is not known:
     * a variable or a field; nor for one whose class is a user interface
     * that the required class implements.
     */
    @Test
    @AIGenerated("Claude Opus 5.5, 2026-11")
    public void testUnknownClass() throws FormatException {
        parse("next(x)");
        parse("count(x)");
        parse("itemsSize(x)");
        parse("sameItems(x,y)");
        parse("sameItems(UserTypeStack(\"s\"),y)");
        parse("countCoeffs(x,y)");
        // a variable receiver leaves the interface, which the argument implements
        parse("sameItems(x,UserTypeQueue(\"q\"))");
        // a field, whose sort is derived from the operator
        parse("next(f)");
        parse("next(self.f)");
        // the outcome of push is a stack or a queue, depending on x
        parse("next(push(x,1))");
        parse("sameItems(push(x,1),UserTypeQueue(\"q\"))");
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

    /** Description of the user interface {@link UserContainer} in messages. */
    static private final String CONTAINER
        = "UserContainer (implemented by UserTypeStack, UserTypeQueue)";

    static private final QualName PACKAGE_NAME = QualName.name("nl.utwente.groove.test.algebra.user");
}
