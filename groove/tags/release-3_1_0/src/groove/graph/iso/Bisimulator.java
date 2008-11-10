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
 * $Id: Bisimulator.java,v 1.16 2007-11-02 08:42:38 rensink Exp $
 */
package groove.graph.iso;

import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.UnaryEdge;
import groove.graph.algebra.ValueNode;
import groove.util.IntSet;
import groove.util.Reporter;
import groove.util.TreeIntSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements an algorithm to partition a given graph into sets of bisimilar
 * graph elements (i.e., nodes and edges). The result is available as a mapping
 * from graph elements to "certificate" objects; two edges are bisimilar if they
 * map to the same (i.e., <tt>equal</tt>) certificate.
 * @author Arend Rensink
 * @version $Revision$
 */
public class Bisimulator implements CertificateStrategy {
    /**
     * Constructs a new bisimulation strategy, on the basis of a given graph.
     * @param graph the underlying graph for the bisimulation strategy; should
     *        not be <tt>null</tt>
     */
    public Bisimulator(Graph graph) {
        this.graph = graph;
    }

    public Graph getGraph() {
        return this.graph;
    }

    /**
     * The result is computed by first initialising arrays of certificates and
     * subsequently iterating over those arrays until the number of distinct
     * certificate values does not grow any more. Each iteration first
     * recomputes the edge certificates using the current node certificate
     * values, and then the node certificates using the current edge certificate
     * values.
     */
    public Map<Element,Certificate<?>> getCertificateMap() {
        reporter.start(GET_CERTIFICATE_MAP);
        // check if the map has been computed before
        if (this.certificateMap == null) {
            getGraphCertificate();
            this.certificateMap = new HashMap<Element,Certificate<?>>();
            // add the node certificates to the certificate map
            for (NodeCertificate nodeCert : this.nodeCerts) {
                this.certificateMap.put(nodeCert.getElement(), nodeCert);
            }
            // add the edge certificates to the certificate map
            for (Certificate<Edge> edgeCert : this.edgeCerts) {
                this.certificateMap.put(edgeCert.getElement(), edgeCert);
            }
        }
        reporter.stop();
        return this.certificateMap;
    }

    /**
     * Returns the pre-computed partition map, if any. If none is stored,
     * computes, stores and returns the inverse of the certificate map.
     * @see #getCertificateMap()
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
     * Returns the pre-computed partition map, if any. If none is stored,
     * computes, stores and returns the inverse of the certificate map.
     * @see #getCertificateMap()
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
    private PartitionMap<Node> computeNodePartitionMap() {
        reporter.start(GET_PARTITION_MAP);
        PartitionMap<Node> result = new PartitionMap<Node>();
        // invert the certificate map
        for (Certificate<Node> cert : this.nodeCerts) {
            result.add(cert);
        }
        reporter.stop();
        return result;
    }

    /**
     * Computes the partition map, i.e., the mapping from certificates to sets
     * of graph elements having those certificates.
     */
    private PartitionMap<Edge> computeEdgePartitionMap() {
        reporter.start(GET_PARTITION_MAP);
        PartitionMap<Edge> result = new PartitionMap<Edge>();
        // invert the certificate map
        int bound = USE_EDGE1_CERTIFICATES ? this.edgeCerts.length : this.edge2CertCount;
        for (int i = 0; i < bound; i++) {
            result.add(this.edgeCerts[i]);
        }
        reporter.stop();
        return result;
    }

    /**
     * The graph certificate is computed as the sum of the node and edge
     * certificates.
     */
    public Object getGraphCertificate() {
        reporter.start(GET_GRAPH_CERTIFICATE);
        // check if the certificate has been computed before
        if (this.graphCertificate == null) {
            computeCertificates();
        }
        reporter.stop();
        // return the computed certificate
        return this.graphCertificate;
    }

    public CertificateStrategy newInstance(Graph graph, boolean strong) {
        return new Bisimulator(graph);
    }

