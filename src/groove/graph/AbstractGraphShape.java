/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id: AbstractGraphShape.java,v 1.2 2007-03-30 15:50:24 rensink Exp $
 */

package groove.graph;

import groove.rel.RelationEdge;
import groove.util.CollectionOfCollections;
import groove.util.Groove;
import groove.util.Reporter;
import groove.util.UnmodifiableCollectionView;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Partial implementation of a graph. Records a set of <tt>GraphListener</tt>s.
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public abstract class AbstractGraphShape implements GraphShape {
    /**
     * Private copy of the static variable to allow compiler optimization.
     */
    static private final boolean GATHER_STATISTICS = Groove.GATHER_STATISTICS;

    /**
     * Counts the number of graphs that were not fixed. Added for debugging purposes: observers of
     * modifiable graphs may cause memory leaks.
     */
    static private int modifiableGraphCount = 0;

    /**
     * Number of effective invocations of {@link #clearCache()}.
     */
    static private int cacheClearCount;

    /**
     * Number of cache clearances counted in the {@link #cacheReferenceQueue}.
     */
    static private int cacheCollectCount;

    /**
     * A global empty soft reference, used to save space when clearing the cache explicitly.
     */
    static public final Reference<? extends GraphShapeCache> NULL_REFERENCE = new SoftReference<GraphShapeCache>(null);

    /**
     * Returns the number of graphs created and never fixed. 
     * @return the number of graphs created and never fixed
     */
    static public int getModifiableGraphCount() {
        return modifiableGraphCount;
    }

    /**
     * Returns the number of times a cache was cleared explicitly.
     * @return the number of times a cache was cleared explicitly
     */
    static public int getCacheClearCount() {
        return cacheClearCount;
    }

    /**
     * Returns the number of times a cache was collected by the garbage collector.
     * @return the number of times a cache was collected by the garbage collector
     */
    static public int getCacheCollectCount() {
        return cacheCollectCount;
    }

    /**
     * Returns the total number of caches created.
     * @return the total number of caches created
     */
    static public int getCacheCreateCount() {
        return GraphCache.getCreateCount();
    }

    /**
     * Provides a textual description of a given graph. Lists the nodes and their outgoing edges.
     * @param graph the graph to be described
     * @return a textual description of <tt>graph</tt>
     */
    public static String toString(GraphShape graph) {
        return "Nodes: " + graph.nodeSet() + "; Edges: " + graph.edgeSet();
    }

    /**
     * This constructor polls the cache reference queue and calls 
     * {@link Reference#clear()} on all encountered references.
     */
    protected AbstractGraphShape() {
        modifiableGraphCount++;
        cacheReference = createNullReference();
    }

    @Deprecated
    public Iterator<? extends Node> nodeIterator() {
        return nodeSet().iterator();
    }

    public int nodeCount() {
        return nodeSet().size();
    }

    @Deprecated
    public Iterator<? extends Edge> edgeIterator() {
        return edgeSet().iterator();
    }

    public int edgeCount() {
        return edgeSet().size();
    }
    
    /**
     * Implements the method by distinguishing between nodes and edges, and deferring the
     * containement question to <tt>nodeSet()</tt> respectively <tt>edgeSet()</tt>
     */
    public boolean containsElement(Element elem) {
        if (elem instanceof Node) {
            return nodeSet().contains(elem);
        } else if (elem instanceof RelationEdge) {
            return nodeSet().containsAll(Arrays.asList(((RelationEdge) elem).ends()));
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

    public Collection<? extends Element> elementSet() {
        Set<Collection<? extends Element>> nodeSetEdgeSet = new HashSet<Collection<? extends Element>>();
        nodeSetEdgeSet.add(nodeSet());
        nodeSetEdgeSet.add(edgeSet());
        return new CollectionOfCollections<Element>(nodeSetEdgeSet);
    }

    @Deprecated
    public Iterator<? extends Element> iterator() {
        return elementSet().iterator();
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
    public Collection<? extends Edge> edgeSet(Node node) {
        Collection<Edge> result = getCache().getNodeEdgeMap().get(node);
        if (result == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableCollection(result);
        }
    }
    
    /**
     * This implementation returns a set view on the incident edge set,
     * selecting just those edges of which <tt>end(i).equals(node)</tt>.
     */
    public Collection<? extends Edge> edgeSet(final Node node, final int i) {
        return new UnmodifiableCollectionView<Edge>(edgeSet(node)) {
        	@Override
            public boolean approves(Object obj) {
                return ((Edge) obj).end(i).equals(node);
            }
        };
    }
    
    public Collection<? extends Edge> outEdgeSet(Node node) {
        return edgeSet(node, Edge.SOURCE_INDEX);
    }
    
    /**
     * Returns a mapping from nodes to sets of edges of this graph.
     */
    public Map<Node, Set<Edge>> nodeEdgeMap() {
        return Collections.unmodifiableMap(getCache().getNodeEdgeMap());
    }

    public Set<? extends Edge> labelEdgeSet(int arity, Label label) {
        Set<? extends Edge> result = labelEdgeMap(arity).get(label);
        if (result != null) {
            return Collections.unmodifiableSet(result);
        } else {
            return Collections.emptySet();
        }
    }

    public Map<Label, ? extends Set<? extends Edge>> labelEdgeMap(int i) {
        return Collections.unmodifiableMap(getLabelEdgeMaps()[i]);
    }

    /**
     * Returns the array of label-to-edge maps from the graph cache.
     * @return the array of label-to-edge maps from the graph cache
     */
    protected Map<Label, Set<Edge>>[] getLabelEdgeMaps() {
        return getCache().getLabelEdgeMaps();
    }
    
    /**
     * Computes an array containing mappings from a label to the set of edges
     * with that label, indexed by the arity of the edges.
     * @return the computed mapping
     */
    protected Map<Label, Set<Edge>>[] computeArityLabelEdgeMap() {
        Map<Label, Set<Edge>>[] result = new Map[AbstractEdge.getMaxEndCount()];
        for (int arity = 0; arity < result.length; arity++) {
            result[arity] = new HashMap<Label, Set<Edge>>();
        }
        for (Edge edge: edgeSet()) {
            Map<Label, Set<Edge>> labelEdgeMap = result[edge.endCount() - 1];
            Set<Edge> labelEdgeSet = labelEdgeMap.get(edge.label());
            if (labelEdgeSet == null) {
                labelEdgeSet = new HashSet<Edge>();
                labelEdgeMap.put(edge.label(), labelEdgeSet);
            }
            labelEdgeSet.add(edge);
        }
        return result;
    }

    /** 
     * Computes and returns a mapping from nodes to sets of outgoing edges for that node.
     * The map returns <tt>null</tt> for nodes without outgoing edges.
     * @return the computed mapping
     */
    protected Map<Node, Set<Edge>> computeOutEdgeMap() {
        Map<Node,Set<Edge>> result = new HashMap<Node,Set<Edge>>();
        for (Edge edge: edgeSet()) {
            Node source = edge.source();
            Set<Edge> outEdgeSet = result.get(source);
            if (outEdgeSet == null) {
                result.put(source, outEdgeSet = new HashSet<Edge>());
            }
            outEdgeSet.add(edge);
        }
        return result;
    }

    public GraphInfo getInfo() {
        return graphInfo;
    }

    /** Callback factory method for a graph information object. 
     * @param info the {@link groove.graph.GraphInfo} to create a fresh instance of
     * @return a fresh instance of {@link groove.graph.GraphInfo} based on <code>info</code>
     */
    protected GraphInfo createInfo(GraphInfo info) {
        return new GraphInfo(info);
    }

    public GraphInfo setInfo(GraphInfo info) {
        return graphInfo = (info == null ? null : createInfo(info));
    }

    public boolean isFixed() {
        return listeners == null;
    }
    public void setFixed() {
        if (!isFixed()) {
            listeners = null;
            if (GATHER_STATISTICS) {
                modifiableGraphCount--;
            }
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
            return listeners.keySet().iterator();
        }
    }

    /**
     * Adds a graph listener to this graph.
     */
    public synchronized void addGraphListener(GraphShapeListener listener) {
        if (!isFixed()) {
            listeners.put(listener,null);
        }
    }

    /**
     * Removes a graph listener from this graph.
     */
    public synchronized void removeGraphListener(GraphShapeListener listener) {
        if (!isFixed()) {
            listeners.remove(listener);
        }
    }

    /**
     * Calls {@link GraphShapeListener#addUpdate(GraphShape, Node)} on all GraphListeners in listeners.
     * @param node the node being added
     */
    protected void fireAddNode(Node node) {
        Iterator<GraphShapeListener> iter = getGraphListeners();
        while (iter.hasNext()) {
            iter.next().addUpdate(this, node);
        }
    }

    /**
     * Calls {@link GraphShapeListener#addUpdate(GraphShape, Edge)} on all GraphListeners in listeners.
     * @param edge the edge being added
     */
    protected void fireAddEdge(Edge edge) {
        Iterator<GraphShapeListener> iter = getGraphListeners();
        while (iter.hasNext()) {
            iter.next().addUpdate(this, edge);
        }
    }

    /**
     * Calls {@link GraphShapeListener#removeUpdate(GraphShape, Node)} on all GraphListeners in listeners.
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
     * Calls {@link GraphShapeListener#removeUpdate(GraphShape, Edge)} on all GraphListeners in listeners.
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
     * Returns a graph cache for this graph.
     * The graph cache is newly created, using {@link #createCache()}, if no
     * cache is currently set. A reference to the cache is created using
     * {@link #createCacheReference(GraphCache)}.
     * @return a graph cache for this graph
     */
    public GraphShapeCache getCache() {
        GraphShapeCache result = cacheReference.get();
        if (result == null) {
            cacheReference = createCacheReference(result = createCache());
        }
        return result;
    }

    /**
     * Returns the current reference to the graph cache for this graph.
     * The referent may be <tt>null</tt>.
     * @return the current reference to the graph cache for this graph
     */
    final public Reference<? extends GraphShapeCache> getCacheReference() {
        return cacheReference;
    }

    /**
     * Sets the current reference to the graph cache to the given value.
     * @param cacheReference the new graph cache reference
     */
    final protected void setCacheReference(Reference<? extends GraphShapeCache> cacheReference) {
        this.cacheReference = cacheReference;
    }

    /**
     * Factory method for a graph cache.
     * This implementation returns a {@link GraphCache}.
     * @return the graph cache
     */
    protected GraphShapeCache createCache() {
        return new GraphShapeCache(this);
    }
    
    /**
     * Factory method for a reference to a given graph cache.
     * @param referent the graph cache for which to create a reference
     * @return This implementation returns a {@link CacheReference}.
     */
    protected CacheReference<? extends GraphShapeCache> createCacheReference(GraphShapeCache referent) {
        return new CacheReference<GraphShapeCache>(referent);
    }

    /** 
     * Cleares the stored graph cache reference.
     * This frees the cache for clearing, if that has not yet occurred,
     * and saves memory by sharing a single null reference.
     */
    public void clearCache() {
        if (GATHER_STATISTICS) {
            if (cacheReference.get() != null) {
                cacheClearCount++;
            }
        }
        cacheReference = createNullReference();
    }

    /**
     * Signals if the current cache reference contains a <code>null</code> value.
     * @return <tt>true</tt> if the current cache reference is <code>null</code>, <tt>false</tt> otherwise
     */
    public final boolean isCacheCleared() {
        return cacheReference.get() == null;
    }

    /** 
     * Callback method invoked when the cache of this graph has been
     * garbage collected.
     * Note that there is a delay between cache collection and the call of this method,
     * and in the meanwhile the cache might already have been reconstructed. 
     * This implementation sets the cache reference to a shared null reference,
     * to save memory.
     * @see #createNullReference()
     */
    protected void notifyCacheCollected() {
        if (GATHER_STATISTICS) {
            cacheCollectCount++;
        }
        // only do something if the cache is currently cleared
        if (isCacheCleared()) {
            cacheReference = createNullReference();
        }
    }
    
    /**
     * Factory method for a reference to a <code>null</code> value.
     * This is invoked when the graph is cleared explicitly (in {@link #clearCache()})
     * or found to have been collected (in {@link #notifyCacheCollected()}).
     * The method should attempt to return a fixed value that can be shared by 
     * all graphs with a cleared cache.
     * @return This implementation returns {@link #NULL_REFERENCE}.
     */
    protected Reference<? extends GraphShapeCache> createNullReference() {
        return NULL_REFERENCE;
    }

    /**
     * Set of  {@link GraphListener} s to be identified of changes in this graph. Set to <tt>null</tt> when the graph is fixed.
     */
    protected Map<GraphShapeListener,Object> listeners = new HashMap<GraphShapeListener,Object>();

    /**
     * Weak referece to the current graph cache.
     * Initialized by a call of the factory method, {@link #createCache()}.
     * @invariant <tt>graphCache.get() == null || labelEdgeMap.get() instanceof GraphCache.
     * @see #createCache()
     */
    private Reference<? extends GraphShapeCache> cacheReference;

    /**
     * Map in which varies kinds of data can be stored.
     */
    private GraphInfo graphInfo;

    /** 
     * Reference queue to store all cache refs cleared by the garbage collector,
     * so we can also call {@link #clearCache()} on the graph.
     */
    static protected ReferenceQueue<GraphShapeCache> cacheReferenceQueue = new ReferenceQueue<GraphShapeCache>();

    /**
     * Reference class for cache references.
     * The additional functionality is that {@link #clearCache()}
     * is called as a result of {@link Reference#clear()},
     * and that {@link #notifyCollected()} if the cache is cound to have been garbage collected.
     */
    protected class CacheReference<R extends GraphShapeCache> extends SoftReference<R> {
    	/** Creates a reference for a given cache referent. */
        protected CacheReference(R referent) {
            super(referent, cacheReferenceQueue);
            // see if there is any post-clearing up to be done for caches
            // that have been collected by the gc
            CacheReference<?> cache = (CacheReference<?>) cacheReferenceQueue.poll();
            while (cache != null) {
                cache.notifyCollected();
                cache = (CacheReference<?>) cacheReferenceQueue.poll();
            }
        }

        /**
         * Invokes {@link #clearCache()} on the corresponding graph.
         */
        @Override
        public void clear() {
            clearCache();
        }
        
        /**
         * Invokes {@link #notifyCacheCollected()} on the corresponding graph.
         */
        public void notifyCollected() {
            notifyCacheCollected();
        }
    }

    /** Reporter instance for profiling graph methods. */
    static public final Reporter reporter = Reporter.register(GraphShape.class);
    /** Handle for profiling the {@link #nodeSet()} method */
    static final int EDGE_SET = reporter.newMethod("edgeSet()");
    /** Handle for profiling the {@link #edgeSet()} method */
    static final int NODE_SET = reporter.newMethod("nodeSet()");
}