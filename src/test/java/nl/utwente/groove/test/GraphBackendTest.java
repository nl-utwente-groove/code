/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
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
package nl.utwente.groove.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.view.AspectGraphCanvas;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.gui.view.CtrlGraphCanvas;
import nl.utwente.groove.gui.view.CtrlGraphViewController;
import nl.utwente.groove.gui.view.GraphBackend;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.LTSGraphCanvas;
import nl.utwente.groove.gui.view.LTSGraphViewController;
import nl.utwente.groove.gui.view.PlainGraphViewController;
import nl.utwente.groove.util.AIGenerated;

/**
 * Checks the selection of the graph backend among the available ones: the user
 * preference wins if it names an available backend, otherwise the ranking decides.
 * In the main project only the JGraph backend is available; the selection is
 * checked on stand-in backends.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class GraphBackendTest {
    @Test
    void jGraphIsTheOnlyAvailableBackend() {
        assertEquals(1, GraphBackend.available().size());
        assertEquals(GraphBackend.JGRAPH, GraphBackend.instance().getName());
        assertEquals("JGraph", GraphBackend.instance().getDisplayName());
    }

    @Test
    void preferenceWinsOverRanking() {
        var jgraph = new StandIn(GraphBackend.JGRAPH);
        var yfiles = new StandIn(GraphBackend.YFILES);
        var other = new StandIn("other");
        // discovery order does not matter for the ranking
        List<GraphBackend> available = List.of(other, jgraph, yfiles);
        assertSame(yfiles, GraphBackend.select(available, null));
        assertSame(yfiles, GraphBackend.select(available, "unknown"));
        assertSame(jgraph, GraphBackend.select(available, GraphBackend.JGRAPH));
        assertSame(other, GraphBackend.select(available, "other"));
        // an unranked backend comes last
        assertSame(jgraph, GraphBackend.select(List.of(other, jgraph), null));
        assertSame(other, GraphBackend.select(List.of(other), null));
        assertThrows(IllegalStateException.class, () -> GraphBackend.select(List.of(), null));
    }

    /** Backend stand-in with a name and no canvases. */
    private static class StandIn implements GraphBackend {
        StandIn(String name) {
            this.name = name;
        }

        private final String name;

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public AspectGraphCanvas newAspectCanvas(AspectGraphViewController controller) {
            throw new UnsupportedOperationException();
        }

        @Override
        public LTSGraphCanvas newLTSCanvas(LTSGraphViewController controller) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CtrlGraphCanvas newCtrlCanvas(CtrlGraphViewController controller) {
            throw new UnsupportedOperationException();
        }

        @Override
        public GraphCanvas<Graph> newPlainCanvas(PlainGraphViewController controller) {
            throw new UnsupportedOperationException();
        }
    }
}
