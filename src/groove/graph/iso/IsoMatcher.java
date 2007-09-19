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
 * $Id: IsoMatcher.java,v 1.8 2007-09-19 09:01:05 rensink Exp $
 */
package groove.graph.iso;

import groove.graph.Element;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.iso.CertificateStrategy.Certificate;
import groove.graph.match.DefaultMatcher;
import groove.graph.match.SearchPlanFactory;
import groove.util.SmallCollection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implements a simulation geared towards producing an isomorphism.
 * This means that injectivity and surjectivity constraints are brought
 * into play in the construction and refinement of the simulation..
 * The graphs' partition maps are used to match elements.
 * @author Arend Rensink
 * @version $Revision: 1.8 $
 */
@Deprecated
public class IsoMatcher extends DefaultMatcher {
	/** The factory for creating search plans. */
	private static final IsoSearchPlanFactory searchPlanFactory = new IsoSearchPlanFactory();
	
	/** Constructs a matcher based on a given injective morphism. */
    public IsoMatcher(Morphism morph) {
        super(morph);
    }
    
    /** This implementation returns an {@link IsoSearchPlanFactory} */
    @Override
	protected SearchPlanFactory getSearchPlanFactory() {
		return searchPlanFactory;
	}
    
	/**
	 * Returns the object from the codomain with the same certificate
	 * as a given key from the domain.
	 * The result can be a single element or a set of elements. 
	 */
	public SmallCollection<Node> getCertEquivalent(Node key) {
		return getCodNodePartitionMap().get((Certificate<Node>) getDomCertificateMap().get(key));
	}
	
	/** 
	 * Returns the certificate partition map of the codomain.
	 * Lazily creates the map first. 
	 */
	protected PartitionMap<Node> getCodNodePartitionMap() {
		if (codPartitionMap == null) {
			codPartitionMap = computeCodNodePartitionMap();
		}
		return codPartitionMap;
	}
	
	/**
	 * Computes the certificate partition map of the codomain,
	 * by querying the codomain's certificate strategy.
	 */
	protected PartitionMap<Node> computeCodNodePartitionMap() {
		return cod().getCertifier().getNodePartitionMap();
	}

	/** 
	 * Returns the map from domain elements to certificates.
	 * Lazily creates the map first. 
	 */
	protected Map<Element, ? extends Certificate<?>> getDomCertificateMap() {
		if (domCertificateMap == null) {
			domCertificateMap = computeDomCertificateMap();
		}
		return domCertificateMap;
	}
	
	/**
	 * Computes the certificate map of the domain,
	 * by querying the codomain's certificate strategy.
	 */
	protected Map<Element,? extends Certificate<?>> computeDomCertificateMap() {
		return dom().getCertifier().getCertificateMap();
	}

	/**
	 * Returns the set of elements already used as images in the matching.
	 */
    public Set<Element> getUsedImages() {
    	if (usedImages == null) {
    		usedImages = new HashSet<Element>();
    	}
    	return usedImages;
    }

	/** The set of images used as singular image. */
    private Set<Element> usedImages;

    /**
     * Mapping from domain elements to certificates.
     */
    private Map<Element,? extends Certificate<?>> domCertificateMap;
    /**
     * Mapping from certificates to codomain element partitions.
     * The images are either {@link Element}s or {@link Collection}s.
     */
    private PartitionMap<Node> codPartitionMap;
}
