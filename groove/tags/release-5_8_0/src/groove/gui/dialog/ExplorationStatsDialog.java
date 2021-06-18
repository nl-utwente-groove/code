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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import groove.gui.Simulator;
import groove.gui.layout.SpringUtilities;

/**
 * Dialog for showing the statistics of the last state space exploration.
 * @author Eduardo Zambon
 */
public class ExplorationStatsDialog extends JDialog implements ActionListener {

    private static String CLOSE_COMMAND = "Close";
    private static String DIALOG_TITLE = "Exploration Statistics";
    private static String STATS_HEADER_TEXT = "Statistics of last state space exploration: ";

    private Simulator simulator;

    /** Creates the dialog. */
    public ExplorationStatsDialog(Simulator simulator, JFrame parent) {
        super(parent, DIALOG_TITLE, true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        this.simulator = simulator;

        // Create the content panel, which is laid out as a single column.
        // Add an empty space of 10 pixels between the dialog and the content
        // panel.
        JPanel dialogContent = new JPanel(new SpringLayout());
        dialogContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Make sure that closeDialog is called whenever the dialog is closed.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                closeDialog();
            }
        });

        // Fill the dialog.
        dialogContent.add(this.getInfoPane());
        dialogContent.add(this.getButtonPanel());

        // Put the panels in a CompactGrid layout.
        SpringUtilities.makeCompactGrid(dialogContent, 2, 1, 0, 0, 0, 0);

        // Add the dialogContent to the dialog.
        add(dialogContent);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private JScrollPane getInfoPane() {
        // Create a text pane
        JTextArea infoPane = new JTextArea();
        infoPane.setEditable(false);
        infoPane.setBackground(Color.WHITE);
        // Text font
        Font font = new Font("Lucida Sans Typewriter", Font.PLAIN, 12);
        infoPane.setFont(font);
        infoPane.setTabSize(4);
        // Get the message and the stack trace from the exception and put them
        // in text pane.

        String info = this.simulator.getModel()
            .getExplorationStats()
            .getReport();
        infoPane.setText(info);

        // Pane to create the scroll bars.
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(700, 500));
        scrollPane.setBorder(BorderFactory.createTitledBorder(null,
            STATS_HEADER_TEXT,
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION));
        scrollPane.setViewportView(infoPane);

        return scrollPane;
    }

    /**
     * Create the button panel.
     */
    private JPanel getButtonPanel() {
        JPanel buttonPanel = new JPanel();

        JButton closeButton = new JButton(CLOSE_COMMAND);
        closeButton.addActionListener(this);
        buttonPanel.add(closeButton);

        return buttonPanel;
    }

    /**
     * The action listener of the dialog.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand()
            .equals(CLOSE_COMMAND)) {
            this.closeDialog();
        }
    }

    private void closeDialog() {
        this.dispose();
    }
}
