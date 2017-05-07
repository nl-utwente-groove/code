/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package groove.explore.result;

import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.Status.Flag;

/**
 * Accepts final states.
 */
public class FinalStateAcceptor extends Acceptor {
    /**
     * Creates a default instance.
     */
    private FinalStateAcceptor() {
        super(true);
    }

    /**
     * Creates an instance with a given exploration bound.
     */
    private FinalStateAcceptor(int bound) {
        super(bound);
    }

    @Override
    public FinalStateAcceptor newAcceptor(int bound) {
        return new FinalStateAcceptor(bound);
    }

    @Override
    public void statusUpdate(GTS gts, GraphState state, int change) {
        if (Flag.FINAL.test(change)) {
            getResult().addState(state);
        }
    }

    /** Prototype acceptor. */
    public static final FinalStateAcceptor PROTOTYPE = new FinalStateAcceptor();
}
