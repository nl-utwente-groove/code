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
package groove.match.rete;

import groove.graph.DefaultEdge;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Node;
import groove.util.Reporter;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class EdgeCheckerNode extends ReteNetworkNode {

    private Element[] pattern = new Element[1];

    protected static final Reporter reporter =
        Reporter.register(EdgeCheckerNode.class);
    protected static final Reporter receiveEdgeReporter =
        reporter.register("receiveEdge(source, gEdge, action)");

    /**
     * @param e
     */
    public EdgeCheckerNode(ReteNetwork network, Edge e) {
        super(network);
        Node n1 = DefaultNode.createNode();
        Node n2 =
            (e.source().equals(e.target())) ? n1 : DefaultNode.createNode();
        this.pattern[0] = DefaultEdge.createEdge(n1, e.label().text(), n2);
        //This is just to fill up the lookup table
        LookupTable t = getPatternLookupTable();
    }

    @Override
    public boolean addSuccessor(ReteNetworkNode nnode) {
        boolean result =
            (nnode instanceof SubgraphCheckerNode)
                || (nnode instanceof ConditionChecker)
                || (nnode instanceof DisconnectedSubgraphChecker);

        if (result) {
            result = super.addSuccessor(nnode);
        }
        return result;
    }

    /**
     * Determines if this n-edge-checker node could potentially be mapped to a given graph edge.     * 
     * @param e the given graph edge
     * @return
     */
    public boolean canBeMappedToEdge(Edge e) {
        Edge e1 = this.getEdge();
        //condition 1: labels must match
        //condition 2: if this is an edge checker for a loop then e should also be a loop
        return //e1.label().equals(e.label()) &&   <-- commented out because we check this in the root
        (!e1.source().equals(e1.target()) || (e.source().equals(e.target())));
    }

    public boolean canBeStaticallyMappedToEdge(Edge e) {
        Edge e1 = this.getEdge();
        //condition 1: labels must match
        //condition 2: if this is an edge checker for a loop then e should also be a loop and vice versa
        return e1.label().text().equals(e.label().text())
            && (e1.source().equals(e1.target()) == (e.source().equals(e.target())));
    }

    /**
     * Receives an edge that is sent from the root to see 
     * if the edge label equals the label of the edge-checker itself.
     * @param source 
     * @param gEdge
     * @param action
     */
    public void receiveEdge(ReteNetworkNode source, groove.graph.Edge gEdge,
            Action action) {
        receiveEdgeReporter.start();
        //Dynamically, an edge-checker
        // tests a g-edge ((v, \alpha(v)), (w, \alpha(w)), \beta(v, w)) 
        // that is sent from the root if alpha(v) = u A alpha(w) =
        // \mu \and beta(v, w) = $. Any g-edge that successfully 
        // passes the test of an edge-checker is sent to all direct
        // successor n-nodes of that edge-checker.             

        //For groove since there are no node labels, we check the equality of edge labels
        //and we check that the received edge is of the same "shape" as the associated edge
        //of this edge-checker n-node, i.e. either both are unary (loop) edges or both are
        //binary(non-loop) edges.
        Edge nEdge = this.getEdge();
        if (this.canBeMappedToEdge(gEdge)) {
            ReteNetworkNode previous = null;
            int repeatedSuccessorIndex = 0;
            for (ReteNetworkNode n : this.getSuccessors()) {
                repeatedSuccessorIndex =
                    (n != previous) ? 0 : (repeatedSuccessorIndex + 1);
                if (n instanceof SubgraphCheckerNode) {
                    ((SubgraphCheckerNode) n).receive(this,
                        repeatedSuccessorIndex, gEdge, action);
                } else if (n instanceof ConditionChecker) {
                    ((ConditionChecker) n).receive(this,
                        repeatedSuccessorIndex, gEdge, action);
                } else if (n instanceof DisconnectedSubgraphChecker) {
                    ((DisconnectedSubgraphChecker) n).receive(this,
                        repeatedSuccessorIndex, gEdge, action);
                }
                previous = n;
            }
        }
        receiveEdgeReporter.stop();
    }

    /**
     * @return the edge associated with this edge-checker
     */
    public Edge getEdge() {
        return (Edge) this.pattern[0];
    }

    @Override
    /**
     * used by the currently processed production rules during construction time
     * and zero otherwise.
     * 
     * This is a construction-time method only.  
     */
    public int size() {
        return this.pattern.length;
    }

    @Override
    public boolean equals(ReteNetworkNode node) {
        return (node != null) && (this == node);
    }

    @Override
    public String toString() {
        return "Checking edge: " + this.getEdge().toString();
    }

    @Override
    public Element[] getPattern() {
        return this.pattern;
    }

}
