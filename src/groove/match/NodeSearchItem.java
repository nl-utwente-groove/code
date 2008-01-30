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
 * $Id: NodeSearchItem.java,v 1.13 2008-01-30 09:33:29 iovka Exp $
 */
package groove.match;

import groove.graph.Node;
import groove.match.SearchPlanStrategy.Search;

import java.util.Collection;
import java.util.Collections;

/**
 * A search item that searches an image for a node.
 * @author Arend Rensink
 * @version $Revision $
 */
class NodeSearchItem extends AbstractSearchItem {
	/** Constructs a new search item, for a given node. */
	public NodeSearchItem(Node node) {
		this.node = node;
        this.boundNodes = Collections.singleton(node);
	}
	
	public Record getRecord(Search search) {
		if (nodeMatched || search.getNodeAnchor(nodeIx) != null) {
			// the node is pre-matched, so there is nothing to do
			return createDummyRecord();
		} else {
			return new NodeRecord(search);
		}
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
    	nodeMatched = strategy.isNodeFound(node);
        nodeIx = strategy.getNodeIx(node);
    }

    /**
	 * The edge for which this search item is to find an image.
	 */
	private final Node node;
	/** Flag indicating if the node is pre-matched. */
	private boolean nodeMatched;
    /** The index of {@link #node} in the result. */
	int nodeIx;
    /** Singleton set consisting only of <code>node</code>. */
    private final Collection<Node> boundNodes;
    
    /**
     * Node search record.
     * The record keeps an iterator over the remaining images to be matched.
     * @author Arend Rensink
     * @version $Revision $
     */
    class NodeRecord extends MultipleRecord<Node> {
        /** Constructs a record for a given matcher. */
        NodeRecord(Search search) {
            super(search);
            assert search.getNode(nodeIx) == null;
        }

        @Override
        public String toString() {
            return NodeSearchItem.this.toString() + " = " + selected;
        }

        @Override
        public void reset() {
            imageIter = null;
            selected = null;
            search.putNode(nodeIx, null);
        }

        @Override
        void init() {
        	imageIter = host.nodeSet().iterator();
        }
        
        /**
         * Actually selects a node image and puts it into the element
         * map of the search.
         * The return value indicates if this has succeeded
         * @param image the value to be inserted in the element map of the matcher
         * @return <code>true</code> if the selection has succeeded
         */
        @Override
        boolean setImage(Node image) {
        	boolean result = search.putNode(nodeIx, image);
        	selected = image;
            return result;
        }

        /**
         * The image for {@link #node} set during the last call to {@link #find()}.
         */
        private Node selected;
    }
}
