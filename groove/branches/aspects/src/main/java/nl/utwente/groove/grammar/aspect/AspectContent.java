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
package nl.utwente.groove.grammar.aspect;

import static nl.utwente.groove.grammar.aspect.AspectParser.ASSIGN;
import static nl.utwente.groove.grammar.aspect.AspectParser.SEPARATOR;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.algebra.Constant;
import nl.utwente.groove.algebra.Operator;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.syntax.Assignment;
import nl.utwente.groove.algebra.syntax.ExprTree;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.grammar.aspect.AspectContent.ColorContent;
import nl.utwente.groove.grammar.aspect.AspectContent.ConstContent;
import nl.utwente.groove.grammar.aspect.AspectContent.ExprContent;
import nl.utwente.groove.grammar.aspect.AspectContent.IdContent;
import nl.utwente.groove.grammar.aspect.AspectContent.IntegerContent;
import nl.utwente.groove.grammar.aspect.AspectContent.LabelPatternContent;
import nl.utwente.groove.grammar.aspect.AspectContent.MultiplicityContent;
import nl.utwente.groove.grammar.aspect.AspectContent.NestedValueContent;
import nl.utwente.groove.grammar.aspect.AspectContent.NullContent;
import nl.utwente.groove.grammar.aspect.AspectContent.OpContent;
import nl.utwente.groove.grammar.type.LabelPattern;
import nl.utwente.groove.grammar.type.Multiplicity;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.util.Colors;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Keywords;
import nl.utwente.groove.util.Pair;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Content of an {@link Aspect}
 * @author Rensink
 * @version $Revision $
 */
