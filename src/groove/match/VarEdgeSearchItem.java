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
 * $Id: VarEdgeSearchItem.java,v 1.6 2007-09-22 09:10:35 rensink Exp $
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
import java.util.Iterator;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class VarEdgeSearchItem extends EdgeSearchItem {

	/** 
	 * Constructs a new search item.
	 * The item will match any edge between the end images, and record
	 * the edge label as value of the wildcard variable.
	 */
	public VarEdgeSearchItem(Edge edge) {
		super(edge);
		this.var = RegExprLabel.getWildcardId(edge.label());
        this.boundVars = Collections.singleton(var);
		assert this.var != null : String.format("Edge %s is not a variable edge", edge);
		assert edge.endCount() <= BinaryEdge.END_COUNT : String.format("Search item undefined for hyperedge", edge);
	}
	
	@Override
	public EdgeRecord getRecord(Search search) {
		return new VarEdgeRecord(search);
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
        varIx = strategy.getVarIx(var);
    }

    /** The variable bound in the wildcard (not <code>null</code>). */
	private final String var;
    /** Singleton set consisting of <code>var</code>. */
    private final Collection<String> boundVars;
    /** The index of {@link #var} in the result. */
    private int varIx;
    
    /** Record for this type of search item. */
    protected class VarEdgeRecord extends EdgeRecord {
        /** Constructs a new record, for a given matcher. */
        protected VarEdgeRecord(Search search) {
            super(search);
        }

        @Override
        void init() {
            super.init();
            varPreMatch = getSearch().getVar(varIx);
        }

        /** 
         * In addition checks if the label of the pre-matched edge is consistent with 
         * the variable. 
         */
        @Override
        Edge nextPreMatched() {
            Edge result = super.nextPreMatched();
            if (result != null && !setLabel(result)) {
                resetEnds();
                result = null;
            }
            return result;
        }

        /**
         * In addition to the super method returning <code>true</code>, the variable
         * should be pre-matched.
         */
        @Override
        boolean isPreDetermined() {
            return super.isPreDetermined() && varPreMatch != null;
        }

        /** This implementation returns the pre-matched label. */
        @Override
        Label getPreMatchedLabel() {
            return varPreMatch;
        }

        @Override
        Iterator< ? extends Edge> computeMultiple() {
            if (varPreMatch != null) {
                return filterImages(getTarget().labelEdgeSet(arity, varPreMatch), false);
            }
            // take the incident edges of the pre-matched source or target, if any
            // otherwise, the set of all edges
            Node imageEnd = getPreMatchedSource();
            if (imageEnd == null) {
                imageEnd = getPreMatchedTarget();
            }
            Collection< ? extends Edge> edgeSet = imageEnd == null ? getTarget().edgeSet() : getTarget().edgeSet(imageEnd);
            return filterImages(edgeSet, true);
        }

        /** Selects the variable image. */
        @Override
        final boolean setLabel(Edge image) {
            boolean result;
            if (varPreMatch == null) {
                Label current = getSearch().putVar(varIx, image.label());
                assert current == null;
                result = true;
            } else {
                result = image.label() == varPreMatch;
                assert getSearch().getVar(varIx) == varPreMatch;
            }
            return result;
        }

        /**
         * Callback method from {@link #undo()} to roll back the variable selection.
         * Reverses the effect of the last {@link #setLabel(Edge)} invocation.
         */
        @Override
        void resetLabel() {
            if (varPreMatch == null) {
                Label oldImage = getSearch().putVar(varIx, null);
                assert selected == null || oldImage == selected.label();
            }
        }

        /** 
         * The pre-matched variable image, if any.
         */
        private Label varPreMatch;
    }
}
