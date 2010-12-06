/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: SearchItem.java,v 1.8 2008-01-30 09:33:29 iovka Exp $
 */
package groove.match;

import groove.graph.Edge;
import groove.rel.LabelVar;
import groove.trans.RuleNode;

import java.util.Collection;

/**
 * Interface for an item in a search plan. The use of a search item has the
 * following phases:
 * <ul>
 * <li> Creation (constructor call). At this time nothing is known about the
 * ordering of search items in the search plan, so nothing is known about
 * already found or pre-matched parts.
 * <li> Activation (call of {@link #activate(SearchPlanStrategy)}). At this
 * time the ordering of the items is known, and indices for the parts can be
 * obtained, as well as knowledge about which parts are already found.
 * <li> Record creation (call of {@link #getRecord(SearchPlanStrategy.Search)}).
 * At this time the pre-matched images are known. Found images are also known,
 * but are due to change at subsequent finds.
 * <li> Record usage (call of {@link Record#find()}). At this time the found
 * images are known.
 * </ul>
 * @author Arend Rensink
 * @version $Revision $
 */
public interface SearchItem extends Comparable<SearchItem> {
    /**
     * Creates an activation record for this search item, for a given search.
     */
    Record getRecord(SearchPlanStrategy.Search search);

    /**
     * Returns the collection of nodes that should already be matched before
     * this item should be scheduled.
     */
    Collection<RuleNode> needsNodes();

    /**
     * Returns the collection of nodes for which this search item will find a
     * matching when activated.
     */
    Collection<RuleNode> bindsNodes();

    /**
     * Returns the collection of label variables that should already be matched
     * before this item should be scheduled.
     */
    Collection<LabelVar> needsVars();

    /**
     * Returns the collection of label variables for which this search item will
     * find a matching when activated.
     */
    Collection<LabelVar> bindsVars();

    /**
     * Returns the collection of edges for which this search item will find a
     * matching.
     */
    Collection<? extends Edge> bindsEdges();

    /**
     * Signals if the image of this search item is a relevant part of the match.
     * An attempt is made by the search strategy to return only matches that
     * differ on relevant parts.
     */
    boolean isRelevant();
    
    /**
     * Prepares the search item for actual searching by providing additional
     * information about the strategy.
     * @param strategy the search strategy to be applied
     */
    void activate(SearchPlanStrategy strategy);

    /**
     * Interface for an activation record of a search item.
     * @author Arend Rensink
     * @version $Revision $
     */
    interface Record {
        /** Returns the relevance status of the enclosing search item. */
        boolean isRelevant();

        /**
         * Indicates if this search record is known to be successful no more
         * than once in a row. That is, the record is singular if
         * {@link #find()} will return <code>true</code> at most once before
         * the next {@link #reset()}.
         */
        boolean isSingular();

        /**
         * Tries to find (and select, if appropriate) the next fit for this
         * search item. Where necessary, the previously selected fit is first
         * undone. The return value indicates if a new fit has been found (and
         * selected).
         * @return <code>true</code> if a fit has been found
         */
        boolean find();

        /**
         * Resets the record to the initial state, at which the search can be
         * restarted.
         */
        void reset();
    }
}
