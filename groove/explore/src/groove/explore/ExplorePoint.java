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
package groove.explore;

import java.util.List;
import java.util.Optional;

import groove.lts.GraphState;
import groove.util.Pair;

/**
 * State in an exploration.
 * Combines a {@link GraphState} with functionality to explore its successors
 * in the order determined by the explore configuration, and to construct an
 * appropriate {@link ExploreProduct}.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface ExplorePoint {
    /** Indicates if this explore point has unexplored successors. */
    public boolean hasNext();

    /** Returns the next unexplored successor of this explore point. */
    public ExplorePoint next();

    /** Returns the graph state wrapped in this explore point. */
    public GraphState getState();

    /**
     * Returns the optional parent of this explore point.
     * All explore points except the initial one have a parent.
     */
    public Optional<Pair<List<groove.lts.GraphTransition>,ExplorePoint>> getParent();
}
