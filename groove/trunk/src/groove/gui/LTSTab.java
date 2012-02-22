/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.gui;

import static groove.gui.Options.SHOW_ANCHORS_OPTION;
import static groove.gui.Options.SHOW_LTS_OPTION;
import static groove.gui.Options.SHOW_PARTIAL_GTS_OPTION;
import static groove.gui.Options.SHOW_STATE_IDS_OPTION;
import static groove.gui.SimulatorModel.Change.GRAMMAR;
import static groove.gui.SimulatorModel.Change.GTS;
import static groove.gui.SimulatorModel.Change.MATCH;
import static groove.gui.SimulatorModel.Change.STATE;
import static groove.gui.jgraph.JGraphMode.PAN_MODE;
import static groove.gui.jgraph.JGraphMode.SELECT_MODE;
import groove.gui.SimulatorModel.Change;
import groove.gui.action.ActionStore;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.JAttr;
import groove.gui.jgraph.LTSJEdge;
import groove.gui.jgraph.LTSJGraph;
import groove.gui.jgraph.LTSJModel;
import groove.gui.jgraph.LTSJVertex;
import groove.lts.GTS;
import groove.lts.GTSAdapter;
import groove.lts.GraphState;
import groove.lts.GraphState.Flag;
import groove.lts.GraphTransition;
import groove.lts.MatchResult;
import groove.lts.RuleTransition;
import groove.view.GrammarModel;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

/**
 * Window that displays and controls the LTS.
 * @author Arend Rensink
 * @version $Revision: 3525 $
 */
public class LTSTab extends JGraphPanel<LTSJGraph> implements
        SimulatorListener, groove.gui.Display.Tab {
    /** Creates a LTS panel for a given simulator. */
    public LTSTab(LTSDisplay display) {
        super(new LTSJGraph(display.getSimulator()), true);
        this.display = display;
        getJGraph().setToolTipEnabled(true);
        setEnabledBackground(JAttr.STATE_BACKGROUND);
        initialise();
    }

    @Override
    public LTSDisplay getDisplay() {
        return this.display;
    }

    @Override
    public Icon getIcon() {
        return Icons.LTS_MODE_ICON;
    }

    @Override
    public String getTitle() {
        return "State Space";
    }

    @Override
    public TabLabel getTabLabel() {
        if (this.tabLabel == null) {
            TabLabel result =
                new TabLabel(getDisplay(), this, getIcon(), "State Space");
            this.tabLabel = result;
        }
        return this.tabLabel;
    }

    @Override
    public boolean isEditor() {
        return false;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void updateGrammar(GrammarModel grammar) {
        // do nothing
    }

    /**
     * Used locally in this file, and gets the option for show/hide LTS 
     */
    public boolean getOptionValue(String option) {
        return getOptions().getItem(option).isEnabled()
            && getOptions().isSelected(option);
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        addRefreshListener(SHOW_ANCHORS_OPTION);
        addRefreshListener(SHOW_STATE_IDS_OPTION);
        addRefreshListener(SHOW_PARTIAL_GTS_OPTION);
        addShowHideListener();
        getJGraph().addMouseListener(new MyMouseListener());
        getSimulatorModel().addListener(this, GRAMMAR, GTS, STATE, MATCH);
    }

    /**
     * It draws lts or disables the lts tab depending on the value
     * of SHOW_LTS_OPTION 
     */
    public void showHideLts() {
        if (getOptionValue(SHOW_LTS_OPTION)) {
            LTSJModel ltsModel = null;
            ltsModel = getJGraph().newModel();
            GTS gts = getSimulatorModel().getGts();
            if (gts != null) {
                ltsModel.loadGraph(gts);
                setJModel(ltsModel);
                getJGraph().freeze();
                getJGraph().getLayouter().start(false);
            }
            getJGraph().setVisible(true);
            getJGraph().setEnabled(true);
        } else {
            getJGraph().setEnabled(false);
            getJGraph().setVisible(false);
        }
    }

    /**
    * It toggles the value of SHOW_LTS_OPTION 
    */
    public void toggleShowLts() {
        this.display.getLtsJGraph().toggleShowHideMode();
        getOptions().setSelected(SHOW_LTS_OPTION,
            this.display.getLtsJGraph().getShowHideMode());
        showHideLts();
        return;
    }

    /**
     * It handles the event coming from LTS hide/show checkbox in the view menu 
     */
    @Override
    protected void refresh() {
        if (getOptionValue(SHOW_LTS_OPTION) != this.display.getLtsJGraph().getShowHideMode()) {
            this.display.getLtsJGraph().toggleShowHideMode();
            showHideLts();
        }
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        if (source.getGts() != null && !getOptionValue(SHOW_LTS_OPTION)) {
            return;
        }
        if (changes.contains(GTS) || changes.contains(GRAMMAR)) {
            GTS gts = source.getGts();
            if (gts == null) {
                setJModel(null);
                setEnabled(false);
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
                    ltsModel = getJGraph().newModel();
                    ltsModel.loadGraph(gts);
                    setJModel(ltsModel);
                } else {
                    ltsModel = getJModel();
                    // (re)load the GTS if it is not the same size as the model
                    if (ltsModel.size() != gts.size()) {
                        ltsModel.loadGraph(gts);
                    }
                }
                getJGraph().freeze();
                getJGraph().getLayouter().start(false);
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
        }
        if (changes.contains(STATE) || changes.contains(MATCH)) {
            GraphState state = source.getState();
            RuleTransition transition = source.getTransition();
            if (getJModel() != null) {
                getJGraph().setActive(state, transition);
            }
        }
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

    /** Returns the action store permanently associated with the simulator. */
    private ActionStore getActions() {
        return getDisplay().getActions();
    }

    /** The tab label for this tab. */
    private TabLabel tabLabel;
    private final LTSDisplay display;
    /**
     * The graph listener permanently associated with this panel.
     */
    private final MyLTSListener ltsListener = new MyLTSListener();

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
                    getDisplay().selectStateTab();
                } else {
                    // scale from screen to model
                    java.awt.Point loc = evt.getPoint();
                    // find cell in model coordinates
                    GraphJCell cell =
                        getJGraph().getFirstCellForLocation(loc.x, loc.y);
                    if (cell instanceof LTSJEdge) {
                        GraphTransition edge = ((LTSJEdge) cell).getEdge();
                        MatchResult match = edge.getSteps().iterator().next();
                        getSimulatorModel().setMatch(match);
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
        public void statusUpdate(GTS lts, GraphState closed, Flag flag) {
            if (getJModel() == null) {
                return;
            }
            GraphJCell jCell = getJModel().getJCellForNode(closed);
            // during automatic generation, we do not always have vertices for
            // all states
            if (jCell != null) {
                jCell.refreshAttributes();
            }
            refreshStatus();
        }
    }

}