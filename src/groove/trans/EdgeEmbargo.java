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
 * $Id: EdgeEmbargo.java,v 1.5 2007-09-16 21:44:30 rensink Exp $
 */
package groove.trans;

import groove.graph.DefaultMorphism;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.rel.VarMorphism;
import groove.util.Groove;

/**
 * A specialised NAC that forbids the presence of a certain edge.
 * @author Arend Rensink
 * @version $Revision: 1.5 $
 */
public class EdgeEmbargo extends DefaultGraphCondition implements NAC {
    /**
     * Constructs an edge embargo on a given graph from a given edge with
     * end nodes in a given graph (presumably a rule lhs).
     * @param graph the graph on which this embargo works
     * @param embargoEdge the edge that is forbidden
     */
    public EdgeEmbargo(Graph graph, Edge embargoEdge, SystemProperties properties) {
        super(new DefaultMorphism(graph, graph.newGraph()), properties);
        this.embargoEdge = embargoEdge;
        int arity = embargoEdge.endCount();
        Node[] endImages = new Node[arity];
        for (int i = 0; i < arity; i++) {
            Node end = embargoEdge.end(i);
            endImages[i] = getNode(end);
            if (endImages[i] == null) {
                endImages[i] = getTarget().addNode();
                putNode(end, endImages[i]);
            }
        }
        cod().addEdge(endImages, embargoEdge.label());
        if (CONSTRUCTOR_DEBUG) {
            Groove.message("Edge embargo: " + this);
            Groove.message("Embargo edge: " + embargoEdge);
        }
    }

    /**
     * Method overridden for efficiency
     */
    @Deprecated
    public boolean forbids(VarMorphism match) {
        Edge forbiddenEdge = match.mapEdge(embargoEdge);
        assert forbiddenEdge != null : embargoEdge + " should have an image under the map " + match.elementMap();
        boolean result;
            result = match.cod().containsElement(forbiddenEdge);
        if (APPLICATION_DEBUG) {
            Groove.message(
                "The following graph "
                    + (result ? "DOES" : "DOES NOT")
                    + " contain the forbidden edge "
                    + forbiddenEdge);
            Groove.message(match.cod());
        }
        return result;
    }

    /**
     * Returns the embargo edge, which is an edge in this NAC's domain 
     * that is tested for in {@link #forbids(VarMorphism)}.
     */
    public Edge getEmbargoEdge() {
        return embargoEdge;
    }

    /**
     * Returns the source node of the forbidden edge. 
     * @ensure <tt>result != null</tt>
     */
    public Node edgeSource() {
        return embargoEdge.source();
    }

    /**
     * Returns the label of the forbidden edge.
     * @ensure <tt>result != null</tt>
     */
    public Label edgeLabel() {
        return embargoEdge.label();
    }
    
    /**
     * The forbidden edge.
     */
    protected final Edge embargoEdge;

    private final static boolean APPLICATION_DEBUG = false;
    private final static boolean CONSTRUCTOR_DEBUG = false;
}