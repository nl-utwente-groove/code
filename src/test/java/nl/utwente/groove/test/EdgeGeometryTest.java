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
 */
package nl.utwente.groove.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.geom.Point2D;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.graph.layout.ElementLayout;
import nl.utwente.groove.gui.view.EdgeGeometry;
import nl.utwente.groove.util.AIGenerated;

/**
 * Checks the conversion between relative and absolute edge label positions.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class EdgeGeometryTest {
    /** A three-segment polyline. */
    private static final List<Point2D> POINTS = List
        .of(new Point2D.Double(0, 0), new Point2D.Double(100, 0), new Point2D.Double(100, 50),
            new Point2D.Double(200, 50));

    @Test
    void relativePositionInvertsLabelPosition() {
        // ratios away from the corners, where the closest segment is unambiguous
        for (double ratio : new double[] {0, 100, 250, 750, 999}) {
            for (double offset : new double[] {-15, 0, 12}) {
                Point2D relative = new Point2D.Double(ratio, offset);
                Point2D at = EdgeGeometry.labelPosition(relative, POINTS);
                Point2D back = EdgeGeometry.relativePosition(at, POINTS);
                assertEquals(ratio, back.getX(), 0.01, "ratio of " + relative);
                assertEquals(offset, back.getY(), 0.01, "offset of " + relative);
            }
        }
    }

    @Test
    void relativePositionRelatesToTheClosestSegment() {
        // a point to the right of the first segment's end, closer to it than to the others
        Point2D back = EdgeGeometry.relativePosition(new Point2D.Double(60, 10), POINTS);
        assertEquals(60 * ElementLayout.PERMILLE / 250.0, back.getX(), 0.01);
        assertEquals(10, back.getY(), 0.01);
        // a point beyond the end is related to the last segment's end
        back = EdgeGeometry.relativePosition(new Point2D.Double(230, 50), POINTS);
        assertEquals(ElementLayout.PERMILLE, back.getX(), 0.01);
        assertEquals(0, back.getY(), 0.01);
    }
}
