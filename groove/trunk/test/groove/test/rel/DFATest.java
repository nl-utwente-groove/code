/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.test.rel;

import static groove.graph.Direction.INCOMING;
import static groove.graph.Direction.OUTGOING;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import groove.automaton.DFA;
import groove.automaton.DFAState;
import groove.automaton.RegAutCalculator;
import groove.automaton.RegExpr;
import groove.automaton.SimpleNFA;
import groove.grammar.QualName;
import groove.grammar.model.GrammarModel;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.Valuation;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeFactory;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeLabel;
import groove.graph.EdgeRole;
import groove.util.Groove;
import groove.util.parse.FormatException;

/**
 * Test class for the construction of NormalAutomata
 * @author Arend Rensink
 * @version $Revision $
 */
public class DFATest {
    /** Directory with test files (relative to the project) */
    static public final String GRAMMAR = "junit/samples/regexpr";
    /** Name of the type graph used in this test. */
    static public final QualName TYPE_NAME = QualName.name("construction");

    private final RegAutCalculator nfaCalculator = new RegAutCalculator(SimpleNFA.PROTOTYPE);

    private TypeGraph type;
    private boolean useType;
    private LabelVar xVar;
    private TypeEdge bEdge;
    private TypeLabel aLabel;

    /** Loads type graph. */
    @Before
    public void setUp() {
        try {
            GrammarModel view = Groove.loadGrammar(GRAMMAR);
            this.type = view.getTypeModel(TYPE_NAME)
                .toResource();
            this.xVar = new LabelVar("x", EdgeRole.BINARY);
            TypeFactory factory = this.type.getFactory();
            Set<? extends TypeEdge> bEdges = this.type.edgeSet(factory.createLabel("b"));
            this.bEdge = bEdges.iterator()
                .next();
            this.aLabel = TypeLabel.createLabel("a");
        } catch (FormatException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    /** Tests acceptance of words by the empty expression. */
    /** Tests equivalence of regular expressions. */
    @Test
    public void regExprEquivTest() {
        // tests for different atoms
        assertEquivalent("a", "flag:a", false);
        assertEquivalent("a", "type:a", false);
        assertEquivalent("type:a", "flag:a", false);
        assertEmpty("a", false);
        // tests for #
        assertEquivalent("type:#A", "type:A", true);
        assertEquivalent("type:#B", "type:A", false);
        this.useType = true;
        assertEquivalent("type:#A", "type:A", false);
        assertEquivalent("type:#B", "type:B", true);
        assertEquivalent("type:#A|type:#A1", "type:A", true);
        this.useType = false;
        // tests for =
        assertEmpty("=", false);
        assertEquivalent("=", "=*", true);
        assertEquivalent("=", "a", false);
        assertEquivalent("(a.b)*", "(a.b)+|=", true);
        // tests for ?
        this.useType = true;
        assertEquivalent("?", "a|b|c", true);
        assertEquivalent("?[a,b]", "a|b", true);
        assertEquivalent("?[^a]", "b|c", true);
        Valuation val = new Valuation();
        val.put(this.xVar, this.bEdge);
        assertEquivalent("?x[^a].?x", "b.b", val, true);
        assertEmpty("?x[^b]", val, true);
        assertEquivalent("?", "a|b|c", true);
        assertEquivalent("flag:?", "flag:c|flag:d", true);
        assertEquivalent("type:?", "type:A|type:B|type:C", true);
        this.useType = false;
        assertEmpty("?", true);
        assertEmpty("flag:?", true);
        assertEmpty("type:?", true);
        // tests for |
        assertEquivalent("a", "a|a", true);
        assertEquivalent("(a.b)|c", "c|(a.b)", true);
        assertEquivalent("((a.b)|c)|d*", "(a.b)|(c|d*)", true);
        assertEquivalent("a", "a|b", false);
        assertEquivalent("a", "a|=", false);
        assertEquivalent("a.b|c", "a.(b|c)", false);
        // tests for .
        assertEquivalent("a", "a.a", false);
        assertEquivalent("(a.b).c", "c.(a.b)", false);
        assertEquivalent("(a.b).c*", "a.(b.c*)", true);
        assertEquivalent("a", "a.=", true);
        assertEquivalent("a.=", "a", true);
        assertEquivalent("(a.b)+", "(a.b)*.a.b", true);
        // tests for -
        assertEmpty("-a", false);
        assertEquivalent("=", "-=", true);
        assertEquivalent("a", "-a", false);
        assertEquivalent("a", "--a", true);
        assertEquivalent("-(a.b)", "-b.-a", true);
        assertEquivalent("-(a|b)", "-a|-b", true);
        assertEquivalent("-((a.b)*)", "(-(a.b))*", true);
        assertEquivalent("-((a.b)+)", "(-(a.b))+", true);
        // tests for *
        assertEquivalent("(a.b)*", "a*.b*", false);
        assertEquivalent("(a.b)*", "(a.b)**", true);
        // tests for +
        assertEquivalent("(a.b)+", "a+.b+", false);
        assertEquivalent("(a.b)+", "(a.b)++", true);
        assertEquivalent("(a.b)*", "(a.b)+*", true);
        assertEquivalent("(a.b)*", "((a.b)*)+", true);
        assertEquivalent("(a.b)*", "(a.b)*|(a.b)+", true);
    }

    /** Additional construction test to exercise some of the other definitions. */
    @Test
    public void testConstruction() {
        SimpleNFA a = createNFA("a.-a");
        DFA forward = a.getDFA(OUTGOING, null);
        assertTrue(forward.isEquivalent(forward.toMinimised()));
        assertTrue(forward.toString() != null);
        DFAState state = forward.getStartState();
        assertTrue(state.isInitial());
        assertFalse(state.isFinal());
        Map<TypeLabel,DFAState> succMap = state.getLabelMap()
            .get(OUTGOING);
        Map<TypeLabel,DFAState> predMap = state.getLabelMap()
            .get(INCOMING);
        assertEquals(Collections.singleton(this.aLabel), succMap.keySet());
        assertTrue(predMap.isEmpty());
        state = succMap.get(this.aLabel);
        assertFalse(state.isInitial());
        assertFalse(state.isFinal());
        succMap = state.getLabelMap()
            .get(OUTGOING);
        predMap = state.getLabelMap()
            .get(INCOMING);
        assertTrue(succMap.isEmpty());
        assertEquals(Collections.singleton(this.aLabel), predMap.keySet());
        state = predMap.get(this.aLabel);
        assertFalse(state.isInitial());
        assertTrue(state.isFinal());
        succMap = state.getLabelMap()
            .get(OUTGOING);
        predMap = state.getLabelMap()
            .get(INCOMING);
        assertTrue(succMap.isEmpty());
        assertTrue(predMap.isEmpty());
        DFA backward = a.getDFA(INCOMING, null);
        backward.isEquivalent(forward);
    }

    /**
     * Tests if a regular expression (given as a string) rise to an empty automaton.
     */
    private void assertEmpty(String e, boolean empty) {
        assertEmpty(e, null, empty);
    }

    /**
     * Tests if a regular expression (given as a string) under a given
     * valuation gives rise to an empty automaton.
     */
    private void assertEmpty(String e, Valuation val, boolean empty) {
        SimpleNFA a = createNFA(e);
        assertEquals(empty, a.getDFA(OUTGOING, val)
            .isEmpty());
    }

    /**
     * Tests if two regular expressions (given as strings) give rise
     * to equivalent DFA's, i.e., with the same language.
     */
    private void assertEquivalent(String e1, String e2, boolean equiv) {
        assertEquivalent(e1, e2, null, equiv);
    }

    /**
     * Tests if two regular expressions (given as strings) under a given valuation
     * give rise to equivalent DFA's, i.e., with the same language.
     */
    private void assertEquivalent(String e1, String e2, Valuation val, boolean equiv) {
        SimpleNFA a1 = createNFA(e1);
        SimpleNFA a2 = createNFA(e2);
        assertEquals(equiv, a1.getDFA(OUTGOING, val)
            .isEquivalent(a2.getDFA(OUTGOING, val)));
    }

    /** Creates an NFA for a given regular expression. */
    private SimpleNFA createNFA(String expr) {
        SimpleNFA result = null;
        try {
            result = createNFA(RegExpr.parse(expr));
        } catch (FormatException exc) {
            fail("Regular expression parse error: " + exc.getMessage());
        }
        return result;
    }

    /** Creates an NFA for a given regular expression. */
    private SimpleNFA createNFA(RegExpr expr) {
        return (SimpleNFA) (this.useType ? this.nfaCalculator.compute(expr, this.type)
            : this.nfaCalculator.compute(expr));
    }
}
