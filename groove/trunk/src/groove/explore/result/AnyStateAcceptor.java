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
import groove.lts.GraphState.Flag;

/**
 * Acceptor that accepts any new state that is added to the LTS.
 * 
 * Implements the following methods:
 * - addUpdate (overridden) - add a state to the result set whenever a new node
 *                            is created in the LTS
 * 
 * @author Maarten de Mol
 * @version $Revision $
 */
public class AnyStateAcceptor extends Acceptor {
    /**
     * Constructor. Only calls super method.
     */
    public AnyStateAcceptor() {
        super();
    }

    /**
     * Constructor. Only calls super method.
     * @param result - the result set in which the accepted states will be stored
     */
    public AnyStateAcceptor(Result result) {
        super(result);
    }

    @Override
    public void addUpdate(GTS gts, GraphState state) {
        if (state.isDone()) {
            getResult().add(state);
        }
    }

    @Override
    public void statusUpdate(GTS graph, GraphState explored, Flag flag) {
        if (flag == Flag.DONE) {
            getResult().add(explored);
            super.statusUpdate(graph, explored, flag);
        }
    }

}