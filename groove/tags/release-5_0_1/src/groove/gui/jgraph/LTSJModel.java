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

import java.util.Collection;
import java.util.Collections;

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

    /**
     * Reacts to a (node of edge) extension of the underlying Graph by mimicking
     * the change in the GraphModel. Can alse deal with NodeSet and EdgeSet
     * additions.
     */
    @Override
    public synchronized void addUpdate(GTS gts, GraphState state) {
        if (this.listening) {
            prepareInsert();
            // add a corresponding GraphCell to the GraphModel
            addNode(state);
            // insert(cells.toArray(), connections, null, attributes);
            doInsert(false);
        }
    }

    /**
     * Reacts to a (node of edge) extension of the underlying Graph by mimicking
     * the change in the GraphModel. Can alse deal with NodeSet and EdgeSet
     * additions.
     */
    @Override
    public synchronized void addUpdate(GTS gts, GraphTransition transition) {
        if (this.listening) {
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
        if (this.listening) {
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
            }
            jCell.setStale(VisualKey.refreshables());
            getJGraph().refreshCells(Collections.singleton(jCell));
        }
    }

    @Override
    public void loadGraph(GTS gts) {
        this.listening = false;
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
        this.listening = true;
    }

    /** Overriden to ensure that the node rendering limit is used. */
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

    /** Overriden to ensure that the node rendering limit is used. */
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

    private boolean listening = true;

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

    /** Default name of an LTS model. */
    static public final String DEFAULT_LTS_NAME = "lts";
}