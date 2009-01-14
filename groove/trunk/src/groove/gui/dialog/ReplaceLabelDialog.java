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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Dialog class that lets the user choose a fresh rule name.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ReplaceLabelDialog {
    /**
     * Constructs a dialog instance, given a set of existing names (that should
     * not be used) as well as a suggested value for the new rule name.
     * @param existingLabels the set of existing labels
     * @param original the label to rename; may be <code>null</code>
     */
    public ReplaceLabelDialog(Set<String> existingLabels, String original) {
        this.existingLabels = existingLabels;
        this.original = original;
    }

    /**
     * Creates a dialog and makes it visible, so that the user can choose the
     * label to rename and its new version.
     * @param frame the frame on which the dialog is shown.
     * @param title the title for the dialog; if <code>null</code>, a default
     *        title is used
     * @return <code>true</code> if the user agreed with the outcome of the
     *         dialog.
     */
    public boolean showDialog(JFrame frame, String title) {
        // set the suggested name in the name field
        JTextField originalField = getOriginalField();
        if (this.original != null) {
            originalField.setText(this.original);
            originalField.setSelectionStart(0);
            originalField.setSelectionEnd(originalField.getText().length());
        }
        getOkButton().setEnabled(isRenamingValid());
        JDialog dialog =
            getOptionPane().createDialog(frame,
                title == null ? DEFAULT_TITLE : title);
        dialog.setVisible(true);
        Object response = getOptionPane().getValue();
        boolean result =
            response == getOkButton() || response == getOriginalField();
        return result;
    }

    /**
     * Lazily creates and returns the option pane that is to form the content of
     * the dialog.
     */
    JOptionPane getOptionPane() {
        if (this.optionPane == null) {
            JLabel originalLabel = new JLabel(FIND_TEXT);
            JLabel replacementLabel = new JLabel(REPLACE_TEXT);
            originalLabel.setPreferredSize(replacementLabel.getPreferredSize());
            JPanel originalPanel = new JPanel(new BorderLayout());
            originalPanel.add(originalLabel, BorderLayout.WEST);
            originalPanel.add(getOriginalField(), BorderLayout.CENTER);
            JPanel replacementPanel = new JPanel(new BorderLayout());
            replacementPanel.add(replacementLabel, BorderLayout.WEST);
            replacementPanel.add(getReplacementField(), BorderLayout.CENTER);
            this.optionPane =
                new JOptionPane(new Object[] {originalPanel, replacementPanel},
                    JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION,
                    null, new Object[] {getOkButton(), getCancelButton()});
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
    private JTextField getOriginalField() {
        if (this.originalField == null) {
            this.originalField = new JTextField();
            this.originalField.getDocument().addDocumentListener(
                new OverlapListener());
            this.originalField.addActionListener(new CloseListener());
        }
        return this.originalField;
    }

    /** Returns the text field in which the user is to enter his input. */
    private JTextField getReplacementField() {
        if (this.replaceField == null) {
            this.replaceField = new JTextField();
            this.replaceField.getDocument().addDocumentListener(
                new OverlapListener());
            this.replaceField.addActionListener(new CloseListener());
        }
        return this.replaceField;
    }

    /** Returns the label to be renamed. */
    public String getOriginal() {
        return getOriginalField().getText();
    }

    /** Returns the renamed label. */
    public String getReplacement() {
        return getReplacementField().getText();
    }

    /**
     * Tests if the renaming is valid. This is the case if the renamed label is
     * not empty and does not equal the original.
     */
    boolean isRenamingValid() {
        String original = getOriginal();
        String replacement = getReplacement();
        return original.length() > 0 && replacement.length() > 0
            && !original.equals(replacement);
    }

    /** The option pane that is the core of the dialog. */
    private JOptionPane optionPane;

    /** The OK button in the dialog. */
    private JButton okButton;

    /** The Cancel button in the dialog. */
    private JButton cancelButton;

    /** The text field where the original label is entered. */
    private JTextField originalField;

    /** The text field where the renamed label is entered. */
    private JTextField replaceField;

    /** Set of existing rule names. */
    private final Set<String> existingLabels;

    /** Suggested name. */
    private final String original;

    /** Default dialog title. */
    static private String DEFAULT_TITLE = "Replace label";
    /** Text of find label on dialog. */
    static private String FIND_TEXT = "Find:";
    /** Text of replace label on dialog */
    static private String REPLACE_TEXT = "Replace with: ";

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
            if (e.getSource() == getCancelButton() || isRenamingValid()) {
                getOptionPane().setValue(e.getSource());
                getOptionPane().setVisible(false);
            }
        }
    }

    /**
     * Document listener that enables or disables the OK button, depending on
     * whether {@link #isRenamingValid()} returns <code>true</code>.
     */
    private class OverlapListener implements DocumentListener {
        /**
         * Empty constructor with the right visibility.
         */
        OverlapListener() {
            // empty
        }

        public void changedUpdate(DocumentEvent e) {
            testRenaming();
        }

        public void insertUpdate(DocumentEvent e) {
            testRenaming();
        }

        public void removeUpdate(DocumentEvent e) {
            testRenaming();
        }

        /**
         * Tests if the content of the name field is a good choice of rule name.
         * The OK button is enabled or disabled as a consequence of this.
         */
        private void testRenaming() {
            getOkButton().setEnabled(isRenamingValid());
        }
    }
}
