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

import groove.control.CtrlAut;
import groove.control.parse.CtrlDoc;
import groove.control.parse.CtrlTokenMaker;
import groove.gui.SimulatorModel.Change;
import groove.gui.jgraph.CtrlJGraph;
import groove.gui.jgraph.JAttr;
import groove.io.store.SystemStore;
import groove.trans.GraphGrammar;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.view.CtrlView;
import groove.view.FormatException;
import groove.view.StoredGrammarView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

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
public class ControlPanel extends JPanel implements SimulatorListener {
    /**
     * @param simulator The Simulator the panel is added to.
     */
    public ControlPanel(Simulator simulator) {
        super();
        this.simulator = simulator;
        // create the layout for this JPanel
        this.setLayout(new BorderLayout());
        this.setFocusable(false);
        this.setFocusCycleRoot(true);
        // fill in the GUI
        RTextScrollPane scroller =
            new RTextScrollPane(500, 400, getControlTextArea(), true);
        this.add(createToolbar(), BorderLayout.NORTH); // set up the split editor pane
        JSplitPane splitPane =
            new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroller,
                createDocPane());
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(1.0);

        this.add(splitPane, BorderLayout.CENTER);
        // add keyboard binding for Save key
        InputMap focusedInputMap =
            getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        focusedInputMap.put(Options.SAVE_KEY, Options.SAVE_ACTION_NAME);
        focusedInputMap.put(Options.CANCEL_KEY, Options.CANCEL_EDIT_ACTION_NAME);
        getActionMap().put(Options.SAVE_ACTION_NAME, getSaveAction());
        getActionMap().put(Options.CANCEL_EDIT_ACTION_NAME, getCancelAction());
        // start listening
        simulator.getModel().addListener(this);
    }

    private JComponent createDocPane() {
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
        renderer.setLeafIcon(null);
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);
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
        return new JScrollPane(result);
    }

    private String getToolTip(Object value) {
        String result = null;
        if (this.toolTipMap != null) {
            result = this.toolTipMap.get(value);
        }
        return result;
    }

    private JToolBar createToolbar() {
        JToolBar result = new JToolBar();
        result.setFloatable(false);
        result.add(createButton(getNewAction()));
        result.add(createButton(getEditAction()));
        result.add(createButton(getSaveAction()));
        result.add(createButton(getCancelAction()));
        result.addSeparator();
        result.add(createButton(getCopyAction()));
        result.add(createButton(getDeleteAction()));
        result.add(createButton(getRenameAction()));
        result.addSeparator();
        result.add(new JLabel("Name: "));
        result.add(getNameField());
        result.addSeparator();
        // result.add(createButton(getPreviewAction()));
        result.add(createButton(getCtrlPreviewAction()));
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
        result.setFocusable(false);
        if (action.getValue(Action.SMALL_ICON) == null) {
            result.setMargin(new Insets(4, 2, 4, 2));
        } else {
            result.setHideActionText(true);
        }
        return result;
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag && getControlTextArea() != null) {
            getControlTextArea().requestFocus();
        }
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        if (changes.contains(Change.GRAMMAR)
            || changes.contains(Change.CONTROL)) {
            refreshAll();
        }
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
            String name = getSelectedControl().getName();
            int answer =
                JOptionPane.showConfirmDialog(this,
                    String.format("Save changes in '%s'?", name), null,
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
        SystemStore store = getGrammar().getStore();
        return store != null && store.isModifiable();
    }

    /**
     * Convenience method to return the current grammar view.
     */
    private StoredGrammarView getGrammar() {
        return getSimulatorModel().getGrammar();
    }

    /**
     * Convenience method to return the currently selected control view.
     */
    public final CtrlView getSelectedControl() {
        return getSimulatorModel().getControl();
    }

    /** Convenience method to indicate if a control view has been selected. */
    public final boolean isControlSelected() {
        return getSelectedControl() != null;
    }

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
    private void refreshAll() {
        for (Refreshable refreshable : this.refreshables) {
            refreshable.refresh();
        }
    }

    /** List of registered refreshables. */
    private final List<Refreshable> refreshables = new ArrayList<Refreshable>();

    /** Convenience method to retrieve the simulator model. */
    private SimulatorModel getSimulatorModel() {
        return getSimulator().getModel();
    }

    /** Returns the simulator to which the control panel belongs. */
    private Simulator getSimulator() {
        return this.simulator;
    }

    /** Simulator to which the control panel belongs. */
    private final Simulator simulator;

    /** Tool type map for syntax help. */
    private Map<?,String> toolTipMap;

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
                        getSimulatorModel().setControl(selectedItem);
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
            if (getGrammar() == null) {
                setEnabled(false);
            } else {
                Set<String> names =
                    new TreeSet<String>(getGrammar().getControlNames());
                for (String controlName : names) {
                    addItem(controlName);
                }
                if (isControlSelected()) {
                    setSelectedItem(getSelectedControl().getName());
                }
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
            ((RSyntaxDocument) getDocument()).setSyntaxStyle(new CtrlTokenMaker());
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
            setBackground();
        }

        private void setBackground() {
            if (isEnabled()) {
                setBackground(isEditable() ? JAttr.EDITOR_BACKGROUND
                        : ENABLED_COLOUR);
            } else {
                setBackground(DISABLED_COLOUR);
            }
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
                if (!enabled && isControlSelected()) {
                    enabled =
                        getSelectedControl().equals(
                            getGrammar().getControlView());
                }
                setEnabled(enabled);
                String program = "";
                if (isControlSelected()) {
                    CtrlView cv = getSelectedControl();
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

    /** Singular instance of the CancelAction. */
    private CancelAction cancelAction;

    /**
     * Action to cancel editing the currently displayed control program.
     */
    private class CancelAction extends RefreshableAction {
        public CancelAction() {
            super(Options.CANCEL_EDIT_ACTION_NAME, Icons.CANCEL_ICON);
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
     * Lazily creates and returns the singleton instance of the CopyAction.
     */
    private CopyAction getCopyAction() {
        if (this.copyAction == null) {
            this.copyAction = new CopyAction();
        }
        return this.copyAction;
    }

    /** Singular instance of the CopyAction. */
    private CopyAction copyAction;

    /**
     * Action to copy the currently displayed control program.
     */
    private class CopyAction extends RefreshableAction {
        public CopyAction() {
            super(Options.COPY_CONTROL_ACTION_NAME, Icons.COPY_ICON);
        }

        public void actionPerformed(ActionEvent e) {
            if (stopEditing(true)) {
                String oldName = getSelectedControl().getName();
                String newName =
                    getSimulator().askNewControlName(
                        "Select new control program name", oldName, true);
                if (newName != null) {
                    getSaveAction().doSave(newName,
                        getControlTextArea().getText());
                    refreshAll();
                }
            }
        }

        @Override
        public void refresh() {
            setEnabled(isControlSelected());
            if (getSimulator().getPanel() == getSimulator().getControlPanel()) {
                getSimulator().getCopyMenuItem().setAction(this);
            }
        }
    }

    /**
     * Lazily creates and returns the singleton instance of the DeleteAction.
     */
    private DeleteAction getDeleteAction() {
        if (this.deleteAction == null) {
            this.deleteAction = new DeleteAction();
        }
        return this.deleteAction;
    }

    /** Singular instance of the DeleteAction. */
    private DeleteAction deleteAction;

    /**
     * Action to delete the currently displayed control program.
     */
    private class DeleteAction extends RefreshableAction {
        public DeleteAction() {
            super(Options.DELETE_CONTROL_ACTION_NAME, Icons.DELETE_ICON);
        }

        public void actionPerformed(ActionEvent e) {
            String controlName = getSelectedControl().getName();
            if (getSimulator().confirmBehaviour(Options.DELETE_CONTROL_OPTION,
                String.format("Delete control program '%s'?", controlName))) {
                stopEditing(false);
                int itemNr = getNameField().getSelectedIndex() + 1;
                if (itemNr == getNameField().getItemCount()) {
                    itemNr -= 2;
                }
                try {
                    if (getSimulatorModel().doDeleteControl(controlName)) {
                        getSimulator().startSimulation();
                    }
                } catch (IOException exc) {
                    getSimulator().showErrorDialog(
                        String.format(
                            "Error while deleting control program '%s'",
                            controlName), exc);
                }
            }
        }

        @Override
        public void refresh() {
            setEnabled(isControlSelected());
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

    /** Singular instance of the EnableAction. */
    private DisableAction disableAction;

    /** Action to disable the currently displayed control program. */
    private class DisableAction extends RefreshableAction {
        public DisableAction() {
            super(Options.DISABLE_CONTROL_ACTION_NAME, Icons.DISABLE_ICON);
        }

        public void actionPerformed(ActionEvent a) {
            if (stopEditing(true)) {
                SystemProperties oldProperties = getGrammar().getProperties();
                SystemProperties newProperties = oldProperties.clone();
                newProperties.setUseControl(false);
                try {
                    getSimulator().getModel().doSetProperties(newProperties);
                } catch (IOException exc) {
                    getSimulator().showErrorDialog(
                        "Error while disabling control", exc);
                }
            }
        }

        @Override
        public void refresh() {
            setEnabled(getGrammar() != null && getGrammar().isUseControl());
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

    /** Singular instance of the EnableAction. */
    private EnableAction enableAction;

    /** Action to enable the currently displayed control program. */
    private class EnableAction extends RefreshableAction {
        public EnableAction() {
            super(Options.ENABLE_CONTROL_ACTION_NAME, Icons.ENABLE_ICON);
        }

        public void actionPerformed(ActionEvent evt) {
            if (stopEditing(true)) {
                doEnable(getSelectedControl().getName());
            }
        }

        /** Enables a control program with a given name. */
        public void doEnable(String controlName) {
            SystemProperties oldProperties = getGrammar().getProperties();
            SystemProperties newProperties = oldProperties.clone();
            newProperties.setUseControl(true);
            newProperties.setControlName(controlName);
            try {
                getSimulator().getModel().doSetProperties(newProperties);
            } catch (IOException exc) {
                getSimulator().showErrorDialog(
                    "Error while enabling control program " + controlName, exc);
            }
        }

        @Override
        public void refresh() {
            setEnabled(isControlSelected()
                && (!getGrammar().isUseControl() || !getGrammar().getControlName().equals(
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

    /** Singular instance of the EditAction. */
    private EditAction editAction;

    /** Action to start editing the currently displayed control program. */
    private class EditAction extends RefreshableAction {
        public EditAction() {
            super(Options.EDIT_CONTROL_ACTION_NAME, Icons.EDIT_ICON);
            putValue(ACCELERATOR_KEY, Options.EDIT_KEY);
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

    /** Singular instance of the NewAction. */
    private NewAction newAction;

    /** Action to create and start editing a new control program. */
    private class NewAction extends RefreshableAction {
        public NewAction() {
            super(Options.NEW_CONTROL_ACTION_NAME, Icons.NEW_ICON);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (stopEditing(true)) {
                String newName =
                    getSimulator().askNewControlName(
                        "Select control program name",
                        Groove.DEFAULT_CONTROL_NAME, true);
                try {
                    if (newName != null) {
                        getSimulatorModel().doAddControl(newName, "");
                        setDirty(true);
                        startEditing();
                    }
                } catch (IOException exc) {
                    getSimulator().showErrorDialog(
                        "Error creating new control program " + newName, exc);
                }
            }
        }

        @Override
        public void refresh() {
            setEnabled(getGrammar() != null);
        }
    }

    /**
     * Lazily creates and returns the singleton instance of the
     * {@link NewAction}.
     */
    private CtrlPreviewAction getCtrlPreviewAction() {
        if (this.ctrlPreviewAction == null) {
            this.ctrlPreviewAction = new CtrlPreviewAction();
        }
        return this.ctrlPreviewAction;
    }

    /** Singular instance of the CtrlPreviewAction. */
    private CtrlPreviewAction ctrlPreviewAction;

    /**
     * Creates a dialog showing the control automaton.
     */
    private class CtrlPreviewAction extends RefreshableAction {
        public CtrlPreviewAction() {
            super(Options.PREVIEW_CONTROL_ACTION_NAME, Icons.CONTROL_MODE_ICON);
        }

        public void actionPerformed(ActionEvent e) {
            if (stopEditing(true)) {
                try {
                    CtrlAut aut = getCtrlAut();
                    if (aut != null) {
                        getJGraph().setModel(aut);
                        getDialog().setVisible(true);
                    }
                } catch (FormatException exc) {
                    getSimulator().showErrorDialog(
                        String.format("Error in control program '%s'",
                            getSelectedControl()), exc);
                }
            }
        }

        @Override
        public void refresh() {
            try {
                setEnabled(getCtrlAut() != null);
            } catch (FormatException e) {
                setEnabled(false);
            }
        }

        private CtrlJGraph getJGraph() throws FormatException {
            if (this.jGraph == null) {
                this.jGraph = new CtrlJGraph(getSimulator());
                this.jGraph.setModel(getCtrlAut());
                this.jGraph.getLayouter().start(true);
            }
            return this.jGraph;
        }

        private CtrlJGraph jGraph;

        private JDialog getDialog() throws FormatException {
            JDialog result = this.dialog;
            if (result == null) {
                JGraphPanel<?> autPanel =
                    new JGraphPanel<CtrlJGraph>(getJGraph(), true);
                autPanel.initialise();
                result =
                    this.dialog =
                        new JDialog(getSimulator().getFrame(),
                            "Control Automaton");
                result.add(autPanel);
                result.setSize(600, 700);
                Point p = getSimulator().getFrame().getLocation();
                result.setLocation(new Point(p.x + 50, p.y + 50));
                result.setVisible(true);
            }
            return result;
        }

        private JDialog dialog;

        /** Convenience method to obtain the currently selected control automaton. */
        private CtrlAut getCtrlAut() throws FormatException {
            CtrlAut result = null;
            StoredGrammarView grammarView = getGrammar();
            if (grammarView != null) {
                GraphGrammar grammar = grammarView.toGrammar();
                CtrlView controlView = getSelectedControl();
                result =
                    controlView == null ? grammar.getCtrlAut()
                            : controlView.toCtrlAut(getGrammar().toGrammar());
            }
            return result;
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

    /** Singular instance of the RenameAction. */
    private RenameAction renameAction;

    /**
     * Action to rename the currently displayed control program.
     */
    private class RenameAction extends RefreshableAction {
        public RenameAction() {
            super(Options.RENAME_CONTROL_ACTION_NAME, Icons.RENAME_ICON);
        }

        public void actionPerformed(ActionEvent e) {
            if (stopEditing(true)) {
                String oldName = getSelectedControl().getName();
                String newName =
                    getSimulator().askNewControlName(
                        "Select control program name", oldName, false);
                if (newName != null) {
                    try {
                        getSimulatorModel().doRenameControl(oldName, newName);
                    } catch (IOException exc) {
                        getSimulator().showErrorDialog(
                            String.format(
                                "Error while renaming control program '%s' into '%s'",
                                oldName, newName), exc);
                    }
                }
            }
        }

        @Override
        public void refresh() {
            setEnabled(isControlSelected());
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

    /** Singular instance of the SaveAction. */
    private SaveAction saveAction;

    private class SaveAction extends RefreshableAction {
        public SaveAction() {
            super(Options.SAVE_CONTROL_ACTION_NAME, Icons.SAVE_ICON);
            putValue(ACCELERATOR_KEY, Options.SAVE_KEY);
        }

        public void actionPerformed(ActionEvent e) {
            if (isDirty()) {
                doSave(getSelectedControl().getName(),
                    getControlTextArea().getText());
                stopEditing(false);
            }
        }

        /** Executes the save action. */
        public void doSave(String name, String program) {
            try {
                getSimulator().getModel().doAddControl(name, program);
            } catch (IOException exc) {
                getSimulator().showErrorDialog(
                    "Error storing control program " + name, exc);
            }
        }

        @Override
        public void refresh() {
            setEnabled(isEditing());
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
