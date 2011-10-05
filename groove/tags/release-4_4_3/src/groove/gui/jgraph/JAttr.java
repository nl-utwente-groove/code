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
 * $Id: JAttr.java,v 1.20 2008-01-30 09:33:13 iovka Exp $
 */
package groove.gui.jgraph;

import groove.util.Colors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;

/**
 * Class of constant definitions.
 * @author Arend Rensink
 * @version $Revision$
 */
public class JAttr {
    /** Foreground colour; defaults to #DEFAULT_CELL_COLOR. */
    Color foreColour = DEFAULT_CELL_COLOR;
    /** Background colour for nodes; defaults to #DEFAULT_BACKGROUND. 
     * A {@code null} value means a whitewashed version of the foreground is used. 
     */
    Color backColour = DEFAULT_BACKGROUND;
    /** Edge line colour; defaults to {@code null}. 
     * If {@code null}, the foreground colour is used (see @link #foreColour}. */
    Color lineColour = null;
    /** Border colour; defaults to {@code null}.
     * If {@code null}, the line colour is used (see {@link #lineColour}). */
    Color borderColour = null;
    /** Font to be used; defaults to the standard UI font. */
    Font font = null;
    /** Line width; defaults to 1. */
    int linewidth = 1;
    /** Extra space between text and border (needed if the node can have
     * a thicker border). Defaults to 0. 
     */
    int inset = 0;
    /** Dash pattern; defaults to no dash. */
    float[] dash = NO_DASH;
    /** Line begin; defaults to {@link GraphConstants#ARROW_NONE}. */
    int lineBegin = GraphConstants.ARROW_NONE;
    /** Line end; defaults to {@link GraphConstants#ARROW_CLASSIC}. */
    int lineEnd = GraphConstants.ARROW_CLASSIC;
    /** Line end size; defaults to {@link GraphConstants#DEFAULTDECORATIONSIZE}. */
    int endSize = GraphConstants.DEFAULTDECORATIONSIZE;
    /** Line begin fill; defaults to {@code true} */
    boolean beginFill = true;
    /** Line end fill; defaults to {@code true} */
    boolean endFill = true;
    /** Editable flag; defaults to {@code false} */
    boolean editable = false;
    /** Node moveability; defaults to {@code true} */
    boolean moveable = true;
    /** Node autosizing; defaults to {@code true} */
    boolean autosize = true;
    /** Node resizeability; defaults to {@code true} */
    boolean resizeable = true;
    /** Selectable flag; defaults to {@code true} */
    boolean selectable = true;
    /** Bendable flag; defaults to {@code true} */
    boolean bendable = true;
    /** Node opacity flag; defaults to {@code true} */
    boolean opaque = true;
    /** Edge (dis)connectability; defaults to {@code true} */
    boolean connectable = true;

    /** Creates the attributes common to nodes and edges. */
    private AttributeMap getAttrs() {
        AttributeMap result = new AttributeMap();
        Color foreground = this.foreColour;
        if (foreground != null) {
            GraphConstants.setForeground(result, foreground);
        }
        Color background =
            this.backColour == null ? whitewash(foreground) : this.backColour;
        if (background != null) {
            GraphConstants.setBackground(result, background);
        }
        Color lineColour =
            this.lineColour == null ? foreground : this.lineColour;
        if (lineColour != null) {
            GraphConstants.setLineColor(result, lineColour);
        }
        if (this.font != null) {
            GraphConstants.setFont(result, this.font);
        }
        GraphConstants.setLineWidth(result, this.linewidth);
        if (this.inset != 0) {
            GraphConstants.setInset(result, this.inset);
        }
        if (this.dash != null) {
            GraphConstants.setDashPattern(result, this.dash);
        }
        GraphConstants.setLineBegin(result, this.lineBegin);
        GraphConstants.setLineEnd(result, this.lineEnd);
        GraphConstants.setEndSize(result, this.endSize);
        GraphConstants.setBeginFill(result, this.beginFill);
        GraphConstants.setEndFill(result, this.endFill);
        GraphConstants.setEditable(result, this.editable);
        GraphConstants.setSelectable(result, this.selectable);
        GraphConstants.setMoveable(result, this.moveable);
        GraphConstants.setBendable(result, this.bendable);
        GraphConstants.setOpaque(result, this.opaque);
        GraphConstants.setSizeable(result, this.resizeable);
        GraphConstants.setAutoSize(result, this.autosize);
        GraphConstants.setConnectable(result, this.connectable);
        GraphConstants.setDisconnectable(result, this.connectable);
        GraphConstants.setRouting(result, new org.jgraph.graph.Edge.Routing() {
            /**
             * Returns {@link #NO_PREFERENCE}.
             */
            public int getPreferredLineStyle(EdgeView edge) {
                return NO_PREFERENCE;
            }

            public List<?> route(GraphLayoutCache cache, EdgeView edge) {
                return null;
            }
        });

        return result;
    }

