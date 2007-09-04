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
 * $Id $
 */

package groove.match;

import groove.calc.Property;
import groove.graph.Graph;
import groove.graph.NodeEdgeMap;
import groove.rel.VarNodeEdgeMap;

import java.util.Collection;
import java.util.Iterator;

/**
 * Interface wrapping a match strategy.
 * A class implementing this interface will generate element maps given
 * a target graph, together with and a partial (initial) map to that target graph.
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public interface MatchStrategy {
    /** 
     * Returns a single match to a given graph, extending a given partial match.
     * The partial match should be defined precisely for the pre-matched elements indicated
     * when constructing the match strategy.
     * @param graph the graph into which the matching is to go
     * @param preMatch a predefined mapping to the elements of <code>graph</code> that 
     * the solution should respect. May be <code>null</code> if there is no predefined mapping
     * @return a mapping to the elements of <code>graph</code> that augments <code>partialMatch</code>
     * and fulfills the requirements to be a total match
     */
    public VarNodeEdgeMap getMatch(Graph graph, NodeEdgeMap preMatch);
    
    /** 
     * Returns the collection of all matches to a given graph that extend a given partial match. 
     * The partial match should be defined precisely for the pre-matched elements indicated
     * when constructing the match strategy.
     * @param graph the graph into which the matching is to go
     * @param preMatch a predefined mapping to the elements of <code>graph</code> that all
     * the solutions should respect. May be <code>null</code> if there is no predefined mapping
     * @return the set of all mappings to the elements of <code>graph</code> that
     * augment <code>partialMatch</code> and fulfil the requirements to be total matches
     */
    public Collection<VarNodeEdgeMap> getMatchSet(Graph graph, NodeEdgeMap preMatch);
    
    /** 
     * Returns an iterator over all matches to a given graph that extend a given partial match.
     * The partial match should be defined precisely for the pre-matched elements indicated
     * when constructing the match strategy.
     * This method is an alternative to {@link #getMatchSet(Graph, NodeEdgeMap)} which allows the
     * matches to be computed lazily. 
     * @param graph the graph into which the matching is to go
     * @param preMatch a predefined mapping to the elements of <code>graph</code> that all
     * the solutions should respect. May be <code>null</code> if there is no predefined mapping
     * @return an iterator over all mappings to the elements of <code>graph</code> that
     * augment <code>partialMatch</code> and fulfil the requirements to be total matches
     */
    public Iterator<VarNodeEdgeMap> getMatchIter(Graph graph, NodeEdgeMap preMatch);

    /**
     * Sets a filter for this strategy.
     * A filter is a property that is required to hold for all search results
     * returned by one of the search methods.
     * @param filter the filter to be set; may be <code>null</code>, in which change
     * no constraints are imposed
     */
	public void setFilter(Property<VarNodeEdgeMap> filter);
}
