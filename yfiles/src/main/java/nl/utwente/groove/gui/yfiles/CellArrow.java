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
package nl.utwente.groove.gui.yfiles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.view.IBoundsProvider;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;

import nl.utwente.groove.util.AIGenerated;

/**
 * Arrow head of an edge end, with the geometry of the JGraph backend's heads: a length
 * and a width that are set independently, so that a head can be made wider than the
 * shaft of a thick edge. The tip of the head is at the point where the edge meets the
 * node; a closed head stops the edge rendering at its base.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
final class CellArrow implements IArrow {
    /** The shapes of arrow heads. */
    enum Shape {
        /**
         * Triangle with a notched base, the classic arrow head of the JGraph backend;
         * the edge runs into the notch.
         */
        CLASSIC,
        /** Filled or hollow triangle. */
        TRIANGLE,
        /** Filled or hollow diamond. */
        DIAMOND,
        /** Two lines meeting at the tip, drawn with the edge's line width. */
        SIMPLE;
    }

    /**
     * Creates an arrow head.
     * @param shape the shape of the head
     * @param length the length of the head, from tip to base
     * @param width the width of the head at its base
     * @param color the colour of the head's outline, and of its fill if filled
     * @param filled if {@code true}, a closed head is filled with the colour; otherwise
     * with white
     * @param lineWidth the line width of the edge; used for the lines of a simple head
     */
    CellArrow(Shape shape, double length, double width, Color color, boolean filled,
              float lineWidth) {
        this.shape = shape;
        this.length = length;
        this.width = width;
        this.color = color;
        this.filled = filled;
        this.lineWidth = lineWidth;
        this.path = createPath(shape, length, width);
    }

    /** The shape of this head. */
    private final Shape shape;
    /** The length of this head, from tip to base. */
    private final double length;
    /** The width of this head at its base. */
    private final double width;
    /** The colour of this head's outline, and of its fill if filled. */
    private final Color color;
    /** If {@code true}, a closed head is filled with the colour, otherwise with white. */
    private final boolean filled;
    /** The line width of the edge, used for the lines of a simple head. */
    private final float lineWidth;
    /** The head in its own coordinates: the tip at the origin, extending along the negative x-axis. */
    private final Path2D path;

    /** Creates the path of a head in its own coordinates. */
    private static Path2D createPath(Shape shape, double length, double width) {
        Path2D result = new Path2D.Double();
        double half = width / 2;
        switch (shape) {
        case CLASSIC:
            result.moveTo(0, 0);
            result.lineTo(-length, -half);
            result.lineTo(-length * NOTCH_RATIO, 0);
            result.lineTo(-length, half);
            result.closePath();
            break;
        case TRIANGLE:
            result.moveTo(0, 0);
            result.lineTo(-length, -half);
            result.lineTo(-length, half);
            result.closePath();
            break;
        case DIAMOND:
            result.moveTo(0, 0);
            result.lineTo(-length / 2, -half);
            result.lineTo(-length, 0);
            result.lineTo(-length / 2, half);
            result.closePath();
            break;
        case SIMPLE:
            result.moveTo(-length, -half);
            result.lineTo(0, 0);
            result.lineTo(-length, half);
            break;
        default:
            throw new IllegalArgumentException();
        }
        return result;
    }

    /** Fraction of the length of a classic head at which its notch lies. */
    private static final double NOTCH_RATIO = 2.0 / 3;

    /*
     * A closed head stops the edge rendering at its base, a classic head at its notch,
     * and a simple head has the edge run to the tip.
     */
    @Override
    public double getLength() {
        return switch (this.shape) {
        case CLASSIC -> this.length * NOTCH_RATIO;
        case TRIANGLE, DIAMOND -> this.length;
        case SIMPLE -> 0;
        };
    }

    @Override
    public double getCropLength() {
        return 0;
    }

    @Override
    @NonNullByDefault({})
    public IBoundsProvider getBoundsProvider(IEdge edge, boolean atSource, PointD anchor,
                                             PointD direction) {
        PointD dir = unit(direction);
        return context -> new RectD(-this.length, -this.width / 2, this.length, this.width)
            .getTransformed(new Matrix2D(dir.getX(), -dir.getY(), dir.getY(), dir.getX(),
                anchor.getX(), anchor.getY()));
    }

    @Override
    @NonNullByDefault({})
    public IVisualCreator getVisualCreator(IEdge edge, boolean atSource, PointD anchor,
                                           PointD direction) {
        return new Placement(anchor, unit(direction));
    }

    /** Normalises a direction vector. */
    private static PointD unit(PointD direction) {
        double length = direction.getVectorLength();
        return length == 0
            ? new PointD(1, 0)
            : new PointD(direction.getX() / length, direction.getY() / length);
    }

    /** Creator of the visual of this head at a given anchor, in a given direction. */
    private final class Placement implements IVisualCreator {
        /** Creates the creator of a visual at a given anchor, in a given direction. */
        Placement(PointD anchor, PointD direction) {
            this.anchor = anchor;
            this.direction = direction;
        }

        /** The position of the tip of the head. */
        private final PointD anchor;
        /** The direction the head points in, as a unit vector. */
        private final PointD direction;

        @Override
        @NonNullByDefault({})
        public IVisual createVisual(IRenderContext context) {
            return new ArrowVisual(this.anchor, this.direction);
        }

        @Override
        @NonNullByDefault({})
        public IVisual updateVisual(IRenderContext context, IVisual oldVisual) {
            if (oldVisual instanceof ArrowVisual visual && visual.arrow() == CellArrow.this) {
                visual.place(this.anchor, this.direction);
                return visual;
            }
            return createVisual(context);
        }
    }

    /** The visual of this head, placed at an anchor in a direction. */
    private final class ArrowVisual implements IVisual {
        /** Creates the visual, placed at a given anchor and direction. */
        ArrowVisual(PointD anchor, PointD direction) {
            place(anchor, direction);
        }

        /** Returns the arrow this is the visual of. */
        CellArrow arrow() {
            return CellArrow.this;
        }

        /** Places the head with its tip at an anchor, pointing in a direction. */
        void place(PointD anchor, PointD direction) {
            this.transform = new AffineTransform(direction.getX(), direction.getY(),
                -direction.getY(), direction.getX(), anchor.getX(), anchor.getY());
        }

        /** The transform placing the head at its anchor, in its direction. */
        private AffineTransform transform = new AffineTransform();

        @Override
        @NonNullByDefault({})
        public void paint(IRenderContext context, Graphics2D g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2
                    .setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.transform(this.transform);
                Path2D shape = CellArrow.this.path;
                if (CellArrow.this.shape == Shape.SIMPLE) {
                    g2.setColor(CellArrow.this.color);
                    g2
                        .setStroke(new BasicStroke(CellArrow.this.lineWidth,
                            BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                    g2.draw(shape);
                } else {
                    g2
                        .setColor(CellArrow.this.filled
                            ? CellArrow.this.color
                            : Color.WHITE);
                    g2.fill(shape);
                    g2.setColor(CellArrow.this.color);
                    g2.setStroke(new BasicStroke(1f));
                    g2.draw(shape);
                }
            } finally {
                g2.dispose();
            }
        }
    }
}
