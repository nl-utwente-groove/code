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
 * $Id: Automaton.java,v 1.4 2008-01-30 09:32:28 iovka Exp $
 */
package groove.rel;

import groove.graph.DefaultEdge;
import groove.graph.DefaultNode;
import groove.graph.Graph;
import groove.graph.LabelStore;
import groove.graph.TypeLabel;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.RuleLabel;
import groove.trans.RuleToHostMap;
import groove.util.Duo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for regular automata. An automaton extends a graph with a start
 * state, an end state, and a flag to indicate whether empty words are accepted.
 * An automaton has {@link DefaultNode}s and {@link DefaultEdge}s;
 * the latter have {@link RuleLabel}s that are one of the following:
 * <ul>
 * <li> Inverted labels of one of the following types
 * <li> Wildcards
 * <li> Sharp node type labels
 * <li> Atoms
 * </ul>
 */
public interface RegAut extends Graph<RegNode,RuleLabel,RegEdge> {
    /** Returns the start node of the automaton. */
    RegNode getStartNode();

    /** Changes the start node of the automaton. */
    void setStartNode(RegNode startNode);

    /** Returns the end node of the automaton. */
    RegNode getEndNode();

    /** Changes the end node of the automaton. */
    void setEndNode(RegNode endNode);

    /** Indicates if the automaton will accept empty words. */
    boolean isAcceptsEmptyWord();

    /** Changes the empty word acceptance. */
    void setAcceptsEmptyWord(boolean acceptsEmptyWord);

    /** Tests if this automaton accepts a given word. */
    boolean accepts(List<String> word);

    /** Returns the set of labels that can be matched by the automaton. */
    Set<TypeLabel> getAlphabet();

    /** Returns the label store used by this automaton. */
    LabelStore getLabelStore();

    /**
     * Returns a relation consisting of pairs of nodes of a given graph between
     * which there is a path matching this automaton.
     * @param graph the graph in which the paths are sought
     * @param startImages set of nodes in <code>graph</code> from which the
     *        matching paths should start; if <code>null</code>, there is no
     *        constraint
     * @param endImages set of nodes in <code>graph</code> at which the
     *        matching paths should end; if <code>null</code>, there is no
     *        constraint
     */
    Set<Result> getMatches(HostGraph graph, Set<HostNode> startImages,
            Set<HostNode> endImages);

    /**
     * Returns a relation consisting of pairs of nodes of a given graph between
     * which there is a path matching this automaton. If this automaton has
     * variables, the pairs are edges with {@link RuleToHostMap} labels giving
     * a valuation of the variables.
     * @param graph the graph in which the paths are sought
     * @param startImages set of nodes in <code>graph</code> from which the
     *        matching paths should start; if <code>null</code>, there is no
     *        constraint
     * @param endImages set of nodes in <code>graph</code> at which the
     *        matching paths should end; if <code>null</code>, there is no
     *        constraint
     * @param valuation mapping from variables to edge labels that should be
     *        adhered to in the matching; if <code>null</code>, there is no
     *        constraint
     */
    Set<Result> getMatches(HostGraph graph, Set<HostNode> startImages,
            Set<HostNode> endImages, Map<LabelVar,TypeLabel> valuation);

    /** Type of the automaton's match results. */
    class Result extends Duo<HostNode> {
        public Result(HostNode one, HostNode two,
                Map<LabelVar,TypeLabel> valuation) {
            super(one, two);
            this.valuation = valuation;
        }

        /**
         * Returns the valuation.
         */
        public Map<LabelVar,TypeLabel> getValuation() {
            return this.valuation;
        }

        @Override
        public boolean equals(Object obj) {
            boolean result = super.equals(obj);
            if (result) {
                Result other = (Result) obj;
                if (this.valuation == null) {
                    result = other.valuation == null;
                } else {
                    result = this.valuation.equals(other.valuation);
                }
            }
            return result;
        }

        @Override
        public int hashCode() {
            return super.hashCode()
                ^ (this.valuation == null ? 0 : this.valuation.hashCode());
        }

        @Override
        public String toString() {
            return "Result [one=" + one() + ", two=" + two() + ", valuation="
                + this.valuation + "]";
        }

        private final Map<LabelVar,TypeLabel> valuation;
    }
}
