/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
 * $Id$
 */
package nl.utwente.groove.gui.look;

import static nl.utwente.groove.gui.look.Values.Mode.FOCUSED;
import static nl.utwente.groove.gui.look.Values.Mode.NONE;
import static nl.utwente.groove.gui.look.Values.Mode.SELECTED;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;

import org.jgraph.graph.GraphConstants;

import nl.utwente.groove.gui.jgraph.JAttr;
import nl.utwente.groove.util.Colors;
import nl.utwente.groove.util.DefaultFixable;
import nl.utwente.groove.util.line.LineStyle;

/** Attribute values for the nodes and edges.
 *
 * @author Arend rensink
 * @version $Revision$
 */
public class Values {
    /** Dash pattern of absent graphs and transitions. */
    public static final float[] ABSENT_DASH = {3.0f, 3.0f};
    /** Dash pattern of abstract type nodes and edges. */
    public static final float[] ABSTRACT_DASH = {6.0f, 2.0f};
    /** Dash pattern of connect edges. */
    public static final float[] CONNECT_DASH = {2f, 4f};
    /** Dash pattern for verdict edges. */
    public static final float[] VERDICT_DASH = {4.0f, 3.0f};
    /** No dash pattern. */
    public static final float[] NO_DASH = {10.f, 0.f};
    /** Background colour of creator nodes and edges. */
    public static final Color CREATOR_BACKGROUND = null;
    /**
     * The default foreground colour used for edges and nodes.
     */
    public static final Color DEFAULT_FOREGROUND = Color.black;
    /**
     * The default background colour used for nodes.
     */
    public static final Color DEFAULT_BACKGROUND = Colors.findColor("243 243 243");
    /** Dash pattern of embargo nodes and edges. */
    public static final float[] EMBARGO_DASH = {2f, 2f};
    /** Background colour of embargo nodes and edges. */
    public static final Color EMBARGO_BACKGROUND = null;
    /** Dash pattern of eraser nodes and edges. */
    public static final float[] ERASER_DASH = {4f, 4f};
    /** Background colour of eraser nodes and edges. */
    public static final Color ERASER_BACKGROUND = Colors.findColor("200 240 255");
    /** Dash pattern of nesting nodes and edges. */
    public static final float[] NESTED_DASH = {2.0f, 3.0f};
    /** Background colour of remark nodes and edges. */
    public static final Color REMARK_BACKGROUND = Colors.findColor("255 255 180");

    /** Background colour of (normal) open states. */
    public static final Color OPEN_BACKGROUND = Color.GRAY.brighter();
    /** Background colour of final states. */
    public static final Color FINAL_BACKGROUND = Colors.findColor("0 200 0");
    /** Foreground colour of result states. */
    public static final Color RESULT_FOREGROUND = Colors.findColor("92 125 23");
    /** Background colour of result states. */
    public static final Color RESULT_BACKGROUND = Colors.findColor("0x90EE90");
    /** Background colour of error states. */
    public static final Color ERROR_BACKGROUND = Color.RED;
    /** Foreground colour of the start state. */
    public static final Color START_FOREGROUND = JAttr.STATE_BACKGROUND;
    /** Background colour of the start state. */
    public static final Color START_BACKGROUND = Color.BLACK;
    /** Background colour of the start state while it is still open. */
    public static final Color START_OPEN_BACKGROUND = Color.GRAY.darker();
    /** Foreground colour for active nodes and edges. */
    public static final Color ACTIVE_COLOR = Color.BLUE;
    /** Foreground colour for the active start node. */
    public static final Color ACTIVE_START_COLOR = Colors.findColor("40 200 255");
    /** Foreground colour for an active final node. */
    public static final Color ACTIVE_FINAL_COLOR = Colors.findColor("30 100 200");
    /** Colour used for transient states. */
    static public final Color RECIPE_COLOR = Colors.findColor("165 42 42");
    /** Colour used for transient active states. */
    static public final Color ACTIVE_RECIPE_COLOR = Colors.findColor("165 42 149");

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
    /** Text display colours to be used in normal display mode. */
    static public final Values.ColorSet NORMAL_COLORS = new Values.ColorSet();
    static {
        NORMAL_COLORS.putColors(FOCUSED, FOCUS_FOREGROUND, FOCUS_BACKGROUND);
        NORMAL_COLORS.putColors(SELECTED, SELECT_FOREGROUND, SELECT_BACKGROUND);
        NORMAL_COLORS.putColors(NONE, NORMAL_FOREGROUND, NORMAL_BACKGROUND);
    }

