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
import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.GraphFactory;
import groove.graph.GraphShape;
import groove.graph.Label;
import groove.graph.LabelStore;
import groove.graph.Node;
import groove.io.DefaultGxl;
import groove.io.Xml;
import groove.rel.Automaton;
import groove.rel.AutomatonCalculator;
import groove.rel.LabelVar;
import groove.rel.NodeRelation;
import groove.rel.RegExpr;
import groove.rel.RelationEdge;
import groove.rel.SetNodeRelation;
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
 * Tests the available {@link Automaton} interface.
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("all")
public class AutomatonTest {
    /** Directory with test files (relative to the project) */
    static public final String GRAPH_TEST_DIR = "junit/graphs";

    static final AutomatonCalculator calculator = new AutomatonCalculator();

    static List<String> wordEmpty, wordA, wordB, wordAA, wordAB, wordBB,
            wordBA, wordABC, wordAAA, wordAAABCAAA;
    /**
     * Graph loader used in this test case.
     */
    static final Xml loader = new DefaultGxl(GraphFactory.getInstance());
    static final String testGraphName = "regexpr-test-graph";
    static GraphShape testGraph;

    static Node nList, nC1, nC2, nC3, nC4, nI0, nI1, nI2, nI3;
    static Set<Node> nC12, nC34;
    static NodeRelation reflexive;

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
                loader.unmarshalGraph(new File(GRAPH_TEST_DIR + "/"
                    + testGraphName + ".gxl"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        LabelStore testStore = new LabelStore();
        for (Edge testEdge : testGraph.edgeSet()) {
            testStore.addLabel(testEdge.label());
        }
        calculator.setLabelStore(testStore);
        nList = getNode("List");
        nC1 = getNode("n1");
        nC2 = getNode("n2");
        nC3 = getNode("n3");
        nC4 = getNode("n4");
        nI0 = getNode("0");
        nI1 = getNode("1");
        nI2 = getNode("2");
        nI3 = getNode("3");
        nC12 = new HashSet<Node>();
        nC12.add(nC1);
        nC12.add(nC2);
        nC34 = new HashSet<Node>();
        nC34.add(nC3);
        nC34.add(nC4);
        reflexive = new SetNodeRelation(testGraph);
        reflexive.addRelated(nList, nList);
        reflexive.addRelated(nC1, nC1);
        reflexive.addRelated(nC2, nC2);
        reflexive.addRelated(nC3, nC3);
        reflexive.addRelated(nC4, nC4);
        reflexive.addRelated(nI0, nI0);
        reflexive.addRelated(nI1, nI1);
        reflexive.addRelated(nI2, nI2);
        reflexive.addRelated(nI3, nI3);
    }

    @Test
    public void testEmptyAccepts() {
        try {
            Automaton aut = createAutomaton("=");
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
            Automaton aut = createAutomaton("A");
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
            Automaton aut = createAutomaton("?");
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
            Automaton aut = createAutomaton("A.B");
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
            Automaton aut = createAutomaton("A|B");
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
            Automaton aut = createAutomaton("A+");
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
            Automaton aut = createAutomaton("A*");
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
            Automaton aut = createAutomaton("-A");
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
            Automaton aut = createAutomaton("next*");
            NodeRelation result = new SetNodeRelation(testGraph);
            result.addRelated(nC1, nC2);
            result.addRelated(nC1, nC3);
            result.addRelated(nC1, nC4);
            result.addRelated(nC2, nC3);
            result.addRelated(nC2, nC4);
            result.addRelated(nC3, nC4);
            result.addRelated(reflexive.getAllRelated());
            assertEquals(result, aut.getMatches(testGraph, null, null));
            result = new SetNodeRelation(testGraph);
            result.addRelated(nC1, nC4);
            result.addRelated(nC2, nC4);
            result.addRelated(nC3, nC4);
            result.addSelfRelated(nC4);
            assertEquals(result,
                aut.getMatches(testGraph, null, Collections.singleton(nC4)));
            result = new SetNodeRelation(testGraph);
            result.addRelated(nC2, nC2);
            result.addRelated(nC2, nC3);
            result.addRelated(nC2, nC4);
            assertEquals(result,
                aut.getMatches(testGraph, Collections.singleton(nC2), null));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    @Test
    public void testPlusMatch() {
        try {
            Automaton aut = createAutomaton("next+");
            NodeRelation result = new SetNodeRelation(testGraph);
            result.addRelated(nC1, nC3);
            result.addRelated(nC1, nC4);
            result.addRelated(nC2, nC3);
            result.addRelated(nC2, nC4);
            assertEquals(result, aut.getMatches(testGraph, nC12, nC34));
            result.addRelated(nC1, nC2);
            assertEquals(result, aut.getMatches(testGraph, nC12, null));
            result = new SetNodeRelation(testGraph);
            result.addRelated(nC1, nC3);
            result.addRelated(nC2, nC3);
            assertEquals(result,
                aut.getMatches(testGraph, null, Collections.singleton(nC3)));
            aut = createAutomaton("?.2");
            result = new SetNodeRelation(testGraph);
            result.addRelated(nI2, nI2);
            result.addRelated(nI3, nI2);
            result.addRelated(nC2, nI2);
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    @Test
    public void testWildcardMatch() {
        try {
            Automaton aut = createAutomaton("?.3");
            NodeRelation result = new SetNodeRelation(testGraph);
            result.addRelated(nI3, nI3);
            result.addRelated(nC3, nI3);
            result.addRelated(nC4, nI3);
            assertEquals(result, aut.getMatches(testGraph, null, null));
            assertEquals(result,
                aut.getMatches(testGraph, null, Collections.singleton(nI3)));
            result = new SetNodeRelation(testGraph);
            assertEquals(result,
                aut.getMatches(testGraph, null, Collections.singleton(nI2)));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    @Test
    public void testWildcardIdMatch() {
        try {
            Automaton aut = createAutomaton("?x.3");
            NodeRelation result = new SetNodeRelation(testGraph);
            addRelated(result, nI3, new String[] {"x", "3"}, nI3);
            addRelated(result, nC3, new String[] {"x", "val"}, nI3);
            addRelated(result, nC4, new String[] {"x", "val"}, nI3);
            assertEquals(result, aut.getMatches(testGraph, null, null, null));
            result = new SetNodeRelation(testGraph);
            addRelated(result, nC3, new String[] {"x", "val"}, nI3);
            addRelated(result, nC4, new String[] {"x", "val"}, nI3);
            assertEquals(result, aut.getMatches(testGraph, null, null,
                Collections.singletonMap(new LabelVar("x", Label.BINARY),
                    (Label) DefaultLabel.createLabel("val"))));
            aut = createAutomaton("?x.?x.3");
            result = new SetNodeRelation(testGraph);
            addRelated(result, nI3, new String[] {"x", "3"}, nI3);
            assertEquals(result, aut.getMatches(testGraph, null, null, null));
            aut = createAutomaton("(List.?x.?y)+.?x");
            result = new SetNodeRelation(testGraph);
            addRelated(result, nList, new String[] {"x", "first", "y", "in"},
                nC1);
            addRelated(result, nList, new String[] {"x", "last", "y", "in"},
                nC4);
            addRelated(result, nList, new String[] {"x", "List", "y", "List"},
                nList);
            assertEquals(result, aut.getMatches(testGraph, null, null, null));
            result = new SetNodeRelation(testGraph);
            addRelated(result, nList, new String[] {"x", "first", "y", "in"},
                nC1);
            addRelated(result, nList, new String[] {"x", "last", "y", "in"},
                nC4);
            assertEquals(result, aut.getMatches(testGraph, null, null,
                Collections.singletonMap(new LabelVar("y", Label.BINARY),
                    (Label) DefaultLabel.createLabel("in"))));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    @Test
    public void testInvMatch() {
        try {
            Automaton aut = createAutomaton("-(?.3)");
            NodeRelation result = new SetNodeRelation(testGraph);
            result.addRelated(nI3, nI3);
            result.addRelated(nI3, nC3);
            result.addRelated(nI3, nC4);
            assertEquals(result, aut.getMatches(testGraph, null, null));
            assertEquals(result,
                aut.getMatches(testGraph, Collections.singleton(nI3), null));
            result = new SetNodeRelation(testGraph);
            assertEquals(result,
                aut.getMatches(testGraph, Collections.singleton(nI2), null));
            aut = createAutomaton("val.-val");
            result = new SetNodeRelation(testGraph);
            result.addRelated(nC3, nC3);
            result.addRelated(nC4, nC4);
            result.addRelated(nC3, nC4);
            result.addRelated(nC4, nC3);
            assertEquals(result, aut.getMatches(testGraph, null, nC34));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    /**
     * Constructs an automaton from a regular expression.
     */
    protected Automaton createAutomaton(String regExpr) throws FormatException {
        RegExpr parsedRegExpr = RegExpr.parse(regExpr);
        return calculator.compute(parsedRegExpr);
    }

    /**
     * Constructs a word (as a list of strings) from a space-separated string.
     */
    protected List<String> createWord(String text) throws FormatException {
        return Arrays.asList(ExprParser.splitExpr(text, " "));
    }

    protected static Node getNode(String selfLabel) {
        Collection<? extends Edge> edgeSet =
            testGraph.labelEdgeSet(DefaultLabel.createLabel(selfLabel));
        if (edgeSet == null || edgeSet.isEmpty()) {
            return null;
        } else {
            return edgeSet.iterator().next().source();
        }
    }

    protected void addRelated(NodeRelation result, Node key, String[] ids,
            Node image) {
        Map<LabelVar,Label> idMap = new HashMap<LabelVar,Label>();
        for (int i = 0; i < ids.length; i += 2) {
            idMap.put(new LabelVar(ids[i], Label.BINARY),
                DefaultLabel.createLabel(ids[i + 1]));
        }
        result.addRelated(new RelationEdge(key, image, idMap));
    }

    protected void addRelated(NodeRelation result, Node key,
            Map<LabelVar,Label> idMap, Node image) {
        result.addRelated(new RelationEdge(key, image, idMap));
    }
}
