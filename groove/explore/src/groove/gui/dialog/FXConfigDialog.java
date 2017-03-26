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
 * $Id: ConfigDialog.java 5815 2016-10-27 10:58:04Z rensink $
 */
package groove.gui.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.eclipse.jdt.annotation.Nullable;

import groove.explore.config.ExploreKey;
import groove.gui.action.Refreshable;
import groove.util.collect.UncasedStringMap;
import groove.util.parse.Fallible;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.scene.layout.BorderPane;

/**
 * Dialog to manage configurations.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class FXConfigDialog<C extends Fallible & Observable> extends Dialog<C>
    implements Initializable {
    /**
     * Constructs a new dialog instance.
     */
    public FXConfigDialog() {
        this.refreshables = new ArrayList<>();
        this.configMap = new UncasedStringMap<>();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ConfigDialog.fxml"));
            loader.setController(this);
            loader.setRoot(getDialogPane());
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getButtonBar().setButtonOrder(ButtonBar.BUTTON_ORDER_NONE);
        getDialogPane().getButtonTypes()
            .setAll(APPLY_TYPE, REVERT_TYPE, START_TYPE, CLOSE_TYPE);
        setResultConverter(b -> getSelectedConfig());
        getApplyButton().setOnAction(this::doApply);
        getCloseButton().addEventFilter(ActionEvent.ACTION, e -> {
            if (!askSave()) {
                e.consume();
            }
        });
        getRevertButton().setOnAction(this::doRevert);
        addRefreshable(this::refreshApply);
        addRefreshable(this::refreshClose);
        addRefreshable(this::refreshCopy);
        addRefreshable(this::refreshDelete);
        addRefreshable(this::refreshNew);
        addRefreshable(this::refreshRevert);
        addRefreshable(this::refreshStart);
    }

    /** Retrieves the button bar from among the dialog pane's children. */
    private ButtonBar getButtonBar() {
        return getDialogPane().getChildren()
            .stream()
            .filter(n -> n instanceof ButtonBar)
            .map(n -> (ButtonBar) n)
            .findAny()
            .get();
    }

    /** Getter for the panel whose central component holds the configuration editor. */
    protected BorderPane getConfigPane() {
        return this.configPane;
    }

    /** The panel whose central component should hold the configuration editor. */
    @FXML
    private BorderPane configPane;

    private ListView<String> getConfigList() {
        return this.configList;
    }

    @FXML
    private ListView<String> configList;

    /** Sets the configuration list selection to a given name.
     * @param name the name to be selected; either {@code null} (in which case
     * the selection should be reset), or guaranteed to be
     * in the list
     */
    private void setConfigListSelection(String name) {
        boolean wasListening = resetConfigListListening();
        SelectionModel<String> selection = getConfigList().getSelectionModel();
        if (name == null) {
            selection.clearSelection();
        } else if (!name.equals(selection.getSelectedItem())) {
            selection.select(name);
        }
        setConfigListListening(wasListening);
    }

    /**
     * Removes the currently selected name from the configuration list.
     */
    private void removeConfigListSelection() {
        boolean wasListening = resetConfigListListening();
        getConfigList().getItems()
            .remove(getConfigList().getSelectionModel()
                .getSelectedIndex());
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

    /** Factory method for the help panel. */
    protected JComponent createHelpPanel() {
        return new JPanel();
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

    /**
     * Asks and attempts to save the current configuration, if it is dirty.
     * @return <code>true</code> if the dialog was not cancelled
     */
    boolean askSave() {
        if (!isDirty()) {
            return true;
        }
        Alert confirm = new Alert(AlertType.CONFIRMATION,
            String.format("Configuration '%s' has been modified. Save changes?", getSelectedName()),
            ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        Optional<ButtonType> answer = confirm.showAndWait();
        answer.ifPresent(b -> {
            if (b == ButtonType.YES) {
                saveConfig();
            } else if (b == ButtonType.NO) {
                setDirty(false);
            }
        });
        return answer.map(b -> b != ButtonType.CANCEL)
            .orElse(true);
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
        public DirtyListener(FXConfigDialog<?> dialog) {
            this.dialog = dialog;
        }

        final private FXConfigDialog<?> dialog;

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
        C newConfig = getSelectedConfig();
        if (!currentName.equals(newName)) {
            getConfigMap().remove(currentName);
            getConfigList().getItems()
                .remove(currentName);
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
        getConfigList().getItems()
            .add(index, newName);
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

    /** Callback factory method to create an empty configuration. */
    abstract protected C createConfig();

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

    /**
     * Callback method to create an object of the generic name type from a
     * string.
     */
    private final static String suggestedName = "newConfig";

    /** Implements the effect of the {@link #getApplyButton()}. */
    @FXML
    private void doApply(javafx.event.ActionEvent e) {
        saveConfig();
    }

    /** Refreshes the {@link #getApplyButton()}. */
    private void refreshApply() {
        getApplyButton().setDisable(!isDirty() || hasError());
    }

    private Button getApplyButton() {
        return (Button) getDialogPane().lookupButton(APPLY_TYPE);
    }

    /** Refreshes the {@link #getCloseButton()}. */
    private void refreshClose() {
        // close button is always enabled
    }

    private Button getCloseButton() {
        return (Button) getDialogPane().lookupButton(CLOSE_TYPE);
    }

    /** Implements the effect of the {@link #getCopyButton()}. */
    @FXML
    private void doCopy(javafx.event.ActionEvent e) {
        if (askSave()) {
            String currentName = getSelectedName();
            C currentConfig = getConfigMap().get(currentName);
            addConfig(generateNewName(currentName), currentConfig);
        }
    }

    /** Refreshes the {@link #getCopyButton()}. */
    private void refreshCopy() {
        getCopyButton().setDisable(!hasSelectedName());
    }

    private Button getCopyButton() {
        return this.copyButton;
    }

    /** Configuration copy button */
    @FXML
    private Button copyButton;

    /** Implements the effect of the {@link #getDeleteButton()}. */
    @FXML
    private void doDelete(javafx.event.ActionEvent e) {
        if (askDelete()) {
            deleteConfig();
        }
    }

    /** Asks and attempts to save the current configuration, if it is dirty. */
    private boolean askDelete() {
        Alert confirm = new Alert(AlertType.CONFIRMATION,
            String.format("Delete configuration '%s'?", getSelectedName()));
        return confirm.showAndWait()
            .map(b -> b == ButtonType.YES)
            .orElse(false);
    }

    /** Refreshes the {@link #getDeleteButton()}. */
    private void refreshDelete() {
        getDeleteButton().setDisable(!hasSelectedName());
    }

    private Button getDeleteButton() {
        return this.deleteButton;
    }

    /** Configuration delete button */
    @FXML
    private Button deleteButton;

    /** Implements the effect of the {@link #getNewButton()}. */
    @FXML
    private void doNew(javafx.event.ActionEvent e) {
        if (askSave()) {
            String newName = generateNewName(suggestedName);
            addConfig(newName, createConfig());
            refreshActions();
        }
    }

    /** Refreshes the {@link #getNewButton()}. */
    private void refreshNew() {
        getNewButton().setDisable(hasError());
    }

    /** Returns the new configuration button. */
    private Button getNewButton() {
        return this.newButton;
    }

    /** New configuration button */
    @FXML
    private Button newButton;

    /** Implements the effect of the {@link #getRevertButton()}. */
    @FXML
    private void doRevert(javafx.event.ActionEvent e) {
        revertConfig();
    }

    /** Refreshes the {@link #getRevertButton()}. */
    private void refreshRevert() {
        getRevertButton().setDisable(!isDirty());
    }

    private Button getRevertButton() {
        return (Button) getDialogPane().lookupButton(REVERT_TYPE);
    }

    /** Refreshes the {@link #getStartButton()}. */
    private void refreshStart() {
        getStartButton().setDisable(hasError() || isDirty() || !hasSelectedName());
    }

    private Button getStartButton() {
        return (Button) getDialogPane().lookupButton(START_TYPE);
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

    private static final ButtonType APPLY_TYPE = ButtonType.APPLY;
    private static final ButtonType REVERT_TYPE = new ButtonType("Revert");
    private static final ButtonType START_TYPE = new ButtonType("Start");
    private static final ButtonType CLOSE_TYPE = ButtonType.CLOSE;
}
