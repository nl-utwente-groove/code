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

import groove.lts.GTS;
import groove.lts.GTSListener;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.MatchResult;
import groove.lts.RuleTransition;

/**
 * Exploration point that does not take randomness or transient states into account.
 * @author Arend Rensink
 * @version $Revision $
 */
class SimpleExplorePoint implements ExplorePoint {
    /** Constructs in initial exploration point from a graph state. */
    SimpleExplorePoint(ExploreInstance exploration, GraphState state) {
        this.exploration = exploration;
        this.state = state;
        this.priority = exploration.computeHeuristic(state);
        setGTSListener();
    }

    /** Constructs an exploration point with a given parent. */
    private SimpleExplorePoint(SimpleExplorePoint parent, GraphTransition path) {
        this.exploration = parent.getExploration();
        this.state = path.target();
        this.priority = parent.getPriority() + this.exploration.computeCost(path.label())
            + this.exploration.computeHeuristic(this.state);
        setGTSListener();
    }

    /**
     * Returns the exploration instance that has generated this point.
     */
    public ExploreInstance getExploration() {
        return this.exploration;
    }

    /** Exploration point from which this one was reached. */
    private final ExploreInstance exploration;
    private boolean freshState;

    private void setGTSListener() {
        this.state.getGTS()
            .addLTSListener(new GTSListener() {
                @Override
                public void addUpdate(GTS gts, GraphState state) {
                    if (state.isPresent()) {
                        SimpleExplorePoint.this.freshState = true;
                    }
                }
            });
    }

    /**
     * The next successor to be returned by {@link #next()}.
     * The value is computed by a call to {@link #hasNext()}.
     */
    private SimpleExplorePoint next;

    @Override
    public boolean hasNext() {
        if (this.next == null && !this.state.isClosed()) {
            MatchResult nextMatch = this.state.getMatch();
            this.freshState = false;
            RuleTransition trans = this.state.applyMatch(nextMatch);
            // if a fresh state was added, the listener has signalled it in the freshState flag
            if (this.freshState) {
                this.next = createPoint(trans);
            }
        }
        return this.next == null;
    }

    private SimpleExplorePoint createPoint(GraphTransition path) {
        return new SimpleExplorePoint(this, path);
    }

    @Override
    public ExplorePoint next() {
        if (hasNext()) {
            SimpleExplorePoint result = this.next;
            this.next = null;
            return result;
        } else {
            throw new IllegalStateException(String.format("State %s is closed", this.state));
        }
    }

    @Override
    public GraphState getState() {
        return this.state;
    }

    /** Graph state wrapped in this exploration point. */
    private final GraphState state;

    @Override
    public int getPriority() {
        return this.priority;
    }

    /** The priority of this exploration point, used if
     * there is a cost or heuristic function involved.
     */
    private final int priority;

}
