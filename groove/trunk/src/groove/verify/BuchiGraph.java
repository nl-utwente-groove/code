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
import groove.graph.AbstractGraph;
import groove.graph.GraphRole;
import groove.util.NestedIterator;
import groove.util.TransformIterator;
import groove.view.FormatException;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Harmen Kastenberg
 * @version $Revision $
 */
public class BuchiGraph extends AbstractGraph<BuchiLocation,BuchiTransition> {
    private BuchiGraph(String name) {
        super(name);
        this.node2location = new HashMap<Node<String>,BuchiLocation>();
        this.visitedNodes = new HashSet<Node<String>>();
    }

    @Override
    public Set<BuchiLocation> nodeSet() {
        return this.locations;
    }

    @Override
    public Set<? extends BuchiTransition> edgeSet() {
        return new TransitionSet();
    }

    @Override
    public BuchiGraph newGraph(String name) {
        return new BuchiGraph(name);
    }

    @Override
    public boolean addNode(BuchiLocation node) {
        return this.locations.add(node);
    }

    @Override
    public boolean removeEdge(BuchiTransition edge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addEdgeWithoutCheck(BuchiTransition edge) {
        return edge.source().addTransition(edge);
    }

    @Override
    public boolean removeNodeWithoutCheck(BuchiLocation node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GraphRole getRole() {
        return GraphRole.BUCHI;
    }

    @Override
    public BuchiGraph clone() {
        BuchiGraph result = newGraph(getName());
        for (BuchiLocation node : nodeSet()) {
            result.addNode(node);
        }
        for (BuchiTransition edge : edgeSet()) {
            result.addEdge(edge);
        }
        for (BuchiLocation initial : initialLocations()) {
            result.addInitialLocation(initial);
        }
        for (BuchiLocation accepting : acceptingLocations()) {
            result.addAcceptingLocation(accepting);
        }
        return result;
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
        final BuchiGraph result = new BuchiGraph(formula);
        try {
            Graph<String> graph = LTL2Buchi.translate(LTLParser.parse(formula));
            Node<String> init = graph.getInit();
            Visitor visitor = new Visitor(result);
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
        return this.initialLocations;
    }

    /**
     * Returns the set of accepting locations.
     */
    public Set<BuchiLocation> acceptingLocations() {
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

    /** The set of all locations. */
    private final Set<BuchiLocation> locations = new HashSet<BuchiLocation>();
    /** The set of initial locations. */
    private final Set<BuchiLocation> initialLocations =
        new HashSet<BuchiLocation>();
    /** The set of accepting locations. */
    private final Set<BuchiLocation> acceptingLocations =
        new HashSet<BuchiLocation>();

    /**
     * Return the prototype graph of this class.
     */
    static public BuchiGraph getPrototype() {
        return new BuchiGraph("");
    }

    private class Visitor {
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

    /** 
     * Offers a modifiable view on the transitions stored in the locations 
     * of this automaton.
     */
    private class TransitionSet extends AbstractSet<BuchiTransition> {
        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object o) {
            if (o instanceof BuchiTransition) {
                BuchiTransition trans = (BuchiTransition) o;
                return trans.source().outTransitions().contains(o);
            } else {
                return false;
            }
        }

        @Override
        public Iterator<BuchiTransition> iterator() {
            return new NestedIterator<BuchiTransition>(
                new TransformIterator<BuchiLocation,Iterator<BuchiTransition>>(
                    nodeSet().iterator()) {
                    @Override
                    protected Iterator<BuchiTransition> toOuter(
                            BuchiLocation from) {
                        return from.outTransitions().iterator();
                    }
                });
        }

        @Override
        public int size() {
            int result = 0;
            for (BuchiLocation state : nodeSet()) {
                result += state.outTransitions().size();
            }
            return result;
        }
    }
}
