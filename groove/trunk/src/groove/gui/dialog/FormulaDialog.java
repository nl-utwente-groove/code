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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Dialog for entering temporal formulae.
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class FormulaDialog {

    /**
     * Constructs an instance of the dialog for a given parent frame, a given
     * (non-<code>null</code>) set of properties, and a flag indicating if the
     * properties should be editable. A further parameter gives a list of
     * default keys that treated specially: they are added by default during
     * editing, and are ordered first in the list.
     */
    public FormulaDialog() {
        this.history = new ArrayList<String>();
        this.history.add("Select a previous formula.");
    }

    /**
     * Makes the dialog visible and awaits the user's response. Since the dialog
     * is modal, this method returns only when the user closes the dialog. The
     * return value indicates if the properties have changed.
     * @param frame the frame on which the fialog is to be displayed
     */
    public void showDialog(Component frame) {
        this.dialog = getContentPane().createDialog(frame, createTitle());
        this.dialog.pack();
        this.dialog.setVisible(true);
    }

    /**
     * @return the title of the dialog
     */
    private String createTitle() {
        return DIALOG_TITLE;
    }

    /**
     * @return the contentpane
     */
    private JOptionPane getContentPane() {
        Object[] buttons = new Object[] {getOkButton(), getCancelButton()};
        this.pane =
            new JOptionPane(createPanel(), JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION, null, buttons);
        return this.pane;
    }

    /**
     * Create and return the main panel.
     * @return the main panel.
     */
    private JPanel createPanel() {
        JPanel result = new JPanel();
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        JPanel history = new JPanel();
        JPanel buttons = new JPanel();

        // editing formula
        this.formulaLabel = new JLabel("Formula:");
        this.formulaField = new JTextField(30);
        this.formulaField.addActionListener(new CloseListener());
        panel.add(this.formulaLabel);
        panel.add(this.formulaField);

        // select formula from list
        this.historyBox = new JComboBox(this.history.toArray());
        this.historyBox.addActionListener(new SelectionListener());
        history.add(this.historyBox);

        // OK or CANCEL
        this.okButton = getOkButton();
        this.cancelButton = getCancelButton();
        buttons.add(this.okButton);
        buttons.add(this.cancelButton);

        result.add(panel);
        result.add(history, BorderLayout.AFTER_LAST_LINE);
        result.add(buttons);

        return result;
    }

    /**
     * Add an item to the history.
     * @param item the item to be added
     */
    private void addItem(String item) {
        if (!this.history.contains(item)) {
            this.history.add(1, item);
        }
    }

    /** The option pane creating the dialog. */
    private JOptionPane pane;

    /** The formula label */
    private JLabel formulaLabel;
    /** The formula field */
    private JTextField formulaField;
    /** The history box */
    private JComboBox historyBox;

    /**
     * Lazily creates and returns a button labelled OK.
     * @return the ok button
     */
    private JButton getOkButton() {
        if (this.okButton == null) {
            this.okButton = new JButton("OK");
            this.okButton.addActionListener(new CloseListener());
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
            this.cancelButton.addActionListener(new CloseListener());
        }
        return this.cancelButton;
    }

    /** The CANCEL button on the option pane. */
    private JButton cancelButton;

    /** The history list */
    private final List<String> history;

    /**
     * Return the property that is entered for verification.
     * @return the property in String format
     */
    public String getProperty() {
        return this.property;
    }

    /** The field in which to store the provided data */
    private String property;

    /** The dialog to be shown */
    private JDialog dialog;

    /** Title of the dialog. */
    public static final String DIALOG_TITLE = "Enter Temporal Formula";

    /**
     * Action listener that closes the dialog and makes sure that the property
     * is set (possibly to null).
     */
    private class CloseListener implements ActionListener {
        /**
         * Empty constructor with the correct visibility.
         */
        public CloseListener() {
            // empty
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == getOkButton()
                || e.getSource() == FormulaDialog.this.formulaField
                || e.getSource() == FormulaDialog.this.historyBox) {
                if (!FormulaDialog.this.formulaField.getText().equals("")) {
                    FormulaDialog.this.property =
                        FormulaDialog.this.formulaField.getText();
                    addItem(FormulaDialog.this.property);
                } else {
                    if (FormulaDialog.this.historyBox.getSelectedIndex() > 0) {
                        FormulaDialog.this.property =
                            FormulaDialog.this.historyBox.getSelectedItem().toString();
                    } else {
                        JOptionPane.showMessageDialog(
                            FormulaDialog.this.dialog,
                            "No formula was selected.", "Formula selection",
                            JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
            } else if (e.getSource() == getCancelButton()) {
                FormulaDialog.this.property = null;
            }

            getContentPane().setVisible(false);
            FormulaDialog.this.dialog.dispose();
        }
    }

    /**
     * Action listener that closes the dialog and makes sure that the property
     * is set (possibly to null).
     */
    private class SelectionListener implements ActionListener {
        /**
         * Empty constructor with the correct visibility.
         */
        public SelectionListener() {
            // empty
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == FormulaDialog.this.historyBox) {
                if (FormulaDialog.this.historyBox.getSelectedIndex() > 0) {
                    FormulaDialog.this.formulaField.setText(FormulaDialog.this.historyBox.getSelectedItem().toString());
                }
            }
        }
    }
}
