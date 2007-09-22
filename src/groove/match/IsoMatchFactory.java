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
 * $Id: IsoMatchFactory.java,v 1.9 2007-09-22 09:10:35 rensink Exp $
 */
package groove.match;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.iso.DefaultIsoChecker;
import groove.graph.iso.CertificateStrategy.Certificate;
import groove.match.SearchPlanStrategy.Search;
import groove.util.SmallCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
 * @version $Revision: 1.9 $
 * @deprecated isomorphism check is now in {@link DefaultIsoChecker}
 */
@Deprecated
public class IsoMatchFactory {
    /** Private constructor, to ensure the class is used as singleton. */
    private IsoMatchFactory() {
        // empty
    }
    
	/** 
	 * This implementation merely returns search items for the edges and nodes of the
	 * graph, in arbitrary order.
	 */
	public SearchPlanStrategy createMatcher(Graph graph) {
        List<SearchItem> items = new ArrayList<SearchItem>();
        for (Certificate<Node> nodeCert: graph.getCertifier().getNodeCertificates()) {
            items.add(createNodeSearchItem(nodeCert));
        }
        for (Edge edge: graph.edgeSet()) {
            items.add(createEdgeSearchItem(edge));
        }
        SearchPlanStrategy result = new SearchPlanStrategy(graph, items, true);
        result.setFixed();
        return result;
	}

    /**
     * This implementation returns an {@link IsoNodeSearchItem}.
     */
	private SearchItem createNodeSearchItem(Certificate<Node> cert) {
		return new IsoNodeSearchItem(cert);
	}

	/**
     * This implementation returns an {@link EdgeSearchItem}.
     */
    private SearchItem createEdgeSearchItem(Edge edge) {
    	return new EdgeSearchItem(edge);
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
        /**
         * Creates a search item for a given node.
         * @param cert the isomorphism certificate of the node
         */
        public IsoNodeSearchItem(Certificate<Node> cert) {
            this.node = cert.getElement();
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

        public void activate(SearchPlanStrategy strategy) {
            nodeIx = strategy.getNodeIx(node);
        }

        /** The node for which the item searches an image. */
        private final Node node;
        /** The certificate of <code>node</code>. */
        private final Certificate<Node> cert;
        /** The index of {@link #node} in the result. */
        private int nodeIx;
        /** Record for an isomorphism node search. */
        private class IsoNodeRecord extends AbstractRecord {
            /** Constructs a new record for this search item. */
            protected IsoNodeRecord(Search matcher) {
                super(matcher);
                images = getTarget().getCertifier().getNodePartitionMap().get(cert);
            }
            
            /** The record is singular it there is exactly one image with the correct certificate. */
            public boolean isSingular() {
                return images instanceof Element;
            }

            @Override
            public String toString() {
                return String.format("%s = %s", IsoNodeSearchItem.this.toString(), getSearch().getNode(nodeIx));
            }

            @Override
            void exit() {
                multiImageIter = null;
                singleImage = null;
            }

            @Override
            void init() {
                if (images == null) {
                    singleImage = null;
                } else if (images.isSingleton()) {
                    singleImage = images.getSingleton();
                } else {
                    multiImageIter = images.iterator();
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
                    getSearch().putNode(nodeIx, image);
                    return true;
                } else {
                    return false;
                }
            }
            
            @Override
            void undo() {
                getSearch().putNode(nodeIx, null);
            }

            /** The image or set of images of the node certificate, according to the target partition map. */
            private final SmallCollection<Node> images;
            /**
             * Iterator over the remaining images, in case {@link #images} is a set.
             */
            private Iterator<Node> multiImageIter;
            /** The single (remaining) image, in case {@link #images} is not a set. */
            private Node singleImage;
        }
    }
}
