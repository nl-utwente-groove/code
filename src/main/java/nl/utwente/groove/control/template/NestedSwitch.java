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
package nl.utwente.groove.control.template;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.Attempt;
import nl.utwente.groove.control.Call;
import nl.utwente.groove.control.NestedCall;
import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.util.Groove;

/**
 * Stack of switches, corresponding to nested procedure and rule calls.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class NestedSwitch implements Attempt.Stage<Location,NestedSwitch>, Comparable<NestedSwitch>,
    Relocatable, Iterable<Switch> {
    /** Constructs a copy of a nested switch. */
    public NestedSwitch(NestedSwitch other) {
        other.forEach(this::push);
    }

    /** Constructs an initially empty switch. */
    public NestedSwitch() {
        // empty
    }

    /** The stack of switches. All of them, except possibly the top, are procedure calls. */
    private final Deque<Switch> switches = new ArrayDeque<>();

    /** Pushes a new inner switch onto this nested switch. */
    public void push(Switch swt) {
        this.switches.push(swt);
        if (this.nestedCall != null) {
            this.nestedCall.push(swt.getCall());
        }
        if (this.transience >= 0) {
            this.transience += swt.getTransience();
        }
    }

    /** Pops the inner switch from this nested switch and returns it. */
    public synchronized Switch pop() {
        Switch result = this.switches.pop();
        if (this.nestedCall != null) {
            this.nestedCall.pop();
        }
        if (this.transience >= 0) {
            this.transience -= result.getTransience();
        }
        return result;
    }

    /** Tests if this nested switch is empty. */
    public boolean isEmpty() {
        return size() == 0;
    }

    /** Returns the depth of this nested switch. */
    public int size() {
        return this.switches.size();
    }

    /** Returns a stream over the switches in this nested switch, from outer to inner. */
    public Stream<Switch> stream() {
        Iterable<Switch> iter = this.switches::descendingIterator;
        return StreamSupport.stream(iter.spliterator(), false);
    }

    /** Returns a stream over the switches in this nested switch, from inner to outer. */
    public Stream<Switch> outStream() {
        return this.switches.stream();
    }

    /** Returns an iterator over the switches in this nested switch, from outer to inner. */
    @Override
    public Iterator<Switch> iterator() {
        return this.switches.descendingIterator();
    }

    /** Returns an iterator over the switches in this nested switch, from inner to outer. */
    public Iterator<Switch> outIterator() {
        return this.switches.iterator();
    }

    /** Returns an iterable over the switches in this nested switch, from inner to outer. */
    public Iterable<Switch> outIterable() {
        return () -> outIterator();
    }

    /** Returns the outermost (initial) switch of this nested switch. */
    public Switch getOuter() {
        return this.switches.getLast();
    }

    /** Returns the outermost (initial) call of this nested switch. */
    public Call getOutermostCall() {
        return getOuter().getCall();
    }

    /** Returns the innermost switch of this nested switch. */
    public Switch getInnermost() {
        return this.switches.peek();
    }

    @Override
    public Call getInnermostCall() {
        return getInnermost().getCall();
    }

    @Override
    public Location onFinish() {
        return getOuter().onFinish();
    }

    @Override
    public synchronized int getTransience() {
        if (this.transience < 0) {
            this.transience = computeTransience();
        }
        return this.transience;
    }

    private int computeTransience() {
        int result = 0;
        for (Switch swit : this.switches) {
            this.transience += swit.getTransience();
        }
        return result;
    }

    private int transience = -1;

    /** Returns the nested call corresponding to this nested switch. */
    @Override
    public synchronized NestedCall getCall() {
        var result = this.nestedCall;
        if (result == null) {
            var callStream = stream().map(s -> s.getCall());
            this.nestedCall = result = new NestedCall(callStream);
        }
        return result;
    }

    private @Nullable NestedCall nestedCall;

    /** Indicates if this switch is part of a recipe execution. */
    public boolean inRecipe() {
        return getCall().inRecipe();
    }

    /** Returns the recipe in which this is a switch, if any. */
    public Optional<Recipe> getRecipe() {
        return getCall().getRecipe();
    }

    @Override
    public NestedSwitch relocate(Relocation map) {
        NestedSwitch result = new NestedSwitch();
        stream().map(s -> s.relocate(map)).forEach(result::push);
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NestedSwitch other)) {
            return false;
        }
        // note: ArrayDeque doesn't do content-based equals
        return Groove.equals(this.switches, other.switches);
    }

    @Override
    public int hashCode() {
        // note: ArrayDeque doesn't do content-based hashing
        return Groove.hashCode(this.switches);
    }

    @Override
    public String toString() {
        return this.switches.toString();
    }

    @Override
    public int compareTo(NestedSwitch o) {
        int result = size() - o.size();
        var iter = iterator();
        var oIter = o.iterator();
        while (result == 0 && iter.hasNext()) {
            result = iter.next().compareTo(oIter.next());
        }
        return result;
    }
}