    AttributeMap getNodeAttrs() {
        AttributeMap result = getAttrs().clone();
        Color borderColour =
            this.borderColour == null ? GraphConstants.getLineColor(result)
                    : this.borderColour;
        if (borderColour != null) {
            GraphConstants.setBorderColor(result, borderColour);
        }
        return result;
    }

    AttributeMap getEdgeAttrs() {
        AttributeMap result = getAttrs().clone();
        result.remove(GraphConstants.BACKGROUND);
        return result;
    }

    /** Line style that always makes right edges. */
    public static final int STYLE_MANHATTAN = 14;

    /**
     * The default line style.
     */
    public static final int DEFAULT_LINE_STYLE =
        GraphConstants.STYLE_ORTHOGONAL;

    /**
     * Border insets for default nodes.
     */
    private static final Insets DEFAULT_INSETS = new Insets(0, 3, 0, 3);

    /**
     * An empty border, to be used as the inner border of a compound border,
     * which inserts some space to the left and right of the label text.
     */
    private static final Border EMPTY_INSET_BORDER = new EmptyBorder(
        DEFAULT_INSETS);

    /**
     * Border insets for emphasised nodes.
     */
    private static final Insets EMPH_INSETS = new Insets(-2, 1, -2, 1);

    /**
     * An empty border, to be used as the inner border of a compound border,
     * which inserts some space to the left and right of the label text.
     */
    private static final Border EMPH_INSET_BORDER =
        new EmptyBorder(EMPH_INSETS);

    /**
     * The line width used for edges and node borders.
     */
    public static final int DEFAULT_LINE_WIDTH = 1;

    /** Default background for editor panels. */
    public static final Color EDITOR_BACKGROUND = new Color(255, 255, 230);

    /** Default background for state panels. */
    public static final Color STATE_BACKGROUND = new Color(242, 250, 254);

    /** The size of the rounded corners for rounded-rectangle vertices. */
    public static final int NORMAL_ARC_SIZE = 5;

    /** The size of the rounded corners for strongly rounded-rectangle vertices. */
    public static final int STRONG_ARC_SIZE = 20;

    /** The default font used in the j-graphs. */
    static public final Font DEFAULT_FONT = GraphConstants.DEFAULTFONT;
    /**
     * The default foreground colour used for edges and nodes.
     */
    public static final Color DEFAULT_CELL_COLOR = Color.black;

    /**
     * The default background colour used for nodes.
     */
    public static final Color DEFAULT_BACKGROUND =
        Colors.findColor("243 243 243");

    /**
     * The standard bounds used for nodes.
     */
    public static final Rectangle DEFAULT_NODE_BOUNDS = new Rectangle(10, 10,
        19, 19);

    /**
     * The standard size used for nodes.
     */
    public static final Dimension DEFAULT_NODE_SIZE = new Dimension(
        DEFAULT_NODE_BOUNDS.width, DEFAULT_NODE_BOUNDS.height);

    /**
     * The border used for nodes.
     */
    public static final Border DEFAULT_BORDER = JAttr.createNodeBorder(
        new LineBorder(DEFAULT_CELL_COLOR, DEFAULT_LINE_WIDTH), false);

    /** Space left outside the borders of nodes to enable larger
     * error or emphasis overlays to be painted correctly.
     * This also influences the initial positioning of the nodes
     * (at creation time).
     */
    public static final int EXTRA_BORDER_SPACE = 6;

    /** Node radius for nodified edges. */
    static final public double NODE_EDGE_RADIUS = 3;

    /** The height of the adornment text box. */
    public static final int ADORNMENT_HEIGHT = 12;
    /** The font used for adornment text. */
    public static final Font ADORNMENT_FONT = DEFAULT_FONT;
    /** Constant defining an italic font, for displaying state identities. */
    static public final Font ITALIC_FONT = DEFAULT_FONT.deriveFont(Font.ITALIC);
    /** Percentage of white in the background colour. */
    static private final int BACKGROUND_WHITEWASH = 90;

    /** Foreground (= border) colour of the rubber band selector. */
    static public final Color RUBBER_FOREGROUND = new Color(150, 150, 150);
    /** Foreground (= border) colour of the rubber band selector. */
    static public final Color RUBBER_BACKGROUND = new Color(100, 212, 224, 40);

    /** Maximum value of the colour dimensions. */
    static private final int MAX_VALUE = 255;

