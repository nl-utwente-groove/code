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
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * @author Eduardo Zambon
 * @version $Revision$
 */
@NonNullByDefault
public class BugReportDialog extends JDialog implements ActionListener, HyperlinkListener {

    private static final String DIALOG_TITLE = "Uncaught Exception in GROOVE";

    private static final String CANCEL_COMMAND = "Close GROOVE";

    private static final String ERROR_MSG = "<HTML><BODY>"
        + "Oops, it seems that GROOVE just crashed on you. Sorry...<BR>"
        + "This undesired behaviour was probably caused by a bug in the code.<BR>"
        + "Please help the developers to improve the tool by submitting a "
        + "<I>Bug Report</i> at the GROOVE project page on Github: "
        + "<A HREF=\"https://github.com/nl-utwente-groove/code/issues\">https://github.com/nl-utwente-groove/code/issues</A><BR>"
        + "In the link given, select 'New Issue' to create a new entry.<BR>"
        + "While submitting your report please describe the steps that led "
        + "to the crash and include the exception stack trace shown below." + "</BODY></HTML>";

    /** Width of the message and stack trace panes, in pixels. */
    private static final int PANE_WIDTH = 700;

    /**
     * Create a bug reporting dialog.
     * @param e the exception that caused the bug.
     */
    public BugReportDialog(Throwable e) {
        super((JFrame) null, DIALOG_TITLE, true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // Create the content panel: the message on top, the stack trace in the
        // middle and the button below. The stack trace pane takes up any extra
        // space when the dialog is resized.
        // Add an empty space of 10 pixels between the dialog and the content
        // panel.
        JPanel dialogContent = new JPanel(new BorderLayout(0, 10));
        dialogContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Make sure that closeDialog is called whenever the dialog is closed.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                closeDialog();
            }
        });

        // Fill the dialog.
        dialogContent.add(this.getErrorMessage(), BorderLayout.NORTH);
        dialogContent.add(this.getStackTracePane(e), BorderLayout.CENTER);
        dialogContent.add(this.getButtonPanel(), BorderLayout.SOUTH);

        // Add the dialogContent to the dialog.
        add(dialogContent);
        pack();
        setVisible(true);
    }

    private JEditorPane getErrorMessage() {
        JEditorPane errorMsg = new JEditorPane("text/html", ERROR_MSG);
        errorMsg.setEditable(false);
        errorMsg.setOpaque(false);
        // Render the HTML in the look-and-feel's message font, rather than in
        // the default style sheet of the HTML kit
        errorMsg.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        Font font = UIManager.getFont("OptionPane.messageFont");
        if (font != null) {
            errorMsg.setFont(font);
        }
        errorMsg.addHyperlinkListener(this);
        // Wrap the text at the pane width, and take the height that results
        errorMsg.setSize(PANE_WIDTH, Integer.MAX_VALUE);
        errorMsg
            .setPreferredSize(new Dimension(PANE_WIDTH, errorMsg.getPreferredSize().height));
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
        // Text font: monospaced, at the size of the look-and-feel's text font
        Font textFont = UIManager.getFont("TextPane.font");
        int fontSize = textFont == null
            ? 12
            : textFont.getSize();
        stackTracePane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, fontSize));
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
        scrollPane.setPreferredSize(new Dimension(PANE_WIDTH, 300));
        scrollPane
            .setBorder(BorderFactory
                .createTitledBorder(null, "Exception Stack Trace:",
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
        if (event.getActionCommand().equals(CANCEL_COMMAND)) {
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
                Desktop.getDesktop().browse(evt.getURL().toURI());
            } catch (Exception e) {
                // Silently fail if we can't open a web-browser.
            }
        }
    }

    private void closeDialog() {
        this.dispose();
    }
}
