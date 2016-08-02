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
package groove.util.line;

import static groove.io.HTMLConverter.HTML_TAG;
import static groove.io.HTMLConverter.createColorTag;
import static groove.io.HTMLConverter.createSpanTag;
import groove.gui.Options;
import groove.io.HTMLConverter;
import groove.io.HTMLConverter.HTMLTag;
import groove.util.Exceptions;
import groove.util.line.Line.ColorType;
import groove.util.line.Line.Style;

import java.awt.Color;
import java.awt.Font;

/**
 * HTML renderer for lines.
 * @author Arend Rensink
 * @version $Revision $
 */
public class HTMLLineFormat extends LineFormat<HTMLLineFormat.HTMLBuilder> {
    private HTMLLineFormat() {
        // empty
    }

    @Override
    public HTMLBuilder applyColored(ColorType type, Color color, HTMLBuilder subline) {
        HTMLTag colorTag = HTMLConverter.createColorTag(color);
        colorTag.on(subline.getResult());
        return subline;
    }

    @Override
    public HTMLBuilder applyStyled(Style style, HTMLBuilder subline) {
        HTMLTag tag;
        switch (style) {
        case BOLD:
            tag = HTMLConverter.STRONG_TAG;
            break;
        case ITALIC:
            tag = HTMLConverter.ITALIC_TAG;
            break;
        case UNDERLINE:
            tag = HTMLConverter.UNDERLINE_TAG;
            break;
        case STRIKE:
            tag = HTMLConverter.STRIKETHROUGH_TAG;
            break;
        case SUPER:
            tag = HTMLConverter.SUPER_TAG;
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
        tag.on(subline.getResult());
        return subline;
    }

    @Override
    public HTMLBuilder applyAtomic(String text) {
        HTMLBuilder result = createResult();
        StringBuilder content = result.getResult();
        content.append(text);
        HTMLConverter.toHtml(content);
        return result;
    }

    @Override
    public HTMLBuilder createHRule() {
        HTMLBuilder result = new HTMLBuilder();
        result.appendHRule();
        return result;
    }

    @Override
    public HTMLBuilder createResult() {
        return new HTMLBuilder();
    }

    /** Returns the singleton instance of this renderer. */
    public static HTMLLineFormat instance() {
        if (instance == null) {
            instance = new HTMLLineFormat();
        }
        return instance;
    }

    private static HTMLLineFormat instance;

    /** Puts an optional colour tag, font tag and an HTML tag around a given text. */
    public static String toHtml(StringBuilder text, Color color) {
        if (text.length() > 0) {
            if (color != null && !color.equals(Color.BLACK)) {
                createColorTag(color).on(text);
            }
            return HTML_TAG.on(HTMLConverter.CENTER_TAG.on(getFontTag().on(text)))
                .toString();
        } else {
            return "";
        }
    }

    private static HTMLTag getFontTag() {
        if (fontTag == null) {
            Font font = Options.getLabelFont();
            String face = font.getFamily();
            int size = font.getSize() - 2;
            // actually a slightly smaller font is more in line with
            // the edge font size, but then the forall symbol is not
            // available
            String argument = String.format("font-family:%s; font-size:%dpx", face, size);
            fontTag = createSpanTag(argument);
        }
        return fontTag;
    }

    /** HTML tag for the text display font. */
    private static HTMLTag fontTag;

    static class HTMLBuilder implements LineFormat.Builder<HTMLBuilder> {
        @Override
        public StringBuilder getResult() {
            return this.content;
        }

        @Override
        public boolean isEmpty() {
            return this.content.length() == 0;
        }

        @Override
        public void append(HTMLBuilder other) {
            this.content.append(other.content);
        }

        @Override
        public void appendLineBreak() {
            this.content.append(HTMLConverter.HTML_LINEBREAK);
        }

        /** Appends a horizontal rule to the content. */
        public void appendHRule() {
            this.content.append("<hr noshade>");
        }

        @Override
        public String toString() {
            return this.content.toString();
        }

        private final StringBuilder content = new StringBuilder();
    }
}
