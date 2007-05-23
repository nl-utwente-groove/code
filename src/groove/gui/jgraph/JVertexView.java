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
 * $Id: JVertexView.java,v 1.9 2007-05-23 11:36:18 rensink Exp $
 */
package groove.gui.jgraph;

import groove.util.Converter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
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
 * @version $Revision: 1.9 $
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
    public CellViewRenderer getRenderer() {
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
		// modify the bounds and border, in case the vertex is an oval
		oval = isDataVertex();
		if (oval) {
			Rectangle2D b = getCachedBounds();
			extraX = (int) b.getWidth()/8;
			extraY = (int) b.getHeight()/8;
			b.setFrame(b.getX()-extraX, b.getY()-extraY, b.getWidth()+2*extraX, b.getHeight()+2*extraY);
		} else {
			extraX = extraY = 0;
		}
	}

	/**
	 * Callback method idicating that a certain vertex is a data vertex
	 * (and so should be rendered differently).
	 */
	private boolean isDataVertex() {
		return getCell() instanceof GraphJVertex && ((GraphJVertex) getCell()).isDataNode();
	}
	
	/** Indicates if the underlying cell is currently emphasized in the model. */
	private boolean isEmphasized() {
		return jGraph.getModel().isEmphasized(getCell());
	}

	/** 
	 * Retrieves the HTML text for the vertex,
	 * and adapts the text colour to the line colour if the line colour is not
	 * black.
	 * @see JVertex#getHtmlText()
	 */
	public String getHtmlText() {
		String result = getCell().getHtmlText();
		if (result.length() > 0) {
			Color lineColor = GraphConstants.getLineColor(getAllAttributes());
			if (lineColor != null && ! lineColor.equals(Color.BLACK)) {
				result = getColoredText(result, lineColor);
			}
		}
		return result;
	}

	/**
	 * Returns a given HTML-formatted text, surrounded by a HTML tag to 
	 * set it in a given colour.
	 */
	private String getColoredText(String innerText, Color lineColor) {
		StringBuffer result = new StringBuffer();
		int red = lineColor.getRed();
		int blue = lineColor.getBlue();
		int green = lineColor.getGreen();
		int alpha = lineColor.getAlpha();
		result.append("<span style=\"color: rgb(");
		result.append(red);
		result.append(",");
		result.append(green);
		result.append(",");
		result.append(blue);
		result.append(");");
		if (alpha != MAX_ALPHA) {
			// the following is taken from the internet; it is to make
			// sure that all html interpretations set the opacity correctly.
			double alphaFraction = ((double) alpha) / MAX_ALPHA;
			result.append("float:left;filter:alpha(opacity=");
			result.append((int) (100 * alphaFraction));
			result.append(");opacity:");
			result.append(alphaFraction);
			result.append(";");
		}
		result.append("\">");
		result.append(innerText);
		result.append("</span>");
		return result.toString();
	}

	/**
	 * Overwrites the super method because we have a different renderer.
	 */
	@Override
	public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
		if (oval) {
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
        Border emphBorder = JAttr.EMPH_BORDER;
        // repaint the standard border to erase it 
        JAttr.DEFAULT_BORDER.paintBorder(jGraph, g, x, y, width, height);
        emphBorder.paintBorder(jGraph, g, x, y, width, height);
        g.setColor(previousColor);
    }

    /** Underlying graph model, used to construct the autosize. */
    private final JGraph jGraph;
    /** Flag indicating that the view should be drawn as an oval. */
    private boolean oval;
    /** Additional horizontal space to left and right to keep text visible in oval. */
    private int extraX;
    /** Additional vertical space to top and bottom to keep text visible in oval. */
    private int extraY;
    
	/** HTML tag to indicate HTML formatting. */
    private static final Converter.HTMLTag htmlTag = Converter.createHtmlTag("html");
    /** HTML tag for the text display font. */
    private static final Converter.HTMLTag fontTag;
