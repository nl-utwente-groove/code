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
 * $Id$
 */
package groove.gui.display;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import groove.control.parse.CtrlDoc;
import groove.grammar.QualName;
import groove.grammar.model.ResourceKind;
import groove.gui.Simulator;
import groove.gui.SimulatorModel;
import groove.gui.SimulatorModel.Change;

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
    ControlDisplay(Simulator simulator) {
        super(simulator, ResourceKind.CONTROL);
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        getSimulatorModel().addListener(this, Change.GRAMMAR, Change.CONTROL);
    }

    @Override
    protected JComponent createInfoPanel() {
        return new TitledPanel("Control syntax", getDocPane(), null, true);
    }

    @Override
    protected void buildInfoPanel() {
        // do nothing
    }

    private JTree getDocPane() {
        if (this.docPane == null) {
            this.docPane = createDocPane();
        }
        return this.docPane;
    }

    private JTree createDocPane() {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        for (Map.Entry<?,? extends List<?>> docEntry : getDoc().getItemTree()
            .entrySet()) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(docEntry.getKey());
            root.add(node);
            for (Object rule : docEntry.getValue()) {
                node.add(new DefaultMutableTreeNode(rule));
            }
        }
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
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) result.getCellRenderer();
        renderer.setBackgroundNonSelectionColor(null);
        renderer.setBackgroundSelectionColor(null);
        renderer.setTextSelectionColor(null);
        renderer.setLeafIcon(null);
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);
        result.setCellRenderer(renderer);
        ToolTipManager.sharedInstance()
            .registerComponent(result);
        result.addMouseListener(new DismissDelayer(result));
        for (int i = 0; i < root.getChildCount(); i++) {
            result
                .expandPath(new TreePath(((DefaultMutableTreeNode) root.getChildAt(i)).getPath()));
        }
        result.setBackground(null);
        return result;
    }

    private String getToolTip(Object value) {
        if (this.toolTipMap == null) {
            this.toolTipMap = getDoc().getToolTipMap();
        }
        return this.toolTipMap.get(value);
    }

    /** Tool type map for syntax help. */
    private Map<?,String> toolTipMap;

    /** The control document object. */
    private CtrlDoc getDoc() {
        if (this.doc == null) {
            this.doc = new CtrlDoc();
        }
        return this.doc;
    }

    private CtrlDoc doc;

    @Override
    protected JToolBar createListToolBar(int separation) {
        JToolBar result = super.createListToolBar(separation);
        result.add(getActions().getPreviewControlAction());
        return result;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel, Set<Change> changes) {
        super.update(source, oldModel, changes);
        if (suspendListening()) {
            QualName selection = source.getSelected(ResourceKind.CONTROL);
            getDocPane().setBackground(selection == null ? null : Color.WHITE);
            activateListening();
        }
    }

    /** Documentation tree. */
    private JTree docPane;
}
