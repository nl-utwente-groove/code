/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.io.external.util;

import static groove.gui.look.EdgeEnd.ARROW;
import static groove.gui.look.Look.ABSENT;
import static groove.gui.look.Look.ABSTRACT;
import static groove.gui.look.Look.ACTIVE;
import static groove.gui.look.Look.ADDER;
import static groove.gui.look.Look.BASIC;
import static groove.gui.look.Look.BIDIRECTIONAL;
import static groove.gui.look.Look.COMPOSITE;
import static groove.gui.look.Look.CONNECT;
import static groove.gui.look.Look.CREATOR;
import static groove.gui.look.Look.CTRL_EXIT_TRANS;
import static groove.gui.look.Look.CTRL_OMEGA_EXIT_TRANS;
import static groove.gui.look.Look.CTRL_OMEGA_TRANS;
import static groove.gui.look.Look.CTRL_TRANSIENT_STATE;
import static groove.gui.look.Look.DATA;
import static groove.gui.look.Look.EMBARGO;
import static groove.gui.look.Look.EQUIV_CLASS;
import static groove.gui.look.Look.ERASER;
import static groove.gui.look.Look.ERROR_STATE;
import static groove.gui.look.Look.FINAL;
import static groove.gui.look.Look.GRAYED_OUT;
import static groove.gui.look.Look.NESTING;
import static groove.gui.look.Look.NODIFIED;
import static groove.gui.look.Look.NO_ARROW;
import static groove.gui.look.Look.OPEN;
import static groove.gui.look.Look.PATTERN;
import static groove.gui.look.Look.PRODUCT;
import static groove.gui.look.Look.REGULAR;
import static groove.gui.look.Look.REMARK;
import static groove.gui.look.Look.RESULT;
import static groove.gui.look.Look.START;
import static groove.gui.look.Look.STATE;
import static groove.gui.look.Look.SUBTYPE;
import static groove.gui.look.Look.TRANS;
import static groove.gui.look.Look.TRANSIENT;
import static groove.gui.look.Look.TYPE;
import static groove.gui.look.VisualKey.ADORNMENT;
import static groove.gui.look.VisualKey.BACKGROUND;
import static groove.gui.look.VisualKey.COLOR;
import static groove.gui.look.VisualKey.DASH;
import static groove.gui.look.VisualKey.EDGE_SOURCE_LABEL;
import static groove.gui.look.VisualKey.EDGE_SOURCE_POS;
import static groove.gui.look.VisualKey.EDGE_SOURCE_SHAPE;
import static groove.gui.look.VisualKey.EDGE_TARGET_LABEL;
import static groove.gui.look.VisualKey.EDGE_TARGET_POS;
import static groove.gui.look.VisualKey.EDGE_TARGET_SHAPE;
import static groove.gui.look.VisualKey.EMPHASIS;
import static groove.gui.look.VisualKey.ERROR;
import static groove.gui.look.VisualKey.FONT;
import static groove.gui.look.VisualKey.FOREGROUND;
import static groove.gui.look.VisualKey.INNER_LINE;
import static groove.gui.look.VisualKey.INSET;
import static groove.gui.look.VisualKey.LABEL;
import static groove.gui.look.VisualKey.LABEL_POS;
import static groove.gui.look.VisualKey.LINE_STYLE;
import static groove.gui.look.VisualKey.LINE_WIDTH;
import static groove.gui.look.VisualKey.NODE_POS;
import static groove.gui.look.VisualKey.NODE_SHAPE;
import static groove.gui.look.VisualKey.NODE_SIZE;
import static groove.gui.look.VisualKey.OPAQUE;
import static groove.gui.look.VisualKey.POINTS;
import static groove.gui.look.VisualKey.VISIBLE;
import groove.gui.jgraph.JAttr;
import groove.gui.look.EdgeEnd;
import groove.gui.look.Line.ColorType;
import groove.gui.look.Look;
import groove.gui.look.NodeShape;
import groove.gui.look.Values;
import groove.gui.look.VisualKey;
import groove.gui.look.VisualMap;
import groove.util.Duo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Class to automatically create a groove2tikz.sty file from the existing
 * {@link Look} enumeration.
 * 
 * @author Eduardo Zambon
 */
