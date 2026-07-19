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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import nl.utwente.groove.explore.config.Bound;
import nl.utwente.groove.explore.config.Collapse;
import nl.utwente.groove.explore.config.Cost;
import nl.utwente.groove.explore.config.Count;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreKey;
import nl.utwente.groove.explore.config.Frontier;
import nl.utwente.groove.explore.config.Goal;
import nl.utwente.groove.explore.config.NextState;
import nl.utwente.groove.explore.config.Outcome;
import nl.utwente.groove.explore.config.Setting;
import nl.utwente.groove.explore.config.Successor;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests for the exploration configuration model in
 * {@code nl.utwente.groove.explore.config}: setting- and configuration-level
 * round trips, parse errors, and cross-key consistency checking.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ExploreConfigTest {
    /** Tests that a fresh configuration is all-default and unparses to the empty string. */
    @Test
    public void testDefaultConfig() throws FormatException {
        var config = new ExploreConfig();
        assertEquals("", config.unparse());
        for (var key : ExploreKey.values()) {
            assertTrue(config.isDefault(key));
            assertEquals(key.getDefaultKind(), config.getKind(key));
        }
        assertTrue(config.check().isEmpty());
        assertEquals(config, ExploreConfig.parse(""));
    }

    /** Tests that every kind of every key survives a textual round trip. */
    @Test
    public void testSettingRoundTrip() throws FormatException {
        for (var key : ExploreKey.values()) {
            for (var kind : key.getKindType().getEnumConstants()) {
                Setting setting = createSetting(kind);
                String text = key.parser().unparse(setting);
                assertEquals(setting, key.parser().parse(text),
                             "Round trip failed for key '%s', kind '%s', text '%s'"
                                 .formatted(key, kind, text));
            }
        }
    }

    /** Creates a non-default-content setting for a given kind. */
    private Setting createSetting(Setting.Kind kind) {
        return switch (kind.contentType()) {
        case NULL -> kind.createSetting();
        case INTEGER -> kind.createSetting(3);
        case STRING -> kind.createSetting("someName");
        case LIMIT -> kind.createSetting(new Bound.Limit(200, 50));
        };
    }

    /** Tests the exact textual form and round trip of a many-featured configuration. */
    @Test
    public void testConfigRoundTrip() throws FormatException {
        var config = new ExploreConfig();
        config.put(ExploreKey.NEXT, NextState.NEWEST.createSetting());
        config.put(ExploreKey.FRONTIER, Frontier.BEAM.createSetting(8));
        config.put(ExploreKey.GOAL, Goal.FORMULA.createSetting("a & b"));
        config.put(ExploreKey.COUNT, Count.COUNT.createSetting(3));
        config.put(ExploreKey.BOUND, Bound.SIZE.createSetting(new Bound.Limit(100, 20)));
        config.put(ExploreKey.COLLAPSE, Collapse.EQUALITY.createSetting());
        String text = config.unparse();
        assertEquals("next=newest frontier=8 goal=\"formula:a & b\" count=3"
            + " bound=size:100+20 collapse=equality", text);
        assertEquals(config, ExploreConfig.parse(text));
    }

    /** Tests that a limit without increment round-trips without the plus sign. */
    @Test
    public void testLimitWithoutIncrement() throws FormatException {
        var setting = Bound.SIZE.createSetting(new Bound.Limit(100, 0));
        var text = ExploreKey.BOUND.parser().unparse(setting);
        assertEquals("size:100", text);
        assertEquals(setting, ExploreKey.BOUND.parser().parse(text));
    }

    /** Tests the various parse error cases. */
    @Test
    public void testParseErrors() {
        assertThrows(FormatException.class, () -> ExploreConfig.parse("bogus=1"));
        assertThrows(FormatException.class, () -> ExploreConfig.parse("next=sideways"));
        assertThrows(FormatException.class, () -> ExploreConfig.parse("next=newest next=oldest"));
        assertThrows(FormatException.class, () -> ExploreConfig.parse("next"));
        assertThrows(FormatException.class, () -> ExploreConfig.parse("frontier=beam:x"));
    }

    /** Tests that a setting cannot be assigned to a key of another kind type. */
    @Test
    public void testPutWrongKind() {
        var config = new ExploreConfig();
        assertThrows(IllegalArgumentException.class,
                     () -> config.put(ExploreKey.GOAL, NextState.OLDEST.createSetting()));
    }

    /** Tests the cross-key consistency checks of the feature model. */
    @Test
    public void testCheck() {
        var config = new ExploreConfig();
        // oldest next-state selection requires all successors
        config.put(ExploreKey.SUCCESSOR, Successor.SINGLE.createSetting());
        assertFalse(config.check().isEmpty());
        // ... but not with a single-state frontier
        config.put(ExploreKey.FRONTIER, Frontier.SINGLE.createSetting());
        assertTrue(config.check().isEmpty());

        // random next-state selection requires in-order successors
        config = new ExploreConfig();
        config.put(ExploreKey.NEXT, NextState.RANDOM.createSetting());
        config.put(ExploreKey.SUCCESSOR, Successor.ALL_RANDOM.createSetting());
        assertFalse(config.check().isEmpty());

        // a beam frontier must be larger than 1
        config = new ExploreConfig();
        config.put(ExploreKey.FRONTIER, Frontier.BEAM.createSetting(1));
        assertFalse(config.check().isEmpty());

        // the trivial goals require a satisfying outcome
        config = new ExploreConfig();
        config.put(ExploreKey.OUTCOME, Outcome.VIOLATE.createSetting());
        assertFalse(config.check().isEmpty());
        config.put(ExploreKey.GOAL, Goal.ANY.createSetting());
        assertFalse(config.check().isEmpty());
        config.put(ExploreKey.GOAL, Goal.RULE.createSetting("someRule"));
        assertTrue(config.check().isEmpty());

        // without a goal there are no results to count
        config = new ExploreConfig();
        config.put(ExploreKey.GOAL, Goal.NONE.createSetting());
        config.put(ExploreKey.COUNT, Count.FIRST.createSetting());
        assertFalse(config.check().isEmpty());

        // a result count must be larger than 1
        config = new ExploreConfig();
        config.put(ExploreKey.COUNT, Count.COUNT.createSetting(1));
        assertFalse(config.check().isEmpty());

        // a cost bound requires a transition cost
        config = new ExploreConfig();
        config.put(ExploreKey.BOUND, Bound.COST.createSetting(new Bound.Limit(10, 0)));
        assertFalse(config.check().isEmpty());
        config.put(ExploreKey.COST, Cost.UNIFORM.createSetting());
        assertTrue(config.check().isEmpty());
    }
}
