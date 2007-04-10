// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: Bisimulator.java,v 1.4 2007-03-27 14:18:33 rensink Exp $
 */
package groove.graph.iso;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.UnaryEdge;
import groove.graph.algebra.ValueNode;
import groove.util.Reporter;
import groove.util.IntSet;
import groove.util.TreeIntSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements an algorithm to partition a given graph into sets of bisimilar graph elements
 * (i.e., nodes and edges).
 * The result is available as a mapping from graph elemens to "certificate" objects;
 * two edges are bisimilar if they map to the same (i.e., <tt>equal</tt>) certificate.  
 * @author Arend Rensink
 * @version $Revision: 1.4 $
 */
public class Bisimulator implements CertificateStrategy {
	/**
	 * The resultion of the tree-based certificate store.
	 */
	static private final int TREE_RESOLUTION = 3;
	/**
	 * Store for node certificates, to count the number of partitions 
	 */
	static private final IntSet certStore = new TreeIntSet(TREE_RESOLUTION);
	/**
     * Array to record the number of iterations done in computing certificates. 
     */
    static private int[] iterateCount = new int[0];
    
    /**
     * Records that the computation of the certificates has taken a certain number of iterations.
     * @param count the number of iterations
     */
    static private void recordIterateCount(int count) {
        if (iterateCount.length < count+1) {
            int[] newIterateCount = new int[count+1];
            System.arraycopy(iterateCount, 0, newIterateCount, 0, iterateCount.length);
            iterateCount = newIterateCount;
        }
        iterateCount[count]++;
    }
    
