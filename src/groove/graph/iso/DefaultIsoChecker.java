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
 * $Id: DefaultIsoChecker.java,v 1.11 2007-08-29 11:07:54 rensink Exp $
 */
package groove.graph.iso;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.Node;
import groove.match.IsoMatchFactory;
import groove.util.Reporter;

/**
 * Implementation of an isomorphism checking algorithm that first tries to
 * decide isomorphism directly on the basis of a {@link groove.graph.iso.CertificateStrategy}. 
 * @author Arend Rensink
 * @version $Revision: 1.11 $
 */
public class DefaultIsoChecker implements IsoChecker {
    /**
     * Returns the total time doing isomorphism-related computations.
     * This includes time spent in cerftificate calculation.
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
     * The number of times graphs were simulated and found to be non-isomorphic. 
     */
    static private int distinctSimCount;
    
    /**
     * The factory used to get morphisms from
     */
    static private GraphFactory graphFactory = GraphFactory.getInstance();

	public boolean areIsomorphic(Graph dom, Graph cod) {
		boolean result;
        reporter.start(ISO_CHECK);
        if (dom.nodeCount() != cod.nodeCount() || dom.edgeCount() != cod.edgeCount()) {
            distinctSizeCount++;
        	result = false;
        } else if (areGraphEqual(dom, cod)) {
            equalGraphsCount++;
        	result = true;
        } else {
        	CertificateStrategy domCertifier = dom.getCertifier();
        	CertificateStrategy codCertifier = cod.getCertifier();
        	if (hasDistinctCerts(codCertifier)) {
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
        		result = IsoMatchFactory.getInstance().createMatcher(dom).getMatch(cod, null) != null;
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
	 * Tests if the elements of a graph have all different certificates.
	 * If this holds, then {@link #areCertEqual(CertificateStrategy, CertificateStrategy)} can be
	 * called to check for isomorphism.
	 * @param certifier the graph to be tested
	 * @return <code>true</code> if <code>graph</code> has distinct certificates
	 */
	private boolean hasDistinctCerts(CertificateStrategy certifier) {
		return certifier.getPartitionMap().isOneToOne();
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
		Map<Element,Object> domCertificateMap = dom.getCertificateMap();
		PartitionMap codPartitionMap = cod.getPartitionMap();
		reporter.restart(ISO_CHECK);
		reporter.restart(ISO_CERT_CHECK);
		result = true;
		// map to store dom-to-cod node mapping
		Map<Node,Node> nodeMap = new HashMap<Node,Node>();
		// iterate over the dom certificates
		Iterator<Map.Entry<Element,Object>> domCertIter = domCertificateMap.entrySet().iterator();
		while (result && domCertIter.hasNext()) {
			Map.Entry<Element,Object> domCertEntry = domCertIter.next();
			Element key = domCertEntry.getKey();
			Object image = codPartitionMap.get(domCertEntry.getValue());
			if (! (image instanceof Element)) {
				result = false;
			} else if (key instanceof Node) {
				result = testAndSet(nodeMap, (Node) key, (Node) image);
			} else {
				Edge edgeKey = (Edge) key;
				Edge edgeImage = (Edge) image;
				int endCount = edgeKey.endCount();
				for (int end = 0; result && end < endCount; end++) {
					result = testAndSet(nodeMap, edgeKey.end(end),
							edgeImage.end(end));
				}
			}
		}
		return result;
	}
	
	/**
	 * Puts a new dom-to-cod node pair into a map, in the meanwhile testing
	 * if this is consistent with the image already in the map, if any.
	 * @param nodeMap the dom-to-cod node map to be extended
	 * @param key the node for which an image is to be inserted
	 * @param image the (new) image fo <code>key</code>
	 * @return <code>true</code> if <code>oldImage == null || oldImage.equals(image)</code>
	 * where <code>oldImage = old.nodeMap.get(key)</code>
	 */
	private boolean testAndSet(Map<Node,Node> nodeMap, Node key, Node image) {
		Node oldImage = nodeMap.put(key, image);
		boolean result = oldImage == null || oldImage.equals(image);
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
