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
package groove.gui.display;

import static groove.gui.Options.SHOW_ANCHORS_OPTION;
import static groove.gui.Options.SHOW_ARROWS_ON_LABELS_OPTION;
import static groove.gui.Options.SHOW_PARTIAL_GTS_OPTION;
import static groove.gui.Options.SHOW_STATE_IDS_OPTION;
import static groove.gui.SimulatorModel.Change.GRAMMAR;
import static groove.gui.SimulatorModel.Change.GTS;
import static groove.gui.SimulatorModel.Change.MATCH;
import static groove.gui.SimulatorModel.Change.STATE;
import static groove.gui.jgraph.JGraphMode.PAN_MODE;
import static groove.gui.jgraph.JGraphMode.SELECT_MODE;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.SimulatorListener;
import groove.gui.SimulatorModel;
import groove.gui.SimulatorModel.Change;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.JAttr;
import groove.gui.jgraph.JGraphMode;
import groove.gui.jgraph.LTSJEdge;
import groove.gui.jgraph.LTSJGraph;
import groove.gui.jgraph.LTSJModel;
import groove.gui.jgraph.LTSJVertex;
import groove.gui.tree.LabelTree;
import groove.lts.GTS;
import groove.lts.GTSAdapter;
import groove.lts.GraphState;
import groove.lts.GraphState.Flag;
import groove.lts.GraphTransition;
import groove.view.GrammarModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Window that displays and controls the current lts graph. Auxiliary class for
 * Simulator.
 * 
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-05 13:28:06 $
 */
public class LTSDisplay extends Display implements SimulatorListener {
    /** Creates a LTS panel for a given simulator. */
    public LTSDisplay(Simulator simulator) {
        super(simulator, DisplayKind.LTS);
        setStateBound(100);
    }

    @Override
    protected void buildDisplay() {
        setLayout(new BorderLayout());
        JToolBar toolBar = Options.createToolBar();
        fillToolBar(toolBar);
        add(toolBar, BorderLayout.NORTH);
        add(getGraphPanel());
    }

    @Override
    protected void installListeners() {
        getJGraph().addMouseListener(new MyMouseListener());
        getSimulatorModel().addListener(this, GRAMMAR, GTS, STATE, MATCH);
    }

    @Override
    protected ListPanel createListPanel() {
        return null;
    }

    @Override
    protected JTree createList() {
        return null;
    }

    @Override
    protected JToolBar createListToolBar() {
        return null;
    }

    @Override
    protected JComponent createInfoPanel() {
        LabelTree labelTree = getJGraph().getLabelTree();
        TitledPanel result =
            new TitledPanel("Transition labels", labelTree,
                labelTree.createToolBar(), true);
        result.setEnabledBackground(JAttr.STATE_BACKGROUND);
        return result;
    }

    private void fillToolBar(JToolBar result) {
        result.removeAll();
        result.add(getActions().getExplorationDialogAction());
        result.addSeparator();
        result.add(getActions().getStartSimulationAction());
        result.add(getActions().getApplyMatchAction());
        result.add(getActions().getAnimateAction());
        result.add(getActions().getExploreAction());
        result.addSeparator();
        result.add(getActions().getBackAction());
        result.add(getActions().getForwardAction());
        result.addSeparator();
        result.add(getJGraph().getModeButton(JGraphMode.SELECT_MODE));
        result.add(getJGraph().getModeButton(JGraphMode.PAN_MODE));
        result.addSeparator();
        result.add(getFilterLTSButton());
        result.add(getBoundSpinnerPanel());
        result.add(Box.createGlue());
    }

    private JToggleButton getFilterLTSButton() {
        if (this.filterLTSButton == null) {
            this.filterLTSButton =
                Options.createToggleButton(getActions().getFilterLTSAction());
        }
        return this.filterLTSButton;
    }

    private JToggleButton filterLTSButton;

    /** Returns true if the LTS JGraph is filtered. */
    public boolean isFilteringLts() {
        return getFilterLTSButton().isSelected();
    }