public sealed interface AspectContent
    permits ColorContent, IntegerContent, MultiplicityContent, LabelPatternContent,
    NestedValueContent, ConstContent, ExprContent, OpContent, IdContent, NullContent {
    /** Retrieves the contained object. */
    Object get();

    /** Returns the kind of this content object. */
    ContentKind kind();

    /** Indicates if this is {@link NullContent}. */
    default boolean isNull() {
        return false;
    }

    /**
     * Returns a string description of this content that
     * can be parsed back by {@link ContentKind#parseContent(String, GraphRole)}
     */
    default String toParsableString() {
        return get().toString();
    }

    /** Returns a parsable string for the aspect
     * consisting of a given aspect kind and this content.
     * @param aspectKind the aspect kind
     * @return a string that, when given to {@link Aspect#newInstance(String, GraphRole)},
     * returns an aspect with this content
     */
    default String toParsableString(AspectKind aspectKind) {
        if (kind() == ContentKind.LEVEL || kind() == ContentKind.MULTIPLICITY) {
            return aspectKind.getName() + ASSIGN + toParsableString() + SEPARATOR;
        } else {
            return aspectKind.getPrefix() + toParsableString();
        }
    }

    /**
     * Relabels this content object by changing all
     * occurrences of a certain label into another.
     * @param oldLabel the label to be changed
     * @param newLabel the new value for {@code oldLabel}
     * @return a clone of this content with changed labels, or
     * the original content if {@code oldLabel} did not occur
     */
    default AspectContent relabel(TypeLabel oldLabel, TypeLabel newLabel) {
        return this;
    }

    /** Type of content that can be wrapped inside an aspect. */
    static public enum ContentKind {
        /** No content. The label text is not checked. */
        NONE {
            @Override
            Pair<NullContent,String> parse(String text, int pos,
                                           GraphRole role) throws FormatException {
                if (text.charAt(pos) != SEPARATOR) {
                    throw new FormatException("Suffix '%s' not allowed",
                        text.substring(pos, text.indexOf(SEPARATOR)));
                }
                return new Pair<>(parseContent("", role), text.substring(pos + 1));
            }

            @Override
            NullContent parseContent(String text, GraphRole role) throws FormatException {
                if (!text.isEmpty()) {
                    throw new FormatException("Malformed null content '%s'", text);
                }
                return new NullContent(this);
            }
        },
        /** Empty content: no text may precede or follow the separator. */
        EMPTY {
            @Override
            Pair<NullContent,String> parse(String text, int pos,
                                           GraphRole role) throws FormatException {
                if (text.charAt(pos) != SEPARATOR) {
                    throw new FormatException("Suffix '%s' not allowed",
                        text.substring(pos, text.indexOf(SEPARATOR)));
                }
                if (pos < text.length() - 1) {
                    throw new FormatException("Label text '%s' not allowed",
                        text.substring(pos + 1));
                }
                return new Pair<>(parseContent("", role), text.substring(pos + 1));
            }

            @Override
            NullContent parseContent(String text, GraphRole role) throws FormatException {
                if (!text.isEmpty()) {
                    throw new FormatException("Malformed null content '%s'", text);
                }
                return new NullContent(this);
            }
        },
        /** Quantifier level name. */
        LEVEL {
            @Override
            Pair<AspectContent,String> parse(String text, int pos,
                                             GraphRole role) throws FormatException {
                AspectContent content;
                int end = text.indexOf(SEPARATOR);
                assert end >= 0;
                content = switch (text.charAt(pos)) {
                case ASSIGN -> content = parseContent(text.substring(pos + 1, end), role);
                case SEPARATOR -> content = new NullContent(this);
                default -> throw new FormatException("Can't parse label");
                };
                return new Pair<>(content, text.substring(end + 1));
            }

            @Override
            IdContent parseContent(String text, GraphRole role) throws FormatException {
                if (text.isEmpty()) {
                    throw new FormatException("Empty quantifier level");
                }
                for (int i = 0; i < text.length(); i++) {
                    char c = text.charAt(i);
                    if (i == 0
                        ? !isValidFirstChar(c)
                        : !isValidNextChar(c)) {
                        throw new FormatException("Invalid quantifier level '%s'", text);
                    }
                }
                return new IdContent(this, text);
            }

        },
        /**
         * String constant, used in a typed value aspect.
         */
        STRING_LITERAL(Sort.STRING),
        /**
         * Boolean constant, used in a typed value aspect.
         */
        BOOL_LITERAL(Sort.BOOL),
        /**
         * Integer number constant, used in a typed value aspect.
         */
        INT_LITERAL(Sort.INT),
        /**
         * Real number constant, used in a typed value aspect.
         */
        REAL_LITERAL(Sort.REAL),
        /**
         * Multiplicity: either a single number,
         * or of the form {@code n..m} where {@code n<m} or {@code m=*}.
         */
        MULTIPLICITY {
            @Override
            Pair<MultiplicityContent,String> parse(String text, int pos,
                                                   GraphRole role) throws FormatException {
                if (text.charAt(pos) != ASSIGN) {
                    throw new FormatException("Malformed multiplicity", text);
                }
                int end = text.indexOf(SEPARATOR, pos);
                assert end >= 0;
                var content = parseContent(text.substring(pos + 1, end), role);
                return new Pair<>(content, text.substring(end + 1));
            }

            @Override
            MultiplicityContent parseContent(String text, GraphRole role) throws FormatException {
                return new MultiplicityContent(Multiplicity.parse(text));
            }
        },
        /**
         * Parameter content; either a number or empty (i.e.e, unnumbered).
         * This can be specified in two distinct ways:
         * <ul>
         * <li> In obsolete syntax: {@code par=$N:}, with {@code N=0} for an
         * unnumbered parameter and {@code N>0} for a parameter with number {@code N-1}
         * <li> In current syntax:  {@code par:} for an unnumbered parameter
         * or {@code par:M} for a parameter with number {@code M}
         * </ul>
         */
        PARAM {
            @Override
            Pair<AspectContent,String> parse(String text, int pos,
                                             GraphRole role) throws FormatException {
                assert text.indexOf(SEPARATOR) >= 0;
                // either the prefix is of the form par=$N: or par:M
                // in the first case, the parameter number is N-1
                String nrText = text.substring(pos + 1);
                switch (text.charAt(pos)) {
                case SEPARATOR: // case par:M
                    if (!nrText.isEmpty() && nrText.charAt(0) == PARAM_START_CHAR) {
                        throw new FormatException("Invalid parameter number %s", nrText);
                    }
                    break;
                case ASSIGN:
                    if (nrText.charAt(0) != PARAM_START_CHAR) {
                        throw new FormatException("Parameter number should start with '%s'",
                            "" + PARAM_START_CHAR);
                    }
                    if (nrText.charAt(nrText.length() - 1) != SEPARATOR) {
                        throw new FormatException("Parameter line should end with '%s'",
                            "" + SEPARATOR);
                    }
                    nrText = nrText.substring(0, nrText.length() - 1);
                    if (nrText.length() == 0) {
                        throw new FormatException("Invalid parameter number %s", nrText);
                    }
                    break;
                default:
                    throw new FormatException("Can't parse parameter");
                }
                return new Pair<>(parseContent(nrText, role), "");
            }

            /** If the text starts with a PARAM_START_CHAR, subtract 1 from the number. */
            @Override
            AspectContent parseContent(String text, GraphRole role) throws FormatException {
                AspectContent result;
                var exc = new FormatException("Invalid parameter number %s", text);
                if (text.isEmpty()) {
                    result = new NullContent(this);
                } else {
                    boolean obsolete = false;
                    int subtract = 0;
                    if (text.charAt(0) == PARAM_START_CHAR) {
                        obsolete = true;
                        subtract = 1;
                        text = text.substring(1);
                    }
                    int nr;
                    try {
                        nr = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        throw exc;
                    }
                    if (nr < 0) {
                        throw exc;
                    }
                    result = obsolete && nr == 0
                        ? new NullContent(this)
                        : new IntegerContent(this, nr - subtract);
                }
                return result;
            }
        },
        /**
         * Argument number.
         * The content is a non-negative value of type {@link Integer}.
         */
        NUMBER {
            @Override
            Pair<IntegerContent,String> parse(String text, int pos,
                                              GraphRole role) throws FormatException {
                assert text.indexOf(SEPARATOR) >= 0;
                if (text.charAt(pos) != SEPARATOR) {
                    throw new FormatException("Can't parse argument");
                }
                String nrText = text.substring(pos + 1);
                return new Pair<>(parseContent(nrText, role), "");
            }

            @Override
            IntegerContent parseContent(String text, GraphRole role) throws FormatException {
                int result;
                FormatException formatExc = new FormatException("Invalid argument number %s", text);
                try {
                    result = Integer.parseInt(text);
                } catch (NumberFormatException exc) {
                    throw formatExc;
                }
                if (result < 0) {
                    throw formatExc;
                }
                return new IntegerContent(this, result);
            }
        },
        /** Content must be a {@link NestedValue}. */
        NESTED {
            @Override
            Pair<NestedValueContent,String> parse(String text, int pos,
                                                  GraphRole role) throws FormatException {
                if (text.charAt(pos) != SEPARATOR) {
                    throw new FormatException("Can't parse quantifier nesting", text);
                }
                return new Pair<>(parseContent(text.substring(pos + 1), role), "");
            }

            @Override
            NestedValueContent parseContent(String text, GraphRole role) throws FormatException {
                NestedValue content = NestedValue.get(text);
                if (content == null) {
                    throw new FormatException("Can't parse quantifier nesting");
                }
                return new NestedValueContent(this, content);
            }
        },
        /** Colour name or RGB value. */
        COLOR {
            @Override
            Pair<ColorContent,String> parse(String text, int pos,
                                            GraphRole role) throws FormatException {
                if (text.charAt(pos) != SEPARATOR) {
                    throw new FormatException("Can't parse colour value");
                }
                return new Pair<>(parseContent(text.substring(pos + 1), role), "");
            }

            @Override
            ColorContent parseContent(String text, GraphRole role) throws FormatException {
                Color result = Colors.findColor(text);
                if (result == null) {
                    throw new FormatException("Can't parse '%s' as colour", text);
                }
                return new ColorContent(this, result);
            }
        },
        /** Node identifier. */
        NAME {
            @Override
            Pair<IdContent,String> parse(String text, int pos,
                                         GraphRole role) throws FormatException {
                if (text.charAt(pos) != SEPARATOR) {
                    throw new FormatException("Can't parse node name");
                }
                return new Pair<>(parseContent(text.substring(pos + 1), role), "");
            }

            @Override
            IdContent parseContent(String text, GraphRole role) throws FormatException {
                for (int i = 0; i < text.length(); i++) {
                    char c = text.charAt(i);
                    if (i == 0
                        ? !isValidFirstChar(c)
                        : !isValidNextChar(c)) {
                        throw new FormatException("Invalid node id '%s'", text);
                    }
                }
                if (text.length() == 0) {
                    throw new FormatException("Node id cannot be empty", text);
                }
                if (text.charAt(0) == '$' || text.equals(Keywords.SELF)) {
                    throw new FormatException("Reserved node id '%s'", text);
                }
                return new IdContent(this, text);
            }
        },
        /** Predicate (attribute) value. */
        TEST_EXPR {
            @Override
            Pair<ExprContent,String> parse(String text, int pos,
                                           GraphRole role) throws FormatException {
                if (text.charAt(pos) != SEPARATOR) {
                    throw new FormatException("Can't parse attribute predicate");
                }
                return new Pair<>(parseContent(text.substring(pos + 1), role), "");
            }

            @Override
            ExprContent parseContent(String text, GraphRole role) throws FormatException {
                return new ExprContent(this, Expression.parseTest(text));
            }
        },
        /** Let expression content. */
        LET_EXPR {
            @Override
            Pair<ExprContent,String> parse(String text, int pos,
                                           GraphRole role) throws FormatException {
                if (text.charAt(pos) != SEPARATOR) {
                    throw new FormatException("Can't parse let expression");
                }
                return new Pair<>(parseContent(text.substring(pos + 1), role), "");
            }

            @Override
            ExprContent parseContent(String text, GraphRole role) throws FormatException {
                return new ExprContent(this, Assignment.parse(text));
            }
        },

        /** Edge declaration content. */
        EDGE {
            @Override
            Pair<LabelPatternContent,String> parse(String text, int pos,
                                                   GraphRole role) throws FormatException {
                if (text.charAt(pos) != SEPARATOR) {
                    throw new FormatException("Can't parse edge pattern declaration");
                }
                return new Pair<>(parseContent(text.substring(pos + 1), role), "");
            }

            @Override
            LabelPatternContent parseContent(String text, GraphRole role) throws FormatException {
                return new LabelPatternContent(this, LabelPattern.parse(text));
            }
        };

        /** Default, empty constructor (without sort). */
        private ContentKind() {
            this.sort = null;
        }

        /** Constructor for aspects of a given sort. */
        private ContentKind(Sort sort) {
            this.sort = sort;
        }

        /**
         * Tries to parse a given string, from a given position, as content
         * of this kind.
         * @param role graph role for which the content is parsed
         * @return a pair consisting of the resulting content value (which
         * may be {@code null} if there is, correctly, no content) and
         * the remainder of the input string
         * @throws FormatException if the input string cannot be parsed
         */
        Pair<? extends AspectContent,String> parse(String text, int pos,
                                                   GraphRole role) throws FormatException {
            // this implementation tries to find a literal of the
            // correct signature, or no content if the signature is not set
            assert text.indexOf(SEPARATOR, pos) >= 0;
            assert this.sort != null;
            if (text.charAt(pos) != SEPARATOR) {
                throw new FormatException("Prefix %s should be followed by '%s' in %s",
                    text.substring(0, pos), "" + SEPARATOR, text);
            }
            if (pos == text.length() - 1) {
                return new Pair<>(new NullContent(this), text.substring(pos + 1));
            } else {
                // the rest of the label should be a constant or operator
                // of the signature
                String value = text.substring(pos + 1);
                assert value != null;
                return new Pair<>(parseContent(value, role), "");
            }
        }

        /**
         * Tries to parse a given string as content of the correct kind.
         * @param role graph role for which the content is parsed
         * @return the resulting content value
         */
        AspectContent parseContent(@NonNull String text, GraphRole role) throws FormatException {
            AspectContent result;
            if (this.sort == null) {
                throw Exceptions.unsupportedOp("No content allowed");
            }
            if (role == GraphRole.TYPE) {
                // in a type graph, this is the declaration of an attribute
                assert text.length() > 0;
                boolean isIdent = Character.isJavaIdentifierStart(text.charAt(0));
                for (int i = 1; isIdent && i < text.length(); i++) {
                    isIdent = Character.isJavaIdentifierPart(text.charAt(i));
                }
                if (!isIdent) {
                    throw new FormatException("Attribute field '%s' must be identifier", text);
                }
                result = new IdContent(this, text);
            } else if (role == GraphRole.HOST) {
                // in a host graph, this is a constant
                result = new ConstContent(this, getSort().createConstant(text));
            } else {
                assert role == GraphRole.RULE;
                // in a rule graph, this is an expression or operator
                // first try for operator
                var op = getSort().getOperator(text);
                if (op != null) {
                    result = new OpContent(this, op);
                } else {
                    // then it must be an expression
                    ExprTree exprTree = Expression.parse(text);
                    // check if the expression has the appropriate type
                    exprTree.toExpression(getSort());
                    result = new ExprContent(this, exprTree);
                }
            }
            return result;
        }

        /**
         * Builds a string description of a given aspect kind and content
         * of this {@link ContentKind}.
        public String toString(AspectKind aspect, AspectContent content) {
            if (content == null) {
                return aspect.getPrefix();
            } else if (this == LEVEL || this == MULTIPLICITY) {
                return aspect.getName() + ASSIGN + content.toParsableString() + SEPARATOR;
            } else {
                return aspect.getPrefix() + content.toParsableString();
            }
        }
         */

        /** Indicates if this content kind has an associated sort. */
        public boolean hasSort() {
            return this.sort != null;
        }

        /** Returns the sort of this content kind, if any. */
        public Sort getSort() {
            return this.sort;
        }

        private final Sort sort;

        /**
         * Builds a string description of a given aspect kind and content
         * of this {@link ContentKind}.
        public String toString(AspectKind aspect, AspectContent content) {
            if (content == null) {
                return aspect.getPrefix();
            } else if (this == LEVEL || this == MULTIPLICITY) {
                return aspect.getName() + ASSIGN + content.toParsableString() + SEPARATOR;
            } else {
                return aspect.getPrefix() + content.toParsableString();
            }
        }
         */

        /**
         * Indicates if a given character is allowed as the first part of a name.
         * Delegates to {@link Character#isJavaIdentifierStart(char)}.
         */
        static private boolean isValidFirstChar(char c) {
            return Character.isJavaIdentifierStart(c);
        }

        /**
         * Indicates if a given character is allowed in a name names.
         * Delegates to {@link Character#isJavaIdentifierPart(char)}.
         */
        static private boolean isValidNextChar(char c) {
            return Character.isJavaIdentifierPart(c);
        }

        /** Start character of parameter strings. */
        static public final char PARAM_START_CHAR = '$';
        /** Reserved name "self". */
        static public final String SELF_NAME = "self";
    }

    /** Correct values of the {@link ContentKind#NESTED} content kind. */
    public static enum NestedValue {
        /** Embedding of one nesting level in another. */
        IN("in"),
        /** Assignment of a nesting level to a rule node. */
        AT("@"),
        /** Count of the number of matches of a universal quantifier. */
        COUNT("count");

        private NestedValue(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return this.text;
        }

        private final String text;

        /** Alternative symbol for {@link #AT}. */
        public static final String AT_SYMBOL = "at";

        /**
         * Returns the nested value corresponding to a certain non-{@code null}
         * name, or {@code null} if there is no such value.
         */
        public static NestedValue get(String name) {
            return nestedValueMap.get(name);
        }

        /** Static mapping from nested value texts to values. */
        private static final Map<String,NestedValue> nestedValueMap = new HashMap<>();

        static {
            // initialise the nested value map
            for (NestedValue value : NestedValue.values()) {
                NestedValue oldValue = nestedValueMap.put(value.toString(), value);
                assert oldValue == null;
            }
            nestedValueMap.put(NestedValue.AT_SYMBOL, NestedValue.AT);
        }
    }

    /** Abstract generic implementation of AspectContent. */
    static abstract class AAspectContent<T> {
        protected AAspectContent(ContentKind kind, T value) {
            this.kind = kind;
            this.value = value;
        }

        /** Returns the content kind. */
        public ContentKind kind() {
            return this.kind;
        }

        /** Returns the contained value. */
        public T get() {
            return this.value;
        }

        private final ContentKind kind;
        private final T value;

        /** Calls {@link #toString()} on the contained object. */
        @Override
        public String toString() {
            return get().toString();
        }

        @Override
        public int hashCode() {
            return Objects.hash(kind(), get());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            var other = (AAspectContent<?>) obj;
            return this.kind() == other.kind() && get().equals(other.get());
        }
    }

    /** Attribute aspect content consisting of an (algebraic) constant. */
    static public final class ConstContent extends AAspectContent<Constant>
        implements AspectContent {
        ConstContent(ContentKind kind, Constant cons) {
            super(kind, cons);
        }

        @Override
        public String toParsableString() {
            return get().getSymbol();
        }
    }

    /** Attribute aspect content consisting of an (algebraic) constant. */
    static public final class OpContent extends AAspectContent<Operator> implements AspectContent {
        OpContent(ContentKind kind, Operator op) {
            super(kind, op);
        }

        @Override
        public String toParsableString() {
            return get().getName();
        }
    }

    /** Attribute aspect content consisting of an expression tree. */
    static public final class ExprContent extends AAspectContent<ExprTree>
        implements AspectContent {
        ExprContent(ContentKind kind, ExprTree expr) {
            super(kind, expr);
        }

        @Override
        public ExprContent relabel(TypeLabel oldLabel, TypeLabel newLabel) {
            ExprTree result = get().relabel(oldLabel, newLabel);
            if (result == get()) {
                return this;
            } else {
                return new ExprContent(kind(), result);
            }
        }

        @Override
        public String toParsableString() {
            return get().getParseString();
        }
    }

    /** Attribute aspect content consisting of a field name. */
    static public final class IdContent extends AAspectContent<String> implements AspectContent {
        IdContent(ContentKind kind, String id) {
            super(kind, id);
        }

        @Override
        public AspectContent relabel(TypeLabel oldLabel, TypeLabel newLabel) {
            IdContent result = this;
            if (kind().sort != null) {
                // this is a field name
                if (oldLabel.getRole() == EdgeRole.BINARY && oldLabel.text().equals(get())) {
                    result = new IdContent(kind(), newLabel.text());
                }
            }
            return result;
        }

    }

    /** Aspect content consisting of an integer. */
    static public final class IntegerContent extends AAspectContent<Integer>
        implements AspectContent {
        IntegerContent(ContentKind kind, Integer value) {
            super(kind, value);
        }
    }

    /** Aspect content consisting of a colour. */
    static public final class ColorContent extends AAspectContent<Color> implements AspectContent {
        ColorContent(ContentKind kind, Color color) {
            super(kind, color);
        }

        @Override
        public String toParsableString() {
            Color color = get();
            int red = color.getRed();
            int green = color.getGreen();
            int blue = color.getBlue();
            int alpha = color.getAlpha();
            String colorString = alpha == 255
                ? "%s,%s,%s"
                : "%s,%s,%s,%s";
            return String.format(colorString, red, green, blue, alpha);
        }
    }

    /** Aspect content consisting of a multiplicity. */
    static public final class MultiplicityContent extends AAspectContent<Multiplicity>
        implements AspectContent {
        MultiplicityContent(Multiplicity mult) {
            super(ContentKind.MULTIPLICITY, mult);
        }
    }

    /** Aspect content consisting of a label pattern. */
    static public final class LabelPatternContent extends AAspectContent<LabelPattern>
        implements AspectContent {
        LabelPatternContent(ContentKind kind, LabelPattern pattern) {
            super(kind, pattern);
        }

        @Override
        public AspectContent relabel(TypeLabel oldLabel, TypeLabel newLabel) {
            LabelPattern newPattern = get().relabel(oldLabel, newLabel);
            return newPattern == get()
                ? this
                : new LabelPatternContent(kind(), newPattern);
        }
    }

    /** Aspect content consisting of a label pattern. */
    static public final class NestedValueContent extends AAspectContent<NestedValue>
        implements AspectContent {
        NestedValueContent(ContentKind kind, NestedValue value) {
            super(kind, value);
        }
    }

    /** Aspect content consisting of an object of unknown type. */
    static public final class NullContent implements AspectContent {
        NullContent(ContentKind kind) {
            this.kind = kind;
        }

        @Override
        public Void get() {
            // Should never be called on this content object
            throw new UnsupportedOperationException();
        }

        @Override
        public ContentKind kind() {
            return this.kind;
        }

        @Override
        public boolean isNull() {
            return true;
        }

        @Override
        public String toParsableString() {
            return "";
        }

        @Override
        public String toParsableString(AspectKind aspectKind) {
            return aspectKind.getPrefix();
        }

        private final ContentKind kind;

        @Override
        public int hashCode() {
            return kind().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NullContent other)) {
                return false;
            }
            return kind() == other.kind();
        }

        @Override
        public String toString() {
            return "";
        }
    }
}
