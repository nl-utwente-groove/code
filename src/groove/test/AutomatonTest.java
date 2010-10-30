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

import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.GraphFactory;
import groove.graph.GraphShape;
import groove.graph.Label;
import groove.graph.Node;
import groove.io.DefaultGxl;
import groove.io.Xml;
import groove.rel.Automaton;
import groove.rel.AutomatonCalculator;
import groove.rel.LabelVar;
import groove.rel.NodeRelation;
import groove.rel.RegExpr;
import groove.rel.SetNodeRelation;
import groove.rel.ValuationEdge;
import groove.rel.VarAutomaton;
import groove.util.ExprParser;
import groove.view.FormatException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

/**
 * Tests the available {@link Automaton} interface.
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("all")
public class AutomatonTest extends TestCase {
    /** Directory with test files (relative to the project) */
    static public final String GRAPH_TEST_DIR = "junit/graphs";

    AutomatonCalculator calculator = new AutomatonCalculator();

    List<String> wordEmpty, wordA, wordB, wordAA, wordAB, wordBB, wordBA,
            wordABC, wordAAA, wordAAABCAAA;
    /**
     * Graph loader used in this test case.
     */
    Xml loader = new DefaultGxl(GraphFactory.getInstance());
    String testGraphName = "regexpr-test-graph";
    GraphShape testGraph;

    Node nList, nC1, nC2, nC3, nC4, nI0, nI1, nI2, nI3;
    Set<Node> nC12, nC34;
    NodeRelation reflexive;

    @Override
    protected void setUp() throws Exception {
        this.wordEmpty = new ArrayList<String>();
        this.wordA = Arrays.asList(new String[] {"A"});
        this.wordB = Arrays.asList(new String[] {"B"});
        this.wordAA = Arrays.asList(new String[] {"A", "A"});
        this.wordAB = Arrays.asList(new String[] {"A", "B"});
        this.wordBB = Arrays.asList(new String[] {"B", "B"});
        this.wordBA = Arrays.asList(new String[] {"B", "A"});
        this.wordAAA = Arrays.asList(new String[] {"A", "A", "A"});
        this.wordABC = Arrays.asList(new String[] {"A", "B", "C"});
        this.wordAAABCAAA =
            Arrays.asList(new String[] {"A", "A", "A", "B", "C", "A", "A", "A"});
        this.testGraph =
            this.loader.unmarshalGraph(new File(GRAPH_TEST_DIR + "/"
                + this.testGraphName + ".gxl"));
        this.nList = getNode("List");
        this.nC1 = getNode("n1");
        this.nC2 = getNode("n2");
        this.nC3 = getNode("n3");
        this.nC4 = getNode("n4");
        this.nI0 = getNode("0");
        this.nI1 = getNode("1");
        this.nI2 = getNode("2");
        this.nI3 = getNode("3");
        this.nC12 = new HashSet<Node>();
        this.nC12.add(this.nC1);
        this.nC12.add(this.nC2);
        this.nC34 = new HashSet<Node>();
        this.nC34.add(this.nC3);
        this.nC34.add(this.nC4);
        this.reflexive = new SetNodeRelation(this.testGraph);
        this.reflexive.addRelated(this.nList, this.nList);
        this.reflexive.addRelated(this.nC1, this.nC1);
        this.reflexive.addRelated(this.nC2, this.nC2);
        this.reflexive.addRelated(this.nC3, this.nC3);
        this.reflexive.addRelated(this.nC4, this.nC4);
        this.reflexive.addRelated(this.nI0, this.nI0);
        this.reflexive.addRelated(this.nI1, this.nI1);
        this.reflexive.addRelated(this.nI2, this.nI2);
        this.reflexive.addRelated(this.nI3, this.nI3);
    }

    public void testEmptyAccepts() {
        try {
            Automaton aut = createAutomaton("=");
            assertTrue(aut.accepts(this.wordEmpty));
            assertFalse(aut.accepts(this.wordA));
            aut = createAutomaton("D*");
            assertTrue(aut.accepts(this.wordEmpty));
            assertFalse(aut.accepts(this.wordA));
            aut = createAutomaton("D+");
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    public void testAtomAccepts() {
        try {
            Automaton aut = createAutomaton("A");
            assertTrue(aut.accepts(this.wordA));
            assertFalse(aut.accepts(this.wordB));
            assertFalse(aut.accepts(this.wordEmpty));
            assertFalse(aut.accepts(this.wordAA));
            assertFalse(aut.accepts(this.wordAB));
            assertFalse(aut.accepts(this.wordBA));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    public void testWildcardAccepts() {
        try {
            Automaton aut = createAutomaton("?");
            assertTrue(aut.accepts(this.wordA));
            assertTrue(aut.accepts(this.wordB));
            assertFalse(aut.accepts(this.wordEmpty));
            assertFalse(aut.accepts(this.wordAA));
            assertFalse(aut.accepts(this.wordAB));
            assertFalse(aut.accepts(this.wordBA));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    public void testSeqAccepts() {
        try {
            Automaton aut = createAutomaton("A.B");
            assertFalse(aut.accepts(this.wordA));
            assertFalse(aut.accepts(this.wordB));
            assertFalse(aut.accepts(this.wordEmpty));
            assertFalse(aut.accepts(this.wordAA));
            assertTrue(aut.accepts(this.wordAB));
            assertFalse(aut.accepts(this.wordBA));
            aut = createAutomaton("A.=");
            assertTrue(aut.accepts(this.wordA));
            assertFalse(aut.accepts(this.wordB));
            assertFalse(aut.accepts(this.wordEmpty));
            assertFalse(aut.accepts(this.wordAA));
            assertFalse(aut.accepts(this.wordAB));
            aut = createAutomaton("=.B");
            assertFalse(aut.accepts(this.wordA));
            assertTrue(aut.accepts(this.wordB));
            assertFalse(aut.accepts(this.wordEmpty));
            assertFalse(aut.accepts(this.wordAA));
            assertFalse(aut.accepts(this.wordAB));
            aut = createAutomaton("A.(=|B)");
            assertTrue(aut.accepts(this.wordA));
            assertFalse(aut.accepts(this.wordB));
            assertFalse(aut.accepts(this.wordEmpty));
            assertFalse(aut.accepts(this.wordAA));
            assertTrue(aut.accepts(this.wordAB));
            assertFalse(aut.accepts(this.wordBA));
            aut = createAutomaton("(=|A).(=|B)");
            assertTrue(aut.accepts(this.wordA));
            assertTrue(aut.accepts(this.wordB));
            assertTrue(aut.accepts(this.wordEmpty));
            assertFalse(aut.accepts(this.wordAA));
            assertTrue(aut.accepts(this.wordAB));
            assertFalse(aut.accepts(this.wordBA));
            aut = createAutomaton("A.B.C");
            assertFalse(aut.accepts(this.wordA));
            assertFalse(aut.accepts(this.wordB));
            assertFalse(aut.accepts(createWord("C")));
            assertFalse(aut.accepts(this.wordEmpty));
            assertFalse(aut.accepts(createWord("A B")));
            assertFalse(aut.accepts(createWord("A C")));
            assertFalse(aut.accepts(createWord("B C")));
            assertTrue(aut.accepts(createWord("A B C")));
            assertFalse(aut.accepts(createWord("B C A")));
            assertFalse(aut.accepts(createWord("A B B")));
            assertFalse(aut.accepts(createWord("A B C D")));
            aut = createAutomaton("A.=.C");
            assertFalse(aut.accepts(this.wordA));
            assertFalse(aut.accepts(createWord("C")));
            assertFalse(aut.accepts(this.wordEmpty));
            assertFalse(aut.accepts(createWord("A B")));
            assertTrue(aut.accepts(createWord("A C")));
            assertFalse(aut.accepts(createWord("B C")));
            assertFalse(aut.accepts(createWord("A B C")));
            assertFalse(aut.accepts(createWord("A C B")));
            aut = createAutomaton("A.(A|B).(B|C)");
            assertFalse(aut.accepts(this.wordA));
            assertFalse(aut.accepts(this.wordB));
            assertFalse(aut.accepts(createWord("C")));
            assertFalse(aut.accepts(this.wordEmpty));
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
            assertFalse(aut.accepts(this.wordEmpty));
            assertFalse(aut.accepts(this.wordA));
            assertTrue(aut.accepts(this.wordB));
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
            assertFalse(aut.accepts(this.wordEmpty));
            assertTrue(aut.accepts(this.wordA));
            assertFalse(aut.accepts(this.wordB));
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

    public void testChoiceAccepts() {
        try {
            Automaton aut = createAutomaton("A|B");
            assertTrue(aut.accepts(this.wordA));
            assertTrue(aut.accepts(this.wordB));
            assertFalse(aut.accepts(createWord("C")));
            assertFalse(aut.accepts(this.wordEmpty));
            assertFalse(aut.accepts(this.wordAA));
            assertFalse(aut.accepts(this.wordAB));
            aut = createAutomaton("A|=");
            assertTrue(aut.accepts(this.wordA));
            assertFalse(aut.accepts(this.wordB));
            assertTrue(aut.accepts(this.wordEmpty));
            assertFalse(aut.accepts(this.wordAA));
            assertFalse(aut.accepts(this.wordAB));
            aut = createAutomaton("=|A");
            assertTrue(aut.accepts(this.wordA));
            assertFalse(aut.accepts(this.wordB));
            assertTrue(aut.accepts(this.wordEmpty));
            assertFalse(aut.accepts(this.wordAA));
            assertFalse(aut.accepts(this.wordAB));
            aut = createAutomaton("A|B|C");
            assertTrue(aut.accepts(this.wordA));
            assertTrue(aut.accepts(this.wordB));
            assertTrue(aut.accepts(createWord("C")));
            assertFalse(aut.accepts(this.wordEmpty));
            assertFalse(aut.accepts(this.wordAA));
            assertFalse(aut.accepts(this.wordAB));
            aut = createAutomaton("A.B|C");
            assertFalse(aut.accepts(this.wordA));
            assertFalse(aut.accepts(this.wordB));
            assertTrue(aut.accepts(createWord("C")));
            assertFalse(aut.accepts(this.wordEmpty));
            assertFalse(aut.accepts(this.wordAA));
            assertTrue(aut.accepts(this.wordAB));
            assertFalse(aut.accepts(createWord("A B C")));
            assertFalse(aut.accepts(createWord("A C")));
            aut = createAutomaton("C|A.B");
            assertFalse(aut.accepts(this.wordA));
            assertFalse(aut.accepts(this.wordB));
            assertTrue(aut.accepts(createWord("C")));
            assertFalse(aut.accepts(this.wordEmpty));
            assertFalse(aut.accepts(this.wordAA));
            assertTrue(aut.accepts(this.wordAB));
            assertFalse(aut.accepts(createWord("A B C")));
            assertFalse(aut.accepts(createWord("A C")));
            aut = createAutomaton("A|B*");
            assertTrue(aut.accepts(this.wordA));
            assertTrue(aut.accepts(this.wordB));
            assertFalse(aut.accepts(createWord("C")));
            assertTrue(aut.accepts(this.wordEmpty));
            assertFalse(aut.accepts(this.wordAA));
            assertFalse(aut.accepts(this.wordAB));
            assertTrue(aut.accepts(this.wordBB));
            assertFalse(aut.accepts(this.wordBA));
            assertTrue(aut.accepts(createWord("B B B")));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    public void testPlusAccepts() {
        try {
            Automaton aut = createAutomaton("A+");
            assertFalse(aut.accepts(this.wordEmpty));
            assertTrue(aut.accepts(this.wordA));
            assertFalse(aut.accepts(this.wordB));
            assertTrue(aut.accepts(this.wordAA));
            assertFalse(aut.accepts(this.wordAB));
            assertFalse(aut.accepts(this.wordBA));
            assertTrue(aut.accepts(createWord("A A A A")));
            aut = createAutomaton("(A|B)+");
            assertFalse(aut.accepts(this.wordEmpty));
            assertTrue(aut.accepts(this.wordA));
            assertTrue(aut.accepts(this.wordB));
            assertTrue(aut.accepts(this.wordAA));
            assertTrue(aut.accepts(this.wordAB));
            assertTrue(aut.accepts(this.wordBA));
            assertFalse(aut.accepts(createWord("A C")));
            assertFalse(aut.accepts(createWord("C B")));
            assertTrue(aut.accepts(createWord("A A A A")));
            assertTrue(aut.accepts(createWord("A B A B B")));
            assertFalse(aut.accepts(createWord("A B C B B")));
            aut = createAutomaton("(A|=)+");
            assertTrue(aut.accepts(this.wordEmpty));
            assertTrue(aut.accepts(this.wordA));
            assertFalse(aut.accepts(this.wordB));
            assertTrue(aut.accepts(this.wordAA));
            assertFalse(aut.accepts(this.wordAB));
            assertFalse(aut.accepts(this.wordBA));
            assertTrue(aut.accepts(createWord("A A A A")));
            aut = createAutomaton("(A|B.B)+");
            assertFalse(aut.accepts(this.wordEmpty));
            assertTrue(aut.accepts(this.wordA));
            assertFalse(aut.accepts(this.wordB));
            assertTrue(aut.accepts(this.wordAA));
            assertFalse(aut.accepts(this.wordAB));
            assertFalse(aut.accepts(this.wordBA));
            assertTrue(aut.accepts(this.wordBB));
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

    public void testStarAccepts() {
        try {
            // these are just the plus tests for the empty word
            Automaton aut = createAutomaton("A*");
            assertTrue(aut.accepts(this.wordEmpty));
            aut = createAutomaton("(A|B)*");
            assertTrue(aut.accepts(this.wordEmpty));
            assertTrue(aut.accepts(this.wordAB));
            assertTrue(aut.accepts(this.wordBB));
            assertFalse(aut.accepts(this.wordABC));
            aut = createAutomaton("(A|=)*");
            assertTrue(aut.accepts(this.wordEmpty));
            aut = createAutomaton("(A|B.B)*");
            assertTrue(aut.accepts(this.wordEmpty));
            assertTrue(aut.accepts(this.wordBB));
            assertTrue(aut.accepts(this.wordAA));
            assertFalse(aut.accepts(this.wordAB));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    public void testInvAccepts() {
        try {
            Automaton aut = createAutomaton("-A");
            assertFalse(aut.accepts(this.wordA));
            // assertTrue(aut.accepts(wordA));
            assertFalse(aut.accepts(this.wordB));
            assertFalse(aut.accepts(this.wordEmpty));
            assertFalse(aut.accepts(this.wordAA));
            assertFalse(aut.accepts(this.wordAB));
            assertFalse(aut.accepts(this.wordBA));
            // aut = createAutomaton("-(A.B)");
            // assertFalse(aut.accepts(wordA));
            // assertFalse(aut.accepts(wordB));
            // assertFalse(aut.accepts(wordEmpty));
            // assertFalse(aut.accepts(wordAA));
            // assertFalse(aut.accepts(wordAB));
            // assertTrue(aut.accepts(wordBA));
            // aut = createAutomaton("-(A.B.C)");
            // assertFalse(aut.accepts(wordA));
            // assertFalse(aut.accepts(wordB));
            // assertFalse(aut.accepts(createWord("C")));
            // assertFalse(aut.accepts(wordEmpty));
            // assertFalse(aut.accepts(createWord("A B")));
            // assertFalse(aut.accepts(createWord("A C")));
            // assertFalse(aut.accepts(createWord("B C")));
            // assertFalse(aut.accepts(createWord("A B C")));
            // assertTrue(aut.accepts(createWord("C B A")));
            // assertFalse(aut.accepts(createWord("B C A")));
            // assertFalse(aut.accepts(createWord("A B B")));
            // assertFalse(aut.accepts(createWord("A B C D")));
            // aut = createAutomaton("-((A|B.C)+)");
            // assertFalse(aut.accepts(wordEmpty));
            // assertTrue(aut.accepts(wordA));
            // assertFalse(aut.accepts(wordB));
            // assertTrue(aut.accepts(wordAA));
            // assertFalse(aut.accepts(createWord("B C")));
            // assertTrue(aut.accepts(createWord("C B")));
            // assertFalse(aut.accepts(createWord("A B C")));
            // assertTrue(aut.accepts(createWord("A C B")));
            // assertFalse(aut.accepts(createWord("A B B B")));
            // assertFalse(aut.accepts(createWord("B C A")));
            // assertTrue(aut.accepts(createWord("C B A")));
            // assertFalse(aut.accepts(createWord("B A B")));
            // assertFalse(aut.accepts(createWord("B B C")));
            // assertTrue(aut.accepts(createWord("A C B A")));
            // assertTrue(aut.accepts(createWord("A A A C B A")));
            // assertFalse(aut.accepts(createWord("A B A C B A")));
            // assertFalse(aut.accepts(createWord("A A C B B")));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    public void testStarMatch() {
        try {
            Automaton aut = createAutomaton("next*");
            NodeRelation result = new SetNodeRelation(this.testGraph);
            result.addRelated(this.nC1, this.nC2);
            result.addRelated(this.nC1, this.nC3);
            result.addRelated(this.nC1, this.nC4);
            result.addRelated(this.nC2, this.nC3);
            result.addRelated(this.nC2, this.nC4);
            result.addRelated(this.nC3, this.nC4);
            result.addRelated(this.reflexive.getAllRelated());
            assertEquals(result, aut.getMatches(this.testGraph, null, null));
            result = new SetNodeRelation(this.testGraph);
            result.addRelated(this.nC1, this.nC4);
            result.addRelated(this.nC2, this.nC4);
            result.addRelated(this.nC3, this.nC4);
            result.addSelfRelated(this.nC4);
            assertEquals(
                result,
                aut.getMatches(this.testGraph, null,
                    Collections.singleton(this.nC4)));
            result = new SetNodeRelation(this.testGraph);
            result.addRelated(this.nC2, this.nC2);
            result.addRelated(this.nC2, this.nC3);
            result.addRelated(this.nC2, this.nC4);
            assertEquals(result, aut.getMatches(this.testGraph,
                Collections.singleton(this.nC2), null));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    public void testPlusMatch() {
        try {
            Automaton aut = createAutomaton("next+");
            NodeRelation result = new SetNodeRelation(this.testGraph);
            result.addRelated(this.nC1, this.nC3);
            result.addRelated(this.nC1, this.nC4);
            result.addRelated(this.nC2, this.nC3);
            result.addRelated(this.nC2, this.nC4);
            assertEquals(result,
                aut.getMatches(this.testGraph, this.nC12, this.nC34));
            result.addRelated(this.nC1, this.nC2);
            assertEquals(result,
                aut.getMatches(this.testGraph, this.nC12, null));
            result = new SetNodeRelation(this.testGraph);
            result.addRelated(this.nC1, this.nC3);
            result.addRelated(this.nC2, this.nC3);
            assertEquals(
                result,
                aut.getMatches(this.testGraph, null,
                    Collections.singleton(this.nC3)));
            aut = createAutomaton("?.2");
            result = new SetNodeRelation(this.testGraph);
            result.addRelated(this.nI2, this.nI2);
            result.addRelated(this.nI3, this.nI2);
            result.addRelated(this.nC2, this.nI2);
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    public void testWildcardMatch() {
        try {
            Automaton aut = createAutomaton("?.3");
            NodeRelation result = new SetNodeRelation(this.testGraph);
            result.addRelated(this.nI3, this.nI3);
            result.addRelated(this.nC3, this.nI3);
            result.addRelated(this.nC4, this.nI3);
            assertEquals(result, aut.getMatches(this.testGraph, null, null));
            assertEquals(
                result,
                aut.getMatches(this.testGraph, null,
                    Collections.singleton(this.nI3)));
            result = new SetNodeRelation(this.testGraph);
            assertEquals(
                result,
                aut.getMatches(this.testGraph, null,
                    Collections.singleton(this.nI2)));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    public void testWildcardIdMatch() {
        try {
            VarAutomaton aut = (VarAutomaton) createAutomaton("?x.3");
            NodeRelation result = new SetNodeRelation(this.testGraph);
            addRelated(result, this.nI3, new String[] {"x", "3"}, this.nI3);
            addRelated(result, this.nC3, new String[] {"x", "val"}, this.nI3);
            addRelated(result, this.nC4, new String[] {"x", "val"}, this.nI3);
            assertEquals(result,
                aut.getMatches(this.testGraph, null, null, null));
            result = new SetNodeRelation(this.testGraph);
            addRelated(result, this.nC3, new String[] {"x", "val"}, this.nI3);
            addRelated(result, this.nC4, new String[] {"x", "val"}, this.nI3);
            assertEquals(result, aut.getMatches(this.testGraph, null, null,
                Collections.singletonMap(new LabelVar("x", Label.BINARY),
                    (Label) DefaultLabel.createLabel("val"))));
            aut = (VarAutomaton) createAutomaton("?x.?x.3");
            result = new SetNodeRelation(this.testGraph);
            addRelated(result, this.nI3, new String[] {"x", "3"}, this.nI3);
            assertEquals(result,
                aut.getMatches(this.testGraph, null, null, null));
            aut = (VarAutomaton) createAutomaton("(List.?x.?y)+.?x");
            result = new SetNodeRelation(this.testGraph);
            addRelated(result, this.nList, new String[] {"x", "first", "y",
                "in"}, this.nC1);
            addRelated(result, this.nList,
                new String[] {"x", "last", "y", "in"}, this.nC4);
            addRelated(result, this.nList, new String[] {"x", "List", "y",
                "List"}, this.nList);
            assertEquals(result,
                aut.getMatches(this.testGraph, null, null, null));
            result = new SetNodeRelation(this.testGraph);
            addRelated(result, this.nList, new String[] {"x", "first", "y",
                "in"}, this.nC1);
            addRelated(result, this.nList,
                new String[] {"x", "last", "y", "in"}, this.nC4);
            assertEquals(result, aut.getMatches(this.testGraph, null, null,
                Collections.singletonMap(new LabelVar("y", Label.BINARY),
                    (Label) DefaultLabel.createLabel("in"))));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    public void testInvMatch() {
        try {
            Automaton aut = createAutomaton("-(?.3)");
            NodeRelation result = new SetNodeRelation(this.testGraph);
            result.addRelated(this.nI3, this.nI3);
            result.addRelated(this.nI3, this.nC3);
            result.addRelated(this.nI3, this.nC4);
            assertEquals(result, aut.getMatches(this.testGraph, null, null));
            assertEquals(result, aut.getMatches(this.testGraph,
                Collections.singleton(this.nI3), null));
            result = new SetNodeRelation(this.testGraph);
            assertEquals(result, aut.getMatches(this.testGraph,
                Collections.singleton(this.nI2), null));
            aut = createAutomaton("val.-val");
            result = new SetNodeRelation(this.testGraph);
            result.addRelated(this.nC3, this.nC3);
            result.addRelated(this.nC4, this.nC4);
            result.addRelated(this.nC3, this.nC4);
            result.addRelated(this.nC4, this.nC3);
            assertEquals(result,
                aut.getMatches(this.testGraph, null, this.nC34));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
    }

    /**
     * Constructs an automaton from a regular expression.
     */
    protected Automaton createAutomaton(String regExpr) throws FormatException {
        return this.calculator.compute(RegExpr.parse(regExpr));
    }

    /**
     * Construcs a word (as a list of strings) from a space-separated string.
     */
    protected List<String> createWord(String text) throws FormatException {
        return Arrays.asList(ExprParser.splitExpr(text, " "));
    }

    protected Node getNode(String selfLabel) {
        Collection<? extends Edge> edgeSet =
            this.testGraph.labelEdgeSet(DefaultLabel.createLabel(selfLabel));
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
        result.addRelated(new ValuationEdge(key, image, idMap));
    }

    protected void addRelated(NodeRelation result, Node key,
            Map<LabelVar,Label> idMap, Node image) {
        result.addRelated(new ValuationEdge(key, image, idMap));
    }
}
