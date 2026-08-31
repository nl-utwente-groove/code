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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.GrammarKey;
import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.grammar.RegExpMatching;
import nl.utwente.groove.util.AIGenerated;

/**
 * Tests the version repair of the legacy boolean {@code ignoreRegExp}
 * property key, which was replaced by the enum-valued {@code regExpMatching}
 * within grammar version 3.12: a stored {@code true} translates to the
 * sloppy discipline, while {@code false} (or an unparsable value) equals
 * the faithful default and is dropped. Like the {@code parallelEdges}
 * repair, the translation is not version-gated, since grammars saved during
 * the 3.12 development period carry the legacy key under the current
 * version stamp.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class RegExpMatchingKeyRepairTest {
    /** A stored {@code true} translates to an explicit sloppy discipline. */
    @Test
    public void testTranslateTrue() {
        var repaired = repair("true");
        assertEquals(RegExpMatching.SLOPPY, repaired.getRegExpMatching());
        assertEquals("sloppy", repaired.getProperty(GrammarKey.REG_EXP_MATCHING.getName()));
    }

    /** A stored {@code false} equals the faithful default and is dropped. */
    @Test
    public void testDropFalse() {
        var repaired = repair("false");
        assertEquals(RegExpMatching.FAITHFUL, repaired.getRegExpMatching());
        assertNull(repaired.getProperty(GrammarKey.REG_EXP_MATCHING.getName()));
    }

    /** An unparsable value is dropped, like {@code false}. */
    @Test
    public void testDropStaleValue() {
        var repaired = repair("banana");
        assertEquals(RegExpMatching.FAITHFUL, repaired.getRegExpMatching());
        assertNull(repaired.getProperty(GrammarKey.REG_EXP_MATCHING.getName()));
    }

    /** A bundle without the legacy key is left alone. */
    @Test
    public void testAbsentKeyKeepsDefault() {
        var properties = new GrammarProperties(true);
        var repaired = properties.repairVersion();
        assertEquals(RegExpMatching.FAITHFUL, repaired.getRegExpMatching());
        assertNull(repaired.getProperty(GrammarKey.REG_EXP_MATCHING.getName()));
    }

    /** Repairs a current-version properties bundle holding the legacy key
     * with a given value, and checks that the legacy key itself is gone
     * afterwards. */
    private GrammarProperties repair(String legacyValue) {
        var properties = new GrammarProperties(true);
        properties.setProperty(GrammarKey.IGNORE_REG_EXP, legacyValue);
        var repaired = properties.repairVersion();
        assertNull(repaired.getProperty(GrammarKey.IGNORE_REG_EXP));
        return repaired;
    }
}
