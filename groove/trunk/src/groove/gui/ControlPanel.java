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

import groove.control.ControlAutomaton;
import groove.control.ControlView;
import groove.control.parse.GCLTokenMaker;
import groove.gui.jgraph.ControlJGraph;
import groove.gui.jgraph.ControlJModel;
import groove.io.SystemStore;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.RuleMatch;
import groove.trans.RuleName;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.StoredGrammarView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 * The Simulator panel that shows the control program, with a button that shows
 * the corresponding control automaton.
 * 
 * @author Tom Staijen
 * @version $0.9$
 */
public class ControlPanel extends JPanel implements SimulationListener {
    /**
     * @param simulator The Simulator the panel is added to.
     */
    public ControlPanel(Simulator simulator) {
        super();
        this.simulator = simulator;
        // create the layout for this JPanel
        this.setLayout(new BorderLayout());
        // fill in the GUI
        RTextScrollPane scroller =
            new RTextScrollPane(500, 400, getControlTextArea(), true);
        this.add(createToolbar(), BorderLayout.NORTH);
        this.add(scroller, BorderLayout.CENTER);
        // add keyboard binding for Save key
        InputMap focusedInputMap =
            getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        focusedInputMap.put(Options.SAVE_KEY, Options.SAVE_ACTION_NAME);
        focusedInputMap.put(Options.CANCEL_KEY, Options.CANCEL_EDIT_ACTION_NAME);
        getActionMap().put(Options.SAVE_ACTION_NAME, getSaveAction());
        getActionMap().put(Options.CANCEL_EDIT_ACTION_NAME, getCancelAction());
        // start listening
        simulator.addSimulationListener(this);
    }

    private JToolBar createToolbar() {
        JToolBar result = new JToolBar();
        result.add(createButton(getNewAction()));
        result.add(createButton(getEditAction()));
        result.add(createButton(getCopyAction()));
        result.add(createButton(getDeleteAction()));
        result.add(createButton(getRenameAction()));
        result.addSeparator();
        result.add(createButton(getSaveAction()));
        result.add(createButton(getCancelAction()));
        result.addSeparator();
        result.add(new JLabel("Name: "));
        result.add(getNameField());
        result.addSeparator();
        result.add(createButton(getPreviewAction()));
        result.add(createButton(getDisableAction()));
        result.add(createButton(getEnableAction()));
        return result;
    }

    /**
     * Creates a button around an action that is resized in case the action
     * doesn't have an icon.
     */
    private JButton createButton(Action action) {
        JButton result = new JButton(action);
        if (action.getValue(Action.SMALL_ICON) == null) {
            result.setMargin(new Insets(4, 2, 4, 2));
        } else {
            result.setHideActionText(true);
        }
        return result;
    }

    /**
     * We do nothing when a transition is applied
     */
    public void applyTransitionUpdate(GraphTransition transition) {
        // // do nothing
    }

    public void setGrammarUpdate(StoredGrammarView grammar) {
        if (!isControlSelected() || !grammarHasControl(getSelectedControl())) {
            String newName = grammar.getControlName();
            setSelectedControl(grammarHasControl(newName) ? newName : null);
        }
        refreshAll();
    }

    public void setRuleUpdate(RuleName name) {
        // nothing happens
    }

    public void setStateUpdate(GraphState state) {
        // nothing happens
    }

    public void setMatchUpdate(RuleMatch match) {
        // nothing happens
    }

    public void setTransitionUpdate(GraphTransition transition) {
        // nothing happens
    }

    public void startSimulationUpdate(GTS gts) {
        // nothing happens
    }

    /** Selects a line in the currently displayed control program, if possible. */
    public void selectLine(int lineNr) {
        getControlTextArea().selectLine(lineNr);
    }

    /**
     * Creates and shows a confirmation dialog for abandoning the currently
     * edited control program.
     */
    private boolean confirmAbandon() {
        boolean result = true;
        if (isDirty()) {
            String name = getSelectedControl();
            int answer =
                JOptionPane.showConfirmDialog(this, String.format(
                    "Save changes in '%s'?", name), null,
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                getSaveAction().doSave(name, getControlTextArea().getText());
            } else {
                result = answer == JOptionPane.NO_OPTION;
            }
        }
        return result;
    }

