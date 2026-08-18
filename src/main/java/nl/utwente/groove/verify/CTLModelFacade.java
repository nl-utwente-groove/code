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
package nl.utwente.groove.verify;

import static nl.utwente.groove.lts.StateProperty.isStateProperty;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.lts.ExploreResult;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.lts.LTSLabels;
import nl.utwente.groove.lts.LTSLabels.Flag;
import nl.utwente.groove.lts.StateProperty;
import nl.utwente.groove.util.parse.FormatException;

/** Facade for models, with the functionality required for CTL model checking.
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public interface CTLModelFacade {
    /** Returns the root node of the model. */
    public Node getRoot();

    /** Returns the number of (exposed) nodes of the model. */
    default public int nodeCount() {
        return nodeSet().size();
    }

    /** Returns the set of (exposed) nodes of the model. */
    public Set<? extends Node> nodeSet();

    /**
     * Returns the exposed outgoing edges of a node.
     */
    public Iterable<? extends Edge> outEdges(Node node);

    // EZ says: change for SF bug #442. See below.
    /**
     * Return the proper index of the given node to be used in the arrays.
     * Usually the index is the same as the node number, but this can change
     * when the GTS has absent states.
     */
    public int toIndex(Node node);

    /** Returns the node for a given index. */
    public Node toNode(int ix);

    /** Converts a model edge to a proposition that holds for its source. */
    public Proposition toProp(Edge edge);

    /**
     * Converts a model node to a set of propositions that hold for it,
     * without investigating its outgoing edges.
     */
    public List<Proposition> toProps(Node node);

    /** Returns the underlying GTS, if this model wraps an exploration result. */
    default public @Nullable GTS getGTS() {
        return null;
    }

    /** Creates a CTL-checkable model from an exploration result. */
    public static CTLModelFacade newModel(ExploreResult result) {
        return new GTSFacade(result);
    }

    /** Creates a CTL-checkable model from a graph plus special labels mapping.
     * @throws FormatException if the graph is not compatible with the special labels.
     */
    public static CTLModelFacade newModel(Graph graph, LTSLabels ltsLabels) throws FormatException {
        return new GraphFacade(graph, ltsLabels == null
            ? LTSLabels.DEFAULT
            : ltsLabels);
    }

    /*
     * EZ says: this is a hack to fix SF bug #442.
     * The new level of indirection introduced by having to check the node
     * index with the model obviously hurts performance a bit. But... this
     * change touched just a few parts of the code and mainly at the
     * initialization. So I'd say that this is not so bad...
     */
    /** Model facade built from an exploration result. */
    static class GTSFacade implements CTLModelFacade {
        /** Maps an exploration result into a model. */
        GTSFacade(ExploreResult result) {
            this.gts = result.getGTS();
            this.result = result;
            this.nodeIdxMap = new HashMap<>();
            this.ixNodeArray = new GraphState[this.gts.getStates().size()];
            int nr = 0;
            for (GraphState state : this.gts.getStates()) {
                this.nodeIdxMap.put(state, nr);
                this.ixNodeArray[nr] = state;
                nr++;
            }
        }

        private final ExploreResult result;
        private final GTS gts;

        @Override
        public GTS getGTS() {
            return this.gts;
        }

        @Override
        public GraphState getRoot() {
            return this.gts.startState();
        }

        @Override
        public Set<? extends GraphState> nodeSet() {
            return this.gts.getStates();
        }

        @Override
        public Iterable<? extends Edge> outEdges(Node node) {
            return ((GraphState) node).getTransitions();
        }

        @Override
        public int toIndex(Node node) {
            if (this.nodeIdxMap == null) {
                return node.getNumber();
            } else {
                return this.nodeIdxMap.get(node);
            }
        }

        /** Mapping from nodes to their numbers,
         * used in preference to the natural node number in case of absent or transient states
         */
        private final Map<GraphState,Integer> nodeIdxMap;

        @Override
        public GraphState toNode(int ix) {
            return this.ixNodeArray[ix];
        }

        /** Graph states, in the order of their index. */
        private final GraphState[] ixNodeArray;

        /** Converts a model edge to a proposition that holds for its source. */
        @Override
        public Proposition toProp(Edge edge) {
            return Proposition.prop(((GraphTransition) edge).label());
        }

        @Override
        public List<Proposition> toProps(Node node) {
            var result = new LinkedList<Proposition>();
            GraphState state = (GraphState) node;
            if (state.isFinal()) {
                result.add(Proposition.derived(Flag.FINAL));
            }
            if (!state.isClosed()) {
                result.add(Proposition.derived(Flag.OPEN));
            }
            if (state == this.gts.startState()) {
                result.add(Proposition.derived(Flag.START));
            }
            if (this.result.contains(state)) {
                result.add(Proposition.derived(Flag.RESULT));
            }
            this.gts
                .getSatisfiedProps(state)
                .stream()
                .map(StateProperty::getName)
                .map(Proposition::derived)
                .forEach(result::add);
            return result;
        }
    }

    /** Model facade from a graph and a special labels mapping. */
    static class GraphFacade implements CTLModelFacade {
        /** Wraps a graph and a special labels mapping into a model.
         * @throws FormatException if the graph is not compatible with the special labels.
         */
        GraphFacade(Graph graph, LTSLabels ltsLabels) throws FormatException {
            this.graph = graph;
            this.ixNodeArray = new Node[graph.nodeCount()];
            for (var node : graph.nodeSet()) {
                this.ixNodeArray[node.getNumber()] = node;
            }
            this.ltsLabels = ltsLabels == null
                ? LTSLabels.DEFAULT
                : ltsLabels;
            this.root = testFormat();
        }

        private final LTSLabels ltsLabels;

        /** Tests if the model is consistent with the special state markers.
         * Returns the (unique) root node of the model.
         * @throws FormatException if the model has special state markers that occur
         * on edge labels
         */
        private Node testFormat() throws FormatException {
            Node result = null;
            for (Edge edge : this.graph.edgeSet()) {
                var label = edge.label().text();
                if (this.ltsLabels.getDerived().contains(label)) {
                    if (!edge.isLoop()) {
                        throw new FormatException(
                            "Special state marker '%s' occurs as edge label in model",
                            edge.label());
                    }
                    if (label.equals(this.ltsLabels.getStartLabel())) {
                        if (result != null) {
                            throw new FormatException(
                                "Start state marker '%s' occurs more than once in model",
                                edge.label());
                        } else {
                            result = edge.source();
                        }
                    }
                }
            }
            if (result == null) {
                throw new FormatException("Start state marker '%s' does not occur in model",
                    this.ltsLabels.getLabel(Flag.START));
            }
            return result;
        }

        @Override
        public Node getRoot() {
            return this.root;
        }

        private final Node root;

        @Override
        public Set<? extends Node> nodeSet() {
            return this.graph.nodeSet();
        }

        @Override
        public Iterable<Edge> outEdges(Node node) {
            return () -> this.graph
                .outEdgeSet(node)
                .stream()
                .filter(e -> !isStateProperty(e.label()))
                .map(Edge.class::cast) // safe upcast from ? extends Edge
                .iterator();
        }

        private final Graph graph;

        @Override
        public int toIndex(Node node) {
            return node.getNumber();
        }

        @Override
        public Node toNode(int ix) {
            return this.ixNodeArray[ix];
        }

        /** Graph states, in the order of their index. */
        private final Node[] ixNodeArray;

        @Override
        public Proposition toProp(Edge edge) {
            // parse the label as an ID or CALL if possible, else wrap it in a literal
            return Proposition.parse(edge.label().text());
        }

        @Override
        public List<Proposition> toProps(Node node) {
            var result = new LinkedList<Proposition>();
            this.graph
                .outEdgeSet(node)
                .stream()
                .map(Edge::label)
                .map(Label::text)
                .filter(StateProperty::isStateProperty)
                .map(Proposition::derived)
                .forEach(result::add);
            return result;
        }
    }
}
