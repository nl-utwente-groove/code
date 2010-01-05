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

import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.util.Converter;
import groove.view.FormatException;
import groove.view.aspect.TypeAspect;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Dialog class that lets the user choose a replacement for a graph label.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RelabelDialog {
    /**
     * Constructs a dialog instance, given a set of existing names (that should
     * not be used) as well as a suggested value for the new rule name.
     * @param existingLabels the set of existing labels (non-empty)
     * @param oldLabel the label to rename; may be <code>null</code>
     */
    public RelabelDialog(Set<Label> existingLabels, Label oldLabel) {
        this.existingLabels = existingLabels;
        this.suggestedLabel = oldLabel;
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
        if (this.suggestedLabel != null) {
            getOldField().setSelectedItem(this.suggestedLabel);
            propagateSelection();
        }
        setOkEnabled();
        JDialog dialog =
            getOptionPane().createDialog(frame,
                title == null ? DEFAULT_TITLE : title);
        dialog.setVisible(true);
        Object response = getOptionPane().getValue();
        boolean result = response == getOkButton() || response == getOldField();
        return result;
    }

    /**
     * Propagates the selection in the old field to all other GUI elements.
     */
    private void propagateSelection() {
        Label selection = (Label) getOldField().getSelectedItem();
        getOldTypeCheckbox().setSelected(selection.isNodeType());
        getNewTypeCheckbox().setSelected(selection.isNodeType());
        getNewField().setText(selection.text());
        getNewField().setSelectionStart(0);
        getNewField().setSelectionEnd(selection.text().length());
        getNewField().requestFocus();
    }

    /** Returns the label to be renamed. */
    public Label getOldLabel() {
        return (Label) getOldField().getSelectedItem();
    }

    /** Returns the renamed label. */
    public Label getNewLabel() {
        Label result;
        try {
            result = getNewLabelWithErrors();
        } catch (FormatException exc) {
            result = null;
        }
        return result;
    }

    /**
     * Returns the renamed label, or throws an exception if the renamed label is
     * not OK.
     */
    private Label getNewLabelWithErrors() throws FormatException {
        Label result = null;
        String text = getNewField().getText();
        if (text.length() > 0) {
            boolean isNodeType = getNewTypeCheckbox().isSelected();
            result = DefaultLabel.createLabel(text, isNodeType);
            if (isNodeType) {
                TypeAspect.NODE_TYPE.getLabelParser().parse(result);
            }
        } else {
            throw new FormatException("Empty replacement label not allowed");
        }
        return result;
    }

    /**
     * Enables or disables the OK-button, depending on the validity of the
     * renaming. Displays the error in {@link #getErrorLabel()} if the renaming
     * is not valid.
     */
    private void setOkEnabled() {
        boolean enabled;
        try {
            Label newLabel = getNewLabelWithErrors();
            enabled = !this.existingLabels.contains(newLabel);
            if (enabled) {
                getErrorLabel().setText(" ");
            } else {
                getErrorLabel().setText(
                    String.format("%s '%s' already exists",
                        newLabel.isNodeType() ? "Node type label" : "Label",
                        newLabel));
            }
        } catch (FormatException exc) {
            getErrorLabel().setText(exc.getMessage());
            enabled = false;
        }
        getOkButton().setEnabled(enabled);
    }

    /**
     * Lazily creates and returns the option pane that is to form the content of
     * the dialog.
     */
    private JOptionPane getOptionPane() {
        if (this.optionPane == null) {
            JLabel oldLabel = new JLabel(OLD_TEXT);
            JLabel newLabel = new JLabel(NEW_TEXT);
            oldLabel.setPreferredSize(newLabel.getPreferredSize());
            JPanel oldPanel = new JPanel(new BorderLayout());
            oldPanel.add(oldLabel, BorderLayout.WEST);
            oldPanel.add(getOldField(), BorderLayout.CENTER);
            oldPanel.add(getOldTypeCheckbox(), BorderLayout.EAST);
            JPanel newPanel = new JPanel(new BorderLayout());
            newPanel.add(newLabel, BorderLayout.WEST);
            newPanel.add(getNewField(), BorderLayout.CENTER);
            newPanel.add(getNewTypeCheckbox(), BorderLayout.EAST);
            JPanel errorPanel = new JPanel(new BorderLayout());
            errorPanel.add(getErrorLabel());
            errorPanel.setPreferredSize(oldPanel.getPreferredSize());
            this.optionPane =
                new JOptionPane(new Object[] {oldPanel, newPanel, errorPanel},
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
    private JComboBox getOldField() {
        if (this.oldField == null) {
            final JComboBox result = this.oldField = new JComboBox();
            result.setFocusable(false);
            result.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList list,
                        Object value, int index, boolean isSelected,
                        boolean cellHasFocus) {
                    if (value instanceof Label) {
                        value =
                            Converter.HTML_TAG.on(DefaultLabel.toHtmlString((Label) value));
                    }
                    return super.getListCellRendererComponent(list, value,
                        index, isSelected, cellHasFocus);
                }
            });
            result.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    propagateSelection();
                }
            });
            for (Label label : this.existingLabels) {
                result.addItem(label);
            }
        }
        return this.oldField;
    }

    /** The text field where the original label is entered. */
    private JComboBox oldField;

    /** Returns the text field in which the user is to enter his input. */
    private JTextField getNewField() {
        if (this.newField == null) {
            this.newField = new JTextField();
            this.newField.getDocument().addDocumentListener(
                new OverlapListener());
            this.newField.addActionListener(new CloseListener());
        }
        return this.newField;
    }

    /** The text field where the renamed label is entered. */
    private JTextField newField;

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

    private JCheckBox getOldTypeCheckbox() {
        if (this.oldTypeCheckbox == null) {
            final JCheckBox result =
                this.oldTypeCheckbox = new JCheckBox(NODE_TYPE_TEXT);
            result.setHorizontalTextPosition(SwingConstants.LEADING);
            result.setFocusable(false);
            // result.setEnabled(false);
            result.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    boolean nodeType = getOldLabel().isNodeType();
                    if (result.isSelected() != nodeType) {
                        result.setSelected(nodeType);
                    }
                }
            });
        }
        return this.oldTypeCheckbox;
    }

    /** Checkbox to indicate that the old label is a node type. */
    private JCheckBox oldTypeCheckbox;

    private JCheckBox getNewTypeCheckbox() {
        if (this.newTypeCheckbox == null) {
            final JCheckBox result =
                this.newTypeCheckbox = new JCheckBox(NODE_TYPE_TEXT);
            result.setHorizontalTextPosition(SwingConstants.LEADING);
            result.setFocusable(false);
            result.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    Font font = getNewField().getFont();
                    font =
                        font.deriveFont(result.isSelected() ? Font.BOLD
                                : Font.PLAIN);
                    getNewField().setFont(font);
                    setOkEnabled();
                }
            });
        }
        return this.newTypeCheckbox;
    }

    /** Checkbox to indicate that the new label is to be a node type. */
    private JCheckBox newTypeCheckbox;

    /** Set of existing rule names. */
    private final Set<Label> existingLabels;

    /** The old label value suggested at construction time; may be {@code null}. */
    private final Label suggestedLabel;
    /** Default dialog title. */
    static private String DEFAULT_TITLE = "Relabel";
    /** Text of find label on dialog. */
    static private String OLD_TEXT = "Old label:";
    /** Text of replace label on dialog */
    static private String NEW_TEXT = "New label: ";
    /** Text of node type checkbox */
    static private String NODE_TYPE_TEXT = "  Node type";

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

    /**
     * Document listener that enables or disables the OK button, using
     * {@link #setOkEnabled()}
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
            setOkEnabled();
        }
    }
}
