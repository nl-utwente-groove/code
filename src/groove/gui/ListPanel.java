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
 */
package groove.gui;

import groove.graph.Element;
import groove.io.HTMLConverter;
import groove.trans.ResourceKind;
import groove.view.FormatError;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
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
 * Panel showing a list of messages. The panel hides itself when the
 * list is empty.
 * @author Arend Rensink
 * @version $Revision: 2142 $
 */
public class ListPanel extends JPanel {
    /**
     * Constructs a new panel.
     */
    public ListPanel() {
        super(new BorderLayout());
        this.protoEntry = FormatError.prototype;
        add(getTitle(), BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(getEntryArea());
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(0, 70));
        add(scrollPane);
        setVisible(false);
    }

    /**
     * Shows or appends a list of selection messages in the window.
     * @param entries the list of messages to be shown
     */
    public void setEntries(Collection<? extends SelectionEntry> entries) {
        if (entries.isEmpty()) {
            if (isVisible()) {
                setVisible(false);
            }
        } else {
            getEntryArea().setListData(entries.toArray());
            if (!isVisible()) {
                setVisible(true);
            }
        }
    }

    /** 
     * Adds an observer to the list.
     * The observer is notified whenever the selection changes or
     * focus is regained. 
     */
    public void addSelectionListener(final Observer listener) {
        getEntryArea().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                listener.update(null, getEntryArea().getSelectedValue());
            }
        });
        getEntryArea().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = getIndexAt(e.getPoint());
                if (index >= 0 && getEntryArea().isSelectedIndex(index)) {
                    listener.update(null, getEntryArea().getSelectedValue());
                }
            }
        });
    }

    /** Returns the currently selected entry. */
    public SelectionEntry getSelectedEntry() {
        return (SelectionEntry) getEntryArea().getSelectedValue();
    }

    @Override
    public Dimension getPreferredSize() {
        if (getEntryArea().getModel().getSize() == 0) {
            return new Dimension();
        } else {
            return super.getPreferredSize();
        }
    }

    @Override
    public Dimension getMaximumSize() {
        if (getEntryArea().getModel().getSize() == 0) {
            return new Dimension();
        } else {
            return super.getMaximumSize();
        }
    }

    /** Lazily creates and returns the panel. */
    private JList getEntryArea() {
        if (this.entryArea == null) {
            this.entryArea = new JList();
        }
        return this.entryArea;
    }

    private void refreshEntryArea() {
        this.entryArea.setBackground(this.protoEntry.getNormalBackground());
        this.entryArea.setForeground(this.protoEntry.getNormalForeground());
        this.entryArea.setSelectionBackground(this.protoEntry.getFocusBackground());
        this.entryArea.setSelectionForeground(this.protoEntry.getFocusForeground());
        this.entryArea.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.entryArea.setCellRenderer(new CellRenderer());
    }

    /** Lazily creates and returns the title label. */
    private JLabel getTitle() {
        if (this.title == null) {
            this.title = new JLabel();
        }
        return this.title;
    }

    /** Sets the panel title to the given text. */
    public void setTitle(String text) {
        getTitle().setText(
            HTMLConverter.HTML_TAG.on(HTMLConverter.STRONG_TAG.on(text)));
    }

    /**
     * Sets the entry type of the list to the given one.
     * A call to {@link #setEntries(Collection)} should follow immediately.
     */
    public void setEntryType(SelectionEntry entryType) {
        this.protoEntry = entryType;
        this.refreshEntryArea();
    }

    /** Returns the index of the list component under a given point, or
     * {@code -1} if there is no component under the point.
     */
    private int getIndexAt(Point point) {
        int result = getEntryArea().locationToIndex(point);
        Rectangle cellBounds = getEntryArea().getCellBounds(result, result);
        boolean cellSelected = cellBounds != null && cellBounds.contains(point);
        return cellSelected ? result : -1;
    }

    /** The text area containing the messages. */
    private JList entryArea;
    /** The title of the panel. */
    private JLabel title;
    /** A prototype object of the entries to be used. */
    private SelectionEntry protoEntry;

    private class CellRenderer extends DefaultListCellRenderer {

        private CellRenderer() {
            super();
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            Component result =
                super.getListCellRendererComponent(list, value, index,
                    isSelected, false);
            if (isSelected && !cellHasFocus) {
                result.setBackground(ListPanel.this.protoEntry.getSelectBackground());
                result.setForeground(ListPanel.this.protoEntry.getSelectForeground());
            }
            return result;
        }
    }

    /** Interface for entries of the list. */
    public interface SelectionEntry {

        /** Normal background color for entries. */
        public Color getNormalBackground();

        /** Normal foreground color for entries. */
        public Color getNormalForeground();

        /** Focus background color for entries. */
        public Color getFocusBackground();

        /** Focus foreground color for entries. */
        public Color getFocusForeground();

        /** Select background color for entries. */
        public Color getSelectBackground();

        /** Select foreground color for entries. */
        public Color getSelectForeground();

        /** Returns the resource kind for which this entry occurs. */
        public ResourceKind getResourceKind();

        /** Returns the resource name for which this entry occurs. */
        public String getResourceName();

        /** Returns the list of elements in which the entry occurs. May be empty. */
        public List<Element> getElements();
    }
}