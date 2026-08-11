/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */

package nl.utwente.groove.test.verify;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.stream.Collectors;

import org.junit.Test;

import org.junit.Assert;
import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.explore.ExploreResult;
import nl.utwente.groove.explore.ExploreResult.Lasso;
import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.Generator;
import nl.utwente.groove.explore.LTLExploreType;
import nl.utwente.groove.explore.strategy.Boundary;
import nl.utwente.groove.explore.strategy.GraphNodeSizeBoundary;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the CTLStarFormula class.
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class LTLTest {
    private LTLExploreType.Kind kind;
    /** Transition system used by this test. */
    private GTS gts;

    /**
     * Tests whether the circular buffer fulfils certain properties and whether
     * the number of counter examples is correct for other properties.
     */
    @Test
    public void testCircularBuffer() {
        prepare("circular-buffer");
    }

    /** Test on a specially designed transition system. */
    @Test
    public void testNormal() {
        prepare(LTLExploreType.Kind.PLAIN);
        testMC();
    }

    /** Test on a specially designed transition system. */
    @Test
    public void testBounded() {
        prepare(LTLExploreType.Kind.BOUNDED);
        testMC();
    }

    /** Test on a specially designed transition system. */
    @Test
    public void testPocket() {
        prepare(LTLExploreType.Kind.POCKET);
        testMC();
    }

    /** Test the proper handling of attributes. */
    @Test
    public void testAttributes() {
        prepare(LTLExploreType.Kind.PLAIN);
        prepare("attributes");
        testFormula("F set_finished", true);
        testFormula("F set_finished(true)", true);
        testFormula("F set_finished( false )", false);
        testFormula("F 'set_finished(true)'", true);
        testFormula("F 'set_finished( true)'", false);
        testFormula("F set_finished()", false);
        testFormula("F set_finished(_)", true);

        testFormula("F set_score", true);
        testFormula("F set_score(n0, 1000)", true);
        testFormula("F set_score(n0, 100)", false);
        testFormula("F set_score(n0, _)", true);
        testFormula("F set_score(_, 100)", false);
    }

    /** Test the treatment of special transition labels (gh #855). */
    @Test
    public void testTransitionLabels() {
        prepare(LTLExploreType.Kind.PLAIN);
        prepare("mc-label");
        // rule p has special transition label 'go'
        testFormula("go U r", false);
        testFormula("go W r", true);
        testFormula("G(go|q)", true);
        testFormula("G go", false);
        // the special label replaces the rule name
        testFormula("G(p|q)", false);
        prepare("attributes-label");
        // rule set_gravity has special label 'grav %s', which does not
        // format to a parseable call and so is matched as a literal
        testFormula("F 'grav 9.81'", true);
        testFormula("F set_gravity(_)", false);
        // rule add_score has special label 'sc(%s,%s)', which formats
        // to a parseable call and so supports argument matching
        testFormula("F sc(n0, 100)", true);
        testFormula("F sc(n0, _)", true);
        testFormula("F sc(n3, _)", false);
        testFormula("F add_score", false);
    }

    /** Test on a specially designed transition system. */
    private void testMC() {
        prepare("mc");
        testFormula("p U r", false);
        testFormula("p W r", true);
        testFormula("GF q", true);
        testFormula("FG p", false);
        testFormula("!FG p", false);
        testFormula("q M (p|r)", true);
        testFormula("p R r", false);
        testFormula("r R (p|q)", true);
        testFormula("G p", false);
        testFormula("G(p|q)", true);
        testFormula("G(p|X(q|X q))", true);
        testFormula("X q", true);
    }

    /** Sets the LTL model-checking flavour. */
    private void prepare(LTLExploreType.Kind kind) {
        this.kind = kind;
    }

    /** Sets the GTS to a given grammar in the JUnit samples. */
    private void prepare(String grammarName) {
        try {
            Generator generator = new Generator("-v", "0", "junit/samples/" + grammarName);
            ExploreResult result = generator.start();
            if (result != null) {
                this.gts = result.getGTS();
            }
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    /** Tests the number of counterexamples in the current;y
     * set GTS for a given formula.
     * @return the exploration result, for further inspection
     */
    private ExploreResult testFormula(String formula, boolean succeed) {
        Boundary boundary = this.kind == LTLExploreType.Kind.PLAIN
            ? null
            : new GraphNodeSizeBoundary(0, 1);
        ExploreType exploreType = new LTLExploreType(this.kind, formula, boundary);
        try {
            Exploration exploration = exploreType.newExploration(this.gts, this.gts.startState());
            exploration.play();
            assertEquals(succeed, exploration.getResult()
                .isEmpty());
            return exploration.getResult();
        } catch (FormatException e) {
            throw Exceptions.illegalState("%s", e);
        }
    }

    /** Tests the shape of the counterexample produced by LTL model
     * checking (see gh #484): it must be a connected lasso starting in the
     * start state, whose recorded states and transitions coincide with the
     * result content, and which passes through a state violating the inner
     * until-property.
     */
    @Test
    @AIGenerated("Claude Fable 5, 2026-08")
    public void testCounterExample() {
        prepare(LTLExploreType.Kind.PLAIN);
        prepare("circular-buffer");
        ExploreResult result = testFormula("G(!get U put)", false);
        Lasso lasso = result.getLasso();
        Assert.assertNotNull(lasso);
        // the prefix runs from the start state to the start of the cycle
        GraphState current = this.gts.startState();
        for (GraphTransition trans : lasso.prefix()) {
            assertEquals(current, trans.source());
            current = trans.target();
        }
        // the cycle is non-empty, connected, and returns to its first state
        Assert.assertFalse(lasso.cycle().isEmpty());
        GraphState cycleStart = current;
        for (GraphTransition trans : lasso.cycle()) {
            assertEquals(current, trans.source());
            current = trans.target();
        }
        assertEquals(cycleStart, current);
        // the result contains exactly the transitions of the lasso,
        // and exactly the lasso transitions' source states
        var lassoTransitions = new HashSet<>(lasso.prefix());
        lassoTransitions.addAll(lasso.cycle());
        assertEquals(lassoTransitions, new HashSet<>(result.getTransitions()));
        var lassoStates = lassoTransitions
            .stream()
            .map(GraphTransition::source)
            .collect(Collectors.toSet());
        assertEquals(lassoStates, new HashSet<>(result.getStates()));
        // the lasso passes through a state violating (!get U put), i.e.,
        // one where put is not enabled (the full buffer)
        Assert
            .assertTrue(lassoStates
                .stream()
                .anyMatch(s -> s
                    .getTransitions()
                    .stream()
                    .noneMatch(t -> t.label().text().equals("put"))));
    }
}
