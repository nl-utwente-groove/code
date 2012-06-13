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
 * $Id: Edge2SearchItem.java,v 1.15 2008-01-30 09:33:29 iovka Exp $
 */
package groove.match;

import groove.graph.BinaryEdge;
import groove.graph.DefaultEdge;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.algebra.ValueNode;
import groove.match.SearchPlanStrategy.Search;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
class Edge2SearchItem extends AbstractSearchItem {
	/**
	 * Creates a search item for a given binary edge.
	 * @param edge the edge to be matched
	 */
	public Edge2SearchItem(BinaryEdge edge) {
		this.edge = edge;
        this.source = edge.source();
        this.target = edge.target();
        this.label = edge.label();
		this.arity = edge.endCount();
        this.selfEdge = source == target;
        this.boundNodes = new HashSet<Node>(Arrays.asList(edge.ends()));
	}
	
	/**
     * Returns the end nodes of the edge.
     */
    @Override
    public Collection<? extends Node> bindsNodes() {
        return boundNodes;
    }

    /** Returns the singleton set consisting of the matched edge. */
    @Override
	public Collection<? extends Edge> bindsEdges() {
		return Collections.singleton(edge);
	}

	/**
	 * Returns the edge for which this item tests.
	 */
	public BinaryEdge getEdge() {
		return edge;
	}
	
	@Override
	public String toString() {
		return String.format("Find %s", getEdge()); 
	}
    
