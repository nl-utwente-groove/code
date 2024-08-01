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
package nl.utwente.groove.gui.action;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;

import nl.utwente.groove.grammar.type.TypeElement;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.display.GraphTab;
import nl.utwente.groove.gui.display.JGraphPanel;
import nl.utwente.groove.gui.display.ResourceDisplay;
import nl.utwente.groove.gui.jgraph.JCell;
import nl.utwente.groove.gui.jgraph.JGraph;
import nl.utwente.groove.gui.list.SearchResult;
import nl.utwente.groove.gui.tree.LabelTree;
import nl.utwente.groove.gui.tree.TypeTree.TypeTreeNode;

/**
 * Action for changing one label into another throughout the grammar.
 */
public class FindReplaceAction extends SimulatorAction
    implements GraphSelectionListener, TreeSelectionListener {
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
        JGraph<?> jGraph = ((GraphTab) display.getMainTab()).getJGraph();
        jGraph.addGraphSelectionListener(this);
        jGraph.getLabelTree().addTreeSelectionListener(this);
    }

    @Override
    public void refresh() {
        setEnabled(getGrammarStore() != null
            && !getGrammarModel().getTypeGraph().getLabels().isEmpty());
    }

    @Override
    public void execute() {
        if (getDisplaysPanel().saveAllEditors(false)) {
            Relabelling result = askFindSearch(this.oldLabel);
            if (result != null) {
                if (result.to() == null) {
                    // Find label.
                    List<SearchResult> searchResults
                        = getSimulatorModel().searchLabel(result.from());
                    getSimulator().setSearchResults(searchResults);
                } else { // Replace label.
                    try {
                        getSimulatorModel().doRelabel(result.from(), result.to());
                    } catch (IOException exc) {
                        showErrorDialog(exc,
                                        String
                                            .format("Error while renaming '%s' into '%s':",
                                                    result.from(), result.to()));
                    }
                }
            }
        }
    }

    /** Sets {@link #oldLabel} based on the {@link JGraph} selection. */
    @Override
    public void valueChanged(GraphSelectionEvent e) {
        this.oldLabel = null;
        Object[] selection = ((JGraph<?>) e.getSource()).getSelectionCells();
        if (selection != null && selection.length > 0) {
            Collection<? extends Label> selectedEntries = ((JCell<?>) selection[0]).getKeys();
            if (selectedEntries.size() > 0) {
                Label selectedEntry = selectedEntries.iterator().next();
                if (selectedEntry instanceof TypeElement te) {
                    this.oldLabel = te.label();
                }
            }
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        this.oldLabel = null;
        TreePath[] selection = ((LabelTree<?>) e.getSource()).getSelectionPaths();
        if (selection != null && selection.length > 0) {
            Object treeNode = selection[0].getLastPathComponent();
            if (treeNode instanceof TypeTreeNode en) {
                this.oldLabel = en.getEntry().getType().label();
            }
        }
    }

    /** The label to be replaced; may be {@code null}. */
    private TypeLabel oldLabel;
}