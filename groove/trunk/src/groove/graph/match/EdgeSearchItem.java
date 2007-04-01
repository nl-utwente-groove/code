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
 * $Id: EdgeSearchItem.java,v 1.3 2007-04-01 12:50:11 rensink Exp $
 */
package groove.graph.match;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import groove.graph.Edge;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class EdgeSearchItem<E extends Edge> implements SearchItem {
	/**
	 * Record of an edge seach item, storing an iterator over the
	 * candidate images.
	 * @author Arend Rensink
	 * @version $Revision $
	 */
	protected class EdgeRecord<M extends Matcher> implements Record {
		/**
		 * Creates a record based on a given underlying matcher.
		 */
		protected EdgeRecord(M matcher) {
			this.matcher = matcher;
			assert ! matcher.getMorphism().containsKey(edge);
		}

		public boolean find() {
			boolean result;
			// first test if we know the method already returned false
			if (findReturnedFalse) {
				reset();
			} else if (selected != null) {
				undo();
			}
			if (isSingular()) {
				// if the method was already called, 
				// it should now certainly return false
				if (findCalled) {
					result = false;
				} else {
					// get the only possible image
					Edge image = getSingular();
					// maybe there is none
					result = image != null && select(image);
				}
			} else {
				// there is more than one possible image
				result = false;
				// iterate over the possible images until one is found
				Iterator<? extends Edge> imageIter = getPotentialImageIter();
				while (!result && imageIter.hasNext()) {
					result = select(imageIter.next());
				}
			}
			findReturnedFalse = !result;
			findCalled = true;
			return result;
		}
		
		/**
		 * Removes the edge added during the last {@link #find()}, if any.
		 */
		public void undo() {
			if (selected != null) {
				NodeEdgeMap elementMap = matcher.getSingularMap();
				if (!isSingular()) {
					for (int i = 0; i < arity; i++) {
						if (!isPreMatched(i)) {
							Node endImage = elementMap.removeNode(edge.end(i));
							assert endImage != null;
						}
					}
				}
				Edge oldImage = elementMap.removeEdge(edge);
				assert oldImage == null || oldImage.equals(selected);
				selected = null;
			} else {
				throw new IllegalStateException();
			}
		}
		
		public void reset() {
			if (selected != null) {
				throw new IllegalStateException();
			} else {
				imagesInitialised = false;
				singular = false;
				potentialImage = null;
				findCalled = false;
				findReturnedFalse = false;
				potentialImageIter = null;
				potentialImageSet = null;
			}
		}

		/**
		 * Selects an image for {@link EdgeSearchItem#edge}, after testing
		 * it for correctness.
		 * @param image the image to be selected
		 * @return <code>true</code> if <code>image</code> was indeed selected
		 */
		public boolean select(Edge image) {
			assert image != null : "Selected image should not be null";
			assert selected == null : String.format("Edge %s already has image %s in map %s", edge, selected, matcher.getSingularMap());
			boolean result = true;
			if (! isSingular()) {
				// select the node ends, insofar necessary
				int[] duplicates = getDuplicates();
				NodeEdgeMap elementMap = matcher.getSingularMap();
				int endIndex = 0;
				for (endIndex = 0; result && endIndex < arity; endIndex++) {
					Node imageEnd = image.end(endIndex);
					if (duplicates[endIndex] < endIndex) {
						result = imageEnd == image.end(duplicates[endIndex]);
					} else if (isPreMatched(endIndex)) {
						result = elementMap.getNode(edge.end(endIndex)) == imageEnd;
					} else {
						Node endImage = elementMap.putNode(edge.end(endIndex), imageEnd);
						assert endImage == null;
					}
				}
				if (! result) {
					// deselect the selected node ends
					for (endIndex--; endIndex >= 0; endIndex--) {
						if (!isPreMatched(endIndex)) {
							elementMap.removeNode(edge.end(endIndex));
						}
					}
				}
			}
			if (result) {
				setSelectedImage(image);
				selected = image;
			}
			return result;
		}
		
		@Override
		public String toString() {
			return EdgeSearchItem.this.toString()+" = "+selected;
		}

		/** 
		 * Actually sets the image in the matcher's map.
		 * Callback method for subclasses that give matches that are not edes.
		 */
		protected void setSelectedImage(Edge image) {
			matcher.getSingularMap().putEdge(edge, image);
		}
		
		/**
		 * Indicates if there is at most one potential image, provided we can
		 * detect this ceaply.
		 * @return if <code>true</code>, there is at most one potential image
		 * (which can then be obtained by invoking {@link #getSingular()});
		 * if <code>false</code>, we have no information
		 */
		protected boolean isSingular() {
			if (!imagesInitialised) {
				initImages();
				imagesInitialised = true;
			}
			return singular;
		}
		
		protected Edge getSingular() {
			if (!imagesInitialised) {
				initImages();
				imagesInitialised = true;
			}
			return potentialImage;
		}
		
		protected Iterator<? extends Edge> getPotentialImageIter() {
			if (potentialImageIter == null) {
				initImages();
				imagesInitialised = true;
				potentialImageIter = potentialImageSet.iterator();
			}
			return potentialImageIter;
		}
		
		/**
		 * Initialises the potential images,
		 * by either calling {@link #setSingular(Edge)} or {@link #setMultiple(Collection)},
		 * depending on whether a unique image can be determined for the edge.
		 */
		protected void initImages() {
			if (isAllEndsPreMatched()) {
				Edge image = edge.imageFor(matcher.getSingularMap());
				assert image != null;
				if (matcher.cod().containsElement(image)) {
					setSingular(image);
				} else {
					setSingular(null);
				}
			} else {
				Set<? extends Edge> imageSet = matcher.cod().labelEdgeSet(arity,
						edge.label());
				if (imageSet == null || imageSet.isEmpty()) {
					setSingular(null);
				} else {
					setMultiple(imageSet);
				}
			}
		}
		
		final protected void setSingular(Edge image) {
			this.singular = true;
			this.potentialImage = image;
		}
		
		final protected void setMultiple(Collection<? extends Edge> imageSet) {
			this.singular = false;
			this.potentialImageSet = imageSet;
		}

		/**
		 * The matcher for which we have instantiated this record.
		 */
		protected final M matcher;
		
		/**
		 * Flag indicating that  {@link #find()}  already returned <code>false</code>.
		 */
		private boolean findReturnedFalse;

		/**
		 * Flag indicating that  {@link #find()}  was already called at least once (since the last  {@link #reset()} ).
		 */
		private boolean findCalled;

		/**
		 * Flag indicating that  {@link #initImages()}  has been called (so  {@link #singular}  and  {@link #potentialImage}  have valid values).
		 */
		private boolean imagesInitialised;
		/**
		 * Flag indicating that  {@link EdgeSearchItem#edge}  has no more than one potential image.
		 */
		private boolean singular;
		/**
		 * The single image of  {@link EdgeSearchItem#edge}, provided  {@link #singular} holds. May be <code>null</code> if there is no image at all.
		 */
		private Edge potentialImage;
		/**
		 * The set of images for the item's edge.
		 */
		private Collection<? extends Edge> potentialImageSet;
		/**
		 * An iterator over the images for the item's edge.
		 */
		private Iterator<? extends Edge> potentialImageIter;
		/**
		 * The image for {@link #edge} set during the last call to {@link #find()}.
		 */
		protected Edge selected;
	}

	/**
	 * Record of an edge seach item, storing an iterator over the candidate
	 * images.
	 * 
	 * @author Arend Rensink
	 * @version $Revision $
	 */
	protected class SingularEdgeRecord<M extends Matcher> implements Record {
		/**
		 * Creates a record based on a given underlying matcher.
		 */
		protected SingularEdgeRecord(M matcher) {
			this.matcher = matcher;
			assert !matcher.getMorphism().containsKey(edge);
		}

		public boolean find() {
			boolean result;
			// first test if we know the method already returned false
			if (findReturnedFalse) {
				reset();
			} else if (selected) {
				undo();
			}
			// if the method was already called,
			// it should now certainly return false
			if (findCalled) {
				result = false;
			} else {
				// get the only possible image
				Edge image = getSingular();
				// maybe there is none
				result = image != null && select(image);
			}
			findReturnedFalse = !result;
			findCalled = true;
			return result;
		}

		/**
		 * Removes the edge added during the last {@link #find()}, if any.
		 */
		public void undo() {
			if (selected) {
				NodeEdgeMap elementMap = matcher.getSingularMap();
				Edge oldImage = elementMap.removeEdge(edge);
				assert oldImage.equals(potentialImage);
				selected = false;
			} else {
				throw new IllegalStateException();
			}
		}

		public void reset() {
			if (selected) {
				throw new IllegalStateException();
			} else {
				imageInitialised = false;
				potentialImage = null;
				findCalled = false;
				findReturnedFalse = false;
			}
		}

		/**
		 * Selects an image for {@link EdgeSearchItem#edge}, after testing it
		 * for correctness.
		 * 
		 * @param image
		 *            the image to be selected
		 * @return <code>true</code> if <code>image</code> was indeed
		 *         selected
		 */
		public boolean select(Edge image) {
			assert image != null : "Selected image should not be null";
			assert !selected : String.format("Edge %s already has image %s in map %s",
					edge,
					potentialImage,
					matcher.getSingularMap());
			NodeEdgeMap elementMap = matcher.getSingularMap();
			elementMap.putEdge(edge, image);
			selected = true;
			return true;
		}

		protected Edge getSingular() {
			if (!imageInitialised) {
				potentialImage = computeSingular();
				imageInitialised = true;
			}
			return potentialImage;
		}

		/**
		 * Computes the potential singular image, or <code>null</code> if the
		 * potential image is not in the codomain.
		 */
		protected Edge computeSingular() {
			Edge result = edge.imageFor(matcher.getSingularMap());
			assert result != null;
			if (! matcher.cod().containsElement(result)) {
				result = null;
			}
			return result;
		}

		/**
		 * The matcher for which we have instantiated this record.
		 */
		protected final M matcher;

		/**
		 * Flag indicating that {@link #find()} already returned
		 * <code>false</code>.
		 */
		private boolean findReturnedFalse;

		/**
		 * Flag indicating that {@link #find()} was already called at least once
		 * (since the last {@link #reset()} ).
		 */
		private boolean findCalled;

		/**
		 * Flag indicating that {@link #computeSingular()} has been called (so
		 * {@link #singular} and {@link #potentialImage} have valid values).
		 */
		private boolean imageInitialised;

		/**
		 * The single image of {@link EdgeSearchItem#edge}, provided
		 * {@link #singular} holds. May be <code>null</code> if there is no
		 * image at all.
		 */
		private Edge potentialImage;
		/**
		 * The image for {@link #edge} set during the last call to
		 * {@link #find()}.
		 */
		protected boolean selected;
	}

	/**
	 * Creates a search item for a given edge, for which it is know
	 * which edge ends have already been matched (in the search plan) before this one.
	 * @param edge the edge to be matched
	 * @param preMatched array of booleans indicating if the corresponding edge
	 * end has been pre-matched according to the search plan; or <code>null</code>
	 * if all ends have been pre-matched.
	 */
	public EdgeSearchItem(E edge, boolean[] preMatched) {
		this.edge = edge;
		this.arity = edge.endCount();
		boolean allEndsPreMatched = true;
		if (preMatched != null) {
			for (boolean endPreMatched : preMatched) {
				allEndsPreMatched &= endPreMatched;
			}
		}
		this.preMatched = allEndsPreMatched ? null : preMatched;
	}
	
	public Record get(Matcher matcher) {
//		if (isAllEndsPreMatched()) {
//			return new SingularEdgeRecord<Matcher>(matcher);
//		} else {
			return new EdgeRecord<Matcher>(matcher);
//		}
	}
	
	/**
	 * Returns the edge for which this item tests.
	 */
	public E getEdge() {
		return edge;
	}
	
	/**
	 * Indicates if a given edge end has been pre-matched.
	 */
	protected boolean isPreMatched(int i) {
		return isAllEndsPreMatched() || preMatched[i];
	}
	
	/**
	 * Indicates if all edge ends have been pre-matched.
	 */
	protected boolean isAllEndsPreMatched() {
		return preMatched == null;
	}
	
	/**
	 * Returns an array of edge indices such that
	 * result[i] is the smallest j smaller than or equal to i
	 * such that <code>edge.end(result[i]) == edge.end(i)</code>.
	 */
	private int[] getDuplicates() {
		if (duplicates == null) {
			duplicates = computeDuplicates();
		}
		return duplicates;
	}
	
	/**
	 * Computes the duplicates array
	 */
	private int[] computeDuplicates() {
		int[] result = new int[arity];
		for (int i = 0; i < arity; i++) {
			Node end = edge.end(i);
			int duplicate = 0;
			while (edge.end(duplicate) != end) {
				duplicate++;
			}
			result[i] = duplicate;
		}
		return result;
	}
	
	/**
	 * Callback factory method for a node search item.
	 */
	protected NodeSearchItem createNodeSearchItem(Node node) {
		return new NodeSearchItem(node);
	}
	
	@Override
	public String toString() {
		return String.format("Find %s", getEdge()); 
	}
			
	/**
	 * The edge for which this search item is to find an image.
	 */
	protected final E edge;
	/** The number of ends of {@link #edge}. */
	protected final int arity;
	/**
	 * Array of flags indicating if the corresponding end of {@link #edge}.
	 * May be <code>null</code> if all ends are matched.
	 */
	protected final boolean[] preMatched;
	/**
	 * Array of lower end indices that are duplicates of a given end.
	 * That is, duplicates[i] is the smallest j smaller than or equal to i
	 * such that <code>edge.end(duplicates[i]) == edge.end(i)</code>.
	 */
	private int[] duplicates;
}
