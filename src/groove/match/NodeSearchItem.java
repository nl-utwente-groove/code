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
 * $Id: NodeSearchItem.java,v 1.7 2007-09-22 16:28:07 rensink Exp $
 */
package groove.match;

import groove.graph.Node;
import groove.match.SearchPlanStrategy.Search;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * A search item that searches an image for a node.
 * @author Arend Rensink
 * @version $Revision $
 */
public class NodeSearchItem extends AbstractSearchItem {
	/** Constructs a new search item, for a given node. */
	public NodeSearchItem(Node node) {
		this.node = node;
        this.boundNodes = Collections.singleton(node);
	}
	
	public NodeRecord getRecord(Search search) {
		return new NodeRecord(search);
	}
	
	/**
     * Returns a singleton set consisting of the single node in this item.
     */
    @Override
    public Collection<? extends Node> bindsNodes() {
        return boundNodes;
    }

    /**
	 * Returns the node that this item tries to match.
	 */
	public Node getNode() {
		return node;
	}
	
	
	@Override
	public String toString() {
		return String.format("Find %s", getNode()); 
	}

	/**
     * This implementation returns the node's hash code.
     */
    @Override
    int getRating() {
        return node.hashCode();
    }

    public void activate(SearchPlanStrategy strategy) {
        nodeIx = strategy.getNodeIx(node);
    }

    /**
	 * The edge for which this search item is to find an image.
	 */
	private final Node node;
    /** The index of {@link #node} in the result. */
	private int nodeIx;
    /** Singleton set consisting only of <code>node</code>. */
    private final Collection<Node> boundNodes;
    
    /**
     * Node search record.
     * The record keeps an iterator over the remaining images to be matched.
     * @author Arend Rensink
     * @version $Revision $
     */
    class NodeRecord extends AbstractRecord {
        /** Constructs a record for a given matcher. */
        NodeRecord(Search search) {
            super(search);
            this.preMatched = search.getNode(nodeIx) != null;
        }

        /**
         * The record is singular if there is a pre-matched image.
         */
        public boolean isSingular() {
            return preMatched;
        }

        @Override
        public String toString() {
            return NodeSearchItem.this.toString() + " = " + selected;
        }

        @Override
        void exit() {
            imageIter = null;
        }

        @Override
        void init() {
            if (!preMatched) {
                imageIter = getTarget().nodeSet().iterator();
            }
        }

        @Override
        boolean next() {
            boolean result;
            if (preMatched) {
                result = isFirst();
            } else {
                result = false;
                while (!result && imageIter.hasNext()) {
                    result = select(imageIter.next());
                }
            }
            return result;
        }

        /** Undoes the effect of {@link #select(Node)}. */
        @Override
        void undo() {
            if (! preMatched) {
                Node oldImage = getSearch().putNode(nodeIx, null);
                assert selected.equals(oldImage) : String.format("Image %s=%s should coincide with %s", node, selected, oldImage);
            }
            selected = null;
        }
        
        /**
         * Actually selects a node image and puts it into the element
         * map of the search.
         * The return value indicates if this has succeeded
         * @param image the value to be inserted in the element map of the matcher
         * @return <code>true</code> if the selection has succeeded
         */
        boolean select(Node image) {
            assert !preMatched;
            boolean result = isAvailable(image);
            if (result) {
                assert selected == null : String.format("Image %s already selected for node %s", image, node);
                getSearch().putNode(nodeIx, image);
                selected = image;
            }
            return result;
        }
        
        /**
         * The images for the item's edge.
         */
        private Iterator<? extends Node> imageIter;
        
        /**
         * The image for {@link #node} set during the last call to {@link #find()}.
         */
        private Node selected;
        
        /** 
         * Flag indicating that the selected image was already in the element map.
         */
        private final boolean preMatched;
    }
}
