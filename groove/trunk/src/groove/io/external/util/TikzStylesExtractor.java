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
import groove.gui.look.VisualKey;
import groove.gui.look.VisualMap;
import groove.util.Duo;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to automatically create a groove2tikz.sty file from the existing
 * {@link Look} enumeration.
 * 
 * @author Eduardo Zambon
 */
public final class TikzStylesExtractor {

    /**
     * Main method.
     */
    public static void main(String[] args) {
        TikzStylesExtractor extractor = new TikzStylesExtractor();
        // Collect the information.
        extractor.run();
        // Write it.
        extractor.write();
    }

    /** The builder that holds the Tikz string. */
    private final StringBuilder result;
    private final List<StyleDuo> styles;

    /**
     * The constructor is private. To perform the conversion just call the
     * main method.
     */
    private TikzStylesExtractor() {
        this.result = new StringBuilder();
        this.styles = new ArrayList<StyleDuo>();
    }

    private void run() {
        append(HEADER);
        for (Look look : Look.values()) {
            append(look);
        }
        append(FOOTER);
    }

    private void write() {
        System.out.println(this.result);
    }

    private void append(String string) {
        this.result.append(string);
    }

    private void add(StyleDuo duo) {
        this.styles.add(duo);
    }

    private void append(Look look) {
        this.styles.clear();
        append(NEW_LINE);
        append(BEGIN_TIKZ_STYLE);
        append(look.name().toLowerCase());
        append(MID_TIKZ_STYLE);
        VisualMap visualMap = look.getVisuals();
        for (VisualKey key : visualMap.keySet()) {
            Object value = visualMap.get(key);
            computeStyles(key, value);
        }
        append(this.styles.toString());
        append(END_TIKZ_STYLE);
    }

    private void computeStyles(VisualKey key, Object value) {
        switch (key) {
        case ADORNMENT:
            break;
        case BACKGROUND:
            break;
        case COLOR:
            break;
        case DASH:
            break;
        case EDGE_SOURCE_LABEL:
            break;
        case EDGE_SOURCE_POS:
            break;
        case EDGE_SOURCE_SHAPE:
            convertEdgeEndShape(EDGE_SOURCE_END_KEY, (EdgeEnd) value);
            break;
        case EDGE_TARGET_LABEL:
            break;
        case EDGE_TARGET_POS:
            break;
        case EDGE_TARGET_SHAPE:
            convertEdgeEndShape(EDGE_TARGET_END_KEY, (EdgeEnd) value);
            break;
        case EMPHASIS:
            break;
        case ERROR:
            break;
        case FONT:
            break;
        case FOREGROUND:
            break;
        case INNER_LINE:
            break;
        case INSET:
            break;
        case LABEL:
            break;
        case LABEL_POS:
            break;
        case LINE_STYLE:
            break;
        case LINE_WIDTH:
            break;
        case NODE_POS:
            break;
        case NODE_SHAPE:
            convertNodeShape((NodeShape) value);
            break;
        case NODE_SIZE:
            break;
        case OPAQUE:
        case POINTS:
        case VISIBLE:
            // Not used.
            break;
        default:
            throw new IllegalArgumentException(
                "Default fall-thought in visual key! Did you add a new style?");
        }
    }

    private void convertNodeShape(NodeShape shape) {
        switch (shape) {
        case DIAMOND:
            add(new StyleDuo(SHAPE_KEY, DIAMOND_VAL));
            add(new StyleDuo(SHAPE_ASPECT_KEY, DIAMOND_ASPECT_VAL));
            break;
        case ELLIPSE:
            add(new StyleDuo(SHAPE_KEY, ELLIPSE_VAL));
            break;
        case OVAL:
            add(new StyleDuo(SHAPE_KEY, RECTANGLE_VAL));
            add(new StyleDuo(ROUNDED_CORNERS_KEY, OVAL_CORNER_VAL));
            break;
        case RECTANGLE:
            add(new StyleDuo(SHAPE_KEY, RECTANGLE_VAL));
            add(new StyleDuo(ROUNDED_CORNERS_KEY, SQUARE_CORNER_VAL));
            break;
        case ROUNDED:
            add(new StyleDuo(SHAPE_KEY, RECTANGLE_VAL));
            add(new StyleDuo(ROUNDED_CORNERS_KEY, ROUND_CORNER_VAL));
            break;
        default:
            throw new IllegalArgumentException(
                "Default fall-thought in node shape! Did you add a new node shape?");
        }
    }

