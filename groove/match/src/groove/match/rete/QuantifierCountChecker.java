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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import groove.algebra.Algebra;
import groove.algebra.JavaIntAlgebra;
import groove.grammar.Condition;
import groove.grammar.Condition.Op;
import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.grammar.rule.RuleElement;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.RuleToHostMap;
import groove.grammar.rule.VariableNode;
import groove.graph.NodeComparator;
import groove.match.TreeMatch;
import groove.match.rete.ReteSimpleMatch.ReteCountMatch;
import groove.util.Visitor;
import groove.util.Visitor.Collector;
import groove.util.collect.TreeHashSet;

/**
 * Asks an associated condition-checker related to a universal
 * quantifier for its total number of actual matches (taking its submatches
 * into account) and passing down a match binding the count attribute node
 * of the quantifier to that value.
 *
 * @author Arash Jalali
 * @version $Revision $
 */
public class QuantifierCountChecker extends ReteNetworkNode implements ReteStateSubscriber {

    private Condition condition;
    private RuleElement[] pattern;

    /**
     * The match containing the single node-binding between the
     * count node and the actual count value calculated at each round.
     */
    private Set<ReteCountMatch> matches = new TreeHashSet<>();
    private ReteCountMatch dummyMatch;

    /**
     * The condition checker for the universal quantifier to be
     * counted.
     */
    private ConditionChecker universalQuantifierChecker;

    /**
     * Inidicates if the latest valid count matches have been
     * sent down to the successors.
     */
    private boolean updatesSent = false;

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
        assert condition.getOp() == Op.FORALL && (condition.getCountNode() != null);
        this.condition = condition;
        makePattern();
        getPatternLookupTable(); //Just to fill out the pattern index
        this.getOwner()
            .getState()
            .subscribe(this, true);

    }

    private void makePattern() {
        ArrayList<RuleNode> rootNodes = new ArrayList<>();
        rootNodes.addAll(this.condition.getRoot()
            .nodeSet());
        Collections.sort(rootNodes, NodeComparator.instance());
        this.pattern = new RuleElement[rootNodes.size() + 1];
        int i = 0;
        for (RuleNode n : rootNodes) {
            this.pattern[i++] = n;
        }
        this.pattern[i] = this.condition.getCountNode();
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
        return (obj instanceof ReteNetworkNode) && this.equals((ReteNetworkNode) obj);
    }

    @Override
    public int hashCode() {
        return this.condition.hashCode();
    }

    @Override
    public RuleElement[] getPattern() {
        return this.pattern;
    }

    @Override
    /**
     * This method should not to be called by any one.
     */
    public void receive(ReteNetworkNode source, int repeatIndex, AbstractReteMatch match) {
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
        assert cc.getCondition()
            .equals(this.condition);
        this.universalQuantifierChecker = cc;
    }

    /**
     * Utility method for easy retrieval of the count node
     * of the associated condition.
     */
    public VariableNode getCountNode() {
        return this.universalQuantifierChecker.getCondition()
            .getCountNode();
    }

    /**
     * This method is called by the associated condition-checker
     * n-node to notify this checker n-node that the pre-calculated
     * count is no longer valid (due to some changes to the conflict
     * set of this condition checker or of its sub-condition-checkers)
     */
    public boolean invalidateCount() {
        boolean result = !this.matches.isEmpty();
        if (result) {
            for (ReteSimpleMatch match : this.matches) {
                match.dominoDelete(null);
            }
            this.matches.clear();
        }
        if (this.dummyMatch != null) {
            this.dummyMatch.dominoDelete(null);
            this.dummyMatch = null;
        }
        this.updatesSent = false;
        return result;
    }

    @Override
    public void clear() {
        this.matches.clear();
        this.dummyMatch = null;
        this.updatesSent = false;
        this.conditionMatcher = null;
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
        if (!this.updatesSent) {
            calculateMatches();
            if (!this.matches.isEmpty()) {
                for (ReteSimpleMatch match : this.matches) {
                    passDownMatchToSuccessors(match);
                }
            }
            if (this.dummyMatch != null) {
                passDownMatchToSuccessors(this.dummyMatch);
            }
            this.updatesSent = true;
        }
    }

    private void calculateMatches() {
        this.matches.clear();
        Set<RuleToHostMap> activeAnchors =
            this.universalQuantifierChecker.getActiveConflictsetAnchors(false);
        if (this.conditionMatcher == null) {
            this.conditionMatcher = this.getOwner()
                .getOwnerEngine()
                .createMatcher(this.universalQuantifierChecker.getCondition(), null);
        }
        if (activeAnchors != null) {
            for (RuleToHostMap anchor : activeAnchors) {
                ReteCountMatch m = getCountMatch(anchor);
                if (m != null) {
                    this.matches.add(m);
                }
            }
        } else {
            ReteCountMatch m = getCountMatch(null);
            if (m != null) {
                this.matches.add(m);
            }
        }
        if (this.condition.getCountNode()
            .getConstant() == null) {
            Algebra<Integer> intAlgebra = JavaIntAlgebra.instance;
            ValueNode countNode = this.getOwner()
                .getOwnerEngine()
                .getNetwork()
                .getHostFactory()
                .createNode(intAlgebra, intAlgebra.toValueFromJava(0));
            this.dummyMatch = new ReteCountMatch(this, countNode);
        } else {
            this.dummyMatch = null;
        }

    }

    private ReteCountMatch getCountMatch(RuleToHostMap anchor) {
        ReteCountMatch countMatch = null;
        List<TreeMatch> matchList = new ArrayList<>();
        Collector<TreeMatch,?> collector = Visitor.newCollector(matchList);
        this.conditionMatcher.traverse(this.getOwner()
            .getOwnerEngine()
            .getNetwork()
            .getState()
            .getHostGraph(), anchor, collector);
        Algebra<Integer> intAlgebra = JavaIntAlgebra.instance;
        ValueNode vn = this.getOwner()
            .getOwnerEngine()
            .getNetwork()
            .getHostFactory()
            .createNode(intAlgebra, intAlgebra.toValueFromJava(matchList.size()));
        if (this.getCountNode()
            .hasConstant()) {
            if (this.getCountNode()
                .getConstant()
                .equals(vn.getTerm())) {
                countMatch = new ReteCountMatch(this, getAnchorNodes(anchor), vn);
            }
        } else {
            countMatch = new ReteCountMatch(this, getAnchorNodes(anchor), vn);
        }
        if (countMatch != null) {
            this.matches.add(countMatch);
        }
        return countMatch;
    }

    private HostNode[] getAnchorNodes(RuleToHostMap map) {
        HostNode[] result = new HostNode[this.pattern.length - 1];
        if (map != null) {
            for (int i = 0; i < this.pattern.length - 1; i++) {
                HostNode hn = map.getNode((RuleNode) this.pattern[i]);
                assert hn != null;
                result[i] = hn;
            }
        }
        return result;
    }

    /**
     * Determines if this quantifier count checker produces different count
     * values for different given higher level anchors.
     *
     * If the return
     * value of this method is <code>true</code> then it means this checker
     * n-node will only produce one count match.
     */
    public boolean isAnchored() {
        return !this.condition.getRoot()
            .isEmpty();
    }

    /**
     * Returns <code>true</code> if the count node that this checker
     * is associated with is a constant, in which case a dummy match
     * is not produced. Returns <code>false</code> otherwise.
     */
    public boolean isConstant() {
        return this.getCountNode()
            .getConstant() != null;
    }

}
