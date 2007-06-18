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
 * $Id: LTSJModel.java,v 1.15 2007-06-18 11:20:49 rensink Exp $
 */
package groove.gui.jgraph;

import groove.control.LocationTransition;
import groove.graph.BinaryEdge;
import groove.graph.Edge;
import groove.graph.Node;
import groove.gui.Options;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.LTS;
import groove.lts.State;
import groove.lts.Transition;
import groove.util.Converter;
import groove.util.Groove;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

/**
 * Graph model adding a concept of active state and transition,
 * with special visual characteristics.
 * @author Arend Rensink
 * @version $Revision: 1.15 $
 */
public class LTSJModel extends GraphJModel {
    /** Creates a new model from a given LTS and set of display options. */
    LTSJModel(LTS lts, Options options) {
        super(lts, LTS_NODE_ATTR, LTS_EDGE_ATTR, options);
    }
    
    /** Constructs a dummy, empty model. */
    private LTSJModel() {
    	// empty
    }
    
	/** Specialises the return type. */
    @Override
	public LTS getGraph() {
		return (LTS) super.getGraph();
	}

	/**
     * Returns the active transition of the LTS, if any.
     * The active transition is the one currently selected in the simulator.
     * Returns <tt>null</tt> if no transition is selected.
     */
    public Transition getActiveTransition() {
        return activeTransition;
    }

    /**
     * Returns the active state of the LTS, if any.
     * The active transition is the one currently displayed in the state frame.
     * Returns <tt>null</tt> if no state is active (which should occur only
     * if no grammar is loaded and hence the LTS is empty).
     */
    public State getActiveState() {
        return activeState;
    }

    /**
     * Sets the active transition to a new value, 
     * and returns the previous value.
     * Both old and new transitions may be <tt>null</tt>.
     * @param trans the new active transition
     * @return the old active transition
     */
    public Transition setActiveTransition(Transition trans) {
        Transition result = activeTransition;
        activeTransition = trans;
        Set<JCell> changedCells = new HashSet<JCell>();
        if (trans != null) {
        	JCell jCell = getJCell(trans);
        	assert jCell != null : String.format("No image for %s in jModel", trans);
            changedCells.add(jCell);
        }
        if (result != null) {
        	JCell jCell = getJCell(result);
        	assert jCell != null : String.format("No image for %s in jModel", result);
            changedCells.add(jCell);
        }
        refresh(changedCells);
        return result;
    }

    /**
     * Sets the active state to a new value, 
     * and returns the previous value.
     * Both old and new states may be <tt>null</tt>.
     * @param state the new active state
     * @return the old active state
     */
    public State setActiveState(State state) {
        State result = activeState;
        activeState = state;
        Set<JCell> changedCells = new HashSet<JCell>();
        if (state != null) {
            changedCells.add(getJCell(state));
        }
        if (result != null) {
            changedCells.add(getJCell(result));
        }
        refresh(changedCells);
        return result;
    }

	@Override
	public boolean isShowNodeIdentities() {
		return getOptionValue(Options.SHOW_STATE_IDS_OPTION);
	}

	/**
     * This implementation returns a {@link TransitionJEdge}.
     */
    @Override
	protected TransitionJEdge createJEdge(BinaryEdge edge) {
		return new TransitionJEdge(edge);
	}

    /**
     * This implementation returns a {@link StateJVertex}.
     */
	@Override
	protected StateJVertex createJVertex(Node node) {
		return new StateJVertex(this, node);
	}

	/** 
	 * This implementation adds special attributes for the start state,
	 * open states, final states, and the active state.
	 * @see #LTS_NODE_ATTR
	 * @see #LTS_START_NODE_ATTR
	 * @see #LTS_OPEN_NODE_ATTR
	 * @see #LTS_FINAL_NODE_ATTR
	 * @see #LTS_NODE_ACTIVE_CHANGE
	 */
	@Override
	protected AttributeMap createJVertexAttr(Node node) {
        AttributeMap result;
        State state = (State) node;
        if (state.equals(getGraph().startState())) {
            result = (AttributeMap) LTS_START_NODE_ATTR.clone();
        } else if (!state.isClosed()) {
            result = (AttributeMap) LTS_OPEN_NODE_ATTR.clone();
        } else if (getGraph().isFinal(state)) {
            result = (AttributeMap) LTS_FINAL_NODE_ATTR.clone();
        } else {
            result = (AttributeMap) LTS_NODE_ATTR.clone();
        }
        if (state.equals(activeState)) {
            result.applyMap(LTS_NODE_ACTIVE_CHANGE);
        }
        return result;
    }

