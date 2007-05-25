/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: JVertexView.java,v 1.12 2007-05-25 09:25:29 rensink Exp $
 */
package groove.gui.jgraph;

import static groove.util.Converter.HTML_TAG;
import static groove.util.Converter.createColorTag;
import static groove.util.Converter.createSpanTag;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.util.Converter.HTMLTag;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.PortView;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;


/**
 * A multi-lined vertex view that caches the label text. The functionality for multi-line editing
 * was taken from {@link org.jgraph.cellview.JGraphMultilineView}, but the class had to be copied
 * to turn the line wrap off.
 * @author Arend Rensink
 * @version $Revision: 1.12 $
 */
public class JVertexView extends VertexView {
    /**
     * Creates a vertex view for a given node, to be displayed on a given graph.
     * @param jNode the node underlying the view
     * @param jGraph the graph on which the node is to be displayed
     */
    public JVertexView(JVertex jNode, JGraph jGraph) {
        super(jNode);
        this.jGraph = jGraph;
		refresh(jGraph.getModel(), jGraph.getGraphLayoutCache(), false);
        jGraph.updateAutoSize(this);
    }
    
    /**
     * Specialises the return type.
	 */
	@Override
	public JVertex getCell() {
		return (JVertex) super.getCell();
	}

	/**
     * This implementation returns the (static) {@link MyRenderer}.
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
	public void refresh(GraphModel model, CellMapper mapper, boolean createDependentViews) {
		super.refresh(model, mapper, createDependentViews);
		// modify the bounds to make room for the border
		text = computeText();
		insets = computeInsets(text.length() == 0);
		adjustBounds(insets);
	}

	/**
	 * Adjusts the cached bounds for this vertex by a given inset.
	 */
	private void adjustBounds(Insets i) {
		Rectangle2D b = getCachedBounds();
		b.setFrame(b.getX(), b.getY(), b.getWidth()+i.left+i.right, b.getHeight()+i.top+i.bottom);
	}
	
	/** 
	 * Computes insets for this view, using the view border and 
	 * taking into account the shape of the vertex.
	 */
	private Insets computeInsets(boolean empty) {
		// calculate the insets of the border
//		Border b = GraphConstants.getBorder(getAllAttributes());
//		if (b == null) {
//			top = left = bottom = right = 0;
//		} else {
		Insets result;
		if (empty) {
			result = (Insets) EMPTY_INSETS.clone();
		} else {
			result = (Insets) DEFAULT_INSETS.clone();//b.getBorderInsets(getRenderer());
		}
		int line = (int) GraphConstants.getLineWidth(getAllAttributes());
		result.top += line;
		result.left += line;
		result.bottom += line;
		result.right += line;
// }
		// add space needed for an oval border
		switch (getVertexShape()) {
		case ELLIPSE_SHAPE:
			Rectangle2D bounds = getCachedBounds();
			result.left += (int) bounds.getWidth()/8;
			result.right += (int) bounds.getWidth()/8;
			result.top += (int) bounds.getHeight()/8;
			result.bottom += (int) bounds.getHeight()/8;
			break;
		case DIAMOND_SHAPE:
			bounds = getCachedBounds();
			result.left += (int) bounds.getWidth()/3;
			result.right += (int) bounds.getWidth()/3;
			result.top += (int) bounds.getHeight()/3;
			result.bottom += (int) bounds.getHeight()/3;
			break;
		}
		return result;
	}

	/** 
	 * Retrieves the HTML text for the vertex,
	 * and adapts the text colour to the line colour if the line colour is not
	 * black.
	 * @see JVertex#getHtmlText()
	 */
	private String computeText() {
		StringBuilder result = new StringBuilder(getCell().getHtmlText());
		if (result.length() > 0) {
			Color lineColor = GraphConstants.getLineColor(getAllAttributes());
			if (lineColor != null && ! lineColor.equals(Color.BLACK)) {
				createColorTag(lineColor).on(result);
			}
			return HTML_TAG.on(fontTag.on(result)).toString();
		} else {
			return "";
		}
	}

