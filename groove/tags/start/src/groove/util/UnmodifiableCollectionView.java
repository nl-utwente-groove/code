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
 * $Id: UnmodifiableCollectionView.java,v 1.1.1.2 2007-03-20 10:43:00 kastenberg Exp $
 */
package groove.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Provides a shared, unmodifiable view upon an underlying collection, filtering those values
 * that satisfy a certain condition, to be provided through the abstract
 * method <tt>approve(Object)</tt>.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:43:00 $
 */
public abstract class UnmodifiableCollectionView<T> extends CollectionView<T> {    
//    /**
//     * Constucts a view upon a set, newly created for this purpose.
//     * Since the set itself is not available, this is only useful for
//     * creating a set whose elements are guaranteed to satisfy a 
//     * certain condition (to be provided by the abstract method
//     * <tt>approves(Object)</tt>). 
//     * This constructor is provided primarily to satisfy the requirements 
//     * on <tt>Set</tt> implementations.
//     * @see #approves(Object)
//     */
//    public UnmodifiableCollectionView() {
//        super();
//    }

    /**
     * Constucts a shared collection view on a given underlying collection.
     */
    public UnmodifiableCollectionView(Collection<?> set) {
        super(set);
    }

    /**
     * Returns an iterator that does not allow removal of values.
     */
    public Iterator<T> iterator() {
        return new FilterIterator<T>(coll.iterator()) {
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
            protected boolean approves(Object obj) {
                return UnmodifiableCollectionView.this.approves(obj);
            }
        };
    }

    /**
     * @throws UnsupportedOperationException always
     */
    public boolean add(Object elem) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException always
     */
    public boolean remove(Object elem) {
        throw new UnsupportedOperationException();
    }
}
