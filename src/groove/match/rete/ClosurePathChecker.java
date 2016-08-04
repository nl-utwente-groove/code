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

import groove.automaton.RegExpr;
import groove.automaton.RegExpr.Plus;
import groove.automaton.RegExpr.Star;
import groove.grammar.host.HostNode;
import groove.grammar.host.HostNodeSet;
import groove.grammar.host.HostNodeTreeHashSet;
import groove.match.rete.RetePathMatch.EmptyPathMatch;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A Path-checker that finds a (kleene or transitive) sequence closure of
 * a certain path expression.
 * @author Arash Jalali
 * @version $Revision $
 */
public class ClosurePathChecker extends AbstractPathChecker implements ReteStateSubscriber {

    private EmptyPathMatch emptyMatch = new EmptyPathMatch(this);

    /**
     * The memory for incoming matches coming from the antecedent.
     */
    private final Set<RetePathMatch> leftMemory;

    /**
     * The memory for loop-back matches received from oneself
     */
    private final Set<RetePathMatch> rightMemory;

    /**
     * Creates a Path-checker note that performs sequencing-closure, i.e.
     * the plus and star (kleene) operators.
     * @param network The RETE network to which this node will belong
     * @param expression The regular path expression, the operator of
     * which should be either {@link Plus} or {@link Star}.
     */
    public ClosurePathChecker(ReteNetwork network, RegExpr expression, boolean isLoop) {
        super(network, expression, isLoop);

        assert (expression.getPlusOperand() != null) || (expression.getStarOperand() != null);
        if (expression.isStar()) {
            this.getOwner().getState().subscribe(this);
        }
        this.leftMemory = new HashSet<>();
        this.rightMemory = new HashSet<>();
    }

    @Override
    public void receive(ReteNetworkNode source, int repeatIndex, RetePathMatch newMatch) {
        if (this.loop && !newMatch.isEmpty() && !newMatch.start().equals(newMatch.end())) {
            return;
        }
        receiveNewIncomingMatch(source, newMatch);
    }

    // EZ says: commented out to avoid warning.
    /*private void altReceiveLoopBackMatches(
            Collection<RetePathMatch> loopBackMatches) {
        List<RetePathMatch> resultingNewMatches =
            new LinkedList<RetePathMatch>();
        for (RetePathMatch loopBackMatch : loopBackMatches) {
            this.rightMemory.add(loopBackMatch);
            loopBackMatch.addContainerCollection(this.rightMemory);
            for (RetePathMatch left : this.rightMemory) {
                if (test(left, loopBackMatch)) {
                    RetePathMatch combined = construct(left, loopBackMatch);
                    if (combined != null) {
                        resultingNewMatches.add(combined);
                    }
                }
            }
        }
        if (resultingNewMatches.size() > 0) {
            passDownMatches(resultingNewMatches);
            for (RetePathMatch newMatch : resultingNewMatches) {
                this.rightMemory.add(newMatch);
                newMatch.addContainerCollection(this.rightMemory);
            }
        }
    }*/

    private void receiveLoopBackMatches(Collection<RetePathMatch> loopBackMatches,
        int recursionCounter) {
        List<RetePathMatch> resultingNewMatches = new LinkedList<>();
        for (RetePathMatch loopBackMatch : loopBackMatches) {
            this.rightMemory.add(loopBackMatch);
            loopBackMatch.addContainerCollection(this.rightMemory);
            for (RetePathMatch left : this.leftMemory) {
                if (test(left, loopBackMatch)) {
                    RetePathMatch combined = construct(left, loopBackMatch);
                    if (combined != null) {
                        resultingNewMatches.add(combined);
                    }
                }
            }

        }
        if (resultingNewMatches.size() > 0) {
            passDownMatches(resultingNewMatches);
            if (recursionCounter > 0) {
                receiveLoopBackMatches(resultingNewMatches, recursionCounter - 1);
            }
        }
    }

