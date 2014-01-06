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

import groove.explore.util.LTSLabels;
import groove.explore.util.LTSLabels.Flag;
import groove.gui.Simulator;
import groove.io.FileType;
import groove.io.GrooveFileChooser;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.EnumSet;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * @author Tom Staijen
 * @version $Revision $
 */
public class SaveLTSAsDialog {

    /** The current Grammar Directory */
    private String currentDirectory;

    /** The OK button on the option pane. */
    private JButton okButton;
    /** The CANCEL button on the option pane. */
    private JButton cancelButton;

    /** Title of the dialog. */
    public static final String DIALOG_TITLE = "Save LTS As";

    /** The option pane creating the dialog. */
    private JOptionPane pane;
    /** The dialog */
    private JDialog dialog;
    /** The simulator, for fetching the frame instance */
    private Simulator simulator;

    /** directory to export to */
    private JTextField dirField;
    /** label start states */
    private JCheckBox startCheck;
    /** label final states */
    private JCheckBox finalCheck;
    /** label result states */
    private JCheckBox resultCheck;
    /** label open states */
    private JCheckBox openCheck;
    /** add state names */
    private JCheckBox nameCheck;

    /** export no states */
    JRadioButton noExportButton;
    /** export all states */
    JRadioButton allExportButton;
    /** export result states only */
    JRadioButton resultExportButton;
    /** export final states only */
    JRadioButton finalExportButton;

    /** Creates a new dialog for options to export the LTS * */
    public SaveLTSAsDialog(Simulator simulator) {
        // nothing to do
    }

    /** Sets the directory to initialise the file directory browser at * */
    public void setCurrentDirectory(String value) {
        this.currentDirectory = value;
    }

    /**
     * Shows the dialog. The passed frame is locked until the dialog is closed.
     * Returns true if the dialog was closed with ok, false in case of cancel.
     */
    public boolean showDialog(Simulator simulator) {
        this.simulator = simulator;
        this.getContentPane().setVisible(true);
        this.dialog =
            getContentPane().createDialog(simulator.getFrame(), createTitle());
        this.dialog.setVisible(true);
        return (getContentPane().getValue() == getOkButton());
    }

