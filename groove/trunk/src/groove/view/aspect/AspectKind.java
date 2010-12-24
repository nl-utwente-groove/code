/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.view.aspect;

import groove.algebra.Algebras;
import groove.algebra.UnknownSymbolException;
import groove.view.FormatException;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Distinguishes the aspects that can be found in a plain graph representation
 * of a rule, host graph or type graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum AspectKind {
    /** Used for comments/documentation. */
    REMARK("rem"),

    // rule roles
    /** Indicates an unmodified element. */
    READER("use", ContentKind.LEVEL),
    /** Indicates an element to be deleted. */
    ERASER("del", ContentKind.LEVEL),
    /** Indicates an element to be created. */
    CREATOR("new", ContentKind.LEVEL),
    /** Indicates a forbidden element. */
    EMBARGO("not", ContentKind.LEVEL),

    // data types
    /** Indicates a data value of unknown type. */
    ATTR("attr"),
    /** Indicates a boolean value or operator. */
    BOOL("bool", ContentKind.BOOL_LITERAL),
    /** Indicates an integer value or operator. */
    INT("int", ContentKind.INT_LITERAL),
    /** Indicates a floating-point value or operator. */
    REAL("real", ContentKind.REAL_LITERAL),
    /** Indicates a string value or operator. */
    STRING("string", ContentKind.STRING_LITERAL),

    // auxiliary attribute-related aspects
    /** Indicates an argument edge. */
    ARGUMENT("arg", ContentKind.NUMBER),
    /** Indicates a product node. */
    PRODUCT("prod"),

    // rule parameters
    /** Indicates a bidirectional rule parameter. */
    PARAM_BI("par", ContentKind.PARAMETER),
    /** Indicates an input rule parameter. */
    PARAM_IN("parin", ContentKind.PARAMETER),
    /** Indicates an output rule parameter. */
    PARAM_OUT("parout", ContentKind.PARAMETER),

    // type-related aspects
    /** Indicates an abstract type. */
    ABSTRACT("abs"),
    /** Indicates a subtype relation. */
    SUBTYPE("sub"),

    // label-related aspects
    /** Indicates that the remainder of the label is a regular expression. */
    PATH("path"),
    /** Indicates that the remainder of the label is a literal. */
    LITERAL(""),

    // quantifier-related aspects
    /** Universal quantifier. */
    FORALL("forall", ContentKind.LEVEL),
    /** Non-vacuous universal quantifier. */
    FORALL_POS("forallx", ContentKind.LEVEL),
    /** Existential quantifier. */
    EXISTS("exists", ContentKind.LEVEL),
    /** Nesting edge. */
    NESTED("nested");

    /** Creates a new aspect kind, without content. */
    private AspectKind(String name) {
        this(name, ContentKind.NONE);
    }

    /** Creates a new aspect kind, with content of a given type. */
    private AspectKind(String name, ContentKind contentKind) {
        this.name = name;
        this.contentKind = contentKind;
        this.aspect = new NewAspect(this, contentKind);
    }

    /** Returns the name of this aspect kind. */
    public String getName() {
        return this.name;
    }

    /** 
     * Returns type of content of this aspect kind.
     * May be {@code null}, if no content kind is allowed.
     */
    public ContentKind getContentKind() {
        return this.contentKind;
    }

    /** Returns a (prototypical) aspect of this kind. */
    public NewAspect getAspect() {
        return this.aspect;
    }

    /** 
     * Indicates if this aspect is among the set of roles.
     * @see #roles 
     */
    public boolean isRole() {
        return roles.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of NAC elements.
     * @see #nac 
     */
    public boolean isNAC() {
        return nac.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of LHS element.
     * @see #lhs 
     */
    public boolean isLHS() {
        return lhs.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of RHS elements.
     * @see #rhs 
     */
    public boolean isRHS() {
        return rhs.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of type aspects.
     * @see #types 
     */
    public boolean isType() {
        return types.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of data aspects.
     * @see #data 
     */
    public boolean isData() {
        return data.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of meta-aspects.
     * @see #meta 
     */
    public boolean isMeta() {
        return meta.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of parameter aspects.
     * @see #params 
     */
    public boolean isParam() {
        return params.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of quantifiers.
     * @see #quantifier 
     */
    public boolean isQuantifier() {
        return quantifier.contains(this);
    }

    /** Indicates that this aspect kind is allowed to appear on edges. */
    public boolean isForEdge() {
        return !nodeOnly.contains(this);
    }

    /** Indicates that this aspect kind is allowed to appear on nodes. */
    public boolean isForNode() {
        return !edgeOnly.contains(this);
    }

    /** Indicates that this aspect kind is always the last on a label. */
    public boolean isLast() {
        return !series.contains(this);
    }

    private final ContentKind contentKind;
    private final String name;
    private final NewAspect aspect;

    /** 
     * Returns the aspect kind corresponding to a certain non-{@code null}
     * name, or {@code null} if there is no such aspect kind.
     */
    public static AspectKind parse(String name) {
        return aspectMap.get(name);
    }

    /** Static mapping from all aspect names to aspects. */
    private static final Map<String,AspectKind> aspectMap =
        new HashMap<String,AspectKind>();

    static {
        // initialise the aspect map
        for (AspectKind aspect : EnumSet.allOf(AspectKind.class)) {
            AspectKind oldAspect = aspectMap.put(aspect.toString(), aspect);
            assert oldAspect == null;
        }
    }

    /** Set of role aspects. */
    public static EnumSet<AspectKind> roles = EnumSet.of(ERASER, CREATOR,
        READER, EMBARGO);
    /** Set of role aspects appearing in NACs. */
    public static EnumSet<AspectKind> nac = EnumSet.of(READER, EMBARGO);
    /** Set of role aspects appearing in LHSs. */
    public static EnumSet<AspectKind> lhs = EnumSet.of(READER, ERASER);
    /** Set of role aspects appearing in RHSs. */
    public static EnumSet<AspectKind> rhs = EnumSet.of(READER, CREATOR);
    /** Set of typed data aspects. */
    public static EnumSet<AspectKind> types = EnumSet.of(ATTR, STRING, BOOL,
        INT, REAL);
    /** Set of data aspects, typed or untyped. */
    public static EnumSet<AspectKind> data =
        EnumSet.of(STRING, BOOL, INT, REAL);
    /** Set of meta-aspects, i.e., which do not reflect real graph structure. */
    public static EnumSet<AspectKind> meta = EnumSet.of(FORALL, FORALL_POS,
        EXISTS, NESTED, REMARK);
    /** Set of parameter aspects. */
    public static EnumSet<AspectKind> params = EnumSet.of(PARAM_BI, PARAM_IN,
        PARAM_OUT);
    /** Set of quantifier aspects, i.e., which do not reflect real graph structure. */
    public static EnumSet<AspectKind> quantifier = EnumSet.of(FORALL,
        FORALL_POS, EXISTS);

    /** Set of all aspects that can be used <i>only</i> on nodes. */
    public static EnumSet<AspectKind> nodeOnly = EnumSet.of(ATTR, PRODUCT);
    /** Set of all aspects that can be used <i>only</i> on edges. */
    public static EnumSet<AspectKind> edgeOnly = EnumSet.of(ARGUMENT, PARAM_BI,
        SUBTYPE, PATH, LITERAL, NESTED);
    /** Set of aspects that may be followed by others, when used in an edge label. */
    public static EnumSet<AspectKind> series = EnumSet.of(READER, ERASER,
        CREATOR, EMBARGO, FORALL, FORALL_POS, EXISTS);

    /** Type of content that can be wrapped inside an aspect. */
    enum ContentKind {
        /** No content. */
        NONE,
        /** Quantifier level name. */
        LEVEL {
            @Override
            String parseContent(String text) throws FormatException {
                if (!isValidFirstChar(text.charAt(0))) {
                    throw new FormatException(
                        "Invalid start character '%c' in name '%s'",
                        text.charAt(0), text);
                }
                for (int i = 1; i < text.length(); i++) {
                    char c = text.charAt(i);
                    if (!isValidNextChar(c)) {
                        throw new FormatException(
                            "Invalid character '%c' in name '%s'", c, text);
                    }
                }
                return text;
            }

            /**
             * Indicates if a given character is allowed in level names. Currently
             * allowed are: letters, digits, currency symbols, underscores and periods.
             * @param c the character to be tested
             */
            private boolean isValidFirstChar(char c) {
                return Character.isJavaIdentifierStart(c);
            }

            /**
             * Indicates if a given character is allowed in level names. Currently
             * allowed are: letters, digits, currency symbols, underscores and periods.
             * @param c the character to be tested
             */
            private boolean isValidNextChar(char c) {
                return Character.isJavaIdentifierPart(c);
            }
        },
        /** 
         * String constant, used in a typed value aspect. 
         */
        STRING_LITERAL,
        /** 
         * Boolean constant, used in a typed value aspect. 
         */
        BOOL_LITERAL("bool"),
        /**
         * Integer number constant, used in a typed value aspect. 
         */
        INT_LITERAL("int"),
        /** 
         * Real number constant, used in a typed value aspect. 
         */
        REAL_LITERAL("real"),
        /** 
         * Parameter number, starting with a dollar sign.
         * The content is a non-negative value of type {@link Integer}. 
         */
        PARAMETER {
            @Override
            Integer parseContent(String text) throws FormatException {
                int result;
                if (text.length() == 0
                    || text.charAt(0) != PARAMETER_START_CHAR) {
                    throw new FormatException(
                        "Parameter number '%s' should start with '%c'", text,
                        PARAMETER_START_CHAR);
                }
                try {
                    result = Integer.parseInt(text.substring(1));
                } catch (NumberFormatException exc) {
                    throw new FormatException("Invalid parameter number ", text);
                }
                return result;
            }

            /** Start character of parameter strings. */
            static private final char PARAMETER_START_CHAR = '$';
        },
        /** 
         * Argument number.
         * The content is a non-negative value of type {@link Integer}. 
         */
        NUMBER {
            @Override
            Integer parseContent(String text) throws FormatException {
                int result;
                try {
                    result = Integer.parseInt(text.substring(1));
                } catch (NumberFormatException exc) {
                    throw new FormatException("Invalid argument number ", text);
                }
                return result;
            }
        };

        /** Default, empty constructor. */
        private ContentKind() {
            this(null);
        }

        /** Constructor for literals of a given signature. */
        private ContentKind(String signature) {
            this.signature = signature;
        }

        /** 
         * Tries to parse a given string can be parsed as content of the correct kind. 
         * This implementation tries to parse the text as a constant of the 
         * given signature.
         * @return the resulting content value 
         */
        Object parseContent(String text) throws FormatException {
            if (this.signature == null) {
                throw new UnsupportedOperationException("No content allowed");
            } else {
                try {
                    if (!Algebras.isConstant(this.signature, text)) {
                        throw new FormatException(
                            "Signature '%s' has no constant %s",
                            this.signature, text);
                    }
                } catch (UnknownSymbolException e) {
                    assert false : String.format(
                        "Method called for unknown signature '%s'",
                        this.signature);
                    return null;
                }
            }
            return text;
        }

        /**
         * Builds a string description of a given aspect kind and content
         * of this {@link ContentKind}.
         */
        String toString(AspectKind aspect, Object content) {
            if (content == null) {
                return aspect.getName() + SEPARATOR;
            } else if (literals.contains(this)) {
                return aspect.getName() + SEPARATOR + content;
            } else {
                return aspect.getName() + ASSIGN + content + SEPARATOR;
            }
        }

        private final String signature;

        static private final char SEPARATOR = ':';
        static private final char ASSIGN = '=';
        static private final EnumSet<ContentKind> literals = EnumSet.of(
            STRING_LITERAL, BOOL_LITERAL, INT_LITERAL, REAL_LITERAL);
    }
}
