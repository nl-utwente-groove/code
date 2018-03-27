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
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import groove.gui.layout.SpringUtilities;

/**
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class BugReportDialog extends JDialog implements ActionListener, HyperlinkListener {

    private static final String DIALOG_TITLE = "Uncaught Exception in GROOVE";

    private static final String CANCEL_COMMAND = "Close GROOVE";

    private static final String ERROR_MSG = "<HTML><BODY><FONT FACE=\"Arial\", SIZE=4>"
        + "Oops, it seems that GROOVE just crashed on you. Sorry...<BR>"
        + "This undesired behaviour was probably caused by a bug in the code.<BR>"
        + "Please help the developers to improve the tool by submitting a "
        + "<I>Bug Report</i> at the GROOVE project page on SourceForge: "
        + "<A HREF=\"http://sourceforge.net/projects/groove/develop\">http://sourceforge.net/projects/groove/develop</A><BR>"
        + "In the link given, select menu 'Tickets' and option 'Bugs' to "
        + "create a new entry.<BR>"
        + "While submitting your report please describe the steps that led "
        + "to the crash and include the exception stack trace shown below." + "</FONT></HTML>";

    /**
     * Create a bug reporting dialog.
     * @param e the exception that caused the bug.
     */
    public BugReportDialog(Throwable e) {
        super((JFrame) null, DIALOG_TITLE, true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);

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
        dialogContent.add(this.getErrorMessage());
        dialogContent.add(this.getStackTracePane(e));
        dialogContent.add(this.getButtonPanel());

        // Put the panels in a CompactGrid layout.
        SpringUtilities.makeCompactGrid(dialogContent, 3, 1, 0, 0, 0, 0);

        // Add the dialogContent to the dialog.
        add(dialogContent);
        pack();
        setVisible(true);
    }

    private JEditorPane getErrorMessage() {
        JEditorPane errorMsg = new JEditorPane();

        errorMsg.setEditable(false);
        errorMsg.setPreferredSize(new Dimension(700, 130));
        errorMsg.setBackground(null);
        // Text font
        Font font = new Font("Sans", Font.PLAIN, 6);
        errorMsg.setFont(font);
        // Handle HTML
        errorMsg.setEditorKit(new HTMLEditorKit());
        errorMsg.addHyperlinkListener(this);

        errorMsg.setText(ERROR_MSG);

        return errorMsg;
    }

    /**
     * Creates the pane with the stack trace of an exception.
     * @param e the exception to be shown in the pane.
     * @return the pane object.
     */
    private JScrollPane getStackTracePane(Throwable e) {
        // Create a text pane
        JTextPane stackTracePane = new JTextPane();
        stackTracePane.setEditable(false);
        // Text font
        Font font = new Font("Serif", Font.PLAIN, 12);
        stackTracePane.setFont(font);
        // Get the message and the stack trace from the exception and put them
        // in text pane.
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        stackTracePane.setText("Exception in GROOVE " + sw.toString());

        // Extra panel to prevent wrapping of the exception message.
        JPanel noWrapPanel = new JPanel(new BorderLayout());
        noWrapPanel.add(stackTracePane);

        // Pane to create the scroll bars.
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(700, 300));
        scrollPane.setBorder(BorderFactory.createTitledBorder(null,
            "Exception Stack Trace:",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION));
        scrollPane.setViewportView(noWrapPanel);

        return scrollPane;
    }

    /**
     * Create the button panel.
     */
    private JPanel getButtonPanel() {
        JPanel buttonPanel = new JPanel();

        JButton cancelButton = new JButton(CANCEL_COMMAND);
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    /**
     * The action listener of the dialog.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand()
            .equals(CANCEL_COMMAND)) {
            this.closeDialog();
        }
    }

    /**
     * Listener to hyper-link clicks.
     */
    @Override
    public void hyperlinkUpdate(HyperlinkEvent evt) {
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                Desktop.getDesktop()
                    .browse(evt.getURL()
                        .toURI());
            } catch (Exception e) {
                // Silently fail if we can't open a web-browser.
            }
        }
    }

    private void closeDialog() {
        this.dispose();
    }
}
