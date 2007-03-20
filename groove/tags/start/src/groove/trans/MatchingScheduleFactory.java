// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: MatchingScheduleFactory.java,v 1.1.1.2 2007-03-20 10:42:56 kastenberg Exp $
 */
package groove.trans;

import java.util.List;

import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Morphism;

/**
 * Interface that offers the functionality of creating a list of
 * graph elements for a given graph, in the order in which they should be
 * matched to minimized backtracking.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public interface MatchingScheduleFactory {
    /** 
     * Factory method returning a list of elements of a given graph.
     * The list is in an order that minimizes backtracking during matching. 
     * @param subject the graph that will be matched
     * @return a modifiable list of all the elements in <code>subject</code>. The list
     * is not aliased and can be modified at will by the client. 
     */
    public List<Element> newMatchingOrder(Graph subject);
    
    /** 
     * Factory method returning a list of elements of a graph, given as
     * the codomain of a morphism. The matching order may be constructed under the 
     * assumption that the morphism
     * domain has been matched already, meaning that all elements in the codomain
     * that serve as images of some domain element already have a unique image
     * and don't have to occur in the result list at all.
     * @param subject the morphism whose codomain is going to be matched
     * @return a modifiable list containing at least all the elements in 
     * <code>subject</code> that are <i>not</i> images of the morphism. The list
     * is not aliased and can be modified at will by the client. 
     */
    public List<Element> newMatchingOrder(Morphism subject);
    
    /**
     * Factory method reordering the graph conditions of a given graph predicate
     * The order should be chosen in order of decreasing chance of applicability,
     * so as to optimize the time needed to find an applicable condition 
     * (if there is any).
     * @param predicate the graph predicate whose conditions are to be ordered
     * @return the conditions of <code>predicate</code> in an optimized order
     */
    public List<GraphCondition> newConditionOrder(GraphPredicate predicate);
}