    /** Opaque colour used for indicating errors in graphs.
     * Chosen so it resembles {@link Colors#ERROR_COLOR} on a white background
     */
    static public final Color ERROR_COLOR_OPAQUE = new Color(245, 215, 205);
    /** Background colour used for focused error items in lists. */
    static public final Color ERROR_FOCUS_BACKGROUND = Color.RED.darker().darker();
    /** Text colour used for focused error items in lists. */
    static public final Color ERROR_FOCUS_FOREGROUND = Color.WHITE;
    /** Background colour used for selected, non-focused error items in lists. */
    static public final Color ERROR_SELECT_BACKGROUND = Colors.ERROR_COLOR;
    /** Text colour used for selected, non-focused error items in lists. */
    static public final Color ERROR_SELECT_FOREGROUND = Color.RED;
    /** Background colour used for non-selected, non-focused error items in lists. */
    static public final Color ERROR_NORMAL_BACKGROUND = Color.WHITE;
    /** Text colour used for non-selected, non-focused error items in lists. */
    static public final Color ERROR_NORMAL_FOREGROUND = Color.RED;
    /** Text display colours to be used in error mode. */
    static public final Values.ColorSet ERROR_COLORS = new Values.ColorSet();
    static {
        ERROR_COLORS.putColors(FOCUSED, ERROR_FOCUS_FOREGROUND, ERROR_FOCUS_BACKGROUND);
        ERROR_COLORS.putColors(SELECTED, ERROR_SELECT_FOREGROUND, ERROR_SELECT_BACKGROUND);
        ERROR_COLORS.putColors(NONE, ERROR_NORMAL_FOREGROUND, ERROR_NORMAL_BACKGROUND);
    }

    /** Colour used for indicating warnings. */
    static public final Color WARNING_COLOR = new Color(190, 110, 0);
    /** Translucent colour used for indicating warnings in tab labels;
     * the analog of {@link Colors#ERROR_COLOR} for errors. */
    static public final Color WARNING_BACKGROUND = new Color(255, 160, 0, 60);
    /** Background colour used for focused warning items in lists. */
    static public final Color WARNING_FOCUS_BACKGROUND = WARNING_COLOR.darker();
    /** Text colour used for focused warning items in lists. */
    static public final Color WARNING_FOCUS_FOREGROUND = Color.WHITE;
    /** Background colour used for selected, non-focused warning items in lists. */
    static public final Color WARNING_SELECT_BACKGROUND = new Color(250, 230, 200);
    /** Text colour used for selected, non-focused warning items in lists. */
    static public final Color WARNING_SELECT_FOREGROUND = WARNING_COLOR;
    /** Background colour used for non-selected, non-focused warning items in lists. */
    static public final Color WARNING_NORMAL_BACKGROUND = Color.WHITE;
    /** Text colour used for non-selected, non-focused warning items in lists. */
    static public final Color WARNING_NORMAL_FOREGROUND = WARNING_COLOR;
    /** Text display colours to be used in warning mode. */
    static public final Values.ColorSet WARNING_COLORS = new Values.ColorSet();
    static {
        WARNING_COLORS.putColors(FOCUSED, WARNING_FOCUS_FOREGROUND, WARNING_FOCUS_BACKGROUND);
        WARNING_COLORS.putColors(SELECTED, WARNING_SELECT_FOREGROUND, WARNING_SELECT_BACKGROUND);
        WARNING_COLORS.putColors(NONE, WARNING_NORMAL_FOREGROUND, WARNING_NORMAL_BACKGROUND);
    }

    /** Colour used for indicating information in graphs. */
    static public final Color INFO_COLOR = new Color(0, 102, 255, 40);
    /** Background colour used for focused info items in lists. */
    static public final Color INFO_FOCUS_BACKGROUND = Color.BLUE;
    /** Text colour used for focused info items in lists. */
    static public final Color INFO_FOCUS_FOREGROUND = Color.WHITE;
    /** Background colour used for selected, non-focused info items in lists. */
    static public final Color INFO_SELECT_BACKGROUND = INFO_COLOR;
    /** Text colour used for selected, non-focused info items in lists. */
    static public final Color INFO_SELECT_FOREGROUND = Color.BLUE;
    /** Background colour used for non-selected, non-focused info items in lists. */
    static public final Color INFO_NORMAL_BACKGROUND = new Color(180, 210, 255, 40);
    /** Text colour used for non-selected, non-focused info items in lists. */
    static public final Color INFO_NORMAL_FOREGROUND = Colors.INFO_COLOR;
    /** Text display colours to be used in info mode. */
    static public final Values.ColorSet INFO_COLORS = new Values.ColorSet();
    static {
        INFO_COLORS.putColors(FOCUSED, INFO_FOCUS_FOREGROUND, INFO_FOCUS_BACKGROUND);
        INFO_COLORS.putColors(SELECTED, INFO_SELECT_FOREGROUND, INFO_SELECT_BACKGROUND);
        INFO_COLORS.putColors(NONE, INFO_NORMAL_FOREGROUND, INFO_NORMAL_BACKGROUND);
    }

