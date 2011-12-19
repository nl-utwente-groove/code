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

import groove.match.rete.RetePathMatch.EmptyPathMatch;
import groove.rel.RegExpr;
import groove.rel.RegExpr.Star;
import groove.util.TreeHashSet;

import java.util.List;
import java.util.Set;

import com.sun.org.apache.xpath.internal.operations.Plus;

/**
 * A Path-checker that finds a (kleene or transitive) sequence closure of
 * a certain path expression.
 * @author Arash Jalali
 * @version $Revision $
 */
public class ClosurePathChecker extends AbstractPathChecker implements
        ReteStateSubscriber {

    private EmptyPathMatch emptyMatch = new EmptyPathMatch(this);

    /**
     * The memory for incoming matches coming from the antecedent.
     */
    protected TreeHashSet<RetePathMatch> leftMemory = null;

    /**
     * The memory for loop-back matches received from oneself
     */
    protected TreeHashSet<RetePathMatch> rightMemory = null;

    /**
     * Creates a Path-checker note that performs sequencing-closure, i.e.
     * the plus and star (kleene) operators.
     * @param network The RETE network to which this node will belong
     * @param expression The regular path expression, the operator of
     * which should be either {@link Plus} or {@link Star}.
     */
    public ClosurePathChecker(ReteNetwork network, RegExpr expression,
            boolean isLoop) {
        super(network, expression, isLoop);

        assert (expression.getPlusOperand() != null)
            || (expression.getStarOperand() != null);
        if (expression.isStar()) {
            this.getOwner().getState().subscribe(this);
        }
        this.leftMemory = new TreeHashSet<RetePathMatch>();
        this.rightMemory = new TreeHashSet<RetePathMatch>();
    }

    @Override
    public void receive(ReteNetworkNode source, int repeatIndex,
            RetePathMatch newMatch) {
        if (this.loop && !newMatch.isEmpty()
            && !newMatch.start().equals(newMatch.end())) {
            return;
        }
        receiveNewIncomingMatch(source, newMatch);
    }

    private void receiveLoopBackMatches(Set<RetePathMatch> loopBackMatches,
            int recursionCounter) {
        Set<RetePathMatch> resultingNewMatches =
            new TreeHashSet<RetePathMatch>();
        for (RetePathMatch loopBackMatch : loopBackMatches) {
            if (loopBackMatch.getNodeCount() == loopBackMatch.getPathLength() + 1) {
                this.rightMemory.add(loopBackMatch);
                loopBackMatch.addContainerCollection(this.rightMemory);
                for (RetePathMatch left : this.leftMemory) {
                    if (this.test(left, loopBackMatch)) {
                        RetePathMatch combined =
                            this.construct(left, loopBackMatch);
                        if (combined != null) {
                            resultingNewMatches.add(combined);
                        }
                    }
                }
            }
        }
        if (resultingNewMatches.size() > 0) {
            passDownMatches(resultingNewMatches);
            if (recursionCounter > 0) {
                receiveLoopBackMatches(resultingNewMatches,
                    recursionCounter - 1);
            }
        }
    }

    private void receiveNewIncomingMatch(ReteNetworkNode source,
            RetePathMatch newMatch) {
        Set<RetePathMatch> resultingMatches = new TreeHashSet<RetePathMatch>();
        resultingMatches.add(new RetePathMatch(this, newMatch));
        if (!newMatch.start().equals(newMatch.end())) {
            this.leftMemory.add(newMatch);
            newMatch.addContainerCollection(this.leftMemory);
            for (RetePathMatch right : this.rightMemory) {
                if (this.test(newMatch, right)) {
                    RetePathMatch combined = this.construct(newMatch, right);
                    if (combined != null) {
                        resultingMatches.add(combined);
                    }
                }
            }
        }
        passDownMatches(resultingMatches);
        if (!this.loop) {
            receiveLoopBackMatches(resultingMatches,
                this.getOwner().getState().getHostGraph().nodeCount());
        }
    }

    private void passDownMatches(Set<RetePathMatch> theMatches) {
        for (RetePathMatch m : theMatches) {
            if (!this.loop || (m.isEmpty() || m.start() == m.end())) {
                passDownMatchToSuccessors(m);
            }
        }
    }

    /**
     * 
     * @return <code>true</code> if the two given patch matches
     * can be combined through the regular expression operator
     * of this node's associated expression. This method is only 
     * called when this operator is binary.
     *  
     */
    protected boolean test(RetePathMatch left, RetePathMatch right) {
        return left.isEmpty() || right.isEmpty()
            || left.end().equals(right.start());
    }

    /**
     * @return combines the left and right matches according the 
     * rules of the associated operator.
     */
    protected RetePathMatch construct(RetePathMatch left, RetePathMatch right) {
        if (!left.isEmpty() && !right.isEmpty()) {
            return left.concatenate(this, right, false);
        } else if (!left.isEmpty()) {
            return left.reoriginate(this);
        } else {
            return right.reoriginate(this);
        }
    }

    @Override
    public int demandOneMatch() {
        //TODO ARASH: implement the demand-based update
        return 0;
    }

    @Override
    public boolean demandUpdate() {
        //TODO ARASH: implement the demand-based update
        return false;
    }

    @Override
    public void clear() {
        super.clear();
        this.leftMemory.clear();
        this.rightMemory.clear();
    }

    @Override
    public List<? extends Object> initialize() {
        super.initialize();
        if (this.getExpression().isStar()) {
            passDownMatchToSuccessors(this.emptyMatch);
        }
        return null;
    }

    @Override
    public void updateBegin() {
        // Do nothing        
    }

    @Override
    public void updateEnd() {
        // Do nothing        
    }

}
