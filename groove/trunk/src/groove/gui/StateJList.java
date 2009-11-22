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

import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.RuleMatch;
import groove.trans.RuleName;
import groove.util.Groove;
import groove.view.AspectualGraphView;
import groove.view.StoredGrammarView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
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
public class StateJList extends JList implements SimulationListener {
    /**
     * Creates a new state list viewer.
     * @param simulator
     */
    protected StateJList(final Simulator simulator) {
        this.simulator = simulator;
        this.simulator.addSimulationListener(this);
        this.listModel = new DefaultListModel();
        setModel(this.listModel);
        this.setEnabled(false);
        // this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Multiple selection - mzimakova
        this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.setCellRenderer(new MyCellRenderer());
        this.addMouseListener(new MyMouseListener());
        addListSelectionListener(new MySelectionListener());
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
        JPopupMenu result = new JPopupMenu();
        result.add(this.simulator.getNewGraphAction());
        // add rest only if mouse is actually over a graph name
        int index = locationToIndex(atPoint);
        if (index > 0 && getCellBounds(index, index).contains(atPoint)) {
            result.addSeparator();
            result.add(this.simulator.getCopyGraphAction());
            result.add(this.simulator.getDeleteGraphAction());
            result.add(this.simulator.getRenameGraphAction());
            result.addSeparator();
            result.add(getSetStartGraphAction());
            result.add(getPreviewGraphAction());
            result.add(this.simulator.getEditGraphAction());
            boolean isEnabledState =
                (StateJList.this.getSelectedIndices().length == 1);
            result.getComponent(6).setEnabled(isEnabledState);
            result.getComponent(7).setEnabled(isEnabledState);
            result.getComponent(8).setEnabled(isEnabledState);
        }
        return result;
    }

