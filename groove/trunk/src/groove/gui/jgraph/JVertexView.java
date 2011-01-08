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
 * $Id: JVertexView.java,v 1.21 2008-02-05 13:28:03 rensink Exp $
 */
package groove.gui.jgraph;

import static groove.gui.jgraph.JAttr.EXTRA_BORDER_SPACE;
import static groove.util.Converter.HTML_TAG;
import static groove.util.Converter.createColorTag;
import static groove.util.Converter.createSpanTag;
import static groove.view.aspect.AspectKind.PRODUCT;
import groove.graph.GraphRole;
import groove.util.Converter.HTMLTag;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectNode;

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
import javax.swing.border.Border;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellMapper;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphCellEditor;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.PortView;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

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
    public JVertexView(GraphJVertex jNode, JGraph jGraph) {
        super(jNode);
        this.jGraph = jGraph;
    }

    /**
     * Specialises the return type.
     */
    @Override
    public GraphJVertex getCell() {
        return (GraphJVertex) super.getCell();
    }

    /**
     * This implementation returns the (static) {@link JVertexView.MyRenderer}.
     */
    @Override
    public MyRenderer getRenderer() {
        return renderer;
    }

    /**
     * This implementation returns the (static) {@link MultiLinedEditor}.
     */
    @Override
    public GraphCellEditor getEditor() {
        return editor;
    }

    @Override
    public void refresh(GraphLayoutCache cache, CellMapper mapper,
            boolean createDependentViews) {
        super.refresh(cache, mapper, createDependentViews);
        this.text = computeText();
    }

    /**
     * Retrieves the HTML text for the vertex, and adapts the text colour to the
     * line colour if the line colour is not black.
     * @see GraphJVertex#getText()
     */
    private String computeText() {
        StringBuilder result = new StringBuilder(getCell().getText());
        if (result.length() > 0) {
            Color lineColor = GraphConstants.getLineColor(getAllAttributes());
            if (lineColor != null && !lineColor.equals(Color.BLACK)) {
                createColorTag(lineColor).on(result);
            }
            return HTML_TAG.on(fontTag.on(result)).toString();
        } else {
            return "";
        }
    }

    /**
     * Callback method indicating that a certain vertex is a data vertex (and so
     * should be rendered differently).
     */
    private int getVertexShape() {
        AspectNode node = null;
        if (getCell() instanceof AspectJVertex) {
            node = ((AspectJVertex) getCell()).getNode();
        }
        GraphRole graphRole =
            node == null ? GraphRole.NONE : node.getGraphRole();
        AspectKind attrKind =
            node == null ? AspectKind.NONE : node.getAttrKind();
        if (graphRole == GraphRole.TYPE) {
            return RECTANGLE_SHAPE;
        } else if (attrKind.isData()) {
            return ELLIPSE_SHAPE;
        } else if (attrKind == PRODUCT) {
            return DIAMOND_SHAPE;
        } else {
            return ROUNDED_RECTANGLE_SHAPE;
        }
    }

    /** Stores the insets value for this view. */
    void setInsets(Insets insets) {
        this.insets = insets;
    }

    /** Returns the insets computed for this vertex view. */
    final Insets getInsets() {
        return this.insets;
    }

    /** Returns the (html formatted) text to be displayed in this vertex view. */
    final String getText() {
        return this.text;
    }

    /**
     * Overwrites the super method because we have a different renderer.
     */
    @Override
    public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
        Rectangle2D bounds = getBounds().getBounds2D();
        // revert to the actual borders by subtracting the
        // extra border space
        bounds.setRect(bounds.getX() + EXTRA_BORDER_SPACE, bounds.getY()
            + EXTRA_BORDER_SPACE, bounds.getWidth() - 2 * EXTRA_BORDER_SPACE,
            bounds.getHeight() - 2 * EXTRA_BORDER_SPACE);
        if (source == null) {
            // be smart about positioning the perimeter point if p is within
            // the limits of the vertex itself, in either x or y coordinate
            double xDrop = bounds.getWidth() / DROP_FRACTION;
            double yDrop = bounds.getHeight() / DROP_FRACTION;
            double minX = bounds.getMinX() + xDrop;
            double maxX = bounds.getMaxX() - xDrop;
            double minY = bounds.getMinY() + yDrop;
            double maxY = bounds.getMaxY() - yDrop;
            boolean xAdjust = p.getX() > minX && p.getX() < maxX;
            boolean yAdjust = p.getY() > minY && p.getY() < maxY;
            if (xAdjust || yAdjust) {
                double x = xAdjust ? p.getX() : bounds.getCenterX();
                double y = yAdjust ? p.getY() : bounds.getCenterY();
                switch (getVertexShape()) {
                case DIAMOND_SHAPE:
                    return getDiamondPerimeterPoint(bounds, x, y, p);
                case RECTANGLE_SHAPE:
                case ROUNDED_RECTANGLE_SHAPE:
                    return getRectanglePerimeterPoint(bounds, x, y, p);
                }
            }
        }
        switch (getVertexShape()) {
        case ELLIPSE_SHAPE:
            return getEllipsePerimeterPoint(bounds, p);
        case DIAMOND_SHAPE:
            return getDiamondPerimeterPoint(bounds, p);
        default:
            if (JAttr.isManhattanStyle(edge.getAllAttributes())) {
                return getRectanglePerimeterPoint(bounds, p,
                    this == edge.getSource().getParentView());
            } else {
                return getRectanglePerimeterPoint(bounds, p);
            }
        }
    }

    /**
     * Computes the perimeter point on a rectangle, lying on the line from the
     * center in the direction of a given point. This implementation is in fact
     * taken from
     * {@link VertexRenderer#getPerimeterPoint(VertexView, Point2D, Point2D)}.
     */
    private Point2D getRectanglePerimeterPoint(Rectangle2D bounds, Point2D p) {
        double xRadius = bounds.getWidth() / 2;
        double yRadius = bounds.getHeight() / 2;
        double centerX = bounds.getCenterX();
        double centerY = bounds.getCenterY();
        double dx = p.getX() - centerX; // Compute Angle
        double dy = p.getY() - centerY;
        double alpha = Math.atan2(dy, dx);
        double pi = Math.PI;
        double t = Math.atan2(yRadius, xRadius);
        double outX, outY;
        if (alpha < -pi + t || alpha > pi - t) { // Left edge
            outX = centerX - xRadius;
            outY = centerY - xRadius * Math.tan(alpha);
        } else if (alpha < -t) { // Top Edge
            outY = centerY - yRadius;
            outX = centerX - yRadius * Math.tan(pi / 2 - alpha);
        } else if (alpha < t) { // Right Edge
            outX = centerX + xRadius;
            outY = centerY + xRadius * Math.tan(alpha);
        } else { // Bottom Edge
            outY = centerY + yRadius;
            outX = centerX + yRadius * Math.tan(pi / 2 - alpha);
        }
        return new Point2D.Double(outX, outY);
    }

    /**
     * Computes a perimeter point on a rectangle, for a manhattan-style line
     * entering horizontally or vertically.
     * @param bounds the bounds of the rectangle
     * @param p the reference point for the perimeter point
     * @param horizontal if <code>true</code>, the line will enter horizontally;
     *        look for a point on one of the sides
     */
    private Point2D getRectanglePerimeterPoint(Rectangle2D bounds, Point2D p,
            boolean horizontal) {
        double centerX = bounds.getCenterX();
        double centerY = bounds.getCenterY();
        double dx = p.getX() - centerX;
        double dy = p.getY() - centerY;
        double outX, outY;
        if (horizontal) { // left or right side
            outX = dx < 0 ? bounds.getMinX() : bounds.getMaxX();
            double room = bounds.getHeight() * (1 - 2 / DROP_FRACTION) * 0.5;
            outY =
                centerY + room * Math.signum(dy)
                    * Math.min(Math.abs(dy) / MAX_RATIO_DISTANCE, 1);
        } else { // top or bottom
            outY = dy < 0 ? bounds.getMinY() : bounds.getMaxY();
            double room = bounds.getWidth() * (1 - 2 / DROP_FRACTION) * 0.5;
            outX =
                centerX + room * Math.signum(dx)
                    * Math.min(Math.abs(dx) / MAX_RATIO_DISTANCE, 1);
        }
        return new Point2D.Double(outX, outY);
    }

    /**
     * Computes the perimeter point on a rectangle, lying on the line from a
     * given point in the direction of another point. The <code>from</code>
     * point is guaranteed to be either horizontally or vertically aligned with
     * the <code>to</code> point. This implementation is in fact taken from
     * {@link VertexRenderer#getPerimeterPoint(VertexView, Point2D, Point2D)}.
     */
    private Point2D getRectanglePerimeterPoint(Rectangle2D bounds,
            double fromX, double fromY, Point2D to) {
        double dx = to.getX() - fromX; // Compute Angle
        double dy = to.getY() - fromY;
        double outX, outY;
        if (dx < 0) { // Left edge
            outX = bounds.getMinX();
            outY = fromY;
        } else if (dy < 0) { // Top Edge
            outX = fromX;
            outY = bounds.getMinY();
        } else if (dx > 0) { // Right Edge
            outX = bounds.getMaxX();
            outY = fromY;
        } else { // Bottom Edge
            outX = fromX;
            outY = bounds.getMaxY();
        }
        return new Point2D.Double(outX, outY);
    }

    /**
     * Computes the perimeter point on an ellipse lying on the line from the
     * center in the direction of a given point. The ellipse is given by its
     * bounds.
     */
    private Point2D getEllipsePerimeterPoint(Rectangle2D bounds, Point2D p) {
        double centerX = bounds.getCenterX();
        double centerY = bounds.getCenterY();
        double dx = p.getX() - centerX;
        double dy = p.getY() - centerY;
        double pDist = dx * dx + dy * dy;
        double xFrac = Math.sqrt(dx * dx / pDist) * bounds.getWidth() / 2;
        double yFrac = Math.sqrt(dy * dy / pDist) * bounds.getHeight() / 2;
        double outX = centerX + xFrac * Math.signum(dx);
        double outY = centerY + yFrac * Math.signum(dy);
        return new Point2D.Double(outX, outY);
    }

    /**
     * Computes the perimeter point on a diamond lying on the line from the
     * center in the direction of a given point. The diamond is given by its
     * outer bounds.
     */
    private Point2D getDiamondPerimeterPoint(Rectangle2D bounds, Point2D to) {
        double centerX = bounds.getCenterX();
        double centerY = bounds.getCenterY();
        double dx = to.getX() - centerX; // Compute Angle
        double dy = to.getY() - centerY;
        double startX, startY, endX, endY;
        if (dx <= 0 && dy <= 0) { // top left edge
            startX = bounds.getMinX();
            startY = centerY;
            endX = centerX;
            endY = bounds.getMinY();
        } else if (dy <= 0) { // top right edge
            startX = centerX;
            startY = bounds.getMinY();
            endX = bounds.getMaxX();
            endY = centerY;
        } else if (dx <= 0) { // bottom left edge
            startX = bounds.getMinX();
            startY = centerY;
            endX = centerX;
            endY = bounds.getMaxY();
        } else { // Bottom right edge
            startX = centerX;
            startY = bounds.getMaxY();
            endX = bounds.getMaxX();
            endY = centerY;
        }
        return lineIntersection(centerX, centerY, dx, dy, startX, startY, endX
            - startX, endY - startY);
    }

    /**
     * Computes the perimeter point on a diamond lying on the line from a given
     * point in the direction of another point. The <code>from</code> point is
     * guaranteed to be either horizontally or vertically aligned with the
     * <code>to</code> point. The diamond is given by its outer bounds.
     */
    private Point2D getDiamondPerimeterPoint(Rectangle2D bounds, double fromX,
            double fromY, Point2D to) {
        double centerX = bounds.getCenterX();
        double centerY = bounds.getCenterY();
        double toX = to.getX();
        double toY = to.getY();
        double dx = toX - fromX; // Compute direction
        double dy = toY - fromY;
        double startX, startY, endX, endY;
        if (toX <= centerX && toY <= centerY) { // top left edge
            startX = bounds.getMinX();
            startY = centerY;
            endX = centerX;
            endY = bounds.getMinY();
        } else if (toY <= centerY) { // top right edge
            startX = centerX;
            startY = bounds.getMinY();
            endX = bounds.getMaxX();
            endY = centerY;
        } else if (toX <= centerX) { // bottom left edge
            startX = bounds.getMinX();
            startY = centerY;
            endX = centerX;
            endY = bounds.getMaxY();
        } else { // Bottom right edge
            startX = centerX;
            startY = bounds.getMaxY();
            endX = bounds.getMaxX();
            endY = centerY;
        }
        return lineIntersection(fromX, fromY, dx, dy, startX, startY, endX
            - startX, endY - startY);
    }

    /**
     * Computes the intersection of two lines.
     * @param x1 Start point of the first line (x-coordinate)
     * @param y1 Start point of the first line (y-coordinate)
     * @param dx1 vector of the first line (x-direction)
     * @param dy1 vector of the first line (y-direction)
     * @param x2 Start point of the second line (x-coordinate)
     * @param y2 Start point of the second line (y-coordinate)
     * @param dx2 vector of the second line (x-direction)
     * @param dy2 vector of the second line (y-direction)
     * @return Intersection point of the two lines, of <code>null</code> if they
     *         are parallel
     */
    private Point2D lineIntersection(double x1, double y1, double dx1,
            double dy1, double x2, double y2, double dx2, double dy2) {
        double above = dx1 * (y2 - y1) - dy1 * (x2 - x1);
        double below = dx2 * dy1 - dx1 * dy2;
        if (below == 0) {
            // the lines are parallel
            return null;
        } else {
            double c2 = above / below;
            double x = x2 + dx2 * c2;
            double y = y2 + dy2 * c2;
            return new Point2D.Double(x, y);
        }
    }

    @Override
    public String toString() {
        return String.format("Vertex view for %s", getCell());
    }

    /**
     * The following is a rather awful hack to ensure the same kind of vertex
     * emphasis throughout editing. It is called from
     * {@link EditorMarqueeHandler} and from {@link JEdgeView.MyEdgeHandle}.
     */
    void paintArmed(Graphics g) {
        Graphics2D newG = (Graphics2D) g.create();
        double scale = this.jGraph.getScale();
        newG.scale(scale, scale);
        // paint the border to erase it (we're in XOR mode)
        this.jGraph.getUI().paintCell(newG, this, getBounds(), true);
        this.armed = true;
        this.jGraph.getUI().paintCell(newG, this, getBounds(), true);
        this.armed = false;
        newG.dispose();
    }

    /** Underlying graph model, used to construct the autosize. */
    private final JGraph jGraph;
    /** Flag indicating that the vertex is empty, i.e., there is no text inside. */
    /** The text on this vertex. */
    private String text;
    /** Additional space to add to view bounds to make room for special borders. */
    private Insets insets;
    /** 
     * Temporary flag set to indicate that this cell should be painted 
     * as selected.
     */
    private boolean armed;

    // switch off port magic

    static {
        PortView.allowPortMagic = false;
    }

    /** Constant indicating a rounded rectangular vertex. */
    static public final int ROUNDED_RECTANGLE_SHAPE = 0;
    /** Constant indicating an ellipse-shaped vertex. */
    static public final int ELLIPSE_SHAPE = 1;
    /** Constant indicating a diamond-shaped vertex. */
    static public final int DIAMOND_SHAPE = 2;
    /** Constant indicating a rectangular vertex. */
    static public final int RECTANGLE_SHAPE = 3;

    /** HTML tag for the text display font. */
    private static final HTMLTag fontTag;

    static {
        Font font = GraphConstants.DEFAULTFONT;
        String face;
        int size;
        if (font == null) {
            face = "Arial";
            size = -1;
        } else {
            face = font.getFamily();
            // actually a slightly smaller font is more in line with
            // the edge font size, but then the forall symbol is not
            // available
            size = font.getSize() - 2;
        }
        String argument =
            String.format("font-family:%s; font-size:%dpx", face, size);
        fontTag = createSpanTag(argument);
    }

    /**
     * Fraction of the width or height that is the minimum for special perimeter
     * point placement.
     */
    static private final double DROP_FRACTION = 10;
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

        public MyRenderer getRendererComponent(org.jgraph.JGraph graph,
                CellView view, boolean sel, boolean focus, boolean preview) {
            assert view instanceof JVertexView : String.format(
                "This renderer is only meant for %s", JVertexView.class);
            this.view = (JVertexView) view;
            this.selectionColor = graph.getHighlightColor();
            AttributeMap attributes = view.getAllAttributes();
            this.dash = GraphConstants.getDashPattern(attributes);
            this.lineColor = GraphConstants.getLineColor(attributes);
            this.selected = sel;
            boolean emph = this.view.armed || sel;
            float lineWidth = GraphConstants.getLineWidth(attributes);
            if (emph) {
                lineWidth += JAttr.EMPH_INCREMENT;
            }
            this.lineWidth = lineWidth;

            AttributeMap secondMap = (AttributeMap) attributes.get("line2map");
            if (secondMap != null) {
                this.twoLines = true;
                this.line2color = GraphConstants.getLineColor(secondMap);
                this.line2width = GraphConstants.getLineWidth(secondMap);
                this.line2dash = GraphConstants.getDashPattern(secondMap);
            } else {
                this.twoLines = false;
            }

            setOpaque(GraphConstants.isOpaque(attributes));
            Color foreground = GraphConstants.getForeground(attributes);
            setForeground((foreground != null) ? foreground
                    : graph.getForeground());
            Color background = GraphConstants.getBackground(attributes);
            background =
                (background != null) ? background : graph.getBackground();
            if (emph) {
                background =
                    new Color(Math.max(background.getRed() - 30, 0), Math.max(
                        background.getGreen() - 30, 0), Math.max(
                        background.getBlue() - 30, 0), background.getAlpha());
            }
            setBackground(background);
            Font font = GraphConstants.getFont(attributes);
            setFont((font != null) ? font : graph.getFont());
            setBorder(createEmptyBorder());
            setText(this.view.getText());
            this.error = this.view.getCell().hasError();
            return this;
        }

        /**
         * In addition to called <code>super.paint()</code>, also draws the
         * selection border, if the vertex is selected.
         */
        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            double width = getSize().getWidth();
            double height = getSize().getHeight();
            Shape shape = getShape(width, height, this.lineWidth, 0);
            if (isOpaque()) {
                paintBackground(g2, shape);
            }
            paintText(g2);
            paintBorder(g2, shape);
            if (this.error) {
                shape = getShape(width, height, 0, EXTRA_BORDER_SPACE);
                g2.setColor(JAttr.ERROR_COLOR);
                //               
                //                g2.setPaint(JAttr.createPaint(shape.getBounds(),
                //                    JAttr.ERROR_COLOR));
                g2.fill(shape);
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
            Paint newPaint =
                JAttr.createPaint(shape.getBounds(), getBackground());
            g.setPaint(newPaint);
            g.fill(shape);
            g.setPaint(oldPaint);
        }

        /**
         * Creates and returns an empty border with the right insets to position
         * text correctly.
         */
        private Border createEmptyBorder() {
            Insets i = this.view.getInsets();
            return i == null ? null : BorderFactory.createEmptyBorder(i.top
                + EXTRA_BORDER_SPACE, i.left + EXTRA_BORDER_SPACE, i.bottom
                + EXTRA_BORDER_SPACE, i.right + EXTRA_BORDER_SPACE);
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
            Dimension result = null;
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
                    // set a large size to avoid spurious line breaks
                    // which would mess up the size calculation
                    // setSize(1000, 1000);
                    result = super.getPreferredSize();
                    // reset the border
                    setBorder(border);
                }
                this.sizeMap.put(text, result);
            }
            // adjust for view insets
            Insets i = computeInsets(result.width, result.height);
            // try to avoid conversion back and forth to double
            result =
                new Dimension(result.width + i.left + i.right + 2
                    * EXTRA_BORDER_SPACE, result.height + i.top + i.bottom + 2
                    * EXTRA_BORDER_SPACE);
            // store the insets in the view, to be used
            // when actually drawing the view
            this.view.setInsets(i);
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
            return String.valueOf(array);
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
                result = (Insets) DEFAULT_INSETS.clone();// b.getBorderInsets(getRenderer());
            }
            // correct for the line width
            int line =
                (int) GraphConstants.getLineWidth(this.view.getAllAttributes());
            result.top += line;
            result.left += line;
            result.bottom += line;
            result.right += line;

            // add space needed for the border
            switch (this.view.getVertexShape()) {
            case ELLIPSE_SHAPE:
                result.left += textWidth / 8;
                result.right += textWidth / 8;
                result.top += textHeight / 8;
                result.bottom += textHeight / 8;
                break;
            case DIAMOND_SHAPE:
                result.left += textWidth / 3;
                result.right += textWidth / 3;
                result.top += textHeight / 3;
                result.bottom += textHeight / 3;
                break;
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
        private Shape getShape(double width, double height, double lineWidth,
                int extension) {
            // subtract the extra border space
            double extra = EXTRA_BORDER_SPACE - extension;
            double x = lineWidth / 2 + extra;
            double y = lineWidth / 2 + extra;
            width = width - lineWidth - 2 * extra;
            height = height - lineWidth - 2 * extra;
            switch (this.view.getVertexShape()) {
            case ELLIPSE_SHAPE:
                return new Ellipse2D.Double(x, y, width, height);
            case DIAMOND_SHAPE:
                return createDiamondShape(x, y, width, height);
            case RECTANGLE_SHAPE:
                return new Rectangle2D.Double(x, y, width, height);
            default:
                return new RoundRectangle2D.Double(x, y, width, height,
                    JAttr.ARC_SIZE, JAttr.ARC_SIZE);
            }
        }

        /** Creates a diamond shape inscribed in the bounds given in the parameters. */
        private Shape createDiamondShape(double x, double y, double width,
                double height) {
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
        protected void firePropertyChange(String propertyName, Object oldValue,
                Object newValue) {
            // Strings get interned...
            if ("text".equals(propertyName)) {
                super.firePropertyChange(propertyName, oldValue, newValue);
            }
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note </a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, byte oldValue,
                byte newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note </a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, char oldValue,
                char newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note </a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, short oldValue,
                short newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note </a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, int oldValue,
                int newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note </a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, long oldValue,
                long newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note </a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, float oldValue,
                float newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note </a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, double oldValue,
                double newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons. See the <a
         * href="#override">Implementation Note </a> for more information.
         */
        @Override
        public void firePropertyChange(String propertyName, boolean oldValue,
                boolean newValue) {
            // empty
        }

        /** The vertex view that is currently installed. */
        private JVertexView view;
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
        /** Mapping from (HTML) text to the preferred size for that text. */
        private final Map<String,Dimension> sizeMap =
            new HashMap<String,Dimension>();

    }
}
