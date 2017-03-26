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
import java.util.function.Function;

import groove.lts.GraphState;

/**
 * Frontier that stops at points that exceed a given size.
 * Optionally the size is increased once the space up to the given size has been exhausted.
 * @author Arend Rensink
 * @version $Revision $
 */
public class BoundedFrontier extends ExploreFrontier {

    /**
     * @param inner the inner exploration frontier
     * @param sizeFunction size function for explore points
     * @param bound maximum size of explore points to be considered
     * @param increment bound increment once everything below bound is exhausted
     */
    public BoundedFrontier(ExploreFrontier inner, Function<GraphState,Integer> sizeFunction,
        int bound, int increment) {
        super(inner.getMaxSize());
        this.sizeFunction = sizeFunction;
        this.bound = bound;
        this.increment = increment;
        this.inner = inner;
        this.rest = new ArrayList<>();
    }

    /** Size function determining whether explore point is within bound. */
    private final Function<GraphState,Integer> sizeFunction;
    /** Current size bound. */
    private int bound;
    /** Non-negative increment; if 0, there is no increment. */
    private final int increment;

    @Override
    public ExplorePoint next() {
        ExplorePoint result = this.inner.next();
        if (this.inner.isEmpty() && !this.rest.isEmpty()) {
            incrementBound();
        }
        return result;
    }

    @Override
    public int size() {
        return this.inner.size() + this.rest.size();
    }

    @Override
    protected void addToFrontier(ExplorePoint point) {
        if (this.sizeFunction.apply(point.getState()) <= this.bound) {
            this.inner.addToFrontier(point);
        } else if (this.increment > 0) {
            this.rest.add(point);
        }
    }

    @Override
    public void removeLast() {
        this.inner.removeLast();
        if (this.inner.isEmpty() && !this.rest.isEmpty()) {
            incrementBound();
        }
    }

    private void incrementBound() {
        this.bound += this.increment;
        List<ExplorePoint> rest = this.rest;
        this.rest = new ArrayList<>();
        for (ExplorePoint p : rest) {
            addToFrontier(p);
        }
    }

    private final ExploreFrontier inner;
    private List<ExplorePoint> rest;
}
