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
 * $Id: AbstractGraphShape.java,v 1.15 2008-01-21 12:57:10 rensink Exp $
 */

package groove.graph;

import groove.rel.RelationEdge;
import groove.util.AbstractCacheHolder;
import groove.util.Groove;
import groove.util.SetView;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Partial implementation of a graph. Records a set of <tt>GraphListener</tt>s.
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class AbstractGraphShape<C extends GraphShapeCache> extends
        AbstractCacheHolder<C> implements GraphShape {
    /**
     * This constructor polls the cache reference queue and calls
     * {@link Reference#clear()} on all encountered references.
     */
    protected AbstractGraphShape() {
        super(null);
        modifiableGraphCount++;
    }

    public int nodeCount() {
        return nodeSet().size();
    }

    public int edgeCount() {
        return edgeSet().size();
    }

    /**
     * Implements the method by distinguishing between nodes and edges, and
     * deferring the containment question to <tt>nodeSet()</tt> respectively
     * <tt>edgeSet()</tt>
     */
    public boolean containsElement(Element elem) {
        if (elem instanceof Node) {
            return nodeSet().contains(elem);
        } else if (elem instanceof RelationEdge) {
            return nodeSet().contains(((Edge) elem).source())
                && nodeSet().contains(((Edge) elem).target());
        } else {
            return edgeSet().contains(elem);
        }
    }

    public boolean containsElementSet(Collection<? extends Element> elements) {
        boolean result = true;
        Iterator<? extends Element> elemIter = elements.iterator();
        while (result && elemIter.hasNext()) {
            result &= containsElement(elemIter.next());
        }
        return result;
    }

    public int size() {
        return nodeCount() + edgeCount();
    }

    public boolean isEmpty() {
        return nodeCount() == 0;
    }

    /**
     * This implementation retrieves the node-to-edges mapping from the cache,
     * and looks up the required set in the image for <tt>node</tt>.
     */
    public Set<? extends Edge> edgeSet(Node node) {
        Set<? extends Edge> result = nodeEdgeMap().get(node);
        if (result == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(result);
        }
    }

    /**
     * This implementation returns a set view on the incident edge set,
     * selecting just those edges of which the source equals {@code node}.
     */
    public Set<? extends Edge> outEdgeSet(final Node node) {
        return new SetView<Edge>(edgeSet(node)) {
            @Override
            public boolean approves(Object obj) {
                return obj instanceof Edge
                    && ((Edge) obj).source().equals(node);
            }
        };
    }

    /**
     * This implementation returns a set view on the incident edge set,
     * selecting just those edges of which the target equals {@code node}.
     */
    public Set<? extends Edge> inEdgeSet(final Node node) {
        return new SetView<Edge>(edgeSet(node)) {
            @Override
            public boolean approves(Object obj) {
                return obj instanceof Edge
                    && ((Edge) obj).target().equals(node);
            }
        };
    }

    /**
     * Returns a mapping from nodes to sets of edges of this graph.
     */
    protected Map<Node,? extends Set<? extends Edge>> nodeEdgeMap() {
        return Collections.unmodifiableMap(getCache().getNodeEdgeMap());
    }

    public Set<? extends Edge> labelEdgeSet(Label label) {
        Set<? extends Edge> result = getLabelEdgeMap().get(label);
        if (result != null) {
            return Collections.unmodifiableSet(result);
        } else {
            return Collections.emptySet();
        }
    }

    /**
     * Returns the array of label-to-edge maps from the graph cache.
     * @return the array of label-to-edge maps from the graph cache
     */
    protected Map<Label,? extends Set<? extends Edge>> getLabelEdgeMap() {
        return getCache().getLabelEdgeMap();
    }

    public GraphInfo getInfo() {
        return this.graphInfo;
    }

    /**
     * Callback factory method for a graph information object.
     * @param info the {@link groove.graph.GraphInfo} to create a fresh instance
     *        of
     * @return a fresh instance of {@link groove.graph.GraphInfo} based on
     *         <code>info</code>
     */
    protected GraphInfo createInfo(GraphInfo info) {
        return new GraphInfo(info);
    }

    public GraphInfo setInfo(GraphInfo info) {
        return this.graphInfo = (info == null ? null : createInfo(info));
    }

    public boolean isFixed() {
        return this.listeners == null;
    }

    public void setFixed() {
        if (!isFixed()) {
            registerFixed();
            this.listeners = null;
            if (GATHER_STATISTICS) {
                modifiableGraphCount--;
            }
        }
    }

    /**
     * Callback method to register that the graph should be set to fixed. Called
     * from {@link #setFixed()} if the graph is actually currently not fixed.
     */
    protected void registerFixed() {
        setCacheCollectable();
    }

    @Override
    public void testFixed(boolean fixed) throws IllegalStateException {
        if (isFixed() != fixed) {
            throw new IllegalStateException(String.format(
                "Expected graph to be %s", fixed ? "fixed" : "unfixed"));
        }
    }

    /** Calls {@link #toString(GraphShape)}. */
    @Override
    public String toString() {
        return toString(this);
    }

    // -------------------- Graph listener methods ---------------------------

    /**
     * Returns an iterator over the graph listeners of this graph.
     * @return an iterator over the graph listeners of this graph
     * @ensure result \subseteq GraphListener
     */
    public Iterator<GraphShapeListener> getGraphListeners() {
        if (isFixed()) {
            return Collections.<GraphShapeListener>emptySet().iterator();
        } else {
            return this.listeners.keySet().iterator();
        }
    }

    /**
     * Adds a graph listener to this graph.
     */
    public synchronized void addGraphListener(GraphShapeListener listener) {
        if (this.listeners != null) {
            this.listeners.put(listener, null);
        }
    }

    /**
     * Removes a graph listener from this graph.
     */
    public synchronized void removeGraphListener(GraphShapeListener listener) {
        if (!isFixed()) {
            this.listeners.remove(listener);
        }
    }

    /**
     * Calls {@link GraphShapeListener#addUpdate(GraphShape, Node)} on all
     * GraphListeners in listeners.
     * @param node the node being added
     */
    protected void fireAddNode(Node node) {
        Iterator<GraphShapeListener> iter = getGraphListeners();
        while (iter.hasNext()) {
            iter.next().addUpdate(this, node);
        }
    }

    /**
     * Calls {@link GraphShapeListener#addUpdate(GraphShape, Edge)} on all
     * GraphListeners in listeners.
     * @param edge the edge being added
     */
    protected void fireAddEdge(Edge edge) {
        Iterator<GraphShapeListener> iter = getGraphListeners();
        while (iter.hasNext()) {
            iter.next().addUpdate(this, edge);
        }
    }

    /**
     * Calls {@link GraphShapeListener#removeUpdate(GraphShape, Node)} on all
     * GraphListeners in listeners.
     * @param node the node being removed
     */
    protected void fireRemoveNode(Node node) {
        Iterator<GraphShapeListener> iter = getGraphListeners();
        while (iter.hasNext()) {
            GraphShapeListener listener = iter.next();
            listener.removeUpdate(this, node);
        }
    }

    /**
     * Calls {@link GraphShapeListener#removeUpdate(GraphShape, Edge)} on all
     * GraphListeners in listeners.
     * @param edge the edge being removed
     */
    protected void fireRemoveEdge(Edge edge) {
        Iterator<GraphShapeListener> iter = getGraphListeners();
        while (iter.hasNext()) {
            GraphShapeListener listener = iter.next();
            listener.removeUpdate(this, edge);
        }
    }

    /**
     * Factory method for a graph cache. This implementation returns a
     * {@link GraphCache}.
     * @return the graph cache
     */
    @Override
    @SuppressWarnings("unchecked")
    protected C createCache() {
        return (C) new GraphShapeCache(this);
    }

    /**
     * Set of {@link GraphListener} s to be identified of changes in this graph.
     * Set to <tt>null</tt> when the graph is fixed.
     */
    protected Map<GraphShapeListener,Object> listeners =
        new HashMap<GraphShapeListener,Object>();

    /**
     * Returns the number of graphs created and never fixed.
     * @return the number of graphs created and never fixed
     */
    static public int getModifiableGraphCount() {
        return modifiableGraphCount;
    }

    /**
     * Provides a textual description of a given graph. Lists the nodes and
     * their outgoing edges.
     * @param graph the graph to be described
     * @return a textual description of <tt>graph</tt>
     */
    public static String toString(GraphShape graph) {
        StringBuffer result = new StringBuffer();
        result.append(graph.getInfo());
        result.append(String.format("Nodes: %s%n", graph.nodeSet()));
        result.append(String.format("Edges: %s%n", graph.edgeSet()));
        return "Nodes: " + graph.nodeSet() + "; Edges: " + graph.edgeSet();
    }

    /**
     * Map in which varies kinds of data can be stored.
     */
    private GraphInfo graphInfo;
    /**
     * Private copy of the static variable to allow compiler optimization.
     */
    static private final boolean GATHER_STATISTICS = Groove.GATHER_STATISTICS;

    /**
     * Counts the number of graphs that were not fixed. Added for debugging
     * purposes: observers of modifiable graphs may cause memory leaks.
     */
    static private int modifiableGraphCount = 0;
}