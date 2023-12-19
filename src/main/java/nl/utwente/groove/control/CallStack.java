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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.template.Location;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.graph.Node;

/**
 * Class wrapping the functionality to deal with control valuations.
 * The choice of providing the functionality like this is driven
 * by the desire to keep the valuations in the form of node arrays,
 * for the sake of keeping a low memory footprint per state.
 *
 * A control valuation represents a call stack. All but possibly the last elements
 * form the top level stack frame consisting of {@code HostNode}s; the last element
 * is either also a {@code HostNode} or contains the rest of the stack,
 * corresponding to the caller.
 *
 * The values in each stack level are in the order of the variables in a {@link Location}.
 *
 * @author Arend Rensink
 * @version $Revision$
 */
public class CallStack {
    /** Returns the value at a given position at the top level of a call stack. */
    static public @Nullable HostNode get(Object[] stack, int index) {
        return (HostNode) stack[index];
    }

    /** Pushes a new top level onto an existing (nested) call stack. */
    public static Object[] push(Object[] stack, Object[] top) {
        assert !isNested(top);
        Object[] result = new Object[top.length + 1];
        System.arraycopy(top, 0, result, 0, top.length);
        result[top.length] = stack;
        return result;
    }

    /**
     * Replaces the current top-level values of a given call stack by the non-{@code null}
     * elements of a new top level, and returns the resulting call stack.
     * <i>Note</i>: this is an in-place modification, and the returned call stack
     * is an alias of the original stack.
     */
    public static Object[] modify(Object[] stack, Object[] top) {
        for (int i = 0; i < top.length; i++) {
            var val = top[i];
            if (val != null) {
                stack[i] = val;
            }
        }
        return stack;
    }

    /**
     * Returns a new call stack in which the top level of a given
     * call stack is replaced by new values,
     * while leaving the nesting intact.
     */
    public static Object[] replace(Object[] stack, Object[] top) {
        return isNested(stack)
            ? push(pop(stack), top)
            : top;
    }

    /**
     * Pops the top level off a nested call stack, and returns the parent level.
     * Only valid if the valuation is actually nested.
     */
    public static Object[] pop(Object[] stack) {
        return isNested(stack)
            ? (Object[]) stack[stack.length - 1]
            : null;
    }

    /** Applies a node mapping recursively throughout a given call stack, and
     * returns the resulting stack.
     */
    static public Object[] map(Object[] stack, Function<HostNode,HostNode> map) {
        Object[] result = new Object[stack.length];
        boolean changed = false;
        boolean nested = isNested(stack);
        var size = nested
            ? stack.length - 1
            : stack.length;
        for (int i = 0; i < size; i++) {
            var oldVal = stack[i];
            var newVal = map.apply((HostNode) stack[i]);
            changed |= newVal != oldVal;
            result[i] = newVal;
        }
        if (nested) {
            var newStack = map((Object[]) stack[size], map);
            changed |= newStack != stack;
            result[size] = newStack;
        }
        return changed
            ? result
            : stack;
    }

    /** Tests if two call stacks have equal content. */
    static public boolean areEqual(Object[] stack1, Object[] stack2) {
        return areEqual(stack1, stack2, null);
    }

    /** Tests if two call stacks have equal content, under a node map
     * from the images of the first valuation to those of the second.
     * The node map may be empty, in which case it is regarded as the identity.
     */
    static public boolean areEqual(Object[] stack1, Object[] stack2,
                                   Map<? extends Node,? extends Node> nodeMap) {
        if (nodeMap == null && stack1 == stack2) {
            return true;
        }
        if (stack1.length != stack2.length) {
            return false;
        }
        boolean isNested = isNested(stack1);
        if (isNested != isNested(stack2)) {
            return false;
        }
        int count = isNested
            ? stack1.length - 1
            : stack1.length;
        for (int i = 0; i < count; i++) {
            Object image = nodeMap == null
                ? stack1[i]
                : nodeMap.get(stack1[i]);
            if (image == null) {
                if (stack2[i] != null) {
                    return false;
                }
            } else if (!image.equals(stack2[i])) {
                return false;
            }
        }
        if (isNested && !areEqual(pop(stack1), pop(stack2))) {
            return false;
        }
        return true;
    }

    /** Computes the hash code of a call stack. */
    static public int hashCode(Object[] stack) {
        return hashCode(stack, null);
    }

    /**
     * Computes the hash code of a call stack, given a modifier map
     * from host nodes to representative objects from which the hash code is to be taken.
     * The modifier may be {@code null}, in which case only the length of the
     * valuation is used.
     */
    static public int hashCode(Object[] stack, Map<? extends Element,?> modifier) {
        int prime = 31;
        int result = 1;
        boolean isNested = isNested(stack);
        int count = isNested
            ? stack.length - 1
            : stack.length;
        for (int i = 0; i < count; i++) {
            Object repr = stack[i] == null
                ? null
                : modifier == null
                    ? stack[i]
                    : modifier.get(stack[i]);
            int code = repr == null
                ? 0
                : repr.hashCode();
            result = result * prime + code;
        }
        if (isNested) {
            result = result * prime + hashCode(pop(stack), modifier);
        }
        return result;
    }

    /** Turns a given call stack into a nested list of objects. */
    static public List<Object> asList(Object[] stack) {
        List<Object> result = new ArrayList<>(Arrays.asList(stack));
        if (isNested(stack)) {
            result.set(stack.length - 1, asList((Object[]) stack[stack.length - 1]));
        }
        return result;
    }

    /** Returns a string representation of a given call stack. */
    static public String toString(Object[] stack) {
        return asList(stack).toString();
    }

    /** Tests if the last element of a call stack is another call stack. */
    static private boolean isNested(Object[] stack) {
        return stack.length > 0 && stack[stack.length - 1] instanceof Object[];
    }
}
