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

import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.lts.GraphTransition;

/**
 * A <code>TransitionAcceptor</code> examines transitions that are added to the
 * LTS and adds their targets to a locally maintained <code>Result</code> set
 * based on a given <code>TransitionCondition</code>. 
 */
public class TransitionAcceptor extends Acceptor {

    private final boolean isPositive;
    private final TransitionCondition condition;

    /**
     * Default constructor, based on a (possibly negated) condition. 
     */
    public TransitionAcceptor(boolean isPositive, TransitionCondition condition) {
        super();
        this.isPositive = isPositive;
        this.condition = condition;
    }

    /**
     * Additional constructor that uses a default positive mode.
     */
    public TransitionAcceptor(TransitionCondition condition) {
        this(true, condition);
    }

    /**
     * Listener to the LTS that is called whenever a new transition is added
     * to it. Evaluates the stored condition, and updates the result set if
     * needed.
     */
    @Override
    public void addUpdate(GraphShape graph, Edge edge) {
        GraphTransition transition = (GraphTransition) edge;
        boolean match = this.condition.evalNewTransition(transition);
        if (this.isPositive == match) {
            getResult().add(transition.target());
        }
    }
}
