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
package groove.graph.iso;

import groove.grammar.host.ValueNode;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.plain.PlainNode;
import groove.util.Reporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface for algorithms to compute isomorphism certificates for a given
 * graph, i.e., a predictor for graph isomorphism. Two graphs are isomorphic
 * only if their certificates are equal (as determined by
 * <tt>equals(Object)</tt>). A certificate strategy is specialised to a graph
 * upon which it works; this is set at creation time.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class CertificateStrategy {
    CertificateStrategy(Graph graph) {
        this.graph = graph;
        // the graph may be null if a prototype is being constructed.
        if (graph != null) {
            this.defaultNodeCerts = new NodeCertificate[graph.getFactory().getMaxNodeNr() + 1];
        } else {
            this.defaultNodeCerts = null;
        }
    }

    /**
     * Returns the underlying graph for which this is the certificate strategy.
     * @return the underlying graph
     */
    public Graph getGraph() {
        return this.graph;
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

    /** Returns the node certificates calculated for the graph. */
    public NodeCertificate[] getNodeCertificates() {
        if (this.nodeCerts == null) {
            computeCertificates();
        }
        return this.nodeCerts;
    }

    /** Returns the edge certificates calculated for the graph. */
    public EdgeCertificate[] getEdgeCertificates() {
        if (this.edgeCerts == null) {
            computeCertificates();
        }
        return this.edgeCerts;
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
        this.edgeCerts = new EdgeCertificate[edgeCount];
        // create the edge certificates
        for (Node node : getGraph().nodeSet()) {
            initNodeCert(node);
        }
        for (Edge edge : getGraph().edgeSet()) {
            initEdgeCert(edge);
        }
    }

    /**
     * Creates a {@link NodeCertificate} for a given graph node, and inserts
     * into the certificate node map.
     */
    private NodeCertificate initNodeCert(final Node node) {
        NodeCertificate nodeCert;
        // if the node is an instance of OperationNode, the certificate
        // of this node also depends on the operation represented by it
        // therefore, the computeNewValue()-method of class
        // CertificateNode must be overridden
        if (node instanceof ValueNode) {
            nodeCert = createValueNodeCertificate((ValueNode) node);
        } else {
            nodeCert = createNodeCertificate(node);
        }
        putNodeCert(nodeCert);
        this.nodeCerts[this.nodeCertCount] = nodeCert;
        this.nodeCertCount++;
        return nodeCert;
    }

    /**
     * Inserts a certificate node either in the array (if the corresponding node
     * is a {@link PlainNode}) or in the map.
     */
    private void putNodeCert(NodeCertificate nodeCert) {
        Node node = nodeCert.getElement();
        int nodeNr = node.getNumber();
        assert nodeNr < this.defaultNodeCerts.length : String.format("Node nr %d higher than maximum %d",
            nodeNr,
            this.defaultNodeCerts.length - 1);
        this.defaultNodeCerts[nodeNr] = nodeCert;
    }

    /**
     * Retrieves a certificate node image for a given graph node from the map,
     * creating the certificate node first if necessary.
     */
    NodeCertificate getNodeCert(final Node node) {
        NodeCertificate result;
        int nodeNr = node.getNumber();
        result = this.defaultNodeCerts[nodeNr];
        assert result != null : String.format("Could not find certificate for %s", node);
        return result;
    }

    /**
     * Creates an {@link EdgeCertificate} for a given graph edge, and inserts
     * into the certificate edge map.
     */
    private void initEdgeCert(Edge edge) {
        Node source = edge.source();
        NodeCertificate sourceCert = getNodeCert(source);
        assert sourceCert != null : String.format("No source certifiate found for %s", edge);
        if (source == edge.target()) {
            EdgeCertificate edge1Cert = createEdge1Certificate(edge, sourceCert);
            this.edgeCerts[this.edgeCerts.length - this.edge1CertCount - 1] = edge1Cert;
            this.edge1CertCount++;
            assert this.edge1CertCount + this.edge2CertCount <= this.edgeCerts.length : String.format("%s unary and %s binary edges do not equal %s edges",
                this.edge1CertCount,
                this.edge2CertCount,
                this.edgeCerts.length);
        } else {
            NodeCertificate targetCert = getNodeCert(edge.target());
            assert targetCert != null : String.format("No target certifiate found for %s", edge);
            EdgeCertificate edge2Cert = createEdge2Certificate(edge, sourceCert, targetCert);
            this.edgeCerts[this.edge2CertCount] = edge2Cert;
            this.edge2CertCount++;
            assert this.edge1CertCount + this.edge2CertCount <= this.edgeCerts.length : String.format("%s unary and %s binary edges do not equal %s edges",
                this.edge1CertCount,
                this.edge2CertCount,
                this.edgeCerts.length);
        }
    }

    abstract NodeCertificate createValueNodeCertificate(ValueNode node);

    abstract NodeCertificate createNodeCertificate(Node node);

    abstract EdgeCertificate createEdge1Certificate(Edge edge,
        groove.graph.iso.CertificateStrategy.NodeCertificate source);

    abstract EdgeCertificate createEdge2Certificate(Edge edge,
        groove.graph.iso.CertificateStrategy.NodeCertificate source,
        groove.graph.iso.CertificateStrategy.NodeCertificate target);

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
        if (this.certificateMap == null) {
            getGraphCertificate();
            this.certificateMap = new HashMap<>();
            // add the node certificates to the certificate map
            for (NodeCertificate nodeCert : this.nodeCerts) {
                this.certificateMap.put(nodeCert.getElement(), nodeCert);
            }
            // add the edge certificates to the certificate map
            for (EdgeCertificate edgeCert : this.edgeCerts) {
                this.certificateMap.put(edgeCert.getElement(), edgeCert);
            }
        }
        return this.certificateMap;
    }

    /**
     * Returns a map from node certificates to sets of nodes of the underlying
     * graph. This is the reverse of {@link #getCertificateMap()}, specialised
     * to nodes. Two nodes from different graphs may only be joined by
     * isomorphism if their certificates are equal; i.e., if they are in the
     * image of the same certificate.
     */
    public PartitionMap<Node> getNodePartitionMap() {
        // check if the map has been computed before
        if (this.nodePartitionMap == null) {
            // no; go ahead and compute it
            getGraphCertificate();
            this.nodePartitionMap = computeNodePartitionMap();
        }
        return this.nodePartitionMap;
    }

    /**
     * Computes the partition map, i.e., the mapping from certificates to sets
     * of graph elements having those certificates.
     */
    private PartitionMap<Node> computeNodePartitionMap() {
        getPartitionReporter.start();
        PartitionMap<Node> result = new PartitionMap<>();
        // invert the certificate map
        for (NodeCertificate cert : this.nodeCerts) {
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
    public PartitionMap<Edge> getEdgePartitionMap() {
        // check if the map has been computed before
        if (this.edgePartitionMap == null) {
            // no; go ahead and compute it
            getGraphCertificate();
            this.edgePartitionMap = computeEdgePartitionMap();
        }
        return this.edgePartitionMap;
    }

    /**
     * Computes the partition map, i.e., the mapping from certificates to sets
     * of graph elements having those certificates.
     */
    private PartitionMap<Edge> computeEdgePartitionMap() {
        getPartitionReporter.start();
        PartitionMap<Edge> result = new PartitionMap<>();
        // invert the certificate map
        int bound = this.edgeCerts.length;
        for (int i = 0; i < bound; i++) {
            result.add(this.edgeCerts[i]);
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
    private final Graph graph;

    /** The pre-computed graph certificate, if any. */
    long graphCertificate;
    /** The pre-computed certificate map, if any. */
    Map<Element,ElementCertificate<?>> certificateMap;
    /** The pre-computed node partition map, if any. */
    PartitionMap<Node> nodePartitionMap;
    /** The pre-computed edge partition map, if any. */
    PartitionMap<Edge> edgePartitionMap;

    /**
     * The list of node certificates in this bisimulator.
     */
    NodeCertificate[] nodeCerts;
    /** The number of elements in {@link #nodeCerts}. */
    int nodeCertCount;
    /**
     * The list of edge certificates in this bisimulator. The array consists of
     * {@link #edge2CertCount} certificates for binary edges, followed by
     * {@link #edge1CertCount} certificates for unary edges.
     */
    EdgeCertificate[] edgeCerts;
    /** The number of binary edge certificates in {@link #edgeCerts}. */
    int edge2CertCount;
    /** The number of unary edge certificates in {@link #edgeCerts}. */
    int edge1CertCount;
    /** Array for storing default node certificates. */
    private final NodeCertificate[] defaultNodeCerts;

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
    static private int[] iterateCountArray = new int[0];

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

    /** Specialised certificate for edges. */
    static public interface EdgeCertificate extends ElementCertificate<Edge> {
        // no added functionality
    }
}
