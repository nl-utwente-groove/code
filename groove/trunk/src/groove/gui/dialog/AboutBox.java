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
 * $Id: AboutBox.java,v 1.3 2008-01-30 09:33:36 iovka Exp $
 */
package groove.gui.dialog;

import groove.util.Version;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Dialog to display About information on the Groove tool.
 * @author Arend Rensink
 * @version $Revision: 1896 $
 */
public class AboutBox extends JDialog {
    /** The title of the dialog. */
    public static final String TITLE = "About GROOVE";
    /** Button text of the Cancel button. */
    public static final String CANCEL_BUTTON_TEXT = "OK";

    /** Creates and displays a throw-away instance of this dialog. */
    public AboutBox(JFrame context) {
        super(context, true);
        setTitle(TITLE);
        Container contentPane = getContentPane();
        contentPane.add(createMessagePane());
        contentPane.add(createOKButton(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(context);
        setVisible(true);
    }

    /** Creates the message pane with the about text. */
    private JPanel createMessagePane() {
        JPanel result = new JPanel();
        JTextArea textField = new JTextArea();
        textField.setEditable(false);
        textField.setText(getMessageText());
        result.add(textField);
        return result;
    }

    /** Creates and returns a button to hide the entire dialog. */
    private JPanel createOKButton() {
        JPanel result = new JPanel();
        JButton button = new JButton(CANCEL_BUTTON_TEXT);
        button.addActionListener(new ActionListener() {
            /** Hides the dialog. */
            public void actionPerformed(ActionEvent e) {
                AboutBox.this.setVisible(false);
                AboutBox.this.dispose();
            }

        });
        result.add(button);
        return result;
    }

    /** Returns the text of the about message. */
    private String getMessageText() {
        StringBuffer result = new StringBuffer();
        result.append("GROOVE tool set\n");
        result.append("Version: ");
        result.append(Version.NUMBER);
        result.append("\n(c) University of Twente, 2002, 2010\n\n");
        result.append("Libraries used: \n* ");
        //        result.append(getCastorText());
        //        result.append("\n* ");
        result.append(getXercesText());
        result.append("\n* ");
        result.append(getJGraphText());
        result.append("\n");
        return result.toString();
    }

    //
    //    /** Returns a description of the Castor component. */
    //    private String getCastorText() {
    //        return "Castor " + org.exolab.castor.util.Version.getBuildVersion();
    //    }

    /** Returns a description of the Xerces component. */
    private String getXercesText() {
        return org.apache.xerces.impl.Version.getVersion();
    }

    /** Returns a description of the JGraph component. */
    private String getJGraphText() {
        return org.jgraph.JGraph.VERSION;
    }
}
