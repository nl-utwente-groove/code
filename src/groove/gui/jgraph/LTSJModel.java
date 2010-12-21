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
 * $Id: LTSJModel.java,v 1.28 2008-03-05 16:49:24 rensink Exp $
 */
package groove.gui.jgraph;

import static groove.gui.jgraph.JAttr.LTS_ACTIVE_EMPH_NODE_CHANGE;
import static groove.gui.jgraph.JAttr.LTS_EDGE_ACTIVE_CHANGE;
import static groove.gui.jgraph.JAttr.LTS_EDGE_ATTR;
import static groove.gui.jgraph.JAttr.LTS_FINAL_NODE_ATTR;
import static groove.gui.jgraph.JAttr.LTS_NODE_ACTIVE_CHANGE;
import static groove.gui.jgraph.JAttr.LTS_NODE_ATTR;
import static groove.gui.jgraph.JAttr.LTS_OPEN_NODE_ATTR;
import static groove.gui.jgraph.JAttr.LTS_RESULT_NODE_ATTR;
import static groove.gui.jgraph.JAttr.LTS_START_NODE_ATTR;
import groove.graph.Label;
import groove.gui.Options;
import groove.lts.DerivationLabel;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.LTS;
import groove.lts.LTSListener;
import groove.util.Converter;
import groove.util.Groove;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jgraph.graph.AttributeMap;

/**
 * Graph model adding a concept of active state and transition, with special
 * visual characteristics.
 * @author Arend Rensink
 * @version $Revision$
 */
