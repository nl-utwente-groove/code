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

import static nl.utwente.groove.explore.util.LTSLabels.PLACEHOLDER;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import nl.utwente.groove.explore.util.LTSLabels;
import nl.utwente.groove.explore.util.LTSLabels.Flag;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.UserSettings;
import nl.utwente.groove.gui.UserSettings.Persistable;
import nl.utwente.groove.io.GrooveFileChooser;
import nl.utwente.groove.util.Exceptions;

/**
 * @author Tom Staijen
 * @version $Revision$
 */
public class SaveLTSAsDialog implements Persistable {

    /** Title of the dialog. */
    public static final String DIALOG_TITLE = "Save LTS As";

    /** Creates a new dialog for options to export the LTS * */
    private SaveLTSAsDialog() {
        // nothing to do
    }

    /** Sets the directory to initialise the file directory browser at * */
    public void setCurrentDirectory(String value) {
        this.currentDirectory = value;
    }

    /** The current Grammar Directory */
    private String currentDirectory;

    /**
     * Shows the dialog. The passed frame is locked until the dialog is closed.
     * Returns true if the dialog was closed with ok, false in case of cancel.
     */
    public boolean showDialog(Simulator simulator) {
        this.simulator = simulator;
        this.getContentPane().setVisible(true);
        var dialog = getContentPane().createDialog(simulator.getFrame(), createTitle());
        dialog.setVisible(true);
        return (getContentPane().getValue() == getOkButton());
    }

    /** The simulator, for fetching the frame instance */
    private Simulator simulator;

    /**
     * @return the contentpane
     */
    JOptionPane getContentPane() {
        Object[] buttons = {getOkButton(), getCancelButton()};
        if (this.pane == null) {
            this.pane = new JOptionPane(createPanel(), JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION, null, buttons);
            ToolTipManager.sharedInstance().registerComponent(this.pane);
            // new JOptionPane(createPanel(), JOptionPane.PLAIN_MESSAGE,
            // JOptionPane.OK_CANCEL_OPTION, null, buttons);
        }
        return this.pane;
    }

    /**
     * Create and return the main panel.
     * @return the main panel.
     */
    private JPanel createPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel dirPanel = new JPanel();
        dirPanel.setLayout(new BoxLayout(dirPanel, BoxLayout.X_AXIS));
        JLabel dirLabel = new JLabel("Directory: ");
        dirLabel.setPreferredSize(new Dimension(60, 0));
        dirLabel.setToolTipText("Directory where the LTS and states are to be saved");
        dirPanel.add(dirLabel);
        dirPanel.add(getDirField());
        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(new BrowseButtonListener());
        browseButton.setPreferredSize(PREF_RIGHT);
        dirPanel.add(browseButton);

        JPanel ltsPanel = new JPanel();
        ltsPanel.setLayout(new BoxLayout(ltsPanel, BoxLayout.X_AXIS));
        JLabel ltsLabel = new JLabel("LTS filename pattern: ");
        ltsLabel.setPreferredSize(PREF_LEFT);
        ltsLabel
            .setToolTipText(String
                .format("LTS file name: "
                    + "'%s' is replaced by the grammar ID, extension determines file format",
                        PLACEHOLDER));
        ltsPanel.add(ltsLabel);
        ltsPanel.add(getLTSPatternField());
        ltsPanel.add(Box.createHorizontalStrut(PREF_RIGHT.width));

        JPanel statePanel = new JPanel();
        statePanel.setLayout(new BoxLayout(statePanel, BoxLayout.X_AXIS));
        JLabel stateLabel = new JLabel("State filename pattern: ");
        stateLabel.setPreferredSize(PREF_LEFT);
        stateLabel
            .setToolTipText(String
                .format("Pattern for state file names: "
                    + "'%s' is replaced by the state number, extension determines file format",
                        PLACEHOLDER));
        statePanel.add(stateLabel);
        statePanel.add(getStatePatternField());
        statePanel.add(Box.createHorizontalStrut(PREF_RIGHT.width));