    /** Text display colours to be used for transient states. */
    static public final Values.ColorSet RECIPE_COLORS = new Values.ColorSet();
    static {
        RECIPE_COLORS.putColors(FOCUSED, Color.WHITE, RECIPE_COLOR.darker());
        RECIPE_COLORS.putColors(SELECTED, RECIPE_COLOR.darker(), SELECT_BACKGROUND);
        RECIPE_COLORS.putColors(NONE, RECIPE_COLOR, NORMAL_BACKGROUND);
    }

    /** Grayed-out foreground color. */
    static public final Color GRAYED_OUT_COLOR = Colors.findColor("200 200 200 100");

    /** Line style that always makes right edges. */
    public static final int STYLE_MANHATTAN = 14;

    static {
        // the LineStyle codes are persisted in graph layouts and interpreted
        // by JGraph; both sides must stay aligned with the core enum
        assert LineStyle.ORTHOGONAL.getCode() == GraphConstants.STYLE_ORTHOGONAL;
        assert LineStyle.SPLINE.getCode() == GraphConstants.STYLE_SPLINE;
        assert LineStyle.BEZIER.getCode() == GraphConstants.STYLE_BEZIER;
        assert LineStyle.MANHATTAN.getCode() == STYLE_MANHATTAN;
    }

    /** Cell selection modes in trees or lists. */
    public static enum Mode {
        /** Focused selection. */
        FOCUSED,
        /** Normal selection. */
        SELECTED,
        /** No selection. */
        NONE;

        /** Converts a pair of boolean values into a selection mode. */
        public static Mode toMode(boolean selected, boolean focused) {
            if (focused) {
                return Mode.FOCUSED;
            } else if (selected) {
                return Mode.SELECTED;
            } else {
                return Mode.NONE;
            }
        }
    }

    /** Set of colours per selection mode. */
    public static class ColorSet extends DefaultFixable {
        /** Adds the foreground and background colours for a given selection mode. */
        public void putColors(Mode mode, Color foreground, Color background) {
            testFixed(false);
            Color oldFore = this.foreColors.put(mode, foreground);
            assert oldFore == null;
            Color oldBack = this.backColors.put(mode, background);
            assert oldBack == null;
            if (this.foreColors.size() == Mode.values().length) {
                setFixed();
            }
        }

        /**
         * Returns the foreground colour for the mode indicated by the parameters.
         * @param selected if {@code true}, use selection mode
         * @param focused if {@code true}, use focused mode
         * @return the colour for the relevant mode
         */
        public Color getForeground(boolean selected, boolean focused) {
            return getColor(this.foreColors, selected, focused);
        }

        /**
         * Returns the foreground colour for the given selection mode
         * @return the colour for the relevant mode
         */
        public Color getForeground(Mode mode) {
            return this.foreColors.get(mode);
        }

        /**
         * Returns the background colour for the mode indicated by the parameters.
         * @param selected if {@code true}, use selection mode
         * @param focused if {@code true}, use focused mode
         * @return the colour for the relevant mode
         */
        public Color getBackground(boolean selected, boolean focused) {
            return getColor(this.backColors, selected, focused);
        }

        /**
         * Returns the background colour for the given selection mode
         * @return the colour for the relevant mode
         */
        public Color getBackground(Mode mode) {
            return this.backColors.get(mode);
        }

        private Color getColor(Map<Mode,Color> colors, boolean selected, boolean focused) {
            return colors.get(Mode.toMode(selected, focused));
        }

        private final Map<Mode,Color> foreColors = new EnumMap<>(Mode.class);
        private final Map<Mode,Color> backColors = new EnumMap<>(Mode.class);
    }
}
