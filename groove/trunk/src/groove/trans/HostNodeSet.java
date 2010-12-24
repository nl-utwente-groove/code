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
 * $Id: NodeSet.java,v 1.9 2008-01-30 09:32:57 iovka Exp $
 */
package groove.trans;

import groove.graph.algebra.ValueNode;
import groove.util.TreeHashSet;

import java.util.Collection;

/**
 * Set of nodes whose storage is based on the node numbers of default nodes.
 * Note that this a <i>weaker</i> equivalence than node equality, except if
 * there are no overlapping node numbers in the set.
 * @author Arend Rensink
 * @version $Revision $
 */
final public class HostNodeSet extends TreeHashSet<HostNode> {
    /** Constructs an empty set with a given initial capacity. */
    public HostNodeSet(int capacity) {
        super(capacity, NODE_RESOLUTION, NODE_RESOLUTION);
    }

    /** Constructs an empty set. */
    public HostNodeSet() {
        this(DEFAULT_CAPACITY);
        // super(DefaultNode.getNodeCount(), HASHCODE_EQUATOR);
        // super(NODE_SET_RESOLUTION, DefaultNode.getNodeCount(),
        // HASHCODE_EQUATOR);
        // super(NODE_SET_RESOLUTION, HASHCODE_EQUATOR);
    }

    /** Constructs a copy of an existing set. */
    public HostNodeSet(Collection<? extends HostNode> other) {
        this(other.size());
        addAll(other);
    }

    /** Constructs a copy of an existing node set. */
    public HostNodeSet(HostNodeSet other) {
        super(other);
    }

    @Override
    protected boolean allEqual() {
        return true;
    }

    @Override
    protected boolean areEqual(HostNode newKey, HostNode oldKey) {
        return true;
    }

    @Override
    protected int getCode(HostNode key) {
        int nr = key.getNumber();
        if (key instanceof ValueNode) {
            assert nr < MAX_VALUE_NODE_NR : String.format(
                "Value node number '%s' too high to ensure correctness of NodeSet implementation",
                nr);
            nr += VALUE_NODE_BASE;
        } else {
            assert nr < MAX_DEFAULT_NODE_NR : String.format(
                "Default node number '%s' too high to ensure correctness of NodeSet implementation",
                nr);
        }
        return nr;
    }

    /** Maximum number of default nodes. */
    static private final long MAX_DEFAULT_NODE_NR = 0x80000000L;
    /** Maximum number of product nodes. */
    static private final long MAX_PRODUCT_NODE_NR = 0x10000000L;
    /** Maximum number of variable nodes. */
    static private final long MAX_VARIABLE_NODE_NR = 0x10000000L;
    /** Maximum number of value nodes. */
    static private final long MAX_VALUE_NODE_NR = 0x60000000L;
    /** Offset added to product node numbers, to keep them distinct. */
    static private final int PRODUCT_NODE_BASE = (int) MAX_DEFAULT_NODE_NR;
    /** Offset added to variable node numbers, to keep them distinct. */
    static private final int VARIABLE_NODE_BASE = PRODUCT_NODE_BASE
        + (int) MAX_PRODUCT_NODE_NR;
    /** Offset added to value node numbers, to keep them distinct. */
    static private final int VALUE_NODE_BASE = VARIABLE_NODE_BASE
        + (int) MAX_VARIABLE_NODE_NR;
    /** The resolution of the tree for a node set. */
    static private final int NODE_RESOLUTION = 4;
}
