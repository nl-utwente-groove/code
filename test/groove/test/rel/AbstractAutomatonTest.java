// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

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
package groove.test.rel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import groove.automaton.RegAut;
import groove.automaton.RegAut.Result;
import groove.automaton.RegAutCalculator;
import groove.automaton.RegExpr;
import groove.automaton.SimpleNFA;
import groove.grammar.QualName;
import groove.grammar.host.DefaultHostGraph;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.host.HostNodeSet;
import groove.grammar.model.GrammarModel;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.Valuation;
import groove.grammar.type.ImplicitTypeGraph;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeFactory;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeLabel;
import groove.grammar.type.TypeNode;
import groove.graph.EdgeRole;
import groove.io.graph.GxlIO;
import groove.util.Groove;
import groove.util.parse.FormatException;
import groove.util.parse.StringHandler;
import junit.framework.Assert;

/**
 * Tests the available {@link RegAut} interface.
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("all")
abstract public class AbstractAutomatonTest {
    /** Directory with test files (relative to the project) */
    static public final String GRAPH_TEST_DIR = "junit/graphs";
    /** Directory with test grammar (relative to the project) */
    static public final String GRAMMAR = "junit/samples/regexpr";
    /** Name of the type graph used in this test. */
    static public final QualName TYPE_NAME = QualName.name("construction");

    RegAutCalculator calculator = new RegAutCalculator();

    static final List<String> wordEmpty = Arrays.asList();
    static final List<String> wordA = Arrays.asList("A");
    static final List<String> wordB = Arrays.asList("B");
    static final List<String> wordAA = Arrays.asList("A", "A");
    static final List<String> wordAB = Arrays.asList("A", "B");
    static final List<String> wordBB = Arrays.asList("B", "B");
    static final List<String> wordBA = Arrays.asList("B", "A");
    static final List<String> wordABC = Arrays.asList("A", "B", "C");
    static final List<String> wordAAA = Arrays.asList("A", "A", "A");
    static final List<String> wordAAABCAAA = Arrays.asList("A", "A", "A", "B", "C", "A", "A", "A");
    static final String testGraphName = "regexpr-test-graph";
    static HostGraph testGraph;

    static HostNode nList, nC1, nC2, nC3, nC4, nI0, nI1, nI2, nI3;
    static HostNodeSet nC12, nC34;
    static Set<Result> reflexive;
    static ImplicitTypeGraph implicitTypeGraph;
    static TypeGraph loadedTypeGraph;
    /** Flag switching between {@link #implicitTypeGraph} and {@link #loadedTypeGraph}. */
    static boolean useLoadedTypeGraph;

    @BeforeClass
    public static void initStatics() {
        try {
            testGraph = new DefaultHostGraph(GxlIO.instance()
                .loadGraph(new File(GRAPH_TEST_DIR + "/" + testGraphName + ".gxl")));
        } catch (IOException e) {
            Assert.fail(e.toString());
        }
        implicitTypeGraph = new ImplicitTypeGraph();
        for (HostEdge testEdge : testGraph.edgeSet()) {
            implicitTypeGraph.addLabel(testEdge.label());
        }
        implicitTypeGraph.addLabel("A");
        implicitTypeGraph.addLabel("B");
        implicitTypeGraph.addLabel("C");
        implicitTypeGraph.addLabel("D");
        try {
            GrammarModel view = Groove.loadGrammar(GRAMMAR);
            loadedTypeGraph = view.getTypeModel(TYPE_NAME)
                .toResource();
        } catch (FormatException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        }
        nList = getNode("List");
        nC1 = getNode("n1");
        nC2 = getNode("n2");
        nC3 = getNode("n3");
        nC4 = getNode("n4");
        nI0 = getNode("0");
        nI1 = getNode("1");
        nI2 = getNode("2");
        nI3 = getNode("3");
        nC12 = new HostNodeSet();
        nC12.add(nC1);
        nC12.add(nC2);
        nC34 = new HostNodeSet();
        nC34.add(nC3);
        nC34.add(nC4);
        reflexive = new HashSet<>();
        addRelated(reflexive, nList, nList);
        addRelated(reflexive, nC1, nC1);
        addRelated(reflexive, nC2, nC2);
        addRelated(reflexive, nC3, nC3);
        addRelated(reflexive, nC4, nC4);
        addRelated(reflexive, nI0, nI0);
        addRelated(reflexive, nI1, nI1);
        addRelated(reflexive, nI2, nI2);
        addRelated(reflexive, nI3, nI3);
    }

    @Before
    public void setUp() {
        this.calculator = new RegAutCalculator(getPrototype());
    }

    @Test
    public void testConstruction() {
        RegAut aut = createAutomaton("?|flag:?|type:?");
        assertTrue(aut.toString() != null);
        assertEquals(getTypeGraph().edgeSet(), aut.getAlphabet());
        testError("!a");
    }

    @Test
    public void testEmptyAccepts() {
        RegAut aut = createAutomaton("=");
        assertTrue(aut.accepts(wordEmpty));
        assertFalse(aut.accepts(wordA));
        aut = createAutomaton("D*");
        assertTrue(aut.accepts(wordEmpty));
        assertFalse(aut.accepts(wordA));
        aut = createAutomaton("D+");
    }

    @Test
    public void testAtomAccepts() {
        RegAut aut = createAutomaton("A");
        assertTrue(aut.accepts(wordA));
        assertFalse(aut.accepts(wordB));
        assertFalse(aut.accepts(wordEmpty));
        assertFalse(aut.accepts(wordAA));
        assertFalse(aut.accepts(wordAB));
        assertFalse(aut.accepts(wordBA));
        useLoadedTypeGraph = true;
        aut = createAutomaton("A");
        useLoadedTypeGraph = false;
        if (aut instanceof SimpleNFA) {
            assertFalse(aut.accepts(wordA));
        }
    }

    @Test
    public void testWildcardAccepts() {
        RegAut aut = createAutomaton("?");
        assertTrue(aut.accepts(wordA));
        assertTrue(aut.accepts(wordB));
        assertFalse(aut.accepts(wordEmpty));
        assertFalse(aut.accepts(wordAA));
        assertFalse(aut.accepts(wordAB));
        assertFalse(aut.accepts(wordBA));
    }

    @Test
    public void testSeqAccepts() {
        RegAut aut = createAutomaton("A.B");
        assertFalse(aut.accepts(wordA));
        assertFalse(aut.accepts(wordB));
        assertFalse(aut.accepts(wordEmpty));
        assertFalse(aut.accepts(wordAA));
        assertTrue(aut.accepts(wordAB));
        assertFalse(aut.accepts(wordBA));
        aut = createAutomaton("A.=");
        assertTrue(aut.accepts(wordA));
        assertFalse(aut.accepts(wordB));
        assertFalse(aut.accepts(wordEmpty));
        assertFalse(aut.accepts(wordAA));
        assertFalse(aut.accepts(wordAB));
        aut = createAutomaton("=.B");
        assertFalse(aut.accepts(wordA));
        assertTrue(aut.accepts(wordB));
        assertFalse(aut.accepts(wordEmpty));
        assertFalse(aut.accepts(wordAA));
        assertFalse(aut.accepts(wordAB));
        aut = createAutomaton("A.(=|B)");
        assertTrue(aut.accepts(wordA));
        assertFalse(aut.accepts(wordB));
        assertFalse(aut.accepts(wordEmpty));
        assertFalse(aut.accepts(wordAA));
        assertTrue(aut.accepts(wordAB));
        assertFalse(aut.accepts(wordBA));
        aut = createAutomaton("(=|A).(=|B)");
        assertTrue(aut.accepts(wordA));
        assertTrue(aut.accepts(wordB));
        assertTrue(aut.accepts(wordEmpty));
        assertFalse(aut.accepts(wordAA));
        assertTrue(aut.accepts(wordAB));
        assertFalse(aut.accepts(wordBA));
        aut = createAutomaton("A.B.C");
        assertFalse(aut.accepts(wordA));
        assertFalse(aut.accepts(wordB));
        assertFalse(aut.accepts(createWord("C")));
        assertFalse(aut.accepts(wordEmpty));
        assertFalse(aut.accepts(createWord("A B")));
        assertFalse(aut.accepts(createWord("A C")));
        assertFalse(aut.accepts(createWord("B C")));
        assertTrue(aut.accepts(createWord("A B C")));
        assertFalse(aut.accepts(createWord("B C A")));
        assertFalse(aut.accepts(createWord("A B B")));
        assertFalse(aut.accepts(createWord("A B C D")));
        aut = createAutomaton("A.=.C");
        assertFalse(aut.accepts(wordA));
        assertFalse(aut.accepts(createWord("C")));
        assertFalse(aut.accepts(wordEmpty));
        assertFalse(aut.accepts(createWord("A B")));
        assertTrue(aut.accepts(createWord("A C")));
        assertFalse(aut.accepts(createWord("B C")));
        assertFalse(aut.accepts(createWord("A B C")));
        assertFalse(aut.accepts(createWord("A C B")));
        aut = createAutomaton("A.(A|B).(B|C)");
        assertFalse(aut.accepts(wordA));
        assertFalse(aut.accepts(wordB));
        assertFalse(aut.accepts(createWord("C")));
        assertFalse(aut.accepts(wordEmpty));
        assertFalse(aut.accepts(createWord("A B")));
        assertFalse(aut.accepts(createWord("A C")));
        assertFalse(aut.accepts(createWord("B C")));
        assertTrue(aut.accepts(createWord("A B C")));
        assertTrue(aut.accepts(createWord("A B B")));
        assertTrue(aut.accepts(createWord("A A C")));
        assertTrue(aut.accepts(createWord("A A B")));
        assertFalse(aut.accepts(createWord("B C A")));
        assertFalse(aut.accepts(createWord("A C B")));
        assertFalse(aut.accepts(createWord("A B C D")));
        aut = createAutomaton("A*.B");
        assertFalse(aut.accepts(wordEmpty));
        assertFalse(aut.accepts(wordA));
        assertTrue(aut.accepts(wordB));
        assertTrue(aut.accepts(createWord("A B")));
        assertFalse(aut.accepts(createWord("A C")));
        assertFalse(aut.accepts(createWord("A A")));
        assertFalse(aut.accepts(createWord("B B")));
        assertFalse(aut.accepts(createWord("A B C")));
        assertFalse(aut.accepts(createWord("A B B")));
        assertFalse(aut.accepts(createWord("A A C")));
        assertTrue(aut.accepts(createWord("A A B")));
        assertFalse(aut.accepts(createWord("A A A A")));
        assertTrue(aut.accepts(createWord("A A A A B")));
        assertFalse(aut.accepts(createWord("A A A A C")));
        aut = createAutomaton("A.B*");
        assertFalse(aut.accepts(wordEmpty));
        assertTrue(aut.accepts(wordA));
        assertFalse(aut.accepts(wordB));
        assertTrue(aut.accepts(createWord("A B")));
        assertFalse(aut.accepts(createWord("A C")));
        assertFalse(aut.accepts(createWord("A A")));
        assertFalse(aut.accepts(createWord("B B")));
        assertFalse(aut.accepts(createWord("A B C")));
        assertTrue(aut.accepts(createWord("A B B")));
        assertFalse(aut.accepts(createWord("C B B")));
        assertFalse(aut.accepts(createWord("A A B")));
        assertFalse(aut.accepts(createWord("B B B B")));
        assertTrue(aut.accepts(createWord("A B B B B")));
        assertFalse(aut.accepts(createWord("C B B B B")));
    }

    @Test
    public void testChoiceAccepts() {
        RegAut aut = createAutomaton("A|B");
        assertTrue(aut.accepts(wordA));
        assertTrue(aut.accepts(wordB));
        assertFalse(aut.accepts(createWord("C")));
        assertFalse(aut.accepts(wordEmpty));
        assertFalse(aut.accepts(wordAA));
        assertFalse(aut.accepts(wordAB));
        aut = createAutomaton("A|=");
        assertTrue(aut.accepts(wordA));
        assertFalse(aut.accepts(wordB));
        assertTrue(aut.accepts(wordEmpty));
        assertFalse(aut.accepts(wordAA));
        assertFalse(aut.accepts(wordAB));
        aut = createAutomaton("=|A");
        assertTrue(aut.accepts(wordA));
        assertFalse(aut.accepts(wordB));
        assertTrue(aut.accepts(wordEmpty));
        assertFalse(aut.accepts(wordAA));
        assertFalse(aut.accepts(wordAB));
        aut = createAutomaton("A|B|C");
        assertTrue(aut.accepts(wordA));
        assertTrue(aut.accepts(wordB));
        assertTrue(aut.accepts(createWord("C")));
        assertFalse(aut.accepts(wordEmpty));
        assertFalse(aut.accepts(wordAA));
        assertFalse(aut.accepts(wordAB));
        aut = createAutomaton("A.B|C");
        assertFalse(aut.accepts(wordA));
        assertFalse(aut.accepts(wordB));
        assertTrue(aut.accepts(createWord("C")));
        assertFalse(aut.accepts(wordEmpty));
        assertFalse(aut.accepts(wordAA));
        assertTrue(aut.accepts(wordAB));
        assertFalse(aut.accepts(createWord("A B C")));
        assertFalse(aut.accepts(createWord("A C")));
        aut = createAutomaton("C|A.B");
        assertFalse(aut.accepts(wordA));
        assertFalse(aut.accepts(wordB));
        assertTrue(aut.accepts(createWord("C")));
        assertFalse(aut.accepts(wordEmpty));
        assertFalse(aut.accepts(wordAA));
        assertTrue(aut.accepts(wordAB));
        assertFalse(aut.accepts(createWord("A B C")));
        assertFalse(aut.accepts(createWord("A C")));
        aut = createAutomaton("A|B*");
        assertTrue(aut.accepts(wordA));
        assertTrue(aut.accepts(wordB));
        assertFalse(aut.accepts(createWord("C")));
        assertTrue(aut.accepts(wordEmpty));
        assertFalse(aut.accepts(wordAA));
        assertFalse(aut.accepts(wordAB));
        assertTrue(aut.accepts(wordBB));
        assertFalse(aut.accepts(wordBA));
        assertTrue(aut.accepts(createWord("B B B")));
    }

    @Test
    public void testSharpAccepts() {
        useLoadedTypeGraph = true;
        RegAut aut = createAutomaton("type:#A");
        if (aut instanceof SimpleNFA) {
            assertFalse(aut.accepts(wordA));
            assertTrue(aut.accepts(createWord("type:A")));
            assertFalse(aut.accepts(createWord("type:A1")));
            assertFalse(aut.accepts(createWord("type:B")));
            aut = createAutomaton("type:A");
            assertFalse(aut.accepts(wordA));
            assertTrue(aut.accepts(createWord("type:A")));
            assertTrue(aut.accepts(createWord("type:A1")));
            assertFalse(aut.accepts(createWord("type:B")));
        }
        useLoadedTypeGraph = false;
    }

    @Test
    public void testPlusAccepts() {
        RegAut aut = createAutomaton("A+");
        assertFalse(aut.accepts(wordEmpty));
        assertTrue(aut.accepts(wordA));
        assertFalse(aut.accepts(wordB));
        assertTrue(aut.accepts(wordAA));
        assertFalse(aut.accepts(wordAB));
        assertFalse(aut.accepts(wordBA));
        assertTrue(aut.accepts(createWord("A A A A")));
        aut = createAutomaton("(A|B)+");
        assertFalse(aut.accepts(wordEmpty));
        assertTrue(aut.accepts(wordA));
        assertTrue(aut.accepts(wordB));
        assertTrue(aut.accepts(wordAA));
        assertTrue(aut.accepts(wordAB));
        assertTrue(aut.accepts(wordBA));
        assertFalse(aut.accepts(createWord("A C")));
        assertFalse(aut.accepts(createWord("C B")));
        assertTrue(aut.accepts(createWord("A A A A")));
        assertTrue(aut.accepts(createWord("A B A B B")));
        assertFalse(aut.accepts(createWord("A B C B B")));
        aut = createAutomaton("(A|=)+");
        assertTrue(aut.accepts(wordEmpty));
        assertTrue(aut.accepts(wordA));
        assertFalse(aut.accepts(wordB));
        assertTrue(aut.accepts(wordAA));
        assertFalse(aut.accepts(wordAB));
        assertFalse(aut.accepts(wordBA));
        assertTrue(aut.accepts(createWord("A A A A")));
        aut = createAutomaton("(A|B.B)+");
        assertFalse(aut.accepts(wordEmpty));
        assertTrue(aut.accepts(wordA));
        assertFalse(aut.accepts(wordB));
        assertTrue(aut.accepts(wordAA));
        assertFalse(aut.accepts(wordAB));
        assertFalse(aut.accepts(wordBA));
        assertTrue(aut.accepts(wordBB));
        assertFalse(aut.accepts(createWord("B C")));
        assertTrue(aut.accepts(createWord("A B B")));
        assertFalse(aut.accepts(createWord("A B B B")));
        assertTrue(aut.accepts(createWord("B B A")));
        assertFalse(aut.accepts(createWord("B A B")));
        assertFalse(aut.accepts(createWord("B B C")));
        assertTrue(aut.accepts(createWord("A B B A")));
        assertTrue(aut.accepts(createWord("A A A B B A")));
        assertFalse(aut.accepts(createWord("A B A B B A")));
        assertFalse(aut.accepts(createWord("A A C B B")));
    }

    @Test
    public void testStarAccepts() {
        // these are just the plus tests for the empty word
        RegAut aut = createAutomaton("A*");
        assertTrue(aut.accepts(wordEmpty));
        aut = createAutomaton("(A|B)*");
        assertTrue(aut.accepts(wordEmpty));
        assertTrue(aut.accepts(wordAB));
        assertTrue(aut.accepts(wordBB));
        assertFalse(aut.accepts(wordABC));
        aut = createAutomaton("(A|=)*");
        assertTrue(aut.accepts(wordEmpty));
        aut = createAutomaton("(A|B.B)*");
        assertTrue(aut.accepts(wordEmpty));
        assertTrue(aut.accepts(wordBB));
        assertTrue(aut.accepts(wordAA));
        assertFalse(aut.accepts(wordAB));
    }

    @Test
    public void testInvAccepts() {
        RegAut aut = createAutomaton("-A");
        assertFalse(aut.accepts(wordA));
        assertFalse(aut.accepts(wordB));
        assertFalse(aut.accepts(wordEmpty));
        assertFalse(aut.accepts(wordAA));
        assertFalse(aut.accepts(wordAB));
        assertFalse(aut.accepts(wordBA));
    }

    @Test
    public void testStarMatch() {
        RegAut aut = createAutomaton("next*");
        Set<RegAut.Result> results = new HashSet<>();
        addRelated(results, nC1, nC2);
        addRelated(results, nC1, nC3);
        addRelated(results, nC1, nC4);
        addRelated(results, nC2, nC3);
        addRelated(results, nC2, nC4);
        addRelated(results, nC3, nC4);
        results.addAll(reflexive);
        assertEquals(results, aut.getMatches(testGraph, null, null));
        assertEquals(results, aut.getMatches(testGraph, null, null));
        results.clear();
        addRelated(results, nC1, nC4);
        addRelated(results, nC2, nC4);
        addRelated(results, nC3, nC4);
        addRelated(results, nC4, nC4);
        assertEquals(results, aut.getMatches(testGraph, null, nC4));
        assertEquals(results, aut.getMatches(testGraph, null, nC4));
        results.clear();
        addRelated(results, nC2, nC2);
        addRelated(results, nC2, nC3);
        addRelated(results, nC2, nC4);
        assertEquals(results, aut.getMatches(testGraph, nC2, null));
        assertEquals(results, aut.getMatches(testGraph, nC2, null));
        results.clear();
        assertEquals(results, aut.getMatches(testGraph, nC3, nC2));
        addRelated(results, nC2, nC3);
        assertEquals(results, aut.getMatches(testGraph, nC2, nC3));
    }

    @Test
    public void testPlusMatch() {
        RegAut aut = createAutomaton("next+");
        Set<Result> results = new HashSet<>();
        addRelated(results, nC1, nC2);
        addRelated(results, nC1, nC3);
        addRelated(results, nC1, nC4);
        assertEquals(results, aut.getMatches(testGraph, nC1, null));
        assertEquals(results, aut.getMatches(testGraph, nC1, null));
        results.clear();
        addRelated(results, nC2, nC3);
        addRelated(results, nC2, nC4);
        assertEquals(results, aut.getMatches(testGraph, nC2, null));
        assertEquals(results, aut.getMatches(testGraph, nC2, null));
        results.clear();
        addRelated(results, nC1, nC3);
        addRelated(results, nC2, nC3);
        assertEquals(results, aut.getMatches(testGraph, null, nC3));
        assertEquals(results, aut.getMatches(testGraph, null, nC3));
        aut = createAutomaton("?.2");
        results.clear();
        addRelated(results, nI2, nI2);
        addRelated(results, nI3, nI2);
        addRelated(results, nC2, nI2);
    }

    @Test
    public void testWildcardMatch() {
        RegAut aut = createAutomaton("?.3");
        Set<Result> result = new HashSet<>();
        addRelated(result, nI3, nI3);
        addRelated(result, nC3, nI3);
        addRelated(result, nC4, nI3);
        assertEquals(result, aut.getMatches(testGraph, null, null));
        assertEquals(result, aut.getMatches(testGraph, null, null));
        assertEquals(result, aut.getMatches(testGraph, null, nI3));
        assertEquals(result, aut.getMatches(testGraph, null, nI3));
        result.clear();
        assertEquals(result, aut.getMatches(testGraph, null, nI2));
        assertEquals(result, aut.getMatches(testGraph, null, nI2));
    }

    @Test
    public void testWildcardIdMatch() {
        RegAut aut = createAutomaton("?x.3");
        assertEquals(createRelated(nI3, nI3),
            aut.getMatches(testGraph, null, null, createValuation("x", "3")));
        assertEquals(createRelated(nC3, nI3, nC4, nI3),
            aut.getMatches(testGraph, null, null, createValuation("x", "val")));
        aut = createAutomaton("?x.?x.3");
        assertEquals(createRelated(nI3, nI3),
            aut.getMatches(testGraph, null, null, createValuation("x", "3")));
        aut = createAutomaton("(List.?x.?y)+.?x");
        assertEquals(createRelated(nList, nC1),
            aut.getMatches(testGraph, null, null, createValuation("x", "first", "y", "in")));
        assertEquals(createRelated(nList, nC4),
            aut.getMatches(testGraph, null, null, createValuation("x", "last", "y", "in")));
        assertEquals(createRelated(nList, nList),
            aut.getMatches(testGraph, null, null, createValuation("x", "List", "y", "List")));
        assertEquals(createRelated(nList, nC1),
            aut.getMatches(testGraph, null, null, createValuation("x", "first", "y", "in")));
    }

    @Test
    public void testInvMatch() {
        RegAut aut = createAutomaton("-(?.3)");
        Set<Result> result = new HashSet<>();
        addRelated(result, nI3, nI3);
        addRelated(result, nI3, nC3);
        addRelated(result, nI3, nC4);
        assertEquals(result, aut.getMatches(testGraph, null, null));
        assertEquals(result, aut.getMatches(testGraph, nI3, null));
        result.clear();
        assertEquals(result, aut.getMatches(testGraph, nI2, null));
        aut = createAutomaton("val.-val");
        result.clear();
        addRelated(result, nC3, nC3);
        addRelated(result, nC4, nC3);
        assertEquals(result, aut.getMatches(testGraph, null, nC3));
        assertEquals(result, aut.getMatches(testGraph, null, nC3));
        result.clear();
        addRelated(result, nC4, nC4);
        addRelated(result, nC3, nC4);
        assertEquals(result, aut.getMatches(testGraph, null, nC4));
        assertEquals(result, aut.getMatches(testGraph, null, nC4));
    }

    /** Factory method for the prototype automaton implementation. */
    abstract protected RegAut getPrototype();

    /**
     * Constructs an automaton from a regular expression.
     */
    protected RegAut createAutomaton(String regExpr) {
        RegAut result = null;
        try {
            RegExpr parsedRegExpr = RegExpr.parse(regExpr);
            result = this.calculator.compute(parsedRegExpr, getTypeGraph());
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
        return result;
    }

    /**
     * Tests that a given expression cannot be used to create an automaton.
     */
    protected void testError(String regExpr) {
        try {
            RegExpr parsedRegExpr = RegExpr.parse(regExpr);
            this.calculator.compute(parsedRegExpr, getTypeGraph());
            fail(regExpr + " should not yield a valid automaton");
        } catch (UnsupportedOperationException exc) {
            // success
        } catch (FormatException exc) {
            fail(regExpr + " should not yield a valid automaton");
        }
    }

    private static TypeGraph getTypeGraph() {
        return useLoadedTypeGraph ? loadedTypeGraph : implicitTypeGraph;
    }

    /**
     * Constructs a word (as a list of strings) from a space-separated string.
     */
    private static List<String> createWord(String text) {
        List<String> result = null;
        try {
            result = Arrays.asList(StringHandler.splitExpr(text, " "));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
        return result;
    }

    private Valuation createValuation(String... ids) {
        Valuation result = new Valuation();
        TypeFactory typeFactory = testGraph.getFactory()
            .getTypeFactory();
        TypeNode top = typeFactory.getTopNode();
        for (int i = 0; i < ids.length; i += 2) {
            String var = ids[i];
            String label = ids[i + 1];
            TypeEdge value = typeFactory.createEdge(top, label, top);
            result.put(new LabelVar(var, EdgeRole.BINARY), value);
        }
        return result;
    }

    private Set<Result> createRelated(HostNode... nodes) {
        Set<Result> result = new HashSet<>();
        for (int i = 0; i < nodes.length; i += 2) {
            result.add(new Result(nodes[i], nodes[i + 1]));
        }
        return result;
    }

    protected static HostNode getNode(String selfLabel) {
        Collection<? extends HostEdge> edgeSet =
            testGraph.edgeSet(TypeLabel.createBinaryLabel(selfLabel));
        return edgeSet.iterator()
            .next()
            .source();
    }

    private static void addRelated(Set<Result> results, HostNode one, HostNode two) {
        results.add(new Result(one, two));
    }
}
