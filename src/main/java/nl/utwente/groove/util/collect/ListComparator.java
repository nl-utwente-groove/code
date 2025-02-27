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
package nl.utwente.groove.util.collect;

import java.util.Comparator;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Compares two lists lexicographically, under their natural order or a dedicated comparator for the elements.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ListComparator<T> implements Comparator<List<T>> {
    /** Creates a list comparator based on a given element comparator. */
    private ListComparator(Comparator<T> elemComparator) {
        this.elemComparator = elemComparator;
    }

    /** Creates a list comparator based on the elements' natural ordering. */
    private ListComparator() {
        this.elemComparator = null;
    }

    /** The element comparator. */
    private final @Nullable Comparator<T> elemComparator;

    @Override
    public int compare(List<T> o1, List<T> o2) {
        int result = 0;
        var bound = Math.min(o1.size(), o2.size());
        for (int i = 0; result == 0 & i < bound; i++) {
            result = compareElems(o1.get(i), o2.get(i));
        }
        if (result == 0) {
            result = o1.size() - o2.size();
        }
        return result;

    }

    /** Compares two list elements. */
    @SuppressWarnings("unchecked")
    private int compareElems(T e1, T e2) {
        var elemComparator = this.elemComparator;
        if (elemComparator == null) {
            return ((Comparable<T>) e1).compareTo(e2);
        } else {
            return elemComparator.compare(e1, e2);
        }
    }

    /** Returns an instance of this comparator for a given element type {@code T},
     * based on {@code T}'s natural ordering.
     */
    static public <T extends Comparable<T>> ListComparator<T> instance() {
        return new ListComparator<>();
    }

    /** Returns an instance of this comparator for a given element type {@code T},
     * based on a given comparator for the list elements.
     */
    static public <T> ListComparator<T> instance(Comparator<T> elemComparator) {
        return new ListComparator<>(elemComparator);
    }
}
