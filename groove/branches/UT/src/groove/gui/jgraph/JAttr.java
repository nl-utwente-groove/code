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
 * $Id: JAttr.java,v 1.1.1.1 2007-03-20 10:05:32 kastenberg Exp $
 */
package groove.gui.jgraph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;

import groove.trans.view.RuleGraph;
import groove.util.Colors;
import groove.util.Groove;

/**
 * Class of constant definitions.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public class JAttr {
    /**
     * The linewidth used for edges and node borders.
     */
    public static final int DEFAULT_LINE_WIDTH = Integer.parseInt(Groove.getGUIProperty("default.width"));

    /**
     * The color used for edges and node.
     */
    public static final Color DEFAULT_CELL_COLOR = Colors.findColor(Groove.getGUIProperty("default.color"));

    /**
     * The default background color used for edges and node.
     */
    public static final Color DEFAULT_BACKGROUND = Colors.findColor(Groove.getGUIProperty("default.background"));

    public static final Insets DEFAULT_INSETS = new Insets(0, 3, 0, 3);
    /**
     * An empty border, to be used as the inner border of a compound border,
     * which inserts some space to the left and right of the label text.
     */
    public static final Border EMPTY_INSET_BORDER = new EmptyBorder(DEFAULT_INSETS);

    /**
     * The border used for nodes.
     */
    public static final Border DEFAULT_BORDER =
        JAttr.createNodeBorder(new LineBorder(DEFAULT_CELL_COLOR, DEFAULT_LINE_WIDTH));

    /**
     * The standard bounds used for nodes.
     */
    public static final Rectangle DEFAULT_NODE_BOUNDS;
    /**
     * The standard size used for nodes.
     */
    public static final Dimension DEFAULT_NODE_SIZE;
    static {
        int[] ba = Groove.toIntArray(Groove.getGUIProperty("default.nodebounds"));
        assert ba != null && ba.length == 4 : "Format error in default node bounds property";
        DEFAULT_NODE_BOUNDS = new Rectangle(ba[0], ba[1], ba[2], ba[3]);
        DEFAULT_NODE_SIZE = new Dimension(ba[2], ba[3]);
    }

    /**
     * Dash pattern specifying "no dash"
     */
    static public final float[] NO_DASH = { 10f, 0f };

    /** Line width of ordinary inactive LTS cells. */
    static public final int LTS_NORM_WIDTH = Integer.parseInt(Groove.getGUIProperty("default.width"));

    /** Line width of active LTS cells. */
    static public final int LTS_ACTIVE_WIDTH = Integer.parseInt(Groove.getGUIProperty("lts.emphasis.width"));

    /** Foreground color of ordinary inactive LTS cells. */
    static public final Color LTS_NORM_COLOR = Colors.findColor(Groove.getGUIProperty("default.color"));

    /** Color of active LTS cells. */
    static public final Color LTS_ACTIVE_COLOR = Colors.findColor(Groove.getGUIProperty("lts.emphasis.color"));

    /** Background color of ordinary (non-emphasized) LTS cells. */
    static public final Color LTS_NORM_BACKGROUND = Colors.findColor(Groove.getGUIProperty("default.background"));

    /** Background color of LTS start cells. */
    static public final Color LTS_START_BACKGROUND = Colors.findColor(Groove.getGUIProperty("lts.start.background"));

    /** Background color of unexplored LTS cells. */
    static public final Color LTS_OPEN_BACKGROUND = Colors.findColor(Groove.getGUIProperty("lts.open.background"));

    /** Background color of final LTS cells. */
    static public final Color LTS_FINAL_BACKGROUND = Colors.findColor(Groove.getGUIProperty("lts.final.background"));

    /** Borders of ordinary (non-active) LTS nodes. */
    static public final Border LTS_NORM_BORDER = createNodeBorder(new LineBorder(LTS_NORM_COLOR, LTS_NORM_WIDTH, true));

    /** Borders of active LTS nodes. */
    static public final Border LTS_ACTIVE_BORDER = createNodeBorder(new LineBorder(LTS_ACTIVE_COLOR, LTS_ACTIVE_WIDTH));
    //
    //    static public final int NR_OF_ROLES = RuleGraph.NR_OF_ROLES;
    //    static public final int ERASER = RuleGraph.ERASER;
    //    static public final int CREATOR = RuleGraph.CREATOR;
    //    static public final int READER = RuleGraph.READER;
    //    static public final int EMBARGO = RuleGraph.EMBARGO;

    static private final String[] PREFIXES = new String[RuleGraph.NR_OF_ROLES];
    static {
        PREFIXES[RuleGraph.READER] = "default.";
        PREFIXES[RuleGraph.EMBARGO] = "embargo.";
        PREFIXES[RuleGraph.ERASER] = "eraser.";
        PREFIXES[RuleGraph.CREATOR] = "creator.";
    }

    // colors
    static public final Color[] RULE_COLOR = new Color[RuleGraph.NR_OF_ROLES];
    static public final int[] RULE_WIDTH = new int[RuleGraph.NR_OF_ROLES];
    static public final float[][] RULE_DASH = new float[RuleGraph.NR_OF_ROLES][];
    static public final Border[] RULE_BORDER = new Border[RuleGraph.NR_OF_ROLES];
    static public final Border[] RULE_EMPH_BORDER = new Border[RuleGraph.NR_OF_ROLES];
    //    static private final Insets[] INSETS = new Insets[RuleGraph.NR_OF_ROLES];
    static {
        for (int role = 0; role < RuleGraph.NR_OF_ROLES; role++) {
            RULE_COLOR[role] = Colors.findColor(Groove.getGUIProperty(PREFIXES[role] + "color"));
            RULE_WIDTH[role] = Integer.parseInt(Groove.getGUIProperty(PREFIXES[role] + "width"));
            float[] dash = Groove.toFloatArray(Groove.getGUIProperty(PREFIXES[role] + "dash"));
            RULE_DASH[role] = dash == null ? JAttr.NO_DASH : dash;
            RULE_BORDER[role] = createNodeBorder(createRuleBorder(RULE_COLOR[role], RULE_WIDTH[role], RULE_DASH[role]));
            RULE_EMPH_BORDER[role] = createNodeBorder(createRuleBorder(RULE_COLOR[role], RULE_WIDTH[role] + 2, RULE_DASH[role]));
        }
    }
    
    /** Method creating a rule border with given color, dash pattern, and width. */
    private static Border createRuleBorder(Color color, int width, float[] dash) {
    	Border BORDER;
        if (dash == NO_DASH) {
            BORDER = new LineBorder(color, width);
        } else {
            BORDER =
                new StrokedLineBorder(
                    color,
                    new BasicStroke(
                        width,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_ROUND,
                        10.0f,
                        dash,
                        1.0f));
        }
        return BORDER;
    }

    /** The color used for hidden cells. */
    static public final Color INVISIBLE_COLOR = Colors.findColor(Groove.getGUIProperty("invisible.color"));
    /** The border used for hidden cells. */
    static public final Border INVISIBLE_BORDER =
        JAttr.createNodeBorder(new LineBorder(JAttr.INVISIBLE_COLOR, JAttr.DEFAULT_LINE_WIDTH));

    /**
     * The standard jgraph attributes used for graying out nodes and edges.
     */
    static public final AttributeMap HIDDEN_ATTR;

    /**
     * The standard jgraph attributes used for representing nodes.
     */
    public static final AttributeMap DEFAULT_NODE_ATTR;

    /**
     * The standard jgraph attributes used for representing edges.
     */
    public static final AttributeMap DEFAULT_EDGE_ATTR;

    static {
        // invisibility
        AttributeMap invisibleAttr = new AttributeMap();
        GraphConstants.setLineColor(invisibleAttr, JAttr.INVISIBLE_COLOR);
        GraphConstants.setForeground(invisibleAttr, JAttr.INVISIBLE_COLOR);
        GraphConstants.setBorder(invisibleAttr, JAttr.INVISIBLE_BORDER);
        GraphConstants.setOpaque(invisibleAttr, false);
        HIDDEN_ATTR = invisibleAttr;

        // set default node attributes
        // GraphConstants.setBorderColor(NODE_ATTR, CELL_COLOR);
        AttributeMap defaultEdgeAttr = new AttributeMap();
        GraphConstants.setEditable(defaultEdgeAttr, false);
        GraphConstants.setForeground(defaultEdgeAttr, JAttr.DEFAULT_CELL_COLOR);
        GraphConstants.setLineColor(defaultEdgeAttr, JAttr.DEFAULT_CELL_COLOR);
        GraphConstants.setLineWidth(defaultEdgeAttr, JAttr.DEFAULT_LINE_WIDTH);
        GraphConstants.setDashPattern(defaultEdgeAttr, JAttr.NO_DASH);
        GraphConstants.setLineEnd(defaultEdgeAttr, GraphConstants.ARROW_CLASSIC);
        GraphConstants.setEndFill(defaultEdgeAttr, true);
        GraphConstants.setBeginFill(defaultEdgeAttr, true);
        GraphConstants.setBendable(defaultEdgeAttr, true);
        GraphConstants.setBackground(defaultEdgeAttr, JAttr.DEFAULT_BACKGROUND);
        GraphConstants.setOpaque(defaultEdgeAttr, true);
        // GraphConstants.setLineStyle(EDGE_ATTR, GraphConstants.BEZIER);
        GraphConstants.setConnectable(defaultEdgeAttr, true);
        GraphConstants.setDisconnectable(defaultEdgeAttr, true);
        // no routing installed, because this is implemented
        // to disable manual changes
        GraphConstants.setRouting(defaultEdgeAttr, new org.jgraph.graph.Edge.Routing() {
			/**
			 * Returns {@link #NO_PREFERENCE}.
			 */
			public int getPreferredLineStyle(EdgeView edge) {
				return NO_PREFERENCE;
			}

			/**
			 * Returns <code>null</code>
			 */
			public List route(EdgeView edge) {
				return null;
			}
        	
        });
        DEFAULT_EDGE_ATTR = defaultEdgeAttr;

        // set default node attributes
        AttributeMap defaultNodeAttr = (AttributeMap) defaultEdgeAttr.clone();
        GraphConstants.setAutoSize(defaultNodeAttr, true);
        GraphConstants.setSizeable(defaultNodeAttr, false);
        GraphConstants.setBorder(defaultNodeAttr, JAttr.DEFAULT_BORDER);
        DEFAULT_NODE_ATTR = defaultNodeAttr;
    }

    /** Line width used for emphasized cells. */
    public static final int EMPH_WIDTH = Integer.parseInt(Groove.getGUIProperty("state.emphasis.width"));
    /** Color used for emphasized cells. */
    public static final Color EMPH_COLOR = Colors.findColor(Groove.getGUIProperty("state.emphasis.color"));
    /** Node border used for emphasized cells. */
    public static final Border EMPH_BORDER =
        createNodeBorder(new LineBorder(JAttr.EMPH_COLOR, JAttr.EMPH_WIDTH) {
            public Insets getBorderInsets(Component c) {
                return new Insets(1,1,1,1);
            }
        });

    /** The emphasis attribute changes for graph edges. */
    public static final AttributeMap EMPH_EDGE_CHANGE = new AttributeMap();
    /** The emphasis attribute changes for graph nodes. */
    public static final AttributeMap EMPH_NODE_CHANGE = new AttributeMap();

    // set the emphasis attributes
    static {
        // edges
        GraphConstants.setLineWidth(EMPH_EDGE_CHANGE, EMPH_WIDTH);
        // nodes
        GraphConstants.setBorder(EMPH_NODE_CHANGE, EMPH_BORDER);
    }

    /**
     * Creates a new border from a given border, by inserting space to the left and right.
     */
    private static Border createNodeBorder(Border border) {
        return new CompoundBorder(border, EMPTY_INSET_BORDER);
    }
}
