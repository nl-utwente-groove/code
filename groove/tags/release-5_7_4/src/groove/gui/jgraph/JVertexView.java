/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
package groove.gui.jgraph;

import static groove.gui.jgraph.JAttr.ADORNMENT_FONT;
import static groove.gui.jgraph.JAttr.EXTRA_BORDER_SPACE;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphCellEditor;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.PortView;
import org.jgraph.graph.VertexView;

import groove.gui.Options;
import groove.gui.look.Look;
import groove.gui.look.MultiLabel;
import groove.gui.look.Values;
import groove.gui.look.VisualKey;
import groove.gui.look.VisualMap;
import groove.util.NodeShape;
import groove.util.line.HTMLLineFormat;
import groove.util.line.LineStyle;

/**
 * A multi-lined vertex view that caches the label text. The functionality for
 * multi-line editing was taken from org.jgraph.cellview.JGraphMultilineView,
 * but the class had to be copied to turn the line wrap off.
 * @author Arend Rensink
 * @version $Revision$
 */
public class JVertexView extends VertexView {
    /**
     * Creates a vertex view for a given node, to be displayed on a given graph.
     * @param jNode the node underlying the view
     * @param jGraph the graph on which the node is to be displayed
     */
    public JVertexView(JVertex<?> jNode, JGraph<?> jGraph) {
        super(jNode);
        this.jGraph = jGraph;
    }

    /*
     * Specialises the return type.
     */
    @Override
    public JVertex<?> getCell() {
        return (JVertex<?>) super.getCell();
    }

    /** Returns the visual attributes map of the viewed cell. */
    public VisualMap getCellVisuals() {
        return getCell().getVisuals();
    }

    /*
     * This implementation returns the (static) {@link JVertexView.MyRenderer}.
     */
    @Override
    public MyRenderer getRenderer() {
        return renderer;
    }

    /*
     * This implementation returns the (static) {@link MultiLinedEditor}.
     */
    @Override
    public GraphCellEditor getEditor() {
        return editor;
    }

    /** Returns the (html formatted) text to be displayed in this vertex view. */
    final String getText() {
        MultiLabel label = getCellVisuals().getLabel();
        Color color = getCellVisuals().getForeground();
        // refresh the text if label or colour have changed
        if (label != this.label || color != this.color) {
            String text = HTMLLineFormat.toHtml(label.toString(HTMLLineFormat.instance()), color);
            this.text = text;
            this.label = label;
            this.color = color;
        }
        return this.text;
    }

