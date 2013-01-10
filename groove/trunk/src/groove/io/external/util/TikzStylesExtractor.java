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

import groove.gui.jgraph.JAttr;
import groove.gui.look.EdgeEnd;
import groove.gui.look.Look;
import groove.gui.look.NodeShape;
import groove.gui.look.Values;
import groove.gui.look.VisualKey;
import groove.gui.look.VisualMap;
import groove.util.Duo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class to automatically create a groove2tikz.sty file from the existing
 * {@link Look} enumeration.
 * 
 * @author Eduardo Zambon
 */
public final class TikzStylesExtractor {

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

    private static final class Style {
        Color background;
        Color foreground;
        float[] dash;
        EdgeEnd sourceEnd;
        EdgeEnd targetEnd;
        int inset;
        float lineWidth;
        NodeShape nodeShape;

        final List<StyleDuo> styles;

        Style() {
            this.styles = new ArrayList<StyleDuo>();
        }

        @Override
        public String toString() {
            return this.styles.toString();
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
            case INSET:
                this.inset = (Integer) value;
                break;
            case LINE_WIDTH:
                this.lineWidth = (Float) value;
                break;
            case NODE_SHAPE:
                this.nodeShape = (NodeShape) value;
                break;
            case ADORNMENT:
            case COLOR:
            case EDGE_SOURCE_LABEL:
            case EDGE_SOURCE_POS:
            case EDGE_TARGET_LABEL:
            case EDGE_TARGET_POS:
            case EMPHASIS:
            case ERROR:
            case FONT:
            case INNER_LINE:
            case LABEL:
            case LABEL_POS:
            case LINE_STYLE:
            case NODE_POS:
            case NODE_SIZE:
            case OPAQUE:
            case POINTS:
            case VISIBLE:
                // Not used because these do not form a static style.
                break;
            default:
                throw new IllegalArgumentException(
                    "Default fall-thought in visual key! Did you add a new style?");
            }
        }

        /** Converts the fields to StyleDuos. */
        void fix() {
            writeEdgeEnds();
            writeNodeShape();
            writeDash();
            writeLineWidth();
            writeForegroundColor();
            writeBackgroundColor();
            writeInset();
        }

        private void add(StyleDuo duo) {
            this.styles.add(duo);
        }

        private void writeEdgeEnds() {
            String srcEnd = getEdgeEndShape(this.sourceEnd);
            String tgtEnd = getEdgeEndShape(this.targetEnd);
            add(new StyleDuo(srcEnd + "-" + tgtEnd, null));
        }

        private void writeNodeShape() {
            final String SHAPE_KEY = "shape";
            final String ROUNDED_CORNERS_KEY = "rounded corners";
            final String RECTANGLE_VAL = "rectangle";

            switch (this.nodeShape) {
            case DIAMOND:
                add(new StyleDuo(SHAPE_KEY, "diamond"));
                add(new StyleDuo("shape aspect", "2"));
                break;
            case ELLIPSE:
                add(new StyleDuo(SHAPE_KEY, "ellipse"));
                break;
            case OVAL:
                add(new StyleDuo(SHAPE_KEY, RECTANGLE_VAL));
                add(new StyleDuo(ROUNDED_CORNERS_KEY, JAttr.STRONG_ARC_SIZE / 5
                    + "pt"));
                break;
            case RECTANGLE:
                add(new StyleDuo(SHAPE_KEY, RECTANGLE_VAL));
                add(new StyleDuo(ROUNDED_CORNERS_KEY, "0pt"));
                break;
            case ROUNDED:
                add(new StyleDuo(SHAPE_KEY, RECTANGLE_VAL));
                add(new StyleDuo(ROUNDED_CORNERS_KEY, JAttr.NORMAL_ARC_SIZE / 5
                    + "pt"));
                break;
            default:
                throw new IllegalArgumentException(
                    "Default fall-thought in node shape! Did you add a new node shape?");
            }
        }

        private void writeDash() {
            // EZ says: for now we just ignore the dash array and set everything to
            // be 'densely dashed' in Tikz.
            if (this.dash != Values.NO_DASH) {
                add(new StyleDuo("densely dashed", null));
            }
        }

