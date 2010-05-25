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

import groove.gui.jgraph.JAttr;
import groove.util.Converter;
import groove.view.FormatError;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import java.util.Observer;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Panel showing a list of error messages. The panel hides itself when the error
 * list is empty.
 * @author Arend Rensink
 * @version $Revision: 2142 $
 */
public class ErrorListPanel extends JPanel {
    /**
     * Constructs a new panel.
     */
    public ErrorListPanel(String title) {
        super(new BorderLayout());
        add(new JLabel(Converter.HTML_TAG.on(Converter.STRONG_TAG.on(title))),
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

    /** 
     * Adds an observer to the error list.
     * The observer is notified whenever the selection changes or
     * focus is regained. 
     */
    public void addSelectionListener(final Observer listener) {
        getErrorArea().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                listener.update(null, getErrorArea().getSelectedValue());
            }
        });
        getErrorArea().addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                // do nothing
            }

            @Override
            public void focusGained(FocusEvent e) {
                listener.update(null, getErrorArea().getSelectedValue());
            }
        });
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
            result.setSelectionBackground(SELECTED_FOCUS_COLOR);
            result.setSelectionForeground(Color.WHITE);
            result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            result.setCellRenderer(new CellRenderer());
        }
        return this.errorArea;
    }

    /** The text area containing the error messages. */
    private JList errorArea;

    /** Colour of selected and focused list items. */
    private static Color SELECTED_FOCUS_COLOR = Color.RED.darker().darker(); //new Color(255, 50, 0, 200);
    /** Colour of selected but unfocused list items. */
    private static Color SELECTED_NON_FOCUS_COLOR = JAttr.ERROR_COLOR;

    private class CellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            Component result =
                super.getListCellRendererComponent(list, value, index,
                    isSelected, false);
            if (isSelected && !cellHasFocus) {
                result.setBackground(SELECTED_NON_FOCUS_COLOR);
                result.setForeground(Color.RED);
            }
            return result;
        }
    }
}