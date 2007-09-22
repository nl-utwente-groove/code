/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id: DefaultIsoChecker.java,v 1.19 2007-09-22 09:10:38 rensink Exp $
 */
package groove.graph.iso;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.graph.iso.CertificateStrategy.Certificate;
import groove.util.Reporter;
import groove.util.SmallCollection;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * Implementation of an isomorphism checking algorithm that first tries to
 * decide isomorphism directly on the basis of a {@link groove.graph.iso.CertificateStrategy}. 
 * @author Arend Rensink
 * @version $Revision: 1.19 $
 */
public class DefaultIsoChecker implements IsoChecker {
    /** Empty constructor, for the singleton in stance of this class. */
    private DefaultIsoChecker() {
        // empty
    }
	public boolean areIsomorphic(Graph dom, Graph cod) {
		boolean result;
		// pre-calculate the node counts to take the time for 
		// constructing the graph out of the isomorphism check time
		int domNodeCount = dom.nodeCount();
		int codNodeCount = cod.nodeCount();
        reporter.start(ISO_CHECK);
        if (domNodeCount != codNodeCount || dom.edgeCount() != cod.edgeCount()) {
            distinctSizeCount++;
        	result = false;
        } else if (areGraphEqual(dom, cod)) {
            equalGraphsCount++;
        	result = true;
        } else {
			CertificateStrategy domCertifier = dom.getCertifier();
			CertificateStrategy codCertifier = cod.getCertifier();
			if (!domCertifier.getGraphCertificate().equals(codCertifier.getGraphCertificate())) {
				intCertOverlap++;
				result = false;
			} else if (hasDistinctCerts(codCertifier)) {
				reporter.start(ISO_CERT_CHECK);
				if (hasDistinctCerts(domCertifier)) {
					result = areCertEqual(domCertifier, codCertifier);
				} else {
					result = false;
				}
				reporter.stop();
				if (result) {
					equalCertsCount++;
				} else {
					distinctCertsCount++;
				}
			} else {
				reporter.start(ISO_SIM_CHECK);
				if (getNodePartitionCount(domCertifier) == getNodePartitionCount(codCertifier)) {
					result = hasIsomorphism(dom, cod);
				} else {
					result = false;
				}
				reporter.stop();
				if (result) {
					equalSimCount++;
				} else {
					distinctSimCount++;
				}
			}
		}
        reporter.stop();
        totalCheckCount++;
        return result;
	}

	/**
	 * Tries to construct an isomorphism between the two given graphs,
	 * and reports if this succeeds.
	 */
	public boolean hasIsomorphism(Graph dom, Graph cod) {
		return computeIsomorphism(dom, cod) != null;
	}
	
