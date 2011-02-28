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

import static groove.graph.GraphRole.NONE;

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
public class DefaultGraph extends AbstractGraph<DefaultNode,DefaultEdge>
        implements Cloneable {
    /**
     * Constructs a prototype object of this class, to be used as a factory for
     * new (default) graphs.
     * @return a prototype <tt>DefaultGraph</tt> instance, only intended to be
     *         used for its <tt>newGraph()</tt> method.
     */
    static public DefaultGraph getPrototype() {
        return new DefaultGraph(NO_NAME);
    }

    /**
     * Constructs a new, empty Graph.
     * @ensure result.isEmpty()
     * @param name the (non-{@code null}) name of the graph.
     */
    public DefaultGraph(String name) {
        super(name);
    }

    /**
     * Constructs a clone of a given Graph.
     * @param graph the DefaultGraph to be cloned
     * @require graph != null
     * @ensure result.equals(graph)
     */
    protected DefaultGraph(DefaultGraph graph) {
        super(graph.getName());
        for (Map.Entry<DefaultNode,Set<DefaultEdge>> edgeEntry : graph.edgeMap.entrySet()) {
            this.edgeMap.put(edgeEntry.getKey(), new HashSet<DefaultEdge>(
                edgeEntry.getValue()));
        }
    }

    @Override
    public boolean containsNode(Node node) {
        return this.edgeMap.containsKey(node);
    }

    @Override
    public boolean containsEdge(Edge<?> edge) {
        Set<DefaultEdge> edgeSet = this.edgeMap.get(edge.source());
        return edgeSet != null && edgeSet.contains(edge);
    }

    public Set<? extends DefaultEdge> edgeSet() {
        Set<DefaultEdge> result = new HashSet<DefaultEdge>();
        for (Map.Entry<DefaultNode,Set<DefaultEdge>> edgeEntry : this.edgeMap.entrySet()) {
            result.addAll(edgeEntry.getValue());
        }
        return Collections.unmodifiableSet(result);
    }

    @Override
    public Set<? extends DefaultEdge> outEdgeSet(Node node) {
        return Collections.unmodifiableSet(this.edgeMap.get(node));
    }

    public Set<? extends DefaultNode> nodeSet() {
        return Collections.unmodifiableSet(this.edgeMap.keySet());
    }

    @Override
    public DefaultGraph clone() {
        DefaultGraph result = new DefaultGraph(this);
        return result;
    }

    public DefaultGraph newGraph(String name) {
        return new DefaultGraph(getName());
    }

    // ------------------------- COMMANDS ------------------------------

    @Override
    public DefaultFactory getFactory() {
        return DefaultFactory.instance();
    }

    public boolean addNode(DefaultNode node) {
        assert !isFixed() : "Trying to add " + node + " to unmodifiable graph";
        boolean added = !containsNode(node);
        if (added) {
            this.edgeMap.put(node, new HashSet<DefaultEdge>());
            fireAddNode(node);
        }
        return added;
    }

    public boolean addEdgeWithoutCheck(DefaultEdge edge) {
        assert isTypeCorrect(edge);
        assert !isFixed() : "Trying to add " + edge + " to unmodifiable graph";
        Set<DefaultEdge> sourceOutEdges = this.edgeMap.get(edge.source());
        boolean added = sourceOutEdges.add(edge);
        if (added) {
            fireAddEdge(edge);
        }
        return added;
    }

    public boolean removeEdge(DefaultEdge edge) {
        assert !isFixed() : "Trying to remove " + edge
            + " from unmodifiable graph";
        Set<DefaultEdge> outEdgeSet = this.edgeMap.get(edge.source());
        boolean removed = outEdgeSet != null && outEdgeSet.remove(edge);
        if (removed) {
            fireRemoveEdge(edge);
        }
        return removed;
    }

    /** Reimplementation to improve performance. */
    @Override
    public boolean removeNode(DefaultNode node) {
        assert !isFixed() : "Trying to remove " + node
            + " from unmodifiable graph";
        boolean result = false;
        Set<DefaultEdge> outEdges = this.edgeMap.remove(node);
        if (outEdges != null) {
            result = true;
            for (DefaultEdge outEdge : outEdges) {
                fireRemoveEdge(outEdge);
            }
            for (Set<DefaultEdge> edgeSet : this.edgeMap.values()) {
                Iterator<DefaultEdge> edgeIter = edgeSet.iterator();
                while (edgeIter.hasNext()) {
                    DefaultEdge edge = edgeIter.next();
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

    public boolean removeNodeWithoutCheck(DefaultNode node) {
        assert !isFixed() : "Trying to remove " + node
            + " from unmodifiable graph";
        boolean result = false;
        Set<DefaultEdge> outEdges = this.edgeMap.remove(node);
        if (outEdges != null) {
            result = true;
            fireRemoveNode(node);
        }
        return result;
    }

    /**
     * Returns the role of this default graph.
     * If not set explicitly, the role is {@code NONE}.
     * @see #setRole(GraphRole)
     */
    public final GraphRole getRole() {
        return this.role;
    }

    /**
     * Changes the role of this default graph.
     * This is only allowed if the graph is not yet fixed.
     * @param role the new role of the graph
     */
    public final void setRole(GraphRole role) {
        this.role = role;
    }

    /**
     * Map from the nodes of this graph to the corresponding sets of outgoing
     * edges.
     * @invariant <tt>edgeMap: DefaultNode -> 2^DefaultEdge</tt>
     */
    private final Map<DefaultNode,Set<DefaultEdge>> edgeMap =
        new HashMap<DefaultNode,Set<DefaultEdge>>();

    /** The role of this default graph. */
    private GraphRole role = NONE;
}