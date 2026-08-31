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
package nl.utwente.groove.gui.dialog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;

import org.junit.jupiter.api.Test;

import nl.utwente.groove.explore.feature.ExploreKey;
import nl.utwente.groove.util.AIGenerated;

/**
 * Structural tests for the exploration configuration dialog, run without
 * instantiating any GUI. Regression background: the dialog builds a row per
 * {@link ExploreKey} generically, but lays the rows out in hand-enumerated
 * sections — when the seed key was added (gh #897), it was initially in no
 * section, so its row was constructed, loaded and saved but never shown.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
public class ExploreConfigDialogTest {
    /**
     * Tests that the dialog sections partition the exploration keys: every
     * key appears in exactly one section, so no key can silently lack an
     * editor in the dialog.
     */
    @Test
    public void testSectionsPartitionKeys() {
        var seen = EnumSet.noneOf(ExploreKey.class);
        for (var entry : ExploreConfigDialog.SECTIONS.entrySet()) {
            for (var key : entry.getValue()) {
                assertTrue(seen.add(key),
                           "Key '%s' appears in more than one dialog section"
                               .formatted(key.getName()));
            }
        }
        assertEquals(EnumSet.allOf(ExploreKey.class), seen,
                     "Every exploration key must appear in a dialog section");
    }
}
