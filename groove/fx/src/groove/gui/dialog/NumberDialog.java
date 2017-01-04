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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * Dialog class that lets the user choose a fresh name.
 * @author Arend Rensink
 * @version $Revision $
 */
public class NumberDialog {
    /** Creates an instance of the dialog, with a given string prompt. */
    public NumberDialog(String prompt) {
        this.prompt = prompt;
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
    public boolean showDialog(JFrame frame, String title, int initial) {
        getNumberField().setValue(initial);
        JDialog dialog = getOptionPane().createDialog(frame, title == null ? DEFAULT_TITLE : title);
        dialog.setVisible(true);
        Object response = getOptionPane().getValue();
        return response == getOkButton();
    }

    /** Returns the number entered in the dialog. */
    public int getResult() {
        return ((Number) getNumberField().getValue()).intValue();
    }

    /**
     * Lazily creates and returns the option pane that is to form the content of
     * the dialog.
     */
    private JOptionPane getOptionPane() {
        if (this.optionPane == null) {
            this.optionPane =
                new JOptionPane(getNumberPanel(), JOptionPane.PLAIN_MESSAGE,
                    JOptionPane.OK_CANCEL_OPTION, null, new Object[] {getOkButton(),
                        getCancelButton()});
        }
        return this.optionPane;
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
    private JPanel getNumberPanel() {
        if (this.numberPanel == null) {
            this.numberPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            this.numberPanel.add(new JLabel(this.prompt));
            this.numberPanel.add(getNumberField());
        }
        return this.numberPanel;
    }

    /** The text field where the rule name is entered. */
    private JPanel numberPanel;

    /** Returns the text field in which the user is to enter his input. */
    private JSpinner getNumberField() {
        if (this.numberField == null) {
            this.numberField = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
            this.numberField.setPreferredSize(new Dimension(50,
                this.numberField.getPreferredSize().height));
        }
        return this.numberField;
    }

    /** The text field where the rule name is entered. */
    private JSpinner numberField;

    /** The string prompt of the dialog. */
    private final String prompt;

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

        @Override
        public void actionPerformed(ActionEvent e) {
            getOptionPane().setValue(e.getSource());
            getOptionPane().setVisible(false);
        }
    }

    /** Default dialog title. */

    static private String DEFAULT_TITLE = "Select rule name";
}
