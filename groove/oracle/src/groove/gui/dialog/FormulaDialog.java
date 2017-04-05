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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import groove.gui.Options;
import groove.util.parse.FormatException;

/**
 * Dialog for entering strings.
 * The dialog remembers previously entered strings and attempts to autocomplete.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class FormulaDialog {
    /**
     * Constructs an instance of the dialog for a given dialog title.
     * @param docMap mapping from syntax documentation lines to (possibly {@code null}) associated tool tips.
     */
    public FormulaDialog(String title, Map<String,String> docMap) {
        this.history = new ArrayList<>();
        this.title = title;
        this.docMap = docMap;
        this.parsed = docMap != null;
    }

    /**
     * Constructs an instance of the dialog for a given dialog title.
     */
    public FormulaDialog(String title) {
        this(title, null);
    }

    /**
     * Makes the dialog visible and awaits the user's response. Since the dialog
     * is modal, this method returns only when the user closes the dialog. The
     * return value indicates if the properties have changed.
     * @param frame the frame on which the dialog is to be displayed
     */
    public String showDialog(Component frame) {
        if (this.title != null) {
            String[] storedValues = Options.getUserPrefs(this.title);
            this.history.clear();
            for (String value : storedValues) {
                String parsedValue = parseText(value);
                if (value != null && parsedValue != null) {
                    this.history.add(value);
                }
            }
        }
        this.dialog = createDialog(frame);
        getChoiceBox().setSelectedItem("");
        getEditor().setText("");
        processTextChange();
        getChoiceBox().revalidate();
        getEditor().selectAll();
        this.dialog.pack();
        this.dialog.setResizable(true);
        this.dialog.setVisible(true);
        if (this.title != null) {
            String[] storedValues = new String[Math.min(this.history.size(), MAX_PERSISTENT_SIZE)];
            for (int i = 0; i < storedValues.length; i++) {
                storedValues[i] = this.history.get(i);
            }
            Options.storeUserPrefs(this.title, storedValues);
        }
        return getResult();
    }

    /**
     * Creates and returns a fresh dialog for the given frame.
     */
    private JDialog createDialog(Component frame) {
        Object[] buttons = new Object[] {getOkButton(), getCancelButton()};
        JPanel input = new JPanel();
        input.setLayout(new BorderLayout());
        input.add(getChoiceBox(), BorderLayout.NORTH);
        // add an error label if there is a parser
        if (this.parsed) {
            JPanel errorPanel = new JPanel(new BorderLayout());
            errorPanel.add(getErrorLabel());
            input.add(errorPanel, BorderLayout.SOUTH);
        }
        JPanel main = new JPanel();
        main.setLayout(new BorderLayout());
        main.add(input, BorderLayout.CENTER);
        if (this.parsed) {
            main.add(createSyntaxPanel(), BorderLayout.EAST);
        }
        JOptionPane panel = new JOptionPane(main, JOptionPane.PLAIN_MESSAGE,
            JOptionPane.OK_CANCEL_OPTION, null, buttons);
        JDialog result = panel.createDialog(frame, this.title);
        result.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        result.addWindowListener(this.closeListener);
        return result;
    }

    private JComponent createSyntaxPanel() {
        final JList<String> list = new JList<>();
        DefaultListModel<String> model = new DefaultListModel<>();
        for (Map.Entry<String,String> entry : this.docMap.entrySet()) {
            model.addElement(entry.getKey());
        }
        list.setModel(model);
        list.setCellRenderer(new MyCellRenderer(this.docMap));
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (e.getSource() == list) {
                    this.manager.setDismissDelay(Integer.MAX_VALUE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (e.getSource() == list) {
                    this.manager.setDismissDelay(this.standardDelay);
                }
            }

            private final ToolTipManager manager = ToolTipManager.sharedInstance();
            private final int standardDelay = this.manager.getDismissDelay();
        });
        return new JScrollPane(list);
    }

    /** Lazily creates and returns the combobox containing the current choices. */
    private MyComboBox getChoiceBox() {
        if (this.choiceBox == null) {
            this.choiceBox = new MyComboBox();
            this.choiceBox
                .setPrototypeDisplayValue("The longest value we want to display completely");
            this.choiceBox.setModel(createModel());
            this.choiceBox.setEditable(true);
            JTextField editor = (JTextField) this.choiceBox.getEditor()
                .getEditorComponent();
            editor.addActionListener(this.closeListener);
            editor.getDocument()
                .addDocumentListener(this.changeListener);
        }
        return this.choiceBox;
    }

    /**
     * Creates and initialises a fresh instance of {@link MyComboBoxModel}.
     */
    private MyComboBoxModel createModel() {
        MyComboBoxModel result = new MyComboBoxModel();
        result.setDirty("");
        return result;
    }

    /** Returns the editor currently used in the {@link #choiceBox}. */
    private JTextField getEditor() {
        return (JTextField) getChoiceBox().getEditor()
            .getEditorComponent();
    }

    /** Returns the model currently used in the {@link #choiceBox}. */
    private MyComboBoxModel getModel() {
        return (MyComboBoxModel) getChoiceBox().getModel();
    }

    /** Reacts to a change in the editor. */
    private void processTextChange() {
        final String currentText = getEditor().getText();
        String result = parseText(currentText);
        getOkButton().setEnabled(result != null && !currentText.isEmpty());
        getModel().setDirty(currentText);
    }

    /** Attempts to parse the given text.
     * Calls {@link #parse(String)} for the actual parsing.
     * @param text the text to be parsed as a property
     * @return {@code null} if the text cannot be parsed,
     * or the parsed result otherwise
     */
    private String parseText(String text) {
        String result;
        if (this.parsed) {
            try {
                result = parse(text);
                getErrorLabel().setText(null);
            } catch (FormatException e) {
                getErrorLabel().setText(e.getMessage());
                result = null;
            }
        } else {
            result = text;
        }
        return result;
    }

    /**
     * Parses a given text as an object of the right kind.
     * @param text the text to be parsed
     * @return the parsed object
     * @throws FormatException if there is a parse error
     */
    abstract protected String parse(String text) throws FormatException;

    /** The choice box */
    private MyComboBox choiceBox;

    /**
     * Lazily creates and returns a button labelled OK.
     * @return the ok button
     */
    private JButton getOkButton() {
        if (this.okButton == null) {
            this.okButton = new JButton("OK");
            this.okButton.addActionListener(this.closeListener);
            this.okButton.setEnabled(false);
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
            this.cancelButton.addActionListener(this.closeListener);
        }
        return this.cancelButton;
    }

    /** The CANCEL button on the option pane. */
    private JButton cancelButton;

    /** Returns the label displaying the current error in entered string (if any). */
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

    private final Map<String,String> docMap;
    /** The history list */
    private final List<String> history;

    /** The title of the dialog. */
    private final String title;

    /**
     * Sets the result of the dialog from the
     * selection of the choice box.
     * Also adds the result to the history.
     */
    private boolean setResult(String resultObject) {
        boolean ok;
        if (resultObject == null) {
            this.result = null;
            ok = true;
        } else {
            this.result = parseText(resultObject);
            ok = this.result != null;
        }
        if (ok && resultObject != null) {
            this.history.remove(resultObject);
            this.history.add(0, resultObject);
        }
        return ok;
    }

    /**
     * Return the property that is entered for verification.
     * @return the property in String format
     */
    public String getResult() {
        return this.result;
    }

    /**
     * Flag indicating that the input string should be parsed.
     */
    private boolean parsed;
    /** The field in which to store the provided data */
    private String result;

    /** The dialog that is currently visible. */
    private JDialog dialog;

    /** The singleton action listener. */
    private final CloseListener closeListener = new CloseListener();

    /** Keeps on creating a dialog until the user enters "stop". */
    static public void main(String[] args) {
        FormulaDialog dialog = createStringDialog("Input a string");
        boolean stop = false;
        do {
            dialog.showDialog(null);
            System.out.printf("Selected string: %s%n", dialog.getResult());
            stop = "stop".equals(dialog.getResult());
        } while (!stop);
        System.exit(0);
    }

    /** Parser that leaves a given string unchanged. */
    public static final FormulaDialog createStringDialog(String title) {
        return new FormulaDialog(title, null) {
            @Override
            protected String parse(String text) throws FormatException {
                return text;
            }
        };
    }

    /** Maximum number of persistently stored entries. */
    private static final int MAX_PERSISTENT_SIZE = 10;

    /**
     * Overrides the {@link JComboBox#configureEditor(ComboBoxEditor, Object)}
     * method to avoid confusing the editor.
     */
    private static class MyComboBox extends JComboBox<String> {
        @Override
        public void configureEditor(ComboBoxEditor anEditor, Object anItem) {
            if (anItem != null && this.configure) {
                super.configureEditor(anEditor, anItem);
            }
        }

        public void doConfigure(boolean configure) {
            this.configure = configure;
        }

        private boolean configure;
    }

    private class MyComboBoxModel implements ComboBoxModel<String> {
        @Override
        public Object getSelectedItem() {
            return this.selectedItem;
        }

        @Override
        public void setSelectedItem(Object anItem) {
            this.selectedItem = anItem;
            // also set this item in the editor, however without changing the
            // data model
            if (anItem != null) {
                this.ignoreChange = true;
                getEditor().setText(anItem.toString());
                getEditor().selectAll();
                this.ignoreChange = false;
            }
        }

        @Override
        public void addListDataListener(ListDataListener l) {
            this.listeners.add(l);
        }

        @Override
        public String getElementAt(int index) {
            synchroniseModel();
            return this.contents.get(index);
        }

        @Override
        public int getSize() {
            synchroniseModel();
            return this.contents.size();
        }

        @Override
        public void removeListDataListener(ListDataListener l) {
            this.listeners.remove(l);
        }

        public void setDirty(String filterText) {
            if (!this.ignoreChange) {
                getChoiceBox().doConfigure(false);
                this.dirty = true;
                this.filterText = filterText;
                this.selectedItem = null;
                for (ListDataListener l : this.listeners) {
                    l.contentsChanged(
                        new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize()));
                }
                getChoiceBox().hidePopup();
                if (getSize() > 0 && filterText.length() > 0) {
                    getChoiceBox().showPopup();
                }
                getChoiceBox().doConfigure(true);
            }
        }

        private void synchroniseModel() {
            if (this.dirty) {
                this.dirty = false;
                this.contents.clear();
                for (String entry : FormulaDialog.this.history) {
                    if (entry.contains(this.filterText)) {
                        this.contents.add(entry);
                    }
                }
            }
        }

        /**
         * Flag controlling whether the model should really be
         * set to dirty. This enables the changes due to a #setSelectedItem(Object)
         * to be ignored.
         */
        private boolean ignoreChange = false;
        /** Flag indicating if the model should be refreshed from the history. */
        private boolean dirty = true;
        /** Text determining which part of the history should be included in the model. */
        private String filterText;
        /** The actual model. */
        private final List<String> contents = new ArrayList<>();
        /** The listeners for this model. */
        private final List<ListDataListener> listeners = new ArrayList<>();
        /**
         * The currently selected item. Note that there is no connection
         * between this and the model.
         * @see #setSelectedItem(Object)
         * @see #getSelectedItem()
         */
        private Object selectedItem;
    }

    /** The singleton document change listener. */
    private final ChangeListener changeListener = new ChangeListener();

    private class ChangeListener implements DocumentListener {
        @Override
        public void changedUpdate(DocumentEvent e) {
            processTextChange();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            processTextChange();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            processTextChange();
        }
    }

    /**
     * Action listener that closes the dialog and makes sure that the property
     * is set (possibly to null).
     */
    private class CloseListener extends WindowAdapter implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean ok = false;
            if (e.getSource() == getOkButton() || e.getSource() instanceof JTextField) {
                ok = setResult(getEditor().getText());
            } else if (e.getSource() == getCancelButton()) {
                ok = setResult(null);
            }
            if (ok) {
                FormulaDialog.this.dialog.setVisible(false);
            }
        }

        @Override
        public void windowClosing(WindowEvent e) {
            if (setResult(null)) {
                FormulaDialog.this.dialog.setVisible(false);
            }
        }
    }

    /** Private cell renderer class that inserts the correct tool tips. */
    private static class MyCellRenderer extends DefaultListCellRenderer {
        MyCellRenderer(Map<String,String> tipMap) {
            this.tipMap = tipMap;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
            Component result =
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (result == this) {
                setToolTipText(this.tipMap.get(value));
            }
            return result;
        }

        private final Map<String,String> tipMap;
    }

}
