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
import static groove.gui.SimulatorModel.Change.GRAMMAR;
import static groove.gui.SimulatorModel.Change.GTS;
import static groove.gui.SimulatorModel.Change.MATCH;
import static groove.gui.SimulatorModel.Change.STATE;
import static groove.gui.jgraph.JGraphMode.PAN_MODE;
import static groove.gui.jgraph.JGraphMode.SELECT_MODE;
import groove.gui.DisplaysPanel.DisplayKind;
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
import groove.view.StoredGrammarView;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

/**
 * Window that displays and controls the current lts graph. Auxiliary class for
 * Simulator.
 * 
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-05 13:28:06 $
 */
public class LTSDisplay extends JGraphPanel<LTSJGraph> implements
        SimulatorListener, Display {

    /** Creates a LTS panel for a given simulator. */
    public LTSDisplay(Simulator simulator) {
        super(new LTSJGraph(simulator), true);
        getJGraph().setToolTipEnabled(true);
        initialise();
    }

    @Override
    public JPanel getListPanel() {
        return null;
    }

    @Override
    public JComponent getPanel() {
        return this;
    }

    @Override
    public DisplayKind getKind() {
        return DisplayKind.LTS;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean disposeAllEditors() {
        // there is nothing to be disposed
        return true;
    }

    @Override
    protected JToolBar createToolBar() {
        JToolBar result = Options.createToolBar();
        result.add(getActions().getSaveLTSAsAction());
        result.addSeparator();
        result.add(getActions().getStartSimulationAction());
        result.add(getActions().getApplyTransitionAction());
        result.add(getActions().getExploreAction());
        result.addSeparator();
        result.add(getActions().getBackAction());
        result.add(getActions().getForwardAction());
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
        getSimulatorModel().addListener(this, GRAMMAR, GTS, STATE, MATCH);
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
        if (changes.contains(GTS) || changes.contains(GRAMMAR)) {
            GTS gts = source.getGts();
            if (gts == null) {
                LTSJModel newLtsModel = getJGraph().newModel();
                getJGraph().getFilteredLabels().clear();
                getJGraph().setModel(newLtsModel);
                setEnabled(false);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        StoredGrammarView grammar =
                            getSimulatorModel().getGrammar();
                        if (grammar != null && grammar.getErrors().isEmpty()) {
                            getActions().getStartSimulationAction().execute();
                        }
                    }
                });
            } else {
                LTSJModel ltsModel;
                if (gts != oldModel.getGts()) {
                    ltsModel = getJGraph().newModel();
                    getJGraph().setModel(ltsModel);
                } else {
                    ltsModel = getJGraph().getModel();
                }
                // (re)load the GTS if it is not the same size as the model
                if (ltsModel.size() != gts.size()) {
                    ltsModel.loadGraph(gts);
                }
                getJGraph().freeze();
                getJGraph().getLayouter().start(false);
                getJGraph().setActive(source.getState(), source.getTransition());
                setEnabled(true);
            }
            if (gts != oldModel.getGts()) {
                if (oldModel.getGts() != null) {
                    oldModel.getGts().removeLTSListener(this.ltsListener);
                }
                if (gts != null) {
                    gts.addLTSListener(this.ltsListener);
                }
            }
            refreshStatus();
        } else if (changes.contains(STATE) || changes.contains(MATCH)) {
            GraphState state = source.getState();
            GraphTransition transition = source.getTransition();
            getJGraph().setActive(state, transition);
        }
    }

    /**
     * Writes a line to the status bar.
     */
    @Override
    protected String getStatusText() {
        StringBuilder text = new StringBuilder();
        GTS gts = getSimulatorModel().getGts();
        if (gts == null) {
            text.append("No start state loaded");
        } else {
            text.append("Currently explored: ");
            text.append(gts.nodeCount());
            text.append(" states");
            if (gts.openStateCount() > 0 || gts.hasFinalStates()) {
                text.append(" (");
                if (gts.openStateCount() > 0) {
                    text.append(gts.openStateCount() + " open");
                    if (gts.hasFinalStates()) {
                        text.append(", ");
                    }
                }
                if (gts.hasFinalStates()) {
                    text.append(gts.getFinalStates().size() + " final");
                }
                text.append(")");
            }
            text.append(", ");
            text.append(gts.edgeCount());
            text.append(" transitions");
        }
        return text.toString();
    }

    /**
     * Shows a given counterexample by emphasising the states in the LTS panel.
     * Returns a message to be displayed in a dialog.
     * @param counterExamples the collection of states that do not satisfy the
     *        property verified
     * @param showTransitions flag to indicate that the canonical incoming transition
     * should also be highlighted.
     */
    public void emphasiseStates(List<GraphState> counterExamples,
            boolean showTransitions) {
        Set<GraphJCell> jCells = new HashSet<GraphJCell>();
        for (int i = 0; i < counterExamples.size(); i++) {
            GraphState state = counterExamples.get(i);
            jCells.add(getJModel().getJCellForNode(state));
            if (showTransitions && i + 1 < counterExamples.size()) {
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
     * The graph listener permanently associated with this exploration strategy.
     */
    private final MyLTSListener ltsListener = new MyLTSListener();

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
            assert gts == getSimulatorModel().getGts() : "I want to listen only to my lts";
            refreshStatus();
        }

        /**
         * May only be called with the current lts as first parameter. Updates
         * the frame title by showing the number of nodes and edges.
         */
        @Override
        public void addUpdate(GTS gts, GraphTransition transition) {
            assert gts == getSimulatorModel().getGts() : "I want to listen only to my lts";
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
                    && getActions().getStartSimulationAction().isEnabled()) {
                    getActions().getStartSimulationAction().execute();
                } else if (evt.isControlDown()) {
                    getSimulatorModel().setDisplay(DisplayKind.HOST);
                } else {
                    // scale from screen to model
                    java.awt.Point loc = evt.getPoint();
                    // find cell in model coordinates
                    GraphJCell cell =
                        getJGraph().getFirstCellForLocation(loc.x, loc.y);
                    if (cell instanceof LTSJEdge) {
                        GraphTransition edge = ((LTSJEdge) cell).getEdge();
                        getSimulatorModel().setMatch(edge);
                    } else if (cell instanceof LTSJVertex) {
                        GraphState node = ((LTSJVertex) cell).getNode();
                        getSimulatorModel().setState(node);
                        if (evt.getClickCount() == 2) {
                            getSimulatorModel().doExploreState();
                        }
                    }
                }
            }
        }
    }
}
