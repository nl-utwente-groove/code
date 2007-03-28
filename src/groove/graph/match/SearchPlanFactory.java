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
 * $Id: SearchPlanFactory.java,v 1.2 2007-03-28 15:12:35 rensink Exp $
 */
package groove.graph.match;

import groove.graph.Graph;



/**
 * Interface that offers the functionality of creating a list of
 * graph elements for a given graph, in the order in which they should be
 * matched to minimized backtracking.
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public interface SearchPlanFactory {
    /** 
     * Factory method returning a list of search items for a graph. 
     * @param graph the graph that is to be matched
     * @return a modifiable list containing at the elements in 
     * <code>subject</code> that are to be matched to have a complete
     * matching. The list is not aliased and can be modified at will by the client. 
     */
	public Iterable<SearchItem> createSearchPlan(Graph graph);
}
