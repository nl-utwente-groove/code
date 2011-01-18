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
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.LTSJEdge;
import groove.gui.jgraph.LTSJGraph;
import groove.gui.jgraph.LTSJModel;
import groove.gui.jgraph.LTSJVertex;
import groove.lts.GTS;
import groove.lts.GTSAdapter;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.RuleMatch;
import groove.trans.RuleName;
import groove.util.Groove;
import groove.view.StoredGrammarView;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
        getJGraph().setEnabled(false);
        MyMouseListener mouseListener = new MyMouseListener();
        getJGraph().addMouseListener(mouseListener);
        getJGraph().addMouseMotionListener(mouseListener);
        getJGraph().addMouseWheelListener(mouseListener);
        getJGraph().addJGraphModeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                getScrollPane().setWheelScrollingEnabled(
                    evt.getNewValue() == SELECT_MODE);
            }
        });
        addRefreshListener(SHOW_ANCHORS_OPTION);
        addRefreshListener(SHOW_STATE_IDS_OPTION);
        simulator.addSimulationListener(this);
        getJGraph().setToolTipEnabled(true);
        initialise();
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

    /**
     * Sets the underlying grammar.
     * 
     * @param grammar the new grammar
     */
    public synchronized void setGrammarUpdate(StoredGrammarView grammar) {
        setGTS(null);
        LTSJModel newModel = getJGraph().newModel();
        getJGraph().setModel(newModel);
        getJGraph().getFilteredLabels().clear();
        setEnabled(false);
        refreshStatus();
    }

    public synchronized void startSimulationUpdate(GTS gts) {
        setGTS(gts);
        LTSJModel newModel = getJGraph().newModel();
        newModel.loadGraph(gts);
        getJGraph().setModel(newModel);
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
        getJGraph().setActive(state, null);
        // we do layouting here because it's too expensive to do it
        // every time a new state is added
        if (this.ltsListener.stateAdded && getJGraph().getLayouter() != null) {
            getJGraph().freeze();
            getJGraph().getLayouter().start(false);
            this.ltsListener.stateAdded = false;
        }
        // getJGraph().scrollTo(state);
        conditionalScrollTo(state);
    }

    /**
     * Sets the LTS emphasis attributes for the LTS edge and its source node
     * corresponding to the new derivation. Also removes the current emphasis,
     * if any. Scrolls the view to the newly emphasised edge.
     */
    public synchronized void setTransitionUpdate(GraphTransition transition) {
        getJGraph().setActive(transition.source(), transition);
        conditionalScrollTo(getJGraph().getActiveTransition());
    }

    /**
     * Removes the emphasis from the currently emphasised edge, if any.
     */
    public void setMatchUpdate(RuleMatch match) {
        if (isGTSactivated()) {
            getJGraph().setActive(getSimulator().getCurrentState(), null);
        }
    }

    /**
     * Removes the emphasis from the currently emphasised edge, if any.
     */
    public synchronized void setRuleUpdate(RuleName name) {
        if (isGTSactivated()) {
            getJGraph().setActive(getSimulator().getCurrentState(), null);
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
     * @param allStates flag to indicate if all states (or just the start state)
     *        should be emphasised
     * @return message describing the size of the counterexample
     */
    protected String emphasiseStates(Set<GraphState> counterExamples,
            boolean allStates) {
        if (!allStates) {
            GraphState initial = getGTS().startState();
            boolean initialIsCounterexample = counterExamples.contains(initial);
            counterExamples = new HashSet<GraphState>();
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
        if (this.isVisible) {
            getSimulator().setGraphPanel(this);
        }
        Set<GraphJCell> jCells = new HashSet<GraphJCell>();
        for (GraphState counterExample : counterExamples) {
            jCells.add(getJModel().getJCellForNode(counterExample));
        }
        getJGraph().setSelectionCells(jCells.toArray());
        //        getJModel().setEmphasised(jCells);
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
            this.stateAdded = true;
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
        public void mousePressed(MouseEvent e) {
            if (getJGraph().getMode() == PAN_MODE
                && e.getButton() == MouseEvent.BUTTON1) {
                this.origX = e.getX();
                this.origY = e.getY();
                getJGraph().setCursor(Groove.CLOSED_HAND_CURSOR);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (getJGraph().getMode() == PAN_MODE
                && e.getButton() == MouseEvent.BUTTON1) {
                this.origX = -1;
                this.origY = -1;
                getJGraph().setCursor(Groove.OPEN_HAND_CURSOR);
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (getJGraph().getMode() == PAN_MODE) {
                int change = -e.getWheelRotation();
                getJGraph().changeScale(change);
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (getJGraph().getMode() == PAN_MODE) {
                if (this.origX == -1) {
                    return; // never happens ??
                }
                Point p = getScrollPane().getViewport().getViewPosition();
                p.x -= (e.getX() - this.origX);
                p.y -= (e.getY() - this.origY);

                Dimension size = getJGraph().getSize();
                Dimension vsize = getScrollPane().getViewport().getExtentSize();

                if (p.x + vsize.width > size.width) {
                    p.x = size.width - vsize.width;
                }
                if (p.y + vsize.height > size.height) {
                    p.y = size.height - vsize.height;
                }
                if (p.x < 0) {
                    p.x = 0;
                }
                if (p.y < 0) {
                    p.y = 0;
                }
                getScrollPane().getViewport().setViewPosition(p);
            }
        }

        @Override
        public void mouseClicked(MouseEvent evt) {
            if (getJGraph().getMode() == SELECT_MODE
                && evt.getButton() == MouseEvent.BUTTON1) {
                if (!isEnabled()
                    && getSimulator().getStartSimulationAction().isEnabled()) {
                    getSimulator().startSimulation();
                } else if (evt.isControlDown()) {
                    getSimulator().setGraphPanel(getSimulator().getStatePanel());
                } else {
                    // scale from screen to model
                    java.awt.Point loc = evt.getPoint();
                    // find cell in model coordinates
                    GraphJCell cell =
                        getJGraph().getFirstCellForLocation(loc.x, loc.y);
                    if (cell instanceof LTSJEdge) {
                        GraphTransition edge = ((LTSJEdge) cell).getEdge();
                        getSimulator().setTransition(edge);
                    } else if (cell instanceof LTSJVertex) {
                        GraphState node = ((LTSJVertex) cell).getNode();
                        if (!getSimulator().getCurrentState().equals(node)) {
                            getSimulator().setState(node);
                        }
                        if (evt.getClickCount() == 2) {
                            getSimulator().exploreState(node);
                        }
                    }
                }
            }
        }

        /** The coordinates of a point where panning started. */
        private int origX = -1, origY = -1;
    }
}
