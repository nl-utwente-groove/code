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

import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.UnaryEdge;
import groove.graph.algebra.ValueNode;
import groove.util.Reporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeSet;

/**
 * Implements an algorithm to partition a given graph into sets of symmetric
 * graph elements (i.e., nodes and edges). The result is available as a mapping
 * from graph elements to "certificate" objects; two edges are predicted to be
 * symmetric if they map to the same (i.e., <tt>equal</tt>) certificate. This
 * strategy goes beyond bisimulation in that it breaks all apparent symmetries
 * in all possible ways and accumulates the results.
 * @author Arend Rensink
 * @version $Revision: 1529 $
 */
public class PaigeTarjanMcKay implements CertificateStrategy {
    /**
     * Constructs a new bisimulation strategy, on the basis of a given graph.
     * The strategy checks for isomorphism weakly, meaning that it might yield
     * false negatives.
     * @param graph the underlying graph for the bisimulation strategy; should
     *        not be <tt>null</tt>
     */
    public PaigeTarjanMcKay(Graph graph) {
        this(graph, false);
    }

    /**
     * Constructs a new bisimulation strategy, on the basis of a given graph.
     * @param graph the underlying graph for the bisimulation strategy; should
     *        not be <tt>null</tt>
     * @param strong if <code>true</code>, the strategy puts more effort into
     *        getting distinct certificates.
     */
    public PaigeTarjanMcKay(Graph graph, boolean strong) {
        this.graph = graph;
        this.strong = strong;
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
        int bound = this.edgeCerts.length;
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
        if (TRACE) {
            System.out.printf("Computing graph certificate%n");
        }
        reporter.start(GET_GRAPH_CERTIFICATE);
        // check if the certificate has been computed before
        if (this.graphCertificate == 0) {
            computeCertificates();
            if (this.graphCertificate == 0) {
                this.graphCertificate = 1;
            }
        }
        reporter.stop();
        if (TRACE) {
            System.out.printf("Graph certificate: %d%n", this.graphCertificate);
        }
        // return the computed certificate
        return this.graphCertificate;
    }

    public CertificateStrategy newInstance(Graph graph, boolean strong) {
        return new PaigeTarjanMcKay(graph);
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
        Queue<Block> splitters = initCertificates();
        this.nodePartitionCount = splitters.size();
        // first iteration
        split(splitters);
        if (TRACE) {
            System.out.printf(
                "First iteration done; %d partitions for %d nodes in %d iterations%n",
                this.nodePartitionCount, this.nodeCertCount, this.iterateCount);
        }
        // // check if duplicate
        // if ((this.strong || BREAK_DUPLICATES)
        // && this.nodePartitionCount < this.nodeCertCount) {
        // // now look for smallest unbroken duplicate certificate (if any)
        // int oldPartitionCount;
        // do {
        // oldPartitionCount = this.nodePartitionCount;
        // List<NodeCertificate> duplicates = getSmallestDuplicates();
        // if (duplicates.isEmpty()) {
        // if (TRACE) {
        // System.out.printf("All duplicate certificates broken%n");
        // }
        // break;
        // }
        // checkpointCertificates();
        // // successively break the symmetry at each of these
        // for (NodeCertificate duplicate : duplicates) {
        // duplicate.breakSymmetry();
        // iterateCertificates();
        // rollBackCertificates();
        // this.nodePartitionCount = oldPartitionCount;
        // }
        // accumulateCertificates();
        // // calculate the edge and node certificates once more
        // // to push out the accumulated node values and get the correct
        // // node partition count
        // advanceEdgeCerts();
        // advanceNodeCerts(true);
        // if (TRACE) {
        // System.out.printf(
        // "Next iteration done; %d partitions for %d nodes in %d iterations%n",
        // this.nodePartitionCount, this.nodeCertCount,
        // this.iterateCount);
        // }
        // } while (true);// this.nodePartitionCount < this.nodeCertCount &&
        // // this.nodePartitionCount > oldPartitionCount);
        // }
        // // so far we have done nothing with the self-edges, so
        // // give them a chance to get their value right
        // int edgeCount = this.edgeCerts.length;
        // for (int i = this.edge2CertCount; i < edgeCount; i++) {
        // this.edgeCerts[i].setNewValue();
        // }
        reporter.stop();
    }

