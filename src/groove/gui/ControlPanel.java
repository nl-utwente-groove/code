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
import groove.control.parse.CtrlTokenMaker;
import groove.gui.SimulatorModel.Change;
import groove.gui.SimulatorPanel.TabKind;
import groove.gui.action.ActionStore;
import groove.gui.jgraph.JAttr;
import groove.io.HTMLConverter;
import groove.io.store.SystemStore;
import groove.view.CtrlView;
import groove.view.StoredGrammarView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
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
final public class ControlPanel extends JPanel implements SimulatorListener,
        SimulatorTab {
    /**
     * @param simulator The Simulator the panel is added to.
     */
    public ControlPanel(Simulator simulator) {
        this.simulator = simulator;
        // create the layout for this JPanel
        this.setLayout(new BorderLayout());
        this.setFocusable(false);
        this.setFocusCycleRoot(true);
    }

    @Override
    public TabKind getKind() {
        return TabKind.CONTROL;
    }

    @Override
    public JPanel getMainPanel() {
        return this;
    }

    @Override
    public String getCurrent() {
        return isControlSelected() ? getSelectedControl().getName() : null;
    }

    /**
     * Initialises the GUI.
     * Should be called after the constructor, and
     * before using the object in any way.
     */
    public void initialise() {
        // fill in the GUI
        RTextScrollPane scroller =
            new RTextScrollPane(500, 400, getControlTextArea(), true);
        this.add(createToolbar(), BorderLayout.NORTH);
        // set up the split editor pane
        JSplitPane splitPane =
            new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroller,
                new JScrollPane(getDocPane()));
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(1.0);

        add(splitPane, BorderLayout.CENTER);
        add(getStatusBar(), BorderLayout.SOUTH);
        // add keyboard binding for Save key
        InputMap focusedInputMap =
            getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        String actionName = Options.getSaveControlActionName(false);
        focusedInputMap.put(Options.SAVE_KEY, actionName);
        focusedInputMap.put(Options.CANCEL_KEY, Options.CANCEL_EDIT_ACTION_NAME);
        getActionMap().put(actionName, getActions().getSaveControlAction());
        getActionMap().put(Options.CANCEL_EDIT_ACTION_NAME,
            getActions().getCancelEditControlAction());
        // start listening
        this.simulator.getModel().addListener(this, Change.GRAMMAR,
            Change.CONTROL);
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
        result.setCellRenderer(renderer);
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

    private JToolBar createToolbar() {
        JToolBar result = new JToolBar();
        result.setFloatable(false);
        result.add(createButton(getActions().getNewControlAction()));
        result.add(createButton(getActions().getEditControlAction()));
        result.add(createButton(getActions().getSaveControlAction()));
        result.add(createButton(getActions().getCancelEditControlAction()));
        //        result.addSeparator();
        //        result.add(createButton(getActions().getCopyControlAction()));
        //        result.add(createButton(getActions().getDeleteControlAction()));
        //        result.add(createButton(getActions().getRenameControlAction()));
        //        result.addSeparator();
        //        result.add(new JLabel("Name: "));
        //        result.add(getNameField());
        result.addSeparator();
        // result.add(createButton(getPreviewAction()));
        result.add(createButton(getActions().getPreviewControlAction()));
        //        result.add(createButton(getActions().getDisableControlAction()));
        //        result.add(createButton(getActions().getEnableControlAction()));
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

    /**
     * Returns the status bar of this panel, if any.
     */
    private JLabel getStatusBar() {
        return this.statusBar;
    }

    /** Text shown in the status bar of this panel. */
    private String getStatusText() {
        StringBuilder result = new StringBuilder();
        if (getSimulatorModel().getControl() == null) {
            result.append("No control program selected");
        } else {
            String controlName = getSimulatorModel().getControl().getName();
            result.append("Control program: ");
            result.append(HTMLConverter.STRONG_TAG.on(controlName));
            if (!controlName.equals(getSimulatorModel().getGrammar().getControlName())) {
                result.append(" (disabled)");
            }
        }
        return HTMLConverter.HTML_TAG.on(result).toString();
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag && getControlTextArea() != null) {
            getControlTextArea().requestFocus();
        }
    }

    /** Returns the GUI component showing the list of control program names. */
    public JPanel getListPanel() {
        if (this.listPanel == null) {
            JToolBar toolBar = getSimulator().createToolBar();
            getList().fillToolBar(toolBar);
            JScrollPane controlPane = new JScrollPane(getList()) {
                @Override
                public Dimension getPreferredSize() {
                    Dimension superSize = super.getPreferredSize();
                    return new Dimension((int) superSize.getWidth(),
                        Simulator.START_LIST_MINIMUM_HEIGHT);
                }
            };

            this.listPanel = new JPanel(new BorderLayout(), false);
            this.listPanel.add(toolBar, BorderLayout.NORTH);
            this.listPanel.add(controlPane, BorderLayout.CENTER);
            // make sure tool tips get displayed
            ToolTipManager.sharedInstance().registerComponent(this.listPanel);
        }
        return this.listPanel;
    }

    /** Returns the list of control programs. */
    private ControlJList getList() {
        if (this.controlJList == null) {
            this.controlJList = new ControlJList(getSimulator());
        }
        return this.controlJList;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        if (changes.contains(Change.GRAMMAR)
            || changes.contains(Change.CONTROL)) {
            refreshAll();
            getDocPane().setBackground(isControlSelected() ? Color.WHITE : null);
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
                getActions().getSaveControlAction().doSave(name);
            } else {
                result = answer == JOptionPane.NO_OPTION;
            }
        }
        return result;
    }

    /** Indicates if the currently loaded grammar is modifiable. */
    public boolean isModifiable() {
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

    /** Stops the editing, and returns the current text of the control program. */
    public String stopEditing() {
        this.editing = false;
        setDirty(false);
        return getControlTextArea().getText();
    }

    /**
     * Cancels the current editing action, if any.
     * @param confirm indicates if the user should be asked for confirmation
     * @return if editing was indeed stopped
     */
    public boolean cancelEditing(boolean confirm) {
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
    public void startEditing() {
        assert !isEditing();
        this.editing = true;
        refreshAll();
    }

    /**
     * Indicates the current editing mode.
     * @return if <code>true</code>, an editing action is going on.
     */
    public boolean isEditing() {
        return this.editing;
    }

    /** Flag indicating if an editing action is going on. */
    private boolean editing;

    /**
     * Returns the dirty status of the editor.
     * @return <code>true</code> if the editor is dirty.
     */
    public final boolean isDirty() {
        return this.dirty;
    }

    /** Sets the status of the editor. */
    public final void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /** Flag indicating if the editor is dirty. */
    private boolean dirty;

    /**
     * Registers a refreshable.
     * @see #refreshAll()
     */
    public void addRefreshable(Refreshable refreshable) {
        this.refreshables.add(refreshable);
    }

    /** Refreshes all registered refreshables. */
    private void refreshAll() {
        for (Refreshable refreshable : this.refreshables) {
            refreshable.refresh();
        }
        getStatusBar().setText(getStatusText());
    }

    /** List of registered refreshables. */
    private final List<Refreshable> refreshables = new ArrayList<Refreshable>();

    /** Convenience method to retrieve the simulator model. */
    private SimulatorModel getSimulatorModel() {
        return getSimulator().getModel();
    }

    /** Convenience method to return the action store. */
    private ActionStore getActions() {
        return getSimulator().getActions();
    }

    /** Returns the simulator to which the control panel belongs. */
    public Simulator getSimulator() {
        return this.simulator;
    }

    /** Simulator to which the control panel belongs. */
    private final Simulator simulator;

    /** Production system control program list. */
    private ControlJList controlJList;

    /** Documentation tree. */
    private JTree docPane;

    /** Panel with the {@link #controlJList}. */
    private JPanel listPanel;

    /** Tool type map for syntax help. */
    private Map<?,String> toolTipMap;
    //
    //    /** Lazily creates and returns the field displaying the control name. */
    //    public JComboBox getNameField() {
    //        if (this.nameField == null) {
    //            this.nameField = new ControlNameField();
    //            this.nameField.setBorder(BorderFactory.createLoweredBevelBorder());
    //            this.nameField.setMaximumSize(new Dimension(150, 24));
    //        }
    //        return this.nameField;
    //    }

    private final JLabel statusBar = new JLabel(" ");

    //
    //    /** Name field of the control program. */
    //    private ControlNameField nameField;
    //
    //    private class ControlNameField extends JComboBox implements Refreshable {
    //        public ControlNameField() {
    //            setBorder(BorderFactory.createLoweredBevelBorder());
    //            setMaximumSize(new Dimension(150, 24));
    //            setEnabled(false);
    //            setEditable(false);
    //            this.selectionListener = new ActionListener() {
    //                @Override
    //                public void actionPerformed(ActionEvent e) {
    //                    String selectedItem = (String) getSelectedItem();
    //                    if (selectedItem != null && stopEditing(true)) {
    //                        getSimulatorModel().setControl(selectedItem);
    //                    }
    //                }
    //            };
    //            addActionListener(this.selectionListener);
    //            addRefreshable(this);
    //        }
    //
    //        @Override
    //        public void refresh() {
    //            removeActionListener(this.selectionListener);
    //            this.removeAllItems();
    //            if (getGrammar() == null) {
    //                setEnabled(false);
    //            } else {
    //                Set<String> names =
    //                    new TreeSet<String>(getGrammar().getControlNames());
    //                for (String controlName : names) {
    //                    addItem(controlName);
    //                }
    //                if (isControlSelected()) {
    //                    setSelectedItem(getSelectedControl().getName());
    //                }
    //                setEnabled(getItemCount() > 0);
    //            }
    //            addActionListener(this.selectionListener);
    //        }
    //
    //        private final ActionListener selectionListener;
    //    }

    /** Lazily creates and returns the area displaying the control program. */
    public ControlTextArea getControlTextArea() {
        if (this.controlTextArea == null) {
            this.controlTextArea = new ControlTextArea();
        }
        return this.controlTextArea;
    }

    /** Panel showing the control program. */
    private ControlTextArea controlTextArea;

    /** Text area that holds the current control program. */
    public class ControlTextArea extends RSyntaxTextArea implements Refreshable {
        /** Constructs an instance of this class. */
        public ControlTextArea() {
            super(new RSyntaxDocument("gcl"));
            ((RSyntaxDocument) getDocument()).setSyntaxStyle(new CtrlTokenMaker());
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
            setEditable(false);
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
                //                boolean enabled = isEditing();
                //                if (!enabled && isControlSelected()) {
                //                    enabled =
                //                        getSimulatorModel().getControl().equals(
                //                            getGrammar().getControlView());
                //                }
                setEnabled(isControlSelected());
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