        private void writeLineWidth() {
            int w = (int) Math.floor(this.lineWidth / 2.0);
            if (w > 0) {
                add(new StyleDuo("line width", w + "pt"));
            }
        }

        private void writeForegroundColor() {
            String c = getColorString(this.foreground);
            add(new StyleDuo("draw", c));
            add(new StyleDuo("text", c));
        }

        private void writeBackgroundColor() {
            String c = getColorString(this.background);
            add(new StyleDuo("fill", c));
        }

        private void writeInset() {
            // TODO Auto-generated method stub

        }

        private String getEdgeEndShape(EdgeEnd end) {
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

        private String getColorString(Color color) {
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();
            // {rgb:red,r;green,g;blue,b}
            return "{rgb,255:red," + r + ";" + "green," + g + ";" + "blue," + b
                + "}";
        }
    }

    /**
     * Main method.
     */
    public static void main(String[] args) {
        TikzStylesExtractor extractor = new TikzStylesExtractor();
        // Collect the information.
        extractor.run();
        // Write it.
        extractor.write();
        System.out.println(extractor.result);
    }

    /** The builder that holds the Tikz string. */
    private final StringBuilder result;
    /** Map from looks to their style representation. */
    private final Map<Look,Style> lookMap;

    /**
     * The constructor is private. To perform the conversion just call the
     * main method.
     */
    private TikzStylesExtractor() {
        this.result = new StringBuilder();
        this.lookMap = new EnumMap<Look,Style>(Look.class);
    }

    private void run() {
        for (Look look : Look.values()) {
            Style style = computeStyle(look);
            this.lookMap.put(look, style);
        }
    }

    private Style computeStyle(Look look) {
        Style style = new Style();
        VisualMap visualMap = look.getVisuals();
        // Iterate over all visual keys, not only the ones in the visual map.
        for (VisualKey key : VisualKey.values()) {
            Object value = visualMap.get(key);
            style.addEntry(key, value);
        }
        style.fix();
        return style;
    }

    private void write() {
        append(HEADER);
        for (Entry<Look,Style> entry : this.lookMap.entrySet()) {
            Look look = entry.getKey();
            Style style = entry.getValue();
            append(NEW_LINE);
            append(BEGIN_TIKZ_STYLE);
            append(look.name().toLowerCase());
            append(MID_TIKZ_STYLE);
            append(style.toString());
            append(END_TIKZ_STYLE);
        }
        append(FOOTER);
    }

    private void append(String string) {
        this.result.append(string);
    }

    private static final String NEW_LINE = "\n";
    private static final String BEGIN_TIKZ_STYLE = "\\tikzstyle{";
    private static final String MID_TIKZ_STYLE = "}=";
    private static final String END_TIKZ_STYLE = NEW_LINE;

    private static final String HEADER =
        "% Package that defines the styles used in Tikz figures exported in GROOVE."
            + NEW_LINE
            + "% This file was automatically generated by the TikzStylesExtraction utility."
            + NEW_LINE + NEW_LINE + "\\ProvidesPackage{groove2tikz}" + NEW_LINE
            + "\\RequirePackage{tikz}" + NEW_LINE + "\\usepackage[T1]{fontenc}"
            + NEW_LINE + "\\usepackage{amssymb}" + NEW_LINE + NEW_LINE
            + "% Includes for Tikz." + NEW_LINE
            + "\\usetikzlibrary{arrows,automata,positioning,er}" + NEW_LINE
            + NEW_LINE + "% Dimension styles" + NEW_LINE
            + "\\newcommand{\\tikzfontsize}{\\footnotesize}" + NEW_LINE
            + "\\newcommand{\\tikzscale}{2}" + NEW_LINE + NEW_LINE
            + "\\tikzstyle every node=[font=\\tikzfontsize\\sffamily]"
            + NEW_LINE;

    private static final String FOOTER = NEW_LINE
        + "% Ugly hack to allow nodes with multiple lines." + NEW_LINE
        + "\\newcommand{\\ml}[1]{" + NEW_LINE
        + "\\begin{tabular}{@{}c@{}}#1\\vspace{-2pt}\\end{tabular}" + NEW_LINE
        + "}" + NEW_LINE + NEW_LINE;

}
