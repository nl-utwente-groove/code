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
import groove.view.AspectualGraphView;
import groove.view.GrammarView;
import groove.view.StoredGrammarView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;

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
        this.setEnabled(false);
        //this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Multiple selection - mzimakova
        this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.setCellRenderer(new MyCellRenderer());
        this.addMouseListener(new MyMouseListener());
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
        if (index >= 0 && getCellBounds(index, index).contains(atPoint)) {
            result.addSeparator();
            result.add(this.simulator.getCopyGraphAction());
            result.add(this.simulator.getDeleteGraphAction());
            result.add(this.simulator.getRenameGraphAction());
            result.addSeparator();
            result.add(getSetStartGraphAction());
            result.add(getPreviewGraphAction());
            result.add(this.simulator.getEditGraphAction());
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
                new AbstractAction(Options.PREVIEW_ACTION_NAME) {
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
            //Multiple selection - mzimakova
            this.setSelectedIndex(this.getSelectedIndex());
            String selection = (String) this.getSelectedValue();
            AspectualGraphView graphView =
                this.simulator.getGrammarView().getGraphView(selection);
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
        // does nothing
    }

    public void setGrammarUpdate(StoredGrammarView grammar) {
        this.removeAll();
        this.setEnabled(false);
        if (grammar != null) {
            refreshStartGraphName();
            refreshList(false);
            this.setEnabled(true);
        }
    }

    public void setMatchUpdate(RuleMatch match) {
        // does nothing
    }

    public void setRuleUpdate(RuleName name) {
        // does nothing
    }

    public void setStateUpdate(GraphState state) {
        // does nothing
    }

    public void setTransitionUpdate(GraphTransition transition) {
        // does nothing
    }

    public void startSimulationUpdate(GTS gts) {
        // does nothing
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
        GrammarView grammar = this.simulator.getGrammarView();
        setList(grammar.getGraphNames(), keepSelection);
    }

    /**
     * Sets the {@link #startGraphName} field, based on the currently loaded
     * grammar.
     */
    private void refreshStartGraphName() {
        this.startGraphName =
            this.simulator.getGrammarView().getStartGraphName();
    }

    /**
     * Sets the list of names to a given set.
     * @param names the set of names to be displayed; will be ordered before
     *        display
     * @param keepSelection if <code>true</code>, attempts to reselect the name
     *        selected before refreshing.
     */
    private void setList(Set<String> names, boolean keepSelection) {
        Object currentSelection = getSelectedValue();
        Object[] sortedNames = names.toArray();
        Arrays.sort(sortedNames);
        this.setListData(sortedNames);
        if (keepSelection) {
            setSelectedValue(currentSelection, true);
        } else if (this.startGraphName != null) {
            setSelectedValue(this.startGraphName, true);
        }
    }

    // --------------
    // Private fields
    // --------------

    /**
     * The simulator to which this directory belongs.
     * @invariant simulator != null
     */
    private final Simulator simulator;
    /**
     * Name of the current start graph, if the start graph is taken from the
     * list; <code>null</code> if there is no start graph or it is not taken
     * from this list.
     */
    private String startGraphName;
    /** Action to set the start graph in the simulator. */
    private Action setStartGraphAction;
    /** Action to preview a graph. */
    private Action previewGraphAction;
    /**
     * The background colour of this component when it is enabled.
     */
    private Color enabledBackground;

    // -----------------------------------------
    // MyMouseListener Class
    // -----------------------------------------

    /** Class to deal with mouse events over the label list. */
    private class MyMouseListener extends MouseAdapter {

        /** Empty constructor with the correct visibility. */
        MyMouseListener() {
            // empty
        }

        @Override
        public void mouseClicked(MouseEvent evt) {
            int index = locationToIndex(evt.getPoint());
            if (evt.getClickCount() == 2) { // Left double click
                if (StateJList.this.isEnabled() && index >= 0) {
                    StateJList.this.doPreviewGraph();
                }
            } else if (evt.getClickCount() == 1
                && evt.getButton() == MouseEvent.BUTTON3) { // Right click
                // Determine if index was really selected
                if (index >= 0
                    && getCellBounds(index, index).contains(evt.getPoint())) {
                    //Multiple selection - mzimakova
                    if (StateJList.this.getSelectedIndices().length < 2) {
                      // Adjust list selection accordingly.
                      StateJList.this.setSelectedIndex(index);
                    }
                }
                createPopupMenu(evt.getPoint()).show(evt.getComponent(),
                    evt.getX(), evt.getY());
            }
        }
    }

    /**
     * Cell renderer that distinguishes the name corresponding to the current
     * start graph.
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
            // first the default functionality
            String s = value.toString();
            if (s.equals(StateJList.this.startGraphName)) {
                if (!isSelected) {
                    // distinguish the current start graph name
                    result.setBackground(Color.LIGHT_GRAY);
                }
                setToolTipText("Current start graph");
            } else {
                setToolTipText("Potential start graph (right-click to get options)");
            }
            return result;
        }
    }
}
