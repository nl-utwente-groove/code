/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
 * $Id$
 */
package groove.abstraction.pattern.match;

import groove.abstraction.pattern.match.Matcher.Search;
import groove.abstraction.pattern.shape.PatternGraph;
import groove.abstraction.pattern.trans.RuleEdge;
import groove.abstraction.pattern.trans.RuleNode;

import java.util.Collection;
import java.util.Collections;

/**
 * Class for an item in a search plan. The use of a search item has the
 * following phases:
 * <ul>
 * <li> Creation (constructor call). At this time nothing is known about the
 * ordering of search items in the search plan, so nothing is known about
 * already found or pre-matched parts.
 * <li> Activation (call of {@link #activate(Matcher)}). At this
 * time the ordering of the items is known, and indices for the parts can be
 * obtained, as well as knowledge about which parts are already found.
 * <li> Record creation (call of {@link #createRecord(Search)}).
 * At this time the pre-matched images are known. Found images are also known,
 * but are due to change at subsequent finds.
 * <li> Record usage (call of {@link Record#next()}). At this time the found
 * images are known.
 * </ul>
 * @author Arend Rensink
 */
public abstract class SearchItem implements Comparable<SearchItem> {

    /** 
     * Flag indicating the relevance of this search item.
     * Default value is <code>true</code>
     * @see #isRelevant()
     */
    private boolean relevant = true;

    /**
     * Creates an activation record for this search item, for a given search.
     */
    abstract Record createRecord(Search search);

    /**
     * Returns a rating for this search item, for the purpose of its natural
     * ordering. An item with higher rating gets scheduled first (failing more
     * urgent criteria).
     */
    abstract int getRating();

    /**
     * Prepares the search item for actual searching by providing additional
     * information about the strategy.
     */
    abstract void activate(Matcher matcher);

    /**
     * Returns the collection of nodes that should already be matched before
     * this item should be scheduled.
     */
    public Collection<RuleNode> needsNodes() {
        return Collections.emptySet();
    }

    /**
     * Returns the collection of nodes for which this search item will find a
     * matching when activated.
     */
    public Collection<RuleNode> bindsNodes() {
        return Collections.emptySet();
    }

    /**
     * Returns the collection of edges for which this search item will find a
     * matching.
     */
    public Collection<RuleEdge> bindsEdges() {
        return Collections.emptySet();
    }

    /**
     * Signals if the image of this search item is a relevant part of the match.
     * An attempt is made by the search strategy to return only matches that
     * differ on relevant parts.
     */
    public final boolean isRelevant() {
        return this.relevant;
    }

    /** 
     * Changes the relevance status of this search item.
     * @see #isRelevant()
     */
    final void setRelevant(boolean relevant) {
        this.relevant = relevant;
    }

    /**
     * This implementation compares items on the basis of their class names, and
     * after that, on the basis of their ratings. A lower rating means a
     * "smaller" search item, which is scheduled earlier.
     */
    public int compareTo(SearchItem other) {
        int result = getClass().getName().compareTo(other.getClass().getName());
        if (result == 0) {
            result = getRating() - other.getRating();
        }
        return result;
    }

    /**
     * Interface for an activation record of a search item.
     * @author Arend Rensink
     */
    interface Record {
        /** Initialises the record for a given host graph. */
        void initialise(PatternGraph host);

        /** Returns the relevance status of the enclosing search item. */
        boolean isRelevant();

        /**
         * Indicates that (in the last search) there where no matches of
         * this record at all. This implies that the search can backtrack
         * to the most recent dependency of this item.
         */
        boolean isEmpty();

        /**
         * Tries to find (and select, if appropriate) the next fit for this
         * search item. Where necessary, the previously selected fit is first
         * undone. The return value indicates if a new fit has been found (and
         * selected).
         * @return <code>true</code> if a fit has been found
         */
        boolean next();

        /**
         * Resets the record so that the previous sequence of find actions
         * is repeated. This is more efficient than {@link #reset()}, but
         * is only valid no {@link #next()} was invoked on a search item
         * on which this one depends.
         */
        void repeat();

        /**
         * Resets the record to the initial state, at which the search can be
         * restarted.
         */
        void reset();
    }

}
