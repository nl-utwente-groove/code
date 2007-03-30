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
 * $Id: IsoSimulation.java,v 1.3 2007-03-30 15:50:46 rensink Exp $
 */
package groove.graph.iso;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.InjectiveMorphism;
import groove.graph.InjectiveSimulation;
import groove.graph.Node;
import groove.util.Reporter;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * Implements a simulation geared towards producing an isomorphism.
 * This means that injectivity and surjectivity constraints are brought
 * into play in the construction and refinement of the simulation..
 * The graphs' partition maps are used to match elements.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public class IsoSimulation extends InjectiveSimulation {
    public IsoSimulation(InjectiveMorphism morph) {
        super(morph);
    }
    
    @Override
    protected void initSimulation() {
        // first do a trivial test on node and edge set sizes, to preclude mistakes later
        if (morph.dom().nodeCount() != morph.cod().nodeCount() || morph.dom().edgeCount() != morph.cod().edgeCount()) {
            throw new IllegalStateException();
        }
        reporter.start(ISO_CERT_COMPUTE);
        domCertificateMap = morph.dom().getCertificateStrategy().getCertificateMap();
        codPartitionMap = morph.cod().getCertificateStrategy().getPartitionMap();
        reporter.stop();
        super.initSimulation();
    }

    /**
     * This implementation just inserts the elements with the same certificate,
     * without any further restrictions.
     */
    @Override
    protected void initNodeImageSet(Node key) {
        putNode(key, getNodeMatches(key));
    }

    /**
     * This implementation just inserts the elements with the same certificate,
     * without any further restrictions.
     */
    @Override
    protected void initEdgeImageSet(Edge key) {
        putEdge(key, getEdgeMatches(key));
    }
    
    /**
     * This implementation uses the edge certificates. It is not called in the course of the current
     * implementation, since everything is initialised directly in {@link #initNodeImageSet(Node)}.
     */
    @Override
    protected Iterator<? extends Node> getNodeMatches(Node key) {
        Object match = codPartitionMap.get(domCertificateMap.get(key));
        if (match == null) {
            throw emptyImageSet;
        } else if (match instanceof Node) {
            return Collections.singleton((Node) match).iterator();
        } else {
            return ((Collection<Node>) match).iterator();
        }
    }
    
    /**
     * This implementation uses the edge certificates. It is not called in the course of the current
     * implementation, since everything is initialised directly in {@link #initEdgeImageSet(Edge)}.
     */
    @Override
    protected Iterator<? extends Edge> getEdgeMatches(Edge key) {
        Object match = codPartitionMap.get(domCertificateMap.get(key));
        if (match == null) {
            throw emptyImageSet;
        } else if (match instanceof Element) {
            return Collections.singleton((Edge) match).iterator();
        } else {
            return ((Collection<Edge>) match).iterator();
        }
    }
    
    /**
     * Mapping from domain elements to certificates.
     */
    private Map<Element,Object> domCertificateMap;
    /**
     * Mapping from certificates to codomain element partitions.
     * The images are either {@link Element}s or {@link Collection}s.
     */
    private PartitionMap codPartitionMap;
    
    static final Reporter reporter = DefaultIsoChecker.reporter;
    static final int ISO_CERT_COMPUTE = reporter.newMethod("Sim-nested certificate computation");
}