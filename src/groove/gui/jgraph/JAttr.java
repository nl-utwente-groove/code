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

import static groove.view.aspect.AspectKind.ADDER;
import static groove.view.aspect.AspectKind.CREATOR;
import groove.util.Colors;
import groove.util.Groove;
import groove.view.aspect.AspectKind;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.EnumMap;
import java.util.EnumSet;
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
    public static Border createRuleBorder(Color color, float width, float[] dash) {
        Border result;
        if (dash == NO_DASH) {
            result = new LineBorder(color, (int) width);
        } else {
            result = new StrokedLineBorder(color, createStroke(width, dash));
        }
        return result;
    }

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
    static private Color whitewash(Color color) {
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

    /** Line style that always makes right edges. */
    public static final int STYLE_MANHATTAN = 14;

    /**
     * The default line style.
     */
    public static final int DEFAULT_LINE_STYLE =
        GraphConstants.STYLE_ORTHOGONAL;

    /** The default font used in the j-graphs. */
    static public final Font DEFAULT_FONT = GraphConstants.DEFAULTFONT;
    /** Constant defining an italic font, for displaying state identities. */
    static public final Font ITALIC_FONT = DEFAULT_FONT.deriveFont(Font.ITALIC);
    /** Percentage of white in the background colour. */
    static private final int BACKGROUND_WHITEWASH = 90;
    /** Maximum value of the colour dimensions. */
    static private final int MAX_VALUE = 255;
    /** Colour used for indicating errors in the graph. */
    static public final Color ERROR_COLOR = new Color(MAX_VALUE, 50, 0, 40);

    /**
     * The line width used for edges and node borders.
     */
    public static final int DEFAULT_LINE_WIDTH = 1;
    /** Line width used for emphasised cells. */
    public static final int EMPH_WIDTH = 3;
    /** Difference in line width between emphasised and non-emphasised. */
    public static final int EMPH_INCREMENT = EMPH_WIDTH - DEFAULT_LINE_WIDTH;

    /**
     * Border insets for emphasised nodes.
     */
    private static final Insets EMPH_INSETS = new Insets(-2, 1, -2, 1);

    /**
     * The colour used for edges and node.
     */
    public static final Color DEFAULT_CELL_COLOR = Color.black;

    /**
     * The default background colour used for nodes.
     */
    public static final Color DEFAULT_BACKGROUND =
        Colors.findColor("245 245 245");

    /**
     * Font for data nodes and edges; is <code>null</code> if no special font is
     * set.
     */
    public static final Font DATA_FONT = DEFAULT_FONT;

    /**
     * Border insets for default nodes.
     */
    public static final Insets DEFAULT_INSETS = new Insets(0, 3, 0, 3);
    /**
     * An empty border, to be used as the inner border of a compound border,
     * which inserts some space to the left and right of the label text.
     */
    public static final Border EMPTY_INSET_BORDER = new EmptyBorder(
        DEFAULT_INSETS);
    /**
     * The border used for nodes.
     */
    public static final Border DEFAULT_BORDER = JAttr.createNodeBorder(
        new LineBorder(DEFAULT_CELL_COLOR, DEFAULT_LINE_WIDTH), false);

    /**
     * The standard bounds used for nodes.
     */
    public static final Rectangle DEFAULT_NODE_BOUNDS;
    /**
     * The standard size used for nodes.
     */
    public static final Dimension DEFAULT_NODE_SIZE;
    static {
        int[] ba =
            Groove.toIntArray(Groove.getGUIProperty("default.nodebounds"));
        assert ba != null && ba.length == 4 : "Format error in default node bounds property";
        DEFAULT_NODE_BOUNDS = new Rectangle(ba[0], ba[1], ba[2], ba[3]);
        DEFAULT_NODE_SIZE = new Dimension(ba[2], ba[3]);
    }

    /**
     * Dash pattern specifying "no dash"
     */
    static private final float[] NO_DASH = {10f, 0f};

    /**
     * The standard jgraph attributes used for graying out nodes and edges.
     */
    static public final AttributeMap GRAYED_OUT_ATTR;

    /**
     * The standard jgraph attributes used for representing nodes.
     */
    public static final AttributeMap DEFAULT_NODE_ATTR;

    /**
     * The standard jgraph attributes used for representing edges.
     */
    public static final AttributeMap DEFAULT_EDGE_ATTR;

    /** The emphasis attribute changes for graph edges. */
    public static final AttributeMap EMPH_EDGE_CHANGE;

    /** The emphasis attribute changes for graph nodes. */
    public static final AttributeMap EMPH_NODE_CHANGE;

    static {
        // graying out
        GRAYED_OUT_ATTR = new Values() {
            {
                this.foreColour = Colors.findColor("200 200 200 100");
                this.opaque = false;
            }
        }.getEdgeAttrs();
        // set default node and edge attributes
        Values defaultValues = new Values();
        DEFAULT_EDGE_ATTR = defaultValues.getEdgeAttrs();
        DEFAULT_NODE_ATTR = defaultValues.getNodeAttrs();
        EMPH_EDGE_CHANGE = defaultValues.getEdgeEmphAttrs();
        EMPH_NODE_CHANGE = defaultValues.getNodeEmphAttrs();
    }

    /**
     * An empty border, to be used as the inner border of a compound border,
     * which inserts some space to the left and right of the label text.
     */
    private static final Border EMPH_INSET_BORDER =
        new EmptyBorder(EMPH_INSETS);

    /** The default node attributes of the LTS */
    static public final AttributeMap LTS_NODE_ATTR;
    /** The start node attributes of the LTS */
    static public final AttributeMap LTS_START_NODE_ATTR;
    /** Unexplored node attributes */
    static public final AttributeMap LTS_OPEN_NODE_ATTR;
    /** Final node attributes */
    static public final AttributeMap LTS_FINAL_NODE_ATTR;
    /** Result node attributes */
    static public final AttributeMap LTS_RESULT_NODE_ATTR;
    /** The default edge attributes of the LTS */
    static public final AttributeMap LTS_EDGE_ATTR;

    /** Active node attributes of the LTS */
    static public final AttributeMap LTS_NODE_ACTIVE_CHANGE;
    /** Active edge attributes of the LTS */
    static public final AttributeMap LTS_EDGE_ACTIVE_CHANGE;
    /** Emphasised active node attributes of the LTS */
    static public final AttributeMap LTS_ACTIVE_EMPH_NODE_CHANGE;

    // set the emphasis attributes
    static {
        // Ordinary LTS nodes and edges
        Values ltsValues = new Values() {
            {
                this.connectable = false;
                this.lineEnd = GraphConstants.ARROW_SIMPLE;
            }
        };
        LTS_NODE_ATTR = ltsValues.getNodeAttrs();
        LTS_EDGE_ATTR = ltsValues.getEdgeAttrs();
        LTS_START_NODE_ATTR = new Values() {
            {
                this.backColour = Color.green;
            }
        }.getNodeAttrs();

        // Special LTS  nodes
        LTS_OPEN_NODE_ATTR = new Values() {
            {
                this.backColour = Color.gray.brighter();
            }
        }.getNodeAttrs();
        LTS_FINAL_NODE_ATTR = new Values() {
            {
                this.backColour = Color.red;
            }
        }.getNodeAttrs();
        LTS_RESULT_NODE_ATTR = new Values() {
            {
                this.backColour = Colors.findColor("255 165 0");
            }
        }.getNodeAttrs();

        // active LTS nodes and edges
        Values ltsActive = new Values() {
            {
                this.lineColour = Color.blue;
                this.linewidth = 3;
            }
        };
        LTS_NODE_ACTIVE_CHANGE = ltsActive.getNodeAttrs();
        LTS_EDGE_ACTIVE_CHANGE = ltsActive.getEdgeAttrs();
        LTS_ACTIVE_EMPH_NODE_CHANGE = ltsActive.getNodeEmphAttrs();
    }

    /** The default node attributes of the control automaton */
    static public final AttributeMap CONTROL_NODE_ATTR;
    /** The start node attributes of the control automaton */
    static public final AttributeMap CONTROL_START_NODE_ATTR;
    /** The sucess node attributes of the control automaton */
    static public final AttributeMap CONTROL_SUCCESS_NODE_ATTR;
    /** The default edge attributes of the control automaton */
    static public final AttributeMap CONTROL_EDGE_ATTR;
    /** The internal lambda edge attributes of the control automaton */
    static public final AttributeMap CONTROL_LAMBDA_EDGE_ATTR;
    /** The internal lambda edge attributes of the control automaton */
    static public final AttributeMap CONTROL_FAILURE_EDGE_ATTR;
    /** The procedure edge attributes of the control automaton automaton */
    static public final AttributeMap CONTROL_SHAPE_EDGE_ATTR;

    static {
        Values ctrlValues = new Values() {
            {
                this.connectable = false;
                this.lineEnd = GraphConstants.ARROW_CLASSIC;
            }
        };
        CONTROL_NODE_ATTR = ctrlValues.getNodeAttrs();
        CONTROL_EDGE_ATTR = ctrlValues.getEdgeAttrs();

        // special nodes
        CONTROL_START_NODE_ATTR = new Values() {
            {
                this.backColour = Color.green;
            }
        }.getNodeAttrs();
        CONTROL_SUCCESS_NODE_ATTR = new Values() {
            {
                this.borderColour = Color.RED;
                this.backColour = Color.RED;
                this.linewidth = 3;
                this.lineColour = Color.BLUE;
            }
        }.getNodeAttrs();

        // special edges
        CONTROL_LAMBDA_EDGE_ATTR = new Values() {
            {
                this.lineColour = Color.GREEN;
            }
        }.getEdgeAttrs();
        CONTROL_FAILURE_EDGE_ATTR = new Values() {
            {
                this.lineColour = Color.RED;
            }
        }.getEdgeAttrs();
        CONTROL_SHAPE_EDGE_ATTR = new Values() {
            {
                this.font = ITALIC_FONT;
                this.lineColour = Color.GRAY;
            }
        }.getEdgeAttrs();
    }

    /**
     * Dash pattern used for nesting elements.
     */
    static private final float[] NESTED_DASH = new float[] {2.0f, 3.0f};
    /**
     * Border used for nesting elements.
     */
    static public final Border NESTED_BORDER = createNodeBorder(
        new StrokedLineBorder(DEFAULT_CELL_COLOR, createStroke(
            DEFAULT_LINE_WIDTH, NESTED_DASH)), false);

    /** Collection of attributes for rule nodes. */
    static public final Map<AspectKind,AttributeMap> RULE_NODE_ATTR =
        new EnumMap<AspectKind,AttributeMap>(AspectKind.class);
    /** Collection of attribute changes for emphasised rule nodes. */
    static public final Map<AspectKind,AttributeMap> RULE_NODE_EMPH_CHANGE =
        new EnumMap<AspectKind,AttributeMap>(AspectKind.class);
    /** Collection of attributes for rule edges. */
    static public final Map<AspectKind,AttributeMap> RULE_EDGE_ATTR =
        new EnumMap<AspectKind,AttributeMap>(AspectKind.class);
    /** Collection of attribute changes for emphasised rule edges. */
    static public final Map<AspectKind,AttributeMap> RULE_EDGE_EMPH_CHANGE =
        new EnumMap<AspectKind,AttributeMap>(AspectKind.class);
    static {
        for (AspectKind aspect : EnumSet.allOf(AspectKind.class)) {
            /** Object to collect the attributes. */
            Values v = new Values(aspect.isRole());
            switch (aspect) {
            case REMARK:
                v.foreColour = Colors.findColor("255 140 0");
                v.backColour = Colors.findColor("255 255 180");
                v.connectable = false;
                break;
            case READER:
                v.connectable = false;
                break;
            case EMBARGO:
                v.foreColour = Color.red;
                v.backColour = null;
                v.linewidth = 5;
                v.dash = new float[] {2, 2};
                v.endFill = false;
                v.connectable = false;
                break;
            case ERASER:
                v.foreColour = Color.blue;
                v.backColour = Colors.findColor("200 240 255");
                v.dash = new float[] {4, 4};
                v.connectable = false;
                break;
            case CREATOR:
                v.foreColour = Color.green.darker();
                v.backColour = null;
                v.linewidth = 3;
                v.connectable = false;
                break;
            case ADDER:
                v.foreColour = Color.green.darker();
                v.backColour = null;
                v.linewidth = 6;
                v.dash = new float[] {2, 2};
                v.endFill = false;
                v.connectable = false;
                break;
            case FORALL:
            case FORALL_POS:
            case EXISTS:
            case NESTED:
                v.dash = NESTED_DASH;
                v.lineEnd = GraphConstants.ARROW_SIMPLE;
                v.endSize = GraphConstants.DEFAULTDECORATIONSIZE - 2;
                v.border = NESTED_BORDER;
                break;
            case SUBTYPE:
                v.lineEnd = GraphConstants.ARROW_TECHNICAL;
                v.endFill = false;
                v.endSize = GraphConstants.DEFAULTDECORATIONSIZE + 5;
                break;
            case ABSTRACT:
                v.dash = new float[] {6.0f, 2.0f};
                v.font = ITALIC_FONT;
                break;
            }

            RULE_NODE_ATTR.put(aspect, v.getNodeAttrs());
            RULE_EDGE_ATTR.put(aspect, v.getEdgeAttrs());
            RULE_EDGE_EMPH_CHANGE.put(aspect, v.getEdgeEmphAttrs());
            RULE_NODE_EMPH_CHANGE.put(aspect, v.getNodeEmphAttrs());
        }
        // special formatting for ADDER
        RULE_NODE_ATTR.get(ADDER).put("line2map", RULE_NODE_ATTR.get(CREATOR));
        RULE_EDGE_ATTR.get(ADDER).put("line2map", RULE_EDGE_ATTR.get(CREATOR));
        RULE_EDGE_EMPH_CHANGE.get(ADDER).put("line2map",
            RULE_EDGE_EMPH_CHANGE.get(CREATOR));
        RULE_NODE_EMPH_CHANGE.get(ADDER).put("line2map",
            RULE_NODE_EMPH_CHANGE.get(CREATOR));
    }

    /** Specialised class to avoid casting for {@link #clone()}. */
    static public class AttributeMap extends org.jgraph.graph.AttributeMap {
        @Override
        public AttributeMap clone() {
            return (AttributeMap) super.clone();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object put(Object key, Object value) {
            return super.put(key, value);
        }
    }

    static class Values {
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
        /** Dash pattern; defaults to no dash. */
        float[] dash = NO_DASH;
        /** Line begin; defaults to {@link GraphConstants#ARROW_NONE}. */
        int lineBegin = GraphConstants.ARROW_NONE;
        /** Line end; defaults to {@link GraphConstants#ARROW_CLASSIC}. */
        int lineEnd = GraphConstants.ARROW_CLASSIC;
        /** Line end size; defaults to {@link GraphConstants#DEFAULTDECORATIONSIZE}. */
        int endSize = GraphConstants.DEFAULTDECORATIONSIZE;
        /** Node border; defaults to a line border of the given colour and width. */
        Border border = null;
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

        Values() {
            this(false);
        }

        Values(boolean isRole) {
            this.isRole = isRole;
        }

        /** Creates the attributes common to nodes and edges. */
        private AttributeMap getAttrs() {
            AttributeMap result = new AttributeMap();
            Color foreground = this.foreColour;
            if (foreground != null) {
                GraphConstants.setForeground(result, foreground);
            }
            Color background =
                this.backColour == null ? whitewash(foreground)
                        : this.backColour;
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
            GraphConstants.setDashPattern(result, this.dash);
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
            GraphConstants.setRouting(result,
                new org.jgraph.graph.Edge.Routing() {
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
            Border border =
                this.border == null ? createBorder(borderColour,
                    this.linewidth, this.dash) : this.border;
            GraphConstants.setBorder(result, createNodeBorder(border, false));
            return result;
        }

        AttributeMap getEdgeAttrs() {
            AttributeMap result = getAttrs().clone();
            GraphConstants.setBackground(result, Color.white);
            return result;
        }

        /** Computes the emphasised node attributes. */
        AttributeMap getNodeEmphAttrs() {
            AttributeMap result = getNodeAttrs().clone();
            float linewidth = GraphConstants.getLineWidth(result);
            GraphConstants.setLineWidth(result, linewidth + EMPH_INCREMENT - 1);
            Color borderColour = GraphConstants.getBorderColor(result);
            Border border =
                this.border == null ? createBorder(borderColour, linewidth,
                    this.dash) : this.border;
            GraphConstants.setBorder(result, createNodeBorder(border, true));
            return result;
        }

        /** Computes the emphasised edge attributes. */
        AttributeMap getEdgeEmphAttrs() {
            AttributeMap result = getEdgeAttrs().clone();
            float linewidth = GraphConstants.getLineWidth(result);
            GraphConstants.setLineWidth(result, linewidth + EMPH_INCREMENT);
            return result;
        }

        private Border createBorder(Color colour, float width, float[] dash) {
            if (this.isRole) {
                return createRuleBorder(colour, width, dash);
            } else {
                return new LineBorder(colour, (int) width);
            }
        }

        private final boolean isRole;
    }
}
