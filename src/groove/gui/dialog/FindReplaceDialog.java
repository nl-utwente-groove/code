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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeLabel;
import groove.grammar.type.TypeNode;
import groove.graph.EdgeRole;
import groove.io.HTMLConverter;
import groove.util.parse.FormatException;

/**
 * Dialog finding/replacing labels.
 * @author Arend Rensink
 * @author Eduardo Zambon
 */
public class FindReplaceDialog {

    /** Cancel result. */
    public static final int CANCEL = 0;
    /** Find result. */
    public static final int FIND = 1;
    /** Replace result. */
    public static final int REPLACE = 2;

    /**
     * Constructs a dialog instance, given a set of existing names (that should
     * not be used) as well as a suggested value for the new rule name.
     * @param typeGraph the type graph containing all labels and sublabels
     * @param oldLabel the label to rename; may be <code>null</code>
     */
    public FindReplaceDialog(TypeGraph typeGraph, TypeLabel oldLabel) {
        this.typeGraph = typeGraph;
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
    public int showDialog(JFrame frame, String title) {
        // set the suggested name in the name field
        if (this.suggestedLabel != null) {
            getOldField().setSelectedItem(this.suggestedLabel);
            propagateSelection();
        }
        setReplaceEnabled();
        JDialog dialog = getOptionPane().createDialog(frame, title == null ? DEFAULT_TITLE : title);
        dialog.setVisible(true);
        Object response = getOptionPane().getValue();
        int result;
        if (response == getReplaceButton() || response == getNewField()) {
            result = REPLACE;
        } else if (response == getFindButton()) {
            result = FIND;
        } else {
            result = CANCEL;
        }
        return result;
    }

    /**
     * Propagates the selection in the old field to all other GUI elements.
     */
    private void propagateSelection() {
        TypeLabel selection = (TypeLabel) getOldField().getSelectedItem();
        getOldTypeLabel().setText(selection.getRole().getDescription(true));
        getNewTypeCombobox().setSelectedIndex(EdgeRole.getIndex(selection.getRole()));
        getNewField().setText(selection.text());
        getNewField().setSelectionStart(0);
        getNewField().setSelectionEnd(selection.text().length());
        getNewField().requestFocus();
    }

    /** Returns the label to be renamed. */
    public TypeLabel getOldLabel() {
        return (TypeLabel) getOldField().getSelectedItem();
    }

    /** Returns the renamed label. */
    public TypeLabel getNewLabel() {
        TypeLabel result;
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
    private TypeLabel getNewLabelWithErrors() throws FormatException {
        TypeLabel result = null;
        String text = getNewField().getText();
        if (text.length() > 0) {
            int labelType = getNewTypeCombobox().getSelectedIndex();
            result = TypeLabel.createLabel(EdgeRole.getRole(labelType), text);
            TypeLabel oldLabel = getOldLabel();
            if (result.equals(oldLabel)) {
                throw new FormatException("Old and new labels coincide");
            } else if (this.typeGraph.isNodeType(oldLabel) && this.typeGraph.isNodeType(result)) {
                TypeNode oldType = this.typeGraph.getNode(oldLabel);
                TypeNode newType = this.typeGraph.getNode(result);
                if (newType != null) {
                    if (this.typeGraph.isSubtype(oldType, newType)) {
                        throw new FormatException("New label '%s' is an existing supertype of '%s'",
                            result, oldLabel);
                    } else if (this.typeGraph.isSubtype(newType, oldType)) {
                        throw new FormatException("New label '%s' is an existing subtype of '%s'",
                            result, oldLabel);
                    }
                }
            }
        } else {
            throw new FormatException("Empty replacement label not allowed");
        }
        return result;
    }

    /**
     * Enables or disables the Replace-button, depending on the validity of the
     * renaming. Displays the error in {@link #getErrorLabel()} if the renaming
     * is not valid.
     */
    private void setReplaceEnabled() {
        boolean enabled;
        try {
            getNewLabelWithErrors();
            getErrorLabel().setText("");
            enabled = true;
        } catch (FormatException exc) {
            getErrorLabel().setText(exc.getMessage());
            enabled = false;
        }
        getReplaceButton().setEnabled(enabled);
        getNameFieldListener().setEnabled(enabled);
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
            oldPanel.add(getOldTypeLabel(), BorderLayout.EAST);
            JPanel newPanel = new JPanel(new BorderLayout());
            newPanel.add(newLabel, BorderLayout.WEST);
            newPanel.add(getNewField(), BorderLayout.CENTER);
            newPanel.add(getNewTypeCombobox(), BorderLayout.EAST);
            JPanel errorPanel = new JPanel(new BorderLayout());
            errorPanel.add(getErrorLabel());
            errorPanel.setPreferredSize(oldPanel.getPreferredSize());
            this.optionPane = new JOptionPane(new Object[] {oldPanel, newPanel, errorPanel},
                JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null,
                new Object[] {getFindButton(), getReplaceButton(), getCancelButton()});
        }
        return this.optionPane;
    }

    /** The option pane that is the core of the dialog. */
    private JOptionPane optionPane;

    /**
     * Returns the Replace button on the dialog.
     */
    private JButton getReplaceButton() {
        if (this.replaceButton == null) {
            this.replaceButton = new JButton("Replace");
            this.replaceButton.addActionListener(new CloseListener());
        }
        return this.replaceButton;
    }

    /** The OK button in the dialog. */
    private JButton replaceButton;

    /**
     * Returns the Find button on the dialog.
     */
    private JButton getFindButton() {
        if (this.findButton == null) {
            this.findButton = new JButton("Find");
            this.findButton.addActionListener(new CloseListener());
        }
        return this.findButton;
    }

    /** The find button in the dialog. */
    private JButton findButton;

    /**
     * Returns the cancel button on the dialog.
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
    private JComboBox<TypeLabel> getOldField() {
        if (this.oldField == null) {
            final JComboBox<TypeLabel> result = this.oldField = getLabelComboBox(this.typeGraph);
            result.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    propagateSelection();
                }
            });
        }
        return this.oldField;
    }

    /** The text field where the original label is entered. */
    private JComboBox<TypeLabel> oldField;

    /** Returns the text field in which the user is to enter his input. */
    private JTextField getNewField() {
        if (this.newField == null) {
            this.newField = new JTextField();
            this.newField.getDocument().addDocumentListener(new OverlapListener());
            this.newField.addActionListener(getNameFieldListener());
        }
        return this.newField;
    }

    /** Returns the close listener for the name field. */
    private CloseListener getNameFieldListener() {
        if (this.nameFieldListener == null) {
            this.nameFieldListener = new CloseListener();
        }
        return this.nameFieldListener;
    }

    /** The text field where the renamed label is entered. */
    private JTextField newField;
    /** Close listener for the new name field. */
    private CloseListener nameFieldListener;

    /** Returns the label displaying the current error in the renaming (if any). */
    private JLabel getErrorLabel() {
        if (this.errorLabel == null) {
            JLabel result = this.errorLabel = new JLabel();
            result.setForeground(Color.RED);
            result.setMinimumSize(getReplaceButton().getPreferredSize());
        }
        return this.errorLabel;
    }

    /** Label displaying the current error in the renaming (if any). */
    private JLabel errorLabel;

    /** Returns the combo box for the old label's type. */
    private JLabel getOldTypeLabel() {
        if (this.oldTypeLabel == null) {
            final JLabel result = this.oldTypeLabel = new JLabel();
            result.setText(getOldLabel().getRole().getDescription(true));
            result.setPreferredSize(getNewTypeCombobox().getPreferredSize());
            result.setBorder(new EtchedBorder());
            result.setEnabled(true);
            result.setFocusable(false);
        }
        return this.oldTypeLabel;
    }

    /** Combobox showing the new label's type. */
    private JLabel oldTypeLabel;

    /** Returns the combo box for the label in the given type graph. */
    private JComboBox<TypeLabel> getLabelComboBox(TypeGraph typeGraph) {
        final JComboBox<TypeLabel> result = new JComboBox<>();
        result.setFocusable(false);
        result.setRenderer(new DefaultListCellRenderer() {
            @SuppressWarnings("rawtypes")
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
                if (value instanceof TypeLabel) {
                    value = HTMLConverter.HTML_TAG.on(((TypeLabel) value).toLine().toHTMLString());
                }
                return super.getListCellRendererComponent(list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus);
            }
        });
        for (TypeLabel label : sortLabels(typeGraph.getLabels())) {
            if (!label.isDataType() && label != TypeLabel.NODE) {
                result.addItem(label);
            }
        }
        return result;
    }

