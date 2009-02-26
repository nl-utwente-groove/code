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

import groove.gui.Simulator;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Tom Staijen
 * @version $Revision $
 */
public class ExportDialog {

    /** The current Grammar Directory*/
    private String currentDirectory;
    
    /** The formula label */
    private JLabel formulaLabel;
    /** The formula field */
    JTextField formulaField;
    /** The history box */
    JComboBox historyBox;
    /** The OK button on the option pane. */
    private JButton okButton;
    /** The CANCEL button on the option pane. */
    private JButton cancelButton;
    /** Title of the dialog. */
    public static final String DIALOG_TITLE = "Enter Temporal Formula";

    /** The option pane creating the dialog. */
    private JOptionPane pane;
    /** The dialog */
    private JDialog dialog;
    /** The simulator */
    private Simulator simulator;

    public ExportDialog(Simulator simulator) {
        this.simulator = simulator;
    }

    public void setCurrentDirectory(String value) {
        this.currentDirectory = value;
    }
    
    public boolean showDialog(Component frame) {
        boolean result;
        boolean stopDialog;
        do {
            getContentPane().setValue(null);
            getContentPane().setVisible(true);
            JDialog dialog =
                getContentPane().createDialog(frame, createTitle());
            dialog.setResizable(true);
            dialog.setVisible(true);
            dialog.dispose();
            Object selectedValue = getContentPane().getValue();
            if (selectedValue == getOkButton()) {
                result = stopDialog = true;
            } else {
                result = false;
                stopDialog = true;
            }
        } while (!stopDialog);
        return result;
    }

    /**
     * @return the contentpane
     */
    JOptionPane getContentPane() {
        Object[] buttons = new Object[] {getOkButton(), getCancelButton()};
        this.pane =
            new JOptionPane(createPanel(), JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION, null, buttons);
        this.pane.setSize(200, 300);
        return this.pane;
    }

    /**
     * Create and return the main panel.
     * @return the main panel.
     */
    private JPanel createPanel() {
        JPanel result = new JPanel();
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        JPanel buttons = new JPanel();

        // editing formula
        
        panel.add(new JLabel("Exporting directory:"));
        
        JTextField dirField = new JTextField(this.currentDirectory);
        panel.add(dirField);
        
        panel.add(new JLabel("LTS Exporting options:"));

        JCheckBox startCheck = new JCheckBox("Label start state");
        JCheckBox finalCheck = new JCheckBox("Label final states");
        JCheckBox openCheck = new JCheckBox("Label open states");
        JCheckBox nameCheck = new JCheckBox("Export state names");
        
        panel.add(startCheck);
        panel.add(finalCheck);
        panel.add(openCheck);
        panel.add(nameCheck);
        
        panel.add(new JLabel("Export States"));
        JOptionPane list = new JOptionPane(new String[]{"None","All states","Final states"});
        
        panel.add(list);
        
        // OK or CANCEL
        this.okButton = getOkButton();
        this.cancelButton = getCancelButton();
        buttons.add(this.okButton);
        buttons.add(this.cancelButton);

        result.add(panel);
        result.add(buttons);

        return result;
    }

    /**
     * @return the title of the dialog
     */
    private String createTitle() {
        return DIALOG_TITLE;
    }

    /**
     * Lazily creates and returns a button labelled OK.
     * @return the ok button
     */
    JButton getOkButton() {
        if (this.okButton == null) {
            this.okButton = new JButton("OK");
            this.okButton.addActionListener(new CloseListener());
        }
        return this.okButton;
    }

    /**
     * Lazily creates and returns a button labelled CANCEL.
     * @return the cancel button
     */
    JButton getCancelButton() {
        if (this.cancelButton == null) {
            this.cancelButton = new JButton("Cancel");
            this.cancelButton.addActionListener(new CloseListener());
        }
        return this.cancelButton;
    }

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
            if (e.getSource() == getOkButton()) {
                // 
            } else if (e.getSource() == getCancelButton()) {
                //
            }

            getContentPane().setVisible(false);
            ExportDialog.this.dialog.dispose();
        }
    }
}
