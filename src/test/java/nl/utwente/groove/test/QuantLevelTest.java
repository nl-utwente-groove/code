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
package nl.utwente.groove.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.RuleModel;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Groove;

/**
 * Tests explicit quantifier level names on test- and let-edges
 * ({@code use=q:test:expr} and {@code new=q:let:field=expr}, gh #725):
 * the named level must be honoured during normalisation, so that the
 * test or assignment is evaluated on the named quantification level
 * rather than the level of its source node. Also tests the error cases
 * of an undefined level name and a level name incompatible with the
 * source node's level.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@SuppressWarnings("javadoc")
public class QuantLevelTest {
    /** Returns a named test grammar. */
    private GrammarModel getGrammar(String grammarName) {
        try {
            return Groove.loadGrammar("junit/rules/" + grammarName);
        } catch (IOException exc) {
            Assert.fail(exc.getMessage());
            throw new IllegalStateException();
        }
    }

    /** Asserts that a rule model has an error containing a given text. */
    private void assertError(RuleModel model, String text) {
        assertTrue(model.hasErrors());
        assertTrue("Unexpected errors: " + model.getErrors(),
                   model.getErrors().toString().contains(text));
    }

    /**
     * Applies the named rule of the quantLevel grammar to the host graph
     * {@code <ruleName>-<hostNr>} and returns the number of edges with a given
     * label in the resulting graph.
     */
    private int applyAndCount(String ruleName, int hostNr, String label) throws Exception {
        var grammarModel = getGrammar("quantLevel");
        grammarModel
            .setLocalActiveNames(ResourceKind.HOST, QualName.name(ruleName + "-" + hostNr));
        var grammar = grammarModel.toGrammar();
        GTS gts = new GTS(grammar);
        GraphState state = gts.startState();
        var match = state
            .getMatches()
            .stream()
            .filter(m -> m.getEvent().getAction().getQualName().toString().equals(ruleName))
            .findFirst()
            .orElseThrow();
        GraphState target = gts.getMatchApplier().apply(state, match).target();
        return (int) target
            .getGraph()
            .edgeSet()
            .stream()
            .filter(e -> e.label().text().equals(label))
            .count();
    }

    /** A test on a top-level node with an explicit quantifier level filters
     * the instances of that quantifier: only the B-nodes whose y-value
     * exceeds the A-node's x-value are marked. */
    @Test
    public void testQuantifiedTest() throws Exception {
        var ruleModel = getGrammar("quantLevel").getRuleModel(QualName.parse("markTest"));
        assertFalse("Unexpected errors: " + ruleModel.getErrors(), ruleModel.hasErrors());
        assertEquals(2, applyAndCount("markTest", 0, "mark"));
    }

    /** A creator let-edge with an explicit quantifier level assigns the field
     * once the quantifier has at least one instance. */
    @Test
    public void testQuantifiedLetCreates() throws Exception {
        var ruleModel = getGrammar("quantLevel").getRuleModel(QualName.parse("guardLet"));
        assertFalse("Unexpected errors: " + ruleModel.getErrors(), ruleModel.hasErrors());
        assertEquals(1, applyAndCount("guardLet", 0, "x"));
    }

    /** A creator let-edge with an explicit quantifier level assigns nothing
     * if the quantifier has no instances. Without the level, the field would
     * be assigned unconditionally. */
    @Test
    public void testQuantifiedLetVacuous() throws Exception {
        assertEquals(0, applyAndCount("guardLet", 1, "x"));
    }

    /** A level name that does not correspond to any quantifier node is an error. */
    @Test
    public void testUndefinedLevel() {
        assertError(getGrammar("quantLevelErrors").getRuleModel(QualName.parse("undefLevel")),
                    "Undefined nesting level");
    }

    /** A level name naming a quantifier that is incomparable with the source
     * node's own level is an error. */
    @Test
    public void testSiblingLevel() {
        assertError(getGrammar("quantLevelErrors").getRuleModel(QualName.parse("siblingLevel")),
                    "incompatible");
    }
}
