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
        result.setInitial(getInitial());
        for (BuchiLocation node : nodeSet()) {
            result.addNode(node);
        }
        for (BuchiTransition edge : edgeSet()) {
            result.addEdge(edge);
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
            Map<Node<String>,BuchiLocation> node2location =
                new HashMap<Node<String>,BuchiLocation>();
            Node<String> init = graph.getInit();
            result.setInitial(getLocation(node2location, init));
            Set<Node<String>> newNodes = new HashSet<Node<String>>();
            newNodes.add(init);
            while (!newNodes.isEmpty()) {
                Iterator<Node<String>> newNodeIter = newNodes.iterator();
                Node<String> node = newNodeIter.next();
                newNodeIter.remove();
                BuchiLocation location = getLocation(node2location, node);
                if (nodeSet().contains(location)) {
                    continue;
                }
                if (node.getAttributes().getBoolean("accepting")) {
                    location.setAccepting();
                }
                addNode(location);
                for (Edge<String> edge : node.getOutgoingEdges()) {
                    assert edge.getSource().equals(node);
                    BuchiLabel label =
                        new BuchiLabel(edge.getAction(), edge.getGuard());
                    Node<String> target = edge.getNext();
                    BuchiTransition transition =
                        new BuchiTransition(location, label, getLocation(
                            node2location, target));
                    addEdgeWithoutCheck(transition);
                    newNodes.add(target);
                }
            }
        } catch (ParseErrorException e) {
            throw new FormatException(e.getMessage());
        }
        return result;
    }

    private BuchiLocation getLocation(
            Map<Node<String>,BuchiLocation> node2location, Node<String> node) {
        BuchiLocation result = node2location.get(node);
        if (result == null) {
            result = new BuchiLocation(node2location.size());
            node2location.put(node, result);
        }
        return result;
    }

    /**
     * Returns the initial location.
     */
    public BuchiLocation getInitial() {
        return this.initial;
    }

    /**
     * Sets the initial location.
     */
    public void setInitial(BuchiLocation location) {
        this.initial = location;
    }

    /** The set of all locations. */
    private final Set<BuchiLocation> locations = new HashSet<BuchiLocation>();
    /** The initial location. */
    private BuchiLocation initial;

    /**
     * Return the prototype graph of this class.
     */
    static public BuchiGraph getPrototype() {
        return new BuchiGraph("");
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