    /**
     * This method only returns a useful result after the graph certificate or
     * partition map has been calculated.
     */
    public int getNodePartitionCount() {
        if (this.nodePartitionCount == 0) {
            computeCertificates();
        }
        return this.nodePartitionCount;
    }

    public Certificate<Node>[] getNodeCertificates() {
        getGraphCertificate();
        return this.nodeCerts;
    }

    public Certificate<Edge>[] getEdgeCertificates() {
        getGraphCertificate();
        return this.edgeCerts;
    }
    
    /** Right now only a strong strategy is implemented. */
    public boolean getStrength() {
        return true;
    }

    /** Computes the node and edge certificate arrays. */
    synchronized private void computeCertificates() {
        // we compute the certificate map
        initCertificates();
        iterateCertificates();
        reporter.stop();
    }

    /**
     * Initialises the node and edge certificate arrays, and the certificate
     * map.
     */
    @SuppressWarnings("unchecked")
    private void initCertificates() {
        // the following two calls are not profiled, as it
        // is likely that this results in the actual graph construction
        int nodeCount = this.graph.nodeCount();
        int edgeCount = this.graph.edgeCount();
        reporter.start(COMPUTE_CERTIFICATES);
        reporter.start(INIT_CERTIFICATES);
        this.nodeCerts = new NodeCertificate[nodeCount];
        this.edgeCerts = new Certificate[edgeCount];
        this.otherNodeCertMap = new HashMap<Node,NodeCertificate>();
        // create the edge certificates
        for (Node node : this.graph.nodeSet()) {
            initNodeCert(node);
        }
        for (Edge edge : this.graph.edgeSet()) {
            initEdgeCert(edge);
        }
        reporter.stop();
    }

    /**
     * Creates a {@link NodeCertificate} for a given graph node, and inserts
     * into the certificate node map.
     */
    private NodeCertificate initNodeCert(final Node node) {
        if (TIME) {
            reporter.start(INIT_CERT_NODE);
        }
        NodeCertificate nodeCert;
        // if the node is an instance of OperationNode, the certificate
        // of this node also depends on the operation represented by it
        // therefore, the computeNewValue()-method of class
        // CertificateNode must be overridden
        if (node instanceof ValueNode) {
            nodeCert = new ValueNodeCertificate((ValueNode) node);
        } else {
            nodeCert = new NodeCertificate(node);
        }
        putNodeCert(nodeCert);
        this.nodeCerts[this.nodeCertCount] = nodeCert;
        this.nodeCertCount++;
        if (TIME) {
            reporter.stop();
        }
        return nodeCert;
    }

    /**
     * Creates a {@link Edge2Certificate} for a given graph edge, and inserts
     * into the certificate edge map.
     */
    private void initEdgeCert(Edge edge) {
        if (TIME) {
            reporter.start(INIT_CERT_EDGE);
        }
        Node source = edge.source();
        NodeCertificate sourceCert = getNodeCert(source);
        assert sourceCert != null : "Edge source of " + edge + " not found in "
            + this.otherNodeCertMap + "; so not in the node set "
            + this.graph.nodeSet() + " of " + this.graph;
        if (edge instanceof UnaryEdge || source == edge.opposite()) {
            if (USE_EDGE1_CERTIFICATES) {
                Edge1Certificate edge1Cert =
                    new Edge1Certificate(edge, sourceCert);
                this.edgeCerts[this.edgeCerts.length - this.edge1CertCount - 1] =
                    edge1Cert;
                this.edge1CertCount++;
                assert this.edge1CertCount + this.edge2CertCount <= this.edgeCerts.length : String.format(
                    "%s unary and %s binary edges do not equal %s edges",
                    this.edge1CertCount, this.edge2CertCount,
                    this.edgeCerts.length);
            } else {
                sourceCert.addValue(edge.label().hashCode());
            }
        } else {
            NodeCertificate targetCert = getNodeCert(edge.opposite());
            assert targetCert != null : "Edge target of " + edge
                + " not found in " + this.otherNodeCertMap
                + "; so not in the node set " + this.graph.nodeSet() + " of "
                + this.graph;
            Edge2Certificate edge2Cert =
                new Edge2Certificate(edge, sourceCert, targetCert);
            this.edgeCerts[this.edge2CertCount] = edge2Cert;
            this.edge2CertCount++;
            assert this.edge1CertCount + this.edge2CertCount <= this.edgeCerts.length : String.format(
                "%s unary and %s binary edges do not equal %s edges",
                this.edge1CertCount, this.edge2CertCount, this.edgeCerts.length);
        }
        if (TIME) {
            reporter.stop();
        }
    }

