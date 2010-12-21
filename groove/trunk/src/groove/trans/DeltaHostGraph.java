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
package groove.trans;

import groove.graph.AbstractGraph;
import groove.graph.DefaultGraph;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.TypeLabel;
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
public class DeltaHostGraph extends AbstractGraph<HostNode,TypeLabel,HostEdge>
        implements HostGraph {
    /**
     * Constructs a graph with a given basis and delta The basis may be
     * <code>null</code>, meaning that it is the empty graph.
     * @param basis the basis for the new delta graph; possibly
     *        <code>null</code>
     * @param delta the delta with respect to the basis; non-<code>null</code>
     * @param copyData if <code>true</code>, the data structures will be
     *        copied from one graph to the next; otherwise, they will be reused
     */
    private DeltaHostGraph(final DeltaHostGraph basis,
            final DeltaApplier delta, boolean copyData) {
        super();
        this.factory =
            basis == null ? HostFactory.instance() : basis.getFactory();
        this.basis = basis;
        this.copyData = copyData;
        if (delta == null || delta instanceof DeltaStore
            || delta instanceof FrozenDeltaApplier) {
            this.delta = delta;
        } else {
            this.delta = new DeltaStore(delta) {
                @Override
                protected Set<HostEdge> createEdgeSet(Collection<HostEdge> set) {
                    HostEdgeSet result;
                    if (set instanceof HostEdgeSet) {
                        result = new HostEdgeSet((HostEdgeSet) set);
                    } else {
                        result = new HostEdgeSet();
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
     * Since the result should be modifiable, returns a {@link DefaultHostGraph}.
     */
    @Override
    public DefaultHostGraph clone() {
        return new DefaultHostGraph(this);
    }

    /**
     * Since the result should be modifiable, returns a {@link DefaultGraph}.
     */
    public HostGraph newGraph() {
        return new DefaultHostGraph();
    }

    /** Creates a new delta graph from a given basis and delta applier. */
    public DeltaHostGraph newGraph(DeltaHostGraph graph, DeltaApplier applier) {
        return new DeltaHostGraph(graph, applier, this.copyData);
    }

    /**
     * Since the graph is fixed, this method always throws an exception.
     * @throws UnsupportedOperationException always.
     */
    @Override
    public HostNode addNode() {
        throw new UnsupportedOperationException();
    }

    /**
     * Since the graph is fixed, this method always throws an exception.
     * @throws UnsupportedOperationException always.
     */
    @Override
    public HostNode addNode(int nr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HostEdge addEdge(HostNode source, TypeLabel label, HostNode target) {
        throw new UnsupportedOperationException();
    }

    /**
     * Since the graph is fixed, this method always throws an exception.
     * @throws UnsupportedOperationException always.
     */
    public boolean addNode(HostNode node) {
        throw new UnsupportedOperationException();
    }

    /**
     * Since the graph is fixed, this method always throws an exception.
     * @throws UnsupportedOperationException always.
     */
    public boolean removeEdge(HostEdge edge) {
        throw new UnsupportedOperationException();
    }

    /**
     * Since the graph is fixed, this method always throws an exception.
     * @throws UnsupportedOperationException always.
     */
    public boolean addEdgeWithoutCheck(HostEdge edge) {
        throw new UnsupportedOperationException();
    }

    /**
     * Since the graph is fixed, this method always throws an exception.
     * @throws UnsupportedOperationException always.
     */
    public boolean removeNodeWithoutCheck(HostNode node) {
        throw new UnsupportedOperationException();
    }

    public Set<HostNode> nodeSet() {
        if (this.nodeEdgeMap == null) {
            initData();
        }
        Set<HostNode> result = this.nodeEdgeMap.keySet();
        return ALIAS_SETS || this.copyData ? result : createNodeSet(result);
    }

    public Set<HostEdge> edgeSet() {
        if (this.edgeSet == null) {
            initData();
        }
        HostEdgeSet result = this.edgeSet;
        return ALIAS_SETS || this.copyData ? result : createEdgeSet(result);
    }

    @Override
    public Set<HostEdge> inEdgeSet(Node node) {
        HostEdgeSet result = getInEdgeMap().get(node);
        return (ALIAS_SETS || this.copyData) && result != null ? result
                : createEdgeSet(result);
    }

    /** Returns a mapping from labels to sets of edges. */
    private Map<HostNode,HostEdgeSet> getInEdgeMap() {
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
    private Map<HostNode,HostEdgeSet> computeInEdgeMap() {
        Map<HostNode,HostEdgeSet> result =
            new LinkedHashMap<HostNode,HostEdgeSet>();
        for (Map.Entry<HostNode,HostEdgeSet> nodeEdgeEntry : this.nodeEdgeMap.entrySet()) {
            HostNode key = nodeEdgeEntry.getKey();
            HostEdgeSet inEdges = createEdgeSet(null);
            for (HostEdge edge : nodeEdgeEntry.getValue()) {
                if (edge.target().equals(key)) {
                    inEdges.add(edge);
                }
            }
            result.put(key, inEdges);
        }
        return result;
    }

    @Override
    public Set<HostEdge> outEdgeSet(Node node) {
        HostEdgeSet result = getOutEdgeMap().get(node);
        return (ALIAS_SETS || this.copyData) && result != null ? result
                : createEdgeSet(result);
    }

    /** Returns a mapping from nodes to sets of outgoing edges. */
    private Map<HostNode,HostEdgeSet> getOutEdgeMap() {
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
    private Map<HostNode,HostEdgeSet> computeOutEdgeMap() {
        Map<HostNode,HostEdgeSet> result =
            new LinkedHashMap<HostNode,HostEdgeSet>();
        for (Map.Entry<HostNode,HostEdgeSet> nodeEdgeEntry : this.nodeEdgeMap.entrySet()) {
            HostNode key = nodeEdgeEntry.getKey();
            HostEdgeSet inEdges = createEdgeSet(null);
            for (HostEdge edge : nodeEdgeEntry.getValue()) {
                if (edge.source().equals(key)) {
                    inEdges.add(edge);
                }
            }
            result.put(key, inEdges);
        }
        return result;
    }

    @Override
    public Set<HostEdge> labelEdgeSet(Label label) {
        HostEdgeSet result = getLabelEdgeMap().get(label);
        return (ALIAS_SETS || this.copyData) && result != null ? result
                : createEdgeSet(result);
    }

    /** Returns a mapping from labels to sets of edges. */
    private Map<TypeLabel,HostEdgeSet> getLabelEdgeMap() {
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
    private Map<TypeLabel,HostEdgeSet> computeLabelEdgeMap() {
        Map<TypeLabel,HostEdgeSet> result =
            new LinkedHashMap<TypeLabel,HostEdgeSet>();
        for (HostEdge edge : edgeSet()) {
            HostEdgeSet edges = result.get(edge.label());
            if (edges == null) {
                result.put(edge.label(), edges = createEdgeSet(null));
            }
            edges.add(edge);
        }
        return result;
    }

    @Override
    public Set<HostEdge> edgeSet(Node node) {
        HostEdgeSet result = getNodeEdgeMap().get(node);
        return (ALIAS_SETS || this.copyData) && result != null ? result
                : createEdgeSet(result);
    }

    /** Returns the mapping from nodes to sets of incident edges. */
    private Map<HostNode,HostEdgeSet> getNodeEdgeMap() {
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
                this.nodeEdgeMap = new LinkedHashMap<HostNode,HostEdgeSet>();
                // apply the delta to fill the structures;
                // the swing target actually shares this graph's structures
                this.delta.applyDelta(new SwingTarget());
            } else {
                // back up to the first initialised graph
                // or the first graph without a basis
                Stack<DeltaHostGraph> basisChain = new Stack<DeltaHostGraph>();
                basisChain.push(this);
                DeltaHostGraph backward = this.basis;
                while (backward.basis != null && !backward.isDataInitialised()) {
                    basisChain.push(backward);
                    backward = backward.basis;
                }
                // now iteratively construct the intermediate graphs
                backward.initData();
                while (!basisChain.isEmpty()) {
                    DeltaHostGraph forward = basisChain.pop();
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
    HostEdgeSet createEdgeSet(Set<HostEdge> edgeSet) {
        if (edgeSet == null) {
            return new HostEdgeSet();
        } else if (edgeSet instanceof HostEdgeSet) {
            return new HostEdgeSet((HostEdgeSet) edgeSet);
        } else {
            return new HostEdgeSet(edgeSet);
        }
    }

    HostNodeSet createNodeSet(Set<HostNode> nodeSet) {
        if (nodeSet == null) {
            return new HostNodeSet();
        } else if (nodeSet instanceof HostNodeSet) {
            return new HostNodeSet((HostNodeSet) nodeSet);
        } else {
            return new HostNodeSet(nodeSet);
        }
    }

    @Override
    public boolean hasCertifier(boolean strong) {
        return this.certifier != null && this.certifier.get() != null;
    }

    @Override
    public CertificateStrategy<HostNode,TypeLabel,HostEdge> getCertifier(
            boolean strong) {
        CertificateStrategy<HostNode,TypeLabel,HostEdge> result =
            this.certifier == null ? null : this.certifier.get();
        if (result == null || result.getStrength() != strong) {
            result =
                AbstractGraph.getCertificateFactory().newInstance(this, strong);
            this.certifier =
                new WeakReference<CertificateStrategy<HostNode,TypeLabel,HostEdge>>(
                    result);
        }
        return result;
    }

    @Override
    protected boolean isTypeCorrect(Node node) {
        return node instanceof HostNode;
    }

    @Override
    protected boolean isTypeCorrect(Edge edge) {
        return edge instanceof HostEdge;
    }

    /**
     * Delta graphs should not renew their factory,
     * as they are shared with the basis.
     */
    final public void renewFactory() {
        if (this.factory == null) {
            throw new UnsupportedOperationException();
        } else {
            this.factory = this.factory.newFactory(this);
        }
    }

    @Override
    public HostFactory getFactory() {
        return this.factory;
    }

    /** The element factory of this host graph. */
    private HostFactory factory;

    /** The fixed (possibly <code>null</code> basis of this graph. */
    DeltaHostGraph basis;
    /** The fixed delta of this graph. */
    DeltaApplier delta;

    /** The (initially null) edge set of this graph. */
    HostEdgeSet edgeSet;
    /** The map from nodes to sets of incident edges. */
    Map<HostNode,HostEdgeSet> nodeEdgeMap;
    /** The map from nodes to sets of incoming edges. */
    Map<HostNode,HostEdgeSet> nodeInEdgeMap;
    /** The map from nodes to sets of outgoing edges. */
    Map<HostNode,HostEdgeSet> nodeOutEdgeMap;
    /** Mapping from labels to sets of edges with that label. */
    Map<TypeLabel,HostEdgeSet> labelEdgeMap;
    /** The certificate strategy of this graph, set on demand. */
    private Reference<CertificateStrategy<HostNode,TypeLabel,HostEdge>> certifier;
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
    static private final DeltaHostGraph copyInstance = new DeltaHostGraph(null,
        null, true);
    /** Factory instance of this class. */
    static private final DeltaHostGraph swingInstance = new DeltaHostGraph(
        null, null, false);

    /**
     * Returns a fixed factory instance of the {@link DeltaHostGraph} class,
     * which either copies or aliases the data.
     * @param copyData if <code>true</code>, the graph produced by the
     *        factory copy their data structure from one graph to the next;
     *        otherwise, data are shared (and hence must be reconstructed more
     *        often)
     */
    static public DeltaHostGraph getInstance(boolean copyData) {
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
        void install(DeltaHostGraph child) {
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
        public boolean addNode(HostNode node) {
            Set<HostEdge> edges = addKeyToMap(this.nodeEdgeMap, node);
            assert edges == null;
            addKeyToMap(this.nodeInEdgeMap, node);
            addKeyToMap(this.nodeOutEdgeMap, node);
            return true;
        }

        /** Removes the node from the node set and the node-edge map. */
        @Override
        public boolean removeNode(HostNode node) {
            Set<HostEdge> edges = removeKeyFromMap(this.nodeEdgeMap, node);
            assert edges.isEmpty();
            removeKeyFromMap(this.nodeOutEdgeMap, node);
            removeKeyFromMap(this.nodeInEdgeMap, node);
            return true;
        }

        /**
         * Adds an edge to all maps stored in this target,
         * if they are not {@code null}.
         * A second parameter determines if the set sets
         * in the map should be copied upon modification.
         */
        final boolean addEdge(HostEdge elem, boolean refreshSource,
                boolean refreshTarget, boolean refreshLabel) {
            boolean result = this.edgeSet.add(elem);
            assert result;
            // adapt node-edge map
            HostNode source = elem.source();
            HostNode target = elem.target();
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
        final boolean removeEdge(HostEdge edge, boolean refreshSource,
                boolean refreshTarget, boolean refreshLabel) {
            boolean result = this.edgeSet.remove(edge);
            assert result;
            // adapt node-edge map
            HostNode source = edge.source();
            HostNode target = edge.target();
            removeEdgeFromMap(this.nodeEdgeMap, source, edge, refreshSource);
            if (source != target) {
                removeEdgeFromMap(this.nodeEdgeMap, target, edge, refreshTarget);
            }
            removeEdgeFromMap(this.nodeOutEdgeMap, source, edge, refreshSource);
            removeEdgeFromMap(this.nodeInEdgeMap, target, edge, refreshTarget);
            removeEdgeFromMap(this.labelEdgeMap, edge.label(), edge,
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
        private <T> HostEdgeSet addKeyToMap(Map<T,HostEdgeSet> map, T key) {
            HostEdgeSet result = null;
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
        private <T> HostEdgeSet addToMap(Map<T,HostEdgeSet> map, T key,
                HostEdge edge, boolean refresh) {
            HostEdgeSet result = null;
            if (map != null) {
                result = map.get(key);
                if (refresh) {
                    map.put(key, result = createEdgeSet(result));
                } else if (result == null) {
                    map.put(key, result = createEdgeSet(null));
                }
                result.add(edge);
            }
            return result;
        }

        /** Removes an edge from a given mapping,
         * if the mapping is not {@code null}. 
         */
        private <T> HostEdgeSet removeEdgeFromMap(Map<T,HostEdgeSet> map,
                T key, HostEdge edge, boolean refresh) {
            HostEdgeSet result = null;
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
        private <T> HostEdgeSet removeKeyFromMap(Map<T,HostEdgeSet> map, T key) {
            HostEdgeSet result = null;
            if (map != null) {
                result = map.remove(key);
            }
            return result;
        }

        /** Edge set to be filled by this target. */
        HostEdgeSet edgeSet;
        /** Node/edge map to be filled by this target. */
        Map<HostNode,HostEdgeSet> nodeEdgeMap;
        /** Node/incoming edge map to be filled by this target. */
        Map<HostNode,HostEdgeSet> nodeInEdgeMap;
        /** Node/outgoing edge map to be filled by this target. */
        Map<HostNode,HostEdgeSet> nodeOutEdgeMap;
        /** Label/edge map to be filled by this target. */
        Map<TypeLabel,HostEdgeSet> labelEdgeMap;
    }

    /** Delta target to initialise the data structures. */
    private class SwingTarget extends DataTarget {
        /** Constructs and instance for a given node and edge set. */
        public SwingTarget() {
            DeltaHostGraph graph = DeltaHostGraph.this;
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
        public boolean addEdge(HostEdge elem) {
            return super.addEdge(elem, false, false, false);
        }

        /**
         * Removes the edge from the edge set, the node-edge map (if it is set),
         * and the label-edge maps (if it is set).
         */
        public boolean removeEdge(HostEdge elem) {
            return super.removeEdge(elem, false, false, false);
        }

        @Override
        void install(DeltaHostGraph child) {
            DeltaHostGraph graph = DeltaHostGraph.this;
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
            DeltaHostGraph graph = DeltaHostGraph.this;
            this.edgeSet = createEdgeSet(graph.edgeSet);
            this.nodeEdgeMap =
                new LinkedHashMap<HostNode,HostEdgeSet>(graph.nodeEdgeMap);
            this.freshNodeKeys = createNodeSet(null);
            if (graph.labelEdgeMap != null) {
                this.labelEdgeMap =
                    new LinkedHashMap<TypeLabel,HostEdgeSet>(graph.labelEdgeMap);
                this.freshLabelKeys = new HashSet<TypeLabel>();
            } else {
                this.freshLabelKeys = null;
            }
            if (graph.nodeInEdgeMap != null) {
                this.nodeInEdgeMap =
                    new LinkedHashMap<HostNode,HostEdgeSet>(graph.nodeInEdgeMap);
            }
            if (graph.nodeOutEdgeMap != null) {
                this.nodeOutEdgeMap =
                    new LinkedHashMap<HostNode,HostEdgeSet>(
                        graph.nodeOutEdgeMap);
            }
        }

        /**
         * Adds the edge to the edge set, the node-edge map (if it is set), and
         * the label-edge maps (if it is set).
         */
        @Override
        public boolean addEdge(HostEdge elem) {
            HostNode source = (elem).source();
            HostNode target = (elem).target();
            boolean refreshSource = this.freshNodeKeys.add(source);
            boolean refreshTarget =
                source != target && this.freshNodeKeys.add(target);
            boolean refreshLabel =
                this.freshLabelKeys != null
                    && this.freshLabelKeys.add((elem).label());
            return super.addEdge(elem, refreshSource, refreshTarget,
                refreshLabel);
        }

        /**
         * Removes the edge from the edge set, the node-edge map (if it is set),
         * and the label-edge maps (if it is set).
         */
        @Override
        public boolean removeEdge(HostEdge edge) {
            HostNode source = edge.source();
            HostNode target = edge.target();
            boolean refreshSource = this.freshNodeKeys.add(source);
            boolean refreshTarget =
                source != target && this.freshNodeKeys.add(target);
            boolean refreshLabel =
                this.freshLabelKeys != null
                    && this.freshLabelKeys.add(edge.label());
            return super.removeEdge(edge, refreshSource, refreshTarget,
                refreshLabel);
        }

        /** Auxiliary set to determine the nodes changed w.r.t. the basis. */
        private final Set<HostNode> freshNodeKeys;
        /** Auxiliary set to determine the labels changed w.r.t. the basis. */
        private final Set<TypeLabel> freshLabelKeys;
    }
}
