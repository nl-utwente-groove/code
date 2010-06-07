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
 * $Id: FormulaDialog.java,v 1.9 2008-02-04 08:50:00 kastenberg Exp $
 */
package groove.gui.dialog;

import groove.gui.Options;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * Dialog for entering strings.
 * The dialog remembers previously entered strings and attempts to autocomplete.
 * @author Arend Rensink
 * @version $Revision:  $
 */
public class StringDialog {
    /**
     * Constructs an instance of the dialog for a given parent frame and
     * a dialog title.
     */
    public StringDialog(String title) {
        this.history = new ArrayList<String>();
        this.title = title;
    }

    /**
     * Makes the dialog visible and awaits the user's response. Since the dialog
     * is modal, this method returns only when the user closes the dialog. The
     * return value indicates if the properties have changed.
     * @param frame the frame on which the dialog is to be displayed
     */
    public String showDialog(Component frame) {
        this.dialog = createDialog(frame);
        if (this.title != null) {
            String[] storedValues = Options.getUserPrefs(this.title);
            this.history.clear();
            for (String value : storedValues) {
                this.history.add(value);
            }
        }
        getChoiceBox().setSelectedItem("");
        getEditor().setText("");
        getChoiceBox().revalidate();
        getEditor().selectAll();
        this.dialog.pack();
        this.dialog.setVisible(true);
        if (this.title != null) {
            String[] storedValues =
                new String[Math.min(this.history.size(), MAX_PERSISTENT_SIZE)];
            for (int i = 0; i < storedValues.length; i++) {
                storedValues[i] = this.history.get(i);
            }
            Options.storeUserPrefs(this.title, storedValues);
        }
        return getResult();
    }

    /**
     * Creates and returns a fresh dialog for the given frame.
     */
    private JDialog createDialog(Component frame) {
        Object[] buttons = new Object[] {getOkButton(), getCancelButton()};
        JOptionPane panel =
            new JOptionPane(getChoiceBox(), JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION, null, buttons);
        JDialog result = panel.createDialog(frame, this.title);
        result.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        result.addWindowListener(this.closeListener);
        return result;
    }

    /** Lazily creates and returns the combobox containing the current choices. */
    private MyComboBox getChoiceBox() {
        if (this.choiceBox == null) {
            this.choiceBox = new MyComboBox();
            this.choiceBox.setPrototypeDisplayValue("The longest value we want to display completely");
            this.choiceBox.setModel(createModel());
            this.choiceBox.setEditable(true);
            JTextField editor =
                (JTextField) this.choiceBox.getEditor().getEditorComponent();
            editor.addActionListener(this.closeListener);
            editor.getDocument().addDocumentListener(this.changeListener);
        }
        return this.choiceBox;
    }

    /**
     * Creates and initialises a fresh instance of {@link MyComboBoxModel}.
     */
    private MyComboBoxModel createModel() {
        MyComboBoxModel result = new MyComboBoxModel();
        result.setDirty("");
        return result;
    }

    /** Returns the editor currently used in the {@link #choiceBox}. */
    private JTextField getEditor() {
        return (JTextField) getChoiceBox().getEditor().getEditorComponent();
    }

    /** Returns the model currently used in the {@link #choiceBox}. */
    private MyComboBoxModel getModel() {
        return (MyComboBoxModel) getChoiceBox().getModel();
    }

    /** Reacts to a change in the editor. */
    private void processTextChange() {
        final String currentText = getEditor().getText();
        getOkButton().setEnabled(!currentText.isEmpty());
        getModel().setDirty(currentText);
    }

    /** The choice box */
    private MyComboBox choiceBox;

    /**
     * Lazily creates and returns a button labelled OK.
     * @return the ok button
     */
    private JButton getOkButton() {
        if (this.okButton == null) {
            this.okButton = new JButton("OK");
            this.okButton.addActionListener(this.closeListener);
            this.okButton.setEnabled(false);
        }
        return this.okButton;
    }

    /** The OK button on the option pane. */
    private JButton okButton;

    /**
     * Lazily creates and returns a button labelled CANCEL.
     * @return the cancel button
     */
    private JButton getCancelButton() {
        if (this.cancelButton == null) {
            this.cancelButton = new JButton("Cancel");
            this.cancelButton.addActionListener(this.closeListener);
        }
        return this.cancelButton;
    }

    /** The CANCEL button on the option pane. */
    private JButton cancelButton;

    /** The history list */
    private final List<String> history;

    /** The title of the dialog. */
    private final String title;

    /**
     * Sets the result of the dialog from the 
     * selection of the choice box.
     * Also adds the result to the history.
     */
    private boolean setResult(Object resultObject) {
        this.result = resultObject == null ? null : resultObject.toString();
        if (this.result != null && !this.result.isEmpty()) {
            this.history.remove(this.result);
            this.history.add(0, this.result);
        }
        return this.result == null || !this.result.isEmpty();
    }

    /**
     * Return the property that is entered for verification.
     * @return the property in String format
     */
    public String getResult() {
        return this.result;
    }

    /** The field in which to store the provided data */
    private String result;

    /** The dialog that is currently visible. */
    private JDialog dialog;

