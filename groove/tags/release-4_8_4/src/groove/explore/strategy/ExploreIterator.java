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
package groove.explore.strategy;

import groove.explore.result.Acceptor;
import groove.lts.GTS;
import groove.lts.GraphState;

/**
 * Interface for the functionality invoked by an exploration
 * strategy.
 * The methods should be called in a sequence satisfying
 * <tt>({@link #prepare}; ({@link #hasNext}+; {@link #doNext}?)*; {@link #finish})*</tt>
 * (where {@link #doNext} should only be invoked if the preceding
 * {@link #hasNext} has returned {@code true}).
 * @author Arend Rensink
 * @version $Revision $
 */
public interface ExploreIterator {
    /** 
     * Initialises the iterator for exploring a given
     * GTS, starting from a given state.
     * @param gts the GTS to be explored
     * @param state the state at which exploration should
     * start; may be {@code null}, in which case the GTS' start state is to be used
     * @param acceptor acceptor object to be used during exploration; may be {@code null}
     */
    public void prepare(GTS gts, GraphState state, Acceptor acceptor);

    /** 
     * Performs the next step in the exploration.
     * Should be called only if {@link #hasNext} holds.
     * @return the (last) state explored as a result of this call.
     */
    public GraphState doNext();

    /** Indicates if there is a next step in the exploration. */
    public boolean hasNext();

    /** 
     * Callback method invoked after exploration has finished.
     * After this method, the only next operation allowed is
     * {@link #prepare}.
     */
    public void finish();
}
