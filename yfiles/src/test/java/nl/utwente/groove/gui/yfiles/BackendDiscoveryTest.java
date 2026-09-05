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
}
