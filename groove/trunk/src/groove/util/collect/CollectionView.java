// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id$
 */
package groove.util.collect;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

/**
 * Provides a shared view upon an underlying collection, filtering those values
 * that satisfy a certain condition, to be provided through the abstract method
 * {@link #approves(Object)}.
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class CollectionView<T> extends AbstractCollection<T> {
    /**
     * Constucts a shared collection view on a given underlying collection.
     */
    public CollectionView(Collection<?> coll) {
        this.coll = coll;
    }

    @Override
    public int size() {
        int result = 0;
        for (Object elem : this.coll) {
            if (approves(elem)) {
                result++;
            }
        }
        return result;
    }

    @Override
    public boolean contains(Object elem) {
        return approves(elem) && this.coll.contains(elem);
    }

    /*
     * (non-Javadoc)
     * @see java.util.Collection#iterator()
     */
    @Override
    public Iterator<T> iterator() {
        return new FilterIterator<T>(this.coll.iterator()) {
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            protected boolean approves(Object obj) {
                return CollectionView.this.approves(obj);
            }
        };
        // return new Iterator<T>() {
        // public boolean hasNext() {
        // while (elemIter.hasNext()
        // && !(headValid && approves(headElem))) {
        // headElem = elemIter.next();
        // headValid = true;
        // if (ITERATE_DEBUG)
        // Groove.message("Searching for hasNext(); now at "+headElem);
        // }
        // if (ITERATE_DEBUG)
        // Groove.message("Found next? "+(headValid &&
        // approves(headElem)?"Yes":"No"));
        // return headValid && approves(headElem);
        // }
        //
        // public T next() {
        // while (!(headValid && approves(headElem))) {
        // headElem = elemIter.next();
        // headValid = true;
        // if (ITERATE_DEBUG)
        // Groove.message("Searching for next(); now at "+headElem);
        // }
        // headValid = false;
        // if (ITERATE_DEBUG)
        // Groove.message("Found next(): "+headElem);
        // return (T) headElem;
        // }
        //
        // public void remove() {
        // throw new UnsupportedOperationException();
        // }
        //
        // private Iterator<?> elemIter = coll.iterator();
        // private Object headElem = null;
        // private boolean headValid = false;
        // };
    }

    /**
     * Adding elements to this type of collection is not possible.
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean add(Object elem) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removing elements from this type of collection is not possible.
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean remove(Object elem) {
        throw new UnsupportedOperationException();
    }

    /**
     * The condition imposed on set members.
     */
    public abstract boolean approves(Object obj);

    /** The underlying collection. */
    protected final Collection<?> coll;
}
