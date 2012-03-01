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
import groove.lts.GraphState.Flag;
import groove.lts.GraphTransition;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

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
        if (this.listening) {
            prepareInsert();
            // add a corresponding GraphCell to the GraphModel
            addNode(state);
            // insert(cells.toArray(), connections, null, attributes);
            doInsert(false, false);
        }
    }

    /** Toggles the visibility of the GTS. */
    public void hideGTS() {
        this.isHidden = !this.isHidden;
        if (this.isHidden) {
            GTS lts = getGraph();
            for (GraphState state : lts.nodeSet()) {
                ((LTSJCell) getJCellForNode(state)).setVisible(false);
            }
            Queue<GraphState> stateQueue = new LinkedList<GraphState>();
            HashSet<GraphState> visited = new HashSet<GraphState>();
            for (GraphState state : lts.getResultStates()) {
                stateQueue.add(state);
                visited.add(state);
                ((LTSJCell) getJCellForNode(state)).setVisible(true);
                while (!stateQueue.isEmpty()) {
                    for (GraphTransition edge : lts.inEdgeSet(state)) {
                        GraphState gstate = edge.source();
                        if (visited.add(gstate)) {
                            ((LTSJCell) getJCellForNode(gstate)).setVisible(true);
                            if (gstate != lts.startState()) {
                                stateQueue.add(gstate);
                            }
                        }
                    }
                    stateQueue.remove();
                    state = stateQueue.peek();
                }
            }
        } else {
            for (GraphJCell ltsCell : getRoots()) {
                if (ltsCell instanceof LTSJVertex) {
                    ((LTSJCell) ltsCell).setVisible(true);
                }

            }
        }
    }

    /**
     * Reacts to a (node of edge) extension of the underlying Graph by mimicking
     * the change in the GraphModel. Can alse deal with NodeSet and EdgeSet
     * additions.
     */
    public synchronized void addUpdate(GTS gts, GraphTransition transition) {
        if (this.listening) {
            prepareInsert();
            // note that (as per GraphListener contract)
            // source and target Nodes (if any) have already been added
            addEdge(transition, false);
            doInsert(false, true);
        }
    }

    @Override
    public void statusUpdate(GTS lts, GraphState explored, Flag flag) {
        // do nothing
    }

    @Override
    public void loadGraph(Graph<GraphState,GraphTransition> gts) {
        // temporarily remove the model as a graph listener
        this.listening = false;
        GTS oldGTS = getGraph();
        if (oldGTS != null && gts != oldGTS) {
            oldGTS.removeLTSListener(this);
        }
        super.loadGraph(gts);
        if (gts != null && gts != oldGTS) {
            ((GTS) gts).addLTSListener(this);
        }
        this.listening = true;
    }

    private boolean listening = true;

    private boolean isHidden = false;

    /** Default name of an LTS model. */
    static public final String DEFAULT_LTS_NAME = "lts";
}