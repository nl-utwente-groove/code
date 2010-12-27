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
 * $Id: CertificateStrategy.java,v 1.5 2007-09-19 09:01:05 rensink Exp $
 */
package groove.graph.iso;

import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.algebra.ValueNode;
import groove.util.Reporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface for algorithms to compute isomorphism certificates for a given
 * graph, i.e., a predictor for graph isomorphism. Two graphs are isomorphic
 * only if their certificates are equal (as determined by
 * <tt>equals(Object)</tt>). A certificate strategy is specialized to a graph
 * upon which it works; this is set at creation time.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class CertificateStrategy<N extends Node,E extends Edge<N>> {
    @SuppressWarnings("unchecked")
    CertificateStrategy(Graph<N,E> graph) {
        this.graph = graph;
        // the graph may be null if a prototype is being constructed.
        if (graph != null) {
            this.defaultNodeCerts =
                new NodeCertificate[graph.getFactory().getMaxNodeNr() + 1];
        } else {
            this.defaultNodeCerts = null;
        }
    }

    /**
     * Returns the underlying graph for which this is the certificate strategy.
     * @return the underlying graph
     */
    public Graph<N,E> getGraph() {
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
    public Certificate<N>[] getNodeCertificates() {
        if (this.nodeCerts == null) {
            computeCertificates();
        }
        return this.nodeCerts;
    }

    /** Returns the edge certificates calculated for the graph. */
    public Certificate<E>[] getEdgeCertificates() {
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
    @SuppressWarnings("unchecked")
    void initCertificates() {
        // the following two calls are not profiled, as it
        // is likely that this results in the actual graph construction
        int nodeCount = getGraph().nodeCount();
        int edgeCount = getGraph().edgeCount();
        this.nodeCerts = new NodeCertificate[nodeCount];
        this.edgeCerts = new EdgeCertificate[edgeCount];
        this.otherNodeCertMap = new HashMap<ValueNode,NodeCertificate<N>>();
        // create the edge certificates
        for (N node : getGraph().nodeSet()) {
            initNodeCert(node);
        }
        for (E edge : getGraph().edgeSet()) {
            initEdgeCert(edge);
        }
    }

    /**
     * Creates a {@link NodeCertificate} for a given graph node, and inserts
     * into the certificate node map.
     */
    private NodeCertificate<N> initNodeCert(final N node) {
        NodeCertificate<N> nodeCert;
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
     * is a {@link DefaultNode}) or in the map.
     */
    private void putNodeCert(NodeCertificate<N> nodeCert) {
        N node = nodeCert.getElement();
        int nodeNr = node.getNumber();
        if (node instanceof ValueNode) {
            Object oldObject =
                this.otherNodeCertMap.put((ValueNode) node, nodeCert);
            assert oldObject == null : "Certificate node " + nodeCert + " for "
                + node + " seems to override " + oldObject;
        } else {
            assert nodeNr < this.defaultNodeCerts.length : String.format(
                "Node nr %d higher than maximum %d", nodeNr,
                this.defaultNodeCerts.length);
            this.defaultNodeCerts[nodeNr] = nodeCert;
        }
    }

    /**
     * Retrieves a certificate node image for a given graph node from the map,
     * creating the certificate node first if necessary.
     */
    NodeCertificate<N> getNodeCert(final N node) {
        NodeCertificate<N> result;
        int nodeNr = node.getNumber();
        if (node instanceof ValueNode) {
            result = this.otherNodeCertMap.get(node);
        } else {
            result = this.defaultNodeCerts[nodeNr];
        }
        assert result != null : String.format(
            "Could not find certificate for %s", node);
        return result;
    }

    /**
     * Creates an {@link EdgeCertificate} for a given graph edge, and inserts
     * into the certificate edge map.
     */
    private void initEdgeCert(E edge) {
        N source = edge.source();
        NodeCertificate<N> sourceCert = getNodeCert(source);
        assert sourceCert != null : "Edge source of " + edge + " not found in "
            + this.otherNodeCertMap + "; so not in the node set "
            + this.graph.nodeSet() + " of " + this.graph;
        if (source == edge.target()) {
            EdgeCertificate<N,E> edge1Cert =
                createEdge1Certificate(edge, sourceCert);
            this.edgeCerts[this.edgeCerts.length - this.edge1CertCount - 1] =
                edge1Cert;
            this.edge1CertCount++;
            assert this.edge1CertCount + this.edge2CertCount <= this.edgeCerts.length : String.format(
                "%s unary and %s binary edges do not equal %s edges",
                this.edge1CertCount, this.edge2CertCount, this.edgeCerts.length);
        } else {
            NodeCertificate<N> targetCert = getNodeCert(edge.target());
            assert targetCert != null : "Edge target of " + edge
                + " not found in " + this.otherNodeCertMap
                + "; so not in the node set " + this.graph.nodeSet() + " of "
                + this.graph;
            EdgeCertificate<N,E> edge2Cert =
                createEdge2Certificate(edge, sourceCert, targetCert);
            this.edgeCerts[this.edge2CertCount] = edge2Cert;
            this.edge2CertCount++;
            assert this.edge1CertCount + this.edge2CertCount <= this.edgeCerts.length : String.format(
                "%s unary and %s binary edges do not equal %s edges",
                this.edge1CertCount, this.edge2CertCount, this.edgeCerts.length);
        }
    }

    abstract NodeCertificate<N> createValueNodeCertificate(ValueNode node);

    abstract NodeCertificate<N> createNodeCertificate(N node);

    abstract EdgeCertificate<N,E> createEdge1Certificate(E edge,
            groove.graph.iso.CertificateStrategy.NodeCertificate<N> source);

    abstract EdgeCertificate<N,E> createEdge2Certificate(E edge,
            groove.graph.iso.CertificateStrategy.NodeCertificate<N> source,
            groove.graph.iso.CertificateStrategy.NodeCertificate<N> target);

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
    public Map<Element,Certificate<?>> getCertificateMap() {
        // check if the map has been computed before
        if (this.certificateMap == null) {
            getGraphCertificate();
            this.certificateMap = new HashMap<Element,Certificate<?>>();
            // add the node certificates to the certificate map
            for (NodeCertificate<N> nodeCert : this.nodeCerts) {
                this.certificateMap.put(nodeCert.getElement(), nodeCert);
            }
            // add the edge certificates to the certificate map
            for (EdgeCertificate<N,E> edgeCert : this.edgeCerts) {
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
    public PartitionMap<N> getNodePartitionMap() {
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
    private PartitionMap<N> computeNodePartitionMap() {
        getPartitionReporter.start();
        PartitionMap<N> result = new PartitionMap<N>();
        // invert the certificate map
        for (NodeCertificate<N> cert : this.nodeCerts) {
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
    public PartitionMap<E> getEdgePartitionMap() {
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
    private PartitionMap<E> computeEdgePartitionMap() {
        getPartitionReporter.start();
        PartitionMap<E> result = new PartitionMap<E>();
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
    abstract public <N1 extends Node,E1 extends Edge<N1>> CertificateStrategy<N1,E1> newInstance(
            Graph<N1,E1> graph, boolean strong);

    /** 
     * Returns the strength of the strategy:
     * A strong strategy will spend more effort in avoiding false negatives. 
     */
    abstract public boolean getStrength();

    /** The graph for which certificates are to be computed. */
    private final Graph<N,E> graph;

    /** The pre-computed graph certificate, if any. */
    long graphCertificate;
    /** The pre-computed certificate map, if any. */
    Map<Element,Certificate<?>> certificateMap;
    /** The pre-computed node partition map, if any. */
    PartitionMap<N> nodePartitionMap;
    /** The pre-computed edge partition map, if any. */
    PartitionMap<E> edgePartitionMap;

    /**
     * The list of node certificates in this bisimulator.
     */
    NodeCertificate<N>[] nodeCerts;
    /** The number of elements in {@link #nodeCerts}. */
    int nodeCertCount;
    /**
     * The list of edge certificates in this bisimulator. The array consists of
     * {@link #edge2CertCount} certificates for binary edges, followed by 
     * {@link #edge1CertCount} certificates for unary edges.
     */
    EdgeCertificate<N,E>[] edgeCerts;
    /** The number of binary edge certificates in {@link #edgeCerts}. */
    int edge2CertCount;
    /** The number of unary edge certificates in {@link #edgeCerts}. */
    int edge1CertCount;
    /** Map from {@link ValueNode}s to node certificates. */
    Map<ValueNode,NodeCertificate<N>> otherNodeCertMap;

    /** Array for storing default node certificates. */
    private final NodeCertificate<N>[] defaultNodeCerts;

    /**
     * Returns an array that, at every index, contains the number of times that
     * the computation of certificates has taken a number of iterations equal to
     * the index.
     */
    static public List<Integer> getIterateCount() {
        List<Integer> result = new ArrayList<Integer>();
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
            System.arraycopy(iterateCountArray, 0, newIterateCount, 0,
                iterateCountArray.length);
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
    static public final Reporter computeCertReporter =
        reporter.register("computeCertificates()");
    /** Handle to profile {@link #getNodePartitionMap()}. */
    static protected final Reporter getPartitionReporter =
        reporter.register("getPartitionMap()");

    /**
     * Type of the certificates constructed by the strategy. A value of this
     * type represents a given graph element in an isomorphism-invariant way.
     * Hence, equality of certificates does not imply equality of the
     * corresponding graph elements.
     */
    static public interface Certificate<EL extends Element> {
        /** Returns the element for which this is a certificate. */
        EL getElement();
    }

    /** Specialised certificate for nodes. */
    static public interface NodeCertificate<N extends Node> extends
            Certificate<N> {
        // no added functionality
    }

    /** Specialised certificate for edges. */
    static public interface EdgeCertificate<N extends Node,E extends Edge<N>>
            extends Certificate<E> {
        // no added functionality
    }
}
