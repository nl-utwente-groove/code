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
 * $Id$
 */
package groove.gui.jgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import groove.graph.Edge;
import groove.graph.Node;
import groove.gui.look.Look;
import groove.gui.look.VisualKey;
import groove.lts.GTS;
import groove.lts.GTSListener;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.Status.Flag;

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
        } else if (isAcceptState(state)) {
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
        } else if (isAcceptTransition(transition)) {
            prepareInsert();
            // note that (as per GraphListener contract)
            // source and target Nodes (if any) have already been added
            JCell<GTS> edgeJCell = addEdge(transition);
            doInsert(false);
            JCell<GTS> stateJCell = getJCellForNode(transition.target());
            stateJCell.setStale(VisualKey.VISIBLE);
            edgeJCell.setStale(VisualKey.VISIBLE);
            // layout should occur after the transition has been added
            // otherwise the forest will not be computed correctly
            getJGraph().doLayout(false);
            getJGraph().scrollToActive();
        }
    }

    @Override
    public void statusUpdate(GTS lts, GraphState explored, int change) {
        JCell<GTS> jCell = registerChange(explored, change);
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
    private JCell<GTS> registerChange(GraphState explored, int change) {
        JVertex<GTS> jCell = getJCellForNode(explored);
        if (jCell != null) {
            if (Flag.CLOSED.test(change)) {
                jCell.setLook(Look.OPEN, false);
            }
            if (Flag.DONE.test(change)) {
                jCell.setLook(Look.RECIPE, explored.isInternalState());
                jCell.setLook(Look.TRANSIENT, explored.isTransient());
                jCell.setLook(Look.FINAL, explored.isFinal());
            }
            if (Flag.ABSENT.test(change)) {
                Iterator<? extends JEdge<GTS>> iter = jCell.getContext();
                while (iter.hasNext()) {
                    iter.next()
                        .setLook(Look.ABSENT, true);
                }
                jCell.setLook(Look.ABSENT, true);
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
        prepareLoad(gts);
        addElements(gts.nodeSet(), null, true);
        if (gts != oldGTS) {
            gts.addLTSListener(this);
        }
        getJGraph().reactivate();
    }

    /**
     * Possibly extends the jModel with additional states from the underlying GTS.
     * This can be more efficient than reloading, e.g., if the state bound has increased.
     */
    public boolean reloadGraph() {
        boolean result = false;
        if (getGraph() != null) {
            int nodeCount = nodeCount();
            int bound = getStateBound();
            if (bound > nodeCount && nodeCount < getGraph().nodeCount()) {
                result = addElements(getGraph().nodeSet(), getGraph().edgeSet(), false);
            } else if (bound < nodeCount) {
                loadGraph(getGraph());
                result = true;
            }
        }
        return result;
    }

    /* Overridden to ensure that the node rendering limit is used. */
    @Override
    protected boolean addNodes(Collection<? extends Node> nodeSet) {
        boolean result = false;
        int nodeCount = nodeCount();
        for (Node node : nodeSet) {
            GraphState state = (GraphState) node;
            if (!isAcceptState(state)) {
                continue;
            }
            LTSJVertex jVertex = (LTSJVertex) getJCellForNode(node);
            if (jVertex != null) {
                result |= jVertex.setVisibleFlag(true);
                continue;
            }
            addNode(node);
            result = true;
            nodeCount++;
            if (nodeCount > getStateBound()) {
                break;
            }
        }
        return result;
    }

    /** Tests if a given graph state is acceptable for addition to the LTS panel. */
    private boolean isAcceptState(GraphState state) {
        if (state.isInternalState() && !getJGraph().isShowRecipeSteps()) {
            return false;
        }
        if (state.isAbsent() && !getJGraph().isShowAbsentStates()) {
            return false;
        }
        return true;
    }

    /* Overridden to ensure that the node rendering limit is observed. */
    @Override
    protected boolean addEdges(Collection<? extends Edge> edgeSet) {
        boolean result = false;
        if (edgeSet == null) {
            for (Node node : this.nodeJCellMap.keySet()) {
                GraphState state = (GraphState) node;
                for (GraphTransition trans : state
                    .getTransitions(getJGraph().getTransitionClass())) {
                    result |= addTransition(trans);
                }
            }
        } else {
            for (Edge edge : edgeSet) {
                GraphTransition trans = (GraphTransition) edge;
                if (getJGraph().getTransitionClass()
                    .admits(trans)) {
                    result |= addTransition((GraphTransition) edge);
                }
            }
        }
        return result;
    }

    /** Tests if a given graph transition is acceptable for addition to the LTS panel. */
    private boolean isAcceptTransition(GraphTransition trans) {
        return getJGraph().getTransitionClass()
            .admits(trans);
    }

    /**
     * Adds a given transition to the jModel, if its properties allow this,
     * its source and target are in the model, and
     * it is not in the model already.
     * @return {@code true} if the transition was indeed added
     */
    private boolean addTransition(GraphTransition trans) {
        GraphState source = trans.source();
        GraphState target = trans.target();
        if (!isVisible(source)) {
            return false;
        }
        // Only add the edges for which we know the target exists.
        if (!trans.isLoop() && !isVisible(target)) {
            return false;
        }
        // make visible if the transition is already there
        LTSJCell jCell = (LTSJCell) getJCellForEdge(trans);
        if (jCell != null) {
            return jCell.setVisibleFlag(true);
        }
        addEdge(trans);
        return true;
    }

    private boolean isVisible(GraphState state) {
        LTSJVertex jVertex = (LTSJVertex) getJCellForNode(state);
        return jVertex != null && jVertex.hasVisibleFlag();
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
    private final List<Node> addedNodes = new ArrayList<>();
    /** Set of edges added during the last exploration. */
    private final List<Edge> addedEdges = new ArrayList<>();
    /** Set of JCells with status changes during the last exploration. */
    private final List<JCell<GTS>> changedCells = new ArrayList<>();

    /** Default name of an LTS model. */
    static public final String DEFAULT_LTS_NAME = "lts";
}