    /**
     * Initialises the node and edge certificate arrays, and the certificate
     * map.
     */
    @SuppressWarnings("unchecked")
    private Queue<Block> initCertificates() {
        // the following two calls are not profiled, as it
        // is likely that this results in the actual graph construction
        int nodeCount = this.graph.nodeCount();
        int edgeCount = this.graph.edgeCount();
        reporter.start(COMPUTE_CERTIFICATES);
        reporter.start(INIT_CERTIFICATES);
        this.nodeCerts = new NodeCertificate[nodeCount];
        this.edgeCerts = new Certificate[edgeCount];
        this.otherNodeCertMap = new HashMap<Node,NodeCertificate>();
        // create the node certificates
        for (Node node : this.graph.nodeSet()) {
            initNodeCert(node);
        }
        for (Edge edge : this.graph.edgeSet()) {
            initEdgeCert(edge);
        }
        Map<Integer,Block> result = new HashMap<Integer,Block>();
        for (NodeCertificate nodeCert : this.nodeCerts) {
            Block block = result.get(nodeCert.getValue());
            if (block == null) {
                result.put(nodeCert.getValue(), block =
                    new Block(nodeCert.getValue()));
                block.setSplitter(true);
            }
            block.append(nodeCert);
        }
        Block[] resultArray = new Block[result.size()];
        result.values().toArray(resultArray);
        Arrays.sort(resultArray);
        reporter.stop();
        return new LinkedList<Block>(Arrays.asList(resultArray));
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
            EdgeCertificate edge1Cert = new EdgeCertificate(edge, sourceCert);
            this.edgeCerts[this.edgeCerts.length - this.edge1CertCount - 1] =
                edge1Cert;
            this.edge1CertCount++;
            assert this.edge1CertCount + this.edge2CertCount <= this.edgeCerts.length : String.format(
                "%s unary and %s binary edges do not equal %s edges",
                this.edge1CertCount, this.edge2CertCount, this.edgeCerts.length);
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

    private void split(Queue<Block> splitterList) {
        while (!splitterList.isEmpty()) {
            // find the first non-empty splitter in the queue
            Block splitter = splitterList.poll();
            if (splitter.size() > 0) {
                splitNext(splitter, splitterList);
            }
        }
    }

    private void splitNext(Block splitter, Queue<Block> splitterList) {
        // update the node certificates related to the splitter nodes
        Collection<Block> splitBlocks = new TreeSet<Block>();
        for (NodeCertificate splitterNode : splitter.getNodes()) {
            for (Edge2Certificate outEdge : splitterNode.outEdges) {
                Block splitBlock = outEdge.getTarget().getBlock();
                if (splitBlock.startSplit()) {
                    splitBlocks.add(splitBlock);
                }
                outEdge.updateTarget();
            }
            for (Edge2Certificate inEdge : splitterNode.inEdges) {
                Block splitBlock = inEdge.getSource().getBlock();
                if (splitBlock.startSplit()) {
                    splitBlocks.add(splitBlock);
                }
                inEdge.updateSource();
            }
        }
        splitter.setSplitter(false);
        // process the split blocks
        for (Block block : splitBlocks) {
            Block[] newBlocks = block.split();
            if (newBlocks.length > 0) {
                int last =
                    block.isSplitter() ? newBlocks.length
                            : newBlocks.length - 1;
                for (int i = 0; i < last; i++) {
                    splitterList.add(newBlocks[i]);
                    newBlocks[i].setSplitter(true);
                    // this.graphCertificate += newBlocks[i].hashCode();
                }
                // if (last < newBlocks.length) {
                // this.graphCertificate += newBlocks[last].hashCode();
                // }
                this.nodePartitionCount += newBlocks.length - 1;
            }
        }
    }

    /** The underlying graph */
    private final Graph graph;
    /**
     * Flag to indicate that more effort should be put into obtaining distinct
     * certificates.
     */
    private final boolean strong;
    /** The pre-computed graph certificate, if any. */
    private long graphCertificate;
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
    // /** The number of frozen elements in {@link #nodeCerts}. */
    // private int frozenNodeCertCount;
    /** The number of elements in {@link #nodeCerts}. */
    private int nodeCertCount;
    /**
     * The list of edge certificates in this bisimulator. The array consists of
     * a number of {@link Edge2Certificate}s, followed by a number of
     * {@link EdgeCertificate}s.
     */
    private Certificate<Edge>[] edgeCerts;
    /** The number of {@link Edge2Certificate}s in {@link #edgeCerts}. */
    private int edge2CertCount;
    // /** The number of frozen {@link Edge2Certificate}s in {@link #edgeCerts}.
    // */
    // private int frozenEdge2CertCount;
    /** The number of {@link EdgeCertificate}s in {@link #edgeCerts}. */
    private int edge1CertCount;
    /** Map from nodes that are not {@link DefaultNode}s to node certificates. */
    private Map<Node,NodeCertificate> otherNodeCertMap;
    /** Total number of iterations in {@link #iterateCertificates()}. */
    private int iterateCount;

    /** Array of default node certificates. */

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
     * Returns the total number of times symmetry was broken during the
     * calculation of the certificates.
     */
    static public int getSymmetryBreakCount() {
        return totalSymmetryBreakCount;
    }

    /**
     * Records that the computation of the certificates has taken a certain
     * number of iterations.
     * @param count the number of iterations
     */
    static private void recordIterateCount(int count) {
        if (iterateCountArray.length < count + 1) {
            int[] newIterateCount = new int[count + 1];
            System.arraycopy(iterateCountArray, 0, newIterateCount, 0,
                iterateCountArray.length);
            iterateCountArray = newIterateCount;
        }
        iterateCountArray[count]++;
    }

    /** Debug flag to switch the use of duplicate breaking on and off. */
    static private final boolean BREAK_DUPLICATES = true;
    /**
     * Array to record the number of iterations done in computing certificates.
     */
    static private int[] iterateCountArray = new int[0];
    /** Array for storing default node certificates. */
    static private NodeCertificate[] defaultNodeCerts =
        new NodeCertificate[DefaultNode.getNodeCount()];
    /** Total number of times the symmetry was broken. */
    static private int totalSymmetryBreakCount;
    /** Growth factor for the length of #defaultNodeCerts. */
    static private final float GROWTH_FACTOR = 1.5f;
    /** Number of bits in an int. */
    static private final int INT_WIDTH = 32;

    /** Static empty list, to be shared among split blocks. */
    private static final List<NodeCertificate> EMPTY_NODE_LIST =
        Collections.emptyList();
    /**
     * Static empty array of blocks, to be returned in case of singular split
     * blocks.
     */
    private static final Block[] EMPTY_BLOCK_ARRAY = new Block[0];

    // --------------------------- reporter definitions ---------------------
    /** Reporter instance to profile methods of this class. */
    static public final Reporter reporter = DefaultIsoChecker.reporter;
    /** Handle to profile {@link #computeCertificates()}. */
    static public final int COMPUTE_CERTIFICATES =
        PartitionRefiner.COMPUTE_CERTIFICATES;
    /** Handle to profile {@link #initCertificates()}. */
    static protected final int INIT_CERTIFICATES =
        PartitionRefiner.INIT_CERTIFICATES;
    /** Handle to profile {@link #initNodeCert(Node)}. */
    static protected final int INIT_CERT_NODE = PartitionRefiner.INIT_CERT_NODE;
    /** Handle to profile {@link #initEdgeCert(Edge)}. */
    static protected final int INIT_CERT_EDGE = PartitionRefiner.INIT_CERT_EDGE;
    /** Handle to profile {@link #iterateCertificates()}. */
    static protected final int ITERATE_CERTIFICATES =
        PartitionRefiner.ITERATE_CERTIFICATES;
    /** Handle to profile {@link #getCertificateMap()}. */
    static protected final int GET_CERTIFICATE_MAP =
        PartitionRefiner.GET_CERTIFICATE_MAP;
    /** Handle to profile {@link #getNodePartitionMap()}. */
    static protected final int GET_PARTITION_MAP =
        PartitionRefiner.GET_PARTITION_MAP;
    /** Handle to profile {@link #getGraphCertificate()}. */
    static protected final int GET_GRAPH_CERTIFICATE =
        PartitionRefiner.GET_GRAPH_CERTIFICATE;
    /** Flag to turn on more time profiling. */
    static private final boolean TIME = false;
    /** Flag to turn on System.out-tracing. */
    static private final boolean TRACE = false;

    /**
     * Class of nodes that carry (and are identified with) an integer
     * certificate value.
     * @author Arend Rensink
     * @version $Revision: 1529 $
     */
    static class NodeCertificate // extends LinkedListCell<NodeCertificate>
            implements Certificate<Node> {
        /** Initial node value to provide a better spread of hash codes. */
        static private final int INIT_NODE_VALUE = 0x126b;

        /**
         * Constructs a new certificate node. The incidence count (i.e., the
         * number of incident edges) is passed in as a parameter. The initial
         * value is set to the incidence count.
         */
        public NodeCertificate(Node node) {
            this.element = node;
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
                && this.value == ((NodeCertificate) obj).value;
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
         * Returns the current certificate value.
         */
        public final int getValue() {
            return this.value;
        }

        /**
         * Adds a certain value to {@link #nextValue}.
         */
        void addNextValue(int value) {
            this.nextValue += value;
        }

        /**
         * Computes, stores and returns a new value for this certificate.
         */
        void setNewValue() {
            this.value = computeNewValue();
        }

        /**
         * Computes, stores and returns a new value for this certificate.
         */
        int computeNewValue() {
            int result = this.nextValue ^ this.value;
            this.nextValue = 0;
            return result;
        }

        /** Returns the element of which this is a certificate. */
        public Node getElement() {
            return this.element;
        }

        /** Adds a self-edge certificate to this node certificate. */
        void addSelf(EdgeCertificate edgeCert) {
            this.value += edgeCert.getValue();
        }

        /** Adds an outgoing edge certificate to this node certificate. */
        void addOutgoing(Edge2Certificate edgeCert) {
            this.outEdges.add(edgeCert);
            this.value += edgeCert.getValue();
        }

        /** Adds an incoming edge certificate to this node certificate. */
        void addIncoming(Edge2Certificate edgeCert) {
            this.inEdges.add(edgeCert);
            this.value += edgeCert.getValue() ^ TARGET_MASK;
        }

        final Block getBlock() {
            return this.container;
        }

        final void setBlock(Block container) {
            this.container = container;
        }

        /** The value for the next invocation of {@link #computeNewValue()} */
        private int nextValue;
        /** The current value, which determines the hash code. */
        int value;
        /** The element for which this is a certificate. */
        private final Node element;
        /** List of certificates of incoming edges. */
        private final List<Edge2Certificate> inEdges =
            new ArrayList<Edge2Certificate>();
        /** List of certificates of outgoing edges. */
        private final List<Edge2Certificate> outEdges =
            new ArrayList<Edge2Certificate>();
        /** Current enclosing block. */
        private Block container;

        static final int TARGET_MASK = 0x5555;

    }

    /**
     * Certificate for value nodes. This takes the actual node identity into
     * account.
     * @author Arend Rensink
     * @version $Revision $
     */
    static class ValueNodeCertificate extends NodeCertificate {
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

        private final ValueNode node;
    }

    static class EdgeCertificate implements Certificate<Edge> {
        EdgeCertificate(Edge edge, NodeCertificate sourceCert) {
            this.edge = edge;
            this.sourceCert = sourceCert;
            this.value = edge.label().hashCode();
            sourceCert.addSelf(this);
        }

        final public Edge getElement() {
            return this.edge;
        }

        @Override
        public int hashCode() {
            return this.sourceCert.hashCode() + this.edge.label().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof EdgeCertificate
                && ((EdgeCertificate) obj).sourceCert.equals(this.sourceCert)
                && ((EdgeCertificate) obj).edge.label().equals(
                    this.edge.label());
        }

        @Override
        public String toString() {
            return "[" + getSource() + "," + getElement().label() + "("
                + this.edge.label().hashCode() + ")]";
        }

        final int getValue() {
            return this.value;
        }

        final NodeCertificate getSource() {
            return this.sourceCert;
        }

        private final Edge edge;
        private final NodeCertificate sourceCert;
        private final int value;
    }

    static class Edge2Certificate extends EdgeCertificate {
        Edge2Certificate(Edge edge, NodeCertificate sourceCert,
                NodeCertificate targetCert) {
            super(edge, sourceCert);
            this.targetCert = targetCert;
            this.labelIndex = edge.label().hashCode();
            sourceCert.addOutgoing(this);
            targetCert.addIncoming(this);
        }

        @Override
        public int hashCode() {
            return super.hashCode() + (getTarget().hashCode() << 2);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Edge2Certificate && super.equals(obj)
                && ((Edge2Certificate) obj).getTarget().equals(getTarget());
        }

        @Override
        public String toString() {
            return "[" + getSource() + "," + getElement().label() + "("
                + this.labelIndex + ")," + getTarget() + "]";
        }

        private NodeCertificate getTarget() {
            return this.targetCert;
        }

        void updateSource() {
            int shift = (this.labelIndex & 0xf) + 1;
            int targetValue = this.targetCert.getValue();
            int increment =
                ((targetValue << shift) | (targetValue >>> (INT_WIDTH - shift)))
                    + this.labelIndex;
            getSource().addNextValue(2 * increment);
        }

        void updateTarget() {
            int shift = 8;
            int sourceValue = getSource().getValue();
            int increment =
                ((sourceValue << shift) | (sourceValue >>> (INT_WIDTH - shift)))
                    + this.labelIndex;
            getTarget().addNextValue(-3 * increment);
        }

        /** The node certificate of the edge target. */
        private final NodeCertificate targetCert;
        /**
         * The hash code of the original edge label.
         */
        private final int labelIndex;
    }

    /** Represents a block of nodes in some partition. */
    class Block implements Comparable<Block> {
        Block(int value) {
            // this.head = new NodeCertificate(this);
            this.nodes = new LinkedList<NodeCertificate>();
            this.value = value;
            PaigeTarjanMcKay.this.graphCertificate += value;
        }

        /** Indicates if this block is in the list of splitters. */
        boolean isSplitter() {
            return this.splitter;
        }

        /** Records that this block has been inserted in the list of splitters. */
        void setSplitter(boolean splitter) {
            this.splitter = splitter;
        }

        /**
         * Starts splitting this block.
         * @return <code>true</code> if the split has just started,
         *         <code>false</code> if splitting had already started before.
         */
        final boolean startSplit() {
            if (!this.splitting) {
                return this.splitting = true;
            } else {
                return false;
            }
        }

        /**
         * Divides all the nodes in this block over new blocks, depending on
         * their value, and returns an array of all the new blocks.
         */
        Block[] split() {
            Map<Integer,Block> blockMap = new HashMap<Integer,Block>();
            Block block = null;
            for (NodeCertificate node : this.nodes) {
                node.setNewValue();
                block = blockMap.get(node.getValue());
                if (block == null) {
                    blockMap.put(node.getValue(), block =
                        new Block(node.getValue()));
                }
                block.append(node);
            }
            if (blockMap.size() == 1) {
                // the one block is given by block
                this.value = block.value;
                this.nodes = block.nodes;
                this.splitting = false;
                return EMPTY_BLOCK_ARRAY;
            } else {
                this.nodes = EMPTY_NODE_LIST;
                // collect and order the sub-blocks
                Block[] result = new Block[blockMap.size()];
                blockMap.values().toArray(result);
                Arrays.sort(result);
                return result;
            }
        }

        /**
         * Appends a given node certificate to this block, and sets the
         * certificate's block to this.
         */
        final void append(NodeCertificate node) {
            this.nodes.add(node);
            node.setBlock(this);
        }

        /** Returns the current size of the block. */
        final int size() {
            return this.nodes.size();
        }

        List<NodeCertificate> getNodes() {
            return this.nodes;
        }

        /**
         * A block is smaller than another if it has fewer nodes, or a smaller
         * hash value.
         */
        public int compareTo(Block other) {
            int result = size() - other.size();
            if (result != 0) {
                return result;
            }
            return this.value < other.value ? -1 : this.value > other.value
                    ? +1 : 0;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Block && ((Block) obj).value == this.value;
        }

        @Override
        public int hashCode() {
            return this.value;
        }

        @Override
        public String toString() {
            return this.nodes.toString();
        }

        /** The distinguishing value of this block. */
        private int value;
        /** List of marked nodes, in case the block is currently being split. */
        private List<NodeCertificate> nodes;
        /** Flag indicating if this block is in the list of splitters. */
        private boolean splitter;
        /** Flag indicating if this block is currently splitting. */
        private boolean splitting;
    }
}
