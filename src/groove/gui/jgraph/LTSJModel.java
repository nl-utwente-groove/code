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

/**
 * Graph model adding a concept of active state and transition, with special
 * visual characteristics.
 * @author Arend Rensink
 * @version $Revision$
 */
final public class LTSJModel extends GraphJModel<GraphState,GraphTransition>
        implements GTSListener {
    /** Creates a new model from a given LTS and set of display options. */
    LTSJModel(LTSJGraph jGraph, LTSJVertex jVertexProt, LTSJEdge jEdgeProt) {
        super(jGraph, jVertexProt, jEdgeProt);
    }

    /* Specialises the return type. */
    @Override
    public LTSJGraph getJGraph() {
        return (LTSJGraph) super.getJGraph();
    }

    /* Specialises the return type. */
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
        this.maxStateNr = -1;
        this.stateLowerBound = 0;
        GTS oldGTS = getGraph();
        if (oldGTS != null && gts != oldGTS) {
            oldGTS.removeLTSListener(this);
        }
        super.loadGraph(gts);
        if (gts != null && gts != oldGTS) {
            ((GTS) gts).addLTSListener(this);
        }
        getJGraph().reactivate();
        this.listening = true;
    }

    /** Loads all graph elements from the current highest actual state number to the current upper bound. */
    public void loadFurther() {
        this.stateLowerBound = this.maxStateNr + 1;
        addElements(getGraph().nodeSet(), getGraph().edgeSet(), false);
    }

    /** Only add nodes that do not exceed the maximum state number. */
    @Override
    protected GraphJVertex addNode(GraphState node) {
        GraphJVertex result = null;
        int nr = node.getNumber();
        if (!isLoading() || isWithinBounds(nr)) {
            result = super.addNode(node);
            if (nr > this.maxStateNr) {
                this.maxStateNr = nr;
            }
        }
        return result;
    }

    @Override
    protected GraphJCell addEdge(GraphTransition edge,
            boolean mergeBidirectional) {
        GraphJCell result = null;
        int sourceNr = edge.source().getNumber();
        int targetNr = edge.target().getNumber();
        if (!isLoading() || sourceNr <= this.stateUpperBound
            && targetNr <= this.stateUpperBound) {
            if (isWithinBounds(sourceNr) || isWithinBounds(targetNr)) {
                result = super.addEdge(edge, mergeBidirectional);
            }
        }
        return result;
    }

    /** Set the filtering flag of this model to the given value. */
    public void setFiltering(boolean filtering) {
        this.filtering = filtering;
    }

    /** Returns the filtering flag of this model. */
    public boolean isFiltering() {
        return this.filtering;
    }

    private boolean listening = true;

    private boolean filtering = false;

    /**
     * Sets the maximum state number to be added.
     * @return the previous bound
     */
    public int setStateBound(int bound) {
        int result = this.stateUpperBound;
        this.stateUpperBound = bound;
        return result;
    }

    /** Tests if a number is within the state lower and upper bounds. */
    private boolean isWithinBounds(int nr) {
        return nr <= this.stateUpperBound && nr >= this.stateLowerBound;
    }

    /** The minimum state number to be added. */
    private int stateLowerBound;
    /** The maximum state number to be added. */
    private int stateUpperBound;

    /** Maximum state number currently added to the JGraph. */
    private int maxStateNr;

    /** Default name of an LTS model. */
    static public final String DEFAULT_LTS_NAME = "lts";
}