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
 *
 * $Id$
 */
package nl.utwente.groove.explore.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.util.Randomness;
import nl.utwente.groove.util.Randomness.Purpose;

/**
 * Frontier pool that yields a uniformly random element on every take.
 * The randomness is drawn from the {@link Randomness} registry
 * ({@link Purpose#EXPLORATION}), so a fixed master seed makes the
 * exploration order reproducible. The pool imposes no depth bound.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class RandomPool implements Pool {
    /** The pool contents; the order is irrelevant except for reproducibility. */
    private final List<GraphState> states = new ArrayList<>();
    /** Source of the random choices, seeded per instance (hence per exploration). */
    private final Random random = Randomness.newRandom(Purpose.EXPLORATION);

    @Override
    public @Nullable GraphState take() {
        var states = this.states;
        int size = states.size();
        if (size == 0) {
            return null;
        }
        // swap the chosen element with the last one, so removal is O(1)
        int index = this.random.nextInt(size);
        GraphState result = states.get(index);
        states.set(index, states.get(size - 1));
        states.remove(size - 1);
        return result;
    }

    @Override
    public void add(GraphState state) {
        this.states.add(state);
    }

    @Override
    public void readd(GraphState state) {
        this.states.add(state);
    }

    @Override
    public void clear() {
        this.states.clear();
    }
}
