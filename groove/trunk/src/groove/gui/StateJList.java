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
import groove.gui.action.ActionStore;
import groove.gui.jgraph.JAttr;
import groove.lts.GTS;
import groove.view.GrammarModel;
import groove.view.HostModel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author Tom Staijen
 * @version $Revision $
 */
public class StateJList extends JList implements SimulatorListener {
    /**
     * Creates a new state list viewer.
     */
    protected StateJList(StateDisplay display) {
        this.display = display;
        this.simulator = display.getSimulator();
        this.listModel = new DefaultListModel();
        setModel(this.listModel);
        this.setEnabled(false);
        this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.setCellRenderer(new MyCellRenderer());
        installListeners();
    }

    private void installListeners() {
        getSimulatorModel().addListener(this, Change.GRAMMAR, Change.STATE,
            Change.HOST);
        addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                StateJList.this.repaint();
            }

            @Override
            public void focusGained(FocusEvent e) {
                StateJList.this.repaint();
            }
        });
        addMouseListener(new MyMouseListener());
        this.listListeners =
            new ListSelectionListener[] {new MySelectionListener()};
        activateListeners();
    }

    private void activateListeners() {
        if (this.listening) {
            throw new IllegalStateException();
        }
        for (ListSelectionListener listener : this.listListeners) {
            addListSelectionListener(listener);
        }
        this.listListeners = null;
        this.listening = true;
    }

    /**
     * Removes all list selection listeners. The listeners should be added back
     * later using {@link #activateListeners()}
     */
    private void suspendListeners() {
        if (!this.listening) {
            throw new IllegalStateException();
        }
        this.listListeners = getListSelectionListeners();
        for (ListSelectionListener listener : this.listListeners) {
            removeListSelectionListener(listener);
        }
        this.listening = false;
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
     * Creates a popup menu, consisting of "use as start graph" & "preview".
     */
    protected JPopupMenu createPopupMenu(Point atPoint) {
        // add rest only if mouse is actually over a graph name
        int index = locationToIndex(atPoint);
        boolean overHost =
            index > 0 && getCellBounds(index, index).contains(atPoint);
        JPopupMenu result = this.display.createListPopupMenu(overHost);
        if (overHost) {
            result.addSeparator();
            result.add(getActions().getSetStartGraphAction());
        } else if (index == 0) {
            result.addSeparator();
            result.add(getActions().getBackAction());
            result.add(getActions().getForwardAction());
        }
        return result;
    }

    // -----------------------------------------
    // Methods from SimulationListener Interface
    // -----------------------------------------

    @Override
    protected ListSelectionModel createSelectionModel() {
        return new MySelectionModel();
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        suspendListeners();
        if (changes.contains(Change.GRAMMAR)) {
            this.listModel.clear();
            if (source.getGrammar() == null) {
                setEnabled(false);
            } else {
                setEnabled(true);
                setList(source.getHostSet());
            }
        }
        if (changes.contains(Change.STATE) || changes.contains(Change.HOST)) {
            refreshCurrentState();
            if (source.hasHost()) {
                setSelectedValue(source.getHost().getName(), true);
            } else {
                setSelectedIndex(0);
            }
        }
        activateListeners();
    }

    /**
     * Sets the list of names to a given set.
     * @param selection the set of names to be selected
     */
    private void setList(Collection<HostModel> selection) {
        Object[] hostNames = getGrammar().getHostNames().toArray();
        Arrays.sort(hostNames);
        // turn the selection into a set of names
        Set<String> selectionNames = new HashSet<String>();
        for (HostModel hostView : selection) {
            selectionNames.add(hostView.getName());
        }
        int[] selectedIndices = new int[selection.size()];
        int selectedCount = 0;
        this.listModel.clear();
        // add the empty string, later to be replaced by the
        // current state indicator
        this.listModel.addElement("");
        for (int i = 0; i < hostNames.length; i++) {
            Object name = hostNames[i];
            this.listModel.addElement(name);
            if (selectionNames.contains(name)) {
                selectedIndices[selectedCount] = i + 1;
                selectedCount++;
            }
        }
        refreshCurrentState();
        if (selectedCount == 0) {
            setSelectedIndex(0);
        } else {
            setSelectedIndices(selectedIndices);
        }
    }

    /** Returns the list of selected graph names. */
    private List<String> getSelectedGraphs() {
        List<String> result = new ArrayList<String>();
        int[] selection = getSelectedIndices();
        for (int i = 0; i < selection.length; i++) {
            int index = selection[i];
            if (index > 0) {
                result.add((String) this.listModel.elementAt(index));
            }
        }
        return result;
    }

    /**
     * Refreshes the value of the current state item of the state list.
     */
    private void refreshCurrentState() {
        String text;
        if (getSimulatorModel().getState() == null) {
            String startKey =
                getActions().getStartSimulationAction().getValue(
                    Action.ACCELERATOR_KEY).toString();
            text =
                String.format("Press %s to start simulation",
                    startKey.substring(startKey.indexOf(" ") + 1));
        } else {
            text =
                String.format("Simulation state: %s",
                    getSimulatorModel().getState());
        }
        this.listModel.setElementAt(text, 0);
        repaint();
    }

    // --------------
    // Private fields
    // --------------

    /**
     * Returns the current grammar view from the simulator.
     */
    private GrammarModel getGrammar() {
        return getSimulatorModel().getGrammar();
    }

    /**
     * Returns the current value of the start graph name. Convenience method for
     * <code>getGrammarView().getStartGraphName()</code>.
     */
    private final String getStartGraphName() {
        return getGrammar() == null ? null : getGrammar().getStartGraphName();
    }

    /** Convenience method to retrieve the current GTS from the simulator. */
    private GTS getCurrentGTS() {
        return getSimulatorModel().getGts();
    }

    /** Returns the simulator to which the state list belongs. */
    private Simulator getSimulator() {
        return this.simulator;
    }

    /** Returns the simulator to which the state list belongs. */
    private SimulatorModel getSimulatorModel() {
        return getSimulator().getModel();
    }

    /** Returns the simulator to which the state list belongs. */
    private ActionStore getActions() {
        return getSimulator().getActions();
    }

    /** The display from which this list is derived. */
    private final StateDisplay display;
    /**
     * The simulator to which this directory belongs.
     * @invariant simulator != null
     */
    private final Simulator simulator;
    /** The list model used in this class. */
    private final DefaultListModel listModel;

    private boolean listening;
    /**
     * Temporary store of suspended list selection listeners.
     * @see #suspendListeners()
     * @see #activateListeners()
     */
    private ListSelectionListener[] listListeners;
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
                    StateJList.this.requestFocus();
                    createPopupMenu(evt.getPoint()).show(evt.getComponent(),
                        evt.getX(), evt.getY());
                }
            } else if (evt.getClickCount() == 2) { // Left double click
                if (StateJList.this.isEnabled() && cellSelected) {
                    getActions().getEditHostOrStateAction().execute();
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            getSimulatorModel().setDisplay(DisplayKind.HOST);
        }
    }

    private class MySelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            getSimulatorModel().setHostSet(getSelectedGraphs());
        }
    }

    /**
     * Cell renderer that distinguishes the name corresponding to the current
     * start graph and to the simulation state indicator.
     */
    private class MyCellRenderer extends DefaultListCellRenderer {
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
            cellHasFocus = isSelected && StateJList.this.isFocusOwner();
            // set tool tips and special formats
            if (index == 0) {
                // set the first item (the current state indicator) to special
                // format
                if (!isSelected) {
                    // distinguish the current start graph name
                    setBackground(JAttr.STATE_BACKGROUND);
                    //                    ((JComponent) result).setBorder(START_GRAPH_BORDER);
                }
                setFont(getFont().deriveFont(Font.ITALIC));
                setToolTipText("Currently selected state of the simulation");
                setIcon(Icons.STATE_MODE_ICON);
            } else {
                if (value.toString().equals(getStartGraphName())) {
                    setToolTipText("Current start graph");
                } else {
                    setToolTipText("Potential start graph");
                }
                setText(StateJList.this.display.getLabelText(value.toString()));
                setIcon(StateJList.this.display.getListIcon(value.toString()));
                boolean error =
                    StateJList.this.display.hasError(value.toString());
                setForeground(JAttr.getForeground(isSelected, cellHasFocus,
                    error));
                setBackground(JAttr.getBackground(isSelected, cellHasFocus,
                    error));
            }
            return result;
        }

        private final Border emptyBorder = new EmptyBorder(1, 3, 1, 0);
    }

    /** Variation on the selection model that makes sure the first item
     * only gets selected if there is a GTS.
     */
    private class MySelectionModel extends DefaultListSelectionModel {
        @Override
        public void setSelectionInterval(int index0, int index1) {
            if (index0 == 0 && getCurrentGTS() == null) {
                index0 = 1;
            }
            if (index0 <= index1) {
                super.setSelectionInterval(index0, index1);
            }
        }

        @Override
        public void setLeadSelectionIndex(int leadIndex) {
            if (leadIndex != 0 || getCurrentGTS() != null) {
                super.setLeadSelectionIndex(leadIndex);
            }
        }

    }
}
