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
package nl.utwente.groove.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import nl.utwente.groove.algebra.Algebra;
import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.algebra.Constant;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.aspect.GraphConverter;
import nl.utwente.groove.grammar.host.DefaultHostGraph;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.StringHandler;

/**
 * Regression tests for {@link StringHandler#toQuoted} and
 * {@link StringHandler#toUnquoted}, in particular the escaping of the escape
 * character itself. Before grammar version 3.12, {@code toQuoted} escaped only
 * the quote character, so a string value ending in a backslash produced an
 * unparsable quoted form.
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("javadoc")
public class StringHandlerTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    /** The exact quoted forms of the three key cases. */
    @Test
    public void quotedForms() {
        // trailing backslash
        assertEquals("\"a\\\\\"", StringHandler.toQuoted("a\\", '"'));
        // embedded quote
        assertEquals("\"a\\\"b\"", StringHandler.toQuoted("a\"b", '"'));
        // double backslash
        assertEquals("\"a\\\\\\\\b\"", StringHandler.toQuoted("a\\\\b", '"'));
    }

    /** Round trips through toQuoted/toUnquoted, for both quote characters. */
    @Test
    public void quoteRoundTrips() throws FormatException {
        String[] tests = {
            // the three cases from the bug report
            "a\\", // trailing backslash
            "a\"b", // embedded double quote
            "a\\\\b", // embedded double backslash
            // further boundary cases
            "", "\\", "a\\\\", "\\a", "a\\b", "a\\\"b", "a'b", "\"", "'", "C:\\dir\\file",
        };
        for (char quote : new char[] {'"', '\''}) {
            for (String test : tests) {
                String quoted = StringHandler.toQuoted(test, quote);
                assertEquals("round trip of [" + test + "] with " + quote, test,
                             StringHandler.toUnquoted(quoted, quote));
            }
        }
    }

    /** An escape before any character other than a quote or escape is kept
     * literally, so pre-3.12 quoted forms with single backslashes read back
     * unchanged. */
    @Test
    public void lenientUnquoting() throws FormatException {
        assertEquals("a\\b", StringHandler.toUnquoted("\"a\\b\"", '"'));
        assertEquals("C:\\dir\\file", StringHandler.toUnquoted("\"C:\\dir\\file\"", '"'));
        // the examples from the toUnquoted javadoc
        assertEquals("line", StringHandler.toUnquoted("'line'", '\''));
        assertEquals("'lin'e", StringHandler.toUnquoted("'\\'lin\\'e'", '\''));
        assertEquals("\\li\\ne'", StringHandler.toUnquoted("'\\li\\\\ne\\''", '\''));
        assertEquals("li'ne\\", StringHandler.toUnquoted("'li\\'ne\\\\'", '\''));
    }

    /** A double backslash now encodes a single one, so a quote preceded by an
     * escaped backslash closes the string; the pre-3.12 quoted form of a
     * backslash directly before a quote is a format error. */
    @Test
    public void legacyBackslashBeforeQuoteErrors() {
        try {
            String result = StringHandler.toUnquoted("\"a\\\\\"b\"", '"');
            fail("expected unbalanced quote error, got [" + result + "]");
        } catch (FormatException exc) {
            // expected
        }
    }

    /** Round trips of string attribute values through the aspect-graph save/load
     * representation, exercising the full writer (ValueNode symbol) and reader
     * (aspect label and expression parsing) chain. */
    @Test
    public void aspectSaveLoadRoundTrips() throws Exception {
        assertAspectRoundTrip("a\\"); // trailing backslash
        assertAspectRoundTrip("a\"b"); // embedded quote
        assertAspectRoundTrip("a\\\\b"); // double backslash
        assertAspectRoundTrip("C:\\dir\\file");
    }

    /** Saves a host graph with the given string attribute value to a GXL file,
     * loads it back, and asserts that the value survived. */
    private void assertAspectRoundTrip(String value) throws Exception {
        DefaultHostGraph host = new DefaultHostGraph("test");
        HostNode source = host.addNode();
        Algebra<?> algebra = AlgebraFamily.DEFAULT.getAlgebra(Sort.STRING);
        HostNode valueNode = host.addNode(algebra, algebra.toValueFromJava(value));
        host.addEdge(source, "attr", valueNode);
        host.setFixed();
        AspectGraph aspectGraph = GraphConverter.toAspectMap(host).getAspectGraph();
        File file = new File(this.tmp.newFolder(), "test.gst");
        Groove.saveGraph(aspectGraph.toPlainGraph(), file);
        AspectGraph reloaded = AspectGraph.newInstance(Groove.loadGraph(file));
        reloaded.getErrors().throwException();
        AspectEdge letEdge = reloaded
            .edgeSet()
            .stream()
            .filter(e -> e.has(AspectKind.LET))
            .findAny()
            .orElseThrow(() -> new AssertionError("no let edge in reloaded graph"));
        Constant constant = (Constant) letEdge.getAssign().getRhs();
        assertEquals(value, constant.getStringRepr());
    }
}
