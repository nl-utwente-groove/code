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
package groove.gui.action;

import groove.graph.Label;
import groove.graph.TypeLabel;
import groove.gui.Icons;
import groove.gui.JGraphPanel;
import groove.gui.LabelTree;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJGraph;
import groove.util.Duo;

import java.io.IOException;
import java.util.Collection;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;

/**
 * Action for changing one label into another throughout the grammar.
 */
public class RelabelGrammarAction extends SimulatorAction implements
        GraphSelectionListener, TreeSelectionListener {
    /** Constructs an instance of the action, for a given simulator. */
    public RelabelGrammarAction(Simulator simulator) {
        super(simulator, Options.RELABEL_ACTION_NAME, Icons.RENAME_ICON);
        addAsListener(getStateDisplay().getMainPanel());
        addAsListener(getRuleDisplay().getMainPanel());
        addAsListener(getTypeTab().getMainPanel());
    }

    /**
     * Adds this action as a listener to the {@link JGraph} and {@link LabelTree}
     * of a given {@link JGraphPanel}.
     */
    private void addAsListener(JGraphPanel<?> panel) {
        panel.getJGraph().addGraphSelectionListener(this);
        panel.getLabelTree().addTreeSelectionListener(this);
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getGrammar() != null
            && getSimulatorModel().getStore().isModifiable()
            && !getSimulatorModel().getGrammar().getLabelStore().getLabels().isEmpty());
    }

    @Override
    public boolean execute() {
        boolean result = false;
        Duo<TypeLabel> relabelling = askRelabelling(this.oldLabel);
        if (relabelling != null) {
            try {
                result =
                    getSimulator().getModel().doRelabel(relabelling.one(),
                        relabelling.two());
            } catch (IOException exc) {
                showErrorDialog(exc, String.format(
                    "Error while renaming '%s' into '%s':", relabelling.one(),
                    relabelling.two()));
            }
        }
        return result;
    }

    /** Sets {@link #oldLabel} based on the {@link GraphJGraph} selection. */
    @Override
    public void valueChanged(GraphSelectionEvent e) {
        this.oldLabel = null;
        Object[] selection = ((GraphJGraph) e.getSource()).getSelectionCells();
        if (selection != null && selection.length > 0) {
            Collection<? extends Label> selectedLabels =
                ((GraphJCell) selection[0]).getListLabels();
            if (selectedLabels.size() > 0) {
                Label selectedLabel = selectedLabels.iterator().next();
                if (selectedLabel instanceof TypeLabel) {
                    this.oldLabel = (TypeLabel) selectedLabel;
                }
            }
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        this.oldLabel = null;
        TreePath[] selection = ((LabelTree) e.getSource()).getSelectionPaths();
        if (selection != null && selection.length > 0) {
            Label selectedLabel =
                ((LabelTree.LabelTreeNode) selection[0].getLastPathComponent()).getLabel();
            if (selectedLabel instanceof TypeLabel) {
                this.oldLabel = (TypeLabel) selectedLabel;
            }
        }
    }

    /** The label to be replaced; may be {@code null}. */
    private TypeLabel oldLabel;
}