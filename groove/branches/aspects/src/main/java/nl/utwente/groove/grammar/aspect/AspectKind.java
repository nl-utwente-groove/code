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
package nl.utwente.groove.grammar.aspect;

import static nl.utwente.groove.grammar.aspect.AspectParser.SEPARATOR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import nl.utwente.groove.algebra.Signature.OpValue;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.annotation.Help;
import nl.utwente.groove.grammar.aspect.AspectContent.ContentKind;
import nl.utwente.groove.grammar.aspect.AspectContent.NestedValue;
import nl.utwente.groove.graph.GraphRole;
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
    /** Indicates a new attribute. */
    LET_NEW(Category.ATTR, "letnew", ContentKind.LET_EXPR),

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
    FORALL(Category.META, "forall", ContentKind.LEVEL),
    /** Non-vacuous universal quantifier. */
    FORALL_POS(Category.META, "forallx", ContentKind.LEVEL),
    /** Existential quantifier. */
    EXISTS(Category.META, "exists", ContentKind.LEVEL),
    /** Optional existential quantifier. */
    EXISTS_OPT(Category.META, "existsx", ContentKind.LEVEL),
    /** Nesting edge. */
    NESTED(Category.META, "nested", ContentKind.NESTED),
    /** Connects two embargo sub-graphs. */
    CONNECT(Category.META, "or", ContentKind.EMPTY),

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
     * @see ContentKind#parseContent(String, GraphRole)
     */
    public Aspect newAspect(String text, GraphRole role) throws FormatException {
        return new Aspect(this, getContentKind().parseContent(text, role));
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
    public Pair<Aspect,String> parseAspect(String input, GraphRole role) throws FormatException {
        assert input.startsWith(getName()) && input.indexOf(SEPARATOR) >= 0;
        // give the text to the content kind to parse
        var result = getContentKind().parse(input, getName().length(), role);
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
     * Indicates if this aspect is among the set of meta-aspects.
     */
    public boolean isMeta() {
        return has(Category.META);
    }

    /**
     * Indicates if this aspect is among the set of parameter aspects.
     */
    public boolean isParam() {
        return has(Category.PARAM);
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

    /**
     * Indicates if this aspect is attribute related.
     * @see #attributers
     */
    public boolean isAttrKind() {
        return attributers.contains(this);
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
    public static Map<String,String> getNodeDocMap(GraphRole role) {
        Map<String,String> result = nodeDocMapMap.get(role);
        if (result == null) {
            nodeDocMapMap.put(role, result = computeNodeDocMap(role));
        }
        return result;
    }

    /**
     * Returns the documentation map for edge aspects occurring for a given graph role.
     * @return A mapping from syntax lines to associated tool tips.
     */
    public static Map<String,String> getEdgeDocMap(GraphRole role) {
        Map<String,String> result = edgeDocMapMap.get(role);
        if (result == null) {
            edgeDocMapMap.put(role, result = computeEdgeDocMap(role));
        }
        return result;
    }

    /** Returns the aspect kinds corresponding to a given signature. */
    public static AspectKind toAspectKind(Sort signature) {
        return sigKindMap.get(signature);
    }

    private static Map<String,String> computeNodeDocMap(GraphRole role) {
        Map<String,String> result = new TreeMap<>();
        Set<AspectKind> nodeKinds = EnumSet.copyOf(allowedNodeKinds.get(role));
        if (role == GraphRole.HOST || role == GraphRole.RULE) {
            nodeKinds.add(LET);
        }
        if (role == GraphRole.RULE) {
            nodeKinds.add(TEST);
            nodeKinds.add(LET_NEW);
        }
        for (AspectKind kind : nodeKinds) {
            Help help = computeHelp(kind, role, true, true);
            if (help != null) {
                result.put(help.getItem(), help.getTip());
            }
            help = computeHelp(kind, role, true, false);
            if (help != null) {
                result.put(help.getItem(), help.getTip());
            }
        }
        return result;
    }

    private static Map<String,String> computeEdgeDocMap(GraphRole role) {
        Map<String,String> result = new TreeMap<>();
        Set<AspectKind> edgeKinds = EnumSet.copyOf(allowedEdgeKinds.get(role));
        edgeKinds.remove(LET);
        edgeKinds.remove(TEST);
        if (role == GraphRole.TYPE) {
            for (AspectKind kind : edgeKinds) {
                if (kind.hasSort()) {
                    edgeKinds.remove(kind);
                }
            }
        }
        for (AspectKind kind : edgeKinds) {
            Help help = computeHelp(kind, role, false, true);
            if (help != null) {
                result.put(help.getItem(), help.getTip());
            }
        }
        return result;
    }

    private static Help computeHelp(AspectKind kind, GraphRole role, boolean forNode,
                                    boolean withLabel) {
        String h = null;
        String s = null;
        List<String> b = new ArrayList<>();
        List<String> p = new ArrayList<>();
        String qBody = "The optional %1$s denotes an associated quantifier level.";
        String qPar = "optional associated quantifier level";
        String flagPar = "text of the flag; must consist of letters, digits, '$', '-' or '_'";
        String edgePar = "text of the edge; must consist of letters, digits, '$', '-' or '_'";
        switch (kind) {
        case ABSTRACT:
            if (!forNode) {
                s = "%s.COLON.label";
                h = "Abstract edge type";
                b.add("Declares an abstract %s-edge between node types.");
                b
                    .add("The edge can only occur between subtypes where it is redeclared concretely.");
                p.add(edgePar);
            } else if (withLabel) {
                s = "%s.COLON.flag";
                h = "Abstract flag";
                b.add("Declares an abstract %s for a node type.");
                b.add("The flag can only occur on subtypes where it is redeclared concretely.");
                p.add(flagPar);
            } else {
                s = "%s.COLON";
                h = "Abstract node type";
                b.add("Declares a node type to be abstract.");
                b.add("Only nodes of concrete subtypes can actually exist.");
            }
            break;

        case ADDER:
            if (!forNode) {
                s = "%s[EQUALS.q]COLON.label";
                h = "Conditional edge creator";
                b.add("Tests for the absence of a %2$s-edge; creates it when applied.");
                b.add(qBody);
                p.add(qPar);
                p.add(edgePar);
            } else if (withLabel) {
                s = "%s[EQUALS.q]COLON.flag";
                h = "Conditional flag creator";
                b.add("Tests for the absence of %2$s; creates it when applied.");
                b.add(qBody);
                p.add(qPar);
                p.add(flagPar);
            } else {
                s = "%s.COLON";
                h = "Conditional node creator";
                b.add("Tests for the absence of a node; creates it when applied.");
            }
            break;
        case ARGUMENT:
            s = "%s.COLON.nr";
            h = "Argument edge";
            b.add("Projects a product node onto argument %s.");
            p.add("argument number, ranging from 0 to the product node arity - 1");
            break;

        case BOOL:
            if (!forNode) {
                s = "%s.COLON.op";
                h = "Boolean operator";
                b.add("Applies operation %s from the BOOL signature");
                b.add("to the arguments of the source PRODUCT node.");
                p.add("boolean operator: one of " + ops(kind));
            } else if (withLabel) {
                if (role == GraphRole.TYPE) {
                    s = "%s.COLON.field";
                    h = "Boolean field";
                    b.add("Declares %s to be a boolean-valued field.");
                } else {
                    s = "%s.COLON.(TRUE|FALSE)";
                    h = "Boolean constant";
                    b.add("Represents a constant boolean value (TRUE or FALSE).");
                }
                //            } else if (role == GraphRole.TYPE) {
                //                s = "%s.COLON";
                //                h = "Boolean type node";
                //                b.add("Represents the type of booleans.");
            } else if (role == GraphRole.RULE) {
                s = "%s.COLON";
                h = "Boolean variable";
                b.add("Declares a boolean-valued variable node.");
            }
            break;

        case COLOR:
            s = "%s.COLON.(rgb|name)";
            h = "Node type colour";
            b.add("Sets the colour of the nodes and outgoing edges of a type.");
            p.add("comma-seperated list of three colour dimensions, with range 0..255");
            p.add("color name");
            break;

        case COMPOSITE:
            s = "%s.COLON.label";
            h = "Composite edge property";
            b.add("Declares an edge to be composite.");
            b.add(Help.it("Currently unsupported."));
            break;

        case CONNECT:
            s = "%s.COLON";
            h = "Embargo choice";
            b.add("Declares a choice between two negative application patterns.");
            break;

        case CREATOR:
            if (!forNode) {
                s = "%s[EQUALS.q]COLON.label";
                h = "Edge creator";
                b.add("Creates a %2$s-edge when applied.");
                b.add(qBody);
                p.add(qPar);
                p.add("label of the created edge");
            } else if (withLabel) {
                s = "%s[EQUALS.q]COLON.flag";
                h = "Flag creator";
                b.add("Creates a %2$s when applied.");
                b.add(qBody);
                p.add(qPar);
                p.add("created flag; should be preceded by FLAG COLON");
            } else {
                s = "%s.COLON";
                h = "Node creator";
                b.add("Creates a node when applied.");
            }
            break;

        case EDGE:
            s = "%s.COLON.QUOTE.format.QUOTE.[COMMA.field]+";
            h = "Nodifier edge pattern";
            b.add("Declares the node type to be a nodified edge,");
            b.add("meaning that it will not be displayed as a node.");
            b.add("Instead, the incoming edges will be labelled by expanding %s");
            b.add("with string representations of the concrete values of the %s list");
            p.add("Label format, with parameter syntax as in <tt>String.format</tt>");
            p.add("Comma-separated list of attribute field names");
            break;

        case EMBARGO:
            if (!forNode) {
                s = "%s[EQUALS.q]COLON.label";
                h = "Edge embargo";
                b.add("Tests for the absence of a %2$s-edge.");
                b.add(qBody);
                p.add(qPar);
                p.add("label of the forbidden edge");
            } else if (withLabel) {
                s = "%s[EQUALS.q]COLON.flag";
                h = "Flag embargo";
                b.add("Tests for the absence of a %2$s.");
                b.add(qBody);
                p.add(qPar);
                p.add("forbidden flag; should be preceded by FLAG COLON");
            } else {
                s = "%s.COLON";
                h = "Node embargo";
                b.add("Tests for the absence of a node.");
            }
            break;

        case ERASER:
            if (!forNode) {
                s = "%s[EQUALS.q]COLON.label";
                h = "Edge eraser";
                b.add("Tests for the presence of a %2$s-edge; deletes it when applied.");
                b.add(qBody);
                p.add(qPar);
                p.add("label of the erased edge");
            } else if (withLabel) {
                s = "%s[EQUALS.q]COLON.flag";
                h = "Flag eraser";
                b.add("Tests for the presence of a %2$s; deletes it when applied.");
                b.add(qBody);
                p.add(qPar);
                p.add("erased flag; should be preceded by FLAG COLON");
            } else {
                s = "%s.COLON";
                h = "Node eraser";
                b.add("Tests for the presence of a node; deletes it when applied.");
            }
            break;

        case EXISTS:
            s = "%s[EQUALS.q]COLON";
            h = "Existential quantification";
            b.add("Tests for the mandatory existence of a graph pattern.");
            b.add("Pattern nodes must have outgoing AT-edges to the quantifier.");
            b.add("Pattern edges may be declared through the optional quantifier level %1$s.");
            p.add("declared name for this quantifier level");
            break;

        case EXISTS_OPT:
            s = "%s[EQUALS.q]COLON";
            h = "Optional existential quantification";
            b.add("Tests for the optional existence of a graph pattern.");
            b.add("Pattern nodes must have outgoing AT-edges to the quantifier.");
            b.add("Pattern edges may be declared through the optional quantifier level %1$s.");
            p.add("declared name for this quantifier level");
            break;

        case FORALL:
            s = "%s[EQUALS.q]COLON";
            h = "Universal quantification";
            b.add("Matches all occurrences of a graph pattern.");
            b.add("The actual number of occurrences is given by an optional outgoing COUNT-edge.");
            b.add("Pattern nodes must have outgoing AT-edges to the quantifier.");
            b.add("Pattern edges may be declared through the optional quantifier level %1$s.");
            p.add("declared name for this quantifier level");
            break;

        case FORALL_POS:
            s = "%s[EQUALS.q]COLON";
            h = "Non-vacuous universal quantification";
            b.add("Matches all occurrences of a graph pattern, provided there is at least one.");
            b.add("The actual number of occurrences is given by an optional outgoing COUNT-edge.");
            b.add("Pattern nodes must have outgoing AT-edges to the quantifier.");
            b.add("Pattern edges may be declared through the optional quantifier level %1$s.");
            p.add("declared name for this quantifier level");
            break;

        case ID:
            s = "%s.COLON.name";
            h = "Node identifier";
            b.add("Assigns an internal node identifier %s.");
            p.add("the declared name for this node");
            break;

        case IMPORT:
            s = "%s.COLON";
            h = "Imported node type";
            b.add("Indicates that the type is imported from another type graph.");
            b.add("This affects the behaviour of hiding (all elements of) a type graph.");
            break;

        case INT:
            if (!forNode) {
                s = "%s.COLON.op";
                h = "Integer operator";
                b.add("Applies operation %1$s from the INT signature");
                b.add("to the arguments of the source PRODUCT node.");
                p.add("integer operator: one of " + ops(kind));
            } else if (withLabel) {
                if (role == GraphRole.TYPE) {
                    s = "%s.COLON.field";
                    h = "Integer field";
                    b.add("Declares %s to be an integer-valued field.");
                } else {
                    s = "%s.COLON.nr";
                    h = "Integer constant";
                    b.add("Represents the constant integer value %1$s.");
                }
                //            } else if (role == GraphRole.TYPE) {
                //                s = "%s.COLON";
                //                h = "Integer type node";
                //                b.add("Represents the type of integers.");
            } else if (role == GraphRole.RULE) {
                s = "INT.COLON";
                h = "Integer variable";
                b.add("Declares an integer-valued variable node.");
            }
            break;

        case LET:
            s = "%s.COLON.name.EQUALS.expr";
            h = "Assignment";
            b.add("Assigns the value of %2$s to the attribute field %1$s.");
            break;

        case LET_NEW:
            s = "%s.COLON.name.EQUALS.expr";
            h = "Assignment";
            b.add("Assigns the value of %2$s to a new attribute field %1$s.");
            break;

        case LITERAL:
            s = "COLON.free";
            h = "Literal edge label";
            b.add("Specifies a %s-labelled edge, where %1$s may be an arbitrary string");
            p.add("a string of arbitrary characters");
            break;

        case MULT_IN:
            s = "%s.EQUALS[lo.DOT.DOT]hi COLON label";
            h = "Incoming edge multiplicity.";
            b.add("Constrains the number of incoming %3$s-edges for every node");
            b.add("to at least %1$s (if specified) and at most %2$s");
            b.add(Help.it("(This is currently unsupported."));
            p.add("optional lower bound");
            p.add("mandatory upper bound ('*' for unbounded)");
            p.add("label of the incoming edge");
            break;

        case MULT_OUT:
            s = "%s.EQUALS[lo.DOT.DOT]hi COLON label";
            h = "Outgoing edge multiplicity.";
            b.add("Constrains the number of outgoing %3$s-edges for every node");
            b.add("to at least %1$s (if specified) and at most %2$s");
            b.add(Help.it("(This is currently unsupported."));
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

        case ATOM:
            break;

        case PARAM_BI:
            if (withLabel) {
                s = "%s.COLON.nr";
                h = "Bidirectional rule parameter";
                b.add("Declares bidirectional rule parameter %1$s (ranging from 0).");
                b
                    .add("When used from a control program this parameter may be instantiated with a concrete value,");
                b.add("or be used as an output parameter, in which case the value");
                b.add("is determined by the matching.");
                p.add("the parameter number, ranging from 0");
            } else {
                s = "PARAM_BI.COLON";
                h = "Anchor node";
                b.add("Declares an explicit anchor node.");
                b.add("This causes the node to be considered relevant in distinguishing matches");
                b.add("even if it is not involved in any deletion, creation or merging.");
            }
            break;

        case PARAM_IN:
            s = "%s.COLON.nr";
            h = "Rule input parameter";
            b.add("Declares rule input parameter %s (ranging from 0).");
            break;

        case PARAM_OUT:
            s = "%s.COLON.nr";
            h = "Rule output parameter";
            b.add("Declares rule output parameter %s (ranging from 0).");
            break;

        case PARAM_ASK:
            s = "%s.COLON.nr";
            h = "Interactive rule parameter";
            b.add("Declares interactive rule parameter %s (ranging from 0).");
            break;

        case PATH:
            s = "%s.COLON.regexpr";
            h = "Regular path expression";
            b.add("Tests for a path satisfying the regular expression %1$s.");
            p.add("regular expression; for the syntax, consult the appropriate tab.");
            break;

        case PRODUCT:
            s = "%s.COLON";
            h = "Product node";
            b.add("Declares a product node, corresponding to a tuple of attribute nodes.");
            break;

        case READER:
            if (!forNode) {
                s = "%s.EQUALS.q.COLON.regexpr";
                h = "Quantified reader edge";
            } else if (withLabel) {
                s = "%s.EQUALS.q.COLON.flag";
                h = "Quantified reader flag";
            }
            b.add("Tests for the presence of %2$s on quantification level %1$s.");
            p.add(qPar);
            p.add("item tested for");
            break;

        case REAL:
            if (!forNode) {
                s = "%s.COLON.op";
                h = "Real-valued operator";
                b.add("Applies operation %1$s from the REAL signature");
                b.add("to the arguments of the source PRODUCT node.");
                p.add("real operator: one of " + ops(kind));
            } else if (withLabel) {
                if (role == GraphRole.TYPE) {
                    s = "%s.COLON.field";
                    h = "Real number field";
                    b.add("Declares %s to be a real number-valued field.");
                } else {
                    s = "%s.COLON.nr.DOT.nr";
                    h = "Real constant";
                    b.add("Represents the constant real value %1$s.%2$s.");
                }
                //            } else if (role == GraphRole.TYPE) {
                //                s = "%s.COLON";
                //                h = "Real type node";
                //                b.add("Represents the type of reals.");
            } else if (role == GraphRole.RULE) {
                s = "%s.COLON";
                h = "Real variable";
                b.add("Declares a real-valued variable node.");
            }
            break;

        case REMARK:
            if (forNode) {
                s = "%s.COLON";
                b.add("Declares a remark node, to be used for documentation");
            } else {
                s = "%s.COLON.text";
                b.add("Declares a remark edge with (free-formatted) text %1$s");
            }
            break;

        case STRING:
            if (!forNode) {
                s = "%s.COLON.op";
                h = "String operator";
                b.add("Applies operation %1$s from the STRING signature");
                b.add("to the arguments of the source PRODUCT node.");
                p.add("string operator: one of " + ops(kind));
            } else if (withLabel) {
                if (role == GraphRole.TYPE) {
                    s = "%s.COLON.field";
                    h = "String field";
                    b.add("Declares %s to be a string-valued field.");
                } else {
                    s = "%s.COLON.QUOTE.text.QUOTE";
                    h = "String constant";
                    b.add("Represents the constant string value %1$s.");
                }
                //            } else if (role == GraphRole.TYPE) {
                //                s = "%s.COLON";
                //                h = "String type node";
                //                b.add("Represents the type of strings.");
            } else if (role == GraphRole.RULE) {
                s = "%s.COLON";
                h = "String variable";
                b.add("Declares a string-valued variable node.");
            }
            break;

        case SUBTYPE:
            s = "%s.COLON";
            h = "Subtype declaration";
            b.add("Declares the source type node to be a subtype of the target type node");
            break;

        case TEST:
            if (withLabel) {
                s = "%s.COLON.constraint";
                h = "Predicate expression";
                b.add("Tests if the boolean expression %s holds in the graph.");
            } else {
                s = "%s.COLON.name.EQUALS.expr";
                h = "Attribute value test";
                b.add("Tests if the attribute field %1$s equals the value of %2$s.");
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

    /** Returns a list of operations from a given signature. */
    static private String ops(AspectKind kind) {
        StringBuilder result = new StringBuilder();
        assert kind.hasSort();
        for (OpValue op : Sort.getKind(kind.getName()).getOpValues()) {
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(Help.it(op.getOperator().getName()));
        }
        return result.toString();
    }

    /** For every relevant graph role the node syntax help entries. */
    private static final Map<GraphRole,Map<String,String>> nodeDocMapMap
        = new EnumMap<>(GraphRole.class);
    /** For every relevant graph role the edge syntax help entries. */
    private static final Map<GraphRole,Map<String,String>> edgeDocMapMap
        = new EnumMap<>(GraphRole.class);
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
            Sort sigKind = Sort.getKind(kind.getName());
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
    /** Set of attribute-related aspects. */
    public static final Set<AspectKind> attributers
        = EnumSet.of(ARGUMENT, STRING, INT, BOOL, REAL, TEST);

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
                        EXISTS_OPT, NESTED, LET, LET_NEW, TEST);
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
        /** Meta-attributes, determining the structure of the rule tree. */
        META(ROLE) {
            @Override
            boolean conflictsForNode(Category other, GraphRole forRole) {
                // META and ROLE don't go together for nodes, just for edges
                return other == ROLE;
            }
        },
        /** The four primitive sorts. */
        SORT(ROLE),
        /** Other attribute-related aspects. */
        ATTR(ROLE),
        /** Identifier declaration. */
        ID(ROLE, META, SORT) {
            @Override
            boolean conflictsForNode(Category other, GraphRole forRole) {
                // ID and SORT don't go together in host graphs
                return other == SORT && forRole == GraphRole.HOST;
            }
        },
        /** Parameter aspect. */
        PARAM(ROLE, SORT, ID),
        /** Label mode. */
        LABEL(ROLE, META),
        /** Colour declaration. */
        COLOR,
        /** Node type-related aspects. */
        TYPE,
        /** Import aspect. */
        IMPORT,
        /** Nodified edge declaration. */
        EDGE(COLOR),
        /** Incoming multiplicity declaration. */
        MULT_IN(LABEL),
        /** Outgoing multiplicity declaration. */
        MULT_OUT(SORT, LABEL, MULT_IN),
        /** Relational nature of an edge. */
        ASSOC(LABEL, MULT_IN, MULT_OUT),;

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
