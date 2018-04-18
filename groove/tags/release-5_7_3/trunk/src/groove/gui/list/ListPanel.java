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
package groove.gui.list;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;
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

import groove.grammar.QualName;
import groove.grammar.model.ResourceKind;
import groove.graph.Element;
import groove.gui.look.Values;
import groove.gui.look.Values.Mode;
import groove.io.HTMLConverter;

/**
 * Panel showing a list of messages. The panel hides itself when the
 * list is empty.
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class ListPanel extends JPanel {
    /**
     * Constructs a new panel.
     */
    public ListPanel(String title) {
        super(new BorderLayout());
        if (title != null) {
            add(getTitle(title), BorderLayout.NORTH);
        }
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
    public void setEntries(Collection<? extends SelectableListEntry> entries) {
        getEntryArea().setListData(entries.toArray(new SelectableListEntry[entries.size()]));
        setVisible(!entries.isEmpty());
    }

    /** Clears all entries from the list. */
    public void clearEntries() {
        getEntryArea().setListData(Collections.<SelectableListEntry>emptySet()
            .toArray(new SelectableListEntry[] {}));
        setVisible(false);
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
    public SelectableListEntry getSelectedEntry() {
        return getEntryArea().getSelectedValue();
    }

    @Override
    public Dimension getPreferredSize() {
        if (getContentSize() == 0) {
            return new Dimension();
        } else {
            return super.getPreferredSize();
        }
    }

    @Override
    public Dimension getMaximumSize() {
        if (getContentSize() == 0) {
            return new Dimension();
        } else {
            return super.getMaximumSize();
        }
    }

    /** Returns true if the list has entries. */
    public boolean hasContent() {
        return getContentSize() > 0;
    }

    private int getContentSize() {
        return getEntryArea().getModel()
            .getSize();
    }

    /** Lazily creates and returns the panel. */
    private JList<SelectableListEntry> getEntryArea() {
        if (this.entryArea == null) {
            JList<SelectableListEntry> result = this.entryArea = new JList<>();
            result.setBackground(getColors().getBackground(Mode.NONE));
            result.setForeground(getColors().getForeground(Mode.NONE));
            result.setSelectionBackground(getColors().getBackground(Mode.FOCUSED));
            result.setSelectionForeground(getColors().getBackground(Mode.FOCUSED));
            result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            result.setCellRenderer(new CellRenderer());
        }
        return this.entryArea;
    }

    /** Lazily creates and returns the title label. */
    private JLabel getTitle(String text) {
        if (this.title == null) {
            this.title = new JLabel(HTMLConverter.HTML_TAG.on(HTMLConverter.STRONG_TAG.on(text)));
        }
        return this.title;
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

    /** Normal background color for entries. */
    protected abstract Values.ColorSet getColors();

    /** The text area containing the messages. */
    private JList<SelectableListEntry> entryArea;
    /** The title of the panel. */
    private JLabel title;

    private class CellRenderer extends DefaultListCellRenderer {

        private CellRenderer() {
            super();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
            Component result =
                super.getListCellRendererComponent(list, value, index, isSelected, false);
            Mode mode = cellHasFocus ? Mode.FOCUSED : isSelected ? Mode.SELECTED : Mode.NONE;
            result.setBackground(getColors().getBackground(mode));
            result.setForeground(getColors().getForeground(mode));
            return result;
        }
    }

    /** Interface for entries of the list. */
    public interface SelectableListEntry {
        /** Returns the resource kind for which this entry occurs. */
        public ResourceKind getResourceKind();

        /** Returns the resource name for which this entry occurs. */
        public QualName getResourceName();

        /** Returns the list of elements in which the entry occurs. May be empty. */
        public List<Element> getElements();
    }
}