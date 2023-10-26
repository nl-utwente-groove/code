/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
 * $Id$
 */
package nl.utwente.groove.util.parse;

import static nl.utwente.groove.io.HTMLConverter.ITALIC_TAG;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.Strings;

/**
 * Abstract class for basic parser functionality.
 * @param <T> the type being parsed. It may be the case that not all values of type {@code T} are possible
 * outputs of the parser. A {@code T}-value is called <i>valid</i> if it is a possible output of the parser.
 * @author Arend Rensink
 * @version $Id $
 */
@NonNullByDefault
public interface Parser<T> {
    /**
     * Returns a HTML-formatted description of a parsable string, starting with uppercase.
     */
    public String getDescription();

    /**
     * Indicates if a given (possible {@code null}) textual value can be parsed.
     */
    default public boolean accepts(String text) {
        try {
            parse(text);
        } catch (FormatException exc) {
            return false;
        }
        return true;
    }

    /**
     * Converts a given (non-{@code null}) textual value to an instance of the
     * type of this parser.
     * @param input the text to be parsed; non-{@code null}
     * @return a value corresponding to {@code input}
     * @throws FormatException if the input string cannot be parsed
     */
    abstract public T parse(String input) throws FormatException;

    /**
     * Turns a given value into a string that, when fed into {@link #parse(String)},
     * will return the original value.
     * @param value a non-{@code null} value of the type of this parser;
     * should satisfy {@link #isValue(Object)}
     * @throws IllegalArgumentException if {@code value} is not a valid instance of type {@code T}
     */
    abstract public <V extends T> String unparse(@NonNull V value) throws IllegalArgumentException;

    /** Casts an object to a value of the target type of this parser, if possible.
     * This requires the object to be a valid value of this parser's target type {@code T},
     * which is checked (in the default implementation) by a call to {@link #isValid(Object)}.
     * @throws IllegalArgumentException if {@code value} is not a valid instance of type {@code T}
     */
    default public T cast(Object value) throws IllegalArgumentException {
        try {
            T result = getValueType().cast(value);
            if (!isValid(result)) {
                throw Exceptions.illegalArg("Value '%s' is not valid for this parser", result);
            }
            return result;
        } catch (ClassCastException exc) {
            throw Exceptions.illegalArg(exc.getMessage());
        }
    }

    /** Returns the value type that this parser generates, as a class object. */
    abstract public Class<? extends T> getValueType();

