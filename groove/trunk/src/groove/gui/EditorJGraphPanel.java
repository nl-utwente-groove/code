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
package groove.gui;

import static groove.io.HTMLConverter.HTML_LINEBREAK;
import static groove.io.HTMLConverter.HTML_TAG;
import static groove.io.HTMLConverter.ITALIC_TAG;
import static groove.io.HTMLConverter.STRONG_TAG;
import static groove.view.aspect.AspectKind.NestedValue.AT;
import static groove.view.aspect.AspectKind.NestedValue.COUNT;
import static groove.view.aspect.AspectKind.NestedValue.IN;
import groove.algebra.Algebras;
import groove.graph.EdgeRole;
import groove.graph.GraphRole;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.JAttr;
import groove.io.HTMLConverter;
import groove.io.HTMLConverter.HTMLTag;
import groove.util.Pair;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectKind.NestedValue;
import groove.view.aspect.AspectParser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;

class EditorJGraphPanel extends JGraphPanel<AspectJGraph> {
    public EditorJGraphPanel(Editor editor) {
        super(editor.getJGraph(), false);
        this.editor = editor;
        this.role = editor.getRole();
    }

    @Override
    protected JToolBar createToolBar() {
        return this.editor.createToolBar();
    }

    @Override
    protected JComponent createLabelPane() {
        JComponent labelPane = super.createLabelPane();
        JSplitPane result =
            new JSplitPane(JSplitPane.VERTICAL_SPLIT, labelPane,
                createSyntaxHelp());
        return result;
    }

    /** Creates and returns a panel for the syntax descriptions. */
    private Component createSyntaxHelp() {
        JPanel result = new JPanel();
        result.setLayout(new BorderLayout());
        result.add(new JLabel("Allowed labels:"), BorderLayout.NORTH);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Nodes", createAspectList(true));
        tabbedPane.addTab("Edges", createAspectList(false));
        result.add(tabbedPane, BorderLayout.CENTER);
        return result;
    }

