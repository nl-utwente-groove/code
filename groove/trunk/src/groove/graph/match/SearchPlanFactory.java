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
 * $Id: SearchPlanFactory.java,v 1.3 2007-08-22 15:04:57 rensink Exp $
 */
package groove.graph.match;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;

import java.util.Collection;



/**
 * Interface offering the functionality to create search plans for subgraph matching.
 * Subgraph matching is the problem of locating a given graph as a subgraph of another.
 * A search plan is a list of search items, all of which should be successfully executed
 * in order for the full graph to be matched.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public interface SearchPlanFactory {
    /** 
     * Factory method returning a list of search items for matching a given graph. 
     * This is a convenience method for {@link #createSearchPlan(Graph, Collection, Collection)} with
     * empty sets of bound nodes and edges.
     * @param graph the graph that is to be matched
     * @return a list of search items that will result in a matching of <code>graph</code>
     * when successfully executed in the given order
     */
    public Iterable<SearchItem> createSearchPlan(Graph graph);
    
    /** 
     * Factory method returning a list of search items for matching a given graph, given also
     * that certain nodes and edges have already been pre-matched (<i>bound</i>).
     * @param graph the graph that is to be matched
     * @param boundNodes the pre-matched nodes
     * @param boundEdges the pre-matched edges
     * @return a list of search items that will result in a matching of <code>graph</code>
     * when successfully executed in the given order
     */
    public Iterable<SearchItem> createSearchPlan(Graph graph, Collection< ? extends Node> boundNodes, Collection< ? extends Edge> boundEdges);
}
