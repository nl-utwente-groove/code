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
package nl.utwente.groove.gui.view;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.AIGenerated;

/**
 * Control points of a smooth Bezier curve interpolating a given list of edge
 * points, in the convention of the {@code BEZIER} line style: the curve passes
 * through every point; the first and last segments are quadratic, the segments
 * between interior points are cubic. At an interior point the tangent is parallel
 * to the chord between its two neighbours, and the control points on either side
 * lie on that tangent at half the projected distance to the respective neighbour.
 * <p>
 * For points {@code p[0..n-1]} (with {@code n >= 3}) the result holds
 * {@code 2*(n-2)} control points {@code b}, laid out so that the curve is
 * <pre>
 *   quadratic  p[0]   -- b[0]                  -- p[1]
 *   cubic      p[i-1] -- b[2*i-3] and b[2*i-2] -- p[i]     for 2 <= i <= n-2
 *   quadratic  p[n-2] -- b[2*(n-2)-1]          -- p[n-1]
 * </pre>
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public final class InterpolatingBezier {
    private InterpolatingBezier() {
        // empty, to prevent instantiation
    }

    /** Fraction of the projected neighbour distance at which a control point is placed. */
    private static final double TENSION = 0.5;

    /**
     * Computes the Bezier control points interpolating a list of points.
     * @param points the points through which the curve should pass
     * @return the control points in the layout described in the class comment;
     * empty if there are fewer than three points, as no interior point then exists
     */
    public static List<Point2D> controlPoints(List<Point2D> points) {
        int n = points.size();
        List<Point2D> result = new ArrayList<>(n < 3
            ? 0
            : 2 * (n - 2));
        for (int i = 1; i < n - 1; i++) {
            Point2D before = points.get(i - 1);
            Point2D at = points.get(i);
            Point2D after = points.get(i + 1);
            // unit vector along the chord between the neighbours
            double chordX = after.getX() - before.getX();
            double chordY = after.getY() - before.getY();
            double chordLength = Math.hypot(chordX, chordY);
            if (chordLength > 0) {
                chordX /= chordLength;
                chordY /= chordLength;
            }
            // control point towards the previous neighbour
            double inX = at.getX() - before.getX();
            double inY = at.getY() - before.getY();
            double inLength = Math.abs(inX * chordX + inY * chordY) * TENSION;
            result.add(new Point2D.Double(at.getX() - inLength * chordX,
                at.getY() - inLength * chordY));
            // control point towards the next neighbour
            double outX = after.getX() - at.getX();
            double outY = after.getY() - at.getY();
            double outLength = Math.abs(outX * chordX + outY * chordY) * TENSION;
            result.add(new Point2D.Double(at.getX() + outLength * chordX,
                at.getY() + outLength * chordY));
        }
        return result;
    }
}
