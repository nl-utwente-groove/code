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

import java.util.Map;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Node;

/**
 * Interface for algorithms to compute isomorphism certificates for a given
 * graph, i.e., a predictor for graph isomorphism. Two graphs are isomorphic
 * only if their certificates are equal (as determined by
 * <tt>equals(Object)</tt>). A certificate strategy is specialized to a graph
 * upon which it works; this is set at creation time.
 * @author Arend Rensink
 * @version $Revision$
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

    /** Returns the node certificates calculated for the graph. */
    public Certificate<Node>[] getNodeCertificates();

    /** Returns the edge certificates calculated for the graph. */
    public Certificate<Edge>[] getEdgeCertificates();

    /**
     * Returns a map from graph elements to certificates for the underlying
     * graph. Two elements from different graphs may only be joined by
     * isomorphism if their certificates are equal.
     * @ensure <tt>result.keySet() \subseteq getGraph().nodeSet() \cup getGraph().edgeSet()</tt>
     * @see #getCertificateMap()
     */
    public Map<Element,? extends Certificate<?>> getCertificateMap();

    /**
     * Returns a map from node certificates to sets of nodes of the underlying
     * graph. This is the reverse of {@link #getCertificateMap()}, specialised
     * to nodes. Two nodes from different graphs may only be joined by
     * isomorphism if their certificates are equal; i.e., if they are in the
     * image of the same certificate.
     */
    public PartitionMap<Node> getNodePartitionMap();

    /**
     * Returns a map from edge certificates to sets of edges of the underlying
     * graph. This is the reverse of {@link #getCertificateMap()}, specialised
     * to edges. Two edges from different graphs may only be joined by
     * isomorphism if their certificates are equal; i.e., if they are in the
     * image of the same certificate.
     */
    public PartitionMap<Edge> getEdgePartitionMap();

    /**
     * Returns the number of (node) certificates occurring as targets in the
     * certificate map.
     * @return <code>getPartitionMap().size()</code>
     */
    public int getNodePartitionCount();

    /**
     * Factory method; returns a certificate strategy for a given graph.
     * @param graph the underlying graph for the new certificate strategy.
     * @return a fresh certificate strategy for <tt>graph</tt>
     */
    public CertificateStrategy newInstance(Graph graph);

    /**
     * Type of the certificates constructed by the strategy. A value of this
     * type represents a given graph element in an isomorphism-invariant way.
     * Hence, equality of certificates does not imply equality of the
     * corresponding graph elements.
     */
    public interface Certificate<E extends Element> {
        /** Returns the element for which this is a certificate. */
        E getElement();
    }
}
