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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.display.DismissDelayer;
import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.io.HTMLConverter.HTMLTag;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.IdValidator;
import nl.utwente.groove.verify.FormulaParser;

/**
 * Dialog for entering strings, with a large textfield rather than a text area.
 * The dialog remembers previously entered strings.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class StringDialog {
    /**
     * Constructs an instance of the dialog for a given dialog title.
     * @param docMap mapping from syntax documentation lines to (possibly {@code null}) associated tool tips.
     */
    public StringDialog(String title, Map<String,String> docMap) {
        this.history = new ArrayList<>();
        this.title = title;
        this.docMap = docMap;
        this.parsed = docMap != null;
    }

    /**
     * Constructs an instance of the dialog for a given dialog title.
     */
    public StringDialog(String title) {
        this(title, null);
    }

    /**
     * Makes the dialog visible and awaits the user's response. Since the dialog
     * is modal, this method returns only when the user closes the dialog. The
     * return value is the entered string.
     * @param frame the frame on which the dialog is to be displayed
     */
    public NamedEntry showDialog(Component frame) {
        if (this.title != null) {
            loadChoiceBox();
        }
        this.dialog = createDialog(frame);
        getChoiceBox().setSelectedIndex(0);
        var last = this.history.isEmpty()
            ? null
            : this.history.getFirst();
        getNameField()
            .setText(last == null
                ? ""
                : last.name());
        getTextArea()
            .setText(last == null
                ? ""
                : last.value());
        processTextChange();
        getChoiceBox().revalidate();
        getTextArea().selectAll();
        this.dialog.pack();
        this.dialog.setResizable(true);
        this.dialog.setVisible(true);
        if (this.title != null) {
            storeChoiceBox();
        }
        return getResult();
    }

    /**
     * Creates and returns a fresh dialog for the given frame.
     */
    private JDialog createDialog(Component frame) {
        Object[] buttons = {getOkButton(), getCancelButton()};
        // Name panel
        JPanel name = new JPanel();
        name.setLayout(new BorderLayout());
        name.add(new JLabel("<html><b>Property name: "), BorderLayout.WEST);
        name.add(getNameField(), BorderLayout.CENTER);
        // input panel with text area and choice box
        JPanel input = new JPanel();
        input.setLayout(new BorderLayout());
        input.setPreferredSize(new Dimension(300, 150));
        input.add(new JLabel("<html><b>Enter value:"), BorderLayout.NORTH);
        input.add(new JScrollPane(getTextArea()), BorderLayout.CENTER);
        input.add(getChoiceBox(), BorderLayout.SOUTH);
        // Error panel
        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.add(getErrorLabel());
        // Main panel
        JPanel main = new JPanel();
        main.setLayout(new BorderLayout());
        main.add(name, BorderLayout.NORTH);
        main.add(input, BorderLayout.CENTER);
        main.add(errorPanel, BorderLayout.SOUTH);
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
        list.addMouseListener(new DismissDelayer(list));
        list.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                super.setSelectionInterval(-1, -1);
            }
        });
        JPanel result = new JPanel(new BorderLayout());
        result.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        result.add(new JLabel("<html><b>Syntax:"), BorderLayout.NORTH);
        result.add(new JScrollPane(list), BorderLayout.CENTER);
        return result;
    }

    private JTextField getNameField() {
        if (this.nameField == null) {
            this.nameField = new JTextField();
            this.nameField.getDocument().addDocumentListener(this.changeListener);
        }
        return this.nameField;
    }

    private JTextField nameField;

    private JTextArea getTextArea() {
        if (this.textArea == null) {
            this.textArea = new JTextArea();
            this.textArea.getDocument().addDocumentListener(this.changeListener);
        }
        return this.textArea;
    }

    private JTextArea textArea;

    /** Lazily creates and returns the combobox containing the current choices. */
    private JComboBox<String> getChoiceBox() {
        var result = this.choiceBox;
        if (result == null) {
            var choiceBox = new JComboBox<String>();
            var choiceItems = StringDialog.this.choiceItems;
            choiceBox.setPrototypeDisplayValue("The longest value we want to display completely");
            choiceBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (choiceBox.getSelectedIndex() >= 1) {
                        var selectedItem = choiceBox.getSelectedItem();
                        if (selectedItem != null) {
                            var entry = choiceItems.get(selectedItem);
                            getNameField().setText(entry.name());
                            getTextArea().setText(entry.value());
                            getTextArea().selectAll();
                            getTextArea().requestFocus();
                        }
                    }
                }
            });
            this.choiceBox = result = choiceBox;
        }
        return result;
    }

    private void loadChoiceBox() {
        String[] storedValues = Options.getUserPrefs(this.title);
        this.history.clear();
        this.choiceItems.clear();
        getChoiceBox().removeAllItems();
        this.choiceBox.addItem("<html><i>Select a previously entered value</i>");
        for (String value : storedValues) {
            var entry = new NamedEntry(value);
            // also add unparsable formulas
            String rep = entry.hasName()
                ? bold(entry.name() + ": ") + entry.value()
                : entry.value();
            rep = parseText(entry.name(), entry.value()) == null
                ? error(rep)
                : rep;
            rep = html(rep);
            getChoiceBox().addItem(rep);
            this.choiceItems.put(rep, entry);
            this.history.add(entry);
        }
    }

    /** Mapping from items in the choice box to the original value. */
    private final Map<String,NamedEntry> choiceItems = new HashMap<>();

    /** Adds HTML error coloring to a text. */
    private String error(String text) {
        return errorColorTag().on(text);
    }

    /** Adds bold formatting to a text. */
    private String bold(String text) {
        return HTMLConverter.ITALIC_TAG.on(text);
    }

    /** Adds HTML tag a text. */
    private String html(String text) {
        return HTMLConverter.HTML_TAG.on(text);
    }

    /** Lazily creates and returns the error colour tag. */
    private HTMLTag errorColorTag() {
        var result = this.errorColorTag;
        if (result == null) {
            this.errorColorTag = result = HTMLConverter.createColorTag(Values.ERROR_COLOR);
        }
        return result;
    }

    /** Error colour tag; call {@link #errorColorTag()} to access. */
    private HTMLTag errorColorTag;

    private void storeChoiceBox() {
        String[] storedValues = new String[Math.min(this.history.size(), MAX_PERSISTENT_SIZE)];
        for (int i = 0; i < storedValues.length; i++) {
            storedValues[i] = this.history.get(i).toString();
        }
        Options.storeUserPrefs(this.title, storedValues);
    }

    /** Reacts to a change in the name field or value text area. */
    private void processTextChange() {
        var currentName = getNameField().getText();
        var currentValue = getTextArea().getText();
        var result = parseText(currentName, currentValue);
        getOkButton().setEnabled(result != null && !result.value().isEmpty());
    }

    /** Attempts to parse the given name and text value.
     * Calls {@link #parse(String)} for the actual parsing.
     * @param value the text to be parsed as a property
     * @return {@code null} if the text cannot be parsed,
     * or the parsed value otherwise
     */
    private NamedEntry parseText(String name, String value) {
        NamedEntry result;
        if (!name.isEmpty() || this.parsed) {
            String error = null;
            try {
                value = parse(value);
                if (!name.isEmpty() && !isName(name)) {
                    error = "Name '%s' should be an identifier".formatted(name);
                    result = null;
                } else {
                    result = new NamedEntry(name, value);
                }
            } catch (FormatException e) {
                error = e.getErrors().iterator().next().toString();
                result = null;
            }
            getErrorLabel().setText(error);
        } else {
            result = new NamedEntry(name, value);
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
    private JComboBox<String> choiceBox;

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
            result.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
            result.setForeground(Color.RED);
            result.setPreferredSize(new Dimension(200, 25));
            result.setMinimumSize(getOkButton().getPreferredSize());
        }
        return this.errorLabel;
    }

    /** Label displaying the current error in the renaming (if any). */
    private JLabel errorLabel;

    private final Map<String,String> docMap;
    /** The history list */
    private final List<NamedEntry> history;

    /** The title of the dialog. */
    private final String title;

    /**
     * Sets the result of the dialog from the
     * selection of the choice box.
     * Also adds the result to the history.
     */
    private boolean setResult(String name, String resultObject) {
        assert name.isEmpty() || isName(name);
        boolean ok;
        if (resultObject == null) {
            this.result = null;
            ok = true;
        } else {
            this.result = parseText(name, resultObject);
            ok = this.result != null;
        }
        if (ok && resultObject != null) {
            var entry = new NamedEntry(name, resultObject);
            this.history.remove(entry);
            this.history.add(0, entry);
        }
        return ok;
    }

    /**
     * Return the property that is entered for verification.
     * @return the property in String format
     */
    public NamedEntry getResult() {
        return this.result;
    }

    /**
     * Flag indicating that the input string should be parsed.
     */
    private boolean parsed;
    /** The field in which to store the provided data */
    private NamedEntry result;

    /** The dialog that is currently visible. */
    private JDialog dialog;

    /** The singleton action listener. */
    private final CloseListener closeListener = new CloseListener();

    /** Checks whether a given string is a valid entry name. */
    static public boolean isName(String name) {
        return ID_VALIDATOR.isValid(name);
    }

    /** Validator for property names. */
    static private final IdValidator ID_VALIDATOR = new IdValidator() {
        @Override
        public boolean isIdentifierStart(char c) {
            return c != FormulaParser.FLAG_PREFIX.charAt(0) && super.isIdentifierStart(c);
        }
    };

    /** Keeps on creating a dialog until the user enters "stop". */
    static public void main(String[] args) {
        StringDialog dialog = createStringDialog("Input a string");
        boolean stop = false;
        do {
            dialog.showDialog(null);
            System.out.printf("Selected string: %s%n", dialog.getResult());
            stop = "stop".equals(dialog.getResult().value());
        } while (!stop);
        System.exit(0);
    }

    /** Parser that leaves a given string unchanged. */
    public static final StringDialog createStringDialog(String title) {
        return new StringDialog(title, null) {
            @Override
            protected String parse(String text) throws FormatException {
                return text;
            }
        };
    }

    /** Maximum number of persistently stored entries. */
    private static final int MAX_PERSISTENT_SIZE = 10;

    /** The singleton document change listener for the name field. */
    private final DocumentListener changeListener = new DocumentListener() {
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
    };

    /**
     * Action listener that closes the dialog and makes sure that the property
     * is set (possibly to null).
     */
    private class CloseListener extends WindowAdapter implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean ok = false;
            if (e.getSource() == getOkButton()) {
                var name = getNameField().getText();
                if (!isName(name)) {
                    name = "";
                }
                ok = setResult(name, getTextArea().getText());
            } else if (e.getSource() == getCancelButton()) {
                ok = setResult("", null);
            }
            if (ok) {
                StringDialog.this.dialog.setVisible(false);
            }
        }

        @Override
        public void windowClosing(WindowEvent e) {
            if (setResult("", null)) {
                StringDialog.this.dialog.setVisible(false);
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
            Component result
                = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (result == this) {
                setToolTipText(this.tipMap.get(value));
            }
            return result;
        }

        private final Map<String,String> tipMap;
    }

    /** Combination of name and value. */
    public record NamedEntry(String name, String value) {
        /** Constructs a new entry out of a given name and value. */
        public NamedEntry(String name, String value) {
            if (!name.isEmpty() && !isName(name)) {
                throw Exceptions.illegalArg("Entry name '%s' is not an allowed identifier", name);
            }
            this.name = name;
            this.value = value;
        }

        /** Constructs an entry from a single string with {@link #SEPARATOR} as separator between name and value. */
        NamedEntry(String combi) {
            this(namePart(combi), valuePart(combi));
        }

        /** Indicates if this entry has a (non-empty) name. */
        public boolean hasName() {
            return !name().isEmpty();
        }

        @Override
        public String toString() {
            return hasName()
                ? name() + SEPARATOR + value()
                : value();
        }

        static private String namePart(String combi) {
            int i = combi.indexOf(SEPARATOR);
            return i < 0
                ? ""
                : combi.substring(0, i);
        }

        static private String valuePart(String combi) {
            int i = combi.indexOf(SEPARATOR);
            return i < 0
                ? combi
                : combi.substring(i + 1);
        }

        static public final char SEPARATOR = ':';
    }
}
