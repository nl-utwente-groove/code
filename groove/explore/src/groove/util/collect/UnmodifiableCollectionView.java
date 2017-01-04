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


import java.util.Collection;
import java.util.Iterator;

/**
 * Provides a shared, unmodifiable view upon an underlying collection, filtering
 * those values that satisfy a certain condition, to be provided through the
 * abstract method <tt>approve(Object)</tt>.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:14 $
 */
public abstract class UnmodifiableCollectionView<T> extends CollectionView<T> {
    /**
     * Constucts a shared collection view on a given underlying collection.
     */
    public UnmodifiableCollectionView(Collection<?> set) {
        super(set);
    }

    /**
     * Returns an iterator that does not allow removal of values.
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
                return UnmodifiableCollectionView.this.approves(obj);
            }
        };
    }

    /**
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean add(Object elem) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean remove(Object elem) {
        throw new UnsupportedOperationException();
    }
}
