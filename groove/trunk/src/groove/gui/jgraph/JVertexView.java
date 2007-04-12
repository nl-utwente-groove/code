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
 * $Id: JVertexView.java,v 1.4 2007-04-12 16:14:49 rensink Exp $
 */
package groove.gui.jgraph;

import groove.util.Converter;
import groove.util.Groove;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphCellEditor;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;


/**
 * A multi-lined vertex view that caches the label text. The functionality for multi-line editing
 * was taken from {@link org.jgraph.cellview.JGraphMultilineView}, but the class had to be copied
 * to turn the line wrap off.
 * @author Arend Rensink
 * @version $Revision: 1.4 $
 */
public class JVertexView extends VertexView {
	/** HTML tag to make text bold. */
    protected static final Converter.HTMLTag strongTag = Converter.createHtmlTag("b");
	/** HTML tag to make text italic. */
    protected static final Converter.HTMLTag italicTag = Converter.createHtmlTag("i");
    /** HTML tag for the text display font. */
    protected static final Converter.HTMLTag fontTag = Converter.createHtmlTag("font", "face=\"Arial\" size=-1");
    /** HTML tag for the hidden style. */
    protected static final Converter.HTMLTag hiddenTag;
    // initialise the hiddenTag
    static {
        Color colour = JAttr.GRAYED_OUT_COLOR;
        int opacity =  (100 * colour.getAlpha())/255;
        String arguments = String.format("style=\"color: rgb(%s,%s,%s); opacity:%s; filter: alpha(opacity=%s);\"",
            colour.getRed(),
            colour.getBlue(),
            colour.getGreen(),
            opacity/100.,
            opacity);
        hiddenTag = Converter.createHtmlTag("span", arguments);
    }
    /** The renderer for all instances of <tt>JVertexView</tt>. */
    static protected final CellViewRenderer renderer = new EditorPaneRenderer();

    /** The editor for all instances of <tt>JVertexView</tt>. */
    static protected final MultiLinedEditor editor = new MultiLinedEditor();
    
