/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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
package nl.utwente.groove.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.GrammarKey;
import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.collect.DeltaMap;
import nl.utwente.groove.util.collect.DeltaMap.Delta;

/**
 * Tests the version repair of the legacy {@code disabledRules} property key,
 * which was replaced by the delta-map-valued {@code ruleEnabling} within
 * grammar version 3.11 (GROOVE 7.4.0) without a conversion, so that grammars
 * saved under earlier versions silently lost their disabled rules (gh #908).
 * The repair is not version-gated, since both keys were written under the
 * same version stamp.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class DisabledRulesKeyRepairTest {
    /** Every name in the legacy list becomes a removal entry. */
    @Test
    public void testTranslateNames() {
        var enabling = repair("start steady  markInput");
        assertEquals(Delta.REMOVE, enabling.get(QualName.parse("start")));
        assertEquals(Delta.REMOVE, enabling.get(QualName.parse("steady")));
        assertEquals(Delta.REMOVE, enabling.get(QualName.parse("markInput")));
        assertEquals(3, enabling.entrySet().size());
    }

    /** Qualified names are translated as such. */
    @Test
    public void testTranslateQualifiedName() {
        var enabling = repair("sub.rule");
        assertEquals(Delta.REMOVE, enabling.get(QualName.name("sub", "rule")));
        assertEquals(1, enabling.entrySet().size());
    }

    /** An empty legacy list is dropped without creating any entry. */
    @Test
    public void testDropEmptyList() {
        var enabling = repair("");
        assertTrue(enabling.entrySet().isEmpty());
    }

    /** A name that is not a valid rule name is dropped; the valid ones
     * around it survive. */
    @Test
    public void testDropInvalidName() {
        var enabling = repair("start 1bad steady");
        assertEquals(Delta.REMOVE, enabling.get(QualName.parse("start")));
        assertEquals(Delta.REMOVE, enabling.get(QualName.parse("steady")));
        assertEquals(2, enabling.entrySet().size());
    }

    /** During the brief coexistence of the two keys, entries of the new key
     * took precedence over the legacy list; the repair preserves that. */
    @Test
    public void testExistingEntryWins() {
        var properties = new GrammarProperties();
        var existing = new DeltaMap<QualName>();
        existing.set(QualName.parse("start"), Delta.ADD);
        existing.set(QualName.parse("other"), Delta.REMOVE);
        properties.setRuleEnabling(existing);
        properties.setProperty(GrammarKey.DISABLED_RULES, "start steady");
        var repaired = properties.repairVersion();
        assertNull(repaired.getProperty(GrammarKey.DISABLED_RULES));
        var enabling = repaired.getRuleEnabling();
        assertEquals(Delta.ADD, enabling.get(QualName.parse("start")));
        assertEquals(Delta.REMOVE, enabling.get(QualName.parse("steady")));
        assertEquals(Delta.REMOVE, enabling.get(QualName.parse("other")));
        assertEquals(3, enabling.entrySet().size());
    }

    /** A bundle without the legacy key is left alone. */
    @Test
    public void testAbsentKeyUntouched() {
        var properties = new GrammarProperties(true);
        var repaired = properties.repairVersion();
        assertNull(repaired.getProperty(GrammarKey.DISABLED_RULES));
        assertTrue(repaired.getRuleEnabling().entrySet().isEmpty());
    }

    /** Repairs a bundle holding the legacy key with a given value, checks
     * that the legacy key itself is gone afterwards, and returns the
     * resulting rule enabling map. */
    private DeltaMap<QualName> repair(String legacyValue) {
        var properties = new GrammarProperties();
        properties.setProperty(GrammarKey.DISABLED_RULES, legacyValue);
        var repaired = properties.repairVersion();
        assertNull(repaired.getProperty(GrammarKey.DISABLED_RULES));
        return repaired.getRuleEnabling();
    }
}
