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
 * $Id: DefaultGraph.java,v 1.8 2008-01-30 09:32:51 iovka Exp $
 */
package groove.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of Graph based on a set of nodes and a mapping from nodes to
 * sets of outgoing edges.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:51 $
 */
public class DefaultGraph extends AbstractGraph<GraphCache> implements
        Cloneable {
    /**
     * Constructs a prototype object of this class, to be used as a factory for
     * new (default) graphs.
     * @return a prototype <tt>DefaultGraph</tt> instance, only intended to be
     *         used for its <tt>newGraph()</tt> method.
     */
    static public Graph getPrototype() {
        return new DefaultGraph();
    }

    /**
     * Constructs a new, empty Graph.
     * @ensure result.isEmpty()
     */
    public DefaultGraph() {
        // we need an explicit empty constructor
    }

    /**
     * Constructs a clone of a given Graph.
     * @param graph the DefaultGraph to be cloned
     * @require graph != null
     * @ensure result.equals(graph)
     */
    protected DefaultGraph(DefaultGraph graph) {
        for (Map.Entry<Node,Set<Edge>> edgeEntry : graph.edgeMap.entrySet()) {
            this.edgeMap.put(edgeEntry.getKey(),
                new HashSet<Edge>(edgeEntry.getValue()));
        }
    }

    /**
     * Constructs a clone of a given Graph.
     * @param graph the DefaultGraph to be cloned
     * @require graph != null
     * @ensure result.equals(graph)
     */
    protected DefaultGraph(Graph graph) {
        addEdgeSet(graph.edgeSet());
    }

    @Override
    public boolean containsNode(Node node) {
        return this.edgeMap.containsKey(node);
    }

    @Override
    public boolean containsEdge(Edge edge) {
        Set<Edge> edgeSet = this.edgeMap.get(edge.source());
        return edgeSet != null && edgeSet.contains(edge);
    }

    @Override
    @Deprecated
    public boolean containsElement(Element elem) {
        if (elem instanceof Node) {
            return this.edgeMap.containsKey(elem);
        } else {
            assert elem instanceof Edge;
            Set<Edge> edgeSet = this.edgeMap.get(((Edge) elem).source());
            return edgeSet != null && edgeSet.contains(elem);
        }
    }

    public Set<? extends Edge> edgeSet() {
        Set<Edge> result = new HashSet<Edge>();
        for (Map.Entry<Node,Set<Edge>> edgeEntry : this.edgeMap.entrySet()) {
            result.addAll(edgeEntry.getValue());
        }
        return Collections.unmodifiableSet(result);
    }

    @Override
    public Set<? extends Edge> outEdgeSet(Node node) {
        return Collections.unmodifiableSet(this.edgeMap.get(node));
    }

    public Set<? extends Node> nodeSet() {
        return Collections.unmodifiableSet(this.edgeMap.keySet());
    }

    @Override
    public Graph clone() {
        Graph result = new DefaultGraph(this);
        return result;
    }

    public Graph newGraph() {
        return new DefaultGraph();
    }

    // ------------------------- COMMANDS ------------------------------

    public boolean addNode(Node node) {
        assert !isFixed() : "Trying to add " + node + " to unmodifiable graph";
        boolean added = !containsElement(node);
        assert added == !new HashSet<Node>(nodeSet()).contains(node) : String.format(
            "Overlapping node number for %s in %s", node, nodeSet());
        if (added) {
            this.edgeMap.put(node, new HashSet<Edge>());
            fireAddNode(node);
        }
        return added;
    }

    public boolean addEdge(Edge edge) {
        // assert edge instanceof BinaryEdge : "This graph implementation only
        // supports binary edges";
        assert !isFixed() : "Trying to add " + edge + " to unmodifiable graph";
        Node source = edge.source();
        Node target = edge.target();
        Set<Edge> sourceOutEdges = this.edgeMap.get(source);
        if (sourceOutEdges == null) {
            addNode(source);
            sourceOutEdges = this.edgeMap.get(source);
        }
        if (!this.edgeMap.containsKey(source)) {
            addNode(source);
        }
        if (!this.edgeMap.containsKey(target)) {
            addNode(target);
        }
        boolean added = sourceOutEdges.add(edge);
        if (added) {
            fireAddEdge(edge);
        }
        return added;
    }

    public boolean addEdgeWithoutCheck(Edge edge) {
        assert !isFixed() : "Trying to add " + edge + " to unmodifiable graph";
        Set<Edge> sourceOutEdges = this.edgeMap.get(edge.source());
        boolean added = sourceOutEdges.add(edge);
        if (added) {
            fireAddEdge(edge);
        }
        return added;
    }

    public boolean removeEdge(Edge edge) {
        assert !isFixed() : "Trying to remove " + edge
            + " from unmodifiable graph";
        Set<Edge> outEdgeSet = this.edgeMap.get(edge.source());
        boolean removed = outEdgeSet != null && outEdgeSet.remove(edge);
        if (removed) {
            fireRemoveEdge(edge);
        }
        return removed;
    }

    public boolean removeNode(Node node) {
        assert !isFixed() : "Trying to remove " + node
            + " from unmodifiable graph";
        boolean result = false;
        Set<Edge> outEdges = this.edgeMap.remove(node);
        if (outEdges != null) {
            result = true;
            for (Edge outEdge : outEdges) {
                fireRemoveEdge(outEdge);
            }
            for (Set<Edge> edgeSet : this.edgeMap.values()) {
                Iterator<Edge> edgeIter = edgeSet.iterator();
                while (edgeIter.hasNext()) {
                    Edge edge = edgeIter.next();
                    if (edge.source().equals(node)
                        || edge.target().equals(node)) {
                        // remove and notify observers
                        edgeIter.remove();
                        fireRemoveEdge(edge);
                    }
                }
            }
            fireRemoveNode(node);
        }
        return result;
    }

    public boolean removeNodeWithoutCheck(Node node) {
        assert !isFixed() : "Trying to remove " + node
            + " from unmodifiable graph";
        boolean result = false;
        Set<Edge> outEdges = this.edgeMap.remove(node);
        if (outEdges != null) {
            result = true;
            fireRemoveNode(node);
        }
        return result;
    }

    /**
     * Map from the nodes of this graph to the corresponding sets of outgoing
     * edges.
     * @invariant <tt>edgeMap: Node -> 2^Edge</tt>
     */
    private final Map<Node,Set<Edge>> edgeMap = new HashMap<Node,Set<Edge>>();
}