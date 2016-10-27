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
package groove.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.jdt.annotation.Nullable;

import groove.explore.config.ExploreKey;
import groove.gui.Icons;
import groove.gui.action.Refreshable;
import groove.util.collect.UncasedStringMap;

/**
 * Dialog to manage configurations.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class ConfigDialog<C> extends JDialog {
    /**
     * Constructs a new dialog instance.
     */
    public ConfigDialog() {
        this.refreshables = new ArrayList<>();
        this.configMap = new UncasedStringMap<>();
    }

    /**
     * Makes the dialog visible, and upon exit, returns the configuration to be started.
     * @return the selected configuration if the dialog was exited by the start action,
     * {@code null} if it was exited in another fashion.
     */
    public Object getConfiguration() {
        // construct the window
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Exploration configurations");
        JPanel contentPanel = new JPanel(new BorderLayout(3, 3));
        contentPanel.setBorder(createEmptyBorder());
        contentPanel.add(getListPanel(), BorderLayout.WEST);
        contentPanel.add(getConfigPanel(), BorderLayout.CENTER);
        ToolTipManager.sharedInstance()
            .registerComponent(contentPanel);
        setContentPane(contentPanel);
        pack();
        setVisible(true);
        return isStart() ? getConfigMap().get(getSelectedName()) : null;
    }

    /** Lazily creates and returns the panel containing the list of configuration names. */
    private JPanel getListPanel() {
        if (this.listPanel == null) {
            JToolBar listToolbar = new JToolBar();
            listToolbar.setFloatable(false);
            listToolbar.add(getNewAction());
            listToolbar.add(getCopyAction());
            listToolbar.add(getDeleteAction());

            this.listPanel = new JPanel(new BorderLayout());
            this.listPanel.setPreferredSize(new Dimension(200, 400));
            this.listPanel.setBorder(createLineBorder());
            this.listPanel.add(listToolbar, BorderLayout.NORTH);
            JScrollPane listScrollPanel = new JScrollPane(getConfigList());
            listScrollPanel.setBorder(null);
            this.listPanel.add(listScrollPanel);
        }
        return this.listPanel;
    }

    private JPanel listPanel;

    private JList<String> getConfigList() {
        if (this.configList == null) {
            this.configList = new JList<>();
            this.configList.setEnabled(true);
            this.configList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            this.configList.setPreferredSize(new Dimension(100, 100));
            this.configList.setModel(getConfigListModel());
            this.configList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    boolean wasListening = resetConfigListListening();
                    // get the name now because it may change if saving reorders the list
                    String name = getConfigList().getSelectedValue();
                    if (wasListening) {
                        if (askSave()) {
                            selectConfig(name);
                        } else {
                            // undo the selection
                            getConfigList().setSelectedValue(getSelectedName(), false);
                        }
                    }
                    setConfigListListening(wasListening);
                }
            });
        }
        return this.configList;
    }

    private JList<String> configList;

    /** Sets the configuration list selection to a given name.
     * @param name the name to be selected; either {@code null} (in which case
     * the selection should be reset), or guaranteed to be
     * in the list
     */
    private void setConfigListSelection(String name) {
        boolean wasListening = resetConfigListListening();
        if (name == null) {
            getConfigList().setSelectedIndex(-1);
        } else if (!name.equals(getConfigList().getSelectedValue())) {
            getConfigList().setSelectedValue(name, true);
        }
        setConfigListListening(wasListening);
    }

    /**
     * Removes the currently selected name from the configuration list.
     */
    private void removeConfigListSelection() {
        boolean wasListening = resetConfigListListening();
        getConfigListModel().remove(getConfigList().getSelectedIndex());
        setConfigListListening(wasListening);
    }

    boolean resetConfigListListening() {
        boolean result = this.configListListening;
        this.configListListening = false;
        return result;
    }

    void setConfigListListening(boolean listening) {
        this.configListListening = listening;
    }

    private boolean configListListening = true;

    private DefaultListModel<String> getConfigListModel() {
        if (this.configListModel == null) {
            this.configListModel = new DefaultListModel<>();
        }
        return this.configListModel;
    }

    private DefaultListModel<String> configListModel;

    /** Returns the panel with the name field, main panel, syntax help, close buttons and error field. */
    private JPanel getConfigPanel() {
        if (this.configPanel == null) {
            // configuration name
            JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            namePanel.add(new JLabel("Name:"));
            namePanel.add(getNameField());
            // error panel
            JPanel errorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            errorPanel.add(getErrorLabel());
            // action buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(new JButton(getApplyAction()));
            buttonPanel.add(new JButton(getRevertAction()));
            buttonPanel.add(new JButton(getStartAction()));
            buttonPanel.add(new JButton(getCloseAction()));

            JComponent mainPanel = createMainPanel();

            JPanel bottomPanel = new JPanel();
            bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
            bottomPanel.add(errorPanel);
            bottomPanel.add(buttonPanel);

            this.configPanel = new JPanel(new BorderLayout(3, 3));
            this.configPanel.setBorder(createBorder());
            this.configPanel.add(namePanel, BorderLayout.NORTH);
            this.configPanel.add(mainPanel, BorderLayout.CENTER);
            this.configPanel.add(bottomPanel, BorderLayout.SOUTH);
        }
        return this.configPanel;
    }

    private JPanel configPanel;

    /** Returns the current content of the name field. */
    String getEditedName() {
        return getNameField().getText();
    }

    private NameField getNameField() {
        if (this.nameField == null) {
            this.nameField = new NameField();
        }
        return this.nameField;
    }

    private NameField nameField;

    private class NameField extends JTextField implements Refreshable {
        NameField() {
            setPreferredSize(new Dimension(200, 25));
            getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void removeUpdate(DocumentEvent e) {
                    notifyNameChanged();
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    notifyNameChanged();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    notifyNameChanged();
                }

                /** Notifies the dialog of a change in the name field. */
                void notifyNameChanged() {
                    String editedName = getEditedName();
                    String nameError = null;
                    if (hasSelectedName() && !editedName.equals(getSelectedName())) {
                        if (editedName.isEmpty()) {
                            nameError = "Empty configuration name";
                        } else if (getConfigMap().containsKey(editedName)) {
                            nameError = "Existing configuration name '" + editedName + "'";
                        } else {
                            boolean validFile;
                            try {
                                File file = new File(editedName).getCanonicalFile();
                                validFile = file.getName()
                                    .equals(editedName);
                            } catch (IOException exc) {
                                validFile = false;
                            } catch (SecurityException exc) {
                                validFile = false;
                            }
                            if (!validFile) {
                                nameError = "Invalid configuration name '" + editedName + "'";
                            }
                        }
                    }
                    getErrorLabel().setError(ERROR_KIND, nameError);
                    testSetDirty();
                    refreshActions();
                }

                private static final String ERROR_KIND = "CONFIG_NAME";
            });
            addRefreshable(this);
        }

        @Override
        public void refresh() {
            setEnabled(hasSelectedName());
        }
    }

    /** Factory method for the main panel. */
    protected JComponent createMainPanel() {
        return new JPanel();
    }

    /** Factory method for the help panel. */
    protected JComponent createHelpPanel() {
        return new JPanel();
    }

    /** Factory method for a line border with small insets. */
    protected final Border createBorder() {
        return BorderFactory.createCompoundBorder(createLineBorder(), createEmptyBorder());
    }

    /** Factory method for a line border. */
    public final Border createLineBorder() {
        return BorderFactory.createLineBorder(Color.DARK_GRAY);
    }

    /** Factory method for small insets border. */
    public final Border createEmptyBorder() {
        return BorderFactory.createEmptyBorder(3, 3, 3, 3);
    }

    /** Returns the currently selected configuration, if any. */
    protected final @Nullable C getSelectedConfig() {
        return hasSelectedName() ? getConfigMap().get(getSelectedName()) : null;
    }

    /** Returns the mapping from names (modulo case distinctions) to configurations. */
    protected final TreeMap<String,C> getConfigMap() {
        return this.configMap;
    }

    /** Mapping from names to corresponding configurations. */
    private final TreeMap<String,C> configMap;

    /** Sets the start flag to {@code true}. */
    void setStart() {
        this.start = true;
    }

    /** Indicates if the start action has been invoked. */
    boolean isStart() {
        return this.start;
    }

    /** Flag recording if the start action has been invoked. */
    private boolean start;

    /** Indicates that there is a currently selected configuration. */
    public boolean hasSelectedName() {
        return getSelectedName() != null;
    }

    /** Returns the currently selected configuration name, if any. */
    String getSelectedName() {
        return this.selectedName;
    }

    /** Selects a new configuration name. */
    void setSelectedName(String selectedName) {
        this.selectedName = selectedName;
    }

    /** Currently selected name. */
    private String selectedName;

    /** Asks and attempts to save the current configuration, if it is dirty. */
    boolean askSave() {
        if (!isDirty()) {
            return true;
        }
        int answer = JOptionPane.showConfirmDialog(this,
            String.format("Configuration '%s' has been modified. Save changes?", getSelectedName()),
            null,
            JOptionPane.YES_NO_CANCEL_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            saveConfig();
        } else if (answer == JOptionPane.NO_OPTION) {
            setDirty(false);
        }
        return answer != JOptionPane.CANCEL_OPTION;
    }

    /** Indicates that the currently selected configuration has unsaved changes. */
    boolean isDirty() {
        return this.dirty;
    }

    /**
     * Changes the dirty status of the currently selected configuration.
     * @return if {@code true}, the dirty status has changed as a result of this action
     */
    boolean setDirty(boolean dirty) {
        boolean result = this.dirty != dirty;
        if (result) {
            this.dirty = dirty;
        }
        return result;
    }

    /** Tests whether the currently edited configuration differs from the
     * stored (selected) configuration, and sets the dirty flag accordingly.
     * @return if {@code true}, the dirty status has changed as a result of this action
     */
    boolean testSetDirty() {
        assert hasSelectedName();
        boolean dirty;
        if (!hasSelectedName()) {
            dirty = false;
        } else {
            String currentName = getSelectedName();
            dirty = !currentName.equals(getEditedName()) || testDirty();
        }
        return setDirty(dirty);
    }

    /** Hook to test if any part of the currently edited configuration is dirty. */
    protected boolean testDirty() {
        return false;
    }

    /** Flag indicated that the currently selected configuration has unsaved changes. */
    private boolean dirty;

    /** Sets the listening mode for editor dirt to {@code false},
     * and returns the previously set mode.
     */
    public boolean resetDirtListening() {
        boolean result = this.dirtListening;
        this.dirtListening = false;
        return result;
    }

    /** Sets the dirt listening mode to a given value. */
    public void setDirtListening(boolean listening) {
        this.dirtListening = listening;
    }

    private boolean dirtListening = true;

    /**
     * Returns a listener that when triggered will test whether any editor is dirty,
     * and refresh all refreshables.
     */
    public DirtyListener getDirtyListener() {
        if (this.dirtyListener == null) {
            this.dirtyListener = new DirtyListener(this);
        }
        return this.dirtyListener;
    }

    private DirtyListener dirtyListener;

    /** Listener class testing for dirtiness upon triggering. */
    public static class DirtyListener implements ItemListener, DocumentListener {
        /** Constructs a listener for a given dialog. */
        public DirtyListener(ConfigDialog<?> dialog) {
            this.dialog = dialog;
        }

        final private ConfigDialog<?> dialog;

        /**
         * Notification method of the {@link ItemListener}
         */
        @Override
        public void itemStateChanged(ItemEvent e) {
            notifyChanged();
        }

        /**
         * Notification method of the {@link DocumentListener}
         */
        @Override
        public void insertUpdate(DocumentEvent e) {
            notifyDocumentChanged();
        }

        /**
         * Notification method of the {@link DocumentListener}
         */
        @Override
        public void removeUpdate(DocumentEvent e) {
            notifyDocumentChanged();
        }

        /**
         * Notification method of the {@link DocumentListener}
         */
        @Override
        public void changedUpdate(DocumentEvent e) {
            notifyDocumentChanged();
        }

        /**
         * Callback method invoked when the listener is used on a document,
         * and the document has changed.
         */
        protected void notifyDocumentChanged() {
            notifyChanged();
        }

        /**
         * If the listener is currently listening, tests
         * for dirt and updates all actions.
         */
        private void notifyChanged() {
            boolean wasListening = this.dialog.resetDirtListening();
            if (wasListening) {
                this.dialog.testSetDirty();
                this.dialog.refreshActions();
            }
            this.dialog.setDirtListening(wasListening);
        }
    }

    /** Deletes the currently selected configuration. */
    void deleteConfig() {
        boolean wasListening = resetConfigListListening();
        String currentName = getSelectedName();
        String nextName = getConfigMap().higherKey(currentName);
        getConfigMap().remove(currentName);
        removeConfigListSelection();
        // select another configuration, if there is one
        if (nextName == null && !getConfigMap().isEmpty()) {
            nextName = getConfigMap().lastKey();
        }
        selectConfig(nextName);
        setConfigListListening(wasListening);
    }

    /** Saves the changes in the currently selected configuration. */
    void saveConfig() {
        boolean wasListening = resetConfigListListening();
        String currentName = getSelectedName();
        String newName = getEditedName();
        C newConfig = extractConfig();
        if (!currentName.equals(newName)) {
            getConfigMap().remove(currentName);
            getConfigListModel().removeElement(currentName);
            addConfig(newName, newConfig);
        } else {
            getConfigMap().put(currentName, newConfig);
            selectConfig(newName);
        }
        setConfigListListening(wasListening);
    }

    /** Sets the edited configuration beck to the stored one. */
    void revertConfig() {
        selectConfig(getSelectedName());
    }

    /**
     * Creates a fresh configuration for a given name, and adds it to the map.
     * Should only be called if the current configuration is not dirty.
     * @param newName name of the configuration; should be fresh with respect to
     * the existing names
     */
    void addConfig(String newName, C newConfig) {
        assert !isDirty();
        assert !this.configMap.containsKey(newName);
        getConfigMap().put(newName, newConfig);
        int index = getConfigMap().headMap(newName)
            .size();
        getConfigListModel().add(index, newName);
        selectConfig(newName);
    }

    /**
     * Sets the selected configuration to a given name, and
     * refreshes the dirty status and the actions.
     * @param name the name of the configuration to be selected;
     * if {@code null}, there should be no selection
     */
    protected void selectConfig(String name) {
        setSelectedName(name);
        getNameField().setText(name);
        setConfigListSelection(name);
        setDirty(false);
        refreshActions();
    }

    /** Callback method to create a fresh configuration. */
    abstract protected C createConfig();

    /** Callback method to extract the configuration from the current settings. */
    protected C extractConfig() {
        // stopgap implementation
        return getConfigMap().get(getSelectedName());
    }

    /**
     * Generates a fresh name by extending a given name so that it does not
     * occur in the set of configurations.
     * @param basis the name to be extended (non-null)
     */
    String generateNewName(String basis) {
        String result = basis;
        for (int i = 1; this.configMap.containsKey(result); i++) {
            result = basis + i;
        }
        return result;
    }

    @Override
    public void dispose() {
        if (askSave()) {
            super.dispose();
        }
    }

    /**
     * Callback method to create an object of the generic name type from a
     * string.
     */
    private final static String suggestedName = "newConfig";

    private static abstract class RefreshableAction extends javax.swing.AbstractAction
        implements Refreshable {
        /** Constructor for subclassing. */
        protected RefreshableAction(String name) {
            super(name);
        }

        /** Constructor for subclassing. */
        protected RefreshableAction(String name, Icon icon) {
            super(name, icon);
        }
    }

    /** Refreshes all refreshables. */
    void refreshActions() {
        for (Refreshable refreshable : this.refreshables) {
            refreshable.refresh();
        }
    }

    /** Adds a refreshable to the list. */
    public void addRefreshable(Refreshable refreshable) {
        refreshable.refresh();
        this.refreshables.add(refreshable);
    }

    private final List<Refreshable> refreshables;

    private ApplyAction getApplyAction() {
        if (this.applyAction == null) {
            this.applyAction = new ApplyAction();
        }
        return this.applyAction;
    }

    private ApplyAction applyAction;

    private class ApplyAction extends RefreshableAction {
        public ApplyAction() {
            super("Apply");
            addRefreshable(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            saveConfig();
        }

        @Override
        public void refresh() {
            setEnabled(isDirty() && !hasError());
        }
    }

    private CloseAction getCloseAction() {
        if (this.closeAction == null) {
            this.closeAction = new CloseAction();
        }
        return this.closeAction;
    }

    private CloseAction closeAction;

    private class CloseAction extends RefreshableAction {
        public CloseAction() {
            super("Close");
            addRefreshable(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }

        @Override
        public void refresh() {
            // always enabled
        }
    }

    private CopyAction getCopyAction() {
        if (this.copyAction == null) {
            this.copyAction = new CopyAction();
        }
        return this.copyAction;
    }

    private CopyAction copyAction;

    private class CopyAction extends RefreshableAction {
        public CopyAction() {
            super("Copy Configuration", Icons.COPY_ICON);
            addRefreshable(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (askSave()) {
                String currentName = getSelectedName();
                C currentConfig = getConfigMap().get(currentName);
                addConfig(generateNewName(currentName), currentConfig);
            }
        }

        @Override
        public void refresh() {
            setEnabled(!hasError() && hasSelectedName());
        }
    }

    private DeleteAction getDeleteAction() {
        if (this.deleteAction == null) {
            this.deleteAction = new DeleteAction();
        }
        return this.deleteAction;
    }

    private DeleteAction deleteAction;

    private class DeleteAction extends RefreshableAction {
        public DeleteAction() {
            super("Delete Configuration", Icons.DELETE_ICON);
            addRefreshable(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (askDelete()) {
                deleteConfig();
            }
        }

        /** Asks and attempts to save the current configuration, if it is dirty. */
        boolean askDelete() {
            int answer = JOptionPane.showConfirmDialog(ConfigDialog.this,
                String.format("Delete configuration '%s'?", getSelectedName()),
                null,
                JOptionPane.YES_NO_OPTION);
            return answer == JOptionPane.YES_OPTION;
        }

        @Override
        public void refresh() {
            setEnabled(hasSelectedName());
        }
    }

    private NewAction getNewAction() {
        if (this.newAction == null) {
            this.newAction = new NewAction();
        }
        return this.newAction;
    }

    private NewAction newAction;

    private class NewAction extends RefreshableAction {
        public NewAction() {
            super("New Configuration", Icons.NEW_ICON);
            addRefreshable(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (askSave()) {
                String newName = generateNewName(suggestedName);
                addConfig(newName, createConfig());
                refreshActions();
            }
        }

        @Override
        public void refresh() {
            setEnabled(!hasError());
        }
    }

    private StartAction getStartAction() {
        if (this.startAction == null) {
            this.startAction = new StartAction();
        }
        return this.startAction;
    }

    private StartAction startAction;

    private class StartAction extends RefreshableAction {
        public StartAction() {
            super("Start");
            addRefreshable(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            setStart();
            dispose();
        }

        @Override
        public void refresh() {
            setEnabled(!hasError() && !isDirty() && hasSelectedName());
        }
    }

    private RevertAction getRevertAction() {
        if (this.revertAction == null) {
            this.revertAction = new RevertAction();
        }
        return this.revertAction;
    }

    private RevertAction revertAction;

    private class RevertAction extends RefreshableAction {
        public RevertAction() {
            super("Revert");
            addRefreshable(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            revertConfig();
        }

        @Override
        public void refresh() {
            setEnabled(isDirty());
        }
    }

    /** Sets or resets an error of a particular category. */
    public void setError(ExploreKey category, String error) {
        getErrorLabel().setError(category, error);
    }

    /** Convenience method to test if the error field contains an error. */
    boolean hasError() {
        return getErrorLabel().hasError();
    }

    private ErrorField getErrorLabel() {
        if (this.errorLabel == null) {
            this.errorLabel = new ErrorField();
            this.errorLabel.setForeground(Color.RED);
        }
        return this.errorLabel;
    }

    private ErrorField errorLabel;

    private class ErrorField extends JLabel implements Refreshable {
        public ErrorField() {
            setForeground(Color.RED);
            this.errorMap = new LinkedHashMap<>();
            addRefreshable(this);
        }

        /** Sets the name error to a given value.
         * @param category the category of the error; only used to distinguish errors
         * @param error the error text; if {@code null} or empty, the error is reset
         */
        void setError(Object category, String error) {
            if (error == null || error.isEmpty()) {
                this.errorMap.remove(category);
            } else {
                this.errorMap.put(category, error);
            }
            showError();
        }

        private final Map<Object,String> errorMap;

        /** Sets the error fields from the recorded error. */
        private void showError() {
            if (this.errorMap.isEmpty()) {
                setText("");
            } else {
                setText(this.errorMap.values()
                    .iterator()
                    .next());
            }
        }

        /** Tests if the field currently contains an error. */
        boolean hasError() {
            return !this.errorMap.isEmpty();
        }

        @Override
        public void refresh() {
            setEnabled(hasSelectedName());
            showError();
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension result = super.getPreferredSize();
            result.height = Math.max(result.height, 15);
            return result;
        }
    }

    /** Main method, for testing purposes. */
    public static void main(String[] args) {
        new ConfigDialog<Object>() {
            @Override
            protected Object createConfig() {
                return new Object();
            }
        }.getConfiguration();
    }
}
