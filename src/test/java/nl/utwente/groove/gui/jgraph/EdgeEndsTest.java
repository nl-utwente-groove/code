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
package nl.utwente.groove.gui.jgraph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Map;

import javax.swing.JScrollPane;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.gui.view.AspectGraphViewModel;
import nl.utwente.groove.gui.view.ParallelEdges;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.gui.view.ViewVertex;
import nl.utwente.groove.gui.view.cell.AspectEdgeCell;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;

/**
 * Tests the end points of edges in the JGraph backend: a straight edge between
 * aligned vertices runs vertically or horizontally, see
 * {@link nl.utwente.groove.gui.view.EdgeGeometry#alignedCentres}.
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class EdgeEndsTest {
    /** Grammar whose start graph is edited. */
    private static final String GRAMMAR = "junit/samples/ferryman.gps";

    @Test
    void stackedVerticesAreJoinedVertically() throws IOException {
        AspectJGraph canvas = editorCanvas();
        AspectEdgeCell edge = loneEdge(canvas);
        ViewVertex<AspectGraph> source = vertex(edge.getSourceVertex());
        ViewVertex<AspectGraph> target = vertex(edge.getTargetVertex());
        // the target above the source, its centre slightly to the right
        move(canvas, source, 100, 200);
        move(canvas, target, 113, 40);
        JEdgeView view = edgeView(canvas, edge);
        Point2D start = view.getPoint(0);
        Point2D end = view.getPoint(1);
        assertEquals(start.getX(), end.getX(), 0.01, "vertical edge");
        Rectangle2D sourceBounds = vertexView(canvas, source).getShapeBounds();
        Rectangle2D targetBounds = vertexView(canvas, target).getShapeBounds();
        assertTrue(start.getX() > sourceBounds.getMinX() && start.getX() < sourceBounds.getMaxX(),
                   "start within the source: " + start + " in " + sourceBounds);
        assertTrue(end.getX() > targetBounds.getMinX() && end.getX() < targetBounds.getMaxX(),
                   "end within the target: " + end + " in " + targetBounds);
        assertEquals(sourceBounds.getMinY(), start.getY(), 0.01, "leaves the source at the top");
        assertEquals(targetBounds.getMaxY(), end.getY(), 0.01, "enters the target at the bottom");
    }

    @Test
    void neighbouringVerticesAreJoinedHorizontally() throws IOException {
        AspectJGraph canvas = editorCanvas();
        AspectEdgeCell edge = loneEdge(canvas);
        ViewVertex<AspectGraph> source = vertex(edge.getSourceVertex());
        ViewVertex<AspectGraph> target = vertex(edge.getTargetVertex());
        // the target to the right of the source, its centre slightly lower
        move(canvas, source, 100, 100);
        move(canvas, target, 250, 103);
        JEdgeView view = edgeView(canvas, edge);
        Point2D start = view.getPoint(0);
        Point2D end = view.getPoint(1);
        assertEquals(start.getY(), end.getY(), 0.01, "horizontal edge");
        Rectangle2D sourceBounds = vertexView(canvas, source).getShapeBounds();
        Rectangle2D targetBounds = vertexView(canvas, target).getShapeBounds();
        assertEquals(sourceBounds.getMaxX(), start.getX(), 0.01, "leaves the source at the right");
        assertEquals(targetBounds.getMinX(), end.getX(), 0.01, "enters the target at the left");
    }

    /** Moves a vertex to a given centre. */
    private static void move(AspectJGraph canvas, ViewVertex<AspectGraph> vertex, double x,
                             double y) {
        VisualMap visuals = new VisualMap();
        visuals.put(VisualKey.NODE_POS, new Point2D.Double(x, y));
        canvas.edit(Map.of(vertex, visuals));
    }

    private static ViewVertex<AspectGraph> vertex(@Nullable ViewVertex<AspectGraph> vertex) {
        assertNotNull(vertex);
        return vertex;
    }

    private static JEdgeView edgeView(AspectJGraph canvas, AspectEdgeCell edge) {
        return assertInstanceOf(JEdgeView.class,
                                canvas.getGraphLayoutCache().getMapping(JCell.of(edge), false));
    }

    private static JVertexView vertexView(AspectJGraph canvas, ViewCell<AspectGraph> vertex) {
        return assertInstanceOf(JVertexView.class,
                                canvas.getGraphLayoutCache().getMapping(JCell.of(vertex), false));
    }

    /** Returns a binary edge cell without parallels. */
    private static AspectEdgeCell loneEdge(AspectJGraph canvas) {
        for (var cell : canvas.getNonNullModel().getViewModel().getCells()) {
            if (cell instanceof AspectEdgeCell candidate && !candidate.isLoop()
                && ParallelEdges.rank(candidate) == 0) {
                return candidate;
            }
        }
        throw new AssertionError("no lone binary edge cell");
    }

    /** Creates an editing canvas on the fixture's start graph. */
    private static AspectJGraph editorCanvas() throws IOException {
        GrammarModel grammar = Groove.loadGrammar(GRAMMAR);
        AspectGraph startGraph = grammar.getStartGraphModel().getSource();
        assert startGraph != null; // the fixture grammar has a start graph
        var controller = new AspectGraphViewController(null, DisplayKind.HOST, true);
        controller.setGrammar(grammar);
        AspectJGraph canvas = (AspectJGraph) controller.getCanvas();
        AspectGraphViewModel model = canvas.newViewModel();
        model.setBeingEdited(true);
        model.loadGraph(startGraph);
        canvas.setViewModel(model);
        // selecting a cell scrolls it into view, which needs a viewport
        new JScrollPane(canvas);
        return canvas;
    }
}