    /**
     * Checks if a given object is a valid value of the type of this parser
     * (where a value is considered <i>valid</i> if it can be the result of a parsing operation).
     * (Convenience method: the default implementation calls {@link #cast(Object)} for the actual check.)
     */
    default public boolean isValue(Object value) {
        try {
            cast(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /** Indicates if a given value is valid in terms of this parser;
     * i.e., if it has a corresponding parsable string.
     * The default implementation always returns <code>true</code>.
     * @param value the value being checked
     * @return {@code true} if {@code value} is valid in terms of this parser
     */
    default public boolean isValid(@NonNull T value) {
        return true;
    }

    /** Indicates if this parser has a default value.
     * The default value is what the empty string gets parsed to.
     * @return {@code true} if the parser has a default value
     */
    default public boolean hasDefault() {
        try {
            getDefaultValue();
            return true;
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }

    /**
     * Tests whether a given value is the default value.
     * Always returns {@code false} if the parse has no default value.
     */
    default public boolean isDefault(Object value) {
        try {
            return getDefaultValue().equals(value);
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }

    /** Tests if a given string represents the default value.
     * This is tested by parsing the string and calling {@link #isDefault(Object)}.
     * If the string is not parsable, this method returns {@code false}.
     * @param text the string value to be checked
     */
    default public boolean parsesToDefault(String text) {
        try {
            return isDefault(parse(text));
        } catch (FormatException exc) {
            return false;
        }
    }

    /**
     * Returns the (non-{@code null}) default value of this parser, i it has one.
     * The default value is what the empty string is parsed to.
     * @return the default value of this parser.
     * @throws UnsupportedOperationException if this parser has no default value
     */
    abstract public T getDefaultValue() throws UnsupportedOperationException;

    /** Path parser. */
    PathParser path = new PathParser();
    /** Integer number parser. */
    IntParser integer = new IntParser(true);
    /** Natural number parser. */
    IntParser natural = new IntParser(false);
    /** Splitting parser based on whitespace. */
    SplitParser<String> splitter = new SplitParser<>(StringParser.identity());
    /** Boolean parser with default value {@code false}. */
    BooleanParser boolTrue = new Parser.BooleanParser(true);
    /** Boolean parser with default value {@code true}. */
    BooleanParser boolFalse = new Parser.BooleanParser(false);

    /** Bare-bones abstract implementation of a {@link Parser}. */
    abstract public class AParser<T> implements Parser<T> {
        /**
         * Constructs a parser with a given description of its parsable strings.
         * Whether the parser has a default value will depend on whether the empty string parses
         * without format exceptions
         * @param description HTML-formatted description of a parsable string, starting with uppercase.
         * May be {@code null}, in which case the description will be obtained through {@link #createDescription()}
         * immediately after construction.
         * @param valueType the value type of this parser
         */
        protected AParser(@Nullable String description, Class<? extends T> valueType) {
            this.description = description;
            this.valueType = valueType;
        }

        /**
         * Constructs a parser with a given description of its parsable strings.
         * @param description HTML-formatted description of a parsable string, starting with uppercase.
         * May be {@code null}, in which case the description will be obtained through {@link #createDescription()}
         * immediately after construction.
         * @param defaultValue the explicit default value of this parser
         */
        @SuppressWarnings("unchecked")
        protected AParser(@Nullable String description, T defaultValue) {
            this.description = description;
            this.valueType = (Class<? extends T>) defaultValue.getClass();
            this.defaultValue = Optional.of(defaultValue);
        }

        /**
         * Callback method to generate the parser description
         * in case it has not been initialised in the constructor.
         */
        protected String createDescription() {
            throw Exceptions.unsupportedOp("Description should have been set in the constructor");
        }

        @Override
        final public String getDescription() {
            var result = this.description;
            if (result == null) {
                this.description = result = createDescription();
            }
            return result;
        }

        private @Nullable String description;

        @Override
        final public Class<? extends T> getValueType() {
            return this.valueType;
        }

        /** The value type that this parser generates, as a class object. */
        private final Class<? extends T> valueType;

        @Override
        public T getDefaultValue() {
            var result = this.defaultValue;
            if (result == null) {
                try {
                    result = Optional.of(parse(""));
                } catch (FormatException e) {
                    result = Optional.empty();
                }
                assert result != null;
                this.defaultValue = result;
            }
            return result
                .orElseThrow(() -> Exceptions
                    .unsupportedOp("This parser does not have a default value"));
        }

        private @Nullable Optional<T> defaultValue;
    }

    /** Parser constructed by wrapping/unwrapping the values of wrapping another ('inner') parser.
     * @param <T> Type produced by this (wrapped) parser
     */
    public class Wrap<T> extends AParser<T> {
        /**
         * Constructs a wrapped parser.
         * @param inner the inner parser
         * @param wrap conversion function from inner to outer type
         * @param unwrap conversion function from outer to inner type
         */
        public <I> Wrap(Parser<I> inner, Class<? extends T> valueType, Function<I,T> wrap,
                        Function<T,I> unwrap) {
            super(inner.getDescription(), valueType);
            this.inner = new Inner<>(inner, valueType, wrap, unwrap);
        }

        private final Inner<?> inner;

        @Override
        public T parse(String input) throws FormatException {
            return this.inner.parse(input);
        }

        @Override
        public <V extends T> String unparse(V value) {
            return this.inner.unparse(value);
        }

        @Override
        public boolean isValid(T value) {
            return this.inner.isValid(value);
        }

        private class Inner<I> extends AParser<T> {
            /**
             * Constructs a wrapped parser.
             * @param inner the inner parser
             * @param wrap conversion function from inner to outer type
             * @param unwrap conversion function from outer to inner type
             */
            public Inner(Parser<I> inner, Class<? extends T> valueType, Function<I,T> wrap,
                         Function<T,I> unwrap) {
                super(inner.getDescription(), valueType);
                this.inner = inner;
                this.wrap = wrap;
                this.unwrap = unwrap;
            }

            private final Parser<I> inner;
            private final Function<I,T> wrap;
            private final Function<T,I> unwrap;

            @Override
            public T parse(String input) throws FormatException {
                return wrap(this.inner.parse(input));
            }

            @Override
            public <V extends T> String unparse(V value) {
                return this.inner.unparse(unwrap(value));
            }

            @Override
            public boolean isValid(T value) {
                return this.inner.isValid(unwrap(value));
            }

            /** Wraps a value of the inner type into the type produced by this parser. */
            public T wrap(I value) {
                return this.wrap.apply(value);
            }

            /** Unwraps a value as produced by this parser into a value of the inner type. */
            public I unwrap(T value) {
                return this.unwrap.apply(value);
            }
        }
    }

    /** Integer parser. */
    class IntParser extends AParser<Integer> {
        /** Creates a parser, with a parameter to determine if
         * negative values are allowed.
         * @param neg if {@code true}, the parser allows negative values.
         */
        protected IntParser(boolean neg) {
            super(neg
                ? "Integer value"
                : "Natural number", 0);
            this.neg = neg;
        }

        /**
         * Indicates if negative numbers are allowed.
         */
        final protected boolean allowsNeg() {
            return this.neg;
        }

        private final boolean neg;

        @Override
        public Integer parse(String input) throws FormatException {
            if (input.isEmpty()) {
                return getDefaultValue();
            } else {
                try {
                    return Integer.parseInt(input);
                } catch (NumberFormatException exc) {
                    throw new FormatException(exc.getMessage());
                }
            }
        }

        @Override
        public boolean isValid(Integer value) {
            return allowsNeg() || value >= 0;
        }

        @Override
        public String unparse(Integer value) {
            return value.toString();
        }
    }

    /** Parser for path values. Does not support a default value. */
    class PathParser extends AParser<Path> {
        private PathParser() {
            super("OS-specific file path", Path.of(""));
        }

        @Override
        public Path parse(String input) throws FormatException {
            try {
                return Paths.get(input);
            } catch (InvalidPathException exc) {
                throw new FormatException(exc.getMessage());
            }
        }

        @Override
        public String unparse(Path value) {
            return value.toString();
        }
    }

    /** Parser that concatenates values from an inner parsers and splits lines at whitespaces. */
    class SplitParser<T> extends AParser<List<T>> {
        /** Constructs a parser. */
        public SplitParser(Parser<T> inner) {
            super("Space-separated list of " + Strings.toLower(inner.getDescription()) + " values",
                  Collections.<T>emptyList());
            this.inner = inner;
        }

        /** The inner parser. */
        private final Parser<T> inner;

        @Override
        public List<T> parse(String input) throws FormatException {
            List<T> result;
            if (input.isBlank()) {
                result = getDefaultValue();
            } else {
                result = new ArrayList<>();
                for (String line : handler.split(input, " ")) {
                    result.add(this.inner.parse(line));
                }
            }
            return result;
        }

        @Override
        public <V extends List<T>> String unparse(V value) {
            List<String> strings = value.stream().map(this.inner::unparse).toList();
            return Groove.toString(strings.toArray(), "", "", " ");
        }

        @Override
        public boolean isValid(List<T> value) {
            return value.stream().allMatch(this.inner::isValid);
        }

        /** String parser recognising no quotes or brackets. */
        private static StringHandler handler = new StringHandler("");
    }

    /**
     * Parser for boolean values, with a default value for the empty string.
     * @author Arend Rensink
     * @version $Revision $
     */
    class BooleanParser extends AParser<Boolean> {
        /**
         * Constructs an instance that accepts the empty string as standing for
         * a given default value.
         */
        public BooleanParser(boolean defaultValue) {
            super(createDescription(defaultValue), defaultValue);
        }

        static private String createDescription(boolean defaultValue) {
            StringBuffer result = new StringBuffer("Either ");
            result.append(HTMLConverter.ITALIC_TAG.on(TRUE));
            if (defaultValue) {
                result.append(" (default)");
            }
            result.append(" or ").append(HTMLConverter.ITALIC_TAG.on(FALSE));
            if (!defaultValue) {
                result.append(" (default)");
            }
            return result.toString();
        }

        @Override
        public Boolean parse(String input) throws FormatException {
            Boolean result;
            if (input.length() == 0) {
                result = getDefaultValue();
            } else if (TRUE.equals(input)) {
                result = true;
            } else if (FALSE.equals(input)) {
                result = false;
            } else {
                throw new FormatException("'%s' is not a valid boolean", input);
            }
            return result;
        }

        @Override
        public String unparse(Boolean value) {
            if (value.equals(getDefaultValue())) {
                return "";
            } else {
                return value.toString();
            }
        }

        /** Representation of <code>true</code>. */
        static private final String TRUE = Boolean.toString(true);
        /** Representation of <code>false</code>. */
        static private final String FALSE = Boolean.toString(false);
    }

    /**
     * Parser subclass that tests whether a given value is a correct value
     * of an {@link Enum} type (passed in as a type parameter).
     */
    class EnumParser<T extends Enum<T>> extends AParser<T> {
        /**
         * Constructs an instance with a given textual representations of the values, and without default value.
         * @param enumType the enum type supported by this property
         * by the empty string
         * @param texts textual values that will be parsed to the corresponding enum values.
         * A {@code null} element in the texts means that the corresponding enum value is considered invalid.
         */
        public EnumParser(Class<T> enumType, String... texts) {
            super(null, enumType);
            this.toStringMap = new EnumMap<>(enumType);
            this.toValueMap = new HashMap<>();
            T[] values = enumType.getEnumConstants();
            assert values.length == texts.length;
            for (int i = 0; i < values.length; i++) {
                var text = texts[i];
                if (text != null) {
                    this.toStringMap.put(values[i], text);
                    var oldValue = this.toValueMap.put(text, values[i]);
                    assert oldValue == null : "Duplicate key " + texts[i];
                }
            }
        }

        /**
         * Constructs an instance with an default value and given textual representations of the values.
         * @param enumType the enum type supported by this property
         * @param defaultValue the value of {@code T} represented
         * by the empty string
         * @param texts textual values that will be parsed to the corresponding enum values.
         * A {@code null} element in the texts means that the corresponding enum value is considered invalid.
         */
        public EnumParser(Class<T> enumType, @NonNull T defaultValue, String... texts) {
            this(enumType, texts);
            this.toValueMap.put("", defaultValue);
        }

        /**
         * Constructs an instance without default value.
         * @param enumType the enum type supported by this property
         */
        public EnumParser(Class<T> enumType) {
            this(enumType, toCamel(enumType.getEnumConstants()));
        }

        /**
         * Constructs an instance with a default value.
         * @param enumType the enum type supported by this property
         * @param defaultValue the value of {@code T} represented
         * by the empty string
         */
        public EnumParser(Class<T> enumType, @NonNull T defaultValue) {
            this(enumType, defaultValue, toCamel(enumType.getEnumConstants()));
        }

        @Override
        public T parse(String input) throws FormatException {
            var result = this.toValueMap.get(input);
            if (result == null) {
                throw new FormatException("Unknown value '%s'", input);
            }
            return result;
        }

        @Override
        public <V extends @NonNull T> String unparse(V value) {
            var result = this.toStringMap.get(value);
            if (result == null) {
                throw Exceptions
                    .illegalArg("'%s' is not valid in this use of %s", value, getValueType());
            }
            return result;
        }

        @Override
        public boolean isValid(@NonNull T value) {
            return this.toStringMap.get(value) != null;
        }

        private final Map<T,@Nullable String> toStringMap;
        private final Map<String,@Nullable T> toValueMap;

        @Override
        protected String createDescription() {
            StringBuffer result = new StringBuffer("One of ");
            int i = 0;
            for (var entry : this.toStringMap.entrySet()) {
                var value = entry.getKey();
                var text = entry.getValue();
                assert text != null;
                result = result.append(ITALIC_TAG.on(text));
                if (isDefault(value)) {
                    result.append(" (default)");
                }
                i++;
                if (i < this.toStringMap.size() - 1) {
                    result.append(", ");
                } else if (i < this.toStringMap.size()) {
                    result.append(" or ");
                }
            }
            return result.toString();
        }

        private static final <T extends Enum<T>> String[] toCamel(T[] vals) {
            String[] result = new String[vals.length];
            for (int i = 0; i < vals.length; i++) {
                result[i] = Strings.toCamel(vals[i].name());
            }
            return result;
        }
    }

    /** Parser constructed by wrapping the values of another (non-defaulting) "inner" parser into {@link Optional}s
     * and using the empty {@link Optional} as a default value.
     * @param <T> Type produced by this (wrapped) parser
     */
    class OptionalParser<T> extends AParser<Optional<T>> {
        /**
         * Constructs a wrapped parser.
         * @param inner the inner parser
         */
        public OptionalParser(Parser<T> inner) {
            super("Optional" + Strings.toLower(inner.getDescription()), Optional.empty());
            this.inner = inner;
        }

        private final Parser<T> inner;

        /** Returns the inner parser of this wrap-parser. */
        final public Parser<T> inner() {
            return this.inner;
        }

        @Override
        public Optional<T> parse(String input) throws FormatException {
            return input.isEmpty()
                ? Optional.empty()
                : Optional.of(this.inner.parse(input));
        }

        @Override
        public <V extends Optional<T>> String unparse(V value) {
            return value.map(this.inner::unparse).orElse("");
        }
    }

    /** Returns a parser, based on given parsing and unparsing functions.
     * It is required that parsing never throws exceptions.
     */
    public static <T extends Fallible> Parser<T> newParser(String description,
                                                           Class<? extends T> valueType,
                                                           Function<String,@NonNull T> parse,
                                                           Function<@NonNull T,String> unparse) {
        return new AParser<>(description, valueType) {
            @Override
            public @NonNull T parse(String input) throws FormatException {
                var result = parse.apply(input);
                result.getErrors().throwException();
                return result;
            }

            @Override
            public <V extends T> String unparse(V value) throws IllegalArgumentException {
                if (value.hasErrors()) {
                    throw Exceptions.illegalArg("Valye has errors: %s", value.getErrors());
                }
                return unparse.apply(value);
            }
        };
    }
}
