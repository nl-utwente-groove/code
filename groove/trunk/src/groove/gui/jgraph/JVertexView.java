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
 * $Id: JVertexView.java,v 1.10 2007-05-23 21:37:16 rensink Exp $
 */
package groove.gui.jgraph;

import static groove.util.Converter.HTML_TAG;
import static groove.util.Converter.createColorTag;
import static groove.util.Converter.createSpanTag;
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
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;


/**
 * A multi-lined vertex view that caches the label text. The functionality for multi-line editing
 * was taken from {@link org.jgraph.cellview.JGraphMultilineView}, but the class had to be copied
 * to turn the line wrap off.
 * @author Arend Rensink
 * @version $Revision: 1.10 $
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
		if (isOval()) {
			Rectangle2D bounds = getCachedBounds();
			result.left += (int) bounds.getWidth()/8;
			result.right += (int) bounds.getWidth()/8;
			result.top += (int) bounds.getHeight()/8;
			result.bottom += (int) bounds.getHeight()/8;
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
	private boolean isOval() {
		return getCell() instanceof GraphJVertex && ((GraphJVertex) getCell()).isDataNode();
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
    	if (isOval()) {
        	return new Ellipse2D.Float(line/2, line/2, size.width-line, size.height-line);
    	} else {
    		return new Rectangle2D.Float(line/2, line/2, size.width-line, size.height-line).getFrame();
    	}
	}
//
//	/**
//	 * Returns a HTML span tag that imposes a given color on a text.
//	 */
//	static private HTMLTag getColorTag(Color color) {
//		HTMLTag result = colorTagMap.get(color);
//		if (result == null) {
//			StringBuffer arg = new StringBuffer();
//			int red = color.getRed();
//			int blue = color.getBlue();
//			int green = color.getGreen();
//			int alpha = color.getAlpha();
//			arg.append("color: rgb(");
//			arg.append(red);
//			arg.append(",");
//			arg.append(green);
//			arg.append(",");
//			arg.append(blue);
//			arg.append(");");
//			if (alpha != MAX_ALPHA) {
//				// the following is taken from the internet; it is to make
//				// sure that all html interpretations set the opacity correctly.
//				double alphaFraction = ((double) alpha) / MAX_ALPHA;
//				arg.append("float:left;filter:alpha(opacity=");
//				arg.append((int) (100 * alphaFraction));
//				arg.append(");opacity:");
//				arg.append(alphaFraction);
//				arg.append(";");
//			}
//			result = Converter.createSpanTag(arg.toString());
//			colorTagMap.put(color, result);
//		}
//		return result;
//	}

	/**
	 * Overwrites the super method because we have a different renderer.
	 */
	@Override
	public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
		if (isOval()) {
			return getEllipsePerimeterPoint(getBounds(), p);
		} else {
			return getRectanglePerimeterPoint(getBounds(), p);
		}
	}

	/**
	 * Computes the perimeter point on a rectangle closest to a given point.
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
	 * Computes the perimeter point on an ellipse closes to a given point.
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

//        
//		@Override
//		public void setText(String text) {
////			if (text.length() == 0) {
////				text = "";//"&nbsp;&nbsp;&nbsp;";
////			}
//			String displayText = Converter.HTML_TAG.on(fontTag.on(text));
//			super.setText(displayText);
//		}

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