    /** Background colour used for selected items in focused lists. */
    static public final Color FOCUS_BACKGROUND = Color.DARK_GRAY;
    /** Text colour used for selected items in focused lists. */
    static public final Color FOCUS_FOREGROUND = Color.WHITE;
    /** Background colour used for selected items in non-focused lists. */
    static public final Color SELECT_BACKGROUND = Color.LIGHT_GRAY;
    /** Text colour used for selected items in non-focused lists. */
    static public final Color SELECT_FOREGROUND = Color.BLACK;
    /** Background colour used for non-selected items in lists. */
    static public final Color NORMAL_BACKGROUND = Color.WHITE;
    /** Text colour used for non-selected items in lists. */
    static public final Color NORMAL_FOREGROUND = Color.BLACK;
    /** Colour used for indicating errors in graphs. */
    static public final Color ERROR_COLOR = new Color(MAX_VALUE, 50, 0, 40);
    /** Background colour used for focused error items in lists. */
    static public final Color ERROR_FOCUS_BACKGROUND =
        Color.RED.darker().darker();
    /** Text colour used for focused error items in lists. */
    static public final Color ERROR_FOCUS_FOREGROUND = Color.WHITE;
    /** Background colour used for selected, non-focused error items in lists. */
    static public final Color ERROR_SELECT_BACKGROUND = ERROR_COLOR;
    /** Text colour used for selected, non-focused error items in lists. */
    static public final Color ERROR_SELECT_FOREGROUND = Color.RED;
    /** Background colour used for non-selected, non-focused error items in lists. */
    static public final Color ERROR_NORMAL_BACKGROUND = Color.WHITE;
    /** Text colour used for non-selected, non-focused error items in lists. */
    static public final Color ERROR_NORMAL_FOREGROUND = Color.RED;

    /**
     * Returns the foreground colour to be used for list items, under
     * certain conditions.
     * @param selected indicates if the item is currently selected
     * @param focused indicates if the list the item appears in is currently
     * focused. (This is <i>not</i> the same as cell focus in a list.)
     * @param error indicates if the cell is associated with an object with
     * (syntax) errors
     * @see #getBackground
     */
    static public Color getForeground(boolean selected, boolean focused,
            boolean error) {
        if (error) {
            if (focused) {
                return ERROR_FOCUS_FOREGROUND;
            } else if (selected) {
                return ERROR_SELECT_FOREGROUND;
            } else {
                return ERROR_NORMAL_FOREGROUND;
            }
        } else {
            if (focused) {
                return FOCUS_FOREGROUND;
            } else if (selected) {
                return SELECT_FOREGROUND;
            } else {
                return NORMAL_FOREGROUND;
            }
        }
    }

    /**
     * Returns the foreground colour to be used for list items, under
     * certain conditions.
     * @param selected indicates if the item is currently selected
     * @param focused indicates if the list the item appears in is currently
     * focused. (This is <i>not</i> the same as cell focus in a list.)
     * @param error indicates if the cell is associated with an object with
     * (syntax) errors
     * @see #getForeground
     */
    static public Color getBackground(boolean selected, boolean focused,
            boolean error) {
        if (error) {
            if (focused) {
                return ERROR_FOCUS_BACKGROUND;
            } else if (selected) {
                return ERROR_SELECT_BACKGROUND;
            } else {
                return ERROR_NORMAL_BACKGROUND;
            }
        } else {
            if (focused) {
                return FOCUS_BACKGROUND;
            } else if (selected) {
                return SELECT_BACKGROUND;
            } else {
                return NORMAL_BACKGROUND;
            }
        }
    }

    /** Line width used for emphasised cells. */
    public static final int EMPH_WIDTH = 3;
    /** Difference in line width between emphasised and non-emphasised. */
    public static final int EMPH_INCREMENT = EMPH_WIDTH - DEFAULT_LINE_WIDTH;

    /**
     * Dash pattern specifying "no dash"
     */
    static private final float[] NO_DASH = new float[] {10.f, 0.f};

    /**
     * Dash pattern used for nesting elements.
     */
    static public final float[] NESTED_DASH = new float[] {2.0f, 3.0f};
    /**
     * Border used for nesting elements.
     */
    static public final Border NESTED_BORDER = createNodeBorder(
        new StrokedLineBorder(DEFAULT_CELL_COLOR, createStroke(
            DEFAULT_LINE_WIDTH, NESTED_DASH)), false);
    /** 
     * Static flag determining if gradient background paint should be used.
     * Gradient paint looks better, but there is a performance hit. 
     */
    static final private boolean GRADIENT_PAINT = false;

    /** Creates a stroke with a given line width and dash pattern. */
    public static Stroke createStroke(float width, float[] dash) {
        Stroke result;
        if (dash == null) {
            result =
                new BasicStroke(width, BasicStroke.CAP_SQUARE,
                    BasicStroke.JOIN_MITER);
        } else {
            result =
                new BasicStroke(width, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 10.0f, dash, 1.0f);
        }
        return result;
    }

