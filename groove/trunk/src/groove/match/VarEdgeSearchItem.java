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
 * $Id: VarEdgeSearchItem.java,v 1.18 2008-01-30 09:33:29 iovka Exp $
 */
package groove.match;

import groove.graph.BinaryEdge;
import groove.graph.Edge;
import groove.graph.Label;
import groove.match.SearchPlanStrategy.Search;
import groove.rel.RegExprLabel;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
class VarEdgeSearchItem extends Edge2SearchItem {

	/** 
	 * Constructs a new search item.
	 * The item will match any edge between the end images, and record
	 * the edge label as value of the wildcard variable.
	 */
	public VarEdgeSearchItem(BinaryEdge edge) {
		super(edge);
		this.var = RegExprLabel.getWildcardId(edge.label());
		this.labelConstraint = RegExprLabel.getWildcardGuard(edge.label());
        this.boundVars = Collections.singleton(var);
		assert this.var != null : String.format("Edge %s is not a variable edge", edge);
		assert edge.endCount() <= BinaryEdge.END_COUNT : String.format("Search item undefined for hyperedge", edge);
	}
	
	/**
     * This implementation returns a singleton set consisting of the variable
     * bound by this item.
     */
    @Override
    public Collection<String> bindsVars() {
        return boundVars;
    }

    @Override
    public void activate(SearchPlanStrategy strategy) {
        super.activate(strategy);
        varFound = strategy.isVarFound(var);
        varIx = strategy.getVarIx(var);
    }

	@Override
	boolean isSingular(Search search) {
		return super.isSingular(search) && (varFound || search.getVarAnchor(varIx) != null);
	}

	@Override
	SingularRecord createSingularRecord(Search search) {
		return new VarEdgeSingularRecord(search, edgeIx, sourceIx, targetIx, varIx);
	}

    @Override
	MultipleRecord<Edge> createMultipleRecord(Search search) {
		return new VarEdgeMultipleRecord(search, edgeIx, sourceIx, targetIx, varIx, sourceFound, targetFound, varFound);
	}

    boolean isLabelConstraintSatisfied(Label label) {
        return labelConstraint == null || labelConstraint.isSatisfied(label.text());
    }
    
	/** The variable bound in the wildcard (not <code>null</code>). */
	private final String var;
    /** Singleton set consisting of <code>var</code>. */
    private final Collection<String> boundVars;
    /** The index of {@link #var} in the result. */
    int varIx;
    /** Flag indicating that {@link #var} is matched before this item is invoked. */
    boolean varFound;

	/** The constraint on the variable valuation, if any. */
	private final groove.util.Property<String> labelConstraint; 
    
    class VarEdgeSingularRecord extends Edge2SingularRecord {
    	/** 
    	 * Constructs a record from a given search, and 
    	 * (possibly <code>null</code>) pre-matched end node and variable images.
    	 */
		VarEdgeSingularRecord(Search search, int edgeIx, int sourceIx, int targetIx, int varIx) {
			super(search, edgeIx, sourceIx, targetIx);
			this.varIx = varIx;
			this.varPreMatch = search.getVarAnchor(varIx);
		}

		/** This implementation returns the variable image from the match. */
		@Override
		Label getLabel() {
			Label result = varPreMatch;
			if (result == null) {
				result = search.getVar(varIx);
			}
			return result;
		}

		/** Tests the label constraint, in addition to calling the super method. */
		@Override
		boolean isImageCorrect(Edge image) {
			return isLabelConstraintSatisfied(edge.label()) && super.isImageCorrect(image);
		}

		private final Label varPreMatch;    
	    /** The index of {@link #var} in the result. */
	    private final int varIx;
    }
    
    /** Record for this type of search item. */
    class VarEdgeMultipleRecord extends Edge2MultipleRecord {
        /** Constructs a new record, for a given matcher. */
        VarEdgeMultipleRecord(Search search, int edgeIx, int sourceIx, int targetIx, int varIx, boolean sourceFound, boolean targetFound, boolean varFound) {
            super(search, edgeIx, sourceIx, targetIx, sourceFound, targetFound);
            this.varFound = varFound;	
            this.varIx = varIx;
            this.varPreMatch = search.getVarAnchor(varIx);
        }

        @Override
        void init() {
            // first initialise varFind, otherwise the images will not be set correctly
            varFind = varPreMatch;
            if (varFind == null && varFound) {
            	varFind = search.getVar(varIx);
            }
            super.init();
        }

        @Override
        void initImages() {
            Set<? extends Edge> edgeSet;
            if (varFind != null) {
                if (isLabelConstraintSatisfied(varFind)) {
                    edgeSet = host.labelEdgeSet(arity, varFind);
                } else {
                    edgeSet = EMPTY_IMAGE_SET;
                }
            } else {
            	// take the incident edges of the pre-matched source or target, if any
            	// otherwise, the set of all edges
                if (sourceFind != null) {
                    edgeSet = host.outEdgeSet(sourceFind);
                } else if (targetFind != null) {
                    edgeSet = host.edgeSet(targetFind, Edge.TARGET_INDEX);
                } else {
                    edgeSet = host.edgeSet();
                }
            }
            initImages(edgeSet, true, true, false, true);
        }

        @Override
		boolean setImage(Edge image) {
            boolean result = isLabelConstraintSatisfied(image.label()) && super.setImage(image);
            if (result && varFind == null) {
                result = search.putVar(varIx, image.label());
            }
			return result;
		}

        @Override
		public void reset() {
			super.reset();
			if (varFind == null) {
				search.putVar(varIx, null);
			}
		}

		/**
         * The pre-matched variable image, if any.
         */
        private final Label varPreMatch;    
	    /** The index of {@link #var} in the result. */
	    private final int varIx;
        /** Flag indicating that {@link #var} is matched before this item is invoked. */
        private final boolean varFound;
        /** 
         * The found variable image, if any.
         */
        private Label varFind;
    }
    
    private static final Set<Edge> EMPTY_IMAGE_SET = Collections.emptySet();
}
