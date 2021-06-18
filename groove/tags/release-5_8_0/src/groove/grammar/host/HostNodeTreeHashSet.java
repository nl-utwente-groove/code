/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.grammar.host;

import groove.util.collect.TreeHashSet;

import java.util.Collection;

/**
 * Set of nodes whose storage is based on the node numbers of default nodes.
 * Note that this a <i>weaker</i> equivalence than node equality, except if
 * there are no overlapping node numbers in the set.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class HostNodeTreeHashSet extends TreeHashSet<HostNode> {
    /** Constructs an empty set with a given initial capacity. */
    public HostNodeTreeHashSet(int capacity) {
        super(capacity, NODE_RESOLUTION, NODE_RESOLUTION);
    }

    /** Constructs an empty set. */
    public HostNodeTreeHashSet() {
        this(DEFAULT_CAPACITY);
    }

    /** Constructs a copy of an existing set. */
    public HostNodeTreeHashSet(Collection<? extends HostNode> other) {
        this(other.size());
        addAll(other);
    }

    /** Constructs a copy of an existing node set. */
    public HostNodeTreeHashSet(HostNodeTreeHashSet other) {
        super(other);
    }

    @Override
    protected boolean allEqual() {
        return true;
    }

    @Override
    protected boolean areEqual(HostNode newKey, HostNode oldKey) {
        assert newKey.equals(oldKey);
        return true;
    }

    @Override
    protected int getCode(HostNode key) {
        int nr = key.getNumber();
        return nr;
    }

    /** The resolution of the tree for a node set. */
    static private final int NODE_RESOLUTION = 4;
}
