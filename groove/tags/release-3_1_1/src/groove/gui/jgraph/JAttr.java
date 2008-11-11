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

import static groove.view.aspect.RuleAspect.EMBARGO;
import groove.util.Colors;
import groove.util.Groove;
import groove.view.aspect.AspectValue;
import groove.view.aspect.RuleAspect;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Stroke;
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

    /**
     * The linewidth used for edges and node borders.
     */
    public static final int DEFAULT_LINE_WIDTH =
        Integer.parseInt(Groove.getGUIProperty("default.width"));

    /**
     * The colour used for edges and node.
     */
    public static final Color DEFAULT_CELL_COLOR =
        Colors.findColor(Groove.getGUIProperty("default.color"));

    /**
     * The default background colour used for edges and node.
     */
    public static final Color DEFAULT_BACKGROUND =
        Colors.findColor(Groove.getGUIProperty("default.background"));

    /**
     * Background colour for data nodes; is <code>null</code> if no special
     * background is set.
     */
    public static final Color DATA_BACKGROUND;
    /**
     * Font for data nodes and edges; is <code>null</code> if no special font
     * is set.
     */
    public static final Font DATA_FONT = DEFAULT_FONT;

    static {
        String valueBackgroundProperty =
            Groove.getGUIProperty("attribute.background");
        if (valueBackgroundProperty == null) {
            DATA_BACKGROUND = null;
        } else {
            DATA_BACKGROUND = Colors.findColor(valueBackgroundProperty);
        }
    }

    /**
     * Border insets for default nodes.
     */
    public static final Insets DEFAULT_INSETS = new Insets(0, 3, 0, 3);
    /**
     * An empty border, to be used as the inner border of a compound border,
     * which inserts some space to the left and right of the label text.
     */
    public static final Border EMPTY_INSET_BORDER =
        new EmptyBorder(DEFAULT_INSETS);
    /**
     * The border used for nodes.
     */
    public static final Border DEFAULT_BORDER =
        JAttr.createNodeBorder(new LineBorder(DEFAULT_CELL_COLOR,
            DEFAULT_LINE_WIDTH), false);

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
    static public final float[] NO_DASH = {10f, 0f};

    /** The color used for grayed-out cells. */
    static public final Color GRAYED_OUT_COLOR =
        Colors.findColor(Groove.getGUIProperty("invisible.color"));
    /** The border used for grayed-out cells. */
    static public final Border GRAYED_OUT_BORDER =
        JAttr.createNodeBorder(new LineBorder(JAttr.GRAYED_OUT_COLOR,
            JAttr.DEFAULT_LINE_WIDTH), false);

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

    static {
        // graying out
        AttributeMap grayedOutAttr = new AttributeMap();
        GraphConstants.setLineColor(grayedOutAttr, JAttr.GRAYED_OUT_COLOR);
        GraphConstants.setForeground(grayedOutAttr, JAttr.GRAYED_OUT_COLOR);
        GraphConstants.setBorder(grayedOutAttr, JAttr.GRAYED_OUT_BORDER);
        GraphConstants.setOpaque(grayedOutAttr, false);
        GRAYED_OUT_ATTR = grayedOutAttr;

        // set default node attributes
        // GraphConstants.setBorderColor(NODE_ATTR, CELL_COLOR);
        AttributeMap defaultEdgeAttr = new AttributeMap();
        GraphConstants.setEditable(defaultEdgeAttr, false);
        GraphConstants.setSelectable(defaultEdgeAttr, true);
        GraphConstants.setMoveable(defaultEdgeAttr, true);
        GraphConstants.setForeground(defaultEdgeAttr, JAttr.DEFAULT_CELL_COLOR);
        GraphConstants.setLineColor(defaultEdgeAttr, JAttr.DEFAULT_CELL_COLOR);
        GraphConstants.setLineWidth(defaultEdgeAttr, JAttr.DEFAULT_LINE_WIDTH);
        GraphConstants.setDashPattern(defaultEdgeAttr, JAttr.NO_DASH);
        GraphConstants.setLineEnd(defaultEdgeAttr, GraphConstants.ARROW_CLASSIC);
        GraphConstants.setEndFill(defaultEdgeAttr, true);
        GraphConstants.setBeginFill(defaultEdgeAttr, true);
        GraphConstants.setBendable(defaultEdgeAttr, true);
        GraphConstants.setBackground(defaultEdgeAttr, Color.WHITE);
        GraphConstants.setOpaque(defaultEdgeAttr, true);
        GraphConstants.setConnectable(defaultEdgeAttr, true);
        GraphConstants.setDisconnectable(defaultEdgeAttr, true);
        // no routing installed, because this is implemented
        // to disable manual changes
        GraphConstants.setRouting(defaultEdgeAttr,
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
        DEFAULT_EDGE_ATTR = defaultEdgeAttr;

        // set default node attributes
        AttributeMap defaultNodeAttr = defaultEdgeAttr.clone();
        GraphConstants.setAutoSize(defaultNodeAttr, true);
        // GraphConstants.setBounds(defaultNodeAttr, DEFAULT_NODE_BOUNDS);
        GraphConstants.setSizeable(defaultNodeAttr, false);
        GraphConstants.setBorder(defaultNodeAttr, DEFAULT_BORDER);
        GraphConstants.setBackground(defaultNodeAttr, DEFAULT_BACKGROUND);
        DEFAULT_NODE_ATTR = defaultNodeAttr;
    }

    /** Line width used for emphasized cells. */
    public static final int EMPH_WIDTH =
        Integer.parseInt(Groove.getGUIProperty("state.emphasis.width"));
    /** Difference in line width between emphasized and non-emphasized. */
    public static final int EMPH_INCREMENT = EMPH_WIDTH - DEFAULT_LINE_WIDTH;
    /**
     * Border insets for emphasised nodes.
     */
    public static final Insets EMPH_INSETS = new Insets(-2, 1, -2, 1);

    /**
     * An empty border, to be used as the inner border of a compound border,
     * which inserts some space to the left and right of the label text.
     */
    public static final Border EMPH_INSET_BORDER = new EmptyBorder(EMPH_INSETS);

    /** Colour used for emphasised cells. */
    public static final Color EMPH_COLOR =
        Colors.findColor(Groove.getGUIProperty("state.emphasis.color"));
    /** Node border used for emphasised cells. */
    public static final Border EMPH_BORDER =
        createNodeBorder(new LineBorder(JAttr.EMPH_COLOR, JAttr.EMPH_WIDTH) {
            // @Override
            // public Insets getBorderInsets(Component c) {
            // return new Insets(0,0,0,0);
            // }
        }, true);

    /** The emphasis attribute changes for graph edges. */
    public static final AttributeMap EMPH_EDGE_CHANGE = new AttributeMap();
    /** The emphasis attribute changes for graph nodes. */
    public static final AttributeMap EMPH_NODE_CHANGE = new AttributeMap();

    // set the emphasis attributes
    static {
        // edges
        GraphConstants.setLineWidth(EMPH_EDGE_CHANGE, EMPH_WIDTH);
        // nodes
        // GraphConstants.setLineWidth(EMPH_NODE_CHANGE, EMPH_WIDTH);
        GraphConstants.setBorder(EMPH_NODE_CHANGE, EMPH_BORDER);
    }
    //
    // /**
    // * Line width of ordinary inactive LTS cells.
    // */
    // static private final int LTS_NORM_WIDTH =
    // Integer.parseInt(Groove.getGUIProperty("default.width"));

    /**
     * Line width of active LTS cells.
     */
    static private final int LTS_ACTIVE_WIDTH =
        Integer.parseInt(Groove.getGUIProperty("lts.emphasis.width"));
    //
    // /**
    // * Foreground colour of ordinary inactive LTS cells.
    // */
    // static private final Color LTS_NORM_COLOR =
    // Colors.findColor(Groove.getGUIProperty("default.color"));

    /**
     * Colour of active LTS cells.
     */
    static private final Color LTS_ACTIVE_COLOR =
        Colors.findColor(Groove.getGUIProperty("lts.emphasis.color"));
    //
    // /**
    // * Background colour of ordinary (non-emphasised) LTS cells.
    // */
    // static private final Color LTS_NORM_BACKGROUND =
    // Colors.findColor(Groove.getGUIProperty("default.background"));

    /**
     * Background colour of LTS start cells.
     */
    static private final Color LTS_START_BACKGROUND =
        Colors.findColor(Groove.getGUIProperty("lts.start.background"));

    /**
     * Background colour of unexplored LTS cells.
     */
    static private final Color LTS_OPEN_BACKGROUND =
        Colors.findColor(Groove.getGUIProperty("lts.open.background"));

    /**
     * Background colour of final LTS cells.
     */
    static private final Color LTS_FINAL_BACKGROUND =
        Colors.findColor(Groove.getGUIProperty("lts.final.background"));
    //
    // /**
    // * Borders of ordinary (non-active) LTS nodes.
    // */
    // static private final Border LTS_NORM_BORDER = createNodeBorder(new
    // LineBorder(LTS_NORM_COLOR, LTS_NORM_WIDTH), false);

    /**
     * Borders of active LTS nodes.
     */
    static private final Border LTS_ACTIVE_BORDER =
        createNodeBorder(new LineBorder(LTS_ACTIVE_COLOR, LTS_ACTIVE_WIDTH),
            false);

    /**
     * Emphasised borders of active LTS nodes.
     */
    static public final Border LTS_ACTIVE_EMPH_BORDER =
        createNodeBorder(new LineBorder(LTS_ACTIVE_COLOR, LTS_ACTIVE_WIDTH
            + EMPH_INCREMENT), true);
    /** The default node attributes of the LTS */
    static public final AttributeMap LTS_NODE_ATTR;
    /** The start node attributes of the LTS */
    static public final AttributeMap LTS_START_NODE_ATTR;
    /** Unexplored node attributes */
    static public final AttributeMap LTS_OPEN_NODE_ATTR;
    /** Final node attributes */
    static public final AttributeMap LTS_FINAL_NODE_ATTR;
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
        // active LTS nodes
        LTS_NODE_ACTIVE_CHANGE = new AttributeMap();
        GraphConstants.setBorder(LTS_NODE_ACTIVE_CHANGE,
            JAttr.LTS_ACTIVE_BORDER);
        GraphConstants.setLineColor(LTS_NODE_ACTIVE_CHANGE,
            JAttr.LTS_ACTIVE_COLOR);
        GraphConstants.setLineWidth(LTS_NODE_ACTIVE_CHANGE,
            JAttr.LTS_ACTIVE_WIDTH);
        // active LTS edges
        LTS_EDGE_ACTIVE_CHANGE = new AttributeMap();
        GraphConstants.setForeground(LTS_EDGE_ACTIVE_CHANGE,
            JAttr.LTS_ACTIVE_COLOR);
        GraphConstants.setLineColor(LTS_EDGE_ACTIVE_CHANGE,
            JAttr.LTS_ACTIVE_COLOR);
        GraphConstants.setLineWidth(LTS_EDGE_ACTIVE_CHANGE,
            JAttr.LTS_ACTIVE_WIDTH);

        // LTS nodes
        LTS_NODE_ATTR = DEFAULT_NODE_ATTR.clone();
        // GraphConstants.setFont(LTS_NODE_ATTR, italicFont);
        // LTS start node
        LTS_START_NODE_ATTR = LTS_NODE_ATTR.clone();
        GraphConstants.setBackground(LTS_START_NODE_ATTR,
            JAttr.LTS_START_BACKGROUND);
        // LTS unexplored nodes
        LTS_OPEN_NODE_ATTR = LTS_NODE_ATTR.clone();
        GraphConstants.setBackground(LTS_OPEN_NODE_ATTR,
            JAttr.LTS_OPEN_BACKGROUND);
        // LTS final nodes
        LTS_FINAL_NODE_ATTR = LTS_NODE_ATTR.clone();
        GraphConstants.setBackground(LTS_FINAL_NODE_ATTR,
            JAttr.LTS_FINAL_BACKGROUND);
        LTS_ACTIVE_EMPH_NODE_CHANGE = new AttributeMap();
        GraphConstants.setBorder(LTS_ACTIVE_EMPH_NODE_CHANGE,
            JAttr.LTS_ACTIVE_EMPH_BORDER);
        GraphConstants.setLineWidth(LTS_ACTIVE_EMPH_NODE_CHANGE,
            JAttr.LTS_ACTIVE_WIDTH);
        // LTS edges
        LTS_EDGE_ATTR = DEFAULT_EDGE_ATTR.clone();
        GraphConstants.setConnectable(LTS_EDGE_ATTR, false);
        GraphConstants.setDisconnectable(LTS_EDGE_ATTR, false);
        GraphConstants.setLineEnd(LTS_EDGE_ATTR, GraphConstants.ARROW_SIMPLE);
    }

    /** Border consisting of a double line. */
    static public final Border DOUBLE_BORDER =
        new CompoundBorder(new LineBorder(Color.BLUE, 2), new LineBorder(
            Color.BLUE, 2));

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
        CONTROL_NODE_ATTR = DEFAULT_NODE_ATTR.clone();

        CONTROL_START_NODE_ATTR = CONTROL_NODE_ATTR.clone();
        GraphConstants.setBackground(CONTROL_START_NODE_ATTR, Color.GREEN);

        CONTROL_SUCCESS_NODE_ATTR = CONTROL_NODE_ATTR.clone();
        GraphConstants.setBorder(CONTROL_SUCCESS_NODE_ATTR,
            LTS_ACTIVE_EMPH_BORDER);
        GraphConstants.setBorderColor(CONTROL_SUCCESS_NODE_ATTR, Color.RED);
        GraphConstants.setBackground(CONTROL_SUCCESS_NODE_ATTR, Color.RED);

        CONTROL_EDGE_ATTR = DEFAULT_EDGE_ATTR.clone();

        GraphConstants.setConnectable(CONTROL_EDGE_ATTR, false);
        GraphConstants.setDisconnectable(CONTROL_EDGE_ATTR, false);

        CONTROL_LAMBDA_EDGE_ATTR = CONTROL_EDGE_ATTR.clone();
        CONTROL_FAILURE_EDGE_ATTR = CONTROL_EDGE_ATTR.clone();
        CONTROL_SHAPE_EDGE_ATTR = CONTROL_EDGE_ATTR.clone();

        GraphConstants.setFont(CONTROL_SHAPE_EDGE_ATTR, ITALIC_FONT);

        GraphConstants.setLineEnd(CONTROL_EDGE_ATTR,
            GraphConstants.ARROW_CLASSIC);
        GraphConstants.setLineColor(CONTROL_LAMBDA_EDGE_ATTR, Color.GREEN);
        GraphConstants.setLineColor(CONTROL_FAILURE_EDGE_ATTR, Color.RED);
        GraphConstants.setLineColor(CONTROL_SHAPE_EDGE_ATTR, Color.GRAY);
    }

    /**
     * Store of colours for each role.
     */
    static private final Map<AspectValue,Color> RULE_COLOR =
        new HashMap<AspectValue,Color>();

    /**
     * Store of line widths for each role.
     */
    static private final Map<AspectValue,Integer> RULE_WIDTH =
        new HashMap<AspectValue,Integer>();

    /**
     * Store of line widths for each role.
     */
    static private final Map<AspectValue,Color> RULE_BACKGROUND =
        new HashMap<AspectValue,Color>();

    /**
     * Store of dash patterns for each role.
     */
    static private final Map<AspectValue,float[]> RULE_DASH =
        new HashMap<AspectValue,float[]>();

    /**
     * Store of borders for each role.
     */
    static private final Map<AspectValue,Border> RULE_BORDER =
        new HashMap<AspectValue,Border>();

    /**
     * Store of emphasised line widths for each role.
     */
    static private final Map<AspectValue,Integer> RULE_EMPH_WIDTH =
        new HashMap<AspectValue,Integer>();

    /**
     * Store of emphasised borders for each role.
     */
    static private final Map<AspectValue,Border> RULE_EMPH_BORDER =
        new HashMap<AspectValue,Border>();

    /**
     * Dash pattern used for nesting elements.
     */
    static private final float[] NESTED_DASH = new float[] {2.0f, 3.0f};
    /**
     * Border used for nesting elements.
     */
    static private final Border NESTED_BORDER =
        createNodeBorder(new StrokedLineBorder(DEFAULT_CELL_COLOR,
            createStroke(DEFAULT_LINE_WIDTH, NESTED_DASH)), false);
    /**
     * Arrow used for nesting edges.
     */
    static private final int NESTED_ARROW = GraphConstants.ARROW_SIMPLE;
    /**
     * Arrow size used for nesting edges.
     */
    static private final int NESTED_ARROW_SIZE =
        GraphConstants.DEFAULTDECORATIONSIZE - 2;
    /**
     * Background colour for nesting nodes.
     */
    static private final Color NESTED_BACKGROUND = Color.WHITE;

    static {
        Map<AspectValue,String> RULE_PREFIXES =
            new HashMap<AspectValue,String>();
        RULE_PREFIXES.put(RuleAspect.READER, "default.");
        RULE_PREFIXES.put(RuleAspect.EMBARGO, "embargo.");
        RULE_PREFIXES.put(RuleAspect.ERASER, "eraser.");
        RULE_PREFIXES.put(RuleAspect.CREATOR, "creator.");
        RULE_PREFIXES.put(RuleAspect.REMARK, "remark.");
        // RULE_PREFIXES.put(RuleAspect.RULE, "rule.");
        for (AspectValue role : RuleAspect.getInstance().getValues()) {
            RULE_COLOR.put(role,
                Colors.findColor(Groove.getGUIProperty(RULE_PREFIXES.get(role)
                    + "color")));
            RULE_WIDTH.put(role,
                Integer.parseInt(Groove.getGUIProperty(RULE_PREFIXES.get(role)
                    + "width")));
            float[] dash =
                Groove.toFloatArray(Groove.getGUIProperty(RULE_PREFIXES.get(role)
                    + "dash"));
            String background =
                Groove.getGUIProperty(RULE_PREFIXES.get(role) + "background");
            if (background != null) {
                RULE_BACKGROUND.put(role, Colors.findColor(background));
            } else {
                RULE_BACKGROUND.put(role, whitewash(RULE_COLOR.get(role)));
            }
            RULE_DASH.put(role, dash == null ? JAttr.NO_DASH : dash);
            RULE_BORDER.put(role, createNodeBorder(
                createRuleBorder(RULE_COLOR.get(role), RULE_WIDTH.get(role),
                    RULE_DASH.get(role)), false));
            RULE_EMPH_WIDTH.put(role, RULE_WIDTH.get(role) + EMPH_INCREMENT);
            RULE_EMPH_BORDER.put(role, createNodeBorder(createRuleBorder(
                RULE_COLOR.get(role), RULE_EMPH_WIDTH.get(role),
                RULE_DASH.get(role)), true));
        }
    }

    /** Collection of attributes for rule nodes. */
    static public final Map<AspectValue,AttributeMap> RULE_NODE_ATTR =
        new HashMap<AspectValue,AttributeMap>();
    /** Collection of attribute changes for emphasised rule nodes. */
    static public final Map<AspectValue,AttributeMap> RULE_NODE_EMPH_CHANGE =
        new HashMap<AspectValue,AttributeMap>();
    /** Collection of attributes for rule edges. */
    static public final Map<AspectValue,AttributeMap> RULE_EDGE_ATTR =
        new HashMap<AspectValue,AttributeMap>();
    /** Collection of attribute changes for emphasised rule edges. */
    static public final Map<AspectValue,AttributeMap> RULE_EDGE_EMPH_CHANGE =
        new HashMap<AspectValue,AttributeMap>();
    /** Collection of attributes for nesting nodes. */
    static public final AttributeMap NESTING_NODE_ATTR;
    /** Collection of attributes for nesting edges. */
    static public final AttributeMap NESTING_EDGE_ATTR;
    static {
        for (AspectValue role : RuleAspect.getInstance().getValues()) {
            // edge attributes
            AttributeMap edgeAttr = JAttr.DEFAULT_EDGE_ATTR.clone();
            GraphConstants.setEditable(edgeAttr, false);
            GraphConstants.setForeground(edgeAttr, RULE_COLOR.get(role));
            GraphConstants.setLineColor(edgeAttr, RULE_COLOR.get(role));
            GraphConstants.setLineWidth(edgeAttr, RULE_WIDTH.get(role));
            GraphConstants.setDashPattern(edgeAttr, RULE_DASH.get(role));
            GraphConstants.setLineEnd(edgeAttr, GraphConstants.ARROW_CLASSIC);
            GraphConstants.setEndFill(edgeAttr, role != EMBARGO);
            GraphConstants.setBeginFill(edgeAttr, true);
            GraphConstants.setBendable(edgeAttr, true);
            GraphConstants.setBackground(edgeAttr, Color.WHITE);
            GraphConstants.setOpaque(edgeAttr, true);
            GraphConstants.setConnectable(edgeAttr, false);
            GraphConstants.setDisconnectable(edgeAttr, false);
            RULE_EDGE_ATTR.put(role, edgeAttr);

            // set default node attributes
            AttributeMap nodeAttr = JAttr.DEFAULT_NODE_ATTR.clone();
            nodeAttr.applyMap(edgeAttr);
            GraphConstants.setBorderColor(nodeAttr, RULE_COLOR.get(role));
            GraphConstants.setAutoSize(nodeAttr, true);
            GraphConstants.setSizeable(nodeAttr, false);
            GraphConstants.setBorder(nodeAttr, RULE_BORDER.get(role));
            GraphConstants.setLineWidth(nodeAttr, RULE_WIDTH.get(role));
            Color background = RULE_BACKGROUND.get(role);
            if (background != null) {
                GraphConstants.setBackground(nodeAttr, background);
            }
            RULE_NODE_ATTR.put(role, nodeAttr);

            // edge emphasis
            AttributeMap edgeEmphChange = JAttr.EMPH_EDGE_CHANGE.clone();
            GraphConstants.setLineWidth(edgeEmphChange,
                JAttr.RULE_EMPH_WIDTH.get(role));
            RULE_EDGE_EMPH_CHANGE.put(role, edgeEmphChange);

            // node emphasis
            AttributeMap nodeEmphChange = JAttr.EMPH_NODE_CHANGE.clone();
            GraphConstants.setBorder(nodeEmphChange,
                JAttr.RULE_EMPH_BORDER.get(role));
            RULE_NODE_EMPH_CHANGE.put(role, nodeEmphChange);
        }
        NESTING_NODE_ATTR = JAttr.DEFAULT_NODE_ATTR.clone();
        GraphConstants.setBorder(NESTING_NODE_ATTR, JAttr.NESTED_BORDER);
        GraphConstants.setDashPattern(NESTING_NODE_ATTR, JAttr.NESTED_DASH);
        GraphConstants.setBackground(NESTING_NODE_ATTR, NESTED_BACKGROUND);
        NESTING_EDGE_ATTR = JAttr.DEFAULT_EDGE_ATTR.clone();
        GraphConstants.setDashPattern(NESTING_EDGE_ATTR, JAttr.NESTED_DASH);
        GraphConstants.setLineEnd(NESTING_EDGE_ATTR, JAttr.NESTED_ARROW);
        GraphConstants.setEndSize(NESTING_EDGE_ATTR, JAttr.NESTED_ARROW_SIZE);
    }

    /** Specialised class to avoid casting for {@link #clone()}. */
    static public class AttributeMap extends org.jgraph.graph.AttributeMap {
        @Override
        public AttributeMap clone() {
            return (AttributeMap) super.clone();
        }
    }
}
