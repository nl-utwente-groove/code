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
 * $Id$
 */
package groove.match.plan;

import groove.grammar.host.HostGraph;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleNode;
import groove.match.plan.PlanSearchStrategy.Search;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

/**
 * Interface for an item in a search plan. The use of a search item has the
 * following phases:
 * <ul>
 * <li> Creation (constructor call). At this time nothing is known about the
 * ordering of search items in the search plan, so nothing is known about
 * already found or pre-matched parts.
 * <li> Activation (call of {@link #activate(PlanSearchStrategy)}). At this
 * time the ordering of the items is known, and indices for the parts can be
 * obtained, as well as knowledge about which parts are already found.
 * <li> Record creation (call of {@link #createRecord(Search)}).
 * At this time the pre-matched images are known. Found images are also known,
 * but are due to change at subsequent finds.
 * <li> Record usage (call of {@link Record#next()}). At this time the found
 * images are known.
 * </ul>
 * @author Arend Rensink
 * @version $Revision $
 */
public interface SearchItem extends Comparable<SearchItem> {
    /**
     * Creates an activation record for this search item, for a given search.
     */
    Record createRecord(Search search);

    /**
     * Returns the collection of nodes that should already be matched before
     * this item should be scheduled.
     */
    Collection<RuleNode> needsNodes();

    /**
     * Returns the collection of nodes for which this search item will find a
     * matching when activated.
     */
    Collection<? extends RuleNode> bindsNodes();

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
    Collection<? extends RuleEdge> bindsEdges();

    /**
     * Indicates if this item tests for nodes without actually binding them.
     * This may affect the dependencies in case of injective matching.
     */
    boolean isTestsNodes();

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
    void activate(PlanSearchStrategy strategy);

    /**
     * Interface for an activation record of a search item.
     * @author Arend Rensink
     * @version $Revision $
     */
    interface Record {
        /** Initialises the record for a given host graph. */
        void initialise(HostGraph host);

        /** Returns the relevance status of the enclosing search item. */
        boolean isRelevant();

        /**
         * Indicates if this search record is known to be successful no more
         * than once in a row. That is, the record is singular if
         * {@link #next()} will return <code>true</code> at most once before
         * the next {@link #reset()}.
         */
        boolean isSingular();

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

    /** The state of a search item record. */
    enum State {
        /** The search starts from scratch. */
        START(false),
        /** The search has failed immediately, and is not yet reset. */
        EMPTY(false),
        /** The (singular) search has succeeded (once); the next time it will fail. */
        FOUND(true),
        /** The (singular) search has succeeded and then failed, and is starting to repeat. */
        FULL(false),
        /** The (multiple) search has yielded some first results, but is not yet completed. */
        PART(true),
        /** The (multiple) search has just started repeating after having yielded some results. */
        PART_START(false),
        /** The (multiple) search is repeating the previously found (partial) results. */
        PART_REPEAT(true),
        /** The (multiple) search has been concluded after yielding at least one result. */
        FULL_START(false),
        /** The (multiple) search is repeating the previously found (complete) results. */
        FULL_REPEAT(true);

        private State(boolean written) {
            this.written = written;
        }

        /** Returns the possible next states after a {@link Record#next()} invocation. */
        public final Set<State> getNext() {
            switch (this) {
            case EMPTY:
                return EnumSet.of(EMPTY);
            case FOUND:
                return EnumSet.of(FULL);
            case FULL:
                return EnumSet.of(FOUND);
            case FULL_REPEAT:
                return EnumSet.of(FULL_REPEAT, FULL_START);
            case FULL_START:
                return EnumSet.of(FULL_REPEAT);
            case PART:
                return EnumSet.of(PART, FULL_START);
            case PART_REPEAT:
                return EnumSet.of(PART_REPEAT, PART);
            case PART_START:
                return EnumSet.of(PART_REPEAT, PART);
            case START:
                return EnumSet.of(EMPTY, FOUND, PART);
            default:
                assert false;
                return null;
            }
        }

        /** Returns the next state after a {@link Record#repeat()} invocation. */
        public final State getRepeat() {
            switch (this) {
            case EMPTY:
                return EMPTY;
            case FOUND:
                return FULL;
            case FULL:
                return FULL;
            case FULL_REPEAT:
                return FULL_START;
            case FULL_START:
                return FULL_START;
            case PART:
                return PART_START;
            case PART_REPEAT:
                return PART_START;
            case PART_START:
                return PART_START;
            case START:
                return START;
            default:
                assert false;
                return null;
            }
        }

        /** Returns the next state after a {@link Record#reset()} invocation. */
        public final State getReset() {
            return START;
        }

        /** Indicates if in this state a value has been written to the search result. */
        public final boolean isWritten() {
            return this.written;
        }

        private final boolean written;
    }
}
