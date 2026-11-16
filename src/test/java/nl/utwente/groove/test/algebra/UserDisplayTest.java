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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.UserAlgebra;
import nl.utwente.groove.algebra.UserSignature;
import nl.utwente.groove.annotation.UserDisplay;
import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.GraphConverter;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.ValueNode;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.HostModel;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.test.algebra.user.UserTypeDisplay;
import nl.utwente.groove.test.algebra.user.UserTypeDisplayFailing;
import nl.utwente.groove.test.algebra.user.UserTypeDisplayTwice;
import nl.utwente.groove.test.algebra.user.UserTypeString;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the custom display strings of user values (see {@link UserDisplay}):
 * that they are shown where asked for, and that they leave the symbols of
 * the values, and so what is saved and parsed, as they were.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.2, 2026-11")
public class UserDisplayTest {
    /** Unloads the user classes, so that other tests start without them. */
    @AfterEach
    public void tearDown() {
        UserSignature.setUserClass(Collections.emptyList());
    }

    /** The display string of a value is what the display method returns;
     * the symbol and the constant of the value are not affected.
     */
    @Test
    public void testDisplayString() throws FormatException {
        load("UserTypeDisplay", "UserTypeString");
        var algebra = UserAlgebra.instance;
        var value = new UserTypeDisplay("a.zip", "x/y", 3);
        assertEquals(DISPLAY, algebra.toDisplayString(value));
        // the symbol is the constructor term
        assertEquals(SYMBOL, algebra.toSymbol(value));
        // which is read back as the value, and displayed as before
        var constant = Sort.USER.createConstant(SYMBOL);
        assertEquals(value, AlgebraFamily.DEFAULT.toValue(constant));
        assertEquals(TERM, constant.getSymbol());
        assertEquals(TERM, constant.toLine().toFlatString());
        // the display string can be had from the constant as well
        assertEquals(DISPLAY, algebra.toDisplayString(constant));
        // a type without display method has no display strings
        assertNull(algebra.toDisplayString(new UserTypeString("ab")));
        assertNull(algebra.toDisplayString(Sort.USER.createConstant("UserTypeString(\"ab\")")));
    }

    /** A display method that fails, or returns nothing to show, yields no display string. */
    @Test
    public void testFailingDisplay() throws FormatException {
        load("UserTypeDisplayFailing");
        var algebra = UserAlgebra.instance;
        // the method throws an exception
        assertNull(algebra.toDisplayString(new UserTypeDisplayFailing(-1)));
        assertNull(algebra.toDisplayString(Sort.USER.createConstant("UserTypeDisplayFailing(-1)")));
        // the method returns null
        assertNull(algebra.toDisplayString(new UserTypeDisplayFailing(0)));
        // the method returns the empty string
        assertNull(algebra.toDisplayString(new UserTypeDisplayFailing(1)));
        assertEquals("code 2", algebra.toDisplayString(new UserTypeDisplayFailing(2)));
    }

    /** Display methods are checked when the user classes are. */
    @Test
    public void testDisplayErrors() {
        checkErrors("UserTypeDisplayErrors",
                    ".showStatic' should be non-static, without parameters and with return type String",
                    ".showPar' should be non-static, without parameters and with return type String",
                    ".showInt' should be non-static, without parameters and with return type String",
                    ".showHidden' is not public", "has more than one display method");
        checkErrors("UserTypeDisplayTwice", "has more than one display method");
        checkErrors("UserOpsDisplay", ".show' is not declared in a user type");
        // neither of two display methods is used
        assertNull(UserAlgebra.instance.toDisplayString(new UserTypeDisplayTwice(1)));
    }

    /** The line of a field with a user value shows the display string if asked to,
     * and the constructor term otherwise.
     */
    @Test
    public void testFieldLine() throws IOException, FormatException {
        var state = GraphConverter.toAspectMap(loadStartGraph(loadGrammar())).getAspectGraph();
        // a value with a display string
        var walk = getField(state, "walk");
        assertTrue(walk.hasUserDisplay());
        var plain = toLine(walk, false);
        assertTrue(plain.startsWith("walk"), plain);
        assertTrue(plain.endsWith(TERM), plain);
        assertEquals(plain, walk.toLine(true, walk.source().getAspects()).toFlatString());
        var display = toLine(walk, true);
        assertEquals(plain.substring(0, plain.indexOf("UserTypeDisplay")) + DISPLAY, display);
        // a value of which the display method fails, and one of another sort
        for (var name : List.of("failing", "name")) {
            var field = getField(state, name);
            assertFalse(field.hasUserDisplay(), name);
            assertEquals(toLine(field, false), toLine(field, true));
        }
    }