//    /** HTML tag for the hidden style. */
//    private static final Converter.HTMLTag hiddenTag;
    
    static {
        Font font = GraphConstants.DEFAULTFONT;
        String face;
        int size;
        if (font == null) {
            face = "Arial";
            size=-1;
        } else {
            face = font.getFamily();
            size = font.getSize() - 2;
        }
        String argument = String.format("style=\"font-family:%s; font-size:%dpx\"", face, size);
        fontTag = Converter.createHtmlTag("span", argument);
//        // initialise the hiddenTag
//        Color colour = JAttr.GRAYED_OUT_COLOR;
//        int opacity =  (100 * colour.getAlpha())/255;
//        String arguments = String.format("style=\"color: rgb(%s,%s,%s); opacity:%s; filter: alpha(opacity=%s);\"",
//            colour.getRed(),
//            colour.getBlue(),
//            colour.getGreen(),
//            opacity/100.,
//            opacity);
//        hiddenTag = Converter.createHtmlTag("span", arguments);
    }
    /** The renderer for all instances of <tt>JVertexView</tt>. */
    static private final CellViewRenderer renderer = new MyRenderer();

    /** The editor for all instances of <tt>JVertexView</tt>. */
    static private final MultiLinedEditor editor = new MultiLinedEditor();
    
    /** The maximum alpha value according to {@link Color#getAlpha()}. */
    private static final int MAX_ALPHA = 255;

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
            setText(this.view.getHtmlText());
            installAttributes(graph, view.getAllAttributes());
            return this;
        }

        @Override
		public synchronized Dimension getPreferredSize() {
			Dimension dimension = super.getPreferredSize();
			// the preferred size may be too high because line breaks are
			// taken into account
			// so try again after the width has been set
			setSize(dimension);
			return super.getPreferredSize();
//			double width = dimension.getWidth();
//			double height = Math.max(dimension.getHeight(), JAttr.DEFAULT_NODE_SIZE.getHeight());
			// // correct if the shape of the vertex is oval
			// if (dataShape) {
			// ovalExtraWidth = (int) width/6;
			// width += ovalExtraWidth;
			// ovalExtraHeight = (int) height/6;
			// height += ovalExtraHeight;
			// }
//			return new Dimension((int) width, (int) height);
		}

		/**
		 * Sets the attributes for this renderer from a given j-graph and
		 * attribute map.
		 */
		private void installAttributes(org.jgraph.JGraph graph,
				AttributeMap attributes) {
			selectionColor = graph.getHighlightColor();
			setOpaque(GraphConstants.isOpaque(attributes));
			Color foreground = GraphConstants.getForeground(attributes);
			setForeground((foreground != null) ? foreground : graph.getForeground());
			Color background = GraphConstants.getBackground(attributes);
			setBackground((background != null) ? background : graph.getBackground());
			Font font = GraphConstants.getFont(attributes);
			setFont((font != null) ? font : graph.getFont());
			setBorder(GraphConstants.getBorder(attributes));
			assert getBorder() != null;
			linewidth = GraphConstants.getLineWidth(attributes);
			if (view.isEmphasized()) {
				linewidth += JAttr.EMPH_INCREMENT;
			}
			dash = GraphConstants.getDashPattern(attributes);
			// borderWidth = Math.max(1,
			// Math.round(GraphConstants.getLineWidth(attributes)));
			// borderColor = GraphConstants.getBorderColor(attributes);
			// if (getBorder() == null && borderColor != null) {
			// borderWidth = Math.max(1,
			// Math.round(GraphConstants.getLineWidth(attributes)));
			// setBorder(BorderFactory.createLineBorder(borderColor,
			// borderWidth));
			// }
			// Rectangle2D r = GraphConstants.getBounds(attributes);
			// if (r != null) {
			// setBounds(Groove.toRectangle(r));
			// }
		}

		@Override
		public void setText(String text) {
			if (text.length() == 0) {
				text = "&nbsp;&nbsp;&nbsp;";
			}
			String displayText = htmlTag.on(fontTag.on(text));
			super.setText(displayText);
		}

		/**
         * In addition to called <code>super.paint()</code>, also draws
         * the selection border, if the vertex is selected.
         */
        @Override
        public void paint(Graphics g) {
        	if (view.oval) {
        		paintOval((Graphics2D) g);
        	} else {
        		super.paint(g);
        	}
        	if (selected) {
        		paintSelectionBorder((Graphics2D) g);
        	}
        }
        
		/** Paints this vertex with an oval border. */
        private void paintOval(Graphics2D g) {
        	Shape shape = getShape(0,0);
			boolean tmp = selected;
			if (isOpaque()) {
				g.setColor(getBackground());
				g.fill(shape);
			}
			g.setColor(getForeground());
			g.setStroke(JAttr.createStroke(linewidth, dash));
			g.draw(shape);
			try {
				// just paint the text
				Border emptyOvalBorder = getOvalBorder();
				setBorder(emptyOvalBorder);
				setOpaque(false);
				selected = false;
				super.paint(g);
			} finally {
				selected = tmp;
			}
        }

		/**
		 * Creates and returns an empty border with the right insets
		 * to position text in an oval vertex correctly.
		 */
		private Border getOvalBorder() {
			Insets i = getBorder().getBorderInsets(this);
			i.left += view.extraX;
			i.right += view.extraX;
			i.top += view.extraY;
			i.bottom += view.extraY;
			return BorderFactory.createEmptyBorder(i.top, i.left, i.bottom, i.right);
		}

        /**
         * Provided for subclassers to paint a selection border.
         */
        private void paintSelectionBorder(Graphics2D g) {
			g.setStroke(GraphConstants.SELECTION_STROKE);
			g.setColor(selectionColor);
			g.draw(getShape(0,0));
		}

        /** 
         * Sets the shape of the vertex to oval or rectangular.
         * Also sets the extra inset to make room for text in an oval vertex. 
         */
        private Shape getShape(float x, float y) {
			Dimension d = getSize();
			float width = linewidth;
			float half = width/2;
        	if (view.oval) {
            	return new Ellipse2D.Float(x+half, y+half, d.width-width, d.height-width);
        	} else {
        		return new Rectangle2D.Float(x+half, y+half, d.width-width, d.height-width);
        	}
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
        /** Linewidth for the border, in case the shape is oval. */
        private float linewidth;
        /** Deash pattern for the border, in case the shape is oval. */
        private float[] dash;
    }
}