    /** Indicates if the currently loaded grammar is modifiable. */
    private boolean isModifiable() {
        SystemStore store = getSimulator().getGrammarStore();
        return store != null && store.isModifiable();
    }

    /**
     * Returns the current grammar view. Convenience method for
     * <code>getSimulator().getGrammarView()</code>.
     */
    private StoredGrammarView getGrammarView() {
        return getSimulator().getGrammarView();
    }

    /**
     * Convenience method to test if the current grammar has a control program
     * by a given name. Equivalent to
     * <code>getGrammarView().getControlNames().contains(controlName)</code>
     */
    private boolean grammarHasControl(String controlName) {
        return getGrammarView().getControlNames().contains(controlName);
    }

    /**
     * Selects a control program for viewing.
     * @param name the control program to be viewed; either <code>null</code> if
     *        there is no control program in the current grammar, or an existing
     *        name in the control names of the current grammar.
     */
    public void setSelectedControl(String name) {
        this.selectedControl = name;
    }

    /**
     * Indicates the currently selected control program name
     * @return either <code>null</code> or an existing control program name
     */
    private final String getSelectedControl() {
        return this.selectedControl;
    }

    /** Convenience method to indicate if a control name has been selected. */
    private final boolean isControlSelected() {
        return getSelectedControl() != null;
    }

    /** Name of the currently visible control program. */
    private String selectedControl;

    /**
     * Stops the current editing action, if any.
     * @param confirm indicates if the user should be asked for confirmation
     * @return if editing was indeed stopped
     */
    private boolean stopEditing(boolean confirm) {
        boolean result = true;
        if (isEditing()) {
            if (!confirm || confirmAbandon()) {
                this.editing = false;
                setDirty(false);
                // if we cancelled editing a new control program
                // the selected name should be reset
                if (!getGrammarView().getControlNames().contains(
                    getSelectedControl())) {
                    setSelectedControl(null);
                }
                getParent().requestFocusInWindow();
                refreshAll();
            } else {
                result = false;
            }
        } else {
            assert !isDirty();
        }
        return result;
    }

    /**
     * Starts a new editing action. Cancels the current editing action if
     * necessary.
     */
    private void startEditing() {
        assert !isEditing();
        this.editing = true;
        refreshAll();
    }

    /**
     * Indicates the current editing mode.
     * @return if <code>true</code>, an editing action is going on.
     */
    private boolean isEditing() {
        return this.editing;
    }

    /** Flag indicating if an editing action is going on. */
    private boolean editing;

    /**
     * Returns the dirty status of the editor.
     * @return <code>true</code> if the editor is dirty.
     */
    private final boolean isDirty() {
        return this.dirty;
    }

    /** Sets the status of the editor. */
    private final void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /** Flag indicating if the editor is dirty. */
    private boolean dirty;

    /**
     * Registers a refreshable.
     * @see #refreshAll()
     */
    private void addRefreshable(Refreshable refreshable) {
        this.refreshables.add(refreshable);
    }

    /** Refreshes all registered refreshables. */
    public void refreshAll() {
        for (Refreshable refreshable : this.refreshables) {
            refreshable.refresh();
        }
    }

    /** List of registered refreshables. */
    private final List<Refreshable> refreshables = new ArrayList<Refreshable>();

    /** Returns the simulator to which the control panel belongs. */
    private Simulator getSimulator() {
        return this.simulator;
    }

    /** Simulator to which the control panel belongs. */
    private final Simulator simulator;

    /**
     * Interface for objects that need to refresh their own status when actions
     * on the control panel occur.
     */
    private interface Refreshable {
        /**
         * Callback method to give the implementing object a chance to refresh
         * its status.
         */
        public void refresh();
    }

    /** Lazily creates and returns the field displaying the control name. */
    private JComboBox getNameField() {
        if (this.nameField == null) {
            this.nameField = new ControlNameField();
            this.nameField.setBorder(BorderFactory.createLoweredBevelBorder());
            this.nameField.setMaximumSize(new Dimension(150, 24));
        }
        return this.nameField;
    }

    /** Name field of the control program. */
    private JComboBox nameField;

