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
import nl.utwente.groove.grammar.Semantics;
import nl.utwente.groove.util.AIGenerated;

/**
 * Tests the version repair of the legacy {@code parallelEdges} property key,
 * which was renamed to {@code semantics} (with new value names) within
 * grammar version 3.12: stored legacy values are translated to their new
 * equivalents, and stale values from before the key was wired are dropped.
 * The repair is not version-gated, since grammars saved during the 3.12
 * development period carry the legacy key under the current version stamp.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class SemanticsKeyRepairTest {
    /** The legacy multigraph modes translate to their new value names.
     * A translated {@code SPO-multi} equals the key's default, so by the
     * canonical-representation invariant of
     * {@link nl.utwente.groove.util.Properties} it is not stored explicitly
     * — and the pre-3.12 conversion must nevertheless not overwrite it
     * with {@code SPO-simple}. */
    @Test
    public void testTranslateMultiModes() {
        var repaired = repair("SPO");
        assertEquals(Semantics.SPO_MULTI, repaired.getSemantics());
        assertNull(repaired.getProperty(GrammarKey.SEMANTICS.getName()));
        repaired = repair("DPO");
        assertEquals(Semantics.DPO, repaired.getSemantics());
        assertEquals("DPO", repaired.getProperty(GrammarKey.SEMANTICS.getName()));
    }

    /** The legacy simple mode translates to an explicitly stored
     * {@code SPO-simple}, now that the key's default is {@code SPO-multi}. */
    @Test
    public void testTranslateSimpleMode() {
        var repaired = repair("none");
        assertEquals(Semantics.SPO_SIMPLE, repaired.getSemantics());
        assertEquals("SPO-simple", repaired.getProperty(GrammarKey.SEMANTICS.getName()));
    }

    /** A stale value from before the key was wired is dropped; the
     * pre-3.12 conversion then pins the simple semantics explicitly. */
    @Test
    public void testDropStaleValue() {
        var repaired = repair("true");
        assertEquals(Semantics.SPO_SIMPLE, repaired.getSemantics());
        assertEquals("SPO-simple", repaired.getProperty(GrammarKey.SEMANTICS.getName()));
    }

    /** A pre-3.12 bundle without the semantics key gets an explicitly
     * stored {@code SPO-simple}: such grammars must keep the simple-graph
     * semantics they were written under, while the absent-key default is
     * {@code SPO-multi}. */
    @Test
    public void testInjectSimpleIntoOldGrammar() {
        var properties = new GrammarProperties();
        var repaired = properties.repairVersion();
        assertEquals(Semantics.SPO_SIMPLE, repaired.getSemantics());
        assertEquals("SPO-simple", repaired.getProperty(GrammarKey.SEMANTICS.getName()));
    }

    /** A current-version bundle without the semantics key is left alone:
     * the {@code SPO-multi} default applies, unstored. */
    @Test
    public void testCurrentGrammarKeepsDefault() {
        var properties = new GrammarProperties(true);
        var repaired = properties.repairVersion();
        assertEquals(Semantics.SPO_MULTI, repaired.getSemantics());
        assertNull(repaired.getProperty(GrammarKey.SEMANTICS.getName()));
    }

    /** An explicitly stored semantics value survives the pre-3.12
     * conversion untouched. */
    @Test
    public void testExplicitValueSurvives() {
        var properties = new GrammarProperties();
        properties.setSemantics(Semantics.DPO);
        var repaired = properties.repairVersion();
        assertEquals(Semantics.DPO, repaired.getSemantics());
        assertEquals("DPO", repaired.getProperty(GrammarKey.SEMANTICS.getName()));
    }

    /** Repairs a properties bundle holding the legacy key with a given value,
     * and checks that the legacy key itself is gone afterwards. */
    private GrammarProperties repair(String legacyValue) {
        var properties = new GrammarProperties();
        properties.setProperty(GrammarKey.PARALLEL_EDGES, legacyValue);
        var repaired = properties.repairVersion();
        assertNull(repaired.getProperty(GrammarKey.PARALLEL_EDGES));
        return repaired;
    }
}
