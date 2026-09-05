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
package nl.utwente.groove.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.layout.ForestLayouter;
import nl.utwente.groove.gui.layout.Layouter;
import nl.utwente.groove.gui.layout.SpringLayouter;
import nl.utwente.groove.gui.view.AspectGraphCanvas;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.gui.view.ViewEdge;
import nl.utwente.groove.gui.view.ViewVertex;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;

/**
 * Checks that the backend-independent layouters, working through the canvas
 * interface only, still produce a layout: every vertex gets a position of its
 * own, vertices end up unmarked for layout, and the intermediate edge points
 * are cleared. The graph is shown on a headless {@link AspectGraphCanvas}, the way
 * the {@code Imager} does it.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class LayouterTest {
    /** Grammar containing the fixture type graph. */
    private static final String GRAMMAR = "junit/types/shadow.gps";
    /** Type graph with a handful of nodes, subtype edges and labelled edges. */
    private static final String TYPE_GRAPH = "OK-01-common-supertype";

    @Test
    void springLayoutPlacesAllVertices() throws IOException {
        checkLayout(SpringLayouter.PROTOTYPE);
    }

    @Test
    void forestLayoutPlacesAllVertices() throws IOException {
        checkLayout(ForestLayouter.PROTOTYPE);
    }

    /** Runs a complete layout with a given prototype layouter and checks the result. */
    private void checkLayout(Layouter prototype) throws IOException {
        AspectGraphCanvas canvas = loadTypeGraph();
        var controller = canvas.getController();
        controller.setLayouter(prototype);
        assertEquals(prototype.getName(), controller.getLayouter().getName());
        controller.doLayout(true);
        assertFalse(canvas.isLayouting(), "layouting flag not reset");
        Set<Point2D> positions = new HashSet<>();
        int vertexCount = 0;
        for (var cell : canvas.getCells()) {
            if (cell instanceof ViewVertex<?> vertex) {
                vertexCount++;
                assertFalse(vertex.isLayoutable(), "vertex still marked for layout");
                assertTrue(positions.add(vertex.getVisuals().getNodePos()),
                           "two vertices at " + vertex.getVisuals().getNodePos());
            } else if (cell instanceof ViewEdge<?> edge) {
                assertEquals(2, edge.getVisuals().getPoints().size(),
                             "edge points not cleared");
            }
        }
        assertTrue(vertexCount > 2, "fixture too small: " + vertexCount + " vertices");
    }

    /** Loads the fixture type graph into a headless type-graph canvas. */
    private AspectGraphCanvas loadTypeGraph() throws IOException {
        GrammarModel grammar = Groove.loadGrammar(GRAMMAR);
        AspectGraph typeGraph = grammar.getTypeModel(QualName.parse(TYPE_GRAPH)).getSource();
        var controller = new AspectGraphViewController(null, DisplayKind.TYPE, false);
        controller.setGrammar(grammar);
        AspectGraphCanvas result = controller.getCanvas();
        result.showGraph(typeGraph);
        return result;
    }
}
