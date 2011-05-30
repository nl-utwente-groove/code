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

import groove.annotation.Help;
import groove.graph.EdgeRole;
import groove.graph.GraphRole;
import groove.gui.jgraph.AspectJEdge;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.GraphJGraph;
import groove.gui.jgraph.JAttr;
import groove.rel.RegExpr;
import groove.util.Pair;
import groove.view.aspect.AspectKind;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
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
        setEnabledBackground(JAttr.EDITOR_BACKGROUND);
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
        initSyntax();
        JPanel result = new JPanel();
        result.setLayout(new BorderLayout());
        result.add(new JLabel("Allowed labels:"), BorderLayout.NORTH);
        final JTabbedPane tabbedPane = new JTabbedPane();
        final int nodeTabIndex = tabbedPane.getTabCount();
        tabbedPane.addTab("Nodes", createSyntaxList(this.nodeKeys));
        final int edgeTabIndex = tabbedPane.getTabCount();
        tabbedPane.addTab("Edges", createSyntaxList(this.edgeKeys));
        if (this.role == GraphRole.RULE) {
            tabbedPane.addTab("RegExpr",
                createSyntaxList(RegExpr.getDocMap().keySet()));
        }
        result.add(tabbedPane, BorderLayout.CENTER);
        // add a listener that switches the syntax help between nodes and edges
        // when a cell edit is started in the JGraph
        this.editor.getJGraph().addPropertyChangeListener(
            new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName() == GraphJGraph.CELL_EDIT_PROPERTY) {
                        int index =
                            evt.getNewValue() instanceof AspectJEdge
                                    ? edgeTabIndex : nodeTabIndex;
                        tabbedPane.setSelectedIndex(index);
                    }
                }
            });
        return result;
    }

    /**
     * Creates and returns a list of aspect descriptions.
     * @param data the data for the {@link JList}
     */
    private JComponent createSyntaxList(Collection<String> data) {
        final JList list = new JList();
        list.setCellRenderer(new MyCellRenderer());
        list.setBackground(JAttr.EDITOR_BACKGROUND);
        list.setListData(data.toArray());
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
        list.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                // do nothing
            }

            @Override
            public void setLeadSelectionIndex(int leadIndex) {
                // do nothing
            }
        });
        return createLabelScrollPane(list);
    }

    /**
     * Initialises the syntax descriptions of all aspect kinds of this 
     * editor's graph mode.
     */
    private void initSyntax() {
        if (this.nodeKeys != null) {
            return;
        }
        this.nodeKeys =
            new TreeSet<String>(AspectKind.getNodeDocMap(this.role).keySet());
        this.edgeKeys =
            new TreeSet<String>(AspectKind.getEdgeDocMap(this.role).keySet());
        // the edge role description for binary edges in rule graphs is inappropriate
        Help extra = null;
        for (Map.Entry<EdgeRole,Pair<String,String>> entry : EdgeRole.getRoleToDocMap().entrySet()) {
            String item = entry.getValue().one();
            switch (entry.getKey()) {
            case BINARY:
                if (this.role == GraphRole.RULE) {
                    extra = EdgeRole.createHelp();
                    extra.setSyntax("regexpr");
                    extra.setHeader("Regular expression path");
                    extra.setBody(
                        "An unadorned edge label in a rule by default denotes a regular expression.",
                        "This means that labels with non-standard characters need to be quoted, or preceded with 'COLON'.");
                    this.edgeKeys.add(extra.getItem());
                } else {
                    this.edgeKeys.add(item);
                }
                break;
            case FLAG:
            case NODE_TYPE:
                this.nodeKeys.add(item);
                break;
            default:
                assert false;
            }
        }
        this.docMap = new HashMap<String,String>();
        this.docMap.putAll(AspectKind.getNodeDocMap(this.role));
        this.docMap.putAll(AspectKind.getEdgeDocMap(this.role));
        this.docMap.putAll(EdgeRole.getDocMap());
        this.docMap.putAll(RegExpr.getDocMap());
        if (extra != null) {
            this.docMap.put(extra.getItem(), extra.getTip());
        }
    }

    /**
     * Comment for <code>editor</code>
     */
    private final Editor editor;
    private final GraphRole role;
    /** Mapping from syntax documentation items to corresponding tool tips. */
    private Map<String,String> docMap;
    private Set<String> nodeKeys;
    private Set<String> edgeKeys;

    /** Private cell renderer class that inserts the correct tool tips. */
    private class MyCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            Component result =
                super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);
            if (result == this) {
                setToolTipText(EditorJGraphPanel.this.docMap.get(value));
            }
            return result;
        }
    }
}