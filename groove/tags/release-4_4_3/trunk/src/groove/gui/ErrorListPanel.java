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
import groove.io.HTMLConverter;
import groove.view.FormatError;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
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
        add(new JLabel(
            HTMLConverter.HTML_TAG.on(HTMLConverter.STRONG_TAG.on(title))),
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
    public void setErrors(Collection<FormatError> errors) {
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
        getErrorArea().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = getIndexAt(e.getPoint());
                if (index >= 0 && getErrorArea().isSelectedIndex(index)) {
                    listener.update(null, getErrorArea().getSelectedValue());
                }
            }
        });
    }

    /** Returns the list of currently selected format errors. */
    public FormatError getSelectedError() {
        return (FormatError) getErrorArea().getSelectedValue();
    }

    @Override
    public Dimension getPreferredSize() {
        if (getErrorArea().getModel().getSize() == 0) {
            return new Dimension();
        } else {
            return super.getPreferredSize();
        }
    }

    @Override
    public Dimension getMaximumSize() {
        if (getErrorArea().getModel().getSize() == 0) {
            return new Dimension();
        } else {
            return super.getMaximumSize();
        }
    }

    /** Lazily creates and returns the error panel. */
    private JList getErrorArea() {
        if (this.errorArea == null) {
            JList result = this.errorArea = new JList();
            result.setBackground(JAttr.ERROR_NORMAL_BACKGROUND);
            result.setForeground(JAttr.ERROR_NORMAL_FOREGROUND);
            result.setSelectionBackground(JAttr.ERROR_FOCUS_BACKGROUND);
            result.setSelectionForeground(JAttr.ERROR_FOCUS_FOREGROUND);
            result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            result.setCellRenderer(new CellRenderer());
        }
        return this.errorArea;
    }

    /** Returns the index of the error list component under a given point, or
     * {@code -1} if there is no component under the point.
     */
    private int getIndexAt(Point point) {
        int result = getErrorArea().locationToIndex(point);
        Rectangle cellBounds = getErrorArea().getCellBounds(result, result);
        boolean cellSelected = cellBounds != null && cellBounds.contains(point);
        return cellSelected ? result : -1;
    }

    /** The text area containing the error messages. */
    private JList errorArea;

    private static class CellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            Component result =
                super.getListCellRendererComponent(list, value, index,
                    isSelected, false);
            if (isSelected && !cellHasFocus) {
                result.setBackground(JAttr.ERROR_SELECT_BACKGROUND);
                result.setForeground(JAttr.ERROR_SELECT_FOREGROUND);
            }
            return result;
        }
    }
}