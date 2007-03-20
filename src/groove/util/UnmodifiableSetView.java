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
 * $Id: UnmodifiableSetView.java,v 1.1.1.1 2007-03-20 10:05:19 kastenberg Exp $
 */
package groove.util;

import java.util.Iterator;
import java.util.Set;

/**
 * Provides a shared view upon an underlying set, filtering those values
 * that satisfy a certain condition, to be provided through the abstract
 * method <tt>approve(Object)</tt>.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public abstract class UnmodifiableSetView<T> extends SetView<T> {    
    /**
     * Constucts a view upon a set, newly created for this purpose.
     * Since the set itself is not available, this is only useful for
     * creating a set whose elements are guaranteed to satisfy a 
     * certain condition (to be provided by the abstract method
     * <tt>approves(Object)</tt>). 
     * This constructor is provided primarily to satisfy the requirements 
     * on <tt>Set</tt> implementations.
     * @see #approves(Object)
     */
    public UnmodifiableSetView() {
        super();
    }

    /**
     * Constucts a shared set view on a given underlying set.
     */
    public UnmodifiableSetView(Set<? super T> set) {
        super(set);
    }

    /**
     * Returns an iterator that does not allow removal of values.
     */
    public Iterator<T> iterator() {
        return new FilterIterator<T>(set.iterator()) {
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
            protected boolean approves(Object obj) {
                return UnmodifiableSetView.this.approves(obj);
            }
        };
    }

    /**
     * @throws UnsupportedOperationException always
     */
    public boolean add(T elem) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException always
     */
    public boolean remove(Object elem) {
        throw new UnsupportedOperationException();
    }
}
