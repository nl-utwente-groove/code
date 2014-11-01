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

import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Variation on the set view in which removal is not supported.
 * @author Arend Rensink
 * @version $Revision$
 */
public class UnmodifiableSetView<T> extends SetView<T> {
    /**
     * Constucts a shared set view on a given underlying set.
     */
    public UnmodifiableSetView(Set<?> set, Predicate<Object> approval) {
        super(set, approval);
    }

    /**
     * Returns an iterator that does not allow removal of values.
     */
    @Override
    public Iterator<T> iterator() {
        return new FilterIterator<T>(this.set.iterator(), getApproval()) {
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean remove(Object elem) {
        throw new UnsupportedOperationException();
    }
}