    private List<TypeLabel> sortLabels(Set<TypeLabel> labels) {
        List<TypeLabel> result = new ArrayList<>(labels.size());
        List<TypeLabel> nodeTypes = new ArrayList<>();
        List<TypeLabel> flags = new ArrayList<>();
        List<TypeLabel> binary = new ArrayList<>();
        for (TypeLabel label : labels) {
            switch (label.getRole()) {
            case NODE_TYPE:
                nodeTypes.add(label);
                break;
            case FLAG:
                flags.add(label);
                break;
            case BINARY:
                binary.add(label);
            }
        }
        Collections.sort(nodeTypes);
        Collections.sort(flags);
        Collections.sort(binary);
        result.addAll(nodeTypes);
        result.addAll(flags);
        result.addAll(binary);
        return result;
    }

    /** Returns the combobox for the new label's type. */
    private JComboBox<String> getNewTypeCombobox() {
        if (this.newTypeChoice == null) {
            final JComboBox<String> result = this.newTypeChoice = new JComboBox<>();
            for (EdgeRole kind : EdgeRole.values()) {
                result.addItem(kind.getDescription(true));
            }
            result.setSelectedIndex(EdgeRole.getIndex(getOldLabel().getRole()));
            result.setEnabled(true);
            result.setFocusable(false);
            result.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Font font = getNewField().getFont();
                    int fontProperty;
                    switch (EdgeRole.getRole(result.getSelectedIndex())) {
                    case NODE_TYPE:
                        fontProperty = Font.BOLD;
                        break;
                    case FLAG:
                        fontProperty = Font.ITALIC;
                        break;
                    default:
                        fontProperty = Font.PLAIN;
                    }
                    font = font.deriveFont(fontProperty);
                    getNewField().setFont(font);
                    setReplaceEnabled();
                }
            });

        }
        return this.newTypeChoice;
    }

    /** Combobox showing the old label's type. */
    private JComboBox<String> newTypeChoice;

    /** Set of existing rule names. */
    private final TypeGraph typeGraph;

    /** The old label value suggested at construction time; may be {@code null}. */
    private final TypeLabel suggestedLabel;
    /** Default dialog title. */
    static private String DEFAULT_TITLE = "Find/Replace Labels";
    /** Text of find label on dialog. */
    static private String OLD_TEXT = "Find label: ";
    /** Text of replace label on dialog */
    static private String NEW_TEXT = "Replace with: ";

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
     * Document listener that enables or disables the OK button, using
     * {@link #setReplaceEnabled()}
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
            testRenaming();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            testRenaming();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            testRenaming();
        }

        /**
         * Tests if the content of the name field is a good choice of rule name.
         * The OK button is enabled or disabled as a consequence of this.
         */
        private void testRenaming() {
            setReplaceEnabled();
        }
    }
}
