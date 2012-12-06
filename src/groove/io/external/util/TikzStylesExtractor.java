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

import groove.gui.look.Look;
import groove.gui.look.NodeShape;
import groove.gui.look.VisualKey;
import groove.gui.look.VisualMap;

/**
 * Class to automatically create a groove2tikz.sty file from the existing
 * {@link Look} enum.
 * 
 * @author Eduardo Zambon
 */
public final class TikzStylesExtractor {

    /**
     * Main method.
     */
    public static void main(String[] args) {
        TikzStylesExtractor extractor = new TikzStylesExtractor();
        extractor.print();
    }

    /** The builder that holds the Tikz string. */
    private final StringBuilder result;

    /**
     * The constructor is private. To perform the conversion just call the
     * main method.
     */
    private TikzStylesExtractor() {
        this.result = new StringBuilder();
    }

    private void append(String string) {
        this.result.append(string);
    }

    /*private void append(StringBuilder sb) {
        this.result.append(sb);
    }*/

    private void print() {
        for (Look look : Look.values()) {
            print(look);
        }
        System.out.println(this.result);
    }

    private void print(Look look) {
        append(NEW_LINE);
        append(BEGIN_TIKZ_STYLE);
        append(look.name().toLowerCase());
        append(MID_TIKZ_STYLE);
        VisualMap visualMap = look.getVisuals();
        for (VisualKey key : visualMap.keySet()) {
            Object value = visualMap.get(key);
            print(key, value);
        }
        // Remove the last comma.
        int l = this.result.length();
        this.result.delete(l - 1, l);
        append(END_TIKZ_STYLE);
    }

    private void print(VisualKey key, Object value) {
        String keyStr = null;
        String valStr = null;

        switch (key) {
        case ADORNMENT:
            break;
        case BACKGROUND:
            keyStr = FILL_KEY;
            valStr = convertColor(value);
            break;
        case COLOR:
            break;
        case DASH:
            keyStr = DENSELY_DASHED;
            break;
        case EDGE_SOURCE_LABEL:
            break;
        case EDGE_SOURCE_POS:
            break;
        case EDGE_SOURCE_SHAPE:
            break;
        case EDGE_TARGET_LABEL:
            break;
        case EDGE_TARGET_POS:
            break;
        case EDGE_TARGET_SHAPE:
            break;
        case EMPHASIS:
            break;
        case ERROR:
            break;
        case FONT:
            break;
        case FOREGROUND:
            keyStr = DRAW_KEY;
            valStr = convertColor(value);
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
            keyStr = SHAPE_KEY;
            valStr = convertNodeShape(value);
            break;
        case NODE_SIZE:
            break;
        case OPAQUE:
        case POINTS:
        case VISIBLE:
            // Not used.
            break;
        default:
            assert false : "Default fallthought in visual key.";
            break;
        }

        if (keyStr != null) {
            append(keyStr);
            if (valStr != null) {
                append(EQUAL + valStr);
            }
            append(COMMA);
        }
    }

    private static String convertColor(Object color) {
        return color.toString();
    }

    private static String convertNodeShape(Object value) {
        NodeShape shape = (NodeShape) value;
        switch (shape) {
        case DIAMOND:
            return "diamond";
        case ELLIPSE:
            return "circle";
        case OVAL:
            return "circle";
        case RECTANGLE:
            return "rectangle";
        case ROUNDED:
            return "rectangle";
        default:
            assert false : "Default fallthought in node shape.";
            return null;
        }
    }

    private static final String NEW_LINE = "\n";
    private static final String EQUAL = "=";
    private static final String COMMA = ",";
    private static final String BEGIN_TIKZ_STYLE = "\\tikzstyle{";
    private static final String MID_TIKZ_STYLE = "}" + EQUAL + "[";
    private static final String END_TIKZ_STYLE = "]" + NEW_LINE;

    private static final String DRAW_KEY = "draw";
    private static final String FILL_KEY = "fill";
    private static final String SHAPE_KEY = "shape";
    private static final String DENSELY_DASHED = "densely dashed";
}
