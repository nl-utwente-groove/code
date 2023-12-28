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
package nl.utwente.groove.control.instance;

import static nl.utwente.groove.control.instance.CallStackChange.Kind.NONE;
import static nl.utwente.groove.control.instance.CallStackChange.Kind.POP;
import static nl.utwente.groove.control.instance.CallStackChange.Kind.PUSH;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.Assignment;
import nl.utwente.groove.control.Binding;
import nl.utwente.groove.control.Binding.Source;
import nl.utwente.groove.control.CallStack;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.util.Exceptions;

/**
 * Call stack change to be applied as part of a {@link Step}.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public record CallStackChange(Kind kind, List<Assignment> assigns, @Nullable CallStackChange pred) {

    private CallStackChange(Kind kind, Assignment... assigns) {
        this(kind, Arrays.asList(assigns), null);
    }

    /** Returns a new call stack change that first applies the effects of this one
     * and then another change passed in as a parameter.
     * @param next the change to be applied after this one
     */
    public CallStackChange then(CallStackChange next) {
        return kind() == NONE
            ? next
            : next.kind() == NONE
                ? this
                : next.after(this);
    }

    /** Returns a list of singular changes based on this composite change,
     *  in the order in which they are applied. */
    public List<CallStackChange> toList() {
        var pred = pred();
        List<CallStackChange> result = pred == null
            ? new ArrayList<>()
            : pred.toList();
        result.add(new CallStackChange(this.kind(), this.assigns(), null));
        return result;
    }

    /** Returns a new call stack change that first applies the effects of another
     * change passed in as a parameter, and then this one.
     * @param first the change to be applied before this one
     */
    public CallStackChange after(CallStackChange first) {
        var oldPred = pred();
        var newPred = oldPred == null
            ? first
            : oldPred.after(first);
        return new CallStackChange(kind(), assigns(), newPred);
    }

    /** Returns the first assignment in this change. */
    public Assignment assign() {
        return assign(0);
    }

    /** Returns the {@code i}-th assignment in this change. */
    public Assignment assign(int i) {
        return assigns().get(i);
    }

    /**
     * Applies this change to a given call stack
     * and returns the modified stack.
     * Only valid for {@link Kind#POP} and {@link Kind#PUSH} assignments;
     * use {@link #apply(Object[], Function)} with non-{@code null} parameter
     * retrieval function to apply {@link Kind#PUSH}.
     * @return the call stack obtained by applying this change
     */
    public Object[] apply(Object[] stack) {
        assert kind() != Kind.PUSH;
        return apply(stack, null);
    }

    /**
     * Applies this change to a given call
     * stack, and returns the modified stack.
     * @param stack the current call stack
     * @param getPar optional function retrieving the value of {@link Source#CREATOR} and
     * {@link Source#ANCHOR} bindings.
     * @return the call stack obtained by applying this assignment
     */
    public Object[] apply(Object[] stack, @Nullable Function<Binding,HostNode> getPar) {
        // first apply the effects of the predecessor, if any
        var pred = pred();
        if (pred != null) {
            stack = pred.apply(stack, getPar);
        }
        // now apply the effects of this change
        Object[] result;
        switch (kind()) {
        case POP:
            result = CallStack.pop(stack);
            if (!assign().isNone()) {
                HostNode[] newTop = assign().apply(stack, getPar);
                result = CallStack.modify(result, newTop);
            }
            break;
        case PUSH:
            result = stack;
            for (int i = 0; i < assigns().size(); i++) {
                HostNode[] newTop = assign(i).apply(stack, getPar);
                result = i == 0
                    ? CallStack.replace(result, newTop)
                    : CallStack.push(result, newTop);
            }
            break;
        case NONE:
            result = stack;
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.assigns.hashCode();
        result = prime * result + this.kind.hashCode();
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CallStackChange other)) {
            return false;
        }
        if (!this.assigns.equals(other.assigns)) {
            return false;
        }
        if (this.kind != other.kind) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        var pred = pred();
        return (pred == null
            ? ""
            : pred.toString() + "; ") + this.kind.name() + this.assigns;
    }

    /** Returns a new {@link #NONE} change (which does not change the stack. */
    public static CallStackChange none() {
        return new CallStackChange(NONE);
    }

    /** Creates a new {@link #PUSH} action with a given list of assignments. */
    public static CallStackChange push(Assignment... assigs) {
        return new CallStackChange(PUSH, assigs);
    }

    /** Creates a new {@link Kind#POP} action with a given assignment. */
    public static CallStackChange pop(Assignment assign) {
        return new CallStackChange(POP, assign);
    }

    /** Kind of {@link CallStackChange}. */
    public static enum Kind {
        /** Create and initialise a frame instance. */
        PUSH,
        /** Pop a frame instance. */
        POP,
        /** Do nothing. */
        NONE,;
    }
}
