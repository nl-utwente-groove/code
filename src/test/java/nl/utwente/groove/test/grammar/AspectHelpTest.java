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
package nl.utwente.groove.test.grammar;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.annotation.HelpMap;
import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.util.AIGenerated;

/**
 * Smoke tests for the aspect syntax-help machinery of {@link AspectKind},
 * which accounts for most of that class's uncovered code: it is otherwise
 * reachable only through the GUI's syntax-help tabs, so an aspect added
 * without help lines, or with a broken format string in its documentation,
 * would only surface as a runtime error in the editor.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class AspectHelpTest {
    /** The node and edge documentation maps of all grammar-resource graph
     * roles must be constructible, non-empty and free of null entries. */
    @Test
    public void testDocMaps() {
        for (GraphRole role : new GraphRole[] {GraphRole.HOST, GraphRole.RULE, GraphRole.TYPE}) {
            checkDocMap(AspectKind.getNodeDocMap(role), "node help for " + role);
            checkDocMap(AspectKind.getEdgeDocMap(role), "edge help for " + role);
        }
    }

    /** Checks that a help map is non-empty and has no null keys or tips. */
    private void checkDocMap(HelpMap map, String description) {
        assertFalse(map.isEmpty(), description + " is empty");
        for (var entry : map.entrySet()) {
            assertNotNull(entry.getKey(), description + " has a null syntax line");
            assertNotNull(entry.getValue(), description + " has a null tool tip");
        }
    }
}
