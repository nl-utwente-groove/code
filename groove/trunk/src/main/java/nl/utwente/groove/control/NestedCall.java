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
package nl.utwente.groove.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.Action;
import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.util.LazyFactory;

/**
 * Stack of nested calls.
 * The outer element is the original call; the inner element is the eventual
 * rule call.
 * All but the inner element are procedure calls; all but the outer element
 * are initial calls of the bodies of the procedure of the next level down.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class NestedCall implements Iterable<Call> {
    /** Constructs in initially empty nested call. */
    public NestedCall() {
        // empty
    }

    /**
     * Constructs a nested call from a given stream of calls.
     */
    public NestedCall(Stream<Call> calls) {
        calls.forEach(this.calls::add);
    }

    private final List<Call> calls = new ArrayList<>();

    /** Returns the stream of calls in this nested call, from outer to inner. */
    public Stream<Call> stream() {
        return this.calls.stream();
    }

    /** Returns an iterator over the calls in this nested call, from outer to inner. */
    @Override
    public Iterator<Call> iterator() {
        return this.calls.iterator();
    }

    /** Pushes a new inner call onto this nested call. */
    public void push(Call call) {
        this.calls.add(call);
        this.recipe.reset();
    }

    /** Removes and returns the inner element of this nested call. */
    public synchronized Call pop() {
        var result = this.calls.remove(this.calls.size());
        this.recipe.reset();
        return result;
    }

    /** Returns the depth of this nested call. */
    public int depth() {
        return this.calls.size();
    }

    /** Returns the outer call of this nested call. */
    public Call getOuter() {
        return this.calls.get(0);
    }

    /** Returns the inner call of this nested call. */
    public Call getInner() {
        return this.calls.get(this.calls.size() - 1);
    }

    /** Returns the rule invoked in the inner call. */
    public Rule getRule() {
        return getInner().getRule();
    }

    /** Indicates if this nested call represents a recipe step.
     * @return {@code true} if and only if {@link #getRecipe()} is not empty.
     * @see #getRecipe()
     */
    public boolean inRecipe() {
        return getRecipe().isPresent();
    }

    /**
     * Returns the outermost recipe of the nested call, if any.
     * @see #inRecipe()
     */
    public Optional<Recipe> getRecipe() {
        return this.recipe.get();
    }

    /** The first recipe in this nested call, or {@code null} if there is none. */
    private LazyFactory<Optional<Recipe>> recipe = LazyFactory
        .instance(() -> stream()
            .map(c -> c.getUnit())
            .filter(u -> u instanceof Recipe)
            .findFirst()
            .map(u -> (Recipe) u));

    /**
     * Returns the top-level visible action in this nested call.
     * This is either the recipe if there is one, or the top-level rule.
     */
    public Action getAction() {
        return getRecipe().map(r -> (Action) r).orElse(getRule());
    }

    @Override
    public String toString() {
        return toString(false);
    }

    /** Returns the concatenated names of all calls in this nested call,
     * separated by '/'.
     * @param allPars if {@code true}, parentheses are always inserted;
     * otherwise, they are only inserted for parameterised calls.
     */
    public String toString(boolean allPars) {
        StringBuilder result = new StringBuilder();
        for (Call call : this) {
            if (result.length() > 0) {
                result.append('/');
            }
            if (allPars || !call.getArgs().isEmpty()) {
                result.append(call.toString());
            } else {
                result.append(call.getUnit().getQualName());
            }
        }
        return result.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.calls);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NestedCall other = (NestedCall) obj;
        return Objects.equals(this.calls, other.calls);
    }
}
