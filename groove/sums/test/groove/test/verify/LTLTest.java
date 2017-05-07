/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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

package groove.test.verify;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import groove.explore.AcceptorValue;
import groove.explore.Exploration;
import groove.explore.ExploreResult;
import groove.explore.ExploreType;
import groove.explore.Generator;
import groove.explore.StrategyValue;
import groove.explore.encode.Serialized;
import groove.explore.encode.Template;
import groove.explore.strategy.GraphNodeSizeBoundary;
import groove.explore.strategy.Strategy;
import groove.lts.GTS;
import groove.util.Exceptions;
import groove.util.parse.FormatException;
import junit.framework.Assert;

/**
 * Tests the CTLStarFormula class.
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class LTLTest {
    private StrategyValue strategyValue;
    private Template<Strategy> strategyTemplate;
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
        prepare(StrategyValue.LTL);
        testMC();
    }

    /** Test on a specially designed transition system. */
    @Test
    public void testBounded() {
        prepare(StrategyValue.LTL_BOUNDED);
        testMC();
    }

    /** Test on a specially designed transition system. */
    @Test
    public void testPocket() {
        prepare(StrategyValue.LTL_POCKET);
        testMC();
    }

    /** Test the proper handling of attributes. */
    @Test
    public void testAttributes() {
        prepare(StrategyValue.LTL);
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
        //        testFormula("r R (p|q)", true);
        testFormula("G p", false);
        testFormula("G(p|q)", true);
        testFormula("G(p|X(q|X q))", true);
        testFormula("X q", true);
    }

    /** Sets the LTL strategy. */
    private void prepare(StrategyValue ltlStrategy) {
        this.strategyValue = ltlStrategy;
        this.strategyTemplate = ltlStrategy.getTemplate();
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
     * set GTS for a given formula. */
    private void testFormula(String formula, boolean succeed) {
        Serialized strategy = null;
        switch (this.strategyValue) {
        case LTL:
            strategy = this.strategyTemplate.toSerialized(formula);
            break;
        case LTL_BOUNDED:
        case LTL_POCKET:
            strategy = this.strategyTemplate.toSerialized(formula, new GraphNodeSizeBoundary(0, 1));
            break;
        default:
            throw Exceptions.UNREACHABLE; // there are no other LTL strategies
        }
        ExploreType exploreType = new ExploreType(strategy, AcceptorValue.CYCLE.toSerialized(), 1);
        try {
            Exploration exploration = exploreType.newExploration(this.gts, this.gts.startState());
            exploration.play();
            assertEquals(succeed, exploration.getResult()
                .isEmpty());
        } catch (FormatException e) {
            Assert.fail();
        }
    }
}
