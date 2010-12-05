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
 * $Id: NewDeltaGraph.java,v 1.11 2008-01-21 14:59:48 rensink Exp $
 */
package groove.graph;

import groove.graph.iso.CertificateStrategy;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Class to serve to capture the graphs associated with graph states. These have
 * the characteristic that they are fixed, and are defined by a delta to another
 * graph (where the delta is the result of a rule application).
 * @author Arend Rensink
 * @version $Revision $
 */
public class NewDeltaGraph extends AbstractGraph<GraphCache> implements
        DeltaGraphFactory<NewDeltaGraph>, Cloneable {
    /**
     * Constructs a graph with a given basis and delta The basis may be
     * <code>null</code>, meaning that it is the empty graph.
     * @param basis the basis for the new delta graph; possibly
     *        <code>null</code>
     * @param delta the delta with respect to the basis; non-<code>null</code>
     * @param copyData if <code>true</code>, the data structures will be
     *        copied from one graph to the next; otherwise, they will be reused
     */
    private NewDeltaGraph(final NewDeltaGraph basis, final DeltaApplier delta,
            boolean copyData) {
        this.basis = basis;
        this.copyData = copyData;
        if (delta == null || delta instanceof DeltaStore
            || delta instanceof FrozenDeltaApplier) {
            this.delta = delta;
        } else {
            this.delta = new DeltaStore(delta) {
                @Override
                @SuppressWarnings("unchecked")
                protected Set<Edge> createEdgeSet(Collection<? extends Edge> set) {
                    @SuppressWarnings("rawtypes")
                    Set result;
                    if (set instanceof DefaultEdgeSet) {
                        result = new DefaultEdgeSet((DefaultEdgeSet) set);
                    } else {
                        result = new DefaultEdgeSet();
                        if (set != null) {
                            result.addAll(set);
                        }
                    }
                    return result;
                }
            };
        }
        setFixed();
    }

    /**
     * Since the result should be modifiable, returns a {@link DefaultGraph}.
     */
    @Override
    public Graph clone() {
        return new DefaultGraph(this);
    }

    /**
     * Since the result should be modifiable, returns a {@link DefaultGraph}.
     */
    public DefaultGraph newGraph() {
        return new DefaultGraph();
    }

    public NewDeltaGraph newGraph(NewDeltaGraph graph, DeltaApplier applier) {
        return new NewDeltaGraph(graph, applier, this.copyData);
    }

    /**
     * Since the graph is fixed, this method always throws an exception.
     * @throws UnsupportedOperationException always.
     */
    public boolean addEdge(Edge edge) {
        throw new UnsupportedOperationException();
    }

    /**
     * Since the graph is fixed, this method always throws an exception.
     * @throws UnsupportedOperationException always.
     */
    public boolean addNode(Node node) {
        throw new UnsupportedOperationException();
    }

    /**
     * Since the graph is fixed, this method always throws an exception.
     * @throws UnsupportedOperationException always.
     */
    public boolean removeEdge(Edge edge) {
        throw new UnsupportedOperationException();
    }

    /**
     * Since the graph is fixed, this method always throws an exception.
     * @throws UnsupportedOperationException always.
     */
    public boolean removeNode(Node node) {
        throw new UnsupportedOperationException();
    }

    /**
     * Since the graph is fixed, this method always throws an exception.
     * @throws UnsupportedOperationException always.
     */
    public boolean addEdgeWithoutCheck(Edge edge) {
        throw new UnsupportedOperationException();
    }

    /**
     * Since the graph is fixed, this method always throws an exception.
     * @throws UnsupportedOperationException always.
     */
    public boolean removeNodeWithoutCheck(Node node) {
        throw new UnsupportedOperationException();
    }

    public Set<Node> nodeSet() {
        if (this.nodeEdgeMap == null) {
            initData();
        }
        Set<Node> result = this.nodeEdgeMap.keySet();
        return ALIAS_SETS || this.copyData ? result : createNodeSet(result);
    }

    public Set<DefaultEdge> edgeSet() {
        if (this.edgeSet == null) {
            initData();
        }
        DefaultEdgeSet result = this.edgeSet;
        return ALIAS_SETS || this.copyData ? result : createEdgeSet(result);
    }

    @Override
    public Set<? extends Edge> inEdgeSet(Node node) {
        DefaultEdgeSet result = getInEdgeMap().get(node);
        return (ALIAS_SETS || this.copyData) && result != null ? result
                : createEdgeSet(result);
    }

    /** Returns a mapping from labels to sets of edges. */
    private Map<Node,DefaultEdgeSet> getInEdgeMap() {
        if (this.nodeInEdgeMap == null) {
            initData();
            if (this.nodeInEdgeMap == null) {
                this.nodeInEdgeMap = computeInEdgeMap();
            }
        }
        return this.nodeInEdgeMap;
    }

    /**
     * Computes the node-to-incoming-edgeset map from the node and edge sets. This
     * method is only used if the map could not be obtained from the basis.
     */
    private Map<Node,DefaultEdgeSet> computeInEdgeMap() {
        Map<Node,DefaultEdgeSet> result =
            new LinkedHashMap<Node,DefaultEdgeSet>();
        for (Map.Entry<Node,DefaultEdgeSet> nodeEdgeEntry : this.nodeEdgeMap.entrySet()) {
            Node key = nodeEdgeEntry.getKey();
            DefaultEdgeSet inEdges = createEdgeSet(null);
            for (DefaultEdge edge : nodeEdgeEntry.getValue()) {
                if (edge.target().equals(key)) {
                    inEdges.add(edge);
                }
            }
            result.put(key, inEdges);
        }
        return result;
    }

    @Override
    public Set<? extends Edge> outEdgeSet(Node node) {
        DefaultEdgeSet result = getOutEdgeMap().get(node);
        return (ALIAS_SETS || this.copyData) && result != null ? result
                : createEdgeSet(result);
    }

    /** Returns a mapping from nodes to sets of outgoing edges. */
    private Map<Node,DefaultEdgeSet> getOutEdgeMap() {
        if (this.nodeOutEdgeMap == null) {
            initData();
            if (this.nodeOutEdgeMap == null) {
                this.nodeOutEdgeMap = computeOutEdgeMap();
            }
        }
        return this.nodeOutEdgeMap;
    }

    /**
     * Computes the node-to-incoming-edgeset map from the node and edge sets. This
     * method is only used if the map could not be obtained from the basis.
     */
    private Map<Node,DefaultEdgeSet> computeOutEdgeMap() {
        Map<Node,DefaultEdgeSet> result =
            new LinkedHashMap<Node,DefaultEdgeSet>();
        for (Map.Entry<Node,DefaultEdgeSet> nodeEdgeEntry : this.nodeEdgeMap.entrySet()) {
            Node key = nodeEdgeEntry.getKey();
            DefaultEdgeSet inEdges = createEdgeSet(null);
            for (DefaultEdge edge : nodeEdgeEntry.getValue()) {
                if (edge.source().equals(key)) {
                    inEdges.add(edge);
                }
            }
            result.put(key, inEdges);
        }
        return result;
    }

    @Override
    public Set<DefaultEdge> labelEdgeSet(Label label) {
        DefaultEdgeSet result = (DefaultEdgeSet) getLabelEdgeMap().get(label);
        return (ALIAS_SETS || this.copyData) && result != null ? result
                : createEdgeSet(result);
    }

    /** Returns a mapping from labels to sets of edges. */
    private Map<Label,? extends Set<? extends Edge>> getLabelEdgeMap() {
        if (this.labelEdgeMap == null) {
            initData();
            if (this.labelEdgeMap == null) {
                this.labelEdgeMap = computeLabelEdgeMap();
            }
        }
        return this.labelEdgeMap;
    }

    /**
     * Computes the label-to-edgeset map from the node and edge sets. This
     * method is only used if the map could not be obtained from the basis.
     */
    private Map<Label,DefaultEdgeSet> computeLabelEdgeMap() {
        Map<Label,DefaultEdgeSet> result =
            new LinkedHashMap<Label,DefaultEdgeSet>();
        for (DefaultEdge edge : edgeSet()) {
            DefaultEdgeSet edges = result.get(edge.label());
            if (edges == null) {
                result.put(edge.label(), edges = createEdgeSet(null));
            }
            edges.add(edge);
        }
        return result;
    }

    @Override
    public Set<? extends Edge> edgeSet(Node node) {
        DefaultEdgeSet result = getNodeEdgeMap().get(node);
        return (ALIAS_SETS || this.copyData) && result != null ? result
                : createEdgeSet(result);
    }

    /** Returns the mapping from nodes to sets of incident edges. */
    private Map<Node,DefaultEdgeSet> getNodeEdgeMap() {
        if (this.nodeEdgeMap == null) {
            initData();
        }
        return this.nodeEdgeMap;
    }

    /**
     * Initialises all the data structures, if this has not yet been done.
     */
    private void initData() {
        if (!isDataInitialised()) {
            assert this.nodeEdgeMap == null;
            assert this.labelEdgeMap == null;
            if (this.basis == null) {
                this.edgeSet = createEdgeSet(null);
                this.nodeEdgeMap = new LinkedHashMap<Node,DefaultEdgeSet>();
                // apply the delta to fill the structures;
                // the swing target actually shares this graph's structures
                this.delta.applyDelta(new SwingTarget());
            } else {
                // back up to the first initialised graph
                // or the first graph without a basis
                Stack<NewDeltaGraph> basisChain = new Stack<NewDeltaGraph>();
                basisChain.push(this);
                NewDeltaGraph backward = this.basis;
                while (backward.basis != null && !backward.isDataInitialised()) {
                    basisChain.push(backward);
                    backward = backward.basis;
                }
                // now iteratively construct the intermediate graphs
                backward.initData();
                while (!basisChain.isEmpty()) {
                    NewDeltaGraph forward = basisChain.pop();
                    DataTarget target =
                        forward.basis.getDataTarget(basisChain.size());
                    // apply the delta to fill the structures
                    forward.delta.applyDelta(target);
                    target.install(forward);
                }
            }
        }
    }

    /** Reports if the data structures of this delta graph have been initialised. */
    private boolean isDataInitialised() {
        return this.edgeSet != null;
    }

    /**
     * Creates a delta target that will construct the necessary data structures
     * for a child graph.
     * @param depth the basis chain depth at which this graph was found.
     */
    private DataTarget getDataTarget(int depth) {
        DataTarget result;
        // data should have been initialised
        assert isDataInitialised();
        if ((depth + 1) % MAX_CHAIN_DEPTH == 0) {
            result = new CopyTarget();
        } else {
            result = this.copyData ? new CopyTarget() : new SwingTarget();
        }
        return result;
    }

    /**
     * Creates a copy of an existing set of edges, or an empty set if the given
     * set is <code>null</code>.
     */
    DefaultEdgeSet createEdgeSet(Set<DefaultEdge> edgeSet) {
        if (edgeSet == null) {
            return new DefaultEdgeSet();
        } else if (edgeSet instanceof DefaultEdgeSet) {
            return new DefaultEdgeSet((DefaultEdgeSet) edgeSet);
        } else {
            return new DefaultEdgeSet(edgeSet);
        }
    }

    NodeSet createNodeSet(Set<Node> nodeSet) {
        if (nodeSet == null) {
            return new NodeSet();
        } else if (nodeSet instanceof NodeSet) {
            return new NodeSet((NodeSet) nodeSet);
        } else {
            return new NodeSet(nodeSet);
        }
    }

    @Override
    public boolean hasCertifier(boolean strong) {
        return this.certifier != null && this.certifier.get() != null;
    }

    @Override
    public CertificateStrategy getCertifier(boolean strong) {
        CertificateStrategy result =
            this.certifier == null ? null : this.certifier.get();
        if (result == null || result.getStrength() != strong) {
            result =
                AbstractGraph.getCertificateFactory().newInstance(this, strong);
            this.certifier = new WeakReference<CertificateStrategy>(result);
        }
        return result;
    }

    /** The fixed (possibly <code>null</code> basis of this graph. */
    NewDeltaGraph basis;
    /** The fixed delta of this graph. */
    DeltaApplier delta;

    /** The (initially null) edge set of this graph. */
    DefaultEdgeSet edgeSet;
    /** The map from nodes to sets of incident edges. */
    Map<Node,DefaultEdgeSet> nodeEdgeMap;
    /** The map from nodes to sets of incoming edges. */
    Map<Node,DefaultEdgeSet> nodeInEdgeMap;
    /** The map from nodes to sets of outgoing edges. */
    Map<Node,DefaultEdgeSet> nodeOutEdgeMap;
    /** Mapping from labels to sets of edges with that label. */
    Map<Label,DefaultEdgeSet> labelEdgeMap;
    /** The certificate strategy of this graph, set on demand. */
    private Reference<CertificateStrategy> certifier;
    /**
     * Flag indicating that data should be copied rather than shared in
     * {@link #getDataTarget(int)}.
     */
    private boolean copyData = true;
    /** Maximum basis chain length at which the data target is set
     * to a {@link CopyTarget} regardless of the value of {@link #copyData}.
     */
    static private final int MAX_CHAIN_DEPTH = 100;
    /**
     * Debug flag for aliasing the node and edge set. Aliasing the sets may give
     * {@link ConcurrentModificationException}s during matching.
     */
    static private final boolean ALIAS_SETS = true;
    /** Factory instance of this class. */
    static private final NewDeltaGraph copyInstance = new NewDeltaGraph(null,
        null, true);
    /** Factory instance of this class. */
    static private final NewDeltaGraph swingInstance = new NewDeltaGraph(null,
        null, false);

    /**
     * Returns a fixed factory instance of the {@link NewDeltaGraph} class,
     * which either copies or aliases the data.
     * @param copyData if <code>true</code>, the graph produced by the
     *        factory copy their data structure from one graph to the next;
     *        otherwise, data are shared (and hence must be reconstructed more
     *        often)
     */
    static public NewDeltaGraph getInstance(boolean copyData) {
        return copyData ? copyInstance : swingInstance;
    }

    /**
     * Superclass for data construction targets. Subclasses should fill the
     * instance variables of this class during construction time and the
     * invocation of the {@link DeltaTarget} add and remove methods.
     * @author Arend Rensink
     * @version $Revision $
     */
    abstract private class DataTarget implements DeltaTarget {
        /** Empty constructor with correct visibility. */
        DataTarget() {
            // empty
        }

        /**
         * Assigns the data structures computed in this data object to a given
         * delta graph.
         * @param child the graph to which the data structures should be
         *        installed
         */
        void install(NewDeltaGraph child) {
            child.edgeSet = this.edgeSet;
            child.nodeEdgeMap = this.nodeEdgeMap;
            child.nodeInEdgeMap = this.nodeInEdgeMap;
            child.nodeOutEdgeMap = this.nodeOutEdgeMap;
            child.labelEdgeMap = this.labelEdgeMap;
            child.delta = null;
            child.basis = null;
        }

        /** Adds the node to the node set and the node-edge map. */
        @Override
        public boolean addNode(Node node) {
            Set<DefaultEdge> edges = addKeyToMap(this.nodeEdgeMap, node);
            assert edges == null;
            addKeyToMap(this.nodeInEdgeMap, node);
            addKeyToMap(this.nodeOutEdgeMap, node);
            return true;
        }

        /** Removes the node from the node set and the node-edge map. */
        @Override
        public boolean removeNode(Node elem) {
            Set<DefaultEdge> edges = removeKeyFromMap(this.nodeEdgeMap, elem);
            assert edges.isEmpty();
            removeKeyFromMap(this.nodeOutEdgeMap, elem);
            removeKeyFromMap(this.nodeInEdgeMap, elem);
            return true;
        }

        /**
         * Adds an edge to all maps stored in this target,
         * if they are not {@code null}.
         * A second parameter determines if the set sets
         * in the map should be copied upon modification.
         */
        final boolean addEdge(Edge elem, boolean refreshSource,
                boolean refreshTarget, boolean refreshLabel) {
            boolean result = this.edgeSet.add((DefaultEdge) elem);
            assert result;
            // adapt node-edge map
            Node source = elem.source();
            Node target = elem.target();
            addToMap(this.nodeEdgeMap, source, elem, refreshSource);
            if (source != target) {
                addToMap(this.nodeEdgeMap, target, elem, refreshTarget);
            }
            // adapt label-edge map
            addToMap(this.nodeOutEdgeMap, source, elem, refreshSource);
            addToMap(this.nodeInEdgeMap, target, elem, refreshTarget);
            addToMap(this.labelEdgeMap, elem.label(), elem, refreshLabel);
            return result;
        }

        /**
         * Removes an edge from all maps stored in this target,
         * if they are not {@code null}.
         * A second parameter determines if the set sets
         * in the map should be copied upon modification.
         */
        final boolean removeEdge(Edge elem, boolean refreshSource,
                boolean refreshTarget, boolean refreshLabel) {
            boolean result = this.edgeSet.remove(elem);
            assert result;
            // adapt node-edge map
            Node source = elem.source();
            Node target = elem.target();
            removeEdgeFromMap(this.nodeEdgeMap, source, elem, refreshSource);
            if (source != target) {
                removeEdgeFromMap(this.nodeEdgeMap, target, elem, refreshTarget);
            }
            removeEdgeFromMap(this.nodeOutEdgeMap, source, elem, refreshSource);
            removeEdgeFromMap(this.nodeInEdgeMap, target, elem, refreshTarget);
            removeEdgeFromMap(this.labelEdgeMap, elem.label(), elem,
                refreshLabel);
            return result;
        }

        /**
         * Adds a key to a given key-to-edgeset mapping.
         * @param <T> the type of the key
         * @param map the mapping to be modified; may be {@code null}
         * @param key the key to be inserted
         * @return the previous edgeset for the key, if the map was not {@code null}
         */
        private <T> DefaultEdgeSet addKeyToMap(Map<T,DefaultEdgeSet> map, T key) {
            DefaultEdgeSet result = null;
            if (map != null) {
                result = map.put(key, result = createEdgeSet(null));
            }
            return result;
        }

        /** Adds an edge to the image of a given key, in a key-to-edgeset mapping.
         * @param <T> the type of the key
         * @param map the mapping to be modified; may be {@code null}
         * @param key the key to be inserted
         * @param edge the edge to be inserted in the key's image; may be {@code null}
         * if only the key should be added
         * @param refresh flag indicating if a new edge set should be created
         * @return the edgeset for the key, if the map was not {@code null}
         */
        private <T> DefaultEdgeSet addToMap(Map<T,DefaultEdgeSet> map, T key,
                Edge edge, boolean refresh) {
            DefaultEdgeSet result = null;
            if (map != null) {
                result = map.get(key);
                if (refresh) {
                    map.put(key, result = createEdgeSet(result));
                } else if (result == null) {
                    map.put(key, result = createEdgeSet(null));
                }
                result.add((DefaultEdge) edge);
            }
            return result;
        }

        /** Removes an edge from a given mapping,
         * if the mapping is not {@code null}. 
         */
        private <T> DefaultEdgeSet removeEdgeFromMap(Map<T,DefaultEdgeSet> map,
                T key, Edge edge, boolean refresh) {
            DefaultEdgeSet result = null;
            if (map != null) {
                result = map.get(key);
                if (refresh) {
                    map.put(key, result = createEdgeSet(result));
                }
                result.remove(edge);
            }
            return result;
        }

        /** Removes either a key from a given mapping,
         * if the mapping is not {@code null}. 
         */
        private <T> DefaultEdgeSet removeKeyFromMap(Map<T,DefaultEdgeSet> map,
                T key) {
            DefaultEdgeSet result = null;
            if (map != null) {
                result = map.remove(key);
            }
            return result;
        }

        /** Edge set to be filled by this target. */
        DefaultEdgeSet edgeSet;
        /** Node/edge map to be filled by this target. */
        Map<Node,DefaultEdgeSet> nodeEdgeMap;
        /** Node/incoming edge map to be filled by this target. */
        Map<Node,DefaultEdgeSet> nodeInEdgeMap;
        /** Node/outgoing edge map to be filled by this target. */
        Map<Node,DefaultEdgeSet> nodeOutEdgeMap;
        /** Label/edge map to be filled by this target. */
        Map<Label,DefaultEdgeSet> labelEdgeMap;
    }

    /** Delta target to initialise the data structures. */
    private class SwingTarget extends DataTarget {
        /** Constructs and instance for a given node and edge set. */
        public SwingTarget() {
            NewDeltaGraph graph = NewDeltaGraph.this;
            // only construct a node set if the node-edge map is not there. */
            this.edgeSet = graph.edgeSet;
            this.nodeEdgeMap = graph.nodeEdgeMap;
            this.nodeInEdgeMap = graph.nodeInEdgeMap;
            this.nodeOutEdgeMap = graph.nodeOutEdgeMap;
            this.labelEdgeMap = graph.labelEdgeMap;
        }

        /**
         * Adds the edge to the edge set, the node-edge map (if it is set), and
         * the label-edge maps (if it is set).
         */
        public boolean addEdge(Edge elem) {
            return super.addEdge(elem, false, false, false);
        }

        /**
         * Removes the edge from the edge set, the node-edge map (if it is set),
         * and the label-edge maps (if it is set).
         */
        public boolean removeEdge(Edge elem) {
            return super.removeEdge(elem, false, false, false);
        }

        @Override
        void install(NewDeltaGraph child) {
            NewDeltaGraph graph = NewDeltaGraph.this;
            graph.edgeSet = null;
            graph.nodeEdgeMap = null;
            graph.nodeInEdgeMap = null;
            graph.nodeOutEdgeMap = null;
            graph.labelEdgeMap = null;
            if (graph.delta == null) {
                graph.basis = child;
                graph.delta = ((DeltaStore) child.delta).invert(true);
            }
            super.install(child);
        }
    }

    /** Delta target to initialise the data structures. */
    private class CopyTarget extends DataTarget {
        /** Constructs and instance for a given node and edge set. */
        public CopyTarget() {
            NewDeltaGraph graph = NewDeltaGraph.this;
            this.edgeSet = createEdgeSet(graph.edgeSet);
            this.nodeEdgeMap =
                new LinkedHashMap<Node,DefaultEdgeSet>(graph.nodeEdgeMap);
            this.freshNodeKeys = createNodeSet(null);
            if (graph.labelEdgeMap != null) {
                this.labelEdgeMap =
                    new LinkedHashMap<Label,DefaultEdgeSet>(graph.labelEdgeMap);
                this.freshLabelKeys = new HashSet<Label>();
            } else {
                this.freshLabelKeys = null;
            }
            if (graph.nodeInEdgeMap != null) {
                this.nodeInEdgeMap =
                    new LinkedHashMap<Node,DefaultEdgeSet>(graph.nodeInEdgeMap);
            }
            if (graph.nodeOutEdgeMap != null) {
                this.nodeOutEdgeMap =
                    new LinkedHashMap<Node,DefaultEdgeSet>(graph.nodeOutEdgeMap);
            }
        }

        /**
         * Adds the edge to the edge set, the node-edge map (if it is set), and
         * the label-edge maps (if it is set).
         */
        @Override
        public boolean addEdge(Edge elem) {
            Node source = elem.source();
            Node target = elem.target();
            boolean refreshSource = this.freshNodeKeys.add(source);
            boolean refreshTarget =
                source != target && this.freshNodeKeys.add(target);
            boolean refreshLabel =
                this.freshLabelKeys != null
                    && this.freshLabelKeys.add(elem.label());
            return super.addEdge(elem, refreshSource, refreshTarget,
                refreshLabel);
        }

        /**
         * Removes the edge from the edge set, the node-edge map (if it is set),
         * and the label-edge maps (if it is set).
         */
        @Override
        public boolean removeEdge(Edge elem) {
            Node source = elem.source();
            Node target = elem.target();
            boolean refreshSource = this.freshNodeKeys.add(source);
            boolean refreshTarget =
                source != target && this.freshNodeKeys.add(target);
            boolean refreshLabel = this.freshLabelKeys.add(elem.label());
            return super.removeEdge(elem, refreshSource, refreshTarget,
                refreshLabel);
        }

        /** Auxiliary set to determine the nodes changed w.r.t. the basis. */
        private final Set<Node> freshNodeKeys;
        /** Auxiliary set to determine the labels changed w.r.t. the basis. */
        private final Set<Label> freshLabelKeys;
    }
}
