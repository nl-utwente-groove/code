/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.explore;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;

/**
 * Exploration product that is a trace through the GTS.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TraceExploreProduct implements ExploreProduct {
    /** Constructs a trace product from its end state.
     * The trace consists of the inverse of the transitions back to the GTS start state.
     * @param endState the end state of the trace.
     */
    public TraceExploreProduct(GraphState endState) {
        this.endState = endState;
    }

    /**
     * Constructs a trace product from a given list of transitions and an end state.
     * The end state is necessary for the case the trace is empty.
     */
    public TraceExploreProduct(List<GraphTransition> trace, GraphState endState) {
        this.trace = new LinkedList<>(trace);
        this.endState = endState;
    }

    /** Returns the sequence of transitions form the GTS start state to the end state of this trace. */
    public Queue<GraphTransition> getTrace() {
        if (this.trace == null) {
            // construct trace from end state
            this.trace = new LinkedList<>();
            GraphState current = this.endState;
            while (current instanceof GraphNextState) {
                GraphTransition in = ((GraphNextState) current).getInTransition();
                this.trace.add(in);
                current = in.source();
            }
        }
        return this.trace;
    }

    private Queue<GraphTransition> trace;

    @Override
    public GraphState getEndState() {
        return this.endState;
    }

    private final GraphState endState;
}
