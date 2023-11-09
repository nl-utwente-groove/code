/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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

import static nl.utwente.groove.grammar.aspect.AspectParser.SEPARATOR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.annotation.Help;
import nl.utwente.groove.annotation.HelpMap;
import nl.utwente.groove.grammar.aspect.AspectContent.ContentKind;
import nl.utwente.groove.grammar.aspect.AspectContent.NestedValue;
import nl.utwente.groove.grammar.aspect.AspectParser.Status;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.Keywords;
import nl.utwente.groove.util.LazyFactory;
import nl.utwente.groove.util.Pair;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Distinguishes the aspects that can be found in a plain graph representation
 * of a rule, host graph or type graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum AspectKind {
    /** Used for comments/documentation. */
    REMARK(Category.REMARK, "rem", ContentKind.NONE),

    // rule roles
    /** Indicates an unmodified element. */
    READER(Category.ROLE, "use", ContentKind.LEVEL),
    /** Indicates an element to be deleted. */
    ERASER(Category.ROLE, "del", ContentKind.LEVEL),
    /** Indicates an element to be created. */
    CREATOR(Category.ROLE, "new", ContentKind.LEVEL),
    /** Indicates an element to be created if not yet present. */
    ADDER(Category.ROLE, "cnew", ContentKind.LEVEL),
    /** Indicates a forbidden element. */
    EMBARGO(Category.ROLE, "not", ContentKind.LEVEL),

    // data types
    /** Indicates a boolean value or operator. */
    BOOL(Category.SORT, Keywords.BOOL, ContentKind.BOOL_LITERAL),
    /** Indicates an integer value or operator. */
    INT(Category.SORT, Keywords.INT, ContentKind.INT_LITERAL),
    /** Indicates a floating-point value or operator. */
    REAL(Category.SORT, Keywords.REAL, ContentKind.REAL_LITERAL),
    /** Indicates a string value or operator. */
    STRING(Category.SORT, Keywords.STRING, ContentKind.STRING_LITERAL),

    // auxiliary attribute-related aspects
    /** Indicates an argument edge. */
    ARGUMENT(Category.ATTR, "arg", ContentKind.NUMBER),
    /** Indicates a product node. */
    PRODUCT(Category.ATTR, "prod", ContentKind.NONE),
    /** Indicates an attribute value. */
    TEST(Category.ATTR, "test", ContentKind.TEST_EXPR),
    /** Indicates an attribute change. */
    LET(Category.ATTR, "let", ContentKind.LET_EXPR),

    // rule parameters
    /** Indicates a bidirectional rule parameter. */
    PARAM_BI(Category.PARAM, Keywords.PAR, ContentKind.PARAM),
    /** Indicates an input rule parameter. */
    PARAM_IN(Category.PARAM, Keywords.PAR_IN, ContentKind.PARAM),
    /** Indicates an output rule parameter. */
    PARAM_OUT(Category.PARAM, Keywords.PAR_OUT, ContentKind.PARAM),
    /** Indicates an interactive rule parameter. */
    PARAM_ASK(Category.PARAM, Keywords.PAR_ASK, ContentKind.PARAM),

    // type-related aspects
    /** Indicates a nodified edge type. */
    EDGE(Category.EDGE, "edge", ContentKind.EDGE),
    /** Indicates an abstract type. */
    ABSTRACT(Category.TYPE, "abs", ContentKind.NONE),
    /** Indicates a subtype relation. */
    SUBTYPE(Category.TYPE, "sub", ContentKind.EMPTY),
    /** Indicates an imported type. */
    IMPORT(Category.IMPORT, "import", ContentKind.EMPTY),
    /** Indicates an incoming multiplicity. */
    MULT_IN(Category.MULT_IN, "in", ContentKind.MULTIPLICITY),
    /** Indicates an outgoing multiplicity. */
    MULT_OUT(Category.MULT_OUT, "out", ContentKind.MULTIPLICITY),
    /** Indicates an outgoing multiplicity. */
    COMPOSITE(Category.ASSOC, "part", ContentKind.NONE),

    // label-related aspects
    /** Default label mode, if none is specified. */
    ATOM(Category.LABEL, "none", ContentKind.NONE) {
        @Override
        public String getPrefix() {
            return "";
        }
    },
    /** Indicates that the remainder of the label is a regular expression. */
    PATH(Category.LABEL, "path", ContentKind.NONE),
    /** Indicates that the remainder of the label is to be taken as literal text. */
    LITERAL(Category.LABEL, "", ContentKind.NONE),

    // quantifier-related aspects
    /** Universal quantifier. */
    FORALL(Category.NESTING, "forall", ContentKind.LEVEL),
    /** Non-vacuous universal quantifier. */
    FORALL_POS(Category.NESTING, "forallx", ContentKind.LEVEL),
    /** Existential quantifier. */
    EXISTS(Category.NESTING, "exists", ContentKind.LEVEL),
    /** Optional existential quantifier. */
    EXISTS_OPT(Category.NESTING, "existsx", ContentKind.LEVEL),
    /** Nesting edge. */
    NESTED(Category.NESTING, "nested", ContentKind.NESTED),
    /** Connects two embargo sub-graphs. */
    CONNECT(Category.NESTING, "or", ContentKind.EMPTY),

    /** Node identity. */
    ID(Category.ID, "id", ContentKind.NAME),
    /** Node type colour. */
    COLOR(Category.COLOR, "color", ContentKind.COLOR);

    /** Creates a new aspect kind.
     * @param name the aspect kind name; will be appended with {@link #SEPARATOR} to form
     * the prefix
     * @param contentKind the kind of content for this aspect
     */
    private AspectKind(Category cat, String name, ContentKind contentKind) {
        this.cat = cat;
        this.name = name;
        this.contentKind = contentKind;
    }

    /** Returns the category to which this aspect belongs. */
    public Category getCategory() {
        return this.cat;
    }

    private final Category cat;

    /** Returns the name of this aspect kind. */
    public String getName() {
        return this.name;
    }

    private final String name;

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Returns the prefix of this aspect kind.
     * The prefix is the text (including {@link AspectParser#SEPARATOR}) by which a plain text
     * label is recognised to have this aspect.
     */
    public String getPrefix() {
        return getName() + SEPARATOR;
    }

    /**
     * Returns type of content of this aspect kind.
     * May be {@code null}, if no content kind is allowed.
     */
    public ContentKind getContentKind() {
        return this.contentKind;
    }

    private final ContentKind contentKind;

    /** Returns a (prototypical) aspect of this kind. */
    public Aspect getAspect() {
        if (this.aspect == null) {
            this.aspect = new Aspect(this);
        }
        return this.aspect;
    }

    private Aspect aspect;

    /** Returns a new aspect of this aspect kind, with content derived by
     * parsing a given content text and graph role.
     * @throws FormatException if the text does not represent content of the
     * correct kind
     * @see ContentKind#parseContent(String, GraphRole, Status)
     */
    public Aspect newAspect(String text, GraphRole role) throws FormatException {
        return new Aspect(this, getContentKind().parseContent(text, role, Status.INIT));
    }

    /**
     * Parses a given string into an aspect of this kind, and the remainder.
     * The string is guaranteed to start with the name of this aspect, and
     * to contain a separator.
     * @param input the string to be parsed
     * @param role graph role for which the parsing is done
     * @return a pair consisting of the resulting aspect and the remainder of
     * the input string, starting from the character after the first occurrence
     * of #SEPARATOR onwards.
     * @throws FormatException if the string does not have content of the
     * correct kind
     */
    Pair<Aspect,String> parseAspect(String input, GraphRole role,
                                    AspectParser.Status status) throws FormatException {
        assert input.startsWith(getName()) && input.indexOf(SEPARATOR) >= 0;
        // give the text to the content kind to parse
        var result = getContentKind().parse(input, getName().length(), role, status);
        return new Pair<>(new Aspect(this, result.one()), result.two());
    }

    /**
     * Indicates if this aspect is among the set of roles.
     * @see #roles
     */
    public boolean isRole() {
        return roles.contains(this);
    }

    /**
     * Indicates if this aspect is among the set of NAC (non-LHS) elements.
     * @see #nac
     */
    public boolean inNAC() {
        return nac.contains(this);
    }

    /**
     * Indicates if this aspect is among the set of LHS element.
     * @see #lhs
     */
    public boolean inLHS() {
        return lhs.contains(this);
    }

    /**
     * Indicates if this aspect is among the set of RHS elements.
     * @see #rhs
     */
    public boolean inRHS() {
        return rhs.contains(this);
    }

    /**
     * Indicates if this element is in the LHS but not the RHS.
     * Convenience method for {@code inLHS() && !inRHS()}.
     */
    public boolean isEraser() {
        return inLHS() && !inRHS();
    }

    /**
     * Indicates if this element is in the RHS but not the LHS.
     * Convenience method for {@code inRHS() && !inLHS()}.
     */
    public boolean isCreator() {
        return inRHS() && !inLHS();
    }

    /** Checks if this aspect kind is of a given category. */
    public boolean has(Category cat) {
        return getCategory() == cat;
    }

    /**
     * Indicates if this aspect is among the set of typed data aspects.
     * @see #getSort()
     */
    public boolean hasSort() {
        return getSort() != null;
    }

    /**
     * Tests if this aspect kind has a given sort.
     * @param sort the sort to test for
     * @see #getSort()
     */
    public boolean hasSort(Sort sort) {
        return getSort() == sort;
    }

    /**
     * Returns the (possibly {@code null}) data sort of this aspect kind.
     * @see #hasSort()
     */
    public Sort getSort() {
        return this.contentKind.getSort();
    }

    /**
     * Indicates if this aspect is an assignment.
     */
    public boolean isAssign() {
        return this == LET;
    }

    /**
     * Indicates if this aspect is among the set of quantifiers.
     * @see #existsQuantifiers
     */
    public boolean isExists() {
        return existsQuantifiers.contains(this);
    }

    /**
     * Indicates if this aspect is among the set of quantifiers.
     * @see #forallQuantifiers
     */
    public boolean isForall() {
        return forallQuantifiers.contains(this);
    }

    /**
     * Indicates if this aspect is among the set of quantifiers.
     */
    public boolean isQuantifier() {
        return isExists() || isForall();
    }

    /** Returns the new parser status after parsing this aspect kind. */
    Status newStatus(Status current) {
        return switch (getCategory()) {
        case ROLE -> Status.ROLE;
        case MULT_IN, MULT_OUT, ASSOC -> current;
        default -> Status.DONE;
        };
    }

    /** Indicates that this aspect kind is always the last on a label. */
    public boolean isLast() {
        return this.contentKind != ContentKind.LEVEL && this.contentKind != ContentKind.MULTIPLICITY
            && this != COMPOSITE;
    }

    /**
     * Returns the aspect kind corresponding to a certain non-{@code null}
     * name, or {@code null} if there is no such aspect kind.
     */
    public static AspectKind getKind(String name) {
        return kindMap.get(name);
    }

    /**
     * Returns the documentation map for node aspects occurring for a given graph role.
     * @return A mapping from syntax lines to associated tool tips.
     */
    public static HelpMap getNodeDocMap(GraphRole role) {
        var result = nodeDocMapMap.get(role);
        if (result == null) {
            nodeDocMapMap.put(role, result = computeNodeDocMap(role));
        }
        return result;
    }

    /**
     * Returns the documentation map for edge aspects occurring for a given graph role.
     * @return A mapping from syntax lines to associated tool tips.
     */
    public static HelpMap getEdgeDocMap(GraphRole role) {
        var result = edgeDocMapMap.get(role);
        if (result == null) {
            edgeDocMapMap.put(role, result = computeEdgeDocMap(role));
        }
        return result;
    }

    /** Returns the aspect kinds corresponding to a given signature. */
    public static AspectKind toAspectKind(Sort signature) {
        return sigKindMap.get(signature);
    }

    private static HelpMap computeNodeDocMap(GraphRole role) {
        var result = new HelpMap();
        Set<AspectKind> nodeKinds = EnumSet.copyOf(allowedNodeKinds.get(role));
        if (role == GraphRole.HOST || role == GraphRole.RULE) {
            nodeKinds.add(LET);
        }
        if (role == GraphRole.RULE) {
            nodeKinds.add(TEST);
        }
        for (AspectKind kind : nodeKinds) {
            for (var helpKind : HelpType.values()) {
                if (helpKind.forNode()) {
                    Help help = computeHelp(kind, role, helpKind);
                    if (help != null) {
                        result.put(help.getItem(), help.getTip());
                    }
                }
            }
        }
        return result;
    }

    private static HelpMap computeEdgeDocMap(GraphRole role) {
        var result = new HelpMap();
        Set<AspectKind> edgeKinds = EnumSet.copyOf(allowedEdgeKinds.get(role));
        edgeKinds.remove(LET);
        edgeKinds.remove(TEST);
        edgeKinds.removeAll(existsQuantifiers);
        edgeKinds.removeAll(forallQuantifiers);
        if (role == GraphRole.TYPE) {
            for (AspectKind kind : edgeKinds) {
                if (kind.hasSort()) {
                    edgeKinds.remove(kind);
                }
            }
        }
        for (AspectKind kind : edgeKinds) {
            for (var helpKind : HelpType.values()) {
                if (!helpKind.forNode()) {
                    result.add(computeHelp(kind, role, helpKind));
                }
            }
        }
        return result;
    }

    @SuppressWarnings("incomplete-switch")
    private static Help computeHelp(AspectKind kind, GraphRole role, HelpType type) {
        String h = null;
        String s = null;
        var b = new HelpList();
        var p = new HelpList();
        final var qBody = "%1$s denotes the associated quantifier level.";
        final var optQBody = "The optional " + qBody;
        final var qPar = "associated quantifier level";
        final var optQPar = "optional " + qPar;
        final var optQDecl
            = "(optional) declared name for this quantifier level.<br>(alternatively given through an ID-declation)";
        final var sortPar
            = "one of the primitive sorts " + toHelpString(Arrays.asList(Sort.values()), "", "");
        final var flagPar = "flag label text; identifier with optional hyphens";
        final var edgePar = "edge label text; identifier with optional hyphens";
        final var parPar
            = "the parameter number, ranging from 0.<br>Parameter numbers must be unique and contiguous";
        final var exprPar = "arithmetic expression; for syntax see the appropriate tab";
        final var regexprPar = "regular expression; for syntax see the appropriate tab";
        switch (kind) {
        case ABSTRACT:
            switch (type) {
            case E:
                s = "%s.COLON.label";
                h = "Abstract edge type";
                b
                    .and("Declares an abstract %s-edge between node types.")
                    .and("The edge can only occur between subtypes where it is redeclared concretely.");
                p.add(edgePar);
                break;
            case N0:
                s = "%s.COLON";
                h = "Abstract node type";
                b
                    .and("Declares a node type to be abstract.")
                    .and("Only nodes of concrete subtypes can actually exist.");
                break;
            case N1:
                s = "%s.COLON.flag";
                h = "Abstract flag type";
                b
                    .and("Declares an abstract %s for a node type.")
                    .and("The flag can only occur on subtypes where it is redeclared concretely.");
                p.add(flagPar);
            }
            break;

        case ADDER:
            switch (type) {
            case E:
                s = "%s[EQUALS.q]COLON.label";
                h = "Conditional edge creator";
                b
                    .and("Tests for the absence of a %2$s-edge; creates it upon application.")//
                    .and(optQBody);
                p
                    .and(optQPar)//
                    .and(edgePar);
                break;
            case N0:
                s = "%s.COLON";
                h = "Conditional node creator";
                b.add("Tests for the absence of a node; creates it upon application.");
                break;
            case N1:
                s = "%s[EQUALS.q]COLON.FLAG.COLON.flag";
                h = "Conditional flag creator";
                b
                    .and("Tests for the absence of %2$s; creates it upon application.")//
                    .and(optQBody);
                p
                    .and(optQPar)//
                    .and(flagPar);
                break;
            case N3:
                s = "%s[EQUALS.q]COLON.LET.COLON.field.EQUALS.expr";
                h = "Conditional attribute field creator";
                b
                    .and("Tests for the absence of an attribute field %2$s")
                    .and("with current value %3$s;")
                    .and("creates the field upon application.")
                    .and(optQBody);
                p.and(optQPar).and("created field name").and(exprPar);
                break;
            }
            break;

        case ARGUMENT:
            s = "%s.COLON.nr";
            h = "Argument edge";
            b.add("Projects its source product node (PRODUCT) onto argument %s.");
            p
                .add("argument number, ranging from 0 to the product node arity - 1.<br>"
                    + "Argument numbers from one PRODUCT-node must be unique and contiguous");
            break;

        case ATOM:
            s = "regexpr";
            h = "Regular expression path";
            b
                .and("Tests for a path satisfying %1$s.")
                .and("To specify a regular label containing non-standard characters, prefix with 'COLON'.");
            p.add(regexprPar);
            break;

        case BOOL:
            switch (type) {
            case E:
                s = "sort.COLON.op";
                h = "Primitive operator";
                b
                    .and("Applies operation %2$s from the signature %1$s")
                    .and("to the arguments of the source node (which must be a product node).");
                p.and("sort of the operator; " + sortPar).and("operator of sort %1$s");
                break;
            case N0:
                if (role == GraphRole.TYPE) {
                    s = "sort.COLON.field";
                    h = "Attribute field";
                    b.add("Declares %2$s to be attribute field of sort %1$s");
                    p.and(sortPar).and("attribute field name");
                } else if (role == GraphRole.HOST) {
                    s = "sort.COLON.constant";
                    h = "Constant value node";
                    b.add("Represents value %2$s of sort %1$s");
                    p.and(sortPar).and("literal value of sort %1$s");
                } else {
                    s = "sort.COLON.[expr]";
                    h = "Variable or value node";
                    b.add("Declares a %1$s node, optionally with value determined by %2$s");
                    p
                        .and(sortPar)
                        .and("Optional expression of sort %1$s, determining the value of the node.<br> "
                            + "(For expression syntax see the appropriate tab)");
                }
                break;
            }
            break;

        case COLOR:
            s = "%s.COLON.(rgb|name)";
            h = "Node type colour";
            switch (role) {
            case HOST -> b
                .add("Sets the colour of the nodes and outgoing edges upon rule application.");
            case RULE -> b.add("Sets the initial colour of the nodes and outgoing edges.");
            case TYPE -> b
                .add("Declares the color of all nodes and outgoing edges of a node type.");
            }
            p.add("comma-seperated list of three colour dimensions, with range 0..255");
            p.add("color name");
            break;

        case COMPOSITE:
            s = "%s.COLON.label";
            h = "Composite edge property";
            b
                .and("Declares an edge type to be composite.")
                .and("Composite edge types implicitly have incoming edge multiplicity 0..1")
                .and("and their instances may not form a cycle in a graph.");
            p.and("label of the composite edge type");
            break;

        case CONNECT:
            s = "%s.COLON";
            h = "Embargo choice";
            b
                .and("Declares a choice between two negative application patterns.")
                .and("Source and target node must be part of two distinct NACs");
            break;

        case CREATOR:
            switch (type) {
            case E:
                s = "%s[EQUALS.q]COLON.label";
                h = "Edge creator";
                b.add("Creates a %2$s-edge upon application.");
                b.add(optQBody);
                p.add(optQPar);
                p.add("label of the created edge");
                break;
            case N0:
                s = "%s.COLON";
                h = "Node creator";
                b.add("Creates a node upon application.");
                break;
            case N1:
                s = "%s[EQUALS.q]COLON.FLAG.COLON.flag";
                h = "Flag creator";
                b.add("Creates a %2$s upon application.");
                b.add(optQBody);
                p.add(optQPar);
                p.add("created flag");
                break;
            case N3:
                s = "%s[EQUALS.q]COLON.LET.field.EQUALS.expr";
                h = "Attribute field creator";
                b
                    .and("Upon application, creates an attribute field %2$s")
                    .and("with value %3$s.")
                    .and(optQBody);
                p.and(optQPar).and("created field name").and(exprPar);
                break;
            }
            break;

        case EDGE:
            s = "%s.COLON.QUOTE.format.QUOTE.[COMMA.field+]";
            h = "Nodifier edge pattern";
            b
                .and("Declares the node type to be a nodified edge,")
                .and("meaning that it will not be displayed as a node.")
                .and("Instead, the incoming edges will be labelled by expanding %s")
                .and("with string representations of the concrete values of the %s list");
            p.add("Label format string, with parameter syntax as in <tt>String.format</tt>");
            p.add("Comma-separated list of attribute field names");
            break;

        case EMBARGO:
            switch (type) {
            case E:
                s = "%s[EQUALS.q]COLON.regexpr";
                h = "Edge embargo";
                b.add("Tests for the absence of a path satisfying %2$s.");
                b.add(optQBody);
                p.add(optQPar);
                p.add(regexprPar);
                break;
            case N0:
                s = "%s.COLON";
                h = "Node embargo";
                b.add("Tests for the absence of a node.");
                break;
            case N1:
                s = "%s[EQUALS.q]COLON.FLAG.flag";
                h = "Flag embargo";
                b.add("Tests for the absence of a %2$s.");
                b.add(optQBody);
                p.add(optQPar);
                p.add("forbidden flag");
                break;
            case N2:
                s = "%s[EQUALS.q]COLON.sort.COLON.field";
                h = "Attribute field embargo";
                b
                    .and("Tests for the absence of an attribute field %3$s")
                    .and("of type %2$s and arbitrary value.")
                    .and(optQBody);
                p.and(optQPar).and(sortPar).and("erased field name");
                break;
            }
            break;

        case ERASER:
            switch (type) {
            case E:
                s = "%s[EQUALS.q]COLON.label";
                h = "Edge eraser";
                b.add("Tests for the presence of a %2$s-edge; deletes it upon application.");
                b.add(optQBody);
                p.add(optQPar);
                p.add("label of the erased edge");
                break;
            case N0:
                s = "%s.COLON";
                h = "Node eraser";
                b.add("Tests for the presence of a node; deletes it upon application.");
                break;
            case N1:
                s = "%s[EQUALS.q]COLON.FLAG.COLON.flag";
                h = "Flag eraser";
                b.add("Tests for the presence of a %2$s-flag; deletes it upon application.");
                b.add(optQBody);
                p.add(optQPar);
                p.add("erased flag");
                break;
            case N2:
                s = "%s[EQUALS.q]COLON.sort.COLON.name";
                h = "Attribute field eraser";
                b
                    .and("Tests for the presence of an attribute field %3$s")
                    .and("of type %2$s and arbitrary value;")
                    .and("deletes the field upon application.")
                    .and(optQBody);
                p.and(optQPar).and(sortPar).and("erased field name");
                break;
            case N3:
                s = "%s[EQUALS.q]COLON.LET.COLON.name.EQUALS.expr";
                h = "Attribute field test and eraser";
                b
                    .and("Tests for the presence of an attribute field %2$s")
                    .and("with current value %3$s;")
                    .and("deletes the field upon application.")
                    .and(optQBody);
                p.and(optQPar).and("erased field name").and(exprPar);
                break;
            }
            break;

        case EXISTS:
            s = "%s[EQUALS.q]COLON";
            h = "Existential quantier";
            b
                .and("Tests for the mandatory existence of a graph pattern, and transforms it.")
                .and("Pattern nodes must have outgoing AT-edges to the quantifier node;")
                .and("pattern edges may be declared by adding the quantifier level name '=%1$s'")
                .and("to their role aspect")
                .and(toHelpString(roles, "(one of ", ")."));
            p.add(optQDecl);
            break;

        case EXISTS_OPT:
            s = "%s[EQUALS.q]COLON";
            h = "Optional existential quantifier";
            b
                .and("Tests for the optional existence of a graph pattern, and transforms it if found.")
                .and("Pattern nodes must have outgoing AT-edges to the quantifier node;")
                .and("pattern edges may be declared by adding the quantifier level name '=%1$s'")
                .and("to their role aspect")
                .and(toHelpString(roles, "(one of ", ")."));
            p.add(optQDecl);
            break;

        case FORALL:
            s = "%s[EQUALS.q]COLON";
            h = "Universal quantifier";
            b
                .and("Matches and transforms all occurrences of a graph pattern.")
                .and("The actual number of occurrences is given by an optional outgoing COUNT-edge.")
                .and("Pattern nodes must have outgoing AT-edges to the quantifier node;")
                .and("pattern edges may be declared by adding the quantifier level name '=%1$s'")
                .and("to their role aspect")
                .and(toHelpString(roles, "(one of ", ")."));
            p.add(optQDecl);
            break;

        case FORALL_POS:
            s = "%s[EQUALS.q]COLON";
            h = "Non-vacuous universal quantifier";
            b
                .and("Matches and transforms all occurrences of a graph pattern, provided there is at least one.")
                .and("The actual number of occurrences is given by an optional outgoing COUNT-edge.")
                .and("Pattern nodes must have outgoing AT-edges to the quantifier node;")
                .and("pattern edges may be declared by adding the quantifier level name '=%1$s'")
                .and("to their role aspect")
                .and(toHelpString(roles, "(one of ", ")."));
            p.add(optQDecl);
            break;

        case ID:
            s = "%s.COLON.name";
            h = "Node identifier";
            b.and("Assigns the (graph-local) name %s to this node.");
            if (role == GraphRole.RULE) {
                b
                    .and("A node identifier serves the following purpose, depending on the kind of node:")
                    .and("<ul><li>For regular graph nodes, to qualify field names within expressions;")
                    .and("<li>For variable nodes, to refer to the variable in expressions;")
                    .and("<li>For quantifier nodes, to associate edges with the quantifier level.</ul>");
            } else {
                b
                    .add("When multiple start graphs are enabled, nodes with the same identifier will be merged.");
            }
            p.add("the declared name for this node; must be unique within the graph");
            break;

        case IMPORT:
            s = "%s.COLON";
            h = "Imported node type";
            b.add("Indicates that the type is imported from another type graph.");
            b.add("This affects the behaviour of hiding (all elements of) a type graph.");
            break;

        case INT:
            // covered by the general help of BOOL
            break;

        case LET:
            if (role == GraphRole.RULE) {
                s = "%s.COLON.field.EQUALS.expr";
                h = "Assignment";
                b
                    .and("Assigns the value of %2$s to the (existing) attribute field %1$s.")
                    .and("The previous value of %1$s is lost");
                p.and("field name").and(exprPar);
            } else if (role == GraphRole.HOST) {
                s = "%s.COLON.field.EQUALS.constant";
                h = "Initialisation";
                b.and("Sets the attribute field %1$s to the initial value %2$s");
                p.and("field name").and("literal value of a primitive sort<br>(" + sortPar + ")");
            }
            break;

        case LITERAL:
            s = "COLON.free";
            h = "Literal edge label";
            b
                .and("Specifies a %1$s-labelled edge, where %1$s may be an arbitrary string")
                .and("Only for use in untyped rule systems");
            p.add("a string of arbitrary characters");
            break;

        case MULT_IN:
            s = "%s.EQUALS[lo.DOT.DOT]hi.COLON.label";
            h = "Incoming edge multiplicity.";
            b.add("Constrains the number of incoming %3$s-edges for every node");
            b.add("to at least %1$s (if specified) and at most %2$s");
            p.add("optional lower bound");
            p.add("mandatory upper bound ('*' for unbounded)");
            p.add("label of the incoming edge");
            break;

        case MULT_OUT:
            s = "%s.EQUALS[lo.DOT.DOT]hi.COLON.label";
            h = "Outgoing edge multiplicity.";
            b.add("Constrains the number of outgoing %3$s-edges for every node");
            b.add("to at least %1$s (if specified) and at most %2$s");
            p.add("optional lower bound");
            p.add("mandatory upper bound ('*' for unbounded)");
            p.add("label of the outgoing edge");
            break;

        case NESTED:
            s = "[%s.COLON](AT|IN|COUNT)";
            h = "Structural nesting edge";
            b.add("Declares quantifier structure (the NESTED-prefix itself is optional):");
            b.add("<li> IN nests one quantifier within another;");
            b.add("<li> AT connects a graph pattern node to a quantifier;");
            b.add("<li> COUNT points to the cardinality of a quantifier.");
            break;

        case PARAM_BI:
            switch (type) {
            case N0:
                s = "%s.COLON";
                h = "Anchor node";
                b.add("Declares an explicit anchor node.");
                b.add("This causes the node to be considered relevant in distinguishing matches");
                b.add("even if it is not involved in any deletion, creation or merging.");
                break;
            case N1:
                s = "%s.COLON.nr";
                h = "Bidirectional rule parameter";
                b
                    .and("Declares bidirectional rule parameter %1$s.")
                    .and("In a control program this parameter may be instantiated with a concrete value,")
                    .and("or be used as an output parameter, in which case the value")
                    .and("is determined by the matching.");
                p.and(parPar);
                break;
            }
            break;

        case PARAM_IN:
            s = "%s.COLON.nr";
            h = "Rule input parameter";
            b
                .and("Declares rule input parameter %s.")
                .and("The value must be provided through a control program,")
                .add("or by setting the 'algebra family' in the system properties to POINT");
            p.and(parPar);
            break;

        case PARAM_OUT:
            s = "%s.COLON.nr";
            h = "Rule output parameter";
            b.and("Declares rule output parameter %s.");
            p.and(parPar);
            break;

        case PARAM_ASK:
            s = "%s.COLON.nr";
            h = "Interactive rule parameter";
            b
                .and("Declares interactive rule parameter %s.")
                .and("The value is provided (upon rule application) through a <br><i>value oracle</i>, set in the system properties.");
            p.and(parPar);
            break;

        case PATH:
            s = "%s.COLON.regexpr";
            h = "Regular path expression";
            b.add("Tests for a path satisfying the regular expression %1$s.");
            p.add(regexprPar);
            break;

        case PRODUCT:
            s = "%s.COLON";
            h = "Product node";
            b.add("Declares a product node, corresponding to a tuple of attribute nodes.");
            break;

        case READER:
            switch (type) {
            case E:
                s = "%s.EQUALS.q.COLON.regexpr";
                h = "Quantified reader edge";
                b.and("Tests for a path satisfying %2$s, on quantification level %1$s.");
                p.and(qPar).and(regexprPar);
                break;
            case N1:
                s = "%s.EQUALS.q.COLON.FLAG.COLON.flag";
                h = "Quantified reader flag";
                b.and("Tests for the presence of a %2$s-flag on quantification level %1$s.");
                p.and(qPar).and("tested flag");
                break;
            case N2:
                s = "%s.EQUALS.q.COLON.sort.COLON.field";
                h = "Attribute field test";
                b
                    .and("Tests for the presence of an attribute field %3$s")
                    .and("on quantification level %1$s")
                    .and("of type %2$s and arbitrary value.");
                p.and(qPar).and(sortPar).and("tested field name");
                break;
            }
            break;

        case REAL:
            // covered by the general help of BOOL
            break;

        case REMARK:
            switch (type) {
            case N0:
                s = "%s.COLON";
                b.add("Declares a remark node, to be used for documentation");
                break;
            case N1:
                s = "%s.COLON.text";
                b.add("Places a remark on an arbitrary node, to be used for documentation");
                break;
            case E:
                s = "%s.COLON.text";
                b.add("Declares a remark edge with (free-formatted) label %1$s");
            }
            break;

        case STRING:
            // covered by the general help of BOOL
            break;

        case SUBTYPE:
            s = "%s.COLON";
            h = "Subtype declaration";
            b.add("Declares the source type node to be a subtype of the target type node");
            break;

        case TEST:
            switch (type) {
            case N0:
                s = "%s.COLON.name.EQUALS.expr";
                h = "Attribute value test";
                b.add("Tests if the attribute field %1$s equals the value of %2$s.");
                break;
            case N1:
                s = "%s.COLON.constraint";
                h = "Predicate expression";
                b.add("Tests if the boolean expression %s holds in the graph.");
                p.add("expression of sort BOOL; see the appropriate type for syntax");
            }
            break;

        default:
            throw new IllegalStateException();
        }
        Help result = null;
        if (s != null) {
            result = new Help(tokenMap);
            result.setSyntax(String.format(s, kind.name()));
            result.setHeader(h);
            result.setBody(b);
            result.setPars(p);
        }
        return result;
    }

    /** List class with auxiliary functionality for concatenating. */
    static private class HelpList extends ArrayList<String> {
        /** Adds a message and returns this list, so calls can be concatenated. */
        public HelpList and(String message) {
            add(message);
            return this;
        }
    }

    /** Auxiliary enumeration to collect help for variations of an aspect. */
    static private enum HelpType {
        /** Edge help. */
        E(false),
        /** Node help (no content). */
        N0(true),
        /** Node help (content v1). */
        N1(true),
        /** Node help (content v2). */
        N2(true),
        /** Node help (content v3). */
        N3(true);

        private HelpType(boolean forNode) {
            this.forNode = forNode;
        }

        /** Indicates if this is help for a node aspect;
         * if not, it is for an edge aspect.
         */
        public boolean forNode() {
            return this.forNode;
        }

        private final boolean forNode;
    }

    /** Turns a set of values into upper case and concatenates them. */
    static private <E extends Enum<E>> String toHelpString(Collection<E> values, String start,
                                                           String end) {
        var caps = values.stream().map(E::name);
        return Groove.toString(caps.toArray(), start, end, ", ", " or ");
    }

    /** For every relevant graph role the node syntax help entries. */
    private static final Map<GraphRole,HelpMap> nodeDocMapMap = new EnumMap<>(GraphRole.class);
    /** For every relevant graph role the edge syntax help entries. */
    private static final Map<GraphRole,HelpMap> edgeDocMapMap = new EnumMap<>(GraphRole.class);
    /** Static mapping from all aspect names to aspects. */
    private static final Map<String,AspectKind> kindMap = new HashMap<>();
    /** Mapping from kind value names to symbols. */
    private static final Map<String,String> tokenMap = new HashMap<>();
    /** Mapping from signature names to aspect kinds. */
    private static final Map<Sort,AspectKind> sigKindMap = new EnumMap<>(Sort.class);

    static {
        // initialise the aspect kind and token maps
        for (AspectKind kind : AspectKind.values()) {
            AspectKind oldKind = kindMap.put(kind.toString(), kind);
            assert oldKind == null;
            tokenMap.put(kind.name(), kind.getName());
            Sort sigKind = Sort.getSort(kind.getName());
            if (sigKind != null) {
                sigKindMap.put(sigKind, kind);
            }
        }
        for (NestedValue value : NestedValue.values()) {
            tokenMap.put(value.name(), value.toString());
        }
        tokenMap.put("COLON", "" + AspectParser.SEPARATOR);
        tokenMap.put("EQUALS", "" + AspectParser.ASSIGN);
        tokenMap.put("DOT", ".");
        tokenMap.put("COMMA", ",");
        tokenMap.put("QUOTE", "\"");
        tokenMap.put("TRUE", "true");
        tokenMap.put("FALSE", "false");
        tokenMap.put("FLAG", "flag");
        tokenMap.put("PRODUCT", "prod");
        tokenMap.put("POINT", "point");
    }

    /** Set of role aspects. */
    public static final Set<AspectKind> roles = EnumSet.of(ERASER, ADDER, CREATOR, READER, EMBARGO);
    /** Set of role aspects appearing (only) in NACs. */
    public static final Set<AspectKind> nac = EnumSet.of(EMBARGO, ADDER, CONNECT);
    /** Set of role aspects appearing in LHSs. */
    public static final Set<AspectKind> lhs = EnumSet.of(READER, ERASER);
    /** Set of role aspects appearing in RHSs. */
    public static final Set<AspectKind> rhs = EnumSet.of(READER, CREATOR, ADDER);
    /** Set of existential quantifier aspects. */
    public static final Set<AspectKind> existsQuantifiers = EnumSet.of(EXISTS, EXISTS_OPT);
    /** Set of universal quantifier aspects. */
    public static final Set<AspectKind> forallQuantifiers = EnumSet.of(FORALL, FORALL_POS);

    /** Mapping from graph roles to the node aspects allowed therein. */
    public static final Map<GraphRole,Set<AspectKind>> allowedNodeKinds
        = new EnumMap<>(GraphRole.class);
    /** Mapping from graph roles to the edge aspects allowed therein. */
    public static final Map<GraphRole,Set<AspectKind>> allowedEdgeKinds
        = new EnumMap<>(GraphRole.class);

    static {
        for (GraphRole role : GraphRole.values()) {
            Set<AspectKind> nodeKinds, edgeKinds;
            switch (role) {
            case HOST:
                nodeKinds = EnumSet.of(REMARK, INT, BOOL, REAL, STRING, COLOR, ID);
                edgeKinds = EnumSet.of(REMARK, ATOM, LITERAL, LET);
                break;
            case RULE:
                nodeKinds = EnumSet
                    .of(REMARK, READER, ERASER, CREATOR, ADDER, EMBARGO, BOOL, INT, REAL, STRING,
                        PRODUCT, PARAM_BI, PARAM_IN, PARAM_OUT, PARAM_ASK, FORALL, FORALL_POS,
                        EXISTS, EXISTS_OPT, ID, COLOR);
                edgeKinds = EnumSet
                    .of(REMARK, READER, ERASER, CREATOR, ADDER, EMBARGO, CONNECT, BOOL, INT, REAL,
                        STRING, ARGUMENT, ATOM, PATH, LITERAL, FORALL, FORALL_POS, EXISTS,
                        EXISTS_OPT, NESTED, LET, TEST);
                break;
            case TYPE:
                nodeKinds
                    = EnumSet.of(REMARK, INT, BOOL, REAL, STRING, ABSTRACT, IMPORT, COLOR, EDGE);
                edgeKinds = EnumSet
                    .of(REMARK, ATOM, INT, BOOL, REAL, STRING, ABSTRACT, SUBTYPE, MULT_IN, MULT_OUT,
                        COMPOSITE);
                break;
            default:
                assert !role.inGrammar();
                nodeKinds = EnumSet.noneOf(AspectKind.class);
                edgeKinds = EnumSet.noneOf(AspectKind.class);
            }
            allowedNodeKinds.put(role, nodeKinds);
            allowedEdgeKinds.put(role, edgeKinds);
        }
    }

    /**
     * Categories of aspects.
     * Each aspect element can have at most one aspect of any given category.
     * Some categories have a default value.
     * Some categories are mutually exclusive.
     * @author Rensink
     * @version $Revision $
     */
    static public enum Category {
        /** Remark aspect. */
        REMARK,
        /** Role within the rule graph. */
        ROLE,
        /** Nesting attributes, determining the structure of the rule tree. */
        NESTING(ROLE) {
            @Override
            boolean conflictsForNode(Category other, GraphRole forRole) {
                // NESTING and ROLE don't go together for nodes, just for edges
                return other == ROLE;
            }
        },
        /** The four primitive sorts. */
        SORT(ROLE),
        /** Other attribute-related aspects. */
        ATTR(ROLE),
        /** Identifier declaration. */
        ID(ROLE, NESTING, SORT) {
            @Override
            boolean conflictsForNode(Category other, GraphRole forRole) {
                // ID and SORT don't go together in host graphs
                return other == SORT && forRole == GraphRole.HOST;
            }
        },
        /** Parameter aspect. */
        PARAM(ROLE, SORT, ID),
        /** Label mode. */
        LABEL(REMARK, ROLE, NESTING),
        /** Colour declaration. */
        COLOR(ROLE, ID, PARAM),
        /** Node type-related aspects. */
        TYPE(LABEL),
        /** Import aspect. */
        IMPORT,
        /** Nodified edge declaration. */
        EDGE(COLOR),
        /** Incoming multiplicity declaration. */
        MULT_IN(LABEL, TYPE),
        /** Outgoing multiplicity declaration. */
        MULT_OUT(SORT, LABEL, TYPE, MULT_IN),
        /** Relational nature of an edge. */
        ASSOC(LABEL, TYPE, MULT_IN, MULT_OUT),;

        /** Declares a category and its compatibility with other ("smaller") categories. */
        private Category(Category... ok) {
            // only "smaller" categories should be declared as compatible.
            for (Category conflict : ok) {
                assert conflict.ordinal() < ordinal();
            }
            this.okArray = ok;
        }

        /** The OK values as an array.
         * This is workaround for the fact that we can't create an EnumSet of Category
         * during class initialisation
         */
        private final Category[] okArray;

        /** Tests if this category is compatible with another.
         * @param other the other category
         * @param forNode switch determining whether this is to be tested for nodes or edges
         * @param forRole the graph role for which this is to be tested
         */
        public boolean ok(Category other, boolean forNode, GraphRole forRole) {
            if (this == other) {
                return true;
            } else if (other.ordinal() < ordinal()) {
                return ok(other) && (forNode
                    ? !conflictsForNode(other, forRole)
                    : !conflictsForEdge(other, forRole));
            } else {
                return other.ok(this, forNode, forRole);
            }
        }

        /** Tests if this category conflicts with another when used on nodes,
         * in spite of the the general compatibility.
         * @param other the other category
         * @param forRole the graph role for which this is to be tested
         */
        boolean conflictsForNode(Category other, GraphRole forRole) {
            return false;
        }

        /** Tests if this category conflicts with another when used on edges,
         * in spite of the the general compatibility.
         * @param other the other category
         * @param forRole the graph role for which this is to be tested
         */
        boolean conflictsForEdge(Category other, GraphRole forRole) {
            return false;
        }

        /** Tests if this category is generally compatible with another,
         * strictly smaller category.
         * @param other the other category
         */
        private boolean ok(Category other) {
            assert other.ordinal() < ordinal();
            // this implementation does not specialise for roles or nodes/edges;
            // that is left to special values
            return ok().contains(other);
        }

        /** Returns the compatible categories as a set. */
        private Set<Category> ok() {
            return this.ok.get();
        }

        /** Creator method for the set of compatible categories. */
        private Set<Category> createOk() {
            var result = EnumSet.noneOf(Category.class);
            Arrays.stream(this.okArray).forEach(result::add);
            return result;
        }

        /** The categories with which this one conflicts. */
        private LazyFactory<Set<Category>> ok = LazyFactory.instance(this::createOk);
    }
}