public final class TikzStylesExtractor {

    public static final Set<Look> mainLooks = EnumSet.of(BASIC, CREATOR,
        CONNECT, DATA, EMBARGO, ERASER, NESTING, PRODUCT, REMARK, TYPE,
        ABSTRACT, SUBTYPE, STATE, TRANS);

    private static final Set<Look> mainNodeLooks = EnumSet.of(BASIC, CREATOR,
        DATA, EMBARGO, ERASER, NESTING, PRODUCT, REMARK, TYPE, ABSTRACT, STATE);

    private static final Set<Look> mainEdgeLooks = EnumSet.of(BASIC, CREATOR,
        CONNECT, EMBARGO, ERASER, NESTING, REMARK, TYPE, ABSTRACT, SUBTYPE,
        TRANS);

    private static final Set<Look> modifyingLooks = EnumSet.of(NODIFIED,
        BIDIRECTIONAL, NO_ARROW, COMPOSITE, ERROR_STATE, START, OPEN, FINAL,
        RESULT, TRANSIENT, ABSENT, ACTIVE);

    private static final Set<Look> unusedLooks = EnumSet.of(REGULAR, ADDER,
        CTRL_TRANSIENT_STATE, CTRL_OMEGA_TRANS, CTRL_EXIT_TRANS,
        CTRL_OMEGA_EXIT_TRANS, PATTERN, EQUIV_CLASS, GRAYED_OUT);

    private static final Set<VisualKey> usedKeys = EnumSet.of(BACKGROUND, DASH,
        EDGE_SOURCE_SHAPE, EDGE_TARGET_SHAPE, FOREGROUND, LINE_WIDTH,
        NODE_SHAPE);

    private static final Set<VisualKey> unusedKeys = EnumSet.of(ADORNMENT,
        COLOR, EDGE_SOURCE_LABEL, EDGE_SOURCE_POS, EDGE_TARGET_LABEL,
        EDGE_TARGET_POS, EMPHASIS, ERROR, FONT, INNER_LINE, INSET, LABEL,
        LABEL_POS, LINE_STYLE, NODE_POS, NODE_SIZE, OPAQUE, POINTS, VISIBLE);

    /** Main method. */
    public static final void main(String[] args) {
        // First check if we are up-to-date with the Look enumeration.
        checkConsistency();
        TikzStylesExtractor extractor = new TikzStylesExtractor();
        // Collect the information.
        extractor.run();
        System.out.println(extractor.result);
    }

    private static final void checkConsistency() {
        Set<Look> allLooks = EnumSet.allOf(Look.class);
        allLooks.removeAll(mainLooks);
        allLooks.removeAll(modifyingLooks);
        allLooks.removeAll(unusedLooks);
        Set<VisualKey> allKeys = EnumSet.allOf(VisualKey.class);
        allKeys.removeAll(usedKeys);
        allKeys.removeAll(unusedKeys);
        if (!allLooks.isEmpty() || !allKeys.isEmpty()) {
            System.err.println("Cowardly refusing to run due to inconsistencies.");
            if (!allLooks.isEmpty()) {
                System.err.println("Don't know how to handle the following looks: "
                    + allLooks);
                System.err.println("Did you add new entries in the Look enumeration?");
            }
            if (!allKeys.isEmpty()) {
                System.err.println("Don't know how to handle the following visual keys: "
                    + allKeys);
                System.err.println("Did you add new entries in the VisualKey enumeration?");
            }
            System.exit(1);
        }
    }

    /**
     * The constructor is private. To perform the conversion just call the
     * main method.
     */
    private TikzStylesExtractor() {
        this.result = new StringBuilder();
    }

    private void run() {
        append(HEADER);
        appendMainColors();
        appendMainStyles();
        appendModifyingStyles();
        append(FOOTER);
    }

    private void appendMainColors() {
        for (ColorType cType : ColorType.values()) {
            Color color = cType.getColor();
            if (color != null) {
                append("\\definecolor{" + cType.name().toLowerCase()
                    + "_c}{RGB}" + Style.getColorStringDefinition(color)
                    + NEW_LINE);
            }
        }
    }

