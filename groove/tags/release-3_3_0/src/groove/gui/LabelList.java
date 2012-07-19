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
 * $Id: LabelList.java,v 1.20 2007-12-03 08:56:08 rensink Exp $
 */
package groove.gui;

import groove.graph.Label;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;
import groove.gui.jgraph.JVertex;
import groove.util.Converter;
import groove.util.ObservableSet;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;

/**
 * Scroll pane showing the list of labels currently appearing in the graph
 * model.
 * @author Arend Rensink
 * @version $Revision$
 * @deprecated replaced by {@link LabelTree}
 */
@Deprecated
public class LabelList extends JList implements GraphModelListener,
        ListSelectionListener {
    /**
     * Constructs a label list associated with a given jgraph and set of
     * filtered labels. {@link #updateModel()} should be called before the list
     * can be used.
     * @param jgraph the jgraph with which this list is to be associated
     */
    public LabelList(JGraph jgraph) {
        this.filteredLabels = jgraph.getFilteredLabels();
        if (this.filteredLabels != null) {
            this.filteredLabels.addObserver(new Observer() {
                public void update(Observable o, Object arg) {
                    LabelList.this.repaint();
                }
            });
        }
        // initialise the list model
        this.listModel = new DefaultListModel();
        setModel(this.listModel);
        // change the cell renderer so it adds a space in front of the labels
        setCellRenderer(new MyCellRenderer());

        this.jgraph = jgraph;
        // take care of the popup menu
        this.popupMenu = createPopupMenu();
        addMouseListener(new MyMouseListener());
        // add a mouse listener to the jgraph to clear the selction of this list
        // as soon as the mouse is pressed in the jgraph
        jgraph.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON1
                    && !isSelectionEmpty()) {
                    clearSelection();
                }
            }
        });
        setEnabled(false);
    }

    /**
     * Returns the jgraph with which this label list is associated.
     */
    public JGraph getJGraph() {
        return this.jgraph;
    }

    /**
     * Returns an unmodifiable view on the label set maintained by this label
     * list.
     */
    public Collection<Label> getLabels() {
        return Collections.unmodifiableSet(this.labels.keySet());
    }

    /**
     * Replaces the jmodel on which this label list is based with the
     * (supposedly new) model in the associated jgraph. Gets the labels from the
     * model and adds them to this label list.
     */
    public void updateModel() {
        if (this.jmodel != null) {
            this.jmodel.removeGraphModelListener(this);
        }
        this.jmodel = this.jgraph.getModel();
        this.labels.clear();
        this.jmodel.addGraphModelListener(this);
        for (int i = 0; i < this.jmodel.getRootCount(); i++) {
            JCell cell = (JCell) this.jmodel.getRootAt(i);
            if (isListable(cell)) {
                addToLabels(cell);
            }
        }
        updateList();
        setEnabled(true);
    }

    /**
     * Returns the set of jcells whose label sets contain a given label.
     * @param label the label looked for
     * @return the set of {@link JCell}s for which {@link JCell#getListLabels()}
     *         contains <tt>label</tt>
     */
    public Set<JCell> getJCells(Object label) {
        return this.labels.get(label);
    }

    /**
     * In addition to delegating the method to <tt>super</tt>, sets the
     * background color to <tt>null</tt> when disabled and back to the default
     * when enabled.
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (enabled != isEnabled()) {
            if (!enabled) {
                this.enabledBackground = getBackground();
                setBackground(null);
            } else if (this.enabledBackground != null) {
                setBackground(this.enabledBackground);
            }
        }
        super.setEnabled(enabled);
    }

    /**
     * Updates the label list according to the change event.
     */
    public void graphChanged(GraphModelEvent e) {
        boolean changed = false;
        GraphModelEvent.GraphModelChange change = e.getChange();
        if (change instanceof JModel.RefreshEdit) {
            changed = processRefresh((JModel.RefreshEdit) change, changed);
        } else {
            changed = processRegularEdit(change, changed);
        }
        if (changed) {
            updateList();
        }
    }

    /**
     * Records the changes imposed by a graph change that is not a
     * {@link JModel.RefreshEdit}.
     */
    private boolean processRegularEdit(GraphModelEvent.GraphModelChange change,
            boolean changed) {
        Map<?,?> changeMap = change.getAttributes();
        if (changeMap != null) {
            for (Object changeEntry : changeMap.entrySet()) {
                Object obj = ((Map.Entry<?,?>) changeEntry).getKey();
                if (isListable(obj)) { // &&
                    // attributes.containsKey(GraphConstants.VALUE))
                    // {
                    changed |= modifyLabels((JCell) obj);
                }
            }
        }
        // added cells mean added labels
        Object[] addedArray = change.getInserted();
        if (addedArray != null) {
            for (Object element : addedArray) {
                // the cell may be a port, so we have to check for
                // JCell-hood
                if (isListable(element)) {
                    JCell cell = (JCell) element;
                    changed |= addToLabels(cell);
                }
            }
        }
        // removed cells mean removed labels
        Object[] removedArray = change.getRemoved();
        if (removedArray != null) {
            for (Object element : removedArray) {
                // the cell may be a port, so we have to check for
                // JCell-hood
                if (isListable(element)) {
                    JCell cell = (JCell) element;
                    changed |= removeFromLabels(cell);
                }
            }
        }
        return changed;
    }

    /**
     * Processes the changes of a {@link JModel.RefreshEdit}.
     */
    private boolean processRefresh(JModel.RefreshEdit change, boolean changed) {
        for (JCell cell : change.getRefreshedJCells()) {
            if (isListable(cell)) {
                changed |= modifyLabels(cell);
            }
        }
        return changed;
    }

    /**
     * Callback method to determine whether a given cell should be included in
     * the label list. This should only be the case if the cell is a
     * {@link JCell} for which {@link JCell#isListable()} holds.
     */
    private boolean isListable(Object cell) {
        return cell instanceof JCell && ((JCell) cell).isListable();
    }

    /**
     * Emphasises/deemphasises cells in the associated jmodel, based on the list
     * selection.
     */
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            Set<JCell> emphSet = new HashSet<JCell>();
            int i = getMinSelectionIndex();
            if (i >= 0) {
                while (i <= getMaxSelectionIndex()) {
                    Label label = (Label) this.listModel.getElementAt(i);
                    if (isSelectedIndex(i)) {
                        emphSet.addAll(this.labels.get(label));
                    }
                    i++;
                }
            }
            this.jmodel.setEmphasized(emphSet);
        }
    }

    /**
     * Updates the list from the internally kept label collection.
     */
    protected void updateList() {
        // temporarily remove this component as selection listener
        removeListSelectionListener(this);
        // clear the selection first
        clearSelection();
        // clear the list
        this.listModel.clear();
        for (Label label : getLabels()) {
            this.listModel.addElement(label);
        }
        // reinstate this component as selection listener
        addListSelectionListener(this);
    }

    /**
     * Creates a popup menu, consisting of show and hide actions.
     */
    protected JPopupMenu createPopupMenu() {
        JPopupMenu result = new JPopupMenu();
        Object[] selectedValues = getSelectedValues();
        if (this.filteredLabels != null && selectedValues.length > 0) {
            result.add(new FilterAction(selectedValues, true));
            result.add(new FilterAction(selectedValues, false));
            result.addSeparator();
        }
        // add the show/hide menu
        JPopupMenu restMenu = new ShowHideMenu(this.jgraph).getPopupMenu();
        for (int i = 0; i < restMenu.getComponentCount();) {
            result.add(restMenu.getComponent(i));
        }
        return result;
    }

    /**
     * Adds a cell to the label map. This means that for all labels of the cell,
     * the cell is inserted in that label's image. The return value indicates if
     * any labels were added
     */
    protected boolean addToLabels(JCell cell) {
        boolean result = false;
        for (Label label : cell.getListLabels()) {
            result |= addToLabels(cell, label);
        }
        return result;
    }

    /**
     * Adds a cell-label pair to the label map. If the label does not yet exist
     * in the map, insetrs it. The return value indicates if the label had to be
     * created.
     */
    private boolean addToLabels(JCell cell, Label label) {
        boolean result = false;
        Set<JCell> currentCells = this.labels.get(label);
        if (currentCells == null) {
            currentCells = new HashSet<JCell>();
            this.labels.put(label, currentCells);
            result = true;
        }
        currentCells.add(cell);
        return result;
    }

    /**
     * Removes a cell from the values of the label map, and removes a label if
     * there are no cells left for it. The return value indicates if there were
     * any labels removed.
     */
    protected boolean removeFromLabels(JCell cell) {
        boolean result = false;
        Iterator<Map.Entry<Label,Set<JCell>>> labelIter =
            this.labels.entrySet().iterator();
        while (labelIter.hasNext()) {
            Map.Entry<Label,Set<JCell>> labelEntry = labelIter.next();
            Set<JCell> cellSet = labelEntry.getValue();
            if (cellSet.remove(cell) && cellSet.isEmpty()) {
                labelIter.remove();
                result = true;
            }
        }
        return result;
    }

    /**
     * Modifies the presence of the cell in the label map. The return value
     * indicates if there were any labels added or removed.
     */
    protected boolean modifyLabels(JCell cell) {
        boolean result = false;
        Set<Label> newLabelSet = new HashSet<Label>(cell.getListLabels());
        // } else {
        // newLabelSet = new HashSet<String>();
        // }
        // go over the existing label map
        Iterator<Map.Entry<Label,Set<JCell>>> labelIter =
            this.labels.entrySet().iterator();
        while (labelIter.hasNext()) {
            Map.Entry<Label,Set<JCell>> labelEntry = labelIter.next();
            Label label = labelEntry.getKey();
            Set<JCell> cellSet = labelEntry.getValue();
            if (newLabelSet.remove(label)) {
                // the cell should be in the set
                cellSet.add(cell);
            } else if (cellSet.remove(cell) && cellSet.isEmpty()) {
                // the cell was in the set but shouldn't have been,
                // and the set is now empty
                labelIter.remove();
                result = true;
            }
        }
        // any new labels left over were not in the label map; add them
        for (Label label : newLabelSet) {
            Set<JCell> newCells = new HashSet<JCell>();
            newCells.add(cell);
            this.labels.put(label, newCells);
            result = true;
        }
        return result;
    }

    /**
     * The list model used for the JList.
     * @require <tt>listModel == listComponent.getModel()</tt>
     */
    protected final DefaultListModel listModel;

    /**
     * The pop-up menu for this label list.
     * @invariant popupMenu != null
     */
    protected final JPopupMenu popupMenu;
    /**
     * The {@link JGraph}associated to this label list.
     */
    protected final JGraph jgraph;

    /**
     * The {@link JModel}currently being viewed by this label list.
     */
    protected JModel jmodel;

    /**
     * The bag of labels in this jmodel.
     */
    protected final Map<Label,Set<JCell>> labels =
        new TreeMap<Label,Set<JCell>>();

    /** Set of filtered labels. */
    private final ObservableSet<Label> filteredLabels;

    /**
     * The background colour of this component when it is enabled.
     */
    private Color enabledBackground;

    /**
     * Special cell renderer that visualises the NO_LABEL label as well as
     * filtered labels.
     */
    private class MyCellRenderer extends DefaultListCellRenderer {
        /** Empty constructor with the correct visibility. */
        MyCellRenderer() {
            // empty
        }

        /** Sets the internally stored label. */
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            this.label = (Label) value;
            return super.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);
        }

        @Override
        public void setText(String labelText) {
            if (this.label != null) {
                StringBuilder text = new StringBuilder();
                StringBuilder toolTipText = new StringBuilder();
                Color foreground = getForeground();
                if (this.label.equals(JVertex.NO_LABEL)) {
                    text.append(Options.NO_LABEL_TEXT);
                    foreground = SPECIAL_COLOR;
                } else if (labelText.length() == 0) {
                    text.append(Options.EMPTY_LABEL_TEXT);
                    foreground = SPECIAL_COLOR;
                } else {
                    text.append(labelText);
                    int count = LabelList.this.labels.get(this.label).size();
                    toolTipText.append(count);
                    toolTipText.append(" occurrence");
                    if (count > 1) {
                        toolTipText.append("s");
                    }
                }
                Converter.toHtml(text);
                if (this.label.isNodeType()) {
                    Converter.STRONG_TAG.on(text);
                }
                if (LabelList.this.filteredLabels != null) {
                    if (toolTipText.length() != 0) {
                        toolTipText.append(Converter.HTML_LINEBREAK);
                    }
                    if (LabelList.this.filteredLabels.contains(this.label)) {
                        Converter.STRIKETHROUGH_TAG.on(text);
                        toolTipText.append("Filtered label; doubleclick to show");
                    } else {
                        toolTipText.append("Visible label; doubleclick to filter");
                    }
                }
                Converter.createColorTag(foreground).on(text);
                if (toolTipText.length() != 0) {
                    setToolTipText(Converter.HTML_TAG.on(toolTipText).toString());
                }
                super.setText(Converter.HTML_TAG.on(text).toString());
            }
        }

        @Override
        public void setBorder(Border border) {
            super.setBorder(new CompoundBorder(border, INSET_BORDER));
        }

        /** The label for which the renderer has been last invoked. */
        private Label label;
    }

    /** Class to deal with mouse events over the label list. */
    private class MyMouseListener extends MouseAdapter {
        /** Empty constructor with the correct visibility. */
        MyMouseListener() {
            // empty
        }

        @Override
        public void mousePressed(MouseEvent evt) {
            if (evt.getButton() == MouseEvent.BUTTON3) {
                int index = locationToIndex(evt.getPoint());
                if (index >= 0) {
                    if (evt.isControlDown()) {
                        addSelectionInterval(index, index);
                    } else if (evt.isShiftDown()) {
                        addSelectionInterval(getAnchorSelectionIndex(), index);
                    } else {
                        setSelectedIndex(index);
                    }
                }
            }
            maybeShowPopup(evt);
        }

        @Override
        public void mouseReleased(MouseEvent evt) {
            maybeShowPopup(evt);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (LabelList.this.filteredLabels != null && e.getClickCount() == 2) {
                int index = locationToIndex(e.getPoint());
                if (index != -1) {
                    Label label = (Label) LabelList.this.listModel.get(index);
                    if (!LabelList.this.filteredLabels.add(label)) {
                        LabelList.this.filteredLabels.remove(label);
                    }
                }
            }
        }

        private void maybeShowPopup(MouseEvent evt) {
            if (evt.isPopupTrigger()) {
                createPopupMenu().show(evt.getComponent(), evt.getX(),
                    evt.getY());
            }
        }
    }

    private class FilterAction extends AbstractAction {
        FilterAction(Object[] cells, boolean filter) {
            super(filter ? Options.FILTER_ACTION_NAME
                    : Options.UNFILTER_ACTION_NAME);
            this.filter = filter;
            this.labels = new ArrayList<Label>();
            for (Object cell : cells) {
                this.labels.add((Label) cell);
            }
        }

        public void actionPerformed(ActionEvent e) {
            if (this.filter) {
                LabelList.this.filteredLabels.addAll(this.labels);
            } else {
                LabelList.this.filteredLabels.removeAll(this.labels);
            }
        }

        private final boolean filter;
        private final Collection<Label> labels;
    }

    /**
     * Border to put some space to the left and right of the labels inside the
     * list.
     */
    static final Border INSET_BORDER = new EmptyBorder(0, 3, 0, 3);
    /** Colour HTML tag for the foreground colour of special labels. */
    static final Color SPECIAL_COLOR = Color.LIGHT_GRAY;
}