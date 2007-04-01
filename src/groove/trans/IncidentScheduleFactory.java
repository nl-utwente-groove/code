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
 * $Id: IncidentScheduleFactory.java,v 1.3 2007-04-01 12:49:54 rensink Exp $
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.match.SearchPlanFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This implementation first returns the edges in order of descending connectivity with
 * the already-matched edges.
 * The connectivity of a given edge is defined as the number of edges incident with the 
 * end nodes of the edge.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 * @deprecated taken over by the {@link SearchPlanFactory} hierarchy
 */
@Deprecated
public class IncidentScheduleFactory extends AbstractScheduleFactory {
    /**
     * Constructs the result as described in the class comment,
     * given an initial set of already-matched nodes and edges.
     */
    @Override
    protected List<Element> newMatchingOrder(Graph subject, Set<Node> matchedNodes, Set<Edge> matchedEdges) {
        List<Element> result = new ArrayList<Element>();
        Set<Edge> remainingEdges = new HashSet<Edge>(subject.edgeSet());
        remainingEdges.removeAll(matchedEdges);
        // first iterate over the edges
        while (!remainingEdges.isEmpty()) {
            // find the edge with the highest connectivity
            Edge bestEdge = null;
            int maxIncluded = 0;
            int maxExcluded = 0;
            for (Edge edge: remainingEdges) {
                Set<Edge> included = new HashSet<Edge>();
                Set<Edge> excluded = new HashSet<Edge>();
                for (int i = 0; i < edge.endCount(); i++) {
                    Collection<? extends Edge> edgeSet = subject.edgeSet(edge.end(i));
                    if (matchedNodes.contains(edge.end(i))) {
                        included.addAll(edgeSet);
                    } else {
                        excluded.addAll(edgeSet);
                    }
                }
                int includedSize = included.size();
                int excludedSize = excluded.size();
                if (bestEdge == null || includedSize > maxIncluded) {
                    bestEdge = edge;
                    maxIncluded = includedSize;
                    maxExcluded = excludedSize;
                } else if (includedSize == maxIncluded && excludedSize > maxExcluded) {
                    bestEdge = edge;
                    maxIncluded = includedSize;
                    maxExcluded = excludedSize;
                }
            }
            result.add(bestEdge);
            remainingEdges.remove(bestEdge);
            for (int i = 0; i < bestEdge.endCount(); i++) {
                matchedNodes.add(bestEdge.end(i));
            }
        }
        // now look for isolated nodes
        for (Node node: subject.nodeSet()) {
            if (!matchedNodes.contains(node)) {
                result.add(node);
            }
        }
        return result;
    }
}