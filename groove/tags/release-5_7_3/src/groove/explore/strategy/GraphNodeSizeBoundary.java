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
package groove.explore.strategy;

import groove.grammar.host.HostGraph;
import groove.verify.ModelChecking.Record;
import groove.verify.ProductTransition;

/**
 * Implementation of interface {@link Boundary} that bases the boundary on the
 * node-count of the graph reached by the given transition.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-02-20 08:37:54 $
 */
public class GraphNodeSizeBoundary extends Boundary {
    /**
     * Constructs a prototype boundary object.
     * To use, invoke {@link #instantiate(Record)}.
     * @param size the initial boundary value
     * @param step the increase at each step
     */
    public GraphNodeSizeBoundary(int size, int step) {
        this(size, step, null);
    }

    /**
     * {@link GraphNodeSizeBoundary} constructor.
     * @param size the initial boundary value
     * @param step the increase at each step
     * @param record record of the model checking run
     */
    private GraphNodeSizeBoundary(int size, int step, Record record) {
        super(record);
        assert step > 0;
        this.size = size;
        this.step = step;
    }

    @Override
    public Boundary instantiate(Record record) {
        return new GraphNodeSizeBoundary(this.size, this.step, record);
    }

    @Override
    public boolean crossingBoundary(ProductTransition transition,
            boolean traverse) {
        boolean result =
            transition.target().getGraphState().getGraph().nodeCount() > this.size;
        return result;
    }

    /** Returns whether the given graph's size crosses this boundary. */
    public boolean crossingBoundary(HostGraph graph) {
        return graph.nodeCount() > this.size;
    }

    @Override
    public void increase() {
        this.size += this.step;
    }

    @Override
    public void increaseDepth() {
        // do nothing
    }

    @Override
    public void decreaseDepth() {
        // do nothing
    }

    @Override
    public int currentDepth() {
        return getRecord().getIteration();
    }

    @Override
    public String toString() {
        return "" + this.size + "," + this.step;
    }

    /** the graph-size of graphs allowed for exploration */
    private int size;
    /** the value with which to increase the boundary with */
    private final int step;
}
