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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SequencedCollection;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.util.Randomness;
import nl.utwente.groove.util.Randomness.Purpose;

/**
 * Size-capped frontier pool, realising beam search: when an insertion
 * overflows the capacity, the state that would otherwise be explored
 * <i>last</i> is dropped, so exploration is deliberately incomplete. The
 * exploration order within the beam is a policy parameter (see
 * {@link Order}); the drop rule follows from it uniformly, as the take-order
 * dual of {@link #take()}: under {@link Order#OLDEST} the newest state is
 * dropped (which is the incoming one), under {@link Order#NEWEST} the
 * oldest, and under {@link Order#RANDOM} a uniformly random one (the
 * incoming state included). The randomness is drawn from the
 * {@link Randomness} registry ({@link Purpose#EXPLORATION}), so a fixed
 * master seed makes the exploration reproducible. The pool imposes no depth
 * bound. All pool operations are amortised constant-time.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class BeamPool implements Pool {
    /** Exploration order within the beam. */
    public enum Order {
        /** The oldest state is explored next (breadth-first order). */
        OLDEST,
        /** The newest state is explored next (depth-first order). */
        NEWEST,
        /** A uniformly random state is explored next. */
        RANDOM,;
    }

    /**
     * Creates a beam pool with a given exploration order and capacity.
     * @param order the exploration order within the beam
     * @param capacity the maximum number of states in the pool; must be positive
     */
    public BeamPool(Order order, int capacity) {
        assert capacity > 0;
        this.order = order;
        this.capacity = capacity;
        if (order == Order.RANDOM) {
            this.random = Randomness.newRandom(Purpose.EXPLORATION);
            var states = new ArrayList<GraphState>();
            this.states = states;
            this.randomStates = states;
        } else {
            this.random = null;
            this.states = new ArrayDeque<>();
            this.randomStates = null;
        }
    }

    private final Order order;
    private final int capacity;
    /** Source of the random choices under {@link Order#RANDOM}, seeded per
     * instance (hence per exploration); {@code null} for the other orders. */
    private final @Nullable Random random;

    @Override
    public @Nullable GraphState take() {
        var states = this.states;
        if (states.isEmpty()) {
            return null;
        }
        return this.order == Order.RANDOM
            ? removeRandom(states.size())
            : states.removeFirst();
    }

    @Override
    public void add(GraphState state) {
        var states = this.states;
        // the incoming state is the newest, so it goes to the tail of the
        // take-order under OLDEST and to the head under NEWEST; the
        // position is irrelevant under RANDOM
        if (this.order == Order.NEWEST) {
            states.addFirst(state);
        } else {
            states.addLast(state);
        }
        trim();
    }

    @Override
    public void readd(GraphState state) {
        // put at the head of the take-order, as the state was already
        // scheduled for exploration; under the random order append instead,
        // replicating RandomPool so that an unrestricted beam draws the
        // same states from the same seed; a slot is normally free because
        // the state was just taken, but a state whose transience was
        // resolved between its discovery and its exploration reaches this
        // method without a preceding take, so trim for robustness
        var states = this.states;
        // the re-added state itself is exempt from the drop: it is
        // partially explored, and the contract of readd is that the state
        // reaches exploration again. Under OLDEST and NEWEST it sits at the
        // head while trim drops the tail; under RANDOM it sits at the tail,
        // so the random victim is drawn from the other states only
        if (this.order == Order.RANDOM) {
            states.addLast(state);
            int size = states.size();
            if (size > this.capacity) {
                removeRandom(size - 1);
            }
        } else {
            states.addFirst(state);
            trim();
        }
    }

    @Override
    public void clear() {
        this.states.clear();
    }

    /** Drops the take-order-last state if the capacity is exceeded: the tail
     * of the deque under {@link Order#OLDEST} and {@link Order#NEWEST}, a
     * uniformly random state under {@link Order#RANDOM}. */
    private void trim() {
        var states = this.states;
        int size = states.size();
        if (size > this.capacity) {
            if (this.order == Order.RANDOM) {
                removeRandom(size);
            } else {
                states.removeLast();
            }
        }
    }

    /** Removes and returns a state drawn uniformly at random from the first
     * {@code bound} positions of the list (only available under the random
     * order). The removal swaps with the last element, so it is
     * constant-time; the list order is irrelevant anyway. */
    private GraphState removeRandom(int bound) {
        var states = randomStates();
        int index = nextInt(bound);
        GraphState result = states.get(index);
        int last = states.size() - 1;
        states.set(index, states.get(last));
        states.remove(last);
        return result;
    }

    /** Returns a uniformly random index below a given bound, from the seeded
     * source (only available under {@link Order#RANDOM}). */
    private int nextInt(int bound) {
        var random = this.random;
        assert random != null;
        return random.nextInt(bound);
    }

    /** Returns the list view of the pool contents (only available under
     * {@link Order#RANDOM}). */
    private List<GraphState> randomStates() {
        var result = this.randomStates;
        assert result != null;
        return result;
    }

    /** The pool contents, in take-order under {@link Order#OLDEST} and
     * {@link Order#NEWEST} (the first element is taken next), backed by a
     * deque so that both ends are constant-time; unordered under
     * {@link Order#RANDOM}, backed by a list to support the constant-time
     * random removal of {@link #removeRandom}. At most {@link #capacity}
     * elements outside a call of {@link #add} or {@link #readd}. */
    private final SequencedCollection<GraphState> states;
    /** Alias of {@link #states} under {@link Order#RANDOM}, typed to give
     * the index access that {@link #removeRandom} needs; {@code null} under
     * the other orders. */
    private final @Nullable List<GraphState> randomStates;
}
