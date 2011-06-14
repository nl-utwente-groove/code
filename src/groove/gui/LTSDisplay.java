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

import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.JGraphMode;
import groove.gui.jgraph.LTSJGraph;
import groove.gui.jgraph.LTSJModel;
import groove.lts.GraphState;
import groove.lts.GraphTransition;

import java.awt.BorderLayout;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

/**
 * Window that displays and controls the current lts graph. Auxiliary class for
 * Simulator.
 * 
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-05 13:28:06 $
 */
public class LTSDisplay extends Display {
    /** Creates a LTS panel for a given simulator. */
    public LTSDisplay(Simulator simulator) {
        super(simulator, DisplayKind.LTS);
    }

    @Override
    protected JComponent createDisplayPanel() {
        return new LTSDisplayPanel();
    }

    @Override
    protected JTabbedPane createTabPane() {
        JTabbedPane result = new JTabbedPane(JTabbedPane.BOTTOM);
        result.add(getStateTab());
        result.setTabComponentAt(0, getStateTab().getTabLabel());
        result.add(getLTSTab());
        result.setTabComponentAt(1, getLTSTab().getTabLabel());
        return result;
    }

    @Override
    protected JComponent createList() {
        return new StateList(getSimulator());
    }

    @Override
    protected JToolBar createListToolBar() {
        JToolBar result = Options.createToolBar();
        result.add(getActions().getEditStateAction());
        result.add(getActions().getSaveStateAction());
        result.addSeparator();
        result.add(getActions().getBackAction());
        result.add(getActions().getForwardAction());
        return result;
    }

    private JToolBar createToolBar() {
        JToolBar result = Options.createToolBar();
        result.add(getActions().getSaveLTSAsAction());
        result.addSeparator();
        result.add(getActions().getStartSimulationAction());
        result.add(getActions().getApplyTransitionAction());
        result.add(getActions().getAnimateAction());
        result.add(getActions().getExploreAction());
        result.addSeparator();
        result.add(getActions().getBackAction());
        result.add(getActions().getForwardAction());
        result.addSeparator();
        result.add(getLtsJGraph().getModeButton(JGraphMode.SELECT_MODE));
        result.add(getLtsJGraph().getModeButton(JGraphMode.PAN_MODE));
        return result;
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
            jCells.add(getLtsModel().getJCellForNode(state));
            if (showTransitions && i + 1 < counterExamples.size()) {
                // find transition to next state
                for (GraphTransition trans : state.getTransitionSet()) {
                    if (trans.target() == counterExamples.get(i + 1)) {
                        jCells.add(getLtsModel().getJCellForEdge(trans));
                        break;
                    }
                }
            }
        }
        getLtsJGraph().setSelectionCells(jCells.toArray());
    }

    /** Returns the LTS tab on this display. */
    public LTSTab getLTSTab() {
        if (this.ltsTab == null) {
            this.ltsTab = new LTSTab(this);
        }
        return this.ltsTab;
    }

    /** Makes sure the state tab is showing on the display;
     * or if the state tab is detached, that the state window is in front. 
     */
    public void selectStateTab() {
        if (this.stateWindow == null) {
            getTabPane().setSelectedIndex(0);
        } else {
            this.stateWindow.toFront();
        }
    }

    /** Attaches the state tab to this display. */
    public void attachStateTab() {
        int index = 0;
        getTabPane().insertTab(null, null, getStateTab(), null, index);
        getTabPane().setTabComponentAt(index, getStateTab().getTabLabel());
        getTabPane().setSelectedIndex(index);
        this.stateWindow = null;
    }

    /** Detaches the state tab into its own window. */
    public void detachStateTab() {
        this.stateWindow = new DisplayWindow(getStateTab());
    }

    /** Returns the state tab on this display. */
    public StateTab getStateTab() {
        if (this.stateTab == null) {
            this.stateTab = new StateTab(this);
        }
        return this.stateTab;
    }

    /** Returns the LTS' JGraph. */
    public LTSJGraph getLtsJGraph() {
        return getLTSTab().getJGraph();
    }

    /** Returns the model of the LTS' JGraph. */
    public LTSJModel getLtsModel() {
        return getLtsJGraph().getModel();
    }

    private LTSTab ltsTab;
    private StateTab stateTab;
    private DisplayWindow stateWindow;

    private class LTSDisplayPanel extends JPanel implements Panel {
        public LTSDisplayPanel() {
            super(new BorderLayout());
            add(createToolBar(), BorderLayout.NORTH);
            add(getTabPane());
        }

        @Override
        public Display getDisplay() {
            return LTSDisplay.this;
        }
    }
}