    /*
     * Overwrites the super method because we have a different renderer.
     */
    @Override
    public Point2D getPerimeterPoint(EdgeView edge, Point2D p, Point2D q) {
        Point2D result = null;
        double qx = q.getX();
        double qy = q.getY();
        // use the adornment bounds if there is an adornment, and the
        // source lies to the northwest of it
        Rectangle2D bounds = getBounds();
        // revert to the actual borders by subtracting the
        // extra border space
        float extra = EXTRA_BORDER_SPACE - getCellVisuals().getLineWidth();
        bounds = new Rectangle2D.Double(bounds.getMinX() + extra, bounds.getMinY() + extra,
            bounds.getWidth() - 2 * extra, bounds.getHeight() - 2 * extra);
        double left = bounds.getMinX();
        double right = bounds.getMaxX();
        double top = bounds.getMinY();
        double bottom = bounds.getMaxY();
        double cx = bounds.getCenterX();
        double cy = bounds.getCenterY();
        // in manhattan line style, we shift the target point so it is
        // in horizontal or vertical reach of the node
        VisualMap edgeVisuals = ((JEdgeView) edge).getCell()
            .getVisuals();
        if (edgeVisuals.getLineStyle() == LineStyle.MANHATTAN && edgeVisuals.getPoints()
            .size() > 2) {
            if ((qx < left || qx > right) && (qy < top || qy > bottom)) {
                if (this == edge.getSource()
                    .getParentView()) {
                    // move qy into horizontal reach
                    double dy = qy - cy;
                    double room = bounds.getHeight() * (1 - 2 / DROP_FRACTION) * 0.5;
                    qy = cy
                        + room * Math.signum(dy) * Math.min(Math.abs(dy) / MAX_RATIO_DISTANCE, 1);
                } else {
                    // move qx into vertical reach
                    double dx = qx - cx;
                    double room = bounds.getWidth() * (1 - 2 / DROP_FRACTION) * 0.5;
                    qx = cx
                        + room * Math.signum(dx) * Math.min(Math.abs(dx) / MAX_RATIO_DISTANCE, 1);
                }
                q = new Point2D.Double(qx, qy);
            }
        }
        if (p == null || p.getX() == cx && p.getY() == cy) {
            // be smart about positioning the perimeter point if q is within
            // the limits of the vertex itself, in either x or y coordinate
            double xDrop = bounds.getWidth() / DROP_FRACTION;
            double yDrop = bounds.getHeight() / DROP_FRACTION;
            double minX = left + xDrop;
            double maxX = right - xDrop;
            double px = (qx > minX && qx < maxX) ? qx : cx;
            double minY = top + yDrop;
            double maxY = bottom - yDrop;
            double py = (qy > minY && qy < maxY) ? qy : cy;
            p = new Point2D.Double(px, py);
        }
        NodeShape shape = getCellVisuals().getNodeShape();
        result = shape.getPerimeterPoint(bounds, p, q);
        // correct for the adornment, if any
        Rectangle2D adornBounds = getAdornBounds();
        // possibly adjust if the target point lies northwest of the adornment
        boolean adorn =
            adornBounds != null && adornBounds.getMaxX() > qx && adornBounds.getMaxY() > qy;
        if (adorn) {
            assert adornBounds != null;
            double rx = result.getX();
            double ry = result.getY();
            double dx = qx - result.getX();
            double dy = qy - result.getY();
            if (adornBounds.intersectsLine(rx, ry, qx, qy)) {
                if (dy == 0) {
                    // target lies straight to the west
                    result.setLocation(adornBounds.getMinX(), result.getY());
                } else {
                    // first try out the intersection with the upper edge
                    double shiftY = ry - adornBounds.getMinY();
                    double shiftX = shiftY * dx / dy;
                    if (result.getX() - shiftX < adornBounds.getMinX()) {
                        // too far left; we need the left edge
                        shiftX = rx - adornBounds.getMinX();
                        shiftY = shiftX * dy / dx;
                    }
                    result.setLocation(rx - shiftX, ry - shiftY);
                }
            }
        }
        return result;
    }

    /** Returns the cell bounds including the parameter adornment, if any. */
    private Rectangle2D getAdornBounds() {
        Rectangle2D result = null;
        String adornment = getCellVisuals().getAdornment();
        if (adornment != null) {
            result = getBounds();
            MyRenderer renderer =
                ((MyRenderer) getRendererComponent(this.jGraph, false, false, false));
            result = new Rectangle2D.Double(result.getX(), result.getY(), renderer.adornWidth,
                renderer.adornHeight);
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("Vertex view for %s", getCell());
    }

    /**
     * The following is a rather awful hack to ensure the same kind of vertex
     * emphasis throughout editing. It is called from
     * {@link JEdgeView.MyEdgeHandle}.
     */
    void paintArmed(Graphics g) {
        Graphics2D newG = (Graphics2D) g.create();
        double scale = this.jGraph.getScale();
        newG.scale(scale, scale);
        // paint the border to erase it (we're in XOR mode)
        this.jGraph.getUI()
            .paintCell(newG, this, getBounds(), true);
        this.armed = true;
        this.jGraph.getUI()
            .paintCell(newG, this, getBounds(), true);
        this.armed = false;
        newG.dispose();
    }

    /** Underlying graph model, used to construct the autosize. */
    private final JGraph<?> jGraph;
    /** The color from which {@link #text} was derived. */
    private Color color;
    /** The label instance from which {@link #text} was derived. */
    private MultiLabel label;
    /** The text on this vertex. */
    private String text;
    /**
     * Temporary flag set to indicate that this cell should be painted
     * as selected.
     */
    private boolean armed;

    // switch off port magic

    static {
        PortView.allowPortMagic = false;
    }

    /**
     * Fraction of the width or height that is the minimum for special perimeter
     * point placement.
     */
    private static final double DROP_FRACTION = 10;
    /**
     * Maximal distance (horizontal or vertical) for perpendicular perimeter
     * points to be placed in ratio.
     */
    static private final double MAX_RATIO_DISTANCE = 250;
    /** Insets for vertices that contain text. */
    static private final Insets DEFAULT_INSETS = new Insets(2, 4, 2, 4);
    /** Insets for empty vertices. */
    static private final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);