	/**
	 * Callback method idicating that a certain vertex is a data vertex
	 * (and so should be rendered differently).
	 */
	private int getVertexShape() {
		if (getCell() instanceof GraphJVertex){
			GraphJVertex cell = (GraphJVertex) getCell();
			if (cell.getActualNode() instanceof ValueNode) {
				return ELLIPSE_SHAPE;
			} else if (cell.getActualNode() instanceof ProductNode) {
				return DIAMOND_SHAPE;
			}
		}
		return RECTANGLE_SHAPE;
	}
	
	/** Returns the insets computed for this vertex view. */
	private final Insets getInsets() {
		return insets;
	}

	/** Returns the (html formatted) text to be displayed in this vertex view. */
	private final String getText() {
		return text;
	}

	/** Indicates if the underlying cell is currently emphasized in the model. */
	private boolean isEmphasized() {
		return jGraph.getModel().isEmphasized(getCell());
	}

	/** 
	 * Returns the line width of the vertex view.
	 * This is the line width stored in the attributes, augmented by
	 * {@link JAttr#EMPH_INCREMENT} if the view is emphasized.
	 * @see #isEmphasized() 
	 */
	public float getLinewidth() {
		float result = GraphConstants.getLineWidth(getAllAttributes());
		if (isEmphasized()) {
			result += JAttr.EMPH_INCREMENT;
		}
		return result;
	}
	
	/** Returns the shape of the vertex view. */
	public Shape getShape(Dimension size) {
		float line = getLinewidth();
		float x = line/2;
		float y = line/2;
		float width = size.width-line;
		float height = size.height-line;
    	switch (getVertexShape()) {
    	case ELLIPSE_SHAPE:
        	return new Ellipse2D.Float(x, y, width, height);
    	case DIAMOND_SHAPE:
    		return createDiamondShape(x, y, width, height);
    	default:
    		return createRectangleShape(x, y, width, height);
    	}
	}
	
	/** Creates a shape tracing the bounds given in the parameters. */
	private Shape createRectangleShape(float x, float y, float width, float height) {
		GeneralPath result = new GeneralPath(GeneralPath.WIND_NON_ZERO, 5);
		result.moveTo(x, y);
		result.lineTo(x+width, y);
		result.lineTo(x+width, y+height);
		result.lineTo(x, y+height);
		result.closePath();
		return result;
	}
	