    private class ControlNameField extends JComboBox implements Refreshable {
        public ControlNameField() {
            setBorder(BorderFactory.createLoweredBevelBorder());
            setMaximumSize(new Dimension(150, 24));
            setEnabled(false);
            setEditable(false);
            this.selectionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedItem = (String) getSelectedItem();
                    if (selectedItem != null && stopEditing(true)) {
                        setSelectedControl(selectedItem);
                        refreshAll();
                    }
                }
            };
            addActionListener(this.selectionListener);
            addRefreshable(this);
        }

        @Override
        public void refresh() {
            removeActionListener(this.selectionListener);
            this.removeAllItems();
            if (getGrammarView() == null) {
                setEnabled(false);
            } else {
                Set<String> names =
                    new TreeSet<String>(getGrammarView().getControlNames());
                if (isControlSelected() && isEditing()) {
                    names.add(getSelectedControl());
                }
                for (String controlName : names) {
                    addItem(controlName);
                }
                setSelectedItem(getSelectedControl());
                setEnabled(getItemCount() > 0);
            }
            addActionListener(this.selectionListener);
        }

        private final ActionListener selectionListener;
    }

    /** Lazily creates and returns the area displaying the control program. */
    private ControlTextArea getControlTextArea() {
        if (this.controlTextArea == null) {
            this.controlTextArea = new ControlTextArea();
        }
        return this.controlTextArea;
    }

    /** Panel showing the control program. */
    private ControlTextArea controlTextArea;

    private class ControlTextArea extends RSyntaxTextArea implements
            Refreshable {
        public ControlTextArea() {
            super(new RSyntaxDocument("gcl"));
            // RSyntaxDocument document = new RSyntaxDocument("gcl");
            ((RSyntaxDocument) getDocument()).setSyntaxStyle(new GCLTokenMaker());
            // setDocument(document);
            setBackground(DISABLED_COLOUR);
            this.changeListener = new DocumentListener() {
                @Override
                public void removeUpdate(DocumentEvent e) {
                    notifyEdit();
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    notifyEdit();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    notifyEdit();
                }

                private void notifyEdit() {
                    setDirty(true);
                    ControlTextArea.this.listenToRefresh = false;
                    refreshAll();
                    ControlTextArea.this.listenToRefresh = true;
                }
            };
            getDocument().addDocumentListener(this.changeListener);
            addRefreshable(this);
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            setBackground(enabled ? ENABLED_COLOUR : DISABLED_COLOUR);
        }

        @Override
        public void refresh() {
            // if the text area has focus, don't do any refreshing
            // as that would destroy the current state of the editing
            if (isDirty()) {
                setEnabled(true);
                setEditable(true);
                requestFocusInWindow();
            } else if (this.listenToRefresh) {
                getDocument().removeDocumentListener(this.changeListener);
                // we enable the area only if it is being edited.
                setEditable(isEditing());
                // the area is editable (meaning it is lighter)
                // if it is actually being edited, or if it is the
                // currently used control program
                boolean enabled = isEditing();
                if (!enabled && isControlSelected()
                    && getGrammarView().isUseControl()) {
                    enabled =
                        getSelectedControl().equals(
                            getGrammarView().getControlName());
                }
                setEnabled(enabled);
                String program = "";
                if (isControlSelected()) {
                    ControlView cv =
                        getGrammarView().getControlView(getSelectedControl());
                    if (cv != null) {
                        program = cv.getProgram();
                    }
                }
                setText(program);
                discardAllEdits();
                if (isEditing()) {
                    requestFocusInWindow();
                }
                getDocument().addDocumentListener(this.changeListener);
            }
        }

        /** Selects a line in the currently displayed control program, if possible. */
        public void selectLine(int lineNr) {
            try {
                int start = getLineStartOffset(lineNr - 1);
                getCaret().setDot(start);
            } catch (BadLocationException e) {
                // do nothing
            }
        }

        private final DocumentListener changeListener;
        /** Flag indicating if refresh actions should be currently listened to. */
        private boolean listenToRefresh = true;
    }

    /** Abstract superclass for actions that can refresh their own status. */
    private abstract class RefreshableAction extends AbstractAction implements
            Refreshable {
        public RefreshableAction(String name, Icon icon) {
            super(name, icon);
            putValue(SHORT_DESCRIPTION, name);
            setEnabled(false);
            addRefreshable(this);
        }
    }

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link CancelAction}.
     */
    private CancelAction getCancelAction() {
        if (this.cancelAction == null) {
            this.cancelAction = new CancelAction();
        }
        return this.cancelAction;
    }

    /** Singular instance of the {@link CancelAction}. */
    private CancelAction cancelAction;

    /**
     * Action to cancel editing the currently displayed control program.
     */
    private class CancelAction extends RefreshableAction {
        public CancelAction() {
            super(Options.CANCEL_EDIT_ACTION_NAME, Groove.CANCEL_ICON);
        }

        public void actionPerformed(ActionEvent e) {
            stopEditing(true);
        }

        @Override
        public void refresh() {
            setEnabled(isEditing());
        }
    }

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link CopyAction}.
     */
    private CopyAction getCopyAction() {
        if (this.copyAction == null) {
            this.copyAction = new CopyAction();
        }
        return this.copyAction;
    }

    /** Singular instance of the {@link CopyAction}. */
    private CopyAction copyAction;

    /**
     * Action to copy the currently displayed control program.
     */
    private class CopyAction extends RefreshableAction {
        public CopyAction() {
            super(Options.COPY_CONTROL_ACTION_NAME, Groove.COPY_ICON);
        }

        public void actionPerformed(ActionEvent e) {
            if (stopEditing(true)) {
                String oldName = getSelectedControl();
                String newName =
                    getSimulator().askNewControlName(
                        "Select new control program name", oldName, true);
                if (newName != null) {
                    getSaveAction().doSave(newName,
                        getControlTextArea().getText());
                    setSelectedControl(newName);
                    refreshAll();
                }
            }
        }

        @Override
        public void refresh() {
            setEnabled(isControlSelected()
                && grammarHasControl(getSelectedControl()));
            if (getSimulator().getPanel() == getSimulator().getControlPanel()) {
                getSimulator().getCopyMenuItem().setAction(this);
            }
        }
    }

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link DeleteAction}.
     */
    private DeleteAction getDeleteAction() {
        if (this.deleteAction == null) {
            this.deleteAction = new DeleteAction();
        }
        return this.deleteAction;
    }

    /** Singular instance of the {@link DeleteAction}. */
    private DeleteAction deleteAction;

    /**
     * Action to delete the currently displayed control program.
     */
    private class DeleteAction extends RefreshableAction {
        public DeleteAction() {
            super(Options.DELETE_CONTROL_ACTION_NAME, Groove.DELETE_ICON);
        }

        public void actionPerformed(ActionEvent e) {
            String controlName = getSelectedControl();
            if (getSimulator().confirmBehaviour(Options.DELETE_CONTROL_OPTION,
                String.format("Delete control program '%s'?", controlName))) {
                stopEditing(false);
                int itemNr = getNameField().getSelectedIndex() + 1;
                if (itemNr == getNameField().getItemCount()) {
                    itemNr -= 2;
                }
                String newName =
                    itemNr >= 0 ? (String) getNameField().getItemAt(itemNr)
                            : null;
                doDelete(controlName);
                setSelectedControl(newName);
                refreshAll();
            }
        }

        /** Deletes a given control program from the grammar. */
        public void doDelete(String controlName) {
            getSimulator().doDeleteControl(controlName);
        }

        @Override
        public void refresh() {
            setEnabled(isControlSelected()
                && grammarHasControl(getSelectedControl()));
            if (getSimulator().getPanel() == getSimulator().getControlPanel()) {
                getSimulator().getDeleteMenuItem().setAction(this);
            }
        }
    }

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link NewAction}.
     */
    private DisableAction getDisableAction() {
        if (this.disableAction == null) {
            this.disableAction = new DisableAction();
        }
        return this.disableAction;
    }

    /** Singular instance of the {@link EnableAction}. */
    private DisableAction disableAction;

    /** Action to disable the currently displayed control program. */
    private class DisableAction extends RefreshableAction {
        public DisableAction() {
            super(Options.DISABLE_CONTROL_ACTION_NAME, Groove.DISABLE_ICON);
        }

        public void actionPerformed(ActionEvent arg0) {
            if (stopEditing(true)) {
                SystemProperties oldProperties =
                    getGrammarView().getProperties();
                SystemProperties newProperties = oldProperties.clone();
                newProperties.setUseControl(false);
                getSimulator().doSaveProperties(newProperties);
            }
        }

        @Override
        public void refresh() {
            setEnabled(getGrammarView() != null
                && getGrammarView().isUseControl());
        }
    }

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link NewAction}.
     */
    private EnableAction getEnableAction() {
        if (this.enableAction == null) {
            this.enableAction = new EnableAction();
        }
        return this.enableAction;
    }

    /** Singular instance of the {@link EnableAction}. */
    private EnableAction enableAction;

    /** Action to enable the currently displayed control program. */
    private class EnableAction extends RefreshableAction {
        public EnableAction() {
            super(Options.ENABLE_CONTROL_ACTION_NAME, Groove.ENABLE_ICON);
        }

        public void actionPerformed(ActionEvent evt) {
            if (stopEditing(true)) {
                doEnable(getSelectedControl());
            }
        }

        /** Enables a control program with a given name. */
        public void doEnable(String controlName) {
            SystemProperties oldProperties = getGrammarView().getProperties();
            SystemProperties newProperties = oldProperties.clone();
            newProperties.setUseControl(true);
            newProperties.setControlName(controlName);
            getSimulator().doSaveProperties(newProperties);
        }

        @Override
        public void refresh() {
            setEnabled(isControlSelected()
                && (!getGrammarView().isUseControl() || !getGrammarView().getControlName().equals(
                    getSelectedControl())));
        }
    }

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link NewAction}.
     */
    private EditAction getEditAction() {
        if (this.editAction == null) {
            this.editAction = new EditAction();
        }
        return this.editAction;
    }

    /** Singular instance of the {@link EditAction}. */
    private EditAction editAction;

    /** Action to start editing the currently displayed control program. */
    private class EditAction extends RefreshableAction {
        public EditAction() {
            super(Options.EDIT_CONTROL_ACTION_NAME, Groove.EDIT_ICON);
        }

        public void actionPerformed(ActionEvent e) {
            startEditing();
        }

        @Override
        public void refresh() {
            setEnabled(isControlSelected() && isModifiable() && !isEditing());
            if (getSimulator().getPanel() == getSimulator().getControlPanel()) {
                getSimulator().getEditMenuItem().setAction(this);
            }
        }
    }

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link NewAction}.
     */
    private NewAction getNewAction() {
        if (this.newAction == null) {
            this.newAction = new NewAction();
        }
        return this.newAction;
    }

    /** Singular instance of the {@link NewAction}. */
    private NewAction newAction;

    /** Action to create and start editing a new control program. */
    private class NewAction extends RefreshableAction {
        public NewAction() {
            super(Options.NEW_CONTROL_ACTION_NAME, Groove.NEW_ICON);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (stopEditing(true)) {
                String newName =
                    getSimulator().askNewControlName(
                        "Select control program name",
                        Groove.DEFAULT_CONTROL_NAME, true);
                if (newName != null) {
                    setSelectedControl(newName);
                    ControlPanel.this.controlTextArea.setText("");
                    setDirty(true);
                    startEditing();
                }
            }
        }

        @Override
        public void refresh() {
            setEnabled(getGrammarView() != null);
        }
    }

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link NewAction}.
     */
    private PreviewAction getPreviewAction() {
        if (this.previewAction == null) {
            this.previewAction = new PreviewAction();
        }
        return this.previewAction;
    }

    /** Singular instance of the {@link PreviewAction}. */
    private PreviewAction previewAction;

    /**
     * Creates a dialog showing the control automaton.
     */
    private class PreviewAction extends RefreshableAction {
        public PreviewAction() {
            super(Options.PREVIEW_CONTROL_ACTION_NAME, Groove.GRAPH_MODE_ICON);
        }

        public void actionPerformed(ActionEvent e) {
            if (stopEditing(true)) {
                assert getSimulator().getGrammarView().getErrors().size() == 0 : "View Button should be disabled if grammar has errors.";

                ControlAutomaton caut;
                try {
                    caut =
                        getGrammarView().getControlView(getSelectedControl()).toAutomaton(
                            getGrammarView().toGrammar());
                } catch (FormatException exc) {
                    getSimulator().showErrorDialog(
                        String.format("Error in control program '%s'",
                            getSelectedControl()), exc);
                    return;
                }
                ControlJGraph cjg =
                    new ControlJGraph(new ControlJModel(caut,
                        getSimulator().getOptions()),
                        ControlPanel.this.simulator);

                AutomatonPanel autPanel =
                    new AutomatonPanel(ControlPanel.this.simulator, cjg);

                JDialog jf =
                    new JDialog(getSimulator().getFrame(), "Control Automaton");
                jf.add(autPanel);
                jf.setSize(600, 700);
                Point p = getSimulator().getFrame().getLocation();
                jf.setLocation(new Point(p.x + 50, p.y + 50));
                jf.setVisible(true);

                cjg.getLayouter().start(true);
            }
        }

        @Override
        public void refresh() {
            setEnabled(isControlSelected());
        }
    }

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link RenameAction}.
     */
    private RenameAction getRenameAction() {
        if (this.renameAction == null) {
            this.renameAction = new RenameAction();
        }
        return this.renameAction;
    }

    /** Singular instance of the {@link RenameAction}. */
    private RenameAction renameAction;

    /**
     * Action to rename the currently displayed control program.
     */
    private class RenameAction extends RefreshableAction {
        public RenameAction() {
            super(Options.RENAME_CONTROL_ACTION_NAME, Groove.RENAME_ICON);
        }

        public void actionPerformed(ActionEvent e) {
            if (stopEditing(true)) {
                String oldName = getSelectedControl();
                String newName =
                    getSimulator().askNewControlName(
                        "Select control program name", oldName, false);
                if (newName != null) {
                    String program = getControlTextArea().getText();
                    getDeleteAction().doDelete(oldName);
                    getSaveAction().doSave(newName, program);
                    if (oldName.equals(getGrammarView().getControlName())) {
                        getEnableAction().doEnable(newName);
                    }
                    setSelectedControl(newName);
                    refreshAll();
                }
            }
        }

        @Override
        public void refresh() {
            setEnabled(isControlSelected()
                && grammarHasControl(getSelectedControl()));
            if (getSimulator().getPanel() == getSimulator().getControlPanel()) {
                getSimulator().getRenameMenuItem().setAction(this);
            }
        }
    }

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link NewAction}.
     */
    private SaveAction getSaveAction() {
        if (this.saveAction == null) {
            this.saveAction = new SaveAction();
        }
        return this.saveAction;
    }

    /** Singular instance of the {@link SaveAction}. */
    private SaveAction saveAction;

    private class SaveAction extends RefreshableAction {
        public SaveAction() {
            super(Options.SAVE_CONTROL_ACTION_NAME, Groove.SAVE_ICON);
            putValue(ACCELERATOR_KEY, Options.SAVE_KEY);
        }

        public void actionPerformed(ActionEvent e) {
            if (isDirty()) {
                doSave(getSelectedControl(), getControlTextArea().getText());
                stopEditing(false);
            }
        }

        /** Executes the save action. */
        public void doSave(String name, String program) {
            getSimulator().doAddControl(name, program);
        }

        @Override
        public void refresh() {
            setEnabled(isEditing());
        }
    }

    private class AutomatonPanel extends JGraphPanel<ControlJGraph> {
        /**
         * The constructor of this panel creates a panel with the Control
         * Automaton of the current grammar.
         * @param simulator
         */
        public AutomatonPanel(Simulator simulator, ControlJGraph graph) {
            super(graph, true, false, simulator.getOptions());
            this.getJGraph().setConnectable(false);
            this.getJGraph().setDisconnectable(false);
            this.getJGraph().setEnabled(true);
            getJGraph().setToolTipEnabled(true);
        }

        @Override
        public ControlJModel getJModel() {
            return (ControlJModel) super.getJModel();
        }
    }

    private static JTextField enabledField = new JTextField();
    private static JTextField disabledField = new JTextField();
    static {
        enabledField.setEditable(true);
        disabledField.setEditable(false);
    }
    /** The background colour of an enabled component. */
    private static Color ENABLED_COLOUR = enabledField.getBackground();
    /** The background colour of a disabled component. */
    private static Color DISABLED_COLOUR = disabledField.getBackground();
}
