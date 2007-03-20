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
 * $Id: AbstractScheduleFactory.java,v 1.1.1.2 2007-03-20 10:42:55 kastenberg Exp $
 */

package groove.trans;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.Node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Abstract class that delegates both factory methods to a single method
 * that takes a graph and the sets of already-matched nodes and edges as parameters.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
abstract public class AbstractScheduleFactory implements MatchingScheduleFactory {
    /**
     * Constructs the result as described in the class comment.
     */
    public List<Element> newMatchingOrder(Graph subject) {
        return newMatchingOrder(subject, new HashSet<Node>(), new HashSet<Edge>());
    }
    
    /**
     * Constructs the result as described in the class comment.
     * The already existing images of the morphism are excluded from the result.
     */
    public List<Element> newMatchingOrder(Morphism subject) {
        Set<Node> matchedNodes = new HashSet<Node>(subject.nodeMap().values());
        Set<Edge> matchedEdges = new HashSet<Edge>(subject.edgeMap().values());
        List<Element> result = newMatchingOrder(subject.cod(), matchedNodes, matchedEdges);
        return result;
    }
    
    /**
     * Returns a new ordering of the conditions in <code>predicate</code>
     * by ordering the existing conditions according to the {@link Comparator}
     * returned by {@link #createConditionComparator()}.
     * In this implementation this means from small to large.
     */
    public List<GraphCondition> newConditionOrder(GraphPredicate predicate) {
        SortedSet<GraphCondition> orderedNACSet = new TreeSet<GraphCondition>(createConditionComparator());
        orderedNACSet.addAll(predicate.getConditions());
        List<GraphCondition> result = new ArrayList<GraphCondition>(predicate.getConditions().size());
        result.addAll(orderedNACSet);
        return result;
    }
    
    /**
     * Constructs the result as described in the class comment.
     */
    abstract protected List<Element> newMatchingOrder(Graph subject, Set<Node> matchedNodes, Set<Edge> matchedEdges);
    /**
     * Returns a comparator of NACs, determining the resultant order of
     * {@link #newConditionOrder(GraphPredicate)}; that is, the smaller elements go first.
     * This implementation calls a NAC smaller than another if it has fewer elements
     * that should be matched.
     */
    protected Comparator<GraphCondition> createConditionComparator() {
        return new Comparator<GraphCondition>() {
            public int compare(GraphCondition first, GraphCondition second) {
                int firstCount = first.getTarget().size() - first.getContext().size();
                int secondCount = second.getTarget().size() - second.getContext().size();
                return firstCount - secondCount;
            }
        };
    }
}