    private void receiveNewIncomingMatch(ReteNetworkNode source, RetePathMatch newMatch) {
        List<RetePathMatch> resultingMatches = new LinkedList<>();
        RetePathMatch m = new RetePathMatch(this, newMatch);
        m.setClosureInfo(new ClosureInfo(m));
        resultingMatches.add(m);
        if (newMatch.start().equals(newMatch.end())) {
            passDownMatches(resultingMatches);
        } else {
            this.leftMemory.add(newMatch);
            newMatch.addContainerCollection(this.leftMemory);
            for (RetePathMatch right : this.rightMemory) {
                if (test(newMatch, right)) {
                    RetePathMatch combined = construct(newMatch, right);
                    if (combined != null) {
                        resultingMatches.add(combined);
                    }
                }
            }
            passDownMatches(resultingMatches);
            if (!this.loop) {
                receiveLoopBackMatches(resultingMatches, this.getOwner()
                    .getState()
                    .getHostGraph()
                    .nodeCount());
                //                altReceiveLoopBackMatches(resultingMatches);
            }
        }
    }

    private void passDownMatches(Collection<RetePathMatch> theMatches) {
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
        return left.isEmpty() || right.isEmpty() || left.end().equals(right.start());
    }

    /**
     * @return combines the left and right matches according the
     * rules of the associated operator.
     */
    protected RetePathMatch construct(RetePathMatch left, RetePathMatch right) {
        assert !right.isEmpty();
        RetePathMatch result = null;
        if (!left.isEmpty()) {
            assert (right.getOrigin() == this) && (right.getClosureInfo() != null);
            ClosureInfo ci2 = right.getClosureInfo();
            ClosureInfo combinedInfo = ci2.getExtension(left);
            if (combinedInfo != null) {
                result = left.concatenate(this, right, false);
                result.setClosureInfo(combinedInfo);
            }
        }
        return result;
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

    /**
     * Contains information about the number of
     * closures and the set of relevant nodes modulo
     * the base of the closure that participate in a path.
     *
     * This is used by the ClosurePathChecker to decide
     * if a path is required to be among the sets of
     * paths covered by this checker or not, to make sure
     * the closure computation terminates.
     *
     * @author Arash Jalali
     * @version $Revision $
     */
    protected static class ClosureInfo {
        /** The network node to which this closure belongs. */
        private final ReteNetworkNode origin;
        /** Set of relevant nodes, excluding the final node. */
        private final HostNodeTreeHashSet relevantNodes;
        /** The end node of the path. */
        private final HostNode end;

        /**
         * Used internally
         */
        protected ClosureInfo(ClosureInfo original) {
            this.origin = original.origin;
            this.relevantNodes = new HostNodeSet(original.relevantNodes);
            this.end = original.end;
        }

        /**
         * Creates info for the base of a closure
         */
        public ClosureInfo(RetePathMatch closureBaseMatch) {
            this.origin = closureBaseMatch.getOrigin();
            this.relevantNodes = new HostNodeSet();
            this.relevantNodes.add(closureBaseMatch.start());
            this.end = closureBaseMatch.end();
            closureBaseMatch.getOrigin();
        }

        /**
         * Returns an info object containing the nodes of this one
         * plus the start node of a match concatenated to the left.
         * @return an extended info object, or {@code null} if the
         * result has a cycle
         */
        public ClosureInfo getExtension(RetePathMatch left) {
            ClosureInfo result = null;
            if (left.getOrigin() == this.origin) {
                // the extending match is also a match of this path checker, so
                // all relevant nodes of the match are also relevant for this closure
                ClosureInfo other = left.getClosureInfo();
                result = new ClosureInfo(this);
                for (HostNode newNode : other.relevantNodes) {
                    if (!result.relevantNodes.add(newNode)) {
                        result = null;
                        break;
                    }
                }
            } else {
                // the extending match is not a match of this path checker
                // but of its left antecedent
                HostNode newNode = left.start();
                if (newNode != this.end && !this.relevantNodes.contains(newNode)) {
                    result = new ClosureInfo(this);
                    result.relevantNodes.add(left.start());
                }
            }
            return result;
        }

        /**
         * Returns an info object containing the nodes of this one
         * plus the nodes of a another closure concatenated to the left.
         * @return an extended info object, or {@code null} if the
         * result has a cycle
         */
        public ClosureInfo getExtension(ClosureInfo left) {
            ClosureInfo result = new ClosureInfo(this);
            for (HostNode newNode : left.relevantNodes) {
                if (!result.relevantNodes.add(newNode)) {
                    result = null;
                    break;
                }
            }
            return result;
        }
    }
}
