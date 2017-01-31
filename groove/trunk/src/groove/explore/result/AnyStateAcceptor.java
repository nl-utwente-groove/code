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
import groove.lts.Status;
import groove.lts.Status.Flag;

/**
 * Acceptor that accepts any new state that is added to the LTS, provided
 * it is done and a real state.
 * @author Maarten de Mol
 * @version $Revision $
 * @see Status#isReal(int)
 */
public class AnyStateAcceptor extends Acceptor {
    /**
     * Constructor. Only calls super method.
     */
    private AnyStateAcceptor() {
        super(true);
    }

    /**
     * Private constructor for an acceptor with a given exploration bound.
     */
    private AnyStateAcceptor(int bound) {
        super(bound);
    }

    @Override
    public AnyStateAcceptor newAcceptor(int bound) {
        return new AnyStateAcceptor(bound);
    }

    @Override
    public void addUpdate(GTS gts, GraphState state) {
        if (state.isRealState()) {
            getResult().addState(state);
        }
    }

    @Override
    public void statusUpdate(GTS graph, GraphState explored, int change) {
        if (Flag.DONE.test(change) && explored.isRealState()) {
            getResult().addState(explored);
        }
    }

    /** Prototype acceptor. */
    public final static AnyStateAcceptor PROTOTYPE = new AnyStateAcceptor();
}