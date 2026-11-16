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
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Test;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.algebra.Algebras;
import nl.utwente.groove.algebra.ErrorValue;
import nl.utwente.groove.algebra.Operator;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.UserSignature;
import nl.utwente.groove.annotation.HelpMap;
import nl.utwente.groove.annotation.IntSeq;
import nl.utwente.groove.annotation.ToolTipPars;
import nl.utwente.groove.test.algebra.user.UserContainer;
import nl.utwente.groove.test.algebra.user.UserNamed;
import nl.utwente.groove.test.algebra.user.UserTypeCounter;
import nl.utwente.groove.test.algebra.user.UserTypeIntString;
import nl.utwente.groove.test.algebra.user.UserTypeQueue;
import nl.utwente.groove.test.algebra.user.UserTypeStack;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
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

    /** Tests that a user operation with a collection as return type is rejected. */
    @Test
    @AIGenerated("Claude Opus 5.5, 2026-11")
    public void testCollectionResult() {
        checkErrors("UserOpsCollectionResult",
                    "values': Return type Set is a collection: a user operation yields a single value");
    }

    /** Tests that the operator documentation can be built for user operations,
     * with one documentation line per parameter of the syntax line. This includes
     * the receiving object of a non-static operation of a user type, which is
     * the first operand without being a Java parameter (gh #929).
     */
    @Test
    @AIGenerated("Claude Fable 5.1, 2026-11")
    public void testOpDocMap() {
        load("UserOps", "UserTypeIntString");
        var docMap = Algebras.getOpDocMap();
        for (var op : UserSignature.getOperators().values()) {
            var name = op.getName();
            if (name.equals("intField") || name.equals("stringField")) {
                // record accessors are not documented
                continue;
            }
            assertEquals(op.getArity(), getParDocs(docMap, name).size(),
                         "Documented parameters of " + name);
        }
    }

    /** Tests that the parameter documentation of a user operation takes its
     * lines from {@link ToolTipPars} as far as that provides them for the
     * declared parameters, and generates the others; in particular, building
     * the operator documentation does not fail on such an annotation.
     */
    @Test
    @AIGenerated("Claude Fable 5.1, 2026-11")
    public void testOpDocPars() {
        load("UserTypeDocumented");
        var docMap = Algebras.getOpDocMap();
        // non-static: the receiving object comes first and has a generated line
        checkParDocs(docMap, "scaledBy", GENERATED, "the factor to scale by");
        // as many lines as parameters
        checkParDocs(docMap, "addUp", "the first summand", "the second summand");
        // too few lines: the remaining parameters have a generated line
        checkParDocs(docMap, "isBetween", "the lower bound", GENERATED, GENERATED);
        // too many lines: the surplus is ignored
        checkParDocs(docMap, "nought");
    }

    /** Tests that the tool tip texts of a user operation are format strings over
     * its operands, and that a text that is not a valid format string is shown
     * as it stands rather than failing the operator documentation.
     */
    @Test
    @AIGenerated("Claude Fable 5.1, 2026-11")
    public void testOpDocFormat() {
        load("UserTypeDocumented");
        var docMap = Algebras.getOpDocMap();
        // valid format string: the operand positions have been filled in
        var plusTip = getTip(docMap, "plus");
        assertTrue(plusTip, plusTip.contains("Yields the sum of "));
        assertFalse(plusTip, plusTip.contains("$s"));
        // invalid format strings in header, body and parameter documentation
        var wholeTip = getTip(docMap, "whole");
        assertTrue(wholeTip, wholeTip.contains("Share of 100%"));
        assertTrue(wholeTip, wholeTip.contains("Yields %2$s, which does not exist."));
        checkParDocs(docMap, "whole", "the share, in %");
    }

    /** Tests fields and operations of the same name in two user types that implement
     * no user interface declaring that name: they are duplicates, for the accessor of
     * a record component as well as for an annotated method (gh #933); and loading
     * the classes regardless keeps the operation of the first class as a whole, rather
     * than replacing its method by that of the second (gh #934).
     */
    @Test
    @AIGenerated("Claude Opus 5.5, 2026-11")
    public void testSameNameWithoutInterface() {
        // each of the classes is correct on its own
        load("UserTypeIntString");
        load("UserTypeDuplicateOps");
        var first = PACKAGE_NAME.extend("UserTypeIntString");
        var second = PACKAGE_NAME.extend("UserTypeDuplicateOps");
        var names = List.of(first, second);
        checkMessages(names,
                      "Duplicate user operation 'intField' in '%s' and '%s'%s"
                          .formatted(first, second, NO_INTERFACE_HINT),
                      "Duplicate user operation 'charAt' in '%s' and '%s'%s"
                          .formatted(first, second, NO_INTERFACE_HINT));
        UserSignature.setUserClass(names);
        var intField = UserSignature.getOperators().get("intField");
        assertNotNull(intField);
        assertEquals(Sort.INT, intField.getResultSort());
        assertSame(UserTypeIntString.class, getImpl(intField).receiver());
        var charAt = UserSignature.getOperators().get("charAt");
        assertNotNull(charAt);
        assertSame(UserTypeIntString.class, getImpl(charAt).receiver());
    }

    /** Tests that user types share the names of their fields and operations by
     * implementing a user interface that declares them: each name is that of a single
     * operator of the interface, also if the interfaces are not among the listed
     * classes, and a sequence-valued field of an interface yields the generated
     * accessors rather than an operator of its own name (gh #933).
     */
    @Test
    @AIGenerated("Claude Opus 5.5, 2026-11")
    public void testInterfaceOperators() {
        load("UserTypeStack", "UserTypeQueue", "UserTypeCounter");
        var ops = UserSignature.getOperators();
        assertEquals(Set
            .of("name", "next", "push", "sameItems", "isEmpty", "itemsAt", "itemsSize", "count",
                "UserTypeStack", "UserTypeQueue", "UserTypeCounter"),
                     ops.keySet());
        // one operator per name, which is that of the interface
        for (var name : List
            .of("name", "next", "push", "sameItems", "isEmpty", "itemsAt", "itemsSize")) {
            assertEquals(1,
                         UserSignature
                             .getImplementations()
                             .keySet()
                             .stream()
                             .filter(op -> op.getName().equals(name))
                             .count(),
                         "Operators named " + name);
        }
        checkReceiver("name", UserNamed.class);
        for (var name : List.of("next", "push", "sameItems", "isEmpty", "itemsAt", "itemsSize")) {
            checkReceiver(name, UserContainer.class);
        }
        checkReceiver("count", UserTypeCounter.class);
        // a constructor has no receiver
        checkReceiver("UserTypeStack", null);
        // the sequence-valued field of the interface
        assertNull(ops.get("items"));
        var itemsAt = ops.get("itemsAt");
        assertNotNull(itemsAt);
        check(itemsAt, false, Sort.INT, Sort.USER, Sort.INT);
        assertEquals(Operator.SeqAccessor.AT, getImpl(itemsAt).accessor());
        assertSame(UserContainer.class, getImpl(itemsAt).executable().getDeclaringClass());
        var itemsSize = ops.get("itemsSize");
        assertNotNull(itemsSize);
        check(itemsSize, false, Sort.INT, Sort.USER);
        assertEquals(Operator.SeqAccessor.SIZE, getImpl(itemsSize).accessor());
        // the signatures of the operations of the interface
        var push = ops.get("push");
        assertNotNull(push);
        check(push, false, Sort.USER, Sort.USER, Sort.INT);
        var sameItems = ops.get("sameItems");
        assertNotNull(sameItems);
        check(sameItems, false, Sort.BOOL, Sort.USER, Sort.USER);
        var isEmpty = ops.get("isEmpty");
        assertNotNull(isEmpty);
        check(isEmpty, false, Sort.BOOL, Sort.USER);
        // the user types that implement the interfaces
        assertEquals(List.of(UserTypeStack.class, UserTypeQueue.class),
                     UserSignature.getUserTypes(UserContainer.class));
        assertEquals(List.of(UserTypeStack.class, UserTypeQueue.class, UserTypeCounter.class),
                     UserSignature.getUserTypes(UserNamed.class));

        // an operator is documented once, naming the user types that implement it
        var docMap = Algebras.getOpDocMap();
        var pushTip = getTip(docMap, "push");
        assertTrue(pushTip, pushTip.contains("UserContainer"));
        assertTrue(pushTip, pushTip.contains("UserTypeStack"));
        assertTrue(pushTip, pushTip.contains("UserTypeQueue"));
        // a default method is documented like an abstract one
        var isEmptyTip = getTip(docMap, "isEmpty");
        assertTrue(isEmptyTip, isEmptyTip.contains("UserTypeQueue"));
        assertFalse(isEmptyTip, isEmptyTip.contains("UserTypeCounter"));
    }

    /** Tests that applying an operator of a user interface invokes the implementation
     * of the user type of the first argument, and that it yields an error value
     * for a value of a type that does not implement the interface (gh #933).
     */
    @Test
    @AIGenerated("Claude Opus 5.5, 2026-11")
    public void testInterfaceDispatch() {
        load("UserTypeStack", "UserTypeQueue", "UserTypeCounter");
        var stack = new UserTypeStack("s", IntSeq.of(1, 2, 3));
        var queue = new UserTypeQueue("q", IntSeq.of(1, 2, 3));
        var counter = new UserTypeCounter("c", 5);
        assertEquals("s", apply("name", stack));
        assertEquals("q", apply("name", queue));
        assertEquals("c", apply("name", counter));
        assertEquals(5, apply("count", counter));
        // the same items, but another element next in line
        assertEquals(3, apply("next", stack));
        assertEquals(1, apply("next", queue));
        // the generated accessors of the sequence-valued field of the interface
        assertEquals(2, apply("itemsAt", stack, 1));
        assertEquals(1, apply("itemsAt", queue, 0));
        assertEquals(3, apply("itemsSize", stack));
        assertEquals(3, apply("itemsSize", queue));
        // a default method of the interface
        assertEquals(false, apply("isEmpty", stack));
        assertEquals(true, apply("isEmpty", new UserTypeQueue("e", IntSeq.of())));
        // a user value as result is of the type of the implementation
        assertEquals(stack.push(4), apply("push", stack, 4));
        assertEquals(queue.push(4), apply("push", queue, 4));
        // and so is a user value among the other arguments
        assertEquals(true, apply("sameItems", stack, new UserTypeStack("t", IntSeq.of(1, 2, 3))));
        assertEquals(false, apply("sameItems", queue, queue.push(4)));
        // the bridge method of the stack does not take a queue
        assertTrue(apply("sameItems", stack, queue) instanceof ErrorValue);
        // an operator has no outcome for a type that does not implement its interface
        checkError(apply("next", counter),
                   "User operation 'next' is not implemented for a value of type UserTypeCounter, "
                       + "only for UserContainer (implemented by UserTypeStack, UserTypeQueue)");
        checkError(apply("itemsSize", counter),
                   "User operation 'itemsSize' is not implemented for a value of type UserTypeCounter, "
                       + "only for UserContainer (implemented by UserTypeStack, UserTypeQueue)");
        checkError(apply("count", stack),
                   "User operation 'count' is not implemented for a value of type UserTypeStack, "
                       + "only for UserTypeCounter");
    }

    /** Tests a sequence of the values of a user interface as the last parameter
     * of a user operation: the arguments can be of different user types (gh #933).
     */
    @Test
    @AIGenerated("Claude Fable 5.2, 2026-11")
    public void testInterfaceSequenceTail() {
        // the user interface has to be among the user classes, listed or discovered
        loadFail("UserOpsInterfaceSeq");
        load("UserNamed", "UserContainer", "UserOpsInterfaceSeq");
        load("UserTypeStack", "UserTypeQueue", "UserTypeCounter", "UserOpsInterfaceSeq");
        var ops = UserSignature.getOperators();
        for (var name : List.of("names", "countItems")) {
            var op = ops.get(name);
            assertNotNull(name, op);
            check(op, false, name.equals("names")
                ? Sort.STRING
                : Sort.INT, Sort.USER);
            assertTrue(name, op.hasTail());
        }
        var stack = new UserTypeStack("s", IntSeq.of(1, 2, 3));
        var queue = new UserTypeQueue("q", IntSeq.of(1, 2));
        var counter = new UserTypeCounter("c", 5);
        assertEquals("c,s,q", apply("names", counter, stack, queue));
        assertEquals("", apply("names"));
        assertEquals(5, apply("countItems", stack, queue));
        assertEquals(0, apply("countItems"));
        // a counter is named, but is no container
        assertTrue(apply("countItems", stack, counter) instanceof ErrorValue);
        assertTrue(apply("names", "text") instanceof ErrorValue);
    }

    /** Tests the errors in sharing names through user interfaces (gh #933). */
    @Test
    @AIGenerated("Claude Opus 5.5, 2026-11")
    public void testInterfaceErrors() {
        var named = PACKAGE_NAME.extend("UserNamed");
        var container = PACKAGE_NAME.extend("UserContainer");
        var stack = PACKAGE_NAME.extend("UserTypeStack");
        // a static operation does not share its name, whatever the order of the classes
        var clash = PACKAGE_NAME.extend("UserOpsStaticClash");
        checkMessages(List.of(stack, clash),
                      "Duplicate user operation 'push' in '%s' and '%s'".formatted(container,
                                                                                   clash));
        checkMessages(List.of(clash, stack),
                      "Duplicate user operation 'push' in '%s' and '%s'".formatted(clash,
                                                                                   container));
        // an operation of its own with the name of a generated accessor of an interface
        load("UserTypeAccessorClash");
        var accessorClash = PACKAGE_NAME.extend("UserTypeAccessorClash");
        checkMessages(List.of(stack, accessorClash),
                      "Duplicate user operation 'itemsSize' in '%s' (generated for sequence-valued field 'items') and '%s'%s"
                          .formatted(container, accessorClash, NO_INTERFACE_HINT));
        // a user interface that is not an interface
        var notInterface = PACKAGE_NAME.extend("UserInterfaceNotInterface");
        checkMessages(List.of(notInterface),
                      "User interface '%s' is not an interface".formatted(notInterface));
        // an overload of the name of an interface operation, beside the implementation
        var overload = PACKAGE_NAME.extend("UserTypeNameOverload");
        checkMessages(List.of(overload),
                      "User operation '%s.name' has the name of operation '%s.name' but does not implement it"
                          .formatted(overload, named));
        // two methods that can both implement an interface operation
        var twice = PACKAGE_NAME.extend("UserTypeNameTwice");
        var comparable = PACKAGE_NAME.extend("UserComparable");
        checkMessages(List.of(twice),
                      "Duplicate user operation '%s.sameAs': operation '%s.sameAs' is implemented more than once"
                          .formatted(twice, comparable));
        // an implementation that differs in being indeterminate
        var indeterminate = PACKAGE_NAME.extend("UserTypeSignatureClash");
        checkMessages(List.of(indeterminate),
                      "User operation '%s.name' differs from operation '%s.name', which it implements, in being indeterminate"
                          .formatted(indeterminate, named));
        // unrelated user interfaces declaring the same operation, each correct on its own
        var titled = PACKAGE_NAME.extend("UserTitled");
        load("UserNamed");
        load("UserTitled");
        checkMessages(List.of(named, titled),
                      "Duplicate user operation 'name' in '%s' and '%s'".formatted(named, titled));
        // sequence-valued fields of an interface that are not sequences of a system sort
        var seqErrors = PACKAGE_NAME.extend("UserInterfaceSeqErrors");
        checkMessages(List.of(seqErrors),
                      "Field '%s.polys' is a sequence of user values, which is only allowed as an operation parameter"
                          .formatted(seqErrors),
                      "Type of field '%s.raw' is not a sequence of a system sort"
                          .formatted(seqErrors));
        // a non-static operation of an interface that is not a user interface
        var plain = PACKAGE_NAME.extend("UserOpsPlainInterface");
        checkMessages(List.of(plain), "User operation '%s.size' is not static".formatted(plain));
    }

    /** Tests that the simple name of a user interface is unique among the user types
     * and user interfaces, whatever their packages (gh #944).
     */
    @Test
    @AIGenerated("Claude Fable 5.2, 2026-11")
    public void testInterfaceNameErrors() {
        var other = PACKAGE_NAME.extend("other");
        var named = PACKAGE_NAME.extend("UserNamed");
        var otherNamed = other.extend("UserNamed");
        var counter = PACKAGE_NAME.extend("UserTypeCounter");
        var otherCounter = other.extend("UserTypeCounter");
        // each is correct on its own
        assertTrue(UserSignature.checkUserClass(List.of(otherNamed)).getErrors().get().isEmpty());
        assertTrue(UserSignature.checkUserClass(List.of(otherCounter)).getErrors().get().isEmpty());
        // two user interfaces: the one that comes second is reported
        checkMessages(List.of(named, otherNamed),
                      "User interface '%s' has the same simple name as user interface '%s'"
                          .formatted(otherNamed, named));
        checkMessages(List.of(otherNamed, named),
                      "User interface '%s' has the same simple name as user interface '%s'"
                          .formatted(named, otherNamed));
        // a user interface and a user type, in either order
        var message = "User interface '%s' has the same simple name as user type '%s'"
            .formatted(otherCounter, counter);
        checkMessages(List.of(counter, otherCounter), message);
        checkMessages(List.of(otherCounter, counter), message);
    }

    /** Tests the operators with a variable argument tail, which stem from a sequence type
     * as the last field of a user type or as the last parameter of a user operation.
     */
    @Test
    @AIGenerated("Claude Fable 5.2, 2026-11")
    public void testSequenceTail() {
        // a sequence of user values needs the type of its elements among the user classes
        loadFail("UserOpsSeq");
        load("UserTypePoly", "UserTypeBag", "UserOpsSeq");
        var ops = UserSignature.getOperators();
        for (var op : ops.values()) {
            boolean tail = true;
            switch (op.getName()) {
            case "UserTypePoly" -> check(op, false, Sort.USER, Sort.STRING, Sort.INT);
            case "UserTypeBag" -> check(op, false, Sort.USER, Sort.INT);
            case "horner" -> check(op, false, Sort.INT, Sort.INT, Sort.INT);
            case "total" -> check(op, false, Sort.REAL, Sort.REAL);
            case "all" -> check(op, false, Sort.BOOL, Sort.BOOL);
            case "join" -> check(op, false, Sort.STRING, Sort.STRING, Sort.STRING);
            case "countCoeffs" -> check(op, false, Sort.INT, Sort.USER);
            case "sumAt" -> check(op, false, Sort.INT, Sort.USER, Sort.INT);
            case "degree" -> {
                check(op, false, Sort.INT, Sort.USER);
                tail = false;
            }
            case "append" -> {
                check(op, false, Sort.USER, Sort.USER, Sort.INT);
                tail = false;
            }
            case "name" -> {
                check(op, false, Sort.STRING, Sort.USER);
                tail = false;
            }
            // the accessors generated for the sequence-valued fields
            case "coeffsAt", "elemsAt" -> {
                check(op, false, Sort.INT, Sort.USER, Sort.INT);
                tail = false;
            }
            case "coeffsSize", "elemsSize" -> {
                check(op, false, Sort.INT, Sort.USER);
                tail = false;
            }
            default -> fail("Unexpected operation " + op.getName());
            }
            assertEquals(tail, op.hasTail(), op.getName());
        }
        assertEquals(15, ops.size());

        var poly = ops.get("UserTypePoly");
        assertNotNull(poly);
        assertEquals("user:UserTypePoly(string,int...)", poly.toString());
        // the tail may be empty, the part before it may not
        assertFalse(poly.allowsArgCount(0));
        assertTrue(poly.allowsArgCount(1));
        assertTrue(poly.allowsArgCount(5));
        assertEquals(Sort.STRING, poly.getParamSort(0));
        assertEquals(Sort.INT, poly.getParamSort(1));
        assertEquals(Sort.INT, poly.getParamSort(4));
        assertEquals(List.of(Sort.STRING), poly.getParamSorts(1));
        assertEquals(List.of(Sort.STRING, Sort.INT, Sort.INT), poly.getParamSorts(3));
        var bag = ops.get("UserTypeBag");
        assertNotNull(bag);
        assertTrue(bag.allowsArgCount(0));
        assertEquals(List.of(), bag.getParamSorts(0));
        // the receiving object of a non-static operation is part of what precedes the tail
        var sumAt = ops.get("sumAt");
        assertNotNull(sumAt);
        assertFalse(sumAt.allowsArgCount(0));
        assertTrue(sumAt.allowsArgCount(1));
        // an operator without a tail has its parameter sorts whatever the number of arguments
        var degree = ops.get("degree");
        assertNotNull(degree);
        assertEquals(List.of(Sort.USER), degree.getParamSorts(3));

        var docMap = Algebras.getOpDocMap();
        // the syntax line marks the last parameter as standing for any number of arguments
        assertTrue(docMap.keySet().toString(),
                   docMap
                       .keySet()
                       .stream()
                       .anyMatch(k -> k.contains("horner</") && k.contains("coeffs</i></font>*)")));
        checkParDocs(docMap, "horner", GENERATED, "any number of ");
        checkParDocs(docMap, "sumAt", GENERATED, "any number of ");
    }

    /** Tests that a sequence type is rejected where it is not allowed. */
    @Test
    @AIGenerated("Claude Fable 5.2, 2026-11")
    public void testSequenceErrors() {
        checkErrors("UserTypeSeqNotLast", ".first' is not the last field");
        checkErrors("UserTypeUserSeq", ".polys' is a sequence of user values");
        checkErrors("UserOpsSeqErrors",
                    "notLast': Sequence type IntSeq is only allowed for the last parameter",
                    "result': Return type IntSeq is a sequence type",
                    "untyped': Sequence type UserSeq does not specify the type of its elements",
                    "generic': Sequence type UserSeq does not specify the type of its elements",
                    "strings': Sequence type UserSeq is for user values, not for elements of type String");
    }

    /** Tests the accessors that are generated for a sequence-valued field of a user type:
     * one for the elements and one for the length, and none of the name of the field.
     */
    @Test
    @AIGenerated("Claude Fable 5.2, 2026-11")
    public void testSequenceAccessors() {
        load("UserTypePoly", "UserTypeTags");
        var ops = UserSignature.getOperators();
        var coeffsAt = ops.get("coeffsAt");
        assertNotNull(coeffsAt);
        check(coeffsAt, false, Sort.INT, Sort.USER, Sort.INT);
        assertEquals(Operator.SeqAccessor.AT, getImpl(coeffsAt).accessor());
        assertEquals("user:coeffsAt(user,int)", coeffsAt.toString());
        var coeffsSize = ops.get("coeffsSize");
        assertNotNull(coeffsSize);
        check(coeffsSize, false, Sort.INT, Sort.USER);
        assertEquals(Operator.SeqAccessor.SIZE, getImpl(coeffsSize).accessor());
        // the element accessor has the sort of the elements
        var tagsAt = ops.get("tagsAt");
        assertNotNull(tagsAt);
        check(tagsAt, false, Sort.STRING, Sort.USER, Sort.INT);
        // the name of the field is not that of an operator
        assertNull(ops.get("coeffs"));
        assertNull(ops.get("tags"));
        // other operators are not accessors
        for (var name : List.of("UserTypePoly", "name", "degree", "sumAt")) {
            var op = ops.get(name);
            assertNotNull(op);
            assertNull(name, getImpl(op).accessor());
        }
        // both accessors are based on the accessor method of the field
        var method = getImpl(coeffsAt).executable();
        assertEquals("coeffs", method.getName());
        assertSame(method, getImpl(coeffsSize).executable());
        // like the accessors of other fields, they are not documented
        var docMap = Algebras.getOpDocMap();
        assertFalse(docMap.keySet().stream().anyMatch(k -> k.contains("coeffsAt</")));
    }

    /** Tests that the names of the accessors generated for a sequence-valued field may
     * not be taken by other operations, and neither may the name of the field itself,
     * in the class of the field or in another one.
     */
    @Test
    @AIGenerated("Claude Fable 5.2, 2026-11")
    public void testSequenceAccessorClashes() {
        var clash = PACKAGE_NAME.extend("UserTypeSeqClash");
        var generated = "'%s' (generated for sequence-valued field 'items')".formatted(clash);
        checkErrors("UserTypeSeqClash",
                    // the other field comes first, the operation comes last
                    "Duplicate user operation 'itemsSize' in '%s' and %s"
                        .formatted(clash, generated),
                    "Duplicate user operation 'itemsAt' in %s and '%s'".formatted(generated, clash),
                    "User operation 'items' in '%s' has the name of sequence-valued field '%s.items', which is reserved"
                        .formatted(clash, clash));
        // each of the following classes is correct on its own
        load("UserOpsSeqName");
        load("UserTypeBag");
        var ops = PACKAGE_NAME.extend("UserOpsSeqName");
        var bag = PACKAGE_NAME.extend("UserTypeBag");
        var expected
            = "User operation 'elems' in '%s' has the name of sequence-valued field '%s.elems', which is reserved"
                .formatted(ops, bag);
        // the order of the classes does not matter
        for (var names : List.of(List.of(ops, bag), List.of(bag, ops))) {
            var errors = UserSignature.checkUserClass(names).getErrors().get();
            assertEquals(1, errors.size(), errors.toString());
            assertEquals(expected, errors.iterator().next().toString());
        }
    }

    /** Tests that a given user class has precisely as many errors as there are
     * given fragments, each of which occurs in one of the errors.
     */
    private void checkErrors(String className, String... fragments) {
        checkErrors(List.of(PACKAGE_NAME.extend(className)), fragments);
    }

    /** Tests that a given list of user classes has precisely as many errors as there are
     * given fragments, each of which occurs in one of the errors.
     */
    private void checkErrors(List<QualName> classNames, String... fragments) {
        var errors = UserSignature.checkUserClass(classNames).getErrors().get();
        assertEquals(fragments.length, errors.size(), errors.toString());
        for (var fragment : fragments) {
            assertTrue(errors.toString(),
                       errors.stream().anyMatch(e -> e.toString().contains(fragment)));
        }
    }

    /** Tests that a given list of user classes has precisely the given error messages,
     * in whatever order.
     */
    @AIGenerated("Claude Opus 5.5, 2026-11")
    private void checkMessages(List<QualName> classNames, String... messages) {
        var errors = UserSignature.checkUserClass(classNames).getErrors().get();
        var actual = errors.stream().map(Object::toString).collect(Collectors.toSet());
        assertEquals(Set.of(messages), actual);
        assertEquals(messages.length, errors.size(), errors.toString());
    }

    /** Returns the implementation of a given user operator. */
    private UserSignature.Impl getImpl(Operator op) {
        var impl = UserSignature.getImplementations().get(op);
        assertNotNull(impl);
        return impl;
    }

    /** Tests that the user operator of a given name has an implementation
     * with a given receiver.
     */
    @AIGenerated("Claude Opus 5.5, 2026-11")
    private void checkReceiver(String opName, Class<?> receiver) {
        var op = UserSignature.getOperators().get(opName);
        assertNotNull(opName, op);
        assertSame("Receiver of " + opName, receiver, getImpl(op).receiver());
    }

    /** Suffix of the message on a duplicate operation of two user types or interfaces. */
    static private final String NO_INTERFACE_HINT
        = "; user types can only share the name of an operation of a user interface that they implement";

    /** Applies the user operator of a given name to given arguments,
     * and returns the outcome, which may be an error value.
     */
    private Object apply(String opName, Object... args) {
        var op = UserSignature.getOperators().get(opName);
        assertNotNull(op);
        return AlgebraFamily.DEFAULT.getOperation(op).applyStrict(Arrays.asList(args));
    }

    /** Tests that a given outcome is an error value with a given message. */
    private void checkError(Object outcome, String message) {
        assertTrue(outcome.toString(), outcome instanceof ErrorValue);
        assertEquals(message, ((ErrorValue) outcome).getException().getMessage());
    }

    /** Tests that the parameter documentation of a given user operation consists
     * of rows that contain the given fragments, in that order.
     */
    private void checkParDocs(HelpMap docMap, String opName, String... fragments) {
        var parDocs = getParDocs(docMap, opName);
        assertEquals(fragments.length, parDocs.size(), "Documented parameters of " + opName);
        for (int i = 0; i < fragments.length; i++) {
            var parDoc = parDocs.get(i);
            assertTrue("Parameter " + i + " of " + opName + " is documented as " + parDoc,
                       parDoc.contains(fragments[i]));
        }
    }

    /** Returns the rows of the parameter table in the tool tip of a given
     * user operation, which must have exactly one item in a given documentation map.
     */
    private List<String> getParDocs(HelpMap docMap, String opName) {
        var rows = Arrays.asList(getTip(docMap, opName).split("<tr>", -1));
        return rows.subList(1, rows.size());
    }

    /** Returns the tool tip of a given user operation, which must have
     * exactly one item in a given documentation map.
     */
    private String getTip(HelpMap docMap, String opName) {
        var tips = docMap
            .entrySet()
            .stream()
            .filter(e -> e.getKey().contains(opName + "</"))
            .map(e -> e.getValue())
            .toList();
        assertEquals(1, tips.size(), "Documentation items for " + opName);
        var tip = tips.get(0);
        assertNotNull(tip);
        return tip;
    }

    /** Fragment that marks a generated parameter documentation line. */
    static private final String GENERATED = "-typed expression";

    private void loadFail(String... classNames) {
        var qualClassNames = Arrays.stream(classNames).map(PACKAGE_NAME::extend).toList();
        try {
            UserSignature.checkUserClass(qualClassNames).throwException();
            fail(classNames + " contain errors and should not be loadable");
        } catch (FormatException exc) {
            var message = exc.getMessage();
            assert message != null; // format exceptions carry a message
            assertFalse("Failed to load " + classNames, message.contains("cannot be loaded"));
        }
    }

    private void load(String... classNames) {
        var qualClassNames = Arrays.stream(classNames).map(PACKAGE_NAME::extend).toList();
        try {
            UserSignature.checkUserClass(qualClassNames).throwException();
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

    static private final QualName PACKAGE_NAME = QualName.name("nl.utwente.groove.test.algebra.user");
}
