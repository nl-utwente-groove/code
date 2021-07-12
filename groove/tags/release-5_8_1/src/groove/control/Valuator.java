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
package groove.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import groove.control.Binding.Source;
import groove.control.instance.Assignment;
import groove.control.instance.Step;
import groove.grammar.host.HostNode;
import groove.graph.Element;
import groove.graph.Node;

/**
 * Type wrapping the functionality to deal with control valuations.
 * The choice of providing the functionality like this is driven
 * by the desire to keep the valuations in the form of node arrays,
 * for the sake of keeping a low memory footprint per state.
 *
 * A control valuation is an {@code Object[]}-array of which all but possibly the last element
 * are {@code HostNode}s, whereas the last element is either also a {@code HostNode} or another
 * control valuation. In the latter case, the control valuation is called nested.
 *
 * The nesting is in inverse stack order; i.e., a control valuation nested in the
 * last element is actually younger than (i.e., on top of) the current one.
 *
 * The values at any given level of a control valuation are in the order of the
 * {@link Assignment} in a given {@link Step}.
 *
 * @author Arend Rensink
 * @version $Revision $
 */
public class Valuator {
    /** Returns the value for a given binding. */
    static public HostNode get(Object[] val, Binding bind) {
        assert bind.getSource() == Source.VAR || bind.getSource() == Source.CALLER;
        return (HostNode) val[bind.getIndex()];
    }

    /** Returns the value at a given position of a valuation. */
    static public HostNode get(Object[] val, int index) {
        return (HostNode) val[index];
    }

    /** Pushes a new top valuation onto an existing (nested) valuation. */
    public static Object[] push(Object[] stack, Object[] top) {
        assert !isNested(top);
        Object[] result = new Object[top.length + 1];
        System.arraycopy(top, 0, result, 0, top.length);
        result[top.length] = stack;
        return result;
    }

    /**
     * Returns a new valuation in which the top level of a given
     * valuation is replaced by new values,
     * while leaving the nesting intact.
     */
    public static Object[] replace(Object[] stack, Object[] top) {
        return isNested(stack) ? push(pop(stack), top) : top;
    }

    /**
     * Pops the top level off a nested valuation, and returns the parent level.
     * Only valid if the valuation is actually nested.
     */
    public static Object[] pop(Object[] stack) {
        return isNested(stack) ? (Object[]) stack[stack.length - 1] : null;
    }

    /** Tests if two valuations have equal content. */
    static public boolean areEqual(Object[] val1, Object[] val2) {
        return areEqual(val1, val2, null);
    }

    /** Tests if two valuations have equal content, under a node map
     * from the images of the first valuation to those of the second.
     * The node map may be empty, in which case it is regarded as the identity.
     */
    static public boolean areEqual(Object[] val1, Object[] val2,
        Map<? extends Node,? extends Node> nodeMap) {
        if (nodeMap == null && val1 == val2) {
            return true;
        }
        if (val1.length != val2.length) {
            return false;
        }
        boolean isNested = isNested(val1);
        if (isNested != isNested(val2)) {
            return false;
        }
        int count = isNested ? val1.length - 1 : val1.length;
        for (int i = 0; i < count; i++) {
            Object image = nodeMap == null ? val1[i] : nodeMap.get(val1[i]);
            if (image == null) {
                if (val2[i] != null) {
                    return false;
                }
            } else if (!image.equals(val2[i])) {
                return false;
            }
        }
        if (isNested && !areEqual(pop(val1), pop(val2))) {
            return false;
        }
        return true;
    }

    /** Computes the hash code of a valuation. */
    static public int hashCode(Object[] val) {
        return hashCode(val, null);
    }

    /**
     * Computes the hash code of a valuation, given a modifier map
     * from host nodes to representative objects from which the hash code is to be taken.
     * The modifier may be {@code null}, in which case only the length of the
     * valuation is used.
     */
    static public int hashCode(Object[] val, Map<? extends Element,?> modifier) {
        int prime = 31;
        int result = 1;
        boolean isNested = isNested(val);
        int count = isNested ? val.length - 1 : val.length;
        for (int i = 0; i < count; i++) {
            Object repr = val[i] == null ? null : modifier == null ? val[i] : modifier.get(val[i]);
            int code = repr == null ? 0 : repr.hashCode();
            result = result * prime + code;
        }
        if (isNested) {
            result = result * prime + hashCode(pop(val), modifier);
        }
        return result;
    }

    /** Turns a given valuation into a nested list of objects. */
    static public List<Object> asList(Object[] val) {
        List<Object> result = new ArrayList<>(Arrays.asList(val));
        if (isNested(val)) {
            result.set(val.length - 1, asList((Object[]) val[val.length - 1]));
        }
        return result;
    }

    /** Returns a string representation of a given valuation. */
    static public String toString(Object[] val) {
        return asList(val).toString();
    }

    /** Tests if the last element of a valuation is another valuation. */
    static private boolean isNested(Object[] val) {
        return val.length > 0 && val[val.length - 1] instanceof Object[];
    }
}
