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
 * $Id: ValueNodeSearchItem.java,v 1.7 2007-09-22 09:10:35 rensink Exp $
 */
package groove.match;

import java.util.Collection;
import java.util.Collections;

import groove.graph.Node;
import groove.graph.algebra.ValueNode;
import groove.match.SearchPlanStrategy.Search;

/**
 * A search item for a value node.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ValueNodeSearchItem extends AbstractSearchItem {
	/**
	 * Creates a search item for a value node.
	 * The image is always the node itself.
	 * @param node the node to be matched
	 */
	public ValueNodeSearchItem(ValueNode node) {
	    if (! node.hasValue()) {
	        throw new IllegalArgumentException(String.format("Cannot search for variable node %s", node));
	    }
		this.node = node;
        this.boundNodes = Collections.<Node>singleton(node);
	}
	
	public ValueNodeRecord getRecord(SearchPlanStrategy.Search matcher) {
		return new ValueNodeRecord(matcher);
	}

    /**
     * Since the order in which value nodes are matched does not make 
     * a difference to the outcome, and the effort is also the same,
     * no natural ordering is imposed.
     * @return <code>0</code> always
     */
    @Override
    int getRating() {
        return 0;
    }

    /**
     * Returns the singleton set consisting of the node matched by this item
     * @see #getNode()
     */
    @Override
    public Collection<Node> bindsNodes() {
        return boundNodes;
    }

    @Override
	public String toString() {
		return String.format("Value %s", node); 
	}

	/** Returns the value node we are looking up. */
	public ValueNode getNode() {
		return node;
	}
	
	
	public void activate(SearchPlanStrategy strategy) {
        nodeIx = strategy.getNodeIx(node);
    }


    /** The value node to be matched. */
	private final ValueNode node;
    /** Singleton set consisting of <code>node</code>. */
    private final Collection<Node> boundNodes;
    /** The index of the value node (in the result. */
    private int nodeIx;
    /**
     * Record of a value node search item.
     * @author Arend Rensink
     * @version $Revision $
     */
    private class ValueNodeRecord extends SingularRecord {
        /**
         * Creates a record based on a given underlying matcher.
         */
        ValueNodeRecord(Search search) {
            super(search);
        }

        /**
         * The first call puts #node to itself;
         * the next call returns <code>false</code>.
         */
        @Override
        boolean set() {
            getSearch().putNode(nodeIx, node);
            return true;
        }
        
        @Override
        void undo() {
            getSearch().putNode(nodeIx, null);
        }

        @Override
        public String toString() {
            return ValueNodeRecord.this.toString();
        }
    }
}
