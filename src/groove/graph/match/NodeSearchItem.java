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
 * $Id: NodeSearchItem.java,v 1.3 2007-06-01 18:04:14 rensink Exp $
 */
package groove.graph.match;

import java.util.Collection;
import java.util.Iterator;

import groove.graph.Node;

/**
 * A search item that searches an image for a node.
 * @author Arend Rensink
 * @version $Revision $
 */
public class NodeSearchItem implements SearchItem {
	/**
	 * Node search record.
	 * The record keeps an iterator over the remaining images to be matched.
	 * @author Arend Rensink
	 * @version $Revision $
	 */
	protected class NodeRecord<M extends Matcher> implements Record {
		/** Constructs a record for a given matcher. */
		protected NodeRecord(M matcher) {
			this.matcher = matcher;
			this.preMatched = matcher.getMorphism().containsKey(node);
		}

		/**
		 * Tries out next elements from the remaining images until one fits.
		 */
		public boolean find() {
			boolean result;
			if (findReturnedFalse) {
				reset();
			} else if (isSelected()) {
				undo();
			}
			if (preMatched) {
				result = findCalled ? false : select(getSingular());
			} else {
				result = false;
				Iterator<? extends Node> imageIter = getImageIter();
				while (!result && imageIter.hasNext()) {
					result = select(imageIter.next());
				}
			}
			findReturnedFalse = !result;
			findCalled = !result;
			return result;
		}
		
		/**
		 * Actually selects a node image and puts it into the element
		 * map of the matcher.
		 * The return value indicates if this has succeeded
		 * @param image the value to be inserted in the element map of the matcher
		 * @return <code>true</code> if the selection has succeeded
		 */
		public boolean select(Node image) {
//			Node currentImage = matcher.getSingularMap().getNode(node);
			boolean result = !preMatched || getSingular().equals(image);
			if (result) {
				setSelected(image);
			}
			return result;
		}
		
		public void undo() {
			if (isSelected()) {
				resetSelected();
			} else {
				throw new IllegalStateException();
			}
		}

		public void reset() {
			if (isSelected()) {
				throw new IllegalStateException();
			} else {
				findCalled = false;
				findReturnedFalse = false;
				imageIter = null;
				selected = null;
			}
		}
		
		@Override
		public String toString() {
			return NodeSearchItem.this.toString()+" = "+selected;
		}

		/**
		 * Indicates if there is currently an image selected.
		 * If so, the record should be undone before a new image is searched.
		 */
		protected boolean isSelected() {
			return selected != null;
		}

		/**
		 * Returns the currently selected image, if any.
		 */
		protected Node getSelected() {
			return selected;
		}

		/**
		 * Sets the selected image and inserts it into the element map of the matcher.
		 */
		protected void setSelected(Node image) {
			assert !isSelected() : String.format("Image %s already selected for node %s", image, node);
			assert preMatched == (getSingular() != null);
			if (! preMatched) {
				matcher.getSingularMap().putNode(node, image);
			}
			selected = image;
		}

		/**
		 * Resets the selected image to <code>null</code> and removes it from
		 * the underlying map.
		 */
		protected void resetSelected() {
			assert isSelected();
			if (! preMatched) {
				Node oldImage = matcher.getSingularMap().removeNode(node);
				assert oldImage.equals(selected) : String.format("Image %s=%s should coincide with %s", node, selected, oldImage);
			}
			selected = null;
		}

		/** 
		 * Returns the singular image of the searched edge,
		 * if indeed the image is singular.
		 * Returns <code>null</code> if there are either fewer or more than
		 * one image.
		 */
		protected Node getSingular() {
			return matcher.getSingularMap().getNode(node);
		}
		
		/**
		 * Returns an iterator over the possible images, creating the
		 * iterator if that has not yet been done.
		 */
		protected Iterator<? extends Node> getImageIter() {
			if (imageIter == null) {
				imageIter = computeImageSet().iterator();
			}
			return imageIter;
		}
		
		/**
		 * Computes the set of possible images for the node.
		 * The set is either the image already in the element map, or
		 * the set of all nodes of the codomain.
		 */
		protected Collection<? extends Node> computeImageSet() {
			return matcher.cod().nodeSet();
		}
		
		/**
		 * The matcher for which we have instantiated this record.
		 */
		protected final M matcher;
		/**
		 * Flag indicating that {@link #find()} already returned <code>false</code>.
		 */
		private boolean findReturnedFalse;
		/**
		 * Flag indicating that {@link #find()} was already
		 * called at least once (since the last {@link #reset()}).
		 */
		private boolean findCalled;
		/**
		 * The images for the item's edge.
		 */
		private Iterator<? extends Node> imageIter;
		
		/**
		 * The image for {@link #node} set during the last call to {@link #find()}.
		 */
		private Node selected;
		
		/** 
		 * Flag indicating that the selected image was already in the
		 * element map and should not be removed during {@link #undo()}.
		 */
		private final boolean preMatched;
	}

	/** Constructs a new search item, for a given node. */
	public NodeSearchItem(Node node) {
		this.node = node;
	}
	
	public NodeRecord<? extends Matcher> get(Matcher matcher) {
		return new NodeRecord<Matcher>(matcher);
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
	 * The edge for which this search item is to find an image.
	 */
	protected final Node node;
}