        JPanel filePanel = new JPanel(new BorderLayout());
        filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.PAGE_AXIS));
        filePanel.setBorder(new TitledBorder(new EtchedBorder(), "Destination"));
        filePanel.add(dirPanel);
        filePanel.add(ltsPanel);
        filePanel.add(statePanel);
        mainPanel.add(filePanel);

        JPanel labelPanel = new JPanel(new GridLayout(0, 1));
        labelPanel.setBorder(new TitledBorder(new EtchedBorder(), "Label options"));
        labelPanel.add(createFlagPanel(Flag.START));
        labelPanel.add(createFlagPanel(Flag.FINAL));
        labelPanel.add(createFlagPanel(Flag.RESULT));
        labelPanel.add(createFlagPanel(Flag.OPEN));
        labelPanel.add(createFlagPanel(Flag.NUMBER));
        labelPanel.add(createFlagPanel(Flag.TRANSIENT));
        labelPanel.add(createFlagPanel(Flag.RECIPE));
        mainPanel.add(labelPanel);

        JPanel savePanel = new JPanel(new GridLayout(0, 1));
        savePanel.setBorder(new TitledBorder(new EtchedBorder(), "Save states"));
        savePanel
            .setToolTipText("Select which states should be saved along with the LTS (in the same directory)");

        savePanel.add(getExportButton(StateExport.NONE));
        savePanel.add(getExportButton(StateExport.RESULT));
        savePanel.add(getExportButton(StateExport.FINAL));
        savePanel.add(getExportButton(StateExport.TOP));
        savePanel.add(getExportButton(StateExport.ALL));
        mainPanel.add(savePanel);

        mainPanel.add(getErrorLabel());

        JPanel buttons = new JPanel();
        // OK or CANCEL
        this.okButton = getOkButton();
        this.cancelButton = getCancelButton();
        buttons.add(this.okButton);
        buttons.add(this.cancelButton);

        JPanel result = new JPanel(new BorderLayout());
        result.add(mainPanel);
        result.add(buttons, BorderLayout.SOUTH);
        return result;
    }

    /** The option pane creating the dialog. */
    private JOptionPane pane;

    /**
     * @return the title of the dialog
     */
    private String createTitle() {
        return DIALOG_TITLE;
    }

    private JPanel createFlagPanel(Flag flag) {
        JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));
        result.add(getFlagCheckBox(flag));
        result.add(getFlagTextField(flag));
        result.add(Box.createHorizontalStrut(PREF_RIGHT.width));
        return result;
    }

    /**
     * Constructs and returns the directory field.
     */
    private JTextField getDirField() {
        if (this.dirField == null) {
            final JTextField result = this.dirField = new JTextField(this.currentDirectory);
            result.setColumns(20);
            result
                .getDocument()
                .addDocumentListener(new DirectoryFieldListener(result,
                    "Destination must be a valid directory"));
        }
        return this.dirField;
    }

    /** directory to export to */
    private JTextField dirField;

    /**
     * Constructs and returns the LTS name pattern field.
     */
    private JTextField getLTSPatternField() {
        if (this.ltsPatternField == null) {
            final JTextField result
                = this.ltsPatternField = new JTextField(getPref(LTS_PATTERN_ENTRY));
            result
                .getDocument()
                .addDocumentListener(new EmptyFieldListener(result,
                    "LTS name pattern should not be empty"));
        }
        return this.ltsPatternField;
    }

    /** directory to export to */
    private JTextField ltsPatternField;

    /**
     * Constructs and returns the LTS name pattern field.
     */
    private JTextField getStatePatternField() {
        if (this.statePatternField == null) {
            final JTextField result
                = this.statePatternField = new JTextField(getPref(STATE_PATTERN_ENTRY));
            result
                .getDocument()
                .addDocumentListener(new PlaceholderFieldListener(result,
                    String.format("State name pattern should contain '%s'", PLACEHOLDER)));
        }
        return this.statePatternField;
    }

    /** directory to export to */
    private JTextField statePatternField;

    private JRadioButton getExportButton(StateExport mode) {
        if (this.exportButtonMap == null) {
            this.exportButtonMap = computeExportButtonMap();
        }
        return this.exportButtonMap.get(mode);
    }

    private Map<StateExport,JRadioButton> computeExportButtonMap() {
        Map<StateExport,JRadioButton> result = new EnumMap<>(StateExport.class);
        ButtonGroup group = new ButtonGroup();
        for (StateExport mode : StateExport.values()) {
            String text = switch (mode) {
            case TOP -> "Top-level states";
            case ALL -> "All states (including transient ones)";
            case FINAL -> "Final states";
            case NONE -> "None";
            case RESULT -> "Result states";
            default -> throw Exceptions.UNREACHABLE;
            };
            JRadioButton button
                = new JRadioButton(text, getPref(STATE_EXPORT_ENTRY).equals(mode.name()));
            result.put(mode, button);
            group.add(button);
        }
        return result;
    }

    private Map<StateExport,JRadioButton> exportButtonMap;

    private JCheckBox getFlagCheckBox(Flag flag) {
        if (this.flagCheckMap == null) {
            this.flagCheckMap = computeFlagCheckMap();
        }
        return this.flagCheckMap.get(flag);
    }

    private Map<Flag,JCheckBox> computeFlagCheckMap() {
        Map<Flag,JCheckBox> result = new EnumMap<>(Flag.class);
        for (Flag flag : Flag.values()) {
            String text = null;
            String tip = null;
            switch (flag) {
            case FINAL:
                text = "Mark final states with:";
                tip = "If ticked, all final states will be labelled";
                break;
            case NUMBER:
                text = "Number all states with:";
                tip = String
                    .format("If ticked, all states will be labelled, with '%s' replaced by the state number",
                            PLACEHOLDER);
                break;
            case TRANSIENT:
                text = "Mark transient states with:";
                tip = String
                    .format("If ticked, transient states will be labelled, "
                        + "with '%s' replaced by the transient depth", PLACEHOLDER);
                break;
            case OPEN:
                text = "Mark open states with:";
                tip = "If ticked, all open states will be labelled";
                break;
            case RESULT:
                text = "Mark result states with:";
                tip = "If ticked, all result states will be labelled";
                break;
            case RECIPE:
                text = "Mark recipe stages with:";
                tip = String
                    .format("If ticked, recipe stages will included and optionally labelled, "
                        + "with '%s' replaced by the recipe name", PLACEHOLDER);
                break;
            case START:
                text = "Mark start state with:";
                tip = "If ticked, the start state will be labelled";
                break;
            default:
                assert false;
            }
            final JCheckBox checkBox = new JCheckBox(text);
            checkBox.setPreferredSize(LARGE_LEFT);
            checkBox.setToolTipText(tip);
            final JTextField textField = getFlagTextField(flag);
            checkBox.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    textField.setEnabled(checkBox.isSelected());
                    textField.setEditable(checkBox.isSelected());
                }
            });
            checkBox.setSelected(getBoolPref(FLAG_SELECT_ENTRY.get(flag)));
            result.put(flag, checkBox);
        }
        return result;
    }

    private Map<LTSLabels.Flag,JCheckBox> flagCheckMap;

    private JTextField getFlagTextField(Flag flag) {
        if (this.flagTextMap == null) {
            this.flagTextMap = computeFlagTextMap();
        }
        return this.flagTextMap.get(flag);
    }

    private Map<Flag,JTextField> computeFlagTextMap() {
        Map<Flag,JTextField> result = new EnumMap<>(Flag.class);
        for (Flag flag : Flag.values()) {
            JTextField textField = new JTextField(getPref(FLAG_TEXT_ENTRY.get(flag)));
            FieldListener listener = null;
            switch (flag) {
            case FINAL:
            case OPEN:
            case RESULT:
            case START:
            case TRANSIENT:
                String message = flag.getDescription() + " label must be non-empty";
                listener = new EmptyFieldListener(textField, message);
                break;
            case NUMBER:
                message = String
                    .format("%s label must contain placeholder '%s'", flag.getDescription(),
                            PLACEHOLDER);
                listener = new PlaceholderFieldListener(textField, message);
                break;
            case RECIPE:
                // no listener
                break;
            default:
                assert false;
            }
            if (listener != null) {
                textField.getDocument().addDocumentListener(listener);
            }
            textField.setEnabled(false);
            textField.setEditable(false);
            result.put(flag, textField);
        }
        return result;
    }

    private Map<LTSLabels.Flag,JTextField> flagTextMap;

    private void error(JComponent source, String message) {
        if (message == null) {
            this.errors.remove(source);
        } else {
            this.errors.put(source, message);
        }
        if (this.errors.isEmpty()) {
            getErrorLabel().setText(" ");
        } else {
            String current = this.errors.values().iterator().next();
            getErrorLabel().setText(current);
        }
        getOkButton().setEnabled(this.errors.isEmpty());
    }

    private Map<JComponent,String> errors = new LinkedHashMap<>();

    private JLabel getErrorLabel() {
        if (this.errorLabel == null) {
            this.errorLabel = new JLabel(" ");
            this.errorLabel.setForeground(Color.RED);
            this.errorLabel.setAlignmentX(0.5f);
        }
        return this.errorLabel;
    }

    private JLabel errorLabel;

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

    /** The OK button on the option pane. */
    private JButton okButton;

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

    /** The CANCEL button on the option pane. */
    private JButton cancelButton;

    /** Returns the current selection for exporting the individual states. */
    public StateExport getExportStates() {
        for (StateExport result : StateExport.values()) {
            if (getExportButton(result).isSelected()) {
                return result;
            }
        }
        return StateExport.NONE;
    }

    /** Returns an absolute path of the directory to export to. */
    public String getDirectory() {
        return getDirField().getText();
    }

    /** Returns an absolute path of the directory to export to. */
    public String getLtsPattern() {
        return getLTSPatternField().getText();
    }

    /** Returns an absolute path of the directory to export to. */
    public String getStatePattern() {
        return getStatePatternField().getText();
    }

    /** Returns the LTS labelling specification. */
    public LTSLabels getLTSLabels() {
        EnumMap<Flag,String> flags = new EnumMap<>(Flag.class);
        for (Flag flag : Flag.values()) {
            if (getFlagCheckBox(flag).isSelected()) {
                flags.put(flag, getFlagTextField(flag).getText());
            }
        }
        return new LTSLabels(flags);
    }

    @Override
    public void sync() {
        putPref(LTS_PATTERN_ENTRY, getLtsPattern());
        putPref(STATE_PATTERN_ENTRY, getStatePattern());
        for (Flag flag : Flag.values()) {
            putPref(FLAG_SELECT_ENTRY.get(flag), getFlagCheckBox(flag).isSelected());
            putPref(FLAG_TEXT_ENTRY.get(flag), getFlagTextField(flag).getText());
        }
        putPref(STATE_EXPORT_ENTRY, getExportStates().name());
    }

    @Override
    public Preferences getPrefs() {
        return userPrefs;
    }

    /** User preferences object for this class. */
    static private final Preferences userPrefs
        = Preferences.userNodeForPackage(SaveLTSAsDialog.class);
    /** User preferences entry for the state filename pattern. */
    static private final Entry STATE_PATTERN_ENTRY
        = new Entry("state-pattern", "s" + PLACEHOLDER + ".gst");
    /** User preferences entry for the LTS filename pattern. */
    static private final Entry LTS_PATTERN_ENTRY = new Entry("lts-pattern", PLACEHOLDER + ".gxl");
    /** User preferences entry for the state export value. */
    static private final Entry STATE_EXPORT_ENTRY = new Entry("state-export", StateExport.NONE);
    /** User preferences entry for the selected flags. */
    static private final Map<Flag,Entry> FLAG_SELECT_ENTRY = new EnumMap<>(Flag.class);
    /** User preferences entry for the flag names. */
    static private final Map<Flag,Entry> FLAG_TEXT_ENTRY = new EnumMap<>(Flag.class);
    static {
        for (Flag flag : Flag.values()) {
            FLAG_SELECT_ENTRY
                .put(flag, new Entry(flag.name() + ".selected", LTSLabels.DEFAULT.hasFlag(flag)));
            FLAG_TEXT_ENTRY.put(flag, new Entry(flag.name() + ".text", flag.getDefault()));
        }
    }

    private final class PlaceholderFieldListener extends FieldListener {
        private PlaceholderFieldListener(JTextField field, String message) {
            super(field, message);
        }

        @Override
        boolean hasError(String text) {
            return text.indexOf(PLACEHOLDER) < 0;
        }
    }

    private final class EmptyFieldListener extends FieldListener {
        private EmptyFieldListener(JTextField field, String message) {
            super(field, message);
        }

        @Override
        boolean hasError(String text) {
            return text.isEmpty();
        }
    }

    private final class DirectoryFieldListener extends FieldListener {
        private DirectoryFieldListener(JTextField field, String message) {
            super(field, message);
        }

        @Override
        boolean hasError(String text) {
            return !new File(text).isDirectory();
        }
    }

    class BrowseButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = GrooveFileChooser.getInstance();
            var dir = this.lastChoice == null
                ? SaveLTSAsDialog.this.currentDirectory
                : this.lastChoice;
            chooser.setCurrentDirectory(new File(dir));
            int result = chooser.showOpenDialog(SaveLTSAsDialog.this.simulator.getFrame());
            // now load, if so required
            if (result == JFileChooser.APPROVE_OPTION) {
                this.lastChoice = dir = chooser.getSelectedFile().getAbsolutePath();
                SaveLTSAsDialog.this.dirField.setText(dir);
            }
            if (result == JFileChooser.CANCEL_OPTION) {
                // System.out.println("Cancelled");
            }
            if (result == JFileChooser.ERROR_OPTION) {
                // System.out.println("Whooops");
            }
        }

        /** Last actively chosen save directory. */
        private String lastChoice;
    }

    private static final Dimension PREF_LEFT = new Dimension(125, 0);
    private static final Dimension LARGE_LEFT = new Dimension(150, 0);
    private static final Dimension PREF_RIGHT = new Dimension(50, 0);

    /**
     * @author Arend Rensink
     * @version $Revision$
     */
    abstract private class FieldListener implements DocumentListener {
        private final JTextField field;
        private final String message;

        private FieldListener(JTextField field, String message) {
            this.field = field;
            this.message = message;
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            checkError();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            checkError();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            // do nothing
        }

        private void checkError() {
            String message = null;
            if (hasError(this.field.getText())) {
                message = this.message;
            }
            error(this.field, message);
        }

        /** Checks if the error message should be displayed. */
        abstract boolean hasError(String text);
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

        @Override
        public void actionPerformed(ActionEvent e) {
            Object value = e.getSource();
            getContentPane().setValue(value);
            getContentPane().setVisible(false);
            // ExportDialog.this.dialog.setVisible(false);
            ToolTipManager.sharedInstance().registerComponent(SaveLTSAsDialog.this.pane);

        }
    }

    /** State export mode. */
    public static enum StateExport {
        /** Export no states. */
        NONE,
        /** Export all states. */
        ALL,
        /** Export only top-level states. */
        TOP,
        /** Export final states. */
        FINAL,
        /** Export result states. */
        RESULT,;
    }

    /** Returns the singleton instance of this dialog. */
    static public SaveLTSAsDialog instance() {
        return INSTANCE;
    }

    /** The singleton instance of this dialog. */
    static private final SaveLTSAsDialog INSTANCE = new SaveLTSAsDialog();
    static {
        UserSettings.register(INSTANCE);
    }
}