    /**
     * Creates and returns a list of aspect descriptions.
     * @param forNode if {@code true}, returns the node aspect descriptions,
     * otherwise returns the edge aspect descriptions
     */
    private JComponent createAspectList(boolean forNode) {
        final JList list = new JList();
        list.setCellRenderer(new MyCellRenderer());
        list.setBackground(JAttr.EDITOR_BACKGROUND);
        list.setListData(createData(forNode).toArray());
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (e.getSource() == list) {
                    this.manager.setDismissDelay(Integer.MAX_VALUE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (e.getSource() == list) {
                    this.manager.setDismissDelay(this.standardDelay);
                }
            }

            private final ToolTipManager manager =
                ToolTipManager.sharedInstance();
            private final int standardDelay = this.manager.getDismissDelay();
        });
        return createLabelScrollPane(list);
    }

    /** Returns the list data for a list of aspect descriptions. */
    private Collection<? extends Object> createData(boolean forNode) {
        initSyntax();
        return (forNode ? this.nodeSyntaxMap : this.edgeSyntaxMap).keySet();
    }

    /**
     * Initialises the syntax descriptions of all aspect kinds of this 
     * editor's graph mode.
     */
    private void initSyntax() {
        if (this.nodeSyntaxMap != null) {
            return;
        }
        this.nodeSyntaxMap =
            new TreeMap<String,Pair<? extends Enum<?>,Boolean>>();
        this.edgeSyntaxMap = new TreeMap<String,Enum<?>>();
        for (AspectKind kind : EnumSet.allOf(AspectKind.class)) {
            if (AspectKind.allowedNodeKinds.get(this.role).contains(kind)) {
                initSyntax(kind, true, true);
                initSyntax(kind, true, false);
            }
            if (AspectKind.allowedEdgeKinds.get(this.role).contains(kind)) {
                initSyntax(kind, false, true);
            }
        }
        for (EdgeRole role : EnumSet.allOf(EdgeRole.class)) {
            initSyntax(role);
        }
    }

    /** 
     * Initialises the syntax description for a given aspect kind.
     * @param kind the aspect kind to initialise the description for
     * @param forNode if {@code true}, take the node version of the aspect,
     * otherwise take the edge version
     */
    private void initSyntax(AspectKind kind, boolean forNode, boolean withLabel) {
        if (kind == AspectKind.NONE) {
            return;
        }
        // the fragment before the colon
        String pre = "";
        // the fragment after the colon; if null, no syntax is created for this element
        String post;
        switch (kind.getContentKind()) {
        case BOOL_LITERAL:
        case INT_LITERAL:
        case REAL_LITERAL:
        case STRING_LITERAL:
            pre = "";
            if (this.role == GraphRole.TYPE || !withLabel) {
                post = this.role == GraphRole.HOST ? null : "";
            } else if (forNode) {
                post = s(v(VAL_I));
            } else {
                post = s(v(OP_I));
            }
            break;
        case COLOR:
            post = s(v(RGB_I)) + "|" + s(v(NAME_I));
            break;
        case EMPTY:
            post = "";
            break;
        case LET_EXPR:
            post = s(v(NAME_I) + "=" + v(EXPR_I));
            break;
        case LEVEL:
            pre = v(Q_I);
            if (kind.isQuantifier()) {
                post = forNode ? "" : null;
            } else if (forNode) {
                post = withLabel ? s(EdgeRole.FLAG.getPrefix() + LABEL_V) : "";
            } else {
                post = LABEL_S;
            }
            break;
        case MULTIPLICITY:
            pre = v(LO_I) + ".." + s(v(HI_I));
            post = LABEL_S;
            break;
        case NAME:
            post = s(v(NAME_I));
            break;
        case NESTED:
            post = "";
            for (NestedValue value : EnumSet.allOf(NestedValue.class)) {
                if (post.length() > 0) {
                    post += "|";
                }
                post += s(value);
            }
            break;
        case NONE:
            if (forNode) {
                if (kind == AspectKind.ABSTRACT && withLabel) {
                    post = s(EdgeRole.FLAG.getPrefix() + LABEL_V);
                } else {
                    post = "";
                }
            } else if (kind == AspectKind.REMARK || kind == AspectKind.LITERAL) {
                post = s(v(FREE_I));
            } else if (kind == AspectKind.PATH) {
                post = s(v(REGEXPR_I));
            } else {
                post = LABEL_S;
            }
            break;
        case NUMBER:
            post = NR_S;
            break;
        case PARAM:
            if (kind == AspectKind.PARAM_BI && !withLabel) {
                post = "";
            } else {
                post = NR_S;
            }
            break;
        case PRED_VAL:
            post = s(v(CONSTRAINT_I));
            break;
        default:
            throw new IllegalStateException();
        }
        if (post != null) {
            String tip = toSyntax(kind, pre, post);
            if (forNode) {
                this.nodeSyntaxMap.put(tip, Pair.newPair(kind, withLabel));
            } else {
                this.edgeSyntaxMap.put(tip, kind);
            }
        }
    }

    private void initSyntax(EdgeRole role) {
        String text;
        switch (role) {
        case BINARY:
            if (this.role == GraphRole.RULE) {
                text = s(v(REGEXPR_I));
            } else {
                text = LABEL_S;
            }
            this.edgeSyntaxMap.put(HTML_TAG.on(text), role);
            break;
        case FLAG:
        case NODE_TYPE:
            text = s(role.getPrefix() + LABEL_V);
            this.nodeSyntaxMap.put(HTML_TAG.on(text),
                new Pair<Enum<?>,Boolean>(role, true));
            break;
        default:
            throw new IllegalStateException();
        }
    }

    private String createTip(AspectKind kind, boolean forNode, boolean withLabel) {
        // header
        String h = null;
        // main body
        Tip t = new Tip();
        // parameter spec
        Map<String,String> p = new HashMap<String,String>();
        String levelLine = "Optionally associates a quantifier level " + Q_I;
        String flagPar =
            "text of the flag; must consist of letters, digits, '$', '-' or '_'";
        String edgePar =
            "text of the edge; must consist of letters, digits, '$', '-' or '_'";
        switch (kind) {
        case ABSTRACT:
            if (forNode) {
                if (withLabel) {
                    h = "Abstract flag type";
                    t.add("Declares an abstract %s-flag for a node type.",
                        LABEL_I);
                    t.add(
                        "The flag can only occur on subtypes where it is redeclared concretely.",
                        LABEL_I);
                    p.put(LABEL_I, flagPar);
                } else {
                    h = "Abstract flag type";
                    t.add("Declares a node type to be abstract.");
                    t.add("Only nodes of concrete subtypes can actually exist.");
                }
            } else {
                h = "Abstract edge type";
                t.add("Declares an abstract %s-edge between node types.",
                    LABEL_I);
                t.add(
                    "The edge can only occur between subtypes where it is redeclared concretely.",
                    LABEL_I);
                p.put(LABEL_I, edgePar);
            }
            break;

        case ADDER:
            if (forNode) {
                if (withLabel) {
                    h = "Conditional flag creator";
                    t.add(
                        "Tests for the absence of a %s-flag; creates it when applied.",
                        LABEL_I);
                    p.put(LABEL_I, flagPar);
                } else {
                    h = "Conditional node creator";
                    t.add("Tests for the absence of a node; creates it when applied.");
                }
            } else {
                h = "Conditional edge creator";
                t.add(
                    "Tests for the absence of a %s-edge; creates it when applied.",
                    LABEL_I);
                p.put(LABEL_I, edgePar);
            }
            t.add(levelLine);
            break;

        case ARGUMENT:
            h = "Argument edge";
            t.add("Projects a product node onto argument %s (ranging from 0).",
                NR_I);
            p.put(NR_I,
                "Argument number, ranging from 0 to the product node arity - 1");
            break;

        case BOOL:
            if (forNode) {
                if (withLabel) {
                    h = "Boolean constant";
                    t.add("Represents the boolean value %s (%s or %s).", VAL_I,
                        i("true"), i("false"));
                    p.put(VAL_I, "boolean value: either " + i("true") + " or "
                        + i("false"));
                } else if (this.role == GraphRole.TYPE) {
                    h = "Boolean type node";
                    t.add("Represents the type of booleans.");
                } else {
                    h = "Boolean variable";
                    t.add("Declares a boolean-valued variable node.");
                }
            } else {
                h = "Boolean operator";
                t.add("Applies operation %s from the %s signature.",
                    s(kind.getName()), OP_I);
                p.put(OP_I, "boolean operator: one of " + ops(kind));
            }
            break;

        case COLOR:
            t.add("Sets the color of the nodes and outgoing edges of a type.");
            t.add(
                "The color is either specified as %s (with values in 0..255) or by %s.",
                RGB_I, NAME_I);
            break;

        case COMPOSITE:
            t.add("Declares an edge to be composite %s.", i("(unsupported)"));
            break;

        case CONNECT:
            t.add("Declares a choice between two negative application patterns.");
            break;

        case CREATOR:
            if (forNode) {
                if (withLabel) {
                    t.add("Creates a %s-flag when applied.", LABEL_I);
                } else {
                    t.add("Creates a node when applied.");
                }
            } else {
                t.add("Creates a %s-edge when applied.", LABEL_I);
            }
            t.add(levelLine);
            break;

        case EMBARGO:
            if (forNode) {
                if (withLabel) {
                    t.add("Tests for the absence of a %s-flag.", LABEL_I);
                } else {
                    t.add("Tests for the absence of a node.");
                }
            } else {
                t.add("Tests for the absence of a %s-edge.", LABEL_I);
            }
            t.add(levelLine);
            break;

        case ERASER:
            if (forNode) {
                if (withLabel) {
                    t.add(
                        "Tests for the presence of a %s-flag; deletes it when applied.",
                        LABEL_I);
                } else {
                    t.add("Tests for the presence of a node; deletes it when applied.");
                }
            } else {
                t.add(
                    "Tests for the presence of a %s-edge; deletes it when applied.",
                    LABEL_I);
            }
            t.add(levelLine);
            break;

        case EXISTS:
            t.add("Tests for the mandatory existence of a graph pattern.");
            t.add(
                "Pattern nodes must have outgoing %s-edges to the quantifier.",
                i(AT));
            t.add(
                "Pattern edges may be declared through the optional quantifier level %s.",
                Q_I);
            break;

        case EXISTS_OPT:
            t.add("Tests for the optional existence of a graph pattern.");
            t.add(
                "Pattern nodes must have outgoing %s-edges to the quantifier.",
                i(AT));
            t.add(
                "Pattern edges may be declared through the optional quantifier level %s.",
                Q_I);
            break;

        case FORALL:
            t.add("Matches all occurrences of a graph pattern.");
            t.add("The number of occurrences is given by an outgoing %s-edge.",
                i(COUNT));
            t.add(
                "Pattern nodes must have outgoing %s-edges to the quantifier.",
                i(AT));
            t.add(
                "Pattern edges may be declared through the optional quantifier level %s.",
                Q_I);
            break;

        case FORALL_POS:
            t.add("Matches all occurrences of a graph pattern, provided there is at least one.");
            t.add("The number of occurrences is given by an outgoing %s-edge.",
                i(COUNT));
            t.add(
                "Pattern nodes must have outgoing %s-edges to the quantifier.",
                i(AT));
            t.add(
                "Pattern edges may be declared through the optional quantifier level %s",
                Q_I);
            break;

        case ID:
            t.add("Assigns an internal node identifier %s.", NAME_I);
            break;

        case INT:
            if (forNode) {
                if (withLabel) {
                    t.add("Represents the integer number value %s.", VAL_I);
                } else if (this.role == GraphRole.TYPE) {
                    t.add("Represents the type of integer numbers.");
                } else {
                    t.add("Declares a integer-valued variable node.");
                }
            } else {
                t.add(
                    "Applies operation %s from the int signature: one of %s.",
                    OP_I, ops(kind));
            }
            break;

        case LET:
            t.add("Assigns a new attibute %s to an outgoing %s-edge %s.",
                EXPR_I, NAME_I, UNSUPPORTED);
            break;

        case LITERAL:
            t.add(
                "Specifies a %s-labelled edge, containing arbitrary characters.",
                FREE_I);
            break;

        case MULT_IN:
            t.add("Incoming edge multiplicity %s.", UNSUPPORTED);
            t.add(
                "Optional lower bound %s, mandatory upper bound %s ('*' for unbounded).",
                LO_I, HI_I);
            break;

        case MULT_OUT:
            t.add("Outgoing edge multiplicity %s.", UNSUPPORTED);
            t.add(
                "Optional lower bound %s, mandatory upper bound %s ('*' for unbounded).",
                LO_I, HI_I);
            break;

        case NESTED:
            t.add(
                "Declares quantifier structure (the %s-prefix itself is optional):",
                i(kind.getName()));
            t.add("<li> %s nests one quantifier withing another;", i(IN));
            t.add("<li> %s connects a graph pattern node to a quantifier;",
                i(AT));
            t.add("<li> %s points to the cardinality of a quantifier.",
                i(COUNT));
            break;

        case NONE:
            break;

        case PARAM_BI:
            if (withLabel) {
                t.add(
                    "Declares bidirectional rule parameter %s (ranging from 0).",
                    NR_I);
            } else {
                t.add("Declares an explicit anchor node.");
            }
            break;

        case PARAM_IN:
            t.add("Declares rule input parameter %s (ranging from 0).", NR_I);
            break;

        case PARAM_OUT:
            t.add("Declares rule output parameter %s (ranging from 0).", NR_I);
            break;

        case PATH:
            t.add("Tests for a path satisfying the regular expression %s.",
                REGEXPR_I);
            break;

        case PRED:
            t.add("Specifies an attribute constraint.", CONSTRAINT_I);
            break;

        case PRODUCT:
            t.add("Declares a product node, corresponding to a tuple of attribute nodes.");
            break;

        case READER:
            if (forNode) {
                if (withLabel) {
                    t.add("Tests for the presence of a %s-flag.", LABEL_I);
                } else {
                    t.add("Tests for the presence of a node.");
                }
            } else {
                t.add("Tests for the presence of a %s-edge.", LABEL_I);
            }
            t.add(levelLine);
            break;

        case REAL:
            if (forNode) {
                if (withLabel) {
                    t.add("Represents the real number value %s.", VAL_I);
                } else if (this.role == GraphRole.TYPE) {
                    t.add("Represents the type of real numbers.");
                } else {
                    t.add("Declares a real-valued variable node.");
                }
            } else {
                t.add(
                    "Applies operation %s from the real signature: one of %s",
                    OP_I, ops(kind));
            }
            break;

        case REMARK:
            if (forNode) {
                t.add("Declares a remark node, to be used for documentation");
            } else {
                t.add("Declares a remark edge with text %s", FREE_I);
            }
            break;

        case STRING:
            if (forNode) {
                if (withLabel) {
                    t.add("Represents the string value %s", VAL_I);
                } else if (this.role == GraphRole.TYPE) {
                    t.add("Represents the type of string values");
                } else {
                    t.add("Declares a string-valued variable node");
                }
            } else {
                t.add(
                    "Applies operation %s from the string signature: one of %s",
                    OP_I, ops(kind));
            }
            break;

        case SUBTYPE:
            t.add("Declares the source type node to be a subtype of the target type node");
            break;

        case UNTYPED:
            t.add("Declares an untyped attribute variable node");
            break;

        default:
            throw new IllegalStateException();
        }
        if (h != null) {
            t.set(h);
        }
        t.set(p);
        return t.toHtml();
    }

    private String createTip(EdgeRole role, boolean forNode) {
        Tip t = new Tip();
        switch (role) {
        case BINARY:
            if (this.role == GraphRole.RULE) {
                t.add("Tests for a binary edge label or regular expression");
            } else {
                t.add("Specifies a binary edge label");
            }
            break;
        case FLAG:
            t.add("Specifies a flag (= non-type node label)");
            break;
        case NODE_TYPE:
            t.add("Specifies a node type");
            break;
        default:
            throw new IllegalStateException();
        }
        return t.toHtml();
    }

    /** Creates a HTML-formatted syntax description for a given aspect kind,
     * by combining the kind name, and text preceding an following the aspect
     * prefix separator {@link AspectParser#SEPARATOR}.
     * @param kind the kind to create a syntax description for
     * @param pre text preceding the separator
     * @param post text following the separator
     */
    private String toSyntax(AspectKind kind, String pre, String post) {
        StringBuilder result = new StringBuilder();
        if (kind != AspectKind.NONE) {
            result.append(STRONG_TAG.on(kind.getName()));
            if (pre.length() > 0) {
                result.append(AspectParser.ASSIGN);
            }
            result.append(pre);
            result.append(STRONG_TAG.on(AspectParser.SEPARATOR));
        }
        result.append(post);
        return HTML_TAG.on(result).toString();
    }

    /**
     * Comment for <code>editor</code>
     */
    private final Editor editor;
    private final GraphRole role;
    private Map<String,Pair<? extends Enum<?>,Boolean>> nodeSyntaxMap;
    private Map<String,Enum<?>> edgeSyntaxMap;

    /** Formats a given string as a required part in a syntax description. */
    static private String s(Object text) {
        return STRONG_TAG.on(text);
    }

    /** Formats a given string as a variable in a syntax description. */
    static private String v(Object id) {
        return HTMLConverter.HTML_LANGLE + id + HTMLConverter.HTML_RANGLE;
    }

    /** Formats an italic string in a syntax description. */
    static private String i(Object id) {
        return ITALIC_TAG.on(id);
    }

    /** Returns a list of operations from a given signature. */
    static private String ops(AspectKind kind) {
        StringBuilder result = new StringBuilder();
        assert kind.isTypedData();
        for (String opName : Algebras.getOperatorNames(kind.getName())) {
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(i(opName));
        }
        return result.toString();
    }

    /** Italic version of the syntax variable "label". */
    static private String LABEL_I = i("label");
    /** Angled version of the syntax variable "label". */
    static private String LABEL_V = v(LABEL_I);
    /** Strong version of the syntax variable "label". */
    static private String LABEL_S = s(LABEL_V);
    /** Italic version of the syntax variable "name". */
    static private String NAME_I = i("name");
    /** Italic version of the syntax variable "nr". */
    static private String NR_I = i("nr");
    /** Angled version of the syntax variable "nr". */
    static private String NR_V = v(NR_I);
    /** Strong version of the syntax variable "nr". */
    static private String NR_S = s(NR_V);
    /** Italic version of the syntax variable "q". */
    static private String Q_I = i("q");
    /** Italic version of the syntax variable "constraint". */
    static private String CONSTRAINT_I = i("constraint");
    /** Italic version of the syntax variable "regexpr". */
    static private String REGEXPR_I = i("regexpr");
    /** Italic version of the syntax variable "lo". */
    static private String LO_I = i("lo");
    /** Italic version of the syntax variable "hi". */
    static private String HI_I = i("hi");
    /** Italic version of the syntax variable "r,g,b". */
    static private String RGB_I = i("r,g,b");
    /** Italic version of the syntax variable "op". */
    static private String OP_I = i("op");
    /** Italic version of the syntax variable "val". */
    static private String VAL_I = i("val");
    /** Italic version of the syntax variable "expr". */
    static private String EXPR_I = i("expr");
    /** Italic version of the syntax variable "free". */
    static private String FREE_I = i("free");

    /** Tool tip announcement "unsupported". */
    static private String UNSUPPORTED = i("(unsupported)");

    /** Private cell renderer class that inserts the correct tool tips. */
    private class MyCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            Component result =
                super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);
            if (result == this) {
                boolean forNode =
                    EditorJGraphPanel.this.nodeSyntaxMap.containsKey(value);
                Enum<?> kind;
                boolean withLabel;
                if (forNode) {
                    Pair<? extends Enum<?>,Boolean> nodeKind =
                        EditorJGraphPanel.this.nodeSyntaxMap.get(value);
                    kind = nodeKind.one();
                    withLabel = nodeKind.two();
                } else {
                    kind = EditorJGraphPanel.this.edgeSyntaxMap.get(value);
                    withLabel = true;
                }
                String tip =
                    kind instanceof AspectKind ? createTip((AspectKind) kind,
                        forNode, withLabel) : createTip((EdgeRole) kind,
                        forNode);
                setToolTipText(tip);
            }
            return result;
        }
    }

    /** Class to facilitate the construction of a tool tip. */
    private static class Tip extends ArrayList<String> {
        /** Adds a formatted line to the tool tip text. */
        @Override
        public boolean add(String text) {
            if (text.charAt(text.length() - 1) != '.') {
                text += '.';
            }
            return super.add(text);
        }

        /** Adds a formatted line to the tool tip text. */
        public void add(String text, Object... args) {
            add(String.format(text, args));
        }

        /** Sets a header for the tool tip. */
        public void set(String header) {
            this.header = header;
        }

        /** Sets parameter documentation for the tool tip. */
        public void set(Map<String,String> param) {
            this.param = param;
        }

        /** Returns a HTML-formatted string concatenating the lines of this tool tip. */
        public String toHtml() {
            StringBuilder result = new StringBuilder();
            if (this.header != null) {
                result.append(STRONG_TAG.on(this.header));
                result.append(HTML_LINEBREAK);
                result.append(HTML_LINEBREAK);
            }
            for (String line : this) {
                if (result.length() > 0) {
                    result.append(" ");
                }
                result.append(line);
                if (line.charAt(line.length() - 1) != '.') {
                    result.append('.');
                }
            }
            if (this.param != null && !this.param.isEmpty()) {
                for (Map.Entry<String,String> par : this.param.entrySet()) {
                    result.append(HTML_LINEBREAK);
                    result.append("<li>");
                    result.append(STRONG_TAG.on(par.getKey()));
                    result.append(" - ");
                    result.append(par.getValue());
                }
            }
            return HTML_TAG.on(DIV_TAG.on(result)).toString();
        }

        private String header;
        private Map<String,String> param;
    }

    private static HTMLTag DIV_TAG =
        HTMLConverter.createDivTag("width: 300px;");
}