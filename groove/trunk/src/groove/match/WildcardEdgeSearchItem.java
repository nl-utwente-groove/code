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
 * $Id: WildcardEdgeSearchItem.java,v 1.4 2007-08-30 15:18:18 rensink Exp $
 */
package groove.match;

import groove.graph.BinaryEdge;
import groove.graph.Edge;
import groove.graph.Label;
import groove.match.SearchPlanStrategy.Search;
import groove.rel.RegExprLabel;

import java.util.Iterator;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class WildcardEdgeSearchItem extends EdgeSearchItem {
	/** 
	 * Constructs a new search item.
	 * The item will match any edge between the end images, and record
	 * the edge label as value of the wildcard variable.
	 */
	public WildcardEdgeSearchItem(Edge edge) {
		super(edge);
		assert RegExprLabel.isWildcard(edge.label()) && RegExprLabel.getWildcardId(edge.label()) == null: String.format("Edge %s is not a true wildcard edge", edge);
		assert edge.endCount() <= BinaryEdge.END_COUNT : String.format("Search item undefined for hyperedge", edge);
	}
	
	@Override
	public EdgeRecord getRecord(Search search) {
		return new WildcardEdgeRecord(search);
	}
    
    /** Record for this type of search item. */
    protected class WildcardEdgeRecord extends EdgeRecord {
        /** Constructs a new record, for a given matcher. */
        protected WildcardEdgeRecord(Search search) {
            super(search);
        }

        /** This method returns <code>false</code>. */
        @Override
        boolean isPreDetermined() {
            return false;
        }
        
        /**
         * This implementation returns <code>null</code>.
         */
        @Override
        Label getPreMatchedLabel() {
            throw new UnsupportedOperationException();
        }

        @Override
        Iterator< ? extends Edge> computeMultiple() {
            if (getPreMatchedSource() != null) {
                return filterImages(getTarget().edgeSet(getPreMatchedSource()), false);
            } else if (getPreMatchedTarget() != null) {
                return filterImages(getTarget().edgeSet(getPreMatchedTarget()), false);
            } else {
                return filterImages(getTarget().edgeSet(), false);
            }
        }
    }
}
