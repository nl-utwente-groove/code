/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.explore.result;

import groove.graph.GraphShape;
import groove.graph.Node;
import groove.lts.GraphState;

/**
 * A <code>StateAcceptor</code> examines states that are added to the LTS and
 * adds them targets to a locally maintained <code>Result</code> set based on
 * a given <code>StateCondition</code>. 
 */
public class StateAcceptor extends Acceptor {

    private final boolean isPositive;
    private final StateCondition condition;

    /**
     * Default constructor, based on a (possibly negated) condition. 
     */
    public StateAcceptor(boolean isPositive, StateCondition condition) {
        super();
        this.isPositive = isPositive;
        this.condition = condition;
    }

    /**
     * Additional constructor that uses a default positive mode.
     */
    public StateAcceptor(StateCondition condition) {
        this(true, condition);
    }

    /**
     * Listener to the LTS that is called whenever a new state is added to it.
     * Evaluates the stored condition, and updates the result set if needed.
     */
    @Override
    public void addUpdate(GraphShape graph, Node node) {
        GraphState state = (GraphState) node;
        boolean match = this.condition.evalNewState(state);
        if (this.isPositive == match) {
            getResult().add(state);
        }
    }
}