	/** Creates a diamond shape inscibed in the bounds given in the parameters. */
	private Shape createDiamondShape(float x, float y, float width, float height) {
		GeneralPath result = new GeneralPath(GeneralPath.WIND_NON_ZERO, 5);
		result.moveTo(x+width/2, y);
		result.lineTo(x+width, y+height/2);
		result.lineTo(x+width/2, y+height);
		result.lineTo(x, y+height/2);
		result.closePath();
		return result;
	}
	/**
	 * Overwrites the super method because we have a different renderer.
	 */
	@Override
	public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
		if (source == null) {
			// be smart about positioning the perimeter point if p is within
			// the limits of the vertex itself, in either x or y coordinate
			Rectangle2D bounds = getBounds();
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
            if (JAttr.isPerpendicularStyle(edge.getAllAttributes())) {
                return getRectanglePerimeterPoint(bounds, p, this == edge.getSource().getParentView());
            } else {
                return getRectanglePerimeterPoint(bounds, p);
            }
		}
	}

    /**
     * Computes the perimeter point on a rectangle, lying on the line from the center 
     * in the direction of a given point.
     * This implementation is in fact taken from {@link VertexRenderer#getPerimeterPoint(VertexView, Point2D, Point2D)}.
     */
    private Point2D getRectanglePerimeterPoint(Rectangle2D bounds, Point2D p) {
        double xRadius = bounds.getWidth()/2;
        double yRadius = bounds.getHeight()/2;
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
            outX = centerX - yRadius * Math.tan(pi/2 - alpha);
        } else if (alpha < t) { // Right Edge
            outX = centerX + xRadius;
            outY = centerY + xRadius * Math.tan(alpha);
        } else { // Bottom Edge
            outY = centerY + yRadius;
            outX = centerX + yRadius * Math.tan(pi/2 - alpha);
        }
        return new Point2D.Double(outX, outY);
    }

    /**
     * Computes a perimeter point on a rectangle, for a perpendicular line
     * entering horizontally or vertically.
     * @param bounds the bounds of the rectangle
     * @param p the reference point for the perimeter point
     * @param horizontal if <code>true</code>, the line will enter horizontally; look for a point on one of the sides
     */
    private Point2D getRectanglePerimeterPoint(Rectangle2D bounds, Point2D p, boolean horizontal) {
        double centerX = bounds.getCenterX();
        double centerY = bounds.getCenterY();
        double dx = p.getX() - centerX;
        double dy = p.getY() - centerY;
        double outX, outY;
        if (horizontal) { // left or right side
            outX = dx < 0 ? bounds.getMinX() : bounds.getMaxX();
            double room = bounds.getHeight()*(1-2/DROP_FRACTION)*0.5;
            outY = centerY + room * Math.signum(dy) * Math.min(Math.abs(dy)/MAX_RATIO_DISTANCE, 1); 
        } else { //top or bottom
            outY = dy < 0 ? bounds.getMinY() : bounds.getMaxY();
            double room = bounds.getWidth()*(1-2/DROP_FRACTION)*0.5;
            outX = centerX + room * Math.signum(dx) * Math.min(Math.abs(dx)/MAX_RATIO_DISTANCE, 1);
        }
        return new Point2D.Double(outX, outY);
    }

	/**
	 * Computes the perimeter point on a rectangle, lying on the line from a given point
	 * in the direction of another point.
	 * The <code>from</code> point is guaranteed to be either horizontally or
	 * vertically aligned with the <code>to</code> point.
	 * This implementation is in fact taken from {@link VertexRenderer#getPerimeterPoint(VertexView, Point2D, Point2D)}.
	 */
	private Point2D getRectanglePerimeterPoint(Rectangle2D bounds, double fromX, double fromY, Point2D to) {
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
	 * Computes the perimeter point on an ellipse lying on the line from the center 
	 * in the direction of a given point.
	 * The ellipse is given by its bounds. 
	 */
	private Point2D getEllipsePerimeterPoint(Rectangle2D bounds, Point2D p) {
		double centerX = bounds.getCenterX();
		double centerY = bounds.getCenterY();
		double dx = p.getX() - centerX;
		double dy = p.getY() - centerY;
		double pDist = dx*dx + dy*dy;
		double xFrac = Math.sqrt(dx*dx/pDist) * bounds.getWidth() / 2;
		double yFrac = Math.sqrt(dy*dy/pDist) * bounds.getHeight() / 2;
		double outX = centerX + xFrac * Math.signum(dx);
		double outY = centerY + yFrac * Math.signum(dy);
		return new Point2D.Double(outX, outY);
	}

	/** 
	 * Computes the perimeter point on a diamond lying on the line from the center 
	 * in the direction of a given point.
	 * The diamond is given by its outer bounds. 
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
		return lineIntersection(centerX, centerY, dx, dy, startX, startY, endX-startX, endY-startY);
	}

	/** 
	 * Computes the perimeter point on a diamond lying on the line from a given point
	 * in the direction of another point.
	 * The <code>from</code> point is guaranteed to be either horizontally or
	 * vertically aligned with the <code>to</code> point.
	 * The diamond is given by its outer bounds. 
	 */
	private Point2D getDiamondPerimeterPoint(Rectangle2D bounds, double fromX, double fromY, Point2D to) {
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
		return lineIntersection(fromX, fromY, dx, dy, startX, startY, endX-startX, endY-startY);
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
	 * @return Intersection point of the two lines, of <code>null</code> if they are parallel
	 */
	private Point2D lineIntersection(double x1, double y1, double dx1, double dy1, double x2, double y2, double dx2, double dy2) {
		double above = dx1*(y2-y1) - dy1*(x2-x1);
		double below = dx2*dy1 - dx1*dy2;
		if (below == 0) {
			// the lines are parallel
			return null;
		} else {
			double c2 = above/below;
			double x = x2+dx2*c2;
			double y = y2+dy2*c2;
			return new Point2D.Double(x,y);
		}
	}

	@Override
    public String toString() {
    	return String.format("Vertex view for %s", getCell());
    }
	
    /**
     * In addition to calling the super method, calls {@link JGraph#updateAutoSize(CellView)}
     * for this view.
	 */
	@Override
	public Map changeAttributes(Map change) {
		Map result = super.changeAttributes(change);
		text = computeText();
		jGraph.updateAutoSize(this);
		return result;
	}

	/**
     * The following is a rather awful hack to ensure the same kind of vertex
     * emphasis throughout editing.
     * It is called from {@link EditorMarqueeHandler} and from {@link JEdgeView.MyEdgeHandle}.
     */
    void paintArmed(Graphics g) {
        Color previousColor = g.getColor();
        int x = (int) bounds.getX();
        int y = (int) bounds.getY();
        int width = (int) bounds.getWidth();
        int height = (int) bounds.getHeight();
        g.setColor(GraphConstants.getLineColor(getAttributes()));
        // repaint the standard border to erase it 
        JAttr.DEFAULT_BORDER.paintBorder(jGraph, g, x, y, width, height);
        JAttr.EMPH_BORDER.paintBorder(jGraph, g, x, y, width, height);
        g.setColor(previousColor);
    }

    /** Underlying graph model, used to construct the autosize. */
    private final JGraph jGraph;
    /** Flag indicating that the vertex is empty, i.e., there is no text inside. */
    /** The text on this vertex. */
    private String text;
    /** Additional space to add to view bounds to make . */
    private Insets insets;
    
    // switch off port magic
    
    static {
    	PortView.allowPortMagic = false;
    }
    
    /** Constant indicating a rectangular vertex. */
    static public final int RECTANGLE_SHAPE = 0;
    /** Constant indicating an ellise-shaped vertex. */
    static public final int ELLIPSE_SHAPE = 1;
    /** Constant indicating a diamond-shaped vertex. */
    static public final int DIAMOND_SHAPE = 2;
    
    /** HTML tag for the text display font. */
    private static final HTMLTag fontTag;

    static {
        Font font = GraphConstants.DEFAULTFONT;
        String face;
        int size;
        if (font == null) {
            face = "Arial";
            size= -1;
        } else {
            face = font.getFamily();
            size = font.getSize() - 2;
        }
        String argument = String.format("font-family:%s; font-size:%dpx", face, size);
        fontTag = createSpanTag(argument);
    }
    
    /** Fraction of the width or height that is the minimum for special perimeter point placement. */
    static private final double DROP_FRACTION = 10;
    /** Maximal distance (horizontal or vertical) for perpendicular perimeter points to be placed in ratio. */
    static private final double MAX_RATIO_DISTANCE = 250;
    /** Insets for vertices that contain text. */
    static private final Insets DEFAULT_INSETS = new Insets(2,4,2,4);
    /** Insets for empty vertices. */
    static private final Insets EMPTY_INSETS = new Insets(0,0,0,0);

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

        public Component getRendererComponent(org.jgraph.JGraph graph, CellView view, boolean sel,
                boolean focus, boolean preview) {
        	assert view instanceof JVertexView : String.format("This renderer is only meant for %s", JVertexView.class);
        	this.view = (JVertexView) view;
            this.selected = sel;
            this.selectionColor = graph.getHighlightColor();
            AttributeMap attributes = view.getAllAttributes();
            this.dash = GraphConstants.getDashPattern(attributes);
            this.lineColor = GraphConstants.getLineColor(attributes);
			setOpaque(GraphConstants.isOpaque(attributes));
			Color foreground = GraphConstants.getForeground(attributes);
			setForeground((foreground != null) ? foreground : graph.getForeground());
			Color background = GraphConstants.getBackground(attributes);
			setBackground((background != null) ? background : graph.getBackground());
			Font font = GraphConstants.getFont(attributes);
			setFont((font != null) ? font : graph.getFont());
            setText(this.view.getText());
            return this;
        }

        /** This method calculates the preferred size without the border. */
        @Override
		public synchronized Dimension getPreferredSize() {
        	// unset the border
        	Border border = getBorder();
        	setBorder(null);
			Dimension dimension = super.getPreferredSize();
			// the preferred size may be too high because line breaks are
			// taken into account, so try again after the width has been set
			setSize(dimension);
			Dimension result = super.getPreferredSize();
			// reset the border
			setBorder(border);
			return result;
		}

		/**
         * In addition to called <code>super.paint()</code>, also draws
         * the selection border, if the vertex is selected.
         */
        @Override
        public void paint(Graphics g) {
        	Graphics2D g2 = (Graphics2D) g;
        	Shape shape = view.getShape(getSize());
        	if (isOpaque()) {
        		paintBackground(g2, shape);
        	}
        	paintText(g2);
        	paintForeground(g2, shape);
        	if (selected) {
        		paintSelectionBorder(g2, shape);
        	}
        }
        
		/** Paints this vertex with an oval border. */
        private void paintText(Graphics2D g) {
			boolean tmp = selected;
			try {
				setBorder(createEmptyBorder());
				setOpaque(false);
				selected = false;
				g.setColor(getForeground());
				super.paint(g);
			} finally {
				selected = tmp;
			}
        }

		/**
		 * Paints the border, with a given shape.
		 */
		private void paintForeground(Graphics2D g, Shape shape) {
			g.setColor(lineColor);
			g.setStroke(JAttr.createStroke(view.getLinewidth(), dash));
			g.draw(shape);
		}

		/**
		 * Paints the background, with a given shape.
		 */
		private void paintBackground(Graphics2D g, Shape shape) {
			g.setColor(getBackground());
			g.fill(shape);
		}

		/**
		 * Creates and returns an empty border with the right insets
		 * to position text in an oval vertex correctly.
		 */
		private Border createEmptyBorder() {
			Insets i = view.getInsets();
			return BorderFactory.createEmptyBorder(i.top, i.left, i.bottom, i.right);
		}

        /**
         * Paint a selection border, witha a given shape.
         */
        private void paintSelectionBorder(Graphics2D g, Shape shape) {
			g.setStroke(GraphConstants.SELECTION_STROKE);
			g.setColor(selectionColor);
			g.draw(shape);
		}

        /** Overridden for performance reasons. Copied from {@link org.jgraph.graph.VertexRenderer}. */
        @Override
        public void validate() {
            // empty
        }

        /** Overridden for performance reasons. Copied from {@link org.jgraph.graph.VertexRenderer}. */
        @Override
        public void revalidate() {
            // empty
        }

        /** Overridden for performance reasons. Copied from {@link org.jgraph.graph.VertexRenderer}. */
        @Override
        public void repaint(long tm, int x, int y, int width, int height) {
            // empty
        }

        /** Overridden for performance reasons. Copied from {@link org.jgraph.graph.VertexRenderer}. */
        @Override
        public void repaint(Rectangle r) {
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
        /** Dash pattern for the border. */
        private float[] dash;
    }
}
