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
package nl.utwente.groove.gui.view;

import java.awt.geom.Point2D;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.AIGenerated;

/**
 * Fan-out of parallel edges: the rank of an unrouted edge among the unrouted edges
 * between the same two vertices, in either direction, and the shift by which a backend
 * draws the ends of such an edge away from the line between the vertex centres, so that
 * parallel edges are told apart. The fan-out is a rendering choice of the backends
 * (see {@code claude/view-facade.md}); the shift is never model data.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.1, 2026-09")
public final class ParallelEdges {
    private ParallelEdges() {
        // no instances
    }

    /** Shift per unit of rank, in pixels; neighbouring ranks differ by 2. */
    public static final int DISTANCE = 4;

    /**
     * Returns the rank of an edge among the unrouted edges between its end vertices, in
     * either direction: the ranks of {@code n} such edges are {@code -(n-1), -(n-3), ...,
     * n-1}, outgoing edges after incoming ones, in the order of the source vertex'
     * context. The rank is 0 for an edge that is routed (has points of its own), a
     * loop, unconnected, or without parallels.
     */
    public static int rank(ViewEdge<?> edge) {
        var source = edge.getSourceVertex();
        var target = edge.getTargetVertex();
        if (source == null || target == null || edge.isLoop()
            || edge.getVisuals().getPoints().size() > 2) {
            return 0;
        }
        // the number of incoming and outgoing parallel edges
        int inCount = 0;
        int outCount = 0;
        // the position of this edge among the outgoing ones
        int rank = 0;
        boolean found = false;
        var iter = source.getContext();
        while (iter.hasNext()) {
            var other = iter.next();
            if (other.getVisuals().getPoints().size() > 2) {
                continue;
            }
            found |= other == edge;
            if (other.getTargetVertex() == target) {
                outCount++;
                if (!found) {
                    rank++;
                }
            } else if (other.getSourceVertex() == target) {
                inCount++;
            }
        }
        // the ranks are the points of an interval centred on 0, at distance 2
        return 2 * (inCount + rank) - (inCount + outCount - 1);
    }

    /**
     * Returns the vector by which the ends of an edge of a given rank are shifted:
     * perpendicular to the direction from its source to its target, to the right of
     * that direction (in screen coordinates) for positive ranks, over
     * {@link #DISTANCE} times the rank, capped by a given maximum.
     * @param rank the rank of the edge, see {@link #rank}
     * @param dx horizontal component of the direction from source to target
     * @param dy vertical component of the direction from source to target
     * @param max the maximum length of the shift, e.g. the radius of the end node
     * @return the shift; the zero vector if the rank is 0 or the direction has no length
     */
    public static Point2D shift(int rank, double dx, double dy, double max) {
        double length = Math.hypot(dx, dy);
        if (rank == 0 || length == 0) {
            return new Point2D.Double();
        }
        double offset = Math.signum(rank) * Math.min(DISTANCE * Math.abs(rank), max);
        return new Point2D.Double(offset * dy / length, -offset * dx / length);
    }
}
