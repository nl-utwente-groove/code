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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.input.IInputModeContext;

import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.view.cell.AViewVertex;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.NodeShape;

/**
 * Node style painting a vertex cell from its visual map: shape, background,
 * border with dash pattern and optional inner line, the emphasis of a selected
 * vertex, the parameter and identifier adornments, and the translucent overlay of a
 * vertex with diagnostics. The text is a yFiles label of the node, not painted here.
 * The style is stateless and shared by all nodes; the node's tag is the cell.
 * <p>
 * The node layout is the visible border rectangle; the overlays extend beyond it by
 * {@link CellStyles#EXTRA_BORDER_SPACE}, which the reported bounds include. Edges are
 * cropped at the perimeter of the node shape, computed by {@link NodeShape} as in the
 * JGraph backend.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
final class CellNodeStyle extends AbstractNodeStyle {
    /** Returns the vertex cell shown by a given node, if the node has one. */
    static @Nullable AViewVertex<?> cellOf(INode node) {
        return node.getTag() instanceof AViewVertex<?> vertex
            ? vertex
            : null;
    }

    @Override
    @NonNullByDefault({})
    protected IVisual createVisual(IRenderContext context, INode node) {
        NodeVisual result = new NodeVisual();
        result.update(node);
        return result;
    }

    @Override
    @NonNullByDefault({})
    protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, INode node) {
        if (oldVisual instanceof NodeVisual visual) {
            visual.update(node);
            return visual;
        }
        return createVisual(context, node);
    }

    /* The overlays are painted outside the layout. */
    @Override
    @NonNullByDefault({})
    protected RectD getBounds(ICanvasContext context, INode node) {
        return node.getLayout().toRectD().getEnlarged(CellStyles.EXTRA_BORDER_SPACE);
    }

    /* The outline is the node shape at the outer edge of the border line. */
    @Override
    @NonNullByDefault({})
    protected GeneralPath getOutline(INode node) {
        return toGeneralPath(shape(node, lineWidth(node)));
    }

    @Override
    @NonNullByDefault({})
    protected boolean isInside(INode node, PointD location) {
        return shape(node, lineWidth(node)).contains(location.getX(), location.getY());
    }

    /*
     * Edge ends are placed as the JGraph backend places them. The perimeter point of
     * the shape is not defined for every direction (an axis-aligned line, or a line
     * from the centre itself); where it comes out non-finite or off the line, the
     * outline intersection of yFiles takes over, since yFiles crops the edge paths
     * with this method and turns a bad point into a path of absurd extent.
     */
    @Override
    @NonNullByDefault({})
    protected PointD getIntersection(INode node, PointD inner, PointD outer) {
        Rectangle2D bounds = bounds(node, lineWidth(node));
        Point2D result = nodeShape(node)
            .getPerimeterPoint(bounds, new Point2D.Double(inner.getX(), inner.getY()),
                               new Point2D.Double(outer.getX(), outer.getY()));
        if (result == null || !Double.isFinite(result.getX()) || !Double.isFinite(result.getY())
            || !onLine(result, inner, outer)) {
            return super.getIntersection(node, inner, outer);
        }
        return new PointD(result.getX(), result.getY());
    }

    /** Indicates if a point lies within the bounding box of a line, with some tolerance. */
    private static boolean onLine(Point2D point, PointD from, PointD to) {
        double tolerance = 1;
        return point.getX() >= Math.min(from.getX(), to.getX()) - tolerance
            && point.getX() <= Math.max(from.getX(), to.getX()) + tolerance
            && point.getY() >= Math.min(from.getY(), to.getY()) - tolerance
            && point.getY() <= Math.max(from.getY(), to.getY()) + tolerance;
    }

    @Override
    @NonNullByDefault({})
    protected boolean isHit(IInputModeContext context, PointD location, INode node) {
        return getOutline(node).areaContains(location, context.getHitTestRadius());
    }

    /** Returns the line width of the border of a node. */
    static float lineWidth(INode node) {
        var cell = cellOf(node);
        return cell == null
            ? 1f
            : cell.getVisuals().getLineWidth();
    }

    /** Returns the shape kind of a node. */
    private static NodeShape nodeShape(INode node) {
        var cell = cellOf(node);
        return cell == null
            ? NodeShape.RECTANGLE
            : cell.getVisuals().getNodeShape();
    }

    /** Returns the layout of a node, enlarged on all sides by a given extension. */
    static Rectangle2D bounds(INode node, double extension) {
        var layout = node.getLayout();
        return new Rectangle2D.Double(layout.getX() - extension, layout.getY() - extension,
            layout.getWidth() + 2 * extension, layout.getHeight() + 2 * extension);
    }

    /** Returns the shape of a node, enlarged on all sides by a given extension. */
    static Shape shape(INode node, double extension) {
        return shape(nodeShape(node), bounds(node, extension));
    }

    /** Creates the Java2D shape of a given kind inscribed in given bounds. */
    static Shape shape(NodeShape kind, Rectangle2D bounds) {
        double x = bounds.getX();
        double y = bounds.getY();
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        return switch (kind) {
        case ELLIPSE -> new Ellipse2D.Double(x, y, width, height);
        case HEXAGON -> hexagon(x, y, width, height);
        case DIAMOND -> diamond(x, y, width, height);
        case RECTANGLE -> new Rectangle2D.Double(x, y, width, height);
        case ROUNDED -> new RoundRectangle2D.Double(x, y, width, height, Values.NORMAL_ARC_SIZE,
            Values.NORMAL_ARC_SIZE);
        case OVAL -> new RoundRectangle2D.Double(x, y, width, height, Values.STRONG_ARC_SIZE,
            Values.STRONG_ARC_SIZE);
        };
    }

    /** Creates a hexagonal shape inscribed in the given bounds. */
    private static Shape hexagon(double x, double y, double width, double height) {
        Path2D result = new Path2D.Double(Path2D.WIND_NON_ZERO, 7);
        double extend = height * NodeShape.HEX_EXTEND_RATIO;
        result.moveTo(x + extend, y);
        result.lineTo(x + width - extend, y);
        result.lineTo(x + width, y + height / 2);
        result.lineTo(x + width - extend, y + height);
        result.lineTo(x + extend, y + height);
        result.lineTo(x, y + height / 2);
        result.closePath();
        return result;
    }

    /** Creates a diamond shape inscribed in the given bounds. */
    private static Shape diamond(double x, double y, double width, double height) {
        Path2D result = new Path2D.Double(Path2D.WIND_NON_ZERO, 5);
        result.moveTo(x + width / 2, y);
        result.lineTo(x + width, y + height / 2);
        result.lineTo(x + width / 2, y + height);
        result.lineTo(x, y + height / 2);
        result.closePath();
        return result;
    }

    /** Converts a Java2D shape to a yFiles path. */
    private static GeneralPath toGeneralPath(Shape shape) {
        GeneralPath result = new GeneralPath();
        double[] coords = new double[6];
        for (var iter = shape.getPathIterator(null); !iter.isDone(); iter.next()) {
            switch (iter.currentSegment(coords)) {
            case java.awt.geom.PathIterator.SEG_MOVETO -> result.moveTo(coords[0], coords[1]);
            case java.awt.geom.PathIterator.SEG_LINETO -> result.lineTo(coords[0], coords[1]);
            case java.awt.geom.PathIterator.SEG_QUADTO -> result
                .quadTo(coords[0], coords[1], coords[2], coords[3]);
            case java.awt.geom.PathIterator.SEG_CUBICTO -> result
                .cubicTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
            case java.awt.geom.PathIterator.SEG_CLOSE -> result.close();
            default -> throw new IllegalStateException();
            }
        }
        return result;
    }

    /**
     * The visual of a node: a snapshot of the painting parameters, refreshed from the
     * cell's visual map on every update.
     */
    private static final class NodeVisual implements IVisual {
        /** Refreshes the painting parameters from the node's cell. */
        void update(INode node) {
            var cell = cellOf(node);
            this.layout = node.getLayout().toRectD().toRectangle2D();
            if (cell == null) {
                this.empty = true;
                return;
            }
            this.empty = false;
            VisualMap visuals = cell.getVisuals();
            this.kind = visuals.getNodeShape();
            this.nodified = CellStyles.isNodified(cell);
            this.opaque = visuals.isOpaque();
            this.emphasised = visuals.isEmphasised();
            this.lineColor = visuals.getForeground();
            float width = visuals.getLineWidth();
            if (this.emphasised) {
                width += CellStyles.EMPH_INCREMENT;
            }
            this.lineWidth = width;
            this.dash = visuals.getDash();
            this.innerLine = visuals.getInnerLine();
            this.background = visuals.getBackground();
            this.overlay = Values.getSeverityOverlay(visuals.getErrorSeverity());
            this.parAdornment = visuals.getParAdornment();
            this.idAdornment = visuals.getIdAdornment();
        }

        @Override
        @NonNullByDefault({})
        public void paint(IRenderContext context, Graphics2D g) {
            if (this.empty) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2
                    .setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                      RenderingHints.VALUE_ANTIALIAS_ON);
                Shape border = shape(this.kind, enlarge(this.layout, this.lineWidth / 2));
                if (!this.nodified && this.opaque) {
                    Color fill = this.background;
                    if (fill == null) {
                        fill = context.getCanvasComponent().getBackground();
                        assert fill != null; // the canvas component has a background
                    }
                    if (this.emphasised) {
                        fill = darken(fill);
                    }
                    g2.setColor(fill);
                    g2.fill(border);
                }
                g2.setColor(this.lineColor);
                g2.setStroke(CellStyles.stroke(this.lineWidth, this.dash));
                g2.draw(border);
                Color innerLine = this.innerLine;
                if (innerLine != null) {
                    g2.setColor(innerLine);
                    g2.setStroke(CellStyles.stroke(CellStyles.INNER_LINE_WIDTH, null));
                    g2.draw(border);
                }
                if (this.emphasised) {
                    g2.setColor(CellStyles.SELECTION_COLOR);
                    g2.setStroke(CellStyles.SELECTION_STROKE);
                    g2.draw(border);
                }
                if (!this.nodified) {
                    paintAdornments(g2);
                    Color overlay = this.overlay;
                    if (overlay != null) {
                        g2.setColor(overlay);
                        g2.fill(shape(this.kind, enlarge(this.layout, CellStyles.EXTRA_BORDER_SPACE)));
                    }
                }
            } finally {
                g2.dispose();
            }
        }

        /** Paints the parameter adornment (top left) and identifier adornment (top right). */
        private void paintAdornments(Graphics2D g2) {
            double left = this.layout.getX() - CellStyles.EXTRA_BORDER_SPACE;
            double top = this.layout.getY() - CellStyles.EXTRA_BORDER_SPACE;
            int height = CellStyles.ADORNMENT_HEIGHT;
            int offset = 2;
            String par = this.parAdornment;
            if (par != null) {
                int width = Math.max(CellStyles.adornmentWidth(par), 6) + offset;
                g2.setColor(this.lineColor);
                g2.fill(new Rectangle2D.Double(left, top, width, height));
                g2.setFont(CellStyles.adornmentFont());
                g2.setColor(Color.WHITE);
                g2.drawString(par, (float) left + 1, (float) top + height - offset);
            }
            String id = this.idAdornment;
            if (id != null) {
                int width = CellStyles.adornmentWidth(id) + offset;
                double x = this.layout.getMaxX() + CellStyles.EXTRA_BORDER_SPACE - width;
                Color fill = this.background;
                if (fill != null) {
                    g2.setColor(fill);
                    g2.fill(new Rectangle2D.Double(x, top, width, height));
                }
                g2.setColor(this.lineColor);
                g2.setFont(CellStyles.adornmentFont());
                g2.drawString(id, (float) x, (float) top + height - offset);
            }
        }

        /** Returns a rectangle enlarged on all sides by a given amount. */
        private static Rectangle2D enlarge(Rectangle2D rect, double extension) {
            return new Rectangle2D.Double(rect.getX() - extension, rect.getY() - extension,
                rect.getWidth() + 2 * extension, rect.getHeight() + 2 * extension);
        }

        /** Darkens a colour slightly, for the background of emphasised nodes. */
        private static Color darken(Color color) {
            float darken = .95f;
            return new Color(Math.max((int) (color.getRed() * darken), 0),
                Math.max((int) (color.getGreen() * darken), 0),
                Math.max((int) (color.getBlue() * darken), 0), color.getAlpha());
        }

        /** If {@code true}, the node shows no cell and nothing is painted. */
        private boolean empty = true;
        /** The bounds of the node, in world coordinates. */
        private Rectangle2D layout = new Rectangle2D.Double();
        /** The shape of the node. */
        private NodeShape kind = NodeShape.RECTANGLE;
        /** If {@code true}, the node shows a nodified edge, which has no adornments or overlay. */
        private boolean nodified;
        /** If {@code true}, the shape is filled before it is drawn. */
        private boolean opaque;
        /** If {@code true}, the node is emphasised as part of the selection. */
        private boolean emphasised;
        /** The colour of the border, and of the adornments. */
        private Color lineColor = Color.BLACK;
        /** The width of the border, widened when the node is emphasised. */
        private float lineWidth = 1f;
        /** The dash pattern of the border; {@code null} if it is solid. */
        private float @Nullable [] dash;
        /** The colour of the inner line; {@code null} if the node has none. */
        private @Nullable Color innerLine;
        /** The fill colour; {@code null} for the background of the canvas. */
        private @Nullable Color background;
        /** The colour of the diagnostics overlay; {@code null} if the node has no error. */
        private @Nullable Color overlay;
        /** The text of the parameter adornment; {@code null} if the node is no parameter. */
        private @Nullable String parAdornment;
        /** The text of the identifier adornment; {@code null} if no identifier is shown. */
        private @Nullable String idAdornment;
    }
}
