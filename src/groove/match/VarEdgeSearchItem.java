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
 * $Id: VarEdgeSearchItem.java,v 1.7 2007-09-25 15:12:34 rensink Exp $
 */
package groove.match;

import groove.graph.BinaryEdge;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
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
public class VarEdgeSearchItem extends Edge2SearchItem {

	/** 
	 * Constructs a new search item.
	 * The item will match any edge between the end images, and record
	 * the edge label as value of the wildcard variable.
	 */
	public VarEdgeSearchItem(BinaryEdge edge) {
		super(edge);
		this.var = RegExprLabel.getWildcardId(edge.label());
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
		return super.isSingular(search) && (varFound || search.getVarPreMatch(varIx) != null);
	}

	@Override
	SingularRecord createSingularRecord(Search search) {
		return new VarEdgeSingularRecord(search);
	}

    @Override
	MultipleRecord createMultipleRecord(Search search) {
		return new VarEdgeMultipleRecord(search);
	}

	/** The variable bound in the wildcard (not <code>null</code>). */
	private final String var;
    /** Singleton set consisting of <code>var</code>. */
    private final Collection<String> boundVars;
    /** The index of {@link #var} in the result. */
    int varIx;
    /** Flag indicating that {@link #var} is matched before this item is invoked. */
    boolean varFound;
    
    class VarEdgeSingularRecord extends Edge2SingularRecord {
    	/** 
    	 * Constructs a record from a given search, and 
    	 * (possibly <code>null</code>) pre-matched end node and variable images.
    	 */
		VarEdgeSingularRecord(Search search) {
			super(search);
			this.varPreMatch = search.getVarPreMatch(varIx);
		}

		/** This implementation returns the variable image from the match. */
		@Override
		Label getLabel() {
			Label result = varPreMatch;
			if (result == null) {
				result = getSearch().getVar(varIx);
			}
			return result;
		}
		
		private final Label varPreMatch;
    }
    
    /** Record for this type of search item. */
    class VarEdgeMultipleRecord extends Edge2MultipleRecord {
        /** Constructs a new record, for a given matcher. */
        VarEdgeMultipleRecord(Search search) {
            super(search);
            varPreMatch = search.getVarPreMatch(varIx);
        }

        @Override
        void init() {
            super.init();
            varFind = varPreMatch;
            if (varFind == null && varFound) {
            	varFind = getSearch().getVar(varIx);
            }
        }

        @Override
        void initImages() {
            Set<? extends Edge> edgeSet;
            if (varFind != null) {
            	edgeSet = getTarget().labelEdgeSet(arity, varFind);
            } else {
            	// take the incident edges of the pre-matched source or target, if any
            	// otherwise, the set of all edges
            	Node imageEnd = sourceFind == null ? targetFind : sourceFind;
            	edgeSet = imageEnd == null ? getTarget().edgeSet() : getTarget().edgeSet(imageEnd);
            }
            initImages(edgeSet, true, true, false, true);
        }

        @Override
		boolean setImage(Edge image) {
			boolean result = super.setImage(image);
			if (result && varFind == null) {
				getSearch().putVar(varIx, image.label());
			}
			return result;
		}

        @Override
		public void reset() {
			super.reset();
			if (varFind == null) {
				getSearch().putVar(varIx, null);
			}
		}

		/**
         * The pre-matched variable image, if any.
         */
        private final Label varPreMatch;
        /** 
         * The found variable image, if any.
         */
        private Label varFind;
    }
}
