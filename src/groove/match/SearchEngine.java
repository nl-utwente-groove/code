/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.match;

import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.graph.LabelStore;
import groove.graph.Node;
import groove.rel.VarNodeEdgeMap;
import groove.trans.Condition;

import java.util.Collection;

/**
 * This is the common interface among factory classes that generate
 * match strategies based on a specific algorithm, such as search plan, 
 * or RETE.
 *   
 * @author Arash Jalali
 * @version $Revision $
 */
public abstract class SearchEngine<MatcherType extends AbstractMatchStrategy<VarNodeEdgeMap>> {

    /**
     * Factory method returning a search engine for 
     * a graph condition with negative tests, 
     * and takes control
     * and common labels into account (if any).
     * @param condition the condition for which a search plan is to be
     *        constructed
     */
    public MatcherType createMatcher(Condition condition) {
        return createMatcher(condition, null, null);
    }

    /**
     * Factory method returning a search plan for a graph condition, taking into
     * account that a certain set of nodes and edges has been matched already.
     * This extends the ordinary search plan with negative tests. Takes control
     * and common labels into account (if any).
     * @param condition the condition for which a search plan is to be
     *        constructed
     * @param anchorNodes the nodes of the condition that have been matched
     *        already
     * @param anchorEdges the edges of the condition that have been matched
     *        already
     */
    public MatcherType createMatcher(Condition condition,
            Collection<? extends Node> anchorNodes,
            Collection<? extends Edge> anchorEdges) {
        return createMatcher(condition, anchorNodes, anchorEdges, null);
    }

    /**
     * Factory method returning a matcher for a graph condition, taking into
     * account that a certain set of nodes and edges has been matched already.
     * This extends the ordinary search plan with negative tests. Takes control
     * and common labels into account (if any).
     * @param condition the condition for which a search plan is to be
     *        constructed
     * @param anchorNodes the nodes of the condition that have been matched
     *        already; if <code>null</code>, the condition's pattern map values
     *        are used
     * @param anchorEdges the edges of the condition that have been matched
     *        already; if <code>null</code>, the condition's pattern map values
     *        are used
     * @param relevantNodes nodes from the condition whose image should be a
     *        distinguishing factor in the returned matches; if
     *        <code>null</code>, all nodes are relevant
     */
    public abstract MatcherType createMatcher(Condition condition,
            Collection<? extends Node> anchorNodes,
            Collection<? extends Edge> anchorEdges,
            Collection<? extends Node> relevantNodes);

    /**
     * Implementations of this method would be factory methods returning 
     * a search strategy object for matching a given
     * graph, given also that certain nodes and edges have already been
     * pre-matched (<i>bound</i>).
     * 
     * @param graph the graph that is to be matched
     * @param anchorNodes the set of pre-matched nodes when searching; may be
     *        <code>null</code> if there are no pre-matched nodes
     * @param anchorEdges the set of pre-matched edges when searching; may be
     *        <code>null</code> if there are no pre-matched edges. It is assumed
     *        that the end nodes of all pre-matched edges are themselves
     *        pre-matched.
     * @param labelStore the node subtype relation in the graph
     * @return an object capable of returning matches in the <code>graph</code>
     *         that adhere to the conditions set by the anchors.  
     *         
     */
    public abstract MatcherType createMatcher(GraphShape graph,
            Collection<? extends Node> anchorNodes,
            Collection<? extends Edge> anchorEdges, LabelStore labelStore);

    /** Indicates if the matchers this factory produces are injective. */
    public abstract boolean isInjective();

    /**
     * Indicates if the matchers this factory produces ignore negations in the
     * host graph.
     */
    public abstract boolean isIgnoreNeg();

}