    /** Lazily creates and returns the set start graph action. */
    private Action getSetStartGraphAction() {
        if (this.setStartGraphAction == null) {
            this.setStartGraphAction =
                new AbstractAction(Options.START_GRAPH_ACTION_NAME) {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        String selection =
                            (String) StateJList.this.getSelectedValue();
                        StateJList.this.simulator.doLoadStartGraph(selection);
                    }
                };
        }
        return this.setStartGraphAction;
    }

    /** Lazily creates and returns the graph preview action. */
    private Action getPreviewGraphAction() {
        if (this.previewGraphAction == null) {
            this.previewGraphAction =
                new AbstractAction(Options.PREVIEW_ACTION_NAME,
                    Groove.GRAPH_MODE_ICON) {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        StateJList.this.doPreviewGraph();
                    }
                };
        }
        return this.previewGraphAction;
    }

    private void doPreviewGraph() {
        if (!isSelectionEmpty()) {
            // Multiple selection - mzimakova
            suspendListeners();
            setSelectedIndex(getSelectedIndex());
            restoreListeners();
            String selection = (String) this.getSelectedValue();
            AspectualGraphView graphView =
                getGrammarView().getGraphView(selection);
            boolean load =
                Editor.previewGraph(graphView.getAspectGraph().toPlainGraph(),
                    Options.START_GRAPH_ACTION_NAME);
            if (load) {
                this.simulator.doLoadStartGraph(selection);
            }
        }
    }

    // -----------------------------------------
    // Methods from SimulationListener Interface
    // -----------------------------------------

    public void applyTransitionUpdate(GraphTransition transition) {
        refreshCurrentState(true);
    }

    public void setGrammarUpdate(StoredGrammarView grammar) {
        this.removeAll();
        this.setEnabled(false);
        if (grammar != null) {
            refreshList(false);
            this.setEnabled(true);
        }
    }

    public void setMatchUpdate(RuleMatch match) {
        refreshCurrentState(true);
    }

    public void setRuleUpdate(RuleName name) {
        // does nothing
    }

    public void setStateUpdate(GraphState state) {
        refreshCurrentState(true);
    }

    public void setTransitionUpdate(GraphTransition transition) {
        refreshCurrentState(true);
    }

    public void startSimulationUpdate(GTS gts) {
        refreshCurrentState(true);
    }

    /**
     * Refreshes the list of names by reloading it from the current grammar.
     * Either the currently selected item is kept, or the start graph is
     * selected (if it is among the graphs in the list).
     * @param keepSelection keepSelection if <code>true</code>, attempts to
     *        reselect the name selected before refreshing; otherwise, attempts
     *        to select the start graph.
     */
    public void refreshList(boolean keepSelection) {
        setList(getGrammarView().getGraphNames(), keepSelection);
    }

    /**
     * Indicates if there is currently a single true graph selected in the list.
     */
    public boolean isGraphSelected() {
        return getSelectedIndices().length == 1 && getSelectedIndex() > 0;
    }

    /**
     * Removes all list selection listeners. The listeners should be added back
     * later using {@link #restoreListeners()}
     */
    private void suspendListeners() {
        if (this.listeners != null) {
            throw new IllegalStateException(
                "Listeners should have been restored");
        }
        this.listeners = getListSelectionListeners();
        for (ListSelectionListener listener : this.listeners) {
            removeListSelectionListener(listener);
        }
    }

    /**
     * Adds all suspended list selection listeners. This should be called after
     * a previous call of {@link #suspendListeners()}.
     */
    private void restoreListeners() {
        if (this.listeners == null) {
            throw new IllegalStateException(
                "Listeners should have been suspended");
        }
        for (ListSelectionListener listener : this.listeners) {
            addListSelectionListener(listener);
        }
        this.listeners = null;
    }

    /**
     * Sets the list of names to a given set.
     * @param names the set of names to be displayed; will be ordered before
     *        display
     * @param keepSelection if <code>true</code>, attempts to reselect the name
     *        selected before refreshing.
     */
    private void setList(Set<String> names, boolean keepSelection) {
        Object[] currentSelection = getSelectedValues();
        Object[] sortedNames = names.toArray();
        Arrays.sort(sortedNames);
        this.listModel.clear();
        // add the empty string, later to be replaced by the
        // current state indicator
        this.listModel.addElement("");
        for (Object name : sortedNames) {
            this.listModel.addElement(name);
        }
        refreshCurrentState(false);
        suspendListeners();
        if (keepSelection) {
            int[] selectedIndices = new int[currentSelection.length];
            for (int i = 0; i < currentSelection.length; i++) {
                selectedIndices[i] =
                    Arrays.asList(sortedNames).indexOf(currentSelection[i]);
            }
            setSelectedIndices(selectedIndices);
        } else if (getStartGraphName() != null) {
            setSelectedValue(getStartGraphName(), true);
        }
        restoreListeners();
    }

    /**
     * Refreshes the value of the current state item of the state list.
     * @param select if <code>true</code>, select the current state item
     */
    private void refreshCurrentState(boolean select) {
        String text;
        if (this.simulator.getCurrentState() == null) {
            text = "simulation not enabled";
        } else {
            text =
                String.format("simulation state: %s",
                    this.simulator.getCurrentState());
        }
        this.listModel.setElementAt(text, 0);
        if (select) {
            // set the selection to the first element (the simulation state
            // indicator)
            suspendListeners();
            setSelectedIndex(0);
            restoreListeners();
        }
        repaint();
    }

    // --------------
    // Private fields
    // --------------

    /**
     * Returns the current grammar view. Convenience method for
     * <code>getSimulator().getGrammarView()</code>.
     */
    private StoredGrammarView getGrammarView() {
        return getSimulator().getGrammarView();
    }

    /**
     * Returns the current value of the start graph name. Convenience method for
     * <code>getGrammarView().getStartGraphName()</code>.
     */
    private final String getStartGraphName() {
        return getGrammarView() == null ? null
                : getGrammarView().getStartGraphName();
    }

    /** Returns the simulator to which the state list belongs. */
    private Simulator getSimulator() {
        return this.simulator;
    }

    /**
     * The simulator to which this directory belongs.
     * @invariant simulator != null
     */
    private final Simulator simulator;
    /** The list model used in this class. */
    private final DefaultListModel listModel;

    /**
     * Temporary store of suspended list selection listeners.
     * @see #suspendListeners()
     * @see #restoreListeners()
     */
    private ListSelectionListener[] listeners;
    /** Action to set the start graph in the simulator. */
    private Action setStartGraphAction;
    /** Action to preview a graph. */
    private Action previewGraphAction;
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
            boolean cellSelected =
                getCellBounds(index, index).contains(evt.getPoint());
            if (evt.getClickCount() == 1) {
                if (evt.getButton() == MouseEvent.BUTTON3) { // Right click
                    // Determine if index was really selected
                    if (index > 0 && cellSelected) {
                        // Multiple selection - mzimakova
                        if (getSelectedIndices().length < 2) {
                            // Adjust list selection accordingly.
                            suspendListeners();
                            setSelectedIndex(index);
                            restoreListeners();
                        }
                    }
                    createPopupMenu(evt.getPoint()).show(evt.getComponent(),
                        evt.getX(), evt.getY());
                }
            } else if (evt.getClickCount() == 2) { // Left double click
                if (StateJList.this.isEnabled() && index > 0 && cellSelected) {
                    StateJList.this.doPreviewGraph();
                }
            }
        }
    }

    private class MySelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            // we do something if, after this event, a single item is selected
            if (getSelectedIndices().length == 1) {
                getSimulator().refreshActions();
            }
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
                    isSelected, hasFocus());
            // ensure some space to the left of the label
            setBorder(this.emptyBorder);
            // set tool tips and special formats
            if (index == 0) {
                // set the first item (the current state indicator) to special
                // format
                if (!isSelected) {
                    // distinguish the current start graph name
                    result.setBackground(Color.LIGHT_GRAY);
                }
                setFont(getFont().deriveFont(Font.ITALIC));
                setToolTipText("Currently selected state of the simulation");
            } else if (value.toString().equals(getStartGraphName())) {
                setFont(getFont().deriveFont(Font.BOLD));
                setToolTipText("Current start graph");
            } else {
                setToolTipText("Potential start graph (right-click to get options)");
            }
            return result;
        }

        private final Border emptyBorder = new EmptyBorder(0, 3, 0, 0);
    }
}
