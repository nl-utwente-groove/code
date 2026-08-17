/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.automaton;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.rule.RuleLabel;
import nl.utwente.groove.grammar.type.TypeEdge;
import nl.utwente.groove.grammar.type.TypeElement;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.graph.EdgeRole;

/**
 * Computes the set of type edges that a regular automaton can traverse
 * on paths between given source and target node types, by exploring the
 * product of the automaton and the type graph. This makes the answer
 * <i>positional</i>: a type edge only counts as traversable if it lies on
 * some path realising the automaton between the given end types, so for
 * instance an unguarded wildcard traverses only what can occur between
 * the node types at its position, rather than every edge type.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class RegAutCoverage {
    /**
     * Creates a coverage computation for a given automaton, between given
     * (exact) possible source and target node types.
     */
    public RegAutCoverage(RegAut aut, Set<TypeNode> sourceTypes, Set<TypeNode> targetTypes) {
        this.aut = aut;
        this.typeGraph = aut.getTypeGraph();
        this.sourceTypes = sourceTypes;
        this.targetTypes = targetTypes;
    }

    private final RegAut aut;
    private final TypeGraph typeGraph;
    private final Set<TypeNode> sourceTypes;
    private final Set<TypeNode> targetTypes;

    /**
     * Returns the set of type edges traversable by the automaton on some
     * path between the source and target node types.
     */
    public Set<TypeEdge> result() {
        var result = this.result;
        if (result == null) {
            this.result = result = compute();
        }
        return result;
    }

    private @Nullable Set<TypeEdge> result;

    /** Computes the value of {@link #result()}. */
    private Set<TypeEdge> compute() {
        // forward exploration of the product, collecting all steps
        Deque<State> queue = new ArrayDeque<>();
        Set<State> reached = new HashSet<>();
        for (TypeNode type : this.sourceTypes) {
            State init = new State(this.aut.getStartNode(), type);
            if (reached.add(init)) {
                queue.add(init);
            }
        }
        List<Step> steps = new ArrayList<>();
        Map<State,List<Step>> inSteps = new HashMap<>();
        while (!queue.isEmpty()) {
            State state = queue.poll();
            assert state != null; // queue is non-empty
            for (RegEdge autEdge : this.aut.outEdgeSet(state.node())) {
                RuleLabel label = autEdge.label();
                boolean inverse = label.isInv();
                if (inverse) {
                    label = label.getInvLabel();
                    assert label != null;
                }
                for (TypeElement match : this.typeGraph.getMatches(label)) {
                    for (Step step : getSteps(state, autEdge.target(), match, inverse)) {
                        steps.add(step);
                        inSteps.computeIfAbsent(step.to(), s -> new ArrayList<>()).add(step);
                        if (reached.add(step.to())) {
                            queue.add(step.to());
                        }
                    }
                }
            }
        }
        // backward exploration from the accepting states
        Set<State> backward = new HashSet<>();
        for (TypeNode type : this.targetTypes) {
            State accept = new State(this.aut.getEndNode(), type);
            if (reached.contains(accept) && backward.add(accept)) {
                queue.add(accept);
            }
        }
        while (!queue.isEmpty()) {
            State state = queue.poll();
            for (Step step : inSteps.getOrDefault(state, List.of())) {
                if (backward.add(step.from())) {
                    queue.add(step.from());
                }
            }
        }
        // collect the type edges on steps between marked states
        Set<TypeEdge> result = new HashSet<>();
        for (Step step : steps) {
            TypeEdge edge = step.edge();
            if (edge != null && backward.contains(step.to())) {
                result.add(edge);
            }
        }
        return result;
    }

    /**
     * Returns the product steps from a given state over a given automaton
     * transition, for one matched type element.
     * A matched type node is a node type test, which stays at the current
     * type; a matched type edge is enabled if the current type can be its
     * (direction-dependent) origin, and moves to any possible type of the
     * opposite end. Non-binary type edges are self-loops on the same host
     * node, so they keep the current type.
     */
    private List<Step> getSteps(State state, RegNode target, TypeElement match, boolean inverse) {
        List<Step> result = new ArrayList<>();
        if (match instanceof TypeNode node) {
            if (node.equals(state.type())) {
                result.add(new Step(state, new State(target, state.type()), null));
            }
        } else {
            TypeEdge edge = (TypeEdge) match;
            TypeNode from = inverse
                ? edge.target()
                : edge.source();
            TypeNode to = inverse
                ? edge.source()
                : edge.target();
            if (from.getSubtypes().contains(state.type())) {
                if (edge.getRole() == EdgeRole.BINARY) {
                    for (TypeNode toType : to.getSubtypes()) {
                        result.add(new Step(state, new State(target, toType), edge));
                    }
                } else {
                    result.add(new Step(state, new State(target, state.type()), edge));
                }
            }
        }
        return result;
    }

    /** State of the product exploration: an automaton node plus an exact node type. */
    private record State(RegNode node, TypeNode type) {
        // no added functionality
    }

    /** Step of the product exploration; the type edge is {@code null} for node type tests. */
    private record Step(State from, State to, @Nullable TypeEdge edge) {
        // no added functionality
    }
}
