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
package nl.utwente.groove.gui.yfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;
import java.util.ServiceLoader;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.gui.view.GraphBackend;
import nl.utwente.groove.util.AIGenerated;

/**
 * Checks that, with this unit on the class path, both backends are discovered and
 * the yFiles backend is the one selected.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class BackendDiscoveryTest {
    @Test
    void bothBackendsAreDiscovered() {
        var names = ServiceLoader
            .load(GraphBackend.class)
            .stream()
            .map(provider -> provider.get().getName())
            .toList();
        assertTrue(names.contains(GraphBackend.JGRAPH), "JGraph backend not found: " + names);
        assertTrue(names.contains(GraphBackend.YFILES), "yFiles backend not found: " + names);
    }

    @Test
    void yFilesBackendIsSelected() {
        var backend = GraphBackend.instance();
        assertInstanceOf(YFilesBackend.class, backend);
        assertEquals(GraphBackend.YFILES, backend.getName());
    }

    /**
     * The About box shows the notice of every available backend; that of yFiles has
     * to state the restriction and name the file that holds it in full.
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    @Test
    void yFilesBackendStatesItsRestriction() {
        var notices = GraphBackend
            .available()
            .stream()
            .map(GraphBackend::getLicenseNotice)
            .filter(Objects::nonNull)
            .toList();
        assertEquals(1, notices.size(), "notices: " + notices);
        var notice = notices.get(0);
        assertTrue(notice.contains("academic, non-commercial"), notice);
        assertTrue(notice.contains("LICENSE-YFILES.txt"), notice);
    }

    @Test
    void preferenceChoosesAmongTheAvailableBackends() {
        var available = GraphBackend.available();
        assertEquals(2, available.size(), "available: " + available);
        // no preference, or an unknown one: the ranking decides
        assertInstanceOf(YFilesBackend.class, GraphBackend.select(available, null));
        assertInstanceOf(YFilesBackend.class, GraphBackend.select(available, "none"));
        // a preference for an available backend wins over the ranking
        assertEquals(GraphBackend.JGRAPH,
                     GraphBackend.select(available, GraphBackend.JGRAPH).getName());
        assertEquals(GraphBackend.YFILES,
                     GraphBackend.select(available, GraphBackend.YFILES).getName());
        // the user sees the display names
        assertEquals("yFiles", GraphBackend.instance().getDisplayName());
    }
}
