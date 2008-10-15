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

import groove.graph.Graph;
import groove.graph.GraphShape;
import groove.graph.Node;

import java.util.List;
import java.util.Set;

/**
 * Interface for regular automata. An automaton extends a graph with a start
 * state, an end state, and a flag to indicate whether empty words are accepted.
 */
public interface Automaton extends Graph {
    /** Returns the start node of the automaton. */
    Node getStartNode();

    /** Changes the start node of the automaton. */
    void setStartNode(Node startNode);

    /** Returns the end node of the automaton. */
    Node getEndNode();

    /** Changes the end node of the automaton. */
    void setEndNode(Node endNode);

    /** Indicates if the automaton will accept empty words. */
    boolean isAcceptsEmptyWord();

    /** Changes the empty word acceptance. */
    void setAcceptsEmptyWord(boolean acceptsEmptyWord);

    /** Tests if this automaton accepts a given word. */
    boolean accepts(List<String> word);

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
    NodeRelation getMatches(GraphShape graph, Set<? extends Node> startImages,
            Set<? extends Node> endImages);
}