    /**
     * Returns an array that, at every index, contains the number of times 
     * that the computation of certificates has taken a number of iterations
     * equal to the index.
     */
    static public List<Integer> getIterateCount() {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < iterateCount.length; i++) {
            result.add(iterateCount[i]);
        }
        return result;
    }

    /**
     * Superclass of graph element certificates.
     */
    public static abstract class Certificate {
    	/**
    	 * Creates a certificate, with a link to the next one in the list.
    	 * @param next
    	 */
    	public Certificate(Certificate next) {
    		this.next = next;
    	}
    	
        /**
         * Returns the certificate value.
         * Note that this means the hash code is not constant during the
         * initial phase, and so no hash sets or maps should be used.
         * @ensure <tt>result == getValue()</tt>
         * @see #getValue()
         */
    	@Override
        public int hashCode() {
            return value;
        }
        
        /**
         * Tests if the other is a {@link Certificate} with the same value.
         */
    	@Override
        public boolean equals(Object obj) {
            return obj instanceof Certificate && (value == ((Certificate) obj).value);
        }

        /**
         * Returns the current certificate value.
         */
        public int getValue() {
            return value;
        }
        
        /**
         * Computes, stores and returns a new value for this certificate.
         * The computation is done by invoking {@link #computeNewValue()}.
         * @return the freshly computed new value
         * @see #computeNewValue()
         */
        protected int setNewValue() {
            return value = computeNewValue();
        }
        
        /**
         * Callback method that provides the new value at each iteration.
         * @return the freshly computed new value
         * @see #setNewValue()
         */
        abstract protected int computeNewValue();

        /** The current value, which determines the hash code. */
        protected int value;
        /**
         * The next certificate in the list.
         */
        final Certificate next;
    }
    
    /**
     * Class of nodes that carry (and are identified with) an integer certificate value.
     * @author Arend Rensink
     * @version $Revision: 1.4 $
     */
    static private class CertificateNode extends Certificate {
    	/** Initial node value to provide a better spread of hash codes. */
    	static private final int INIT_NODE_VALUE = 0x126b;
        /**
         * Constructs a new certificate node.
         * The incidence count (i.e., the number of incident edges) is passed in as a parameter.
         * The initial value is set to the incidence count.
         */
        public CertificateNode(CertificateNode next) {
        	super(next);
        	value = INIT_NODE_VALUE;
        }

    	@Override
        public String toString() {
            return "c" + value;
        }
        
        /**
         * Returns <tt>true</tt> of <tt>obj</tt> is also a {@link CertificateNode}
         * and has the same value as this one.
         * @see #getValue()
         */
    	@Override
        public boolean equals(Object obj) {
            return obj instanceof CertificateNode && (value == ((Certificate) obj).value);
        }

        /**
         * The new value for this certificate node
         * is the sum of the values of the incident certificate edges.
         */
    	@Override
        protected int computeNewValue() {
        	int result = nextValue ^ value;
        	nextValue = 0;
        	return result;
        }

        /**
         * Adds to the current value.
         * Used during construction, to record the intitial value of incident edges.
         */
        protected void addValue(int inc) {
            value += inc;
        }
        
        /**
         * Adds a certain value to {@link #nextValue}.
         */
        protected void addNextValue(int value) {
        	nextValue += value;
        }
        
        /** The value for the next invocation of {@link #computeNewValue()} */
        private int nextValue;
    }
    
    /**
     * An edge with certificate nodes as endpoints.
     * The hash code is computed dynamically, on the basis of the current
     * certificate node value.
     * @author Arend Rensink
     * @version $Revision: 1.4 $
     */
    static private class CertificateEdge extends Certificate {
//        /** Constructs a certificate edge for a predicate (i.e., a unary edge). */
//        public CertificateEdge(CertificateNode source, DefaultLabel label) {
//            this.source = source;
//            this.target = source;
//            this.labelIndex = label.getIndex();
//            source.add(value);
//            initValue();
//        }

        /**
         * Constructs a certificate for a binary edge.
         * @param source The source certificate node
         * @param label The target certificate node
         * @param target The label of the original edge
         */
        public CertificateEdge(CertificateNode source, Label label, CertificateNode target, CertificateEdge next) {
        	super(next);
            this.source = source;
            this.target = source == target ? null : target;
            this.label = label;
            this.labelIndex = label.hashCode();
            initValue();
            source.addValue(value);
            target.addValue(value << 1);
        }

    	@Override
        public String toString() {
            return "["+source+","+label+"("+labelIndex+"),"+target+"]";
        }
        
        /**
         * Returns <tt>true</tt> if <tt>obj</tt> is also a {@link CertificateEdge}
         * and has the same value, as well as the same source and target values, as this one.
         * @see #getValue()
         */
    	@Override
        public boolean equals(Object obj) {
            if (obj instanceof CertificateEdge) {
                CertificateEdge other = (CertificateEdge) obj; 
                if (value != other.value || labelIndex != other.labelIndex || source.value != other.source.value) {
                	return false;
                } else if (target == source) {
                	return other.target == other.source;
                } else {
                	return target.value == other.target.value;
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
            int targetShift = (labelIndex & 0xf) + 1;
            int sourceHashCode = source.value;
            int targetHashCode = target.value;
            int result = ((sourceHashCode << 8) | (sourceHashCode >>> 24))
            	+ ((targetHashCode << targetShift) | (targetHashCode >>> targetShift))
            	+ value;
            source.nextValue += 2*result;
            target.nextValue -= 3*result;
            return result;
        }
        
        /**
		 * Initializes the value. Callback method from the constructor. This
		 * implementation takes the label index as the initial value.
		 */
        protected void initValue() {
            value = labelIndex;
        }
        
        /**
         * Returns the label index of the underlying label for this certificate.
         */
        protected int getLabelIndex() {
            return labelIndex;
        }

        /** The source certificate for the edge. */
        private final CertificateNode source;
        /** The target certificate for the edge; may be <tt>null</tt>. */
        private final CertificateNode target;
        /**
         * The hash code of the original edge label.
         */
        private final int labelIndex;
        /**
         * The original edge label.
         */
        private final Label label;
    }
    
    /**
     * An edge with only one endpoint.
     * The hash code is computed dynamically, on the basis of the current
     * certificate node value.
     * @author Arend Rensink
     * @version $Revision: 1.4 $
     */
    static private class CertificateFlag extends Certificate {
        /** Constructs a certificate edge for a predicate (i.e., a unary edge). */
        public CertificateFlag(CertificateNode source, Label label, CertificateFlag next) {
        	super(next);
            this.source = source;
            this.label = label;
            this.labelIndex = label.hashCode();
            initValue();
            source.addValue(value);
        }

    	@Override
        public String toString() {
            return "["+source+","+label+"("+labelIndex+")]";
        }

        /**
         * Returns <tt>true</tt> if <tt>obj</tt> is also a {@link CertificateFlag}
         * and has the same value, as well as the same source and target values, as this one.
         * @see #getValue()
         */
    	@Override
        public boolean equals(Object obj) {
            if (obj instanceof CertificateFlag) {
                CertificateFlag other = (CertificateFlag) obj; 
                return (value == other.value && labelIndex == other.labelIndex);
            } else {
                return false;
            }
        }

        /**
         * Computes the value on the basis of the end nodes and the label index.
         */
    	@Override
        protected int computeNewValue() {
            int sourceHashCode = source.hashCode();
            return (sourceHashCode << 8) + (sourceHashCode >> 24) + value;
        }
        
        /**
		 * Initializes the value. Callback method from the constructor. This
		 * implementation takes the label index as the initial value.
		 */
        protected void initValue() {
            value = labelIndex << 4;
        }
        
        /**
         * Returns the label index of the underlying label for this certificate.
         */
        protected int getLabelIndex() {
            return labelIndex;
        }

        /** The source certificate for the edge. */
        private final CertificateNode source;
        /**
         * The hash code of the original edge label.
         */
        private final int labelIndex;
        /**
         * The original edge label.
         */
        private final Label label;
    }

    /**
     * Constructs a new bisimulation strategy, on the basis of a given graph.
     * @param graph the underlying graph for the bisimulation strategy;
     * should not be <tt>null</tt>
     */
    public Bisimulator(Graph graph) {
        this.graph = graph;
    }

    public Graph getGraph() {
        return graph;
    }

    /**
     * The result is computed by first initializing arrays of certificates
     * and subsequently iterating over those arrays until the number of
     * distinct certificate values does not grow any more.
     * Each iteration first recomputes the edge certificates using the
     * current node certificate values, and then the node certificates
     * using the current edge certificate values. 
     */
    public Map<Element, Object> getCertificateMap() {
        reporter.start(GET_CERTIFICATE_MAP);
        // check if the map has been computed before
        if (certificateMap == null) {
            getGraphCertificate();
        }
        reporter.stop();
        return certificateMap;
    }

    /**
     * Returns the pre-computed partition map, if any.
     * If none is stored, computes, stores and returns the inverse of the certificate map.
     * @see #getCertificateMap()
     */
    public PartitionMap getPartitionMap() {
        // check if the map has been computed before
        if (this.partitionMap == null) {
    		// no; go ahead and compute it
            this.partitionMap = computePartitionMap();
        }
        return this.partitionMap;
    }

	/**
	 * Computes the partition map, i.e., the mapping from certificates
	 * to sets of graph elements having those certificates. 
	 */
	private PartitionMap computePartitionMap() {
        reporter.start(GET_PARTITION_MAP);
		PartitionMap result = new PartitionMap();
		// invert the certificate map
		reporter.stop();
		Map<Element,Object> certMap = getCertificateMap();
		reporter.restart(GET_PARTITION_MAP);
		for (Map.Entry<Element,Object> certEntry: certMap.entrySet()) {
		    Element key = certEntry.getKey();
		    Object certificate = certEntry.getValue();
		    result.add(certificate, key);
		}
        reporter.stop();
		return result;
	}

    /**
     * The graph certificate is computed as the sum of the node and edge certificates.
     */
    public Object getGraphCertificate() {
        reporter.start(GET_GRAPH_CERTIFICATE);
        // check if the certificate has been computed before
        if (graphCertificate == null) {
            computeCertificates();
        }
        reporter.stop();
        // return the computed certificate
        return graphCertificate;
    }

    public CertificateStrategy newInstance(Graph graph) {
        return new Bisimulator(graph);
    }

    /**
     * This method only returns a useful result after the graph certificate or
     * partition map has been calculated.
     */
    public int getNodePartitionCount() {
    	if (nodePartitionCount == 0) {
    		computeCertificates();
    	}
    	return nodePartitionCount;
    }

    /** Computes the node and edge certificate arrays. */
    private void computeCertificates() {
        reporter.start(COMPUTE_CERTIFICATES);
        // we compute the certificate map 
        initCertificates();
        iterateCertificates();
        // so far we have done nothing with the flags, so 
        // give them a chance to get their hash code right
        Certificate flagCerts = flagCertList;
        while (flagCerts != null) {
            flagCerts.setNewValue();
            flagCerts = flagCerts.next;
        }
        dispose();
        reporter.stop();
    }
    
    /**
     * Initializes the node and edge certificate arrays,
     * and the certificate map.
     */
    private void initCertificates() {
        reporter.start(INIT_CERTIFICATES);
        int nodeCount = graph.nodeCount();
        int edgeCount = graph.edgeCount();
        // mapping from graph elements to the corresponding certificate elements
        certificateMap = new HashMap<Element,Object>(nodeCount+edgeCount);
        // create the edge certificates
        if (TIME) reporter.start(EDGE_CERTS);
        for (Edge edge: graph.edgeSet()) {
            initCertEdge(edge);
		}
		if (TIME) reporter.stop();
		assert nodeCertCount <= nodeCount : "Number of node certificates ("+nodeCertCount+") for "+graph.edgeSet()+" exceeds number of nodes ("+nodeCount+") in "+graph.nodeSet();
		// if there were loose nodes, we don't have certificates for all nodes
		if (nodeCertCount < nodeCount) {
			if (TIME) reporter.start(NODE_CERTS);
			for (Node node: graph.nodeSet()) {
				getCertNode(node);
			}
        if (TIME) reporter.stop();
		}
        reporter.stop();
    }

    /** 
     * Creates a {@link CertificateNode} for a given graph node,
     * and inserts into the certificate node map. 
     */
    private CertificateNode initCertNode(final Node node) {
    	if (TIME) reporter.start(INIT_CERT_NODE);
        CertificateNode certNode;
        // if the node is an instance of OperationNode, the certificate
        // of this node also depends on the operation represented by it
        // therefore, the computeNewValue()-method of class
        // CertificateNode must be overridden
        if (node instanceof ValueNode) {
            certNode = new CertificateNode(nodeCertList) {
            	@Override
                protected int computeNewValue() {
                    // only take the last 8 bits of the operation-hashcode
                    int operationHashCode = ((ValueNode) node).getOperation().hashCode() & 127;
                    return super.computeNewValue() + operationHashCode;
                }
            };
            certNode.setNewValue();
        }
        else {
            certNode = new CertificateNode(nodeCertList);
        }
        Object oldObject = certificateMap.put(node, certNode);
        assert oldObject == null : "Certificate node "+certNode+" for "+node+" seems to override "+oldObject;
        nodeCertList = certNode;
        nodeCertCount++;
        if (TIME) reporter.stop();
        return certNode;
    }

    /**
     * Retrieves a certificate node image for a given graph node from the map,
     * creating the certificate node first if necessary. 
     */
    private CertificateNode getCertNode(final Node node) {
    	CertificateNode result = (CertificateNode) certificateMap.get(node);
    	if (result == null) {
    		return initCertNode(node);
    	} else {
    		return result;
    	}
    }

    /** 
     * Creates a {@link CertificateEdge} for a given graph edge,
     * and inserts into the certificate edge map. 
     */
    private void initCertEdge(Edge edge) {
    	if (TIME) reporter.start(INIT_CERT_EDGE);
        assert ! certificateMap.containsKey(edge) : "Edge "+edge+" already in certificate map "+certificateMap;
        Certificate edgeImage;
        Label label = edge.label();
        Node source = edge.source();
        CertificateNode sourceImage = getCertNode(source); // (CertificateNode) certificateMap.get(source);
        assert sourceImage != null : "Edge source of " + edge + " not found in "
                + certificateMap + "; so not in the node set " + graph.nodeSet() + " of "
                + graph;
        if (edge instanceof UnaryEdge || source == edge.opposite()) {
            edgeImage = flagCertList = new CertificateFlag(sourceImage, label, flagCertList);
        } else {
            CertificateNode targetImage = getCertNode(edge.opposite()); //(CertificateNode) certificateMap
//                    .get(edge.opposite());
            assert targetImage != null : "Edge target of " + edge + " not found in "
                    + certificateMap + "; so not in the node set " + graph.nodeSet() + " of "
                    + graph;
            edgeImage = edgeCertList = new CertificateEdge(sourceImage, label, targetImage, edgeCertList);
        }
        Object oldObject = certificateMap.put(edge, edgeImage);
        assert oldObject == null : "Certificate node "+edgeImage+" for "+edge+" seems to override "+oldObject;
        if (TIME) reporter.stop();
    }
    
    /** Iterates node certificates until this results in a stable partitioning. */
    private synchronized void iterateCertificates() {
    	// get local copies of attributes for speedup
    	IntSet certStore = Bisimulator.certStore;
    	Certificate nodeCertList = this.nodeCertList;
    	Certificate edgeCertList = this.edgeCertList;
    	int nodeCertCount = this.nodeCertCount;
    	int nodePartitionCount = 0;
    	int certificateValue;
        // collect and then count the number of certificates
        boolean goOn;
        int iterateCount = 0;
        do {
            reporter.start(ITERATE_CERTIFICATES);
            certificateValue = nodeCertCount;
            certStore.clear(nodeCertCount);
            // first compute the new edge certificates
            Certificate edgeCerts = edgeCertList;
            while (edgeCerts != null) {
                certificateValue += edgeCerts.setNewValue();
                edgeCerts = edgeCerts.next;
            }
            // now compute the new node certificates
            Certificate nodeCerts = nodeCertList;
            while (nodeCerts != null) {
                int newCert = nodeCerts.setNewValue();
                if (iterateCount > 0 && nodePartitionCount < nodeCertCount) {
                	certStore.add(newCert);
                }
                certificateValue += newCert;
                nodeCerts = nodeCerts.next;
            }
            int newNodePartitionCount = certStore.size();
            // we stop the iteration when the number of partitions has not grown
            // moreover, when the number of partitions equals the number of nodes then
            // it cannot grow, so we might as well stop straight away
            // note, however, that doing so gives rise to more false positives
            // which, on the other hand, are easily recognisable as such
            goOn = iterateCount == 0 || newNodePartitionCount > nodePartitionCount;// && newNodePartitionCount < nodeCount;
            if (nodePartitionCount < nodeCertCount) {
            	nodePartitionCount = newNodePartitionCount;
            }
            iterateCount++;
            reporter.stop();
        } while (goOn);
        this.nodePartitionCount = nodePartitionCount;
        this.graphCertificate = new Long(certificateValue);
        recordIterateCount(iterateCount);
    }
    
    /**
     * Abandons the auxiliary arrays to free some space.
     */
    private void dispose() {
    	nodeCertList = null;
    	edgeCertList = null;
    	flagCertList = null;
//    	certNodes = Bisimulator.certNodes;
//    	certNodeCount = Bisimulator.certNodeCount;
//        for (int i = 0; i < certNodeCount; i++) {
//            certNodes[i].dispose();
//        }
    }

    /** The underlying graph */
    private final Graph graph;
    /** The pre-computed graph certificate, if any. */
    private Object graphCertificate;
    /** The pre-computed crtificate maps, if any. */
    private Map<Element,Object> certificateMap;
    /** The pre-computed partition map, if any. */
    private PartitionMap partitionMap;
    /**
     * The number of pre-computed node partitions.
     */
    private int nodePartitionCount;
    /**
     * The list of node certificates in this bisimulator.
     */
    private CertificateNode nodeCertList;
    /** The number of elements in {@link #nodeCertList}. */
    private int nodeCertCount;
    /**
     * The list of edge certificates in this bisimulator.
     */
    private CertificateEdge edgeCertList;
    /**
     * The list of flag certificates in this bisimulator.
     */
    private CertificateFlag flagCertList;
    

    // --------------------------- reporter definitions ---------------------
    /** Reporter instance to profile methods of this class. */
    static public final Reporter reporter = Reporter.register(Bisimulator.class);
    static public final int COMPUTE_CERTIFICATES = reporter.newMethod("computeCertificates()");
    static protected final int INIT_CERTIFICATES = reporter.newMethod("initCertificates()");
    static protected final int NODE_CERTS = reporter.newMethod("Nested node certs");
    static protected final int EDGE_CERTS = reporter.newMethod("Nested edge certs");
    static protected final int INIT_CERT_NODE = reporter.newMethod("initCertNode()");
    static protected final int INIT_CERT_EDGE = reporter.newMethod("initCertEdge()");
    static protected final int ITERATE_CERTIFICATES = reporter.newMethod("iterateCertificates()");
    static protected final int GET_CERTIFICATE_MAP = reporter.newMethod("getCertificateMap()");
    static protected final int GET_PARTITION_MAP = reporter.newMethod("getPartitionMap()");
    static protected final int GET_GRAPH_CERTIFICATE = reporter.newMethod("getGraphCertificate()");
    
    static private final boolean TIME = false;
}
