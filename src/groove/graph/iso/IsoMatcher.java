/*
 * $Id: IsoMatcher.java,v 1.1.1.1 2007-03-20 10:05:36 kastenberg Exp $
 */
package groove.graph.iso;

import groove.graph.Element;
import groove.graph.InjectiveMorphism;
import groove.graph.match.DefaultMatcher;
import groove.graph.match.SearchPlanFactory;
import groove.util.Reporter;

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
 * @version $Revision: 1.1.1.1 $
 */
public class IsoMatcher extends DefaultMatcher {
	private static final IsoSearchPlanFactory searchPlanFactory = new IsoSearchPlanFactory();
	
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
	
	protected Map<Object, Object> getCodPartitionMap() {
		if (codPartitionMap == null) {
			codPartitionMap = computeCodPartitionMap();
		}
		return codPartitionMap;
	}
	
	protected Map<Object,Object> computeCodPartitionMap() {
		return cod().getCertificateStrategy().getPartitionMap();
	}

	protected Map<Element, Object> getDomCertificateMap() {
		if (domCertificateMap == null) {
			domCertificateMap = computeDomCertificateMap();
		}
		return domCertificateMap;
	}
	
	protected Map<Element,Object> computeDomCertificateMap() {
		return dom().getCertificateStrategy().getCertificateMap();
	}

    public Set<Element> getUsedImages() {
    	if (usedImages == null) {
    		usedImages = new HashSet<Element>();
    	}
    	return usedImages;
    }

//    /**
//     * This implementation adds a given image to the used images, while testing if the image was
//     * already there. If it was already there, this means injectivity is violated
//     * and hence an {@link IllegalStateException} is thrown.
//     */
//    protected void notifySingular(ImageSet<?> changed) {
//        if (!getUsedImages().add(changed.getSingular())) {
//            throw nonInjective;
//        }
//        super.notifySingular(changed);
//    }
//    
//    @Override
//	public boolean addNode(Node key, Node image) {
//    	boolean result = !getUsedImages().contains(image) && super.addNode(key, image);
//    	if (result) {
//    		getUsedImages().add(image);
//    	}
//    	return result;
//	}
//    
//    @Override
//	public boolean addEdge(Edge key, Edge image) {
//    	boolean result = !getUsedImages().contains(image) && super.addEdge(key, image);
//    	if (result) {
//    		getUsedImages().add(image);
//    	}
//    	return result;
//	}
////
////	@Override
////	public void removeNode(Node key) {
////		getUsedImages().remove(getSingularMap().getNode(key));
////		super.removeNode(key);
////	}
//
//	@Override
//	public void removeEdge(Edge key) {
//		getUsedImages().remove(getSingularMap().getEdge(key));
//		super.removeEdge(key);
//	}

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
    private Map<Object,Object> codPartitionMap;
    
    static final Reporter reporter = DefaultIsoChecker.reporter;
    static final int ISO_CERT_COMPUTE = reporter.newMethod("Sim-nested certificate computation");
}
