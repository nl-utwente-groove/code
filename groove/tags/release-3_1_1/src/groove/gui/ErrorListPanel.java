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
 * $Id: ErrorListPanel.java,v 1.5 2008-01-30 09:33:36 iovka Exp $
 */
package groove.gui;

import groove.util.Groove;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

/**
 * Panel showing a list of error messages. The panel hides itself when the error
 * list is empty.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ErrorListPanel extends JPanel {
    /**
     * Constructs a new panel.
     */
    public ErrorListPanel() {
        super(new BorderLayout());
        add(new JLabel("<html><b>Format errors in graph</b></html>"),
            BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(getErrorArea());
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(0, 70));
        add(scrollPane);
        setVisible(false);
    }

    /**
     * Shows or appends a list of error messages in the error window.
     * @param errors the list of error messages to be shown
     */
    public void setErrors(List<String> errors) {
        if (errors.isEmpty()) {
            if (isVisible()) {
                setVisible(false);
                getErrorArea().setText("");
            }
        } else {
            StringBuffer text = new StringBuffer();
            text.append(Groove.toString(errors.toArray(), "", "", "\n"));
            getErrorArea().setText(text.toString());
            getErrorArea().setSelectionStart(0);
            if (!isVisible()) {
                setVisible(true);
            }
        }
    }

    /** Lazily creates and returns the error panel. */
    private JTextArea getErrorArea() {
        if (this.errorArea == null) {
            this.errorArea = new JTextArea();
            this.errorArea.setEditable(false);
            this.errorArea.setBackground(SystemColor.text);
            this.errorArea.setForeground(Color.RED);
            // errorArea.setPreferredSize(new Dimension(0, 70));
        }
        return this.errorArea;
    }

    /** The text area containing the error messages. */
    private JTextArea errorArea;
}