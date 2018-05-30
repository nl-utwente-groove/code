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
package groove.grammar.host;

import static groove.graph.GraphRole.HOST;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jdt.annotation.NonNull;

import groove.algebra.AlgebraFamily;
import groove.grammar.type.TypeLabel;
import groove.graph.AGraph;
import groove.graph.Edge;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.iso.CertificateStrategy;
import groove.transform.DeltaApplier;
import groove.transform.DeltaStore;
import groove.transform.DeltaTarget;
import groove.transform.FrozenDeltaApplier;
import groove.transform.StoredDeltaApplier;
import groove.util.parse.FormatErrorSet;

/**
 * Class to serve to capture the graphs associated with graph states. These have
 * the characteristic that they are fixed, and are defined by a delta to another
 * graph (where the delta is the result of a rule application).
 * @author Arend Rensink
 * @version $Revision $
 */
public final class DeltaHostGraph extends AGraph<HostNode,HostEdge>
    implements HostGraph, Cloneable {
    /**
     * Constructs a graph with an empty basis and a delta determining
     * the elements of the graph.
     * @param name the name of the graph
     * @param delta the delta determining the initial graph
     * @param factory the factory for new graph elements
     * @param copyData if <code>true</code>, the data structures will be
     *        copied from one graph to the next; otherwise, they will be reused
     */
    private DeltaHostGraph(String name, HostElement[] delta, HostFactory factory,
        boolean copyData) {
        super(name);
        this.factory = factory;
        this.basis = null;
        this.copyData = copyData;
        this.delta = new FrozenDeltaApplier(delta);
        setFixed();
    }

    /**
     * Constructs a graph with a given (non-{@code null}) basis and delta.
     * @param name the name of the graph
     * @param basis the (non-{@code null}) basis for the new delta graph
     * @param delta the delta with respect to the basis; non-<code>null</code>
     * @param copyData if <code>true</code>, the data structures will be
     *        copied from one graph to the next; otherwise, they will be reused
     */
    private DeltaHostGraph(String name, final DeltaHostGraph basis, final DeltaApplier delta,
        boolean copyData) {
        super(name);
        this.basis = basis;
        this.factory = basis.getFactory();
        this.copyData = copyData;
        if (delta == null || delta instanceof StoredDeltaApplier) {
            this.delta = (StoredDeltaApplier) delta;
        } else {
            this.delta = new DeltaStore(delta);
        }
        setFixed();
    }

    @Override
    public GraphRole getRole() {
        return HOST;
    }

    /**
     * Since the result should be modifiable, returns a {@link DefaultHostGraph}.
     */
    @Override
    public DefaultHostGraph clone() {
        return new DefaultHostGraph(this, null);
    }

    @Override
    public HostGraph clone(AlgebraFamily family) {
        return new DefaultHostGraph(this, family);
    }

    /**
     * Since the result should be modifiable, returns a {@link DefaultHostGraph}.
     */
    @Override
    public HostGraph newGraph(String name) {
        return new DefaultHostGraph(name, getFactory());
    }

    /**
     * Creates a new delta graph from a given basis and delta applier.
     * @param name the name of the new graph
     */
    public DeltaHostGraph newGraph(String name, DeltaHostGraph graph, DeltaApplier applier) {
        return new DeltaHostGraph(name, graph, applier, this.copyData);
    }

    /** Creates a new delta graph from a given element array.
     * @param name the name of the new graph
     */
    public DeltaHostGraph newGraph(String name, HostElement[] elements, HostFactory factory) {
        return new DeltaHostGraph(name, elements, factory, this.copyData);
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
    public HostEdge addEdge(HostNode source, Label label, HostNode target) {
        throw new UnsupportedOperationException();
    }

    /**
     * Since the graph is fixed, this method always throws an exception.
     * @throws UnsupportedOperationException always.
     */
    @Override
    public boolean addNode(HostNode node) {
        throw new UnsupportedOperationException();
    }

    /**
     * Since the graph is fixed, this method always throws an exception.
     * @throws UnsupportedOperationException always.
     */
    @Override
    public boolean removeEdge(HostEdge edge) {
        throw new UnsupportedOperationException();
    }

    /**
     * Since the graph is fixed, this method always throws an exception.
     * @throws UnsupportedOperationException always.
     */
    @Override
    public boolean addEdge(HostEdge edge) {
        throw new UnsupportedOperationException();
    }

    /**
     * Since the graph is fixed, this method always throws an exception.
     * @throws UnsupportedOperationException always.
     */
    @Override
    public boolean removeNode(HostNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<HostNode> nodeSet() {
        if (this.nodeEdgeStore == null) {
            initData();
        }
        Set<HostNode> result = this.nodeEdgeStore.keySet();
        return ALIAS_SETS || this.copyData ? result : createNodeSet(result);
    }

    @Override
    public HostEdgeSet edgeSet() {
        if (this.edgeSet == null) {
            initData();
        }
        HostEdgeSet result = this.edgeSet;
        return ALIAS_SETS || this.copyData ? result : createEdgeSet(result);
    }

    @Override
    public HostEdgeSet inEdgeSet(Node node) {
        HostEdgeSet result = getInEdgeStore().get(node);
        return (ALIAS_SETS || this.copyData) && result != null ? result : createEdgeSet(result);
    }

    /** Returns a mapping from labels to sets of edges. */
    private HostEdgeStore<HostNode> getInEdgeStore() {
        if (this.nodeInEdgeStore == null) {
            initData();
            if (this.nodeInEdgeStore == null) {
                this.nodeInEdgeStore = computeInEdgeStore();
            }
        }
        return this.nodeInEdgeStore;
    }

    /**
     * Computes the node-to-incoming-edgeset map from the node and edge sets. This
     * method is only used if the map could not be obtained from the basis.
     */
    private HostEdgeStore<HostNode> computeInEdgeStore() {
        HostEdgeStore<HostNode> result = new HostEdgeStore<>();
        for (Map.Entry<HostNode,HostEdgeSet> nodeEdgeEntry : this.nodeEdgeStore.entrySet()) {
            HostNode key = nodeEdgeEntry.getKey();
            HostEdgeSet inEdges = createEdgeSet(null);
            for (HostEdge edge : nodeEdgeEntry.getValue()) {
                if (edge.target()
                    .equals(key)) {
                    inEdges.add(edge);
                }
            }
            result.put(key, inEdges);
        }
        return result;
    }

    @Override
    public HostEdgeSet outEdgeSet(Node node) {
        HostEdgeSet result = getOutEdgeStore().get(node);
        return (ALIAS_SETS || this.copyData) && result != null ? result : createEdgeSet(result);
    }

    /** Returns a mapping from nodes to sets of outgoing edges. */
    private HostEdgeStore<HostNode> getOutEdgeStore() {
        if (this.nodeOutEdgeStore == null) {
            initData();
            if (this.nodeOutEdgeStore == null) {
                this.nodeOutEdgeStore = computeOutEdgeStore();
            }
        }
        return this.nodeOutEdgeStore;
    }

    /**
     * Computes the node-to-incoming-edgeset map from the node and edge sets. This
     * method is only used if the map could not be obtained from the basis.
     */
    private HostEdgeStore<HostNode> computeOutEdgeStore() {
        HostEdgeStore<HostNode> result = new HostEdgeStore<>();
        for (Map.Entry<HostNode,HostEdgeSet> nodeEdgeEntry : this.nodeEdgeStore.entrySet()) {
            HostNode key = nodeEdgeEntry.getKey();
            HostEdgeSet inEdges = createEdgeSet(null);
            for (HostEdge edge : nodeEdgeEntry.getValue()) {
                if (edge.source()
                    .equals(key)) {
                    inEdges.add(edge);
                }
            }
            result.put(key, inEdges);
        }
        return result;
    }

    @Override
    public HostEdgeSet edgeSet(Label label) {
        HostEdgeSet result = getLabelEdgeStore().get(label);
        return (ALIAS_SETS || this.copyData) && result != null ? result : createEdgeSet(result);
    }

    /** Returns a mapping from labels to sets of edges. */
    private HostEdgeStore<TypeLabel> getLabelEdgeStore() {
        if (this.labelEdgeStore == null) {
            initData();
            if (this.labelEdgeStore == null) {
                this.labelEdgeStore = computeLabelEdgeStore();
            }
        }
        return this.labelEdgeStore;
    }

    /**
     * Computes the label-to-edgeset map from the node and edge sets. This
     * method is only used if the map could not be obtained from the basis.
     */
    private HostEdgeStore<@NonNull TypeLabel> computeLabelEdgeStore() {
        HostEdgeStore<@NonNull TypeLabel> result = new HostEdgeStore<>();
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
    public HostEdgeSet edgeSet(Node node) {
        HostEdgeSet result = getNodeEdgeStore().get(node);
        return (ALIAS_SETS || this.copyData) && result != null ? result : createEdgeSet(result);
    }

    /** Returns the mapping from nodes to sets of incident edges. */
    private HostEdgeStore<HostNode> getNodeEdgeStore() {
        if (this.nodeEdgeStore == null) {
            initData();
        }
        return this.nodeEdgeStore;
    }

    /**
     * Initialises all the data structures, if this has not yet been done.
     */
    private void initData() {
        if (!isDataInitialised()) {
            assert this.nodeEdgeStore == null;
            assert this.labelEdgeStore == null;
            if (this.basis == null) {
                this.edgeSet = createEdgeSet(null);
                this.nodeEdgeStore = new HostEdgeStore<>();
                // apply the delta to fill the structures;
                // the swing target actually shares this graph's structures
                this.delta.applyDelta(new SwingTarget());
            } else {
                // back up to the first initialised graph
                // or the first graph without a basis
                Stack<DeltaHostGraph> basisChain = new Stack<>();
                basisChain.push(this);
                DeltaHostGraph backward = this.basis;
                while (backward.basis != null && !backward.isDataInitialised()) {
                    basisChain.push(backward);
                    backward = backward.basis;
                }
                // now iteratively construct the intermediate graphs
                backward.initData();
                int deltaSize = 0;
                int totalDelta = 0;
                int chainLength = 0;
                while (!basisChain.isEmpty()) {
                    DeltaHostGraph forward = basisChain.pop();
                    DataTarget target = forward.basis.getDataTarget(chainLength, totalDelta);
                    if (target instanceof CopyTarget) {
                        deltaSize = 0;
                        totalDelta = 0;
                        chainLength = 0;
                    }
                    deltaSize += forward.delta.size();
                    totalDelta += deltaSize;
                    chainLength += 1;
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
     */
    private DataTarget getDataTarget(int chainLength, int totalDelta) {
        DataTarget result;
        // data should have been initialised
        assert isDataInitialised();
        if (exceedsCopyBound(chainLength, totalDelta)) {
            result = new CopyTarget(!this.copyData);
        } else {
            result = this.copyData ? new CopyTarget(false) : new SwingTarget();
        }
        return result;
    }

    /**
     * Indicates if a given combined delta size and/or chain length is large enough
     * to prefer copying the data structures over sharing.
     */
    private boolean exceedsCopyBound(int chainLength, int totalDelta) {
        return totalDelta > 2 * size() || chainLength > MAX_CHAIN_LENGTH;
    }

    /**
     * Creates a copy of an existing set of edges, or an empty set if the given
     * set is <code>null</code>.
     */
    HostEdgeSet createEdgeSet(Set<HostEdge> edgeSet) {
        return HostEdgeSet.newInstance(edgeSet);
    }

    HostNodeSet createNodeSet(Set<HostNode> nodeSet) {
        return HostNodeSet.newInstance(nodeSet);
    }

    @Override
    public boolean hasCertifier(boolean strong) {
        return this.certifier != null && this.certifier.get() != null;
    }

    @Override
    public CertificateStrategy getCertifier(boolean strong) {
        CertificateStrategy result = this.certifier == null ? null : this.certifier.get();
        if (result == null || result.getStrength() != strong) {
            result = AGraph.getCertificateFactory()
                .newInstance(this, strong);
            this.certifier = new WeakReference<>(result);
        }
        return result;
    }

    @Override
    protected boolean isTypeCorrect(Node node) {
        return node instanceof HostNode && getFactory().containsNode((HostNode) node);
    }

    @Override
    protected boolean isTypeCorrect(Edge edge) {
        return edge instanceof HostEdge && getFactory().containsEdge((HostEdge) edge);
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
    StoredDeltaApplier delta;

    /** The (initially null) edge set of this graph. */
    HostEdgeSet edgeSet;
    /** The map from nodes to sets of incident edges. */
    HostEdgeStore<HostNode> nodeEdgeStore;
    /** The map from nodes to sets of incoming edges. */
    HostEdgeStore<HostNode> nodeInEdgeStore;
    /** The map from nodes to sets of outgoing edges. */
    HostEdgeStore<HostNode> nodeOutEdgeStore;
    /** Mapping from labels to sets of edges with that label. */
    HostEdgeStore<@NonNull TypeLabel> labelEdgeStore;
    /** The certificate strategy of this graph, set on demand. */
    private Reference<CertificateStrategy> certifier;
    /**
     * Flag indicating that data should be copied rather than shared in
     * {@link #getDataTarget(int,int)}.
     */
    private boolean copyData = true;

    @Override
    public FormatErrorSet checkTypeConstraints() {
        return getTypeGraph().check(this);
    }

    /** Maximum basis chain length at which the data target is set
     * to a {@link CopyTarget} regardless of the value of {@link #copyData}.
     */
    static private final int MAX_CHAIN_LENGTH = 25;
    /**
     * Debug flag for aliasing the node and edge set. Aliasing the sets may give
     * {@link ConcurrentModificationException}s during matching.
     */
    static private final boolean ALIAS_SETS = true;
    /** Factory instance of this class, in which data is copied. */
    static private final DeltaHostGraph copyInstance =
        new DeltaHostGraph("copy prototype", (HostElement[]) null, null, true);
    /** Factory instance of this class, in which data is aliased. */
    static private final DeltaHostGraph swingInstance =
        new DeltaHostGraph("swing prototype", (HostElement[]) null, null, false);

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
            child.nodeEdgeStore = this.nodeEdgeStore;
            child.nodeInEdgeStore = this.nodeInEdgeStore;
            child.nodeOutEdgeStore = this.nodeOutEdgeStore;
            child.labelEdgeStore = this.labelEdgeStore;
            child.delta = null;
            child.basis = null;
        }

        /* Adds the node to the node set and the node-edge map. */
        @Override
        public boolean addNode(HostNode node) {
            boolean fresh = addKeyToStore(this.nodeEdgeStore, node);
            if (fresh) {
                addKeyToStore(this.nodeInEdgeStore, node);
                addKeyToStore(this.nodeOutEdgeStore, node);
            } else {
                assert node instanceof ValueNode : String.format("Node %s already occured in graph",
                    node);
            }
            return fresh;
        }

        /* Removes the node from the node set and the node-edge map. */
        @Override
        public boolean removeNode(HostNode node) {
            HostEdgeSet edges = removeKeyFromStore(this.nodeEdgeStore, node);
            assert edges != null : String.format("Node %s did not occur in graph", node);
            assert edges.isEmpty() : String.format("Node %s still had incident edges %s",
                node,
                edges);
            removeKeyFromStore(this.nodeOutEdgeStore, node);
            removeKeyFromStore(this.nodeInEdgeStore, node);
            return true;
        }

        /**
         * Adds an edge to all maps stored in this target,
         * if they are not {@code null}.
         */
        final boolean addEdge(HostEdge edge, boolean refreshSource, boolean refreshTarget,
            boolean refreshLabel) {
            boolean result = this.edgeSet.add(edge);
            assert result : String.format("Edge %s already occured in graph", edge);
            // adapt node-edge map
            HostNode source = edge.source();
            HostNode target = edge.target();
            addToEdgeToStore(this.nodeEdgeStore, source, edge, refreshSource);
            if (source != target) {
                addToEdgeToStore(this.nodeEdgeStore, target, edge, refreshTarget);
            }
            // adapt label-edge map
            addToEdgeToStore(this.nodeOutEdgeStore, source, edge, refreshSource);
            addToEdgeToStore(this.nodeInEdgeStore, target, edge, refreshTarget);
            addToEdgeToStore(this.labelEdgeStore, edge.label(), edge, refreshLabel);
            return result;
        }

        /**
         * Removes an edge from all maps stored in this target,
         * if they are not {@code null}.
         * A second parameter determines if the set sets
         * in the map should be copied upon modification.
         */
        final boolean removeEdge(HostEdge edge, boolean refreshSource, boolean refreshTarget,
            boolean refreshLabel) {
            boolean result = this.edgeSet.remove(edge);
            assert result : String.format("Edge %s did not occur in graph", edge);
            // adapt node-edge map
            HostNode source = edge.source();
            HostNode target = edge.target();
            removeEdgeFromStore(this.nodeEdgeStore, source, edge, refreshSource);
            if (source != target) {
                removeEdgeFromStore(this.nodeEdgeStore, target, edge, refreshTarget);
            }
            removeEdgeFromStore(this.nodeOutEdgeStore, source, edge, refreshSource);
            removeEdgeFromStore(this.nodeInEdgeStore, target, edge, refreshTarget);
            removeEdgeFromStore(this.labelEdgeStore, edge.label(), edge, refreshLabel);
            return result;
        }

        /**
         * Adds a key to a given key-to-edgeset mapping.
         * @param <T> the type of the key
         * @param map the mapping to be modified; may be {@code null}
         * @param key the key to be inserted
         * @return {@code true} if the key was indeed added to the map,
         * or the map was {@code null}
         */
        private <T> boolean addKeyToStore(HostEdgeStore<T> map, T key) {
            boolean result = true;
            if (map != null) {
                result = map.addKey(key);
            }
            return result;
        }

        /** Removes either a key from a given mapping,
         * if the mapping is not {@code null}.
         */
        private <T> HostEdgeSet removeKeyFromStore(HostEdgeStore<T> map, T key) {
            HostEdgeSet result = null;
            if (map != null) {
                result = map.remove(key);
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
        private <T> HostEdgeSet addToEdgeToStore(HostEdgeStore<T> map, T key, HostEdge edge,
            boolean refresh) {
            HostEdgeSet result = null;
            if (map != null) {
                result = map.addEdge(key, edge, refresh);
            }
            return result;
        }

        /** Removes an edge from a given mapping,
         * if the mapping is not {@code null}.
         */
        private <T> HostEdgeSet removeEdgeFromStore(HostEdgeStore<T> store, T key, HostEdge edge,
            boolean refresh) {
            HostEdgeSet result = null;
            if (store != null) {
                result = store.removeEdge(key, edge, refresh);
            }
            return result;
        }

        /** Edge set to be filled by this target. */
        HostEdgeSet edgeSet;
        /** Node/edge map to be filled by this target. */
        HostEdgeStore<HostNode> nodeEdgeStore;
        /** Node/incoming edge map to be filled by this target. */
        HostEdgeStore<HostNode> nodeInEdgeStore;
        /** Node/outgoing edge map to be filled by this target. */
        HostEdgeStore<HostNode> nodeOutEdgeStore;
        /** Label/edge map to be filled by this target. */
        HostEdgeStore<@NonNull TypeLabel> labelEdgeStore;
    }

    /** Delta target to initialise the data structures. */
    private class SwingTarget extends DataTarget {
        /** Constructs and instance for a given node and edge set. */
        public SwingTarget() {
            DeltaHostGraph graph = DeltaHostGraph.this;
            // only construct a node set if the node-edge map is not there. */
            this.edgeSet = graph.edgeSet;
            this.nodeEdgeStore = graph.nodeEdgeStore;
            this.nodeInEdgeStore = graph.nodeInEdgeStore;
            this.nodeOutEdgeStore = graph.nodeOutEdgeStore;
            this.labelEdgeStore = graph.labelEdgeStore;
        }

        /**
         * Adds the edge to the edge set, the node-edge map (if it is set), and
         * the label-edge maps (if it is set).
         */
        @Override
        public boolean addEdge(HostEdge edge) {
            return super.addEdge(edge, false, false, false);
        }

        /**
         * Removes the edge from the edge set, the node-edge map (if it is set),
         * and the label-edge maps (if it is set).
         */
        @Override
        public boolean removeEdge(HostEdge edge) {
            return super.removeEdge(edge, false, false, false);
        }

        @Override
        void install(DeltaHostGraph child) {
            DeltaHostGraph graph = DeltaHostGraph.this;
            graph.edgeSet = null;
            graph.nodeEdgeStore = null;
            graph.nodeInEdgeStore = null;
            graph.nodeOutEdgeStore = null;
            graph.labelEdgeStore = null;
            if (graph.delta == null) {
                graph.basis = child;
                graph.delta = ((DeltaStore) child.delta).invert(true);
            }
            super.install(child);
        }
    }

    /** Delta target to initialise the data structures. */
    private class CopyTarget extends DataTarget {
        /**
         * Constructs and instance for a given node and edge set.
         * @param deepCopy if {@code true}, the maps are completely copied;
         * otherwise, the image maps are shared. Deep copying is necessary if
         * the {@link CopyTarget} is used in combination with {@link SwingTarget}s
         */
        public CopyTarget(boolean deepCopy) {
            DeltaHostGraph graph = DeltaHostGraph.this;
            this.edgeSet = createEdgeSet(graph.edgeSet);
            this.nodeEdgeStore = copy(graph.nodeEdgeStore, deepCopy);
            this.freshSourceKeys = createNodeSet(deepCopy ? this.nodeEdgeStore.keySet() : null);
            this.freshTargetKeys = createNodeSet(deepCopy ? this.nodeEdgeStore.keySet() : null);
            if (graph.labelEdgeStore != null) {
                this.labelEdgeStore = copy(graph.labelEdgeStore, deepCopy);
                this.freshLabelKeys = new HashSet<>();
                if (deepCopy) {
                    this.freshLabelKeys.addAll(this.labelEdgeStore.keySet());
                }
            } else {
                this.freshLabelKeys = null;
            }
            if (graph.nodeInEdgeStore != null) {
                this.nodeInEdgeStore = copy(graph.nodeInEdgeStore, deepCopy);
            }
            if (graph.nodeOutEdgeStore != null) {
                this.nodeOutEdgeStore = copy(graph.nodeOutEdgeStore, deepCopy);
            }
        }

        private <K> HostEdgeStore<K> copy(HostEdgeStore<K> source, boolean deepCopy) {
            return new HostEdgeStore<>(source, deepCopy);
        }

        /**
         * Adds the edge to the edge set, the node-edge map (if it is set), and
         * the label-edge maps (if it is set).
         */
        @Override
        public boolean addEdge(HostEdge edge) {
            HostNode source = edge.source();
            HostNode target = edge.target();
            boolean refreshSource = this.freshSourceKeys.add(source);
            boolean refreshTarget = this.freshTargetKeys.add(target);
            boolean refreshLabel =
                this.freshLabelKeys != null && this.freshLabelKeys.add(edge.label());
            return super.addEdge(edge, refreshSource, refreshTarget, refreshLabel);
        }

        /**
         * Removes the edge from the edge set, the node-edge map (if it is set),
         * and the label-edge maps (if it is set).
         */
        @Override
        public boolean removeEdge(HostEdge edge) {
            HostNode source = edge.source();
            HostNode target = edge.target();
            boolean refreshSource = this.freshSourceKeys.add(source);
            boolean refreshTarget = this.freshTargetKeys.add(target);
            boolean refreshLabel =
                this.freshLabelKeys != null && this.freshLabelKeys.add(edge.label());
            return super.removeEdge(edge, refreshSource, refreshTarget, refreshLabel);
        }

        /** Auxiliary set to determine the source nodes changed w.r.t. the basis. */
        private final HostNodeSet freshSourceKeys;
        /** Auxiliary set to determine the source nodes changed w.r.t. the basis. */
        private final HostNodeSet freshTargetKeys;
        /** Auxiliary set to determine the labels changed w.r.t. the basis. */
        private final Set<TypeLabel> freshLabelKeys;
    }
}
