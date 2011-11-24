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

import groove.algebra.JavaIntAlgebra;
import groove.graph.algebra.ValueNode;
import groove.graph.algebra.VariableNode;
import groove.match.TreeMatch;
import groove.trans.Condition;
import groove.trans.RuleElement;
import groove.trans.Condition.Op;
import groove.util.Visitor.Collector;

import java.util.ArrayList;
import java.util.List;

/**
 * Asks an associated condition-checker related to a universal
 * quantifier for its total number of actual matches (taking its submatches
 * into account) and passing down a match binding the count attribute node
 * of the quantifier to that value.
 * 
 * @author Arash Jalali
 * @version $Revision $
 */
public class QuantifierCountChecker extends ReteNetworkNode implements
        ReteStateSubscriber {

    private Condition condition;
    private RuleElement[] pattern;

    /**
     * The match containing the single node-binding between the
     * count node and the actual count value calculated at each round.
     */
    private ReteSimpleMatch match = null;

    /**
     * The condition checker for the universal quantifier to be
     * counted.
     */
    private ConditionChecker universalQuantifierChecker;

    /**
     * The full matcher used to ask the matches for counting
     * purposes.
     */
    private ReteSearchStrategy conditionMatcher = null;

    /**
     * @param network The RETE network this checker n-node would belong to.
     */
    public QuantifierCountChecker(ReteNetwork network, Condition condition) {
        super(network);
        assert condition.getOp() == Op.FORALL
            && (condition.getCountNode() != null);
        this.condition = condition;
        this.pattern = new RuleElement[] {condition.getCountNode()};
        this.getOwner().getState().subscribe(this, true);
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
    public boolean equals(ReteNetworkNode node) {
        return (node instanceof QuantifierCountChecker)
            && (((QuantifierCountChecker) node).condition.equals(this.condition));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ReteNetworkNode)
            && this.equals((ReteNetworkNode) obj);
    }

    @Override
    public RuleElement[] getPattern() {
        return this.pattern;
    }

    @Override
    /**
     * This method should not to be called by any one.
     */
    public void receive(ReteNetworkNode source, int repeatIndex,
            AbstractReteMatch match) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return 1;
    }

    /**
     * The condition checker n-node associated with 
     * the universal quantifier condition which this
     * n-node is supposed to count the matches of.
     */
    public ConditionChecker getUniversalQuantifierChecker() {
        return this.universalQuantifierChecker;
    }

    /**
     * Set the associated condition checker for the universal quantifier  
     */
    public void setUniversalQuantifierChecker(ConditionChecker cc) {
        assert cc.getCondition().equals(this.condition);
        this.universalQuantifierChecker = cc;
    }

    /**
     * Utility method for easy retrieval of the count node 
     * of the associated condition.
     */
    public VariableNode getCountNode() {
        return this.universalQuantifierChecker.getCondition().getCountNode();
    }

    /**
     * This method is called by the associated condition-checker
     * n-node to notify this checker n-node that the pre-calculated
     * count is no longer valid (due to some changes to the conflict
     * set of this condition checker or of its sub-condition-checkers)
     */
    public boolean invalidateCount() {
        boolean result = this.match != null;
        if (this.match != null) {
            this.match.dominoDelete(null);
            this.match = null;
        }
        return result;
    }

    @Override
    public void clear() {
        this.match = null;
    }

    @Override
    public List<? extends Object> initialize() {
        return null;
    }

    @Override
    public void updateBegin() {
        //There's nothing to do
        //we just have to wait for all the updates to happen
        //before we can start calculating the counts
    }

    @Override
    public void updateEnd() {
        if (this.match == null) {
            this.match = calculateMatch();
            if (this.match != null) {
                passDownMatchToSuccessors(this.match);
            }
        }
    }

    private ReteSimpleMatch calculateMatch() {
        ReteSimpleMatch result = null;
        List<TreeMatch> matchList = new ArrayList<TreeMatch>();
        Collector<TreeMatch,?> collector = Collector.newCollector(matchList);
        if (this.conditionMatcher == null) {
            this.conditionMatcher =
                this.getOwner().getOwnerEngine().createMatcher(
                    this.universalQuantifierChecker.getCondition(), null, null);
        }
        this.conditionMatcher.traverse(
            this.getOwner().getOwnerEngine().getNetwork().getState().getHostGraph(),
            null, collector);
        ValueNode vn =
            this.getOwner().getOwnerEngine().getNetwork().getHostFactory().createNodeFromJava(
                JavaIntAlgebra.instance, matchList.size());
        if (this.getCountNode().getConstant() != null) {
            if (this.getCountNode().getConstant().getSymbol().equals(
                vn.getSymbol())) {
                result =
                    new ReteSimpleMatch(this, vn, this.getOwner().isInjective());
            }
        } else {
            result =
                new ReteSimpleMatch(this, vn, this.getOwner().isInjective());
        }
        return result;
    }

}