    /** The maximum alpha value according to {@link Color#getAlpha()}. */
    private static final int MAX_ALPHA = 255;

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
     * This implementation returns the (static) {@link TextAreaRenderer}.
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
	 * This implementation is in fact taken from {@link VertexRenderer#getPerimeterPoint(VertexView, Point2D, Point2D)}.
	 */
	@Override
	public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
		Rectangle2D bounds = getBounds();
		double x = bounds.getX();
		double y = bounds.getY();
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		double xCenter = x + width / 2;
		double yCenter = y + height / 2;
		double dx = p.getX() - xCenter; // Compute Angle
		double dy = p.getY() - yCenter;
		double alpha = Math.atan2(dy, dx);
		double xout = 0, yout = 0;
		double pi = Math.PI;
		double pi2 = Math.PI / 2.0;
		double beta = pi2 - alpha;
		double t = Math.atan2(height, width);
		if (alpha < -pi + t || alpha > pi - t) { // Left edge
			xout = x;
			yout = yCenter - width * Math.tan(alpha) / 2;
		} else if (alpha < -t) { // Top Edge
			yout = y;
			xout = xCenter - height * Math.tan(beta) / 2;
		} else if (alpha < t) { // Right Edge
			xout = x + width;
			yout = yCenter + width * Math.tan(alpha) / 2;
		} else { // Bottom Edge
			yout = y + height;
			xout = xCenter + height * Math.tan(beta) / 2;
		}
		return new Point2D.Double(xout, yout);
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
    

    /**
     * Multi-line vertex renderer based on a {@link JTextArea}.
     * @author Arend Rensink
     * @version $Revision $
     */
    public static class TextAreaRenderer extends JTextArea implements CellViewRenderer {
    	/** The underlying <code>JGraph</code>. */
        protected transient org.jgraph.JGraph graph = null;

        /** Cached selected value. */
        transient protected boolean selected;

        public Component getRendererComponent(org.jgraph.JGraph graph, CellView view, boolean sel,
                boolean focus, boolean preview) {
            setText(graph.convertValueToString(view));
            this.graph = graph;
            this.selected = sel;
            installAttributes(graph, view.getAllAttributes());
            return this;
        }

        /**
         * In addition to called <code>super.paint()</code>, also draws
         * the selection border, if the vertex is selected.
         */
        @Override
        public void paint(Graphics g) {
        	super.paint(g);
        	paintSelectionBorder(g);
        }

        /**
         * Provided for subclassers to paint a selection border.
         */
        protected void paintSelectionBorder(Graphics g) {
            if (selected) {
                ((Graphics2D) g).setStroke(GraphConstants.SELECTION_STROKE);
                g.setColor(graph.getHighlightColor());
                Dimension d = getSize();
                g.drawRect(0, 0, d.width - 1, d.height - 1);
            }
        }

        protected void installAttributes(org.jgraph.JGraph graph, AttributeMap attributes) {
            setOpaque(GraphConstants.isOpaque(attributes));
            Color foreground = GraphConstants.getForeground(attributes);
            setForeground((foreground != null) ? foreground : graph.getForeground());
            Color background = GraphConstants.getBackground(attributes);
            setBackground((background != null) ? background : graph.getBackground());
            Font font = GraphConstants.getFont(attributes);
            setFont((font != null) ? font : graph.getFont());
            setBorder(GraphConstants.getBorder(attributes));
            assert getBorder() != null;
            Color bordercolor = GraphConstants.getBorderColor(attributes);
            if (getBorder() == null && bordercolor != null) {
                int borderWidth = Math.max(1, Math.round(GraphConstants.getLineWidth(attributes)));
                setBorder(BorderFactory.createLineBorder(bordercolor, borderWidth));
            }
//            Rectangle2D r = GraphConstants.getBounds(attributes);
//            if (r != null) {
//                setBounds(Groove.toRectangle(r));
//            }
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
    }
    
    /**
     * Milti-line vertex renderer, based on a {@link JEditorPane} with <tt>html</tt>
     * formatting. 
     */
    public static class EditorPaneRenderer extends JEditorPane implements CellViewRenderer {
    	/** The underlying <code>JGraph</code>. */
        protected transient org.jgraph.JGraph graph = null;

        /** Cached hasFocus and selected value. */
        transient protected boolean hasFocus, selected, preview;

        public EditorPaneRenderer() {
        	super("text/html", "");
        	setMinimumSize(JAttr.DEFAULT_NODE_SIZE);
        }

        public Component getRendererComponent(org.jgraph.JGraph graph, CellView view, boolean sel,
                boolean focus, boolean preview) {
        	assert view instanceof JVertexView : String.format("This renderer is only meant for %s", JVertexView.class);
//            JVertex jVertex = ((JVertexView) view).getCell();
            setText(((JVertexView) view).getHtmlText());//((JGraph) graph).getModel().isGrayedOut(jVertex));
            this.graph = graph;
            this.selected = sel;
            this.preview = preview;
            this.hasFocus = focus;
            installAttributes(graph, view.getAllAttributes());
            return this;
        }

        /**
         * In addition to called <code>super.paint()</code>, also draws
         * the selection border, if the vertex is selected.
         */
        @Override
        public void paint(Graphics g) {
        	super.paint(g);
        	paintSelectionBorder(g);
        }

        @Override
        public void setText(String text) {
        	if (text.length() == 0) {
        		text = "&nbsp;&nbsp;&nbsp;";
        	}
        	String displayText = fontTag.on(text);
        	super.setText(displayText);
        }
        
        @Override
		public synchronized Dimension getPreferredSize() {
				Dimension dimension = super.getPreferredSize();
				// the preferred size may be too high because line breaks are
				// taken into account
				// so try again after the width has been set
				setSize(dimension);
				dimension = super.getPreferredSize();
				double height = Math.max(dimension.getHeight(),
						JAttr.DEFAULT_NODE_SIZE.getHeight());
				return new Dimension((int) dimension.getWidth(), (int) height);
		}

		/**
         * Provided for subclassers to paint a selection border.
         */
        protected void paintSelectionBorder(Graphics g) {
            if (selected) {
                ((Graphics2D) g).setStroke(GraphConstants.SELECTION_STROKE);
                g.setColor(graph.getHighlightColor());
                Dimension d = getSize();
                g.drawRect(0, 0, d.width - 1, d.height - 1);
            }
        }

        protected void installAttributes(org.jgraph.JGraph graph, AttributeMap attributes) {
            setOpaque(GraphConstants.isOpaque(attributes));
            Color foreground = GraphConstants.getForeground(attributes);
            setForeground((foreground != null) ? foreground : graph.getForeground());
            Color background = GraphConstants.getBackground(attributes);
            setBackground((background != null) ? background : graph.getBackground());
            Font font = GraphConstants.getFont(attributes);
            setFont((font != null) ? font : graph.getFont());
            setBorder(GraphConstants.getBorder(attributes));
            assert getBorder() != null;
            Color bordercolor = GraphConstants.getBorderColor(attributes);
            if (getBorder() == null && bordercolor != null) {
                int borderWidth = Math.max(1, Math.round(GraphConstants.getLineWidth(attributes)));
                setBorder(BorderFactory.createLineBorder(bordercolor, borderWidth));
            }
            Rectangle2D r = GraphConstants.getBounds(attributes);
            if (r != null) {
                setBounds(Groove.toRectangle(r));
            }
        }
    }
}
