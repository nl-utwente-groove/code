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
 * $Id: StatePanel.java,v 1.2 2007-03-27 14:18:34 rensink Exp $
 */
package groove.gui;

import static groove.gui.Options.SHOW_NODE_IDS_OPTION;
import static groove.gui.Options.SHOW_ASPECTS_OPTION;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.StateJGraph;
import groove.gui.layout.LayoutMap;
import groove.lts.GTS;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.State;
import groove.trans.NameLabel;
import groove.trans.view.RuleViewGrammar;
import groove.util.Groove;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jgraph.graph.GraphConstants;

/**
 * Window that displays and controls the current state graph. Auxiliary class for Simulator.
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public class StatePanel extends JGraphPanel<StateJGraph> implements SimulationListener {
	/** Display name of this panel. */
    protected static final String FRAME_NAME = "Current state";

    // --------------------- INSTANCE DEFINITIONS ----------------------

    /**
     * Constructs a new state panel.
     */
    public StatePanel(final Simulator simulator) {
        super(new StateJGraph(simulator), true, simulator.getOptions());
        simulator.addSimulationListener(this);
        addOptionListener(SHOW_NODE_IDS_OPTION, createNodeIdsOptionListener());
        addOptionListener(SHOW_ASPECTS_OPTION, createAspectsOptionListener());
        getJGraph().setToolTipEnabled(true);
        // make sure that emphasis due to selections in the label list
        // cause any selected transition to be deselected first
        getJGraph().getLabelList().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (currentTransition != null) {
                    simulator.setRule(currentTransition.getRule().getName());
                }
            }
        });
    }

    /**
     * Specialises the return type to {@link GraphJModel}.
     */
    @Override
    public GraphJModel getJModel() {
        if (jGraph.isEnabled()) {
            return getJGraph().getModel();
        } else {
            return null;
        }
    }

    /**
     * Sets the underlying model of this state frame to the initial graph of the new grammar.
     */
    public synchronized void setGrammarUpdate(RuleViewGrammar grammar) {
        stateJModelMap.clear();
        currentTransition = null;
        GTS gts = grammar.gts();
        if (gts == null) {
            jGraph.setModel(GraphJModel.EMPTY_JMODEL);
            setEnabled(false);
            setStatus("No state selected");
        } else {
            GraphState startState = grammar.gts().startState();
            GraphJModel stateJModel = getStateJModel(startState);
            // since the GTS states have lost their layout information, we try to
            // retrieve it from the grammar start graph
            LayoutMap<Node,Edge> layoutMap = GraphInfo.getLayoutMap(grammar.getStartGraph());
            if (layoutMap != null) {
                stateJModel.applyLayout(layoutMap);
            }
            jGraph.setModel(stateJModel);
            setStatus("" + startState);
        }
    }

    /**
     * Sets the underlying model of this state frame to a new graph. Creates a state model for the
     * new graph, if it was not displayed before. Also stops and restarts the layouter as required.
     * @param state the new underlying state graph
     * @require state instanceof GraphState
     */
    public synchronized void setStateUpdate(GraphState state) {
        // stop layouting of current model, if any
        if (getJModel() != null && currentTransition != null) {
        	getJModel().clearEmphasized();
            currentTransition = null;
        }
        // set the graph model to the new state
        jGraph.setModel(getStateJModel(state));
        setStatus("" + state);
    }

    /**
     * Resets the emphasis in the state model and the current derivation.
     */
    public synchronized void setRuleUpdate(NameLabel rule) {
        if (currentTransition != null) {
            getJModel().clearEmphasized();
            setStatus("" + currentTransition.source());
            currentTransition = null;
        }
    }

    /**
     * Changes the current state display by emphasizing the target of a given direct derivation.
     * Emphasis is by fat lines.
     */
    public synchronized void setTransitionUpdate(GraphTransition trans) {
        jGraph.getLabelList().clearSelection();
        Set<Element> emphElems = new HashSet<Element>();
        if (currentTransition != trans) {
            currentTransition = trans;
            Graph sourceGraph = trans.source().getGraph();
            // check if we're in the right state to display the derivation
            if (!getJModel().graph().equals(sourceGraph)) {
                // get a model for the new graph and set it
                jGraph.setModel(getStateJModel(trans.source()));
            }
            // now emphasize at will
            Morphism match = currentTransition.matching();
            assert match != null: "Transition "+currentTransition+" should have valid matching";
            assert match.nodeMap() != null : "Matching "+match+" has no node map";
            for (Node matchedNode: match.nodeMap().values()) {
                emphElems.add(matchedNode);
            }
            assert match.edgeMap() != null : "Matching "+match+" has no edge map";
            for (Edge matchedEdge: match.edgeMap().values()) {
                emphElems.add(matchedEdge);
            }
            if (!emphElems.isEmpty()) {
                //FIXME: in the case of attributed graphs, it can happen that we match elements that
                // are not in the graph physically. These elements then also do not have any jgraph-component
                // associated. Therefore, we only add JCells of graph elements that can be emphasized. 
            	Rectangle scope = Groove.toRectangle(getJGraph().getElementBounds(emphElems));
            	if (scope != null)
            		jGraph.scrollRectToVisible(scope);
            }
            setStatus("" + trans.source() + " (with match of " + currentTransition.getRule().getName() + ")");
        }
        GraphJModel currentModel = getJModel();
        currentModel.setEmphasized(currentModel.getJCellSet(emphElems));
    }

    /**
     * Sets the state to the current derivation's cod. Copies and freezes the bounds from the
     * current state and starts layout to find positions for newly created nodes.
     */
    public synchronized void applyTransitionUpdate(GraphTransition transition) {
        // first normalize currently displayed graph
        // (note that the requirements of this method guarantee that there is one)
    	GraphJModel currentModel = getJModel();
    	currentModel.clearEmphasized();
        // get a graph model for the target state
        GraphState newState = transition.target();
        GraphJModel newStateJModel = getStateJModel(newState);
        currentTransition = null;
        // set the graph model to the new state
        jGraph.setModel(newStateJModel);
        setStatus("" + newState);
    }

	/**
	 * Callback method to create a listner for the {@link #SHOW_NODE_IDS_OPTION} option.
	 */
	protected ChangeListener createNodeIdsOptionListener() {
		return new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				boolean newState = ((JCheckBoxMenuItem) e.getSource()).getState();
				for (GraphJModel jModel: stateJModelMap.values()) {
					jModel.setShowNodeIdentities(newState);
				}
				getJGraph().refreshView();
			}
		};
	}

	/**
	 * Callback method to create a listner for the {@link #SHOW_ANCHORS_OPTION} option.
	 */
	protected ChangeListener createAspectsOptionListener() {
		return new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				boolean newState = ((JCheckBoxMenuItem) e.getSource()).getState();
				for (GraphJModel jModel: stateJModelMap.values()) {
					jModel.setShowAspects(newState);
				}
				getJGraph().refreshView();
			}
		};
	}
    
    /**
     * Sets the text ion the status bar
     */
    protected void setStatus(String text) {
        if (text.length() == 0) {
            statusBar.setText(FRAME_NAME);
        } else {
            statusBar.setText(FRAME_NAME + ": " + text);
        }
    }

    /**
     * Returns a graph model for a given state graph. The graph model is retrieved from
     * stateJModelMap; if there is no image for the requested state then one is created.
     */
    protected GraphJModel getStateJModel(GraphState state) {
        GraphJModel result = stateJModelMap.get(state);
        if (result == null) {
            result = computeStateJModel(state);
            stateJModelMap.put(state, result);
        }
        return result;
    }

	/**
	 * Computes a fresh GraphJModel for a given graph state.
	 */
	protected GraphJModel computeStateJModel(GraphState state) {
		GraphJModel result;
		result = new GraphJModel(state.getGraph());
		result.setShowNodeIdentities(getOptions().getValue(SHOW_NODE_IDS_OPTION));
		result.setShowAspects(getOptions().getValue(SHOW_ASPECTS_OPTION));
		// try to find layout information for the state
		if (state instanceof GraphNextState) {
			GraphState oldState = ((GraphNextState) state).source();
			Morphism morphism = ((GraphNextState) state).morphism();
			// walk back along the derivation chain to find one for
			// which we have a state model (and hence layout information)
			while (!stateJModelMap.containsKey(oldState)
					&& oldState instanceof GraphNextState) {
		        morphism = ((GraphNextState) oldState).morphism().then(morphism);
				oldState = ((GraphNextState) oldState).source();
			}
			GraphJModel oldJModel = stateJModelMap.get(oldState);
			if (oldJModel != null) {
				copyLayout(oldJModel, result, morphism);
			}
		}
		return result;
	}

    /**
	 * Copies the layout information from the current j-model to a new one,
	 * modulo a mapping from the nodes and edges of the underlying graphs.
	 * @param oldStateJModel the model to copy the layout information from
	 * @param newStateJModel the model to copy the layout information to
	 * @param derivationMap mapping from the nodes and edges of the old to the new j-model
	 */
	private void copyLayout(GraphJModel oldStateJModel, GraphJModel newStateJModel, NodeEdgeMap derivationMap) {
	    for (Map.Entry<Node,Node> entry: derivationMap.nodeMap().entrySet()) {
	        JCell sourceCell = oldStateJModel.getJCell(entry.getKey());
	        JCell targetCell = newStateJModel.getJCell(entry.getValue());
	        assert targetCell != null : "Target element "+entry.getValue()+" unknown";
	        Rectangle2D sourceBounds = GraphConstants.getBounds(sourceCell.getAttributes());
	        GraphConstants.setBounds(targetCell.getAttributes(), sourceBounds);
	        newStateJModel.removeLayoutable(targetCell);
	    }
	    for (Map.Entry<Edge,Edge> entry: derivationMap.edgeMap().entrySet()) {
	        JCell sourceCell = oldStateJModel.getJCell(entry.getKey());
	        JCell targetCell = newStateJModel.getJCell(entry.getValue());
	        assert targetCell != null : "Target element "+entry.getValue()+" unknown";
	        List<?> sourcePoints = GraphConstants.getPoints(sourceCell.getAttributes());
	        if (sourcePoints != null) {
	        	GraphConstants.setPoints(targetCell.getAttributes(), new LinkedList<Object>(sourcePoints));
	        }
	        newStateJModel.removeLayoutable(targetCell);
	    }
	}
	
    /**
     * Mapping from state graphs to the corresponding graph models.
     * @invariant stateJModelMap: State --> GraphJModel
     */
    protected final Map<State,GraphJModel> stateJModelMap = new HashMap<State,GraphJModel>();
    /**
     * The currently activated transition.
     * @invariant currentTransition.source() == stateJModel.graph()
     */
    protected GraphTransition currentTransition;
}