    private void appendMainStyles() {
        append(MAIN_STYLE_COMMENT);
        for (Look mainLook : mainLooks) {
            Style style = new Style(mainLook);
            VisualMap visualMap = mainLook.getVisuals();
            for (VisualKey key : usedKeys) {
                Object value = visualMap.get(key);
                style.addEntry(key, value);
            }
            style.fix();
            append(style.toString());
        }
    }

    private void appendModifyingStyles() {
        append(MOD_STYLE_COMMENT);
        List<StyleDuo> styles = new ArrayList<StyleDuo>();
        for (Look modifyingLook : modifyingLooks) {
            styles.clear();
            append(BEGIN_TIKZ_STYLE);
            append(modifyingLook.name().toLowerCase());
            append(MID_TIKZ_STYLE);
            computeModifyingStyle(modifyingLook, styles);
            append(styles.toString());
            append(END_TIKZ_STYLE);
        }
    }

    private void computeModifyingStyle(Look look, List<StyleDuo> styles) {
        // EZ says: this is a very ad-hoc implementation but I couldn't think
        // of a better way to do this.
        String edgeEnd = Style.getEdgeEndShape(ARROW);
        Color foreground = look.getVisuals().getForeground();
        Color background = look.getVisuals().getBackground();
        switch (look) {
        case NODIFIED:
            Style.writeNodeShape(NodeShape.ELLIPSE, styles);
            styles.add(new StyleDuo("minimum size", "4pt"));
            break;
        case BIDIRECTIONAL:
            styles.add(new StyleDuo(edgeEnd + "-" + edgeEnd, null));
            break;
        case NO_ARROW:
            styles.add(new StyleDuo("-", null));
            break;
        case COMPOSITE:
            String srcEdgeEnd = Style.getEdgeEndShape(EdgeEnd.COMPOSITE);
            styles.add(new StyleDuo(srcEdgeEnd + "-" + edgeEnd, null));
            break;
        case ERROR_STATE:
            Style.writeForegroundColor(foreground, styles);
            break;
        case START:
            Style.writeForegroundColor(foreground, styles);
            Style.writeBackgroundColor(background, styles);
            break;
        case OPEN:
        case FINAL:
        case RESULT:
            Style.writeBackgroundColor(background, styles);
            break;
        case TRANSIENT:
            Style.writeNodeShape(NodeShape.DIAMOND, styles);
            Style.writeForegroundColor(foreground, styles);
            break;
        case ABSENT: // TODO
            break;
        case ACTIVE: // TODO
            break;
        default:
            throw new IllegalArgumentException("Invalid modifying look!");
        }
    }

    private void append(String string) {
        this.result.append(string);
    }

    /** The builder that holds the Tikz string. */
    private final StringBuilder result;

    private static final String NEW_LINE = "\n";
    public static final String NODE_SUFFIX = "_node";
    public static final String EDGE_SUFFIX = "_edge";
    private static final String BEGIN_TIKZ_STYLE = "\\tikzstyle{";
    private static final String MID_TIKZ_STYLE = "}=";
    private static final String END_TIKZ_STYLE = NEW_LINE;

    private static final String DENSELY_DASHED_KEY = "densely dashed";
    private static final String SHAPE_KEY = "shape";
    private static final String DIAMOND_VAL = "diamond";
    private static final String ELLIPSE_VAL = "ellipse";
    private static final String RECTANGLE_VAL = "rectangle";

    private static final String HEADER =
        "% Package that defines the styles used in Tikz figures exported in GROOVE."
            + NEW_LINE
            + "% This file was automatically generated by the TikzStylesExtraction utility."
            + NEW_LINE
            + NEW_LINE
            + "\\ProvidesPackage{groove2tikz}"
            + NEW_LINE
            + "\\RequirePackage{tikz}"
            + NEW_LINE
            + "\\usepackage[T1]{fontenc}"
            + NEW_LINE
            + "\\usepackage{amssymb}"
            + NEW_LINE
            + NEW_LINE
            + "% Includes for Tikz."
            + NEW_LINE
            + "\\usetikzlibrary{arrows,automata,positioning,er}"
            + NEW_LINE
            + NEW_LINE
            + "% Dimension styles."
            + NEW_LINE
            + "\\newcommand{\\tikzfontsize}{\\footnotesize}"
            + NEW_LINE
            + "\\newcommand{\\tikzscale}{2}"
            + NEW_LINE
            + NEW_LINE
            + "\\tikzstyle every node=[font=\\tikzfontsize\\sffamily, inner sep=2.5pt, minimum size=9pt]"
            + NEW_LINE + NEW_LINE + "% Extra style for edge labels." + NEW_LINE
            + "\\tikzstyle{lab}=[fill=white, inner sep=1pt]" + NEW_LINE
            + NEW_LINE + "% Default colors for TeX strings." + NEW_LINE;

