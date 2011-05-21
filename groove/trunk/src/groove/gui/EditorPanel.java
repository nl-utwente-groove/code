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
 * $Id: EditorDialog.java,v 1.15 2008-01-30 09:33:35 iovka Exp $
 */
package groove.gui;

import groove.graph.GraphRole;
import groove.gui.action.CancelEditGraphAction;
import groove.gui.action.SaveGraphAction;
import groove.view.StoredGrammarView;
import groove.view.StoredGrammarView.TypeViewList;
import groove.view.aspect.AspectGraph;

import java.awt.BorderLayout;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

/**
 * Dialog wrapping a graph editor, such that no file operations are possible.
 * @author Arend Rensink
 * @version $Revision$
 */
public class EditorPanel extends JPanel {
    /**
     * Constructs an instance of the dialog, for a given graph or rule.
     * @param simulator the simulator on which this panel is placed
     * @param graph the input graph for the editor
     */
    public EditorPanel(Simulator simulator, final AspectGraph graph) {
        setName(graph.getName());
        setFocusCycleRoot(true);
        this.simulator = simulator;
        this.options = simulator.getOptions();
        this.editor =
            new Editor(null, this.options,
                simulator.getModel().getGrammar().getProperties()) {
                @Override
                protected void doQuit() {
                    EditorPanel.this.doCancel();
                }

                @Override
                protected void updateTitle() {
                    ButtonTabComponent tab =
                        getTabbedPane().getTabComponentOf(EditorPanel.this);
                    if (tab == null) {
                        getTabbedPane().getFrameOf(EditorPanel.this).setTitle(
                            getTitle());
                    } else {
                        tab.setTitle(getTitle());
                    }
                    getSaveButton().setEnabled(isDirty());
                }

                @Override
                protected void updateStatus() {
                    super.updateStatus();
                    ButtonTabComponent tab =
                        getTabbedPane().getTabComponentOf(EditorPanel.this);
                    if (tab != null) {
                        tab.setError(!getModel().getErrorMap().isEmpty());

                    }
                }

                @Override
                JToolBar createToolBar() {
                    JToolBar toolbar = new JToolBar();
                    toolbar.add(getSaveButton());
                    toolbar.add(getCancelButton());
                    addModeButtons(toolbar);
                    addUndoButtons(toolbar);
                    addCopyPasteButtons(toolbar);
                    addGridButtons(toolbar);
                    return toolbar;
                }

                @Override
                public GraphRole getRole() {
                    return graph.getRole();
                }
            };
    }

    /** Starts the editor with the graph passed in at construction time. */
    public void start(AspectGraph graph) {
        this.editor.setTypeView(getTypeView());
        this.editor.setGraph(graph, true);
        setLayout(new BorderLayout());
        JSplitPane mainPanel = this.editor.getMainPanel();
        mainPanel.setBorder(null);
        add(mainPanel);
    }

    /** Returns the title of this panel. The title is the name plus an optional
     * indication of the (dirty) status of the editor.
     */
    public String getTitle() {
        String title = (isDirty() ? "*" : "") + getGraph().getName();
        return title;
    }

    /** Returns the resulting aspect graph of the editor. */
    public AspectGraph getGraph() {
        return getEditor().getGraph();
    }

    /** Changes the type graph in the editor,
     * according to the current type view in the simulator. 
     */
    public void setType() {
        this.editor.setTypeView(getTypeView());
    }

    /** Returns the type graph associated with the grammar, if any. */
    TypeViewList getTypeView() {
        StoredGrammarView grammar = this.simulator.getModel().getGrammar();
        return grammar == null ? null : grammar.getTypeViewList();
    }

    /** Returns the editor instance of this panel. */
    public Editor getEditor() {
        return this.editor;
    }

    /** Returns the simulator instance of this panel. */
    public Simulator getSimulator() {
        return this.simulator;
    }

    /** Indicates if the editor has unsaved changes. */
    public boolean isDirty() {
        return getEditor().isDirty();
    }

    /** Changes the startus of the editor to dirty. */
    public void setDirty(boolean dirty) {
        getEditor().setDirty(dirty);
    }

    /** Indicates if the editor is currently saving changes. */
    public boolean isSaving() {
        return this.saving;
    }

    /** Changes the edited graph. */
    public void change(AspectGraph newGraph) {
        getEditor().setGraph(newGraph, false);
    }

    /** Renames the edited graph. */
    public void rename(String newName) {
        AspectGraph newGraph = getGraph().clone();
        newGraph.setName(newName);
        newGraph.setFixed();
        getEditor().setGraph(newGraph, false);
    }

    /** Returns the tabbed view pane of the simulator (on which this panel is displayed). */
    private SimulatorPanel getTabbedPane() {
        return this.simulator.getSimulatorPanel();
    }

    /** Creates and returns a Cancel button, for use on the tool bar. */
    private JButton getCancelButton() {
        if (this.cancelButton == null) {
            Action cancelAction = getCancelAction();
            JButton result = new JButton(cancelAction);
            result.setText(null);
            result.setToolTipText("Cancel editing");
            this.cancelButton = result;
        }
        return this.cancelButton;
    }

    /** Creates and returns the cancel action. */
    private CancelEditGraphAction getCancelAction() {
        if (this.cancelAction == null) {
            this.cancelAction = new CancelEditGraphAction(this);
        }
        return this.cancelAction;
    }

    /** Creates and returns an OK button, for use on the tool bar. */
    private JButton getSaveButton() {
        if (this.saveButton == null) {
            Action saveAction = getSaveAction();
            JButton result = new JButton(saveAction);
            result.setText(null);
            result.setToolTipText("Save changes");
            this.saveButton = result;
        }
        return this.saveButton;
    }

    /** Creates and returns the save action. */
    public SaveGraphAction getSaveAction() {
        if (this.saveAction == null) {
            this.saveAction = new SaveGraphAction(this);
        }
        return this.saveAction;
    }

    /** Calls {@link CancelEditGraphAction#execute()}. */
    public boolean doCancel() {
        return getCancelAction().execute();
    }

    private JButton saveButton;
    private SaveGraphAction saveAction;
    private JButton cancelButton;
    private CancelEditGraphAction cancelAction;
    /** Options of this dialog. */
    private final Options options;
    /** The simulator to which the panel reports. */
    private final Simulator simulator;
    /** The editor wrapped in the panel. */
    private final Editor editor;
    /** Flag indicating that the editor is in the process of saving. */
    private boolean saving;
}