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
 * $Id: NodeSetEdgeSetGraph.java,v 1.1.1.2 2007-03-20 10:42:42 kastenberg Exp $
 */
package groove.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Graph implementation based on distinct sets of nodes and edges.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class NodeSetEdgeSetGraph
    extends AbstractGraph
    implements InternalGraph {
    /**
     * Extension of <tt>Set</tt> that invokes the notify methods of the graph
     * when elements are added or deleted
     */
    protected class NotifySet<E extends Element> extends HashSet<E> {
        /**
         * An iterator over the underlying hash set that extends <tt>remove()</tt>
         * by invoking the graph listeners.
         */
        class NotifySetIterator implements Iterator<E> {
            public boolean hasNext() {
                return setIterator.hasNext();
            }
            
            public E next() {
                latest = setIterator.next();
                return latest;
            }
            
            public void remove() {
                setIterator.remove();
                if (latest instanceof Node) {
                	fireRemoveNode((Node) latest);
                } else {
                	fireRemoveEdge((Edge) latest);
                }
            }
            
            private final Iterator<E> setIterator = NotifySet.super.iterator();
            E latest;
        }
        
        public NotifySet() {
        	// we need an explicit empty constructor
        }
        
        /**
         * Initializes the set <i>without</i> notification.
         */
        public NotifySet(Set<? extends E> init) {
        	for (E elem: init) {
                super.add(elem);
            }
        }
        
        /**
         * Overwrites the method from <tt>Set</tt> to take care
         * of proper notification.
         */
        public Iterator<E> iterator() {
            return new NotifySetIterator();
        }
        
        /**
         * Overwrites the method from <tt>Set</tt> to ensure
         * proper observer notification in all cases. 
         * @require <tt>elem instanceof Element</tt>
         */
        public final boolean add(E elem) {
            if (super.add(elem)) {
            	if (elem instanceof Node) {
            		fireAddNode((Node) elem);
            	} else {
            		fireAddEdge((Edge) elem);
            	}
                return true;
            } else
                return false;
        }

        public final boolean addAll(Collection<? extends E> elemSet) {
            boolean added = false;
            for (E elem: elemSet) {
                added |= add(elem);
            }
            return added;
        }
        
        /**
         * Overwrites the method from <tt>Set</tt> to ensure
         * proper observer notification in all cases. 
         * @require <tt>elem instanceof Element</tt>
         */
        public final boolean remove(Object elem) {
            if (super.remove(elem)) {
            	if (elem instanceof Node) {
            		fireRemoveNode((Node) elem);
            	} else {
            		fireRemoveEdge((Edge) elem);
            	}
                return true;
            } else
                return false;
        }

        public final boolean removeAll(Collection<?> elemSet) {
            boolean removed = false;
            for (Object elem: elemSet) {
                removed |= remove(elem);
            }
            return removed;
        }
    }

    /**
     * Constructs a protytpe object of this class, to be used as a factory
     * for new (default) graphs.
     * @return a prototype <tt>GeneralGraph</tt> instance, only intended to
     * be used for its <tt>newGraph()</tt> method.
     */
    static public Graph getPrototype() {
        return new NodeSetEdgeSetGraph();
    }

    /**
     * Creates a new, empty graph.
     */
    public NodeSetEdgeSetGraph() {
        graphNodeSet = createNodeSet();
        graphEdgeSet = createEdgeSet();
    }

    /** 
     * Constructs a clone of a given graph.
     * @param graph the graph to be cloned
     * @require graph != null
     */
    public NodeSetEdgeSetGraph(Graph graph) {
        graphNodeSet = createNodeSet(graph.nodeSet());
        graphEdgeSet = createEdgeSet(graph.edgeSet());
    }

    /** 
     * Constructs a clone of a given {@link NodeSetEdgeSetGraph}.
     * @param graph the graph to be cloned
     * @require graph != null
     */
    protected NodeSetEdgeSetGraph(NodeSetEdgeSetGraph graph) {
        graphNodeSet = createNodeSet(graph.graphNodeSet);
        graphEdgeSet = createEdgeSet(graph.graphEdgeSet);
    }

    // ------------------------- COMMANDS ------------------------------

    public boolean addNode(Node node) {
    	boolean result;
        reporter.start(ADD_NODE);
		assert !isFixed() : "Trying to add " + node + " to unmodifiable graph";
		result = graphNodeSet.add(node);
		assert nodeCount() == new NodeSet(nodeSet()).size() : String.format("Overlapping node number for %s in %s",
				node,
				nodeSet());
		reporter.stop();
		return result;
	}

    /**
	 * This implementation may be relied upon to call
	 * <tt>{@link #addEdgeWithoutCheck(Edge)}</tt> for the actual addition of
	 * the edge.
	 */
    public boolean addEdge(Edge edge) {
        reporter.start(ADD_EDGE);
        assert !isFixed() : "Trying to add " + edge + " to unmodifiable graph";
        boolean added = !graphEdgeSet.contains(edge);
        if (added) {
            Node[] dependentNodes = edge.ends();
            for (int i = 0; i < dependentNodes.length; i++) {
                graphNodeSet.add(dependentNodes[i]);
            }
            addEdgeWithoutCheck(edge);
        }
        reporter.stop();
        return added;
    }

    /**
     * This implementation may be relied upon to call <tt>{@link #removeNodeWithoutCheck(Node)}</tt> for
     * the actual removal of the node.
     */
    public boolean removeNode(Node node) {
        reporter.start(REMOVE_NODE);
        assert !isFixed() : "Trying to remove "
            + node
            + " from unmodifiable graph";
        boolean removed = graphNodeSet.contains(node);
        if (removed) {
            Iterator<Edge> edgeIter = graphEdgeSet.iterator();
            while (edgeIter.hasNext()) {
                Edge edge = edgeIter.next();
                if (edge.hasEnd(node)) {
                    edgeIter.remove();
                }
            }
            removeNodeWithoutCheck(node);
            //            notifyGraphListenersOfRemove(node);
        }
        reporter.stop();
        return removed;
    }

    public boolean removeEdge(Edge edge) {
        reporter.start(REMOVE_EDGE);
        try {
            assert !isFixed() : "Trying to remove "
                + edge
                + " from unmodifiable graph";
            return graphEdgeSet.remove(edge);
        } finally {
            //        if (removed)
            //            notifyGraphListenersOfRemove(edge);
            reporter.stop();
        }
    }

    public boolean removeNodeSet(Collection<Node> nodeSet) {
        reporter.start(REMOVE_NODE);
        try {
            // first remove edges that depend on a node to be removed
            Iterator<Edge> edgeIter = graphEdgeSet.iterator();
            while (edgeIter.hasNext()) {
                Edge other = edgeIter.next();
                boolean otherRemoved = false;
                Node[] parts = other.ends();
                for (int i = 0; !otherRemoved && i < parts.length; i++) {
                    if (nodeSet.contains(parts[i])) {
                        edgeIter.remove();
                        otherRemoved = true;
                    }
                }
            }
            // now remove the nodes
            return removeNodeSetWithoutCheck(nodeSet);
        } finally {
            reporter.stop();
        }
    }

    // -------------------- PackageGraph methods ---------------------

    public boolean addEdgeWithoutCheck(Edge edge) {
        reporter.start(ADD_EDGE);
        try {
            return graphEdgeSet.add(edge);
            //        if (added)
            //            notifyGraphListenersOfAdd((Edge) edge);
        } finally {
            reporter.stop();
        }
    }

    public boolean addEdgeSetWithoutCheck(Collection<Edge> edgeSet) {
        return graphEdgeSet.addAll(edgeSet);
    }

    public boolean removeNodeWithoutCheck(Node node) {
        reporter.start(REMOVE_NODE);
        try {
            return graphNodeSet.remove(node);
        } finally {
            reporter.stop();
        }
    }

    public boolean removeNodeSetWithoutCheck(Collection<Node> nodeSet) {
        return graphNodeSet.removeAll(nodeSet);
    }

    // ------------- general methods (see AbstractGraph) ----------

    public Graph clone() {
        reporter.start(CLONE);
        Graph result = new NodeSetEdgeSetGraph(this);
        reporter.stop();
        return result;
    }

    public Graph newGraph() {
        return new NodeSetEdgeSetGraph();
    }

    public Set<? extends Edge> edgeSet() {
        return Collections.unmodifiableSet(graphEdgeSet);
    }

    public Set<? extends Node> nodeSet() {
        return Collections.unmodifiableSet(graphNodeSet);
    }

    /**
     * Factory method for an empty node set of this graph.
     */
    protected Set<Node> createNodeSet() {
        return new NotifySet<Node>();
    }

    /**
     * Factory method for an empty edge set of this graph.
     */
    protected Set<Edge> createEdgeSet() {
        return new NotifySet<Edge>();
    }

    /**
     * Factory method for a node set of this graph with initial elements.
     */
    protected Set<Node> createNodeSet(Set<? extends Node> nodeSet) {
        return new NotifySet<Node>(nodeSet);
    }

    /**
     * Factory method for an edge set of this graph with initial elements.
     */
    protected Set<Edge> createEdgeSet(Set<? extends Edge> edgeSet) {
        return new NotifySet<Edge>(edgeSet);
    }

    /** The set of edges of this graph. */
    protected final Set<Edge> graphEdgeSet;
    /** The set of nodes of this graph. */
    protected final Set<Node> graphNodeSet;
}
