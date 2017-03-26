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

import java.util.ArrayList;
import java.util.List;

/**
 * Set of explore points, collected as successors of a given explore point.
 * @author Arend Rensink
 * @version $Revision $
 */
class ExplorePointSet {
    ExplorePointSet(boolean singleton) {
        this.singleton = singleton;
        this.points = new ArrayList<>();
    }

    private final boolean singleton;

    /** Indicates if this point set is bounded to a singleton. */
    private boolean isSingleton() {
        return this.singleton;
    }

    /** Clears the current set of exploration points, enabling reuse of this set. */
    void clear() {
        this.points.clear();
    }

    /** Adds an exploration point to the set.
     * This should only be invoked if the set is not already full, according to {@link #isFull()}.
     * @param point the point to be added; non-<code>null</code>
     * @throws IllegalStateException if the point set is full prior to invocation
     * @see #isFull()
     */
    void addPoint(ExplorePoint point) {
        if (isFull()) {
            throw new IllegalStateException("Point set is already full: " + this.points);
        }
        this.points.add(point);
    }

    /** Indicates if the point set is full.
     * This may occur if it is a singleton point set, and a point has already been added.
     * @return {@code true} if the point set is full
     */
    boolean isFull() {
        return isSingleton() && this.points.size() == 1;
    }

    /** Returns the current list of exploration points. */
    List<ExplorePoint> getPoints() {
        return this.points;
    }

    private final List<ExplorePoint> points;
}
