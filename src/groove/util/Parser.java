/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id: Property.java,v 1.1 2008-01-30 09:32:03 iovka Exp $
 */
package groove.util;

import groove.gui.look.Line;
import groove.gui.look.Line.Style;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rudimentary parser functionality.
 * @param <T> the type being parsed
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public interface Parser<T> {
    /**
     * Returns a description of the parsable strings.
     */
    public Line getDescription();

    /**
     * Indicates if a given (possibly {@code null}) textual value can be parsed.
     */
    public boolean accepts(String text);

    /**
     * Converts a given (possibly {@code null}) textual value to an instance of the
     * type of this parser.
     * @param text the text to be parsed
     * @return a value corresponding to {@code text}; returns
     * {@link #getDefaultValue()} for {@code null} or empty input
     */
    public T parse(String text);

    /**
     * Turns a given value into a string that, when fed into {@link #parse(String)},
     * will return the original value.
     * @param value a non-{@code null} value of the type of this parser;
     * should satisfy {@link #isValue(Object)}
     */
    public String toParsableString(Object value);

    /**
     * Tests if a given value of the type of this parser can be
     * represented as a string that can be parsed back.
     */
    public boolean isValue(Object value);

    /**
     * Returns the default value, i.e., the value that the empty or
     * {@code null} string parses to.
     * May be {@code null}, so don't use {@link #equals(Object)} to test equality!
     */
    public T getDefaultValue();

    /**
     * Returns a human-readable string representation of the default value.
     * Note that the default string and the empty string both parse
     * to the default value.
     * @see #getDefaultValue()
     */
    public String getDefaultString();

    /** Tests whether a given value is the default value. */
    public boolean isDefault(Object value);

    /** Trimmed string parser. */
    public static StringParser trim = new StringParser(true);
    /** Identity string parser. */
    public static StringParser identity = new StringParser(false);
    /** Integer number parser. */
    public static IntParser integer = new IntParser(true);
    /** Natural number parser. */
    public static IntParser natural = new IntParser(false);
    /** Splitting parser based on whitespace. */
    public static SplitParser splitter = new SplitParser();
    /** Boolean parser with default value {@code false}. */
    public static BooleanParser boolTrue = new BooleanParser(true);
    /** Boolean parser with default value {@code true}. */
    public static BooleanParser boolFalse = new BooleanParser(false);

    /** Identity string parser. */
    static public class StringParser implements Parser<String> {
        private StringParser(boolean trim) {
            this.trim = trim;
        }

        private final boolean trim;

        @Override
        public boolean accepts(String text) {
            return true;
        }

        @Override
        public String parse(String text) {
            return text == null ? getDefaultValue() : this.trim ? text.trim() : text;
        }

        @Override
        public Line getDescription() {
            return Line.atom("Any string value");
        }

        @Override
        public String toParsableString(Object value) {
            return (String) value;
        }

        @Override
        public boolean isValue(Object value) {
            return value == null || value instanceof String;
        }

        @Override
        public String getDefaultValue() {
            return "";
        }

        @Override
        public String getDefaultString() {
            return "";
        }

        @Override
        public boolean isDefault(Object value) {
            return (value instanceof String) && ((String) value).length() == 0;
        }
    }

    /** Integer parser. */
    static public class IntParser implements Parser<Integer> {
        private IntParser(boolean neg) {
            this.neg = neg;
        }

        private final boolean neg;

        @Override
        public boolean accepts(String text) {
            if (text == null || text.length() == 0) {
                return true;
            }
            try {
                int number = Integer.parseInt(text);
                return this.neg || number >= 0;
            } catch (NumberFormatException ext) {
                return false;
            }
        }

        @Override
        public Integer parse(String text) {
            return text == null || text.length() == 0 ? getDefaultValue() : Integer.parseInt(text);
        }

        @Override
        public Line getDescription() {
            return Line.atom((this.neg ? "Integer value" : "Natural number") + " (default 0)");
        }

        @Override
        public String toParsableString(Object value) {
            return value == null ? null : value.toString();
        }

        @Override
        public boolean isValue(Object value) {
            return value == null || value instanceof Integer;
        }

        @Override
        public Integer getDefaultValue() {
            return 0;
        }

        @Override
        public String getDefaultString() {
            return "0";
        }

        @Override
        public boolean isDefault(Object value) {
            return value instanceof Integer && ((Integer) value).intValue() == 0;
        }
    }

    /** Parser that concatenates and splits lines at whitespaces. */
    static public class SplitParser implements Parser<List<String>> {
        @Override
        public boolean accepts(String text) {
            return true;
        }

        @Override
        public List<String> parse(String text) {
            return text == null || text.length() == 0 ? getDefaultValue()
                : Arrays.asList(text.trim().split("\\s"));
        }

        @Override
        public Line getDescription() {
            return Line.atom("A space-separated list of names");
        }

        @Override
        public String toParsableString(Object value) {
            return Groove.toString(((Collection<?>) value).toArray(), "", "", " ");
        }

        @Override
        public boolean isValue(Object value) {
            boolean result = value instanceof List;
            if (result) {
                for (Object part : (List<?>) value) {
                    if (!(part instanceof String) || ((String) part).indexOf(' ') >= 0) {
                        result = false;
                        break;
                    }
                }
            }
            return result;
        }

        @Override
        public List<String> getDefaultValue() {
            return Collections.<String>emptyList();
        }

        @Override
        public String getDefaultString() {
            return "";
        }

        @Override
        public boolean isDefault(Object value) {
            return value instanceof List && ((List<?>) value).size() == 0;
        }
    }

    /**
     * Parser for boolean values, with a default value for the empty string.
     * @author Arend Rensink
     * @version $Revision $
     */
    static public class BooleanParser implements Parser<Boolean> {
        /**
         * Constructs an instance that accepts the empty string as
         * a given default value.
         */
        public BooleanParser(boolean defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public Line getDescription() {
            Line result = Line.atom("Either ");
            result = result.append(TRUE_LINE);
            if (this.defaultValue) {
                result = result.append(" (default)");
            }
            result = result.append(" or ").append(FALSE_LINE);
            if (!this.defaultValue) {
                result = result.append(" (default)");
            }
            return result;
        }

        @Override
        public boolean accepts(String text) {
            return TRUE.equals(text) || FALSE.equals(text) || "".equals(text) || text == null;
        }

        @Override
        public Boolean parse(String text) {
            Boolean result = null;
            if (text == null || text.length() == 0) {
                result = getDefaultValue();
            } else if (TRUE.equals(text)) {
                result = true;
            } else if (FALSE.equals(text)) {
                result = false;
            }
            return result;
        }

        @Override
        public String toParsableString(Object value) {
            if (value.equals(getDefaultValue())) {
                return "";
            } else {
                return value.toString();
            }
        }

        @Override
        public boolean isValue(Object value) {
            return value instanceof Boolean;
        }

        @Override
        public Boolean getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String getDefaultString() {
            return "" + this.defaultValue;
        }

        @Override
        public boolean isDefault(Object value) {
            return value instanceof Boolean
                && ((Boolean) value).booleanValue() == this.defaultValue;
        }

        /** Value that the empty string converts to. */
        private final boolean defaultValue;

        /** Representation of <code>true</code>. */
        static private final String TRUE = Boolean.toString(true);
        static private final Line TRUE_LINE = Line.atom(TRUE).style(Style.ITALIC);
        /** Representation of <code>false</code>. */
        static private final String FALSE = Boolean.toString(false);
        static private final Line FALSE_LINE = Line.atom(FALSE).style(Style.ITALIC);
    }

    /**
     * Properties subclass that tests whether a given value is a correct value
     * of an {@link Enum} type (passed in as a type parameter).
     */
    static public class EnumParser<T extends Enum<T>> implements Parser<T> {
        /**
         * Constructs an instance with a flag to indicate if the empty string
         * should be approved.
         * @param enumType the enum type supported by this property
         * @param defaultValue the value of {@code T} represented
         * by the empty string
         */
        public EnumParser(Class<T> enumType, T defaultValue, String... texts) {
            this.enumType = enumType;
            this.defaultValue = defaultValue;
            this.toStringMap = new EnumMap<T,String>(enumType);
            this.toValueMap = new HashMap<String,T>();
            T[] values = enumType.getEnumConstants();
            assert values.length == texts.length;
            for (int i = 0; i < values.length; i++) {
                this.toStringMap.put(values[i], texts[i]);
                this.toValueMap.put(texts[i], values[i]);
            }
            this.toValueMap.put("", defaultValue);
            this.toValueMap.put(null, defaultValue);
        }

        /**
         * Constructs an instance with a flag to indicate if the empty string
         * should be approved.
         * @param enumType the enum type supported by this property
         * @param defaultValue if non-{@code null}, the value of {@code T} represented
         * by the empty string
         */
        public EnumParser(Class<T> enumType, T defaultValue) {
            this(enumType, defaultValue, camel(enumType.getEnumConstants()));
        }

        @Override
        public T getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String getDefaultString() {
            String result = this.toStringMap.get(this.defaultValue);
            return result == null ? "" : result;
        }

        @Override
        public boolean isDefault(Object value) {
            return value == this.defaultValue;
        }

        /** Flag indicating if the empty string is approved. */
        private final T defaultValue;
        /** The type of enum. */
        private final Class<T> enumType;

        @Override
        public Line getDescription() {
            Line result = Line.atom("One of ");
            T[] values = this.enumType.getEnumConstants();
            for (int i = 0; i < values.length; i++) {
                T val = values[i];
                result = result.append(Line.atom(this.toStringMap.get(val)).style(Style.ITALIC));
                if (isDefault(val)) {
                    result = result.append(" (default)");
                }
                if (i < values.length - 2) {
                    result = result.append(", ");
                } else if (i < values.length - 1) {
                    result = result.append(" or ");
                }
            }
            return result;
        }

        @Override
        public boolean accepts(String text) {
            return this.toValueMap.containsKey(text);
        }

        @Override
        public T parse(String text) {
            return this.toValueMap.get(text);
        }

        @Override
        public String toParsableString(Object value) {
            return isDefault(value) ? "" : this.toStringMap.get(value);
        }

        @Override
        public boolean isValue(Object value) {
            return this.toStringMap.containsKey(value);
        }

        private final Map<T,String> toStringMap;
        private final Map<String,T> toValueMap;

        private static final <T extends Enum<T>> String[] camel(T[] vals) {
            String[] result = new String[vals.length];
            for (int i = 0; i < vals.length; i++) {
                result[i] = Groove.camel(vals[i].name());
            }
            return result;
        }
    }
}
