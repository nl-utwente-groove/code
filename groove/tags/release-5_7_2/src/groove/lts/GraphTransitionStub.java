// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/**
 * 
 */
package groove.lts;

import groove.grammar.Action;

/**
 * An object recording the necessary information to reconstruct an
 * {@link GraphTransition} from a source {@link GraphState}.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface GraphTransitionStub {
    /**
     * Returns the action that has generated this transition.
     */
    Action getAction();

    /**
     * Returns the target state of this graph transition stub, given
     * a certain source state.
     */
    GraphState getTarget(GraphState source);

    /**
     * Constructs a graph transition from this transition stub, based on a given
     * source.
     * @param source the source state for the graph transition
     * @return A graph transition based on the given source, and the rule,
     *         anchor images and target state stored in this out-transition.
     */
    GraphTransition toTransition(GraphState source);

    /**
     * Returns the event that underlies the transition from a given source to
     * this object.
     */
    GraphTransitionKey getKey(GraphState source);
}