	/**
	 * This implementation adds special attributes for the active transition.
	 * @see #LTS_EDGE_ATTR
	 * @see #LTS_EDGE_ACTIVE_CHANGE
	 */
	@Override
    protected AttributeMap createJEdgeAttr(Set<? extends Edge> edgeSet) {
        AttributeMap result = (AttributeMap) LTS_EDGE_ATTR.clone();
        if (activeTransition != null && edgeSet.contains(activeTransition)) {
            result.applyMap(LTS_EDGE_ACTIVE_CHANGE);
        }
        return result;
    }

    /** Adds the correct border emphasis. */
	@Override
	protected AttributeMap getJVertexEmphAttr(JVertex jCell) {
		AttributeMap result;
        State state = (State) ((GraphJVertex) jCell).getNode();
        if (state.equals(getActiveState())) {
        	result = LTS_ACTIVE_EMPH_NODE_CHANGE;
        } else {
        	result = super.getJVertexEmphAttr(jCell);
        }
        return result;
	}

	/**
     * This implementation checks if the edge to be added is a flag with special label
     * according to {@link #isSpecialLabel(String)}; if so, the edge is not added to the jmodel
     * and its source jcell is returned instead.
     */
	@Override
	protected boolean isSourceCompatible(Edge edge) {
		return isSpecialEdge(edge) || super.isSourceCompatible(edge);
    }
    
    /**
     * Tests if the edge is special in the sense of being a <i>flag</i> (orfor
     * legacy reasons a self-edge) with
     * a label that is special according to {@link #isSpecialLabel(String)}.
     * @see #isSpecialLabel(String)
     */
    protected boolean isSpecialEdge(Edge edge) {
        if (edge.endCount() == 1) {
            return isSpecialLabel(edge.label().text());
        } else if (edge.endCount() == 2 && edge.source().equals(edge.end(Edge.TARGET_INDEX))) {
            return isSpecialLabel(edge.label().text());
        } else {
            return false;
        }
    }
    
    /**
	 * Indicates whether a label, when occurring on a self-edge in the LTS,
	 * indicates a special role of its source/target node rather than modelling
	 * a transition. In that case the edge will probably not be displayed explicitly
	 * but rather through special attributes of its source node.
	 * @see #addEdge(Edge)
	 */
	protected boolean isSpecialLabel(String label) {
	    return specialLabels != null && specialLabels.contains(label);
	}

//	/**
//     * This implementation adds a label to the set if
//     * the j-vertex is the start state, an open state or a final state.
//     * @see LTS#START_LABEL_TEXT
//     * @see LTS#OPEN_LABEL_TEXT
//     * @see LTS#FINAL_LABEL_TEXT
//     */
//    @Override
//    protected Collection<String> getLabels(JVertex jCell) {
//        LTS lts = getGraph();
//        State state = (GraphState) ((GraphJVertex) jCell).getNode();
//        Set<String> result = new HashSet<String>();
//        if (lts.startState().equals(state)) {
//            result.add(LTS.START_LABEL_TEXT);
//        } 
//        if (!state.isClosed()) {
//            result.add(LTS.OPEN_LABEL_TEXT);
//        }
//        if (lts.isFinal(state)) {
//            result.add(LTS.FINAL_LABEL_TEXT);
//        }
//        return result;
//    }

    /**
     * The active state of the LTS.
     * Is null if there is no active state.
     * @invariant activeState == null || ltsJModel.graph().contains(activeState)
     */
    private State activeState;
    /**
     * The currently active transition of the LTS.
     * The source node of emphasizedEdge (if non-null) is also emphasized.
     * Is null if there is no currently emphasized edge.
     * @invariant activeTransition == null || ltsJModel.graph().contains(activeTransition)
     */
    private Transition activeTransition;

    /**
     * Set of special edge labels that, when occurring on self-edges, should not be displayed as edges in the LTS
     * but are instead mapped to the jvertex also representing the edge's end point.
     */
    private final Set<String> specialLabels = new HashSet<String>();
    {
        specialLabels.add(LTS.START_LABEL_TEXT);
        specialLabels.add(LTS.OPEN_LABEL_TEXT);
        specialLabels.add(LTS.FINAL_LABEL_TEXT);
    }

