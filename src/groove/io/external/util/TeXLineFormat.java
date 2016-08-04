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

import groove.io.Util;
import groove.util.Duo;
import groove.util.Pair;
import groove.util.line.Line.ColorType;
import groove.util.line.Line.Style;
import groove.util.line.LineFormat;

import java.awt.Color;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * TeX renderer for node and edge labels.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TeXLineFormat extends LineFormat<TeXLineFormat.TeXBuilder> {
    private TeXLineFormat() {
        // empty
    }

    @Override
    public TeXBuilder applyColored(ColorType type, Color color, TeXBuilder subline) {
        Duo<String> marker = getColorMarker(type, color);
        subline.surround(marker, Mode.TEXT);
        return subline;
    }

    @Override
    public TeXBuilder applyStyled(Style style, TeXBuilder subline) {
        Pair<Duo<String>,Mode> marker = getStyleMarker(style);
        subline.surround(marker.one(), marker.two());
        return subline;
    }

    @Override
    public TeXBuilder applyAtomic(String text) {
        TeXBuilder result = createResult();
        for (char c : text.toCharArray()) {
            Pair<String,Mode> spec = getCharSpec(c);
            if (spec == null) {
                result.append("" + c, Mode.TEXT);
            } else {
                result.append(spec.one(), spec.two());
            }
        }
        return result;
    }

    /** Just returns the empty line for now. */
    @Override
    public TeXBuilder createHRule() {
        TeXBuilder result = new TeXBuilder();
        return result;
    }

    @Override
    public TeXBuilder createResult() {
        return new TeXBuilder();
    }

    /** Returns the singleton instance of this renderer. */
    public static TeXLineFormat instance() {
        return instance;
    }

    private static final TeXLineFormat instance = new TeXLineFormat();

    /** Returns the prefix and postfix to set text in a given character style,
     * together with a flag indicating if the pre-and postfix require math mode. */
    private static Pair<Duo<String>,Mode> getStyleMarker(Style style) {
        return styleMap.get(style);
    }

    /** Mapping from character styles to the pre- and postfix achieving that style. */
    private static final Map<Style,Pair<Duo<String>,Mode>> styleMap;

    static {
        Map<Style,Pair<Duo<String>,Mode>> result =
            new EnumMap<>(Style.class);
        for (Style style : Style.values()) {
            String start, end;
            Mode mode;
            switch (style) {
            case BOLD:
                start = "\\textbf{";
                end = "}";
                mode = Mode.TEXT;
                break;
            case ITALIC:
                start = "\\textit{";
                end = "}";
                mode = Mode.TEXT;
                break;
            case STRIKE:
                start = "\\sout{";
                end = "}";
                mode = Mode.TEXT;
                break;
            case UNDERLINE:
                start = "\\uline{";
                end = "}";
                mode = Mode.TEXT;
                break;
            case SUPER:
                start = "^{";
                end = "}";
                mode = Mode.MATH;
                break;
            default:
                start = end = null;
                mode = null;
                assert false;
            }
            result.put(style, Pair.newPair(Duo.newDuo(start, end), mode));
        }
        styleMap = result;
    }

    /** Returns a colour specification for a given colour. */
    private static Duo<String> getColorMarker(ColorType type, Color color) {
        String colorString;
        if (type == ColorType.RGB) {
            colorString =
                "\\color[RGB]{" + color.getRed() + "," + color.getGreen() + "," + color.getBlue()
                    + "}";
        } else {
            colorString = colorMap.get(type);
        }
        return Duo.newDuo("{" + colorString, "}");
    }

    /** Mapping from logical colours to LaTeX xcolor specifications. */
    private static final Map<ColorType,String> colorMap;

    static {
        Map<ColorType,String> result = new EnumMap<>(ColorType.class);
        for (ColorType type : ColorType.values()) {
            String color;
            switch (type) {
            case CREATOR:
            case EMBARGO:
            case ERASER:
            case REMARK:
                color = type.name().toLowerCase() + "_c";
                break;
            default:
                color = null;
            }
            result.put(type, "\\color{" + color + "}");
        }
        colorMap = result;
    }

    /**
     * For special characters (that cannot be inserted as-is in a LaTeX text)
     * returns a LaTeX string and a flag indicating whether the string should
     * set in math mode.
     */
    private static Pair<String,Mode> getCharSpec(char c) {
        return charMap.get(c);
    }

    /** Adds an entry to the {@link #charMap}. */
    private static void addChar(char c, String s, Mode mode) {
        charMap.put(c, Pair.newPair(s, mode));
    }

    private static Map<Character,Pair<String,Mode>> charMap =
        new HashMap<>();

    static {
        addChar(Util.EXISTS, "\\exists", Mode.MATH);
        addChar(Util.FORALL, "\\forall", Mode.MATH);
        addChar(Util.LC_PI, "\\pi", Mode.MATH);
        addChar(Util.THIN_SPACE, "\\;", Mode.MATH);
        addChar(Util.DT, "{\\blacktriangledown}", Mode.MATH);
        addChar(Util.UT, "{\\blacktriangle}", Mode.MATH);
        addChar(Util.LT, "{\\blacktriangleleft}", Mode.MATH);
        addChar(Util.RT, "{\\blacktriangleright}", Mode.MATH);
        addChar('<', "<", Mode.MATH);
        addChar('>', ">", Mode.MATH);
        addChar('{', "\\{", Mode.BOTH);
        addChar('}', "\\}", Mode.BOTH);
        addChar('[', "{[}", Mode.BOTH);
        addChar(']', "{]}", Mode.BOTH);
        addChar('-', "-", Mode.MATH);
        addChar('+', "+", Mode.BOTH);
        addChar('|', "|", Mode.MATH);
        addChar('\\', "\\backslash", Mode.MATH);
        addChar('~', "\\~{}", Mode.BOTH);
        addChar('&', "\\&", Mode.BOTH);
        addChar('%', "\\%", Mode.BOTH);
        addChar('_', "\\_", Mode.BOTH);
        addChar('#', "\\#", Mode.BOTH);
        addChar('$', "\\$", Mode.BOTH);
        addChar('^', "\\^{}", Mode.TEXT);
        addChar('\n', "\\\\", Mode.BOTH);
        for (char c = '0'; c <= '9'; c++) {
            addChar(c, "" + c, Mode.BOTH);
        }
    }

    static class TeXBuilder implements LineFormat.Builder<TeXBuilder> {
        /* Make sure the result does not need math mode. */
        @Override
        public StringBuilder getResult() {
            setTextMode();
            return this.content;
        }

        @Override
        public boolean isEmpty() {
            return this.content.length() == 0;
        }

        @Override
        public void append(TeXBuilder other) {
            if (isEmpty()) {
                this.content.append(other.content);
                this.requiresMath = other.requiresMath;
                this.providesMath = other.providesMath;
                this.mathOnly = other.mathOnly;
            } else {
                if (this.providesMath != other.requiresMath) {
                    this.content.append("$");
                }
                this.content.append(other.content);
                this.providesMath = other.providesMath;
                this.mathOnly &= other.mathOnly;
            }
        }

        @Override
        public void appendLineBreak() {
            append(TEX_LINEBREAK, Mode.TEXT);
        }

        /**
         * Appends a text (set in a certain mode) to this result.
         */
        void append(String text, Mode mode) {
            if (isEmpty()) {
                this.requiresMath = this.providesMath = this.mathOnly = (mode == Mode.MATH);
            } else if (this.providesMath ? mode == Mode.TEXT : mode == Mode.MATH) {
                // the math mode must change
                this.content.append("$");
                this.providesMath = !this.providesMath;
                this.mathOnly = false;
            }
            this.content.append(text);
        }

        /**
         * Surrounds the text with a pre- and postfix,
         * set in a certain mode.
         */
        void surround(Duo<String> marker, Mode mode) {
            boolean math = (mode == Mode.MATH);
            if (!isEmpty()) {
                if (math) {
                    if (!this.mathOnly) {
                        setTextMode();
                        this.content.insert(0, "\\text{");
                        this.content.append("}");
                    }
                } else {
                    setTextMode();
                }
            }
            this.content.insert(0, marker.one());
            this.content.append(marker.two());
            this.requiresMath = this.providesMath = this.mathOnly = math;
        }

        /**
         * Inserts math mode delimiters to that this result
         * neither requires nor provides math mode.
         */
        private void setTextMode() {
            if (this.requiresMath) {
                this.content.insert(0, "$");
                this.requiresMath = false;
            }
            if (this.providesMath) {
                this.content.append("$");
                this.providesMath = false;
            }
            this.mathOnly = false;
        }

        private final StringBuilder content = new StringBuilder();
        /** flag indicating that this result starts in math mode. */
        private boolean requiresMath;
        /** flag indicating that this result ends in math mode. */
        private boolean providesMath;
        /** Flag indicating that this result is in math mode throughout. */
        private boolean mathOnly;
        private static final String TEX_LINEBREAK = "\\\\";
    }

    /** Mode in which a character can be set. */
    private static enum Mode {
        /** Math mode only. */
        MATH,
        /** Text mode only. */
        TEXT,
        /** Either math or text mode. */
        BOTH;
    }
}
