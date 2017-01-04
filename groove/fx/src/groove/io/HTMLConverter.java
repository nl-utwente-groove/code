// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id$
 */
package groove.io;

import groove.gui.Options;
import groove.gui.look.Values;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

/**
 * Performs conversions to and from HTML code.
 * @author Arend Rensink
 * @version $Revision$
 */
public class HTMLConverter {
    /** Converts a string representation of a unicode hex char to a HTML encoding thereof. */
    public static String toHtml(char unicode) {
        return "&#" + ((int) unicode) + ";";
    }

    /**
     * Converts a piece of text to HTML by replacing special characters to their
     * HTML encodings.
     */
    static public String toHtml(Object text) {
        return toHtml(new StringBuilder(text.toString())).toString();
    }

    /**
     * Converts a piece of text to HTML by replacing special characters to their
     * HTML encodings.
     */
    static public StringBuilder toHtml(StringBuilder text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String html = null;
            switch (c) {
            case '/':
            case '<':
            case '>':
                html = toHtml(c);
                break;
            case '\n':
                html = HTML_LINEBREAK;
                break;
            case Util.DT:
                html = HTML_DT;
                break;
            case Util.UT:
                html = HTML_UT;
                break;
            case Util.LT:
                html = HTML_LT;
                break;
            case Util.RT:
                html = HTML_RT;
                break;
            default:
                if (c > 0xFF) {
                    html = toHtml(c);
                }
            }
            if (html != null) {
                text.replace(i, i + 1, html);
                i += html.length() - 1;
            }
        }
        return text;
    }

    private static final String HTML_UT;
    private static final String HTML_DT;
    private static final String HTML_RT;
    private static final String HTML_LT;

    static {
        HTMLTag symbolTag = null;
        if (Options.getSymbolFont() != null && Options.getSymbolFont() != Options.getLabelFont()) {
            Font font = Options.getSymbolFont();
            String face = font.getFamily();
            int size = font.getSize();
            // actually a slightly smaller font is more in line with
            // the edge font size, but then the forall symbol is not
            // available
            String argument = String.format("font-family:%s; font-size:%dpx", face, size);
            symbolTag = createSpanTag(argument);
        }
        HTML_UT = tagOn(symbolTag, Util.UT);
        HTML_DT = tagOn(symbolTag, Util.DT);
        HTML_LT = tagOn(symbolTag, Util.LT);
        HTML_RT = tagOn(symbolTag, Util.RT);
    }

    private static final String tagOn(HTMLTag tag, char c) {
        if (tag == null) {
            return toHtml(c);
        } else {
            return tag.on(toHtml(c));
        }
    }

    /**
     * Returns a hyperlink tag for the given URL.
     */
    static public HTMLTag createHyperlink(String url) {
        return new HTMLTag(LINK_TAG_NAME, "name", url);
    }

    /**
     * Returns an HTML tag embedder.
     */
    static public HTMLTag createHtmlTag(String tag) {
        return new HTMLTag(tag);
    }

    /**
     * Returns an HTML tag embedded with an argument string.
     */
    static public HTMLTag createHtmlTag(String tag, String attribute, String arguments) {
        return new HTMLTag(tag, attribute, arguments);
    }

    /**
     * Returns a span tag with a style argument.
     */
    static public HTMLTag createSpanTag(String arguments) {
        return new HTMLTag(SPAN_TAG_NAME, STYLE_ATTR_NAME, arguments);
    }

    /**
     * Returns a span tag with a style argument.
     */
    static public HTMLTag createDivTag(String arguments) {
        return new HTMLTag(DIV_TAG_NAME, STYLE_ATTR_NAME, arguments);
    }

    /**
     * Returns a HTML span tag that imposes a given colour on a text.
     */
    static public HTMLTag createColorTag(Color color) {
        HTMLTag result = colorTagMap.get(color);
        if (result == null) {
            StringBuffer arg = new StringBuffer();
            int red = color.getRed();
            int blue = color.getBlue();
            int green = color.getGreen();
            int alpha = color.getAlpha();
            arg.append("color: rgb(");
            arg.append(red);
            arg.append(",");
            arg.append(green);
            arg.append(",");
            arg.append(blue);
            arg.append(");");
            if (alpha != MAX_ALPHA) {
                // the following is taken from the internet; it is to make
                // sure that all html interpretations set the opacity correctly.
                double alphaFraction = ((double) alpha) / MAX_ALPHA;
                arg.append("float:left;filter:alpha(opacity=");
                arg.append((int) (100 * alphaFraction));
                arg.append(");opacity:");
                arg.append(alphaFraction);
                arg.append(";");
            }
            result = HTMLConverter.createSpanTag(arg.toString());
            colorTagMap.put(color, result);
        }
        return result;
    }

    /** Converts the first letter of a given string to upper- or lowercase. */
    static public String toUppercase(String text, boolean upper) {
        return toUppercase(new StringBuilder(text), upper).toString();
    }

    /** Converts the first letter of a given string to upper- or lowercase. */
    static public StringBuilder toUppercase(StringBuilder text, boolean upper) {
        Character firstChar = text.charAt(0);
        if (upper) {
            firstChar = Character.toUpperCase(firstChar);
        } else {
            firstChar = Character.toLowerCase(firstChar);
        }
        text.replace(0, 1, firstChar.toString());
        return text;
    }

    /**
     * Strips the color tags from the HTML line.
     * @param htmlLine the line to be striped
     * @return 1 if the line was blue, 2 if green, 3 if red and 0 otherwise.
     */
    public static int removeColorTags(StringBuilder htmlLine) {
        String originalLine = htmlLine.toString();
        int result = 0;
        if (!ERASER_TAG.off(htmlLine).equals(originalLine)) {
            result = 1;
        } else if (!CREATOR_TAG.off(htmlLine).equals(originalLine)) {
            result = 2;
        } else if (!EMBARGO_TAG.off(htmlLine).equals(originalLine)) {
            result = 3;
        } else if (!REMARK_TAG.off(htmlLine).equals(originalLine)) {
            result = 4;
        }
        return result;
    }

    /**
     * Strips the font tags from the HTML line.
     * @param htmlLine the line to be striped
     * @return 1 if the line was bold, 2 if the line was italic, 3 if the line
     *         was both bold and italic, and 0 otherwise.
     */
    public static int removeFontTags(StringBuilder htmlLine) {
        String originalLine = htmlLine.toString();
        int bold = 0;
        int italic = 0;
        if (!STRONG_TAG.off(htmlLine).equals(originalLine)) {
            bold = 1;
            originalLine = htmlLine.toString();
        }
        if (!ITALIC_TAG.off(htmlLine).equals(originalLine)) {
            italic = 2;
        }
        return bold + italic;
    }

    // The readable codes do not work on the Mac in some situations. Replaced
    // them with the numeric codes - this fixes it. -- Maarten
    /** Non-breaking space character. */
    static public final String NBSP = "&#160;";
    /** Name of the link tag (<code>a</code>). */
    static public final String LINK_TAG_NAME = "a";
    /** Name of the span tag (<code>span</code>). */
    static public final String SPAN_TAG_NAME = "span";
    /** Name of the span tag (<code>div</code>). */
    static public final String DIV_TAG_NAME = "div";
    /** Table tag. */
    static public final String TABLE_TAG_NAME = "table";
    /** Name of the span style attribute. */
    static public final String STYLE_ATTR_NAME = "style";
    /** Tag to horizontally centre multiline text. */
    static public final HTMLTag CENTER_TAG = new HTMLTag("center");
    /** HTML tag. */
    static public final HTMLTag HTML_TAG = new HTMLTag("html");
    /** Italic font tag. */
    static public final HTMLTag ITALIC_TAG = new HTMLTag("i");
    /** Font strikethrough tag. */
    static public final HTMLTag STRIKETHROUGH_TAG = new HTMLTag("s");
    /** Strong font tag. */
    static public final HTMLTag STRONG_TAG = new HTMLTag("strong");
    /** Subscript font tag. */
    static public final HTMLTag SUB_TAG = new HTMLTag("sub");
    /** Superscript font tag. */
    static public final HTMLTag SUPER_TAG = new HTMLTag("sup");
    /** Font underline tag. */
    static public final HTMLTag UNDERLINE_TAG = new HTMLTag("u");
    /** The <code>html</code> tag to insert a line break. */
    static public final String HTML_LINEBREAK = createHtmlTag("br").tagBegin;
    /** The <code>html</code> tag to insert a horizontal line. */
    static public final String HTML_HORIZONTAL_LINE = createHtmlTag("hr").tagBegin;

    /** Map from colours to HTML tags imposing the colour on a text. */
    private static final Map<Color,HTMLTag> colorTagMap = new HashMap<>();
    /** The maximum alpha value according to {@link Color#getAlpha()}. */
    private static final int MAX_ALPHA = 255;

    /** Blue colour tag. */
    public static final HTMLTag ERASER_TAG = createColorTag(Values.ERASER_FOREGROUND);
    /** Green colour tag. */
    public static final HTMLTag CREATOR_TAG = createColorTag(Values.CREATOR_FOREGROUND);
    /** Red colour tag. */
    public static final HTMLTag EMBARGO_TAG = createColorTag(Values.EMBARGO_FOREGROUND);
    /** Remark colour tag. */
    public static final HTMLTag REMARK_TAG = createColorTag(Values.REMARK_FOREGROUND);

    /**
     * Class that allows some handling of HTML text.
     */
    static public class HTMLTag {
        /** Constructs a tag with a given name. */
        public HTMLTag(String tag) {
            this.tagBegin = String.format("<%s>", tag);
            this.tagEnd = String.format("</%s>", tag);
        }

        /** Constructs a tag with a given name and attribute name/value. */
        public HTMLTag(String tag, String attrName, String attrValue) {
            this.tagBegin = String.format("<%s %s=\"%s\">", tag, attrName, toHtml(attrValue));
            this.tagEnd = String.format("</%s>", tag);
        }

        /**
         * Puts the tag around a given object description, and returns the
         * result. The description is assumed to be in HTML format.
         * @param text the object from which the description is to be abstracted
         */
        public String on(Object text) {
            return on(new StringBuilder(text.toString())).toString();
        }

        /**
         * Puts the tag around a given string builder, and returns the result.
         * The changes are implemented in the string builder itself, i.e., the
         * parameter is modified. The description is assumed to be in HTML
         * format.
         * @param text the string builder that is to be augmented with this tag
         */
        public StringBuilder on(StringBuilder text) {
            text.insert(0, this.tagBegin);
            text.append(this.tagEnd);
            return text;
        }

        /**
         * Puts the tag around a given string, first converting special HTML
         * characters if required, and returns the result.
         * @param text the object from which the description is to be abstracted
         * @param convert if true, text is converted to HTML first.
         */
        public String on(Object text, boolean convert) {
            if (convert) {
                return on(toHtml(new StringBuilder(text.toString()))).toString();
            } else {
                return on(text);
            }
        }

        /**
         * Puts the tag around the strings in a given array, and returns the
         * result. The description is assumed to be in HTML format.
         * @param text the array of objects from which the description is to be
         *        abstracted
         */
        public String[] on(Object[] text) {
            return on(text, false);
        }

        /**
         * Puts the tag around the strings in a given array, first converting
         * special HTML characters if required, and returns the result.
         * @param text the array of objects from which the description is to be
         *        abstracted
         * @param convert if true, text is converted to HTML first.
         */
        public String[] on(Object[] text, boolean convert) {
            String[] result = new String[text.length];
            for (int labelIndex = 0; labelIndex < text.length; labelIndex++) {
                result[labelIndex] = on(text[labelIndex], convert);
            }
            return result;
        }

        /**
         * Strips the HTML tags from the string given.
         * @param text the string to be analyzed.
         * @return the input string unmodified if it did not contain the the
         *         HTML tags or the string striped from the tags.
         */
        public String off(StringBuilder text) {
            int tagEndStart = text.indexOf(this.tagEnd);
            int tagBeginStart = text.indexOf(this.tagBegin);
            if (tagEndStart > -1 && tagBeginStart > -1) {
                int end = tagEndStart + this.tagEnd.length();
                text.replace(tagEndStart, end, "");
                end = tagBeginStart + this.tagBegin.length();
                text.replace(tagBeginStart, end, "");
            }
            return text.toString();
        }

        /**
         * Strips the HTML tags from the string given.
         * @param text the string to be analyzed.
         * @return the input string unmodified if it did not contain the the
         *         HTML tags or the string striped from the tags.
         */
        public String off(String text) {
            int tagEndStart = text.indexOf(this.tagEnd);
            int tagBeginStart = text.indexOf(this.tagBegin);
            if (tagEndStart > -1 && tagBeginStart > -1) {
                int end = tagEndStart + this.tagEnd.length();
                text = text.substring(0, tagEndStart) + text.substring(end);
                end = tagBeginStart + this.tagBegin.length();
                text = text.substring(0, tagBeginStart) + text.substring(end);
            }
            return text;
        }

        /** Start text of this tag. */
        public final String tagBegin;
        /** End text of this tag. */
        public String tagEnd;
    }

}