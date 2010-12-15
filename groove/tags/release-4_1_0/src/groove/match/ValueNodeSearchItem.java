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
 * $Id: ValueNodeSearchItem.java,v 1.14 2008-01-30 09:33:30 iovka Exp $
 */
package groove.match;

import groove.graph.algebra.ValueNode;
import groove.match.SearchPlanStrategy.Search;
import groove.trans.RuleNode;

import java.util.Collection;
import java.util.Collections;

/**
 * A search item for a value node.
 * @author Arend Rensink
 * @version $Revision $
 */
class ValueNodeSearchItem extends AbstractSearchItem {
    /**
     * Creates a search item for a value node. The image is always the node
     * itself.
     * @param node the node to be matched
     */
    public ValueNodeSearchItem(ValueNode node) {
        this.node = node;
        this.boundNodes = Collections.<RuleNode>singleton(node);
    }

    public ValueNodeRecord getRecord(SearchPlanStrategy.Search matcher) {
        return new ValueNodeRecord(matcher);
    }

    /**
     * Since the order in which value nodes are matched does not make a
     * difference to the outcome, and the effort is also the same, no natural
     * ordering is imposed.
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
    public Collection<RuleNode> bindsNodes() {
        return this.boundNodes;
    }

    @Override
    public String toString() {
        return String.format("Value %s", this.node);
    }

    /** Returns the value node we are looking up. */
    public ValueNode getNode() {
        return this.node;
    }

    public void activate(SearchPlanStrategy strategy) {
        this.nodeIx = strategy.getNodeIx(this.node);
    }

    /** Singleton set consisting of <code>node</code>. */
    private final Collection<RuleNode> boundNodes;
    /** The (constant) variable node to be matched. */
    final ValueNode node;
    //    /** The value node that represents the value of the constant. */
    //    final ValueNode nodeImage;
    /** The index of the value node (in the result. */
    int nodeIx;

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
         * The first call puts #node to itself; the next call returns
         * <code>false</code>.
         */
        @Override
        boolean set() {
            return this.search.putNode(ValueNodeSearchItem.this.nodeIx,
                ValueNodeSearchItem.this.node);
        }

        @Override
        public void reset() {
            this.search.putNode(ValueNodeSearchItem.this.nodeIx, null);
        }

        @Override
        public String toString() {
            return ValueNodeSearchItem.this.toString();
        }
    }
}
