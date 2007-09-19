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
 * $Id: EdgeSearchItem.java,v 1.8 2007-09-19 16:06:13 rensink Exp $
 */
package groove.match;

import groove.graph.DefaultEdge;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.algebra.ValueNode;
import groove.match.SearchPlanStrategy.Search;
import groove.util.FilterIterator;

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
        this.ends = edge.ends();
        this.label = edge.label();
		this.arity = edge.endCount();
        this.duplicates = computeDuplicates();
        this.boundNodes = new HashSet<Node>();
        this.neededNodes = new HashSet<Node>();
        for (Node node: edge.ends()) {
            if (isBindable(node)) {
                boundNodes.add(node);
            } else {
                neededNodes.add(node);
            }
        }
	}
	
	/** Determines whether a given node can be bound as a result of binding this edge. */
	private boolean isBindable(Node node) {
	    return !(node instanceof ValueNode) || !((ValueNode) node).hasValue();
	}
	
	public EdgeRecord getRecord(Search matcher) {
		return new EdgeRecord( matcher);
	}
    
    /**
     * Returns the non-value end nodes of the edge.
     */
    @Override
    public Collection<Node> bindsNodes() {
        return boundNodes;
    }
    
    /**
     * Returns the value end nodes of the edge.
     */
    @Override
    public Collection<Node> needsNodes() {
        return neededNodes;
    }

    /**
	 * Returns the edge for which this item tests.
	 */
	public Edge getEdge() {
		return edge;
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
            // compare first the edge labels, then the edge ends
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
	final Edge edge;
    /**
     * The ends of {@link #edge}, separately stored for efficiency.
     */
    final Node[] ends;
    /** The label of {@link #edge}, separately stored for efficiency. */
    final Label label;
	/** The number of ends of {@link #edge}. */
	final int arity;
	/**
	 * Array of lower end indices that are duplicates of a given end.
	 * That is, duplicates[i] is the smallest j smaller than or equal to i
	 * such that <code>edge.end(duplicates[i]) == edge.end(i)</code>.
	 */
	private final int[] duplicates;
    /** The set of non-value end nodes of this edge. */
    private final Set<Node> boundNodes;
    /** The set of value end nodes of this edge. */
    private final Set<Node> neededNodes;

    /**
     * Record of an edge search item, storing an iterator over the
     * candidate images.
     * @author Arend Rensink
     * @version $Revision $
     */
    class EdgeRecord extends AbstractRecord {
        /**
         * Creates a record based on a given search.
         */
        EdgeRecord(Search search) {
            super(search);
            endPreMatches = new Node[arity];
            assert ! getResult().containsKey(edge) : String.format("Edge %s already in %s", edge, getResult());
        }

        /** 
         * The record is singular if the image is either pre-matched
         * or pre-determined.
         * @see #isPreMatched()
         * @see #isPreDetermined()
         */
        public boolean isSingular() {
            return isPreMatched() || isPreDetermined();
        }

        @Override
        void exit() {
            multipleIter = null;
            allEndsPreMatched = false;
            preMatchedEndIndex = 0;
            edgePreMatch = null;
        }

        @Override
        void init() {
            allEndsPreMatched = true;
            preMatchedEndIndex = -1;
            for (int i = 0; i < arity; i++) {
                Node endImage = getResult().getNode(ends[i]);
                endPreMatches[i] = endImage;
                if (endImage == null) {
                    allEndsPreMatched = false;
                } else if (preMatchedEndIndex < 0) {
                    preMatchedEndIndex = i;
                }
            }
            edgePreMatch = getResult().getEdge(edge);
        }
        
        /**
         * If {@link #isPreMatched()}, delegates to {@link #nextPreMatched()}.
         * Otherwise, if {@link #isPreDetermined()}, delegates to {@link #nextPreDetermined()}.
         * Otherwise, delegates to {@link #nextMultiple()}.
         */
        @Override
        final boolean next() {
            Edge result;
            if (isPreMatched()) {
                result = nextPreMatched();
            } else if (isPreDetermined()) {
                result = nextPreDetermined();
            } else {
                result = nextMultiple();
            }
            if (result == null) {
                return false;
            } else {
                selected = result;
                return true;
            }
        }

        /**
         * Callback method to signal if the edge
         * is pre-matched in the result map.
         */
        final boolean isPreMatched() {
            return edgePreMatch != null;
        }

        /**
         * Callback method from {@link #next()} in case there is a pre-matched edge
         * in the result map (as tested by {@link #isPreMatched()}).
         * In that case, the edge ends need to be checked for consistency (using {@link #setEnds(Edge)}).
         * @return the pre-matched edge image, if the edge ends are consistent with thte current result map
         */
        Edge nextPreMatched() {
            Edge result = null;
            if (isFirst() && setEnds(edgePreMatch)) {
                result = edgePreMatch;
            } 
            return result;
        }

        /** 
         * Callback method to signal if the edge image
         * is completely determined by the pre-matched ends.
         */
        boolean isPreDetermined() {
            return allEndsPreMatched;
        }

        /**
         * Callback method from {@link #next()} in case the edge image can be completely
         * constructed from pre-matched parts (as tested by {@link #isPreDetermined()}).
         * This needs to be tested for presence in the target graph.
         * @return the pre-determined edge image, in case it occurs in the target
         */
        final Edge nextPreDetermined() {
            Edge result = null;
            if (isFirst()) {
                Edge image = computePreDetermined();
                if (image != null && getTarget().containsElement(image)) {
                    setEdge(image);
                    result = image;
                }
            }
            return result;
        }
        
        /** 
         * Factory method to create a binary edge from the pre-matched edge ends.
         * Calls {@link #getPreMatchedSource()}, {@link #getPreMatchedTarget()} and {@link #getPreMatchedLabel()}
         * for the edge parts. 
         */
        Edge computePreDetermined() {
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
            return label;
        }
        
        /**
         * Selects and returns the next correct edge from the target graph.
         * Callback method from {@link #next()} in case there is not a singular
         * (pre-matched or pre-determined) image.
         */
        final Edge nextMultiple() {
            if (multipleIter == null) {
                multipleIter = computeMultiple();
            }
            return multipleIter.hasNext() ? multipleIter.next() : null;
        }

        /**
         * Returns an interator over those images for {@link #edge} that are consistent with the
         * pre-matched edge ends.
         * The iterator actually selects the returned edges in the result map, as a side effect.
         */
        Iterator<? extends Edge> computeMultiple() {
            // it does not pay off here to take only the incident edges of pre-matched ends,
            // no doubt because building the necessary additional data structures takes more
            // time than is saved by trying out fewer images
            return filterImages(getTarget().labelEdgeSet(arity, label), false);
        }
        
        /**
         * Returns an iterator over the elements of a given image set,
         * which filters the edges for which {@link #setEnds(Edge)} is successful.
         * As a side effect, calls {@link #setEdge(Edge)} with the selected image
         * before returning it.
         * @param images the set of potential images
         * @param checkLabel flag to indicate if #selectLabel(Edge) should also be called
         * before selecting an edge
         */
        Iterator<? extends Edge> filterImages(Collection<? extends Edge> images, final boolean checkLabel) {
            return new FilterIterator<Edge>(images.iterator()) {
                @Override
                protected boolean approves(Object obj) {
                    Edge edge = (Edge) obj;
                    boolean result = !checkLabel || setLabel(edge);
                    if (result) {
                        result = setEnds(edge);
                        if (result) {
                            setEdge(edge);
                        } else if (checkLabel) {
                            resetLabel();
                        }
                    }
                    return result;
                }                
            };
        }
//        
//        /**
//         * Selects an image for {@link EdgeSearchItem#edge}, after testing it for correctness.
//         * Successively calls {@link #selectParts(Edge)} and (if successful)
//         * {@link #selectEdge(Edge)}.
//         * @param image the image to be selected
//         * @return <code>true</code> if <code>image</code> was indeed selected
//         */
//        boolean select(Edge image) {
//            assert image != null : "Selected image should not be null";
//            boolean result = selectParts(image);
//            if (result) {
//                selectEdge(image);
//                selected = image;
//            }
//            return result;
//        }
        
        /**
         * Selects the edge label.
         * In this case this just comes down to testing equality with the image label.
         */
        boolean setLabel(Edge image) {
            return label == image.label();
        }

        /** 
         * Select the edge end images, if they are compatible with
         * the pre-matched ends. 
         */
        boolean setEnds(Edge image) {
            boolean result = true;
            int endIndex = 0;
            for (endIndex = 0; result && endIndex < arity; endIndex++) {
                result = setEnd(endIndex, image);
            }
            // roll back if one of the selections was unsuccessful
            if (!result) {
                // deselect the selected node ends
                for (endIndex--; endIndex >= 0; endIndex--) {
                    resetEnd(endIndex);
                }
            }
            return result;
        }

        /**
         * Tests or selects the image of a given edge end.
         */
        final boolean setEnd(int endIndex, Edge image) {
            boolean result;
            Node imageEnd = image.end(endIndex);
            if (endPreMatches[endIndex] != null) {
                // test if the intended image has the correct end
                result = imageEnd == endPreMatches[endIndex];
            } else if (duplicates[endIndex] < endIndex) {
                // test if the intended image has the same duplication
                result = imageEnd == image.end(duplicates[endIndex]);
            } else if (isAvailable(imageEnd)) {
                Node keyEnd = ends[endIndex];
                // put the end image in the result map
                Node endImage = getResult().putNode(keyEnd, imageEnd);
                assert endImage == null : String
                        .format("Node %s already has image %s when selecting %s (map: %s)",
                            keyEnd,
                            endImage,
                            imageEnd,
                            getResult());
                result = true;
            } else {
                result = false;
            }
            return result;
        }
        
        /**
         * Puts the actual edge image into the result map,
         * unde the assumption that the edge is not pre-matched.
         */
        void setEdge(Edge image) {
            Edge current = getResult().putEdge(edge, image);
            assert current == null : String
                    .format("Edge %s already has image %s when selecting %s (map: %s)",
                        edge,
                        current,
                        image,
                        getResult());
        }
        
        /**
         * Successively calls {@link #resetEnds()} and {@link #resetEdge()}.
         */
        @Override
        final void undo() {
            if (!isPreDetermined()) {
                resetEnds();
                resetLabel();
            }
            if (!isPreMatched()) {
                resetEdge();
            }
            selected = null;
        }
        
        /** 
         * Rolls back the effect of {@link #setLabel(Edge)}. 
         * For this implementation, there is nothing to roll back.
         */
        void resetLabel() {
            // empty
        }

        /**
         * Callback method from {@link #undo()} to undo the selection of the edge ends.
         * Reverses the effect of {@link #setEnds(Edge)} if that method returned <code>true</code>.
         */
        final void resetEnds() {
            for (int i = 0; i < arity; i++) {
                resetEnd(i);
            }
        }

        final void resetEnd(int i) {
            if (endPreMatches[i] == null && duplicates[i] == i) {
                Node endImage = getResult().removeNode(ends[i]);
                assert selected == null || endImage == selected.end(i) : String
                        .format("Node %s had image %s instead of expected %s (map: %s)",
                            ends[i],
                            endImage,
                            selected.end(i),
                            getResult());
            }
        }
        
        /**
         * Callback method from {@link #undo()} to undo the selection of the edge itself. Reverses
         * the effect of {@link #setEdge(Edge)} if that method returned <code>true</code>.
         */
        void resetEdge() {
            Edge image = getResult().removeEdge(edge);
            assert image.equals(selected) : String
                    .format("Edge %s had image %s instead of expected %s (map: %s)",
                        edge,
                        image,
                        selected,
                        getResult());
        }

        @Override
        public String toString() {
            return EdgeSearchItem.this.toString()+" = "+selected;
        }

        /**
         * The pre-matched images for the edge ends, if any.
         * A value of <code>null</code> means that no image is currently selected
         * for the node, or the node was pre-matched.
         */
        private final Node[] endPreMatches;
        /**
         * Flag indicating that all edge ends were pre-metched.
         */
        private boolean allEndsPreMatched;
        /** Index of a pre-matched end, or <code>-1</code> if no end is pre-matched. */
        private int preMatchedEndIndex;

        /** The pre-matched image for the edge, if any. */
        private Edge edgePreMatch;
        /**
         * An iterator over the images for the item's edge.
         */
        private Iterator< ? extends Edge> multipleIter;
        /** Image found by the latest call to {@link #next()}, if any; erased by {@link #undo()}. */ 
        Edge selected;
    }
}
