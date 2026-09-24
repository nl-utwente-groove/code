/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
 */
package nl.utwente.groove.grammar.host;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Equator;
import nl.utwente.groove.util.collect.ForkableHashSet;

/**
 * Set of host edges that can be forked in time proportional to the square root of its
 * size, see {@link ForkableHashSet}. Like {@link HostEdgeSet}, it identifies edges by
 * their number, so that parallel edges (content-equal copies with distinct numbers) are
 * distinct elements, and equality never calls {@link Object#equals(Object)}.
 * This is the global edge set of graphs that get a copy of their parent's data
 * (copy mode); its iteration order is bucket order, a function of the edge numbers and
 * of the history of the set.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public final class ForkableHostEdgeSet extends ForkableHashSet<HostEdge> {
    /** Constructs an empty set. */
    public ForkableHostEdgeSet() {
        super(NumberEquator.INSTANCE);
    }

    /** Forks a given set, which afterwards evolves independently of it. */
    public ForkableHostEdgeSet(ForkableHostEdgeSet original) {
        super(original);
    }

    /** Equator identifying host edges by their number, as {@link HostEdgeTreeHashSet} does. */
    @NonNullByDefault({})
    private static final class NumberEquator implements Equator<HostEdge> {
        @Override
        public int getCode(HostEdge key) {
            return key.getNumber();
        }

        @Override
        public boolean areEqual(HostEdge newKey, HostEdge oldKey) {
            assert newKey.getNumber() != oldKey.getNumber() || newKey.equals(oldKey);
            return newKey.getNumber() == oldKey.getNumber();
        }

        @Override
        public boolean allEqual() {
            return true;
        }

        static final NumberEquator INSTANCE = new NumberEquator();
    }
}