    /**
     * Retrieves a certificate node image for a given graph node from the map,
     * creating the certificate node first if necessary.
     */
    private NodeCertificate getNodeCert(final Node node) {
        NodeCertificate result;
        int nodeNr = DefaultNode.getNodeNr(node);
        if (nodeNr != DefaultNode.NO_NODE_NUMBER) {
            result = defaultNodeCerts[nodeNr];
        } else {
            result = this.otherNodeCertMap.get(node);
        }
        assert result != null : String.format(
            "Could not find certificate for %s", node);
        return result;
    }

    /**
     * Inserts a certificate node either in the array (if the corresponding node
     * is a {@link DefaultNode}) or in the map.
     */
    private void putNodeCert(NodeCertificate nodeCert) {
        Node node = nodeCert.getElement();
        int nodeNr = DefaultNode.getNodeNr(node);
        if (nodeNr != DefaultNode.NO_NODE_NUMBER) {
            if (defaultNodeCerts.length <= nodeNr) {
                NodeCertificate[] newNodeCerts =
                    new NodeCertificate[1 + (int) (nodeNr * GROWTH_FACTOR)];
                System.arraycopy(defaultNodeCerts, 0, newNodeCerts, 0,
                    defaultNodeCerts.length);
                defaultNodeCerts = newNodeCerts;
            }
            defaultNodeCerts[nodeNr] = nodeCert;
        } else {
            Object oldObject = this.otherNodeCertMap.put(node, nodeCert);
            assert oldObject == null : "Certificate node " + nodeCert + " for "
                + node + " seems to override " + oldObject;
        }
    }

