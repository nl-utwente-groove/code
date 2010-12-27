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
 * $Id: AutomatonTest.java,v 1.10 2008-02-05 13:28:27 rensink Exp $
 */
package groove.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import groove.graph.LabelKind;
import groove.graph.LabelStore;
import groove.graph.TypeLabel;
import groove.io.DefaultGxl;
import groove.io.Xml;
import groove.rel.LabelVar;
import groove.rel.RegAut;
import groove.rel.RegAut.Result;
import groove.rel.RegAutCalculator;
import groove.rel.RegExpr;
import groove.trans.DefaultHostGraph;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.util.ExprParser;
import groove.view.FormatException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the available {@link RegAut} interface.
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("all")
public class AutomatonTest {
    /** Directory with test files (relative to the project) */
    static public final String GRAPH_TEST_DIR = "junit/graphs";

    static final RegAutCalculator calculator = new RegAutCalculator();

    static List<String> wordEmpty, wordA, wordB, wordAA, wordAB, wordBB,
            wordBA, wordABC, wordAAA, wordAAABCAAA;
    /**
     * Graph loader used in this test case.
     */
    static final Xml loader = new DefaultGxl();
    static final String testGraphName = "regexpr-test-graph";
    static HostGraph testGraph;

    static HostNode nList, nC1, nC2, nC3, nC4, nI0, nI1, nI2, nI3;
    static Set<HostNode> nC12, nC34;
    static Set<Result> reflexive;
    static LabelStore testStore;

