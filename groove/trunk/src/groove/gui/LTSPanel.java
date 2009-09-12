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
import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.gui.jgraph.AbstrLTSJModel;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.LTSJGraph;
import groove.gui.jgraph.LTSJModel;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.LTS;
import groove.lts.LTSAdapter;
import groove.lts.State;
import groove.trans.RuleMatch;
import groove.trans.RuleName;
import groove.view.StoredGrammarView;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Window that displays and controls the current lts graph. Auxiliary class for
 * Simulator.
 * 
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-05 13:28:06 $
 */
public class LTSPanel extends JGraphPanel<LTSJGraph> implements
        SimulationListener {

    /** Creates a LTS panel for a given simulator. */
    public LTSPanel(Simulator simulator) {
        super(new LTSJGraph(simulator), true, simulator.getOptions());
        this.simulator = simulator;
        getJGraph().addMouseListener(new MyMouseListener());
        addRefreshListener(SHOW_ANCHORS_OPTION);
        addRefreshListener(SHOW_STATE_IDS_OPTION);
        simulator.addSimulationListener(this);
        getJGraph().setToolTipEnabled(true);
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

    /**
     * Sets the underlying grammar.
     * 
     * @param grammar the new grammar
     */
    public synchronized void setGrammarUpdate(StoredGrammarView grammar) {
        setGTS(null);
        getJGraph().setModel(LTSJModel.EMPTY_LTS_JMODEL);
        getJGraph().getFilteredLabels().clear();
        setEnabled(false);
        refreshStatus();
    }

    public synchronized void startSimulationUpdate(GTS gts) {
        setGTS(gts);
        if (getSimulator().isAbstractSimulation()) {
            getJGraph().setModel(AbstrLTSJModel.newInstance(gts, getOptions()));
        } else {
            getJGraph().setModel(LTSJModel.newInstance(gts, getOptions()));
        }

        setStateUpdate(gts.startState());
        setEnabled(true);
        refreshStatus();
    }

    /**
     * Sets the LTS emphasis attributes for the LTS node corresponding to the
     * new state. Also removes the emphasis from the currently emphasised node
     * and edge, if any. Scrolls the view to the newly emphasised node.
     */
    public synchronized void setStateUpdate(GraphState state) {
        getJModel().setActive(state, null);
        // we do layouting here because it's too expensive to do it
        // every time a new state is added
        if (this.ltsListener.stateAdded && getJGraph().getLayouter() != null) {
            getJModel().freeze();
            getJGraph().getLayouter().start(false);
            this.ltsListener.stateAdded = false;
        }
        getJGraph().scrollTo(state);
    }

    /**
     * Sets the LTS emphasis attributes for the LTS edge and its source node
     * corresponding to the new derivation. Also removes the current emphasis,
     * if any. Scrolls the view to the newly emphasised edge.
     */
    public synchronized void setTransitionUpdate(GraphTransition transition) {
        getJModel().setActive(transition.source(), transition);
        if (getSimulator().isAbstractSimulation()) {
            getJGraph().scrollTo(getJModel().getActiveState());
        } else {
            getJGraph().scrollTo(getJModel().getActiveTransition());
        }
    }

    /**
     * Removes the emphasis from the currently emphasised edge, if any.
     */
    public void setMatchUpdate(RuleMatch match) {
        if (isGTSactivated()) {
            getJModel().setActive(getJModel().getActiveState(), null);
        }
    }

    /**
     * Removes the emphasis from the currently emphasised edge, if any.
     */
    public synchronized void setRuleUpdate(RuleName name) {
        if (isGTSactivated()) {
            getJModel().setActive(getJModel().getActiveState(), null);
        }
    }

    /**
     * Sets the lts as in <tt>setStateUpdate</tt> for the currently selected
     * derivation's cod state.
     */
    public synchronized void applyTransitionUpdate(GraphTransition transition) {
        setStateUpdate(transition.target());
    }

    /**
     * Sets the value of the gts field, and changes the subject of the GTS
     * listener. The return value indicates if this changes the value.
     * @param gts the new value for the gts fiels; may be <code>null</code>
     * @return <code>true</code> if the new value differs from the old
     */
    private boolean setGTS(GTS gts) {
        boolean result = gts != this.gts;
        if (result) {
            if (this.gts != null) {
                this.gts.removeGraphListener(this.ltsListener);
            }
            if (gts != null) {
                gts.addGraphListener(this.ltsListener);
            }
        }
        this.gts = gts;
        return result;
    }

    /**
     * @return Returns the gts.
     */
    final GTS getGts() {
        return this.gts;
    }

    /**
     * @return Returns the simulator.
     */
    final Simulator getSimulator() {
        return this.simulator;
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

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.simulator.setGraphPanelEnabled(this, enabled);
    }

    /**
     * Shows a given counterexample by emphasising the states in the LTS panel.
     * Returns a message to be displayed in a dialog.
     * @param counterExamples the collection of states that do not satisfy the
     *        property verified
     * @param allStates flag to indicate if all states (or just the start state)
     *        should be emphasised
     * @return message describing the size of the counterexample
     */
    protected String emphasiseStates(Set<State> counterExamples,
            boolean allStates) {
        if (!allStates) {
            State initial = getGts().startState();
            boolean initialIsCounterexample = counterExamples.contains(initial);
            counterExamples = new HashSet<State>();
            if (initialIsCounterexample) {
                counterExamples.add(initial);
            }
        }
        String message;
        if (counterExamples.isEmpty()) {
            message = "There were no counter-examples.";
        } else if (counterExamples.size() == 1) {
            message = "There was 1 counter-example.";
        } else {
            message =
                String.format("There were %d counter-examples.",
                    counterExamples.size());
        }
        // reset lts display visibility
        getSimulator().setGraphPanel(this);
        Set<JCell> jCells = new HashSet<JCell>();
        for (State counterExample : counterExamples) {
            jCells.add(getJModel().getJCell(counterExample));
        }
        getJModel().setEmphasized(jCells);
        return message;
    }

    /**
     * The underlying lts of ltsJModel.
     * 
     * @invariant lts == ltsJModel.graph()
     */
    private GTS gts;

    /** The simulator to which this panel belongs. */
    private final Simulator simulator;

    /** The graph lisener permanently associated with this exploration strategy. */
    private final MyLTSListener ltsListener = new MyLTSListener();

    /**
     * Listener that makes sure the panel status gets updated when the LYS is
     * extended.
     */
    private class MyLTSListener extends LTSAdapter {
        /** Empty constructor with the correct visibility. */
        MyLTSListener() {
            // empty
        }

        /**
         * May only be called with the current lts as first parameter. Updates
         * the frame title by showing the number of nodes and edges.
         */
        @Override
        public void addUpdate(GraphShape graph, Node node) {
            assert graph == getGts() : "I want to listen only to my lts";
            this.stateAdded = true;
            refreshStatus();
        }

        /**
         * May only be called with the current lts as first parameter. Updates
         * the frame title by showing the number of nodes and edges.
         */
        @Override
        public void addUpdate(GraphShape graph, Edge edge) {
            assert graph == getGts() : "I want to listen only to my lts";
            refreshStatus();
        }

        /**
         * If a state is closed, its background should be reset.
         */
        @Override
        public void closeUpdate(LTS graph, State closed) {
            JCell jCell = getJModel().getJCell(closed);
            // during automatic generation, we do not always have vertices for
            // all states
            if (jCell != null) {
                getJModel().refresh(Collections.singleton(jCell));
            }
            refreshStatus();
        }

        boolean stateAdded;
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
            if (evt.getButton() == MouseEvent.BUTTON1) {
                if (!isEnabled()
                    && getSimulator().getStartSimulationAction().isEnabled()) {
                    getSimulator().startSimulation();
                } else if (evt.isControlDown()) {
                    getSimulator().setGraphPanel(getSimulator().getStatePanel());
                }
            }
        }
    }
}
