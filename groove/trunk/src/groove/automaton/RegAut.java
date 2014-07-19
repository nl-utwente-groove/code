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
package groove.automaton;

import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.rule.RuleLabel;
import groove.grammar.rule.RuleToHostMap;
import groove.grammar.rule.Valuation;
import groove.grammar.type.TypeElement;
import groove.grammar.type.TypeGraph;
import groove.graph.GGraph;
import groove.graph.plain.PlainEdge;
import groove.graph.plain.PlainNode;
import groove.util.Duo;

import java.util.List;
import java.util.Set;

/**
 * Interface for regular automata. An automaton extends a graph with a start
 * state, an end state, and a flag to indicate whether empty words are accepted.
 * An automaton has {@link PlainNode}s and {@link PlainEdge}s;
 * the latter have {@link RuleLabel}s that are one of the following:
 * <ul>
 * <li> Inverted labels of one of the following types
 * <li> Wildcards
 * <li> Sharp node type labels
 * <li> Atoms
 * </ul>
 */
public interface RegAut extends GGraph<RegNode,RegEdge> {
    /** Returns the start node of the automaton. */
    RegNode getStartNode();

    /** Changes the start node of the automaton. */
    void setStartNode(RegNode startNode);

    /** Returns the end node of the automaton. */
    RegNode getEndNode();

    /** Changes the end node of the automaton. */
    void setEndNode(RegNode endNode);

    /**
     * Merges two nodes in this graph, by adding all edges to and from the first
     * node to the second, and subsequently removing the first.
     * @param from node to be deleted
     * @param to node to receive copies of the edges to and from the other
     * @return {@code true} if {@code from} is distinct from
     *         {@code to}, so a merge actually took place
     */
    boolean mergeNodes(RegNode from, RegNode to);

    /** Indicates if the automaton will accept empty words. */
    boolean isAcceptsEmptyWord();

    /** Changes the empty word acceptance. */
    void setAcceptsEmptyWord(boolean acceptsEmptyWord);

    /** Tests if this automaton accepts a given word. */
    boolean accepts(List<String> word);

    /** Returns the set of labels that can be matched by the automaton. */
    Set<TypeElement> getAlphabet();

    /** Returns the label store used by this automaton. */
    TypeGraph getTypeGraph();

    /**
     * Returns a relation consisting of pairs of nodes of a given graph between
     * which there is a path matching this automaton.
     * @param graph the graph in which the paths are sought
     * @param startImage set of nodes in <code>graph</code> from which the
     *        matching paths should start; if <code>null</code>, there is no
     *        constraint
     * @param endImage set of nodes in <code>graph</code> at which the
     *        matching paths should end; if <code>null</code>, there is no
     *        constraint
     */
    Set<Result> getMatches(HostGraph graph, HostNode startImage,
            HostNode endImage);

    /** Construct a new automaton, with a given start node, end node and type graph. */
    RegAut newAutomaton(RegNode start, RegNode end, TypeGraph typeGraph);

    /**
     * Returns a relation consisting of pairs of nodes of a given graph between
     * which there is a path matching this automaton. If this automaton has
     * variables, the pairs are edges with {@link RuleToHostMap} labels giving
     * a valuation of the variables.
     * @param graph the graph in which the paths are sought
     * @param startImage set of nodes in <code>graph</code> from which the
     *        matching paths should start; if <code>null</code>, there is no
     *        constraint
     * @param endImage set of nodes in <code>graph</code> at which the
     *        matching paths should end; if <code>null</code>, there is no
     *        constraint
     * @param valuation mapping from variables to edge labels that should be
     *        adhered to in the matching; if <code>null</code>, there is no
     *        constraint
     */
    Set<Result> getMatches(HostGraph graph, HostNode startImage,
            HostNode endImage, Valuation valuation);

    /** Type of the automaton's match results. */
    class Result extends Duo<HostNode> {
        public Result(HostNode one, HostNode two) {
            super(one, two);
        }
    }
}