    /** Iterates node certificates until this results in a stable partitioning. */
    private void iterateCertificates() {
        // get local copies of attributes for speedup
        IntSet certStore = Bisimulator.certStore;
        int nodeCertCount = this.nodeCertCount;
        int partitionCount = 0;
        int certificateValue;
        // collect and then count the number of certificates
        boolean goOn;
        int iterateCount = 0;
        int breakSymmetryCount = 0;
        do {
            reporter.start(ITERATE_CERTIFICATES);
            certificateValue = nodeCertCount;
            certStore.clear(nodeCertCount);
            // first compute the new edge certificates
            for (int i = 0; i < this.edge2CertCount; i++) {
                Certificate<Edge> edgeCert = this.edgeCerts[i];
                certificateValue += edgeCert.setNewValue();
            }
            // now compute the new node certificates
            // while keeping track of the lowest value, in case
            // we need to break symmetry
            int minCertValue = Integer.MAX_VALUE;
            NodeCertificate minCert = null;
            for (NodeCertificate nodeCert : this.nodeCerts) {
                int newCert = nodeCert.setNewValue();
                if (iterateCount > 0 && partitionCount < nodeCertCount) {
                    if (!certStore.add(newCert)) {
                        // this certificate is a duplicate; maybe it is the smallest
                        if (BREAK_SYMMETRIES && newCert < minCertValue || minCert == null) {
                            minCertValue = newCert;
                            minCert = nodeCert;
                        }
                    }
                }
                certificateValue += newCert;
            }
            int newPartitionCount = certStore.size();
            // break symmetry if we have converged on a partition count
            // lower than the node count
            if (BREAK_SYMMETRIES && iterateCount > 0 && newPartitionCount == partitionCount && newPartitionCount < nodeCertCount) {
                minCert.breakSymmetry();
                breakSymmetryCount ++;
                totalSymmetryBreakCount ++;
            }
            // we stop the iteration when the number of partitions has not grown
            // moreover, when the number of partitions equals the number of
            // nodes then
            // it cannot grow, so we might as well stop straight away
            // note, however, that doing so gives rise to more false positives
            // which, on the other hand, are easily recognisable as such
            if (iterateCount == 0) {
                goOn = true;
            } else if (BREAK_SYMMETRIES) {
                goOn = newPartitionCount < nodeCertCount && breakSymmetryCount < MAX_BREAK_SYMMETRY;
            } else {
                goOn = newPartitionCount > partitionCount;
            }
            if (partitionCount < nodeCertCount) {
                partitionCount = newPartitionCount;
            }
            iterateCount++;
            reporter.stop();
        } while (goOn);
        this.nodePartitionCount = partitionCount;
        this.graphCertificate = new Long(certificateValue);
        if (USE_EDGE1_CERTIFICATES) {
            // so far we have done nothing with the flags, so
            // give them a chance to get their hash code right
            int edgeCount = this.edgeCerts.length;
            for (int i = this.edge2CertCount; i < edgeCount; i++) {
                this.edgeCerts[i].setNewValue();
            }
        }
        recordIterateCount(iterateCount);
    }

    /** The underlying graph */
    private final Graph graph;
    /** The pre-computed graph certificate, if any. */
    private Object graphCertificate;
    /** The pre-computed certificate map, if any. */
    private Map<Element,Certificate<?>> certificateMap;
    /** The pre-computed node partition map, if any. */
    private PartitionMap<Node> nodePartitionMap;
    /** The pre-computed edge partition map, if any. */
    private PartitionMap<Edge> edgePartitionMap;
    /**
     * The number of pre-computed node partitions.
     */
    private int nodePartitionCount;
    /**
     * The list of node certificates in this bisimulator.
     */
    private NodeCertificate[] nodeCerts;
    /** The number of elements in {@link #nodeCerts}. */
    private int nodeCertCount;
    /**
     * The list of edge certificates in this bisimulator. The array consists of
     * a number of {@link Edge2Certificate}s, followed by a number of
     * {@link Edge1Certificate}s.
     */
    private Certificate<Edge>[] edgeCerts;
    /** The number of {@link Edge2Certificate}s in {@link #edgeCerts}. */
    private int edge2CertCount;
    /** The number of {@link Edge1Certificate}s in {@link #edgeCerts}. */
    private int edge1CertCount;
    /** Map from nodes that are not {@link DefaultNode}s to node certificates. */
    private Map<Node,NodeCertificate> otherNodeCertMap;

    /** Array of default node certificates. */

    /**
     * Returns an array that, at every index, contains the number of times that
     * the computation of certificates has taken a number of iterations equal to
     * the index.
     */
    static public List<Integer> getIterateCount() {
        List<Integer> result = new ArrayList<Integer>();
        for (int element : iterateCount) {
            result.add(element);
        }
        return result;
    }

    /**
     * Returns the total number of times symmetry was broken
     * during the calculation of the certificates.
     */
    static public int getSymmetryBreakCount() {
        return totalSymmetryBreakCount;
    }

    /** Array of default node certificates. */

    /**
     * Records that the computation of the certificates has taken a certain
     * number of iterations.
     * @param count the number of iterations
     */
    static private void recordIterateCount(int count) {
        if (iterateCount.length < count + 1) {
            int[] newIterateCount = new int[count + 1];
            System.arraycopy(iterateCount, 0, newIterateCount, 0,
                iterateCount.length);
            iterateCount = newIterateCount;
        }
        iterateCount[count]++;
    }

