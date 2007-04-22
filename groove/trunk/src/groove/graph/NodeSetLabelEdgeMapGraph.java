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
 * $Id: NodeSetLabelEdgeMapGraph.java,v 1.4 2007-04-22 23:32:22 rensink Exp $
 */
package groove.graph;

import groove.util.CollectionOfCollections;
import groove.util.SetOfDisjointSets;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Graph implementation based on a set of nodes and a map from labels to edges.
 * implementations
 * @author Arend Rensink
 * @version $Revision: 1.4 $
 */
public class NodeSetLabelEdgeMapGraph extends AbstractGraph<GraphCache> {
    /**
     * Constructs a protytpe object of this class, to be used as a factory
     * for new (default) graphs.
     * @return a prototype <tt>GeneralGraph</tt> instance, only intended to
     * be used for its <tt>newGraph()</tt> method.
     */
    static Graph getPrototype() {
        return new NodeSetLabelEdgeMapGraph();
    }

    /**
     * Creates a new, empty graph.
     */
    public NodeSetLabelEdgeMapGraph() {
    	// we need an explicit empty constructor
    }

    /** 
     * Constructs a clone of a given Graph.
     * @param graph the DefaultGraph to be cloned
     * @require graph != null
     * @ensure result.equals(graph)
     */
    protected NodeSetLabelEdgeMapGraph(NodeSetLabelEdgeMapGraph graph) {
        for (Map.Entry<Label,Set<Edge>> edgeMapEntry: graph.labelEdgeMap.entrySet()) {
            labelEdgeMap.put(edgeMapEntry.getKey(), new HashSet<Edge>(edgeMapEntry.getValue()));
        }
        nodeSet.addAll(graph.nodeSet);
    }

    @Override
    public boolean containsElement(Element elem) {
        reporter.start(CONTAINS_ELEMENT);
        try {
            if (elem instanceof Node)
                return nodeSet.contains(elem);
            else {
                assert elem instanceof Edge;
                Set<Edge> elemSet = labelEdgeMap.get(((Edge) elem).label());
                return elemSet != null && elemSet.contains(elem);
            }
        } finally {
            reporter.stop();
        }
    }

    // ------------------------- COMMANDS ------------------------------

    public boolean addNode(Node node) {
        reporter.start(ADD_NODE);
        assert !isFixed() : "Trying to add " + node + " to unmodifiable graph";
        boolean added = nodeSet.add(node);
        if (added) {
            assert nodeCount() == new HashSet<Node>(nodeSet()).size() : String.format("Overlapping node number for %s in %s", node, nodeSet());
            fireAddNode(node);
        }
        reporter.stop();
        return added;
    }

    public boolean addEdge(Edge edge) {
        reporter.start(ADD_EDGE);
        assert !isFixed() : "Trying to add " + edge + " to unmodifiable graph";
        Set<Edge> edgeSet = labelEdgeMap.get(edge.label());
        if (edgeSet == null) {
            labelEdgeMap.put(edge.label(), edgeSet = new HashSet<Edge>());
        }
        boolean added = edgeSet.add(edge);
        if (added) {
            Node[] elemParts = edge.ends();
            for (int i = 0; i < elemParts.length; i++) {
                if (nodeSet.add(elemParts[i]))
                    fireAddNode(elemParts[i]);
            }
            fireAddEdge(edge);
        }
        reporter.stop();
        return added;
    }

    public boolean removeNode(Node node) {
        reporter.start(REMOVE_NODE);
        assert !isFixed() : "Trying to remove " + node + " from unmodifiable graph";
        boolean removed = nodeSet.remove(node);
        if (removed) {
            Iterator<Edge> edgeIter = new CollectionOfCollections<Edge>(labelEdgeMap.values()).iterator();
            while (edgeIter.hasNext()) {
                Edge edge = edgeIter.next();
                if (edge.hasEnd(node)) {
                    edgeIter.remove();
                    fireRemoveEdge(edge);
                }
            }
            fireRemoveNode(node);
        }
        reporter.stop();
        return removed;
    }

    public boolean removeEdge(Edge edge) {
        reporter.start(REMOVE_EDGE);
        Set<Edge> edgeSet = labelEdgeMap.get(edge.label());
        boolean removed = edgeSet != null && edgeSet.remove(edge);
        if (removed)
            fireRemoveEdge(edge);
        reporter.stop();
        return removed;
    }

    @Override
    public boolean removeNodeSet(Collection<Node> nodeSet) {
        reporter.start(REMOVE_NODE);
        boolean removed = false;
        Iterator<Edge> edgeIter = new CollectionOfCollections<Edge>(labelEdgeMap.values()).iterator();
        while (edgeIter.hasNext()) {
            Edge edge = edgeIter.next();
            boolean otherRemoved = false;
            Node[] parts = edge.ends();
            for (int i = 0; !otherRemoved && i < parts.length; i++) {
                if (nodeSet.contains(parts[i])) {
                    edgeIter.remove();
                    fireRemoveEdge(edge);
                    removed = otherRemoved = true;
                }
            }
        }
        for (Node node: nodeSet) {
            boolean nodeRemoved = this.nodeSet.remove(node);
            if (nodeRemoved) {
                fireRemoveNode(node);
                removed = true;
            }
        }
        reporter.stop();
        return removed;
    }

    // -------------------- PackageGraph methods ---------------------

    public boolean addEdgeWithoutCheck(Edge edge) {
        reporter.start(ADD_EDGE);
        Label label = edge.label();
        Set<Edge> edgeSet = labelEdgeMap.get(label);
        if (edgeSet == null) {
            labelEdgeMap.put(label, edgeSet = new HashSet<Edge>());
        }
        boolean added = edgeSet.add(edge);
        if (added)
            fireAddEdge(edge);
        reporter.stop();
        return added;
    }

    public boolean removeNodeWithoutCheck(Node node) {
        reporter.start(REMOVE_NODE);
        boolean removed = nodeSet.remove(node);
        if (removed)
            fireRemoveNode(node);
        reporter.stop();
        return removed;
    }

    // ------------- general methods (see AbstractGraph) ----------

    @Override
    public Graph clone() {
        reporter.start(CLONE);
        Graph result = new NodeSetLabelEdgeMapGraph(this);
        reporter.stop();
        return result;
    }

    public Graph newGraph() {
        return new NodeSetLabelEdgeMapGraph();
    }

    public Set<? extends Edge> edgeSet() {
        return new SetOfDisjointSets<Edge>(labelEdgeMap.values());
    }

    public Set<? extends Node> nodeSet() {
        return Collections.unmodifiableSet(nodeSet);
    }

    /** Map from labels to sets of edges with that label. */
    protected final Map<Label,Set<Edge>> labelEdgeMap = new HashMap<Label,Set<Edge>>();
    /** Set of nodes of this graph. */
    protected final Set<Node> nodeSet = new HashSet<Node>();
}
