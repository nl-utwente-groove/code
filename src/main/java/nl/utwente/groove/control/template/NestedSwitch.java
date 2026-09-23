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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.Attempt;
import nl.utwente.groove.control.Call;
import nl.utwente.groove.control.NestedCall;
import nl.utwente.groove.grammar.Callable.Kind;
import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.util.AIGenerated;

/**
 * Immutable stack of switches, corresponding to nested procedure and rule calls.
 * Instances are created through a {@link Builder}; the derived data
 * (nested call, transience) are computed once, at build time.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class NestedSwitch implements Attempt.Stage<Location,NestedSwitch>, Comparable<NestedSwitch>,
    Relocatable, Iterable<Switch> {
    /** Constructs an empty nested switch. */
    public NestedSwitch() {
        this(List.of());
    }

    /** Constructs a nested switch from an unmodifiable list of switches, from outer to inner. */
    private NestedSwitch(List<Switch> switches) {
        this.switches = switches;
        this.call = new NestedCall(switches.stream().map(Switch::getCall).toList());
        int transience = 0;
        for (Switch swit : switches) {
            transience += swit.getTransience() + (swit.getUnit().getKind() == Kind.RECIPE
                ? 1
                : 0);
        }
        this.transience = transience;
    }

    /** The stack of switches, from outer to inner. All of them, except possibly the inner, are procedure calls. */
    private final List<Switch> switches;

    /** Returns a builder initialised with the switches of this nested switch. */
    public Builder toBuilder() {
        return new Builder(this.switches);
    }

    /** Tests if this nested switch is empty. */
    public boolean isEmpty() {
        return this.switches.isEmpty();
    }

    /** Returns the depth of this nested switch. */
    public int size() {
        return this.switches.size();
    }

    /** Returns a stream over the switches in this nested switch, from outer to inner. */
    public Stream<Switch> stream() {
        return this.switches.stream();
    }

    /** Returns a stream over the switches in this nested switch, from inner to outer. */
    public Stream<Switch> outStream() {
        return this.switches.reversed().stream();
    }

    /** Returns an iterator over the switches in this nested switch, from outer to inner. */
    @Override
    public Iterator<Switch> iterator() {
        return this.switches.iterator();
    }

    /** Returns an iterator over the switches in this nested switch, from inner to outer. */
    public Iterator<Switch> outIterator() {
        return this.switches.reversed().iterator();
    }

    /** Returns an iterable over the switches in this nested switch, from inner to outer. */
    public Iterable<Switch> outIterable() {
        return this.switches.reversed();
    }

    /** Returns the outermost (initial) switch of this nested switch. */
    public Switch getOuter() {
        return this.switches.get(0);
    }

    /** Returns the outermost (initial) call of this nested switch. */
    public Call getOutermostCall() {
        return getOuter().getCall();
    }

    /** Returns the innermost switch of this nested switch. */
    public Switch getInnermost() {
        assert !isEmpty(); // a nested switch is never empty
        return this.switches.get(this.switches.size() - 1);
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
    public int getTransience() {
        return this.transience;
    }

    /** Transient depth entered by this switch. */
    private final int transience;

    /** Returns the nested call corresponding to this nested switch. */
    @Override
    public NestedCall getCall() {
        return this.call;
    }

    /** The nested call corresponding to this nested switch. */
    private final NestedCall call;

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
        var result = new Builder();
        stream().map(s -> s.relocate(map)).forEach(result::push);
        return result.build();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NestedSwitch other)) {
            return false;
        }
        return this.switches.equals(other.switches);
    }

    @Override
    public int hashCode() {
        // computed from inner to outer, as the former deque-based version did
        int result = 1;
        for (var sw : this.switches.reversed()) {
            result = 31 * result + sw.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        // listed from inner to outer, as the former deque-based version did
        return this.switches.reversed().toString();
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

    /**
     * Builder for a {@link NestedSwitch}: a mutable stack of switches
     * from which nested switches can be built.
     * The builder stays usable after {@link #build()}, and the result
     * shares no mutable state with it.
     */
    @AIGenerated("Claude Fable 5.1, 2026-09")
    public static class Builder {
        /** Constructs an initially empty builder. */
        public Builder() {
            // empty
        }

        /** Constructs a builder initialised with a given list of switches, from outer to inner. */
        private Builder(List<Switch> switches) {
            this.switches.addAll(switches);
        }

        /** The stack of switches, from outer to inner. */
        private final List<Switch> switches = new ArrayList<>();

        /** Pushes a new inner switch onto this builder. */
        public Builder push(Switch swt) {
            this.switches.add(swt);
            return this;
        }

        /** Pops the inner switch from this builder and returns it. */
        public Switch pop() {
            return this.switches.remove(this.switches.size() - 1);
        }

        /** Tests if this builder currently holds no switches. */
        public boolean isEmpty() {
            return this.switches.isEmpty();
        }

        /** Builds the nested switch consisting of the switches currently in this builder. */
        public NestedSwitch build() {
            return new NestedSwitch(List.copyOf(this.switches));
        }
    }
}
