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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
 * @author Arend Rensink and Eduardo Zambon
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

    /**
     * Search item record offering basic functionality for querying the
     * underlying search and target.
     */
    abstract class BasicRecord implements Record {

        /** The underlying search for this record. */
        final Search search;
        /** The host graph of this record.*/
        PatternGraph host;

        /** Constructs a record for a given search. */
        BasicRecord(Search search) {
            this.search = search;
        }

        @Override
        public void initialise(PatternGraph host) {
            reset();
            this.host = host;
        }

        @Override
        final public boolean isRelevant() {
            return SearchItem.this.isRelevant();
        }
    }

    /**
     * Record type for a search item known to yield at most one solution.
     * @author Arend Rensink and Eduardo Zambon
     * @version $Revision: 3291 $
     */
    abstract class SingularRecord extends BasicRecord {

        /** The state of the search record. */
        private State state;

        /** Constructs an instance for a given search. */
        SingularRecord(Search search) {
            super(search);
            this.state = State.START;
        }

        /** Always returns <code>true</code>. */
        @Override
        public final boolean isSingular() {
            return true;
        }

        @Override
        public final boolean isEmpty() {
            return this.state == State.EMPTY;
        }

        public final boolean next() {
            State nextState = null;
            switch (this.state) {
            case START:
                nextState = find() ? State.FOUND : State.EMPTY;
                break;
            case FOUND:
                erase();
                nextState = State.FULL;
                break;
            case EMPTY:
                // the state is unchanged
                nextState = State.EMPTY;
                break;
            case FULL:
                boolean result = write();
                assert result;
                nextState = State.FOUND;
                break;
            default:
                assert false;
            }
            assert this.state.getNext().contains(nextState) : String.format(
                "Illegal transition %s -next-> %s", this.state, nextState);
            this.state = nextState;
            return nextState.isWritten();
        }

        @Override
        public final void repeat() {
            if (this.state.isWritten()) {
                erase();
            }
            this.state = this.state.getRepeat();
        }

        public final void reset() {
            if (this.state.isWritten()) {
                erase();
            }
            this.state = this.state.getReset();
        }

        /**
         * Tries to find the unique solution and write it to the target map.
         * This encapsulates {@link #write()}.
         * @return <code>true</code> if finding and writing the solution was successful.
         */
        abstract boolean find();

        /**
         * Tries to write the previously found unique solution to the target map.
         * @return <code>true</code> if setting the solution was successful.
         */
        abstract boolean write();

        /**
         * Erases the currently set solution from the target map.
         */
        abstract void erase();

        @Override
        public String toString() {
            return String.format("%s: %b", SearchItem.this.toString(),
                this.state.isWritten());
        }

    }

    /**
     * Abstract implementation of a search item record expected to have more
     * than one solution.
     * @author Arend Rensink and Eduardo Zambon
     */
    abstract class MultipleRecord<E> extends BasicRecord {

        /** An iterator over the images for the item. */
        Iterator<? extends E> imageIter;
        /** The previously found images. */
        List<E> oldImages;
        /** Index in {@link #oldImages} recording the state of the repetition */
        int oldImageIndex;
        /** The state of this search item. */
        private State state;

        /** Constructs a record for a given search. */
        MultipleRecord(Search search) {
            super(search);
            this.oldImages = new ArrayList<E>();
            this.oldImageIndex = 0;
            this.state = State.START;
        }

        /** This implementation returns <code>false</code>. */
        @Override
        public final boolean isSingular() {
            return false;
        }

        @Override
        public void initialise(PatternGraph host) {
            reset();
            this.host = host;
        }

        @Override
        public final boolean isEmpty() {
            return this.state == State.EMPTY;
        }

        /**
         * If {@link #imageIter} is not initialised, first invokes
         * {@link #init()}. Then iterates over the images of {@link #imageIter}
         * until one is found for which {@link #write(Object)} is satisfied.
         * Calls {@link #reset()} if no such image is found.
         */
        public final boolean next() {
            State nextState;
            switch (this.state) {
            case EMPTY:
                nextState = State.EMPTY;
                break;
            case FULL_REPEAT:
                int index = this.oldImageIndex;
                if (index == this.oldImages.size()) {
                    erase();
                    nextState = State.FULL_START;
                } else {
                    write(this.oldImages.get(index));
                    this.oldImageIndex = index + 1;
                    nextState = State.FULL_REPEAT;
                }
                break;
            case FULL_START:
                write(this.oldImages.get(0));
                this.oldImageIndex = 1;
                nextState = State.FULL_REPEAT;
                break;
            case PART:
                E image = find();
                if (image == null) {
                    erase();
                    nextState = State.FULL_START;
                } else {
                    this.oldImages.add(image);
                    nextState = State.PART;
                }
                break;
            case PART_REPEAT:
                index = this.oldImageIndex;
                write(this.oldImages.get(index));
                if (index == this.oldImages.size() - 1) {
                    nextState = State.PART;
                } else {
                    this.oldImageIndex = index + 1;
                    nextState = State.PART_REPEAT;
                }
                break;
            case PART_START:
                write(this.oldImages.get(0));
                if (this.oldImages.size() == 1) {
                    nextState = State.PART;
                } else {
                    this.oldImageIndex = 1;
                    nextState = State.PART_REPEAT;
                }
                break;
            case START:
                image = find();
                if (image == null) {
                    nextState = State.EMPTY;
                } else {
                    this.oldImages.clear();
                    this.oldImages.add(image);
                    nextState = State.PART;
                }
                break;
            default:
                assert false;
                nextState = null;
            }
            assert this.state.getNext().contains(nextState) : String.format(
                "Illegal transition %s -next-> %s", this.state, nextState);
            this.state = nextState;
            return nextState.isWritten();
        }

        @Override
        public final void repeat() {
            if (this.state.isWritten()) {
                erase();
            }
            this.state = this.state.getRepeat();
        }

        @Override
        public final void reset() {
            if (this.state.isWritten()) {
                erase();
            }
            this.imageIter = null;
            this.state = this.state.getReset();
        }

        /**
         * Callback method from {@link #next()} to find and install an image. 
         * The return value is the image found, or {@code null} if the find
         * has not succeeded
         */
        final E find() {
            E result = null;
            if (this.imageIter == null) {
                init();
            }
            while (result == null && this.imageIter.hasNext()) {
                result = this.imageIter.next();
                if (!write(result)) {
                    result = null;
                }
            }
            return result;
        }

        /**
         * Callback method from {@link #next()} to initialise the variables
         * necessary for searching; in any case {@link #imageIter}.
         */
        abstract void init();

        /**
         * Callback method from {@link #next()} to install an image. This method
         * is expected to call other methods of the underlying search to store
         * images of nodes and edges. The return value indicates if this has
         * been successful.
         */
        abstract boolean write(E image);

        /** Erases the last image written in the target map. */
        abstract void erase();

    }

}
