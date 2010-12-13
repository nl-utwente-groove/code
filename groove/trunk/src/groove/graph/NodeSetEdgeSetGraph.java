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
 * $Id: NodeSetEdgeSetGraph.java,v 1.10 2008-01-30 09:32:57 iovka Exp $
 */
package groove.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Graph implementation based on distinct sets of nodes and edges.
 * @author Arend Rensink
 * @version $Revision$
 */
public class NodeSetEdgeSetGraph extends AbstractGraph<GraphCache> implements
        Cloneable {
    /**
     * Constructs a prototype object of this class, to be used as a factory for
     * new (default) graphs.
     * @return a prototype <tt>GeneralGraph</tt> instance, only intended to be
     *         used for its <tt>newGraph()</tt> method.
     */
    static public Graph getPrototype() {
        return new NodeSetEdgeSetGraph();
    }

    /**
     * Creates a new, empty graph.
     */
    public NodeSetEdgeSetGraph() {
        this.graphNodeSet = createNodeSet();
        this.graphEdgeSet = createEdgeSet();
    }

    /**
     * Constructs a clone of a given graph.
     * @param graph the graph to be cloned
     * @require graph != null
     */
    public NodeSetEdgeSetGraph(Graph graph) {
        this.graphNodeSet = createNodeSet(graph.nodeSet());
        this.graphEdgeSet = createEdgeSet(graph.edgeSet());
    }

    /**
     * Constructs a clone of a given {@link NodeSetEdgeSetGraph}.
     * @param graph the graph to be cloned
     * @require graph != null
     */
    protected NodeSetEdgeSetGraph(NodeSetEdgeSetGraph graph) {
        this.graphNodeSet = createNodeSet(graph.graphNodeSet);
        this.graphEdgeSet = createEdgeSet(graph.graphEdgeSet);
    }

    // ------------------------- COMMANDS ------------------------------

    public boolean addNode(Node node) {
        boolean result;
        assert !isFixed() : "Trying to add " + node + " to unmodifiable graph";
        result = this.graphNodeSet.add(node);
        return result;
    }

    /**
     * This implementation may be relied upon to call
     * <tt>{@link #addEdgeWithoutCheck(Edge)}</tt> for the actual addition of
     * the edge.
     */
    public boolean addEdge(Edge edge) {
        assert !isFixed() : "Trying to add " + edge + " to unmodifiable graph";
        boolean added = !this.graphEdgeSet.contains(edge);
        if (added) {
            this.graphNodeSet.add(edge.source());
            this.graphNodeSet.add(edge.target());
            addEdgeWithoutCheck(edge);
        }
        return added;
    }

    /**
     * This implementation may be relied upon to call
     * <tt>{@link #removeNodeWithoutCheck(Node)}</tt> for the actual removal
     * of the node.
     */
    public boolean removeNode(Node node) {
        assert !isFixed() : "Trying to remove " + node
            + " from unmodifiable graph";
        boolean removed = this.graphNodeSet.contains(node);
        if (removed) {
            Iterator<Edge> edgeIter = this.graphEdgeSet.iterator();
            while (edgeIter.hasNext()) {
                Edge edge = edgeIter.next();
                if (edge.source().equals(node) || edge.target().equals(node)) {
                    edgeIter.remove();
                }
            }
            removeNodeWithoutCheck(node);
            // notifyGraphListenersOfRemove(node);
        }
        return removed;
    }

    public boolean removeEdge(Edge edge) {
        assert !isFixed() : "Trying to remove " + edge
            + " from unmodifiable graph";
        return this.graphEdgeSet.remove(edge);
    }

    @Override
    public boolean removeNodeSet(Collection<Node> nodeSet) {
        boolean result;
        // first remove edges that depend on a node to be removed
        Iterator<Edge> edgeIter = this.graphEdgeSet.iterator();
        while (edgeIter.hasNext()) {
            Edge other = edgeIter.next();
            if (nodeSet.contains(other.source())
                || nodeSet.contains(other.target())) {
                edgeIter.remove();
            }
        }
        // now remove the nodes
        result = removeNodeSetWithoutCheck(nodeSet);
        return result;
    }

    // -------------------- PackageGraph methods ---------------------

    public boolean addEdgeWithoutCheck(Edge edge) {
        assert isTypeCorrect(edge);
        boolean result;
        result = this.graphEdgeSet.add(edge);
        return result;
    }

    @Override
    public boolean addEdgeSetWithoutCheck(Collection<? extends Edge> edgeSet) {
        return this.graphEdgeSet.addAll(edgeSet);
    }

    public boolean removeNodeWithoutCheck(Node node) {
        assert isTypeCorrect(node);
        boolean result;
        result = this.graphNodeSet.remove(node);
        return result;
    }

    @Override
    public boolean removeNodeSetWithoutCheck(Collection<? extends Node> nodeSet) {
        return this.graphNodeSet.removeAll(nodeSet);
    }

    // ------------- general methods (see AbstractGraph) ----------

    @Override
    public Graph clone() {
        Graph result = new NodeSetEdgeSetGraph(this);
        return result;
    }

    public Graph newGraph() {
        return new NodeSetEdgeSetGraph();
    }

    public Set<? extends Edge> edgeSet() {
        return Collections.unmodifiableSet(this.graphEdgeSet);
    }

    public Set<? extends Node> nodeSet() {
        return Collections.unmodifiableSet(this.graphNodeSet);
    }

    /**
     * Factory method for an empty node set of this graph.
     */
    protected Set<Node> createNodeSet() {
        return new NodeNotifySet();
    }

    /**
     * Factory method for an empty edge set of this graph.
     */
    protected Set<Edge> createEdgeSet() {
        return new EdgeNotifySet();
    }

    /**
     * Factory method for a node set of this graph with initial elements.
     */
    protected Set<Node> createNodeSet(Set<? extends Node> nodeSet) {
        return new NodeNotifySet(nodeSet);
    }

    /**
     * Factory method for an edge set of this graph with initial elements.
     */
    protected Set<Edge> createEdgeSet(Set<? extends Edge> edgeSet) {
        return new EdgeNotifySet(edgeSet);
    }

    /** The set of edges of this graph. */
    protected final Set<Edge> graphEdgeSet;
    /** The set of nodes of this graph. */
    protected final Set<Node> graphNodeSet;

    /**
     * Extension of <tt>Set</tt> that invokes the notify methods of the graph
     * when elements are added or deleted
     */
    abstract private class NotifySet<E extends Element> extends
            LinkedHashSet<E> {
        /**
         * An iterator over the underlying hash set that extends
         * <tt>remove()</tt> by invoking the graph listeners.
         */
        class NotifySetIterator implements Iterator<E> {
            public boolean hasNext() {
                return this.setIterator.hasNext();
            }

            public E next() {
                this.latest = this.setIterator.next();
                return this.latest;
            }

            public void remove() {
                this.setIterator.remove();
                if (this.latest instanceof Node) {
                    fireRemoveNode((Node) this.latest);
                } else {
                    fireRemoveEdge((Edge) this.latest);
                }
            }

            private final Iterator<E> setIterator = superIterator();
            E latest;
        }

        /** Constructs an empty set. */
        public NotifySet() {
            // we need an explicit empty constructor
        }

        /**
         * Initializes the set <i>without</i> notification.
         */
        public NotifySet(Set<? extends E> init) {
            for (E elem : init) {
                super.add(elem);
            }
        }

        /**
         * Overwrites the method from <tt>Set</tt> to take care of proper
         * notification.
         */
        @Override
        public Iterator<E> iterator() {
            return new NotifySetIterator();
        }

        /** Returns <code>super.iterator()</code>. */
        Iterator<E> superIterator() {
            return super.iterator();
        }

        /**
         * Overwrites the method from <tt>Set</tt> to ensure proper observer
         * notification in all cases.
         * @require <tt>elem instanceof Element</tt>
         */
        @Override
        public final boolean add(E elem) {
            if (super.add(elem)) {
                if (elem instanceof Node) {
                    fireAddNode((Node) elem);
                } else {
                    fireAddEdge((Edge) elem);
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public final boolean addAll(Collection<? extends E> elemSet) {
            boolean added = false;
            for (E elem : elemSet) {
                added |= add(elem);
            }
            return added;
        }

        /**
         * Overwrites the method from <tt>Set</tt> to ensure proper observer
         * notification in all cases.
         */
        @Override
        public final boolean remove(Object elem) {
            if (super.remove(elem)) {
                if (elem instanceof Node) {
                    fireRemoveNode((Node) elem);
                } else {
                    fireRemoveEdge((Edge) elem);
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public final boolean removeAll(Collection<?> elemSet) {
            boolean removed = false;
            for (Object elem : elemSet) {
                removed |= remove(elem);
            }
            return removed;
        }

        /** Callback method, invoked when an element has been added to the set. */
        abstract protected void fireAdd(E elem);

        /**
         * Callback method, invoked when an element has been removed from the
         * set.
         */
        abstract protected void fireRemove(E elem);
    }

    /**
     * Class that delegates {@link #fireAdd(Node)} to
     * {@link NodeSetEdgeSetGraph#fireAddNode(Node)} and
     * {@link #fireRemove(Node)} to
     * {@link NodeSetEdgeSetGraph#fireRemoveNode(Node)}
     */
    private class NodeNotifySet extends NotifySet<Node> {
        /**
         * Constructs an empty set.
         */
        public NodeNotifySet() {
            super();
        }

        /**
         * Constructs a set initialised with a given set of elements, without
         * firing the notification.
         */
        public NodeNotifySet(Set<? extends Node> init) {
            super(init);
        }

        /** Delegates to {@link NodeSetEdgeSetGraph#fireAddNode(Node)} . */
        @Override
        final protected void fireAdd(Node elem) {
            fireAddNode(elem);
        }

        /** Delegates to {@link NodeSetEdgeSetGraph#fireRemoveNode(Node)} . */
        @Override
        final protected void fireRemove(Node elem) {
            fireRemoveNode(elem);
        }

    }

    /**
     * Class that delegates {@link #fireAdd(Edge)} to
     * {@link NodeSetEdgeSetGraph#fireAddNode(Node)} and
     * {@link #fireRemove(Edge)} to
     * {@link NodeSetEdgeSetGraph#fireRemoveNode(Node)}
     */
    private class EdgeNotifySet extends NotifySet<Edge> {
        /**
         * Constructs an empty set.
         */
        public EdgeNotifySet() {
            super();
        }

        /**
         * Constructs a set initialised with a given set of elements, without
         * firing the notification.
         */
        public EdgeNotifySet(Set<? extends Edge> init) {
            super(init);
        }

        /** Delegates to {@link NodeSetEdgeSetGraph#fireAddEdge(Edge)} . */
        @Override
        final protected void fireAdd(Edge elem) {
            fireAddEdge(elem);
        }

        /** Delegates to {@link NodeSetEdgeSetGraph#fireRemoveEdge(Edge)} . */
        @Override
        final protected void fireRemove(Edge elem) {
            fireRemoveEdge(elem);
        }
    }
}