    @BeforeClass
    public static void setUp() {
        wordEmpty = new ArrayList<String>();
        wordA = Arrays.asList(new String[] {"A"});
        wordB = Arrays.asList(new String[] {"B"});
        wordAA = Arrays.asList(new String[] {"A", "A"});
        wordAB = Arrays.asList(new String[] {"A", "B"});
        wordBB = Arrays.asList(new String[] {"B", "B"});
        wordBA = Arrays.asList(new String[] {"B", "A"});
        wordAAA = Arrays.asList(new String[] {"A", "A", "A"});
        wordABC = Arrays.asList(new String[] {"A", "B", "C"});
        wordAAABCAAA =
            Arrays.asList(new String[] {"A", "A", "A", "B", "C", "A", "A", "A"});
        try {
            testGraph =
                new DefaultHostGraph(loader.unmarshalGraph(new File(
                    GRAPH_TEST_DIR + "/" + testGraphName + ".gxl")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        testStore = new LabelStore();
        for (HostEdge testEdge : testGraph.edgeSet()) {
            testStore.addLabel(testEdge.label());
        }
        testStore.addLabel(TypeLabel.createBinaryLabel("A"));
        testStore.addLabel(TypeLabel.createBinaryLabel("B"));
        testStore.addLabel(TypeLabel.createBinaryLabel("C"));
        testStore.addLabel(TypeLabel.createBinaryLabel("D"));
        nList = getNode("List");
        nC1 = getNode("n1");
        nC2 = getNode("n2");
        nC3 = getNode("n3");
        nC4 = getNode("n4");
        nI0 = getNode("0");
        nI1 = getNode("1");
        nI2 = getNode("2");
        nI3 = getNode("3");
        nC12 = new HashSet<HostNode>();
        nC12.add(nC1);
        nC12.add(nC2);
        nC34 = new HashSet<HostNode>();
        nC34.add(nC3);
        nC34.add(nC4);
        reflexive = new HashSet<Result>();
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

    @Test
    public void testEmptyAccepts() {
        try {
            RegAut aut = createAutomaton("=");
            assertTrue(aut.accepts(wordEmpty));
            assertFalse(aut.accepts(wordA));
            aut = createAutomaton("D*");
            assertTrue(aut.accepts(wordEmpty));
            assertFalse(aut.accepts(wordA));
            aut = createAutomaton("D+");
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    @Test
    public void testAtomAccepts() {
        try {
            RegAut aut = createAutomaton("A");
            assertTrue(aut.accepts(wordA));
            assertFalse(aut.accepts(wordB));
            assertFalse(aut.accepts(wordEmpty));
            assertFalse(aut.accepts(wordAA));
            assertFalse(aut.accepts(wordAB));
            assertFalse(aut.accepts(wordBA));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    @Test
    public void testWildcardAccepts() {
        try {
            RegAut aut = createAutomaton("?");
            assertTrue(aut.accepts(wordA));
            assertTrue(aut.accepts(wordB));
            assertFalse(aut.accepts(wordEmpty));
            assertFalse(aut.accepts(wordAA));
            assertFalse(aut.accepts(wordAB));
            assertFalse(aut.accepts(wordBA));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    @Test
    public void testSeqAccepts() {
        try {
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
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    @Test
    public void testChoiceAccepts() {
        try {
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
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    @Test
    public void testPlusAccepts() {
        try {
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
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    @Test
    public void testStarAccepts() {
        try {
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
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    @Test
    public void testInvAccepts() {
        try {
            RegAut aut = createAutomaton("-A");
            assertFalse(aut.accepts(wordA));
            assertFalse(aut.accepts(wordB));
            assertFalse(aut.accepts(wordEmpty));
            assertFalse(aut.accepts(wordAA));
            assertFalse(aut.accepts(wordAB));
            assertFalse(aut.accepts(wordBA));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    @Test
    public void testStarMatch() {
        try {
            RegAut aut = createAutomaton("next*");
            Set<RegAut.Result> results = new HashSet<RegAut.Result>();
            addRelated(results, nC1, nC2);
            addRelated(results, nC1, nC3);
            addRelated(results, nC1, nC4);
            addRelated(results, nC2, nC3);
            addRelated(results, nC2, nC4);
            addRelated(results, nC3, nC4);
            results.addAll(reflexive);
            assertEquals(results, aut.getMatches(testGraph, null, null));
            results.clear();
            addRelated(results, nC1, nC4);
            addRelated(results, nC2, nC4);
            addRelated(results, nC3, nC4);
            addRelated(results, nC4, nC4);
            assertEquals(results,
                aut.getMatches(testGraph, null, Collections.singleton(nC4)));
            results.clear();
            addRelated(results, nC2, nC2);
            addRelated(results, nC2, nC3);
            addRelated(results, nC2, nC4);
            assertEquals(results,
                aut.getMatches(testGraph, Collections.singleton(nC2), null));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    @Test
    public void testPlusMatch() {
        try {
            RegAut aut = createAutomaton("next+");
            Set<Result> results = new HashSet<Result>();
            addRelated(results, nC1, nC3);
            addRelated(results, nC1, nC4);
            addRelated(results, nC2, nC3);
            addRelated(results, nC2, nC4);
            assertEquals(results, aut.getMatches(testGraph, nC12, nC34));
            addRelated(results, nC1, nC2);
            assertEquals(results, aut.getMatches(testGraph, nC12, null));
            results.clear();
            addRelated(results, nC1, nC3);
            addRelated(results, nC2, nC3);
            assertEquals(results,
                aut.getMatches(testGraph, null, Collections.singleton(nC3)));
            aut = createAutomaton("?.2");
            results.clear();
            addRelated(results, nI2, nI2);
            addRelated(results, nI3, nI2);
            addRelated(results, nC2, nI2);
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    @Test
    public void testWildcardMatch() {
        try {
            RegAut aut = createAutomaton("?.3");
            Set<Result> result = new HashSet<Result>();
            addRelated(result, nI3, nI3);
            addRelated(result, nC3, nI3);
            addRelated(result, nC4, nI3);
            assertEquals(result, aut.getMatches(testGraph, null, null));
            assertEquals(result,
                aut.getMatches(testGraph, null, Collections.singleton(nI3)));
            result.clear();
            assertEquals(result,
                aut.getMatches(testGraph, null, Collections.singleton(nI2)));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    @Test
    public void testWildcardIdMatch() {
        try {
            RegAut aut = createAutomaton("?x.3");
            Set<Result> result = new HashSet<Result>();
            addRelated(result, nI3, new String[] {"x", "3"}, nI3);
            addRelated(result, nC3, new String[] {"x", "val"}, nI3);
            addRelated(result, nC4, new String[] {"x", "val"}, nI3);
            assertEquals(result, aut.getMatches(testGraph, null, null, null));
            result.clear();
            addRelated(result, nC3, new String[] {"x", "val"}, nI3);
            addRelated(result, nC4, new String[] {"x", "val"}, nI3);
            assertEquals(result, aut.getMatches(testGraph, null, null,
                Collections.singletonMap(new LabelVar("x", LabelKind.BINARY),
                    TypeLabel.createBinaryLabel("val"))));
            aut = createAutomaton("?x.?x.3");
            result.clear();
            addRelated(result, nI3, new String[] {"x", "3"}, nI3);
            assertEquals(result, aut.getMatches(testGraph, null, null, null));
            aut = createAutomaton("(List.?x.?y)+.?x");
            result.clear();
            addRelated(result, nList, new String[] {"x", "first", "y", "in"},
                nC1);
            addRelated(result, nList, new String[] {"x", "last", "y", "in"},
                nC4);
            addRelated(result, nList, new String[] {"x", "List", "y", "List"},
                nList);
            assertEquals(result, aut.getMatches(testGraph, null, null, null));
            result.clear();
            addRelated(result, nList, new String[] {"x", "first", "y", "in"},
                nC1);
            addRelated(result, nList, new String[] {"x", "last", "y", "in"},
                nC4);
            assertEquals(result, aut.getMatches(testGraph, null, null,
                Collections.singletonMap(new LabelVar("y", LabelKind.BINARY),
                    TypeLabel.createBinaryLabel("in"))));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    @Test
    public void testInvMatch() {
        try {
            RegAut aut = createAutomaton("-(?.3)");
            Set<Result> result = new HashSet<Result>();
            addRelated(result, nI3, nI3);
            addRelated(result, nI3, nC3);
            addRelated(result, nI3, nC4);
            assertEquals(result, aut.getMatches(testGraph, null, null));
            assertEquals(result,
                aut.getMatches(testGraph, Collections.singleton(nI3), null));
            result.clear();
            assertEquals(result,
                aut.getMatches(testGraph, Collections.singleton(nI2), null));
            aut = createAutomaton("val.-val");
            result.clear();
            addRelated(result, nC3, nC3);
            addRelated(result, nC4, nC4);
            addRelated(result, nC3, nC4);
            addRelated(result, nC4, nC3);
            assertEquals(result, aut.getMatches(testGraph, null, nC34));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    /**
     * Constructs an automaton from a regular expression.
     */
    protected RegAut createAutomaton(String regExpr) throws FormatException {
        RegExpr parsedRegExpr = RegExpr.parse(regExpr);
        return calculator.compute(parsedRegExpr, testStore);
    }

    /**
     * Constructs a word (as a list of strings) from a space-separated string.
     */
    protected List<String> createWord(String text) throws FormatException {
        return Arrays.asList(ExprParser.splitExpr(text, " "));
    }

    protected static HostNode getNode(String selfLabel) {
        Collection<? extends HostEdge> edgeSet =
            testGraph.labelEdgeSet(TypeLabel.createBinaryLabel(selfLabel));
        if (edgeSet == null || edgeSet.isEmpty()) {
            return null;
        } else {
            return edgeSet.iterator().next().source();
        }
    }

    private static void addRelated(Set<Result> results, HostNode one,
            HostNode two) {
        results.add(new Result(one, two, null));
    }

    protected void addRelated(Set<Result> result, HostNode key, String[] ids,
            HostNode image) {
        Map<LabelVar,TypeLabel> idMap = new HashMap<LabelVar,TypeLabel>();
        for (int i = 0; i < ids.length; i += 2) {
            idMap.put(new LabelVar(ids[i], LabelKind.BINARY),
                TypeLabel.createBinaryLabel(ids[i + 1]));
        }
        result.add(new Result(key, image, idMap));
    }
}
