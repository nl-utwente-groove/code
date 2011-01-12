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

import groove.graph.Graph;
import groove.lts.GTS;
import groove.lts.GTSListener;
import groove.lts.GraphState;
import groove.lts.GraphTransition;

import java.util.HashSet;
import java.util.Set;

/**
 * Graph model adding a concept of active state and transition, with special
 * visual characteristics.
 * @author Arend Rensink
 * @version $Revision$
 */
final public class LTSJModel extends GraphJModel<GraphState,GraphTransition>
        implements GTSListener {
    /** Creates a new model from a given LTS and set of display options. */
    LTSJModel(LTSJVertex jVertexProt, LTSJEdge jEdgeProt) {
        super(jVertexProt, jEdgeProt);
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
    public synchronized void addUpdate(GTS gts, GraphState state) {
        prepareInsert();
        // add a corresponding GraphCell to the GraphModel
        addNode(state);
        // insert(cells.toArray(), connections, null, attributes);
        doInsert(false, false);
    }

    /**
     * Reacts to a (node of edge) extension of the underlying Graph by mimicking
     * the change in the GraphModel. Can alse deal with NodeSet and EdgeSet
     * additions.
     */
    public synchronized void addUpdate(GTS gts, GraphTransition transition) {
        prepareInsert();
        // note that (as per GraphListener contract)
        // source and target Nodes (if any) have already been added
        addEdge(transition);
        doInsert(false, true);
    }

    @Override
    public void closeUpdate(GTS lts, GraphState explored) {
        // do nothing
    }

    @Override
    public void loadGraph(Graph<GraphState,GraphTransition> gts) {
        // temporarily remove the model as a graph listener
        if (getGraph() != null) {
            getGraph().removeLTSListener(this);
        }
        super.loadGraph(gts);
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
        Set<GraphJCell> changedCells = new HashSet<GraphJCell>();
        GraphTransition previousTrans = this.activeTransition;
        if (previousTrans != trans) {
            this.activeTransition = trans;
            if (previousTrans != null) {
                LTSJCell jCell = (LTSJCell) getJCellForEdge(previousTrans);
                assert jCell != null : String.format(
                    "No image for %s in jModel", previousTrans);
                if (jCell.setActive(false)) {
                    changedCells.add(jCell);
                }
            }
            if (trans != null) {
                LTSJCell jCell = (LTSJCell) getJCellForEdge(trans);
                assert jCell != null : String.format(
                    "No image for %s in jModel", trans);
                if (jCell.setActive(true)) {
                    changedCells.add(jCell);
                }
            }
        }
        GraphState previousState = this.activeState;
        if (state != previousState) {
            this.activeState = state;
            if (previousState != null) {
                LTSJVertex jCell = (LTSJVertex) getJCellForNode(previousState);
                if (jCell.setActive(false)) {
                    changedCells.add(jCell);
                }
            }
            if (state != null) {
                LTSJVertex jCell = (LTSJVertex) getJCellForNode(state);
                if (jCell.setActive(true)) {
                    changedCells.add(jCell);
                }
            }
        }
        if (!changedCells.isEmpty()) {
            cellsChanged(changedCells.toArray());
        }
    }

    /**
     * This implementation checks if the edge is a transition of an 
     * unmodifying rule.
     */
    @Override
    protected boolean isUnaryEdge(GraphTransition edge) {
        return isUnmodifyingRule(edge) || super.isUnaryEdge(edge);
    }

    /** Tests if the underlying rule of a graph transition edges is unmodifying. */
    protected boolean isUnmodifyingRule(GraphTransition edge) {
        return !edge.getEvent().getRule().isModifying();
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

    /** Default name of an LTS model. */
    static public final String DEFAULT_LTS_NAME = "lts";
}