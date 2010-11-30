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

import groove.graph.GraphShape;
import groove.graph.Node;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.LTSAdapter;

/**
 * Strategy that closes every state it explores, and adds the newly generated
 * states to a pool, together with information regarding the outgoing
 * transitions of its parent. Subclasses must decide on the order of the pool;
 * e.g., breadth-first or depth-first.
 */
abstract public class ClosingStrategy extends AbstractStrategy {
    @Override
    public void prepare(GTS gts, GraphState startState) {
        super.prepare(gts, startState);
        // for the closing strategy, there is no problem in aliasing
        // the graph data structures. On the whole, this seems wise, to
        // avoid excessive garbage collection.
        // gts.getRecord().setCopyGraphs(true);
        getGTS().addGraphListener(this.exploreListener);
        clearPool();
    }

    @Override
    protected boolean updateAtState() {
        boolean result = (this.atState = getFromPool()) != null;
        if (!result) {
            getGTS().removeGraphListener(this.exploreListener);
        }
        return result;
    }

    /** Callback method to add a pool element to the pool. */
    abstract protected void putInPool(GraphState element);

    /** Returns the next element from the pool of explorable states. */
    abstract protected GraphState getFromPool();

    /** Clears the pool, in order to prepare the strategy for reuse. */
    abstract protected void clearPool();

    /** Listener to keep track of states added to the GTS. */
    private final ExploreListener exploreListener = new ExploreListener();

    /** A queue with states to be explored, used as a FIFO. */
    private class ExploreListener extends LTSAdapter {
        @Override
        public void addUpdate(GraphShape graph, Node node) {
            putInPool((GraphState) node);
        }
    }
}
