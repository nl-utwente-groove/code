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
 * $Id: AltIsoMatchFactory.java,v 1.1 2007-09-15 17:25:27 rensink Exp $
 */
package groove.match;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.iso.PartitionMap;
import groove.match.SearchPlanStrategy.Search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
public class AltIsoMatchFactory {
    /** Private constructor, to ensure the class is used as singleton. */
    private AltIsoMatchFactory() {
        // empty
    }
    
	/** 
	 * This implementation merely returns search items for the edges and nodes of the
	 * graph, in arbitrary order.
	 */
	public SearchPlanStrategy createMatcher(Graph source, Graph target) {
        List<SearchItem> result = new ArrayList<SearchItem>();
        Map<Element,Object> certMap = source.getCertifier().getCertificateMap();
        PartitionMap partMap = target.getCertifier().getPartitionMap();
        for (Edge edge: source.edgeSet()) {
        	Object image = partMap.get(certMap.get(edge));
        	if (image instanceof Element) {
        		
        	}
            result.add(createEdgeSearchItem(edge));
        }
        for (Node node: source.nodeSet()) {
            result.add(createNodeSearchItem(node, certMap.get(node)));
        }
        return new SearchPlanStrategy(result, true);
	}

    /**
     * This implementation returns an {@link IsoNodeSearchItem}.
     */
	private SearchItem createNodeSearchItem(Node node, Object cert) {
		return new IsoNodeSearchItem(node, cert);
	}

	/**
     * This implementation returns an {@link EdgeSearchItem}.
     */
    private SearchItem createEdgeSearchItem(Edge edge) {
    	return new EdgeSearchItem(edge);
    }

    /** Returns the singleton instance of this factory class. */
    static public AltIsoMatchFactory getInstance() {
        return instance;
    }
    
    /** The fixed, singleton instance of this factory. */
    static private final AltIsoMatchFactory instance = new AltIsoMatchFactory();

    /**
     * A search item that searches an image for an edge for the purpose
     * of isomorphism checking, using the certificates computed for the domain and codomain.
     * @author Arend Rensink
     * @version $Revision $
     */
    static public class IsoNodeSearchItem extends AbstractSearchItem {
        /**
         * Creates a search item for a given node.
         * @param node the node from the domain for which we search images
         * @param cert the isomorphism certificate of the node
         */
        public IsoNodeSearchItem(Node node, Object cert) {
            this.node = node;
            this.cert = cert;
        }
        
        /**
         * Returns the singleton set consisting of the node that this item searches.
         */
        @Override
        public Collection<Node> bindsNodes() {
            return Collections.singleton(node);
        }

        /**
         * Returns a fresh search item record for the given node.
         */
        public IsoNodeRecord getRecord(Search search) {
            return new IsoNodeRecord(search);
        }
        
        /**
         * This method returns the hash code of the certificate as rating.
         */
        @Override
        int getRating() {
            return cert.hashCode();
        }

        @Override
        public String toString() {
            return String.format("Node %s(%s)", node, cert);
        }

        /** The node for which the item searches an image. */
        private final Node node;
        /** The certificate of <code>node</code>. */
        private final Object cert;
        
        /** Record for an isomorphism node search. */
        private class IsoNodeRecord extends AbstractRecord {
            /** Constructs a new record for this search item. */
            protected IsoNodeRecord(Search matcher) {
                super(matcher);
                images = getTarget().getCertifier().getPartitionMap().get(cert);
            }
            
            /** The record is singular it there is exactly one image with the correct certificate. */
            public boolean isSingular() {
                return images instanceof Element;
            }

            @Override
            public String toString() {
                return String.format("%s = %s", IsoNodeSearchItem.this.toString(), getResult().getNode(node));
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
            
            /** 
             * Tries out a given node image.
             * The return value indicates if this has been successful.
             * @param image the proposed node image
             * @return <code>true</code> if <code>image</code> was suitable and
             * has been selected
             */
            private boolean select(Node image) {
                if (isAvailable(image)) {
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
    }
}
