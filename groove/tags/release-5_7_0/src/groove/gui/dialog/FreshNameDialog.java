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
 * $Id$
 */
package groove.gui.dialog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.eclipse.jdt.annotation.Nullable;

import groove.util.parse.FormatException;

/**
 * Dialog class that lets the user choose a fresh name.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class FreshNameDialog<Name> {
    /**
     * Constructs a dialog instance, given a set of existing names (that should
     * not be used) as well as a suggested value for the new name.
     * @param existingNames the set of already existing names
     * @param suggestion the suggested name to start with
     * @param mustBeFresh flag indicating that the name to be chosen should not
     *        be among the existing names
     */
    public FreshNameDialog(Set<Name> existingNames, String suggestion, boolean mustBeFresh) {
        this.existingNames = existingNames.stream()
            .map(n -> n.toString())
            .collect(Collectors.toSet());
        this.existingLowerCaseNames = this.existingNames.stream()
            .map(n -> n.toLowerCase())
            .collect(Collectors.toSet());
        this.suggestion = mustBeFresh ? generateNewName(suggestion) : suggestion;
    }

    /**
     * Generates a fresh name by extending a given name so that it does not
     * occur in a set of existing names.
     * @param basis the name to be extended (non-null)
     * @return An extension of <code>basis</code> that is not in
     *         <code>existingNames</code>
     */
    private String generateNewName(String basis) {
        String result = basis;
        for (int i = 1; this.existingNames.contains(result); i++) {
            result = basis + i;
        }
        return result;
    }

    /**
     * Callback method to create an object of the generic name type from a
     * string.
     */
    abstract protected Name createName(String name) throws FormatException;

    /**
     * Creates a dialog and makes it visible, so that the user can choose a file
     * name. The return value indicates if a valid new rule name was input.
     * @param frame the frame on which the dialog is shown.
     * @param title the title for the dialog; if <code>null</code>, a default
     *        title is used
     * @return <code>true</code> if the user agreed with the outcome of the
     *         dialog.
     */
    public boolean showDialog(JFrame frame, String title) {
        // set the suggested name in the name field
        JTextField nameField = getNameField();
        nameField.setText(this.suggestion.toString());
        nameField.setSelectionStart(0);
        nameField.setSelectionEnd(nameField.getText()
            .length());
        setOkEnabled();
        JDialog dialog = getOptionPane().createDialog(frame, title == null ? DEFAULT_TITLE : title);
        dialog.setVisible(true);
        Object response = getOptionPane().getValue();
        boolean result = response == getOkButton() || response == getNameField();
        return result;
    }

    /**
     * Enables or disables the OK button, depending in the validity of the name
     * field.
     */
    private void setOkEnabled() {
        boolean enabled = true;
        String errorText = " ";
        String name = getChosenName();
        if (this.existingNames.contains(name)) {
            if (!this.suggestion.equals(name)) {
                errorText = "Name already exists";
                enabled = false;
            }
        } else if (this.existingLowerCaseNames.contains(name.toLowerCase())) {
            errorText = "Name already exists (with different case)";
            enabled = false;
        } else {
            try {
                setName(createName(name));
            } catch (FormatException exc) {
                errorText = exc.getMessage();
                enabled = false;
            }
        }
        if (!enabled) {
            setName(null);
        }
        getErrorLabel().setText(errorText);
        getOkButton().setEnabled(enabled);
        getNameFieldListener().setEnabled(enabled);
    }

    /**
     * Lazily creates and returns the option pane that is to form the content of
     * the dialog.
     */
    private JOptionPane getOptionPane() {
        if (this.optionPane == null) {
            JTextField nameField = getNameField();
            this.optionPane = new JOptionPane(new Object[] {nameField, getErrorLabel()},
                JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null,
                new Object[] {getOkButton(), getCancelButton()});
        }
        return this.optionPane;
    }

    /** Returns the rule name currently filled in in the name field. */
    private String getChosenName() {
        return getNameField().getText();
    }

    /** The option pane that is the core of the dialog. */
    private JOptionPane optionPane;

    /**
     * Returns the OK button on the dialog.
     */
    private JButton getOkButton() {
        if (this.okButton == null) {
            this.okButton = new JButton("OK");
            this.okButton.addActionListener(new CloseListener());
        }
        return this.okButton;
    }

    /** The OK button in the dialog. */
    private JButton okButton;

    /**
     * Returns the OK button on the dialog.
     */
    private JButton getCancelButton() {
        if (this.cancelButton == null) {
            this.cancelButton = new JButton("Cancel");
            this.cancelButton.addActionListener(new CloseListener());
        }
        return this.cancelButton;
    }

    /** The Cancel button in the dialog. */
    private JButton cancelButton;

    /** Returns the text field in which the user is to enter his input. */
    private JTextField getNameField() {
        if (this.nameField == null) {
            this.nameField = new JTextField(30);
            this.nameField.getDocument()
                .addDocumentListener(new OverlapListener());
            this.nameField.addActionListener(getNameFieldListener());
        }
        return this.nameField;
    }

    /** Returns the close listener for the name field. */
    private CloseListener getNameFieldListener() {
        if (this.nameFieldListener == null) {
            this.nameFieldListener = new CloseListener();
        }
        return this.nameFieldListener;
    }

    /** The text field where the rule name is entered. */
    private JTextField nameField;

    /** Close listener for the name field. */
    private CloseListener nameFieldListener;

    private JLabel getErrorLabel() {
        if (this.errorLabel == null) {
            JLabel result = this.errorLabel = new JLabel();
            result.setForeground(Color.RED);
            result.setMinimumSize(getOkButton().getPreferredSize());
        }
        return this.errorLabel;
    }

    /** Label displaying the current error in the renaming (if any). */
    private JLabel errorLabel;

    /** Set of existing names. */
    private final Set<String> existingNames;
    /** Set of existing names, in lower case. */
    private final Set<String> existingLowerCaseNames;
    /** Suggested name. */
    private final String suggestion;

    /**
     * Returns the name chosen by the user in the course of the dialog. The
     * return value is guaranteed to be distinct from any of the existing names
     * entered at construction time.
     */
    public final @Nullable Name getName() {
        return this.name;
    }

    /**
     * Sets the value of the chosen name field.
     */
    private final void setName(@Nullable Name name) {
        this.name = name;
    }

    /** The rule name selected by the user. */
    private @Nullable Name name;

    /**
     * Action listener that closes the dialog and sets the option pane's value
     * to the source of the event, provided the source of the event is the
     * cancel button, or the value of the text field is a valid rule name.
     */
    private class CloseListener implements ActionListener {
        /** Empty constructor with the right visibility. */
        CloseListener() {
            this.enabled = true;
        }

        /** Enables or disables the close listened. */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (this.enabled) {
                getOptionPane().setValue(e.getSource());
                getOptionPane().setVisible(false);
            }
        }

        private boolean enabled;
    }

    /**
     * Document listener that enables or disables the OK button, by calling
     * {@link #setOkEnabled()}
     */
    private class OverlapListener implements DocumentListener {
        /**
         * Empty constructor with the right visibility.
         */
        OverlapListener() {
            // empty
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            setOkEnabled();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            setOkEnabled();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            setOkEnabled();
        }
    }

    /** Default dialog title. */

    static private String DEFAULT_TITLE = "Select rule name";
}
