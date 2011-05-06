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

import groove.rel.RegExpr;
import groove.rel.RegExpr.Empty;
import groove.rel.RegExpr.Neg;
import groove.rel.RegExpr.Star;
import groove.trans.RuleElement;
import groove.trans.RuleFactory;
import groove.trans.RuleNode;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public abstract class AbstractPathChecker extends ReteNetworkNode {

    /**
     * The static start and end node patterns of this checker node.
     */
    protected RuleNode[] endPointsPattern = new RuleNode[2];

    /**
     * The regular path expression checked by this checker node
     */
    protected RegExpr expression;

    /**
     * Creates a path checker node based on a given regular expression and a pair of
     * nodes representing the start and end nodes of the path in its pattern.
     * For internal used only.
     *      
     */
    protected AbstractPathChecker(ReteNetwork network, RegExpr expression,
            boolean isLoop) {
        super(network);
        assert (network != null) && (expression != null);
        this.expression = expression;
        RuleFactory f = RuleFactory.instance();
        RuleNode n1 = f.createNode(f.getMaxNodeNr());
        RuleNode n2 = (isLoop) ? n1 : f.createNode(f.getMaxNodeNr());
        this.endPointsPattern = new RuleNode[] {n1, n2};
    }

    /**
     * Creates a path checker node based on a given regular expression.
     * 
     * @param network The RETE network this checker will belong to
     * @param expression regular expression for which this object is a path checker
     */
    public AbstractPathChecker(ReteNetwork network, RegExpr expression) {
        this(network, expression, false);
    }

    @Override
    public RuleElement[] getPattern() {
        return this.endPointsPattern;
    }

    /**
     * @return The regular expression object associated with this checker.
     */
    public RegExpr getExpression() {
        return this.expression;
    }

    /**
     * @return <code>true</code> if this checker node
     * always generates positive matches, i.e. matches
     * which correspond with actual series of edges with concrete
     * end points. The {@link Empty} path operator, 
     * the kleene ({@link Star}) operator, and the negation
     * operator {@link Neg}) are operators that sometimes/always 
     * generate non-positive matches.
     */
    public boolean isPositivePathGenerator() {
        return this.getExpression().isAcceptsEmptyWord()
            || (this.getExpression().getNegOperand() != null);
    }

    /**
     * Should be called by the antecedents to hand in a new match 
     * @param source The antecedent that is calling this method
     * @param repeatedIndex The counter index in case the given <code>source</code>
     * occurs more than once in the list of this node's antecedents.
     * @param newMatch The match produced by the antecedent. 
     */
    public abstract void receive(ReteNetworkNode source, int repeatedIndex,
            RetePathMatch newMatch);

    @Override
    public boolean equals(ReteNetworkNode node) {
        return (this == node)
            || ((node instanceof WildcardPathChecker)
                && this.getOwner().equals(node.getOwner()) && this.expression.equals(((WildcardPathChecker) node).getExpression()));
    }

    @Override
    public int size() {
        return -this.getExpression().getOperands().size();
    }

    /**
     * Calls the appropriate <code>receive</code> method if a given successor
     * to hand in the new match, if the new match should in fact be handed over 
     * to that particular successor.
     * 
     * @param newMatch the new match
     * @param n The successor n-node in the RETE network
     * @param repeatedSuccessorIndex The repeating index for the successor . 
     *       (See the {@link SubgraphCheckerNode#receive(ReteNetworkNode, int, groove.trans.HostElement, Action)}
     *       for more info on this parameter.)
     *          
     *           
     */
    protected void giveNewMatchToSuccessor(RetePathMatch newMatch,
            ReteNetworkNode n, int repeatedSuccessorIndex) {
        if (n instanceof SubgraphCheckerNode) {
            ((SubgraphCheckerNode) n).receive(this, repeatedSuccessorIndex,
                newMatch);
        } else if (n instanceof ConditionChecker) {
            ((ConditionChecker) n).receive(newMatch);
        } else if (n instanceof SubgraphCheckerNode) {
            ((SubgraphCheckerNode) n).receive(this, repeatedSuccessorIndex,
                newMatch);
        } else if (n instanceof DisconnectedSubgraphChecker) {
            ((DisconnectedSubgraphChecker) n).receive(this,
                repeatedSuccessorIndex, newMatch);
        } else if (n instanceof AbstractPathChecker) {
            ((AbstractPathChecker) n).receive(this, repeatedSuccessorIndex,
                newMatch);
        }
    }

    @Override
    public String toString() {
        return "- Checker Path for: " + this.getExpression().toString();
    }

}
