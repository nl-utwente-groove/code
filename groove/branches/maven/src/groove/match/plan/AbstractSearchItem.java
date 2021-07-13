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
 * $Id $
 */

package groove.match.plan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import groove.grammar.host.HostGraph;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleNode;
import groove.match.plan.PlanSearchStrategy.Search;
import groove.util.Exceptions;

/**
 * Abstract implementation of a search item, offering some basic search
 * functionality.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract class AbstractSearchItem implements SearchItem {
    /*
     * This implementation returns the empty set.
     */
    @Override
    public Collection<? extends RuleNode> bindsNodes() {
        return Collections.emptySet();
    }

    /*
     * This implementation returns the empty set.
     */
    @Override
    public Collection<LabelVar> bindsVars() {
        return Collections.emptySet();
    }

    /*
     * This implementation returns the empty set.
     */
    @Override
    public Collection<? extends RuleEdge> bindsEdges() {
        return Collections.emptySet();
    }

    /*
     * This implementation returns the empty set.
     */
    @Override
    public Collection<RuleNode> needsNodes() {
        return Collections.emptySet();
    }

    /*
     * The default implementation returns the empty set.
     */
    @Override
    public Collection<LabelVar> needsVars() {
        return Collections.emptySet();
    }

    /* The default implementation returns {@code false}. */
    @Override
    public boolean isTestsNodes() {
        return false;
    }

    /*
     * This implementation compares items on the basis of their class names, and
     * after that, on the basis of their ratings. A lower rating means a
     * "smaller" search item, which is scheduled earlier.
     * If this implementation returns {@code 0}, the type of the parameter
     * is guaranteed to coincide with the type of this item.
     */
    @Override
    public int compareTo(SearchItem other) {
        int result = getClass().getName()
            .compareTo(other.getClass()
                .getName());
        if (result == 0) {
            result = getRating() - getRating(other);
        }
        return result;
    }

    /**
     * Returns the rating of a search item, for the purpose of
     * {@link #compareTo(SearchItem)}. This is obtained by {@link #getRating()}
     * if the item is an {@link AbstractSearchItem}; otherwise, it is derived
     * from the item's class name.
     */
    private int getRating(SearchItem other) {
        if (other instanceof AbstractSearchItem) {
            return ((AbstractSearchItem) other).getRating();
        } else {
            return 0;
        }
    }

    /**
     * Returns a rating for this search item, for the purpose of its natural
     * ordering. An item with higher rating gets scheduled first (failing more
     * urgent criteria).
     */
    abstract int getRating();

    /** Creates a new dummy search record. */
    final Record createDummyRecord() {
        return new DummyRecord();
    }

    /**
     * Changes the relevance status of this search item.
     * @see #isRelevant()
     */
    void setRelevant(boolean relevant) {
        this.relevant = relevant;
    }

    @Override
    final public boolean isRelevant() {
        return this.relevant;
    }

    /**
     * Flag indicating the relevance of this search item.
     * Default value is <code>true</code>
     * @see #isRelevant()
     */
    private boolean relevant = true;

    @Override
    public final int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = computeHashCode();
            if (this.hashCode == 0) {
                this.hashCode++;
            }
        }
        return this.hashCode;
    }

    /** Callback method to compute this search item's hash code. */
    int computeHashCode() {
        return getClass().hashCode();
    }

    private int hashCode;

    /** This implementation is guaranteed to return {@code true}
     * only if the type of the argument equals the type of this object.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }

    /**
     * Dummy search record, which does nothing upon {@link #next()} except
     * alternatingly return <code>true</code> and <code>false</code>.
     */
    final class DummyRecord implements Record {
        @Override
        public void initialise(HostGraph host) {
            this.found = false;
        }

        /**
         * This record alternates between <code>true</code> and
         * <code>false</code>, and resets to <code>true</code> upon
         * invocation of {@link #reset()}.
         */
        @Override
        public boolean next() {
            this.found = !this.found;
            return this.found;
        }

        /** This implementation returns <code>true</code>. */
        @Override
        public boolean isSingular() {
            return true;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean isRelevant() {
            return AbstractSearchItem.this.isRelevant();
        }

        @Override
        public void repeat() {
            reset();
        }

        @Override
        public void reset() {
            this.found = false;
        }

        @Override
        public String toString() {
            return String.format("%s: %s", AbstractSearchItem.this.toString(), this.found);
        }

        /**
         * Flag indicating if the last call of #find returned <code>true</code>,
         * and hence the next should return <code>false</code>.
         */
        private boolean found;
    }

    /**
     * Search item record offering basic functionality for querying the
     * underlying search and target.
     */
    abstract class BasicRecord implements Record {
        /** Constructs a record for a given search. */
        BasicRecord(Search search) {
            this.search = search;
        }

        @Override
        public void initialise(HostGraph host) {
            reset();
            this.host = host;
        }

        @Override
        final public boolean isRelevant() {
            return AbstractSearchItem.this.isRelevant();
        }

        /** The underlying search for this record. */
        HostGraph host;
        final Search search;
    }

    /**
     * Record type for a search item known to yield at most one solution.
     * @author Arend Rensink
     * @version $Revision$
     */
    abstract class SingularRecord extends BasicRecord {
        /** Constructs an instance for a given search. */
        SingularRecord(Search search) {
            super(search);
        }

        /**
         * Always returns <code>true</code>.
         */
        @Override
        final public boolean isSingular() {
            return true;
        }

        @Override
        final public boolean isEmpty() {
            return this.state == State.EMPTY;
        }

        @Override
        final public boolean next() {
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
                throw Exceptions.UNREACHABLE;
            }
            assert this.state.getNext()
                .contains(nextState) : String.format("Illegal transition %s -next-> %s",
                    this.state,
                    nextState);
            this.state = nextState;
            return nextState.isWritten();
        }

        @Override
        final public void repeat() {
            if (this.state.isWritten()) {
                erase();
            }
            this.state = this.state.getRepeat();
        }

        @Override
        final public void reset() {
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
            return String.format("%s: %b",
                AbstractSearchItem.this.toString(),
                this.state.isWritten());
        }

        /** The state of the search record. */
        State state = State.START;
    }

    /**
     * Abstract implementation of a search item record expected to have more
     * than one solution.
     * @author Arend Rensink
     * @version $Revision$
     */
    abstract class MultipleRecord<E> extends BasicRecord {
        /** Constructs a record for a given search. */
        MultipleRecord(Search search) {
            super(search);
        }

        /** This implementation returns <code>false</code>. */
        @Override
        final public boolean isSingular() {
            return false;
        }

        @Override
        final public boolean isEmpty() {
            return this.state == State.EMPTY;
        }

        /**
         * If {@link #imageIter} is not initialised, first invokes
         * {@link #init()}. Then iterates over the images of {@link #imageIter}
         * until one is found for which {@link #write(Object)} is satisfied.
         * Calls {@link #reset()} if no such image is found.
         */
        @Override
        final public boolean next() {
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
                @Nullable E image = find();
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
                throw Exceptions.UNREACHABLE;
            }
            assert this.state.getNext()
                .contains(nextState) : String.format("Illegal transition %s -next-> %s",
                    this.state,
                    nextState);
            this.state = nextState;
            return nextState.isWritten();
        }

        @Override
        final public void repeat() {
            if (this.state.isWritten()) {
                erase();
            }
            this.state = this.state.getRepeat();
        }

        @Override
        final public void reset() {
            if (this.state.isWritten()) {
                erase();
            }
            this.imageIter = null;
            this.state = this.state.getReset();
        }

        /**
         * Callback method from {@link #next()} to initialise the variables
         * necessary for searching; in any case {@link #imageIter}.
         */
        abstract void init();

        /**
         * Callback method from {@link #next()} to find and install an image.
         * The return value is the image found, or {@code null} if the find
         * has not succeeded
         */
        final @Nullable E find() {
            @Nullable E result = null;
            if (this.imageIter == null) {
                init();
            }
            while (result == null && this.imageIter.hasNext()) {
                E next = this.imageIter.next();
                if (write(next)) {
                    result = next;
                }
            }
            return result;
        }

        /**
         * Callback method from {@link #next()} to install an image. This method
         * is expected to call other methods of the underlying search to store
         * images of nodes and edges. The return value indicates if this has
         * been successful.
         */
        abstract boolean write(E image);

        /** Erases the last image written in the target map. */
        abstract void erase();

        /**
         * An iterator over the images for the item's edge.
         */
        Iterator<? extends E> imageIter;

        /**
         * The previously found images.
         */
        List<E> oldImages = new ArrayList<>();
        /** Index in {@link #oldImages} recording the state of the repetition */
        int oldImageIndex = 0;
        /** The state of this search item. */
        private State state = State.START;
    }
}
