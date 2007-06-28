package groove.gui.jgraph;

import groove.control.ControlAutomaton;
import groove.control.ControlState;
import groove.control.ControlTransition;
import groove.graph.BinaryEdge;
import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.graph.Label;
import groove.graph.Node;
import groove.gui.Options;
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

public class ControlJModel extends GraphJModel {

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
	 *  Creates an empty ControlJModel
	 */
	public ControlJModel()
	{
		super();
	}
	
	public ControlJModel(GraphShape shape, Options options)
	{
		super(shape, LTS_NODE_ATTR, LTS_EDGE_ATTR, options);
		this.reload();
	}

	@Override
	public ControlAutomaton getGraph()
	{
		return (ControlAutomaton) super.getGraph();
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

	/** Dummy LTS model. */
	static public final ControlJModel EMPTY_CONTROL_JMODEL = new ControlJModel();
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
			super(ControlJModel.this, edge);
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
	    		ControlTransition trans = (ControlTransition) part;
	    		String description;
    			description = trans.label().text();
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
	}

	/**
	 * JVertex class that describes the underlying node as a graph state.
	 * @author Arend Rensink
	 * @version $Revision $
	 */
    private class StateJVertex extends GraphJVertex {
    	/** 
    	 * Creates a new instance for a given node (required to be a {@link ControlState})
    	 * in an LTS model.
    	 */
		StateJVertex(ControlJModel jModel, Node node) {
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
		 * Specialises the return type to {@link ControlState}.
		 */
		@Override
		public ControlState getNode() {
			return (ControlState) super.getNode();
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
			ControlAutomaton lts = getGraph();
			Set<String> result = new HashSet<String>();
			if (lts.startState().equals(getNode())) {
				result.add(LTS.START_LABEL_TEXT);
			}
			if (lts.isFinal(getNode())) {
				result.add(LTS.FINAL_LABEL_TEXT);
			}
			return result;
		}

		@Override
		public StringBuilder getLine(Edge edge) {
			return Converter.toHtml(new StringBuilder(getListLabel(edge)));
		}
	}
}
