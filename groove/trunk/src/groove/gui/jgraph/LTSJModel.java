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

import groove.graph.Edge;
import groove.graph.Node;
import groove.gui.look.Look;
import groove.gui.look.VisualKey;
import groove.lts.GTS;
import groove.lts.GTSListener;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.Status.Flag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Graph model adding a concept of active state and transition, with special
 * visual characteristics.
 * @author Arend Rensink
 * @version $Revision$
 */
final public class LTSJModel extends JModel<GTS> implements GTSListener {
    /** Creates a new model from a given LTS and set of display options. */
    LTSJModel(LTSJGraph jGraph) {
        super(jGraph);
    }

    /* Specialises the return type. */
    @Override
    public LTSJGraph getJGraph() {
        return (LTSJGraph) super.getJGraph();
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

    @Override
    public synchronized void addUpdate(GTS gts, GraphState state) {
        if (isExploring()) {
            this.addedNodes.add(state);
        } else {
            prepareInsert();
            // add a corresponding GraphCell to the GraphModel
            addNode(state);
            doInsert(false);
        }
    }

    @Override
    public synchronized void addUpdate(GTS gts, GraphTransition transition) {
        if (isExploring()) {
            this.addedEdges.add(transition);
        } else {
            prepareInsert();
            // note that (as per GraphListener contract)
            // source and target Nodes (if any) have already been added
            JCell<GTS> edgeJCell = addEdge(transition);
            doInsert(false);
            JCell<GTS> stateJCell = getJCellForNode(transition.target());
            stateJCell.setStale(VisualKey.VISIBLE);
            edgeJCell.setStale(VisualKey.VISIBLE);
        }
    }

    @Override
    public void statusUpdate(GTS lts, GraphState explored, Flag flag, int oldStatus) {
        JCell<GTS> jCell = registerChange(explored, flag);
        if (jCell != null) {
            if (isExploring()) {
                this.changedCells.add(jCell);
            } else {
                getJGraph().refreshCells(Collections.singleton(jCell));
            }
        }
    }

    /**
     * Registers a status change in a previously explored state.
     * @return the cell that was changed as a consequence to the state change;
     * {@code null} if there was no change.
     */
    private JCell<GTS> registerChange(GraphState explored, Flag flag) {
        JVertex<GTS> jCell = getJCellForNode(explored);
        if (jCell != null) {
            switch (flag) {
            case CLOSED:
                jCell.setLook(Look.OPEN, false);
                break;
            case ERROR:
                jCell.setStale(VisualKey.ERROR);
                break;
            case DONE:
                if (explored.isAbsent()) {
                    for (JEdge<GTS> jEdge : jCell.getContext()) {
                        jEdge.setLook(Look.ABSENT, true);
                    }
                    jCell.setLook(Look.ABSENT, true);
                }
                jCell.setLook(Look.RECIPE, explored.isInternalState());
                jCell.setLook(Look.TRANSIENT, explored.isTransient());
                jCell.setLook(Look.FINAL, explored.isFinal());
                break;
            case RESULT:
                jCell.setLook(Look.RESULT, explored.isResult());
            }
            jCell.setStale(VisualKey.refreshables());
        }
        return jCell;
    }

    @Override
    public void loadGraph(GTS gts) {
        GTS oldGTS = getGraph();
        // temporarily remove the model as a graph listener
        if (oldGTS != null && gts != oldGTS) {
            oldGTS.removeLTSListener(this);
        }
        super.loadGraph(gts);
        if (gts != null && gts != oldGTS) {
            gts.addLTSListener(this);
        }
        getJGraph().reactivate();
    }

    /* Overridden to ensure that the node rendering limit is used. */
    @Override
    protected void addNodes(Collection<? extends Node> nodeSet) {
        int nodesAdded = 0;
        for (Node node : nodeSet) {
            GraphState state = (GraphState) node;
            if (state.isInternalState() && !getJGraph().isShowRecipeSteps()) {
                continue;
            }
            if (state.isAbsent() && !getJGraph().isShowAbsentStates()) {
                continue;
            }
            addNode(node);
            nodesAdded++;
            if (nodesAdded > getStateBound()) {
                break;
            }
        }
    }

    /* Overridden to ensure that the node rendering limit is used. */
    @Override
    protected void addEdges(Collection<? extends Edge> edgeSet) {
        for (Edge edge : edgeSet) {
            GraphTransition trans = (GraphTransition) edge;
            if (trans.isInternalStep() && !getJGraph().isShowRecipeSteps()) {
                continue;
            }
            if ((trans.source().isAbsent() || trans.target().isAbsent())
                    && !getJGraph().isShowAbsentStates()) {
                continue;
            }
            // Only add the edges for which we know the state was added.
            if (edge.source().getNumber() <= getStateBound()
                && edge.target().getNumber() <= getStateBound()) {
                addEdge(edge);
            }
        }
    }

    /**
     * Sets the maximum state number to be added.
     * @return the previous bound
     */
    public int setStateBound(int bound) {
        int result = this.stateBound;
        this.stateBound = bound;
        return result;
    }

    /** Returns the maximum state number to be displayed. */
    public int getStateBound() {
        return this.stateBound;
    }

    /** The maximum state number to be added. */
    private int stateBound;

    /**
     * Indicates if the model is set to exploring mode.
     * In exploring mode, changes to the GTS are registered but not
     * passed on to the GUI.
     */
    public boolean isExploring() {
        return this.exploring;
    }

    /**
     * Sets or resets the exploring mode.
     * When exploring is set to {@code false}, all registered changes
     * are pushed to the GUI.
     */
    public void setExploring(boolean exploring) {
        boolean changed = (this.exploring != exploring);
        if (changed) {
            this.exploring = exploring;
            if (exploring) {
                this.addedNodes.clear();
                this.addedEdges.clear();
                this.changedCells.clear();
            } else {
                if (!this.addedNodes.isEmpty() || !this.addedEdges.isEmpty()) {
                    addElements(this.addedNodes, this.addedEdges, false);
                }
                if (!this.changedCells.isEmpty()) {
                    getJGraph().refreshCells(this.changedCells);
                }
            }
        }
    }

    private boolean exploring;
    /** Set of nodes added during the last exploration. */
    private final List<Node> addedNodes = new ArrayList<Node>();
    /** Set of edges added during the last exploration. */
    private final List<Edge> addedEdges = new ArrayList<Edge>();
    /** Set of JCells with status changes during the last exploration. */
    private final List<JCell<GTS>> changedCells = new ArrayList<JCell<GTS>>();

    /** Default name of an LTS model. */
    static public final String DEFAULT_LTS_NAME = "lts";
}