    /**
     * The resolution of the tree-based certificate store.
     */
    static private final int TREE_RESOLUTION = 3;
    /**
     * The maximum number of times a symmetry breaking step will be undertaken.
     */
    static private final int MAX_BREAK_SYMMETRY = 10;
    /**
     * Store for node certificates, to count the number of partitions
     */
    static private final IntSet certStore = new TreeIntSet(TREE_RESOLUTION);
    /** Debug flag to switch the use of {@link Edge1Certificate}s on and off. */
    static private final boolean USE_EDGE1_CERTIFICATES = true;
    /** Debug flag to switch the use symmetry breaking on and off. */
    static private final boolean BREAK_SYMMETRIES = false;
    /**
     * Array to record the number of iterations done in computing certificates.
     */
    static private int[] iterateCount = new int[0];
    /** Array for storing default node certificates. */
    static private NodeCertificate[] defaultNodeCerts =
        new NodeCertificate[DefaultNode.getNodeCount()];
    /** Total number of times the symmetry was broken. */
    static private int totalSymmetryBreakCount;
    /** Growth factor for the length of #defaultNodeCerts. */
    static private final float GROWTH_FACTOR = 1.5f;

    // --------------------------- reporter definitions ---------------------
    /** Reporter instance to profile methods of this class. */
    static public final Reporter reporter =
        Reporter.register(Bisimulator.class);
    /** Handle to profile {@link #computeCertificates()}. */
    static public final int COMPUTE_CERTIFICATES =
        reporter.newMethod("computeCertificates()");
    /** Handle to profile {@link #initCertificates()}. */
    static protected final int INIT_CERTIFICATES =
        reporter.newMethod("initCertificates()");
    /** Handle to profile nested node certification. */
    static protected final int NODE_CERTS =
        reporter.newMethod("Nested node certs");
    /** Handle to profile nested edge certification. */
    static protected final int EDGE_CERTS =
        reporter.newMethod("Nested edge certs");
    /** Handle to profile {@link #initNodeCert(Node)}. */
    static protected final int INIT_CERT_NODE =
        reporter.newMethod("initCertNode()");
    /** Handle to profile {@link #initEdgeCert(Edge)}. */
    static protected final int INIT_CERT_EDGE =
        reporter.newMethod("initCertEdge()");
    /** Handle to profile {@link #iterateCertificates()}. */
    static protected final int ITERATE_CERTIFICATES =
        reporter.newMethod("iterateCertificates()");
    /** Handle to profile {@link #getCertificateMap()}. */
    static protected final int GET_CERTIFICATE_MAP =
        reporter.newMethod("getCertificateMap()");
    /** Handle to profile {@link #getNodePartitionMap()}. */
    static protected final int GET_PARTITION_MAP =
        reporter.newMethod("getPartitionMap()");
    /** Handle to profile {@link #getGraphCertificate()}. */
    static protected final int GET_GRAPH_CERTIFICATE =
        reporter.newMethod("getGraphCertificate()");

    static private final boolean TIME = false;

    /**
     * Superclass of graph element certificates.
     */
    public static abstract class Certificate<E extends Element> implements
            CertificateStrategy.Certificate<E> {
        /** Constructs a certificate for a given graph element. */
        Certificate(E element) {
            this.element = element;
        }

        /**
         * Returns the certificate value. Note that this means the hash code is
         * not constant during the initial phase, and so no hash sets or maps
         * should be used.
         * @ensure <tt>result == getValue()</tt>
         * @see #getValue()
         */
        @Override
        public int hashCode() {
            return this.value;
        }

        /**
         * Tests if the other is a {@link Certificate} with the same value.
         */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof Certificate
                && (this.value == ((Certificate<?>) obj).value);
        }

        /**
         * Returns the current certificate value.
         */
        public int getValue() {
            return this.value;
        }

