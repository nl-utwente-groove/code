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
package groove.graph;

import static groove.graph.GraphRole.NONE;

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
public class NodeSetEdgeSetGraph<N extends Node,E extends GEdge<N>> extends AGraph<N,E> implements
    Cloneable {
    /**
     * Creates a new, named empty graph.
     * @param name name of the new graph
     */
    public NodeSetEdgeSetGraph(String name) {
        super(name);
        this.graphNodeSet = createNodeSet();
        this.graphEdgeSet = createEdgeSet();
    }

    /**
     * Constructs a clone of a given graph.
     * @param graph the graph to be cloned
     * @require graph != null
     */
    public NodeSetEdgeSetGraph(GGraph<N,E> graph) {
        super(graph.getName());
        this.graphNodeSet = createNodeSet(graph.nodeSet());
        this.graphEdgeSet = createEdgeSet(graph.edgeSet());
        GraphInfo.transfer(graph, this, null);
    }

    // ------------------------- COMMANDS ------------------------------

    @Override
    public boolean addNode(N node) {
        boolean result;
        assert !isFixed() : "Trying to add " + node + " to unmodifiable graph";
        result = this.graphNodeSet.add(node);
        return result;
    }

    @Override
    public boolean removeEdge(E edge) {
        assert !isFixed() : "Trying to remove " + edge + " from unmodifiable graph";
        return this.graphEdgeSet.remove(edge);
    }

    // -------------------- PackageGraph methods ---------------------

    @Override
    public boolean addEdge(E edge) {
        assert isTypeCorrect(edge);
        boolean result;
        result = this.graphEdgeSet.add(edge);
        return result;
    }

    @Override
    public boolean removeNode(N node) {
        assert isTypeCorrect(node);
        boolean result;
        result = this.graphNodeSet.remove(node);
        return result;
    }

    @Override
    public boolean removeNodeSet(Collection<? extends N> nodeSet) {
        return this.graphNodeSet.removeAll(nodeSet);
    }

    // ------------- general methods (see AbstractGraph) ----------

    @Override
    public NodeSetEdgeSetGraph<N,E> clone() {
        NodeSetEdgeSetGraph<N,E> result = new NodeSetEdgeSetGraph<>(this);
        return result;
    }

    @Override
    public NodeSetEdgeSetGraph<N,E> newGraph(String name) {
        return new NodeSetEdgeSetGraph<>(getName());
    }

    @Override
    public Set<? extends E> edgeSet() {
        return Collections.unmodifiableSet(this.graphEdgeSet);
    }

    @Override
    public Set<? extends N> nodeSet() {
        return Collections.unmodifiableSet(this.graphNodeSet);
    }

    @Override
    public GraphRole getRole() {
        return NONE;
    }

    /**
     * Factory method for an empty node set of this graph.
     */
    protected Set<N> createNodeSet() {
        return new NodeNotifySet();
    }

    /**
     * Factory method for an empty edge set of this graph.
     */
    protected Set<E> createEdgeSet() {
        return new EdgeNotifySet();
    }

    /**
     * Factory method for a node set of this graph with initial elements.
     */
    protected Set<N> createNodeSet(Set<? extends N> nodeSet) {
        return new NodeNotifySet(nodeSet);
    }

    /**
     * Factory method for an edge set of this graph with initial elements.
     */
    protected Set<E> createEdgeSet(Set<? extends E> edgeSet) {
        return new EdgeNotifySet(edgeSet);
    }

    /** The set of edges of this graph. */
    protected final Set<E> graphEdgeSet;
    /** The set of nodes of this graph. */
    protected final Set<N> graphNodeSet;

    /**
     * Extension of <tt>Set</tt> that invokes the notify methods of the graph
     * when elements are added or deleted
     */
    abstract private class NotifySet<EL extends Element> extends LinkedHashSet<EL> {
        /**
         * An iterator over the underlying hash set that extends
         * <tt>remove()</tt> by invoking the graph listeners.
         */
        class NotifySetIterator implements Iterator<EL> {
            @Override
            public boolean hasNext() {
                return this.setIterator.hasNext();
            }

            @Override
            public EL next() {
                this.latest = this.setIterator.next();
                return this.latest;
            }

            @Override
            @SuppressWarnings("unchecked")
            public void remove() {
                this.setIterator.remove();
                if (this.latest instanceof Node) {
                    fireRemoveNode((N) this.latest);
                } else {
                    fireRemoveEdge((E) this.latest);
                }
            }

            private final Iterator<EL> setIterator = superIterator();
            EL latest;
        }

        /** Constructs an empty set. */
        public NotifySet() {
            // we need an explicit empty constructor
        }

        /**
         * Initializes the set <i>without</i> notification.
         */
        public NotifySet(Set<? extends EL> init) {
            for (EL elem : init) {
                super.add(elem);
            }
        }

        /**
         * Overwrites the method from <tt>Set</tt> to take care of proper
         * notification.
         */
        @Override
        public Iterator<EL> iterator() {
            return new NotifySetIterator();
        }

        /** Returns <code>super.iterator()</code>. */
        Iterator<EL> superIterator() {
            return super.iterator();
        }

        /**
         * Overwrites the method from <tt>Set</tt> to ensure proper observer
         * notification in all cases.
         * @require <tt>elem instanceof Element</tt>
         */
        @SuppressWarnings("unchecked")
        @Override
        public final boolean add(EL elem) {
            if (super.add(elem)) {
                if (elem instanceof Node) {
                    fireAddNode((N) elem);
                } else {
                    fireAddEdge((E) elem);
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public final boolean addAll(Collection<? extends EL> elemSet) {
            boolean added = false;
            for (EL elem : elemSet) {
                added |= add(elem);
            }
            return added;
        }

        /**
         * Overwrites the method from <tt>Set</tt> to ensure proper observer
         * notification in all cases.
         */
        @SuppressWarnings("unchecked")
        @Override
        public final boolean remove(Object elem) {
            if (super.remove(elem)) {
                if (elem instanceof Node) {
                    fireRemoveNode((N) elem);
                } else {
                    fireRemoveEdge((E) elem);
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
        abstract protected void fireAdd(EL elem);

        /**
         * Callback method, invoked when an element has been removed from the
         * set.
         */
        abstract protected void fireRemove(EL elem);
    }

    /**
     * Class that delegates {@link #fireAdd(Node)} to
     * {@link NodeSetEdgeSetGraph#fireAddNode(Node)} and
     * {@link #fireRemove(Node)} to
     * {@link NodeSetEdgeSetGraph#fireRemoveNode(Node)}
     */
    private class NodeNotifySet extends NotifySet<N> {
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
        public NodeNotifySet(Set<? extends N> init) {
            super(init);
        }

        /** Delegates to {@link NodeSetEdgeSetGraph#fireAddNode(Node)} . */
        @Override
        final protected void fireAdd(N elem) {
            fireAddNode(elem);
        }

        /** Delegates to {@link NodeSetEdgeSetGraph#fireRemoveNode(Node)} . */
        @Override
        final protected void fireRemove(N elem) {
            fireRemoveNode(elem);
        }

    }

    /**
     * Class that delegates {@link #fireAdd} to
     * {@link NodeSetEdgeSetGraph#fireAddEdge} and
     * {@link #fireRemove} to
     * {@link NodeSetEdgeSetGraph#fireRemoveEdge}
     */
    private class EdgeNotifySet extends NotifySet<E> {
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
        public EdgeNotifySet(Set<? extends E> init) {
            super(init);
        }

        /** Delegates to {@link NodeSetEdgeSetGraph#fireAddEdge} . */
        @Override
        final protected void fireAdd(E elem) {
            fireAddEdge(elem);
        }

        /** Delegates to {@link NodeSetEdgeSetGraph#fireRemoveEdge} . */
        @Override
        final protected void fireRemove(E elem) {
            fireRemoveEdge(elem);
        }
    }

}
