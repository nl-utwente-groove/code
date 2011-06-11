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
 * $Id: CAPanel.java,v 1.18 2008-03-18 12:18:19 fladder Exp $
 */
package groove.gui;

import groove.control.parse.CtrlDoc;
import groove.gui.SimulatorModel.Change;
import groove.io.HTMLConverter;
import groove.trans.ResourceKind;
import groove.view.GrammarModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * The Simulator panel that shows the control program, with a button that shows
 * the corresponding control automaton.
 * 
 * @author Tom Staijen
 * @version $0.9$
 */
final public class ControlDisplay extends ResourceDisplay {
    /**
     * @param simulator The Simulator the panel is added to.
     */
    public ControlDisplay(Simulator simulator) {
        super(simulator, ResourceKind.CONTROL);
        getSimulatorModel().addListener(this, Change.GRAMMAR, Change.CONTROL);
        activateListening();
    }

    @Override
    protected JComponent createDisplayPanel() {
        return new ControlPanel();
    }

    private JTree getDocPane() {
        if (this.docPane == null) {
            this.docPane = createDocPane();
        }
        return this.docPane;
    }

    private JTree createDocPane() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        final JTree result = new JTree(root) {
            @Override
            public String getToolTipText(MouseEvent evt) {
                if (getRowForLocation(evt.getX(), evt.getY()) == -1) {
                    return null;
                }
                TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
                Object userObject =
                    ((DefaultMutableTreeNode) curPath.getLastPathComponent()).getUserObject();
                return getToolTip(userObject);
            }

        };
        result.setRootVisible(false);
        result.setShowsRootHandles(true);
        DefaultTreeCellRenderer renderer =
            (DefaultTreeCellRenderer) result.getCellRenderer();
        renderer.setBackgroundNonSelectionColor(null);
        renderer.setBackgroundSelectionColor(null);
        renderer.setTextSelectionColor(null);
        renderer.setLeafIcon(null);
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);
        result.setCellRenderer(renderer);
        ToolTipManager.sharedInstance().registerComponent(result);
        result.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (e.getSource() == result) {
                    this.manager.setDismissDelay(Integer.MAX_VALUE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (e.getSource() == result) {
                    this.manager.setDismissDelay(this.standardDelay);
                }
            }

            private final ToolTipManager manager =
                ToolTipManager.sharedInstance();
            private final int standardDelay = this.manager.getDismissDelay();
        });
        CtrlDoc doc = new CtrlDoc();
        this.toolTipMap = doc.getToolTipMap();
        // load the tree
        for (Map.Entry<?,? extends List<?>> docEntry : doc.getItemTree().entrySet()) {
            DefaultMutableTreeNode node =
                new DefaultMutableTreeNode(docEntry.getKey());
            for (Object rule : docEntry.getValue()) {
                node.add(new DefaultMutableTreeNode(rule));
            }
            root.add(node);
        }
        ((DefaultTreeModel) result.getModel()).reload();
        for (int i = 0; i < root.getChildCount(); i++) {
            result.expandPath(new TreePath(
                ((DefaultMutableTreeNode) root.getChildAt(i)).getPath()));
        }
        result.setBackground(null);
        return result;
    }

    private String getToolTip(Object value) {
        String result = null;
        if (this.toolTipMap != null) {
            result = this.toolTipMap.get(value);
        }
        return result;
    }

    /** Returns the GUI component showing the list of control program names. */
    @Override
    public JPanel getListPanel() {
        if (this.listPanel == null) {
            JScrollPane controlPane = new JScrollPane(getList()) {
                @Override
                public Dimension getPreferredSize() {
                    Dimension superSize = super.getPreferredSize();
                    return new Dimension((int) superSize.getWidth(),
                        Simulator.START_LIST_MINIMUM_HEIGHT);
                }
            };

            this.listPanel = new JPanel(new BorderLayout(), false);
            this.listPanel.add(createListToolBar(), BorderLayout.NORTH);
            this.listPanel.add(controlPane, BorderLayout.CENTER);
            // make sure tool tips get displayed
            ToolTipManager.sharedInstance().registerComponent(this.listPanel);
        }
        return this.listPanel;
    }

    @Override
    protected JToolBar createListToolBar(int separation) {
        JToolBar result = super.createListToolBar(separation);
        result.add(getActions().getPreviewControlAction());
        return result;
    }

    /** Returns the list of control programs. */
    @Override
    final protected ResourceList getList() {
        if (this.controlJList == null) {
            this.controlJList = new ResourceList(this);
        }
        return this.controlJList;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        super.update(source, oldModel, changes);
        if (suspendListening()) {
            String selection = source.getSelected(ResourceKind.CONTROL);
            getDocPane().setBackground(selection == null ? null : Color.WHITE);
            activateListening();
        }
    }

    /**
     * Convenience method to return the current grammar view.
     */
    private GrammarModel getGrammar() {
        return getSimulatorModel().getGrammar();
    }

    @Override
    protected void decorateLabelText(String name, StringBuilder text) {
        if (name.equals(getGrammar().getActiveControlName())) {
            HTMLConverter.STRONG_TAG.on(text);
            HTMLConverter.HTML_TAG.on(text);
        }
    }

    /** Production system control program list. */
    private ResourceList controlJList;

    /** Documentation tree. */
    private JTree docPane;

    /** Panel with the {@link #controlJList}. */
    private JPanel listPanel;

    /** Tool type map for syntax help. */
    private Map<?,String> toolTipMap;

    /** Panel of this display. */
    private class ControlPanel extends JPanel implements Panel {
        /** Constructs an instance of the panel. */
        public ControlPanel() {
            // create the layout for this JPanel
            this.setLayout(new BorderLayout());
            setFocusable(false);
            JSplitPane splitPane =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getTabPane(),
                    new JScrollPane(getDocPane()));
            splitPane.setOneTouchExpandable(true);
            splitPane.setResizeWeight(1.0);

            add(splitPane, BorderLayout.CENTER);
        }

        @Override
        public Display getDisplay() {
            return ControlDisplay.this;
        }
    }
}
