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

import groove.view.FormatError;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionListener;

/**
 * Panel showing a list of error messages. The panel hides itself when the error
 * list is empty.
 * @author Arend Rensink
 * @version $Revision: 2142 $
 */
public class NewErrorListPanel extends JPanel {
    /**
     * Constructs a new panel.
     */
    public NewErrorListPanel() {
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
    public void setErrors(List<FormatError> errors) {
        if (errors.isEmpty()) {
            if (isVisible()) {
                setVisible(false);
            }
        } else {
            getErrorArea().setListData(errors.toArray());
            if (!isVisible()) {
                setVisible(true);
            }
        }
    }

    /** Adds a selection listener to the error list. */
    public void addSelectionListener(ListSelectionListener listener) {
        getErrorArea().addListSelectionListener(listener);
    }

    /** Returns the list of currently selected format errors. */
    public FormatError getSelectedError() {
        return (FormatError) getErrorArea().getSelectedValue();
    }

    /** Lazily creates and returns the error panel. */
    private JList getErrorArea() {
        if (this.errorArea == null) {
            JList result = this.errorArea = new JList();
            result.setBackground(SystemColor.text);
            result.setForeground(Color.RED);
            result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        return this.errorArea;
    }

    /** The text area containing the error messages. */
    private JList errorArea;
}