    /** The renderer for all instances of <tt>JVertexView</tt>. */
    static private final MyRenderer renderer = new MyRenderer();

    /** The editor for all instances of <tt>JVertexView</tt>. */
    static private final MultiLinedEditor editor = new MultiLinedEditor();

    /**
     * Multi-line vertex renderer, based on a {@link JLabel} with <tt>html</tt>
     * formatting.
     */
    private static class MyRenderer extends JLabel implements CellViewRenderer {
        /** Constructs a renderer instance. */
        MyRenderer() {
            setMinimumSize(JAttr.DEFAULT_NODE_SIZE);
        }

        @Override
        public MyRenderer getRendererComponent(org.jgraph.JGraph graph, CellView view, boolean sel,
            boolean focus, boolean preview) {
            assert view instanceof JVertexView : String.format("This renderer is only meant for %s",
                JVertexView.class);
            this.view = (JVertexView) view;
            this.cell = this.view.getCell();
            VisualMap visuals = this.visuals = this.view.getCellVisuals();
            this.adornment = this.visuals.getAdornment();
            if (this.adornment == null) {
                this.adornHeight = 0;
                this.adornWidth = 0;
            } else {
                this.adornHeight = 12;
                this.adornWidth = getAdornWidth(this.adornment);
            }
            this.selectionColor = graph.getHighlightColor();
            this.dash = visuals.getDash();
            this.lineColor = visuals.getForeground();
            this.selected = visuals.isEmphasised();
            boolean emph = this.view.armed || this.selected;
            float lineWidth = visuals.getLineWidth();
            if (emph) {
                lineWidth += JAttr.EMPH_INCREMENT;
            }
            this.lineWidth = lineWidth;

            Color innerLineColor = visuals.getInnerLine();
            if (innerLineColor != null) {
                this.twoLines = true;
                this.line2color = innerLineColor;
                this.line2width = 1;
                this.line2dash = (float[]) VisualKey.DASH.getDefaultValue();
            } else {
                this.twoLines = false;
            }
            setOpaque(visuals.isOpaque());
            Color foreground = visuals.getForeground();
            setForeground((foreground != null) ? foreground : graph.getForeground());
            Color background = visuals.getBackground();
            background = (background != null) ? background : graph.getBackground();
            if (emph) {
                float darken = .95f;
                background = new Color(Math.max((int) (background.getRed() * darken), 0),
                    Math.max((int) (background.getGreen() * darken), 0),
                    Math.max((int) (background.getBlue() * darken), 0), background.getAlpha());
            }
            if (background == null ? getBackground() != null
                : !background.equals(getBackground())) {
                setBackground(background);
            }
            Font font = Options.getLabelFont()
                .deriveFont(visuals.getFont());
            setFont((font != null) ? font : graph.getFont());
            setText(this.view.getText());
            this.error = visuals.isError();
            this.nodeEdge = this.cell.getLooks()
                .contains(Look.NODIFIED);
            // do this last: it calls getTextSize, which depends on nodeEdge among others
            setBorder(createEmptyBorder());
            return this;
        }

        /**
         * In addition to called <code>super.paint()</code>, also draws the
         * selection border, if the vertex is selected.
         */
        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            Shape shape = getShape(this.lineWidth / 2);
            if (this.visuals.getLabel() == null) {
                paintBorder(g2, shape);
            } else {
                if (isOpaque()) {
                    paintBackground(g2, shape);
                }
                paintText(g2);
                paintBorder(g2, shape);
                paintErrorOverlay(g2);
                paintParameter(g2);
            }
        }

