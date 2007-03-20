/*
 * $Id: IsoMatcher.java,v 1.2 2007-03-20 23:02:57 rensink Exp $
 */
package groove.graph.iso;

import groove.graph.Element;
import groove.graph.InjectiveMorphism;
import groove.graph.match.DefaultMatcher;
import groove.graph.match.SearchPlanFactory;

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
 * @version $Revision: 1.2 $
 */
public class IsoMatcher extends DefaultMatcher {
	/** The factory for creating search plans. */
	private static final IsoSearchPlanFactory searchPlanFactory = new IsoSearchPlanFactory();
	
	/** Constructs a matcher based on a given injective morphism. */
    public IsoMatcher(InjectiveMorphism morph) {
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
	public Object getCertEquivalent(Element key) {
		return getCodPartitionMap().get(getDomCertificateMap().get(key));
	}
	
	/** 
	 * Returns the certificate partition map of the codomain.
	 * Lazily creates the map first. 
	 */
	protected PartitionMap getCodPartitionMap() {
		if (codPartitionMap == null) {
			codPartitionMap = computeCodPartitionMap();
		}
		return codPartitionMap;
	}
	
	/**
	 * Computes the certificate partition map of the codomain,
	 * by querying the codomain's certificate strategy.
	 */
	protected PartitionMap computeCodPartitionMap() {
		return cod().getCertificateStrategy().getPartitionMap();
	}

	/** 
	 * Returns the map from domain elements to certificates.
	 * Lazily creates the map first. 
	 */
	protected Map<Element, Object> getDomCertificateMap() {
		if (domCertificateMap == null) {
			domCertificateMap = computeDomCertificateMap();
		}
		return domCertificateMap;
	}
	
	/**
	 * Computes the certificate map of the domain,
	 * by querying the codomain's certificate strategy.
	 */
	protected Map<Element,Object> computeDomCertificateMap() {
		return dom().getCertificateStrategy().getCertificateMap();
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
    private Map<Element,Object> domCertificateMap;
    /**
     * Mapping from certificates to codomain element partitions.
     * The images are either {@link Element}s or {@link Collection}s.
     */
    private PartitionMap codPartitionMap;
}
