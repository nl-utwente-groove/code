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
 * $Id: NodeSearchItem.java,v 1.2 2007-08-28 22:01:23 rensink Exp $
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
	
    @Override
	public NodeRecord getRecord(Search search) {
		return new NodeRecord(search);
	}
	
	/**
     * Returns a singleton set consisting of the single node in this item.
     */
    @Override
    public Collection<Node> bindsNodes() {
        return boundNodes;
    }
//
//    /**
//     * Throws an exception if the node searched in this item is pre-matched;
//     * does nothing otherwise.
//     */
//    public void schedule(Collection<Node> preMatchedNodes, Collection<String> preMatchedVars) {
//        if (preMatchedNodes.contains(getNode())) {
//            throw new IllegalStateException(String.format("Node searched by %s is pre-matched in %s", toString(), preMatchedNodes));
//        }
//    }

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
	 * The edge for which this search item is to find an image.
	 */
	private final Node node;
    
    /** Singleton set consisting only of <code>node</code>. */
    private final Collection<Node> boundNodes;
    
    /**
     * Node search record.
     * The record keeps an iterator over the remaining images to be matched.
     * @author Arend Rensink
     * @version $Revision $
     */
    protected class NodeRecord extends AbstractRecord {
        /** Constructs a record for a given matcher. */
        protected NodeRecord(Search search) {
            super(search);
            this.preMatched = search.getResult().containsKey(node);
        }

        @Override
        void exit() {
            imageIter = null;
            found = false;
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
                result = found;
            } else {
                result = false;
//                Iterator<? extends Node> imageIter = getImageIter();
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
                Node oldImage = getResult().removeNode(node);
                assert selected.equals(oldImage) : String.format("Image %s=%s should coincide with %s", node, selected, oldImage);
            }
            selected = null;
            found = true;
        }
//
//        /**
//         * Tries out next elements from the remaining images until one fits.
//         */
//        public boolean find() {
//            boolean result;
//            if (findFailed) {
//                reset();
//            } else if (isSelected()) {
//                resetSelected();
//            }
//            if (preMatched) {
//                result = findCalled ? false : select(getSingular());
//            } else {
//                result = false;
//                Iterator<? extends Node> imageIter = getImageIter();
//                while (!result && imageIter.hasNext()) {
//                    result = select(imageIter.next());
//                }
//            }
//            findCalled = !findFailed; // if findReturnedFalse before the actual call, then reset() done, so findCalled should false after. If ! findReturnedFalse before the actual call, so no reset in this call, so findCalled should be true after.
//            findFailed = !result;
//            return result;
//        }
        
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
                getResult().putNode(node, image);
                selected = image;
            }
            return result;
        }
//
//        /** Returns the record to pristine state, so that the search can start anew. */
//        public void reset() {
//            if (isSelected()) {
//                resetSelected();
//            }
//            findCalled = false;
//            findFailed = false;
//            imageIter = null;
//        }
        
        @Override
        public String toString() {
            return NodeSearchItem.this.toString()+" = "+selected;
        }
//
//        /**
//         * Indicates if there is currently an image selected.
//         * If so, the record should be undone before a new image is searched.
//         */
//        protected boolean isSelected() {
//            return selected != null;
//        }
//
//        /**
//         * Returns the currently selected image, if any.
//         */
//        protected Node getSelected() {
//            return selected;
//        }
//
//        /**
//         * Sets the selected image and inserts it into the element map of the matcher.
//         */
//        protected void setSelected(Node image) {
//            assert !isSelected() : String.format("Image %s already selected for node %s", image, node);
//            assert preMatched == (getSingular() != null);
//            if (! preMatched) {
//                getResult().putNode(node, image);
//            }
//            selected = image;
//        }
//
//        /**
//         * Resets the selected image to <code>null</code> and removes it from
//         * the underlying map.
//         */
//        protected void resetSelected() {
//            assert isSelected();
//            if (! preMatched) {
//                Node oldImage = getResult().removeNode(node);
//                assert oldImage.equals(selected) : String.format("Image %s=%s should coincide with %s", node, selected, oldImage);
//            }
//            selected = null;
//        }
//
//        /** 
//         * Returns the singular image of the searched edge,
//         * if indeed the image is singular.
//         * Returns <code>null</code> if there are either fewer or more than
//         * one image.
//         */
//        protected Node getSingular() {
//            return getResult().getNode(node);
//        }
//        
//        /**
//         * Returns an iterator over the possible images, creating the
//         * iterator if that has not yet been done.
//         */
//        protected Iterator<? extends Node> getImageIter() {
//            if (imageIter == null) {
//                imageIter = computeImageSet().iterator();
//            }
//            return imageIter;
//        }
//        
//        /**
//         * Computes the set of possible images for the node.
//         * The set is either the image already in the element map, or
//         * the set of all nodes of the codomain.
//         */
//        protected Iterator<? extends Node> computeImageSet() {
//            return getTarget().nodeSet().iterator();
//        }
//        
//        /**
//         * The matcher for which we have instantiated this record.
//         */
//        protected final SearchPlanStrategy.Search search;
//        /**
//         * Flag indicating that {@link #find()} already returned <code>false</code>.
//         */
//        private boolean findFailed;
//        /**
//         * Flag indicating that {@link #find()} was already
//         * called at least once (since the last {@link #reset()}).
//         */
//        private boolean findCalled;
        /**
         * The images for the item's edge.
         */
        private Iterator<? extends Node> imageIter;
        /** 
         * Flag indicating that at least one image has already been 
         * delived by {@link #next()}.
         */
        private boolean found;
        
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