    private static final String MAIN_STYLE_COMMENT =
        NEW_LINE
            + "% Main styles. (Should be used first in a node and edge definition.)"
            + NEW_LINE;

    private static final String MOD_STYLE_COMMENT =
        NEW_LINE
            + "% Modifying styles. (To be used in conjunction - AFTER - a main style.)"
            + NEW_LINE + NEW_LINE;

    private static final String FOOTER = NEW_LINE
        + "% Ugly hack to allow nodes with multiple lines." + NEW_LINE
        + "\\newcommand{\\ml}[1]{" + NEW_LINE
        + "\\begin{tabular}{@{}c@{}}#1\\vspace{-2pt}\\end{tabular}" + NEW_LINE
        + "}" + NEW_LINE;

    private static final class Style {
        Color background;
        Color foreground;
        float[] dash;
        EdgeEnd sourceEnd;
        EdgeEnd targetEnd;
        float lineWidth;
        NodeShape nodeShape;

        final Look look;
        final List<StyleDuo> nodes;
        final List<StyleDuo> edges;

        Style(Look look) {
            this.look = look;
            this.nodes = new ArrayList<StyleDuo>();
            this.edges = new ArrayList<StyleDuo>();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(NEW_LINE);
            if (!this.nodes.isEmpty()) {
                sb.append(BEGIN_TIKZ_STYLE);
                sb.append(this.look.name().toLowerCase() + NODE_SUFFIX);
                sb.append(MID_TIKZ_STYLE);
                sb.append(this.nodes.toString());
                sb.append(END_TIKZ_STYLE);
            }
            if (!this.edges.isEmpty()) {
                sb.append(BEGIN_TIKZ_STYLE);
                sb.append(this.look.name().toLowerCase() + EDGE_SUFFIX);
                sb.append(MID_TIKZ_STYLE);
                sb.append(this.edges.toString());
                sb.append(END_TIKZ_STYLE);
            }
            return sb.toString();
        }

        /** Fills the style fields. */
        void addEntry(VisualKey key, Object value) {
            switch (key) {
            case BACKGROUND:
                this.background = (Color) value;
                break;
            case DASH:
                this.dash = (float[]) value;
                break;
            case EDGE_SOURCE_SHAPE:
                this.sourceEnd = (EdgeEnd) value;
                break;
            case EDGE_TARGET_SHAPE:
                this.targetEnd = (EdgeEnd) value;
                break;
            case FOREGROUND:
                this.foreground = (Color) value;
                break;
            case LINE_WIDTH:
                this.lineWidth = (Float) value;
                break;
            case NODE_SHAPE:
                this.nodeShape = (NodeShape) value;
                break;
            default:
                break;
            }
        }

        /** Converts the fields to StyleDuos. */
        void fix() {
            // Node styles.
            if (mainNodeLooks.contains(this.look)) {
                writeNodeShape();
                writeDash(this.nodes);
                writeLineWidth(this.nodes);
                writeForegroundColor();
                writeBackgroundColor();
            }
            // Edge styles.
            if (mainEdgeLooks.contains(this.look)) {
                writeDraw();
                writeEdgeEnds();
                writeDash(this.edges);
                writeLineWidth(this.edges);
                writeEdgeColor();
            }
        }

        private void writeNodeShape() {
            writeNodeShape(this.nodeShape, this.nodes);
        }

        private void writeDash(List<StyleDuo> list) {
            if (this.dash == Values.NESTED_DASH) {
                list.add(new StyleDuo("densely dotted", null));
            } else if (this.dash != Values.NO_DASH) {
                list.add(new StyleDuo(DENSELY_DASHED_KEY, null));
            }
        }

