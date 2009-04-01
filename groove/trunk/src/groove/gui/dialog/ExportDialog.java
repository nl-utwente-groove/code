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
import groove.io.ExtensionFilter;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * @author Tom Staijen
 * @version $Revision $
 */
public class ExportDialog {

    /** The current Grammar Directory */
    private String currentDirectory;

    /** The OK button on the option pane. */
    private JButton okButton;
    /** The CANCEL button on the option pane. */
    private JButton cancelButton;

    /** Title of the dialog. */
    public static final String DIALOG_TITLE = "LTS Exporter";

    /** The option pane creating the dialog. */
    private JOptionPane pane;
    /** The dialog */
    private JDialog dialog;
    /** The simulator, for fetching the frame instance */
    private Simulator simulator;

    // directory to export to
    private JTextField dirField;
    // label start states
    private JCheckBox startCheck;
    // label final states
    private JCheckBox finalCheck;
    // label open states
    private JCheckBox openCheck;
    // add state names
    private JCheckBox nameCheck;

    // export no states
    JRadioButton list0;
    // export all states
    JRadioButton list1;
    // export final states only
    JRadioButton list2;

    /** Value that indicates no states are exported **/ 
    public static final int STATES_NONE = 0;
    /** Value that indicates only final states are exported **/
    public static final int STATES_FINAL = 1;
    /** Value that indicates all states are exported **/
    public static final int STATES_ALL = 2;

    /** Creates a new dialog for options to export the LTS * */
    public ExportDialog(Simulator simulator) {
        // nothing to do
    }

    /** Sets the directoy to initialize the file directoy browser at * */
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
        JPanel result = new JPanel();
        JPanel panel = new JPanel();
        JPanel buttons = new JPanel();

        panel.setLayout(new GridLayout(0, 1));

        // editing formula

        JLabel exportLabel = new JLabel("Exporting directory:");
        panel.add(exportLabel);

        JPanel filePanel = new JPanel();
        filePanel.setLayout(new FlowLayout());
        this.dirField = new JTextField(this.currentDirectory);
        filePanel.add(this.dirField);
        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(new BrowseButtonListener());
        filePanel.add(browseButton);
        panel.add(filePanel);

        panel.add(new JLabel(" "));
        panel.add(new JLabel("LTS Exporting options:"));

        this.startCheck = new JCheckBox("Label start state");
        this.finalCheck = new JCheckBox("Label final states");
        this.openCheck = new JCheckBox("Label open states");
        this.nameCheck = new JCheckBox("Export state names");

        panel.add(this.startCheck);
        panel.add(this.finalCheck);
        panel.add(this.openCheck);
        panel.add(this.nameCheck);

        panel.add(new JLabel(" "));
        panel.add(new JLabel("Export States:"));

        this.list0 = new JRadioButton("None", true);
        this.list1 = new JRadioButton("All states", false);
        this.list2 = new JRadioButton("Final states", false);

        ButtonGroup group = new ButtonGroup();
        group.add(this.list0);
        group.add(this.list1);
        group.add(this.list2);

        panel.add(this.list0);
        panel.add(this.list1);
        panel.add(this.list2);

        panel.add(new JLabel(" "));

        // OK or CANCEL
        this.okButton = getOkButton();
        this.cancelButton = getCancelButton();
        buttons.add(this.okButton);
        buttons.add(this.cancelButton);

        result.add(panel);
        result.add(buttons);
        result.add(panel);

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

        }
    }

    /** Retuns the current selection for exporting the individual states * */
    public int getExportStates() {
        if (this.list0.isSelected()) {
            return STATES_NONE;
        } else if (this.list1.isSelected()) {
            return STATES_ALL;
        } else if (this.list2.isSelected()) {
            return STATES_FINAL;
        }
        return -1;
    }

    /** Returns an absolute path of the directory to export to * */
    public String getDirectory() {
        return this.dirField.getText();
    }

    /** Returns if open states should be labeled with "open" * */
    public boolean showOpen() {
        return this.openCheck.isSelected();
    }

    /** Returns if final states should be labeled with "final" * */
    public boolean showFinal() {
        return this.finalCheck.isSelected();
    }

    /** Returns if states should be labeled with their name * */
    public boolean showNames() {
        return this.nameCheck.isSelected();
    }

    /** Returns if the start state should be labeled with "start" * */
    public boolean showStart() {
        return this.startCheck.isSelected();
    }

    class BrowseButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser =
                new JFileChooser(ExportDialog.this.dirField.getText());
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int result =
                chooser.showOpenDialog(ExportDialog.this.simulator.getFrame());
            // now load, if so required
            if (result == JFileChooser.APPROVE_OPTION) {
                ExportDialog.this.dirField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
            if (result == JFileChooser.CANCEL_OPTION) {
                System.out.println("Cancelled");
            }
            if (result == JFileChooser.ERROR_OPTION) {
                System.out.println("Whooops");
            }

        }
    }

}
