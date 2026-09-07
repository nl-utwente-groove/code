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
package nl.utwente.groove.gui.view;

import java.awt.geom.Point2D;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.graph.layout.ElementLayout;
import nl.utwente.groove.util.AIGenerated;

/**
 * Backend-independent geometry of edges, shared by the backends and the exporters
 * so that they agree on where things are.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public final class EdgeGeometry {
    private EdgeGeometry() {
        // no instances
    }

    /**
     * Converts a relative edge label position into the point at which the label is
     * centred, given the points of the edge.
     * The relative position consists of a distance along the edge, as a fraction of
     * the total length of the polyline through the points in permille (see
     * {@link ElementLayout#PERMILLE}), and a signed distance perpendicular to the
     * segment on which that point lies; a positive distance is to the right when
     * travelling along the edge (in screen coordinates, with the y-axis pointing down).
     * This is the JGraph convention, in which GROOVE's layouts are stored.
     * @param relative the relative label position
     * @param points the points of the edge, at least two
     * @return the absolute label position
     */
    public static Point2D labelPosition(Point2D relative, List<Point2D> points) {
        int pointCount = points.size();
        double[] segments = new double[pointCount - 1];
        double length = 0;
        Point2D pt = points.get(0);
        for (int i = 1; i < pointCount; i++) {
            Point2D next = points.get(i);
            double segment = pt.distance(next);
            segments[i - 1] = segment;
            length += segment;
            pt = next;
        }
        // dist is the distance along the edge at which the label lies
        double dist = relative.getX() / ElementLayout.PERMILLE * length;
        double offset = relative.getY();
        // find the segment on which the label lies
        length = 0;
        int index = 1;
        double segment = segments[0];
        while (dist > length + segment && index < pointCount - 1) {
            length += segment;
            segment = segments[index++];
        }
        Point2D p0 = points.get(index - 1);
        Point2D pe = points.get(index);
        // factor is the proportion along this segment the label lies at
        double factor = segment == 0
            ? 0
            : (dist - length) / segment;
        double dx = pe.getX() - p0.getX();
        double dy = pe.getY() - p0.getY();
        // the unit normal of the segment, pointing to its right
        double nx = segment == 0
            ? 0
            : -dy / segment;
        double ny = segment == 0
            ? 0
            : dx / segment;
        return new Point2D.Double(p0.getX() + dx * factor + nx * offset,
            p0.getY() + dy * factor + ny * offset);
    }

    /**
     * Converts the point at which an edge label is centred into the relative label
     * position, the inverse of {@link #labelPosition}: the label is related to the
     * segment of the polyline closest to it (measured to the segment, not its line),
     * with the distance along the edge in permille of the total length and the signed
     * perpendicular offset from that segment.
     * @param at the absolute label position
     * @param points the points of the edge, at least two
     * @return the relative label position
     */
    @AIGenerated("Claude Fable 5.1, 2026-09")
    public static Point2D relativePosition(Point2D at, List<Point2D> points) {
        int pointCount = points.size();
        double total = 0;
        for (int i = 1; i < pointCount; i++) {
            total += points.get(i - 1).distance(points.get(i));
        }
        double bestDistance = Double.MAX_VALUE;
        double bestAlong = 0;
        double bestOffset = 0;
        double before = 0;
        for (int i = 1; i < pointCount; i++) {
            Point2D p0 = points.get(i - 1);
            Point2D p1 = points.get(i);
            double dx = p1.getX() - p0.getX();
            double dy = p1.getY() - p0.getY();
            double segment = Math.hypot(dx, dy);
            double factor;
            double offset;
            if (segment == 0) {
                factor = 0;
                offset = 0;
            } else {
                double ax = at.getX() - p0.getX();
                double ay = at.getY() - p0.getY();
                factor = Math.max(0, Math.min(1, (ax * dx + ay * dy) / (segment * segment)));
                // the offset is positive to the right of the direction of travel
                offset = (ax * -dy + ay * dx) / segment;
            }
            Point2D foot
                = new Point2D.Double(p0.getX() + dx * factor, p0.getY() + dy * factor);
            double distance = foot.distance(at);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestAlong = before + factor * segment;
                bestOffset = offset;
            }
            before += segment;
        }
        double ratio = total == 0
            ? 0
            : bestAlong / total * ElementLayout.PERMILLE;
        return new Point2D.Double(ratio, bestOffset);
    }
}