    /** Creates a rule border with given colour, dash pattern, and width. */
    public static Border createRuleBorder(Color color, float width,
            float[] dash, boolean emph) {
        Border result;
        if (dash == NO_DASH) {
            result = new LineBorder(color, (int) width);
        } else {
            result = new StrokedLineBorder(color, createStroke(width, dash));
        }
        return createNodeBorder(result, emph);
    }

    /**
     * Creates paint for a vertex with given bounds and (inner) colour.
     */
    static public Paint createPaint(Rectangle b, Color c) {
        // only bother with special paint if the vertex is not too small to notice
        if (!GRADIENT_PAINT || b.width < 10 && b.height < 10) {
            return c;
        } else {
            int cx = b.x + b.width / 2;
            int cy = b.y + b.height / 2;
            int fx = b.x + b.width / 3;
            int fy = b.y + 2 * b.height / 3;
            int rx = b.width - fx;
            int ry = b.height - fy;
            float r = (float) Math.sqrt(rx * rx + ry * ry);
            Paint newPaint =
                new RadialGradientPaint(cx, cy, r, fx, fy,
                    new float[] {0f, 1f}, getGradient(c), CycleMethod.NO_CYCLE);
            return newPaint;
        }
    }

    /** Lazily creates and returns the colour gradient derived from a given colour. */
    static private Color[] getGradient(Color c) {
        Color[] result = gradientMap.get(c);
        if (result == null) {
            float factor = .9f;
            Color inC =
                new Color((int) Math.min(c.getRed() / factor, 255),
                    (int) Math.min(c.getGreen() / factor, 255), (int) Math.min(
                        c.getBlue() / factor, 255), c.getAlpha());
            Color outC =
                new Color((int) (c.getRed() * factor),
                    (int) (c.getGreen() * factor),
                    (int) (c.getBlue() * factor), c.getAlpha());
            gradientMap.put(c, result = new Color[] {inC, outC});
        }
        return result;
    }

    /** Mapping from colours to colour gradients for {@link #createPaint(Rectangle, Color)}. */
    static private Map<Color,Color[]> gradientMap =
        new HashMap<Color,Color[]>();

    /** Tests if a given code is a recognised line style. */
    public static boolean isLineStyle(int style) {
        return style >= GraphConstants.STYLE_ORTHOGONAL
            && style <= STYLE_MANHATTAN;
    }

    /**
     * Tests if a set of attributes specifies an effective Manhattan line style.
     * The Manhattan line style is effective if the edge has more than two
     * points.
     */
    public static boolean isManhattanStyle(
            org.jgraph.graph.AttributeMap attributes) {
        if (GraphConstants.getLineStyle(attributes) == STYLE_MANHATTAN) {
            List<?> points = GraphConstants.getPoints(attributes);
            return points != null && points.size() > 2;
        } else {
            return false;
        }
    }

    /**
     * Converts a colour dimension to a value that is whitewashed by
     * {@link #BACKGROUND_WHITEWASH} degrees.
     */
    static private int whitewash(int value) {
        int distance = MAX_VALUE - value;
        return value + (distance * BACKGROUND_WHITEWASH / 100);
    }

    /**
     * Converts a colour dimension to a value that is whitewashed by
     * {@link #BACKGROUND_WHITEWASH} degrees.
     */
    static public Color whitewash(Color color) {
        int red = whitewash(color.getRed());
        int green = whitewash(color.getGreen());
        int blue = whitewash(color.getBlue());
        int alpha = whitewash(color.getAlpha());
        return new Color(red, green, blue, alpha);
    }

    /**
     * Creates a new border from a given border, by inserting space to the left
     * and right.
     */
    private static Border createNodeBorder(Border border, boolean emph) {
        return new CompoundBorder(border, emph ? EMPH_INSET_BORDER
                : EMPTY_INSET_BORDER);
    }

    /** Specialised class to avoid casting for {@link #clone()}. */
    static public class AttributeMap extends org.jgraph.graph.AttributeMap {
        /** Constructor for an empty map. */
        public AttributeMap() {
            // empty constructor
        }

        /** Constructor for a copy of an existing map. */
        public AttributeMap(org.jgraph.graph.AttributeMap map) {
            super(map);
        }

        @Override
        public AttributeMap clone() {
            return (AttributeMap) super.clone();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object put(Object key, Object value) {
            return super.put(key, value);
        }

        /** 
         * Retains only the values of this attribute map that
         * differ from a given map.
         */
        public AttributeMap diff(AttributeMap other) {
            List<Object> removedKeys = new ArrayList<Object>();
            for (Map.Entry<?,?> entry : ((Map<?,?>) this).entrySet()) {
                if (entry.getValue().equals(other.get(entry.getKey()))) {
                    removedKeys.add(entry.getKey());
                }
            }
            for (Object key : removedKeys) {
                remove(key);
            }
            return this;
        }
    }
}
