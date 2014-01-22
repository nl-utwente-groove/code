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
package groove.graph.plain;

import static groove.graph.GraphRole.NONE;
import groove.graph.AGraph;
import groove.graph.Edge;
import groove.graph.GraphRole;
import groove.graph.Node;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of Graph based on a set of nodes and a mapping from nodes to
 * sets of outgoing edges.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:51 $
 */
public class PlainGraph extends AGraph<PlainNode,PlainEdge> implements Cloneable {
    /**
     * Constructs a prototype object of this class, to be used as a factory for
     * new (default) graphs.
     * @return a prototype <tt>DefaultGraph</tt> instance, only intended to be
     *         used for its <tt>newGraph()</tt> method.
     */
    static public PlainGraph getPrototype() {
        return new PlainGraph(NO_NAME, NONE);
    }

    /**
     * Constructs a new, empty Graph.
     * @ensure result.isEmpty()
     * @param name the (non-{@code null}) name of the graph.
     */
    public PlainGraph(String name, GraphRole role) {
        super(name);
        this.role = role;
    }

    /**
     * Constructs a clone of a given Graph.
     * @param graph the DefaultGraph to be cloned
     * @require graph != null
     * @ensure result.equals(graph)
     */
    protected PlainGraph(PlainGraph graph) {
        super(graph.getName());
        this.role = graph.getRole();
        for (Map.Entry<PlainNode,Set<PlainEdge>> edgeEntry : graph.edgeMap.entrySet()) {
            this.edgeMap.put(edgeEntry.getKey(), new LinkedHashSet<PlainEdge>(edgeEntry.getValue()));
        }
    }

    @Override
    public boolean containsNode(Node node) {
        return this.edgeMap.containsKey(node);
    }

    @Override
    public boolean containsEdge(Edge edge) {
        Set<PlainEdge> edgeSet = this.edgeMap.get(edge.source());
        return edgeSet != null && edgeSet.contains(edge);
    }

    public Set<? extends PlainEdge> edgeSet() {
        Set<PlainEdge> result = new LinkedHashSet<PlainEdge>();
        for (Map.Entry<PlainNode,Set<PlainEdge>> edgeEntry : this.edgeMap.entrySet()) {
            result.addAll(edgeEntry.getValue());
        }
        return Collections.unmodifiableSet(result);
    }

    @Override
    public Set<? extends PlainEdge> outEdgeSet(Node node) {
        return Collections.unmodifiableSet(this.edgeMap.get(node));
    }

    public Set<? extends PlainNode> nodeSet() {
        return Collections.unmodifiableSet(this.edgeMap.keySet());
    }

    @Override
    public PlainGraph clone() {
        PlainGraph result = new PlainGraph(this);
        return result;
    }

    public PlainGraph newGraph(String name) {
        return new PlainGraph(name, getRole());
    }

    // ------------------------- COMMANDS ------------------------------

    @Override
    public PlainFactory getFactory() {
        return PlainFactory.instance();
    }

    public boolean addNode(PlainNode node) {
        assert !isFixed() : "Trying to add " + node + " to unmodifiable graph";
        boolean added = !containsNode(node);
        if (added) {
            this.edgeMap.put(node, new LinkedHashSet<PlainEdge>());
            fireAddNode(node);
        }
        return added;
    }

    public boolean addEdge(PlainEdge edge) {
        assert isTypeCorrect(edge);
        assert !isFixed() : "Trying to add " + edge + " to unmodifiable graph";
        Set<PlainEdge> sourceOutEdges = this.edgeMap.get(edge.source());
        boolean added = sourceOutEdges.add(edge);
        if (added) {
            fireAddEdge(edge);
        }
        return added;
    }

    public boolean removeEdge(PlainEdge edge) {
        assert !isFixed() : "Trying to remove " + edge + " from unmodifiable graph";
        Set<PlainEdge> outEdgeSet = this.edgeMap.get(edge.source());
        boolean removed = outEdgeSet != null && outEdgeSet.remove(edge);
        if (removed) {
            fireRemoveEdge(edge);
        }
        return removed;
    }

    /** Reimplementation to improve performance. */
    @Override
    public boolean removeNodeContext(PlainNode node) {
        assert !isFixed() : "Trying to remove " + node + " from unmodifiable graph";
        boolean result = false;
        Set<PlainEdge> outEdges = this.edgeMap.remove(node);
        if (outEdges != null) {
            result = true;
            for (PlainEdge outEdge : outEdges) {
                fireRemoveEdge(outEdge);
            }
            for (Set<PlainEdge> edgeSet : this.edgeMap.values()) {
                Iterator<PlainEdge> edgeIter = edgeSet.iterator();
                while (edgeIter.hasNext()) {
                    PlainEdge edge = edgeIter.next();
                    if (edge.source().equals(node) || edge.target().equals(node)) {
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

    public boolean removeNode(PlainNode node) {
        assert !isFixed() : "Trying to remove " + node + " from unmodifiable graph";
        boolean result = false;
        Set<PlainEdge> outEdges = this.edgeMap.remove(node);
        if (outEdges != null) {
            result = true;
            fireRemoveNode(node);
        }
        return result;
    }

    /**
     * Returns the role of this default graph, as set in the constructor.
     */
    public final GraphRole getRole() {
        return this.role;
    }

    /**
     * Map from the nodes of this graph to the corresponding sets of outgoing
     * edges.
     * @invariant <tt>edgeMap: DefaultNode -> 2^DefaultEdge</tt>
     */
    private final Map<PlainNode,Set<PlainEdge>> edgeMap =
        new LinkedHashMap<PlainNode,Set<PlainEdge>>();

    /** The role of this default graph. */
    private final GraphRole role;
}