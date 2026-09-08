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
package nl.utwente.groove.gui.view;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.view.cell.AspectEdgeCell;
import nl.utwente.groove.gui.view.cell.AspectVertexCell;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.line.LineStyle;

/**
 * A fragment of an edited graph, as the clipboard holds it: the cells of a
 * selection with their editable labels and their layout, detached from any view
 * model. The fragment is at the level of cells rather than graph elements, so that
 * a paste gives back exactly the cells that were copied: their label texts (also
 * when those do not parse) and the way the edges are grouped into cells.
 * @param vertices the vertices of the fragment
 * @param edges the edges of the fragment, between the vertices
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public record GraphFragment(List<Vertex> vertices, List<Edge> edges) {
    /**
     * A vertex of a fragment.
     * @param labels the editable labels of the vertex
     * @param position the centre of the vertex
     */
    public record Vertex(EditableLabels labels, Point2D position) {
        // no members
    }

    /**
     * An edge of a fragment.
     * @param source index of the source vertex in the fragment's vertex list
     * @param target index of the target vertex in the fragment's vertex list
     * @param labels the editable labels of the edge
     * @param points the points of the edge, including its end points
     * @param labelPosition the relative label position
     * @param lineStyle the line style
     */
    public record Edge(int source, int target, EditableLabels labels, List<Point2D> points,
        Point2D labelPosition, LineStyle lineStyle) {
        // no members
    }

    /**
     * Builds the fragment of a collection of cells: the vertex cells among them, and
     * the edge cells whose end vertices are both among them.
     */
    public static GraphFragment of(Collection<? extends ViewCell<AspectGraph>> cells) {
        Map<ViewCell<AspectGraph>,Integer> indices = new HashMap<>();
        List<Vertex> vertices = new ArrayList<>();
        for (var cell : cells) {
            if (cell instanceof AspectVertexCell vertex) {
                indices.put(vertex, vertices.size());
                vertices
                    .add(new Vertex(new EditableLabels(vertex.getEditableLabels()),
                        copy(vertex.getVisuals().getNodePos())));
            }
        }
        List<Edge> edges = new ArrayList<>();
        for (var cell : cells) {
            if (cell instanceof AspectEdgeCell edge) {
                Integer source = indices.get(edge.getSourceVertex());
                Integer target = indices.get(edge.getTargetVertex());
                if (source != null && target != null) {
                    VisualMap visuals = edge.getVisuals();
                    List<Point2D> points = new ArrayList<>();
                    for (Point2D point : visuals.getPoints()) {
                        points.add(copy(point));
                    }
                    edges
                        .add(new Edge(source, target, new EditableLabels(edge.getEditableLabels()),
                            List.copyOf(points), copy(visuals.getLabelPos()),
                            visuals.getLineStyle()));
                }
            }
        }
        return new GraphFragment(List.copyOf(vertices), List.copyOf(edges));
    }

    private static Point2D copy(Point2D point) {
        return new Point2D.Double(point.getX(), point.getY());
    }

    /** Indicates if the fragment has no vertices. */
    public boolean isEmpty() {
        return this.vertices.isEmpty();
    }

    /**
     * Returns the bounding box of the vertex positions and edge points; an empty
     * rectangle at the origin for an empty fragment.
     */
    public Rectangle2D getBounds() {
        Rectangle2D result = null;
        for (var vertex : this.vertices) {
            result = extend(result, vertex.position());
        }
        for (var edge : this.edges) {
            for (var point : edge.points()) {
                result = extend(result, point);
            }
        }
        return result == null
            ? new Rectangle2D.Double()
            : result;
    }

    private static Rectangle2D extend(@Nullable Rectangle2D bounds, Point2D point) {
        if (bounds == null) {
            return new Rectangle2D.Double(point.getX(), point.getY(), 0, 0);
        }
        bounds.add(point);
        return bounds;
    }

    /** Returns the label texts of the fragment, one per line: the string form of the fragment. */
    public String toText() {
        StringBuilder result = new StringBuilder();
        for (var vertex : this.vertices) {
            append(result, vertex.labels());
        }
        for (var edge : this.edges) {
            append(result, edge.labels());
        }
        return result.toString();
    }

    private static void append(StringBuilder result, EditableLabels labels) {
        for (String text : labels) {
            if (result.length() > 0) {
                result.append('\n');
            }
            result.append(text);
        }
    }
}