        /**
         * Computes, stores and returns a new value for this certificate. The
         * computation is done by invoking {@link #computeNewValue()}.
         * @return the freshly computed new value
         * @see #computeNewValue()
         */
        protected int setNewValue() {
            return this.value = computeNewValue();
        }

        /**
         * Callback method that provides the new value at each iteration.
         * @return the freshly computed new value
         * @see #setNewValue()
         */
        abstract protected int computeNewValue();

        /** Returns the element of which this is a certificate. */
        public E getElement() {
            return this.element;
        }

        /** The current value, which determines the hash code. */
        protected int value;
        /** The element for which this is a certificate. */
        private final E element;
    }

    /**
     * Class of nodes that carry (and are identified with) an integer
     * certificate value.
     * @author Arend Rensink
     * @version $Revision$
     */
    static private class NodeCertificate extends Certificate<Node> {
        /** Initial node value to provide a better spread of hash codes. */
        static private final int INIT_NODE_VALUE = 0x126b;

        /**
         * Constructs a new certificate node. The incidence count (i.e., the
         * number of incident edges) is passed in as a parameter. The initial
         * value is set to the incidence count.
         */
        public NodeCertificate(Node node) {
            super(node);
            this.value = INIT_NODE_VALUE;
        }

        @Override
        public String toString() {
            return "c" + this.value;
        }

        /**
         * Returns <tt>true</tt> of <tt>obj</tt> is also a
         * {@link NodeCertificate} and has the same value as this one.
         * @see #getValue()
         */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof NodeCertificate
                && (this.value == ((Certificate<?>) obj).value);
        }

        /** 
         * Change the certificate value predictably
         * to break symmetry.
         */
        public void breakSymmetry() {
            this.value += this.value << 1;
        }
        
        /**
         * The new value for this certificate node is the sum of the values of
         * the incident certificate edges.
         */
        @Override
        protected int computeNewValue() {
            int result = this.nextValue ^ this.value;
            this.nextValue = 0;
            return result;
        }

        /**
         * Adds to the current value. Used during construction, to record the
         * initial value of incident edges.
         */
        protected void addValue(int inc) {
            this.value += inc;
        }

        /**
         * Adds a certain value to {@link #nextValue}.
         */
        protected void addNextValue(int value) {
            this.nextValue += value;
        }

        /** The value for the next invocation of {@link #computeNewValue()} */
        int nextValue;
    }

    /**
     * Certificate for value nodes. This takes the actual node identity into
     * account.
     * @author Arend Rensink
     * @version $Revision $
     */
    static private class ValueNodeCertificate extends NodeCertificate {
        /**
         * Constructs a new certificate node. The incidence count (i.e., the
         * number of incident edges) is passed in as a parameter. The initial
         * value is set to the incidence count.
         */
        public ValueNodeCertificate(ValueNode node) {
            super(node);
            this.node = node;
            this.value = node.getNumber();
        }

        /**
         * Returns <tt>true</tt> if <tt>obj</tt> is also a
         * {@link ValueNodeCertificate} and has the same node as this one.
         */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof ValueNodeCertificate
                && this.node.equals(((ValueNodeCertificate) obj).node);
        }

        /**
         * The new value for this certificate node is the sum of the values of
         * the incident certificate edges.
         */
        @Override
        protected int computeNewValue() {
            int result = this.nextValue ^ this.value;
            this.nextValue = 0;
            return result;
        }

        private final ValueNode node;
    }

    /**
     * An edge with certificate nodes as endpoints. The hash code is computed
     * dynamically, on the basis of the current certificate node value.
     * @author Arend Rensink
     * @version $Revision$
     */
    static private class Edge2Certificate extends Certificate<Edge> {
        /**
         * Constructs a certificate for a binary edge.
         * @param edge The target certificate node
         * @param source The source certificate node
         * @param target The label of the original edge
         */
        public Edge2Certificate(Edge edge, NodeCertificate source,
                NodeCertificate target) {
            super(edge);
            this.source = source;
            this.target = target;
            this.labelIndex = ((DefaultLabel) edge.label()).hashCode();
            initValue();
            source.addValue(this.value);
            target.addValue(this.value << 1);
        }

        @Override
        public String toString() {
            return "[" + this.source + "," + getElement().label() + "("
                + this.labelIndex + ")," + this.target + "]";
        }

        /**
         * Returns <tt>true</tt> if <tt>obj</tt> is also a
         * {@link Edge2Certificate} and has the same value, as well as the same
         * source and target values, as this one.
         * @see #getValue()
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Edge2Certificate) {
                Edge2Certificate other = (Edge2Certificate) obj;
                if (this.value != other.value
                    || this.labelIndex != other.labelIndex
                    || this.source.value != other.source.value) {
                    return false;
                } else if (this.target == this.source) {
                    return other.target == other.source;
                } else {
                    return this.target.value == other.target.value;
                }
            } else {
                return false;
            }
        }

        /**
         * Computes the value on the basis of the end nodes and the label index.
         */
        @Override
        protected int computeNewValue() {
            int targetShift = (this.labelIndex & 0xf) + 1;
            int sourceHashCode = this.source.value;
            int targetHashCode = this.target.value;
            int result =
                ((sourceHashCode << 8) | (sourceHashCode >>> 24))
                    + ((targetHashCode << targetShift) | (targetHashCode >>> targetShift))
                    + this.value;
            this.source.nextValue += 2 * result;
            this.target.nextValue -= 3 * result;
            return result;
        }

        /**
         * Initialises the value. Callback method from the constructor. This
         * implementation takes the label index as the initial value.
         */
        protected void initValue() {
            this.value = this.labelIndex;
        }

        /** The source certificate for the edge. */
        private final NodeCertificate source;
        /** The target certificate for the edge; may be <tt>null</tt>. */
        private final NodeCertificate target;
        /**
         * The hash code of the original edge label.
         */
        private final int labelIndex;
    }

    /**
     * An edge with only one endpoint. The hash code is computed dynamically, on
     * the basis of the current certificate node value.
     * @author Arend Rensink
     * @version $Revision$
     */
    static private class Edge1Certificate extends Certificate<Edge> {
        /** Constructs a certificate edge for a predicate (i.e., a unary edge). */
        public Edge1Certificate(Edge edge, NodeCertificate source) {
            super(edge);
            this.source = source;
            this.labelIndex = ((DefaultLabel) edge.label()).hashCode();
            initValue();
            source.addValue(this.value);
        }

        @Override
        public String toString() {
            return "[" + this.source + "," + getElement().label() + "("
                + this.labelIndex + ")]";
        }

        /**
         * Returns <tt>true</tt> if <tt>obj</tt> is also a
         * {@link Edge1Certificate} and has the same value, as well as the same
         * source and target values, as this one.
         * @see #getValue()
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Edge1Certificate) {
                Edge1Certificate other = (Edge1Certificate) obj;
                return (this.value == other.value && this.labelIndex == other.labelIndex);
            } else {
                return false;
            }
        }

        /**
         * Computes the value on the basis of the end nodes and the label index.
         */
        @Override
        protected int computeNewValue() {
            int sourceHashCode = this.source.hashCode();
            int result =
                (sourceHashCode << 8) + (sourceHashCode >> 24) + this.value;
            // source.nextValue += result;
            return result;
        }

        /**
         * Initialises the value. Callback method from the constructor. This
         * implementation takes the label index as the initial value.
         */
        protected void initValue() {
            this.value = this.labelIndex << 4;
        }

        /** The source certificate for the edge. */
        private final NodeCertificate source;
        /**
         * The hash code of the original edge label.
         */
        private final int labelIndex;
    }
}
