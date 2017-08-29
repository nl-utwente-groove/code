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

import groove.gui.look.Values;
import groove.util.Properties;
import groove.util.Properties.CheckerMap;
import groove.util.parse.FormatErrorSet;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;

/**
 * Dialog for editing a properties map.
 * @author Arend Rensink
 * @version $Revision $
 */
public class PropertiesDialog {
    /**
     * Constructs an instance of the dialog for a given parent frame, a given
     * (non-<code>null</code>) set of properties, and a flag indicating if
     * the properties should be editable. A further parameter gives a list of
     * default keys that treated specially: they are added by default during
     * editing, and are ordered first in the list.
     * @param properties the initial properties table
     */
    public PropertiesDialog(Properties properties) {
        this.table = new PropertiesTable(properties.getKeyType(), true);
        this.table.setProperties(properties);
    }

    /**
     * In addition to creating an instance of the dialog, also sets
     * a non-empty checker map for the edited values.
     * @param properties the initial properties table
     * @param checkerMap checkers for the property values
     * @see #PropertiesDialog(Properties)
     */
    public PropertiesDialog(Properties properties, CheckerMap checkerMap) {
        this(properties);
        this.table.setCheckerMap(checkerMap);
    }

    /**
     * Makes the dialog visible and awaits the user's response. Since the dialog
     * is modal, this method returns only when the user closes the dialog. The
     * return value indicates if the properties have changed.
     * @param frame the frame on which the dialog is to be displayed
     * @return <code>true</code> if the properties have changed during the
     *         time the dialog was visible.
     */
    public boolean showDialog(Component frame) {
        boolean result;
        boolean stopDialog;
        do {
            getContentPane().setValue(null);
            getContentPane().setVisible(true);
            JDialog dialog = getContentPane().createDialog(frame, createTitle());
            dialog.setResizable(true);
            dialog.setVisible(true);
            dialog.dispose();
            Object selectedValue = getContentPane().getValue();
            if (this.table.isChanged()) {
                if (selectedValue == getOkButton()) {
                    result = stopDialog = true;
                } else {
                    int abandon = showAbandonDialog();
                    result = abandon == JOptionPane.YES_OPTION;
                    stopDialog = abandon != JOptionPane.CANCEL_OPTION;
                }
            } else {
                // nothing was changed during editing
                result = false;
                stopDialog = true;
            }
        } while (!stopDialog);
        return result;
    }

    /** Returns the (edited) properties. */
    public Map<String,String> getProperties() {
        return this.table.getProperties();
    }

    /**
     * Creates and shows a confirmation dialog for abandoning the currently
     * edited graph.
     */
    private int showAbandonDialog() {
        int response =
            JOptionPane.showConfirmDialog(getContentPane(), "Use changed properties?", null,
                JOptionPane.YES_NO_CANCEL_OPTION);
        return response;
    }

    private String createTitle() {
        return DIALOG_TITLE;
    }

    JOptionPane getContentPane() {
        if (this.pane == null) {
            int mode;
            Object[] buttons;
            mode = JOptionPane.OK_CANCEL_OPTION;
            buttons = new Object[] {getOkButton(), createCancelButton()};
            this.pane =
                new JOptionPane(createTablePane(), JOptionPane.PLAIN_MESSAGE, mode, null, buttons);
        }
        return this.pane;
    }

    /** The option pane creating the dialog. */
    private JOptionPane pane;

    /**
     * Lazily creates and returns a button labelled OK that signals the editors
     * to stop editing. This makes sure that any partially edited result is not
     * lost.
     */
    JButton getOkButton() {
        if (this.okButton == null) {
            this.okButton = new JButton("OK");
            this.okButton.addActionListener(new CloseListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    TableCellEditor editor = getInnerTable().getCellEditor();
                    if (editor == null || editor.stopCellEditing()) {
                        super.actionPerformed(e);
                    }
                }
            });
        }
        return this.okButton;
    }

    /** The OK button on the option pane. */
    private JButton okButton;

    /** Creates and returns a button labelled Cancel. */
    private JButton createCancelButton() {
        JButton result = new JButton("Cancel");
        result.addActionListener(new CloseListener());
        return result;
    }

    /** Creates the pane for the table model. */
    public Container createTablePane() {
        JPanel result = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(this.table);
        scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
        result.add(scrollPane);
        result.add(getErrorLabel(), BorderLayout.SOUTH);
        return result;
    }

    /**
     * Public getter for the inner JTable.
     * Used within Editor.java to create the proper 'Ok' button on the preview
     * dialog (which is displayed after rule editing).
     */
    public PropertiesTable getInnerTable() {
        return this.table;
    }

    /** The table component. */
    private final PropertiesTable table;

    private JLabel getErrorLabel() {
        if (this.errorLabel == null) {
            this.errorLabel = new JLabel();
            this.errorLabel.setBorder(new EmptyBorder(new Insets(5, 0, 0, 0)));
            this.errorLabel.setForeground(Values.ERROR_NORMAL_FOREGROUND);
            this.errorLabel.setText(null);
            getInnerTable().getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        setErrorText();
                    }
                });
            getInnerTable().getModel().addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    setErrorText();
                }
            });
        }
        return this.errorLabel;
    }

    /**
     * Refreshes the text of the error label, depending on the
     * currently selected cell of the properties table.
     */
    private void setErrorText() {
        FormatErrorSet errors = getInnerTable().getSelectedErrors();
        if (errors.isEmpty()) {
            this.errorLabel.setText("");
        } else {
            this.errorLabel.setText(errors.iterator().next().toString());
        }
    }

    private JLabel errorLabel;

    /** Title of the dialog. */
    public static final String DIALOG_TITLE = "Properties editor";
    /** Column header of the property column. */
    public static final String PROPERTY_HEADER = "Property";
    /** Column header of the value column. */
    public static final String VALUE_HEADER = "Value";

    /**
     * Action listener that closes the dialog and sets the option pane's value
     * to the source of the event.
     */
    private class CloseListener implements ActionListener {
        /**
         * Empty constructor with the correct visibility.
         */
        public CloseListener() {
            // empty
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            getContentPane().setValue(e.getSource());
            getContentPane().setVisible(false);
        }
    }
}
