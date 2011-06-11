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
 * $Id$
 */
package groove.gui;

import groove.gui.SimulatorModel.Change;
import groove.gui.jgraph.JAttr;
import groove.trans.ResourceKind;
import groove.view.GrammarModel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * List of available resources, showing their enabledness status.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ResourceList extends JList implements SimulatorListener {
    /**
     * Creates a new state list viewer.
     */
    protected ResourceList(ResourceDisplay display) {
        this.display = display;
        this.setEnabled(false);
        this.setCellRenderer(new ResourceCellRenderer());
        installListeners();
    }

    /** Installs all listeners, and sets the listening status to {@code true}. */
    protected void installListeners() {
        getSimulatorModel().addListener(this, Change.GRAMMAR,
            Change.toChange(getResourceKind()));
        addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                ResourceList.this.repaint();
            }

            @Override
            public void focusGained(FocusEvent e) {
                ResourceList.this.repaint();
            }
        });
        addMouseListener(new MyMouseListener());
        addListSelectionListener(new MySelectionListener());
        activateListening();
    }

    /**
     * Sets the listening status to {@code false}, if it was not already {@code false}.
     * @return {@code true} if listening was suspended as a result of this call;
     * {@code false} if it was already suspended.
     */
    protected final boolean suspendListening() {
        boolean result = this.listening;
        if (result) {
            this.listening = false;
        }
        return result;
    }

    /** Sets the listening flag to {@code true}. */
    protected final void activateListening() {
        if (this.listening) {
            throw new IllegalStateException();
        }
        this.listening = true;
    }

    /** Returns the listening status. */
    protected final boolean isListening() {
        return this.listening;
    }

    /** Returns the resource display to which this list belongs. */
    protected final ResourceDisplay getDisplay() {
        return this.display;
    }

    /** Returns the resource kind of this list. */
    protected final ResourceKind getResourceKind() {
        return getDisplay().getResourceKind();
    }

    /**
     * In addition to delegating the method to <tt>super</tt>, sets the
     * background colour to <tt>null</tt> when disabled and back to the default
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
     * Creates a popup menu.
     */
    protected JPopupMenu createPopupMenu(Point atPoint) {
        int index = locationToIndex(atPoint);
        boolean overItem =
            index != -1 && getCellBounds(index, index).contains(atPoint);
        JPopupMenu result = getDisplay().createListPopupMenu(overItem);
        return result;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        if (suspendListening()) {
            if (changes.contains(Change.GRAMMAR)) {
                if (!source.hasGrammar()) {
                    setListData(new String[0]);
                    setEnabled(false);
                } else {
                    setEnabled(true);
                    refreshList(getGrammar().getNames(getResourceKind()));
                }
            }
            refreshSelection(source.getSelectSet(getResourceKind()));
            activateListening();
        }
    }

    /**
     * Refreshes the list model from a given set of names.
     * @param names the names to put into the list model
     */
    protected void refreshList(Set<String> names) {
        Object[] nameArray = names.toArray();
        Arrays.sort(nameArray);
        setListData(nameArray);
    }

    /**
     * Refreshes the list selection from a set of selected names.
     */
    protected void refreshSelection(Set<String> selection) {
        if (selection.isEmpty()) {
            clearSelection();
        } else {
            int[] indices = new int[selection.size()];
            int count = 0;
            for (int i = 1; i < getModel().getSize(); i++) {
                if (selection.contains(getModel().getElementAt(i))) {
                    indices[count] = i;
                    count++;
                }
            }
            setSelectedIndices(indices);
        }
    }

    /**
     * Returns the current grammar view from the simulator.
     */
    final protected GrammarModel getGrammar() {
        return getSimulatorModel().getGrammar();
    }

    /** Returns the simulator to which the state list belongs. */
    final protected SimulatorModel getSimulatorModel() {
        return getDisplay().getSimulatorModel();
    }

    /** The display from which this list is derived. */
    private final ResourceDisplay display;
    /** Flag indicating if listeners should be active. */
    private boolean listening;
    /**
     * The background colour of this component when it is enabled.
     */
    private Color enabledBackground;

    /** Class to deal with mouse events over the label list. */
    private class MyMouseListener extends MouseAdapter {

        /** Empty constructor with the correct visibility. */
        MyMouseListener() {
            // empty
        }

        @Override
        public void mouseClicked(MouseEvent evt) {
            int index = locationToIndex(evt.getPoint());
            Rectangle cellBounds = getCellBounds(index, index);
            boolean cellSelected =
                cellBounds != null && cellBounds.contains(evt.getPoint());
            if (evt.getClickCount() == 1) {
                if (evt.getButton() == MouseEvent.BUTTON3) { // Right click
                    // Determine if index was really selected
                    if (index >= 0 && cellSelected) {
                        if (getSelectedIndices().length < 2) {
                            // Adjust list selection accordingly.
                            setSelectedIndex(index);
                        }
                    }
                    ResourceList.this.requestFocus();
                    createPopupMenu(evt.getPoint()).show(evt.getComponent(),
                        evt.getX(), evt.getY());
                }
            } else if (evt.getClickCount() == 2) { // Left double click
                if (isEnabled() && cellSelected) {
                    getDisplay().getEditAction().execute();
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            getSimulatorModel().setDisplay(getDisplay().getKind());
        }
    }

    private class MySelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (suspendListening()) {
                getSimulatorModel().doSelectSet(getResourceKind(),
                    (getSelectedNames()));
                activateListening();
            }
        }

        /** Returns the list of selected names. */
        private List<String> getSelectedNames() {
            List<String> result = new ArrayList<String>();
            for (int i = 1; i <= getMaxSelectionIndex(); i++) {
                if (isSelectedIndex(i)) {
                    result.add(getModel().getElementAt(i).toString());
                }
            }
            return result;
        }
    }

    /**
     * Cell renderer that distinguishes the name, editing status, distry status
     * and error status.
     */
    protected class ResourceCellRenderer extends DefaultListCellRenderer {
        // This is the only method defined by ListCellRenderer.
        // We just reconfigure the JLabel each time we're called.
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, // cell index
                boolean isSelected, // is the cell selected
                boolean cellHasFocus) // the list and the cell have the focus
        {
            Component result =
                super.getListCellRendererComponent(list, value, index,
                    isSelected, false);
            // ensure some space to the left of the label
            setBorder(this.emptyBorder);
            String selection = value.toString();
            boolean error = getDisplay().hasError(selection);
            setForeground(JAttr.getForeground(isSelected, cellHasFocus, error));
            setBackground(JAttr.getBackground(isSelected, cellHasFocus, error));
            // set tool tips and special formats
            setToolTipText(getDisplay().getToolTip(selection));
            setIcon(getDisplay().getListIcon(selection));
            setText(getDisplay().getLabelText(selection));
            return result;
        }

        private final Border emptyBorder = new EmptyBorder(1, 3, 1, 0);
    }
}
