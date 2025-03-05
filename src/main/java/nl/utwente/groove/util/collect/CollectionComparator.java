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

import java.util.Collection;
import java.util.Comparator;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Compares two collections lexicographically, based on the order in which their iterators
 * return elements. Elements are compared under their natural order
 * or a dedicated comparator for the elements.
 * @author Arend Rensink
 * @version $Revision$
 */
public class CollectionComparator<T> implements Comparator<Collection<T>> {
    /** Creates a collection comparator based on a given element comparator. */
    private CollectionComparator(Comparator<T> elemComparator) {
        this.elemComparator = elemComparator;
    }

    /** Creates a list comparator based on the elements' natural ordering. */
    private CollectionComparator() {
        this.elemComparator = null;
    }

    /** The element comparator. */
    private final @Nullable Comparator<T> elemComparator;

    @Override
    public int compare(Collection<T> o1, Collection<T> o2) {
        int result = 0;
        var i1 = o1.iterator();
        var i2 = o2.iterator();
        while (result == 0 && i1.hasNext() && i2.hasNext()) {
            result = compareElems(i1.next(), i2.next());
        }
        if (result == 0) {
            result = i1.hasNext()
                ? -1
                : +1;
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
    static public <T extends Comparable<T>> CollectionComparator<T> instance() {
        return new CollectionComparator<>();
    }

    /** Returns an instance of this comparator for a given element type {@code T},
     * based on a given comparator for the list elements.
     */
    static public <T> CollectionComparator<T> instance(Comparator<T> elemComparator) {
        return new CollectionComparator<>(elemComparator);
    }
}