        /**
         * Paints a transparent overlay for an error node.
         */
        private void paintErrorOverlay(Graphics2D g2) {
            if (this.error) {
                Shape shape = getShape(EXTRA_BORDER_SPACE);
                g2.setColor(Values.ERROR_COLOR);
                g2.fill(shape);
            }
        }

        private void paintParameter(Graphics2D g2) {
            if (this.adornment != null) {
                g2.setColor(getForeground());
                int offset = 2;
                // make sure anonymous parameters are adorned correctly
                int width = Math.max(this.adornWidth, 6) + offset;
                g2.fillRect(0, 0, width, this.adornHeight);
                g2.setFont(ADORNMENT_FONT);
                g2.setColor(Color.white);
                g2.drawString(this.adornment, 1, this.adornHeight - offset);
            }
        }

        /** Paints the text in the foreground colour. */
        private void paintText(Graphics2D g) {
            boolean tmp = this.selected;
            try {
                setOpaque(false);
                this.selected = false;
                g.setColor(getForeground());
                super.paintComponent(g);
            } finally {
                this.selected = tmp;
            }
        }

        /**
         * Paints the border, with a given shape.
         */
        private void paintBorder(Graphics2D g, Shape shape) {
            g.setColor(this.lineColor);
            g.setStroke(JAttr.createStroke(this.lineWidth, this.dash));
            g.draw(shape);
            if (this.twoLines) {
                g.setColor(this.line2color);
                g.setStroke(JAttr.createStroke(this.line2width, this.line2dash));
                g.draw(shape);
            }
            if (this.selected) {
                paintSelectionBorder(g, shape);
            }
        }

        /**
         * Paints the background, with a given shape.
         */
        private void paintBackground(Graphics2D g, Shape shape) {
            Paint oldPaint = g.getPaint();
            Paint newPaint = JAttr.createPaint(shape.getBounds(), getBackground());
            g.setPaint(newPaint);
            g.fill(shape);
            g.setPaint(oldPaint);
        }

        /**
         * Creates and returns an empty border with the right insets to position
         * text correctly.
         */
        private Border createEmptyBorder() {
            Insets i = computeInsets();
            return i == null ? null
                : BorderFactory.createEmptyBorder(i.top + EXTRA_BORDER_SPACE,
                    i.left + EXTRA_BORDER_SPACE,
                    i.bottom + EXTRA_BORDER_SPACE,
                    i.right + EXTRA_BORDER_SPACE);
        }

        /**
         * Paint a selection border, with a a given shape.
         */
        private void paintSelectionBorder(Graphics2D g, Shape shape) {
            g.setStroke(GraphConstants.SELECTION_STROKE);
            g.setColor(this.selectionColor);
            g.draw(shape);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension result = getTextSize();
            // adjust for view insets
            Insets i = computeInsets(result.width, result.height);
            result = new Dimension(result.width + i.left + i.right + 2 * EXTRA_BORDER_SPACE,
                result.height + i.top + i.bottom + 2 * EXTRA_BORDER_SPACE);
            return result;
        }

        /** Reconstructs the insets from the total (preferred) size. */
        private Insets computeInsets() {
            Dimension size = getTextSize();
            return computeInsets(size.width, size.height);
        }

        private Dimension getTextSize() {
            Dimension result;
            if (this.cell.isStale(VisualKey.NODE_SIZE)) {
                result = computeTextSize();
                this.cell.putVisual(VisualKey.NODE_SIZE, result);
            } else {
                result = new Dimension();
                result.setSize(this.visuals.getNodeSize());
            }
            return result;
        }

