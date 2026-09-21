/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.test.explore;

import static nl.utwente.groove.test.explore.ExploreTestSupport.loadGrammar;
import static nl.utwente.groove.test.explore.ExploreTestSupport.loadGrammarModel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.ClassRule;
import org.junit.Test;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.host.ValueNode;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GTS.CollapseMode;
import nl.utwente.groove.test.MasterSeedGuard;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the per-run collapse and algebra overrides. These are per-GTS
 * features: they apply when a fresh state space is created (Restart) and are
 * recorded there; a continued exploration must be consistent with the
 * recorded values.
 * @author Arend Rensink
 * @version $Revision$
 */
public class OverridesTest {
    /** Restores the master-seed state that the tests in this class modify. */
    @ClassRule
    public static final MasterSeedGuard SEED_GUARD = new MasterSeedGuard();


    /**
     * Tests the collapse override: the overridden mode is recorded on the
     * GTS and reaches the state set. Equality collapse is strictly stronger
     * than the (grammar-default) isomorphism collapse, so it can only
     * enlarge the state space; an explicit isomorphism override coincides
     * with the ferryman grammar's default.
     */
    @Test
    public void testCollapseOverride() throws Exception {
        Grammar grammar = loadGrammar("ferryman");
        GTS byDefault = explore(grammar, "");
        assertEquals(CollapseMode.COLLAPSE_ISO_STRONG, byDefault.getCollapseMode());
        GTS equality = explore(grammar, "collapse=equality");
        assertEquals(CollapseMode.COLLAPSE_EQUAL, equality.getCollapseMode());
        assertTrue(equality.nodeCount() >= byDefault.nodeCount(),
                   "Equality collapse cannot shrink the state space");
        GTS iso = explore(grammar, "collapse=isomorphism");
        assertEquals(CollapseMode.COLLAPSE_ISO_STRONG, iso.getCollapseMode());
        assertEquals(byDefault.nodeCount(), iso.nodeCount(),
                     "Explicit isomorphism collapse coincides with the grammar default");
    }

    /**
     * Tests the algebra override: the overridden family is realised by the
     * grammar the GTS is built on, is recorded on the GTS, and reaches the
     * start graph, the derivation record and the rules' search plans. The
     * counter grammar has value nodes in the start graph and constants in
     * the rules, so a family mismatch between the two leaves the start state
     * without successors (gh #923).
     */
    @Test
    public void testAlgebraOverride() throws Exception {
        GrammarModel model = loadGrammarModel("attribute-count-to-n");
        GTS byDefault = explore(model, "");
        assertEquals(AlgebraFamily.DEFAULT, byDefault.getAlgebraFamily());
        assertNull(byDefault.getAlgebraOverride());
        assertTrue(byDefault.nodeCount() > 1);
        GTS big = explore(model, "algebra=big");
        assertEquals(AlgebraFamily.BIG, big.getAlgebraFamily());
        assertEquals(AlgebraFamily.BIG, big.getAlgebraOverride());
        assertEquals(AlgebraFamily.BIG, big.getGrammar().getProperties().getAlgebraFamily(),
                     "The override should be realised by the grammar");
        assertEquals(AlgebraFamily.BIG, big.getRecord().getFamily(),
                     "The override should reach the derivation record");
        var values = big.startState().getGraph().nodeSet().stream()
            .filter(ValueNode.class::isInstance).map(ValueNode.class::cast).toList();
        assertFalse(values.isEmpty());
        for (var value : values) {
            assertEquals(AlgebraFamily.BIG, value.getAlgebra().getFamily(),
                         "The override should reach the start graph");
        }
        assertEquals(byDefault.nodeCount(), big.nodeCount(),
                     "The big family should give the same state space as the default one");
        // the point family collapses all values, so it can only shrink the state space
        GTS point = explore(model, "algebra=point");
        assertEquals(AlgebraFamily.POINT, point.getAlgebraOverride());
        assertTrue(point.nodeCount() > 0);
        assertTrue(point.nodeCount() <= byDefault.nodeCount());
        // an explicit override equal to the grammar's family is no override
        // and needs no derived grammar
        GTS explicit = explore(model, "algebra=default");
        assertNull(explicit.getAlgebraOverride());
        assertSame(model.toGrammar(), explicit.getGrammar());
    }

    /**
     * Tests that a GTS built on the model's own grammar refuses an
     * exploration overriding the algebra family: the override can only be
     * realised by compiling the grammar through the exploration type.
     */
    @Test
    public void testAlgebraOverrideNeedsDerivedGrammar() throws Exception {
        GrammarModel model = loadGrammarModel("attribute-count-to-n");
        GTS gts = new GTS(model.toGrammar());
        var big = ExploreTypeConverter.toExploreType(ExploreConfig.parse("algebra=big"));
        assertThrows(IllegalStateException.class, () -> big.newExploration(gts, null));
    }

    /**
     * Tests the guard: a continued exploration cannot change the recorded
     * collapse mode or algebra family, but a continuation whose explicit
     * override resolves to the recorded value is allowed.
     */
    @Test
    public void testPerGtsGuard() throws Exception {
        Grammar grammar = loadGrammar("ferryman");
        GTS gts = explore(grammar, "");
        var equality = ExploreTypeConverter.toExploreType(ExploreConfig.parse("collapse=equality"));
        assertThrows(FormatException.class,
                     () -> equality.newExploration(gts, gts.startState()));
        var point = ExploreTypeConverter.toExploreType(ExploreConfig.parse("algebra=point"));
        assertThrows(FormatException.class, () -> point.newExploration(gts, gts.startState()));
        // an explicit override that resolves to the recorded value is fine
        var iso = ExploreTypeConverter.toExploreType(ExploreConfig.parse("collapse=isomorphism"));
        iso.newExploration(gts, gts.startState());
        // and so is inheriting the grammar values
        var inherit = ExploreTypeConverter.toExploreType(new ExploreConfig());
        inherit.newExploration(gts, gts.startState());
        // a GTS explored under an algebra override can only be continued
        // under the same override, not by inheriting the grammar's family
        GTS big = explore(loadGrammarModel("attribute-count-to-n"), "algebra=big");
        assertThrows(FormatException.class, () -> inherit.newExploration(big, big.startState()));
        assertThrows(FormatException.class, () -> point.newExploration(big, big.startState()));
        var alsoBig = ExploreTypeConverter.toExploreType(ExploreConfig.parse("algebra=big"));
        alsoBig.newExploration(big, big.startState());
    }

    /** Explores a fresh GTS with a given configuration, under a fixed
     * master seed, and returns the GTS. */
    private GTS explore(Grammar grammar, String config) throws Exception {
        return ExploreTestSupport.explore(grammar, config).getGTS();
    }

    /** Explores a fresh GTS over the grammar of a given model with a given
     * configuration, under a fixed master seed, and returns the GTS. */
    private GTS explore(GrammarModel model, String config) throws Exception {
        return ExploreTestSupport.explore(model, config).getGTS();
    }
}
