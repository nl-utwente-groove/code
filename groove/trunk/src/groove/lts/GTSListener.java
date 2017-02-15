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
 * $Id$
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
     * The default implementation is empty.
     * @param gts the GTS that has been updated
     * @param state the state that has been added
     */
    public default void addUpdate(GTS gts, GraphState state) {
        // empty default implementation
    }

    /**
     * Signals that a transition has been added to a given GTS.
     * The default implementation is empty.
     * @param gts the GTS that has been updated
     * @param transition the transition that has been added
     */
    public default void addUpdate(GTS gts, GraphTransition transition) {
        // empty default implementation
    }

    /**
     * Signals that a status flag in a graph
     * state has changed.
     * The default implementation is empty.
     * @param gts the GTS in which the change occurred
     * @param state the graph state whose status has changed
     * @param change the vector of changes; a flag has been (un)set if {@link Flag#test(int)}
     * returns true when applied to {@code change}
     * @see Flag#test(int)
     */
    public default void statusUpdate(GTS gts, GraphState state, int change) {
        // empty default implementation
    }
}