    /** 
     * Factory method for {@link LTSJModel}.
     * Creates and returns a new model from a given LTS and set of display options.
     * Returns {@link #EMPTY_LTS_JMODEL} if the LTS is <code>null</code>. 
     */
    static public LTSJModel newInstance(LTS lts, Options options) {
    	if (lts == null) {
    		return EMPTY_LTS_JMODEL;
    	} else {
			LTSJModel result = new LTSJModel(lts, options);
			result.reload();
			return result;
		}
    }
    
	/** Dummy LTS model. */
	static public final LTSJModel EMPTY_LTS_JMODEL = new LTSJModel();
    /** The default node attributes of the LTS */
    private static final AttributeMap LTS_NODE_ATTR;
    /** The start node attributes of the LTS */
    private static final AttributeMap LTS_START_NODE_ATTR;
    /** Unexplored node attributes */
    private static final AttributeMap LTS_OPEN_NODE_ATTR;
    /** Final node attributes */
    private static final AttributeMap LTS_FINAL_NODE_ATTR;
    /** The default edge attributes of the LTS */
    private static final AttributeMap LTS_EDGE_ATTR;

    /** Active node attributes of the LTS */
    private static final AttributeMap LTS_NODE_ACTIVE_CHANGE;
    /** Active edge attributes of the LTS */
    private static final AttributeMap LTS_EDGE_ACTIVE_CHANGE;
    /** Emphasized active node attributes of the LTS */
    private static final AttributeMap LTS_ACTIVE_EMPH_NODE_CHANGE;

    // set the emphasis attributes
    static {
        // active LTS nodes
        LTS_NODE_ACTIVE_CHANGE = new AttributeMap();
        GraphConstants.setBorder(LTS_NODE_ACTIVE_CHANGE, JAttr.LTS_ACTIVE_BORDER);
        GraphConstants.setLineColor(LTS_NODE_ACTIVE_CHANGE, JAttr.LTS_ACTIVE_COLOR);
        GraphConstants.setLineWidth(LTS_NODE_ACTIVE_CHANGE, JAttr.LTS_ACTIVE_WIDTH);
        // active LTS edges
        LTS_EDGE_ACTIVE_CHANGE = new AttributeMap();
        GraphConstants.setForeground(LTS_EDGE_ACTIVE_CHANGE, JAttr.LTS_ACTIVE_COLOR);
        GraphConstants.setLineColor(LTS_EDGE_ACTIVE_CHANGE, JAttr.LTS_ACTIVE_COLOR);
        GraphConstants.setLineWidth(LTS_EDGE_ACTIVE_CHANGE, JAttr.LTS_ACTIVE_WIDTH);

        // LTS nodes
        LTS_NODE_ATTR = (AttributeMap) JAttr.DEFAULT_NODE_ATTR.clone();
//        GraphConstants.setFont(LTS_NODE_ATTR, italicFont);
        // LTS start node
        LTS_START_NODE_ATTR = (AttributeMap) LTS_NODE_ATTR.clone();
        GraphConstants.setBackground(LTS_START_NODE_ATTR, JAttr.LTS_START_BACKGROUND);
        // LTS unexplored nodes
        LTS_OPEN_NODE_ATTR = (AttributeMap) LTS_NODE_ATTR.clone();
        GraphConstants.setBackground(LTS_OPEN_NODE_ATTR, JAttr.LTS_OPEN_BACKGROUND);
        // LTS final nodes
        LTS_FINAL_NODE_ATTR = (AttributeMap) LTS_NODE_ATTR.clone();
        GraphConstants.setBackground(LTS_FINAL_NODE_ATTR, JAttr.LTS_FINAL_BACKGROUND);
        LTS_ACTIVE_EMPH_NODE_CHANGE = new AttributeMap();
        GraphConstants.setBorder(LTS_ACTIVE_EMPH_NODE_CHANGE, JAttr.LTS_ACTIVE_EMPH_BORDER);
        GraphConstants.setLineWidth(LTS_ACTIVE_EMPH_NODE_CHANGE, JAttr.LTS_ACTIVE_WIDTH);
        // LTS edges
        LTS_EDGE_ATTR = (AttributeMap) JAttr.DEFAULT_EDGE_ATTR.clone();
        GraphConstants.setConnectable(LTS_EDGE_ATTR, false);
        GraphConstants.setDisconnectable(LTS_EDGE_ATTR, false);
        GraphConstants.setLineEnd(LTS_EDGE_ATTR, GraphConstants.ARROW_SIMPLE);
    }
	/**
	 * JEdge class that describes the underlying edge as a graph transition.
	 * @author Arend Rensink
	 * @version $Revision $
	 */
    private class TransitionJEdge extends GraphJEdge {
    	/** Creates a new instance from a given edge (required to be a {@link GraphTransition}). */
		TransitionJEdge(BinaryEdge edge) {
			super(LTSJModel.this, edge);
		}