	/**
	 * Tries to construct an isomorphism between the two given graphs.
	 * The result is a bijective mapping from the nodes and edges
	 * of the source graph to those of the target graph, or <code>null</code>
	 * if no such mapping could be found. 
	 * @param dom the first graph to be compared
	 * @param cod the second graph to be compared
	 */
	public NodeEdgeMap getIsomorphism(Graph dom, Graph cod) {
		NodeEdgeMap result = computeIsomorphism(dom, cod);
		if (result != null && result.nodeMap().size() != dom.nodeCount()) {
	        // there's sure to be an isomorphism, but we have to add the isolated nodes
			PartitionMap<Node> codPartitionMap = cod.getCertifier().getNodePartitionMap();
			Set<Node> usedNodeImages = new HashSet<Node>();
			Certificate<Node>[] nodeCerts = dom.getCertifier().getNodeCertificates();
			for (Certificate<Node> nodeCert: nodeCerts) {
				Node node = nodeCert.getElement();
				if (!result.containsKey(node)) {
					// this is an isolated node
					SmallCollection<Node> nodeImages = codPartitionMap.get(nodeCert);
					if (nodeImages.isSingleton()) {
						// it follows that there is only one isolated node
						result.putNode(node, nodeImages.getSingleton());
						break;
					} else {
						// find an unused node
						for (Node nodeImage: nodeImages) {
							if (!usedNodeImages.contains(nodeImage)) {
								result.putNode(node, nodeImage);
								usedNodeImages.add(nodeImage);
								break;
							}
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Tries to construct an isomorphism between the two given graphs.
	 * The result is a bijective mapping from the nodes and edges
	 * of the source graph to those of the target graph, or <code>null</code>
	 * if no such mapping could be found. 
	 * @param dom the first graph to be compared
	 * @param cod the second graph to be compared
	 */
	private NodeEdgeMap computeIsomorphism(Graph dom, Graph cod) {
		// make sure the graphs are of the same size
		if (dom.nodeCount() != cod.nodeCount() || dom.edgeCount() != cod.edgeCount()) {
			return null;
		}
		NodeEdgeMap result = new NodeEdgeHashMap();	
		PartitionMap<Edge> codPartitionMap = cod.getCertifier().getEdgePartitionMap();
		// the mapping has to be injective, so we remember the used cod nodes
		Set<Node> usedNodeImages = new HashSet<Node>();
		// the set of dom nodes that have an image in result, but whose incident
		// images possibly don't
		Set<Node> connectedNodes = new HashSet<Node>();
		Map<Edge, Collection<Edge>> edgeImageMap = new HashMap<Edge, Collection<Edge>>();
		Certificate<Edge>[] edgeCerts = dom.getCertifier().getEdgeCertificates();
		for (Certificate<Edge> edgeCert: edgeCerts) {
			SmallCollection<Edge> images = codPartitionMap.get(edgeCert);
			if (images == null) {
				return null;
			} else if (images.isSingleton()) {
				if (!setEdge(edgeCert.getElement(),
						images.getSingleton(),
						result,
						connectedNodes,
						usedNodeImages)) {
					return null;
				}
			} else {
				edgeImageMap.put(edgeCert.getElement(), images);
			}
		}
		while (!edgeImageMap.isEmpty()) {
			if (connectedNodes.isEmpty()) {
				// there are no edges connected to the part of the graph that
				// is already mapped;
				Iterator<Map.Entry<Edge,Collection<Edge>>> edgeImageEntryIter = edgeImageMap.entrySet().iterator();
				Map.Entry<Edge, Collection<Edge>> edgeImageEntry = edgeImageEntryIter.next();
				edgeImageEntryIter.remove();
				Edge domEdge = edgeImageEntry.getKey();
				// search a suitable value
				boolean found = false;
				for (Edge codEdge: edgeImageEntry.getValue()) {
					search:
					for (Node valueEnd: codEdge.ends()) {
						if (usedNodeImages.contains(valueEnd)) {
							continue search;
						}
					}
					// this image is OK
					for (int i = 0; i < domEdge.endCount(); i++) {
						Node domNode = domEdge.end(i);
						connectedNodes.add(domNode);
						Node codNode = codEdge.end(i);
						usedNodeImages.add(codNode);
						result.putNode(domNode, codNode);
					}
					result.putEdge(domEdge, codEdge);
					found = true;
					break;
				}
				if (!found) {
					return null;
				}
			} else {
				Iterator<Node> connectedNodeIter = connectedNodes.iterator();
				Node connectedNode = connectedNodeIter.next();
				connectedNodeIter.remove();
				for (Edge edge : dom.edgeSet(connectedNode)) {
					Collection<Edge> images = edgeImageMap.remove(edge);
					if (images != null
							&& !selectEdge(edge,
									images,
									result,
									connectedNodes,
									usedNodeImages)) {
						// the edge is unmapped, and no suitable image can be found
						return null;
					}
				}
			}
		}
		return result;
	}

	/** 
	 * Inserts an edge from a set of possible edges into the result mapping,
	 * if one can be found that is consistent with the current state.
	 * @param key the dom edge to be inserted
	 * @param values the set of cod edges that should be tried as image of <code>key</code>
	 * @param result the result map
	 * @param newlyMappedDomNodes the set of dom nodes that are mapped but may have unmapped incident edges
	 * @param usedCodNodes the set of node values in <code>result</code>
	 * @return <code>true</code> if the key/value-pair was successfully added to <code>result</code>
	 */
	private boolean selectEdge(Edge key, Collection<Edge> values, NodeEdgeMap result, Set<Node> newlyMappedDomNodes, Set<Node> usedCodNodes) {
		int arity = key.endCount();
		Node[] nodeImages = new Node[arity];
		for (int i = 0; i < arity; i++) {
			nodeImages[i] = result.getNode(key.end(i));
		}
		for (Edge value : values) {
			boolean correct = true;
			for (int i = 0; correct && i < key.endCount(); i++) {
				if (nodeImages[i] == null) {
					correct = !usedCodNodes.contains(value.end(i));
				} else {
					correct = nodeImages[i] == value.end(i);
				}
			}
			if (correct) {
				for (int i = 0; correct && i < key.endCount(); i++) {
					if (nodeImages[i] == null) {
						result.putNode(key.end(i), value.end(i));
						newlyMappedDomNodes.add(key.end(i));
						usedCodNodes.add(value.end(i));
					}
				}
				result.putEdge(key, value);
				return true;
			}
		}
		return true;
	}

	/** 
	 * Inserts an edge into the result mapping,
	 * testing if the resulting end node mapping is consistent with the
	 * current state.
	 * @param key the dom edge to be inserted
	 * @param value the cod edge that is the image of <code>key</code>
	 * @param result the result map
	 * @param newlyMappedDomNodes the set of dom nodes that are mapped but may have unmapped incident edges
	 * @param usedCodNodes the set of node values in <code>result</code>
	 * @return <code>true</code> if the key/value-pair was successfully added to <code>result</code>
	 */
	private boolean setEdge(Edge key, Edge value, NodeEdgeMap result, Set<Node> newlyMappedDomNodes, Set<Node> usedCodNodes) {
		for (int i = 0; i < key.endCount(); i++) {
			Node end = key.end(i);
			Node endImage = value.end(i);
			Node oldEndImage = result.putNode(end, endImage);
			if (oldEndImage == null) {
				if (!usedCodNodes.add(endImage)) {
					return false;
				}
				newlyMappedDomNodes.add(end);
			} else if (oldEndImage != endImage) {
				return false;
			}
		}
		result.putEdge(key, value);
		return true;
	}
	
	/**
	 * Tests if the elements of a graph have all different certificates.
	 * If this holds, then {@link #areCertEqual(CertificateStrategy, CertificateStrategy)} can be
	 * called to check for isomorphism.
	 * @param certifier the graph to be tested
	 * @return <code>true</code> if <code>graph</code> has distinct certificates
	 */
	private boolean hasDistinctCerts(CertificateStrategy certifier) {
		return certifier.getNodePartitionMap().isOneToOne();
	}

	/**
	 * Convenience method for <code>graph.getCertificateStrategy().getNodePartitionCount()</code>.
	 */
	private int getNodePartitionCount(CertificateStrategy certifier) {
		return certifier.getNodePartitionCount();
	}
	
	/**
	 * Tests if an isomorphism can be constructed on the basis of distinct certificates.
	 * It is assumed that <code>hasDistinctCerts(dom)</code> holds. 
	 * @param dom the first graph to be tested
	 * @param cod the second graph to be tested
	 */
	private boolean areCertEqual(CertificateStrategy dom, CertificateStrategy cod) {
		boolean result;
		reporter.stop();
		reporter.stop();
		// the certificates uniquely identify the dom elements;
		// it suffices to test if this gives rise to a consistent one-to-one node map
//		Certificate<Node>[] nodeCerts = dom.getNodeCertificates();
		Certificate<Edge>[] edgeCerts = dom.getEdgeCertificates();
		PartitionMap<Edge> codPartitionMap = cod.getEdgePartitionMap();
		reporter.restart(ISO_CHECK);
		reporter.restart(ISO_CERT_CHECK);
		result = true;
		// map to store dom-to-cod node mapping
		Map<Node,Node> nodeMap = new HashMap<Node,Node>();
//		// iterate over the dom node certificates
//		int nodeCount = nodeCerts.length;
//		for (int i = 0; result && i < nodeCount; i++) {
//			Certificate<Node> domNodeCert = nodeCerts[i];
//			SmallCollection<Node> image = codPartitionMap.get(domNodeCert);
//			result = image.isSingleton();
//			if (result) {
//				nodeMap.put(domNodeCert.getElement(), image.getSingleton());
//			}
//		}
		// iterate over the dom edge certificates
		int edgeCount = edgeCerts.length;
		for (int i = 0; result && i < edgeCount; i++) {
			Certificate<Edge> domEdgeCert = edgeCerts[i];
			SmallCollection<Edge> image = codPartitionMap.get(domEdgeCert);
			result = image != null && image.isSingleton();
			if (result) {
				Edge edgeKey = domEdgeCert.getElement();
				Edge edgeImage = image.getSingleton();
				int endCount = edgeKey.endCount();
				for (int end = 0; result && end < endCount; end++) {
					Node endImage = nodeMap.get(edgeKey.end(end));
					if (endImage == null) {
					    nodeMap.put(edgeKey.end(end), endImage);
					} else {
					    result = endImage.equals(edgeImage.end(end));
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * This method wraps a node and edge set equality test on two graphs,
	 * under the assumption that the node and edge counts are already known
	 * to coincide.
	 */
	private boolean areGraphEqual(Graph dom, Graph cod) {
		reporter.start(EQUALS_TEST);
//		boolean result = ((DeltaGraph) dom).equalNodeEdgeSets((DeltaGraph)cod);
		Set<?> domEdgeSet = dom.edgeSet();
		Set<?> codEdgeSet = cod.edgeSet();
		boolean result = domEdgeSet.equals(codEdgeSet);
//		assert result == (dom.edgeCount() == 0 && dom.nodeCount() == cod.nodeCount()) || dom.nodeEdgeMap().equals(cod.nodeEdgeMap()): "TreeStoreSet.equals wrongly gives "+result+" on\n"+dom.nodeSet()+"\n"+cod.nodeSet()+"\n"+dom.edgeSet()+"\n"+cod.edgeSet();
		reporter.stop();
		return result;
	}
	
	/** Returns the singleton instance of this class. */
	static public DefaultIsoChecker getInstance() {
	    return instance;
	}
	
    /**
	 * Returns the number of times an isomorphism was suspected on the basis
	 * of the "early warning system", viz. the graph certificate.
	 */
	static public int getIntCertOverlap() {
	    return intCertOverlap;
	}

    /**
     * Returns the total time doing isomorphism-related computations.
     * This includes time spent in certificate calculation.
     */
    static public long getTotalTime() {
        return getIsoCheckTime() + getCertifyingTime();
    }

    /**
     * Returns the time spent calculating certificates, certificate maps
     * and partition maps in {@link Bisimulator}.
     */
    static public long getCertifyingTime() {
        return Bisimulator.reporter
                .getTotalTime(Bisimulator.COMPUTE_CERTIFICATES)
                + Bisimulator.reporter
                        .getTotalTime(Bisimulator.GET_PARTITION_MAP);
    }
    
    /**
     * Returns the time spent checking for isomorphism.
     * This does not include the time spent computing isomorphism certificates;
     * that is reported instead by {@link #getCertifyingTime()}.
     */
    static public long getIsoCheckTime() {
        return reporter.getTotalTime(ISO_CHECK);
    }

    /**
     * Returns the time spent establishing isomorphism by direct equality.
     */
    static public long getEqualCheckTime() {
        return reporter.getTotalTime(EQUALS_TEST);
    }

    /**
     * Returns the time spent establishing isomorphism by certificate equality.
     */
    static public long getCertCheckTime() {
        return reporter.getTotalTime(ISO_CERT_CHECK);
    }
    
    /**
     * Returns the time spent establishing isomorphism by explicit simulation.
     */
    static public long getSimCheckTime() {
        return reporter.getTotalTime(ISO_SIM_CHECK);
    }
    
    /**
     * Returns the number of total checks performed, i.e., the
     * number of calls to {@link #areIsomorphic(Graph, Graph)}.
     */
    static public int getTotalCheckCount() {
        return totalCheckCount;
    }
    
    /**
     * Returns the number of times that non-isomorphism was established on the basis
     * of graph sizes.
     */
    static public int getDistinctSizeCount() {
        return distinctSizeCount;
    }
    
    /**
     * Returns the number of times that isomorphism was established on the basis
     * of graph equality.
     */
    static public int getEqualGraphsCount() {
        return equalGraphsCount;
    }
    
    /**
     * Returns the number of times that isomorphism was established on the basis
     * of (a one-to-one mapping betwen) certificates.
     */
    static public int getEqualCertsCount() {
        return equalCertsCount;
    }
    
    /**
     * Returns the number of times that non-isomorphism was established on the basis
     * of (a one-to-one mapping betwen) certificates.
     */
    static public int getDistinctCertsCount() {
        return distinctCertsCount;
    }
    
    /**
     * Returns the number of times that isomorphism was established on the basis
     * of simulation.
     */
    static public int getEqualSimCount() {
        return equalSimCount;
    }
    
    /**
     * Returns the number of times that isomorphism was established on the basis
     * of simulation.
     */
    static public int getDistinctSimCount() {
        return distinctSimCount;
    }
    
    /** The singleton instance of this class. */
    static private final DefaultIsoChecker instance = new DefaultIsoChecker();
    /** The total number of isomorphism checks. */
    static private int totalCheckCount;
    /**
     * The number of times graph sizes were compares and found to be different. 
     */
    static private int distinctSizeCount;
    /**
     * The number of times graphs were compared based on their elements and found to be isomorphic. 
     */
    static private int equalGraphsCount;
    /**
     * The number of times graphs were compared based on their certificates and found to be isomorphic. 
     */
    static private int equalCertsCount;
    /**
     * The number of times graphs were compared based on their certificates and found to be non-isomorphic. 
     */
    static private int distinctCertsCount;
    /**
     * The number of times graphs were simulated and found to be isomorphic. 
     */
    static private int equalSimCount;
    /**
     * The number of isomorphism warnings given while exploring the GTS.
     */
    static private int intCertOverlap = 0;
    /**
     * The number of times graphs were simulated and found to be non-isomorphic. 
     */
    static private int distinctSimCount;
    
	/** Reporter instance for profiling IsoChecker methods. */
    static public final Reporter reporter = Reporter.register(IsoChecker.class);
    /** Handle for profiling {@link #areIsomorphic(Graph, Graph)}. */
    static public final int ISO_CHECK = reporter.newMethod("areIsomorphic(Graph,Graph)");
    /** Handle for profiling {@link #areCertEqual(CertificateStrategy, CertificateStrategy)}. */
    static final int ISO_CERT_CHECK = reporter.newMethod("Isomorphism by certificates");
    /** Handle for profiling isomorphism by simulation. */
    static final int ISO_SIM_CHECK = reporter.newMethod("Isomorphism by simulation");
    /** Handle for profiling {@link #areGraphEqual(Graph, Graph)}. */
    static final int EQUALS_TEST = reporter.newMethod("Equality test");
}
