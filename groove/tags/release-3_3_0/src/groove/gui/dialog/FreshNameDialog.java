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
 * $Id: RuleNameDialog.java,v 1.6 2008-01-30 09:33:36 iovka Exp $
 */
package groove.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
    public FreshNameDialog(Set<Name> existingNames, String suggestion,
            boolean mustBeFresh) {
        this.existingNames = new HashSet<Name>(existingNames);
        this.suggestion =
            mustBeFresh ? generateNewName(suggestion, existingNames)
                    : createName(suggestion);
    }

    /**
     * Generates a fresh name by extending a given name so that it does not
     * occur in a set of existing names.
     * @param basis the name to be extended (non-null)
     * @param existingNames the set of names that are forbidden (non-null)
     * @return An extension of <code>basis</code> that is not in
     *         <code>existingNames</code>
     */
    Name generateNewName(String basis, Set<Name> existingNames) {
        Name result = createName(basis);
        for (int i = 1; existingNames.contains(result); i++) {
            result = createName(basis + i);
        }
        return result;
    }

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
        nameField.setSelectionEnd(nameField.getText().length());
        getOkButton().setEnabled(isNameFieldValid());
        JDialog dialog =
            getOptionPane().createDialog(frame,
                title == null ? DEFAULT_TITLE : title);
        dialog.setVisible(true);
        Object response = getOptionPane().getValue();
        boolean result =
            response == getOkButton() || response == getNameField();
        setName(result ? getChosenName() : null);
        return result;
    }

    /**
     * Lazily creates and returns the option pane that is to form the content of
     * the dialog.
     */
    JOptionPane getOptionPane() {
        if (this.optionPane == null) {
            JTextField nameField = getNameField();
            this.optionPane =
                new JOptionPane(nameField, JOptionPane.PLAIN_MESSAGE,
                    JOptionPane.OK_CANCEL_OPTION, null, new Object[] {
                        getOkButton(), getCancelButton()});
        }
        return this.optionPane;
    }

    /**
     * Returns the OK button on the dialog.
     */
    JButton getOkButton() {
        if (this.okButton == null) {
            this.okButton = new JButton("OK");
            this.okButton.addActionListener(new CloseListener());
        }
        return this.okButton;
    }

    /**
     * Returns the OK button on the dialog.
     */
    JButton getCancelButton() {
        if (this.cancelButton == null) {
            this.cancelButton = new JButton("Cancel");
            this.cancelButton.addActionListener(new CloseListener());
        }
        return this.cancelButton;
    }

    /** Returns the text field in which the user is to enter his input. */
    private JTextField getNameField() {
        if (this.nameField == null) {
            this.nameField = new JTextField();
            this.nameField.getDocument().addDocumentListener(
                new OverlapListener());
            this.nameField.addActionListener(new CloseListener());
        }
        return this.nameField;
    }

    /** Returns the rule name currently filled in in the name field. */
    private Name getChosenName() {
        return createName(getNameField().getText());
    }

    /**
     * Returns the name chosen by the user in the course of the dialog. The
     * return value is guaranteed to be distinct from any of the existing names
     * entered at construction time.
     */
    public final Name getName() {
        return this.name;
    }

    /**
     * Sets the value of the chosen name field.
     */
    private final void setName(Name name) {
        this.name = name;
    }

    /**
     * Tests if {@link #getChosenName()} is a correct value for the new rule
     * name. This is the case if it equals the originally suggested name, or is
     * not in the set of existing names.
     * @return <code>true</code> if {@link #getChosenName()} was found to be a
     *         correct value
     */
    boolean isNameFieldValid() {
        Name label = getChosenName();
        return this.suggestion.equals(label)
            || !this.existingNames.contains(label)
            && label.toString().length() != 0;
    }

    /**
     * Callback method to create an object of the generic name type from a
     * string.
     */
    abstract protected Name createName(String name);

    /** The option pane that is the core of the dialog. */
    private JOptionPane optionPane;

    /** The OK button in the dialog. */
    private JButton okButton;

    /** The Cancel button in the dialog. */
    private JButton cancelButton;

    /** The text field where the rule name is entered. */
    private JTextField nameField;

    /** Set of existing rule names. */
    private final Set<Name> existingNames;

    /** Suggested name. */
    private final Name suggestion;

    /** The rule name selected by the user. */
    private Name name;

    /**
     * Action listener that closes the dialog and sets the option pane's value
     * to the source of the event, provided the source of the event is the
     * cancel button, or the value of the text field is a valid rule name.
     */
    private class CloseListener implements ActionListener {
        /** Empty constructor with the right visibility. */
        CloseListener() {
            // empty
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == getCancelButton() || isNameFieldValid()) {
                getOptionPane().setValue(e.getSource());
                getOptionPane().setVisible(false);
            }
        }
    }

    /**
     * Document listener that enables or disables the OK button, depending on
     * whether {@link #isNameFieldValid()} returns <code>true</code>.
     */
    private class OverlapListener implements DocumentListener {
        /**
         * Empty constructor with the right visibility.
         */
        OverlapListener() {
            // empty
        }

        public void changedUpdate(DocumentEvent e) {
            testNameField();
        }

        public void insertUpdate(DocumentEvent e) {
            testNameField();
        }

        public void removeUpdate(DocumentEvent e) {
            testNameField();
        }

        /**
         * Tests if the content of the name field is a good choice of rule name.
         * The OK button is enabled or disabled as a consequence of this.
         */
        private void testNameField() {
            getOkButton().setEnabled(isNameFieldValid());
        }
    }

    /** Default dialog title. */
    
    static private String DEFAULT_TITLE = "Select rule name";
}
