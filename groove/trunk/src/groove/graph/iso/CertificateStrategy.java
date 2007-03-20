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
 * $Id: CertificateStrategy.java,v 1.1.1.1 2007-03-20 10:05:36 kastenberg Exp $
 */
package groove.graph.iso;

import java.util.Map;

import groove.graph.Element;
import groove.graph.Graph;

/**
 * Interface for algorithms to compute isomorphism certificates for a given graph,
 * i.e., a predictor for graph isomorphism.
 * Two graphs are isomorphic only if their certificates are equal
 * (as determined by <tt>equals(Object)</tt>).
 * A certificate strategy is specialized to a graph upon which it works;
 * this is set at creation time.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public interface CertificateStrategy {
    /**
     * Returns the underlying graph for which this is the certificate strategy.
     * @return the underlying graph
     */
    public Graph getGraph();
    
    /**
     * Method to compute the isomorphism certificate for the underlying graph.
     * @return the isomorphism certificate for the underlying graph.
     */
    public Object getGraphCertificate();
//    
//    /**
//     * Method to compute the isomorphism certificate for the underlying graph,
//     * to a given precision. The larger the precision, the more accurate (but probably
//     * also costly in time and space) the certificate. From precision <tt>1</tt> 
//     * onward, the graph certificate is computed from the individual graph elements;
//     * i.e., it is derived from the value set of <tt>getCertificateMap(precision)</tt>
//     * @param precision the precision to which the certificate is to be computed;
//     * <tt>0</tt> is the lowest precision
//     * @return an isomorphism certificate for the underlying graph
//     * @see #getCertificateMap(int)
//     * @deprecated will be removed from the interface; use {@link #getGraphCertificate()} instead
//     */
//    public Object getGraphCertificate(int precision);
//    
    /**
     * Returns a map from graph elements to certificates for the underlying graph.
     * Two elements from different graphs may only be joined by isomorphism
     * if their certificates are equal.
     * @ensure <tt>result.keySet() \subseteq getGraph().nodeSet() \cup getGraph().edgeSet()</tt>
     * @see #getCertificateMap()
     */
    public Map<Element, Object> getCertificateMap();
  
    /**
     * Returns a map from certificates to sets of nodes and edges of the underlying graph.
     * This is the reverse of {@link #getCertificateMap()}, specialized to nodes.
     * Two elements from different graphs may only be joined by isomorphism
     * if their certificates are equal; i.e., if they are in the image of the same certificate.
     * The return type is a map to either {@link Element}s or {@link java.util.Collection}s.
     * @ensure <tt>result.get(c).contains(e)</tt> iff <tt>getCertificateMap.get(e).equals(c)</tt>
     */
    public Map<Object,Object> getPartitionMap();

    /**
     * Returns the number of (node) certificates occurring as targets in the certificate map.
     * @return <code>getPartitionMap().size()</code>
     */
    public int getNodePartitionCount();

    /**
     * Factory method; returns a certificate strategy for a given graph.
     * @param graph the underlying graph for the new certificate strategy.
     * @return a fresh certificate strategy for <tt>graph</tt>
     */
    public CertificateStrategy newInstance(Graph graph);
}