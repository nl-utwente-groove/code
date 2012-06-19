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
package groove.match.plan;

import groove.algebra.Algebra;
import groove.algebra.AlgebraFamily;
import groove.graph.algebra.ValueNode;
import groove.graph.algebra.VariableNode;
import groove.match.plan.PlanSearchStrategy.Search;
import groove.trans.HostGraph;
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
     * Creates a search item for a value node.
     * @param node the node to be matched
     */
    public ValueNodeSearchItem(VariableNode node, AlgebraFamily family) {
        this.node = node;
        this.boundNodes = Collections.<RuleNode>singleton(node);
        this.algebra = family.getAlgebra(node.getSignature());
        this.value = this.algebra.getValueFromString(node.getSymbol());
    }

    public ValueNodeRecord createRecord(
            groove.match.plan.PlanSearchStrategy.Search matcher) {
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
    public VariableNode getNode() {
        return this.node;
    }

    public void activate(PlanSearchStrategy strategy) {
        this.nodeIx = strategy.getNodeIx(this.node);
    }

    /** Singleton set consisting of <code>node</code>. */
    private final Collection<RuleNode> boundNodes;
    /** The (constant) variable node to be matched. */
    final VariableNode node;
    /** The algebra family in which the value is to be created. */
    final Algebra<?> algebra;
    /** Representation of the constant in the appropriate algebra representaton. */
    final Object value;
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

        @Override
        public void initialise(HostGraph host) {
            super.initialise(host);
            this.image =
                host.getFactory().createNode(ValueNodeSearchItem.this.algebra,
                    ValueNodeSearchItem.this.value);
        }

        @Override
        boolean find() {
            return write();
        }

        @Override
        void erase() {
            this.search.putNode(ValueNodeSearchItem.this.nodeIx, null);
        }

        @Override
        boolean write() {
            return this.search.putNode(ValueNodeSearchItem.this.nodeIx,
                this.image);
        }

        @Override
        public String toString() {
            return ValueNodeSearchItem.this.toString();
        }

        /** The constant value of the variable node, if any. */
        private ValueNode image;
    }
}