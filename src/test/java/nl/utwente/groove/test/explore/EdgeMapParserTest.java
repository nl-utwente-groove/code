// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id$
 */
package nl.utwente.groove.test.explore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.explore.config.EdgeMapParser;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the label resolution of the edge-bound condition parser (gh #732):
 * labels may carry an explicit {@code type:} or {@code flag:} prefix; a bare
 * name denotes a binary edge if one exists, otherwise it must resolve
 * unambiguously.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@SuppressWarnings("javadoc")
public class EdgeMapParserTest {
    /** Type graph with binary edge {@code a}, node types {@code A} and {@code a},
     * flag {@code f}, and the name {@code x} both as node type and as flag. */
    private TypeGraph typeGraph;

    @Before
    public void setup() {
        this.typeGraph = new TypeGraph(QualName.name("test"));
        TypeNode a = this.typeGraph.addNode(TypeLabel.createLabel(EdgeRole.NODE_TYPE, "A"));
        this.typeGraph.addNode(TypeLabel.createLabel(EdgeRole.NODE_TYPE, "a"));
        this.typeGraph.addNode(TypeLabel.createLabel(EdgeRole.NODE_TYPE, "x"));
        this.typeGraph.addEdge(a, TypeLabel.createLabel(EdgeRole.BINARY, "a"), a);
        this.typeGraph.addEdge(a, TypeLabel.createLabel(EdgeRole.FLAG, "f"), a);
        this.typeGraph.addEdge(a, TypeLabel.createLabel(EdgeRole.FLAG, "x"), a);
        this.typeGraph.setFixed();
    }

    /** Parses a single-entry map and returns its (unique) label. */
    private TypeLabel parseSingle(String text) throws FormatException {
        Map<TypeLabel,Integer> map = EdgeMapParser.parse(this.typeGraph, text);
        assertEquals(1, map.size());
        return map.keySet().iterator().next();
    }

    /** Asserts that parsing fails with an error containing a given text. */
    private void assertError(String text, String message) {
        try {
            EdgeMapParser.parse(this.typeGraph, text);
            fail("Expected a format error on '" + text + "'");
        } catch (FormatException exc) {
            assertTrue("Unexpected error: " + exc.getMessage(),
                       exc.getMessage().contains(message));
        }
    }

    /** An explicit type: prefix selects the node type label. */
    @Test
    public void testTypePrefix() throws FormatException {
        TypeLabel label = parseSingle("type:A>3");
        assertEquals(EdgeRole.NODE_TYPE, label.getRole());
        assertEquals("A", label.text());
    }

    /** An explicit flag: prefix selects the flag label. */
    @Test
    public void testFlagPrefix() throws FormatException {
        TypeLabel label = parseSingle("flag:f>1");
        assertEquals(EdgeRole.FLAG, label.getRole());
        assertEquals("f", label.text());
    }

    /** A bare name that is both a binary edge and a node type resolves to
     * the binary edge; the prefixed form still selects the node type. */
    @Test
    public void testBarePrefersBinary() throws FormatException {
        assertEquals(EdgeRole.BINARY, parseSingle("a>2").getRole());
        assertEquals(EdgeRole.NODE_TYPE, parseSingle("type:a>2").getRole());
    }

    /** A bare name that only names a flag still resolves (backward
     * compatibility with the unprefixed syntax). */
    @Test
    public void testBareUniqueNonBinary() throws FormatException {
        assertEquals(EdgeRole.FLAG, parseSingle("f>1").getRole());
    }

    /** A bare name that names both a node type and a flag (and no binary
     * edge) is an error rather than an arbitrary pick. */
    @Test
    public void testBareAmbiguous() {
        assertError("x>1", "ambiguous");
        // the prefixed forms disambiguate
        try {
            assertEquals(EdgeRole.NODE_TYPE, parseSingle("type:x>1").getRole());
            assertEquals(EdgeRole.FLAG, parseSingle("flag:x>1").getRole());
        } catch (FormatException exc) {
            fail(exc.getMessage());
        }
    }

    /** Unknown names are errors, also in prefixed form. */
    @Test
    public void testUnknown() {
        assertError("zzz>1", "not a valid edge name");
        assertError("type:f>1", "not a valid edge name");
        assertError("flag:A>1", "not a valid edge name");
    }

    /** Multi-entry maps and the numeric bound still parse as before. */
    @Test
    public void testMultiEntry() throws FormatException {
        Map<TypeLabel,Integer> map = EdgeMapParser.parse(this.typeGraph, "a>2,type:A>3");
        assertEquals(2, map.size());
        assertError("a>-1", "not a valid edge bound");
        assertError("a", "not a valid condition");
    }
}
