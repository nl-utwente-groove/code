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

import groove.lts.GraphState;
import groove.lts.LTS;
import groove.lts.State;

/**
 * Acceptor that accepts any new state that is added to the LTS.
 * Implements the following methods:
 * - closeUpdate - accepts the closed state
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
    public void closeUpdate(LTS graph, State state) {
        getResult().add((GraphState) state);
    }
}