    /**
     * @return the contentpane
     */
    JOptionPane getContentPane() {
        Object[] buttons = new Object[] {getOkButton(), getCancelButton()};
        if (this.pane == null) {
            this.pane =
                new JOptionPane(createPanel(), JOptionPane.PLAIN_MESSAGE,
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

        JPanel filePanel = new JPanel();
        filePanel.setBorder(new TitledBorder(new EtchedBorder(), "Destination"));
        filePanel.setLayout(new FlowLayout());
        this.dirField =
            new JTextField(new File(this.currentDirectory, "#.gxl").toString());
        this.dirField.setColumns(25);
        filePanel.add(this.dirField);
        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(new BrowseButtonListener());
        filePanel.add(browseButton);
        filePanel.setToolTipText("Filename pattern: '#' is replaced by grammar ID, file extension determines file format");
        mainPanel.add(filePanel);

        JPanel labelPanel = new JPanel(new GridLayout(0, 1));
        labelPanel.setBorder(new TitledBorder(new EtchedBorder(),
            "Label options"));
        this.startCheck = new JCheckBox("Mark start state");
        this.startCheck.setToolTipText(String.format(
            "If ticked, the start state will be labelled '%s'",
            LTSLabels.Flag.START.getDefault()));
        this.finalCheck = new JCheckBox("Mark final states");
        this.finalCheck.setToolTipText(String.format(
            "If ticked, all final states will be labelled '%s'",
            LTSLabels.Flag.FINAL.getDefault()));
        this.resultCheck = new JCheckBox("Mark result states");
        this.resultCheck.setToolTipText(String.format(
            "If ticked, all result states will be labelled '%s'",
            LTSLabels.Flag.RESULT.getDefault()));
        this.openCheck = new JCheckBox("Mark open states");
        this.openCheck.setToolTipText(String.format(
            "If ticked, all open states will be labelled '%s'",
            LTSLabels.Flag.OPEN.getDefault()));
        this.nameCheck = new JCheckBox("Number all states");
        this.nameCheck.setToolTipText(String.format(
            "If ticked, all states will be labelled '%s', with '#' replaced by the state number",
            LTSLabels.Flag.NUMBER.getDefault()));

        labelPanel.add(this.startCheck);
        labelPanel.add(this.finalCheck);
        labelPanel.add(this.resultCheck);
        labelPanel.add(this.openCheck);
        labelPanel.add(this.nameCheck);
        mainPanel.add(labelPanel);

        JPanel savePanel = new JPanel(new GridLayout(0, 1));
        savePanel.setBorder(new TitledBorder(new EtchedBorder(), "Save states"));
        savePanel.setToolTipText("Select which states should be saved along with the LTS (in the same directory)");
        this.noExportButton = new JRadioButton("None", true);
        this.allExportButton = new JRadioButton("All states", false);
        this.resultExportButton = new JRadioButton("Result states", false);
        this.finalExportButton = new JRadioButton("Final states", false);

        ButtonGroup group = new ButtonGroup();
        group.add(this.noExportButton);
        group.add(this.allExportButton);
        group.add(this.resultExportButton);
        group.add(this.finalExportButton);

        savePanel.add(this.noExportButton);
        savePanel.add(this.allExportButton);
        savePanel.add(this.resultExportButton);
        savePanel.add(this.finalExportButton);

        mainPanel.add(savePanel);

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
            Object value = e.getSource();
            getContentPane().setValue(value);
            getContentPane().setVisible(false);
            // ExportDialog.this.dialog.setVisible(false);
            ToolTipManager.sharedInstance().registerComponent(
                SaveLTSAsDialog.this.pane);

        }
    }

    /** Retuns the current selection for exporting the individual states * */
    public StateExport getExportStates() {
        if (this.noExportButton.isSelected()) {
            return StateExport.NONE;
        } else if (this.allExportButton.isSelected()) {
            return StateExport.ALL;
        } else if (this.resultExportButton.isSelected()) {
            return StateExport.RESULT;
        } else if (this.finalExportButton.isSelected()) {
            return StateExport.FINAL;
        }
        return StateExport.NONE;
    }

    /** Returns an absolute path of the directory to export to. */
    public String getFile() {
        return this.dirField.getText();
    }

    /** Returns the LTS labelling specification. */
    public LTSLabels getLTSLabels() {
        EnumSet<LTSLabels.Flag> flags = EnumSet.noneOf(LTSLabels.Flag.class);
        if (this.openCheck.isSelected()) {
            flags.add(Flag.OPEN);
        }
        if (this.finalCheck.isSelected()) {
            flags.add(Flag.FINAL);
        }
        if (this.startCheck.isSelected()) {
            flags.add(Flag.START);
        }
        if (this.nameCheck.isSelected()) {
            flags.add(Flag.NUMBER);
        }
        return new LTSLabels(flags.toArray(new LTSLabels.Flag[0]));
    }

    /** Returns if open states should be labelled with "open". */
    public boolean showOpen() {
        return this.openCheck.isSelected();
    }

    /** Returns if final states should be labelled with "final". */
    public boolean showFinal() {
        return this.finalCheck.isSelected();
    }

    /** Returns if states should be labelled with their name. */
    public boolean showNames() {
        return this.nameCheck.isSelected();
    }

    /** Returns if the start state should be labelled with "start". */
    public boolean showStart() {
        return this.startCheck.isSelected();
    }

    class BrowseButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = GrooveFileChooser.getInstance(FileType.GXL);
            int result =
                chooser.showOpenDialog(SaveLTSAsDialog.this.simulator.getFrame());
            // now load, if so required
            if (result == JFileChooser.APPROVE_OPTION) {
                SaveLTSAsDialog.this.dirField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
            if (result == JFileChooser.CANCEL_OPTION) {
                // System.out.println("Cancelled");
            }
            if (result == JFileChooser.ERROR_OPTION) {
                // System.out.println("Whooops");
            }

        }
    }

    /** State export mode. */
    public static enum StateExport {
        /** Export no states. */
        NONE,
        /** Export all states. */
        ALL,
        /** Export final states. */
        FINAL,
        /** Export result states. */
        RESULT, ;
    }
}
