/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.gui.action;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.type.TypeElement;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.display.GraphTab;
import nl.utwente.groove.gui.display.ResourceDisplay;
import nl.utwente.groove.gui.list.SearchResult;
import nl.utwente.groove.gui.tree.LabelTree;
import nl.utwente.groove.gui.tree.TypeTree.TypeTreeNode;
import nl.utwente.groove.gui.view.AspectGraphCanvas;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.GraphCanvasListener;

/**
 * Action for changing one label into another throughout the grammar.
 */
@NonNullByDefault
public class FindReplaceAction extends SimulatorAction
    implements GraphCanvasListener<AspectGraph>, TreeSelectionListener {
    /** Constructs an instance of the action, for a given simulator. */
    public FindReplaceAction(Simulator simulator) {
        super(simulator, Options.FIND_REPLACE_ACTION_NAME, Icons.SEARCH_ICON);
        putValue(ACCELERATOR_KEY, Options.SEARCH_KEY);
        addAsListener(getHostDisplay());
        addAsListener(getRuleDisplay());
        addAsListener(getTypeDisplay());
    }

    /**
     * Adds this action as a listener to the graph canvas and {@link LabelTree}
     * of a given {@link ResourceDisplay}.
     */
    private void addAsListener(ResourceDisplay display) {
        AspectGraphCanvas canvas = ((GraphTab) display.getMainTab()).getJGraph();
        canvas.addCanvasListener(this);
        var labelTree = canvas.getController().getLabelTree();
        assert labelTree != null; // the graph tab installs the label tree with the canvas
        labelTree.addTreeSelectionListener(this);
    }

    @Override
    public void refresh() {
        setEnabled(getGrammarStore() != null
            && !getOccurringLabels(getGrammarModel()).isEmpty());
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

    /** Sets {@link #oldLabel} based on the canvas selection. */
    @Override
    public void selectionChanged(GraphCanvas<AspectGraph> canvas) {
        this.oldLabel = null;
        var selection = canvas.getSelection();
        if (!selection.isEmpty()) {
            Collection<? extends Label> selectedEntries = selection.get(0).getKeys();
            if (selectedEntries.size() > 0) {
                Label selectedEntry = selectedEntries.iterator().next();
                if (selectedEntry instanceof TypeElement te) {
                    this.oldLabel = te.label();
                }
            }
        }
    }

    @Override
    public void valueChanged(@Nullable TreeSelectionEvent e) {
        this.oldLabel = null;
        if (e != null) {
            TreePath[] selection = ((LabelTree<?>) e.getSource()).getSelectionPaths();
            if (selection != null && selection.length > 0) {
                Object treeNode = selection[0].getLastPathComponent();
                if (treeNode instanceof TypeTreeNode en) {
                    this.oldLabel = en.getEntry().getContent().label();
                }
            }
        }
    }

    /** The label to be replaced; may be {@code null}. */
    private @Nullable TypeLabel oldLabel;
}
