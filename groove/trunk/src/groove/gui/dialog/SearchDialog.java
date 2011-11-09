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

import groove.graph.TypeGraph;
import groove.graph.TypeLabel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Dialog class that lets the user select a graph label for a search.
 * @author Eduardo Zambon
 */
public class SearchDialog {
    /**
     * Constructs a dialog instance, given a set of existing names.
     * @param typeGraph the type graph containing all labels and sublabels
     */
    public SearchDialog(TypeGraph typeGraph) {
        this.typeGraph = typeGraph;
    }

    /**
     * Creates a dialog and makes it visible, so that the user can choose the
     * label to search.
     * @param frame the frame on which the dialog is shown.
     * @param title the title for the dialog; if <code>null</code>, a default
     *        title is used
     * @return <code>true</code> if the user agreed with the outcome of the
     *         dialog.
     */
    public boolean showDialog(JFrame frame, String title) {
        getOkButton().setEnabled(true);
        JDialog dialog =
            getOptionPane().createDialog(frame,
                title == null ? DEFAULT_TITLE : title);
        dialog.setVisible(true);
        Object response = getOptionPane().getValue();
        boolean result = response == getOkButton();
        return result;
    }

    /** Returns the label to be renamed. */
    public TypeLabel getSearchLabel() {
        return (TypeLabel) getLabelComboBox().getSelectedItem();
    }

    /**
     * Lazily creates and returns the option pane that is to form the content of
     * the dialog.
     */
    private JOptionPane getOptionPane() {
        if (this.optionPane == null) {
            JLabel jLabel = new JLabel(TEXT);
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(jLabel, BorderLayout.NORTH);
            panel.add(getLabelComboBox(), BorderLayout.SOUTH);
            this.optionPane =
                new JOptionPane(new Object[] {panel},
                    JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION,
                    null, new Object[] {getOkButton(), getCancelButton()});
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
    private JComboBox getLabelComboBox() {
        if (this.labelComboBox == null) {
            this.labelComboBox =
                LabelDialogFactory.getLabelComboBox(this.typeGraph);
        }
        return this.labelComboBox;
    }

    /** The text field where the original label is entered. */
    private JComboBox labelComboBox;

    /** Set of existing rule names. */
    private final TypeGraph typeGraph;
    /** Default dialog title. */
    static private String DEFAULT_TITLE = "Search";
    /** Text of find label on dialog. */
    static private String TEXT = "Label to search: ";

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
            getOptionPane().setValue(e.getSource());
            getOptionPane().setVisible(false);
        }
    }

}