    private void convertEdgeEndShape(String key, EdgeEnd end) {
        switch (end) {
        case ARROW:
            add(new StyleDuo(key, ARROW_EDGE_END_VAL));
            break;
        case COMPOSITE:
            add(new StyleDuo(key, COMPOSITE_EDGE_END_VAL));
            break;
        case DOUBLE_LINE:
            // EDUARDO: Ask Arend about this one...
            break;
        case NESTING:
            // EDUARDO: Ask Arend about this one...
            break;
        case NONE:
            add(new StyleDuo(key, NONE_EDGE_END_VAL));
            break;
        case SIMPLE:
            add(new StyleDuo(key, SIMPLE_EDGE_END_VAL));
            break;
        case SUBTYPE:
            add(new StyleDuo(key, SUBTYPE_EDGE_END_VAL));
            break;
        case UNFILLED:
            // EDUARDO: Ask Arend about this one...
            break;
        default:
            throw new IllegalArgumentException(
                "Default fall-thought in node shape! Did you add a new edge end shape?");
        }
    }

    private static final String NEW_LINE = "\n";
    private static final String BEGIN_TIKZ_STYLE = "\\tikzstyle{";
    private static final String MID_TIKZ_STYLE = "}=";
    private static final String END_TIKZ_STYLE = NEW_LINE;

    private static final String SHAPE_KEY = "shape";
    private static final String DIAMOND_VAL = "diamond";
    private static final String ELLIPSE_VAL = "ellipse";
    private static final String RECTANGLE_VAL = "rectangle";

    private static final String ROUNDED_CORNERS_KEY = "rounded corners";
    private static final String SQUARE_CORNER_VAL = "0pt";
    private static final String ROUND_CORNER_VAL = JAttr.NORMAL_ARC_SIZE / 5
        + "pt";
    private static final String OVAL_CORNER_VAL = JAttr.STRONG_ARC_SIZE / 5
        + "pt";

    private static final String EDGE_SOURCE_END_KEY = "<";
    private static final String EDGE_TARGET_END_KEY = ">";
    private static final String ARROW_EDGE_END_VAL = "stealth";
    private static final String COMPOSITE_EDGE_END_VAL = "diamond";
    private static final String NONE_EDGE_END_VAL = "space";
    private static final String SIMPLE_EDGE_END_VAL = "to";
    private static final String SUBTYPE_EDGE_END_VAL = "open triangle 60";

    // Extra style entries.
    private static final String SHAPE_ASPECT_KEY = "shape aspect";
    private static final String DIAMOND_ASPECT_VAL = "2";

    private static final String HEADER =
        "% Package that defines the styles used in Tikz figures exported in GROOVE."
            + NEW_LINE
            + "% This file was automatically generated by the TikzStylesExtraction utility."
            + NEW_LINE + NEW_LINE + "\\ProvidesPackage{groove2tikz}" + NEW_LINE
            + "\\RequirePackage{tikz}" + NEW_LINE + "\\usepackage[T1]{fontenc}"
            + NEW_LINE + "\\usepackage{amssymb}" + NEW_LINE + NEW_LINE
            + "% Includes for Tikz." + NEW_LINE
            + "\\usetikzlibrary{arrows,automata,positioning,er}" + NEW_LINE;

    private static final String FOOTER = NEW_LINE
        + "% Ugly hack to allow nodes with multiple lines." + NEW_LINE
        + "\\newcommand{\\ml}[1]{" + NEW_LINE
        + "\\begin{tabular}{@{}c@{}}#1\\vspace{-2pt}\\end{tabular}" + NEW_LINE
        + "}" + NEW_LINE + NEW_LINE;

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
