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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.ClassRule;
import org.junit.Test;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.grammar.Grammar;
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
     * Tests the algebra override: the overridden family is recorded on the
     * GTS and reaches the start graph and the derivation record.
     */
    @Test
    public void testAlgebraOverride() throws Exception {
        Grammar grammar = loadGrammar("attributed-graphs");
        GTS byDefault = explore(grammar, "");
        assertEquals(AlgebraFamily.DEFAULT, byDefault.getAlgebraFamily());
        GTS point = explore(grammar, "algebra=point");
        assertEquals(AlgebraFamily.POINT, point.getAlgebraFamily());
        assertEquals(AlgebraFamily.POINT, point.getRecord().getFamily(),
                     "The override should reach the derivation record");
        assertTrue(point.nodeCount() > 0);
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
    }

    /** Explores a fresh GTS with a given configuration, under a fixed
     * master seed, and returns the GTS. */
    private GTS explore(Grammar grammar, String config) throws Exception {
        return ExploreTestSupport.explore(grammar, config).getGTS();
    }
}
