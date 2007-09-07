/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: NodeSet.java,v 1.5 2007-09-07 19:13:37 rensink Exp $
 */
package groove.graph;

import groove.util.TreeHashSet3;

import java.util.Collection;

/**
 * Set of nodes whose storage is based on the node numbers of default nodes.
 * Note that this a <i>weaker</i> equivalence than node equality,
 * except if there are no overlapping node numbers in the set.
 * @author Arend Rensink
 * @version $Revision $
 */
public class NodeSet extends TreeHashSet3<Node> {
    /** Constructs an empty set. */
	public NodeSet() {
		super(DefaultNode.getNodeCount(), HASHCODE_EQUATOR);
		//            super(NODE_SET_RESOLUTION, DefaultNode.getNodeCount(), HASHCODE_EQUATOR);
		//            super(NODE_SET_RESOLUTION, HASHCODE_EQUATOR);
	}

    /** Constructs a copy of an existing set. */
	public NodeSet(Collection<? extends Node> other) {
		super(other, DefaultNode.getNodeCount(), HASHCODE_EQUATOR);
		//            super(other, NODE_SET_RESOLUTION, HASHCODE_EQUATOR);
	}

	@Override
	protected int getCode(Object key) {
        if (key instanceof DefaultNode) {
            return ((DefaultNode) key).getNumber();
        } else {
            return key.hashCode();
        }
	}
}
