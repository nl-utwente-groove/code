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
package groove.explore;

import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * Frontier of incompletely explored states.
 * The ordering of the frontier determines part of the exploration strategy.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract class ExploreFrontier {
    /**
     * Constructs a frontier with a given maximum size.
     */
    protected ExploreFrontier(int maxSize) {
        this.maxSize = maxSize;
    }

    /** Indicates there is a next state to be explored in the frontier. */
    public boolean hasNext() {
        return size() > 0;
    }

    /** Returns and removes the next element from the frontier.
     * @throws NoSuchElementException if the frontier is empty
     */
    abstract public ExplorePoint next();

    /** Checks if there are no more elements in the frontier.
     * @see #size()
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /** Returns the number of remaining elements in the frontier. */
    abstract public int size();

    /** Adds an explore point to the frontier.
     * If the maximum size is thereby exceeded, also removes an element.
     * @see ExploreFrontier#addToFrontier(ExplorePoint)
     * @see #removeLast()
     */
    public void add(ExplorePoint point) {
        addToFrontier(point);
        if (hasMaxSize() && size() > getMaxSize()) {
            removeLast();
        }
    }

    /** Callback method to add a new element to the underlying collection,
     * without maintaining the maximum size.
     * @see #add(ExplorePoint)
     */
    abstract protected void addToFrontier(ExplorePoint point);

    /** Removes the last element of the frontier. */
    abstract public void removeLast();

    /**
     * Adds a collection of explore points,
     * taking the frontier size into account.
     * @param points the explore points to be added
     */
    public void addAll(Collection<ExplorePoint> points) {
        for (ExplorePoint p : points) {
            add(p);
        }
    }

    /** Indicates if this frontier has a maximum size. */
    public boolean hasMaxSize() {
        return this.maxSize != 0;
    }

    /**
     * Returns the maximum size of this frontier, if any.
     * @return the maximum frontier size, or {@code 0} if there is no maximum
     */
    public int getMaxSize() {
        return this.maxSize;
    }

    private final int maxSize;
}
