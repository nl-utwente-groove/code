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

import groove.graph.Element;
import groove.graph.Label;
import groove.graph.TypeElement;
import groove.graph.TypeLabel;
import groove.gui.GraphTab;
import groove.gui.Icons;
import groove.gui.JGraphPanel;
import groove.gui.LabelTree;
import groove.gui.LabelTree.EntryNode;
import groove.gui.Options;
import groove.gui.ResourceDisplay;
import groove.gui.Simulator;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJGraph;
import groove.gui.list.SearchResult;
import groove.util.Duo;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;

/**
 * Action for changing one label into another throughout the grammar.
 */
public class FindReplaceAction extends SimulatorAction implements
        GraphSelectionListener, TreeSelectionListener {
    /** Constructs an instance of the action, for a given simulator. */
    public FindReplaceAction(Simulator simulator) {
        super(simulator, Options.FIND_REPLACE_ACTION_NAME, Icons.SEARCH_ICON);
        putValue(ACCELERATOR_KEY, Options.SEARCH_KEY);
        addAsListener(getHostDisplay());
        addAsListener(getRuleDisplay());
        addAsListener(getTypeDisplay());
    }

    /**
     * Adds this action as a listener to the {@link JGraph} and {@link LabelTree}
     * of a given {@link JGraphPanel}.
     */
    private void addAsListener(ResourceDisplay display) {
        GraphTab graphTab = (GraphTab) display.getMainTab();
        graphTab.getJGraph().addGraphSelectionListener(this);
        graphTab.getEditArea().getLabelTree().addTreeSelectionListener(this);
    }

    @Override
    public void refresh() {
        setEnabled(getGrammarStore() != null
            && getGrammarStore().isModifiable()
            && !getGrammarModel().getTypeGraph().getLabels().isEmpty());
    }

    @Override
    public void execute() {
        if (getDisplaysPanel().saveAllEditors(false)) {
            Duo<TypeLabel> result = askFindSearch(this.oldLabel);
            if (result != null) {
                if (result.two() == null) {
                    // Find label.
                    List<SearchResult> searchResults =
                        getSimulatorModel().searchLabel(result.one());
                    getSimulator().setSearchResults(searchResults);
                } else { // Replace label.
                    try {
                        getSimulatorModel().doRelabel(result.one(),
                            result.two());
                    } catch (IOException exc) {
                        showErrorDialog(exc, String.format(
                            "Error while renaming '%s' into '%s':",
                            result.one(), result.two()));
                    }
                }
            }
        }
    }

    /** Sets {@link #oldLabel} based on the {@link GraphJGraph} selection. */
    @Override
    public void valueChanged(GraphSelectionEvent e) {
        this.oldLabel = null;
        Object[] selection = ((GraphJGraph) e.getSource()).getSelectionCells();
        if (selection != null && selection.length > 0) {
            Collection<? extends Element> selectedEntries =
                ((GraphJCell) selection[0]).getKeys();
            if (selectedEntries.size() > 0) {
                Element selectedEntry = selectedEntries.iterator().next();
                if (selectedEntry instanceof TypeElement) {
                    this.oldLabel = ((TypeElement) selectedEntry).label();
                }
            }
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        this.oldLabel = null;
        TreePath[] selection = ((LabelTree) e.getSource()).getSelectionPaths();
        if (selection != null && selection.length > 0) {
            Object treeNode = selection[0].getLastPathComponent();
            if (treeNode instanceof EntryNode) {
                Label selectedLabel =
                    ((EntryNode) treeNode).getEntry().getLabel();
                if (selectedLabel instanceof TypeLabel) {
                    this.oldLabel = (TypeLabel) selectedLabel;
                }
            }
        }
    }

    /** The label to be replaced; may be {@code null}. */
    private TypeLabel oldLabel;
}