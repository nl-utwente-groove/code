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
package groove.verify;

import gov.nasa.ltl.graph.Edge;
import gov.nasa.ltl.graph.Graph;
import gov.nasa.ltl.graph.Node;
import gov.nasa.ltl.trans.LTL2Buchi;
import gov.nasa.ltl.trans.ParseErrorException;
import groove.view.FormatException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Harmen Kastenberg
 * @version $Revision $
 */
public class BuchiGraph {
    private BuchiGraph() {
        this.node2location = new HashMap<Node<String>,BuchiLocation>();
        this.visitedNodes = new HashSet<Node<String>>();
    }

    /**
     * Create a {@link BuchiGraph} from the provided LTL formula.
     * 
     * @param formula the formula for which to create an equivalent
     *        {@link BuchiGraph}
     * @return the {@link BuchiGraph}
     * @throws FormatException if the formula contains (parsing) errors
     */
    public BuchiGraph newBuchiGraph(String formula) throws FormatException {
        final BuchiGraph result = new BuchiGraph();
        try {
            Graph<String> graph = LTL2Buchi.translate(LTLParser.parse(formula));
            Node<String> init = graph.getInit();
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
    private boolean isVisited(Node<String> node) {
        return !this.visitedNodes.add(node);

    }

    private BuchiLocation getLocation(Node<String> node) {
        BuchiLocation result = null;
        if (this.node2location.containsKey(node)) {
            result = this.node2location.get(node);
        } else {
            result = new BuchiLocation(this.node2location.size());
            this.node2location.put(node, result);
        }
        return result;
    }

    /**
     * Returns the set of initial locations.
     */
    public Set<BuchiLocation> initialLocations() {
        if (null == this.initialLocations) {
            this.initialLocations = new HashSet<BuchiLocation>();
        }
        return this.initialLocations;
    }

    /**
     * Returns the set of accepting locations.
     */
    public Set<BuchiLocation> acceptingLocations() {
        if (null == this.acceptingLocations) {
            this.acceptingLocations = new HashSet<BuchiLocation>();
        }
        return this.acceptingLocations;
    }

    /**
     * Add the provided Büchi location to the set of initial locations.
     * @return see {@link Set#add(Object)}
     */
    public boolean addInitialLocation(BuchiLocation location) {
        return initialLocations().add(location);
    }

    /**
     * Add the provided Buechi location to the set of accepting locations.
     * @return see {@link Set#add(Object)}
     */
    public boolean addAcceptingLocation(BuchiLocation location) {
        return acceptingLocations().add(location);
    }

    /**
     * @return see {@link Set#add(Object)}
     */
    public boolean addTransition(BuchiTransition transition) {
        return transition.source().addTransition(transition);
    }

    private final Map<Node<String>,BuchiLocation> node2location;
    /** Set of already visited nodes. */
    private final Set<Node<String>> visitedNodes;

    private Set<BuchiLocation> initialLocations;

    private Set<BuchiLocation> acceptingLocations;

    /**
     * Return the prototype graph of this class.
     */
    static public BuchiGraph getPrototype() {
        return new BuchiGraph();
    }

    private interface IVisitor {
        /**
         * Visit the provided node.
         * @param node the node to visit
         */
        public void visitNode(Node<String> node);

        /**
         * Visit the provided edge;
         * @param edge the edge to visit
         */
        public void visitEdge(Edge<String> edge);
    }

    private class Visitor implements IVisitor {
        private BuchiGraph graph;

        public Visitor(BuchiGraph graph) {
            this.graph = graph;
        }

        public void visitNode(Node<String> node) {
            // only do something if the node has not already been visited
            if (!isVisited(node)) {
                BuchiLocation location = getLocation(node);
                for (Edge<String> edge : node.getOutgoingEdges()) {
                    visitEdge(edge);
                }
                if (node.getAttributes().getBoolean("accepting")) {
                    location.setAccepting();
                    this.graph.addAcceptingLocation(location);
                }
            }
        }

        public void visitEdge(Edge<String> edge) {
            BuchiLabel label =
                new BuchiLabel(edge.getAction(), edge.getGuard());
            Node<String> source = edge.getSource();
            Node<String> target = edge.getNext();
            BuchiTransition transition =
                new BuchiTransition(getLocation(source), label,
                    getLocation(target));
            this.graph.addTransition(transition);
            visitNode(target);
        }
    }
}
