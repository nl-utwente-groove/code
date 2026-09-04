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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.gui.view.InterpolatingBezier;
import nl.utwente.groove.util.AIGenerated;

/**
 * Tests the control-point construction of {@link InterpolatingBezier}, which the
 * TikZ export uses for edges with the Bezier line style.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class InterpolatingBezierTest {
    private static final double EPS = 1e-9;

    /** Fewer than three points give no interior point, hence no control points. */
    @Test
    void tooFewPoints() {
        assertTrue(InterpolatingBezier.controlPoints(List.of()).isEmpty());
        assertTrue(InterpolatingBezier.controlPoints(List.of(p(0, 0))).isEmpty());
        assertTrue(InterpolatingBezier.controlPoints(List.of(p(0, 0), p(10, 0))).isEmpty());
    }

    /** Two control points per interior point, in the documented layout. */
    @Test
    void controlPointCount() {
        assertEquals(2, InterpolatingBezier.controlPoints(List.of(p(0, 0), p(5, 5), p(10, 0))).size());
        assertEquals(6, InterpolatingBezier
            .controlPoints(List.of(p(0, 0), p(1, 1), p(2, 0), p(3, 1), p(4, 0)))
            .size());
    }

    /** On a straight, evenly spaced polyline the control points stay on the line,
     * a quarter of the spacing before and after each interior point. */
    @Test
    void collinearPoints() {
        List<Point2D> controls = InterpolatingBezier
            .controlPoints(List.of(p(0, 0), p(10, 0), p(20, 0), p(30, 0)));
        assertEquals(4, controls.size());
        assertPoint(5, 0, controls.get(0));
        assertPoint(15, 0, controls.get(1));
        assertPoint(15, 0, controls.get(2));
        assertPoint(25, 0, controls.get(3));
    }

    /** At an interior point the two control points lie on the line through the
     * point parallel to the chord between its neighbours, on opposite sides. */
    @Test
    void tangentParallelToChord() {
        Point2D before = p(0, 0);
        Point2D at = p(10, 10);
        Point2D after = p(20, 0);
        List<Point2D> controls = InterpolatingBezier.controlPoints(List.of(before, at, after));
        // the chord is horizontal, so both control points are at the height of the interior point
        assertEquals(10, controls.get(0).getY(), EPS);
        assertEquals(10, controls.get(1).getY(), EPS);
        // half the projected distance to either neighbour, which is 10 on each side
        assertEquals(5, controls.get(0).getX(), EPS);
        assertEquals(15, controls.get(1).getX(), EPS);
    }

    /** Coinciding neighbours give a zero-length chord; the control points then
     * collapse onto the interior point instead of producing NaN coordinates. */
    @Test
    void degenerateChord() {
        List<Point2D> controls = InterpolatingBezier.controlPoints(List.of(p(0, 0), p(5, 5), p(0, 0)));
        assertPoint(5, 5, controls.get(0));
        assertPoint(5, 5, controls.get(1));
    }

    private static Point2D p(double x, double y) {
        return new Point2D.Double(x, y);
    }

    private static void assertPoint(double x, double y, Point2D actual) {
        assertEquals(x, actual.getX(), EPS, "x of " + actual);
        assertEquals(y, actual.getY(), EPS, "y of " + actual);
    }
}