        private void writeLineWidth(List<StyleDuo> list) {
            int w = (int) Math.floor(this.lineWidth / 2.0);
            if (w > 0) {
                list.add(new StyleDuo("line width", w + "pt"));
            }
        }

        private void writeForegroundColor() {
            writeForegroundColor(this.foreground, this.nodes);
        }

        private void writeBackgroundColor() {
            writeBackgroundColor(this.background, this.nodes);
        }

        private void writeDraw() {
            this.edges.add(new StyleDuo("draw", null));
        }

        private void writeEdgeEnds() {
            String srcEnd = getEdgeEndShape(this.sourceEnd);
            String tgtEnd = getEdgeEndShape(this.targetEnd);
            this.edges.add(new StyleDuo(srcEnd + "-" + tgtEnd, null));
        }

        private void writeEdgeColor() {
            String c = getColorString(this.foreground);
            this.edges.add(new StyleDuo("color", c));
        }

        static void writeForegroundColor(Color foreground, List<StyleDuo> styles) {
            String c = getColorString(foreground);
            styles.add(new StyleDuo("draw", c));
            styles.add(new StyleDuo("text", c));
        }

        static void writeBackgroundColor(Color background, List<StyleDuo> styles) {
            String c = getColorString(background);
            styles.add(new StyleDuo("fill", c));
        }

        static String getEdgeEndShape(EdgeEnd end) {
            switch (end) {
            case ARROW:
            case UNFILLED:
            case NESTING:
                return "stealth'";
            case COMPOSITE:
                return "diamond";
            case DOUBLE_LINE:
                // TODO: Control Automaton. See recipes.gps
                return "";
            case NONE:
                return "";
            case SIMPLE:
                return "to";
            case SUBTYPE:
                return "open triangle 60";
            default:
                throw new IllegalArgumentException(
                    "Default fall-thought in edge end shape! Did you add a new edge end shape?");
            }
        }

        static void writeNodeShape(NodeShape nodeShape, List<StyleDuo> styles) {
            final String ROUNDED_CORNERS_KEY = "rounded corners";

            switch (nodeShape) {
            case DIAMOND:
                styles.add(new StyleDuo(SHAPE_KEY, DIAMOND_VAL));
                styles.add(new StyleDuo("shape aspect", "2"));
                break;
            case ELLIPSE:
                styles.add(new StyleDuo(SHAPE_KEY, ELLIPSE_VAL));
                break;
            case OVAL:
                styles.add(new StyleDuo(SHAPE_KEY, RECTANGLE_VAL));
                styles.add(new StyleDuo(ROUNDED_CORNERS_KEY,
                    JAttr.STRONG_ARC_SIZE / 5 + "pt"));
                break;
            case RECTANGLE:
                styles.add(new StyleDuo(SHAPE_KEY, RECTANGLE_VAL));
                styles.add(new StyleDuo(ROUNDED_CORNERS_KEY, "0pt"));
                break;
            case ROUNDED:
                styles.add(new StyleDuo(SHAPE_KEY, RECTANGLE_VAL));
                styles.add(new StyleDuo(ROUNDED_CORNERS_KEY,
                    JAttr.NORMAL_ARC_SIZE / 5 + "pt"));
                break;
            default:
                throw new IllegalArgumentException(
                    "Default fall-thought in node shape! Did you add a new node shape?");
            }
        }

        static String getColorString(Color color) {
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();
            // {rgb:red,r;green,g;blue,b}
            return "{rgb,255:red," + r + ";" + "green," + g + ";" + "blue," + b
                + "}";
        }

        static String getColorStringDefinition(Color color) {
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();
            // {r,g,b}
            return "{" + r + "," + g + "," + b + "}";
        }
    }

    /** Key, value pairs. */
    private static final class StyleDuo extends Duo<String> {

        public StyleDuo(String one, String two) {
            super(one, two);
        }

        @Override
        public String toString() {
            String one = one();
            String two = two();
            if (two == null) {
                return one;
            } else {
                return String.format("%s=%s", one, two);
            }
        }

    }
}
