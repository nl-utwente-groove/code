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

import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.RuleTransition;

/**
 * A <code>PredicateAcceptor</code> is an acceptor that adds states to its
 * result set based on a given predicate. The predicate must either evaluate
 * graph states or graph transitions.
 * 
 * @see Predicate
 * @author Maarten de Mol 
 */
public class PredicateAcceptor extends Acceptor {
    private final Predicate<GraphState> P;
    private final Predicate<RuleTransition> Q;

    /**
     * Default constructor. Initialises predicate and sets a default result object.
     */
    public PredicateAcceptor(Predicate<?> predicate) {
        this(new Result(), predicate);
    }

    /**
     * Default constructor. Initialises predicate and result object.
     */
    @SuppressWarnings("unchecked")
    public PredicateAcceptor(Result result, Predicate<?> predicate) {
        super(result);
        if (predicate.forStates()) {
            this.P = (Predicate<GraphState>) predicate;
            this.Q = null;
        } else {
            this.P = null;
            this.Q = (Predicate<RuleTransition>) predicate;
        }
    }

    /**
     * Listener to the LTS that is called whenever a new state is added to it.
     * Evaluates the state predicate, and updates the result set if needed.
     */
    @Override
    public void addUpdate(GTS gts, GraphState state) {
        if (this.P.forStates() && this.P.eval(state)) {
            this.getResult().add(state);
        }
    }

    /**
     * Listener to the LTS that is called whenever a new transition is added to
     * it. Evaluates the transition predicate, and updates the result set if
     * needed.
     */
    @Override
    public void addUpdate(GTS gts, GraphTransition transition) {
        if (this.Q != null && transition instanceof RuleTransition
            && this.Q.eval((RuleTransition) transition)) {
            getResult().add(transition.target());
        }
    }
}
