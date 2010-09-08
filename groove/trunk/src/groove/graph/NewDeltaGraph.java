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
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
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
        DeltaGraphFactory<NewDeltaGraph> {
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
     * Since the result should be modifiable, returns a {@link DeltaGraph}.
     */
    @Override
    public Graph clone() {
        return new DeltaGraph(this);
    }

    /**
     * Since the result should be modifiable, returns a {@link DeltaGraph}.
     */
    public DeltaGraph newGraph() {
        return new DeltaGraph();
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
    public Set<DefaultEdge> labelEdgeSet(int arity, Label label) {
        DefaultEdgeSet result = labelEdgeMap(arity).get(label);
        return (ALIAS_SETS || this.copyData) && result != null ? result
                : createEdgeSet(result);
    }

    @Override
    protected Map<Label,DefaultEdgeSet> labelEdgeMap(int i) {
        return getLabelEdgeMaps().get(i);
    }

    @Override
    protected List<Map<Label,DefaultEdgeSet>> getLabelEdgeMaps() {
        if (this.labelEdgeMaps == null) {
            initData();
            if (this.labelEdgeMaps == null) {
                this.labelEdgeMaps = computeLabelEdgeMaps();
            }
        }
        return this.labelEdgeMaps;
    }

    /**
     * Computes the label-to-edgeset map from the node and edge sets. This
     * method is only used if the map could not be obtained from the basis.
     */
    private List<Map<Label,DefaultEdgeSet>> computeLabelEdgeMaps() {
        List<Map<Label,DefaultEdgeSet>> result =
            new ArrayList<Map<Label,DefaultEdgeSet>>();
        result.add(null);
        for (int i = 0; i <= Edge.END_COUNT; i++) {
            result.add(new LinkedHashMap<Label,DefaultEdgeSet>());
        }
        for (DefaultEdge edge : edgeSet()) {
            Map<Label,DefaultEdgeSet> labelEdgeMap =
                result.get(edge.endCount());
            DefaultEdgeSet edges = labelEdgeMap.get(edge.label());
            if (edges == null) {
                labelEdgeMap.put(edge.label(), edges = createEdgeSet(null));
            }
            edges.add(edge);
        }
        return result;
    }

    @Override
    public Map<Node,DefaultEdgeSet> nodeEdgeMap() {
        if (this.nodeEdgeMap == null) {
            initData();
            // if (nodeEdgeMap == null) {
            // nodeEdgeMap = computeNodeEdgeMap();
            // }
        }
        return this.nodeEdgeMap;
    }

    @Override
    public Set<? extends Edge> edgeSet(Node node) {
        DefaultEdgeSet result = nodeEdgeMap().get(node);
        return (ALIAS_SETS || this.copyData) && result != null ? result
                : createEdgeSet(result);
    }

    /**
     * Initialises all the data structures, if this has not yet been done.
     */
    private void initData() {
        if (!isDataInitialised()) {
            assert this.nodeEdgeMap == null;
            assert this.labelEdgeMaps == null;
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
    // /** The (initially null) node set of this graph. */
    // private NodeSet nodeSet;
    /** The map from nodes to sets of incident edges. */
    Map<Node,DefaultEdgeSet> nodeEdgeMap;
    /** List of maps from labels to sets of edges with that label and arity. */
    List<Map<Label,DefaultEdgeSet>> labelEdgeMaps;
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
    static abstract private class DataTarget implements DeltaTarget {
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
            child.labelEdgeMaps = this.labelEdgeMaps;
            child.delta = null;
            child.basis = null;
        }

        /** Edge set to be filled by this target. */
        DefaultEdgeSet edgeSet;
        /** Node/edge map to be filled by this target. */
        Map<Node,DefaultEdgeSet> nodeEdgeMap;
        /** Label/edge map to be filled by this target. */
        List<Map<Label,DefaultEdgeSet>> labelEdgeMaps;
    }

    /** Delta target to initialise the data structures. */
    private class SwingTarget extends DataTarget {
        /** Constructs and instance for a given node and edge set. */
        public SwingTarget() {
            NewDeltaGraph graph = NewDeltaGraph.this;
            // only construct a node set if the node-edge map is not there. */
            this.edgeSet = graph.edgeSet;
            this.nodeEdgeMap = graph.nodeEdgeMap;
            this.labelEdgeMaps = graph.labelEdgeMaps;
        }

        /**
         * Adds the edge to the edge set, the node-edge map (if it is set), and
         * the label-edge maps (if it is set).
         */
        public boolean addEdge(Edge elem) {
            boolean result = this.edgeSet.add((DefaultEdge) elem);
            assert result;
            int arity = elem.endCount();
            // adapt node-edge map
            DefaultEdgeSet outEdgeSet = this.nodeEdgeMap.get(elem.source());
            if (outEdgeSet == null) {
                this.nodeEdgeMap.put(elem.source(), outEdgeSet =
                    createEdgeSet(null));
            }
            outEdgeSet.add((DefaultEdge) elem);
            if (elem.source() != elem.opposite()) {
                DefaultEdgeSet inEdgeSet =
                    this.nodeEdgeMap.get(elem.opposite());
                if (inEdgeSet == null) {
                    this.nodeEdgeMap.put(elem.opposite(), inEdgeSet =
                        createEdgeSet(null));
                }
                inEdgeSet.add((DefaultEdge) elem);
            }
            // adapt label-edge map
            if (this.labelEdgeMaps != null) {
                Label label = elem.label();
                Map<Label,DefaultEdgeSet> arityLabelEdgeMap =
                    this.labelEdgeMaps.get(arity);
                DefaultEdgeSet edgeSet = arityLabelEdgeMap.get(label);
                if (edgeSet == null) {
                    arityLabelEdgeMap.put(label, edgeSet =
                        createEdgeSet(edgeSet));
                }
                edgeSet.add((DefaultEdge) elem);
            }
            return result;
        }

        /** Adds the node to the node set and the node-edge map. */
        public boolean addNode(Node elem) {
            Set<DefaultEdge> edges =
                this.nodeEdgeMap.put(elem, createEdgeSet(null));
            assert edges == null;
            return true;
        }

        /**
         * Removes the edge from the edge set, the node-edge map (if it is set),
         * and the label-edge maps (if it is set).
         */
        public boolean removeEdge(Edge elem) {
            boolean result = this.edgeSet.remove(elem);
            assert result;
            int arity = elem.endCount();
            // adapt node-edge map
            this.nodeEdgeMap.get(elem.source()).remove(elem);
            if (elem.source() != elem.opposite()) {
                this.nodeEdgeMap.get(elem.opposite()).remove(elem);
            }
            // adapt label-edge map
            if (this.labelEdgeMaps != null) {
                Label label = elem.label();
                this.labelEdgeMaps.get(arity).get(label).remove(elem);
            }
            return result;
        }

        /** Removes the node from the node set and the node-edge map. */
        public boolean removeNode(Node elem) {
            Set<DefaultEdge> edges = this.nodeEdgeMap.remove(elem);
            assert edges.isEmpty();
            return true;
        }

        @Override
        void install(NewDeltaGraph child) {
            NewDeltaGraph graph = NewDeltaGraph.this;
            graph.edgeSet = null;
            graph.nodeEdgeMap = null;
            graph.labelEdgeMaps = null;
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
            if (graph.labelEdgeMaps != null) {
                this.labelEdgeMaps = new ArrayList<Map<Label,DefaultEdgeSet>>();
                this.freshLabelKeys = new ArrayList<Set<Label>>();
                for (Map<Label,DefaultEdgeSet> arityLabelEdgeMap : graph.labelEdgeMaps) {
                    if (arityLabelEdgeMap == null) {
                        this.labelEdgeMaps.add(null);
                        this.freshLabelKeys.add(null);
                    } else {
                        this.labelEdgeMaps.add(new LinkedHashMap<Label,DefaultEdgeSet>(
                            arityLabelEdgeMap));
                        this.freshLabelKeys.add(new HashSet<Label>());
                    }
                }
            } else {
                this.freshLabelKeys = null;
            }
        }

        /**
         * Adds the edge to the edge set, the node-edge map (if it is set), and
         * the label-edge maps (if it is set).
         */
        public boolean addEdge(Edge elem) {
            boolean result = this.edgeSet.add((DefaultEdge) elem);
            assert result;
            int arity = elem.endCount();
            // adapt node-edge map
            Node source = elem.source();
            Node opposite = elem.opposite();
            DefaultEdgeSet outEdgeSet = this.nodeEdgeMap.get(source);
            if (this.freshNodeKeys.add(source)) {
                this.nodeEdgeMap.put(source, outEdgeSet =
                    createEdgeSet(outEdgeSet));
            }
            outEdgeSet.add((DefaultEdge) elem);
            if (source != opposite) {
                DefaultEdgeSet inEdgeSet = this.nodeEdgeMap.get(opposite);
                if (this.freshNodeKeys.add(opposite)) {
                    this.nodeEdgeMap.put(opposite, inEdgeSet =
                        createEdgeSet(inEdgeSet));
                }
                inEdgeSet.add((DefaultEdge) elem);
            }
            // adapt label-edge map
            if (this.labelEdgeMaps != null) {
                Label label = elem.label();
                Map<Label,DefaultEdgeSet> arityLabelEdgeMap =
                    this.labelEdgeMaps.get(arity);
                DefaultEdgeSet edgeSet = arityLabelEdgeMap.get(label);
                Set<Label> freshArityLabelKeys = this.freshLabelKeys.get(arity);
                if (freshArityLabelKeys.add(label)) {
                    arityLabelEdgeMap.put(label, edgeSet =
                        createEdgeSet(edgeSet));
                }
                edgeSet.add((DefaultEdge) elem);
            }
            return result;
        }

        /** Adds the node to the node set and the node-edge map. */
        public boolean addNode(Node elem) {
            DefaultEdgeSet edges =
                this.nodeEdgeMap.put(elem, createEdgeSet(null));
            assert edges == null : String.format(
                "Node %s already has incident edges %s", elem, edges);
            this.freshNodeKeys.add(elem);
            return true;
        }

        /**
         * Removes the edge from the edge set, the node-edge map (if it is set),
         * and the label-edge maps (if it is set).
         */
        public boolean removeEdge(Edge elem) {
            boolean result = this.edgeSet.remove(elem);
            assert result;
            int arity = elem.endCount();
            // adapt node-edge map
            Node source = elem.source();
            Node opposite = elem.opposite();
            DefaultEdgeSet outEdgeSet = this.nodeEdgeMap.get(source);
            if (this.freshNodeKeys.add(source)) {
                this.nodeEdgeMap.put(source, outEdgeSet =
                    createEdgeSet(outEdgeSet));
            }
            outEdgeSet.remove(elem);
            if (source != opposite) {
                DefaultEdgeSet inEdgeSet = this.nodeEdgeMap.get(opposite);
                if (this.freshNodeKeys.add(opposite)) {
                    this.nodeEdgeMap.put(opposite, inEdgeSet =
                        createEdgeSet(inEdgeSet));
                }
                inEdgeSet.remove(elem);
            }
            // adapt label-edge map
            if (this.labelEdgeMaps != null) {
                Label label = elem.label();
                Map<Label,DefaultEdgeSet> arityLabelEdgeMap =
                    this.labelEdgeMaps.get(arity);
                DefaultEdgeSet labelEdgeSet = arityLabelEdgeMap.get(label);
                Set<Label> freshArityLabelKeys = this.freshLabelKeys.get(arity);
                if (freshArityLabelKeys.add(label)) {
                    arityLabelEdgeMap.put(label, labelEdgeSet =
                        createEdgeSet(labelEdgeSet));
                }
                labelEdgeSet.remove(elem);
            }
            return result;
        }

        /** Removes the node from the node set and the node-edge map. */
        public boolean removeNode(Node elem) {
            DefaultEdgeSet edges = this.nodeEdgeMap.remove(elem);
            assert edges.isEmpty() : String.format(
                "Removed node %s still has incident edges %s", elem, edges);
            this.freshNodeKeys.remove(elem);
            return true;
        }

        /** Auxiliary set to determine the nodes changed w.r.t. the basis. */
        private final Set<Node> freshNodeKeys;
        /** Auxiliary set to determine the labels changed w.r.t. the basis. */
        private final List<Set<Label>> freshLabelKeys;
    }
}
