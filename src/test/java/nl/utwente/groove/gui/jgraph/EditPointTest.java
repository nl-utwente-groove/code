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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.menu.SetLineStyleMenu;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.gui.view.AspectGraphViewModel;
import nl.utwente.groove.gui.view.ParallelEdges;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.gui.view.cell.AspectEdgeCell;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.line.LineStyle;

/**
 * Tests the point-editing actions of the editor: where a point is added or which one
 * is removed, given the location at which the action is invoked.
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class EditPointTest {
    /** Grammar whose start graph is edited. */
    private static final String GRAMMAR = "junit/samples/ferryman.gps";

    /** A bent edge: right from (100,100) to the corner (200,100), then down to (200,300). */
    private static final List<Point2D> BENT = List
        .of(new Point2D.Double(100, 100), new Point2D.Double(200, 100),
            new Point2D.Double(200, 300));

    @Test
    void pointIsAddedWhereAskedIntoTheClosestSegment() throws IOException {
        AspectJGraph canvas = editorCanvas();
        AspectEdgeCell edge = bentEdge(canvas);
        // closer to the vertical segment than to the horizontal one, though the
        // distances to the corner and the start sum to less
        Point2D at = new Point2D.Double(190, 150);
        canvas.getController().getAddPointAction().execute(edge, at);
        assertEquals(List.of(BENT.get(0), BENT.get(1), at, BENT.get(2)),
                     edge.getVisuals().getPoints());
    }

    @Test
    void menuItemPassesTheMenuLocationOnce() throws IOException {
        AspectJGraph canvas = editorCanvas();
        AspectEdgeCell edge = bentEdge(canvas);
        canvas.setSelectionCell(JCell.of(edge));
        Point2D at = new Point2D.Double(190, 150);
        var action = canvas.getController().getAddPointAction();
        action.createMenuItem(at).doClick();
        assertEquals(List.of(BENT.get(0), BENT.get(1), at, BENT.get(2)),
                     edge.getVisuals().getPoints());
        // invoked again without a location (nor a pointer, headless): the point goes
        // beside the first segment, not to the menu location again
        action.actionPerformed(new ActionEvent(canvas, ActionEvent.ACTION_PERFORMED, ""));
        List<Point2D> points = edge.getVisuals().getPoints();
        assertEquals(5, points.size());
        assertNotEquals(at, points.get(1));
        assertEquals(BENT.get(1), points.get(2));
    }

    @Test
    void nearestIntermediatePointIsRemoved() throws IOException {
        AspectJGraph canvas = editorCanvas();
        AspectEdgeCell edge = bentEdge(canvas);
        Point2D added = new Point2D.Double(190, 150);
        canvas.getController().getAddPointAction().execute(edge, added);
        // nearer to the corner than to the added point
        canvas.getController().getRemovePointAction().execute(edge, new Point2D.Double(205, 95));
        assertEquals(List.of(BENT.get(0), added, BENT.get(2)), edge.getVisuals().getPoints());
        // an end point is never removed
        canvas.getController().getRemovePointAction().execute(edge, new Point2D.Double(100, 100));
        assertEquals(List.of(BENT.get(0), BENT.get(2)), edge.getVisuals().getPoints());
    }

    @Test
    void straightLineStyleAddsNoPoint() throws IOException {
        AspectJGraph canvas = editorCanvas();
        AspectEdgeCell edge = loneEdge(canvas);
        move(canvas, edge, 100, 100, 300, 100);
        canvas.setSelectionCell(JCell.of(edge));
        setLineStyle(canvas, LineStyle.MANHATTAN);
        assertEquals(LineStyle.MANHATTAN, edge.getVisuals().getLineStyle());
        assertEquals(2, edge.getVisuals().getPoints().size(), "a Manhattan edge needs no bend");
    }

    @Test
    void curvedLineStyleAddsAPointBesideTheEdge() throws IOException {
        AspectJGraph canvas = editorCanvas();
        AspectEdgeCell edge = loneEdge(canvas);
        move(canvas, edge, 100, 100, 300, 100);
        canvas.setSelectionCell(JCell.of(edge));
        setLineStyle(canvas, LineStyle.BEZIER);
        assertEquals(LineStyle.BEZIER, edge.getVisuals().getLineStyle());
        List<Point2D> points = edge.getVisuals().getPoints();
        assertEquals(3, points.size(), "a curve needs a bend: " + points);
        Point2D bend = points.get(1);
        assertEquals(200, bend.getX(), 0.01, "halfway");
        double offset = Math.abs(bend.getY() - 100);
        assertTrue(offset > 5 && offset < 40, "at a small distance from the edge: " + bend);
    }

    @Test
    void currentLineStyleIsNoEdit() throws IOException {
        AspectJGraph canvas = editorCanvas();
        AspectEdgeCell edge = loneEdge(canvas);
        move(canvas, edge, 100, 100, 300, 100);
        canvas.setSelectionCell(JCell.of(edge));
        setLineStyle(canvas, LineStyle.BEZIER);
        var history = canvas.getNonNullModel().getViewModel().getEditHistory();
        assertNotNull(history);
        int[] changes = {0};
        history.addListener(() -> changes[0]++);
        List<Point2D> points = edge.getVisuals().getPoints();
        setLineStyle(canvas, LineStyle.BEZIER);
        assertEquals(0, changes[0], "choosing the current style again is no edit");
        assertEquals(points, edge.getVisuals().getPoints());
    }

    @Test
    void lineStyleMenuServesTheSelectedEdges() throws IOException {
        AspectJGraph canvas = editorCanvas();
        AspectEdgeCell edge = loneEdge(canvas);
        var vertex = edge.getSourceVertex();
        assertNotNull(vertex);
        var menu = new SetLineStyleMenu(canvas);
        canvas.setSelectionCell(JCell.of(vertex));
        assertFalse(menu.isEnabled(), "no edge selected");
        canvas.setSelectionCells(new Object[] {JCell.of(vertex), JCell.of(edge)});
        assertTrue(menu.isEnabled(), "an edge selected among a vertex");
        setLineStyle(canvas, LineStyle.MANHATTAN);
        assertEquals(LineStyle.MANHATTAN, edge.getVisuals().getLineStyle());
    }

    /** Invokes the line style action for a style on the canvas. */
    private static void setLineStyle(AspectJGraph canvas, LineStyle style) {
        canvas
            .getController()
            .getSetLineStyleAction(style)
            .actionPerformed(new ActionEvent(canvas, ActionEvent.ACTION_PERFORMED, ""));
    }

    /** Returns a lone binary edge, bent as {@link #BENT}. */
    private static AspectEdgeCell bentEdge(AspectJGraph canvas) {
        AspectEdgeCell edge = loneEdge(canvas);
        move(canvas, edge, 100, 100, 200, 300);
        VisualMap visuals = new VisualMap();
        visuals.put(VisualKey.POINTS, BENT);
        canvas.edit(Map.of(edge, visuals));
        assertEquals(BENT, edge.getVisuals().getPoints());
        return edge;
    }

    /** Moves the end vertices of an edge to given centres. */
    private static void move(AspectJGraph canvas, AspectEdgeCell edge, double sourceX,
                             double sourceY, double targetX, double targetY) {
        var source = edge.getSourceVertex();
        var target = edge.getTargetVertex();
        assertNotNull(source);
        assertNotNull(target);
        VisualMap sourceVisuals = new VisualMap();
        sourceVisuals.put(VisualKey.NODE_POS, new Point2D.Double(sourceX, sourceY));
        VisualMap targetVisuals = new VisualMap();
        targetVisuals.put(VisualKey.NODE_POS, new Point2D.Double(targetX, targetY));
        canvas.edit(Map.<ViewCell<AspectGraph>,VisualMap>of(source, sourceVisuals, target,
                                                             targetVisuals));
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