    /** The singleton action listener. */
    private final CloseListener closeListener = new CloseListener();

    /** Keeps on creating a dialog until the user enters "stop". */
    static public void main(String[] args) {
        StringDialog dialog = new StringDialog("Input a string");
        boolean stop = false;
        do {
            dialog.showDialog(null);
            System.out.printf("Selected string: %s%n", dialog.getResult());
            stop = "stop".equals(dialog.getResult());
        } while (!stop);
        System.exit(0);
    }

    /** Maximum number of persistently stored entries. */
    private static final int MAX_PERSISTENT_SIZE = 10;

    /** 
     * Overrides the {@link JComboBox#configureEditor(ComboBoxEditor, Object)}
     * method to avoid confusing the editor. 
     */
    private class MyComboBox extends JComboBox {
        @Override
        public void configureEditor(ComboBoxEditor anEditor, Object anItem) {
            if (anItem != null && this.configure) {
                super.configureEditor(anEditor, anItem);
            }
        }

        public void doConfigure(boolean configure) {
            this.configure = configure;
        }

        private boolean configure;
    }

    private class MyComboBoxModel implements ComboBoxModel {
        @Override
        public Object getSelectedItem() {
            return this.selectedItem;
        }

        @Override
        public void setSelectedItem(Object anItem) {
            this.selectedItem = anItem;
            // also set this item in the editor, however without changing the 
            // data model
            if (anItem != null) {
                this.ignoreChange = true;
                getEditor().setText(anItem.toString());
                getEditor().selectAll();
                this.ignoreChange = false;
            }
        }

        @Override
        public void addListDataListener(ListDataListener l) {
            this.listeners.add(l);
        }

        @Override
        public Object getElementAt(int index) {
            synchroniseModel();
            return this.contents.get(index);
        }

        @Override
        public int getSize() {
            synchroniseModel();
            return this.contents.size();
        }

        @Override
        public void removeListDataListener(ListDataListener l) {
            this.listeners.remove(l);
        }

        public void setDirty(String filterText) {
            if (!this.ignoreChange) {
                getChoiceBox().doConfigure(false);
                this.dirty = true;
                this.filterText = filterText;
                this.selectedItem = null;
                for (ListDataListener l : this.listeners) {
                    l.contentsChanged(new ListDataEvent(this,
                        ListDataEvent.CONTENTS_CHANGED, 0, getSize()));
                }
                getChoiceBox().hidePopup();
                if (getSize() > 0) {
                    getChoiceBox().showPopup();
                }
                getChoiceBox().doConfigure(true);
            }
        }

        private void synchroniseModel() {
            if (this.dirty) {
                this.dirty = false;
                this.contents.clear();
                for (String entry : StringDialog.this.history) {
                    if (entry.contains(this.filterText)) {
                        this.contents.add(entry);
                    }
                }
            }
        }

        /** 
         * Flag controlling whether the model should really be
         * set to dirty. This enables the changes due to a #setSelectedItem(Object)
         * to be ignored.
         */
        private boolean ignoreChange = false;
        /** Flag indicating if the model should be refreshed from the history. */
        private boolean dirty = true;
        /** Text determining which part of the history should be included in the model. */
        private String filterText;
        /** The actual model. */
        private final List<String> contents = new ArrayList<String>();
        /** The listeners for this model. */
        private final List<ListDataListener> listeners =
            new ArrayList<ListDataListener>();
        /**
         * The currently selected item. Note that there is no connection
         * between this and the model.
         * @see #setSelectedItem(Object)
         * @see #getSelectedItem()
         */
        private Object selectedItem;
    }

    /** The singleton document change listener. */
    private final ChangeListener changeListener = new ChangeListener();

    private class ChangeListener implements DocumentListener {
        @Override
        public void changedUpdate(DocumentEvent e) {
            processTextChange();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            processTextChange();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            processTextChange();
        }
    }

    /**
     * Action listener that closes the dialog and makes sure that the property
     * is set (possibly to null).
     */
    private class CloseListener implements ActionListener, WindowListener {
        public void actionPerformed(ActionEvent e) {
            boolean ok = false;
            if (e.getSource() == getOkButton()
                || e.getSource() instanceof JTextField) {
                ok = setResult(getEditor().getText());
            } else if (e.getSource() == getCancelButton()) {
                ok = setResult(null);
            }
            if (ok) {
                StringDialog.this.dialog.setVisible(false);
            }
        }

        @Override
        public void windowActivated(WindowEvent e) {
            // do nothing
        }

        @Override
        public void windowClosed(WindowEvent e) {
            System.out.printf("Source %s, window %s%n", e.getSource(),
                e.getWindow());
        }

        @Override
        public void windowClosing(WindowEvent e) {
            System.out.printf("Source %s, window %s%n", e.getSource(),
                e.getWindow());
            if (setResult(null)) {
                StringDialog.this.dialog.setVisible(false);
            }
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            // do nothing
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
            // do nothing
        }

        @Override
        public void windowIconified(WindowEvent e) {
            // do nothing
        }

        @Override
        public void windowOpened(WindowEvent e) {
            // do nothing
        }
    }
}
