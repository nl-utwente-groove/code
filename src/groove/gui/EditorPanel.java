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

import groove.util.Groove;
import groove.view.aspect.AspectGraph;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
     * @param fresh if {@code true}, the graph is fresh (and therefore the
     * editor is immediately dirty)
     */
    public EditorPanel(Simulator simulator, final AspectGraph graph,
            boolean fresh) {
        setName(graph.getName());
        setFocusCycleRoot(true);
        this.simulator = simulator;
        this.options = simulator.getOptions();
        this.editor =
            new Editor(null, this.options,
                simulator.getGrammarView().getProperties()) {
                @Override
                protected void doQuit() {
                    handleCancel();
                }

                @Override
                protected void updateTitle() {
                    String title =
                        (isDirty() ? "*" : "") + getGraph().getName();
                    getTabbedPane().getTabComponentOf(EditorPanel.this).setTitle(
                        title);
                    getOkButton().setEnabled(isDirty());
                }

                @Override
                protected void updateStatus() {
                    super.updateStatus();
                    getTabbedPane().getTabComponentOf(EditorPanel.this).setError(
                        !getModel().getErrorMap().isEmpty());
                }

                @Override
                JToolBar createToolBar() {
                    JToolBar toolbar = new JToolBar();
                    toolbar.setFloatable(false);
                    toolbar.add(getOkButton());
                    toolbar.add(getCancelButton());
                    addModeButtons(toolbar);
                    addUndoButtons(toolbar);
                    addCopyPasteButtons(toolbar);
                    addGridButtons(toolbar);
                    return toolbar;
                }
            };
        this.graph = graph;
        this.fresh = fresh;
    }

    /** Starts the editor with the graph passed in at construction time. */
    public void start() {
        this.editor.setTypeView(this.simulator.getTypeView());
        this.editor.setGraph(this.graph, true);
        this.editor.setDirty(this.fresh);
        setLayout(new BorderLayout());
        add(this.editor.getMainPanel());
    }

    /** Returns the resulting aspect graph of the editor. */
    public AspectGraph getGraph() {
        return this.graph;
    }

    /** Changes the type graph in the editor,
     * according to the current type view in the simulator. 
     */
    public void setType() {
        this.editor.setTypeView(this.simulator.getTypeView());
    }

    /** Returns the editor instance of this panel. */
    public Editor getEditor() {
        return this.editor;
    }

    /** Returns the tabbed view pane of the simulator (on which this panel is displayed). */
    private SimulatorPanel getTabbedPane() {
        return this.simulator.getSimulatorPanel();
    }

    /** Creates and returns a Cancel button, for use on the tool bar. */
    private JButton getCancelButton() {
        if (this.cancelButton == null) {
            Action cancelAction =
                new AbstractAction(Options.CANCEL_EDIT_ACTION_NAME,
                    Groove.CANCEL_ICON) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handleCancel();
                    }
                };
            JButton result = new JButton(cancelAction);
            result.setText(null);
            result.setToolTipText("Cancel editing");
            this.cancelButton = result;
        }
        return this.cancelButton;
    }

    /** Creates and returns an OK button, for use on the tool bar. */
    private JButton getOkButton() {
        if (this.okButton == null) {
            Action saveAction =
                new AbstractAction(Options.SAVE_ACTION_NAME, Groove.SAVE_ICON) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handleOk();
                    }
                };
            saveAction.putValue(Action.ACCELERATOR_KEY, Options.SAVE_KEY);
            JButton result = new JButton(saveAction);
            result.setText(null);
            result.setToolTipText("Save changes");
            this.okButton = result;
        }
        return this.okButton;
    }

    /**
     * Implements the effect of pressing the OK button.
     */
    void handleOk() {
        if (this.editor.isDirty()) {
            boolean success = false;
            switch (this.editor.getRole()) {
            case HOST:
                success = this.simulator.doAddGraph(this.editor.getGraph());
                break;
            case RULE:
                success = this.simulator.doAddRule(this.editor.getGraph());
                break;
            case TYPE:
                success = this.simulator.doAddType(this.editor.getGraph());
                break;
            }
            if (success) {
                this.editor.setDirty(false);
            }
        }
    }

    /**
     * Implements the effect of cancelling.
     * @return {@code true} if the editor panel was closed as an effect of
     * this call
     */
    boolean handleCancel() {
        boolean result = true;
        if (this.editor.isDirty()) {
            int confirm =
                JOptionPane.showConfirmDialog(this,
                    String.format("%s '%s' has been modified. Save changes?",
                        this.editor.getRole().toString(true),
                        getGraph().getName()), null,
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                handleOk();
            }
            result = (confirm != JOptionPane.CANCEL_OPTION);
        }
        if (result) {
            dispose();
        }
        return result;
    }

    /** Besides calling the super method, also disposes the editor frame. */
    private void dispose() {
        getTabbedPane().remove(this);
    }

    private JButton okButton;
    private JButton cancelButton;
    /** The graph with which the editor has been initially set. */
    private final AspectGraph graph;
    /** Flag indicating that this is a fresh graph, not already in the simulator. */
    private final boolean fresh;
    /** Options of this dialog. */
    private final Options options;
    /** The simulator to which the panel reports. */
    private final Simulator simulator;
    /** The editor wrapped in the panel. */
    private final Editor editor;
}