        /** Computes the size of the text inscription. */
        private Dimension computeTextSize() {
            Dimension result;
            if (this.nodeEdge) {
                result = JAttr.NODE_EDGE_DIMENSION;
            } else {
                String text = convertDigits(getText());
                result = this.sizeMap.get(text);
                if (result == null) {
                    if (text.length() == 0) {
                        result = JAttr.DEFAULT_NODE_SIZE;
                    } else {
                        Border border = getBorder();
                        // reset the border to make sure only the text size gets
                        // measured
                        setBorder(null);
                        result = super.getPreferredSize();
                        // reset the border
                        setBorder(border);
                    }
                    this.sizeMap.put(text, result);
                }
            }
            return result;
        }

        private int getAdornWidth(String text) {
            Integer result = this.adornWidthMap.get(text);
            if (result == null) {
                result = SwingUtilities.computeStringWidth(getFontMetrics(ADORNMENT_FONT), text);
                this.adornWidthMap.put(text, result);
            }
            return result;
        }

        /**
         * Converts all digits in a string in the range 2-9 to 0. The idea is
         * that this will not affect the size of the string, but will unify many
         * keys in the size map.
         */
        private String convertDigits(String original) {
            char[] array = original.toCharArray();
            // flag indicating that we're inside a HTML tag
            boolean htmlTag = false;
            // flag indicating that we're inside a special HTML character
            boolean htmlChar = false;
            for (int i = 0; i < array.length; i++) {
                char c = array[i];
                if (htmlChar) {
                    htmlChar = c != ';';
                } else if (c == '&') {
                    htmlChar = true;
                } else if (htmlTag) {
                    htmlTag = c != '>';
                } else if (c == '<') {
                    htmlTag = true;
                } else if ('2' <= c && c <= '9') {
                    array[i] = '0';
                }
            }
            return String.valueOf(array)
                .intern();
        }

        /**
         * Computes insets for this view, using the view border and taking into
         * account the precalculated text width and height.
         */
        private Insets computeInsets(int textWidth, int textHeight) {
            Insets result;
            if (getText().length() == 0) {
                result = (Insets) EMPTY_INSETS.clone();
            } else {
                result = (Insets) DEFAULT_INSETS.clone();
            }
            // correct for the adornment space
            result.left += Math.max(0, this.adornWidth - 6);
            // correct for the predefined inset
            int inset = this.visuals.getInset();
            result.left += inset;
            result.right += inset;
            result.top += inset;
            result.bottom += inset;
            // add space needed for non-rectangular shapes
            switch (this.visuals.getNodeShape()) {
            case ELLIPSE:
                result.left += textWidth / 8;
                result.right += textWidth / 8;
                result.top += textHeight / 8;
                result.bottom += textHeight / 8;
                break;
            case HEXAGON:
                result.left += textHeight * NodeShape.HEX_EXTEND_RATIO - inset;
                result.right += textHeight * NodeShape.HEX_EXTEND_RATIO - inset;
                break;
            case DIAMOND:
                // TODO actually, if the first or last line is very long
                // then the required amount of additional space is more than this
                result.left += textWidth / 3;
                result.right += textWidth / 3;
                result.top += textHeight / 3;
                result.bottom += textHeight / 3;
                break;
            default:
                // no adjustments
            }
            return result;
        }

        /**
         * Returns the shape of the vertex.
         * The vertex is to be painted at the origin (x=0, y=0)
         * and to take a given size.
         * A second parameter controls how much the shape
         * should extend at each side beyond the size.
         */
        private Shape getShape(double extension) {
            // subtract the extra border space
            double extra = EXTRA_BORDER_SPACE - extension;
            Dimension s = getSize();
            double x = extra;
            double y = extra;
            double width = s.getWidth() - 2 * extra;
            double height = s.getHeight() - 2 * extra;
            switch (this.visuals.getNodeShape()) {
            case ELLIPSE:
                return new Ellipse2D.Double(x, y, width, height);
            case HEXAGON:
                return createHexagonShape(x, y, width, height);
            case DIAMOND:
                return createDiamondShape(x, y, width, height);
            case RECTANGLE:
                return new Rectangle2D.Double(x, y, width, height);
            case ROUNDED:
                return new RoundRectangle2D.Double(x, y, width, height, JAttr.NORMAL_ARC_SIZE,
                    JAttr.NORMAL_ARC_SIZE);
            case OVAL:
                return new RoundRectangle2D.Double(x, y, width, height, JAttr.STRONG_ARC_SIZE,
                    JAttr.STRONG_ARC_SIZE);
            default:
                assert false;
                return null;
            }
        }

