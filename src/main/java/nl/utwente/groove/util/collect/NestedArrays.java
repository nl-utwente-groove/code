/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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
package nl.utwente.groove.util.collect;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Utility methods for <i>nested arrays</i>: object arrays whose last element
 * may recursively be another such array, forming a stack of levels.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class NestedArrays {
    /** Private constructor to prevent this class from being instantiated. */
    private NestedArrays() {
        // empty
    }

    /** Tests if two nested arrays have equal content. */
    static public boolean areEqual(Object[] array1, Object[] array2) {
        return areEqual(array1, array2, null);
    }

    /** Tests if two nested arrays have equal content, under a map
     * from the top-level entries of the first array to those of the second.
     * The map may be {@code null}, in which case it is regarded as the identity.
     */
    static public boolean areEqual(Object[] array1, Object[] array2, @Nullable Map<?,?> map) {
        if (map == null && array1 == array2) {
            return true;
        }
        if (array1.length != array2.length) {
            return false;
        }
        boolean isNested = isNested(array1);
        if (isNested != isNested(array2)) {
            return false;
        }
        int count = isNested
            ? array1.length - 1
            : array1.length;
        for (int i = 0; i < count; i++) {
            Object image = map == null
                ? array1[i]
                : map.get(array1[i]);
            if (image == null) {
                if (array2[i] != null) {
                    return false;
                }
            } else if (!image.equals(array2[i])) {
                return false;
            }
        }
        if (isNested && !areEqual(pop(array1), pop(array2))) {
            return false;
        }
        return true;
    }

    /** Computes the hash code of a nested array. */
    static public int hashCode(Object[] array) {
        return hashCode(array, null);
    }

    /**
     * Computes the hash code of a nested array, given an optional modifier map
     * from entries to representative objects from which the hash code is to be
     * taken instead. Entries without representative contribute {@code 0}.
     */
    static public int hashCode(Object[] array, @Nullable Map<?,?> modifier) {
        int prime = 31;
        int result = 1;
        boolean isNested = isNested(array);
        int count = isNested
            ? array.length - 1
            : array.length;
        for (int i = 0; i < count; i++) {
            Object repr = array[i] == null
                ? null
                : modifier == null
                    ? array[i]
                    : modifier.get(array[i]);
            int code = repr == null
                ? 0
                : repr.hashCode();
            result = result * prime + code;
        }
        if (isNested) {
            result = result * prime + hashCode(pop(array), modifier);
        }
        return result;
    }

    /** Returns the nested array forming the last element of a given
     * nested array. Only valid if the array is actually nested.
     */
    static private Object[] pop(Object[] array) {
        assert isNested(array);
        return (Object[]) array[array.length - 1];
    }

    /** Tests if the last element of an array is another array. */
    static private boolean isNested(Object[] array) {
        return array.length > 0 && array[array.length - 1] instanceof Object[];
    }
}
