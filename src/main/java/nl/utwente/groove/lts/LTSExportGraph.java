/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.lts;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.graph.AEdge;
import nl.utwente.groove.graph.AGraph;
import nl.utwente.groove.graph.GEdge;
import nl.utwente.groove.graph.GGraph;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.plain.PlainLabel;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.collect.SetView;

/**
 * Read-only graph view of a GTS fragment in the shape in which an LTS is saved:
 * the nodes are the present (and, unless recipe steps are shown, non-inner) states
 * of the fragment, and the edges are the transitions between those states together
 * with the self-loops that an {@link LTSLabels} object prescribes for special states.
 * Nothing is materialised: nodes and edges are computed on the fly while iterating,
 * so that saving a large LTS costs no memory beyond the GTS itself (gh #854).
 * Note that the per-node and per-label edge sets inherited from {@link AGraph}
 * are served from a cache that does hold all edges.
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.1, 2026-09")
public class LTSExportGraph extends AGraph<GraphState,GEdge<GraphState>> {
    /**
     * Constructs a view of a given fragment.
     * @param fragment the GTS fragment being viewed; should not change afterwards
     * @param flags determines which special labels are added as self-loops
     * @param answer if non-{@code null}, the exploration result whose states are
     * labelled as such (if {@code flags} says so)
     */
    LTSExportGraph(GTSFragment fragment, LTSLabels flags, @Nullable ExploreResult answer) {
        super(fragment.getName(), false);
        this.fragment = fragment;
        this.flags = flags;
        this.answer = answer;
        this.nodeSet = SetView
            .instance(fragment.nodeSet(),
                      obj -> obj instanceof GraphState state && includes(state));
        // carry over the exploration metadata recorded in the GTS info,
        // notably the random seed (gh #897)
        GraphInfo.transferProperties(fragment.gts(), this, null);
        setFixed();
    }

    private final GTSFragment fragment;
    private final LTSLabels flags;
    private final @Nullable ExploreResult answer;

    /** Indicates if a given state is a node of this view. */
    private boolean includes(GraphState state) {
        // don't include transient states unless forced to
        if (state.isInner() && !this.flags.showRecipes()) {
            return false;
        }
        if (state.isAbsent()) {
            return false;
        }
        return this.fragment.nodeSet().contains(state);
    }

    /** Indicates if a given transition is an edge of this view. */
    private boolean includes(GraphTransition trans) {
        // don't include partial transitions unless forced to
        if (trans.isInnerStep() && !this.flags.showRecipes()) {
            return false;
        }
        // don't include transitions of which an end state was left out
        return includes(trans.source()) && includes(trans.target());
    }

    @Override
    public Set<? extends GraphState> nodeSet() {
        return this.nodeSet;
    }

    private final Set<GraphState> nodeSet;

    @Override
    public Set<? extends GEdge<GraphState>> edgeSet() {
        return this.edgeSet;
    }

    private final Set<GEdge<GraphState>> edgeSet = new AbstractSet<>() {
        @Override
        public Iterator<GEdge<GraphState>> iterator() {
            return edges().iterator();
        }

        @Override
        public int size() {
            return (int) edges().count();
        }
    };

    /** Streams the edges of this view: the flag self-loops of all nodes, in node order,
     * followed by the transitions, in fragment order. */
    private Stream<GEdge<GraphState>> edges() {
        Stream<Flag> flags = nodeSet()
            .stream()
            .flatMap(state -> flagLabels(state).stream().map(label -> new Flag(state, label)));
        Stream<GraphTransition> transitions
            = this.fragment.edgeSet().stream().filter(this::includes);
        return Stream.concat(flags, transitions);
    }

    /** Returns the labels of the flag self-loops of a given node,
     * in the order in which they are saved. */
    private List<String> flagLabels(GraphState state) {
        var flags = this.flags;
        var answer = this.answer;
        List<String> result = new ArrayList<>();
        if (flags.showResult() && answer != null && answer.contains(state)) {
            result.add(flags.getResultLabel());
        }
        if (flags.showFinal() && state.isFinal()) {
            result.add(flags.getFinalLabel());
        }
        if (flags.showStart() && this.fragment.startState().equals(state)) {
            result.add(flags.getStartLabel());
        }
        if (flags.showOpen() && !state.isClosed()) {
            result.add(flags.getOpenLabel());
        }
        if (flags.showNumber()) {
            result.add(flags.getNumberLabel().replaceAll("#", "" + state.getNumber()));
        }
        if (flags.showTransience() && state.isTransient()) {
            result
                .add(flags
                    .getTransienceLabel()
                    .replaceAll("#", "" + state.getActualFrame().getTransience()));
        }
        if (flags.showRecipes() && state.isInner()) {
            state
                .getActualFrame()
                .getRecipe()
                .map(Recipe::getQualName)
                .ifPresent(n -> result.add(flags.getRecipeLabel().replaceAll("#", "" + n)));
        }
        for (var prop : state.getSatisfiedProps()) {
            if (!prop.isSystem()) {
                result.add(prop.getName());
            }
        }
        return result;
    }

    @Override
    public GraphRole getRole() {
        return GraphRole.LTS;
    }

    @Override
    public LTSExportGraph clone() {
        return new LTSExportGraph(this.fragment, this.flags, this.answer);
    }

    @Override
    public GGraph<GraphState,GEdge<GraphState>> newGraph(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addNode(GraphState node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addEdge(GEdge<GraphState> edge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeEdge(GEdge<GraphState> edge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeNode(GraphState node) {
        throw new UnsupportedOperationException();
    }

    /** Self-loop on a state carrying a special state label. */
    private static class Flag extends AEdge<GraphState,PlainLabel> {
        Flag(GraphState state, String label) {
            super(state, PlainLabel.parseLabel(label), state);
        }
    }
}
