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
import java.util.NoSuchElementException;

import groove.explore.config.TraverseKind;

/**
 * Basic frontier for {@link TraverseKind#RANDOM} traversal.
 * The underlying data structure is a stack.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RandomFrontier extends BasicFrontier {
    RandomFrontier(int maxSize) {
        super(TraverseKind.RANDOM, maxSize);
        this.points = new ArrayList<>();
    }

    @Override
    public int size() {
        return this.points.size();
    }

    @Override
    public ExplorePoint next() {
        if (this.points.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.points.remove(this.points.size() - 1);
    }

    @Override
    protected void addToFrontier(ExplorePoint point) {
        int i = (int) ((size() + 1) * Math.random());
        if (i == size()) {
            this.points.add(point);
        } else {
            this.points.add(this.points.get(i));
            this.points.set(i, point);
        }
    }

    @Override
    public void removeLast() {
        // we may as well remove the first elements, as they are in random order anyway
        next();
    }

    private final List<ExplorePoint> points;
}
