/*
 * $Id: IsoMatchFactory.java,v 1.1 2007-08-24 17:34:57 rensink Exp $
 */
package groove.match;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Node;
import groove.match.IsoMatchStrategy.IsoSearch;
import groove.match.SearchPlanStrategy.Search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class creating isomorphism matchers.
 * There is room for optimisation, in the following respects:
 * <ul>
 * <li> The search plan does not take multiplicity of certificates into account. It would speed up the actual search 
 * to start with singular certificates, but the construction of the search plan would suffer. Since iso search
 * plans will probably be used only once, it is not clear where the balance lies.
 * <li> There is no provision for using pre-matched or identical elements. If a large part of the graph
 * remains unchanged throughout the transformation, it will be very beneficial to take this into account.
 * </ul>
 * @author Arend Rensink
 * @version $Revision: 1.1 $
 */
public class IsoMatchFactory {
    /** Private constructor, to ensure the class is used as singleton. */
    private IsoMatchFactory() {
        // empty
    }
    
	/** 
	 * This implementation merely returns search items for the edges and nodes of the
	 * graph, in arbitrary order.
	 */
	public IsoMatchStrategy createSearchPlan(Graph graph) {
        List<SearchItem> result = new ArrayList<SearchItem>();
        Map<Element,Object> certMap = graph.getCertifier().getCertificateMap();
        for (Node node: graph.nodeSet()) {
            result.add(createNodeSearchItem(node, certMap.get(node)));
        }
        for (Edge edge: graph.edgeSet()) {
            result.add(createEdgeSearchItem(edge, certMap.get(edge)));
        }
        return new IsoMatchStrategy(result);
	}

    /**
     * This implementation returns an {@link IsoNodeSearchItem}
     */
	protected SearchItem createNodeSearchItem(Node node, Object cert) {
		return new IsoNodeSearchItem(node, cert);
	}

	/**
     * This implementation returns an {@link IsoEdgeSearchItem}
     */
    protected SearchItem createEdgeSearchItem(Edge edge, Object cert) {
    	return new IsoEdgeSearchItem(edge);
    }

    /** Returns the singleton instance of this factory class. */
    static public IsoMatchFactory getInstance() {
        return instance;
    }
    
    /** The fixed, singleton instance of this factory. */
    static private final IsoMatchFactory instance = new IsoMatchFactory();

    /**
     * A search item that searches an image for an edge for the purpose
     * of isomorphism checking, using the certificates computed for the domain and codomain.
     * @author Arend Rensink
     * @version $Revision $
     */
    static public class IsoNodeSearchItem extends AbstractSearchItem {
        private class IsoNodeRecord extends AbstractRecord {
            /** Constructs a new record for this search item. */
            protected IsoNodeRecord(Search matcher) {
                super(matcher);
                images = getTarget().getCertifier().getPartitionMap().get(cert);
            }
            
            @Override
            void exit() {
                multiImageIter = null;
                singleImage = null;
            }

            @Override
            void init() {
                if (images == null || images instanceof Node) {
                    singleImage = (Node) images;
                } else {
                    multiImageIter = ((Collection<Node>) images).iterator();
                }
            }
            
            @Override
            boolean next() {
                boolean result;
                if (multiImageIter == null) {
                    result = singleImage != null && select(singleImage);
                    singleImage = null;
                } else {
                    result = false;
                    while (!result && multiImageIter.hasNext()) {
                        result = select(multiImageIter.next());
                    }
                }                
                return result;
            }
            
            private boolean select(Node image) {
                if (getSearch().isAvailable(image)) {
                    getResult().putNode(node, image);
                    return true;
                } else {
                    return false;
                }
            }
            
            @Override
            void undo() {
                getResult().removeNode(node);
            }

            /** The image or set of images of the node certificate, according to the target partition map. */
            private final Object images;
            /**
             * Iterator over the remaining images, in case {@link #images} is a set.
             */
            private Iterator<Node> multiImageIter;
            /** The single (remaining) image, in case {@link #images} is not a set. */
            private Node singleImage;
        }

        /**
         * Creates a search item for a given edge.
         * @param node the edge from the domain for which we search images
         */
        public IsoNodeSearchItem(Node node, Object cert) {
            this.node = node;
            this.cert = cert;
        }
        
        /**
         * Returns a fresh search item record for the given node.
         * The search record is required to be an {@link IsoSearch}.
         * @param search the matcher for which the record is to be created;
         * should be an {@link IsoSearch}
         */
        @Override
        public IsoNodeRecord getRecord(Search search) {
            return new IsoNodeRecord(search);
        }
        
        private final Node node;
        private final Object cert;
    }
    

    /**
     * A search item that searches an image for an edge in an isomorphism.
     * @author Arend Rensink
     * @version $Revision $
     */
    static public class IsoEdgeSearchItem extends EdgeSearchItem {
        private class IsoEdgeRecord extends EdgeRecord {
            /** Creates a record for a given matcher. */
            protected IsoEdgeRecord(IsoSearch matcher) {
                super(matcher);
            }

            /**
             * The search plan should have made sure that all 
             * end nodes have been matched.
             */
            @Override
            void init() {
                Edge image = edge.imageFor(getResult());
                setSingular(image);
            }
        }
        
        /**
         * Creates a search item for a given edge.
         * 
         */
        public IsoEdgeSearchItem(Edge edge) {
            super(edge, null);
        }
        
        @Override
        public IsoEdgeRecord getRecord(Search matcher) {
            return new IsoEdgeRecord((IsoSearch) matcher);
        }
    }
}
