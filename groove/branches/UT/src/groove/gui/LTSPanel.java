// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/* 
 * $Id: LTSPanel.java,v 1.1.1.1 2007-03-20 10:05:29 kastenberg Exp $
 */
package groove.gui;

import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JModel;
import groove.gui.jgraph.LTSJGraph;
import groove.gui.jgraph.LTSJModel;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.LTS;
import groove.lts.LTSAdapter;
import groove.lts.LTSListener;
import groove.lts.State;
import groove.trans.NameLabel;
import groove.trans.view.RuleViewGrammar;

import java.util.Collections;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Window that displays and controls the current lts graph. Auxiliary class for
 * Simulator.
 * 
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $ $Date: 2007-03-20 10:05:29 $
 */
public class LTSPanel extends JGraphPanel<LTSJGraph> implements SimulationListener {
    /**
     * Empty model, used in case there is no LTS to display.
     */
    protected static final JModel EMPTY_JMODEL = new LTSJModel();

    /** Creates a LTS panel for a given simulator. */
    public LTSPanel(Simulator simulator) {
        super(new LTSJGraph(simulator), true);
        this.simulator = simulator;
        simulator.addSimulationListener(this);
        jGraph.setToolTipEnabled(true);
    }

    /**
     * Specialises the return type to a {@link LTSJModel}.
     */
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
     * @param grammar
     *                 the new grammar
     */
    public synchronized void setGrammarUpdate(RuleViewGrammar grammar) {
        if (isLTSLoaded()) {
            lts.setFixed();
            lts.removeGraphListener(ltsListener);
        }
        lts = grammar.gts();
        if (lts == null) {
            getJGraph().setModel(EMPTY_JMODEL);
            setEnabled(false);
        } else {
            lts.addGraphListener(ltsListener);
            getJGraph().setModel(new LTSJModel(lts, simulator.getOptions()));
            setStateUpdate((GraphState) lts.startState());
        }
        setShowAnchorsOptionListener();
        updateStatus();
    }

    /**
     * Sets the LTS emphasis attributes for the LTS node curresponding to the
     * new state. Also removes the emphasis from the currently emphasized node
     * and edge, if any. Scrolls the view to the newly emphasized node.
     */
    public synchronized void setStateUpdate(GraphState state) {
        // first deemphasize the currently emphasized edge, if any
    	getJModel().setActiveTransition(null);
        // emphasize state if it isn't already done
        getJModel().setActiveState(state);
        // we do layouting here because it's too expensive to do it
        // every time a new state is added
        if (getJGraph().getLayouter() != null) {
        	getJModel().freeze();
        	getJGraph().getLayouter().start(false);
        }
        // addUpdate(lts, state);
        getJGraph().scrollTo(state);
    }

    /**
     * Sets the LTS emphasis attributes for the LTS edge and its source node
     * corresponding to the new derivation. Also removes the current emphasis,
     * if any. Scrolls the view to the newly emphasized edge.
     */
    public synchronized void setTransitionUpdate(GraphTransition transition) {
    	getJModel().setActiveState(transition.source());
        getJModel().setActiveTransition(transition);
        getJGraph().scrollTo(getJModel().getActiveTransition());
    }

    /**
     * Removes the emphasis from the currently emphasized edge, if any.
     */
    public void setRuleUpdate(NameLabel name) {
        if (isLTSLoaded()) {
        	getJModel().setActiveTransition(null);
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
     * Indicates if an LTS is currently loaded.
     * This may fail to be the case if there is no grammar loaded,
     * or if the loaded grammar has no start state.
     */
    public boolean isLTSLoaded() {
        return lts != null;
    }
    /**
     * Sets a listener to the anchor image option, if that has not yet been done.
     */
    protected void setShowAnchorsOptionListener() {
    	if (! showAnchorsOptionListenerSet) {
    		JCheckBoxMenuItem anchorImageOptionItem = simulator.getOptions().getItem(Options.SHOW_ANCHORS_OPTION);
    		if (anchorImageOptionItem != null) {
    	        // listen to the option controlling the rule anchor display
    			anchorImageOptionItem.addChangeListener(new ChangeListener() {
    				public void stateChanged(ChangeEvent e) {
    					getJModel().reload();
    					getJGraph().getLabelList().updateModel();
//    					getLTSJModel().refresh(getLTSJModel().getJCellSet(lts.edgeSet()));
//    					getJGraph().repaint();
    				}
    	        });
    	        showAnchorsOptionListenerSet = true;
    		}
    	}
    }

    /**
     * Writes a line to the status bar.
     */
    protected void updateStatus() {
        String text;
        if (!isLTSLoaded()) {
            text = "No start state loaded";
        } else {
            text = "" + lts.nodeCount() + " nodes, " + lts.edgeCount() + " edges";
        }
        getStatusBar().setText(text);
    }

    /**
     * The production simulator to which this frame belongs.
     */
    private final Simulator simulator;

    /**
     * The underlying lts of ltsJModel.
     * 
     * @invariant lts == ltsJModel.graph()
     */
    private LTS lts;
    /** Flag to indicate that the anchor image option listener has been set. */
    private boolean showAnchorsOptionListenerSet = false;

    /** The graph lisener permanently associated with this exploration strategy. */
    private final LTSListener ltsListener = new LTSAdapter() {
        /**
         * May only be called with the current lts as first parameter. Updates the
         * frame title by showing the number of nodes and edges.
         */
        public void addUpdate(GraphShape graph, Node node) {
            assert graph == lts : "I want to listen only to my lts";
            updateStatus();
        }

        /**
         * May only be called with the current lts as first parameter. Updates the
         * frame title by showing the number of nodes and edges.
         */
        public void addUpdate(GraphShape graph, Edge edge) {
            assert graph == lts : "I want to listen only to my lts";
            updateStatus();
        }

        /**
         * If a state is closed, its background should be reset.
         */
        public void closeUpdate(LTS graph, State closed) {
            JCell jCell = getJModel().getJCell(closed);
            // during automatic generation, we do not always have vertices for all states
            if (jCell != null) {
            	getJModel().refresh(Collections.singleton(jCell));
            }
        }
    };
}