public class LTSJModel extends GraphJModel<GraphState,GraphTransition>
        implements LTSListener {
    /** Creates a new model from a given LTS and set of display options. */
    LTSJModel(GTS lts, Options options) {
        super(lts, LTS_NODE_ATTR, LTS_EDGE_ATTR, options);
    }

    /** Constructs a dummy, empty model. */
    protected LTSJModel() {
        // empty
    }

    /** Specialises the return type. */
    @Override
    public GTS getGraph() {
        return (GTS) super.getGraph();
    }

    /**
     * If the super call returns <code>null</code>, use
     * {@link #DEFAULT_LTS_NAME}.
     */
    @Override
    public String getName() {
        String result = super.getName();
        if (result == null) {
            result = DEFAULT_LTS_NAME;
        }
        return result;
    }

    /**
     * Reacts to a (node of edge) extension of the underlying Graph by mimicking
     * the change in the GraphModel. Can alse deal with NodeSet and EdgeSet
     * additions.
     */
    public synchronized void addUpdate(LTS lts, GraphState state) {
        initializeTransients();
        // add a corresponding GraphCell to the GraphModel
        addNode(state);
        // insert(cells.toArray(), connections, null, attributes);
        doInsert();
    }

    /**
     * Reacts to a (node of edge) extension of the underlying Graph by mimicking
     * the change in the GraphModel. Can alse deal with NodeSet and EdgeSet
     * additions.
     */
    public synchronized void addUpdate(LTS lts, GraphTransition transition) {
        initializeTransients();
        // note that (as per GraphListener contract)
        // source and target Nodes (if any) have already been added
        addEdge(transition);
        Object[] addedCells = toAddedJCellsArray();
        doInsert();
        // new edges should be behind the nodes
        toBack(addedCells);
    }

    @Override
    public void closeUpdate(LTS lts, GraphState explored) {
        // do nothing
    }

    @Override
    public void reload() {
        // temporarily remove the model as a graph listener
        getGraph().removeLTSListener(this);
        super.reload();
        // add the model as a graph listener
        getGraph().addLTSListener(this);
    }

    /**
     * Returns the active transition of the LTS, if any. The active transition
     * is the one currently selected in the simulator. Returns <tt>null</tt> if
     * no transition is selected.
     */
    public GraphTransition getActiveTransition() {
        return this.activeTransition;
    }

    /**
     * Returns the active state of the LTS, if any. The active transition is the
     * one currently displayed in the state frame. Returns <tt>null</tt> if no
     * state is active (which should occur only if no grammar is loaded and
     * hence the LTS is empty).
     */
    public GraphState getActiveState() {
        return this.activeState;
    }

    /**
     * Sets the active state and transition to a new value. Both old and new
     * values may be <tt>null</tt>.
     * @param state the new active state
     * @param trans the new active transition
     */
    public void setActive(GraphState state, GraphTransition trans) {
        Set<JCell> changedCells = new HashSet<JCell>();
        GraphTransition previousTrans = this.activeTransition;
        if (previousTrans != trans) {
            this.activeTransition = trans;
            if (trans != null) {
                JCell jCell = getJCellForEdge(trans);
                assert jCell != null : String.format(
                    "No image for %s in jModel", trans);
                changedCells.add(jCell);
            }
            if (previousTrans != null) {
                JCell jCell = getJCellForEdge(previousTrans);
                assert jCell != null : String.format(
                    "No image for %s in jModel", previousTrans);
                changedCells.add(jCell);
            }
        }
        GraphState previousState = this.activeState;
        // if (state != previousState) {
        this.activeState = state;
        if (state != null) {
            changedCells.add(getJCellForNode(state));
        }
        if (previousState != null) {
            changedCells.add(getJCellForNode(previousState));
        }
        // }
        if (!changedCells.isEmpty()) {
            refresh(changedCells);
        }
    }

    @Override
    public boolean isShowNodeIdentities() {
        return getOptionValue(Options.SHOW_STATE_IDS_OPTION);
    }

    /**
     * Node hiding doesn't mean much in the LTS, so always show the edges unless
     * explicitly filtered.
     */
    @Override
    boolean isShowUnfilteredEdges() {
        return true;
    }

    /**
     * This implementation returns a {@link LTSJModel.TransitionJEdge}.
     */
    @Override
    protected TransitionJEdge createJEdge(GraphTransition edge) {
        return new TransitionJEdge(edge);
    }

    /**
     * This implementation returns a {@link LTSJModel.StateJVertex}.
     */
    @Override
    protected StateJVertex createJVertex(GraphState node) {
        return new StateJVertex(this, node);
    }

    /**
     * This implementation adds special attributes for the start state, open
     * states, final states, and the active state.
     * @see JAttr#LTS_NODE_ATTR
     * @see JAttr#LTS_START_NODE_ATTR
     * @see JAttr#LTS_OPEN_NODE_ATTR
     * @see JAttr#LTS_FINAL_NODE_ATTR
     * @see JAttr#LTS_RESULT_NODE_ATTR
     * @see JAttr#LTS_NODE_ACTIVE_CHANGE
     */
    @Override
    protected AttributeMap createJVertexAttr(GraphState state) {
        AttributeMap result;

        if (getGraph().isResult(state)) {
            result = LTS_RESULT_NODE_ATTR.clone();
        } else if (state.equals(getGraph().startState())) {
            result = LTS_START_NODE_ATTR.clone();
        } else if (!state.isClosed()) {
            result = LTS_OPEN_NODE_ATTR.clone();
        } else if (getGraph().isFinal(state)) {
            result = LTS_FINAL_NODE_ATTR.clone();
        } else {
            result = LTS_NODE_ATTR.clone();
        }
        if (state.equals(this.activeState)) {
            result.applyMap(LTS_NODE_ACTIVE_CHANGE);
        }
        return result;
    }

    /**
     * This implementation adds special attributes for the active transition.
     * @see JAttr#LTS_EDGE_ATTR
     * @see JAttr#LTS_EDGE_ACTIVE_CHANGE
     */
    @Override
    protected void modifyJEdgeAttr(AttributeMap result,
            Set<GraphTransition> edgeSet) {
        super.modifyJEdgeAttr(result, edgeSet);
        result.applyMap(LTS_EDGE_ATTR.clone());
        if (this.activeTransition != null
            && edgeSet.contains(this.activeTransition)) {
            result.applyMap(LTS_EDGE_ACTIVE_CHANGE);
        }
    }

    /** Adds the correct border emphasis. */
    @Override
    protected AttributeMap getJVertexEmphAttr(JVertex jCell) {
        AttributeMap result;
        @SuppressWarnings("unchecked")
        GraphState state =
            ((GraphJVertex<GraphState,GraphTransition>) jCell).getNode();
        if (state.equals(getActiveState())) {
            result = LTS_ACTIVE_EMPH_NODE_CHANGE;
        } else {
            result = super.getJVertexEmphAttr(jCell);
        }
        return result;
    }

    /**
     * This implementation checks if the edge to be added is a flag with special
     * label according to {@link #isSpecialLabel(String)}; if so, the edge is
     * not added to the jmodel and its source jcell is returned instead.
     */
    @Override
    protected boolean isUnaryEdge(GraphTransition edge) {
        return isSpecialEdge(edge) || isUnmodifyingRule(edge)
            || super.isUnaryEdge(edge);
    }

    /** Tests if the underlying rule of a graph transition edges is unmodifying. */
    protected boolean isUnmodifyingRule(GraphTransition edge) {
        return !edge.getEvent().getRule().isModifying();
    }

    /**
     * Tests if the edge is special in the sense of being a <i>flag</i> (or for
     * legacy reasons a self-edge) with a label that is special according to
     * {@link #isSpecialLabel(String)}.
     * @see #isSpecialLabel(String)
     */
    protected boolean isSpecialEdge(GraphTransition edge) {
        if (edge.source().equals(edge.target())) {
            return isSpecialLabel(edge.label().text());
        } else {
            return false;
        }
    }

    /**
     * Indicates whether a label, when occurring on a self-edge in the LTS,
     * indicates a special role of its source/target node rather than modelling
     * a transition. In that case the edge will probably not be displayed
     * explicitly but rather through special attributes of its source node.
     * @see #addEdge(GraphTransition)
     */
    protected boolean isSpecialLabel(String label) {
        return this.specialLabels != null && this.specialLabels.contains(label);
    }

    /** Sets the active state. */
    protected void setterActiveState(GraphState s) {
        this.activeState = s;
    }

    /**
     * The active state of the LTS. Is null if there is no active state.
     * @invariant activeState == null || ltsJModel.graph().contains(activeState)
     */
    private GraphState activeState;
    /**
     * The currently active transition of the LTS. The source node of
     * emphasizedEdge (if non-null) is also emphasized. Is null if there is no
     * currently emphasized edge.
     * @invariant activeTransition == null ||
     *            ltsJModel.graph().contains(activeTransition)
     */
    private GraphTransition activeTransition;

    /**
     * Set of special edge labels that, when occurring on self-edges, should not
     * be displayed as edges in the LTS but are instead mapped to the jvertex
     * also representing the edge's end point.
     */
    private final Set<String> specialLabels = new HashSet<String>();
    {
        this.specialLabels.add(LTS.START_LABEL_TEXT);
        this.specialLabels.add(LTS.OPEN_LABEL_TEXT);
        this.specialLabels.add(LTS.FINAL_LABEL_TEXT);
    }

    /**
     * Factory method for {@link LTSJModel}. Creates and returns a new model
     * from a given LTS and set of display options. Returns
     * {@link #EMPTY_LTS_JMODEL} if the LTS is <code>null</code>.
     */
    static public LTSJModel newInstance(GTS lts, Options options) {
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

    /** Default name of an LTS model. */
    static public final String DEFAULT_LTS_NAME = "lts";

    /**
     * JEdge class that describes the underlying edge as a graph transition.
     * @author Arend Rensink
     * @version $Revision $
     */
    private class TransitionJEdge extends
            GraphJEdge<GraphState,GraphTransition> {
        /**
         * Creates a new instance from a given edge (required to be a
         * {@link GraphTransition}).
         */
        TransitionJEdge(GraphTransition edge) {
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
            for (Object part : getUserObject()) {
                GraphTransition trans = (GraphTransition) part;
                String description;
                if (isShowAnchors()) {
                    description = trans.getEvent().toString();
                } else {
                    description =
                        trans.getEvent().getRule().getName().toString();
                }
                displayedLabels[labelIndex] =
                    Converter.STRONG_TAG.on(description, true);
                labelIndex++;
            }
            if (displayedLabels.length == 1) {
                result.append(displayedLabels[0]);
            } else {
                result.append(Groove.toString(displayedLabels, "<br>- ", "",
                    "<br>- "));
            }
            return result.toString();
        }

        /**
         * This implementation returns either the transition label, or the event
         * label, depending on #isShowAnchors().
         */
        @Override
        public Label getLabel(GraphTransition edge) {
            return isShowAnchors() ? new DerivationLabel(edge.getEvent())
                    : super.getLabel(edge);
        }
    }

    /**
     * JVertex class that describes the underlying node as a graph state.
     * @author Arend Rensink
     * @version $Revision $
     */
    public class StateJVertex extends GraphJVertex<GraphState,GraphTransition> {
        /**
         * Creates a new instance for a given node (required to be a
         * {@link GraphState}) in an LTS model.
         */
        StateJVertex(LTSJModel jModel, GraphState node) {
            super(jModel, node, true);
        }

        /** A state is also visible if it is open, final, or the start state. */
        @Override
        public boolean isVisible() {
            return isSpecialNode() || hasVisibleIncidentEdge();
        }

        /**
         * Tests if the state is the start state, a final state, or not yet
         * closed.
         */
        private boolean isSpecialNode() {
            LTS lts = getGraph();
            GraphState state = getNode();
            return lts.startState().equals(state) // || !state.isClosed()
                || lts.isFinal(state);
        }

        @Override
        StringBuilder getNodeDescription() {
            StringBuilder result = new StringBuilder("State ");
            result.append(Converter.UNDERLINE_TAG.on(getNode()));
            // if a control location is available, add this to the tooltip
            // if( this.getNode().getLocation() != null ) {
            // result.append("ctrl: " + this.getNode().getLocation());
            // }
            return result;
        }

        /**
         * This implementation adds a label to the set if the j-vertex is the
         * start state, an open state or a final state.
         * @see LTS#START_LABEL_TEXT
         * @see LTS#OPEN_LABEL_TEXT
         * @see LTS#FINAL_LABEL_TEXT
         */
        @Override
        public Collection<String> getPlainLabels() {
            Set<String> result = new HashSet<String>();
            if (isStart()) {
                result.add(LTS.START_LABEL_TEXT);
            }
            if (!isClosed()) {
                result.add(LTS.OPEN_LABEL_TEXT);
            }
            if (isFinal()) {
                result.add(LTS.FINAL_LABEL_TEXT);
            }
            result.addAll(super.getPlainLabels());
            return result;
        }

        /**
         * @return true if the state is a result state.
         */
        public boolean isResult() {
            return getGraph().isResult(getNode());
        }

        /**
         * @return true if the state is a start state.
         */
        public boolean isStart() {
            return getGraph().startState().equals(getNode());
        }

        /**
         * @return true if the state is closed.
         */
        public boolean isClosed() {
            return getNode().isClosed();
        }

        /**
         * @return true if the state is final.
         */
        public boolean isFinal() {
            return getGraph().isFinal(getNode());
        }

        /**
         * This implementation returns either the transition label, or the event
         * label, depending on #isShowAnchors().
         */
        @Override
        public Label getLabel(GraphTransition edge) {
            return isShowAnchors() ? new DerivationLabel(edge.getEvent())
                    : super.getLabel(edge);
        }

        @Override
        public StringBuilder getLine(GraphTransition edge) {
            return Converter.toHtml(new StringBuilder(edge.label().text()));
        }
    }
}