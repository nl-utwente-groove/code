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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import groove.lts.GTS;
import groove.lts.GTSListener;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.MatchResult;
import groove.lts.RuleTransition;
import groove.util.Pair;

/**
 * Exploration point that does not take randomness or transient states into account
 * @author Arend Rensink
 * @version $Revision $
 */
class SimpleExplorePoint implements ExplorePoint {
    /** Constructs in initial exploration point from a graph state. */
    SimpleExplorePoint(GraphState state) {
        this.parent = null;
        this.path = null;
        this.state = state;
        setGTSListener();
    }

    /** Constructs an exploration point with a given parent. */
    private SimpleExplorePoint(SimpleExplorePoint parent, List<GraphTransition> path) {
        this.parent = parent;
        this.path = path;
        this.state = path.get(path.size() - 1)
            .target();
        setGTSListener();
    }

    /** Exploration point from which this one was reached. */
    private final SimpleExplorePoint parent;
    /** Sequence of transitions from the parent to this explore point; null if there is no parent. */
    private final List<GraphTransition> path;
    /** Graph state wrapped in this exploration point. */
    private final GraphState state;

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
                this.next = createPoint(Collections.singletonList(trans));
            }
        }
        return this.next == null;
    }

    private SimpleExplorePoint createPoint(List<GraphTransition> path) {
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

    @Override
    public Optional<Pair<List<GraphTransition>,ExplorePoint>> getParent() {
        return this.parent == null ? Optional.empty()
            : Optional.of(Pair.newPair(this.path, this.parent));
    }
}
