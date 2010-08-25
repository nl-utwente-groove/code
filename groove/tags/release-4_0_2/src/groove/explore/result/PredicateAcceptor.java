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
import groove.graph.Node;
import groove.lts.GraphState;
import groove.lts.GraphTransition;

/**
 * A <code>PredicateAcceptor</code> is an acceptor that adds states to its
 * result set based on a given predicate. The predicate must either evaluate
 * graph states or graph transitions.
 * 
 * @see Predicate
 * @author Maarten de Mol 
 */
public class PredicateAcceptor extends Acceptor {

    private final boolean statePredicate;
    private final Predicate<GraphState> P;
    private final boolean transitionPredicate;
    private final Predicate<GraphTransition> Q;

    /**
     * Default constructor. Initializes predicate.
     */
    @SuppressWarnings("unchecked")
    public PredicateAcceptor(Predicate<?> predicate) {
        super();

        this.statePredicate = predicate.statePredicate;
        if (this.statePredicate) {
            this.P = (Predicate<GraphState>) predicate;
        } else {
            this.P = null;
        }

        this.transitionPredicate = predicate.transitionPredicate;
        if (this.transitionPredicate) {
            this.Q = (Predicate<GraphTransition>) predicate;
        } else {
            this.Q = null;
        }
    }

    /**
     * Listener to the LTS that is called whenever a new state is added to it.
     * Evaluates the state predicate, and updates the result set if needed.
     */
    @Override
    public void addUpdate(GraphShape graph, Node node) {
        if (!this.statePredicate) {
            return;
        }
        GraphState state = (GraphState) node;
        if (this.P.eval(state)) {
            this.getResult().add(state);
        }
    }

    /**
     * Listener to the LTS that is called whenever a new transition is added to
     * it. Evaluates the transition predicate, and updates the result set if
     * needed.
     */
    @Override
    public void addUpdate(GraphShape graph, Edge edge) {
        if (!this.transitionPredicate) {
            return;
        }
        GraphTransition transition = (GraphTransition) edge;
        if (this.Q.eval(transition)) {
            getResult().add(transition.target());
        }
    }
}