    /**
     * This implementation first attempts to compare edge labels and ends,
     * if the other search item is also an {@link Edge2SearchItem};
     * otherwise, it delegates to super. 
     */
    @Override
    public int compareTo(SearchItem other) {
        int result = 0;
        if (other instanceof Edge2SearchItem) {
            // compare first the edge labels, then the edge ends
            Edge otherEdge = ((Edge2SearchItem) other).getEdge();
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

    public void activate(SearchPlanStrategy strategy) {
    	// one would like the following assertion, 
    	// but since negative search items for the same edge also reserve the
    	// index, the assertion may fail in case of a positive and negative test
    	// on the same edge (stupid!)
    	// assert !strategy.isEdgeFound(edge);
        edgeIx = strategy.getEdgeIx(edge);
        sourceFound = strategy.isNodeFound(source);
        sourceIx = strategy.getNodeIx(source);
        if (selfEdge) {
        	targetFound = sourceFound;
        	targetIx = sourceIx;
        } else {
        	targetFound = strategy.isNodeFound(target);
        	targetIx = strategy.getNodeIx(target);
        }
    }

    /**
     * This method returns the hash code of the label as rating.
     */
    @Override
    int getRating() {
        return edge.label().hashCode();
    }

    final public Record getRecord(Search search) {
		if (isPreMatched(search)) {
			// the edge is unexpectedly pre-matched
			return createDummyRecord();
		} else if (isSingular(search)) {
			return createSingularRecord(search);
		} else {
			return createMultipleRecord(search);
		}
	}
    
    /** Indicates if the edge is pre-matched in the search. */
    boolean isPreMatched(Search search) {
    	return search.getEdgeAnchor(edgeIx) != null;
    }
    
    /** Indicates if the edge has a singular image in the search. */
    boolean isSingular(Search search) {
    	boolean sourceSingular = sourceFound || search.getNodeAnchor(sourceIx) != null;
    	boolean targetSingular = targetFound || search.getNodeAnchor(targetIx) != null;
    	return sourceSingular && targetSingular;
    }

    /** Creates a record for the case the image is singular. */
    SingularRecord createSingularRecord(Search search) {
    	return new Edge2SingularRecord(search, edgeIx, sourceIx, targetIx);
    }
    
    /** Creates a record for the case the image is not singular. */
    MultipleRecord<Edge> createMultipleRecord(Search search) {
    	return new Edge2MultipleRecord(search, edgeIx, sourceIx, targetIx, sourceFound, targetFound);
    }
	/**
	 * The edge for which this search item is to find an image.
	 */
	final BinaryEdge edge;
    /**
     * The source end of {@link #edge}, separately stored for efficiency.
     */
    final Node source;
    /**
     * The target end of {@link #edge}, separately stored for efficiency.
     */
    final Node target;
    /** The label of {@link #edge}, separately stored for efficiency. */
    final Label label;
	/** The number of ends of {@link #edge}. */
	final int arity;
	/**
	 * Flag indicating that {@link #edge} is a self-edge.
	 */
	final boolean selfEdge;
    /** The set of end nodes of this edge. */
    private final Set<Node> boundNodes;

    /** The index of the edge in the search. */
    int edgeIx;
    /** The index of the source in the search. */
    int sourceIx;
    /** The index of the target in the search. */
    int targetIx;
    /** Indicates if the source is found before this item is invoked. */
    boolean sourceFound;
    /** Indicates if the target is found before this item is invoked. */
    boolean targetFound;
    
    /** 
     * Search record to be used if the edge image is completely
     * determined by the pre-matched ends.
     * @author Arend Rensink
     * @version $Revision $
     */
    class Edge2SingularRecord extends SingularRecord {
    	/** Constructs an instance for a given search. */
		public Edge2SingularRecord(Search search, int edgeIx, int sourceIx, int targetIx) {
			super(search);
			this.edgeIx = edgeIx;
			this.sourceIx = sourceIx;
			this.targetIx = targetIx;
			this.sourcePreMatch = search.getNodeAnchor(sourceIx);
			this.targetPreMatch = search.getNodeAnchor(targetIx);
		}

		@Override
		final boolean set() {
			Edge image = getEdgeImage();
			assert image != null;
			boolean result = isImageCorrect(image);
			if (result) {
				search.putEdge(edgeIx, image);
			}
			return result;
		}
		
		/** Tests if the (uniquely determined) edge image can be used. */
		boolean isImageCorrect(Edge image) {
			return host.containsElement(image);
		}
		
		/** 
		 * Creates and returns the edge image, as constructed from
		 * the available end node images. 
		 */
		private Edge getEdgeImage() {
			Node sourceFind = this.sourcePreMatch;
			if (sourceFind == null) {
				sourceFind = search.getNode(sourceIx);
			}
			assert sourceFind != null : String.format("Source node of %s has not been found", edge);
			Node targetFind = this.targetPreMatch;
			if (targetFind == null) {
				targetFind = search.getNode(targetIx);
			}
			assert targetFind != null : String.format("Target node of %s has not been found", edge);
			return DefaultEdge.createEdge(sourceFind, getLabel(), targetFind);
		}
		
		/** Callback method to determine the label of the edge image. */
		Label getLabel() {
			return label;
		}

        @Override
        public String toString() {
            return Edge2SearchItem.this.toString()+" = "+getEdgeImage();
        }

		/** The pre-matched (fixed) source image, if any. */
		private final Node sourcePreMatch;
		/** The pre-matched (fixed) target image, if any. */
		private final Node targetPreMatch;
	    /** The index of the edge in the search. */
	    private final int edgeIx;
	    /** The index of the source in the search. */
	    private final int sourceIx;
	    /** The index of the target in the search. */
	    private final int targetIx;
    }
    
    /**
     * Record of an edge search item, storing an iterator over the
     * candidate images.
     * @author Arend Rensink
     * @version $Revision $
     */
    class Edge2MultipleRecord extends MultipleRecord<Edge> {
        /**
         * Creates a record based on a given search.
         */
        Edge2MultipleRecord(Search search, int edgeIx, int sourceIx, int targetIx, boolean sourceFound, boolean targetFound) {
            super(search);
            this.edgeIx = edgeIx;
            this.sourceIx = sourceIx;
            this.targetIx = targetIx;
            this.sourceFound = sourceFound;
            this.targetFound = targetFound;
            this.sourcePreMatch = search.getNodeAnchor(sourceIx);
            this.targetPreMatch = search.getNodeAnchor(targetIx);
            assert search.getEdge(edgeIx) == null : String.format("Edge %s already in %s", edge, search);
        }

        @Override
        void init() {
            sourceFind = sourcePreMatch;
            if (sourceFind == null && sourceFound) {
            	sourceFind = search.getNode(sourceIx);
                assert sourceFind != null : String.format("Source node of %s not found", edge); 
            }
            targetFind = targetPreMatch;
            if (targetFind == null && targetFound) {
            	targetFind = search.getNode(targetIx);
                assert targetFind != null : String.format("Target node of %s not found", edge); 
            }
            initImages();
        }
        
        @Override
        boolean setImage(Edge image) {
        	assert image instanceof BinaryEdge;
        	if (sourceFind == null) {
        		if (! search.putNode(sourceIx, image.source())) {
        			return false;
        		}
        	} else if (checkSource) {
        		if (image.source() != sourceFind) {
        			return false;
        		}
        	}
        	if (selfEdge) {
        	    if (image.opposite() != image.source()) {
        	        return false;
        	    }
        	} else {
        		if (targetFind == null) {
        			if (! search.putNode(targetIx, image.opposite())) { 
        				return false;
        			}
        		} else if (checkTarget) {
        			if (image.opposite() != targetFind) {
        				return false;
        			}
        		}
        	}
        	if (checkLabel) {
        		if (image.label() != label) {
            		return false;
            	}
        	}
        	if (setEdge) {
        		search.putEdge(edgeIx, image);
        	}
        	selected = image;
        	return true;
        }

        @Override
        public void reset() {
        	super.reset();
        	if (selected != null) {
        		if (setEdge) {
        			search.putEdge(edgeIx, null);
        		}
        		if (sourceFind == null) {
        			search.putNode(sourceIx, null);
        		}
           		if (targetFind == null && !selfEdge) {
        			search.putNode(targetIx, null);
        		}
        	}
        }
        
        /** IOVKA this comment is not updated !
         * Returns an iterator over those images for {@link #edge} that are consistent with the
         * pre-matched edge ends.
         * The iterator actually selects the returned edges in the result map, as a side effect.
         */
        void initImages() {
        	Set<? extends Edge> result = null;
        	boolean checkLabel = false;
            // it does not pay off here to take only the incident edges of pre-matched ends,
            // no doubt because building the necessary additional data structures takes more
            // time than is saved by trying out fewer images
        	Set<? extends Edge> labelEdgeSet = host.labelEdgeSet(arity, label);
        	if (sourceFind != null) {
        		Set<? extends Edge> nodeEdgeSet = host.edgeSet(sourceFind);
        		if (nodeEdgeSet.size() < labelEdgeSet.size()) {
        			result = nodeEdgeSet;
        			checkLabel = true;
        		}
			} else if (targetFind != null) {
        		Set<? extends Edge> nodeEdgeSet = host.edgeSet(targetFind);
        		if (nodeEdgeSet == null) {
                    assert targetFind instanceof ValueNode : String.format("Host graph does not contain edges for node %s", targetFind);
        		    result = Collections.emptySet();
        		} else if (nodeEdgeSet.size() < labelEdgeSet.size()) {
        			result = nodeEdgeSet;
        			checkLabel = true;
        		}
			}
        	if (result == null) {
        		result = labelEdgeSet;
        	}
        	initImages(result, true, true, checkLabel, true);
        }
        
        /** 
         * Callback method to set the iterator over potential images.
         * Also sets flags indicating whether potential images still have to be 
         * checked for correctness of the source, target or label parts.
         * @param imageSet the iterator over potential images
         * @param checkSource if <code>true</code>, the sources of potential images 
         * have to be compared with {@link #sourceFind}
         * @param checkTarget if <code>true</code>, the sources of potential images 
         * have to be compared with {@link #targetFind}
         * @param checkLabel if <code>true</code>, the sources of potential images 
         * have to be compared with #label.
         */
        final void initImages(Set<? extends Edge> imageSet, boolean checkSource, boolean checkTarget, boolean checkLabel, boolean setEdge) {
        	this.imageIter = imageSet.iterator();
        	this.checkSource = checkSource;
        	this.checkTarget = checkTarget;
        	this.checkLabel = checkLabel;
        	this.setEdge = setEdge;
        }

        @Override
        public String toString() {
            return Edge2SearchItem.this.toString()+" = "+selected;
        }

        /** The index of the edge in the search. */
        final private int edgeIx;
        /** The index of the source in the search. */
        final int sourceIx;
        /** The index of the target in the search. */
        final int targetIx;
        /** Indicates if the source is found before this item is invoked. */
        final private boolean sourceFound;
        /** Indicates if the target is found before this item is invoked. */
        final private boolean targetFound;
        
        private final Node sourcePreMatch;
        private final Node targetPreMatch;
        /**
         * The pre-matched image for the edge source, if any.
         * A value of <code>null</code> means that no image is currently selected
         * for the source, or the source was pre-matched.
         */
        Node sourceFind;
        /**
         * The pre-matched image for the edge target, if any.
         * A value of <code>null</code> means that no image is currently selected
         * for the target, or the target was pre-matched.
         */
        Node targetFind;
        /** 
         * Flag indicating the if sources of images returned by {@link #initImages()} 
         * have to be checked against the found source image.
         */
        private boolean checkSource;
        /** 
         * Flag indicating the if targets of images returned by {@link #initImages()} 
         * have to be checked against the found target image.
         */
        private boolean checkTarget;
        /** 
         * Flag indicating the if labels of images returned by {@link #initImages()} 
         * have to be checked against the edge label.
         */
        private boolean checkLabel;
        /** Flag indicating if the edge image should actually be set in the search. */
        private boolean setEdge;
        /** Image found by the latest call to {@link #find()}, if any. */ 
        Edge selected;
    }
}