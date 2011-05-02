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

import static groove.io.HTMLConverter.HTML_TAG;
import static groove.io.HTMLConverter.ITALIC_TAG;
import static groove.io.HTMLConverter.STRONG_TAG;
import groove.graph.GraphRole;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.JAttr;
import groove.io.HTMLConverter;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectKind.NestedValue;
import groove.view.aspect.AspectParser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

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

    private Component createSyntaxHelp() {
        JPanel result = new JPanel();
        result.setLayout(new BorderLayout());
        result.add(new JLabel("Prefixes:"), BorderLayout.NORTH);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Nodes", createAspectList(true));
        tabbedPane.addTab("Edges", createAspectList(false));
        result.add(tabbedPane, BorderLayout.CENTER);
        return result;
    }

    private JComponent createAspectList(boolean forNode) {
        JList list = new JList();
        list.setBackground(JAttr.EDITOR_BACKGROUND);
        list.setListData(createData(forNode).toArray());
        return createLabelScrollPane(list);
    }

    private Collection<? extends Object> createData(boolean forNode) {
        initSyntax();
        return forNode ? this.nodeSyntaxSet : this.edgeSyntaxSet;
    }

    private void initSyntax() {
        if (this.nodeSyntaxSet != null) {
            return;
        }
        this.nodeSyntaxSet = new TreeSet<String>();
        this.edgeSyntaxSet = new TreeSet<String>();
        for (AspectKind kind : EnumSet.allOf(AspectKind.class)) {
            if (AspectKind.allowedNodeKinds.get(this.role).contains(kind)) {
                initSyntax(kind, true);
            }
            if (AspectKind.allowedEdgeKinds.get(this.role).contains(kind)) {
                initSyntax(kind, false);
            }
        }
    }

    private void initSyntax(AspectKind kind, boolean forNode) {
        // the fragment before the colon
        String pre = "";
        // the fragment after the colon
        String post;
        switch (kind.getContentKind()) {
        case BOOL_LITERAL:
        case INT_LITERAL:
        case REAL_LITERAL:
        case STRING_LITERAL:
            pre = "";
            if (this.role == GraphRole.TYPE) {
                post = "";
            } else if (forNode) {
                post = var("const");
            } else {
                post = strong(var("op"));
            }
            break;
        case COLOR:
            post = strong(var("rgb")) + "|" + strong(var("name"));
            break;
        case EMPTY:
            post = "";
            break;
        case LET_EXPR:
            post = strong(var("name") + "=" + var("val"));
            break;
        case LEVEL:
            pre = var("level");
            post = forNode ? "" : strong(var("label"));
            break;
        case MULTIPLICITY:
            post = var("low") + ".." + strong(var("high"));
            break;
        case NAME:
            post = strong(var("name"));
            break;
        case NESTED:
            post = "";
            for (NestedValue value : EnumSet.allOf(NestedValue.class)) {
                if (post.length() > 0) {
                    post += "|";
                }
                post += strong(value.toString());
            }
            break;
        case NONE:
            post = forNode ? "" : strong(var("label"));
            break;
        case NUMBER:
            post = strong(var("nr"));
            break;
        case PARAM:
            post = var("nr");
            if (kind != AspectKind.PARAM_BI) {
                post = strong(post);
            }
            break;
        case PRED_VAL:
            post = strong(var("constraint"));
            break;
        default:
            throw new IllegalStateException();
        }
        Set<String> syntaxSet =
            forNode ? this.nodeSyntaxSet : this.edgeSyntaxSet;
        syntaxSet.add(toSyntax(kind, pre, post));
    }

    static private String strong(String text) {
        return STRONG_TAG.on(text);
    }

    static private String var(String id) {
        return HTMLConverter.toHtml("<") + ITALIC_TAG.on(id)
            + HTMLConverter.toHtml(">");
    }

    private String toSyntax(AspectKind kind, String pre, String post) {
        StringBuilder result = new StringBuilder(STRONG_TAG.on(kind.getName()));
        if (pre.length() > 0) {
            result.append(AspectParser.ASSIGN);
        }
        result.append(pre);
        result.append(STRONG_TAG.on(AspectParser.SEPARATOR));
        result.append(post);
        return HTML_TAG.on(result).toString();
    }

    /**
     * Comment for <code>editor</code>
     */
    private final Editor editor;
    private final GraphRole role;
    private Set<String> nodeSyntaxSet;
    private Set<String> edgeSyntaxSet;
}