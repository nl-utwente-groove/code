/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import nl.utwente.groove.io.external.format.ecore.EcoreOptions;
import nl.utwente.groove.io.external.format.ecore.EcoreOptions.Ordering;

/**
 * Dialog class that lets the user choose the encoding options of the Ecore
 * porter. This is the successor of the never-completed 2012 {@code ConfigDialog}:
 * the options are few because the encoding has one canonical form.
 * @author Arend Rensink
 * @version $Revision$
 */
public class EcoreOptionsDialog {
    /** Constructs a dialog instance, initialised with given options. */
    public EcoreOptionsDialog(EcoreOptions options) {
        this.options = options;
    }

    /**
     * Creates a dialog and makes it visible, so that the user can choose the
     * encoding options. If the return value is {@code true}, the chosen options
     * can be retrieved through {@link #getOptions()}.
     * @param frame the frame on which the dialog is shown
     * @param title the title for the dialog; if {@code null}, a default title is used
     * @return {@code true} if the user confirmed the dialog
     */
    public boolean showDialog(JFrame frame, String title) {
        getNoneButton().setSelected(this.options.ordering() == Ordering.NONE);
        getIndexButton().setSelected(this.options.ordering() == Ordering.INDEX);
        getIdentifierBox().setSelected(this.options.useIdentifiers());
        JDialog dialog = getOptionPane().createDialog(frame, title == null
            ? DEFAULT_TITLE
            : title);
        dialog.setVisible(true);
        boolean result = getOptionPane().getValue() == getOkButton();
        if (result) {
            this.options = new EcoreOptions(getIndexButton().isSelected()
                ? Ordering.INDEX
                : Ordering.NONE, getIdentifierBox().isSelected());
        }
        return result;
    }

    /** Returns the options as chosen in the course of the dialog. */
    public EcoreOptions getOptions() {
        return this.options;
    }

    /** The options displayed by, and modified in, this dialog. */
    private EcoreOptions options;

    /**
     * Lazily creates and returns the option pane that is to form the content of
     * the dialog.
     */
    private JOptionPane getOptionPane() {
        if (this.optionPane == null) {
            this.optionPane = new JOptionPane(
                new Object[] {new JLabel("Order of multi-valued features:"), getNoneButton(),
                    getIndexButton(), getIdentifierBox()},
                JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null,
                new Object[] {getOkButton(), getCancelButton()});
        }
        return this.optionPane;
    }

    /** The option pane that is the core of the dialog. */
    private JOptionPane optionPane;

    /** Returns the radio button for the {@link Ordering#NONE} option. */
    private JRadioButton getNoneButton() {
        if (this.noneButton == null) {
            this.noneButton = new JRadioButton("Not represented (values become plain edges)");
            getOrderingGroup().add(this.noneButton);
        }
        return this.noneButton;
    }

    /** The radio button for the {@link Ordering#NONE} option. */
    private JRadioButton noneButton;

    /** Returns the radio button for the {@link Ordering#INDEX} option. */
    private JRadioButton getIndexButton() {
        if (this.indexButton == null) {
            this.indexButton
                = new JRadioButton("Indexed (values become nodes with an index attribute)");
            getOrderingGroup().add(this.indexButton);
        }
        return this.indexButton;
    }

    /** The radio button for the {@link Ordering#INDEX} option. */
    private JRadioButton indexButton;

    /** Returns the button group of the ordering radio buttons. */
    private ButtonGroup getOrderingGroup() {
        if (this.orderingGroup == null) {
            this.orderingGroup = new ButtonGroup();
        }
        return this.orderingGroup;
    }

    /** The button group of the ordering radio buttons. */
    private ButtonGroup orderingGroup;

    /** Returns the check box for the identifier option. */
    private JCheckBox getIdentifierBox() {
        if (this.identifierBox == null) {
            this.identifierBox = new JCheckBox("Use XMI identifiers as node identities");
        }
        return this.identifierBox;
    }

    /** The check box for the identifier option. */
    private JCheckBox identifierBox;

    /** Returns the OK button on the dialog. */
    private JButton getOkButton() {
        if (this.okButton == null) {
            this.okButton = new JButton("OK");
            this.okButton.addActionListener(new CloseListener());
        }
        return this.okButton;
    }

    /** The OK button in the dialog. */
    private JButton okButton;

    /** Returns the Cancel button on the dialog. */
    private JButton getCancelButton() {
        if (this.cancelButton == null) {
            this.cancelButton = new JButton("Cancel");
            this.cancelButton.addActionListener(new CloseListener());
        }
        return this.cancelButton;
    }

    /** The Cancel button in the dialog. */
    private JButton cancelButton;

    /** Action listener that closes the dialog, recording the button pressed. */
    private class CloseListener implements ActionListener {
        /** Empty constructor with the right visibility. */
        CloseListener() {
            // empty by design
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            getOptionPane().setValue(e.getSource());
            getOptionPane().setVisible(false);
        }
    }

    /** The default title of the dialog. */
    private static final String DEFAULT_TITLE = "Ecore encoding options";
}