    private JPanel getBoundSpinnerPanel() {
        JPanel result = this.boundSpinnerPanel;
        if (result == null) {
            result = new JPanel();
            result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));
            result.add(Box.createRigidArea(new Dimension(5, 0)));
            result.add(new JLabel("Show states up to"));
            result.add(Box.createRigidArea(new Dimension(5, 0)));
            result.add(getBoundSpinner());
            result.add(Box.createGlue());
            this.boundSpinnerPanel = result;
        }
        return this.boundSpinnerPanel;
    }

    private JPanel boundSpinnerPanel;

    private JSpinner getBoundSpinner() {
        if (this.boundSpinner == null) {
            this.boundSpinner = new JSpinner(getBoundSpinnerModel());
            this.boundSpinner.setMaximumSize(new Dimension(10, 100));
            this.boundSpinner.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if (getJModel() != null) {
                        getActions().getReloadLTSAction().execute();
                    }
                }
            });
        }
        return this.boundSpinner;
    }

    private JSpinner boundSpinner;

    private SpinnerNumberModel getBoundSpinnerModel() {
        if (this.boundSpinnerModel == null) {
            this.boundSpinnerModel = new SpinnerNumberModel();
            this.boundSpinnerModel.setMinimum(100);
            this.boundSpinnerModel.setMaximum(100000);
            this.boundSpinnerModel.setStepSize(100);
            this.boundSpinnerModel.setValue(100);
        }
        return this.boundSpinnerModel;
    }

    private SpinnerNumberModel boundSpinnerModel;

    /** Sets the maximum state number to be displayed. */
    public void setStateBound(int bound) {
        getBoundSpinnerModel().setValue(bound);
    }

    /** Retrieves the maximum state number to be displayed. */
    public int getStateBound() {
        return (Integer) getBoundSpinnerModel().getValue();
    }

    /* Also changes the enabled status of the spinner. */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        getBoundSpinner().setEnabled(enabled);
        ((NumberEditor) getBoundSpinner().getEditor()).getTextField().setBackground(
            enabled ? getGraphPanel().getEnabledBackground() : null);
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
        if (getJModel() == null) {
            return;
        }
        Set<GraphJCell> jCells = new HashSet<GraphJCell>();
        for (int i = 0; i < counterExamples.size(); i++) {
            GraphState state = counterExamples.get(i);
            jCells.add(getJModel().getJCellForNode(state));
            if (showTransitions && i + 1 < counterExamples.size()) {
                // find transition to next state
                for (GraphTransition trans : state.getTransitions(GraphTransition.Class.ANY)) {
                    if (trans.target() == counterExamples.get(i + 1)) {
                        jCells.add(getJModel().getJCellForEdge(trans));
                        break;
                    }
                }
            }
        }
        getJGraph().setSelectionCells(jCells.toArray());
    }

    /** Returns the LTS tab on this display. */
    public LTSGraphPanel getGraphPanel() {
        LTSGraphPanel result = this.graphPanel;
        if (result == null) {
            result = this.graphPanel = new LTSGraphPanel(getJGraph());
            result.initialise();
            result.addRefreshListener(SHOW_ANCHORS_OPTION);
            result.addRefreshListener(SHOW_STATE_IDS_OPTION);
            result.addRefreshListener(SHOW_PARTIAL_GTS_OPTION);
            result.addRefreshListener(SHOW_ARROWS_ON_LABELS_OPTION);
        }
        return result;
    }

    /** Returns the LTS' JGraph. */
    public LTSJGraph getJGraph() {
        LTSJGraph result = this.jGraph;
        if (result == null) {
            result = this.jGraph = new LTSJGraph(getSimulator());
        }
        return result;
    }

    /** Returns the model of the LTS' JGraph. */
    public LTSJModel getJModel() {
        return getJGraph().getModel();
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        if (changes.contains(GTS) || changes.contains(GRAMMAR)) {
            GTS gts = source.getGts();
            if (gts == null) {
                getJGraph().setModel(null);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        GrammarModel grammar = getSimulatorModel().getGrammar();
                        if (grammar != null && grammar.getErrors().isEmpty()) {
                            getActions().getStartSimulationAction().execute();
                        }
                    }
                });
            } else {
                LTSJModel ltsModel;
                if (gts != oldModel.getGts()) {
                    ltsModel = (LTSJModel) getJGraph().newModel();
                    ltsModel.setFiltering(isFilteringLts());
                    ltsModel.setStateBound(getStateBound());
                    ltsModel.loadGraph(gts);
                    getJGraph().setModel(ltsModel);
                } else {
                    ltsModel = getJModel();
                    // (re)load the GTS if it is not the same size as the model
                    if (ltsModel.size() != gts.size()) {
                        ltsModel.loadGraph(gts);
                    }
                    GraphState state = source.getState();
                    GraphTransition transition = source.getTransition();
                    getJGraph().setActive(state, transition);
                }
                getGraphPanel().refreshBackground();
                getJGraph().refreshFiltering();
                getJGraph().doLayout(true);
                setEnabled(true);
            }
            if (gts != oldModel.getGts()) {
                if (oldModel.getGts() != null) {
                    oldModel.getGts().removeLTSListener(this.ltsListener);
                }
                if (gts != null) {
                    gts.addLTSListener(this.ltsListener);
                    updateStatus(gts);
                }
            }
        }
        if (changes.contains(STATE) || changes.contains(MATCH)) {
            if (getJModel() != null) {
                GraphState state = source.getState();
                GraphTransition transition = source.getTransition();
                getJGraph().setActive(state, transition);
            }
        }
    }

    /**
     * Toggles the filtering of the LTS display.
     */
    public void toggleFilterLts() {
        boolean isFiltering = isFilteringLts();
        getJGraph().getModel().setFiltering(isFiltering);
        getJGraph().refreshFiltering();
        if (!isFiltering) {
            getJGraph().doLayout(false);
        }
        getGraphPanel().refreshBackground();
        setEnabled(true);
    }

    /**
     * The LTS listener permanently associated with this display.
     */
    private final MyLTSListener ltsListener = new MyLTSListener();
    private LTSGraphPanel graphPanel;
    private LTSJGraph jGraph;

    /** Returns an LTS display for a given simulator. */
    public static LTSDisplay newInstance(Simulator simulator) {
        LTSDisplay result = new LTSDisplay(simulator);
        result.buildDisplay();
        return result;
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
            assert gts == getSimulatorModel().getGts() : "I want to listen only to my lts";
            updateStatus(gts);
        }

        /**
         * May only be called with the current lts as first parameter. Updates
         * the frame title by showing the number of nodes and edges.
         */
        @Override
        public void addUpdate(GTS gts, GraphTransition transition) {
            assert gts == getSimulatorModel().getGts() : "I want to listen only to my lts";
            updateStatus(gts);
        }

        /**
         * If a state is closed, its background should be reset.
         */
        @Override
        public void statusUpdate(GTS gts, GraphState closed, Flag flag) {
            assert gts == getSimulatorModel().getGts() : "I want to listen only to my lts";
            updateStatus(gts);
        }
    }

    /**
     * Writes a line to the status bar.
     */
    private void updateStatus(GTS gts) {
        StringBuilder text = new StringBuilder();
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
        getGraphPanel().getStatusBar().setText(text.toString());
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
                } else {
                    // scale from screen to model
                    java.awt.Point loc = evt.getPoint();
                    // find cell in model coordinates
                    GraphJCell cell =
                        getJGraph().getFirstCellForLocation(loc.x, loc.y);
                    if (cell instanceof LTSJEdge) {
                        GraphTransition trans = ((LTSJEdge) cell).getEdge();
                        getSimulatorModel().setTransition(trans);
                    } else if (cell instanceof LTSJVertex) {
                        GraphState node = ((LTSJVertex) cell).getNode();
                        getSimulatorModel().setState(node);
                        if (evt.getClickCount() == 2) {
                            getActions().getExploreAction().doExploreState();
                        }
                    }
                }
            }
        }
    }

    /**
     * Window that displays and controls the LTS.
     * @author Arend Rensink
     * @version $Revision$
     */
    public class LTSGraphPanel extends JGraphPanel<LTSJGraph> {
        /** Creates a LTS panel for a given simulator. */
        public LTSGraphPanel(LTSJGraph jGraph) {
            super(jGraph);
            getJGraph().setToolTipEnabled(true);
            setEnabledBackground(JAttr.STATE_BACKGROUND);
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            getJGraph().getModeAction(SELECT_MODE).setEnabled(enabled);
            getJGraph().getModeAction(PAN_MODE).setEnabled(enabled);
            if (enabled) {
                getJGraph().getModeButton(SELECT_MODE).doClick();
            }
            LTSDisplay.this.setEnabled(enabled);
        }

        /**
         * Refreshes the background colour, based on the question whether the LTS is
         * filtered or incompletely displayed. 
         */
        public void refreshBackground() {
            boolean incomplete = isFilteringLts();
            LTSJModel jModel = getJGraph().getModel();
            if (!incomplete) {
                incomplete = jModel.size() < jModel.getGraph().size();
            }
            Color background =
                incomplete ? JAttr.FILTER_BACKGROUND : JAttr.STATE_BACKGROUND;
            setEnabledBackground(background);
            setEnabled(isEnabled());
        }
    }
}
