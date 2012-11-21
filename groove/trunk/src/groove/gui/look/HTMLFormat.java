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
package groove.gui.look;

import static groove.io.HTMLConverter.HTML_TAG;
import static groove.io.HTMLConverter.createColorTag;
import static groove.io.HTMLConverter.createSpanTag;
import groove.gui.look.Line.Style;
import groove.io.HTMLConverter;
import groove.io.HTMLConverter.HTMLTag;

import java.awt.Color;
import java.awt.Font;

import org.jgraph.graph.GraphConstants;

/**
 * HTML renderer for lines.
 * @author Arend Rensink
 * @version $Revision $
 */
public class HTMLFormat extends LineFormat {
    private HTMLFormat() {
        // empty
    }

    @Override
    protected String getLineBreak() {
        return HTMLConverter.HTML_LINEBREAK;
    }

    @Override
    public StringBuilder applyColored(Color color, StringBuilder subline) {
        HTMLTag colorTag = HTMLConverter.createColorTag(color);
        return colorTag.on(subline);
    }

    @Override
    public StringBuilder applyStyled(Style style, StringBuilder subline) {
        HTMLTag tag;
        switch (style) {
        case BOLD:
            tag = HTMLConverter.STRONG_TAG;
            break;
        case ITALIC:
            tag = HTMLConverter.ITALIC_TAG;
            break;
        case STRIKE:
            tag = HTMLConverter.STRIKETHROUGH_TAG;
            break;
        case SUPER:
            tag = HTMLConverter.SUPER_TAG;
            break;
        default:
            assert false;
            tag = null;
        }
        return tag.on(subline);
    }

    @Override
    public StringBuilder applyAtomic(String text) {
        return HTMLConverter.toHtml(new StringBuilder(text));
    }

    /** Returns the singleton instance of this renderer. */
    public static HTMLFormat instance() {
        return instance;
    }

    /** Puts an optional colour tag, a font tag and an HTML tag around a given text. */
    public static String toHtml(String text, Color color) {
        return toHtml(new StringBuilder(text), color);
    }

    /** Puts an optional colour tag, font tag and an HTML tag around a given text. */
    public static String toHtml(StringBuilder text, Color color) {
        if (text.length() > 0) {
            if (color != null && !color.equals(Color.BLACK)) {
                createColorTag(color).on(text);
            }
            return HTML_TAG.on(HTMLConverter.CENTER_TAG.on(fontTag.on(text))).toString();
        } else {
            return "";
        }
    }

    /** HTML tag for the text display font. */
    public static final HTMLTag fontTag;

    static {
        Font font = GraphConstants.DEFAULTFONT;
        String face;
        int size;
        if (font == null) {
            face = "SansSerif";
            size = -1;
        } else {
            face = font.getFamily();
            // actually a slightly smaller font is more in line with
            // the edge font size, but then the forall symbol is not
            // available
            size = font.getSize() - 2;
        }
        String argument =
            String.format("font-family:%s; font-size:%dpx", face, size);
        fontTag = createSpanTag(argument);
    }

    private static final HTMLFormat instance = new HTMLFormat();
}