		@Override
		StringBuilder getEdgeKindDescription() {
			return new StringBuilder("transition");
		}

		@Override
		String getLabelDescription() {
			StringBuffer result = new StringBuffer(", generated by ");
	    	String[] displayedLabels = new String[getUserObject().size()];
	    	int labelIndex = 0;
	    	for (Object part: getUserObject()) {
	    		GraphTransition trans = (GraphTransition) part;
	    		String description;
	    		if (isShowAnchors()) {
	    			description = trans.getEvent().toString();
	    		} else {
	    			description = trans.getEvent().getName().toString();
	    		}
	    		displayedLabels[labelIndex] = Converter.STRONG_TAG.on(description, true);
	    		labelIndex++;
	    	}
	    	if (displayedLabels.length == 1) {
	    		result.append(displayedLabels[0]);
	    	} else {
	    		result.append(Groove.toString(displayedLabels, "<br>- ", "", "<br>- "));
	    	}
			return result.toString();
		}

		@Override
		String getListLabel(Edge edge) {
			String result;
			assert edge instanceof GraphTransition || edge instanceof LocationTransition : "Edge set contains "
					+ edge;
			// fix for locationtransition (Tom Staijen: Control)
			if (isShowAnchors() || edge instanceof LocationTransition) {
				result = ((Transition) edge).label().text();
			} else {
				result = ((GraphTransition) edge).getEvent().getName().text();
			}
			return result;
		}

		@Override
		StringBuilder getLine(Edge edge) {
			return new StringBuilder(getListLabel(edge));
		}
	}

	/**
	 * JVertex class that describes the underlying node as a graph state.
	 * @author Arend Rensink
	 * @version $Revision $
	 */
    private class StateJVertex extends GraphJVertex {
    	/** 
    	 * Creates a new instance for a given node (required to be a {@link GraphState})
    	 * in an LTS model.
    	 */
		StateJVertex(LTSJModel jModel, Node node) {
			super(jModel, node, false);
		}

        /** A state is also visible if it is open, final, or the start state. */
        @Override
        public boolean isVisible() {
            return isSpecialNode() || super.isVisible();
        }

        /** Tests if the state is the start state, a final state, or not yet closed. */
        private boolean isSpecialNode() {
            LTS lts = getGraph();
            State state = getNode();
            return lts.startState().equals(state) || !state.isClosed() || lts.isFinal(state);
        }

        /**
		 * Specialises the return type to {@link GraphState}.
		 */
		@Override
		public GraphState getNode() {
			return (GraphState) super.getNode();
		}

		@Override
		StringBuilder getNodeDescription() {
			StringBuilder result = new StringBuilder("State ");
			result.append(Converter.UNDERLINE_TAG.on(getNode()));
			return result;
		}

		/**
	     * This implementation adds a label to the set if
	     * the j-vertex is the start state, an open state or a final state.
	     * @see LTS#START_LABEL_TEXT
	     * @see LTS#OPEN_LABEL_TEXT
	     * @see LTS#FINAL_LABEL_TEXT
	     */
		@Override
		public Collection<String> getPlainLabels() {
			LTS lts = getGraph();
			Set<String> result = new HashSet<String>();
			if (lts.startState().equals(getNode())) {
				result.add(LTS.START_LABEL_TEXT);
			}
			if (!getNode().isClosed()) {
				result.add(LTS.OPEN_LABEL_TEXT);
			}
			if (lts.isFinal(getNode())) {
				result.add(LTS.FINAL_LABEL_TEXT);
			}
			return result;
		}

		@Override
		String getListLabel(Edge edge) {
			String result;
			assert edge instanceof GraphTransition : "Edge set contains "
					+ edge;
			// fix for locationtransitions (Tom Staijen: Control)
			if (isShowAnchors() || edge instanceof LocationTransition) {
				result = ((Transition) edge).label().text();
			} else {
				result = ((GraphTransition) edge).getEvent().getName().text();
			}
			return result;
		}

		@Override
		StringBuilder getLine(Edge edge) {
			return Converter.toHtml(new StringBuilder(getListLabel(edge)));
		}
	}
}