        /** Creates a hexagonal shape inscribed in the bounds given in the parameters. */
        private Shape createHexagonShape(double x, double y, double width, double height) {
            GeneralPath result = new GeneralPath(Path2D.WIND_NON_ZERO, 5);
            double extend = height * NodeShape.HEX_EXTEND_RATIO;
            // stat at top left corner
            result.moveTo(x + extend, y);
            // to top right
            result.lineTo(x + width - extend, y);
            // to right
            result.lineTo(x + width, y + height / 2);
            // to bottom right
            result.lineTo(x + width - extend, y + height);
            // to bottom left
            result.lineTo(x + extend, y + height);
            // to left
            result.lineTo(x, y + height / 2);
            result.closePath();
            return result;
        }

        /** Creates a diamond shape inscribed in the bounds given in the parameters. */
        private Shape createDiamondShape(double x, double y, double width, double height) {
            GeneralPath result = new GeneralPath(Path2D.WIND_NON_ZERO, 5);
            result.moveTo(x + width / 2, y);
            result.lineTo(x + width, y + height / 2);
            result.lineTo(x + width / 2, y + height);
            result.lineTo(x, y + height / 2);
            result.closePath();
            return result;
        }

        /**
         * Overridden for performance reasons. Copied from
         * {@link org.jgraph.graph.VertexRenderer}.
         */
        @Override
        public void validate() {
            // empty
        }

        /**
         * Overridden for performance reasons. Copied from
         * {@link org.jgraph.graph.VertexRenderer}.
         */
        @Override
        public void revalidate() {
            // empty
        }

        /**
         * Overridden for performance reasons. Copied from
         * {@link org.jgraph.graph.VertexRenderer}.
         */
        @Override
        public void repaint(long tm, int x, int y, int width, int height) {
            // empty
        }

        /**
         * Overridden for performance reasons. Copied from
         * {@link org.jgraph.graph.VertexRenderer}.
         */
        @Override
        public void repaint(Rectangle r) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note </a> for more information.
         */
        @Override
        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
            // we test for equality here; equals rather than == catches a few more
            if ("text".equals(propertyName) && oldValue != newValue) {
                super.firePropertyChange(propertyName, oldValue, newValue);
            }
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note </a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note </a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, char oldValue, char newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note </a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, short oldValue, short newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note </a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, int oldValue, int newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note </a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, long oldValue, long newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note </a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, float oldValue, float newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note </a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, double oldValue, double newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note </a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
            // empty
        }

        /** The vertex view that is currently installed. */
        private JVertexView view;
        /** The vertex that is currently installed. */
        private JVertex<?> cell;
        /** The visual map of the vertex that is currently installed. */
        private VisualMap visuals;
        /** Indicates if the cell is a nodified edge. */
        private boolean nodeEdge;
        /** The underlying <code>JGraph</code>. */
        private Color selectionColor;
        /** Flag indicating that the vertex has been selected. */
        private boolean selected;
        /** Color of the border (which could be different from the text color). */
        private Color lineColor;
        /** Line width for the renderer. */
        private float lineWidth;
        /** Dash pattern for the border. */
        private float[] dash;

        // secondary options for drawing another line over the primary line
        private boolean twoLines = false;
        private Color line2color;
        private float[] line2dash;
        private float line2width;
        /** Flag indicating that the vertex has an error. */
        private boolean error;
        private String adornment;
        private int adornHeight;
        private int adornWidth;
        /** Mapping from (HTML) text to the preferred size for that text. */
        private final Map<String,Dimension> sizeMap = new HashMap<>();
        private final Map<String,Integer> adornWidthMap = new HashMap<>();
    }
}
