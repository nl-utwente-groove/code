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
 * $Id: IndegreeScheduleFactory.java,v 1.2 2007-03-30 15:50:26 rensink Exp $
 */
package groove.trans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.rel.RegExprLabel;
import groove.util.Bag;
import groove.util.HashBag;

/**
 * Strategy that yields the edges in order of ascending indegree of
 * their source nodes.
 * The idea is that the "roots" of a graph (those starting in nodes with
 * small indegree) are likely to give a better immediate reduction of
 * the number of possible matches.
 * Furthermore, regular expression edges are saved to the last.
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public class IndegreeScheduleFactory extends AbstractScheduleFactory {
    /**
     * Constructs the result according to the principle set forth in
     * the class comment.
     */
    @Override
    protected List<Element> newMatchingOrder(Graph subject, Set<Node> matchedNodes, Set<Edge> matchedEdges) {
        List<Element> result = new ArrayList<Element>();
        final Set<Node> remainingNodes = new HashSet<Node>(subject.nodeSet());
        remainingNodes.removeAll(matchedNodes);
        Set<Edge> remainingEdges = new HashSet<Edge>(subject.edgeSet());
        remainingEdges.removeAll(matchedEdges);
        // compute indegrees
        final Bag<Node> indegrees = new HashBag<Node>();
        for (Edge edge: subject.edgeSet()) {
            for (int i = 1; i < edge.endCount(); i++) {
                if (!edge.end(i).equals(edge.source())) {
                    indegrees.add(edge.end(i));
                }
            }
        }
        // comparator to pick the best remaining edge
        // by delegating to compareTo(Bag, Set, Edge, Edge)
        Comparator<Edge> edgeComparator = new Comparator<Edge>() {
            public int compare(Edge o1, Edge o2) {
                return compareTo(indegrees, remainingNodes, o1, o2);
            }
            
        };
        // pick the best remaining edge each time and add it to the
        // result, adjusting the remaining edges, nodes and indegrees
        while (! remainingEdges.isEmpty()) {
            Edge bestEdge = Collections.max(remainingEdges, edgeComparator);
            result.add(bestEdge);
            remainingEdges.remove(bestEdge);
            for (int i = 0; i < bestEdge.endCount(); i++) {
                Node end = bestEdge.end(i);
                remainingNodes.remove(end);
                if (!end.equals(bestEdge.source())) {
                    indegrees.remove(end);
                }
            }
        }
        // remaining nodes are loose nodes: add them
        result.addAll(remainingNodes);
        return result;
    }
    
    /**
     * Compares two booleans, and returns the result of the comparison as an integer.
     * @return <code>+1</code> if <code>first</code> is <code>true</code>
     * but <code>second</code> is not, <code>-1</code> if the reverse is the case,
     * and <code>0</code> if their values are equal.
     */
    protected int compare(boolean first, boolean second) {
        return first ? (second ? 0 : +1) : (second ? -1 : 0);
    }
    
    /**
     * Compares two labels, with the purpose of determining which one
     * should be tried first.
     * The rules are as follows:
     * <ul>
     * <li> Regular expression labels are worse than others
     * <li> Reflexive regular expressions are worse than others
     * <li> Non-regular expression labels are ordered by their natural ordering
     * </ul>
     */
    protected int compare(Label first, Label second) {
        int result = compare(second instanceof RegExprLabel, first instanceof RegExprLabel);
        if (result != 0) {
            return result;
        }
        if (first instanceof RegExprLabel) {
            result = compare(!((RegExprLabel) first).getAutomaton().isAcceptsEmptyWord(), !((RegExprLabel) second).getAutomaton().isAcceptsEmptyWord());
        }
        return result;
    }
    
    /**
     * Compares two edges in their quality as the next edge to be scheduled
     * for matching.
     * This implementation considers <code>second</code> to be better (= larger) than
     * <code>first</code> if, in order of descending importance
     * <ul> The label of <code>first</code> is a regular expression whereas that
     * of <code>second</code> is not;
     * <li> The nomber of already-matched end points of <code>first</code> is lower than
     * that of <code>second</code>;
     * <li> The indegree of the source node of <code>first</code> is larger than
     * that of <code>second</code>;
     * <li> The indegree of the target (i.e., last) end node of <code>first</code> is 
     * larger than that of <code>second</code>
     * </ul>
     * If all fails, and <code>first</code> and <code>second</code> are not
     * equal, the method delegates to {@link Edge#compareTo(Element)}.
     * @return A negative number if <code>second</code> is better than <code>first</code>,
     * zero if they are equally good, and a positive number if <code>first</code> is better.
     */
    protected int compareTo(Bag<Node> indegrees, Set<Node> remainingNodes, Edge first, Edge second) {
        // equal edges yield 0
        if (first.equals(second)) {
            return 0;
        }
        // first test for regularity of the labels (false = better)
        int result = compare(first.label(), second.label());
        if (result != 0) {
            return result;
        }
        // now test for connectivity to matched nodes (higher = better)
        result = connectivity(remainingNodes, first) - connectivity(remainingNodes, second);
        if (result != 0) {
            return result;
        }
        // now test for the indegree of the source (lower = better)
        result = sourceIndegree(indegrees, second) - sourceIndegree(indegrees, first); 
        if (result != 0) {
            return result;
        }
        // now test for the indegree of the target (lower = better)
        result = targetIndegree(indegrees, second) - targetIndegree(indegrees, first); 
        if (result != 0) {
            return result;
        }
        // we give up; use the natural ordering
        return first.compareTo(second);
    }
    
    /**
     * Returns the number of connected end points of a given edge.
     * An end point is connected if it is not in the set of remaining nodes.
     */
    private int connectivity(Set<Node> remainingNodes, Edge edge) {
        int result = 0;
        for (int i = 0; i < edge.endCount(); i++) {
            Node end = edge.end(i);
            if (!remainingNodes.contains(end)) {
                result++;
            }
        }
        return result;
    }

    /**
     * Returns the indegree of the source node of an edge.
     */
    private int sourceIndegree(Bag<Node> indegrees, Edge edge) {
        return indegrees.multiplicity(edge.source());
    }

    /**
     * Returns the indegree of the target node of an edge.
     */
    private int targetIndegree(Bag<Node> indegrees, Edge edge) {
        return indegrees.multiplicity(edge.end(edge.endCount()-1));
    }
}