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
package nl.utwente.groove.grammar.host;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Deterministic set of host nodes.
 * Determinism means that, after a fixed sequence of inserts and removals,
 * the order of the elements as returned by {@link #iterator()} is also fixed.
 * This class provides an intermediate type that can be changed easily
 * to inherit from {@link LinkedHashSet} or {@link HostNodeTreeHashSet}. 
 * @author Arend Rensink
 * @version $Revision$
 */
public final class HostNodeSet extends HostNodeTreeHashSet {
    /** Constructs an empty instance with default capacity. */
    public HostNodeSet() {
        super();
    }

    /** Clones a given set. */
    public HostNodeSet(HostNodeSet other) {
        super(other);
    }

    /** Constructs an empty instance with given initial capacity. */
    public HostNodeSet(int capacity) {
        super(capacity);
    }

    /** Clones a given set. */
    public HostNodeSet(Collection<? extends HostNode> other) {
        super(other);
    }

    /**
     * Factory method to copy a given set of host nodes or create a new one.
     * @param set the set to be copied; if {@code null}, a fresh empty set is returned.
     */
    final public static HostNodeSet newInstance(Collection<HostNode> set) {
        HostNodeSet result;
        if (set == null) {
            result = new HostNodeSet();
        } else if (set instanceof HostNodeSet) {
            result = new HostNodeSet((HostNodeSet) set);
        } else {
            result = new HostNodeSet(set);
        }
        return result;
    }
}
