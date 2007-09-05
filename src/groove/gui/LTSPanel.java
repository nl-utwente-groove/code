/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: LTSPanel.java,v 1.16 2007-09-05 14:12:42 rensink Exp $
 */
package groove.gui;

import static groove.gui.Options.SHOW_ANCHORS_OPTION;
import static groove.gui.Options.SHOW_STATE_IDS_OPTION;
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
import groove.view.DefaultGrammarView;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;

/**
 * Window that displays and controls the current lts graph. Auxiliary class for
 * Simulator.
 * 
 * @author Arend Rensink
 * @version $Revision: 1.16 $ $Date: 2007-09-05 14:12:42 $
 */
public class LTSPanel extends JGraphPanel<LTSJGraph> implements SimulationListener {
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
     * @param grammar
     *                 the new grammar
     */
    public synchronized void setGrammarUpdate(DefaultGrammarView grammar) {
        setGTS(null);
        getJGraph().setModel(LTSJModel.EMPTY_LTS_JMODEL);
        getJGraph().getFilteredLabels().clear();
        setEnabled(false);
        refreshStatus();
    }

	public synchronized void startSimulationUpdate(GTS gts) {
        setGTS(gts);
		getJGraph().setModel(LTSJModel.newInstance(gts, getOptions()));
		setStateUpdate(gts.startState());
		setEnabled(true);
		refreshStatus();
	}
//
//	/**
//	 * Callback method to create a fresh JModel for a given lts. If the lts is
//	 * <code>null</code>, returns {@link #EMPTY_JMODEL}.
//	 */
//	protected LTSJModel createJModel(LTS lts) {
//		return lts == null ? EMPTY_JMODEL : LTSJModel.newInstance(lts, getOptions());
//	}

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
    public synchronized void setRuleUpdate(NameLabel name) {
        if (isGTSactivated()) {
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
     * Sets the value of the gts field, and changes the subject of the GTS listener.
     * The return value indicates if this changes the value.
     * @param gts the new value for the gts fiels; may be <code>null</code>
     * @return <code>true</code> if the new value differs from the old
     */
    private boolean setGTS(GTS gts) {
    	boolean result = gts != this.gts;
    	if (result) {
    		if (this.gts != null) {
    			this.gts.removeGraphListener(ltsListener);
    		} if (gts != null) {
    			gts.addGraphListener(ltsListener);
    		}
    	}
    	this.gts = gts;
    	return result;
    }
    
    /**
     * Indicates if an LTS is currently loaded.
     * This may fail to be the case if there is no grammar loaded,
     * or if the loaded grammar has no start state.
     */
    private boolean isGTSactivated() {
        return gts != null;
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
            text.append(gts.nodeCount());
            text.append(" states");
            if (gts.openStateCount() > 0) {
            	text.append(" ("+gts.openStateCount()+" open)");
            }
            text.append(", ");
            text.append(gts.edgeCount());
            text.append(" transitions");
        }
        return text.toString();
    }
    
    
    @Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		simulator.setGraphPanelEnabled(this, enabled);
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
    private final LTSListener ltsListener = new MyLTSListener(); 
    
    /**
     * Listener that makes sure the panel status gets updated when the
     * LYS is extended.
     */
    private class MyLTSListener extends LTSAdapter {
        /**
         * May only be called with the current lts as first parameter. Updates the
         * frame title by showing the number of nodes and edges.
         */
    	@Override
        public void addUpdate(GraphShape graph, Node node) {
            assert graph == gts : "I want to listen only to my lts";
            refreshStatus();
        }

        /**
         * May only be called with the current lts as first parameter. Updates the
         * frame title by showing the number of nodes and edges.
         */
    	@Override
        public void addUpdate(GraphShape graph, Edge edge) {
            assert graph == gts : "I want to listen only to my lts";
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
    }
    
	/** 
	 * Mouse listener that creates the popup menu and switches the view to 
	 * the rule panel on double-clicks.
	 */
	private class MyMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent evt) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
            	if (! isEnabled() && simulator.getStartSimulationAction().isEnabled()) {
            		simulator.startSimulation(simulator.getCurrentGrammar());
            	} else if (evt.getClickCount() == 2) {
            		simulator.setGraphPanel(simulator.getStatePanel());
            	} 
            }
        }
    }
}
