/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.verify.ltl2ba;

import gov.nasa.ltl.graph.Edge;
import gov.nasa.ltl.graph.Graph;
import gov.nasa.ltl.graph.Node;
import gov.nasa.ltl.trans.LTL2Buchi;
import gov.nasa.ltl.trans.ParseErrorException;
import groove.verify.BuchiLocation;
import groove.verify.BuchiTransition;
import groove.verify.DefaultBuchiLocation;
import groove.view.FormatException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Harmen Kastenberg
 * @version $Revision $
 */
public class NASABuchiGraph extends AbstractBuchiGraph {
    private final Map<Node,BuchiLocation> node2location;
    /** Set of already visited nodes. */
    private final Set<Node> visitedNodes;

    private NASABuchiGraph() {
        this.node2location = new HashMap<Node,BuchiLocation>();
        this.visitedNodes = new HashSet<Node>();
    }

    /**
     * Return the prototype graph of this class.
     */
    static public BuchiGraph getPrototype() {
        return new NASABuchiGraph();
    }

    @Override
    public boolean isEnabled(BuchiTransition transition,
            Set<String> applicableRules) {
        return false;
    }

    public BuchiGraph newBuchiGraph(String formula) throws FormatException {
        final BuchiGraph result = new NASABuchiGraph();
        try {
            Graph graph = LTL2Buchi.translate(formula);
            Node init = graph.getInit();
            IVisitor visitor = new Visitor(result);
            visitor.visitNode(init);
            result.addInitialLocation(getLocation(init));
        } catch (ParseErrorException e) {
            throw new FormatException(e.getMessage());
        }
        return result;
    }

    /** 
     * Indicates if a given node has already been visited.
     * also sets the status to visited. 
     */
    private boolean isVisited(Node node) {
        return !this.visitedNodes.add(node);

    }

    private BuchiLocation getLocation(Node node) {
        BuchiLocation result = null;
        if (this.node2location.containsKey(node)) {
            result = this.node2location.get(node);
        } else {
            result = new DefaultBuchiLocation();
            this.node2location.put(node, result);
        }
        return result;
    }

    private interface IVisitor {
        /**
         * Visit the provided node.
         * @param node the node to visit
         */
        public void visitNode(Node node);

        /**
         * Visit the provided edge;
         * @param edge the edge to visit
         */
        public void visitEdge(Edge edge);
    }

    private class Visitor implements IVisitor {
        private BuchiGraph graph;

        public Visitor(BuchiGraph graph) {
            this.graph = graph;
        }

        public void visitNode(Node node) {
            // only do something if the node has not already been visited
            if (!isVisited(node)) {
                BuchiLocation location = getLocation(node);
                for (Edge edge : node.getOutgoingEdges()) {
                    visitEdge(edge);
                }
                if (node.getAttributes().getBoolean("accepting")) {
                    location.setAccepting();
                    this.graph.addAcceptingLocation(location);
                }
            }
        }

        public void visitEdge(Edge edge) {
            NASABuchiLabel label =
                new NASABuchiLabel(edge.getAction(), edge.getGuard());
            Node source = edge.getSource();
            Node target = edge.getNext();
            BuchiTransition transition =
                new NASABuchiTransition(getLocation(source), label,
                    getLocation(target));
            this.graph.addTransition(transition);
            visitNode(target);
        }
    }
}
