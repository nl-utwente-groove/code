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
import groove.trans.RuleEdge;
import groove.trans.RuleElement;
import groove.trans.RuleFactory;
import groove.trans.RuleLabel;
import groove.trans.RuleNode;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public abstract class AbstractPathChecker extends ReteNetworkNode {

    /**
     * The static pattern representing this path's regular expression edge.
     */
    protected RuleEdge[] pattern;

    /**
     * The regular path expression checked by this checker node
     */
    protected RegExpr expression;

    /**
     * Determines if the path matches produced by this
     * checker should have the same end and starting node.
     */
    protected final boolean loop;

    /**
     * Creates a path checker node based on a given regular expression 
     * and a flag that determines if this checker is loop path checker.
     */
    public AbstractPathChecker(ReteNetwork network, RegExpr expression,
            boolean isLoop) {
        super(network);
        assert (network != null) && (expression != null);
        this.expression = expression;
        RuleFactory f = RuleFactory.newInstance();
        RuleNode n1 = f.createNode(f.getMaxNodeNr());
        RuleNode n2 = (isLoop) ? n1 : f.createNode(f.getMaxNodeNr());
        this.pattern =
            new RuleEdge[] {f.createEdge(n1, new RuleLabel(expression), n2)};
        this.loop = isLoop;
    }

    @Override
    public RuleElement[] getPattern() {
        return this.pattern;
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

    @Override
    public void receive(ReteNetworkNode source, int repeatIndex,
            AbstractReteMatch match) {
        assert match instanceof RetePathMatch;
        this.receive(source, repeatIndex, (RetePathMatch) match);
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

    @Override
    public String toString() {
        return "- Path-checker for: " + this.getExpression().toString();
    }

    public boolean isLoop() {
        return this.loop;
    }

}
