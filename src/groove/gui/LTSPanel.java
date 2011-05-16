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
 * $Id: LTSPanel.java,v 1.21 2008-02-05 13:28:06 rensink Exp $
 */
package groove.gui;

import static groove.gui.Options.SHOW_ANCHORS_OPTION;
import static groove.gui.Options.SHOW_STATE_IDS_OPTION;
import static groove.gui.jgraph.JGraphMode.PAN_MODE;
import static groove.gui.jgraph.JGraphMode.SELECT_MODE;
import groove.graph.Element;
import groove.gui.SimulatorModel.Change;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.JGraphMode;
import groove.gui.jgraph.LTSJEdge;
import groove.gui.jgraph.LTSJGraph;
import groove.gui.jgraph.LTSJModel;
import groove.gui.jgraph.LTSJVertex;
import groove.lts.GTS;
import groove.lts.GTSAdapter;
import groove.lts.GraphState;
import groove.lts.GraphTransition;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JToolBar;

/**
 * Window that displays and controls the current lts graph. Auxiliary class for
 * Simulator.
 * 
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-05 13:28:06 $
 */
public class LTSPanel extends JGraphPanel<LTSJGraph> implements
        SimulatorListener {

    /** Creates a LTS panel for a given simulator. */
    public LTSPanel(Simulator simulator) {
        super(new LTSJGraph(simulator), true);
        getJGraph().setToolTipEnabled(true);
        initialise();
    }

    @Override
    protected JToolBar createToolBar() {
        JToolBar result = new JToolBar();
        result.add(getSimulator().getStartSimulationAction());
        result.add(getSimulator().getExploreAction());
        result.add(getSimulator().getSaveGraphAction());
        result.addSeparator();
        result.add(getJGraph().getModeButton(JGraphMode.SELECT_MODE));
        result.add(getJGraph().getModeButton(JGraphMode.PAN_MODE));
        return result;
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        addRefreshListener(SHOW_ANCHORS_OPTION);
        addRefreshListener(SHOW_STATE_IDS_OPTION);
        getJGraph().addMouseListener(new MyMouseListener());
        getSimulatorModel().addListener(this);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        getJGraph().getModeAction(SELECT_MODE).setEnabled(enabled);
        getJGraph().getModeAction(PAN_MODE).setEnabled(enabled);
        if (enabled) {
            getJGraph().getModeButton(SELECT_MODE).doClick();
        }
    }

    /**
     * Specialises the return type to a {@link LTSJModel}.
     */
    @Override
    public LTSJModel getJModel() {
        if (getJGraph().isEnabled()) {
            return getJGraph().getModel();
        } else {
            return null;
        }
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        if (changes.contains(Change.GTS)) {
            GTS gts = source.getGts();
            if (gts == null) {
                LTSJModel newModel = getJGraph().newModel();
                getJGraph().getFilteredLabels().clear();
                getJGraph().setModel(newModel);
                setGTS(gts);
                setEnabled(false);
            } else {
                if (gts != oldModel.getGts()) {
                    LTSJModel newModel = getJGraph().newModel();
                    newModel.loadGraph(gts);
                    getJGraph().setModel(newModel);
                    setGTS(gts);
                }
                getJGraph().freeze();
                getJGraph().getLayouter().start(false);
                getJGraph().setActive(source.getState(), source.getTransition());
                setEnabled(true);
            }
            refreshStatus();
        } else if (changes.contains(Change.STATE)
            || changes.contains(Change.TRANS)) {
            GraphState state = source.getState();
            GraphTransition transition = source.getTransition();
            getJGraph().setActive(state, transition);
            //            List<Object> selectedCells = new ArrayList<Object>();
            //            if (source.getSelectedState() != null) {
            //                selectedCells.add(getJGraph().getModel().getJCellForNode(
            //                    source.getSelectedState()));
            //            }
            //            if (source.getSelectedTransition() != null) {
            //                selectedCells.add(getJGraph().getModel().getJCellForEdge(
            //                    source.getSelectedTransition()));
            //            }
            //            if (!selectedCells.isEmpty()) {
            //                getJGraph().setSelectionCells(selectedCells.toArray());
            //            }
            conditionalScrollTo(transition == null ? state : transition);
        }
    }

    /**
     * Sets the value of the gts field, and changes the subject of the GTS
     * listener. The return value indicates if this changes the value.
     * @param gts the new value for the gts field; may be <code>null</code>
     * @return <code>true</code> if the new value differs from the old
     */
    private boolean setGTS(GTS gts) {
        boolean result = gts != this.gts;
        if (result) {
            if (this.gts != null) {
                this.gts.removeLTSListener(this.ltsListener);
            }
            if (gts != null) {
                gts.addLTSListener(this.ltsListener);
            }
        }
        this.gts = gts;
        return result;
    }

    /**
     * @return Returns the gts.
     */
    final GTS getGTS() {
        return this.gts;
    }

    /**
     * Indicates if an LTS is currently loaded. This may fail to be the case if
     * there is no grammar loaded, or if the loaded grammar has no start state.
     */
    private boolean isGTSactivated() {
        return this.gts != null;
    }

    /**
     * Writes a line to the status bar.
     */
    @Override
    protected String getStatusText() {
        StringBuilder text = new StringBuilder();
        if (!isGTSactivated()) {
            text.append("No start state loaded");
        } else {
            text.append("Currently explored: ");
            text.append(this.gts.nodeCount());
            text.append(" states");
            if (this.gts.openStateCount() > 0 || this.gts.hasFinalStates()) {
                text.append(" (");
                if (this.gts.openStateCount() > 0) {
                    text.append(this.gts.openStateCount() + " open");
                    if (this.gts.hasFinalStates()) {
                        text.append(", ");
                    }
                }
                if (this.gts.hasFinalStates()) {
                    text.append(this.gts.getFinalStates().size() + " final");
                }
                text.append(")");
            }
            text.append(", ");
            text.append(this.gts.edgeCount());
            text.append(" transitions");
        }
        return text.toString();
    }

    /***
     * Only scroll when the panel is visible.
     */
    private void conditionalScrollTo(Element nodeOrEdge) {
        if (this.isVisible) {
            getJGraph().scrollTo(nodeOrEdge);
        }
    }

    /**
     * Shows a given counterexample by emphasising the states in the LTS panel.
     * Returns a message to be displayed in a dialog.
     * @param counterExamples the collection of states that do not satisfy the
     *        property verified
     * @param inTransitions flag to indicate that the canonical incoming transition
     * should also be highlighted.
     */
    public void emphasiseStates(List<GraphState> counterExamples,
            boolean inTransitions) {
        // reset lts display visibility
        if (this.isVisible) {
            getSimulator().switchTabs(this);
        }
        Set<GraphJCell> jCells = new HashSet<GraphJCell>();
        for (int i = 0; i < counterExamples.size(); i++) {
            GraphState state = counterExamples.get(i);
            jCells.add(getJModel().getJCellForNode(state));
            if (inTransitions && i + 1 < counterExamples.size()) {
                // find transition to next state
                for (GraphTransition trans : state.getTransitionSet()) {
                    if (trans.target() == counterExamples.get(i + 1)) {
                        jCells.add(getJModel().getJCellForEdge(trans));
                        break;
                    }
                }
            }
        }
        getJGraph().setSelectionCells(jCells.toArray());
    }

    /**
     * The underlying lts of ltsJModel.
     * 
     * @invariant lts == ltsJModel.graph()
     */
    private GTS gts;

    /**
     * The graph listener permanently associated with this exploration strategy.
     */
    private final MyLTSListener ltsListener = new MyLTSListener();

    /** Indicator of the visibility of the LTSPanel. */
    private boolean isVisible = true;

    /***
     * Notifies the LTSPanel that its visibility (inclusion in the GUI) has
     * changed. The visibility changes the behavior of scrolling only (nothing
     * else is affected).
     */
    public void setGUIVisibility(boolean visible) {
        this.isVisible = visible;
    }

    /**
     * Listener that makes sure the panel status gets updated when the LYS is
     * extended.
     */
    private class MyLTSListener extends GTSAdapter {
        /** Empty constructor with the correct visibility. */
        MyLTSListener() {
            // empty
        }

        /**
         * May only be called with the current lts as first parameter. Updates
         * the frame title by showing the number of nodes and edges.
         */
        @Override
        public void addUpdate(GTS gts, GraphState state) {
            assert gts == getGTS() : "I want to listen only to my lts";
            refreshStatus();
        }

        /**
         * May only be called with the current lts as first parameter. Updates
         * the frame title by showing the number of nodes and edges.
         */
        @Override
        public void addUpdate(GTS gts, GraphTransition transition) {
            assert gts == getGTS() : "I want to listen only to my lts";
            refreshStatus();
        }

        /**
         * If a state is closed, its background should be reset.
         */
        @Override
        public void closeUpdate(GTS lts, GraphState closed) {
            GraphJCell jCell = getJModel().getJCellForNode(closed);
            // during automatic generation, we do not always have vertices for
            // all states
            if (jCell != null) {
                jCell.refreshAttributes();
            }
            refreshStatus();
        }
    }

    /**
     * Mouse listener that creates the popup menu and switches the view to the
     * rule panel on double-clicks.
     */
    private class MyMouseListener extends MouseAdapter {
        /** Empty constructor with the correct visibility. */
        MyMouseListener() {
            // empty
        }

        @Override
        public void mouseClicked(MouseEvent evt) {
            if (getJGraph().getMode() == SELECT_MODE
                && evt.getButton() == MouseEvent.BUTTON1) {
                if (!isEnabled()
                    && getSimulator().getStartSimulationAction().isEnabled()) {
                    getSimulator().startSimulation();
                } else if (evt.isControlDown()) {
                    getSimulator().switchTabs(getSimulator().getStatePanel());
                } else {
                    // scale from screen to model
                    java.awt.Point loc = evt.getPoint();
                    // find cell in model coordinates
                    GraphJCell cell =
                        getJGraph().getFirstCellForLocation(loc.x, loc.y);
                    if (cell instanceof LTSJEdge) {
                        GraphTransition edge = ((LTSJEdge) cell).getEdge();
                        getSimulatorModel().setTransition(edge);
                    } else if (cell instanceof LTSJVertex) {
                        GraphState node = ((LTSJVertex) cell).getNode();
                        getSimulatorModel().setState(node);
                        if (evt.getClickCount() == 2) {
                            getSimulatorModel().exploreState(node);
                        }
                    }
                }
            }
        }
    }
}
