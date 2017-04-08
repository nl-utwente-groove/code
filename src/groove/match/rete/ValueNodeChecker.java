/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.match.rete;

import java.util.List;

import groove.algebra.Algebra;
import groove.algebra.AlgebraFamily;
import groove.algebra.Constant;
import groove.grammar.host.ValueNode;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.VariableNode;

/**
 * Represents a node-checker that produces ONE match containing
 * one value node corresponding with the node in the pattern.
 *
 * This singleton match is produced upon receiving initialization
 * signal from the RETE network.
 *
 * @author Arash Jalali
 * @version $Revision $
 */
public class ValueNodeChecker extends NodeChecker implements ReteStateSubscriber {

    final VariableNode node;

    /**
     * Creates
     * @param network The network this n-node is to belong to
     */
    public ValueNodeChecker(ReteNetwork network, VariableNode variableNode) {
        super(network);
        this.pattern[0] = variableNode;
        this.node = variableNode;
        this.getOwner()
            .getState()
            .subscribe(this);
    }

    @Override
    public int demandOneMatch() {
        return 0;
    }

    @Override
    public boolean demandUpdate() {
        return false;
    }

    @Override
    public boolean equals(Object node) {
        return equals((ReteNetworkNode) node);
    }

    @Override
    public boolean equals(ReteNetworkNode node) {
        return (node instanceof ValueNodeChecker) && ((ValueNodeChecker) node).getConstant()
            .equals(this.getConstant());
    }

    @Override
    public int hashCode() {
        return getConstant().hashCode();
    }

    /**
     * Returns the constant value this checker node represents
     */
    public Constant getConstant() {
        return ((VariableNode) this.pattern[0]).getConstant();
    }

    @Override
    public void receive(ReteNetworkNode source, int repeatIndex, AbstractReteMatch match) {
        throw new UnsupportedOperationException("This method is not supposed to have been called.");
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public void clear() {
        //Nothing to do
    }

    @Override
    public List<? extends Object> initialize() {
        VariableNode varNode = (VariableNode) this.pattern[0];
        Algebra<?> algebra = AlgebraFamily.getInstance()
            .getAlgebra(varNode.getSort());
        ValueNode valueNode = getOwner().getHostFactory()
            .createNode(algebra, algebra.toValue(varNode.getConstant()));
        ReteSimpleMatch match = new ReteSimpleMatch(this, valueNode, getOwner().isInjective());
        passDownMatchToSuccessors(match);
        return null;
    }

    @Override
    public void updateBegin() {
        //Do nothing
    }

    @Override
    public void updateEnd() {
        //Do nothing
    }

    @Override
    public boolean canBeStaticallyMappedTo(RuleNode node) {
        assert(node instanceof VariableNode) && (((VariableNode) node).getConstant() != null);
        return (node instanceof VariableNode) && ((VariableNode) node).getConstant()
            .equals(this.node.getConstant());
    }
}
