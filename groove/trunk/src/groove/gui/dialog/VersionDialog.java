/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.gui.dialog;

import groove.grammar.GrammarProperties;
import groove.io.HTMLConverter;
import groove.util.Version;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

/**
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class VersionDialog {

    /**
     * Shows the dialog for loading an newer grammar version.
     * @param parent the parent frame
     * @param grammarProperties the properties of the grammar being loaded
     * @return true if loading should continue, false otherwise
     */
    public static boolean showNew(Component parent,
            GrammarProperties grammarProperties) {
        String msg =
            "<HTML><FONT color=#770000>Warning: </FONT>"
                + "loading grammar from newer GROOVE version (<FONT color=#000077>"
                + grammarProperties.getGrooveVersion() + "</FONT>, current is "
                + "<FONT color=#000077>" + Version.getCurrentGrooveVersion()
                + "</FONT>).\nOpening this grammar may cause errors. "
                + "Continue anyway?\n\n";
        int buttonPressed =
            JOptionPane.showConfirmDialog(parent, msg,
                "Warning: loading new grammar", JOptionPane.YES_NO_OPTION);
        return buttonPressed == JOptionPane.YES_OPTION;
    }

    /**
     * Shows the dialog for loading an older grammar version.
     * @param parent the parent frame
     * @param grammarProperties the properties of the grammar being loaded
     * @return 0 if loading should continue, overwriting the current file,
     *         1 if loading should continue, creating a new local grammar, and
     *         -1 otherwise
     */
    public static int showOldFile(Component parent,
            GrammarProperties grammarProperties) {
        StringBuilder msg = new StringBuilder(HTMLConverter.HTML_TAG.tagBegin);
        msg.append("<FONT color=#770000>Warning: </FONT>");
        if (grammarProperties.getGrooveVersion().equals("0.0.0")) {
            msg.append("loading unrecognized grammar version "
                + "(absent or outdated system properties file).\n");
        } else {
            msg.append("loading grammar from old GROOVE version (<FONT color=#000077>"
                + grammarProperties.getGrooveVersion()
                + "</FONT>, current is "
                + "<FONT color=#000077>"
                + Version.getCurrentGrooveVersion()
                + "</FONT>).\n");
        }
        msg.append("Loading this grammar will automatically convert "
            + "it to the current version.");
        String overwrite_text = "Convert";
        String save_as_text = "Convert As";
        String cancel_text = "Cancel";
        String[] options = {overwrite_text, save_as_text, cancel_text};
        switch (JOptionPane.showOptionDialog(parent, msg.toString().toString(),
            "Warning: loading old grammar", JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE, null, options, overwrite_text)) {
        case JOptionPane.YES_OPTION:
            return 0;
        case JOptionPane.NO_OPTION:
            return 1;
        default:
            return -1;
        }
    }

    /**
     * Shows the dialog for loading an older zipped/url grammar version.
     * @param parent the parent frame
     * @param grammarProperties the properties of the grammar being loaded
     * @return true if loading should continue, creating a new local grammar,
     *         false if loading should be canceled
     */
    public static boolean showOldURL(Component parent,
            GrammarProperties grammarProperties) {
        String msg =
            "<HTML><FONT color=#770000>Warning: </FONT>"
                + "loading grammar from old GROOVE version (<FONT color=#000077>"
                + grammarProperties.getGrooveVersion() + "</FONT>, current is "
                + "<FONT color=#000077>" + Version.getCurrentGrooveVersion()
                + "</FONT>).\nLoading this grammar will automatically convert "
                + "it (as a new grammar) to the current version.\n\n";
        String save_as_text = "Convert As";
        String cancel_text = "Cancel";
        String[] options = {save_as_text, cancel_text};
        int buttonPressed =
            JOptionPane.showOptionDialog(parent, msg,
                "Warning: loading old grammar", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, save_as_text);
        return buttonPressed == JOptionPane.YES_OPTION;
    }

    /** Shows the about dialog from the help menu. */
    public static void showAbout(final Component parent) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(null);
        buttonPanel.add(new JButton(new AbstractAction("External Libraries") {
            @Override
            public void actionPerformed(ActionEvent e) {
                LibrariesTable.instance().showDialog(parent);
            }
        }));
        buttonPanel.add(new JButton(new AbstractAction("Contributors") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ContributorsTable.instance().showDialog(parent, "Contributors");
            }
        }));
        Border aboutBorder =
            new CompoundBorder(
                BorderFactory.createBevelBorder(BevelBorder.LOWERED),
                BorderFactory.createEmptyBorder(0, 5, 5, 5));
        JTextPane aboutLabel = new JTextPane();
        aboutLabel.setBorder(aboutBorder);
        aboutLabel.setContentType("text/html");
        aboutLabel.setText(Version.getAboutHTML());
        aboutLabel.setEditable(false);
        JPanel aboutPanel = new JPanel(new BorderLayout());
        aboutPanel.add(aboutLabel);
        aboutPanel.add(buttonPanel, BorderLayout.SOUTH);
        JOptionPane.showMessageDialog(parent, aboutPanel, "About",
            JOptionPane.INFORMATION_MESSAGE);
    }
}
