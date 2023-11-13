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

import java.util.Optional;
import java.util.Stack;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.grammar.Action;
import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.util.LazyFactory;

/**
 * Stack of calls.
 * The bottom element is the original call; the top element is the eventual
 * rule call.
 * All but the top element are procedure calls; all but the bottom element
 * are initial calls of the bodies of the procedure of the next level down.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class CallStack extends Stack<Call> {
    /**
     * Constructs an initially empty stack.
     */
    public CallStack(Stream<Call> calls) {
        calls.forEach(c -> this.add(c));
    }

    /** Returns the rule invoked in the top element of the call stack. */
    public Rule getRule() {
        return peek().getRule();
    }

    /** Indicates if this call stack represents a recipe step.
     * @return {@code true} if and only if {@link #getRecipe()} is non-{@code null}
     * @see #getRecipe()
     */
    public boolean inRecipe() {
        return getRecipe().isPresent();
    }

    /**
     * Returns the outermost recipe of the call stack, if any.
     * @see #inRecipe()
     */
    public Optional<Recipe> getRecipe() {
        return this.recipe.get();
    }

    /** The first recipe in the call stack, or {@code null} if there is none. */
    private Supplier<Optional<Recipe>> recipe = LazyFactory
        .instance(() -> stream()
            .map(c -> c.getUnit())
            .filter(u -> u instanceof Recipe)
            .findFirst()
            .map(u -> (Recipe) u));

    /**
     * Returns the top-level action in this call stack.
     * This is either the recipe if there is one, or the top-level rule.
     */
    public Action getAction() {
        return getRecipe().map(r -> (Action) r).orElse(getRule());
    }

    @Override
    public String toString() {
        return toString(false);
    }

    /** Returns the concatenated names of all calls in the stack,
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
}
