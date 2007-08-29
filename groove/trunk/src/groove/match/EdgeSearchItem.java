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
 * $Id: EdgeSearchItem.java,v 1.3 2007-08-29 11:07:44 rensink Exp $
 */
package groove.match;

import groove.graph.DefaultEdge;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.match.SearchPlanStrategy.Search;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class EdgeSearchItem extends AbstractSearchItem {
	/**
	 * Creates a search item for a given edge, for which it is know
	 * which edge ends have already been matched (in the search plan) before this one.
	 * @param edge the edge to be matched
	 */
	public EdgeSearchItem(Edge edge) {
		this.edge = edge;
//        this.endPreMatched = new boolean[edge.endCount()];
		this.arity = edge.endCount();
        this.duplicates = computeDuplicates();
//		boolean allEndsBound = true;
//		if (bound != null) {
//			for (boolean endBound : bound) {
//				allEndsBound &= endBound;
//			}
//		}
//		this.preMatched = allEndsBound ? null : bound;
        this.boundNodes = new HashSet<Node>(Arrays.asList(edge.ends()));
	}
	
    @Override
	public EdgeRecord getRecord(Search matcher) {
		return new EdgeRecord( matcher);
	}
	
	/**
     * Returns the end nodes of the edge.
     */
    @Override
    public Collection<Node> bindsNodes() {
        return boundNodes;
    }
//    
//    public void schedule(Collection<Node> preMatchedNodes, Collection<String> preMatchedVars) {
//        allEndsPreMatched = true;
//        for (int i = 0; i < arity; i++) {
//            allEndsPreMatched &= endPreMatched[i] = preMatchedNodes.contains(edge.end(i));
//        }
//    }

    /**
	 * Returns the edge for which this item tests.
	 */
	public Edge getEdge() {
		return edge;
	}
//	
//	/**
//	 * Indicates if a given edge end has been pre-matched.
//	 */
//	protected boolean isPreMatched(int i) {
//		return isAllPreMatched() || endPreMatched[i];
//	}
//	
//	/**
//	 * Indicates if all edge ends have been pre-matched.
//	 */
//	protected boolean isAllPreMatched() {
//		return endPreMatched == null;
//	}
//	
//	/**
//	 * Returns an array of edge indices such that
//	 * result[i] is the smallest j smaller than or equal to i
//	 * such that <code>edge.end(result[i]) == edge.end(i)</code>.
//	 */
//	private int[] getDuplicates() {
//		if (duplicates == null) {
//			duplicates = computeDuplicates();
//		}
//		return duplicates;
//	}
	
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
//	
//	/**
//	 * Callback factory method for a node search item.
//	 */
//	protected NodeSearchItem createNodeSearchItem(Node node) {
//		return new NodeSearchItem(node);
//	}
	
	@Override
	public String toString() {
		return String.format("Find %s", getEdge()); 
	}
    
    /**
     * This implementation first attempts to compare edge labels and ends,
     * if the other search item is also an {@link EdgeSearchItem};
     * otherwise, it delegates to super. 
     */
    @Override
    public int compareTo(SearchItem other) {
        int result = 0;
        if (other instanceof EdgeSearchItem) {
            Edge otherEdge = ((EdgeSearchItem) other).getEdge();
            result = getEdge().label().compareTo(otherEdge.label());
            for (int i = 0; result == 0 && i < arity; i++) {
                result = edge.end(i).compareTo(otherEdge.end(i));
            }
        } 
        if (result == 0) {
            return super.compareTo(other);
        } else {
            return result;
        }
    }

    /**
     * This method returns the hash code of the label as rating.
     */
    @Override
    int getRating() {
        return edge.label().hashCode();
    }

    /**
	 * The edge for which this search item is to find an image.
	 */
	private final Edge edge;
	/** The number of ends of {@link #edge}. */
	private final int arity;
//	/**
//	 * Array of flags indicating if the corresponding end of {@link #edge} is scheduled
//     * to be pre-matched.
//	 */
//	private final boolean[] endPreMatched;
//    /**
//     * Flag indicating that all the edge ends have been scheduled to be pre-matched.
//     */
//    private boolean allEndsPreMatched;
	/**
	 * Array of lower end indices that are duplicates of a given end.
	 * That is, duplicates[i] is the smallest j smaller than or equal to i
	 * such that <code>edge.end(duplicates[i]) == edge.end(i)</code>.
	 */
	private final int[] duplicates;
    /** The set of end nodes of this edge. */
    private final Set<Node> boundNodes;

    /**
     * Record of an edge seach item, storing an iterator over the
     * candidate images.
     * @author Arend Rensink
     * @version $Revision $
     */
    protected class EdgeRecord extends AbstractRecord {
        /**
         * Creates a record based on a given underlying matcher.
         */
        protected EdgeRecord(Search search) {
            super(search);
            endPreMatches = new Node[arity];
            assert ! getResult().containsKey(edge) : String.format("Edge %s already in %s", edge, getResult());
        }

        @Override
        void exit() {
            multipleIter = null;
            endsPreMatched = false;
            edgePreMatch = null;
        }

        @Override
        void init() {
            endsPreMatched = true;
            for (int i = 0; i < arity; i++) {
                Node endImage = getResult().getNode(edge.end(i));
                endPreMatches[i] = endImage;
                endsPreMatched &= endImage != null;
            }
            edgePreMatch = getResult().getEdge(getEdge());
        }
        
        @Override
        boolean next() {
            boolean result;
            if (isSingular()) {
                if (isFirst()) {
                    Edge image = getSingular();
                    result = image != null && select(image);
                } else {
                    result = false;
                }
            } else {
                // there is more than one possible image
                result = false;
                // iterate over the possible images until one is found
                Iterator<? extends Edge> imageIter = getMultiple();
                while (!result && imageIter.hasNext()) {
                    result = select(imageIter.next());
                }
            }
            return result;
        }

        /**
         * Indicates if the record is singular, i.e., there is exactly one potential image.
         * This is the case if either all edge ends are pre-matched or the edge itself is pre-matched.
         * @return if <code>true</code>, there is at most one potential image
         * (which can then be obtained by invoking {@link #getSingular()})
         * @see #isPreMatched()
         * @see #isPreDetermined()
         */
        final boolean isSingular() {
            return isPreMatched() || isPreDetermined();
        }

        /**
         * Callback method from {@link #isSingular()} to signal if the edge
         * is pre-matched.
         */
        final boolean isPreMatched() {
            return edgePreMatch != null;
        }
        
        /** 
         * Callback method from {@link #isSingular()} to signal if the edge image
         * is completely determined by the pre-matched ends.
         */
        boolean isPreDetermined() {
            return endsPreMatched;
        }
        
        /** 
         * Returns the singular image of the searched edge,
         * if indeed the image is singular according to {@link #isSingular()}.
         * @return the unique image according to this search record,
         * or <code>null</code> if the image is not singular.
         */
        Edge getSingular() {
            if (edgePreMatch != null) {
                return edgePreMatch;
            } else if (isPreDetermined()) {
                Edge result = computePreDetermined();
                if (getTarget().containsElement(result)) {
                    return result;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
        
        /** 
         * Factory method to create a binary edge from the pre-matched edge ends.
         * Calls {@link #getPreMatchedSource()}, {@link #getPreMatchedTarget()} and {@link #getPreMatchedLabel()}
         * for the edge parts. 
         */
        final Edge computePreDetermined() {
            return DefaultEdge.createEdge(getPreMatchedSource(), getPreMatchedLabel(), getPreMatchedTarget());
        }
        
        /** Callback method from {@link #computePreDetermined()} to retrieve the pre-matched source end. */
        final Node getPreMatchedSource() {
            return endPreMatches[Edge.SOURCE_INDEX];
        }
        
        /** Callback method from {@link #computePreDetermined()} to retrieve the pre-matched target end. */
        final Node getPreMatchedTarget() {
            return endPreMatches[Edge.TARGET_INDEX];
        }
        
        /** Callback method from {@link #computePreDetermined()} to retrieve the pre-matched label. */
        Label getPreMatchedLabel() {
            return getEdge().label();
        }
        
        //
        //        /** 
        //         * Actually sets the image in the matcher's map.
        //         * Callback method for subclasses that give matches that are not edes.
        //         */
        //        protected void setSelectedImage(Edge image) {
        //            getResult().putEdge(edge, image);
        //        }
        //        
        //        /**
        //         * Indicates if there is at most one potential image, provided we can
        //         * detect this ceaply.
        //         * @return if <code>true</code>, there is at most one potential image
        //         * (which can then be obtained by invoking {@link #getSingular()});
        //         * if <code>false</code>, we have no information
        //         */
        //        protected boolean isSingular() {
        //            return singular != null;
        //        }
        //        
        //        /** 
        //         * Returns the singular image of the searched edge,
        //         * if indeed the image is singular according to {@link #isSingular()}.
        //         * Returns <code>null</code> if there are either fewer or more than
        //         * one image.
        //         */
        //        protected Edge getSingular() {
        //            return singular;
        //        }
                
        /** Returns an iterator over the potential images. */
        Iterator< ? extends Edge> getMultiple() {
            if (multipleIter == null) {
                multipleIter = computeMultiple().iterator();
            }
            return multipleIter;
        }

        Collection<? extends Edge> computeMultiple() {
            return getTarget().labelEdgeSet(arity, edge.label());
        }
        
        /**
         * Selects an image for {@link EdgeSearchItem#edge}, after testing it for correctness.
         * Successively calls {@link #selectEnds(Edge)} and (if successful)
         * {@link #selectEdge(Edge)}.
         * @param image the image to be selected
         * @return <code>true</code> if <code>image</code> was indeed selected
         */
        boolean select(Edge image) {
            assert image != null : "Selected image should not be null";
            boolean result = selectEnds(image);
            if (result) {
                selectEdge(image);
                selected = image;
            }
            return result;
        }
        
        /** Callback method from {@link #select(Edge)} to select the edge ends. */
        boolean selectEnds(Edge image) {
            boolean result = true;
            // if the ends are all pre-matched, there is nothing to do
            if (!isPreDetermined()) {
                NodeEdgeMap elementMap = getResult();
                int endIndex = 0;
                for (endIndex = 0; result && endIndex < arity; endIndex++) {
                    Node keyEnd = edge.end(endIndex);
                    Node imageEnd = image.end(endIndex);
                    if (duplicates[endIndex] < endIndex) {
                        // test if the intended image has the same duplication
                        result = imageEnd == image.end(duplicates[endIndex]);
                    } else if (endPreMatches[endIndex] != null) {
                        // test if the intended image has the correct end
                        result = imageEnd == endPreMatches[endIndex];
                    } else if (getSearch().isAvailable(imageEnd)) {
                        // put the end image in the result map
                        Node endImage = elementMap.putNode(keyEnd, imageEnd);
                        assert endImage == null : String
                                .format("Node %s already has image %s when selecting %s (map: %s)",
                                    keyEnd,
                                    endImage,
                                    imageEnd,
                                    elementMap);
                    } else {
                        result = false;
                    }
                }
                // roll back if one of the selections was unsuccessful
                if (!result) {
                    // deselect the selected node ends
                    for (endIndex--; endIndex >= 0; endIndex--) {
                        if (duplicates[endIndex] == endIndex && endPreMatches[endIndex] == null) {
                            elementMap.removeNode(edge.end(endIndex));
                        }
                    }
                }
            }
            return result;
        }

        /** 
         * Callback method from {@link #select(Edge)} to put the actual edge image into the result map,
         * if the image was not pre-selected. 
         */
        void selectEdge(Edge image) {
            if (edgePreMatch == null) {
                Edge current = getResult().putEdge(edge, image);
                assert current == null : String
                        .format("Edge %s already has image %s when selecting %s (map: %s)",
                            getEdge(),
                            current,
                            image,
                            getResult());
            }
        }
        
        /**
         * Successively calls {@link #undoEnds()} and {@link #undoEdge()}.
         */
        @Override
        void undo() {
            undoEnds();
            undoEdge();
            selected = null;
        }

        /**
         * Callback method from {@link #undo()} to undo the selection of the edge ends.
         * Reverses the effect of {@link #selectEnds(Edge)} if that method returned <code>true</code>.
         */
        void undoEnds() {
            if (! isPreDetermined()) {
                for (int i = 0; i < arity; i++) {
                    if (endPreMatches[i] == null && duplicates[i] == i) {
                        Node endImage = getResult().removeNode(edge.end(i));
                        assert endImage == selected.end(i) : String
                                .format("Node %s had image %s instead of expected %s (map: %s)",
                                    getEdge().end(i),
                                    endImage,
                                    selected.end(i),
                                    getResult());
                    }
                }
            }
        }

        /**
         * Callback method from {@link #undo()} to undo the selection of the edge itself.
         * Reverses the effect of {@link #selectEdge(Edge)} if that method returned <code>true</code>. 
         */
        void undoEdge() {
            if (edgePreMatch == null) {
                Edge image = getResult().removeEdge(edge);
                assert image.equals(selected) : String
                        .format("Edge %s had image %s instead of expected %s (map: %s)",
                            getEdge(),
                            image,
                            selected,
                            getResult());
            }
        }

        @Override
        public String toString() {
            return EdgeSearchItem.this.toString()+" = "+selected;
        }
//
//        /** 
//         * Actually sets the image in the matcher's map.
//         * Callback method for subclasses that give matches that are not edes.
//         */
//        protected void setSelectedImage(Edge image) {
//            getResult().putEdge(edge, image);
//        }
//        
//        /**
//         * Indicates if there is at most one potential image, provided we can
//         * detect this ceaply.
//         * @return if <code>true</code>, there is at most one potential image
//         * (which can then be obtained by invoking {@link #getSingular()});
//         * if <code>false</code>, we have no information
//         */
//        protected boolean isSingular() {
//            return singular != null;
//        }
//        
//        /** 
//         * Returns the singular image of the searched edge,
//         * if indeed the image is singular according to {@link #isSingular()}.
//         * Returns <code>null</code> if there are either fewer or more than
//         * one image.
//         */
//        protected Edge getSingular() {
//            return singular;
//        }
        
        //        /**
//         * The image for {@link #edge} set during the last call to {@link #next()}, if any.
//         */
//        private Edge edgeImage;
        /**
         * The pre-matched images for the edge ends, if any.
         * A value of <code>null</code> means that no image is currently selected
         * for the node, or the node was pre-matched.
         */
        private final Node[] endPreMatches;
        /**
         * Flag indicating that all edge ends were pre-metched.
         */
        private boolean endsPreMatched;

        /** The pre-matched image for the edge, if any. */
        private Edge edgePreMatch;
        /**
         * An iterator over the images for the item's edge.
         */
        private Iterator< ? extends Edge> multipleIter;
        /** Edge image with which {@link #select(Edge)} was successfully called. */ 
        Edge selected;
    }

    /**
     * Record of an edge seach item, storing an iterator over the candidate
     * images.
     * 
     * @author Arend Rensink
     * @version $Revision $
     */
    protected class SingularEdgeRecord implements Record {
        /**
         * Creates a record based on a given underlying matcher.
         */
        protected SingularEdgeRecord(SearchPlanStrategy.Search matcher) {
            this.search = matcher;
            assert !matcher.getResult().containsKey(edge);
        }

        public boolean find() {
            boolean result;
            // first test if we know the method already returned false
            if (findFailed) {
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
            findFailed = !result;
            findCalled = true;
            return result;
        }

        /**
         * Removes the edge added during the last {@link #find()}, if any.
         */
        public void undo() {
            if (selected) {
                NodeEdgeMap elementMap = search.getResult();
                Edge oldImage = elementMap.removeEdge(edge);
                assert oldImage.equals(potentialImage);
                selected = false;
            } else {
                throw new IllegalStateException();
            }
        }

        /** Resets the record to pristine state, so that the search can start anew. */
        public void reset() {
            if (selected) {
                undo();
            }
            imageInitialised = false;
            potentialImage = null;
            findCalled = false;
            findFailed = false;
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
                    search.getResult());
            NodeEdgeMap elementMap = search.getResult();
            elementMap.putEdge(edge, image);
            selected = true;
            return true;
        }

        /** 
         * Returns the singular image of the searched edge.
         * Returns <code>null</code> if the edge has no image at all.
         */
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
            Edge result = edge.imageFor(search.getResult());
            assert result != null;
            if (! search.getTarget().containsElement(result)) {
                result = null;
            }
            return result;
        }

        /**
         * The matcher for which we have instantiated this record.
         */
        protected final Search search;

        /**
         * Flag indicating that {@link #find()} already returned
         * <code>false</code>.
         */
        private boolean findFailed;

        /**
         * Flag indicating that {@link #find()} was already called at least once
         * (since the last {@link #reset()} ).
         */
        private boolean findCalled;

        /**
         * Flag indicating that {@link #computeSingular()} has been called (so
         * {@link #potentialImage} has a valid value).
         */
        private boolean imageInitialised;

        /**
         * The single image of {@link EdgeSearchItem#edge}. 
         * May be <code>null</code> if there is no image at all.
         */
        private Edge potentialImage;
        /**
         * The image for {@link #edge} set during the last call to
         * {@link #find()}.
         */
        protected boolean selected;
    }
}
