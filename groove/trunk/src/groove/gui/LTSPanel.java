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
 * $Id: LTSPanel.java,v 1.3 2007-03-30 15:50:35 rensink Exp $
 */
package groove.gui;

import static groove.gui.Options.SHOW_ANCHORS_OPTION;
import static groove.gui.jgraph.LTSJModel.EMPTY_JMODEL;

import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.LTSJGraph;
import groove.gui.jgraph.LTSJModel;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.LTS;
import groove.lts.LTSAdapter;
import groove.lts.LTSListener;
import groove.lts.State;
import groove.trans.NameLabel;

import java.util.Collections;

/**
 * Window that displays and controls the current lts graph. Auxiliary class for
 * Simulator.
 * 
 * @author Arend Rensink
 * @version $Revision: 1.3 $ $Date: 2007-03-30 15:50:35 $
 */
public class LTSPanel extends JGraphPanel<LTSJGraph> implements SimulationListener {
    /** Creates a LTS panel for a given simulator. */
    public LTSPanel(Simulator simulator) {
        super(new LTSJGraph(simulator), true, simulator.getOptions());
        addRefreshListener(SHOW_ANCHORS_OPTION);
        simulator.addSimulationListener(this);
        jGraph.setToolTipEnabled(true);
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
     * @param gts
     *                 the new grammar
     */
    public synchronized void setGrammarUpdate(GTS gts) {
        if (isLTSLoaded()) {
            lts.setFixed();
            lts.removeGraphListener(ltsListener);
        }
        lts = gts;
        getJGraph().setModel(createJModel(lts));
        if (lts == null) {
            setEnabled(false);
        } else {
            lts.addGraphListener(ltsListener);
            setStateUpdate((GraphState) lts.startState());
        }
        refreshStatus();
    }

	/**
	 * Callback method to create a fresh JModel for a given lts.
	 * If the lts is <code>null</code>, returns {@link #EMPTY_JMODEL}.
	 */
	protected LTSJModel createJModel(LTS lts) {
		return lts == null ? EMPTY_JMODEL : new LTSJModel(lts, getOptions());
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
//
//	/**
//	 * Callback factory method for a listener to the anchors show option.
//	 */
//	protected ChangeListener createAnchorsOptionListener() {
//		return new ChangeListener() {
//			public void stateChanged(ChangeEvent e) {
//				getJModel().refresh();
////				getJGraph().getLabelList().updateModel();
//			}
//		};
//	}

    /**
     * Writes a line to the status bar.
     */
    @Override
    protected String getStatusText() {
        String text;
        if (!isLTSLoaded()) {
            text = "No start state loaded";
        } else {
            text = "" + lts.nodeCount() + " nodes, " + lts.edgeCount() + " edges";
        }
        return text;
    }
    
    /**
     * The underlying lts of ltsJModel.
     * 
     * @invariant lts == ltsJModel.graph()
     */
    private LTS lts;

    /** The graph lisener permanently associated with this exploration strategy. */
    private final LTSListener ltsListener = new LTSAdapter() {
        /**
         * May only be called with the current lts as first parameter. Updates the
         * frame title by showing the number of nodes and edges.
         */
    	@Override
        public void addUpdate(GraphShape graph, Node node) {
            assert graph == lts : "I want to listen only to my lts";
            refreshStatus();
        }

        /**
         * May only be called with the current lts as first parameter. Updates the
         * frame title by showing the number of nodes and edges.
         */
    	@Override
        public void addUpdate(GraphShape graph, Edge edge) {
            assert graph == lts : "I want to listen only to my lts";
            refreshStatus();
        }

        /**
         * If a state is closed, its background should be reset.
         */
    	@Override
        public void closeUpdate(LTS graph, State closed) {
            JCell jCell = getJModel().getJCell(closed);
            // during automatic generation, we do not always have vertices for all states
            if (jCell != null) {
            	getJModel().refresh(Collections.singleton(jCell));
            }
            refreshStatus();
        }
    };
}
