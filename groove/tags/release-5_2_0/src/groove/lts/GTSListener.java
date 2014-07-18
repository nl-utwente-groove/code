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
/*
 * $Id: LTSListener.java,v 1.2 2008-01-30 09:32:18 iovka Exp $
 */
package groove.lts;

import groove.lts.Status.Flag;

/**
 * A listener to certain types of GTS updates.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface GTSListener {
    /**
     * Signals that a state has been added to a given GTS.
     * @param gts the GTS that has been updated
     * @param state the state that has been added
     */
    void addUpdate(GTS gts, GraphState state);

    /**
     * Signals that a transition has been added to a given GTS.
     * @param gts the GTS that has been updated
     * @param transition the transition that has been added
     */
    void addUpdate(GTS gts, GraphTransition transition);

    /**
     * Signals that a status flag in a graph
     * state has changed.
     * @param gts the GTS in which the change occurred
     * @param state the graph state whose status has changed
     * @param flag the status flag that is indicative of the state.
     * Must satisfy {@link Flag#isChange()}
     * @param oldStatus status of {@code state} before the change
     */
    public void statusUpdate(GTS gts, GraphState state, Flag flag, int oldStatus);
}