    /** A state is saved with the constructor terms of its user values,
     * and so is read back as the same state.
     */
    @Test
    public void testSaveState(@TempDir Path dir) throws IOException, FormatException {
        var grammar = loadGrammar();
        var host = loadStartGraph(grammar);
        var state = GraphConverter.toAspectMap(host).getAspectGraph();
        assertEquals("let:walk=" + SYMBOL, getField(state, "walk").label().text());
        File file = dir.resolve("state.gst").toFile();
        Groove.saveGraph(state.toPlainGraph(), file);
        var loaded = AspectGraph.newInstance(Groove.loadGraph(file));
        assertEquals("let:walk=" + SYMBOL, getField(loaded, "walk").label().text());
        var loadedHost = new HostModel(grammar, loaded).toResource();
        var values = getValues(host);
        assertTrue(values.contains(new UserTypeDisplay("a.zip", "x/y", 3)), values.toString());
        assertEquals(values, getValues(loadedHost));
    }

    /** Returns the values in a given host graph. */
    private Set<Object> getValues(HostGraph host) {
        Set<Object> result = new LinkedHashSet<>();
        for (var node : host.nodeSet()) {
            if (node instanceof ValueNode valueNode) {
                result.add(valueNode.getValue());
            }
        }
        return result;
    }

    /** Returns the edge of a given state that assigns a value to a field of a given name. */
    private AspectEdge getField(AspectGraph state, String name) throws FormatException {
        @Nullable AspectEdge result = null;
        for (var edge : state.edgeSet()) {
            if (edge.isAssign() && edge.getAssign().getLhs().equals(name)) {
                result = edge;
            }
        }
        assert result != null : "No field '%s' in %s".formatted(name, state.edgeSet());
        return result;
    }

    /** Returns the text of the node label line for a given field. */
    private String toLine(AspectEdge field, boolean userDisplay) {
        return field.toLine(true, field.source().getAspects(), userDisplay).toFlatString();
    }

    /** Loads the grammar of this test, and with it the user classes. */
    private GrammarModel loadGrammar() throws IOException {
        return Groove.loadGrammar(GRAMMAR);
    }

    /** Returns the start graph of a given grammar, which serves as a state. */
    private HostGraph loadStartGraph(GrammarModel grammar) throws FormatException {
        return grammar.toGrammar().getStartGraph();
    }

    /** Loads the user classes of given names. */
    private void load(String... classNames) {
        UserSignature.setUserClass(Stream.of(classNames).map(PACKAGE_NAME::extend).toList());
    }

    /** Tests that a given user class has precisely as many errors as there are
     * given fragments, each of which occurs in one of the errors.
     */
    private void checkErrors(String className, String... fragments) {
        var errors = UserSignature
            .checkUserClass(List.of(PACKAGE_NAME.extend(className)))
            .getErrors()
            .get();
        assertEquals(fragments.length, errors.size(), errors.toString());
        for (var fragment : fragments) {
            assertTrue(errors.stream().anyMatch(e -> e.toString().contains(fragment)),
                       errors.toString());
        }
    }

    /** Grammar with a start graph that has user values with and without display string. */
    static private final String GRAMMAR = "junit/samples/user-display.gps";
    /** Symbol of the user value of this test. */
    static private final String SYMBOL = "UserTypeDisplay(\"a.zip\",\"x/y\",3)";
    /** The user value of this test as a parsed constant is shown. */
    static private final String TERM = "UserTypeDisplay(\"a.zip\", \"x/y\", 3)";
    /** Display string of the user value of this test. */
    static private final String DISPLAY = "walk <x/y>";
    static private final QualName PACKAGE_NAME = QualName.name("nl.utwente.groove.test.algebra.user");
}
