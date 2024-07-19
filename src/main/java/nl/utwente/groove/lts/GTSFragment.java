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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.explore.ExploreResult;
import nl.utwente.groove.explore.util.LTSLabels;
import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.graph.AGraph;
import nl.utwente.groove.graph.GGraph;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.multi.MultiGraph;
import nl.utwente.groove.graph.multi.MultiNode;

/**
 * Fragment of a GTS, consisting of a subset of the states and transitions
 * of a given GTS.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class GTSFragment extends AGraph<GraphState,GraphTransition> {
    /** Constructs an (initially empty) fragment of a given GTS, with a given name.
     */
    public GTSFragment(GTS gts, String name) {
        super(name, false);
        this.gts = gts;
    }

    /** Constructs an (initially empty) fragment of a given GTS.
     * The name of the fragment is derived from the GTS name, by appending "-fragment".
     */
    public GTSFragment(GTS gts) {
        this(gts, gts.getName() + "-fragment");
    }

    /** Constructs a fragment of a given GTS, with given initial sets of states and transitions. */
    public GTSFragment(GTS gts, Collection<? extends GraphState> states,
                       Collection<? extends GraphTransition> transitions) {
        this(gts);
        states.forEach(this::addNode);
        transitions.forEach(this::addEdge);
    }

    /**
     * Returns the GTS of which this is a fragment.
     */
    public GTS gts() {
        return this.gts;
    }

    private final GTS gts;

    @Override
    public Set<GraphState> nodeSet() {
        return this.states;
    }

    private final Set<GraphState> states = new LinkedHashSet<>();

    @Override
    public Set<GraphTransition> edgeSet() {
        return this.transitions;
    }

    private final Set<GraphTransition> transitions = new LinkedHashSet<>();

    /** Returns the start state of the GTS. */
    public GraphState startState() {
        return this.gts.startState();
    }

    @Override
    public @NonNull GGraph<GraphState,GraphTransition> newGraph(String name) {
        return new GTSFragment(gts(), name);
    }

    @Override
    public boolean addNode(GraphState node) {
        return this.states.add(node);
    }

    @Override
    public boolean addEdge(GraphTransition edge) {
        return this.transitions.add(edge);
    }

    @Override
    public boolean removeEdge(GraphTransition edge) {
        return this.transitions.remove(edge);
    }

    @Override
    public boolean removeNode(GraphState node) {
        return this.states.add(node);
    }

    @Override
    public GraphRole getRole() {
        return GraphRole.LTS;
    }

    @Override
    public GTSFragment clone() {
        var result = new GTSFragment(gts(), getName());
        nodeSet().forEach(result::addNode);
        edgeSet().forEach(result::addEdge);
        return result;
    }

    /** Adds (non-internal) paths to all unreachable states in the current fragment.
     */
    public void complete() {
        complete(false);
    }

    /** Adds paths to all unreachable states in the current fragment.
     * A flag controls whether internal transitions are to be added as well.
     */
    public void complete(boolean internal) {
        var unreachables = new HashSet<>(nodeSet());
        edgeSet().stream().map(GraphTransition::target).forEach(unreachables::remove);
        Queue<GraphState> queue = new LinkedList<>(unreachables);
        var reached = new HashSet<>();
        while (!queue.isEmpty()) {
            GraphState target = queue.poll();
            if (!reached.add(target)) {
                continue;
            }
            // it's a new target
            if (!(target instanceof GraphNextState incoming)) {
                continue;
            }
            // it's not the start state
            if (target.isInternalState()) {
                if (internal) {
                    addEdgeContext(incoming);
                }
                queue.add(incoming.source());
                continue;
            }
            // it's not an internal state; we have to look for an incoming
            // non-internal transition
            if (!incoming.isInternalStep()) {
                addEdgeContext(incoming);
                queue.add(incoming.source());
                continue;
            }
            // it's an internal step leading to a non-internal state,
            // hence the final step of a recipe transition
            if (internal) {
                addEdgeContext(incoming);
            }
            // traverse back to a non-transient state
            GraphState source = incoming.source();
            while (source.isInternalState()) {
                incoming = (GraphNextState) source;
                if (internal) {
                    addEdgeContext(incoming);
                }
                source = incoming.source();
            }
            // incoming is now the initial transition of the recipe transition
            // leading from source to target
            // look for the corresponding recipe transition
            for (GraphTransition outgoing : source.getTransitions()) {
                if (!(outgoing instanceof RecipeTransition candidate)) {
                    continue;
                }
                if (candidate.getInitial() == incoming && candidate.target() == target) {
                    addEdgeContext(candidate);
                    break;
                }
            }
            queue.add(source);
        }
        nodeSet().add(gts().startState());
    }

    /**
     * Transforms this GTS fragment to a plain graph representation,
     * optionally including special node flags to represent start, final and
     * open states, and state identifiers.
     * @param flags object determining what special labels will be added
     * @param answer if non-{@code null}, the result that should be saved.
     * Only used if {@code filter} equals {@link Filter#RESULT}
     */
    public MultiGraph toPlainGraph(LTSLabels flags, @Nullable ExploreResult answer) {
        MultiGraph result = new MultiGraph(getName(), GraphRole.LTS);
        Map<GraphState,MultiNode> nodeMap = new HashMap<>();
        for (GraphState state : nodeSet()) {
            // don't include transient states unless forced to
            if (state.isInternalState() && !flags.showRecipes()) {
                continue;
            }
            if (state.isAbsent()) {
                continue;
            }
            MultiNode image = result.addNode(state.getNumber());
            nodeMap.put(state, image);
            if (flags.showResult() && answer != null && answer.contains(state)) {
                result.addEdge(image, flags.getResultLabel(), image);
            }
            if (flags.showFinal() && state.isFinal()) {
                result.addEdge(image, flags.getFinalLabel(), image);
            }
            if (flags.showStart() && gts().startState().equals(state)) {
                result.addEdge(image, flags.getStartLabel(), image);
            }
            if (flags.showOpen() && !state.isClosed()) {
                result.addEdge(image, flags.getOpenLabel(), image);
            }
            if (flags.showNumber()) {
                String label = flags.getNumberLabel().replaceAll("#", "" + state.getNumber());
                result.addEdge(image, label, image);
            }
            if (flags.showTransience() && state.isTransient()) {
                String label = flags
                    .getTransienceLabel()
                    .replaceAll("#", "" + state.getActualFrame().getTransience());
                result.addEdge(image, label, image);
            }
            if (flags.showRecipes() && state.isInternalState()) {
                Optional<Recipe> recipe = state.getActualFrame().getRecipe();
                recipe.map(r -> r.getQualName()).ifPresent(n -> {
                    String label = flags.getRecipeLabel().replaceAll("#", "" + n);
                    result.addEdge(image, label, image);
                });
            }
        }
        for (GraphTransition transition : edgeSet()) {
            // don't include partial transitions unless forced to
            if (transition.isInternalStep() && !flags.showRecipes()) {
                continue;
            }
            MultiNode sourceImage = nodeMap.get(transition.source());
            MultiNode targetImage = nodeMap.get(transition.target());
            result.addEdge(sourceImage, transition.label().text(), targetImage);
        }
        return result;

    }
}
