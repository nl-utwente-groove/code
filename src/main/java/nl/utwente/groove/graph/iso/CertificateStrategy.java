/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.graph.iso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Reporter;

/**
 * Interface for algorithms to compute isomorphism certificates for a given
 * graph, i.e., a predictor for graph isomorphism. Two graphs are isomorphic
 * only if their certificates are equal (as determined by
 * <tt>equals(Object)</tt>). A certificate strategy is specialised to a graph
 * upon which it works; this is set at creation time.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
abstract public class CertificateStrategy {
    CertificateStrategy(@Nullable Graph graph) {
        this.graph = graph;
    }

    /**
     * Returns the underlying graph for which this is the certificate strategy.
     * @return the underlying graph
     */
    public Graph getGraph() {
        var result = this.graph;
        assert result != null : "Prototype certificate strategy has no graph";
        return result;
    }

    /**
     * Method to compute the isomorphism certificate for the underlying graph.
     * @return the isomorphism certificate for the underlying graph.
     */
    public Object getGraphCertificate() {
        if (TRACE) {
            System.err.printf("Computing graph certificate%n");
        }
        // check if the certificate has been computed before
        if (this.graphCertificate == 0) {
            computeCertificates();
            if (this.graphCertificate == 0) {
                this.graphCertificate = 1;
            }
        }
        if (TRACE) {
            System.err.printf("Graph certificate: %d%n", this.graphCertificate);
        }
        // return the computed certificate
        return this.graphCertificate;
    }

    /**
     * Tests if the edge bundles of the underlying graph have been computed.
     * @see #getEdgeBundles()
     */
    boolean hasEdgeBundles() {
        return this.edgeBundles != null;
    }

    /**
     * Returns the edges of the underlying graph, grouped into bundles of
     * parallel copies. For a non-simple graph the bundles are computed as part
     * of {@link #initCertificates()}, since the certificates are assigned to
     * bundles rather than to individual edges; otherwise they are computed on
     * demand. Retrieving them does not trigger the computation of the
     * certificates.
     */
    EdgeBundles getEdgeBundles() {
        var result = this.edgeBundles;
        if (result == null) {
            this.edgeBundles = result = EdgeBundles.newInstance(getGraph());
        }
        return result;
    }

    /**
     * Returns the parallel copies of a given edge of the underlying graph,
     * being the edges represented by that edge's certificate. Only defined if
     * the certificate has a multiplicity larger than one.
     * @see EdgeCertificate#getMultiplicity()
     */
    Edge[] getCopies(Edge edge) {
        return getEdgeBundles().getCopies(edge);
    }

    /** The pre-computed edge bundles, if any. */
    private @Nullable EdgeBundles edgeBundles;

    /** Returns the node certificates calculated for the graph. */
    public NodeCertificate[] getNodeCertificates() {
        if (this.nodeCerts == null) {
            computeCertificates();
        }
        var result = this.nodeCerts;
        assert result != null;
        return result;
    }

    /** Returns the edge certificates calculated for the graph. */
    public EdgeCertificate[] getEdgeCertificates() {
        if (this.edgeCerts == null) {
            computeCertificates();
        }
        var result = this.edgeCerts;
        assert result != null;
        return result;
    }

    /** Computes the node and edge certificate arrays. */
    void computeCertificates() {
        // we compute the certificate map
        computeCertReporter.start();
        initCertificates();
        iterateCertificates();
        computeCertReporter.stop();
    }

    /** Iterates and so finishes the computation of the certificates. */
    abstract void iterateCertificates();

    /**
     * Initialises the node and edge certificate arrays, and the certificate
     * map.
     */
    void initCertificates() {
        // the following two calls are not profiled, as it
        // is likely that this results in the actual graph construction
        int nodeCount = getGraph().nodeCount();
        int edgeCount = getGraph().edgeCount();
        this.nodeCerts = new NodeCertificate[nodeCount];
        // the table is at least twice the number of nodes, to keep the
        // linear probing short
        int capacity = Integer.highestOneBit(Math.max(nodeCount, 2)) * 4;
        this.nodeCertTable = new int[capacity];
        this.nodeCertMask = capacity - 1;
        for (Node node : getGraph().nodeSet()) {
            initNodeCert(node);
        }
        // create the edge certificates
        if (getGraph().isSimple()) {
            this.edgeCerts = new EdgeCertificate[edgeCount];
            for (Edge edge : getGraph().edgeSet()) {
                initEdgeCert(edge, 1);
            }
        } else {
            // parallel copies are interchangeable, and provably carry equal
            // certificate values at every iteration, so they share a single
            // certificate that records how many of them there are (gh #906)
            var bundles = getEdgeBundles();
            int bundleCount = bundles.size();
            this.edgeCerts = new EdgeCertificate[bundleCount];
            for (int i = 0; i < bundleCount; i++) {
                initEdgeCert(bundles.getRepresentative(i), bundles.getCount(i));
            }
        }
    }

    /**
     * Creates a {@link NodeCertificate} for a given graph node, and inserts
     * into the certificate node map.
     */
    private NodeCertificate initNodeCert(final Node node) {
        NodeCertificate nodeCert;
        // a node that is completely identified by its certificate seed
        // (such as a data value node) gets a dedicated certificate that
        // compares by seed only
        if (node.hasIdentityCertificate()) {
            nodeCert = createIdentityNodeCertificate(node);
        } else {
            nodeCert = createNodeCertificate(node);
        }
        putNodeCert(nodeCert);
        var nodeCerts = this.nodeCerts;
        assert nodeCerts != null;
        nodeCerts[this.nodeCertCount] = nodeCert;
        this.nodeCertCount++;
        return nodeCert;
    }

    /**
     * Enters the certificate about to be stored at index {@link #nodeCertCount}
     * of {@link #nodeCerts} into the lookup table, under its node's number.
     */
    private void putNodeCert(NodeCertificate nodeCert) {
        var nodeCertTable = this.nodeCertTable;
        assert nodeCertTable != null;
        int slot = nodeCertSlot(nodeCert.getElement().getNumber());
        while (nodeCertTable[slot] != 0) {
            slot = (slot + 1) & this.nodeCertMask;
        }
        nodeCertTable[slot] = this.nodeCertCount + 1;
    }

    /**
     * Retrieves the certificate of a given graph node from the lookup table.
     */
    NodeCertificate getNodeCert(final Node node) {
        var nodeCertTable = this.nodeCertTable;
        var nodeCerts = this.nodeCerts;
        assert nodeCertTable != null && nodeCerts != null;
        int nodeNr = node.getNumber();
        int slot = nodeCertSlot(nodeNr);
        while (true) {
            int entry = nodeCertTable[slot];
            assert entry != 0 : String.format("Could not find certificate for %s", node);
            NodeCertificate result = nodeCerts[entry - 1];
            if (result.getElement().getNumber() == nodeNr) {
                return result;
            }
            slot = (slot + 1) & this.nodeCertMask;
        }
    }

    /** Returns the initial slot of a node number in {@link #nodeCertTable}. */
    @AIGenerated("Claude Opus 5.5, 2026-09")
    private int nodeCertSlot(int nodeNr) {
        // spread the (typically consecutive) node numbers over the table
        int hash = nodeNr * 0x9E3779B9;
        return (hash ^ (hash >>> 16)) & this.nodeCertMask;
    }

    /**
     * Creates an {@link EdgeCertificate} for a given bundle of parallel edges,
     * identified by one of its copies, and inserts it into the certificate
     * edge map.
     * @param edge a representative of the bundle
     * @param multiplicity the number of parallel copies in the bundle
     */
    private void initEdgeCert(Edge edge, int multiplicity) {
        var edgeCerts = this.edgeCerts;
        assert edgeCerts != null;
        Node source = edge.source();
        NodeCertificate sourceCert = getNodeCert(source);
        assert sourceCert != null : String.format("No source certifiate found for %s", edge);
        if (source == edge.target()) {
            EdgeCertificate edge1Cert = createEdge1Certificate(edge, sourceCert, multiplicity);
            edgeCerts[edgeCerts.length - this.edge1CertCount - 1] = edge1Cert;
            this.edge1CertCount++;
            assert this.edge1CertCount + this.edge2CertCount <= edgeCerts.length : String
                .format("%s unary and %s binary edges do not equal %s edges", this.edge1CertCount,
                        this.edge2CertCount, edgeCerts.length);
        } else {
            NodeCertificate targetCert = getNodeCert(edge.target());
            assert targetCert != null : String.format("No target certifiate found for %s", edge);
            EdgeCertificate edge2Cert
                = createEdge2Certificate(edge, sourceCert, targetCert, multiplicity);
            edgeCerts[this.edge2CertCount] = edge2Cert;
            this.edge2CertCount++;
            assert this.edge1CertCount + this.edge2CertCount <= edgeCerts.length : String
                .format("%s unary and %s binary edges do not equal %s edges", this.edge1CertCount,
                        this.edge2CertCount, edgeCerts.length);
        }
    }

    /** Factory method for the certificate of a node with an identity
     * certificate (see {@link Node#hasIdentityCertificate()}). */
    abstract NodeCertificate createIdentityNodeCertificate(Node node);

    abstract NodeCertificate createNodeCertificate(Node node);

    /** Factory method for the certificate of a bundle of parallel unary edges.
     * @param edge a representative of the bundle
     * @param multiplicity the number of parallel copies in the bundle */
    abstract EdgeCertificate createEdge1Certificate(Edge edge,
                                                    nl.utwente.groove.graph.iso.CertificateStrategy.NodeCertificate source,
                                                    int multiplicity);

    /** Factory method for the certificate of a bundle of parallel binary edges.
     * @param edge a representative of the bundle
     * @param multiplicity the number of parallel copies in the bundle */
    abstract EdgeCertificate createEdge2Certificate(Edge edge,
                                                    nl.utwente.groove.graph.iso.CertificateStrategy.NodeCertificate source,
                                                    nl.utwente.groove.graph.iso.CertificateStrategy.NodeCertificate target,
                                                    int multiplicity);

    /**
     * Returns a map from graph elements to certificates for the underlying
     * graph. Two elements from different graphs may only be joined by
     * isomorphism if their certificates are equal.
     * The result is computed by first initialising arrays of certificates and
     * subsequently iterating over those arrays until the number of distinct
     * certificate values does not grow any more. Each iteration first
     * recomputes the edge certificates using the current node certificate
     * values, and then the node certificates using the current edge certificate
     * values.
     */
    public Map<Element,ElementCertificate<?>> getCertificateMap() {
        // check if the map has been computed before
        var result = this.certificateMap;
        if (result == null) {
            getGraphCertificate();
            this.certificateMap = result = new HashMap<>();
            // add the node certificates to the certificate map
            for (NodeCertificate nodeCert : getNodeCertificates()) {
                result.put(nodeCert.getElement(), nodeCert);
            }
            // add the edge certificates to the certificate map;
            // every parallel copy is a key, mapped to its bundle's certificate
            for (EdgeCertificate edgeCert : getEdgeCertificates()) {
                if (edgeCert.getMultiplicity() == 1) {
                    result.put(edgeCert.getElement(), edgeCert);
                } else {
                    for (Edge edge : getCopies(edgeCert.getElement())) {
                        result.put(edge, edgeCert);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns a map from node certificates to sets of nodes of the underlying
     * graph. This is the reverse of {@link #getCertificateMap()}, specialised
     * to nodes. Two nodes from different graphs may only be joined by
     * isomorphism if their certificates are equal; i.e., if they are in the
     * image of the same certificate.
     */
    public PartitionMap<NodeCertificate> getNodePartitionMap() {
        // check if the map has been computed before
        var result = this.nodePartitionMap;
        if (result == null) {
            // no; go ahead and compute it
            getGraphCertificate();
            this.nodePartitionMap = result = computeNodePartitionMap();
        }
        return result;
    }

    /**
     * Computes the partition map, i.e., the mapping from certificates to sets
     * of graph elements having those certificates.
     */
    private PartitionMap<NodeCertificate> computeNodePartitionMap() {
        getPartitionReporter.start();
        PartitionMap<NodeCertificate> result = new PartitionMap<>();
        // invert the certificate map
        for (NodeCertificate cert : getNodeCertificates()) {
            result.add(cert);
        }
        getPartitionReporter.stop();
        return result;
    }

    /**
     * Returns a map from edge certificates to sets of edges of the underlying
     * graph. This is the reverse of {@link #getCertificateMap()}, specialised
     * to edges. Two edges from different graphs may only be joined by
     * isomorphism if their certificates are equal; i.e., if they are in the
     * image of the same certificate.
     */
    public PartitionMap<EdgeCertificate> getEdgePartitionMap() {
        // check if the map has been computed before
        var result = this.edgePartitionMap;
        if (result == null) {
            // no; go ahead and compute it
            getGraphCertificate();
            this.edgePartitionMap = result = computeEdgePartitionMap();
        }
        return result;
    }

    /**
     * Computes the partition map, i.e., the mapping from certificates to sets
     * of graph elements having those certificates.
     */
    private PartitionMap<EdgeCertificate> computeEdgePartitionMap() {
        getPartitionReporter.start();
        PartitionMap<EdgeCertificate> result = new PartitionMap<>();
        // invert the certificate map
        var edgeCerts = getEdgeCertificates();
        int bound = edgeCerts.length;
        for (int i = 0; i < bound; i++) {
            result.add(edgeCerts[i]);
        }
        getPartitionReporter.stop();
        return result;
    }

    /**
     * Returns the number of (node) certificates occurring as targets in the
     * certificate map.
     * @return <code>getPartitionMap().size()</code>
     */
    abstract public int getNodePartitionCount();

    /**
     * Factory method; returns a certificate strategy for a given graph.
     * @param graph the underlying graph for the new certificate strategy.
     * @param strong if <code>true</code>, a strong certifier is created.
     * @return a fresh certificate strategy for <tt>graph</tt>
     * @see #getStrength()
     */
    abstract public CertificateStrategy newInstance(Graph graph, boolean strong);

    /**
     * Returns the strength of the strategy:
     * A strong strategy will spend more effort in avoiding false negatives.
     */
    abstract public boolean getStrength();

    /** The graph for which certificates are to be computed. */
    private final @Nullable Graph graph;

    /** The pre-computed graph certificate, if any. */
    long graphCertificate;
    /** The pre-computed certificate map, if any. */
    @Nullable Map<Element,ElementCertificate<?>> certificateMap;
    /** The pre-computed node partition map, if any. */
    @Nullable PartitionMap<NodeCertificate> nodePartitionMap;
    /** The pre-computed edge partition map, if any. */
    @Nullable PartitionMap<EdgeCertificate> edgePartitionMap;

    /**
     * The list of node certificates in this bisimulator.
     */
    NodeCertificate @Nullable [] nodeCerts;
    /** The number of elements in {@link #nodeCerts}. */
    int nodeCertCount;
    /**
     * The list of edge certificates in this bisimulator. The array consists of
     * {@link #edge2CertCount} certificates for binary edges, followed by
     * {@link #edge1CertCount} certificates for unary edges.
     */
    EdgeCertificate @Nullable [] edgeCerts;
    /** The number of binary edge certificates in {@link #edgeCerts}. */
    int edge2CertCount;
    /** The number of unary edge certificates in {@link #edgeCerts}. */
    int edge1CertCount;
    /**
     * Open-addressed lookup table from node numbers to certificates, filled in
     * {@link #initCertificates()}: each slot holds an index into
     * {@link #nodeCerts}, raised by one so that zero stands for an empty slot.
     * Sized by the graph's node count rather than by the factory's node
     * numbers, which grow with every node the exploration ever creates.
     */
    private int @Nullable [] nodeCertTable;
    /** Mask of the capacity of {@link #nodeCertTable}, a power of two. */
    private int nodeCertMask;

    /**
     * Returns an array that, at every index, contains the number of times that
     * the computation of certificates has taken a number of iterations equal to
     * the index.
     */
    static public List<Integer> getIterateCount() {
        List<Integer> result = new ArrayList<>();
        for (int element : iterateCountArray) {
            result.add(element);
        }
        return result;
    }

    /**
     * Records that the computation of the certificates has taken a certain
     * number of iterations.
     * @param count the number of iterations
     */
    static void recordIterateCount(int count) {
        if (iterateCountArray.length < count + 1) {
            int[] newIterateCount = new int[count + 1];
            System.arraycopy(iterateCountArray, 0, newIterateCount, 0, iterateCountArray.length);
            iterateCountArray = newIterateCount;
        }
        iterateCountArray[count]++;
    }

    /**
     * Array to record the number of iterations done in computing certificates.
     */
    static private int[] iterateCountArray = {};

    /** Flag to turn on System.out-tracing. */
    static final boolean TRACE = false;

    // --------------------------- reporter definitions ---------------------
    /** Reporter instance to profile methods of this class. */
    static public final Reporter reporter = IsoChecker.reporter;
    /** Handle to profile {@link #computeCertificates()}. */
    static public final Reporter computeCertReporter = reporter.register("computeCertificates()");
    /** Handle to profile {@link #getNodePartitionMap()}. */
    static protected final Reporter getPartitionReporter = reporter.register("getPartitionMap()");

    /**
     * Type of the certificates constructed by the strategy. A value of this
     * type represents a part of the graph structure in an isomorphism-invariant
     * way. Hence, equality of certificates does not imply equality of the
     * corresponding graph elements.
     */
    static public interface Certificate {
        /** Returns the current value of the certificate. */
        public int getValue();

        /** Adds a further number to the certificate value. */
        public void modifyValue(int mod);
    }

    /**
     * Certificate representing a graph element
     */
    static public interface ElementCertificate<EL extends Element> extends Certificate {
        /** Returns the element for which this is a certificate. */
        EL getElement();
    }

    /** Specialised certificate for nodes. */
    static public interface NodeCertificate extends ElementCertificate<Node> {
        // no added functionality
    }

    /**
     * Specialised certificate for edges. An edge certificate stands for a
     * bundle of parallel edges, of which {@link #getElement()} returns a
     * representative; only in a non-simple graph can a bundle have more than
     * one member.
     */
    static public interface EdgeCertificate extends ElementCertificate<Edge> {
        /**
         * Returns the number of parallel edges for which this is the
         * certificate; at least one. The edges themselves are obtained through
         * {@link CertificateStrategy#getCopies(Edge)}.
         */
        int getMultiplicity();
    }
}
